package application;
	
import java.awt.Button;
import java.awt.TextArea;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;


public class Main extends Application {
public static ExecutorService ThreadPool;
public static Vector<Client> Clients = new Vector<Client>();
ServerSocket serverSocket;


public void startServer(String IP, int port){
	try{serverSocket = new ServerSocket();
	serverSocket.bind(new InetSocketAddress(IP, port));
	}catch(Exception e) {
		e.printStackTrace();
		if(!serverSocket.isClosed()) {
			stopServer();
		}
		return;
	}
	//클 기다림	
	Runnable thread = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
				try {
					Socket socket=serverSocket.accept();
					Clients.add(new Client(socket));
					System.out.println("[접속]"+socket.getRemoteSocketAddress()+": "+Thread.currentThread().getName());
				}catch(Exception e) {
					if(!serverSocket.isClosed()) {stopServer();}break;
					}
			} 
		}
	};
	
	ThreadPool = Executors.newCachedThreadPool();
	ThreadPool.submit(thread);
}




public void stopServer(){
	try {
		Iterator<Client> iterator = Clients.iterator();
		
		while(iterator.hasNext()) {
			Client client= iterator.next();
			client.socket.close();
			iterator.remove();
		}
		if(serverSocket !=null && !serverSocket.isClosed()) {
			serverSocket.close();
		}
		if(ThreadPool != null && !ThreadPool.isShutdown()) {
			ThreadPool.shutdown();
		}
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}


	
	
	@Override
	public void start(Stage primaryStage) {
		BorderPane root = new BorderPane();
		root.setPadding(new Insets(5));
		
		javafx.scene.control.TextArea textArea = new javafx.scene.control.TextArea();
		textArea.setEditable(false);
		textArea.setFont(new Font("나눔고딕",15));
		root.setCenter(textArea);
		
		
		javafx.scene.control.Button toggleButton = new javafx.scene.control.Button("시작하기");
		toggleButton.setMaxWidth(Double.MAX_VALUE);
		BorderPane.setMargin(toggleButton, new Insets(1,0,0,0));
		root.setBottom(toggleButton);
	
		
		String IP="127.0.0.1";
		int port= 9876;
		
		toggleButton.setOnAction(event->{
			if(toggleButton.getText().equals("시작하기")) {
				startServer(IP, port);
				Platform.runLater(()->{
					String message = String.format("[서버시작]\n", IP, port);
					textArea.appendText(message);
					toggleButton.setText("종료하기");	
				});
				
			}
			else {stopServer();
			Platform.runLater(()->{
				String message = String.format("[서버종료]\n", IP, port);
				textArea.appendText(message);
				toggleButton.setText("시작하기");	
			});
}
			
			
			
		});
		
		Scene scene = new Scene(root, 400,400);
		primaryStage.setTitle("♥채팅서버");
		primaryStage.setOnCloseRequest(event->stopServer());
		primaryStage.setScene(scene);
		primaryStage.show();
	 
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
