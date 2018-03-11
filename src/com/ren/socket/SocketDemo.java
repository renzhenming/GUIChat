package com.ren.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
/**
 * socket好比发货的码头，packet就是需要发送的集装箱货物，这个货物需要发送到哪里，它的地址
 * 信息是在货物上标明的，所以packet构造方法上传递了发送目的地的地址和该地址上接收程序的端口
 * @author renzhenming
 *
 */
public class SocketDemo {

	public static void main(String[] args) throws IOException {
		new Receive().start();
		new Sender().start();
	}
}

class Receive extends Thread{
	@Override
	public void run() {
		super.run();
		try {
			//用于接收传递过来的数据
			DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
			//指定端口号，用于接收指定端口传递的数据
			DatagramSocket socket = new DatagramSocket(9999);
					
			while(true){
				//执行之后会将接收到的数据放在packet中
				socket.receive(packet);
				//转换成字符串打印
				byte[] arr = packet.getData();
				int len = packet.getLength();
				String ip = packet.getAddress().getHostAddress();
				int port = packet.getPort();
				System.out.println(ip+":"+port+":"+new String(arr,0,len));
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class Sender extends Thread{
	@Override
	public void run() {
		super.run();
		try {
			Scanner sc = new Scanner(System.in);
			DatagramSocket socket = new DatagramSocket();
			
			while(true){
				String scan = sc.nextLine();
				DatagramPacket packet = 
						new DatagramPacket(scan.getBytes(), //或者用127.0.0.1也可以
								scan.getBytes().length, InetAddress.getByName("192.168.1.103"), 9999);
				socket.send(packet);
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}















