#!/bin/bash
export TERM=${TERM:-dumb}

set -e

pushd easy-api-gateway
./gradlew clean bootRepackage
popd
