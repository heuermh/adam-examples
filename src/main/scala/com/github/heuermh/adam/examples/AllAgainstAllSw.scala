/**
  *  Copyright 2015-2017 held jointly by the individual authors.
  * 
  *  Licensed under the Apache License, Version 2.0 (the "License");
  *  you may not use this file except in compliance with the License.
  *  You may obtain a copy of the License at
  * 
  *  http://www.apache.org/licenses/LICENSE-2.0
  * 
  *  Unless required by applicable law or agreed to in writing, software
  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  See the License for the specific language governing permissions and
  *  limitations under the License.
  */
package com.github.heuermh.adam.examples

import org.apache.spark.{ SparkConf, SparkContext }
import org.apache.spark.SparkContext._

import org.apache.spark.rdd.RDD

import org.bdgenomics.adam.algorithms.smithwaterman.SmithWatermanConstantGapScoring

import org.bdgenomics.adam.rdd.ADAMContext._

import org.bdgenomics.adam.rdd.contig.NucleotideContigFragmentRDD

import org.bdgenomics.formats.avro.NucleotideContigFragment

/**
 * All against all Smith Waterman example.
 * 
 * @author  Michael Heuer
 */
object AllAgainstAllSw {
  def main(args: Array[String]) {
    if (args.length < 1) {
      System.err.println("at least one argument required, e.g. foo.adam")
      System.exit(1)
    }

    val conf = new SparkConf()
      .setAppName("All against all Smith Waterman")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.kryo.registrator", "org.bdgenomics.adam.serialization.ADAMKryoRegistrator")
      .set("spark.kryo.referenceTracking", "true")

    val sc = new SparkContext(conf)

    def byContigName(pair: (NucleotideContigFragment, NucleotideContigFragment)): Boolean = {
      val name1 = pair._1.getContig.getContigName
      val name2 = pair._2.getContig.getContigName
      // don't compare identical sequences
      if (name1 == name2)
        false
      // compare only in one direction
      (name1.compareTo(name2) < 0)
    }

    def sw(pair: (NucleotideContigFragment, NucleotideContigFragment)): (String) = {
      val seq1 = pair._1.getFragmentSequence
      val seq2 = pair._2.getFragmentSequence
      val name1 = pair._1.getContig.getContigName
      val name2 = pair._2.getContig.getContigName

      val sw = new SmithWatermanConstantGapScoring(seq1, seq2, 1.0, 0.0, -0.333, -0.333)
      //val identityX = CigarUtils.percentIdentity(seq1, seq2, sw.yStart, sw.cigarY)
      //val identityY = CigarUtils.percentIdentity(seq2, seq1, sw.xStart, sw.cigarX)
      val identityX = CigarUtils.percentIdentity(seq1, seq2, sw.xStart, sw.cigarX)
      val identityY = CigarUtils.percentIdentity(seq2, seq1, sw.yStart, sw.cigarY)
      List(name1, name2,
        sw.xStart, sw.cigarX.toString, identityX,
        sw.yStart, sw.cigarY.toString, identityY)
        .mkString("\t")
    }

    val contigFragments: NucleotideContigFragmentRDD = sc.loadParquetContigFragments(args(0))
    val contigs = contigFragments.mergeFragments()

    val contigsRdd: RDD[NucleotideContigFragment] = contigs.rdd
    val allAgainstAll = contigsRdd.cartesian(contigsRdd)
    val forward = allAgainstAll.filter(byContigName)
    val cigar = forward.map(sw)
    cigar.saveAsTextFile(args(0).replace(".adam", ".cigar"))
  }
}
