package jxl;
import java.io.*;
import java.sql.*;



public class SqlToArff
{

	public static BufferedReader nbr=new BufferedReader(new InputStreamReader(System.in));
	public static String filename=ExcelReader.filename;
	public static String dirpath="";
	public static int K;//ExcelReader.K;
	public static Connection csql=null;
	static File f = null;
	static int totalOverAll =0;
	//---variables for Linear regression
	static double weightedMAElr=0.0;
	static double weightedRMSElr=0.0;
	//--variables for Least Median Square
	static double weightedMAElms=0.0;
	static double weightedRMSElms=0.0;
	public static double[][] error= new double[2][2];

	public static String  arffDir() throws IOException
	{
/*		System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------");
		System.out.println("								Give directory path to save Cluster arff files");
		System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
		String dirpath1=nbr.readLine();
		dirpath=dirpath1.replace('\\','/');
*/		
		dirpath=ExcelReader.dirpath+ExcelReader.filename+"#"+K+"/";
		return dirpath;
	}
	private static double[] getCoefficientLMS(String[] columnHeader, String equation) {
		double[] coefficient = new double[columnHeader.length];
		equation = equation .replaceAll(".*=", "");
		equation=equation.replaceAll("Linear Regression Model", "");
		equation=equation.replaceAll("\n", "");
		equation=equation.replaceAll("\t", "");
		equation=equation.replaceAll(" ", "");
		//System.out.println("----"+equation+"-----");
		String equationPart[]= equation .split("\\+");
		for(int i =0; i<columnHeader.length;++i){
			G: for(int j =0;j<equationPart.length;++j){
				String[] twoParts= equationPart[j].split("\\*");
				if(twoParts.length==2){
					if(twoParts[1].equals(columnHeader[i])){
						coefficient[i]= Double.parseDouble(twoParts[0]);
						break G;
					}
				}else if(twoParts.length==1){
					coefficient[i]=Double.parseDouble(twoParts[0]);
					break G;
				}
			}
		}
		return coefficient;
	}
	public static void updateArffFiles() throws Exception
	{
		for(int i=1;i<=K;i++)
		{
			csql=ConnToSql.main();
			Statement stmntsql=csql.createStatement();
			stmntsql.executeUpdate("USE "+filename+";");

			String sql1="SELECT * FROM "+"For"+K+"Cluster"+i;
			ResultSet rssql=stmntsql.executeQuery(sql1);
			java.sql.ResultSetMetaData rsmdsql=rssql.getMetaData();
			int columnCount = rsmdsql.getColumnCount();

			f = new File(dirpath+"Cluster"+i+".arff");
			f.createNewFile();
			//System.out.println(" .Arrf File created\n");

			String header="@relation luster"+i+'\n';
			for(int j=1;j<=columnCount;++j){
				header += "@ATTRIBUTE "+ rsmdsql.getColumnName(j)+" numeric"+'\n';
			}
			FileOutputStream fout = new FileOutputStream(f,true);
			fout.write((header).getBytes());

			//Data row wise entry-------------------------------
			String data="@data ";
			fout.write(data.getBytes());
			fout.write('\n');
			data="";
			while(rssql.next())
			{
				for(int k=1;k<=columnCount;k++)
				{	
					if(k<columnCount)
						data += rssql.getString(k)+",";
					else 
						data += rssql.getString(k);
				}	

				fout.write(data.getBytes());
				fout.write('\n');
				data="";
			}
			rssql.beforeFirst();
			//Applying LINEAR_REGRESSION..........................................
			int total=0;
			double MAElr=0.0;
			double RMSElr=0.0;
			try
			{
			while(rssql.next()){
				total++;
			}
			rssql.beforeFirst();
			totalOverAll+=total;
			double[] coefficient = RegressionLinear.main(dirpath+"cluster"+i+".arff",i);
			
			//ERROR calculation in Linear regression..................................................
			
			double y=0.0;
			while(rssql.next())
			{
				int n=0;
				for( n=0;n<columnCount-1;n++)
				{	
					y += Double.parseDouble(rssql.getString(n+1))*coefficient[n];
				}	
				y += coefficient[n+1];
				//To set the precision of double we have done the following
				if(Math.abs(y-Double.parseDouble(rssql.getString(n+1)))>0.00001){
					MAElr += Math.abs(y-Double.parseDouble(rssql.getString(n+1)));
				}
				RMSElr += Math.pow((y-Double.parseDouble(rssql.getString(n+1))), 2);
				y=0.0;
			}
			rssql.beforeFirst();
			MAElr=MAElr/(double)total;
			RMSElr=RMSElr/(double)total;
			RMSElr=Math.sqrt(RMSElr);
			
			weightedMAElr=total*MAElr;
			weightedRMSElr=total*RMSElr;
			}catch(Exception e){
				Printer.error("Linear Regression and Least Median Square Regression Can not be applied \" NOT ENOGH ROWS in FILE Cluster"+i+".xls \"");
				System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------");
				MAElr=0;
				RMSElr=0;
				weightedMAElr=total*MAElr;
				weightedRMSElr=total*RMSElr;
		
			}
			//--------------------------------------------------------------
			//------LeastMedianSquare Regression----------------------------
			String equation = RegressionLeastMedianSquare.main(dirpath+"cluster"+i+".arff",i);
			String[] columnHeader = new String[columnCount];
			for(int g =1;g<=columnCount;++g){
				columnHeader[g-1]=rsmdsql.getColumnName(g);
			}
			double[] coefficientLMS = null;
			try{
				coefficientLMS = getCoefficientLMS(columnHeader,equation);
			//ERROR calculation in Linear regression..................................................
			double MAElms=0.0;
			double RMSElms=0.0;
			double ylms=0.0;
		
			while(rssql.next())
			{
				int n=0;
				for( n=0;n<columnCount-1;n++)
				{	
					ylms += Double.parseDouble(rssql.getString(n+1))*coefficientLMS[n];
				}	
				ylms += coefficientLMS[columnCount-1];
				MAElms += Math.abs(ylms-Double.parseDouble(rssql.getString(n+1)));
				RMSElms += Math.pow((ylms-Double.parseDouble(rssql.getString(n+1))), 2);
				ylms=0.0;
			}
			rssql.beforeFirst();
			MAElms=MAElms/(double)total;
			RMSElms=RMSElms/(double)total;
			RMSElms=Math.sqrt(RMSElms);
			weightedMAElms=total*MAElms;
			weightedRMSElms=total*RMSElms;
			}
			catch(Exception e){
				if(total!=0){
					Printer.error("Least Median Square can not be applied because \" Rows are less than column in \"Cluster"+i+".xls File.");
					
					System.out.println("\n================================================================================================================================================================");
					Printer.main("Check Cluster Files");
					Printer.main(".......Program Terminating.......");
					System.out.println("================================================================================================================================================================");
					System.exit(1);
				}
				
			}
		}
		weightedMAElr=weightedMAElr/totalOverAll;
		weightedRMSElr=weightedRMSElr/totalOverAll;

		weightedMAElms=weightedMAElms/totalOverAll;
		weightedRMSElms=weightedRMSElms/totalOverAll;
		System.out.println("\n================================================================================================================================================================");
		Printer.main("Linear Regression");
		System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------");
		Printer.main("weightedMAE= "+weightedMAElr+"    "+", weightedRMSE= "+weightedRMSElr);
		System.out.println("\n================================================================================================================================================================");
		Printer.main("Least Median Square Regression");
		System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------");
		Printer.main("weightedMAE= "+weightedMAElms+"    "+", weightedRMSE= "+weightedRMSElms);
		System.out.println("\n================================================================================================================================================================");
		error[0][0]=weightedMAElr;
		error[0][1]=weightedRMSElr;
		error[1][0]=weightedMAElms;
		error[1][1]=weightedRMSElms;
	}
	public static void main(int x) throws Exception
	{
		K=x;
		arffDir();
		updateArffFiles();
	}


}
