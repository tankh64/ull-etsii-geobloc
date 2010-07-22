#
# Makefile for compiling and executing files under
# demo/samples/jdbcoci/native-xa/
# Please use rundemo.bat on NT, win64
#
# Usage: make <all> | <oci> | <ocitns> | <single demo>
#
# Before you run any demo programs, you should:
#       1. set up your database and demo schema HR/HR. Please refer to
#          Samples-Readme.txt, section Setting Up Schemas for details.
#       2. Modify this makefile to use appropriate host/port/SID/service_name.
#          search for MODIFY_HERE ... to process your modification.
#
# Features demonstrated:
#       Native XA.
#

 

JDBC_URL=jdbc:oracle:oci:@

DEMO_CLASSPATH=.:${ORACLE_HOME}/jlib/jndi.jar:${ORACLE_HOME}/jlib/jta.jar
CLASSPATH=${ORACLE_HOME}/jdbc/lib/ojdbc14.jar:${DEMO_CLASSPATH}

# MODIFY_HERE
# Please use a correct path on your machine
JAVAC=/usr/local/packages/jdk14/bin/javac
JAVA=/usr/local/packages/jdk14/bin/java




all: clobber compile run


oci:
	make all JDBC_URL=jdbc:oracle:oci:@

# MODIFY_HERE
# Please use appropriate <host> <port> and <service_name>
ocitns:
	make all JDBC_URL="jdbc:oracle:oci:@\(DESCRIPTION=\(ADDRESS=\(PROTOCOL=tcp\)\(HOST=localhost\)\(PORT=1521\)\)\(CONNECT_DATA=\(SERVICE_NAME=orcl.oracle.com\)\)\)"


compile:
	$(JAVAC) -classpath $(CLASSPATH) -g XA6.java

%.class : %.java
	$(JAVAC) -classpath $(CLASSPATH) -g $<

run: XA6


# MODIFY_HERE
# Please use appropriate connect string that is defined in tnsnames.ora
# Please see XA6.java for setup comments
XA6: XA6.class
	$(JAVA) -classpath $(CLASSPATH) -DJDBC_URL=$(JDBC_URL) XA6 orcl.oracle.com

clobber:
	rm -f *.class
