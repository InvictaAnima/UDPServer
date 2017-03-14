package udp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;

public class UDPServer {
	
	private byte[] buffer;
	private int serverPort;
	private InetAddress serverAddress;	
	private DatagramSocket datagramSocket;
	DatagramPacket datagramPacket;
	boolean connectionExist;
	InetAddress clientAddress;
	LinkedList <Connection> connections;
	
	public UDPServer(String hostname, int serverPort){
		buffer = null;
		this.serverPort = serverPort;
		
		try {
			this.serverAddress = InetAddress.getByName(hostname);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			
		}
		
		try {
			this.datagramSocket = new DatagramSocket(serverPort);
		} catch (SocketException e) {
			e.printStackTrace();
		}		
		
		connectionExist = false;
		datagramPacket = null;
		clientAddress = null;
		connections = new LinkedList<>();
	}
	
	public void send(String message){
		buffer = new byte[65508]; 
		buffer = (message).getBytes();
		datagramPacket = new DatagramPacket(buffer, buffer.length ,serverAddress, serverPort);
		
		try {
			datagramSocket.send(datagramPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void receive(){
		buffer = new byte[65508]; 
		datagramPacket = new DatagramPacket(buffer, buffer.length);
		
		try {
			datagramSocket.receive(datagramPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println(new String(datagramPacket.getData()).substring(0, datagramPacket.getLength()));
		
		clientAddress = datagramPacket.getAddress();
		connectionExist = false;
		
		for(Connection c : connections){
			if(c.getClientAddress().equals(clientAddress)){
				System.out.println("istniejacy");
				connectionExist = true;
				c.perform(datagramPacket.getData());				
			}
		}
		
		if(connectionExist == false){
			System.out.println("nowy");
			Connection tmp = new Connection(datagramPacket.getPort(),datagramPacket.getAddress());
			connections.add(tmp);
			tmp.perform(datagramPacket.getData());			
		}
		
	}
	
	public void print(){
		System.out.println(serverAddress);
	}
	
	
	
	private class Connection{
		private int clientPort;
		private InetAddress clientAddress;
		private CircularBuffer lastReceived;
		
		public Connection(int clientPort, InetAddress clientAddress){
			this.clientPort = clientPort;
			this.clientAddress = clientAddress;
			lastReceived = new CircularBuffer(33);
		}
		
		public void perform(byte[] message){
			int i = (message[0]<<24)&0xff000000|
				       (message[1]<<16)&0x00ff0000|
				       (message[2]<< 8)&0x0000ff00|
				       (message[3]<< 0)&0x000000ff;
			//System.out.println(new String(message).substring(0, 8));
			System.out.println(i);
			
		}
		
		public InetAddress getClientAddress(){
			return clientAddress;
		}
		
		private class CircularBuffer{
			int readIndex;
			int writeIndex;
			int size;
			int buffer[];
			
			public CircularBuffer(int size){
				this.readIndex = 0;
				this.writeIndex = 0;
				this.size = size;
				this.buffer = new int[size];
			}
			
			public void write(int value){
				buffer[writeIndex] = value;
				readIndex = writeIndex;
				
				writeIndex ++;
				writeIndex %= size; 
			}
			
			public int read(){
				return buffer[readIndex];
			}
			
			public String readAll(){
				String all = "";
				int index = readIndex;
				for(int i=0;i<size;i++){			
					all += "(" + buffer[index] + ", " + index + "), ";
					
					index--;;
					if(index < 0) index = size - 1;
				}
				
				return all;
			}
		}
		
		
	}
	
	
}
