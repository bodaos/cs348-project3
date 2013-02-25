package MultithreadedServer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.imageio.ImageIO;

public class HTTPProcessor {

	/**
	 * @param args
	 */
	public static String rootDir;
	public static String userDir; 

	public static String process(String request, String root, String user){
		rootDir = root;
		userDir = user;
		String[] requestArray = request.split(" ");
		if(requestArray.length < 4) {
			return BadRequestResponse();
		}
		String relativePath = requestArray[2];

		if(HTTPProcessor.getMIME(relativePath).equals("image/jpeg")){
			//HTTPProcessor.imageHandler(request, rootDir, userDir, out, new BufferedOutputStream(clientSocket.getOutputStream()));
			return PageNotFoundResponse();
		}
		return requestHandler(request);
	}
	public static String requestHandler(String request){
		String[] requestArray = request.split(" ");
		String requestType;
		String relativePath = requestArray[2];;
		if( requestArray[1].equals("GET")){
			requestType = "GET";
		}else if (requestArray[1].equals("HEAD")){
			relativePath = requestArray[2];
			requestType = "HEAD";
		}else{
			return BadRequestResponse();
		}
		//System.out.println(relativePath);
		//To add a index.html if the path is a directory
		String dir = isUserDir(relativePath)? userDir: rootDir;
		relativePath = reformatPath(relativePath);
		String content = "";

		try {
			BufferedReader in = new BufferedReader(new FileReader(dir+ relativePath));
			String str;
			while ((str = in.readLine()) != null) {
				content +=str;
			}
			in.close();
		} catch (IOException e) {
			System.out.println(relativePath+ ":file reader problem");
			return PageNotFoundResponse();
		}
		String output =requestType.equals("GET")?  headerGenerator(relativePath, content)+content: headerGenerator(relativePath, content);

		return output;

	}

	public static String reformatPath(String relativePath){
		if (relativePath.endsWith("/")) relativePath = relativePath+ "index.html";
		if(isUserDir(relativePath)){
			int start = relativePath.indexOf("~");
			int end = relativePath.indexOf("/", start);
			if(end < 0) return BadRequestResponse();
			String username = relativePath.substring(start, end);
			relativePath =  relativePath.replaceAll(username, username.substring(1)+ "/public_html");
		}
		return relativePath;
	}
	public static boolean isUserDir(String relativePath){
		if(relativePath.startsWith("/~")){
			return true;
		}
		return false;
	}
	public static String headerGenerator(String relativePath, String content){
		String output =   "HTTP/1.1 200 OK \r\n"
				+ getDateString()+ "\r\n"
				+ "Content-Type:"+ getMIME(relativePath)+"\r\n"
				+ "Content-Length:"+content.length()+ "\r\n\r\n";
		return output;
	}

	public static String getMIME(String str){
		if(str.endsWith("/")) return "text/html";
		String extension = str.substring(str.lastIndexOf(".")+1);
		switch (extension){
		case "html":
			return "text/html";
		case "htm":
			return "text/html";
		case "jpeg":
			return "image/jpeg";
		case "jpg":
			return "image/jpeg";
		case "gif":
			return "image/gif";
		case "js":
			return "application/javascript";
		case "css":
			return "text/css";
		}
		return "error";
	}

	public static byte[] imageToByte (String relativePath) throws FileNotFoundException{
		File file = new File(rootDir+ relativePath);
		InputStream ios = new FileInputStream(file);
		ByteArrayOutputStream ous = new ByteArrayOutputStream();

		try {
			byte []buffer = new byte[4096];
			int read = 0;
			try {
				while ( (read = ios.read(buffer)) != -1 ) {
					//System.out.println(read);
					ous.write(buffer, 0, read);
				}
			} catch (IOException e) {
				System.err.println("cannot read in image!");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return ous.toByteArray();
		} finally { 
			try {
				if ( ous != null ) 
					ous.close();
			} catch ( IOException e) {
			}

			try {
				if ( ios != null ) 
					ios.close();
			} catch ( IOException e) {
			}
		}

	}
	public static void imageHandler(String request, String rootDir2, String userDir2, PrintWriter out, OutputStream outStream) throws FileNotFoundException{
		String[] requestArray = request.split(" ");
		String relativePath = requestArray[2];
		byte[] imgByte = imageToByte(relativePath);
		String output =  "HTTP/1.1 200 OK \r\n"
				+ getDateString()+ "\r\n"
				+ "Content-Type:"+ getMIME(relativePath)+"\r\n"
				+ "Content-Length:"+imgByte.length+ "\r\n\r\n";
		//System.out.println(output);
		out.println(output);
		System.out.println(output);
		out.flush();
		try {
			outStream.write(imgByte, 0, imgByte.length);
			outStream.flush();
		} catch (IOException e) {
			System.err.println("image failed output");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String BadRequestResponse(){
		System.out.println("bad request");
		String message = "Page Not Found!";
		return "HTTP/1.1 400 Bad Request \r\n"
		+ getDateString()+ "r\n"
		+ "Content-Type:"+"text/html"+"\r\n"
		+ "Content-Length:"+message.length()+ "\r\n\r\n"
		+ message+ "\r\n\r\n";
	}
	public static String PageNotFoundResponse(){
		System.out.println("file not found");
		String message = "Page Not Found!";
		return "HTTP/1.1 404 Not Found \r\n"
		+ getDateString()+ "r\n"
		+ "Content-Type:"+"text/html"+"\r\n"
		+ "Content-Length:"+message.length()+ "\r\n\r\n"
		+ message+ "\r\n\r\n";
	}

	public static String getDateString(){
		Date date = new Date();
		SimpleDateFormat simpleDateFormat =
				new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.US);
		return simpleDateFormat.format(date);
	}
	public static void main(String[] args) {
		//System.out.println(reformatPath("/~leffinger/cat.jpg"));
		//System.out.println(reformatPath("/~leffinger/"));
		//System.out.println(reformatPath("/courses/"));


	}

}
