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

import static com.github.heuermh.adam.examples.CigarUtils.percentIdentity;

import static org.junit.Assert.assertEquals;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.TextCigarCodec;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit test for CigarUtils.
 *
 * @author  Michael Heuer
 */
public final class CigarUtilsTest {
    private String reference;
    private String read;
    private int referenceStart;
    private Cigar cigar;
    private static final String SEQ = "ATCGATCGAT";

    @Before
    public void setUp() {
        reference = SEQ;
        read = SEQ;
        referenceStart = 0;
        cigar = TextCigarCodec.decode("10=");
    }

    @Test(expected=NullPointerException.class)
    public void testPercentIdentityNullReference() {
        percentIdentity(null, read, referenceStart, cigar);
    }

    @Test(expected=NullPointerException.class)
    public void testPercentIdentityNullRead() {
        percentIdentity(reference, null, referenceStart, cigar);
    }

    @Test(expected=NullPointerException.class)
    public void testPercentIdentityNullCigar() {
        percentIdentity(reference, read, referenceStart, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testPercentIdentityEmptyReference() {
        percentIdentity("", read, referenceStart, cigar);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testPercentIdentityReferenceStartTooSmall() {
        percentIdentity(reference, read, -1, cigar);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testPercentIdentityReferenceStartTooLarge() {
        percentIdentity(reference, read, 10, cigar);
    }

    @Test
    public void testPercentIdentityExactMatchEq() {
        assertEquals(100.0f, percentIdentity(reference, read, referenceStart, cigar), 0.1f);
    }

    @Test
    public void testPercentIdentityExactMatchM() {
        assertEquals(100.0f, percentIdentity(reference, read, referenceStart, TextCigarCodec.decode("10M")), 0.1f);
    }

    @Test
    public void testPercentIdentityExactMatchMix() {
        assertEquals(100.0f, percentIdentity(reference, read, referenceStart, TextCigarCodec.decode("6=4M")), 0.1f);
    }

    @Test
    public void testPercentIdentityExactMatchMixShortReference() {
        assertEquals(100.0f, percentIdentity(reference.substring(0, 6), read, referenceStart, TextCigarCodec.decode("6M")), 0.1f);
    }

    @Test
    public void testPercentIdentityExactMatchMixShortRead() {
        assertEquals(60.0f, percentIdentity(reference, read.substring(0, 6), referenceStart, TextCigarCodec.decode("6M4N")), 0.1f);
    }

    @Ignore // needs work
    public void testPercentIdentityIndel() {
        //System.out.println("--- insertion");
        assertEquals(0.0f, percentIdentity("ATCGAATCGAT", read, 0, TextCigarCodec.decode("5M1I5M")), 0.1f);
        //System.out.println("--- done");
        assertEquals(0.0f, percentIdentity(read, "ATCGAATCGAT", 0, TextCigarCodec.decode("5M1D5M")), 0.1f);
    }
}
