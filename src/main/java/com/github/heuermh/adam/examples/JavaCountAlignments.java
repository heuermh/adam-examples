/**
 * Copyright 2015-2021 held jointly by the individual authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.heuermh.adam.examples;

import org.apache.spark.SparkConf;

import org.apache.spark.SparkContext;
import org.apache.spark.SparkContext;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaPairRDD;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;

import org.apache.spark.rdd.RDD;

import org.bdgenomics.adam.api.java.JavaADAMContext;

import org.bdgenomics.adam.ds.ADAMContext;

import org.bdgenomics.adam.ds.read.AlignmentDataset;

import org.bdgenomics.formats.avro.Alignment;

import scala.Function1;
import scala.Option;
import scala.Tuple2;

/**
 * Count alignments example implemented in Java.
 *
 * @author  Michael Heuer
 */
public final class JavaCountAlignments {

    /**
     * Main.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        if (args.length < 1) {
            System.err.println("at least one argument required, e.g. foo.sam");
            System.exit(1);
        }

        SparkConf conf = new SparkConf()
            .setAppName("Java Count Alignments")
            .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .set("spark.kryo.registrator", "org.bdgenomics.adam.serialization.ADAMKryoRegistrator")
            .set("spark.kryo.referenceTracking", "true");

        SparkContext sc = new SparkContext(conf);
        JavaADAMContext jac = new JavaADAMContext(new ADAMContext(sc));
        AlignmentDataset alignments = jac.loadAlignments(args[0]);
        JavaRDD<Alignment> jrdd = alignments.jrdd();

        JavaRDD<String> referenceNames = jrdd.map(new Function<Alignment, String>() {
                @Override
                public String call(final Alignment rec) {
                    return rec.getReadMapped() ? rec.getReferenceName() : "unmapped";
                }
            });

        JavaPairRDD<String, Integer> counts = referenceNames.mapToPair(new PairFunction<String, String, Integer>() {
                @Override
                public Tuple2<String, Integer> call(final String referenceName) {
                    return new Tuple2<String, Integer>(referenceName, Integer.valueOf(1));
                }
            });

        JavaPairRDD<String, Integer> reducedCounts = counts.reduceByKey(new Function2<Integer, Integer, Integer>() {
                @Override
                public Integer call(final Integer value0, final Integer value1) {
                    return Integer.valueOf(value0.intValue() + value1.intValue());
                }
            });

        reducedCounts.foreach(new VoidFunction<Tuple2<String, Integer>>() {
                @Override
                public void call(final Tuple2<String, Integer> count) {
                    System.out.println(count.toString());
                }
            });
    }
}
