package GossipTool;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

public class server {
	private HashMap<Socket, Integer> consumer = new HashMap<>();//存储消费者socket信息
	private int num = 10000;
	private ArrayBlockingQueue<String> message = new ArrayBlockingQueue<String>(num);//存储生产者传过来的消息
	boolean flg = true;//标记什么时候开启发送给消费者的消息的线程

	public void handle() {
		try {
			ServerSocket server = new ServerSocket(6789);//开启socket服务
			while (true) {
				Socket client = server.accept();//阻塞等待生产者或消费者的连接
				Thread t = new Thread(new Runnable() { 
					@Override
					public void run() {
						InputStream in;
						try {
							in = client.getInputStream();
							
							BufferedReader br = new BufferedReader(new InputStreamReader(in));
							String str = br.readLine();
							//System.out.println(str);
							while (str != null) { //读取生产者或消费者发过来的消息
								//System.out.println(str);
								if (str.indexOf("Consumer") != -1) {//如果是消费者就加入到hashmap
									consumer.put(client, 1);
									str = br.readLine();
								} else {//生产者的消息
									
										//for (int i = 0; i < 10000; i++) {
											if(message.size() >= num){
												Persistence();
											}
											message.add(str);
											str = br.readLine();
										//}
										if(consumer.size()==0){
											Persistence();
										}
									
									
								}
								System.out.println("consumer:" + consumer.size());
								System.out.println("message：" + message.size());
								if (consumer.size()!= 0 && message.size()==0){
									messageDispatch();
								}
								if (consumer.size()!= 0 && message.size() != 0 && flg) {//一切条件满足可以开启发送消费者消息的线程只能执行一次
									flg = false;
									messageDispatch();
								}
							}

						} catch (SocketException e) {//如果消费者掉线直接在hashmap删除该消费者的信息
							consumer.remove(client);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
				t.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void messageDispatch() { //向消费者发送消息
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				
				if(new File("C:\\Users\\Administrator\\Desktop\\message.txt").exists()){
					
					FileReader fr;
					String str = null;
					BufferedReader read = null;
					try {
						fr = new FileReader("C:\\Users\\Administrator\\Desktop\\message.txt");
						 read = new BufferedReader(fr);
						 str = read.readLine();
					
					
					while(str != null){
						ArrayList<Socket> list = new ArrayList<>(consumer.keySet());
						for(int i = 0; i < list.size();i++){
							OutputStream out = list.get(i).getOutputStream();
							out.write(str.getBytes());
							out.flush();
							str = read.readLine();
						}
				}
					read.close();
					fr.close();
					//Thread.sleep(500);
					boolean t = new File("C:\\Users\\Administrator\\Desktop\\message.txt").delete();
					if(t){
						System.out.println("磁盘文件已经全部消费完毕");
					}else{
						System.out.println(t);
					}
					} catch (FileNotFoundException e) {
						
						e.printStackTrace();
					} catch (IOException e) {
						
						e.printStackTrace();
					}
			}
				
				while (consumer.size() != 0&&message.size()!= 0) {
						consumer.forEach((k, v) -> {
							try {
								if(message.size()!=0){
									OutputStream out = k.getOutputStream();
									out.write(message.poll().getBytes());
									out.flush();	
								}
															
						
							} catch (NullPointerException e1) {
								System.out.println("出现message为空");
								
							}catch(Exception e){
								e.printStackTrace();
							}
							
							
						});
					
				}
				flg = true;
				//System.out.println("发送完毕");
				if(message.size()!= 0)
					
				Persistence();
				
			}
			
		});
		t.setPriority(10);
		t.start();
	}
	
	public void Persistence(){//持久化到磁盘
		System.out.println("测试"+message);
		while(message.size() != 0){
			FileOutputStream  fo = null;
			try {
				fo = new FileOutputStream(new File("C:\\Users\\Administrator\\Desktop\\message.txt"),true);
				 
				fo.write((message.poll()+"\n").getBytes());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				try {
					fo.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	public static void main(String[] args) {
		server server = new server();
		server.handle();
	}
}
