import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server  {
    private Scanner inS = new Scanner(System.in);
    private Battleship battleship;
    private Battleship enemyBattleship;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public Battleship getBattleship(){
        return battleship;
    }

    public Server(){
        battleship = new Battleship();
        enemyBattleship = new Battleship(true);
    }

    public void initGame() {
        printBoards();
        System.out.println("TWÓJ PRZECIWNIK ZACZYNA!");
        System.out.println("Podaj wspolrzedne do trafienia: (kolumna;wiersz)");
        System.out.println("lewy-gorny rog ma wspolrzedne A0");
    }

    public void printBoards(){
        System.out.print("TWOJA PLANSZA:");
        battleship.printField();
        System.out.print("PLANSZA PRZECIWNIKA:");
        enemyBattleship.printField();
    }

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        initGame();

        String inputLine = in.readLine();
        while (true) {
            if(checkInput(inputLine)){
                markField(inputLine,"@", battleship);
                System.out.println("PRZECIWNIK TRAFIŁ W " + inputLine);
                printBoards();
                inputLine = sendMessage("TRAFIONY!;"+inputLine);
            } else {
                String msgIn = inS.nextLine();
                inputLine = sendMessage("PUDŁO!;"+msgIn);
            }
        }
    }

    public String sendMessage(String msg) throws IOException {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }

    private void markField(String msgIn, String c, Battleship b) {
        int row, column;
        row = msgIn.charAt(1)-48;
        column = msgIn.charAt(0) - 65;
        b.field[row][column] = c;
    }

    private Boolean checkInput(String input) {
        int row, column;
        row = input.charAt(1)-48;
        column = input.charAt(0) - 65;
        return battleship.field[row][column].equals("#");
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }
    public static void main(String[] args) throws IOException {
        Server server=new Server();
        server.start(6666);
    }
}
