package com.ren.socket;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
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
	private DatagramSocket socket;
	private BufferedWriter writer;

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
		send = new Button("send");
		log = new Button("log");
		clear = new Button("clear");
		shake = new Button("shake");
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
		
		try {
			//初始化发送码头
			socket = new DatagramSocket();
			writer = new BufferedWriter(new FileWriter("log.txt",true));
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	private void event() {
		//关闭按钮事件
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					socket.close();
					writer.close();
					System.exit(0);
				} catch (Exception e2) {
				}
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
		//消息记录
		log.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					logFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		//清空屏幕
		log.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				viewText.setText("");
			}
		});
		//震动
		shake.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					send(new byte[]{-1}, tField.getText());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		//快捷键监听
		sendText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				//control+enter键发送消息
				if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) {
					try {
						send();
					} catch (Exception e2) {
					}
				}
				
			}
		});
	}

	//通过不断改变界面在屏幕中的位置达到震动的效果
	protected void shake() {
		int x = this.getLocation().x;
		int y = this.getLocation().y;
		
		for (int i = 0; i < 10; i++) {
			try {
				this.setLocation(x + 10, y + 10);
				Thread.sleep(20);
				this.setLocation(x + 10, y - 10);
				Thread.sleep(20);
				this.setLocation(x - 10, y + 10);
				Thread.sleep(20);
				this.setLocation(x - 10, y - 10);
				Thread.sleep(20);
				this.setLocation(x, y);
			} catch (Exception e) {
			}
		}
	}

	protected void logFile() throws IOException {
		//刷新缓冲区
		writer.flush();
		//读取消息记录文件
		FileInputStream fis = new FileInputStream("log.txt");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		int len;
		byte[] arr = new byte[8192];
		while((len = fis.read(arr))!= -1){
			baos.write(arr,0,len);
		}
		String log = baos.toString();
		viewText.setText(log);
		fis.close();
		baos.close();
	}

	protected void send() throws IOException {
		String message = sendText.getText();
		String ip = tField.getText();
		ip = ip.trim().length() == 0?"255.255.255.255":ip;
		send(message.getBytes(),ip);
		
		String time = getCurrentTime();
		String sendMessage = time+" 我对:"+(ip.equals("255.255.255.255")?"所有人":ip)+"说:\r\n "+message+"\r\n\r\n";
		viewText.append(sendMessage);
		//将发送的消息写入文件
		writer.write(sendMessage);
		sendText.setText("");
		
	}

	private void send(byte[] bytes, String ip) throws IOException {
		DatagramPacket packet = new DatagramPacket
				(bytes,bytes.length,InetAddress.getByName(ip),9999);
		socket.send(packet);
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
					
					if (arr[0] == -1 && len == 1) {
						shake();
						continue;
					}
					
					String message = new String(arr,0,len);
					String time = getCurrentTime();
					String ip = packet.getAddress().getHostAddress();
					String receivedMessage = time+" "+ip+"对我说:\r\n"+message+"\r\n";
					viewText.append(receivedMessage);
					//写入收到的消息
					writer.write(receivedMessage);
				}
			} catch (Exception e) {
			}
		}
	}

	public static void main(String[] args) {
		new GUIChat();
	}

}
