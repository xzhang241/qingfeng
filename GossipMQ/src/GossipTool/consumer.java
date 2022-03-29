package GossipTool;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;

public class consumer {
	private Socket client = null;
	private OutputStream out = null;
	private String implPath = "Impl" ;
	private String IP = "127.0.0.1";
	public  void resv(){
		try {
			client = new Socket(IP,6789);
				out = client.getOutputStream();
				String str ="Consumer"+String.valueOf(new Random().nextInt(10000000))+"\n";
				InputStream in = client.getInputStream();
				out.write(str.getBytes());
				out.flush();
				while(true){
					byte [] b = new byte[1024];
					int len = in.read(b);
					String resv = new String(b,0,len);
					System.out.println(resv);
					//Service ser = (Service)Class.forName(implPath).newInstance();
					//ser.handle(resv);
				}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public consumer(String iP,String implPath) {
		super();
		IP = iP;
		this.implPath = implPath;
	}
	
	public consumer() {
		super();
		
	}
	public static void main(String[] args) {
		consumer c = new consumer("127.0.0.1","Impl");
		c.resv();
	}
}
