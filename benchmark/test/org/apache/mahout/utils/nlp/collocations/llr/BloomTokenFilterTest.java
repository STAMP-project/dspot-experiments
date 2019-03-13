/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.mahout.utils.nlp.collocations.llr;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.CharsetEncoder;
import org.apache.commons.io.Charsets;
import org.apache.hadoop.util.bloom.Filter;
import org.apache.hadoop.util.bloom.Key;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.mahout.common.MahoutTestCase;
import org.junit.Test;


public final class BloomTokenFilterTest extends MahoutTestCase {
    private static final CharsetEncoder encoder = Charsets.UTF_8.newEncoder();

    private static final String input = "The best of times the worst of times";

    private static final String[] allTokens = new String[]{ "The", "best", "of", "times", "the", "worst", "of", "times" };

    private static final String[] expectedNonKeepTokens = new String[]{ "best", "times", "the", "worst", "times" };

    private static final String[] expectedKeepTokens = new String[]{ "The", "of", "of" };

    private static final String[] filterTokens = new String[]{ "The", "of" };

    private static final String[] notFilterTokens = new String[]{ "best", "worst", "the", "times" };

    private static final String[] shingleKeepTokens = new String[]{ "The best", "best of times", "the worst", "worst of times", "of times" };

    private static final String[] expectedShingleTokens = new String[]{ "The best", "best of times", "of times", "the worst", "worst of times", "of times" };

    /**
     * test standalone filter without tokenfilter wrapping
     */
    @Test
    public void testFilter() throws IOException {
        Filter filter = BloomTokenFilterTest.getFilter(BloomTokenFilterTest.filterTokens);
        Key k = new Key();
        for (String s : BloomTokenFilterTest.filterTokens) {
            BloomTokenFilterTest.setKey(k, s);
            assertTrue((("Key for string " + s) + " should be filter member"), filter.membershipTest(k));
        }
        for (String s : BloomTokenFilterTest.notFilterTokens) {
            BloomTokenFilterTest.setKey(k, s);
            assertFalse((("Key for string " + s) + " should not be filter member"), filter.membershipTest(k));
        }
    }

    /**
     * normal case, unfiltered analyzer
     */
    @Test
    public void testAnalyzer() throws IOException {
        Reader reader = new StringReader(BloomTokenFilterTest.input);
        Analyzer analyzer = new WhitespaceAnalyzer();
        TokenStream ts = analyzer.tokenStream(null, reader);
        ts.reset();
        BloomTokenFilterTest.validateTokens(BloomTokenFilterTest.allTokens, ts);
        ts.end();
        ts.close();
    }

    /**
     * filtered analyzer
     */
    @Test
    public void testNonKeepdAnalyzer() throws IOException {
        Reader reader = new StringReader(BloomTokenFilterTest.input);
        Analyzer analyzer = new WhitespaceAnalyzer();
        TokenStream ts = analyzer.tokenStream(null, reader);
        ts.reset();
        TokenStream f = /* toss matching tokens */
        new BloomTokenFilter(BloomTokenFilterTest.getFilter(BloomTokenFilterTest.filterTokens), false, ts);
        BloomTokenFilterTest.validateTokens(BloomTokenFilterTest.expectedNonKeepTokens, f);
        ts.end();
        ts.close();
    }

    /**
     * keep analyzer
     */
    @Test
    public void testKeepAnalyzer() throws IOException {
        Reader reader = new StringReader(BloomTokenFilterTest.input);
        Analyzer analyzer = new WhitespaceAnalyzer();
        TokenStream ts = analyzer.tokenStream(null, reader);
        ts.reset();
        TokenStream f = /* keep matching tokens */
        new BloomTokenFilter(BloomTokenFilterTest.getFilter(BloomTokenFilterTest.filterTokens), true, ts);
        BloomTokenFilterTest.validateTokens(BloomTokenFilterTest.expectedKeepTokens, f);
        ts.end();
        ts.close();
    }

    /**
     * shingles, keep those matching whitelist
     */
    @Test
    public void testShingleFilteredAnalyzer() throws IOException {
        Reader reader = new StringReader(BloomTokenFilterTest.input);
        Analyzer analyzer = new WhitespaceAnalyzer();
        TokenStream ts = analyzer.tokenStream(null, reader);
        ts.reset();
        ShingleFilter sf = new ShingleFilter(ts, 3);
        TokenStream f = new BloomTokenFilter(BloomTokenFilterTest.getFilter(BloomTokenFilterTest.shingleKeepTokens), true, sf);
        BloomTokenFilterTest.validateTokens(BloomTokenFilterTest.expectedShingleTokens, f);
        ts.end();
        ts.close();
    }
}

