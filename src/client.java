package so_gong;

import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import java.awt.geom.*;

public class client extends Frame implements Runnable, ActionListener {
	private TextArea msgView = new TextArea("",1,1,1);
	private TextField sendBox = new TextField("");
	private TextField nameBox = new TextField();
	private TextField roomBox = new TextField("0");
	
	private Label pInfo = new Label("대기실 : 명");
	
	private java.awt.List pList = new java.awt.List();
	private Button startButton = new Button("게임 시작");
	private Button stopButton = new Button("기권");
	private Button enterButton = new Button("입장");
	private Button exitButton = new Button("대기실로");
	
	private Label infoView = new Label("인디언 포커",1);
	
	private BufferedReader reader;
	private PrintWriter writer;
	private Socket socket;
	private int roomNumber = -1;
	private String userName = null;
	
	public client(String title) {
		super(title);
		setLayout(null);
		msgView.setEditable(false);
		infoView.setBounds(10,10,480,30);
		infoView.setBackground(new Color(200,200,255));
		
		add(infoView);
		
		Panel p = new Panel();
		p.setBackground(new Color(200,255,255));
		p.setLayout(new GridLayout(3,3));
		p.add(new Label("이 름:",2));
		p.add(nameBox);
		p.add(new Label("방 번호:",2));
		p.add(roomBox);
		p.add(enterButton);
		p.add(exitButton);
		enterButton.setEnabled(false);
		p.setBounds(500,30,250,70);
		
		Panel p2 = new Panel();
		p2.setBackground(new Color(255,255,100));
		p2.setLayout(new BorderLayout());
		Panel p2_1= new Panel();
		p2_1.add(startButton);
		p2_1.add(stopButton);
		p2.add(pInfo,"North");
		p2.add(pList,"Center");
		p.add(p2_1, "South");
		startButton.setEnabled(false);
		stopButton.setEnabled(false);
		p2.setBounds(500,100,250,180);
		
		Panel p3 = new Panel();
		p3.setLayout(new BorderLayout());
		p3.add(msgView,"Center");
		p3.add(sendBox,"South");
		p3.setBounds(500,300,250,250);
		add(p);
		add(p2);
		add(p3);
		
		sendBox.addActionListener(this);
		enterButton.addActionListener(this);
		exitButton.addActionListener(this);
		startButton.addActionListener(this);
		stopButton.addActionListener(this);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent win) {
				System.exit(0);
			}
		});
		
	}
	
	public void actionPerformed(ActionEvent ac) {
		if(ac.getSource()==sendBox) {
			String msg = sendBox.getText();
			if(msg.length() == 0)
				return;
			if(msg.length() == 0)
				return;
			if(msg.length()>=30)
				msg = msg.substring(0,30);
			try {
				writer.println("[MSG]"+msg);
				sendBox.setText("");
			}
			catch(Exception ie) {
				
			}
		}
		else if(ac.getSource() == enterButton) {
			try {
				if(Integer.parseInt(roomBox.getText())<1) {
					infoView.setText("방 번호가 잘못되었습니다. 1 이상");
					return;
				}
				writer.println("[ROOM]"+Integer.parseInt(roomBox.getText()));
				msgView.setText("");
			}
			catch(Exception ie) {
				infoView.setText("입력하신 사항에 오류가 있습니다.");
			}
		}
		else if(ac.getSource() == exitButton) {
			try {
				goToWaitRoom();
				startButton.setEnabled(false);
				stopButton.setEnabled(false);
			}catch(Exception e) {}
		}
		else if(ac.getSource() == startButton) {
			try {
				writer.println("[START]");
				infoView.setText("상대의 결정을 기다리는 중 입니다,");
				startButton.setEnabled(false);
			}
			catch(Exception e) {}
		}
		else if(ac.getSource() == stopButton) {
			try {
				writer.println("[DROPGAME]");
				endGame("기권 하였습니다.");
			}
			catch(Exception e) {
				
			}
		}
		
	}


	private void endGame(String msg) {
		infoView.setText(msg);
		startButton.setEnabled(false);
		stopButton.setEnabled(false);
		
	}

	private void goToWaitRoom() {
		if(userName == null) {
			String name = nameBox.getText().trim();
			if(name.length()<=2||name.length()>10) {
				infoView.setText("이름이 잘못되었습니다. 3~10자");
				nameBox.requestFocus();
				return;
			}
			userName = name;
			writer.println("[NAME]"+userName);
			nameBox.setText(userName);
			nameBox.setEditable(false);
		}
		msgView.setText("");
		writer.println("[ROOM]0");
		infoView.setText("대기실에 입장하셨습니다.");
		roomBox.setText("0");
		enterButton.setEnabled(true);
		exitButton.setEnabled(false);
		
	}

	public void run() {
		String msg;
		try {
			while((msg = reader.readLine())!=null) {
				if(msg.startsWith("[ROOM]")) {
					if(!msg.equals("[ROOM]0")) {
						enterButton.setEnabled(false);
						exitButton.setEnabled(true);
						infoView.setText(msg.substring(6)+"번 방에 입장하셨습니다.");
					}
					else
						infoView.setText("대기실에 입장하셔습니다.");
					roomNumber = Integer.parseInt(msg.substring(6));
				}
				else if(msg.startsWith("[FULL]")) {
					infoView.setText("방이 차서 입장할 수 없습니다.");
				}
				else if(msg.startsWith("[PLAYERS]")) {
					nameList(msg.substring(9));
				}
				else if(msg.startsWith("[ENTER]")) {
					pList.add(msg.substring(7));
					playersInfo();
					msgView.append("["+msg.substring(7)+"]님이 입장하였습니다.");
				}
				else if(msg.startsWith("[EXIT]")) {
					pList.remove(msg.substring(6));
					playersInfo();
					msgView.append("["+msg.substring(6)+"]님이 다른 방으로 입장하였습니다.\n");
					if(roomNumber !=0){
						endGame("상대가 나갔습니다.");
					}
				}
				else if(msg.startsWith("[DISCONNECT]")) {
					pList.remove(msg.substring(12));
					playersInfo();
					msgView.append("["+msg.substring(12)+"]님이 접속을 끊었습니다.");
					if(roomNumber != 0)
						endGame("상대가 나갔습니다.");
				}
				else if(msg.startsWith("[DROPGAME]")) {
					
				}
				else if(msg.startsWith("[WIN]")) {
					
				}
				else if(msg.startsWith("[LOSE]")) {
					
				}
				else 
					msgView.append(msg+"\n");
		

			}
			
		}catch(IOException ie) {
			msgView.append(ie+"\n");
		}
		msgView.append("접속이 끊겼습니다.");
		
	}

	private void playersInfo() {
		int count = pList.getItemCount();
		if(roomNumber == 0) {
			pInfo.setText("대기실 : "+count+"명");
		}
		else
			pInfo.setText(roomNumber +"번 방:"+count+"명");
		
		if (count==2 && roomNumber !=0){
			startButton.setEnabled(true);
		}
		else
			startButton.setEnabled(false);
		
	}

	private void nameList(String msg) {
		pList.removeAll();
		StringTokenizer st = new StringTokenizer(msg,"\t");
		while(st.hasMoreElements())
			pList.add(st.nextToken());
		playersInfo();
		
	}
	private void connect() {
		try {
			msgView.append("서버에 연결을 요청합니다.\n");
			socket = new Socket("127.0.0.1",7778);
			msgView.append("----연결 성공 ---- \n");
			msgView.append("이름을 입력하고 대기실로 입장하세요.\n");
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(),true);
			new Thread(this).start();

		}catch(Exception e) {
			msgView.append(e+"\n\n 연결실패 ....\n");
		}
	}
	public static void main(String[] args) {
		client c = new client("인디언 포커");
		c.setSize(1000,1000);
		c.setVisible(true);
		c.connect();
	}

}
