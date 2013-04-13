package edu.brown.cs32.mlazos.server;
import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * A chat server, listening for incoming connections and passing them
 * off to {@link ClientHandler}s.
 */
public class Server extends Thread 
{

	private int _port;
	private ServerSocket _socket;
	private Socket _botSocket;
	private ClientPool _clients;
	private RecieveTrafficData _bot;
	private BufferedReader _trafficInput;
	private boolean _running;
	private	ConcurrentHashMap<String, Double> _trafficData;

	/**
	 * Initialize a server on the given port. This server will not listen until
	 * it is launched with the start() method.
	 * 
	 * @param port
	 * @throws IOException
	 */
	public Server(int port, int botPort, ConcurrentHashMap<String, Double> trafficDataMap) throws IOException 
	{
		if (port <= 1024 || botPort <= 1024) 
		{
			throw new IllegalArgumentException("Ports under 1024 are reserved!");
		}
		
		
		_port = port;
		_clients = new ClientPool();
		_socket = new ServerSocket(port);
		_botSocket = new Socket("localhost", botPort);
		_trafficInput = new BufferedReader(new InputStreamReader(_botSocket.getInputStream()));
		_trafficData = trafficDataMap;
		
	}

	/**
	 * Wait for and handle connections indefinitely.
	 */
	public void run() 
	{
		_running = true;
		//TODO: Set up a while loop to receive all the socket connection
		//requests made by a client
	
		_bot = new RecieveTrafficData();
		_bot.start();
		
		while(_running)
		{
			try
			{
				Socket clientConnection = _socket.accept();
				System.out.println("Connected to client.");
				new ClientHandler(_clients, clientConnection).start();
			}
			catch(IOException e)
			{
				System.out.println("Server cannot accept connections.");
			}
			
		}
		//Helpful code:
		//	System.out.println("Connected to a client.");
		//	new ClientHandler(_clients, clientConnection).start();
	}
	
	/**
	 * Stop waiting for connections, close all connected clients, and close
	 * this server's {@link ServerSocket}.
	 * 
	 * @throws IOException if any socket is invalid.
	 */
	public void kill() throws IOException {
		_running = false;
		_clients.killall();
		_socket.close();
	}
	
	class RecieveTrafficData extends Thread
	{
		public void run()
		{
			String[] data;
			Double traffic;
			
			while(_running)
			{
				try
				{
					data = _trafficInput.readLine().split("\t");
					
					if(data.length == 2 && isParsable(data[1]))
					{
						traffic = Double.parseDouble(data[1]);
						
						if(traffic >= 1)
						{
							System.out.println(data[0] + "\t" + data[1]);
							_trafficData.put(data[0], traffic);
						}
					}
				}
				catch(IOException e){}
				catch(NumberFormatException e){}
			}
		}
	}
	
	private boolean isParsable(String s)
	{
		
		final String Digits     = "(\\p{Digit}+)";
    	final String HexDigits  = "(\\p{XDigit}+)";
    	// an exponent is 'e' or 'E' followed by an optionally 
    	// signed decimal integer.
    	final String Exp        = "[eE][+-]?"+Digits;
    	final String fpRegex    =
        ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
         "[+-]?(" + // Optional sign character
         "NaN|" +           // "NaN" string
         "Infinity|" +      // "Infinity" string

         // A decimal floating-point string representing a finite positive
         // number without a leading sign has at most five basic pieces:
         // Digits . Digits ExponentPart FloatTypeSuffix
         // 
         // Since this method allows integer-only strings as input
         // in addition to strings of floating-point literals, the
         // two sub-patterns below are simplifications of the grammar
         // productions from the Java Language Specification, 2nd 
         // edition, section 3.10.2.

         // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
         "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

         // . Digits ExponentPart_opt FloatTypeSuffix_opt
         "(\\.("+Digits+")("+Exp+")?)|"+

   		// Hexadecimal strings
   		"((" +
   		// 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
   		"(0[xX]" + HexDigits + "(\\.)?)|" +

    	// 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
    	"(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

    	")[pP][+-]?" + Digits + "))" +
         "[fFdD]?))" +
         "[\\x00-\\x20]*");// Optional trailing "whitespace"
        
    	return Pattern.matches(fpRegex, s);
	}
}

