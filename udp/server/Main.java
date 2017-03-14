package udp.server;

import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		UDPServer t = new UDPServer("localhost",7);
		int i=0;
		String d;
		Scanner scan = new Scanner(System.in);	
		
		while(true){
			t.receive();		
		}
		
	}
}
