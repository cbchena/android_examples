/*
 * Copyright (c) 2014 Yrom Wang <http://www.yrom.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * 2016/12/3 15:05
 * PS: 搜索【传至后台的】这个字段，找到byte数组，其中有视频、音频的数据流，
 *     将它们传给后台混合编译，就可以了。
 */

package net.yrom.screenrecorder;

import android.annotation.TargetApi;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 屏幕录制 2016/12/1 14:27
 * 必须支持android 5.0系统以上
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ScreenRecorder extends Thread {
    private static final String TAG = "ScreenRecorder";

    private int mWidth;
    private int mHeight;
    private int mBitRate;
    private int mDpi;
    private String mDstPath;
    private MediaProjection mMediaProjection;
    // parameters for the encoder
    private static final String MIME_TYPE = "video/avc"; // H.264 Advanced Video Coding
    private static final String AUDIO_MIME_TYPE = "audio/mp4a-latm";
    private static final int FRAME_RATE = 30; // 30 fps
    private static final int IFRAME_INTERVAL = 10; // 10 seconds between I-frames
    private static final int TIMEOUT_US = 10000;

    private MediaCodec mEncoder;
    private MediaCodec mAudioEncoder;
    private Surface mSurface;
    private MediaMuxer mMuxer;
    private boolean mMuxerStarted = false;
    private int mVideoTrackIndex = -1;
    private int mAudioTrackIndex = -1;
    private int tital = 0;
    private AtomicBoolean mQuit = new AtomicBoolean(false);
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
    private MediaCodec.BufferInfo mBufferInfoAudio = new MediaCodec.BufferInfo();
    private VirtualDisplay mVirtualDisplay;

    public ScreenRecorder(int width, int height, int bitrate, int dpi, MediaProjection mp, String dstPath) {
        super(TAG);
        mWidth = width;
        mHeight = height;
        mBitRate = bitrate;
        mDpi = dpi;
        mMediaProjection = mp;
        mDstPath = dstPath;
    }


    public ScreenRecorder(MediaProjection mp) {
        // 480p 2Mbps
        this(640, 480, 2000000, 1, mp, "/sdcard/test.mp4");
    }

    /**
     * stop task
     */
    public final void quit() {
        mQuit.set(true);
    }

    @Override
    public void run() {
        try {
            try {
                prepareEncoder();
                mMuxer = new MediaMuxer(mDstPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            mVirtualDisplay = mMediaProjection.createVirtualDisplay(TAG + "-display",
                    mWidth, mHeight, mDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                    mSurface, null, null);
            Log.d(TAG, "created virtual display: " + mVirtualDisplay);
            recordVirtualDisplay();
        } finally {
            release();
        }
    }

    private AudioRecord audioRecord; // 录音器 2016/12/3 14:33
    private final int SAMPLES_PER_FRAME = 2048;
//    private long audioStartTime; // 开始时间 2016/12/3 14:33
//    private long presentationTimeUs; // 音视频的同步时间 2016/12/3 14:33

    /**
     * 读取视频流数据的outputBufferId，通过该id可以获取buffer数据 2016/12/1 15:23
     */
    private void recordVirtualDisplay() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        int audioSource = MediaRecorder.AudioSource.MIC;
        int sampleRateInHz = 44100;
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat); // 最小的缓冲区 2016/12/3 14:52
        audioRecord = new AudioRecord(
                audioSource,   //音频源
                sampleRateInHz,    //采样率
                channelConfig,  //音频通道
                audioFormat,    //音频格式
                bufferSizeInBytes //缓冲区
        );

        audioRecord.startRecording();
        int bufferReadResult;
        ByteBuffer[] codecInputBuffers = mAudioEncoder.getInputBuffers();
        int byteSize = SAMPLES_PER_FRAME;
        if (bufferSizeInBytes > SAMPLES_PER_FRAME)
            byteSize = SAMPLES_PER_FRAME * 2;

        while (!mQuit.get()) {
            byte[] buffer = new byte[byteSize];
            bufferReadResult = audioRecord.read(buffer, 0, byteSize);

            // 判断是否读取成功
            if(bufferReadResult == AudioRecord.ERROR_BAD_VALUE
                    || bufferReadResult == AudioRecord.ERROR_INVALID_OPERATION) {
                Log.e(TAG, "Read error");
            }

//            long audioPresentationTimeNs = System.nanoTime();
//            if (audioStartTime == 0)
//                audioStartTime = audioPresentationTimeNs;

            // 录音时长 2016/12/3 14:12
//            presentationTimeUs = (audioPresentationTimeNs - audioStartTime) / 1000;

            // 通过outputBufferId获取buffer 2016/12/1 15:23
            int outputBufferId = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_US);
            Log.i(TAG, "dequeue output buffer outputBufferAudioId=" + outputBufferId);
            if (outputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                resetOutputFormat();
            } else if (outputBufferId == MediaCodec.INFO_TRY_AGAIN_LATER) {
                Log.d(TAG, "retrieving buffers time out!");
                try {
                    // wait 10ms
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            } else if (outputBufferId >= 0 && tital == 2) {

                if (!mMuxerStarted) {
                    throw new IllegalStateException("MediaMuxer dose not call addTrack(format) ");
                }

                // 根据outputBufferId获取ByteBuffer后，写入文件 2016/12/1 15:24
                encodeToVideoTrack(outputBufferId);
                mEncoder.releaseOutputBuffer(outputBufferId, false);
            }

            int outputBufferAudioId = mAudioEncoder.dequeueOutputBuffer(mBufferInfoAudio, TIMEOUT_US);
            Log.i(TAG, "dequeue output buffer outputBufferAudioId=" + outputBufferAudioId);
            if (outputBufferAudioId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                resetAudioOutputFormat();
            } else if (outputBufferAudioId == MediaCodec.INFO_TRY_AGAIN_LATER) {
                Log.d(TAG, "retrieving buffers time out!");
                try {
                    // wait 10ms
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            } else if (outputBufferAudioId >= 0 && tital == 2) {
                if (!mMuxerStarted) {
                    throw new IllegalStateException("MediaMuxer dose not call addTrack(format) ");
                }

                /**
                 * 必须要加 2016/12/2 10:27
                 * queueInputBuffer和dequeueInputBuffer搜索是一对方法，两个要在一起使用哦。
                 首先，这一对函数的应用场合是对输入的数据流进行编码或者解码处理的时候，你会通过各种方法获得一个ByteBuffer的数组，这些数据就是准备处理的数据。
                 你要通过自己的方法找到你要处理的部分，然后调用dequeueInputBuffer方法提取出要处理的部分（也就是一个ByteBuffer数据流），把这一部分放到缓存区。
                 接下来就是你自己对于这个数据流的处理了。
                 然后在处理完毕之后，一定要调用queueInputBuffer把这个ByteBuffer放回到队列中，这样才能正确释放缓存区。
                 对于输出的数据流，同样也有一对这样的函数，叫做queueOutputBuffer和dequeueOutputBuffer，作用类似哦。
                 */
                int index = mAudioEncoder.dequeueInputBuffer(TIMEOUT_US);
                if (index >= 0 && audioRecord != null) {
                    ByteBuffer inputBuffer = codecInputBuffers[index];
                    inputBuffer.clear();
                    inputBuffer.put(buffer);

                    int size = inputBuffer.limit(); // 字节大小，必须得与new byte[2048*2]读取一致 2016/12/3 14:47

                    mAudioEncoder.queueInputBuffer(index, 0 /* offset */,
                            size, mBufferInfo.presentationTimeUs /* timeUs */, 0);
                }

                // 根据outputBufferId获取ByteBuffer后，写入文件 2016/12/1 15:24
                encodeToAudioTrack(outputBufferAudioId);
                mAudioEncoder.releaseOutputBuffer(outputBufferAudioId, false);
            }
        }
    }

    /**
     * 写入文件 2016/12/1 15:24
     * @param outputBufferId bufferId
     */
    private void encodeToVideoTrack(int outputBufferId) {
        ByteBuffer encodedData = mEncoder.getOutputBuffer(outputBufferId);
        if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
            // The codec config data was pulled out and fed to the muxer when we got
            // the INFO_OUTPUT_FORMAT_CHANGED status.
            // Ignore it.
            Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
            mBufferInfo.size = 0;
        }

        if (mBufferInfo.size == 0) {
            Log.d(TAG, "info.size == 0, drop it.");
            encodedData = null;
        } else {
            Log.d(TAG, "got buffer, info: size=" + mBufferInfo.size
                    + ", video presentationTimeUs=" + mBufferInfo.presentationTimeUs
                    + ", offset=" + mBufferInfo.offset);
        }

        if (encodedData != null) {
            encodedData.position(mBufferInfo.offset);
            encodedData.limit(mBufferInfo.offset + mBufferInfo.size);

            // 获取流数据，从ByteBuffer 获取  byte[]，可以将byte[]传至后台 2016/12/1 16:29
            byte[] bytes = new byte[encodedData.remaining()];
            encodedData.get(bytes, 0, bytes.length);
            System.out.println("Video传至后台的 byte[] length: " + bytes.length);

            mMuxer.writeSampleData(mVideoTrackIndex, encodedData, mBufferInfo);
            Log.i(TAG, "sent " + mBufferInfo.size + " bytes to muxer...");
        }
    }

    /**
     * 写入文件 2016/12/1 15:24
     * @param outputBufferId bufferId
     */
    private void encodeToAudioTrack(int outputBufferId) {
        ByteBuffer encodedData = mAudioEncoder.getOutputBuffer(outputBufferId);

        if ((mBufferInfoAudio.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
            // The codec config data was pulled out and fed to the muxer when we got
            // the INFO_OUTPUT_FORMAT_CHANGED status.
            // Ignore it.
            Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
            mBufferInfoAudio.size = 0;
        }

        if (mBufferInfoAudio.size == 0) {
            Log.d(TAG, "info.size == 0, drop it.");
            encodedData = null;
        } else {
            Log.d(TAG, "got buffer, info: size=" + mBufferInfoAudio.size
                    + ", audio presentationTimeUs=" + mBufferInfoAudio.presentationTimeUs
                    + ", offset=" + mBufferInfoAudio.offset);
        }

        if (encodedData != null) {
            encodedData.position(mBufferInfoAudio.offset);
            encodedData.limit(mBufferInfoAudio.offset + mBufferInfoAudio.size);

            // 获取流数据，从ByteBuffer 获取  byte[]，可以将byte[]传至后台 2016/12/1 16:29
            byte[] bytes = new byte[encodedData.remaining()];
            encodedData.get(bytes, 0, bytes.length);
            System.out.println("Audio传至后台的 byte[] length: " + bytes.length);

            mBufferInfoAudio.presentationTimeUs = mBufferInfo.presentationTimeUs;
            mMuxer.writeSampleData(mAudioTrackIndex, encodedData, mBufferInfoAudio);
            Log.i(TAG, "sent " + mBufferInfoAudio.size + " bytes to muxer...");
        }
    }

    private void resetOutputFormat() {
        // should happen before receiving buffers, and should only happen once
        if (mMuxerStarted) {
            throw new IllegalStateException("output format already changed!");
        }
        MediaFormat newFormat = mEncoder.getOutputFormat();

        /**
         * 2016/12/1 15:32
         * 介绍完H.264的基本原理，下面看看Android上具体的实现。
         * 其实Android系统的MediaCodec类库已经帮助我们完成了较多的工作，
         * 我们只需要在开始录制时（或每一次传输视频帧前）在视频帧之前写入SPS和PPS信息即可。
         * MediaCodec已经默认在数据流（视频帧和SPS、PPS）之前添加了start code(0x01)，我们不需要手动填写。
         */
        newFormat.getByteBuffer("csd-0");    // SPS
        newFormat.getByteBuffer("csd-1");    // PPS

        Log.i(TAG, "output format changed.\n new format: " + newFormat.toString());
        mVideoTrackIndex = mMuxer.addTrack(newFormat);

        tital++;
        if (tital == 2) {
            mMuxer.start();
            mMuxerStarted = true;
        }

        Log.i(TAG, "started media muxer, videoIndex=" + mVideoTrackIndex);
    }

    private void resetAudioOutputFormat() {
        // should happen before receiving buffers, and should only happen once
        if (mMuxerStarted) {
            throw new IllegalStateException("output format already changed!");
        }
        MediaFormat newAudioFormat = mAudioEncoder.getOutputFormat();

//        /**
//         * 2016/12/1 15:32
//         * 介绍完H.264的基本原理，下面看看Android上具体的实现。
//         * 其实Android系统的MediaCodec类库已经帮助我们完成了较多的工作，
//         * 我们只需要在开始录制时（或每一次传输视频帧前）在视频帧之前写入SPS和PPS信息即可。
//         * MediaCodec已经默认在数据流（视频帧和SPS、PPS）之前添加了start code(0x01)，我们不需要手动填写。
//         */
//        newAudioFormat.getByteBuffer("csd-0");    // SPS
//        newAudioFormat.getByteBuffer("csd-1");    // PPS

        mAudioTrackIndex = mMuxer.addTrack(newAudioFormat);

        tital++;
        if (tital == 2) {
            mMuxer.start();
            mMuxerStarted = true;
        }

        Log.i(TAG, "started media muxer, audioIndex=" + mAudioTrackIndex);
    }

    private void prepareEncoder() throws IOException {

        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);

        Log.d(TAG, "created video format: " + format);
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

        MediaFormat audioFormat = new MediaFormat();
        audioFormat.setString(MediaFormat.KEY_MIME, AUDIO_MIME_TYPE);
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);
        audioFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, 44100);
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
        audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectHE);

        mAudioEncoder = MediaCodec.createEncoderByType(AUDIO_MIME_TYPE);
        mAudioEncoder.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

        // 这一步非常关键，它设置的，是MediaCodec的编码源，也就是说，我要告诉mEncoder，你给我解码哪些流。
        mSurface = mEncoder.createInputSurface();
        Log.d(TAG, "created input surface: " + mSurface);
        mEncoder.start();
        mAudioEncoder.start();
    }

    private void release() {
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }

        if (mAudioEncoder != null) {
            mAudioEncoder.stop();
            mAudioEncoder.release();
            mAudioEncoder = null;
        }

        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }

        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
        }

        if (mMediaProjection != null) {
            mMediaProjection.stop();
        }

        if (mMuxer != null) {
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        }
    }
}
