package jxl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class K_Means_Old {
	
	public BufferedReader nbr=new BufferedReader(new InputStreamReader(System.in));
	//public static int K=ExcelReader.K;
	public static int K;
	static double mean[][];
	static int count[];
	
	private static void k_Means() throws Exception {
		int total=0;
		Connection csql=ConnToSql.main();	
		Statement stmntsql=csql.createStatement();
		ResultSet rs=stmntsql.getResultSet();
		stmntsql.executeUpdate("USE "+ExcelReader.filename+";");
		String sql = "SELECT * from sheet1";
		rs=stmntsql.executeQuery(sql);
		
		ResultSetMetaData rsmd = rs.getMetaData();
		Connection csql_new=ConnToSql.main();	
		Statement stmntsql_new=csql_new.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		ResultSet rs_new=stmntsql_new.getResultSet();
		stmntsql_new.executeUpdate("USE "+ExcelReader.filename+";");
		String sql_new = "SELECT * from sheet1";
		
		int columnCount = rsmd.getColumnCount();
		//column count also contain serial number column so negate that one out
		columnCount--;
		//array to store the mean values
		mean =new double[K][columnCount];
		//now initialize means to be the first K rows of the Database
		rs.next();
		
		for(int i=0;i<K;i++){
			for(int j=1;j<columnCount;++j){
				mean[i][j-1] = rs.getDouble(j+1);
			}
			rs.next();
		}
	
		
		rs.close();
		//adding additional label column in sheet1
		//Note here that-New "statement" should be used otherwise "resultset" will get closed. So lets not use the variable at all . instead use "csql.createStatement()."
		csql.createStatement().executeUpdate("ALTER TABLE sheet1 ADD cluster_label INTEGER ;");
		csql.createStatement().executeUpdate("UPDATE sheet1 set cluster_label = -1;");
		rs_new=stmntsql_new.executeQuery(sql_new);
		//rs.beforeFirst();
		int condition_Flag = 1;
	//	int epoch=0;
	//	while(epoch<10 && condition_Flag>0){
		while(condition_Flag>0){
			condition_Flag = 0;
			rs_new.beforeFirst();
			//epoch++;
			while(rs_new.next()){
				int label=-1;
				double distanceOld = Double.POSITIVE_INFINITY ;
				for(int i =0;i<K;++i){
					double distance = 0.0;
					//Euclidean implementation without square root :P
					for(int j = 1;j<=columnCount;++j){
						distance = distance + Math.pow((mean[i][j-1]-rs_new.getDouble(j+1)), 2);
					}
					int tmp=Double.compare(distance, distanceOld);
				//	System.out.println(tmp);
					if(tmp < 0){
						label = i+1;
					}
					distanceOld = distance;
				}
				//Condition for K-Means iteration is checked by if any of the cluster label
				//is changed then iteration is required
				//need to use New resultSet . the old one does not know about extra added column.
				if(rs_new.getInt(columnCount+2)!=label){
					condition_Flag++;
				}
				rs_new.updateInt(columnCount+2,label);
				rs_new.updateRow();
			}
			for(int i =0;i<K;++i){
				for(int j =1;j<=columnCount;++j){
					mean[i][j-1]=0;
				}
			}
			//New Mean Calculations
			count = new int[K+1];
			for(int i =0;i<=K;i++)
				count[i]=0;
			rs_new.beforeFirst();
			while(rs_new.next()){
				for(int j=1;j<=columnCount;++j){
					mean[rs_new.getInt(columnCount+2)-1][j-1] = mean[rs_new.getInt(columnCount+2)-1][j-1] + rs_new.getDouble(j+1);
				}
				count[rs_new.getInt(columnCount+2)-1]++;
			}
			for(int i=0;i<K;i++){
				for(int j=1;j<=columnCount;++j){
					mean[i][j-1]=mean[i][j-1]/count[i];
				}
			}
		}
		//separating clusters....................
		String dbHeader="";
		for(int i = 2 ; i<= columnCount+1 ; i++){
			if(i<columnCount+1)
				dbHeader=dbHeader+rsmd.getColumnName(i)+" Double, ";
			else 
				dbHeader=dbHeader+rsmd.getColumnName(i)+" Double";
		}
		String sqlCluster= "Create table "+"For"+K;
		for(int i=0;i<K;++i){
			csql.createStatement().executeUpdate("DROP TABLE IF EXISTS "+"For"+K+ "Cluster"+(i+1));
			sqlCluster = sqlCluster + "Cluster"+(i+1)+" ( "+dbHeader+" );";
			csql.createStatement().execute(sqlCluster);
		//	System.out.println(""+sqlCluster);
			sqlCluster= "Create table "+"For"+K;
		}
		
		//cluster files are created .....now insert respective tuples to them
		ResultSet rs2 = csql.createStatement().getResultSet();
		rs2= csql.createStatement().executeQuery("SELECT * from sheet1 ;");
		String sql4="";
		while(rs2.next()){
			total++;
			for(int i=2;i<=(columnCount+1);i++){
				if(i<columnCount+1)
					sql4= sql4+rs2.getString(i)+", ";
				else if(i==columnCount+1)
					sql4= sql4+rs2.getString(i)+""; 
			}
			sql4="INSERT INTO "+"For"+K +"Cluster"+rs2.getString(columnCount+2)+" VALUES (" + sql4+" );";
			try{	
			//System.out.println(""+sql4);
				csql.createStatement().executeUpdate(sql4);
			}
			catch(Exception e){ 
				System.out.println("Error in inserting tuples .. "+e);
			}
			sql4="";
		}
		csql.createStatement().executeUpdate("ALTER TABLE sheet1 DROP COLUMN cluster_label;");
	}
	public static void main(int x ) throws Exception{
		K=x;
		k_Means();
	}
}