package application;
	
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.crypto.SealedObject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;


public class Main extends Application {
	Socket socket;
	TextArea textArea;
	
	public void startClient(String IP, int port){
		Thread thread = new Thread() {
			public void run() {
				try {
					socket = new Socket(IP, port);
					receive();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					if(!socket.isClosed()) {
						stopClient();
						System.out.println("서버 접속실패");
						Platform.exit();
					}
				} 
				
				
			}
			
		};
		thread.start();
	}
	
	
	
	public void stopClient(){
		try {
			if(socket !=null && !socket.isClosed()) {
				socket.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	public void receive(){
		while(true) {
			
			try {
				InputStream in=socket.getInputStream();
				byte[] buffer = new byte[512];
				int length = in.read(buffer);
				if(length==-1) throw new IOException();
				String message = new String(buffer,0,length,"UTF-8");
				Platform.runLater(()->{
					textArea.appendText(message);		
				} );
				
				}catch(Exception e) {
					stopClient(); break;}
		}
		
		
		
	}
	public void send(String message){
		Thread thread = new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					OutputStream out = socket.getOutputStream(); 
					byte[] buffer = message.getBytes("UTF-8");
					out.write(buffer);
					out.flush();
				}  catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			}
			
		}; thread.start();
		
	}
	

	
	
	
	@Override
	public void start(Stage primaryStage) {
		BorderPane root = new BorderPane();
		root.setPadding(new Insets(5));
		 
		HBox hbox = new HBox();
		hbox.setSpacing(5);
		
		TextField username = new TextField() ;
		username.setPrefWidth(150);
		username.setPromptText("닉네임 입력하세요");
		hbox.setHgrow(username, Priority.ALWAYS);
		
		TextField IPText = new TextField("127.0.0.1");
		TextField portText = new TextField("9876");
		portText.setPrefWidth(80);
		
		hbox.getChildren().addAll(username,IPText,portText);
		root.setTop(hbox);
		
		textArea = new TextArea();
		textArea.setEditable(false);
		root.setCenter(textArea);

		
		TextField input = new TextField();
		input.setPrefWidth(Double.MAX_VALUE);
		input.setDisable(true);
		input.setOnAction(event -> {
			send(username.getText()+ ": " + input.getText() + "\n");
			input.setText("");
			input.requestFocus();
			
			
			
		});
		
		Button sendButton = new Button("보내기");
		sendButton.setDisable(true);
		sendButton.setOnAction(event-> {
			send(username.getText()+": "+input.getText()+"\n");
			input.setText("");
			input.requestFocus();
		});
		
		
		Button connectionButton = new Button("접속하기");
		connectionButton.setOnAction(evnet -> {
			if(connectionButton.getText().equals("접속하기")) {
				int port=9876;
				try {
					port= Integer.parseInt(portText.getText());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();}
				
					startClient(IPText.getText(), port);
					Platform.runLater(()->{
						textArea.appendText("[접속]\n");
					});
						connectionButton.setText("종료하기");
						input.setDisable(false);
						sendButton.setDisable(false);
						input.requestFocus();
					
					} else {
				stopClient();
				Platform.runLater(()->{
					textArea.appendText("[퇴장~]\n");
				});
					connectionButton.setText("접속하기");
					input.setDisable(true);
					sendButton.setDisable(true);
					}
			
		});
		
		BorderPane pane = new BorderPane();
		pane.setLeft(connectionButton);
		pane.setCenter(input);
		pane.setRight(sendButton);
		root.setBottom(pane);
		
		
		Scene scene = new Scene(root,400,400);
		primaryStage.setTitle("♥채팅 클라이언트");
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest(evnet -> stopClient());
		primaryStage.show();
		
	  		
		connectionButton.requestFocus();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
