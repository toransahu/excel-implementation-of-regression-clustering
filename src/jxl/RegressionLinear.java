package jxl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import weka.classifiers.functions.LeastMedSq;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SimpleLinearRegression;
import weka.classifiers.functions.SimpleLogistic;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.experiment.InstanceQuery;

@SuppressWarnings("unused")
public class RegressionLinear  {
	public static double[] main(String filePath,int i) throws Exception{
		//System.out.println(""+filePath);
		DataSource source = new DataSource(filePath);
		Instances dataset = source.getDataSet();
		dataset.setClassIndex(dataset.numAttributes()-1);
		
		LinearRegression lr = new LinearRegression();
		lr.buildClassifier(dataset);
		
		
		
		
		
		double[] coefficient = lr.coefficients();
/*		for(int i=0;i<dataset.numAttributes()+1;++i){
			System.out.println(coefficient[i]);
		}
*/		
		String lrm=lr.toString();
		lrm=lrm.replaceAll("Linear Regression Model", "");
		lrm=lrm.replaceAll("\n", "");
		//lrm=lrm.replaceAll("\t", "");
		//lrm=lrm.replaceAll(" ", "");
		Printer.main("Linear Regression Model for Cluster"+i+".xls:");
		System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		Printer.main(lrm);
		System.out.println("");
		System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------");


		return coefficient;
	}
}