package com.example.voice_rcd;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import com.example.voice_rcd.check_picture.copy.CopyPicture;
import com.example.voice_rcd.upload.Upload;
import com.nostra13.example.universalimageloader.Constants;
import com.nostra13.example.universalimageloader.ImagePagerSingleActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天消息适配器 2015/2/2 16:42
 */
public class ChatMsgViewAdapter extends BaseAdapter {

    int count = 0;

	public static interface IMsgViewType {
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}

	private static final String TAG = ChatMsgViewAdapter.class.getSimpleName();

	private List<ChatMsgEntity> coll;

	private Context ctx;

	private LayoutInflater mInflater;
	private MediaPlayer mMediaPlayer = new MediaPlayer();

	public ChatMsgViewAdapter(Context context, List<ChatMsgEntity> coll) {
		ctx = context;
		this.coll = coll;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return count;
	}

	public Object getItem(int position) {
		return coll.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		ChatMsgEntity entity = coll.get(position);

		if (entity.isMsgMe()) {
			return IMsgViewType.IMVT_TO_MSG;
		} else {
			return IMsgViewType.IMVT_COM_MSG;
		}

	}

	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		final ChatMsgEntity entity = coll.get(position);
		boolean isMsgMe = entity.isMsgMe();

		ViewHolder viewHolder = null;
		if (convertView == null) { // 信息类型 false在右边  true在左边
			if (isMsgMe) {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_text_right, null);
			} else {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_text_left, null);
			}

			viewHolder = new ViewHolder();
			viewHolder.tvSendTime = (TextView) convertView
					.findViewById(R.id.tv_sendtime);
			viewHolder.tvUserName = (TextView) convertView
					.findViewById(R.id.tv_username);
			viewHolder.tvContent = (TextView) convertView
					.findViewById(R.id.tv_chatcontent);
            viewHolder.rl_img = (RelativeLayout) convertView
                    .findViewById(R.id.rl_img);
            viewHolder.img = (ImageView) convertView
                    .findViewById(R.id.img);
            viewHolder.loc = (TextView) convertView
                    .findViewById(R.id.txtLoc);
            viewHolder.tvProgress = (TextView) convertView
                    .findViewById(R.id.txtProgress);
            viewHolder.btnReSend = (Button) convertView
                    .findViewById(R.id.btnReSend);
			viewHolder.tvTime = (TextView) convertView
					.findViewById(R.id.tv_time);
			viewHolder.isMsgMe = isMsgMe;

			convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.btnReSend.setVisibility(View.GONE);
        }

        // 添加点击事件 2015/2/25 16:18
        viewHolder.img.setOnClickListener(null);
        viewHolder.img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (entity != null && entity.getMsgType() == Const.CHAT_TYPE_PICTURE) {
                    _showImg(entity.getContent());
                }
            }
        });

        // 设置时间 2015/2/2 19:57
        viewHolder.tvSendTime.setText(Utils.formatDateTime(entity.getDate()));
        if (!entity.isDisplayTime())// 判断是否显示时间
            viewHolder.tvSendTime.setVisibility(View.GONE);
        else
            viewHolder.tvSendTime.setVisibility(View.VISIBLE);

        if (entity.isMsgMe()) // 设置用户名称
            viewHolder.tvUserName.setText(((MainActivity)ctx).getMyName());

        if (entity.getMsgType() == Const.CHAT_TYPE_VOICE) { // 内容为音频 2015/1/26 16:48
            viewHolder.tvContent.setText("");
            viewHolder.tvContent.setVisibility(View.VISIBLE);
            viewHolder.rl_img.setVisibility(View.GONE);
            viewHolder.tvContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing, 0);
//            viewHolder.tvContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.voice_rcd_cancel_bg, 0);
            viewHolder.tvTime.setText(entity.getVoiceTime());
        } else if (entity.getMsgType() == Const.CHAT_TYPE_WORDS){ // 内容为文本
            viewHolder.tvContent.setText(entity.getContent());
            viewHolder.tvContent.setVisibility(View.VISIBLE);
            viewHolder.rl_img.setVisibility(View.GONE);
            viewHolder.tvContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            viewHolder.tvTime.setText("");
        } else if (entity.getMsgType() == Const.CHAT_TYPE_PICTURE) { // 图片
            int direct = (entity.isMsgMe())?0:1;
            viewHolder.tvContent.setVisibility(View.GONE);
            viewHolder.rl_img.setVisibility(View.VISIBLE);
            viewHolder.loc.setVisibility(View.GONE);
            Bitmap bitmap = Utils.getBitmapFromLruCache(entity.getContent());
            if (bitmap == null) { // 存入内存，下次可以直接获取 2015/2/4 17:14
                bitmap = CopyPicture.getInstance(ctx).getBitmapByUrl(entity.getContent());
                Utils.addBitmapToLruCache(entity.getContent(), bitmap);
            }

            viewHolder.img.setImageBitmap(Utils.clipit(bitmap, direct));
            viewHolder.tvTime.setText("");
            _uploadPicture(viewHolder, entity); // 上传图片
        } else if (entity.getMsgType() == Const.CHAT_TYPE_LOCATION) { // 位置信息
            int direct = (entity.isMsgMe())?0:1;
            viewHolder.tvContent.setVisibility(View.GONE);
            viewHolder.rl_img.setVisibility(View.VISIBLE);
            viewHolder.loc.setVisibility(View.VISIBLE);
            viewHolder.loc.getBackground().setAlpha(100);
            viewHolder.loc.setText(entity.getDesc());
            Bitmap bitmap = Utils.getBitmapFromLruCache(entity.getContent());
            if (bitmap == null) { // 存入内存，下次可以直接获取 2015/2/4 17:14
                bitmap = CopyPicture.getInstance(ctx).getBitmapByUrl(entity.getContent(), 350, 280);
                Utils.addBitmapToLruCache(entity.getContent(), bitmap);
            }

            viewHolder.img.setImageBitmap(bitmap);
//            viewHolder.img.setImageBitmap(Utils.clipit(bitmap, direct));
            viewHolder.tvTime.setText("");
        }

        // 点击事件处理 2015/2/4 19:50
        viewHolder.tvContent.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if (entity.getMsgType() == 2) { // 为音频，则点击播放音频
					playMusic(entity.getContent()) ;
				}
			}
		});

        // 重发事件处理 2015/2/4 19:50
        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.btnReSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                entity.setStatus(Const.CHAT_UPLOAD_ING);
                entity.setProgress(null);
                finalViewHolder.btnReSend.setVisibility(View.GONE);
                _uploadPicture(finalViewHolder, entity); // 上传图片
            }
        });

		return convertView;
	}

    /**
     * 显示图片 2015/2/25 16:31
     * @param img 点击的图片位置
     */
    private void _showImg(String img) {
        // 查询数据库，获取所有的图片
        MainActivity main = ((MainActivity)ctx);
        List<ChatMsgEntity> lstCe = main.get_dbChatManager().getData(main.get_chatId());
        List<String> lstUrl = new ArrayList<String>();
        int idx = 0;
        int i = 0;
        for(ChatMsgEntity chatMsgEntity: lstCe) { // 塞选出图片
            if (chatMsgEntity.getMsgType() == Const.CHAT_TYPE_PICTURE) {
                lstUrl.add(chatMsgEntity.getContent());
                if (img.equals(chatMsgEntity.getContent())) { // 判断点击的位置索引
                    idx = i;
                }

                i++;
            }
        }

        String[] url = lstUrl.toArray(new String[lstUrl.size()]);
        Intent intent = new Intent(main, ImagePagerSingleActivity.class);

        Bundle bundle = new Bundle();
        bundle.putStringArray(Constants.Extra.IMAGES, url); // 所有的图片路径
        bundle.putInt("idx", idx); // 点击的位置索引

        intent.putExtra("data", bundle);

        main.startActivity(intent);
    }

    /**
     * 上传图片 2015/2/4 16:25
     * @param chatMsgEntity
     */
    private void _uploadPicture(final ViewHolder viewHolder, final ChatMsgEntity chatMsgEntity) {
        String progress = chatMsgEntity.getProgress(); // 当前上传进度
        if (chatMsgEntity.getStatus() == Const.CHAT_UPLOAD_ING
                && progress == null) { // 进行上传图片

            chatMsgEntity.setProgress("0%");
            viewHolder.img.setColorFilter(Color.parseColor("#77000000"));
            viewHolder.tvProgress.setText(chatMsgEntity.getProgress());
            Handler _handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Bundle bundle = msg.getData();
                    if (bundle == null) return;

                    System.out.println("开始上传: " + chatMsgEntity.getContent());
                    chatMsgEntity.setProgress(bundle.getString("progress"));
                    viewHolder.tvProgress.setText(chatMsgEntity.getProgress());
                }
            };

            Handler _endHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    System.out.println("结束上传: " + chatMsgEntity.getContent());
                    viewHolder.img.setColorFilter(null);
                    viewHolder.tvProgress.setText("");

                    MainActivity main = (MainActivity)ctx;

                    // 修改数据库 2015/2/4 17:40
                    chatMsgEntity.setStatus(Const.CHAT_UPLOAD_SUCCESS);
                    chatMsgEntity.setProgress("100%");
                    main.updateChatMsgEntity(chatMsgEntity);
                    main.send("发送成功", false); // 自动回复
                }
            };

            Handler _errorHandler = new Handler() { // 发送错误
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    System.out.println("上传发生错误");

                    viewHolder.img.setColorFilter(null);
                    viewHolder.tvProgress.setText("");
                    viewHolder.btnReSend.setVisibility(View.VISIBLE);

                    // 修改数据库 2015/2/4 19:12
                    chatMsgEntity.setStatus(Const.CHAT_UPLOAD_ERROR);
                    chatMsgEntity.setProgress(null);
                    ((MainActivity)ctx).updateChatMsgEntity(chatMsgEntity);
                }
            };

            // 判断文件是否存在
            if (!new File(chatMsgEntity.getContent()).exists()) {
                viewHolder.tvProgress.setText("");
                return;
            }

            // 上传至服务器 2015/2/4 17:27
            Upload.upload(chatMsgEntity.getContent(), _handler, _endHandler, _errorHandler);
        } else if (chatMsgEntity.getStatus() == Const.CHAT_UPLOAD_ING) {
            viewHolder.tvProgress.setText(progress);
            viewHolder.img.setColorFilter(Color.parseColor("#77000000"));
        } else if (chatMsgEntity.getStatus() == Const.CHAT_UPLOAD_ERROR) {
            viewHolder.btnReSend.setVisibility(View.VISIBLE);
        }
    }

	static class ViewHolder {
		public TextView tvSendTime;
		public TextView tvUserName;
		public TextView tvContent;
        public RelativeLayout rl_img;
        public ImageView img;
        public TextView loc;
        public TextView tvProgress;
		public TextView tvTime;
        public Button btnReSend;
		public boolean isMsgMe = true;
	}

	/**
	 * @Description
	 * @param name
	 */
	private void playMusic(String name) {
		try {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(name);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void stop() {

	}

}
