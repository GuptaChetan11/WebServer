import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class Server {

    int cnt = 0;

    public Consumer<Socket> getConsumer() {
        return (clientSocket) -> {
            try (PrintWriter toSocket = new PrintWriter(clientSocket.getOutputStream(), true)) {
                cnt++;
                toSocket.println(cnt + " Hello from the Server " + clientSocket.getInetAddress());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        };
    }

    public static void main(String[] args) {
        int port = 8010;
        Server server = new Server();
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(70000);
            System.out.println("Server is listening on port: " + port);
            while (true) {
                Socket clienSocket = serverSocket.accept();

                // create and start a new thread for each client
                Thread thread = new Thread(() -> server.getConsumer().accept(clienSocket));
                thread.start();

            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                    System.out.println("server is closed");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}