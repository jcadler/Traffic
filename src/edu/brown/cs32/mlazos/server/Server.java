package edu.brown.cs32.mlazos.server;
import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import edu.brown.cs32.jcadler.retrieval.Retriever;
import javax.xml.parsers.SAXParserFactory;


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
	private boolean _botConnectionSuccess;
        private Retriever r;

	/**
	 * Initialize a server on the given port. This server will not listen until
	 * it is launched with the start() method.
	 * 
	 * @param port
	 * @throws IOException
	 */
	public Server(int port, int botPort, ConcurrentHashMap<String, Double> trafficDataMap, Retriever ret) throws IOException 
	{
		if (port <= 1024 || botPort <= 1024) 
		{
			throw new IllegalArgumentException("Ports under 1024 are reserved!");
		}
		
		_port = port;
		_clients = new ClientPool();
		_socket = new ServerSocket(port);
		try
		{
			_botSocket = new Socket("localhost", botPort);
			_botConnectionSuccess = true;
			_trafficInput = new BufferedReader(new InputStreamReader(_botSocket.getInputStream()));
			_trafficData = trafficDataMap;
		}
		catch(ConnectException e)
		{
			_botConnectionSuccess = false;
		}
                r=ret;
	}

	/**
	 * Wait for and handle connections indefinitely.
	 */
	public void run() 
	{
                SAXParserFactory f = SAXParserFactory.newInstance();
		_running = true;
		if(_botConnectionSuccess)
		{
			_bot = new RecieveTrafficData();
			_bot.start();
		}
		
		while(_running)
		{
			try
			{
				Socket clientConnection = _socket.accept();
				System.out.println("Connected to client.");
				new ClientHandler(_clients, clientConnection,r,f.newSAXParser()).start();
			}
			catch(IOException e)
			{
				System.out.println("Server cannot accept connections.");
                                System.out.println(e.getMessage());
                                _running=false;
			}
                        catch(Exception e)
                        {
                            System.out.println(e.getMessage());
                            _running=false;
                        }
		}
	}
	
	/**
	 * Stop waiting for connections, close all connected clients, and close
	 * this server's {@link ServerSocket}.
	 * 
	 * @throws IOException if any socket is invalid.
	 */
	public void kill() throws IOException 
	{
		_running = false;
		_clients.killall();
		_botSocket.close();
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

