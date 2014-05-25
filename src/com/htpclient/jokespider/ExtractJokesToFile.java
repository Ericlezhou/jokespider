package com.htpclient.jokespider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class ExtractJokesToFile implements Runnable
{
	private String filename = null;
	private String rs = null;

	public ExtractJokesToFile(String filename, String rs)
	{
		this.filename = filename;
		this.rs = rs;
	}

	@Override
	public void run()
	{
		String mark1 = "article-title";
		String mark2 = "article-text";

		StringBuilder sb = new StringBuilder(1024 * 10);

		InputStream is;
		try
		{
			is = new FileInputStream(new File(filename));

			InputStreamReader isr = new InputStreamReader(is,
					Charset.forName("gbk"));

			BufferedReader br = new BufferedReader(isr, 8192 * 10);

			String line = null;

			boolean isFind = false;
//			sb.append(filename);
			sb.append("Title: \n");
			while ((line = br.readLine()) != null)
			{
		
				if (line.contains(mark1))
				{
					int index = line.indexOf(">");
					int index2 = line.indexOf("</");
					sb.append((line.substring(index + 1, index2)).replaceAll(
							"<.*?>|\\s+", "") + "\n");
				}
				if (line.indexOf(mark2) >= 0)
				{
					if((line = br.readLine()) != null && !line.contains("src="))
					{
						sb.append("Content: " + line.replaceAll("<.*?>|\\s+", "")
								+ "\n\n");
						isFind = true;
						break;
					}
						isFind = false;
						break;
				}
			}
			if (isFind)
			{
				java.io.RandomAccessFile rsf = new RandomAccessFile(rs, "rw");
				FileChannel fc = rsf.getChannel();
				fc.force(true);
//				FileLock fl = fc.lock();
				ByteBuffer bb = ByteBuffer.allocate(sb.length() * 2);
				bb.put(sb.toString().getBytes());
				bb.flip();
				fc.write(bb, rsf.length());
//				fl.release();
				fc.close();
				rsf.close();
			}
			System.out.println(Thread.currentThread().getName() + " 对 "
					+ filename + "处理完毕 at " + System.currentTimeMillis());

		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
