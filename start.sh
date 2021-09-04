#!/usr/bin/env bash

set -eu

JAVA_OPTS=(
  "-Xmx1G"
  "-Xms1G"
  "-Dcom.sun.management.jmxremote"
  "-Dcom.sun.management.jmxremote.local.only=true"
  "-Dcom.sun.management.jmxremote.port=1100"
  "-Dcom.sun.management.jmxremote.authenticate=false"
  "-Dcom.sun.management.jmxremote.ssl=false"
#  -XX:+HeapDumpOnOutOfMemoryError
  -XX:+ExitOnOutOfMemoryError
)

main() {
  local version=${1:?provide version}
  ./gradlew ":jetty${version}:shadowJar"
  java ${JAVA_OPTS[@]} -cp "./jetty${version}/build/libs/jetty${version}-1.0-SNAPSHOT-all.jar" me.serce.Main
}

main "$@"
