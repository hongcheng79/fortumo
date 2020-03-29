#!/bin/bash
JARS=`ls *.jar`

if [ ${#JARS[@]} -eq 0 ]; then
    echo "No jar file to run !!!"
else
    echo "JAVA_OPTS:$JAVA_OPTS"
    java -jar "${JARS[0]}"
fi