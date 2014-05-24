package com.htpclient.jokespider;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.LinkStringFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;

public class HtmlParseTool
{
	public static Set<String> extracLinks(String url, LinkStringFilter filter)
	{
		Set<String> links = new HashSet<String>();

		try
		{
			Parser parser = new Parser(url);

			parser.setEncoding("gb2312");

			NodeFilter frameFilter = new NodeFilter()
			{

				@Override
				public boolean accept(Node node)
				{
					if (node.getText().startsWith("frame src="))
					{
						return true;
					}
					else
					{
						return false;
					}
				}
			};
			OrFilter linkFilter = new OrFilter(new NodeClassFilter(LinkTag.class), frameFilter);

			NodeList list = parser.extractAllNodesThatMatch(linkFilter);

			for (int i = 0; i < list.size(); i++)
			{
				Node tag = list.elementAt(i);
				// <a>
				if (tag instanceof LinkTag)
				{
					LinkTag linkTag = (LinkTag) tag;

					if (filter.accept(linkTag))
					{
						String linkUrl = linkTag.getLink();
						links.add(linkUrl);
					}
				}
				else
				// <frame>
				{
					String frame = tag.getText();
					int start = frame.indexOf("src=");
					if (start == -1) break;
					frame = frame.substring(start);
					int end = frame.indexOf(" ");
					if (end == -1)
					{
						end = frame.indexOf(">");
					}

					String frameUrl = frame.substring(5, end - 1);

					if (filter.accept(tag))
					{
						links.add(frameUrl);
					}
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return links;
	}
}
