package GossipTool;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class product {
	private static Socket client = null;
	private static OutputStream out = null;
	private static String IP = "127.0.0.1";
	public  void send(String str){
		try {
			 
			//Scanner sc = new Scanner(System.in);
				
				//InputStream in = client.getInputStream();
				out.write(str.getBytes());
				out.flush();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
//			try {
//				//out.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
	}
	
	public product(String iP) {
		super();
		IP = iP;
	}
	

	public product() {
		super();
		
	}

	public static void main(String[] args) throws UnknownHostException, IOException {
		product p = new product();
		client = new Socket(IP,6789);//创建流套接字并将其连接到指定IP地址的指定端口号。
		out = client.getOutputStream();
		//p.send("张");
		Scanner sc = new Scanner(System.in);
		while(sc.hasNextLine()){
			String str= sc.nextLine();
			for (int i = 0; i < 10000; i++) {
				p.send(str+"\n");
			}
			
		}
	}
}
