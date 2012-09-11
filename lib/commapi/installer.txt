#!/bin/sh

mvn install:install-file -DgroupId=javax.comm -DartifactId=comm -Dversion=2.0.3 -Dpackaging=jar -Dfile=comm.jar

