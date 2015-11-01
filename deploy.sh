#!/bin/sh

service tomcat7 stop

echo "Removing old files ..."
/bin/rm -rf /var/lib/tomcat7/webapps/ARE /var/lib/tomcat7/webapps/ARE.war

echo "Sleeping 5 seconds ..."
sleep 5

echo "Copying new files ..."
/bin/cp ./build/war/ARE.war /var/lib/tomcat7/webapps/

service tomcat7 start

