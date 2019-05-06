import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;


public final class EchoServer extends Thread {

		private static final int PORT = 8889;
		private EchoServer() {}
		public static void main(String args[]) {
			EchoServer echoServer = new EchoServer();
			if (echoServer != null) {
				echoServer.start();
			}
		}
		
		@SuppressWarnings("resource")
		public void run() {
		  try {
			  ServerSocket server = new ServerSocket(PORT, 1);
			  System.out.println("Server running");
			  while (true) {
				  Socket client = server.accept();
				  System.out.println("Client connected");
				  while (true) {
					  BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
					  System.out.println("Read from client");
					  String textLine = reader.readLine() + "\n";
					  if (textLine.equalsIgnoreCase("EXIT\n")) {
						  System.out.println("EXIT invoked, closing client");
						  break;
					  }
					  BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
					  System.out.println("Echo input to	 client");
					  writer.write("ECHO from server: " + textLine, 0, textLine.length() + 18);
					  writer.flush();
				  }
				  client.close();
			  }
		  } catch (IOException e) {
			  System.err.println(e);
		  }
		}
	}
