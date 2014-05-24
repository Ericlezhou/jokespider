package com.htpclient.jokespider;

import java.util.LinkedList;

//use the linkedlist to represent the queue storing the unvisited url  
public class Queue
{
	private LinkedList<Object> queue = new LinkedList<Object>();

	// get in the queue
	public void enQueue(Object oj)
	{
		queue.addLast(oj);
	}

	// get out the queue
	public Object deQueue()
	{
		return queue.removeFirst();
	}

	// queue is empty or not
	public boolean isEmpty()
	{
		return queue.isEmpty();
	}

	// queue contains the specified element
	public boolean contains(Object oj)
	{
		return queue.contains(oj);
	}
}
