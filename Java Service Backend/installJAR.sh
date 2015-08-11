#!/bin/bash

javac -d bin *.java
cd bin
jar cf wikiwalking.jar wikiwalking
mv wikiwalking.jar ../../WEB-INF/lib/
touch ../../WEB-INF/lib/*