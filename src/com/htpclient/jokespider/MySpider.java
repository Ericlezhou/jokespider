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
			// 下载网页,并把路径保存在数组中
			downLoader.downloadFile(urlToVisit);
//			filePathList.add(downLoader.downloadFile(urlToVisit));

			// 该 URL 放入已访问的 URL 中
			LinkQueue.addVisitedUrl(urlToVisit);
			// 提取出下载网页中的 URL
			Set<String> links = HtmlParseTool.extracLinks(urlToVisit,
					linkStringFilter);
			// 新的未访问的 URL 入队
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
		System.out.println("开始进行查找的时间:" + System.currentTimeMillis());

		String outputPath = "rs.txt";

		for (String filePath : mySpider.filePathList)
		{
			threadPool.execute(new ExtractJokesToFile(filePath, outputPath));
		}
		threadPool.shutdown(); // 关闭线程池 if(!mySpider.filePathList.isEmpty())
	}
}
