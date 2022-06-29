#!/bin/sh
set -e

mvn clean package
docker image rm servicebooker
docker buildx build -t servicebooker .