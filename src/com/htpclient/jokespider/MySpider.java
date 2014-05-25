package com.htpclient.jokespider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.ClientProtocolException;
import org.htmlparser.filters.LinkStringFilter;

public class MySpider
{
	private ArrayList<String> filePathList = new ArrayList<String>();
	private static ThreadPoolExecutor threadPool = null;

	private void initCrawlerWithSeeds(String[] seeds)
	{
		for (int i = 0; i < seeds.length; i++)
		{
			LinkQueue.unVisitedUrlEnQueue(seeds[i]);
		}
	}

	private void getFileList(File file)
	{
		ArrayList<String> fileList = new ArrayList<String>();
		
		if (file != null && file.isDirectory())
		{
			File[] fileArr = file.listFiles();
			for (File f : fileArr)
				fileList.add(file.getName() + "\\" + f.getName());
		}
		this.filePathList = fileList;
	}

	public void crawling(String[] seeds) throws ClientProtocolException,
			IOException
	{
		LinkStringFilter linkStringFilter = new LinkStringFilter(
				"http://xiaohua.zol.com.cn/detail");

		initCrawlerWithSeeds(seeds);

		while (!LinkQueue.unVisitedUrlIsEmpty()
				&& LinkQueue.getVisitedurlNum() < 1000)
		{
			String urlToVisit = (String) LinkQueue.unVisitedUrlDeQueue();

			if (urlToVisit == null) continue;

			DownloadFile downLoader = new DownloadFile();
			// ������ҳ,����·��������������
			downLoader.downloadFile(urlToVisit);
//			filePathList.add(downLoader.downloadFile(urlToVisit));

			// �� URL �����ѷ��ʵ� URL ��
			LinkQueue.addVisitedUrl(urlToVisit);
			// ��ȡ��������ҳ�е� URL
			Set<String> links = HtmlParseTool.extracLinks(urlToVisit,
					linkStringFilter);
			// �µ�δ���ʵ� URL ���
			for (String link : links)
			{
				LinkQueue.unVisitedUrlEnQueue(link);
			}

		}
	}

	public static void main(String[] args) throws IOException
	{
		MySpider mySpider = new MySpider();
		mySpider.crawling(new String[] { "http://xiaohua.zol.com.cn/new/" });

		File file = new File("temp");
		mySpider.getFileList(file);
		
		threadPool = new ThreadPoolExecutor(10, 10 * 2, 6, TimeUnit.SECONDS,
				new ArrayBlockingQueue(100),
				new ThreadPoolExecutor.DiscardOldestPolicy());
		System.out.println("��ʼ���в��ҵ�ʱ��:" + System.currentTimeMillis());

		String outputPath = "rs.txt";

		for (String filePath : mySpider.filePathList)
		{
			threadPool.execute(new ExtractJokesToFile(filePath, outputPath));
		}
		threadPool.shutdown(); // �ر��̳߳� if(!mySpider.filePathList.isEmpty())
	}
}
