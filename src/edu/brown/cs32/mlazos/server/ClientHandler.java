package edu.brown.cs32.mlazos.server;
import java.io.*;
import java.net.*;

/**
 * Encapsulate IO for the given client {@link Socket}, with a group of
 * other clients in the given {@link ClientPool}.
 */
public class ClientHandler extends Thread {
	private ClientPool _pool;
	private Socket _client;
	private BufferedReader _input;
	private PrintWriter _output;
	
	/**
	 * Constructs a {@link ClientHandler} on the given client with the given pool.
	 * 
	 * @param pool a group of other clients to chat with
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
		
		//TODO: Set up the buffered reader and writer for the sockets to communicate with
		_input = new BufferedReader(new InputStreamReader(_client.getInputStream()));
		_output = new PrintWriter(_client.getOutputStream());
	}
	
	/**
	 * Send and receive data from the client. The first line received will be
	 * interpreted as the cleint's user-name.
	 */
	public void run() 
	{
		//TODO: Get the inputs sent by client and broadcast it to the rest of the
		//clients. 		
		String msg;
		String user;
		//TODO: The first input is the username of the client.
		
		_pool.add(this);
		
		try{
				user = _input.readLine();
				_pool.broadcast("User " + user + " logged in.", this);
				
				while(true)
				{
				msg = _input.readLine();
				
				_pool.broadcast(user + ": " + msg, this);
				}
		}
		catch(IOException e)
		{
			System.out.println("Cannot read from clients.");
		}
	}
		
	

	/**
	 * Send a string to the client via the socket
	 * 
	 * @param message text to send
	 */
	public void send(String message) 
	{
		//TODO: Set up the methods, so it will send the message to the client
		_output.println(message);
		_output.flush();
	}

	/**
	 * Close this socket and its related streams.
	 * 
	 * @throws IOException Passed up from socket
	 */
	public void kill() throws IOException {
		//TODO: Close all the streams after the client disconnects.
		_input.close();
		_output.close();
		_client.close();
	}
}

