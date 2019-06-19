package so_gong;

import java.awt.*;
import java.net.*;
import java.nio.Buffer;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;


class Card extends JFrame implements ActionListener{
	private JLabel l1;
	private JLabel l2;
	private BufferedImage i1;
	private BufferedImage i2;
	private String info = "게임 중지";
	
	private boolean enable = false;
	private boolean running = false;
	private PrintWriter writer;
	private JTextField input;
	private JButton button;
	private JLabel label;
	private JLabel how;
	private JLabel how_your;
	private String real;
	int[] card = new int[11];
	private int my_chip;
	private int your_chip;
	private int your_bet_chip;
	
	Label temp = new Label();
	
	int total = 0;
	void init() {
		my_chip =40;
		your_chip =40;
		String s = Integer.toString(my_chip);
		String s2 = Integer.toString(your_chip);
		how.setText("현재 나의 칩 개수 :"+s);
		how_your.setText("현재 상대 칩 개수:"+s2);
		 for(int i=1;i<=10;i++) {
			 card[i] = i;
		 }
	 }
	
	void re_set() {
		my_chip = 40;
		your_chip =0;
		your_bet_chip = 0;
		total = 0;
		how_your.setText("현재 상대방의 칩 개수:");
		how.setText("현재 나의 칩 개수 :");
		label.setText("총 배팅 금액:");
	}
	
	public void call_number() {
		 int temp;
		 double randomV;
		 int i;
		 Random r = new Random();
		 r.setSeed(System.currentTimeMillis());
		 while(true) {
			 i = r.nextInt(9)+1;
			 if(card[i]!=-1) {
				 temp = card[i];
				 card[i] =-1;
				 break;
			 }
		 }
		
		 String s;
		 s = Integer.toString(temp);
		 real = s;
		 writer.println("[CARD]"+s);
	 }
	
	private int chip = 40;
	public Card(Container p) {
		try {
			i1 = ImageIO.read(new File("./image/0.png"));
			i2 = ImageIO.read(new File("./image/0.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		l1 = new JLabel(new ImageIcon(i1));
		l2 = new JLabel(new ImageIcon(i2));
		l1.setBounds(50,50,230,400);
		l2.setBounds(400,50,230,400);
		input = new JTextField();
		input.setBounds(50,480,50,25);
		button = new JButton("배팅");
		button.setBounds(100,480,70,25);
		button.addActionListener(this);
		label = new JLabel();
		label.setBounds(300,480,100,50);
		label.setText("총 배팅 금액:");
		how = new JLabel();
		how.setBounds(450,480,150,50);
		how.setText("현재 나의 칩 개수:");
		how_your = new JLabel();
		how_your.setBounds(450,500,150,50);
		how_your.setText("현재 상대방의 칩 개수:");
		p.add(how_your);
		p.add(how);
		p.add(label);
		p.add(l1);
		p.add(l2);
		p.add(input);
		p.add(button);
	}
	public void startGame(String col,Label l) throws InterruptedException {
		temp = l;
		running = true;
		Thread.sleep(2000);
		if(col.equals("FIRST")) {
			enable = true;
			l.setText("칩을 배팅 하십시오.");
			button.setEnabled(true);
		}
		else {
			enable = false;
			l.setText("상대방이 칩을 베팅 중입니다.");
			button.setEnabled(false);
		}
		
	}
	
	public void stopGame() {
		writer.println("[STOPGAME]");
		enable = false;
		running = false;
	}
	public boolean isRunning() {
		return running;
	}
	
	public boolean check() {
		if(chip == 0) {
			return false;
		}
		else 
			return true;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	public void setWriter(PrintWriter writer) {
		this.writer = writer;
	}
	
	public void set_op(String s,Container p,Label l,int game,Button startbutton) throws IOException, InterruptedException {
		if(s.startsWith("W") || s.startsWith("L")||s.startsWith("D")) {
			i1 = ImageIO.read(new File("./image/"+real+".png"));
			l1.setIcon(new ImageIcon(i1));
			if(s.startsWith("W")) {
				my_chip = my_chip + total;
				total =0;
				String j = Integer.toString(total);
				String k = Integer.toString(my_chip);
				how.setText("현재 나의 칩 개수 :"+k);
				label.setText("배팅 칩 개수:"+j);
				l.setText("당신이 이겼습니다.");
				Thread.sleep(5000);
			}
			else if(s.startsWith("L")) {
				String k = Integer.toString(my_chip);
				your_chip = your_chip + total;
				total = 0;
				String j = Integer.toString(total);
				String m = Integer.toString(your_chip);
				how_your.setText("현재 상대 칩 개수 :"+m);
				how.setText("현재 나의 칩 개수 :"+k);
				label.setText("배팅 칩 개수:"+j);
				l.setText("당신이 졌습니다.");
				Thread.sleep(5000);
			}
			else if(s.startsWith("D")) {
				l.setText("상대방과 나의 카드가 같으므로 무승부 입니다.");
			}
			if(my_chip==0) {
				writer.print("[LOSE]");
				l.setText("가지고 있는 칩이 없기 때문에 패배하셨습니다.");
				Thread.sleep(5000);
				i1 = ImageIO.read(new File("./image/0.png"));
				i2 = ImageIO.read(new File("./image/0.png"));
				l1.setIcon(new ImageIcon(i1));
				l2.setIcon(new ImageIcon(i2));
				game = 0;
				writer.println("[STOPTHEGAME]");
				l.setText("게임을 원하시면 게임시작을 눌러주세요");
				startbutton.setEnabled(true);
			}
			else if(my_chip>0&&your_chip>0) {
				l.setText("딜러가 카드를 나눠주는 중입니다 ... ");
				writer.println("[START]");
				Thread.sleep(5000);
				i1 = ImageIO.read(new File("./image/"+0+".png"));
				l1.setIcon(new ImageIcon(i1));
			}
			else if(your_chip==0) {
				l.setText("상대방이 칩이 없기때문에 승리하셨습니다.");
				Thread.sleep(5000);
				i1 = ImageIO.read(new File("./image/0.png"));
				i2 = ImageIO.read(new File("./image/0.png"));
				l1.setIcon(new ImageIcon(i1));
				l2.setIcon(new ImageIcon(i2));
				writer.println("[STOPTHEGAME]");
				l.setText("게임을 원하시면 게임시작을 눌러주세요");
				startbutton.setEnabled(true);
				game = 1;
			}
			
		}
		else{
			try {
				i2 = ImageIO.read(new File("./image/"+s+".png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			l2.setIcon(new ImageIcon(i2));
		}
		}
	
	
	public void info(String s,Label l,Container p) {
		String a = "상대방이 "+s.substring(6)+"개를 배팅하였습니다. 배팅하십시오.";
		button.setEnabled(true);
		String s2 = s.substring(6);
		l.setText(a);
		total += Integer.parseInt(s2);
		your_bet_chip = Integer.parseInt(s2);
		your_chip -= Integer.parseInt(s2);
		String t2 = Integer.toString(your_chip);
		how_your.setText("현재 상대 칩 개수 :"+t2);
		String t = Integer.toString(total);
		label.setText("총 배팅금액:"+t);

	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == button) {
			String s = "[CHIP]";
			String k = input.getText();
			if(Integer.parseInt(k)<1) {
				temp.setText("배팅 금액은 1보다 커야 합니다.");
				
			}
			else if(Integer.parseInt(k)<your_bet_chip) {
				temp.setText("배팅금액은 "+Integer.toString(your_bet_chip)+"보다 크거나 같아야 합니다.");
			}
			else{
			s= s+k;
			writer.println(s);
			my_chip -= Integer.parseInt(k);
			total += Integer.parseInt(k);
			String t = Integer.toString(total);
			temp.setText("상대방이 배팅 중 입니다.. ");
			button.setEnabled(false);
			label.setText("총 배팅금액:"+t);
			how.setText("현재 나의 칩 개수 :"+my_chip);
			}
		}
		
	}




	
	
}


public class client extends JFrame implements Runnable, ActionListener {
	
	
	private int game =0;
	
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

	Card card;
	
	public client(String title) {
		super(title);
		
		setLayout(null);
		msgView.setEditable(false);
		infoView.setBounds(100,10,480,30);
		infoView.setBackground(new Color(200,200,255));
		add(infoView);
		Panel p = new Panel();
	
		card = new Card(this);
		p.setBackground(new Color(200,255,255));
		p.setLayout(new GridLayout(3,3));
		p.add(new Label("이 름:",2));
		p.add(nameBox);
		p.add(new Label("방 번호:",2));
		p.add(roomBox);
		p.add(enterButton);
		p.add(exitButton);
		enterButton.setEnabled(false);
		p.setBounds(700,30,250,70);
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
		p2.setBounds(700,100,250,180);
		
		Panel p3 = new Panel();
		p3.setLayout(new BorderLayout());
		p3.add(msgView,"Center");
		p3.add(sendBox,"South");
		p3.setBounds(700,300,250,250);
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
				card.init();

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
				else if(msg.startsWith("[CARD]")) {
					String temp = msg.substring(6);
					try {
						card.set_op(temp,this,infoView,game,startButton);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(msg.startsWith("[FULL]")) {
					infoView.setText("방이 차서 입장할 수 없습니다.");
				}
				else if(msg.startsWith("[PLAYERS]")) {
					nameList(msg.substring(9));
				}
				else if(msg.startsWith("[ENTER]")) {
					card.re_set();
					pList.add(msg.substring(7));
					playersInfo();
					msgView.append("["+msg.substring(7)+"]님이 입장하였습니다.");
				}
				else if(msg.startsWith("[EXIT]")) {
					card.re_set();
					pList.remove(msg.substring(6));
					playersInfo();
					msgView.append("["+msg.substring(6)+"]님이 다른 방으로 입장하였습니다.\n");
					if(roomNumber !=0){
						endGame("상대가 나갔습니다.");
					}
				}
				else if(msg.startsWith("[DISCONNECT]")) {
					card.re_set();
					pList.remove(msg.substring(12));
					playersInfo();
					msgView.append("["+msg.substring(12)+"]님이 접속을 끊었습니다.");
					if(roomNumber != 0)
						endGame("상대가 나갔습니다.");
				}
				else if(msg.startsWith("[WHO]")) {
					if(game == 0) {
						card.init();
						String who = msg.substring(5);
						card.startGame(who,infoView);
						card.call_number();
						stopButton.setEnabled(true);
						game = 1;
					}
					else {
						String who = msg.substring(5);
						card.startGame(who,infoView);
						card.call_number();
						stopButton.setEnabled(true);
					}

				}
				else if(msg.startsWith("[WHOO]")) {
					String who = msg.substring(6);
					card.startGame(who,infoView);
	
				}
				else if(msg.startsWith("[CHIP]")) {
					card.info(msg,infoView,this);
					
				}
				//else if(msg.startsWith("[WIN]")) {
					//card.set_op(msg,this,infoView,game,startButton);
				//}
				else 
					msgView.append(msg+"\n");

			}
			
		}catch(IOException ie) {
			msgView.append(ie+"\n");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			socket = new Socket("127.0.0.1",7780);
			msgView.append("----연결 성공 ---- \n");
			msgView.append("이름을 입력하고 대기실로 입장하세요.\n");
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(),true);
			new Thread(this).start();
			card.setWriter(writer);

		}catch(Exception e) {
			msgView.append(e+"\n\n 연결실패 ....\n");
		}
	}
	public static void main(String[] args) {
		
		client c = new client("인디언 포커");
		c.setSize(1000,600);
		c.setVisible(true);
		c.connect();
			
	}

}
