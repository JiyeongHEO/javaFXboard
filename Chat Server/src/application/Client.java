package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class Client {
Socket socket;
public Client(Socket socket){ this.socket=socket; receive();}
public void receive(){
	Runnable thread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				while(true) {
					InputStream in = socket.getInputStream();
					byte[] buffer = new byte[512];
					int length = in.read(buffer);
					if(length==-1) throw new IOException(); 
					System.out.println("[수신성공]"+socket.getRemoteSocketAddress()+":"+Thread.currentThread().getName());
					String message = new String(buffer,0,length,"UTF-8");
					for(Client client: Main.Clients) {client.send(message);}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				try {
					System.out.println("[오류]"+socket.getRemoteSocketAddress()+":"+Thread.currentThread().getName());
				}catch(Exception e2){e2.printStackTrace();}
			}
			
		}
	}; 
	Main.ThreadPool.submit(thread);
}





public void send(String message){
	Runnable thread = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				OutputStream out= socket.getOutputStream(); 
				byte[] buffer = message.getBytes("UTF-8");
				out.write(buffer);
				out.flush();
				
			} catch (Exception e) {
				try {
					// TODO Auto-generated catch block
					System.out.println("[오류]"+socket.getRemoteSocketAddress()+":"+Thread.currentThread().getName());
					Main.Clients.remove(Client.this);
					socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}		
			
		}
	};
	Main.ThreadPool.submit(thread);
	
	
}
}
