#!/bin/bash
export TERM=${TERM:-dumb}

set -e

pushd easy-api-gateway
./gradlew bootRepackage
popd
cp easy-api-gateway/build/libs/apigateway.jar builds/