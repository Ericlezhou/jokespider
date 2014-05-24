package com.htpclient.jokespider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class Test
{
	public static void main(String[] args) throws URISyntaxException, ClientProtocolException,
			IOException
	{

		CloseableHttpClient httpclient = HttpClients.createDefault();
		URI uri = new URIBuilder().setScheme("https").setHost("www.google.com.hk")
				.setPath("/search").setParameter("q", "httpclient")
				.setParameter("btnG", "Google Search").setParameter("aq", "f")
				.setParameter("oq", "").build();

		HttpGet httpget = new HttpGet(uri);

		CloseableHttpResponse response = httpclient.execute(httpget);

		System.out.println(response);

		HttpEntity entities = response.getEntity();
		OutputStream os = new FileOutputStream(new File("result.txt"));
		entities.writeTo(os);

		/* standard inputream and outpustream read/write way */
		InputStream is = entities.getContent();
		// OutputStream os = new FileOutputStream(new File("result.txt"));

		byte[] buf = new byte[1024];
		int length = -1;
		while ((length = is.read(buf)) != -1)
			os.write(buf, 0, length);
		is.close();
		os.close();
		/*-----------------------------------------------*/
		System.out.println(entities);
		response.close();
		httpclient.close();

	}
}
