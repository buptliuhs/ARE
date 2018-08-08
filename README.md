# ARE
Activity Recognition Engine (ARE) is a system that is capable of recognising physical activities by using triaxial accelerometer sensors. ARE is also capable of viewing the raw acceleration signals. The current supported device in ARE is the uSense (refer to FARSEEING). Supporting for AX3 from Open Movement is under development. 

## Operating System
Ubuntu 14.04 LTS. Actually, ARE is platform-independent, you can install it on Windows or any Unix-Like operating system as long as you can make Scilab work.

## Dev & Prod env
$ sudo apt-get install openjdk-7-jdk ant

$ sudo apt-get install openjdk-7-jre (prod)

## Install mysql
$ sudo apt-get install mysql-server mysql-client

## Install tomcat
$ sudo apt-get install tomcat7

## Import database
$ mysql -u root -p

mysql> create database are;
Query OK, 1 row affected (0.01 sec)

$ mysql -u root -p are < dump.sql

## Update tomcat server.xml
$ sudo vi /var/lib/tomcat7/conf/server.xml

Add following right before </Host>

```html
<Context docBase="ARE" path="/ARE"
  reloadable="true" source="org.eclipse.jst.jee.server:ARE">
  <Resource auth="Container" driverClassName="com.mysql.jdbc.Driver"
    factory="org.apache.tomcat.jdbc.pool.DataSourceFactory"
    initialSize="10" logAbandoned="true" maxActive="100" maxIdle="30"
    maxWait="10000" minEvictableIdleTimeMillis="30000" name="jdbc/ARE"
    password="root" removeAbandoned="true" removeAbandonedTimeout="60"
    testOnBorrow="true" testOnReturn="false" testWhileIdle="true"
    timeBetweenEvictionRunsMillis="30000" type="javax.sql.DataSource"
    url="jdbc:mysql://localhost:3306/are" username="root"
    validationInterval="30000" validationQuery="SELECT 1" />
</Context> 
```

## Add mysql JDBC driver to tomcat lib path
wget http://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-5.1.35.tar.gz

Extract mysql-connector-java-5.1.35-bin.jar from the tar ball.

Copy mysql-connector-java-5.1.35-bin.jar to /usr/share/tomcat7/lib

## Create folders
$ sudo mkdir -p /opt/uoa/

$ sudo chown -R tomcat7:tomcat7 /opt/uoa

## Copy ARE.war to /var/lib/tomcat7/webapps

## Install scilab
$ sudo apt-get install scilab

## Update index.html
Backup /var/lib/tomcat7/webapps/ROOT/index.html to /var/lib/tomcat7/webapps/ROOT/index.html.bak

Edit /var/lib/tomcat7/webapps/ROOT/index.html to following content:

```html
<html>
  <head>
    <meta http-equiv="refresh" content="0;URL=http://<hostname>/ARE/">
  </head>
  <body>
  </body>
</html>
```

## TOMCAT Settings
Add following line to /etc/default/tomcat7

AUTHBIND=yes

JAVA_OPTS=”...... -Xmx4096m ……..”

SCILAB_EXECUTABLE=/usr/bin/scilab-cli

SCILAB_EXECUTABLE=D:\scilab\bin\Scilex.exe

## Reboot tomcat7
sudo service tomcat7 restart

## CAUTION:
### Issues when running on Ubuntu 12.04:
- Set JAVA_HOME in /etc/default/tomcat7
- Copy tomcat-jdbc.jar to /usr/share/tomcat7/lib

