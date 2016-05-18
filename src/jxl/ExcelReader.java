package jxl;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.ResultSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class ExcelReader {
	public static Statement stmntxl = ConnToXls.stmntxl;
	public static BufferedReader nbr=new BufferedReader(new InputStreamReader(System.in));
	public static String filepath="";	
	public static String filename="";
	public static String dirpath="";
	public static int K=0;
	public static int total=0;
	static String  getXls() throws IOException{
		// input format is "C:\\Users\\root\\Desktop\\Example.xls";
		
		System.out.println("================================================================================================================================================================");
		Printer.main("ENTER THE PATH OF EXCEL FILE:");
		Printer.main("(Format:C:\\Users\\Abc\\Temp.xls)");
		Printer.main("Warning: Excel file must be with .XLS, NOT .XLSX");
		System.out.println("================================================================================================================================================================");
		String filepath1=nbr.readLine();
		filepath=filepath1.replace('\\','/');
		return filepath;
		// Format of returned path "C:/Users/root/Desktop/Example.xls"
	}
	public static String getFileName(){
		String fn="";
		char filePathArray[]= filepath.toCharArray();
		int filePathLength=filepath.length();
		for(int i=filePathLength-1;i>=0;i--){
			if(filePathArray[i] != '/')
				fn = fn + filePathArray[i];
			else break;
		}
		String filename1 = new StringBuffer(fn).reverse().toString();
		filepath.replaceAll(filename1, "");
		filename=filename1.replace('.','_');// '.' is replaced with '_' because SQL database can not be named with '.' in it.
		return filename;
	}
	public static String  clusterDir() throws IOException{
		
				dirpath=ExcelReader.filepath.replace(ExcelReader.filename.replace('_', '.'),"");
				return dirpath;
			}
	
	
	public static int getK(){
		System.out.println("\n================================================================================================================================================================");
		Printer.main("ENTER THE COUNT OF CLUSTER CENTERS(K):");
		System.out.println("================================================================================================================================================================");
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		try {
			K =Integer.parseInt(br.readLine());
		}
		catch (NumberFormatException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		return K ;
	}
	public static void getArff() throws Exception{
		Connection csql = ConnToSql.main();
		Statement stmntsql=csql.createStatement();
		stmntsql.executeUpdate("USE "+filename+";");

		String sql1="SELECT * FROM sheet1;";
		ResultSet rssql=stmntsql.executeQuery(sql1);
		java.sql.ResultSetMetaData rsmdsql=rssql.getMetaData();
		int columnCount = rsmdsql.getColumnCount();

		String dirpath = ExcelReader.filepath;
		dirpath=dirpath.replace(ExcelReader.filename, "");
		
		File f = new File(ExcelReader.dirpath+ExcelReader.filename.replace("_xls", "")+".arff");
		f.createNewFile();
		//System.out.println(" .Arrf File created\n");

		String header="@relation "+ExcelReader.filename.replace("_xls", "")+".arff"+'\n';
		for(int j=2;j<=columnCount;++j){
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
			for(int k=2;k<=columnCount;k++)
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
}
	public static void readExcel() throws Exception{
		Connection csql=ConnToSql.main();
		Statement stmntsql=csql.createStatement();
		try
		{	
			String query = "select * from [Sheet1$];";
			ResultSet rsxl = ConnToXls.stmntxl.executeQuery( query );
			ResultSetMetaData metadataxl = rsxl.getMetaData();
			int columns = metadataxl.getColumnCount();
			String sql1="";
			sql1=sql1+"serialNumber Integer PRIMARY KEY, ";
			for(int i = 1 ; i<= columns ; i++){
				if(i<columns)
					sql1=sql1+metadataxl.getColumnName(i)+" Double, ";
			   else  
				   	sql1=sql1+metadataxl.getColumnName(i)+" Double";	
			}
			String filename=getFileName();
			String sql2 = "CREATE DATABASE IF NOT EXISTS "+filename;// database created...
			stmntsql.executeUpdate(sql2);
			stmntsql.executeUpdate("USE "+filename);
			String sql3="CREATE TABLE Sheet1 ("+sql1+")";
		   	stmntsql.executeUpdate("DROP TABLE IF EXISTS Sheet1");
	   		stmntsql.executeUpdate(sql3);//Tables created successfully...
			String sql5="INSERT INTO Sheet1 VALUES (";
			int serialNumber=1;
			while(rsxl.next()){
				sql5 = sql5 + serialNumber++ +", ";
				for(int i=1;i<=columns;i++){	
					if(i!=columns)
						sql5= sql5+rsxl.getString(metadataxl.getColumnName(i))+",";
					else 
						sql5=sql5+rsxl.getString(metadataxl.getColumnName(i))+");";
				}
				stmntsql.executeUpdate(sql5);
				sql5="INSERT INTO Sheet1 VALUES (";
				total++;
			}//Records inserted successfully...
		}
		catch( Exception e ){
			System.err.println("calException in ExcelReader.java : "+e);
		}
		stmntsql.close();
		csql.close();
	}
	static void closeConnToXls()throws Exception{
		try{
			ConnToXls.stmntxl.close();
			ConnToXls.cxl.close();
		}
		catch( Exception e ){
			System.err.println("closeConnToXls Exception in ExcelReader.java: "+e );
		}
	}
	public static void main() throws Exception{
		getXls();
		getFileName();
		clusterDir();
		getK();
		ConnToXls.main();//getting connection from .XLS file
		readExcel();//.XLS file is read by 'readEcel' method and SQL statement is passed
		closeConnToXls();
		getArff();
	}
}