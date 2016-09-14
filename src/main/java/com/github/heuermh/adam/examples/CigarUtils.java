/**
 * Copyright 2015 held jointly by the individual authors.
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

import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;

/**
 * Utility methods on CIGAR strings.
 *
 * @author  Michael Heuer
 */
public final class CigarUtils {

    /**
     * Calculate the percent identity of the reference and read sequences
     * given the reference start and CIGAR string.
     *
     * @param reference reference sequence, must not be null
     * @param read read sequence, must not be null
     * @param referenceStart reference start, zero-based coordinate system
     * @param cigar CIGAR string, must not be null
     */
    public static float percentIdentity(final String reference,
                                        final String read,
                                        final int referenceStart,
                                        final Cigar cigar) {
        if (reference == null) {
            throw new NullPointerException("reference must not be null");
        }
        if (reference.length() == 0) {
            throw new IllegalArgumentException("reference must not be empty");
        }
        if (read == null) {
            throw new NullPointerException("read must not be null");
        }
        if (referenceStart < 0) {
            throw new IllegalArgumentException("referenceStart must be at least zero");
        }
        if (referenceStart >= reference.length()) {
            throw new IllegalArgumentException("referenceStart must be less than reference length");
        }
        if (cigar == null) {
            throw new NullPointerException("cigar must not be null");
        }
        int matches = 0;
        int referenceIndex = referenceStart;
        int readIndex = 0;

        for (CigarElement element : cigar.getCigarElements()) {
            int length = element.getLength();
            CigarOperator op = element.getOperator();

            //System.err.println("start:  " + referenceStart);
            //System.err.println("referenceIndex: " + referenceIndex);
            //System.err.println("reference length: " + reference.length());
            //System.err.println("referenceIndex + length: " + (referenceIndex + length));
            //System.err.println("ref:    " + reference.substring(referenceIndex));
            //System.err.println("read:   " + read.substring(readIndex));
            //System.err.println("readIndex: " + readIndex);
            //System.err.println("read length: " + read.length());
            //System.err.println("readIndex + length: " + (readIndex + length));
            //System.err.println("op:     " + op);
            //System.err.println("length: " + length);
            //System.err.println("cigar:  " + cigar.toString());

            switch (op) {
            case D:
                referenceIndex -= length;
                break;
            case I:
                readIndex -= length;
                break;
            case EQ:
                matches += length;
                break;
            case M:
                for (int i = 0; i < length; i++) {
                    if (reference.charAt(referenceIndex + i) == read.charAt(readIndex + i)) {
                        matches++;
                    }
                }
                break;
            default: break;
            }
            if (op.consumesReferenceBases()) {
                referenceIndex += length;
            }
            if (op.consumesReadBases()) {
                readIndex += length;
            }
            //System.out.println("matches: " + matches);
        }
        //System.out.println("matches / reference.length(): " + ((matches / (float) reference.length()) * 100.0f));
        return (matches / (float) reference.length()) * 100.0f;
    }
}
