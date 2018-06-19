#!/bin/bash

set -e

# JAVA_OPTS=${JAVA_OPTS:="-Xmx256m"}

exec java -jar $JAVA_OPTS target/tele-bot-1.0.0-SNAPSHOT-standalone.jar