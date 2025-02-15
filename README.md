# File Transfer System(MultiThreaded WEBSERVER) using Java Sockets

A client-server application that enables file upload and download operations using Java Socket Programming.

## Features
- Multi-threaded server handling multiple client connections
- File upload functionality
- File download functionality
- Progress tracking for file transfers
- Thread pool implementation
- Separate directories for server and client files

## Prerequisites
- Java Development Kit (JDK) 8 or higher
- IDE (Eclipse, IntelliJ IDEA, or any preferred IDE)

## Running the Application
1. Start the Server
2. Start the Client
3. Place test.txt file in project root
4. Files will be uploaded to server_files and downloaded to client_files

## Implementation Details
- Server runs on port 8010
- Uses thread pool for handling multiple clients
- Buffer size: 4096 bytes
- Supports file upload and download operations
- Progress tracking during transfer

## Future Enhancements
- GUI implementation
- File transfer encryption
- Resume interrupted transfers
- File integrity verification
- User authentication

## Known Issues
- Large files might cause memory issues
- Basic error handling needs improvement

## Project Structure
```bash
file-transfer-system/
│
├── src/
│   ├── Server.java
│   └── Client.java
│
├── server_files/
├── client_files/
└── test.txt
```
