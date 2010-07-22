#
# Makefile for compiling and executing files under
# demo/samples/generic/NestedCollection/
# Please use rundemo.bat on NT, win64
#
# Usage: make <all> | <thin> | <oci> | <ocitns> | <single demo>
#
# Before you run any demo programs, you should:
#       1. set up your database and make sure your database started with
#          COMPATIBLE parameter with the value of 9.0.0.0.0 or greater
#          in your database init files (e.g., tkinit.ora)
#       2. set up demo schema. Please refer to Samples-Readme.txt, section
#          Setting Up Schema for details.
#       3. Modify this makefile to use appropriate host/port/service_name.
#          search for MODIFY_HERE to process your modification.
#
# Features demonstrated:
#       Creation and retrieval of nested collections 
#


JDBC_URL=jdbc:oracle:oci:@

DEMO_CLASSPATH=.:${ORACLE_HOME}/jlib/jndi.jar:${ORACLE_HOME}/jlib/jta.jar
CLASSPATH=${ORACLE_HOME}/jdbc/lib/ojdbc14.jar:${DEMO_CLASSPATH}


# MODIFY_HERE
# Please use a correct path on your machine
JDK14_HOME=/usr/local/packages/jdk14
JAVAC=${JDK14_HOME}/bin/javac
JAVA=${JDK14_HOME}/bin/java



all: clobber compile run

# MODIFY_HERE
# Please use appropriate <host> <port> and <service_name>
thin:
	make all JDBC_URL=jdbc:oracle:thin:@//localhost:1521/orcl.oracle.com

oci:
	make all JDBC_URL=jdbc:oracle:oci:@

# MODIFY_HERE
# Please use appropriate <host> <port> and <service_name>
ocitns:
	make all JDBC_URL="jdbc:oracle:oci:@\(DESCRIPTION=\(ADDRESS=\(PROTOCOL=tcp\)\(HOST=localhost\)\(PORT=1521\)\)\(CONNECT_DATA=\(SERVICE_NAME=orcl.oracle.com\)\)\)"



compile:
	$(JAVAC) -classpath $(CLASSPATH) -g `find . -name \*.java -print`

%.class : %.java
	$(JAVAC) -classpath $(CLASSPATH) -g  $<

print: 
	echo "Classpath is $(CLASSPATH)"

run: NestedCollection

NestedCollection: NestedCollection.class 
	$(JAVA) -classpath $(CLASSPATH) -DJDBC_URL=$(JDBC_URL) NestedCollection

clobber:
	rm -f *.class
	rm -f *.out
  
