adam-examples
=============

[![Build Status](https://travis-ci.org/heuermh/adam-examples.svg?branch=master)](https://travis-ci.org/heuermh/adam-examples)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.heuermh.adamexamples/adam-examples_2.12.svg?maxAge=600)](http://search.maven.org/#search%7Cga%7C1%7Ccom.github.heuermh.adamexamples)

Examples in Java and Scala for ADAM: Genomic Data System.  Apache 2 licensed.


### Hacking adam-examples

Install

 * JDK 1.8 or later, http://openjdk.java.net
 * Scala 2.12.15 or later, http://www.scala-lang.org
 * Apache Maven 3.3.1 or later, http://maven.apache.org
 * Apache Spark 3.2.0 or later, http://spark.apache.org
 * ADAM: Genomic Data System 0.37.0 or later, https://github.com/bigdatagenomics/adam


To build

    $ mvn install


### Running adam-examples using ```spark-submit```

    $ spark-submit \
      --master local[4] \
      --class com.github.heuermh.adam.examples.CountAlignments \
      target/adam-examples_2.12-${ADAM_VERSION}.jar \
      src/test/resources/small.sam
    
    (1,20)


    $ spark-submit \
      --master local[4] \
      --class com.github.heuermh.adam.examples.CountAlignmentsPerRead \
      target/adam-examples_2.12-${ADAM_VERSION}.jar \
      src/test/resources/small.sam
    
    (simread:1:237728409:true,1)
    (simread:1:195211965:false,1)
    (simread:1:163841413:false,1)
    (simread:1:231911906:false,1)
    (simread:1:26472783:false,1)
    (simread:1:165341382:true,1)
    (simread:1:240344442:true,1)
    (simread:1:50683371:false,1)
    (simread:1:240997787:true,1)
    (simread:1:14397233:false,1)
    (simread:1:207027738:true,1)
    (simread:1:20101800:true,1)
    (simread:1:5469106:true,1)
    (simread:1:186794283:true,1)
    (simread:1:189606653:true,1)
    (simread:1:101556378:false,1)
    (simread:1:37577445:false,1)
    (simread:1:89554252:false,1)
    (simread:1:153978724:false,1)
    (simread:1:169801933:true,1)
