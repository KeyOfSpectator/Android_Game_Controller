package com.example.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Socket_util {
	
	public Socket connectSocket(Socket socket){
		//addr
	 	InetAddress addr = null;
	 	try {
	 		//addr = InetAddress.getByName("10.214.25.67"); //Gypsy
	 		addr = InetAddress.getByName("192.168.109.1"); //Gypsy
	 	} catch (UnknownHostException e) {
	 		// TODO Auto-generated catch block
	 		e.printStackTrace();
	 	}

	 	//socket
	 	//port
	 	int port = 6666;
        socket = null;
		try {
			socket = new Socket(addr, port);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		return socket;
	}
	
	public void sendMessage(Socket socket , String msg) throws IOException{
		 PrintWriter out;
		 out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		 out.println(msg);
         out.flush();
	}
	
	public void closeSocket(Socket socket){
		 closeSocket(socket);
	}
	
}
