import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class Client{
    Scanner inS = new Scanner(System.in);
    private Battleship battleship;
    private Battleship enemyBattleship;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public Battleship getBattleship(){
        return battleship;
    }

    public void startConnection(String ip, int port) throws IOException {
        battleship = new Battleship();
        enemyBattleship = new Battleship(true);
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String sendMessage(String msg) throws IOException {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public void initGame() {
        printBoards();
        System.out.println("ZACZYNASZ!");
        System.out.println("Podaj wspolrzedne do trafienia: (kolumna;wiersz)");
        System.out.println("lewy-gorny rog ma wspolrzedne A0");
    }

    public void printBoards(){
        System.out.print("TWOJA PLANSZA:");
        battleship.printField();
        System.out.print("PLANSZA PRZECIWNIKA:");
        enemyBattleship.printField();
    }


    public static void main(String[] args) throws IOException {
        Client client = new Client();
        try {
            client.startConnection("127.0.0.1", 6666);
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.initGame();

        String respond = "";
        while (true) {
            String msgIn = client.inS.nextLine();
            try {
                respond = client.sendMessage(msgIn);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String[] strings = respond.split(";");

            if(strings[0].equals("PUDŁO!")){
                if(client.checkInput(strings[1])){
                    respond = client.sendMessage("TRAFIONY!"+strings[1]);
                }
                client.markField(msgIn, ".", client.enemyBattleship);
                client.printBoards();
                System.out.println(respond);
            } else {
                client.markField(msgIn, "#", client.enemyBattleship);
                client.printBoards();
                System.out.println(respond);
                System.out.println("TWÓJ RUCH!");
            }
        }
    }

    private Boolean checkInput(String input) {
        int row, column;
        row = input.charAt(1)-48;
        column = input.charAt(0) - 65;
        return battleship.field[row][column].equals("#");
    }


    private void markField(String msgIn, String c, Battleship b) {
        int row, column;
        row = msgIn.charAt(1)-48;
        column = msgIn.charAt(0) - 65;
        b.field[row][column] = c;
    }
}
