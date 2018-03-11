package com.ren.socket;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Date;
import java.text.SimpleDateFormat;


public class GUIChat extends Frame{
	
	private static final long serialVersionUID = 1L;
	private Button send;
	private Button log;
	private Button clear;
	private Button shake;
	private TextArea viewText;
	private TextArea sendText;
	private TextField tField;

	public GUIChat(){
		Init();
		southPanel();
		centerPanel();
		event();
	}

	private void centerPanel() {
		Panel center = new Panel();
		//显示的文本区域
		viewText = new TextArea();
		//发送的文本区域
		sendText = new TextArea();
		//设置为边界布局管理器
		center.setLayout(new BorderLayout());
		//发送的文本区域放在南边
		center.add(sendText, BorderLayout.SOUTH);
		//显示区域放在中间
		center.add(viewText, BorderLayout.CENTER);
		//设置不可以编辑
		viewText.setEditable(false);
		//设置背景色
		viewText.setBackground(Color.WHITE);
		//设置字体大小
		sendText.setFont(new Font("xxx", Font.PLAIN, 15));
		viewText.setFont(new Font("xxx", Font.PLAIN, 15));
		//将这个panel设置在中间位置
		this.add(center,BorderLayout.CENTER);
		
	}

	private void southPanel() {
		Panel south = new Panel();
		//用于输入聊天对象的IP地址
		tField = new TextField(15);
		tField.setText("127.0.0.1");
		send = new Button("发送");
		log = new Button("记录");
		clear = new Button("清屏");
		shake = new Button("震动");
		//添加如布局
		south.add(tField);
		south.add(send);
		south.add(log);
		south.add(clear);
		south.add(shake);
		
		this.add(south, BorderLayout.SOUTH);
		
	}

	private void Init() {
		this.setLocation(500, 50);
		this.setSize(400,600);
		this.setVisible(true);
		//开启接收消息线程
		new Receive().start();
	}
	

	private void event() {
		//关闭按钮事件
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		//发送按钮事件
		send.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					send();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}


	protected void send() throws IOException {
		String message = sendText.getText();
		String ip = tField.getText();
		DatagramSocket socket = new DatagramSocket();
		DatagramPacket packet = new DatagramPacket
				(message.getBytes(),message.getBytes().length,InetAddress.getByName(ip),9999);
		socket.send(packet);
		
		String time = getCurrentTime();
		viewText.append(time+" 我对:"+ip+"说:\r\n "+message+"\r\n\r\n");
		sendText.setText("");
		
	}

	private String getCurrentTime() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
		return format.format(date);
	}
	
	//接收消息的线程
	private class Receive extends Thread{
		@Override
		public void run() {
			super.run();
			try {
				DatagramSocket socket = new DatagramSocket(9999);
				DatagramPacket packet = new DatagramPacket(new byte[8192], 8192);
				while(true){
					socket.receive(packet);
					byte[]arr = packet.getData();
					int len = packet.getLength();
					String message = new String(arr,0,len);
					String time = getCurrentTime();
					String ip = packet.getAddress().getHostAddress();
					viewText.append(time+" "+ip+"对我说:\r\n"+message+"\r\n");
				}
			} catch (Exception e) {
			}
		}
	}

	public static void main(String[] args) {
		new GUIChat();
	}

}
