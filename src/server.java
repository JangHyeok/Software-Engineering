package so_gong;

import java.net.*;
import java.io.*;
import java.util.*;



public class server {
	private ServerSocket server;
	private BManager bMan=new BManager();   
	private Random rnd= new Random();

	int[][] chips = new int[10][2];
	
	int[] total_chips = new int[10];
	int[][] cards = new int[10][2];

	void startServer(){       
		for(int i=0;i<10;i++) {
			for(int j=0;j<2;j++) {
				chips[i][j] = -1;
			}
		}
		try{
			server=new ServerSocket(7780);
			System.out.println("서버소켓이 생성되었습니다.");
			while(true){
				
				Socket socket=server.accept(); 
				Room_Thread rt=new Room_Thread(socket);
				rt.start(); 
				
				bMan.add(rt);
				System.out.println("접속자 수: "+bMan.size());
				}
			}catch(Exception e){
				System.out.println(e);
				}
		}
	public static void main(String[] args){
		server server=new server();
		server.startServer();
		}
	
	
	class Room_Thread extends Thread{
		  private int roomNumber=-1;        
		  private String userName=null;       
		  private Socket socket;              
		  
		  private int who_first =0;
		  private boolean ready=false;
		  private BufferedReader reader;     
		  
		  private PrintWriter writer;           
		  
		  Room_Thread(Socket socket){    
			  this.socket=socket;
			  }
		  
		  Socket getSocket(){              
			  return socket;
			  }
		  int getRoomNumber(){             
			  return roomNumber;
			  }
		  String getUserName(){             
			  return userName;
			  }
		  boolean isReady(){                 
			  return ready;
			  }
		  public void run(){
			  try{
				  reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
				  writer=new PrintWriter(socket.getOutputStream(), true);
				  
				  String msg;                     
				  while((msg=reader.readLine())!=null){ 
					  if(msg.startsWith("[NAME]")){
						  
						  userName=msg.substring(6);          
						  }
					  
					  else if(msg.startsWith("[ROOM]")){
						  int roomNum=Integer.parseInt(msg.substring(6));
						  if( !bMan.isFull(roomNum)){            
							  if(roomNumber!=-1)
								  bMan.sendTorthers(this, "[EXIT]"+userName);
							 
							  roomNumber=roomNum;
							  
							  writer.println(msg); 
							  writer.println(bMan.getNamesInRoom(roomNumber));
							  
							  bMan.sendTorthers(this, "[ENTER]"+userName);
							  }
						  else 
							  writer.println("[FULL]");        
						  }
					  
					  else if(roomNumber>=1 && msg.startsWith("[CARD]")) {
						  bMan.set_card(this,msg);
						  bMan.sendTorthers(this, msg);
					  }
					  
					  else if(msg.startsWith("[MSG]"))
						  bMan.sendToRoom(roomNumber,"["+userName+"]: "+msg.substring(5));
					  
					  else if(msg.startsWith("[START]")){
						  ready=true;  
						  bMan.card_and_chip_init(this);
						  if(bMan.isReady(roomNumber)){  
							  who_first =rnd.nextInt(2);
							  if(who_first==0) {
								  bMan.sendTo(0,"[WHO]FIRST");
								  bMan.sendTo(1,"[WHO]SECOND");
								  }
							  else{
								  bMan.sendTo(1,"[WHO]FIRST");
								  bMan.sendTo(0,"[WHO]SECOND");
								  }
							  }
						  }
					 
					  else if(msg.startsWith("[STOPGAME]"))
						  ready=false;
					 
					  else if(msg.startsWith("[DROPGAME]")){
						  ready=false;
						  bMan.sendTorthers(this, "[DROPGAME]");
						  }
					
					  else if(msg.startsWith("[CHIP]")) {
						  String m = msg.substring(6);
						  bMan.input(this,m,who_first);
					  }
					  else if(msg.startsWith("[STOPTHEGAME]")) {
						  ready = false;
					  }
					  }
				  }catch(Exception e){
					  
				  }finally{
					  try{
						  bMan.remove(this);
						  if(reader!=null) reader.close();
						  if(writer!=null) writer.close();
						  if(socket!=null) socket.close();
						  reader=null; 
						  writer=null; 
						  socket=null;
						  
						  System.out.println(userName+"님이 접속을 끊었습니다.");
						  System.out.println("접속자 수: "+bMan.size());
						  
						  bMan.sendToRoom(roomNumber,"[DISCONNECT]"+userName);
						  
					  }catch(Exception e){}
					  }
			  }
		  }
	
	class BManager extends Vector{    
		void input(Room_Thread rt,String msg,int who_first) {
			for(int i=0;i<size();i++) {
				  if(getRoomNumber(i)==rt.getRoomNumber() && getrt(i)!=rt) {
					  String s;
					  s = "[CHIP]"+msg;
					  sendTo(i,s);
				  }
			 }
			 for(int i=0;i<size();i++) {
				  if(getRoomNumber(i)==rt.getRoomNumber() && getrt(i)==rt) {
					  chips[rt.getRoomNumber()][i] = Integer.parseInt(msg);
				  }
			 }
			 int count =2;
			 for(int i=0;i<2;i++) {
				 if(chips[rt.getRoomNumber()][i]!=-1) {
					 count--;
				 }
			 }
			 if(count == 0) {
				if(who_first == 0) {
					if(chips[rt.getRoomNumber()][0] == chips[rt.getRoomNumber()][1]) {
						if(cards[rt.getRoomNumber()][0] > cards[rt.getRoomNumber()][1]) {
							sendTo(0,"[CARD]WIN");
							sendTo(1,"[CARD]LOSE");
							chips[rt.getRoomNumber()][0] = -1;
							chips[rt.getRoomNumber()][1] = -1;
							count =2;
							
						}
						else if(cards[rt.getRoomNumber()][0] < cards[rt.getRoomNumber()][1]) {
							sendTo(1,"[CARD]WIN");
							sendTo(0,"[CARD]LOSE");
							chips[rt.getRoomNumber()][0] = -1;
							chips[rt.getRoomNumber()][1] = -1;
							count =2;
						}
						else if(cards[rt.getRoomNumber()][0] == cards[rt.getRoomNumber()][1]) {
							sendTo(1,"[CARD]DRAW");
							sendTo(0,"[CARD]DRAW");
							chips[rt.getRoomNumber()][0] = -1;
							chips[rt.getRoomNumber()][1] = -1;
							count =2;
						}
						
						chips[rt.getRoomNumber()][0] = -1;
						chips[rt.getRoomNumber()][1] = -1;
						count =2;
						
					}
					else if(chips[rt.getRoomNumber()][0]<chips[rt.getRoomNumber()][1]) {
						sendTo(0,"[WHOO]FIRST");
						sendTo(1,"[WHOO]SECOND");
						chips[rt.getRoomNumber()][0] = -1;
						chips[rt.getRoomNumber()][1] = -1;
						count =2;
					}
				}
				if(who_first == 1) {
					if(chips[rt.getRoomNumber()][1] == chips[rt.getRoomNumber()][0]) {
						if(cards[rt.getRoomNumber()][1] > cards[rt.getRoomNumber()][0]) {
							sendTo(1,"[CARD]WIN");
							sendTo(0,"[CARD]LOSE");
							chips[rt.getRoomNumber()][0] = -1;
							chips[rt.getRoomNumber()][1] = -1;
							count =2;
							
						}
						else if(cards[rt.getRoomNumber()][1] < cards[rt.getRoomNumber()][0]) {
							sendTo(0,"[CARD]WIN");
							sendTo(1,"[CARD]LOSE");
							chips[rt.getRoomNumber()][0] = -1;
							chips[rt.getRoomNumber()][1] = -1;
							count =2;
						}
						else if(cards[rt.getRoomNumber()][1] == cards[rt.getRoomNumber()][0]) {
							sendTo(1,"[CARD]DRAW");
							sendTo(0,"[CARD]DRAW");
							chips[rt.getRoomNumber()][0] = -1;
							chips[rt.getRoomNumber()][1] = -1;
							count =2;
						}
						chips[rt.getRoomNumber()][0] = -1;
						chips[rt.getRoomNumber()][1] = -1;
						
						count =2;
						
					}
					else if(chips[rt.getRoomNumber()][1]<chips[rt.getRoomNumber()][0]) {
						sendTo(1,"[WHOO]FIRST");
						sendTo(0,"[WHOO]SECOND");
						count = 2;;
						chips[rt.getRoomNumber()][0] = -1;
						chips[rt.getRoomNumber()][1] = -1;
						count =2;
					}
				}
			 }
		}
		  BManager(){}
		  void add(Room_Thread rt){           
			  super.add(rt);
			  }
		  void remove(Room_Thread rt){        
			  super.remove(rt);
			  }
		  Room_Thread getrt(int i){            
			  return (Room_Thread)elementAt(i);
			  }
		  Socket getSocket(int i){              
			  return getrt(i).getSocket();
			  }
		  
		  
		  void sendTo(int i, String msg){
			 try{
				 PrintWriter pw= new PrintWriter(getSocket(i).getOutputStream(), true);
				 pw.println(msg);
				 }catch(Exception e){}  
			 }
		  int getRoomNumber(int i){            
			  return getrt(i).getRoomNumber();
			  }
		  synchronized boolean isFull(int roomNum){    
			  if(roomNum==0)
				  return false;                 
			 
			  int count=0;
			  for(int i=0;i<size();i++) {
				  if(roomNum==getRoomNumber(i))
					  count++;
			  }
			  if(count>=2)
				  return true;
			  return false;
			  }
		 
		  void sendToRoom(int roomNum, String msg){
			  for(int i=0;i<size();i++)
				  if(roomNum==getRoomNumber(i))
					  sendTo(i, msg);
			  }
		  
		  void sendTorthers(Room_Thread rt, String msg){
			  for(int i=0;i<size();i++)
				  if(getRoomNumber(i)==rt.getRoomNumber() && getrt(i)!=rt)
					  sendTo(i, msg);
			  }
		  void set_card(Room_Thread rt,String msg) {
			  String s = msg.substring(6);
			  int k = Integer.parseInt(s);
			  for(int i=0;i<size();i++) {
				  if(getRoomNumber(i) == rt.getRoomNumber()&&getrt(i)==rt) {
					  cards[getRoomNumber(i)][i] = k;
				  }
			  }
		  }
		  
		  
		  
		  synchronized boolean isReady(int roomNum){
			  int count=0;
			  for(int i=0;i<size();i++) {
				  if(roomNum==getRoomNumber(i) && getrt(i).isReady())
					  count++;
			  }
			  if(count==2)
				  return true;
			  return false;
	    }
		 
		  String getNamesInRoom(int roomNum){
			  StringBuffer sb=new StringBuffer("[PLAYERS]");
			  for(int i=0;i<size();i++)
				  if(roomNum==getRoomNumber(i))
					  sb.append(getrt(i).getUserName()+"\t");
			  return sb.toString();
			  }
		  void card_and_chip_init(Room_Thread rt) {
			  for(int i=0;i<size();i++) {
				  if(getRoomNumber(i) == rt.getRoomNumber()&&getrt(i)==rt) {
					  cards[i][0] = -1;
					  cards[i][1] = -1;
					  chips[i][0] = -1;
					  chips[i][1] = -1;
				  }
			  }
		  }
		  }
		
}
