/*
 * This sample shows how to open, close BLOB and CLOB
 * and test whether they are open or not.
 *
 * note: 1. It needs jdk1.2 or later version and classes12.zip
 *       2. It drops, creates, and populates table 
 *          basic_lob_table including types of BLOB and CLOB
 */

// You need to import the java.sql package to use JDBC
import java.sql.*;

// You need to import the oracle.sql package to use oracle.sql.BLOB
import oracle.sql.*;

import oracle.jdbc.pool.OracleDataSource;

class OpenCloseLob
{
  public static void main (String args [])
       throws SQLException
  {
    String url = "jdbc:oracle:oci8:@";
    try {
      String url1 = System.getProperty("JDBC_URL");
      if (url1 != null)
        url = url1;
    } catch (Exception e) {
      // If there is any security exception, ignore it
      // and use the default
    }

    // Create an OracleDataSource instance and set properties
    OracleDataSource ods = new OracleDataSource();
    ods.setUser("hr");
    ods.setPassword("hr");
    ods.setURL(url);

    // Connect to the database
    Connection conn = ods.getConnection();
    // It's faster when auto commit is off
    conn.setAutoCommit (false);

    // Create a Statement
    Statement stmt = conn.createStatement ();

    try
    {
      stmt.execute ("drop table basic_lob_table");
    }
    catch (SQLException e)
    {
      // An exception could be raised here if the
      // table did not exist already.
    }

    // Create a table containing a BLOB and a CLOB
    stmt.execute ("create table basic_lob_table (x varchar2 (30), " +
                  "b blob, c clob)");

    // Populate the table
    stmt.execute ("insert into basic_lob_table values ('one', " +
                  "'010101010101010101010101010101', 'onetwothreefour')");

    // Select the lobs
    ResultSet rset = stmt.executeQuery ("select * from basic_lob_table");
    while (rset.next ())
    {
      // Get the lobs
      BLOB blob = (BLOB) rset.getObject (2);
      CLOB clob = (CLOB) rset.getObject (3);

      // Open the lobs
      System.out.println ("Open the lobs");
      blob.open (BLOB.MODE_READWRITE);
      clob.open (CLOB.MODE_READWRITE);

      // Check if the lobs are opened
      System.out.println ("blob.isOpen()="+blob.isOpen());
      System.out.println ("clob.isOpen()="+clob.isOpen());

      // Close the lobs
      System.out.println ("Close the lobs");
      blob.close ();
      clob.close ();

      // Check if the lobs are opened
      System.out.println ("blob.isOpen()="+blob.isOpen());
      System.out.println ("clob.isOpen()="+clob.isOpen());    
    }

    // Close the ResultSet
    rset.close ();

    // Close the Statement 
    stmt.close ();
  
    // Close the connection
    conn.close ();
  }
}
