import java.io.*; 
import java.net.*; 
import java.util.*;
import java.nio.channels.FileChannel;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.swing.*;
public class WebServer{
    public static void main(String argv[]) throws Exception  {

          String requestMessageLine; 
          String fileName;
          ServerSocket listenSocket = new ServerSocket(6789); 
          Socket connectionSocket = listenSocket.accept();

          BufferedReader inFromClient =  new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); 
          DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

          requestMessageLine = inFromClient.readLine();

			
          StringTokenizer tokenizedLine = 
            new StringTokenizer(requestMessageLine);

       if (tokenizedLine.nextToken().equals("GET")){
			fileName = tokenizedLine.nextToken();

			if (fileName.startsWith("/") == true ) {
				fileName  = fileName.substring(1);
			}
                      File file = new File(fileName); 
					  File tempFile = new File("index2.html");
						
		  
		BufferedReader reader = new BufferedReader(new FileReader(file));
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));		
		BufferedWriter writer2 = new BufferedWriter(new FileWriter(tempFile));	
		
		String currentLine, currentLine1, tempString, cssFile;
		
		while((currentLine = reader.readLine()) != null){
			
			if(currentLine.contains("href=")){
				//System.out.println("found!");
				String[] css = currentLine.split("\\s+");
				for(int i = 0; i < css.length; i++){
					tempString = css[i];
					if(tempString.startsWith("href=")){
						Pattern p = Pattern.compile("\"([^\"]*)\"");
						Matcher m = p.matcher(tempString);
						while (m.find()) {
							cssFile = m.group(1);
							BufferedReader reader2 = new BufferedReader(new FileReader(cssFile));
							writer.write("<style>\n");
							while((currentLine1 = reader2.readLine()) != null) {			
								writer.write(currentLine1);
								writer.write("\n");
							}
							writer.write("</style>");
							reader2.close();
						}
						
						currentLine = "";
					}
				}
			}
			if(currentLine.contains("</BODY>")){
				String t;
					writer.write("<table>\n");
					while((t = inFromClient.readLine()) != null && !t.equals("")){
						StringTokenizer st = new StringTokenizer(t, ":");
						writer.write("<tr>\n");
						
						while (st.hasMoreTokens()) {
							 System.out.println();
							 writer.write("<td>"+st.nextToken()+"</td>\n");
						}
						writer.write("</tr>\n");
						
						
						System.out.println(t+"-");			
					}
					writer.write("</table>\n");				
			}			
			writer.write(currentLine);
			writer.write("\n");
		}	
		writer.close(); 
		reader.close();
		writer2.close();	
		


          FileInputStream inFile  = new FileInputStream (tempFile);
			int numOfBytes = (int) tempFile.length();
            byte[] fileInBytes = new byte[numOfBytes]; 
			inFile.read(fileInBytes);

          outToClient.writeBytes("HTTP/1.0 200 Document Follows\r\n");
				
		  outToClient.writeBytes("Content-Length: " + numOfBytes + "\r\n"); 
          outToClient.writeBytes("\r\n");
          outToClient.write(fileInBytes, 0, numOfBytes);		  
		  inFile.close();
	  
		  inFromClient.close();	
          connectionSocket.close();
	  
      }

     else System.out.println("Bad Request Message"); 
  
    } 
	 
} 