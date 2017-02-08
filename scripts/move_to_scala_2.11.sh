#!/bin/bash

set +x

find . -name "pom.xml" -exec sed \
     -e "s/\<artifactId\>adam-examples_2.10\<\/artifactId\>/\<artifactId\>adam-examples_2.11\<\/artifactId\>/g" \
     -e "s/\<name\>adam-examples_2.10\<\/name\>/\<name\>adam-examples_2.11\<\/name\>/g" \
     -e "s/\<scala.version\>2.10.6\<\/scala.version\>/\<scala.version\>2.11.8\<\/scala.version\>/g" \
     -e "s/\<scala.artifact.suffix\>2.10\<\/scala.artifact.suffix\>/\<scala.artifact.suffix\>2.11\<\/scala.artifact.suffix\>/g" \
     -i .2.11.bak '{}' \;
find . -name "*.2.11.bak" -exec rm {} \;
