package jxl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ConnToXls {
	public static Connection cxl = null;
	public static Statement stmntxl = null;	
	
	public static Connection main() {
		try
		{
			Class.forName( "sun.jdbc.odbc.JdbcOdbcDriver" );
			//using DSN-less connection
			cxl = DriverManager.getConnection( "jdbc:odbc:Driver={Microsoft Excel Driver (*.xls)};DBQ="+ExcelReader.filepath);
			stmntxl = cxl.createStatement();
		}
		catch( Exception e )
		{
			System.err.println("connToXls method Exception in 'Excelreader.java file' : " +e);
		}
		return cxl;
	}
}
