package edu.brown.cs32.mlazos.client;
import java.io.*;

public class Main {
	
	// Change the port number if the port number is already being used.
	private static final int DEFAULT_PORT = 6172;
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

		Client client = new Client(port);
		client.start();
	}
}

