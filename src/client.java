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
	
	private Label pInfo = new Label("���� : ��");
	
	private java.awt.List pList = new java.awt.List();
	private Button startButton = new Button("���� ����");
	private Button stopButton = new Button("���");
	private Button enterButton = new Button("����");
	private Button exitButton = new Button("���Ƿ�");
	
	private Label infoView = new Label("�ε�� ��Ŀ",1);
	
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
		p.add(new Label("�� ��:",2));
		p.add(nameBox);
		p.add(new Label("�� ��ȣ:",2));
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
					infoView.setText("�� ��ȣ�� �߸��Ǿ����ϴ�. 1 �̻�");
					return;
				}
				writer.println("[ROOM]"+Integer.parseInt(roomBox.getText()));
				msgView.setText("");
			}
			catch(Exception ie) {
				infoView.setText("�Է��Ͻ� ���׿� ������ �ֽ��ϴ�.");
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
				infoView.setText("����� ������ ��ٸ��� �� �Դϴ�,");
				startButton.setEnabled(false);
			}
			catch(Exception e) {}
		}
		else if(ac.getSource() == stopButton) {
			try {
				writer.println("[DROPGAME]");
				endGame("��� �Ͽ����ϴ�.");
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
				infoView.setText("�̸��� �߸��Ǿ����ϴ�. 3~10��");
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
		infoView.setText("���ǿ� �����ϼ̽��ϴ�.");
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
						infoView.setText(msg.substring(6)+"�� �濡 �����ϼ̽��ϴ�.");
					}
					else
						infoView.setText("���ǿ� �����ϼŽ��ϴ�.");
					roomNumber = Integer.parseInt(msg.substring(6));
				}
				else if(msg.startsWith("[FULL]")) {
					infoView.setText("���� ���� ������ �� �����ϴ�.");
				}
				else if(msg.startsWith("[PLAYERS]")) {
					nameList(msg.substring(9));
				}
				else if(msg.startsWith("[ENTER]")) {
					pList.add(msg.substring(7));
					playersInfo();
					msgView.append("["+msg.substring(7)+"]���� �����Ͽ����ϴ�.");
				}
				else if(msg.startsWith("[EXIT]")) {
					pList.remove(msg.substring(6));
					playersInfo();
					msgView.append("["+msg.substring(6)+"]���� �ٸ� ������ �����Ͽ����ϴ�.\n");
					if(roomNumber !=0){
						endGame("��밡 �������ϴ�.");
					}
				}
				else if(msg.startsWith("[DISCONNECT]")) {
					pList.remove(msg.substring(12));
					playersInfo();
					msgView.append("["+msg.substring(12)+"]���� ������ �������ϴ�.");
					if(roomNumber != 0)
						endGame("��밡 �������ϴ�.");
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
		msgView.append("������ ������ϴ�.");
		
	}

	private void playersInfo() {
		int count = pList.getItemCount();
		if(roomNumber == 0) {
			pInfo.setText("���� : "+count+"��");
		}
		else
			pInfo.setText(roomNumber +"�� ��:"+count+"��");
		
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
			msgView.append("������ ������ ��û�մϴ�.\n");
			socket = new Socket("127.0.0.1",7778);
			msgView.append("----���� ���� ---- \n");
			msgView.append("�̸��� �Է��ϰ� ���Ƿ� �����ϼ���.\n");
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(),true);
			new Thread(this).start();

		}catch(Exception e) {
			msgView.append(e+"\n\n ������� ....\n");
		}
	}
	public static void main(String[] args) {
		client c = new client("�ε�� ��Ŀ");
		c.setSize(1000,1000);
		c.setVisible(true);
		c.connect();
	}

}
