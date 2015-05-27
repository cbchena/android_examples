package com.example.voice_rcd.upload;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.example.voice_rcd.upload.CustomMultipartEntity.ProgressListener;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Multipart上传
 * @author Jason
 *
 */
public class HttpMultipartPost extends AsyncTask<String, Integer, String> {

    private List<String> filePathList;
	private ProgressDialog pd;
	private long totalSize;
    private Handler _handler;
    private Handler _endHandler;
    private Handler _errorHandler;

	public HttpMultipartPost(List<String> filePathList) {
		this.filePathList = filePathList;
	}

    public HttpMultipartPost(List<String> filePathList,
                             Handler handler, Handler endHandler, Handler errorHandler) {
        this(filePathList);
        _handler = handler;
        _endHandler = endHandler;
        _errorHandler = errorHandler;
    }

	@Override
	protected void onPreExecute() {
//		pd = new ProgressDialog(context);
//		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		pd.setMessage("Uploading Picture...");
//		pd.setCancelable(false);
//		pd.show();
	}

	@Override
	protected String doInBackground(String... params) {
		String serverResponse = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext httpContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost("http://192.168.1.122:8080/FileUploadServlet/FileUpload?TAG=MLE");

		try {
			CustomMultipartEntity multipartContent = new CustomMultipartEntity(
					new ProgressListener() {
						@Override
						public void transferred(long num) {
							publishProgress((int) ((num / (float) totalSize) * 100));
						}
					});

			// We use FileBody to transfer an image
			//把上传内容添加到MultipartEntity
			for (int i = 0; i < filePathList.size(); i++) {
				multipartContent.addPart("file", new FileBody(new File(
						filePathList.get(i))));
				multipartContent.addPart("data",
	                    new StringBody(filePathList.get(i), Charset
	                                    .forName(org.apache.http.protocol.HTTP.UTF_8)));
			}
			

			
			totalSize = multipartContent.getContentLength();
			System.out.println("totalSize:========="+totalSize);

			// Send it
			httpPost.setEntity(multipartContent);
			HttpResponse response = httpClient.execute(httpPost, httpContext);
			serverResponse = EntityUtils.toString(response.getEntity());
			
		} catch (Exception e) {
			e.printStackTrace();
            if (_errorHandler != null)
                _errorHandler.sendEmptyMessage(0);
		}

		return serverResponse;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
        int result = progress[0];
        if (_handler != null) {
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("progress", result + "%");

            message.setData(bundle);
            _handler.sendMessage(message);
        }

//        pd.setProgress(result);
	}

	@Override
	protected void onPostExecute(String result) {
		System.out.println("result: " + result);
        if (_endHandler != null && result != null)
            _endHandler.sendEmptyMessage(0);

//		pd.dismiss();
	}

	@Override
	protected void onCancelled() {
		System.out.println("cancle");
        if (_errorHandler != null)
            _errorHandler.sendEmptyMessage(0);
	}

    public List<String> getFilePathList() {
        return filePathList;
    }
}
