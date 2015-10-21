#!/bin/sh
#
# run with plugin
set -e
echo "running tests first..."
mvn verify
echo "starting up"
mvn validate -Drun
