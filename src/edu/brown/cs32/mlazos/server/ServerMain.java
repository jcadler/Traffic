package edu.brown.cs32.mlazos.server;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServerMain 
{
	// Change the port number if the port number is already being used.
	private static final int DEFAULT_PORT = 7543;
	public static void main(String[] args) throws IOException {
		// Launch a chat server on the default port.
		int port = DEFAULT_PORT;
		if (args.length != 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				// Ignore it.
			}
		}

		Server server = new Server(7544, 4588, new ConcurrentHashMap<String, Double>(1000));
		server.start();

		// Listen for any commandline input; quit on "exit" or emptyline
		Scanner scanner = new Scanner(System.in);
		String line = null;
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			if (line.length() == 0 || line.equalsIgnoreCase("exit")) {
				server.kill();
				System.exit(0);
			}
		}
	}
}

