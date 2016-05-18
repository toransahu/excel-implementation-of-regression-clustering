package jxl;
import java.sql.Connection;
import java.sql.SQLException;
//import java.sql.ResultSetMetaData;
//import java.sql.Statement;
//import java.sql.ResultSet;
import java.sql.DriverManager;

public class ConnToSql 
{
	//Statement is not handled through this file
	public static Connection main()throws Exception
	{
		final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
		final String DB_URL1 = "jdbc:mysql://localhost:3306/";
		final String USER = "root";
		final String PASS = "mysqlsamplepassword";
		
			Connection csql = null;
			try
			{
				Class.forName(JDBC_DRIVER);
				csql= DriverManager.getConnection(DB_URL1, USER, PASS);
			}
			catch( SQLException e )
			{
				System.err.println("Exception in connToSQL.java "+e);
			}
		return csql;
	}
}
