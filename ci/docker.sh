#!/bin/sh
export TERM=${TERM:-dumb}

set -e

cp apigateway-jar/apigateway*.jar easy-api-gateway/src/main/docker
