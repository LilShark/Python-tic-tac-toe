// Game Client Side

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import java.net.*;
import java.util.*;

public class Client extends JPanel {

private char playerMark;
private int playerID;
private int otherPlayer;
private int x;
private int currentplayerscore;
private int OppplayerScore;
private boolean setButtons;
private JButton[] buttons = new JButton[9];
private clientConnection CC;

Scanner scan = new Scanner(System.in);

public Client() {
// client side values refreshing/reseting to 0
setButtons = false;

x = 0;

playerID = 0;

otherPlayer = 0;

currentplayerscore = 0;

OppplayerScore = 0;

}

//Build Gui for the Client

public void buildGui() {

JFrame window = new JFrame("player #" + playerID); 

window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

window.getContentPane().add(this); window.setBounds(400,400,400,400); 

window.setLocationRelativeTo(null); 

setLayout(new GridLayout(3,3));
//setting the values for playerID for both the players and synchronising them
if(playerID == 1) {

otherPlayer = 2;
setButtons = true;
toggleButtons();

}

else {

otherPlayer = 1 ;
setButtons = false;
toggleButtons();

Thread t2 = new Thread(new Runnable() {

public void run() {

updateTurn();
}
});

t2.start();
}

window.setVisible(true); // show the window

}

public void initializeButtons()

{

for(int i = 0; i <= 8; i++)
{

buttons[i] = new JButton();
buttons[i].setText(" ");
buttons[i].setBackground(Color.WHITE);
add(buttons[i]);

}
}
//Gets the button clicked and send to server

public void keyPress() {

ActionListener al = new ActionListener() {
public void actionPerformed(ActionEvent ae) {
JButton buttonClicked = (JButton) ae.getSource();
buttonClicked.setText(String.valueOf(playerMark));
buttonClicked.setBackground(Color.RED);

for(x = 0 ; x<9 ; x++) {
//noting down which button was clicked
if(buttonClicked == buttons[x]) {

break;

}
}
//sending the buttonvalue to server
CC.sendButtonVal(x);
System.out.println("value sent to server");
setButtons = false;
toggleButtons();
displayVector();
System.out.println("You Clicked button no "+ x+" Wait for Player #" + otherPlayer);

Thread t = new Thread(new Runnable() {

public void run() {

updateTurn();
}
});

t.start();

}
};

for(int i=0;i<9;i++) {
buttons[i].addActionListener(al);
}
}

//Receives Value from server and write in Client 
public void updateTurn() {

int n = CC.recButtonVal();

oppMove(n);
//writing opposition move and reflecting it in gui and commandprompt
System.out.println("Opponent feedback has been written");
setButtons = true;
toggleButtons();
}

public void toggleButtons() {

if(setButtons == false) {

for (int i=0;i<9;i++) {

buttons[i].setEnabled(setButtons);
}
}
else {

for(int i =0;i<9;i++) {

if(buttons[i].getText().charAt(0) == ' ') {

buttons[i].setEnabled(setButtons);
}
}
}
}
//To write an opponent feedback

public void oppMove(int oppval) {

char oppPlayerMark;

if (playerMark == 'X') {

oppPlayerMark = 'O';

}

else {

oppPlayerMark ='X';

}

buttons[oppval].setText(String.valueOf(oppPlayerMark));
buttons[oppval].setBackground(Color.CYAN);
buttons[oppval].setEnabled(false);

displayVector();

}

public void playerMarkDecider(){

if (playerID == 1) {

playerMark = 'X';
}

else {

playerMark = 'O';
}
}

public void UpdatePoints(char x) {

if(x == playerMark) {

currentplayerscore++;
}

else {

OppplayerScore++;
}
}

//display the victorious player 
public void displayVector() {

if(checkForWinner('X') == true) { 

JOptionPane pane = new JOptionPane();

int dialogResult = JOptionPane.showConfirmDialog(pane, "X wins. \n you(#1):"+currentplayerscore+" \t opponent(#2):"+OppplayerScore+" \n Would you like to play again?","Game over.",JOptionPane.YES_NO_OPTION);

if(dialogResult == JOptionPane.YES_OPTION) 

{resetTheButtons();} 

else {

CC.closeConnection();
System.exit(0);
}
}

else if(checkForWinner('O') == true) {

JOptionPane pane = new JOptionPane();

int dialogResult = JOptionPane.showConfirmDialog(pane, "O wins. \n you(#1):"+currentplayerscore+" \t opponent(#2):"+OppplayerScore+" \n Would you like to play again?","Game over.",JOptionPane.YES_NO_OPTION);

if(dialogResult == JOptionPane.YES_OPTION) resetTheButtons();

else {

CC.closeConnection();
System.exit(0);
}
}

else if(checkDraw()) {
    
JOptionPane pane = new JOptionPane();
int dialogResult = JOptionPane.showConfirmDialog(pane,"Draw \n you(#1):"+currentplayerscore+" \t opponent(#2):"+OppplayerScore+" \n Would you like to play again?","Game over.", JOptionPane.YES_NO_OPTION);
if(dialogResult == JOptionPane.YES_OPTION) 
{resetTheButtons();} 

else {

CC.closeConnection();
System.exit(0);
}
}
}
private void resetTheButtons() {

if(playerMark == 'O') {

playerMark = 'O';
setButtons = true;

}
else {

playerMark = 'X' ;
setButtons = false;

}
toggleButtons();

for(int i =0;i<9;i++) {

buttons[i].setText(" ");
buttons[i].setBackground(Color.WHITE);
}
}

// checks for draw
public boolean checkDraw() {

boolean full = true;
for(int i = 0 ; i<9;i++) {

if(buttons[i].getText().charAt(0) == ' ') {

full = false;
}
}
return full;
}

// checks for a winner

public boolean checkForWinner(char x) {

if(checkRows(x) == true || checkColumns(x) == true || checkDiagonals(x) ==true) {
return true;
}
else return false;
}

// checks rows for a win
public boolean checkRows(char x) {
int i = 0;
for(int j = 0;j<3;j++) {

if( buttons[i].getText().equals(buttons[i+1].getText()) &&
buttons[i].getText().equals(buttons[i+2].getText()) && buttons[i].getText().charAt(0) != ' ' &&
buttons[i].getText().charAt(0) == x) {

UpdatePoints(x);
return true;
}
i = i+3;
}
return false;
}

// checks columns for a win

public boolean checkColumns(char x) {
int i = 0;
for(int j = 0;j<3;j++) {
if( buttons[i].getText().equals(buttons[i+3].getText()) &&
buttons[i].getText().equals(buttons[i+6].getText())&& buttons[i].getText().charAt(0) != ' ' &&
buttons[i].getText().charAt(0) == x)
{

UpdatePoints(x);
return true;
}
i++;
}
return false;
}
// checks diagonals for a win

public boolean checkDiagonals(char x) {
if(buttons[0].getText().equals(buttons[4].getText()) &&
buttons[0].getText().equals(buttons[8].getText())&& buttons[0].getText().charAt(0) !=' ' && buttons[0].getText().charAt(0) == x) 
{

UpdatePoints(x);
return true;

}
else if(buttons[2].getText().equals(buttons[4].getText()) && buttons[2].getText().equals(buttons[6].getText())&& buttons[2].getText().charAt(0) !=' ' && buttons[2].getText().charAt(0) == x) 
{

UpdatePoints(x);
return true;
}
else return false;
}

public void connectToserver() {

CC = new clientConnection();
}

private class clientConnection {
private Socket socket;
private DataInputStream Din;
private DataOutputStream Dout;
private String ip;

public clientConnection() {

try {

System.out.println("Client side.");
System.out.println("Enter IP address:");
ip = scan.nextLine();
socket = new Socket(ip,26771);
Din = new DataInputStream(socket.getInputStream()); Dout = new DataOutputStream(socket.getOutputStream()); playerID = Din.readInt();
System.out.println("player # " + playerID + "is connected to server"); }catch (IOException ex) {

// System.out.println("IOException in client constructor"); System.exit(0);
}
}

public void sendButtonVal(int a) {

try {

Dout.writeInt(a);
Dout.flush();
System.out.println("value sent to server !");

}catch(IOException ex) {

System.out.println("IOException in sendButton()");
System.exit(0);

}

}

public int recButtonVal() {

int n=-1;

try {

n = Din.readInt();

System.out.println("Value received from server"); }catch (IOException ex) {

System.out.println("error in recButton()");

}

return n;

}

public void closeConnection() {

try {

socket.close();

System.out.println("Socket closed successfully");

}catch(IOException ex) {

System.out.println("IOException while closing");

}
}
}

public static void main(String[] args) {

Client cl = new Client();

cl.connectToserver();
cl.playerMarkDecider();
cl.initializeButtons();
cl.buildGui();
cl.keyPress();
}
}

