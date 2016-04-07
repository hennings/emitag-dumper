#!/bin/sh

mvn package
mkdir target/dist
mkdir target/dist64
cp lib/jperipheral-i386/lib target/dist/
cp lib/jperipheral-x64/lib target/dist64/
cp target/emitag-2.0-SNAPSHOT-shaded.jar target/dist
cp target/emitag-2.0-SNAPSHOT-shaded.jar target/dist64
cp src/main/scripts/run.sh target/dist
cp src/main/scripts/run.sh target/dist64
cp src/main/scripts/run.bat target/dist
cp src/main/scripts/run.bat target/dist64


