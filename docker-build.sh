#!/bin/sh
mvn clean package
docker buildx build -t servicebooker .