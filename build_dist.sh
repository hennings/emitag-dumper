#!/bin/sh

mvn package
mkdir -p target/dist
mkdir -p target/dist64
cp lib/jperipheral-i386/lib/*.dll target/dist/
cp lib/jperipheral-x64/lib/*.dll target/dist64/
cp target/emitag-2.0-SNAPSHOT-shaded.jar target/dist
cp target/emitag-2.0-SNAPSHOT-shaded.jar target/dist64
cp src/main/scripts/run.sh target/dist
cp src/main/scripts/run.sh target/dist64
cp src/main/scripts/run.bat target/dist
cp src/main/scripts/run.bat target/dist64
zip -j target/emitagapp-i386.zip target/dist/*
zip -j target/emitagapp-x64.zip target/dist64/*

