#!/bin/bash
# file: build.sh
# 
#   1. cleanup
#   2. build client (npm)
#   3. build server (war) 
#   4. copy result (war) to current folder
#

rm -rf server/build 
rm -rf client/dist 

cd client

npm install

npm run-script build

cd ../server

gradle bundle

cd ..

cp server/build/libs/walmart-1.0.war ./walmart.war 


# java -jar walmart.war