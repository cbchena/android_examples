package com.example.voice_rcd.upload;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

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
    private Map<String, String> _params;
    private boolean _isStart = false;

	public HttpMultipartPost(List<String> filePathList) {
		this.filePathList = filePathList;
	}

    public HttpMultipartPost(List<String> filePathList,
                             Handler handler, Handler endHandler,
                             Handler errorHandler, Map<String, String> params) {
        this(filePathList);
        _handler = handler;
        _endHandler = endHandler;
        _errorHandler = errorHandler;
        _params = params;
    }

	@Override
	protected void onPreExecute() {
//		pd = new ProgressDialog(context);
//		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		pd.setMessage("Uploading Picture...");
//		pd.setCancelable(false);
//		pd.show();
	}

    /**
     * 处理参数 2015/2/27 11:51
     * @param mapParams 参数
     * @return String
     */
    private static String getUrl(String url, Map<String, String> mapParams) {

        url += "?";
        int idx = 0;
        for (String string : mapParams.keySet()) {
            if (idx > 0)
                url += "&" + string + "=" + mapParams.get(string);
            else
                url += string + "=" + mapParams.get(string);

            idx++;
        }

        System.out.println("HttpMultipartPost's url: " + url);
        return url;
    }

	@Override
	protected String doInBackground(String... params) {
		String serverResponse = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext httpContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(getUrl("上传地址...", _params));

		try {
			CustomMultipartEntity multipartContent = new CustomMultipartEntity(
					new CustomMultipartEntity.ProgressListener() {
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

			// Send it
			httpPost.setEntity(multipartContent);
			HttpResponse response = httpClient.execute(httpPost, httpContext);
			serverResponse = EntityUtils.toString(response.getEntity());
			
		} catch (Exception e) {
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
	protected void onPostExecute(String context) {

        JSONObject jsonObject;
        String result = null;
        if (context == null) return;

        try {
            jsonObject = new JSONObject(context);
            result = jsonObject.getString("result");
        } catch (JSONException e) {
//            logger.error("End upload error.", e);
        }

//        logger.debug("End upload result code is {}.", result);
        if (_endHandler != null && "0".equals(result))
            _endHandler.sendEmptyMessage(0);

//		pd.dismiss();
	}

	@Override
	protected void onCancelled() {
        if (_errorHandler != null)
            _errorHandler.sendEmptyMessage(0);
	}

    public List<String> getFilePathList() {
        return filePathList;
    }
}
