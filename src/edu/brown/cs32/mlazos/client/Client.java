package edu.brown.cs32.mlazos.client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Scanner;

/**
 * A Client Class that sends and receives messages from and to the server.
 */
public class Client {

	private Socket _socket;
	private boolean _running;
	private int _port;
	private BufferedReader _input;
	private PrintWriter _output;
	private ReceiveThread _thread;

	/**
	 * Constructs a Client with the given port.
	 * 
	 * @param port the port number the client will connect to
	 */
	public Client(int port)
	{
		_port = port;
	}
	
	/**
	 * Starts the Client, so it connects to the sever.
	 * It will set up all the necessary requirements, 
	 * before sending and receiving messages.
	 */
	public void start()
	{
		try 
		{
			_socket = new Socket("localhost", _port);
			_input = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
			_output = new PrintWriter(_socket.getOutputStream(), true);
			_running = true;
			run();
		}
		catch (IOException ex) 
		{
			System.out.println("ERROR: Can't connect to server");
		} 
	}

	/**
	 * Starts a thread that will listen to messages sent by the
	 * server. It will use the main thread to send messages to the server.
	 */
	private void run()
	{
		// Listen for any commandline input; quit on "exit" or emptyline
		_thread = new ReceiveThread();
		_thread.start();
		
		
		while(true)
		{
			try 
			{
				BufferedReader consoleRead = new BufferedReader(new InputStreamReader(System.in));	
				String line = consoleRead.readLine();
				if(line.equals("") || line.equals("exit"))
				{
					kill();
					return;
				}
				send(line);
			} 
			catch (IOException e) 
			{
				System.out.println("Cannot output to server.");
			}
		}
		               
	}

	/**
	 * A method that sends a message to the server.
	 * 
	 * @param message that will be sent to the server for broadcasting.
	 */
	public void send(String message) 
	{
		//TODO: Set up the methods, so it will send the message to the server
		_output.println(message);
		_output.flush();
	}

	/**
	 * Shuts down the client closing all the connections.
	 */
	public void kill() throws IOException {
		//TODO: Close all the streams after the client disconnects.
		_running = false;
		_socket.close();
		_output.close();
		_input.close();
	}

	/**
	 * A thread that will receive the messages sent by the server to
	 * display to the user.
	 */
	class  ReceiveThread extends Thread {
        public void run() 
        {
        	//TODO: Receive all the messages sent by the socket and display it
        	//to the client.
        	while(_running)
        	{
        		String response;
        		
        		if(!_running)
        		{
        			return;
        		}
        		
        		try 
        		{
        			response = _input.readLine();
        			System.out.println(response);
        		} 
        		catch (IOException e) 
        		{
        			// TODO Auto-generated catch block
        			System.out.println("Can't recieve messages.");
        			break;
        		}
        	
        	}
        }
}
}
