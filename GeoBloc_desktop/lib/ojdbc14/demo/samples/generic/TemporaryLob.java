/*
 * This sample shows how to create and free
 * temporary BLOB and CLOB.
 *
 * It needs jdk1.2 or later version and classes12.zip
 */

// You need to import the java.sql package to use JDBC
import java.sql.*;

// You need to import the oracle.sql package to use
// oracle.sql.BLOB
import oracle.sql.*;

import oracle.jdbc.pool.OracleDataSource;

class TemporaryLob
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

    // Create a OracleDataSource instance and set properties
    OracleDataSource ods = new OracleDataSource();
    ods.setUser("hr");
    ods.setPassword("hr");
    ods.setURL(url);

    // Connect to the database
    Connection conn = ods.getConnection();

    // Create a temporary BLOB
    BLOB tempBlob = BLOB.createTemporary (conn, 
                                          false, 
                                          BLOB.DURATION_SESSION);

    // See if the BLOB is temporary 
    System.out.println ("tempBlob.isTemporary()="+
                        tempBlob.isTemporary());

    // See if the BLOB is temporary use the BLOB utility API
    System.out.println ("BLOB.isTemporary(tempBlob)="+
                        BLOB.isTemporary(tempBlob));

    // Free the temporary BLOB. 
    // You can also use "BLOB.freeTemporary(tempBlob);".
    tempBlob.freeTemporary ();

    // Create a temporary CLOB
    CLOB tempClob = CLOB.createTemporary (conn, 
                                          false, 
                                          CLOB.DURATION_SESSION);

    // See if the CLOB is temporary 
    System.out.println ("tempClob.isTemporary()="+
                        tempClob.isTemporary());

    // See if the CLOB is temporary use the CLOB utility API
    System.out.println ("CLOB.isTemporary(tempClob)="+
                        CLOB.isTemporary(tempClob));

    // Free the temporary CLOB. 
    // You can also use "CLOB.freeTemporary(tempClob);".
    tempClob.freeTemporary ();

    // Close the connection
    conn.close();
  }
}
