package ie.gmit.dip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A multi-threaded server side application
 * It uses two HashSet for storing user data, 
 * 'users' stores data for the user names,
 * 'TheClients' stores data for every user threads.
 * The nested class 'ClientHandler' is responsible for 
 * adding,and removing user data and broadcast user messages
 * from one thread to all other threads.
 * Enter ctrl + c to terminate the program.
 * 
 * @author Sunoj Jose
 *
 */
public class ChatServer {

	private static int port;
	private static ServerSocket theServer;
	private static Socket theSocket;
	private Set<String> users = new HashSet<String>();
	private static Set<ClientHandler> theClients = new HashSet<ClientHandler>();

	/**
	 * Receives port name as command line argument
	 * and initializes 'theServer' for the port.
	 * 'theServer' accepts connection requests from clients
	 * and an instance of ExecutorService is created to handle that user.
	 * @param args
	 */
	public static void main(String[] args) {

		ChatServer chatServer = new ChatServer();
		port = Integer.parseInt(args[0]);
		try {

			theServer = new ServerSocket(port);
			System.out.println("Connecting on port " + port + "...");
			while (true) {
				theSocket = theServer.accept();
				System.out.println("Established a connection from " + theSocket.getInetAddress() + " on port "
						+ theSocket.getPort());
				System.out.println("A new user is accepted.");
				ClientHandler newClient = new ClientHandler(theSocket, chatServer);
				theClients.add(newClient);
				ExecutorService pool = Executors.newFixedThreadPool(50);
				pool.execute(newClient);

			}
		} catch (IOException e) {
			System.out.println("Server error..." + e.getMessage());
			e.printStackTrace();
		}
	}

	
	/**
	 * This nested class is a sub class of Runnable
	 * which is responsible for maintaining client's data and sending chat data
	 * It notifies each clients when a new user got connection and when a user quits.
	 * @author Sunoj Jose
	 *
	 */
	private static class ClientHandler implements Runnable {

		Socket theSocket;
		ChatServer theServer;

		PrintWriter print_writer;

		public ClientHandler(Socket theSocket, ChatServer theServer) {
			super();
			this.theSocket = theSocket;
			this.theServer = theServer;
			try {
				this.print_writer = new PrintWriter(this.theSocket.getOutputStream(), true);
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

		/**
		 * send chat message from current thread to all other active threads
		 * by invoking broadcast method with the chat message as argument.
		 * @param message
		 * @param thisUser
		 */
		private void broadcast(String message, ClientHandler thisUser) {
			for (ClientHandler otherUser : theClients) {
				if (otherUser != thisUser) {
					otherUser.broadcast(message);
				}
			}
			
		}
		
		/**
		 * print chat message when invoked by other broadcast method
		 * @param message
		 */
		private void broadcast(String message) {
			print_writer.println(message);
		}

		/**
		 * send details(names) of the active clients to each newly created threads
		 * by using data from 'users' set
		 */
		private void displayUsers() {
			if (theServer.users.isEmpty())
				print_writer.println("Server: No Other Users Are Online Currently.");
			else
				print_writer.println("Server: " + theServer.users + ", Currenlty Online.");
		}
		
		/**
		 * When a user quits by entering Q(or q)their data is removed 
		 * from the respective sets.
		 * @param theUser
		 * @param thisUser
		 */
		private void removeUser(String theUser, ClientHandler thisUser) {
			boolean isRemoved = theServer.users.remove(theUser);
			if (isRemoved) {
				theClients.remove(thisUser);
			}
		}

		/**
		 * This method is responsible for handling activities related to 
		 * each threads of clients 
		 */
		public void run() {

			try {

				BufferedReader br = new BufferedReader(new InputStreamReader(theSocket.getInputStream()));

				displayUsers();
				
				String user = br.readLine();
					
				synchronized (theServer) {
					theServer.users.add(user);
				}

				String server_message = "Server: New User, " + user + " ,joins...";
				broadcast(server_message, this);
				String user_message;

				while (true) {
					user_message = br.readLine();
					if (!(user_message.equalsIgnoreCase("Q"))) {
						server_message = user + ":" + user_message;
						broadcast(server_message, this);
					} else {
						removeUser(user, this);
						theSocket.close();
						server_message = "Server: " + user + " quits...";
						broadcast(server_message, this);
					}
				}

			} catch (IOException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}

		}

	}

}
