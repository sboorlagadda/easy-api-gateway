#!/bin/sh
export TERM=${TERM:-dumb}

set -e

cp apigateway-jar/apigateway*.jar image
cp easy-api-gateway/src/main/docker/Dockerfile image
