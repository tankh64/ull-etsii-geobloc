/* 
 * This sample demonstrate basic Lob support
 *
 * It shows how to use PL/SQL package DBMS_LOB 
 * to do operations on BLOB and CLOB data type.
 * 
 * note: 1. It needs jdk1.2 or later version and classes12.zip
 *       2. It drops, creates, and populates table
 *          basic_lob_table in the database
 */

import java.sql.*;
import java.io.*;
import java.util.*;

// Importing the Oracle Jdbc driver package makes the code more readable
import oracle.jdbc.*;
import oracle.jdbc.pool.OracleDataSource;

//needed for new CLOB and BLOB classes
import oracle.sql.*;

public class PLSQL_LobExample
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

    // Create a OracleDataSource instance and set properties
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
      // An exception could be raised here if the table did not exist already
      // but we gleefully ignore it
    }

    // Create a table containing a BLOB and a CLOB
    stmt.execute ("create table basic_lob_table (x varchar2 (30), b blob, c clob)");
    
    // Populate the table
    stmt.execute ("insert into basic_lob_table values ('one', " +
                  "'010101010101010101010101010101', 'onetwothreefour')");
    stmt.execute ("insert into basic_lob_table values ('two', " +
                  "'0202020202020202020202020202', 'twothreefourfivesix')");
    
    System.out.println ("Dumping lobs");

    // Select the lobs
    ResultSet rset = stmt.executeQuery ("select * from basic_lob_table");
    while (rset.next ())
    {
      // Get the lobs
      BLOB blob = ((OracleResultSet)rset).getBLOB (2);
      CLOB clob = ((OracleResultSet)rset).getCLOB (3);

      // Print the lob contents
      dumpBlob (conn, blob);
      dumpClob (conn, clob);

      // Change the lob contents
      fillClob (conn, clob, 2000);
      fillBlob (conn, blob, 4000);
    }

    System.out.println ("Dumping lobs again");

    // Select the lobs again
    rset = stmt.executeQuery ("select * from basic_lob_table");
    while (rset.next ())
    {
      // Get the lobs
      BLOB blob = ((OracleResultSet)rset).getBLOB (2);
      CLOB clob = ((OracleResultSet)rset).getCLOB (3);

      // Print the lobs contents
      dumpBlob (conn, blob);
      dumpClob (conn, clob);
    }

    // Close all resources
    rset.close();
    stmt.close();
    conn.close();
  }

  // Utility function to dump Clob contents
  static void dumpClob (Connection conn, CLOB clob)
    throws Exception
  {
    OracleCallableStatement cstmt1 =
      (OracleCallableStatement)
        conn.prepareCall ("begin ? := dbms_lob.getLength (?); end;");
    OracleCallableStatement cstmt2 =
      (OracleCallableStatement)
        conn.prepareCall ("begin dbms_lob.read (?, ?, ?, ?); end;");

    cstmt1.registerOutParameter (1, Types.NUMERIC);
    cstmt1.setCLOB (2, clob);
    cstmt1.execute ();

    long length = cstmt1.getLong (1);
    long i = 0;
    int chunk = 10;

    while (i < length)
    {
      cstmt2.setCLOB (1, clob);
      cstmt2.setLong (2, chunk);
      cstmt2.registerOutParameter (2, Types.NUMERIC);
      cstmt2.setLong (3, i + 1);
      cstmt2.registerOutParameter (4, Types.VARCHAR);
      cstmt2.execute ();

      long read_this_time = cstmt2.getLong (2);
      String string_this_time = cstmt2.getString (4);

      System.out.print ("Read " + read_this_time + " chars: ");
      System.out.println (string_this_time);
      i += read_this_time;
    }

    cstmt1.close ();
    cstmt2.close ();
  }

  // Utility function to dump Blob contents
  static void dumpBlob (Connection conn, BLOB blob)
    throws Exception
  {
    OracleCallableStatement cstmt1 =
      (OracleCallableStatement)
        conn.prepareCall ("begin ? := dbms_lob.getLength (?); end;");
    OracleCallableStatement cstmt2 =
      (OracleCallableStatement)
        conn.prepareCall ("begin dbms_lob.read (?, ?, ?, ?); end;");

    cstmt1.registerOutParameter (1, Types.NUMERIC);
    cstmt1.setBLOB (2, blob);
    cstmt1.execute ();

    long length = cstmt1.getLong (1);
    long i = 0;
    int chunk = 10;

    while (i < length)
    {
      cstmt2.setBLOB (1, blob);
      cstmt2.setLong (2, chunk);
      cstmt2.registerOutParameter (2, Types.NUMERIC);
      cstmt2.setLong (3, i + 1);
      cstmt2.registerOutParameter (4, Types.VARBINARY);
      cstmt2.execute ();

      long read_this_time = cstmt2.getLong (2);
      byte [] bytes_this_time = cstmt2.getBytes (4);

      System.out.print ("Read " + read_this_time + " bytes: ");

      int j;
      for (j = 0; j < read_this_time; j++)
	      System.out.print (bytes_this_time [j] + " ");
      System.out.println ();

      i += read_this_time;
    }

    cstmt1.close ();
    cstmt2.close ();
  }

  // Utility function to put data in a Clob
  static void fillClob (Connection conn, CLOB clob, long length)
    throws Exception
  {
    OracleCallableStatement cstmt1 =
      (OracleCallableStatement)
        conn.prepareCall ("begin dbms_lob.write (?, ?, ?, ?); end;");

    long i = 0;
    long chunk = 10;

    while (i < length)
    {
      cstmt1.setCLOB (1, clob);
      cstmt1.setLong (2, chunk);
      cstmt1.setLong (3, i + 1);
      cstmt1.setString (4, i + "hello world");
      cstmt1.execute ();

      i += chunk;
      if (length - i < chunk)
	      chunk = length - i;
    }

    cstmt1.close ();
  }

  // Utility function to put data in a Blob
  static void fillBlob (Connection conn, BLOB blob, long length)
    throws Exception
  {
    OracleCallableStatement cstmt1 =
      (OracleCallableStatement)
        conn.prepareCall ("begin dbms_lob.write (?, ?, ?, ?); end;");

    long i = 0;
    long chunk = 10;

    byte [] data = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

    while (i < length)
    {
      cstmt1.setBLOB (1, blob);
      cstmt1.setLong (2, chunk);
      cstmt1.setLong (3, i + 1);
      data [0] = (byte)i;
      cstmt1.setBytes (4, data);
      cstmt1.execute ();

      i += chunk;
      if (length - i < chunk)
       	chunk = length - i;
    }

    cstmt1.close ();
  }
}
