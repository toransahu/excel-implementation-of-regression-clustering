package jxl;

import weka.classifiers.functions.LeastMedSq;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;


public class RegressionLeastMedianSquare {
	public static String main(String filePath, int i) throws Exception{
		LeastMedSq lms = null;
		String equation="";
		DataSource source = new DataSource(filePath);
		try{
			Instances dataset = source.getDataSet();
			dataset.setClassIndex(dataset.numAttributes()-1);
			
			lms = new LeastMedSq();
			lms.buildClassifier(dataset);
			equation = lms.toString();
			String lmsm=lms.toString();
			lmsm=lmsm.replaceAll("Linear Regression Model", "");
			lmsm=lmsm.replaceAll("\n", "");
			//lmsm=lmsm.replaceAll("\t", "");
			//lmsm=lmsm.replaceAll(" ", "");
			Printer.main("Least Median Square Regression Model for Cluster"+i+".xls:");
			System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
			Printer.main(lmsm);
			System.out.println("");
			System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		}
		catch (Exception e) {
			//System.err.println("Least Median Square Regression can't be applied, check table under " +filePath);
			// TODO: handle exception
		}
		return equation;
	}
}
