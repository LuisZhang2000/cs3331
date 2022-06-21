import java.io.*;
import java.net.*;
import java.util.*;

public class WebServer {
    public static void main(String[] args) throws IOException {
        
        int port = Integer.parseInt(args[0]);
        ServerSocket server = new ServerSocket(port);

        //System.out.println("\ntesting\n");

        // Processing loop.
        while (true) {
            try {
                Socket socket = server.accept();
                InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                BufferedReader reader = new BufferedReader(isr);
                
                String line = reader.readLine();
                String[] headerInfo = line.split(" ");
                String method = headerInfo[0];
                String fileName = new String(headerInfo[1].substring(1));
                String fileType = (headerInfo[1].substring(headerInfo[1].indexOf("."))).substring(1);
                String contentType = new String();

                if (fileType.equals("html")) {
                    contentType = "Content-Type: text/html";
                } else if (fileType.equals("png")) {
                    contentType = "Content-Type: image/png";
                }

                File file = new File(fileName);
                PrintStream result = new PrintStream(socket.getOutputStream(), true);

                if (method.equals("GET")) {

                    if (!file.exists()) {
                        result.println("HTTP/1.1 404 Not Found");
                        result.println();
                        result.println("File Not Found");
                        System.out.println("File Request Failed");
                    } else {
                        result.println("HTTP/1.1 200 OK");
                        result.println(contentType);
                        result.println(("Content-Length: " + file.length()));
                        result.println();

                        FileInputStream fis = new FileInputStream(file);

                        byte data[] = new byte[fis.available()];
                        fis.read(data);
                        result.write(data);
                        fis.close();

                        System.out.println("File Request Successful");
                    }
                    result.close();
                    socket.close();
                } else {
                    System.out.println("Only GET is supported");
                }

                server.close();
            } catch (Exception e) {

            }
        }
    }
}
