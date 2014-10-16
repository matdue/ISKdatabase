package de.matdue.ISKdatabase;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

	public static void main(String[] args) throws Exception {
		Class.forName("org.sqlite.JDBC");
		
		Connection connection = null;
	    try
	    {
	      // create a database connection
	      connection = DriverManager.getConnection("jdbc:sqlite:eve.db");
	      connection.setAutoCommit(false);
	      Statement statement = connection.createStatement();
	      statement.setQueryTimeout(30);  // set timeout to 30 sec.
	      
	      statement.executeUpdate("PRAGMA auto_vacuum = FULL");
	      statement.executeUpdate("PRAGMA page_size = 4096");
	      statement.executeUpdate("PRAGMA user_version = 3");

	      statement.executeUpdate("drop table if exists android_metadata");
	      statement.executeUpdate("CREATE TABLE android_metadata (locale TEXT)");
	      PreparedStatement prep = connection.prepareStatement("insert into android_metadata values(?)");
	      prep.setString(1, "en-GB");
   		  prep.execute();
   		  
   		  statement.executeUpdate("drop table if exists invTypes");
	      statement.executeUpdate("CREATE TABLE invTypes (typeID INTEGER, typeName TEXT)");
	      prep = connection.prepareStatement("insert into invTypes values(?,?)");
   		  
	      InputStream dataStream = Main.class.getResourceAsStream("/data/invTypes-hyperion.csv");
   		  BufferedReader reader = new BufferedReader(new InputStreamReader(dataStream, Charset.forName("UTF-8")));
   		  String line;
   		  while ((line = reader.readLine()) != null) {
   			int separator = line.indexOf(';');
            int typeID = Integer.parseInt(line.substring(0, separator));
            String typeName = line.substring(separator + 1);
            
            prep.setInt(1,  typeID);
            prep.setString(2, typeName);
            prep.execute();
   		  }
   		  reader.close();

   		  statement.executeUpdate("drop table if exists staStations");
	      statement.executeUpdate("CREATE TABLE staStations (stationID INTEGER, stationName TEXT)");
	      prep = connection.prepareStatement("insert into staStations values(?,?)");
   		  
	      dataStream = Main.class.getResourceAsStream("/data/staStations-hyperion.csv");
   		  reader = new BufferedReader(new InputStreamReader(dataStream, Charset.forName("UTF-8")));
   		  while ((line = reader.readLine()) != null) {
   			int separator = line.indexOf(';');
            int typeID = Integer.parseInt(line.substring(0, separator));
            String typeName = line.substring(separator + 1);
            
            prep.setInt(1,  typeID);
            prep.setString(2, typeName);
            prep.execute();
   		  }
   		  reader.close();

	      connection.commit();
	    }
	    catch(SQLException e)
	    {
	      // if the error message is "out of memory", 
	      // it probably means no database file is found
	      System.err.println(e.getMessage());
	    }
	    finally
	    {
	      try
	      {
	        if(connection != null)
	          connection.close();
	      }
	      catch(SQLException e)
	      {
	        // connection close failed.
	        System.err.println(e);
	      }
	    }
	}

}
