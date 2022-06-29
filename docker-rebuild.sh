#!/bin/sh
set -e

./mvnw clean package
[[ $(docker image ls -q servicebooker) ]] && docker image rm servicebooker
docker buildx build -t servicebooker .