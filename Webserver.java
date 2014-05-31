import java.io.*;
import java.net.*;
import java.util.*;
	
public final class Webserver
{

	public static void main(String[] argv) throws Exception
	{
		//set port number
		int port=Integer.parseInt(argv[0]);
		//set up new listening socket
		ServerSocket listenSocket = new ServerSocket(port);
		//continuously loop to wait for connections
		while(true)
		{
		  //
		  Socket connectSocket = listenSocket.accept();
		  
  		  HttpRequest request = new HttpRequest(connectSocket);
		  //send newly created connection to private thread
		  Thread thread = new Thread(request); 
			
		  thread.start();
		}
	}
}
	final class HttpRequest implements Runnable
	{
		final static String CRLF = "\r\n";
		Socket socket;
		
		public HttpRequest(Socket socket) throws Exception
		{
			this.socket=socket;
		}
		
		public void run()
		{
		   try{
		     processRequest();
		  }catch(Exception e){
		     System.out.println(e);
		    }
		}
	private void processRequest() throws Exception
	 { 
		
		InputStream is = socket.getInputStream();
		
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		
		BufferedReader br = new BufferedReader(
				    new InputStreamReader(is));
	 
	
	String requestLine = br.readLine();
	
	System.out.print(requestLine);
	
	String headerLine = null;
	
	while((headerLine = br.readLine()).length()!=0)
	 {
	  System.out.println(headerLine);
	 }
	
	os.close();
	br.close();
	socket.close();
	
	}
	}
	  

		
