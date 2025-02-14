import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ExecutorService threadPool;

    public Server(int poolSize) {
        this.threadPool = Executors.newFixedThreadPool(poolSize);
    }

    public void handleClient(Socket clientSocket) {
        //socket === client
        try (PrintWriter toSocket = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader fromSocket = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            toSocket.println("Hello from server " + clientSocket.getInetAddress());
            String line = fromSocket.readLine();
            System.out.println(line);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = 8010;
        int poolSize = 10; // we can adjust this pool size according to our needs
        Server server = new Server(poolSize);

        try {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                serverSocket.setSoTimeout(60000); //timeout set to 60s
                System.out.println("Server is listening on port " + port);

                while (true) {
                    Socket clientSocket = serverSocket.accept();

                    // Use the thread pool to handle the client
                    server.threadPool.execute(() -> server.handleClient(clientSocket));
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            // Shutdown the thread pool when the server exits
            server.threadPool.shutdown();
        }
    }
}