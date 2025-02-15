Markdown

Collapse
# File Transfer System using Java Sockets

A client-server application that enables file upload and download operations using Java Socket Programming.

## Features

- Multi-threaded server handling multiple client connections
- File upload functionality
- File download functionality
- Progress tracking for file transfers
- Thread pool implementation for efficient resource management
- Separate directories for server and client files
- Basic error handling and logging

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- Basic understanding of Socket Programming
- IDE (Eclipse, IntelliJ IDEA, or any preferred IDE)

## Project Structure
file-transfer-system/
│
├── src/
│ ├── Server.java
│ └── Client.java
│
├── server_files/ # Directory for server-side files
├── client_files/ # Directory for client-side files
├── test.txt # Sample test file
└── README.md



## Getting Started

### Installation

1. Clone the repository
```bash
git clone https://github.com/yourusername/file-transfer-system.git
cd file-transfer-system
Compile the Java files
BASH

javac src/*.java
Running the Application
Start the Server
BASH

java src.Server
Start the Client
BASH

java src.Client
Usage
Server Side
The server automatically:

Creates a server_files directory if it doesn't exist
Listens on port 8010
Handles multiple client connections using a thread pool
Processes upload and download requests
Client Side
The client can:

Upload files to server
Download files from server
Track transfer progress
Handle multiple file transfers
Code Examples
Uploading a File
Java

// Client side
uploadFile("test.txt", toSocket, dataOutputStream);
Downloading a File
Java

// Client side
downloadFile("test.txt", toSocket, fromSocket, dataInputStream);
Protocol
The application uses a simple text-based protocol:

Upload Command:

UPLOAD <filename> <filesize>
Download Command:

DOWNLOAD <filename>
Exit Command:

EXIT
Configuration
Server configuration can be modified in Server.java:

Java

private static final int PORT = 8010;
private static final int POOL_SIZE = 10;
private static final int BUFFER_SIZE = 4096;
Error Handling
The system handles various errors including:

File not found
Connection issues
Invalid commands
IO exceptions
Contributing
Fork the repository
Create your feature branch (git checkout -b feature/AmazingFeature)
Commit your changes (git commit -m 'Add some AmazingFeature')
Push to the branch (git push origin feature/AmazingFeature)
Open a Pull Request
Future Enhancements
 GUI implementation
 File transfer encryption
 Resume interrupted transfers
 File integrity verification
 User authentication
 Transfer speed limiting
 Directory transfer support
Known Issues
Large files might cause memory issues
No encryption for file transfers
Basic error handling needs improvement


Acknowledgments
Java Socket Programming Documentation
Oracle Java Tutorial
Stack Overflow Community
Contact - chetang1109@gmail.com
Name - @GuptaChetan11
Project Link: https://github.com/GuptaChetan11/file-transfer-system



System Requirements
Operating System: Windows/Linux/MacOS
RAM: 2GB minimum
Storage: 100MB minimum
Java Runtime Environment (JRE) 8 or higher
Performance
Tested with files up to 1GB
Supports multiple concurrent transfers
Memory usage depends on buffer size and number of concurrent connections

Collapse

