#!/bin/bash

while true
do
  curl -X POST "http://127.0.0.1:8080/api/v1/hello"
  curl -X GET "http://127.0.0.1:8080/api/v1/hello" 
  curl -X GET "http://127.0.0.1:8080/api/v1/hi/Pluto"
  curl -X GET "http://127.0.0.1:8080/api/v1/aloha/Pippo" 
done
