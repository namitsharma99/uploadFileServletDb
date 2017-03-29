package mycode.java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.tomcat.util.codec.binary.Base64;

@WebServlet("/save")
// to avoid this error -
// "java.lang.IllegalStateException: Unable to process parts as no multi-part configuration has been provided",
// @MultipartConfig annotation has been added
@MultipartConfig
public class MyServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// defining the credentials for the jdbc connection !!
		final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
		final String DB_URL = "jdbc:mysql://127.0.0.1:3306/namit_schema";
		final String USER = "root";
		final String PASS = "Intel@01";

		
		// getting the values from the JSP form
		String strId = request.getParameter("id");
		int id = strId == null ? 0 : Integer.parseInt(strId);
		String name = request.getParameter("name");
		Part myFile = request.getPart("myFile");

		InputStream inputStream = null;
		String encodedImgStr = null;
		
		
		// to check if multipartfile code is working or not, try syso for name and size
		if (null != myFile) {
			System.out.println(myFile.getName());
			System.out.println(myFile.getSize());
			inputStream = myFile.getInputStream();
		}

		
		// preparing statement for execution of object persistence in DB
		Connection connection = null;
		try {
			Class.forName(JDBC_DRIVER);
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			String myQuery = "insert into storage values (?, ?, ?)";
			PreparedStatement preparedstatement = connection
					.prepareStatement(myQuery);
			preparedstatement.setInt(1, id);
			preparedstatement.setString(2, name);
			preparedstatement.setBlob(3, inputStream);

			preparedstatement.execute();
			System.out.println("Statements executed");
			preparedstatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		///////          // fetching the above record from the DB and displaying on the web-page //       ///////////////
		
		String fetchedName = null;
		Blob fetchedBlob = null;
		byte[] imageBytes = null;
		
		String myNewQuery = "select * from storage where id = " + id;
		byte[] encodedImage = null;
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(myNewQuery);

			while (resultSet.next()) {
				fetchedBlob = resultSet.getBlob("file");
				fetchedName = resultSet.getString("name");
			}
			InputStream ipStream = null;
			if (null != fetchedBlob) {
				ipStream = fetchedBlob.getBinaryStream(1, fetchedBlob.length());
				System.out.println("ipStream -- " + ipStream);
			}
			
		
			// Approach 1
			// this is for trying base64 encoding
			imageBytes = fetchedBlob.getBytes(1, (int)fetchedBlob.length());			
			encodedImage = Base64.encodeBase64(imageBytes);	
			encodedImgStr = new String(encodedImage);
			System.out.println("Encoded image --> "+encodedImgStr);
			
			ServletOutputStream outputStream = response.getOutputStream();
			response.setContentType("html");
			String htmlContent = 
					  "<html> "
					+ 	"<body> "
					+ 		"hello " + fetchedName 
					+ 		"<br> "
					+		"<img width='50' height='80'  src='data:image/png;base64, "+encodedImgStr+"'>"
					+ 	"</body> "
					+ "</html>";
			outputStream.write(htmlContent.getBytes());
	
			
			/* ----------------------------------------------------------------------------------------------------------- */
			
			
			// Approach 2
			// this is for trying temporary file creation
			// this file will stay till JVM is running, because when we mention deleteOnExit, it is expected to be removed :P
			
			String prefix = "foobar";
		    String suffix = ".tmp";
			File tempFile2 = File.createTempFile(prefix, suffix);
			FileOutputStream fos = new FileOutputStream(tempFile2);
			fos.write(imageBytes);
			fos.close();
		    tempFile2.deleteOnExit();
		    System.out.format("Canonical filename: %s\n", tempFile2.getCanonicalFile());
		    System.out.println("Temp file : " + tempFile2.getAbsolutePath());
		    
		    // uncomment the below section, if you would want to try using temporary file for image hosting
		    // modern days browsers don't allow this, due to security issues, and yes they sound good with the reason
		    // Not allowed to load local resource <-- error we get
		    
		    /* ServletOutputStream outputStream = response.getOutputStream();
			response.setContentType("html");
			
			String htmlContent = 
					  "<html> "
					+ 	"<body> "
					+ 		"hello " + fetchedName 
					+ 		"<br> "
					+ 		"<img src=\""+ tempFile2.getAbsolutePath() +"\" height=\"40\" width=\"40\" >"
					+ 	"</body> "
					+ "</html>";
			outputStream.write(htmlContent.getBytes());*/
			
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
