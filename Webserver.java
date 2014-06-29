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

		StringTokenizer tokens = new StringTokenizer(requestLine);	
		
		tokens.nextToken(); //skipping over GET command
		
		String fileName = "." + tokens.nextToken();	
		//open the requested file
		FileInputStream fis = null;

		boolean fileExists = true;
		
		try{
		
		 
		  fis= new FileInputStream(fileName);
		}catch(FileNotFoundException e)
		{
			fileExists = false;
		}

		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;
	
		if (fileExists)	
		{ 
			statusLine = "HTTP/1.0 200  ok "+ CRLF;
			contentTypeLine = "Content-type:" + contentType( fileName )+ CRLF;
			
		}else {
			statusLine = "HTTP/1.0 404 Not Found" + CRLF;
			contentTypeLine = "Content-type: text/html" +CRLF;
			entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>"	+
					"<BODY>Not Found</BODY></HTML>";
		}	

		//send the status line & content-type line, then send a CLRF
		os.writeBytes(statusLine);
		os.writeBytes(contentTypeLine);
		os.writeBytes(CRLF);//indicates end of header lines

		if(fileExists)
		{
		  sendBytes(fis, os);
		  fis.close();	
		}else {
		  os.writeBytes(entityBody);
		}
		

		os.close();
		br.close();
		socket.close();
	
	}

	private static void sendBytes(FileInputStream fis,  OutputStream os)
	throws Exception
	{
			byte[] buffer = new byte[1024];
			int bytes = 0;

			//building 1k buffer
			while((bytes = fis.read(buffer))!=-1) os.write(buffer, 0, bytes);
	}
	
	private static String contentType(String fileName)
	{
		if(fileName.endsWith(".html") || fileName.endsWith(".htm")) {
			return "text/html";
		}
		
		
		if(fileName.endsWith(".jpg")) {
			return "image/jpeg";
		}
		if(fileName.endsWith(".gif")) {
			return "image/gif";
		}
		return "application/octet-stream";
	}	
	
}
