package com.ren.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Socket_TCP_Client {

	public static void main(String[] args) throws UnknownHostException, IOException {
		//socket1();
		socket2();
	}
	//多个
	private static void socket2() throws UnknownHostException, IOException {
		Socket socket = new Socket("127.0.0.1",12345);
		BufferedReader br = new BufferedReader
				(new InputStreamReader(socket.getInputStream()));
		PrintStream ps = new PrintStream(socket.getOutputStream());
		
		System.out.println(br.readLine());
		ps.println("我可以去哪里逛逛呢");
		System.out.println(br.readLine());
		ps.println("谢谢");
		socket.close();
		
	}

	//一次读取一个
	private static void socket1() throws UnknownHostException, IOException {
		Socket socket = new Socket("127.0.0.1",12345);
		//输入流可以读取服务端输出流写出的数据
		InputStream in = socket.getInputStream();
		//输出流可以写出数据到服务端输入流
		OutputStream out = socket.getOutputStream();
		out.write("打开百度首页".getBytes());
		byte[] arr = new byte[1024];
		int len = in.read(arr);
		System.out.println("收到服务端数据："+new String(arr,0,len));
	}

}
