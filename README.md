# NetworkTechnology_Project
A Multi-User Chat Application In Java

This project contains two executable Java classes; ChatSever, and ChatClient.

ChatServer: An executable class deals multi-thread communication.  
  •	An infinite while loop accepts connection requests and assigns threads
  •	Uses two HashSets for user data – adds user when connection occurs and removes when they quitted. 
  •	Display users who are already online, when a new user enters
  •	The static nested class UserHandler implements Runnable is used for handling threads using overridden run() method.
  •	The run method also uses an infinite loop to broadcast messages
  •	This loop for the current thread terminates when this user enters 'q' irrespective of the case
  •	press ctrl + c to terminate this application
  •	Must be run before ChatClient
To compile and run: javac ie/gmit/dip/ChatServer.java; java ie.gmit.dip.ChatServer [port_name];
where port_name is an integer.

ChatClinet: An executable class handles multi-user communication.
  •	Takes command line arguments hostname and portname where args[0] is hostname. If args[0] = null, then localhost is assigned.
  •	Initializes theSocket and the two threads for communication.
  •	Has two static nested classes Implements  Runnable Interface for handling user chat threads
  •	The static nested class InboundTaskHandler handles incoming messages uses the overridden run() method of Runnable Interface
  •	The static nested class OutboundTaskHandler handles outbound messages uses the same method
  •	Calls the  thread instances(for incoming and outgoing) from main()
  •	User can stop chat by entering q or Q
  •	Must be run after ChatServer
To compile and run: javac ie/gmit/dip/ChatClient.java; java ie.gmit.dip.ChatClient [host_name] [port_name];
where host_name is a String and  port_name is an integer.


