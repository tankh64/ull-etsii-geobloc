/* 
 * This sample demonstrate basic Lob support.
 * (insert, fetch rows of BLOB and CLOB)
 *
 * It 1. drops, create, and populates table basic_lob_table
 *       with blob, clob data types in the database.
 *    2. Please use jdk1.2 or later version and classes12.zip 
 */

import java.sql.*;
import java.io.*;
import java.util.*;

// Importing the Oracle Jdbc driver package makes
// the code more readable
import oracle.jdbc.*;
import oracle.jdbc.pool.OracleDataSource;

//needed for new CLOB and BLOB classes
import oracle.sql.*;

public class LobExample
{
  public static void main (String args [])
       throws Exception
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

    // Create an OracleDataSouce instance and set properties
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
                  "'010101010101010101010101010101', " +
                  "'onetwothreefour')");
    stmt.execute ("insert into basic_lob_table values ('two', " +
                  "'0202020202020202020202020202', " +
                  "'twothreefourfivesix')");
    
    System.out.println ("Dumping lobs");

    // Select the lobs. Since the Lob contents will be modified,
    // we need to lock the table rows. This can be done by doing
    // "select ... for update", but we don't need to select for
    // update here because we have "autocommit" turned off and
    // the previous "create table" statement already have the
    // whole table locked.
    ResultSet rset = stmt.executeQuery
                          ("select * from basic_lob_table");
    while (rset.next ())
    {
      // Get the lobs
      BLOB blob = ((OracleResultSet)rset).getBLOB (2);
      CLOB clob = ((OracleResultSet)rset).getCLOB (3);

      // Print the lob contents
      dumpBlob (blob);
      dumpClob (clob);

      // Change the lob contents
      fillClob (clob, 2000);
      fillBlob (blob, 4000);
    }

    System.out.println ("Dumping lobs again");

    rset = stmt.executeQuery
                ("select * from basic_lob_table");
    while (rset.next ())
    {
      // Get the lobs
      BLOB blob = ((OracleResultSet)rset).getBLOB (2);
      CLOB clob = ((OracleResultSet)rset).getCLOB (3);

      // Print the lobs contents
      dumpBlob (blob);
      dumpClob (clob);
    }
    // Close all resources
    rset.close();
    stmt.close();
    conn.close(); 
  }

  // Utility function to dump Clob contents
  static void dumpClob (CLOB clob)
    throws Exception
  {
    // get character stream to retrieve clob data
    Reader instream = clob.getCharacterStream();

    // create temporary buffer for read
    char[] buffer = new char[10];

    // length of characters read
    int length = 0;

    // fetch data  
    while ((length = instream.read(buffer)) != -1)
    {
      System.out.print("Read " + length + " chars: ");

      for (int i=0; i<length; i++)
        System.out.print(buffer[i]);
      System.out.println();
    }

    // Close input stream
    instream.close();
  }

  // Utility function to dump Blob contents
  static void dumpBlob (BLOB blob)
    throws Exception
  {
    // Get binary output stream to retrieve blob data
    InputStream instream = blob.getBinaryStream();

    // Create temporary buffer for read
    byte[] buffer = new byte[10];

    // length of bytes read
    int length = 0;

    // Fetch data  
    while ((length = instream.read(buffer)) != -1)
    {
      System.out.print("Read " + length + " bytes: ");

      for (int i=0; i<length; i++)
        System.out.print(buffer[i]+" ");
      System.out.println();
    }

    // Close input stream
    instream.close();
  }

  // Utility function to put data in a Clob
  static void fillClob (CLOB clob, long length)
    throws Exception
  {
    Writer outstream = clob.getCharacterOutputStream();

    int i = 0;
    int chunk = 10;

    while (i < length)
    {
      outstream.write(i + "hello world", 0, chunk);

      i += chunk;
      if (length - i < chunk)
	chunk = (int) length - i;
    }
    outstream.close();
  }

  // Utility function to put data in a Blob
  static void fillBlob (BLOB blob, long length)
    throws Exception
  {
    OutputStream outstream =
                 blob.getBinaryOutputStream();

    int i = 0;
    int chunk = 10;

    byte [] data = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

    while (i < length)
    {
      data [0] = (byte)i;
      outstream.write(data, 0, chunk);

      i += chunk;
      if (length - i < chunk)
	chunk = (int) length - i;
    }
    outstream.close();
  }
}
