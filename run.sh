#!/bin/bash
# file: run.sh
# 
#   1. build
#   2. start application
#   3. open localhost:8080
# 

./build.sh

delayed_open() {
  sleep 3
  open "http://localhost:8080"  
}

delayed_open &

java -jar walmart.war


