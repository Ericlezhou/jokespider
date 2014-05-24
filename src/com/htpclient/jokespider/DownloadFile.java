package com.htpclient.jokespider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class DownloadFile
{
	public String getFileNameByUrl(String url, String contentType)
	{
		// 移除 http:
		url = url.substring(7);
		// text/html 类型
		if (contentType.indexOf("html") != -1)
		{
			url = url.replaceAll("[\\?/:*|<>\"]", "_") + ".html";
			return url;
		}
		// 如 application/pdf 类型
		else
		{
			return url.replaceAll("[\\?/:*|<>\"]", "_") + "."
					+ contentType.substring(contentType.lastIndexOf("/") + 1);
		}
	}

	/**
	 * 保存网页字节数组到本地文件，filePath 为要保存的文件的相对地址
	 */
	private void saveToLocal(InputStream is, String filePath)
	{
		try
		{
			OutputStream os = new FileOutputStream(new File(filePath));

			byte[] buf = new byte[1024];

			int len = -1;
			while ((len = is.read(buf, 0, 1024)) != -1)
			{
				os.write(buf);
			}
			os.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	// 下载 URL 指向的网页
	@SuppressWarnings("deprecation")
	public String downloadFile(String url) throws ClientProtocolException, IOException
	{
		String filePath = null;

		CloseableHttpClient httpclient = HttpClients.createDefault();
		// 在RequestConfig当中设置连接时间和请求时间
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000)
				.setConnectionRequestTimeout(5000).build();

		HttpGet httpget = new HttpGet(url);
		// 配置get的具体信息
		httpget.setConfig(requestConfig);
		// 获取服务器发回的response
		CloseableHttpResponse response = httpclient.execute(httpget);

		int statusCode = response.getStatusLine().getStatusCode();
		// 状态码为200正常
		if (statusCode != HttpStatus.SC_OK)
		{
			System.err.println("Method failed:" + response.getStatusLine());
			filePath = null;
			// ---------
			httpget = new HttpGet("http://xiaohua.zol.com.cn" + url);
			// 配置get的具体信息
			httpget.setConfig(requestConfig);
			// 获取服务器发回的response
			response = httpclient.execute(httpget);

			statusCode = response.getStatusLine().getStatusCode();

		}
		// 获取response的实体
		HttpEntity entity = response.getEntity();
		// 使用输入流保存实体内容
		InputStream is = entity.getContent();
		// 从实体中获取contentType的内容
		Header header = entity.getContentType();
		// 由contentType的内容生成文件的保存路径
		filePath = "temp\\" + getFileNameByUrl(url, header.getValue());

		saveToLocal(is, filePath);
		// 关闭并释放连接
		response.close();

		httpclient.close();

		return filePath;

	}

}
