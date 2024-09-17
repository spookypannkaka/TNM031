// An example class that uses the secure server socket class
package server;

import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import java.security.*;
import java.util.StringTokenizer;


public class SecureAdditionServer {
	private int port;
	// This is not a reserved port number
	static final int DEFAULT_PORT = 8189;
	static final String KEYSTORE = "src/server/LIUkeystore.ks";
	static final String TRUSTSTORE = "src/server/LIUtruststore.ks";
	static final String KEYSTOREPASS = "123456";
	static final String TRUSTSTOREPASS = "abcdef";

    // Designate the folder where files will be stored
    static final String SERVER_FILE_DIRECTORY = "server-files/";
	
	/** Constructor
	 * @param port The port where the server
	 *    will listen for requests
	 */
	SecureAdditionServer( int port ) {
		this.port = port;
	}
	
	/** The method that does the work for the class */
	public void run() {
		try {
			KeyStore ks = KeyStore.getInstance( "JCEKS" );
			ks.load( new FileInputStream( KEYSTORE ), KEYSTOREPASS.toCharArray() );
			
			KeyStore ts = KeyStore.getInstance( "JCEKS" );
			ts.load( new FileInputStream( TRUSTSTORE ), TRUSTSTOREPASS.toCharArray() );
			
			KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
			kmf.init( ks, KEYSTOREPASS.toCharArray() );
			
			TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
			tmf.init( ts );
			
			SSLContext sslContext = SSLContext.getInstance( "TLS" );
			sslContext.init( kmf.getKeyManagers(), tmf.getTrustManagers(), null );
			SSLServerSocketFactory sslServerFactory = sslContext.getServerSocketFactory();
			SSLServerSocket sss = (SSLServerSocket) sslServerFactory.createServerSocket( port );
			sss.setEnabledCipherSuites( sss.getSupportedCipherSuites() );
			
			System.out.println("\n>>>> SecureAdditionServer: active ");
			SSLSocket incoming = (SSLSocket)sss.accept();

      		BufferedReader in = new BufferedReader( new InputStreamReader( incoming.getInputStream() ) );
			PrintWriter out = new PrintWriter( incoming.getOutputStream(), true );			
			
			// Old numbers code
			/*String str;
			while ( !(str = in.readLine()).equals("") ) {
				double result = 0;
				StringTokenizer st = new StringTokenizer( str );
				try {
					while( st.hasMoreTokens() ) {
						double d = Double.parseDouble(st.nextToken());
						result += d;
					}
					out.println( "The result is " + result );
				}
				catch( NumberFormatException nfe ) {
					out.println( "Sorry, your list contains an invalid number" );
				}
			}*/

            // Create server directory if it doesn't exist
            File directory = new File(SERVER_FILE_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdir();
            }

            String command;
            while ((command = in.readLine()) != null && !command.equals("")) {
                // Handle upload, download, delete
                if (command.startsWith("UPLOAD")) {
                    handleUpload(command, in, out);
                } else if (command.startsWith("DOWNLOAD")) {
                    handleDownload(command, out);
                } else if (command.startsWith("DELETE")) {
                    handleDelete(command, out);
                } else {
                    out.println("Invalid command. Use UPLOAD, DOWNLOAD, DELETE.");
                }
            }

			incoming.close();
		}
		catch( Exception x ) {
			System.out.println( x );
			x.printStackTrace();
		}
	}

    /** Handle file upload from client */
    private void handleUpload(String command, BufferedReader in, PrintWriter out) {
        try {
            String filename = command.split(" ")[1];
            File file = new File(SERVER_FILE_DIRECTORY + filename);
            PrintWriter fileOut = new PrintWriter(new FileWriter(file));
    
            String fileLine;
            while (!(fileLine = in.readLine()).equals("")) {
                fileOut.println(fileLine); // Write the file line by line
            }
            
            fileOut.close();  // Ensure the file is properly closed after writing
            out.println("File uploaded successfully.");
            
        } catch (IOException e) {
            out.println("Error uploading file: " + e.getMessage());
        }
    }

    /** Handle file download to client */
    private void handleDownload(String command, PrintWriter out) {
        try {
            String filename = command.split(" ")[1];
            File file = new File(SERVER_FILE_DIRECTORY + filename);

            if (file.exists()) {
                out.println("File found");
                BufferedReader fileReader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = fileReader.readLine()) != null) {
                    out.println(line); // Send each line of the file to the client
                }
                out.println(""); // Indicate end of file transfer
                fileReader.close();
            } else {
                out.println("File not found.");
            }
        } catch (IOException e) {
            out.println("Error downloading file: " + e.getMessage());
        }
    }

    /** Handle file deletion */
    private void handleDelete(String command, PrintWriter out) {
        String filename = command.split(" ")[1];
        File file = new File(SERVER_FILE_DIRECTORY + filename);

        if (file.exists()) {
            if (file.delete()) {
                out.println("File deleted successfully.");
            } else {
                out.println("Error deleting file.");
            }
        } else {
            out.println("File not found.");
        }
    }
	
	
	/** The test method for the class
	 * @param args[0] Optional port number in place of
	 *        the default
	 */
	public static void main( String[] args ) {
		int port = DEFAULT_PORT;
		if (args.length > 0 ) {
			port = Integer.parseInt( args[0] );
		}
		SecureAdditionServer addServe = new SecureAdditionServer( port );
		addServe.run();
	}
}

