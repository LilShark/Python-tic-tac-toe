import java.util.*;
import java.net.*;
import java.io.*;

public class Server {

private int playerCount;
private int player1Val;
private int player2Val;
private ServerConnection player1;
private ServerConnection player2;
private ServerSocket ss;

Scanner scan = new Scanner(System.in);
//Creates a Server socket

public Server() {

playerCount = 0;

try{

ss  = new ServerSocket(26771); } catch(IOException ex) { System.out.println("IOException in Server Constructor");

}
}
//Connects with 2 clients

private void initiate() {

try {

System.out.println("Waiting for clients to connect with the server......."); 
while (playerCount < 2) {
Socket s = ss.accept();

playerCount++;

// reflected in command prompt output
System.out.println("player #"+ playerCount + " is connected." ); 

ServerConnection SC = new ServerConnection(s,playerCount); 

if(playerCount == 1) {

    player1 = SC;
// if first client to join to server then player 1
} 
else {

player2 = SC;
// else player 2
}

Thread t = new Thread(SC);

t.start();
// start the thread/server
}
System.out.println("No more connections accepted.(both the players have joined)");

// no more than 2 players accepted
}catch (IOException ex){

System.out.println("IOException while initiating connection, please try again.");

}
}

private class ServerConnection implements Runnable {
private Socket socket;
private DataInputStream Din;
private DataOutputStream Dout;
private int playerID;

ServerConnection (Socket s, int id) {

socket = s;
// socket is created
playerID = id;
try {

Din = new DataInputStream (socket.getInputStream()); Dout = new DataOutputStream(socket.getOutputStream());

} 
catch(IOException ex) {
System.out.println("IOException in ServerConnection");

}

}

public void run() {

try {

Dout.writeInt(playerID);
Dout.flush();
while(true) {

if(playerID == 1) {

player1Val = Din.readInt();

System.out.println("player1 clicked "+ player1Val);
// output shown in command prompt as to which grid player 1 has selected
player2.sendButtonVal(player1Val);
// reflect which button did player 1 select and display that in player 2 GUI

}

else if(player1.socket.isClosed() || player2.socket.isClosed()) { System.out.println("Connection lost/broken. Please connect again.");
// connection lost
break;
 
}

else {

player2Val = Din.readInt(); System.out.println("player2 clicked "+ player2Val); player1.sendButtonVal(player2Val);

}

}}catch(Exception ex) {

player1.closeConnection();
player2.closeConnection();

}
}

public void sendButtonVal(int n) {
try {

Dout.writeInt(n);

Dout.flush();

System.out.println("Block mark value succesfully written back to client"); 
// to verify if the value was successfully written in the GUI for both the clients 
} catch(IOException ex){
System.out.println("IOException in sending block move value to opponent");

}
}

public void closeConnection() {
try {

    socket.close();

    System.out.println("Connection successfully closed.");

}
catch(IOException ex) {

    System.out.println("IOException while closing the connection");
}
}
}

public static void main(String args[]) {

Server server = new Server();
server.initiate();

}
}
