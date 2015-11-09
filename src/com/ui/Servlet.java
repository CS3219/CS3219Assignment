package com.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.controller.Controller;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.*;

@MultipartConfig
public class Servlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		ArrayList<String> cvFiles= new ArrayList<String>();
		String jobPos = "";
	    String destPath = System.getProperty("user.home")+"/uploads/";
	    String jobDescName="";
	    File dir = new File(destPath);
	    String results="";
	    
	    createDir(dir);
	    boolean isMultipart = ServletFileUpload.isMultipartContent(req);
	       if (isMultipart) {
	           FileItemFactory factory = new DiskFileItemFactory();
	           ServletFileUpload upload = new ServletFileUpload(factory);
	           List items = null;
	           try {
	               items = upload.parseRequest(req);
	           } catch (FileUploadException e) {
	            //Problem in reading stream
	               e.printStackTrace();
	           }
	           Iterator itr = items.iterator();
	           int fileNum=1;
	           while (itr.hasNext()) {
	            //Iterate files
	               FileItem item = (FileItem) itr.next();
	               if (!item.isFormField()) {
	                   try {
	                	   String fieldName= item.getFieldName();
	                       String itemName = item.getName();
	                       if ("".equals(itemName)){
	                           continue;
	                       }
	                       if(fieldName.equals("upload_jobdesc")){
	                       File savedFile = new File(destPath + "/" +itemName);
	                       item.write(savedFile);
	                       jobDescName=savedFile.getAbsoluteFile().toString();
	                     
	                       }else{
	                    	   File savedFile = new File(destPath + "/" +itemName+fileNum);
		                       item.write(savedFile);
		                       cvFiles.add(savedFile.getAbsoluteFile().toString());
		                       fileNum++;
	                       }
	                      
	                   } catch (Exception e) {
	                           e.printStackTrace();
	                       //Could not save file
	                   }
	               }
	               else{
	            	   jobPos = item.getString();
	            	   System.out.println(item.getFieldName());
	               }
	           }
	         results = Controller.startAnalyser(jobPos, jobDescName,cvFiles);
	       //   System.out.println("Position: " + jobPos); 
	           
	       }else{
	    	   results="Error: Uploading failed.";
	       }
	    displayResults(results,resp); 	       
	}

	private void displayResults(String results, HttpServletResponse resp) throws IOException {
		// TODO Auto-generated method stub
		resp.getWriter().println("<html>");
		resp.getWriter().println("<head>");
		resp.getWriter().println("<title>Results</title>");
		
		resp.getWriter().println("<style type=\"text/css\">");
		resp.getWriter().println("body {");
		resp.getWriter().println("background-image: url(\"bg-image.jpg\");");
		resp.getWriter().println("background-color: #000000;}");

		resp.getWriter().println("#header, #footer {");
		resp.getWriter().println("font-size: large;");
		resp.getWriter().println("padding: 0.3em;");
		resp.getWriter().println("background: #000000;}");

		resp.getWriter().println("table {");
		resp.getWriter().println("margin: 120px;");
		resp.getWriter().println("padding-top: 2px;");
		resp.getWriter().println("padding-bottom: 120px;");
		resp.getWriter().println("padding-right: 40px;");
		resp.getWriter().println("padding-left: 40px;");
		resp.getWriter().println("background-color: #ffffff;");
		resp.getWriter().println("border: 2px solid black;");
		resp.getWriter().println("opacity: 0.6;");
		resp.getWriter().println("filter: alpha(opacity = 60); }");

		resp.getWriter().println("td {");
		resp.getWriter().println("padding: 10px;}");
		
		resp.getWriter().println("</style>");
		
		resp.getWriter().println("</head>");
		resp.getWriter().println("<body>");
		resp.getWriter().println("<div align=\"center\">");
		resp.getWriter().println("<form action=\"index.html\" method=\"post\"");
		resp.getWriter().println("enctype=\"multipart/form-data\">");
		resp.getWriter().println("<table style=\"height: 564px; \">");
		resp.getWriter().println("<tr>");
		resp.getWriter().println("<td style=\"border-bottom: 1pt solid black; height: 20px\" colspan=\"2\">");
		resp.getWriter().println("<h1>Results:</h1>");
		resp.getWriter().println("</td>");
		resp.getWriter().println("</tr>");
		resp.getWriter().println("<tr>");
		resp.getWriter().println("<td align= \"center\" colspan=\"2\" style=\"width: 350px; height: 136px;\"><textarea style=\"width: 486px; height: 327px;\">");
		resp.getWriter().println(results);
		resp.getWriter().println("</textarea></td>");
		
		resp.getWriter().println("</tr>");
		resp.getWriter().println("<tr><td align=\"center\" colspan=\"2\"><input type=\"submit\" value=\"Submit New Description\"></td></tr>");
		resp.getWriter().println("</table>");
		resp.getWriter().println("</form>");
		resp.getWriter().println("</div>");
		resp.getWriter().println("</body>");
		resp.getWriter().println("</html>");

	}

	private void createDir(File dir) {
		// TODO Auto-generated method stub
		if(!dir.exists()){
			dir.mkdir();
		}
	}

	private static String getSubmittedFileName(Part part) {
		for (String cd : part.getHeader("content-disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				String fileName = cd.substring(cd.indexOf('=') + 1).trim()
						.replace("\"", "");
				return fileName.substring(fileName.lastIndexOf('/') + 1)
						.substring(fileName.lastIndexOf('\\') + 1);
			}
		}
		return null;
	}
}
