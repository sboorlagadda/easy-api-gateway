#!/bin/bash
export TERM=${TERM:-dumb}

set -e

pushd git-pull-requests
./gradlew clean test
popd
