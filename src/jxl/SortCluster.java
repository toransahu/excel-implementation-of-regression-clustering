package jxl;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class SortCluster {
	public static Connection csql=null;
	public static int K=ExcelReader.K;

	void clusterTables() throws Exception{
		csql=ConnToSql.main();
		Statement stmntsql=csql.createStatement();
		ResultSet rs=stmntsql.getResultSet();
		String sql = "SELECT * from sheet2";
		stmntsql.executeUpdate("USE "+ExcelReader.filename+";");
		rs=stmntsql.executeQuery(sql);

		Statement stmntsql1=csql.createStatement();
		ResultSet rs1=stmntsql1.getResultSet();
		String sql1 = "SELECT * from sheet2";
		stmntsql1.executeUpdate("USE "+ExcelReader.filename+";");
		rs1=stmntsql1.executeQuery(sql1);

		Statement stmntsql2=csql.createStatement();
		stmntsql2.executeUpdate("USE "+ExcelReader.filename+";");
		ResultSetMetaData rsmd=rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		String sql2="";
		for(int i = 1 ; i<columnCount ; i++){	
			if(i!=(columnCount-1))
				sql2=sql2+rsmd.getColumnName(i)+ " Double, ";
			else
				sql2=sql2+rsmd.getColumnName(i)+ " Double ";
		}
		for(int i=0;i<K;++i){
			stmntsql.executeUpdate("DROP TABLE IF EXISTS "+"For"+K+"Cluster"+(i+1));
			String sql3="CREATE TABLE "+"For"+K+"Cluster"+(i+1)+ " (" + sql2 + ");" ;
			stmntsql.executeUpdate(sql3);
		}
		String sql4="";
		while(rs1.next()){ // only rs1 use krna hai(nya result set hi use krna hai)
			for(int i=1;i<=(columnCount);i++){	
				if(i<(columnCount-1))
					sql4= sql4+rs1.getString(i)+", "; // only rs1 hi use krna hai(nya result set hi use krna hai)
				else if(i==(columnCount-1))
					sql4= sql4+rs1.getString(i)+" );"; // only rs1 hi use krna hai(nya result set hi use krna hai)
				else
					sql4="INSERT INTO "+"For"+K+"Cluster"+rs1.getString(columnCount)+" VALUES (" + sql4; // only rs1 hi use krna hai(nya result set hi use krna hai)
			}//System.out.println(sql4);
			try{	
				stmntsql2.executeUpdate(sql4);
			}
			catch(Exception e){ // only stmntsql hi use krna hai(dusra statement hi use krna hai, rs1 k liye jo hai usko chhodh k koi bhi chalega)
				System.out.println("cdccdc "+e);
			}
			sql4="";
		}
		try{
			stmntsql.close();
			stmntsql2.close();
			csql.close();
		}
		catch( Exception e ){
			System.err.println( e+ "closeConnToSQL Exception" );
		}
	}

	public static void main() throws Exception{
		SortCluster sc=new SortCluster();
		sc.clusterTables();
	}
}
