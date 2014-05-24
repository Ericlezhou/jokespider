package com.htpclient.jokespider;

import java.util.HashSet;

public class LinkQueue
{
	private static HashSet<Object> visitedUrl = new HashSet<Object>();

	private static Queue unVisitedUrl = new Queue();

	// get unvisited url queue
	public static Queue getUnVisitedUrl()
	{
		return unVisitedUrl;
	}

	// add one url into the visited set
	public static void addVisitedUrl(String url)
	{
		visitedUrl.add(url);
	}

	// remove one url from the visited set
	public static void deleteVisitedUrl(String url)
	{
		visitedUrl.remove(url);
	}

	// get one unvisited url out from the queue
	public static Object unVisitedUrlDeQueue()
	{
		return unVisitedUrl.deQueue();
	}

	// get one unvisited url into the queue and 保证每个url只被访问一次
	public static void unVisitedUrlEnQueue(String url)
	{
		if (url != null && !url.trim().equals("") && !visitedUrl.contains(url)
				&& !unVisitedUrl.contains(url)) unVisitedUrl.enQueue(url);
	}

	// get the number of visitedUrls
	public static int getVisitedurlNum()
	{
		return visitedUrl.size();
	}

	// is unvisited queue empty?
	public static boolean unVisitedUrlIsEmpty()
	{
		return unVisitedUrl.isEmpty();
	}

}
