package com.ren.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Socket_TCP_Server {

	public static void main(String[] args) throws Exception {
		//socket1();
		//socket2();
		socket3();
	}
	//多线程开启服务器，可以接收多个客户端来咨询
	private static void socket3() throws IOException {
		final ServerSocket serverSocket = new ServerSocket(12345);
		
		while(true){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					try {
						Socket socket = serverSocket.accept();
						BufferedReader br = new BufferedReader
								(new InputStreamReader(socket.getInputStream()));
						PrintStream ps = new PrintStream(socket.getOutputStream());
						
						ps.println("欢迎来到中国");
						//readLine是以换行为结束符读取一行数据的，如果客户端写入数据的时候使用了print而不是println，就会导致
						//数据无法读取
						System.out.println(br.readLine());
						ps.println("你可以先去游览北京长城");
						System.out.println(br.readLine());
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
		
		
	}

	//实现多次读取
	private static void socket2() throws Exception {
		ServerSocket serverSocket = new ServerSocket(12345);
		Socket socket = serverSocket.accept();
		
		BufferedReader br = new BufferedReader
				(new InputStreamReader(socket.getInputStream()));
		PrintStream ps = new PrintStream(socket.getOutputStream());
		
		ps.println("欢迎来到中国");
		//readLine是以换行为结束符读取一行数据的，如果客户端写入数据的时候使用了print而不是println，就会导致
		//数据无法读取
		System.out.println(br.readLine());
		ps.println("你可以先去游览北京长城");
		System.out.println(br.readLine());
		socket.close();

	}

	//实现单词读取
	private static void socket1() throws IOException {
		//注意端口号不能超过65535
		ServerSocket server = new ServerSocket(12345);
		//接收服务端数据，这个操作会阻塞线程，直到收到数据才往下执行
		Socket socket = server.accept();
		//接收到的客户端消息
		InputStream is = socket.getInputStream();
		byte[] arr = new byte[1024];
		int len = is.read(arr);
		System.out.println("收到客户端数据："+new String(arr,0,len));
		//向客户端发送消息需要获取一个输入流然后write
		OutputStream os = socket.getOutputStream();
		os.write("百度一下，你就知道".getBytes());
	}

}
