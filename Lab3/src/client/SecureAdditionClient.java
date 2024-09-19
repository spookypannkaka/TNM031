// A client-side class that uses a secure TCP/IP socket
package client;

import java.io.*;
import java.net.*;
import java.security.KeyStore;
import javax.net.ssl.*;

public class SecureAdditionClient {
	private InetAddress host;
	private int port;
	// This is not a reserved port number 
	static final int DEFAULT_PORT = 8189;
	static final String KEYSTORE = "src/client/LIUkeystore.ks";
	static final String TRUSTSTORE = "src/client/LIUtruststore.ks";
	static final String KEYSTOREPASS = "123456";
	static final String TRUSTSTOREPASS = "abcdef";
  
	
	// Constructor @param host Internet address of the host where the server is located
	// @param port Port number on the host where the server is listening
	public SecureAdditionClient( InetAddress host, int port ) {
		this.host = host;
		this.port = port;
	}
	
  // The method used to start a client object
	public void run() {
		try {

			// Load the keystore file, with the client's private key and certificates
			KeyStore ks = KeyStore.getInstance( "JCEKS" );
			ks.load( new FileInputStream( KEYSTORE ), KEYSTOREPASS.toCharArray() );
			
			// Load trusted certificates, has the server's certificate
			KeyStore ts = KeyStore.getInstance( "JCEKS" );
			ts.load( new FileInputStream( TRUSTSTORE ), TRUSTSTOREPASS.toCharArray() );
			
			KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
			kmf.init( ks, KEYSTOREPASS.toCharArray() );
			
			TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
			tmf.init( ts );
			
			SSLContext sslContext = SSLContext.getInstance( "TLS" ); // TLS as chosen protocol
			sslContext.init( kmf.getKeyManagers(), tmf.getTrustManagers(), null ); // Initialize key managers that hold certificates
			SSLSocketFactory sslFact = sslContext.getSocketFactory();      	
			SSLSocket client =  (SSLSocket)sslFact.createSocket(host, port); // Here we connect with the server
			client.setEnabledCipherSuites( client.getSupportedCipherSuites() );
			System.out.println("\n>>>> SSL/TLS handshake completed");
			
			BufferedReader socketIn;
			socketIn = new BufferedReader( new InputStreamReader( client.getInputStream() ) );
			PrintWriter socketOut = new PrintWriter( client.getOutputStream(), true );

			//
			BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
			
			// Sending numbers to the server (old code)
			/*
			String numbers = "1.2 3.4 5.6";
			System.out.println( ">>>> Sending the numbers " + numbers+ " to SecureAdditionServer" );
			socketOut.println( numbers );
			System.out.println( socketIn.readLine() );
			
			socketOut.println ( "" );
			*/

			while (true) {
                System.out.println("Enter command: UPLOAD, DOWNLOAD, DELETE followed by filename (or EXIT to quit):");
                String command = userInput.readLine();
                
				// Quit the program with EXIT
                if (command.equalsIgnoreCase("EXIT")) {
                    break;
                }

				// Send command to server
                socketOut.println(command);

				// Upload code
                if (command.startsWith("UPLOAD")) {
                    String filename = command.split(" ")[1];
                    File file = new File(filename);
                    if (file.exists()) {
                        System.out.println("Uploading file: " + filename);
                        BufferedReader fileReader = new BufferedReader(new FileReader(file));
                        String line;
                        while ((line = fileReader.readLine()) != null) {
                            socketOut.println(line);
                        }
                        socketOut.println(""); // Indicate end of file
                        fileReader.close();

                        // Wait for server confirmation of upload success
                        String serverResponse = socketIn.readLine();
                        if (serverResponse.equals("File uploaded successfully.")) {
                            System.out.println("File uploaded successfully: " + filename);
                        } else {
                            System.out.println("Server error during upload: " + serverResponse);
                        }
                    } else {
                        System.out.println("File not found: " + filename);
                    }

				// Download code
                } else if (command.startsWith("DOWNLOAD")) {
                    String filename = command.split(" ")[1];

                    // Read server response
                    String response = socketIn.readLine();
                    if (response.equals("File found")) {
                        // Open a local file to save the downloaded content
                        PrintWriter fileOut = new PrintWriter(new FileWriter(filename));

                        String line;
                        System.out.println("Downloading file...");
                        while (!(line = socketIn.readLine()).equals("")) {
                            fileOut.println(line);  // Save each line from the server to the file
                        }
                        fileOut.close();
                        System.out.println("File downloaded successfully.");
                    } else {
                        System.out.println("File not found on server.");
                    }

				// Delete code
                } else if (command.startsWith("DELETE")) {
                    String response = socketIn.readLine();
                    System.out.println(response); // Server response for deletion
                } else {
                    System.out.println("Invalid command. Use UPLOAD, DOWNLOAD, DELETE, or EXIT.");
                }
            }

			
		}
		catch( Exception x ) {
			System.out.println( x );
			x.printStackTrace();
		}
	}
	
	
	// The test method for the class @param args Optional port number and host name
	public static void main( String[] args ) {
		try {
			InetAddress host = InetAddress.getLocalHost();
			int port = DEFAULT_PORT;
			if ( args.length > 0 ) {
				port = Integer.parseInt( args[0] );
			}
			if ( args.length > 1 ) {
				host = InetAddress.getByName( args[1] );
			}
			SecureAdditionClient addClient = new SecureAdditionClient( host, port );
			addClient.run();
		}
		catch ( UnknownHostException uhx ) {
			System.out.println( uhx );
			uhx.printStackTrace();
		}
	}
}
