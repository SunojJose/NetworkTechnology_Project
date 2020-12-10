package ie.gmit.dip;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * A multi-threaded client side application
 * Uses two different threads for inbound and outbound communication.
 * For handling these tasks it uses two nested classes.
 * Enter Q or q to terminate this application.
 * 
 * @author Sunoj Jose
 *
 */

public class ChatClient {

	private static Socket theSocket;
	private String theClient;

	/**
	 * Receives hostName as first argument and portName as second.
	 * If args[0] is an empty String then takes localhost as the host.
	 * Initializes theSocket and the two threads for communication.
	 * @param args
	 */
	public static void main(String[] args) {
		String hostName;
		int portName;

		if (args.length > 0) {
			hostName = args[0];
		} else {
			hostName = "localhost";
		}

		portName = Integer.parseInt(args[1]);

		try {
			System.out.println("Connecting" + hostName + " on port " + portName + "...");
			theSocket = new Socket(hostName, portName);
			System.out.println("Established a connection to " + theSocket.getRemoteSocketAddress());

			OutboundTaskHandler taskOut = new OutboundTaskHandler(theSocket, new ChatClient());
			Thread thread = new Thread(taskOut);
			thread.start();

			InboundTaskHandler taskIn = new InboundTaskHandler(theSocket, new ChatClient());
			Thread trd = new Thread(taskIn);
			trd.start();

		} catch (UnknownHostException e) {

			System.err.println(e);
		} catch (IOException e) {

			System.err.println(e);
		}

	}

	/**
	 * Set the name of current user
	 * @param user
	 */
	public void setClient(String user) {
		this.theClient = user;
	}

	/**
	 * To access current user name
	 * @return
	 */
	public String getClient() {
		return this.theClient;
	}

	/**
	 * A nested class to handle threads of incoming messages
	 * 
	 * @author sunoj Jose
	 *
	 */
	private static class InboundTaskHandler implements Runnable {

		Socket theSocket;
		ChatClient chatClient;
		BufferedReader br;

		/**
		 * Constructor
		 * @param theSocket
		 * @param user
		 */
		public InboundTaskHandler(Socket theSocket, ChatClient user) {
			super();
			this.theSocket = theSocket;
			this.chatClient = user;
			try {
				this.br = new BufferedReader(new InputStreamReader(this.theSocket.getInputStream()));
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}

		/**
		 * Handles the thread for incoming communication-
		 * reads and prints the chats- until the user quits
		 */
		public void run() {

			try {
				
				String input = null;

				while ((input = br.readLine()) != null) {
					System.out.println("\n" + input);
					if (chatClient.getClient() != null) {
						System.out.print(chatClient.getClient() + ": ");
					}
				}
			} catch (IOException e) {
				System.err.println(e);
			}

		}

	}

	/**
	 * A nested class to handle threads of out bound chats
	 * 
	 * @author Sunoj Jose
	 *
	 */
	private static class OutboundTaskHandler implements Runnable {

		Socket theSocket;
		ChatClient chatClient;

		PrintWriter print_writer;

		/**
		 * Constructor
		 * @param theSocket
		 * @param user
		 */
		public OutboundTaskHandler(Socket theSocket, ChatClient user) {
			super();
			this.theSocket = theSocket;
			this.chatClient = user;
			try {
				this.print_writer = new PrintWriter(this.theSocket.getOutputStream(), true);
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

		/**
		 * Reads and sends chats to the server until the user enter Q or q
		 */
		public void run() {

			try {
				
				Console c = System.console();
				String userName = c.readLine("\nServer: Please enter a name> ");

				chatClient.setClient(userName);
				System.out.println("Server: Hi, " + userName + " ,Please enter your message OR enter q to quit>");
				print_writer.println(userName);

				while (true) {
					String output = c.readLine(userName + ": ");
					print_writer.println(output);
					print_writer.flush();
					if (output.equalsIgnoreCase("Q")) {
						System.out.println("Server: " + userName + " quits...");
						break;
					}
				}
				theSocket.close();
			} catch (IOException e) {
				System.err.println(e);
			}

		}

	}

}
