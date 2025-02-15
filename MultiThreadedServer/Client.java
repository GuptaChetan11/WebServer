import java.io.*;
import java.net.*;

public class Client {
    private static final String DOWNLOAD_DIR = "client_files";
    private static final int BUFFER_SIZE = 4096;

    // Creates a runnable task for client operations
    public Runnable getRunnable() throws UnknownHostException, IOException {
        return new Runnable() {
            @Override
            public void run() {
                int port = 8010;
                Socket socket = null;
                try {
                    // create download directory if it doesn't exists
                    new File(DOWNLOAD_DIR).mkdirs();

                    // Connect to server
                    InetAddress address = InetAddress.getByName("localhost");
                    socket = new Socket(address, port);
                    System.out.println("Connected to server: " + socket.getInetAddress());

                    try (
                            // initialize all streams for communication
                            PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
                            BufferedReader fromServer = new BufferedReader(
                                    new InputStreamReader(socket.getInputStream()));
                            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());) {
                        // Receive and print server's greeting
                        String serverGreeting = fromServer.readLine();
                        System.out.println("Server: " + serverGreeting);

                        // Example operations - can be modified according to user's needs

                        // upload a file
                        uploadFile("test.txt", toServer, dataOutputStream);
                        String uploadResponse = fromServer.readLine();
                        System.out.println("Server response: " + uploadResponse);

                        // download the same file
                        downloadFile("test.txt", toServer, fromServer, dataInputStream);

                        // Send exit command
                        toServer.println("EXIT");
                    }
                } catch (IOException ex) {
                    System.err.println("Client error: " + ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    closeSocket(socket);
                }
            }
        };
    }

    // Handles file upload to server
    private void uploadFile(String fileName, PrintWriter toServer, DataOutputStream dos) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("file not found: " + fileName);
            return;
        }

        System.out.println("Starting upload of file: " + fileName);
        // send upload command with filename and size
        toServer.println("UPLOAD " + fileName + " " + file.length());

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            long totalSent = 0;
            int read;

            while ((read = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, read);
                totalSent += read;

                // Print progress
                if (totalSent % (BUFFER_SIZE * 10) == 0) {
                    System.out.printf("Upload progress: %.2f%%\n",
                            (double) totalSent / file.length() * 100);
                }
            }
            dos.flush();
        } catch (IOException ex) {
            System.err.println("Error uploading file: " + ex.getMessage());
            ex.printStackTrace();
        }
        System.out.println(("File uploaded successfully: " + fileName));
    }

    // Handles file download from server
    private void downloadFile(String fileName, PrintWriter toServer, BufferedReader fromServer,
            DataInputStream dis) throws IOException {
        System.out.println("Requesting download of file: " + fileName);
        // send download command
        toServer.println("DOWNLOAD: " + fileName);

        // Get file size from server
        long fileSize = Long.parseLong(fromServer.readLine());
        if (fileSize == -1) {
            System.out.println("file not found on server: " + fileName);
            return;
        }

        File file = new File(DOWNLOAD_DIR + File.separator + fileName);
        System.out.println("Starting download of file: " + fileName + " (Size: " + fileSize + " bytes)");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            long remaining = fileSize;
            long totalRead = 0;

            while (remaining > 0) {
                int read = dis.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                if (read == -1) {
                    break;
                }
                fos.write(buffer, 0, read);
                remaining -= read;
                totalRead += read;

                // Print progress
                if (totalRead % (BUFFER_SIZE * 10) == 0) {
                    System.out.printf("Download progress: %.2f%%\n",
                            (double) totalRead / fileSize * 100);
                }
            }
        }
        System.out.println("file downloaded successfully: " + fileName);
    }

    // Helper method to close socket safely
    private void closeSocket(Socket socket) {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
                System.out.println("socket closed");
            } catch (IOException ex) {
                System.err.println("error closing socket: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();

        // create multiple client threads
        for (int i = 0; i < 5; i++) { // reduced from 100 to 5 for testing
            try {
                Thread thread = new Thread(client.getRunnable());
                thread.start();

                // adding small delay between two consecutive client connections
                Thread.sleep(1000); // 1 sec delay
            } catch (IOException | InterruptedException ex) {
                System.err.println("error in creating client thread: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}