package com.htpclient.jokespider;

import java.io.IOException;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;
import org.htmlparser.filters.LinkStringFilter;

public class MySpider
{
	private void initCrawlerWithSeeds(String[] seeds)
	{
		for (int i = 0; i < seeds.length; i++)
		{
			LinkQueue.unVisitedUrlEnQueue(seeds[i]);
		}
	}

	public void crawling(String[] seeds) throws ClientProtocolException, IOException
	{
		LinkStringFilter linkStringFilter = new LinkStringFilter("http://xiaohua.zol.com.cn/de");

		initCrawlerWithSeeds(seeds);

		while (!LinkQueue.unVisitedUrlIsEmpty() && LinkQueue.getVisitedurlNum() < 100)
		{
			String urlToVisit = (String) LinkQueue.unVisitedUrlDeQueue();

			if (urlToVisit == null) continue;

			DownloadFile downLoader = new DownloadFile();
			// ������ҳ
			downLoader.downloadFile(urlToVisit);
			// �� URL �����ѷ��ʵ� URL ��
			LinkQueue.addVisitedUrl(urlToVisit);
			// ��ȡ��������ҳ�е� URL
			Set<String> links = HtmlParseTool.extracLinks(urlToVisit, linkStringFilter);
			// �µ�δ���ʵ� URL ���
			for (String link : links)
			{
				LinkQueue.unVisitedUrlEnQueue(link);
			}

		}
	}

	public static void main(String[] args) throws IOException
	{
		new MySpider().crawling(new String[] { "http://xiaohua.zol.com.cn/" });

	}
}
