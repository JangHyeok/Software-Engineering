package mains;

import java.net.*;

import java.io.*;

import java.util.*;

public class ServerTest2{
	private ServerSocket server;
	private BManager bMan=new BManager();   
	private Random rnd= new Random();       
	
	public ServerTest2(){}
	
	void startServer(){                         
		try{
			server=new ServerSocket(7778);
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
		ServerTest2 server=new ServerTest2();
		server.startServer();
		}

  class Room_Thread extends Thread{
	  private int roomNumber=-1;        
	  private String userName=null;       
	  private Socket socket;              
	  
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
				  
				  else if(roomNumber>=1 && msg.startsWith("[CARD]"))
					  bMan.sendTorthers(this, msg);
				  
				  else if(msg.startsWith("[MSG]"))
					  bMan.sendToRoom(roomNumber,"["+userName+"]: "+msg.substring(5));
				  
				  else if(msg.startsWith("[START]")){
					  ready=true;  
					  
					  if(bMan.isReady(roomNumber)){  
						  int a=rnd.nextInt(2);
						  if(a==0){
							 
							  }
						  else{
							  
							  }
						  }
					  }
				 
				  else if(msg.startsWith("[STOPGAME]"))
					  ready=false;
				 
				  else if(msg.startsWith("[DROPGAME]")){
					  ready=false;
					  bMan.sendTorthers(this, "[DROPGAME]");
					  }
				 
				  else if(msg.startsWith("[WIN]")){
					  ready=false;
					  writer.println("[WIN]");
					  bMan.sendTorthers(this, "[LOSE]");
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
	  }
  }
