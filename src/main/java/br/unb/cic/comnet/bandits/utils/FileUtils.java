package br.unb.cic.comnet.bandits.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

import br.unb.cic.comnet.bandits.environment.GeneralParameters;
import jade.util.Logger;

public class FileUtils {
	
	public static boolean appendRatingsInfo(String fileName, String info, Logger logger) {
		String fullFileName = GeneralParameters.mountOutputFileName(fileName);
		try (
			 FileWriter fw = new FileWriter(fullFileName, true);
			 BufferedWriter bw = new BufferedWriter(fw);
			 PrintWriter pw = new PrintWriter(bw)
		){
			pw.println(info + ";" + LocalDateTime.now().toString());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			logger.log(Logger.WARNING, "Can not write out info!");			
		}		
		return false;
	}
	
	public static boolean appendAttackerCost(String fileName, Long pulls, Double epsilon, Double accumulatedCost, Logger logger) {
		String fullFileName = GeneralParameters.mountOutputFileName(fileName);
		try (
			 FileWriter fw = new FileWriter(fullFileName, true);
			 BufferedWriter bw = new BufferedWriter(fw);
			 PrintWriter pw = new PrintWriter(bw)
		){
			pw.println(String.format("%.4f;%.4f", epsilon, accumulatedCost) + ";" + pulls + ";" + LocalDateTime.now().toString());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			logger.log(Logger.WARNING, "Can not write out info!");			
		}		
		return false;
	} 	
	
}
