package com.controller;

import java.io.IOException;
import java.util.ArrayList;
import com.ui.ConvertPDF;
import com.analyser.*;
import com.parser.*;
public class Controller {
static ArrayList<CVObject> cvs = new ArrayList<CVObject>();

	public Controller(){
		
	}

	public static String startAnalyser(String jobPosition,String jobDescFileName, ArrayList<String> cvsFileName){
		String result = "";
		
		JobDescObject jobDescObj = parseJobDesc(jobDescFileName);
		ArrayList<CVObject> CVObj = parseCVs(cvsFileName);
		ArrayList<ArrayList<String>> resultList = analyse(jobDescObj,CVObj,jobPosition);
		int i=1;
		for(ArrayList<String> row : resultList){
			result+=("Candidate " + i + ":\n");
			result+=("Name: " + row.get(0));
			result+=("Rating: " + row.get(1) + "\n\n");
			i++;
		}
		
		return result;
		
	}
	
	private static JobDescObject parseJobDesc(String jobDescFileName){
		JobDescParser jobDescParser = new JobDescParser();
		ConvertPDF convert = new ConvertPDF();
		String fileName = null;
			try {
				fileName = convert.convertTxtToPDF(jobDescFileName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			JobDescObject jobDescriptions = jobDescParser.parseJobDesc(fileName);
			return jobDescriptions;
	}
	
	private static ArrayList<CVObject> parseCVs(ArrayList<String> cvsFileName) {
		CVParser cvParser = new CVParser();
		ConvertPDF convert = new ConvertPDF();

		cvs.clear();
		for(int i = 0; i < cvsFileName.size(); i++) {
			String fileName = null;
			try {
				fileName = convert.convertTxtToPDF(cvsFileName.get(i));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cvs.add(cvParser.parseCV(fileName));
		}
		return cvs;
		
	}

	private static ArrayList<ArrayList<String>> analyse(JobDescObject jobdesc, ArrayList<CVObject> cvs, String position){
		Analyser analyser = new Analyser();
		
		ArrayList<ArrayList<String>> results = analyser.analyse(jobdesc, cvs, position);
		return results;
		
	}
	
	 
}
