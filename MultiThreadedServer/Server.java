import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {
    private final ExecutorService threadPool;
    private static final String UPLOAD_DIR = "server_files";
    private static final int BUFFER_SIZE = 4096;
    private boolean running = true; //state of server

    // Constructor initializes thread pool and creates upload directory
    public Server(int poolSize) {
        this.threadPool = Executors.newFixedThreadPool(poolSize);

        // Create directory for storing uploaded files
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    // Handles individual client connections
    public void handleClient(Socket clientSocket) {
        System.out.println("new client connected: " + clientSocket.getInetAddress());

        try (
                // Initialize all streams for communication
                PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());) {
            // Send welcome message to client
            toClient.println("Hello from Server: " + clientSocket.getInetAddress());

            // Continue processing client commands until client exits

            while (true) {
                String command = fromClient.readLine();
                if (command == null || command == "EXIT") {
                    System.out.println("Client disconnected: " + clientSocket.getInetAddress());
                    break;
                }

                // Parse command and parameters
                String[] parts = command.split(" ", 3);
                switch (parts[0]) {
                    case "UPLOAD":
                        handleUpload(parts[1], Long.parseLong(parts[2]), dataInputStream);
                        toClient.println("file uploaded successfully");
                        break;

                    case "DOWNLOAD":
                        handleDownload(parts[1], dataOutputStream, toClient);
                        break;

                    default:
                        System.out.println("Regular message from client: " + command);
                        toClient.println("Message received");
                }
            }
        } catch (IOException ex) {
            System.err.println("error in handling client: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Handles file upload from client
    private void handleUpload(String fileName, long fileSize, DataInputStream dis) throws IOException {
        File file = new File(UPLOAD_DIR + File.separator + fileName);
        System.out.println("Starting file upload: " + fileName + " (Size: " + fileSize + " bytes)");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            long remaining = fileSize;
            long totalRead = 0;

            while (remaining > 0) {
                int read = dis.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                if (read == -1)
                    break;
                fos.write(buffer, 0, read);
                remaining -= read;
                totalRead += read;

                // Print progress
                if (totalRead % (BUFFER_SIZE * 10) == 0) {
                    System.out.printf("Upload progress: %.2f%%\n",
                            (double) totalRead / fileSize * 100);
                }
            }
        }
        System.out.println("file upload completed successfully: " + fileName);
    }

    // Handles file download to client
    private void handleDownload(String fileName, DataOutputStream dos, PrintWriter toClient) throws IOException {
        File file = new File(UPLOAD_DIR + File.separator + fileName);
        if (!file.exists()) {
            System.out.println("file not found: " + fileName);
            toClient.println("-1");
            return;
        }

        System.out.println("Starting file download: " + fileName);
        toClient.println(file.length());

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            long totalSent = 0;
            int read;

            while ((read = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, read);
                totalSent += read;
                // Print progress
                if (totalSent % (BUFFER_SIZE * 10) == 0) {
                    System.out.printf("Download progress: %.2f%%\n",
                            (double) totalSent / file.length() * 100);
                }
            }
            dos.flush();
        }
        System.out.println("file download completed successfully: " + fileName);
    }

    //to automatically shutdown server
    public void shutdown(){
        running = false;
    }
    public static void main(String[] args) {
        int port = 8010;
        int poolSize = 10;
        Server server = new Server(poolSize);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(60000); // 1 minute timeout
            System.out.println("Server is listening on port: " + port);

            while (server.running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    // Handle each client in a separate thread from the pool
                    server.threadPool.execute(() -> server.handleClient(clientSocket));
                } catch (SocketTimeoutException e) {
                    System.out.println("No client connected in the last minute");
                    server.shutdown();
                }
            }
        } catch (IOException ex) {
            System.err.println("Server error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            server.threadPool.shutdown();
            System.out.println("server shutdown completed");
        }
    }
}