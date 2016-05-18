package jxl;
import java.io.*;
import java.sql.*;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ExcelWriterPOI{
	public static BufferedReader nbr=new BufferedReader(new InputStreamReader(System.in));
	public static String filepath="";
	//public static int K=ExcelReader.K;
	public static int K;
	public static Connection csql=null;
	public static String dirpath=ExcelReader.dirpath;
	
	static void createClusterFiles() throws IOException{
		File dir = new File(dirpath+""+ExcelReader.filename+"#"+K);
		dir.mkdir();
		for(int i=1;i<=K;i++){
			File f = null;
			f = new File(dirpath+ExcelReader.filename+"#"+K+"/"+"Cluster"+i+".xls");
			f.createNewFile();//System.out.println("File created\n");
		}
	}
	
	public static void updateClusterFiles() throws Exception{
		int count=0;
		csql=ConnToSql.main();
		System.out.println("\n================================================================================================================================================================");
		Printer.main("For K="+K);
		System.out.println("================================================================================================================================================================");
		//System.out.println("\n================================================================================================================================================================");
		Printer.main("File(s) Generated:");
		System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		for(int clusternum=1;clusternum<=K;clusternum++){
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Cluster"+clusternum);
			Class.forName( "sun.jdbc.odbc.JdbcOdbcDriver" );
			Statement stmntsql=csql.createStatement();
			stmntsql.executeUpdate("USE "+ExcelReader.filename+";");
			String sql1="SELECT * FROM "+"For"+K+"Cluster"+clusternum;
			ResultSet rssql=stmntsql.executeQuery(sql1);
			java.sql.ResultSetMetaData rsmdsql=rssql.getMetaData();
			int columnCount = rsmdsql.getColumnCount();
			int rownum=0;
			int cellnum = 0;
			Cell cell;
			Row row = sheet.createRow(rownum++);
			for(int j = 1 ; j<=columnCount ; j++){	
				cell = row.createCell(cellnum++);
				cell.setCellValue((String)rsmdsql.getColumnName(j));
			}
			count=0;
			while(rssql.next()){
				row = sheet.createRow(rownum++);
				cellnum = 0;
				for(int k=1;k<=columnCount;k++){	
					cell = row.createCell(cellnum++);
					cell.setCellValue(Double.parseDouble(rssql.getString(k)));
				}	
				count++;
			}
			rssql.beforeFirst();
			try {
			    FileOutputStream out = new FileOutputStream(new File(dirpath+ExcelReader.filename+"#"+K+"/"+"Cluster"+clusternum+".xls"));
			    workbook.write(out);
			    out.close();
			Printer.main("Cluster"+clusternum+".xls");
			    File f=new File(dirpath+ExcelReader.filename+"#"+K+"/"+"Count.txt");
				f.createNewFile();
				FileOutputStream fout=new FileOutputStream(f,true);
				fout.write(("Cluster"+clusternum+" Count #--->"+String.valueOf(count)+"\t").getBytes());
				fout.write(("Count %--->"+count*100.0/ExcelReader.total+"%\n\n").getBytes());
				//fout.write(10);
			} 
			catch (FileNotFoundException e) {
			    e.printStackTrace();
			} 
			catch (IOException e1){
			    e1.printStackTrace();
			}
		}
		System.out.println("\n--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		//System.out.println("\n================================================================================================================================================================");
		//System.out.println("									    SUCCESS...");
		//System.out.println("================================================================================================================================================================");
		//System.out.println("Cluster Files Created Successfully..");
	}
	
	public static void main(int x) throws Exception{
		K=x;
		createClusterFiles();
		updateClusterFiles();
	}
}