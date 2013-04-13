package edu.brown.cs32.mlazos.server;
import java.io.IOException;
import java.util.*;
/**
 * A group of {@link ClientHandler}s representing a "chat room".
 */
public class ClientPool {
	private LinkedList<ClientHandler> _clients;
	
	/**
	 * Initialize a new {@link ClientPool}.
	 */
	public ClientPool() 
	{
		_clients = new LinkedList<ClientHandler>();
	}
	
	/**
	 * Add a new client to the chat room.
	 * 
	 * @param client to add
	 */
	public synchronized void add(ClientHandler client)
	{
		_clients.add(client);
	}
	
	/**
	 * Remove a client from the pool. Only do this if you intend to clean up
	 * that client later.
	 * 
	 * @param client to remove
	 * @return true if the client was removed, false if they were not there.
	 */
	public synchronized boolean remove(ClientHandler client) 
	{
		return _clients.remove(client);
	}
	

	/**
	 * Close all {@link ClientHandler}s and empty the pool
	 */
	public synchronized void killall() 
	{

		for (ClientHandler client : _clients) {
			try {
				client.kill();
			} catch (IOException e) {
				// There's nothing we can do here.
			}
		}

		_clients.clear();
	}
}

