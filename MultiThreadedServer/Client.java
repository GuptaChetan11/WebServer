import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    public Runnable getRunnable() throws UnknownHostException, IOException {
        return new Runnable() {
            @Override
            public void run() {
                int port = 8010;
                Socket socket = null;
                try {
                    InetAddress address = InetAddress.getByName("localhost");
                    socket = new Socket(address, port);
                    try (
                            PrintWriter toSocket = new PrintWriter(socket.getOutputStream(), true);
                            BufferedReader fromSocket = new BufferedReader(
                                    new InputStreamReader(socket.getInputStream()));) {
                        toSocket.println("Hello from the client " + socket.getLocalAddress());
                        String line = fromSocket.readLine();
                        System.out.println("Response from server: " + line);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    if (socket != null && !socket.isClosed()) {
                        try {
                            socket.close();
                            System.out.println("socket is closed");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        };
    }

    public static void main(String[] args) {
        Client client = new Client();
        for (int i = 0; i < 100; i++) {
            try {
                Thread thread = new Thread(client.getRunnable());
                thread.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
