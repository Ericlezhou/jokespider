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
		// �Ƴ� http:
		url = url.substring(7);
		// text/html ����
		if (contentType.indexOf("html") != -1)
		{
			url = url.replaceAll("[\\?/:*|<>\"]", "_") + ".html";
			return url;
		}
		// �� application/pdf ����
		else
		{
			return url.replaceAll("[\\?/:*|<>\"]", "_") + "."
					+ contentType.substring(contentType.lastIndexOf("/") + 1);
		}
	}

	/**
	 * ������ҳ�ֽ����鵽�����ļ���filePath ΪҪ������ļ�����Ե�ַ
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

	// ���� URL ָ�����ҳ
	@SuppressWarnings("deprecation")
	public String downloadFile(String url) throws ClientProtocolException, IOException
	{
		String filePath = null;

		CloseableHttpClient httpclient = HttpClients.createDefault();
		// ��RequestConfig������������ʱ�������ʱ��
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000)
				.setConnectionRequestTimeout(5000).build();

		HttpGet httpget = new HttpGet(url);
		// ����get�ľ�����Ϣ
		httpget.setConfig(requestConfig);
		// ��ȡ���������ص�response
		CloseableHttpResponse response = httpclient.execute(httpget);

		int statusCode = response.getStatusLine().getStatusCode();
		// ״̬��Ϊ200����
		if (statusCode != HttpStatus.SC_OK)
		{
			System.err.println("Method failed:" + response.getStatusLine());
			filePath = null;
			// ---------
			httpget = new HttpGet("http://xiaohua.zol.com.cn" + url);
			// ����get�ľ�����Ϣ
			httpget.setConfig(requestConfig);
			// ��ȡ���������ص�response
			response = httpclient.execute(httpget);

			statusCode = response.getStatusLine().getStatusCode();

		}
		// ��ȡresponse��ʵ��
		HttpEntity entity = response.getEntity();
		// ʹ������������ʵ������
		InputStream is = entity.getContent();
		// ��ʵ���л�ȡcontentType������
		Header header = entity.getContentType();
		// ��contentType�����������ļ��ı���·��
		filePath = "temp\\" + getFileNameByUrl(url, header.getValue());

		saveToLocal(is, filePath);
		// �رղ��ͷ�����
		response.close();

		httpclient.close();

		return filePath;

	}

}
