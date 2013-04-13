package edu.brown.cs32.mlazos.server;
import java.io.*;
import java.net.*;
import java.util.List;

import edu.brown.cs32.jcadler.nodeWay.Node;
import edu.brown.cs32.jcadler.nodeWay.Way;
import edu.brown.cs32.mlazosjcadler.drawer.Street;

/**
 * Encapsulate IO for the given client {@link Socket}, with a group of
 * other clients in the given {@link ClientPool}.
 */
public class ClientHandler extends Thread 
{
	private ClientPool _pool;
	private Boolean _running;
	private Socket _client;
	private BufferedReader _input;
	private PrintWriter _output;
	private Double minLat;
	private Double minLong;
	private Double maxLat;
	private Double maxLong;
	private String startID;
	private String endID;
	private String street1;
	private String street2;
	
	/**
	 * Constructs a {@link ClientHandler} on the given client with the given pool.
	 * 
	 * @param - the group of clients
	 * @param client the client to handle
	 * @throws IOException if the client socket is invalid
	 * @throws IllegalArgumentException if pool or client is null
	 */
	public ClientHandler(ClientPool pool, Socket client) throws IOException 
	{
		if (pool == null || client == null) 
		{
			throw new IllegalArgumentException("Cannot accept null arguments.");
		}
		
		_pool = pool;
		_client = client;
	}
	
	public void run() 
	{
		
		_running = true;
		_pool.add(this);
		
		try
		{
			while(_running) //handle request, send response; if a request is bad, kill and return.
			{
				_input.readLine();
			}
		}
		catch(IOException e)
		{
			System.out.println("Cannot read from clients.");
		}
	}

	//The following methods generate response data
	public List<Way> getWaysInRange()
	{
		return null;
	}
	
	public List<Street> findShortestPath()
	{
		return null;
	}
	
	public List<String> getNames()
	{
		return null;
	}
	
	public Node getIntersection()
	{
		return null;
	}

	/**
	 * Close this socket and its related streams.
	 * 
	 * @throws IOException Passed up from socket
	 */
	public void kill() throws IOException 
	{
		_running = false;
		_pool.remove(this);
		_input.close();
		_output.close();
		_client.close();
	}
}

