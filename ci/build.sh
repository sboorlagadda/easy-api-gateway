#!/bin/bash
export TERM=${TERM:-dumb}

set -e

VERSION=$(cat apigateway-version/version)

pushd easy-api-gateway
./gradlew build -Pversion=$VERSION
popd
cp easy-api-gateway/build/libs/apigateway*.jar builds/
