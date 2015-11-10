package com.ui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.controller.Controller;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.*;
import org.apache.commons.io.FileUtils;

@SuppressWarnings("serial")
@MultipartConfig
public class Servlet extends HttpServlet {
	@SuppressWarnings("rawtypes")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		ArrayList<String> cvFiles = new ArrayList<String>();
		String jobPos = "";
		String jobDescName = "";
		String results = "";
		boolean fileErr = false;
		int maxFileNum = 21;
		String tmp_dir_prefix = "CVAnalyser_";

		// get the default temporary folders path
		String default_tmp = System.getProperty("java.io.tmpdir");
		System.out.println(default_tmp);
		Path tmp_2 = null;
		try {
			tmp_2 = Files.createTempDirectory(tmp_dir_prefix);
		} catch (IOException e) {
			displayErrorAlert(resp);
			fileErr = true;
		}
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		if (isMultipart) {
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			List items = null;
			try {
				items = upload.parseRequest(req);
			} catch (FileUploadException e) {
				if (!fileErr) {
					displayErrorAlert(resp);
					fileErr = true;
				}
			}
			Iterator itr = items.iterator();
			int fileNum = 1;
			while (itr.hasNext()) {
				// Iterate files
				if (fileNum > maxFileNum) {
					resp.setContentType("text/html");
					resp.getWriter().println(
							"<script type=\"text/javascript\">");
					resp.getWriter()
							.println("alert('Max file upload is 20!');");
					resp.getWriter()
							.println(" window.location = 'index.html';");
					resp.getWriter().println("</script>");
					fileErr = true;
				} else {
					FileItem item = (FileItem) itr.next();
					if (!item.isFormField()) {
						try {
							String fieldName = item.getFieldName();
							String itemName = item.getName();
							if ("".equals(itemName)) {
								continue;
							}
							if (fieldName.equals("upload_jobdesc")) {
								File savedFile = new File(tmp_2.toString()
										+ "/" + itemName);
								item.write(savedFile);
								jobDescName = savedFile.getAbsoluteFile()
										.toString();

							} else {
								File savedFile = new File(tmp_2.toString()
										+ "/" + fileNum + itemName);
								item.write(savedFile);
								cvFiles.add(savedFile.getAbsoluteFile()
										.toString());
								fileNum++;
							}

						} catch (Exception e) {
							if (!fileErr) {
								displayErrorAlert(resp);
								fileErr = true;
							}
						}
					} else {
						jobPos = item.getString();
						System.out.println(item.getFieldName());
					}
				}
			}
			try {
				results = Controller
						.startAnalyser(jobPos, jobDescName, cvFiles);
			} catch (Exception e) {
				resp.setContentType("text/html");
				resp.getWriter().println("<script type=\"text/javascript\">");
				resp.getWriter().println("alert('Please ensure that you have uploaded PDF Files!');");
				resp.getWriter().println(" window.location = 'index.html';");
				resp.getWriter().println("</script>");
				fileErr = true;
			}
			// System.out.println("Position: " + jobPos);

		} else {
			if (!fileErr) {
				displayErrorAlert(resp);
				fileErr = true;
			}
		}
		try {
			File dir = new File(tmp_2.toString());
			FileUtils.cleanDirectory(dir);
			dir.delete();
		} catch (Exception e) {
			if (!fileErr) {
				displayErrorAlert(resp);
				fileErr = true;
			}
		}
		if (!fileErr) {
			displayResults(results, resp);
		}
	}

	private void displayErrorAlert(HttpServletResponse resp) throws IOException {
		resp.setContentType("text/html");
		resp.getWriter().println("<script type=\"text/javascript\">");
		resp.getWriter().println("alert('Error uploading file!');");
		resp.getWriter().println(" window.location = 'index.html';");
		resp.getWriter().println("</script>");

	}

	private void displayResults(String results, HttpServletResponse resp)
			throws IOException {
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
		resp.getWriter().println("opacity: 0.8;");
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
		resp.getWriter()
				.println(
						"<td style=\"border-bottom: 1pt solid black; height: 20px\" colspan=\"2\">");
		resp.getWriter().println("<h1>Results:</h1>");
		resp.getWriter().println("</td>");
		resp.getWriter().println("</tr>");
		resp.getWriter().println("<tr>");
		resp.getWriter()
				.println(
						"<td align= \"center\" colspan=\"2\" style=\"width: 350px; height: 136px;\"><textarea style=\"width: 486px; height: 327px;\">");
		resp.getWriter().println(results);
		resp.getWriter().println("</textarea></td>");

		resp.getWriter().println("</tr>");
		resp.getWriter()
				.println(
						"<tr><td align=\"center\" colspan=\"2\"><input type=\"submit\" value=\"Submit New Description\"></td></tr>");
		resp.getWriter().println("</table>");
		resp.getWriter().println("</form>");
		resp.getWriter().println("</div>");
		resp.getWriter().println("</body>");
		resp.getWriter().println("</html>");

	}

}
