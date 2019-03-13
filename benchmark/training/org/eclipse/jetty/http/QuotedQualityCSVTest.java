/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.http;


import java.util.ArrayList;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static QuotedQualityCSV.MOST_SPECIFIC_MIME_ORDERING;


public class QuotedQualityCSVTest {
    @Test
    public void test7231_5_3_2_example1() {
        QuotedQualityCSV values = new QuotedQualityCSV();
        values.addValue(" audio/*; q=0.2, audio/basic");
        MatcherAssert.assertThat(values, Matchers.contains("audio/basic", "audio/*"));
    }

    @Test
    public void test7231_5_3_2_example2() {
        QuotedQualityCSV values = new QuotedQualityCSV();
        values.addValue("text/plain; q=0.5, text/html,");
        values.addValue("text/x-dvi; q=0.8, text/x-c");
        MatcherAssert.assertThat(values, Matchers.contains("text/html", "text/x-c", "text/x-dvi", "text/plain"));
    }

    @Test
    public void test7231_5_3_2_example3() {
        QuotedQualityCSV values = new QuotedQualityCSV();
        values.addValue("text/*, text/plain, text/plain;format=flowed, */*");
        // Note this sort is only on quality and not the most specific type as per 5.3.2
        MatcherAssert.assertThat(values, Matchers.contains("text/*", "text/plain", "text/plain;format=flowed", "*/*"));
    }

    @Test
    public void test7231_5_3_2_example3_most_specific() {
        QuotedQualityCSV values = new QuotedQualityCSV(MOST_SPECIFIC_MIME_ORDERING);
        values.addValue("text/*, text/plain, text/plain;format=flowed, */*");
        MatcherAssert.assertThat(values, Matchers.contains("text/plain;format=flowed", "text/plain", "text/*", "*/*"));
    }

    @Test
    public void test7231_5_3_2_example4() {
        QuotedQualityCSV values = new QuotedQualityCSV();
        values.addValue("text/*;q=0.3, text/html;q=0.7, text/html;level=1,");
        values.addValue("text/html;level=2;q=0.4, */*;q=0.5");
        MatcherAssert.assertThat(values, Matchers.contains("text/html;level=1", "text/html", "*/*", "text/html;level=2", "text/*"));
    }

    @Test
    public void test7231_5_3_4_example1() {
        QuotedQualityCSV values = new QuotedQualityCSV();
        values.addValue("compress, gzip");
        values.addValue("");
        values.addValue("*");
        values.addValue("compress;q=0.5, gzip;q=1.0");
        values.addValue("gzip;q=1.0, identity; q=0.5, *;q=0");
        MatcherAssert.assertThat(values, Matchers.contains("compress", "gzip", "*", "gzip", "gzip", "compress", "identity"));
    }

    @Test
    public void testOWS() {
        QuotedQualityCSV values = new QuotedQualityCSV();
        values.addValue("  value 0.5  ;  p = v  ;  q =0.5  ,  value 1.0 ");
        MatcherAssert.assertThat(values, Matchers.contains("value 1.0", "value 0.5;p=v"));
    }

    @Test
    public void testEmpty() {
        QuotedQualityCSV values = new QuotedQualityCSV();
        values.addValue(",aaaa,  , bbbb ,,cccc,");
        MatcherAssert.assertThat(values, Matchers.contains("aaaa", "bbbb", "cccc"));
    }

    @Test
    public void testQuoted() {
        QuotedQualityCSV values = new QuotedQualityCSV();
        values.addValue("  value 0.5  ;  p = \"v  ;  q = \\\"0.5\\\"  ,  value 1.0 \"  ");
        MatcherAssert.assertThat(values, Matchers.contains("value 0.5;p=\"v  ;  q = \\\"0.5\\\"  ,  value 1.0 \""));
    }

    @Test
    public void testOpenQuote() {
        QuotedQualityCSV values = new QuotedQualityCSV();
        values.addValue("value;p=\"v");
        MatcherAssert.assertThat(values, Matchers.contains("value;p=\"v"));
    }

    @Test
    public void testQuotedQuality() {
        QuotedQualityCSV values = new QuotedQualityCSV();
        values.addValue("  value 0.5  ;  p = v  ;  q = \"0.5\"  ,  value 1.0 ");
        MatcherAssert.assertThat(values, Matchers.contains("value 1.0", "value 0.5;p=v"));
    }

    @Test
    public void testBadQuality() {
        QuotedQualityCSV values = new QuotedQualityCSV();
        values.addValue("value0.5;p=v;q=0.5,value1.0,valueBad;q=X");
        MatcherAssert.assertThat(values, Matchers.contains("value1.0", "value0.5;p=v"));
    }

    @Test
    public void testBad() {
        QuotedQualityCSV values = new QuotedQualityCSV();
        // None of these should throw exceptions
        values.addValue(null);
        values.addValue("");
        values.addValue(";");
        values.addValue("=");
        values.addValue(",");
        values.addValue(";;");
        values.addValue(";=");
        values.addValue(";,");
        values.addValue("=;");
        values.addValue("==");
        values.addValue("=,");
        values.addValue(",;");
        values.addValue(",=");
        values.addValue(",,");
        values.addValue(";;;");
        values.addValue(";;=");
        values.addValue(";;,");
        values.addValue(";=;");
        values.addValue(";==");
        values.addValue(";=,");
        values.addValue(";,;");
        values.addValue(";,=");
        values.addValue(";,,");
        values.addValue("=;;");
        values.addValue("=;=");
        values.addValue("=;,");
        values.addValue("==;");
        values.addValue("===");
        values.addValue("==,");
        values.addValue("=,;");
        values.addValue("=,=");
        values.addValue("=,,");
        values.addValue(",;;");
        values.addValue(",;=");
        values.addValue(",;,");
        values.addValue(",=;");
        values.addValue(",==");
        values.addValue(",=,");
        values.addValue(",,;");
        values.addValue(",,=");
        values.addValue(",,,");
        values.addValue("x;=1");
        values.addValue("=1");
        values.addValue("q=x");
        values.addValue("q=0");
        values.addValue("q=");
        values.addValue("q=,");
        values.addValue("q=;");
    }

    /* ------------------------------------------------------------ */
    private static final String[] preferBrotli = new String[]{ "br", "gzip" };

    private static final String[] preferGzip = new String[]{ "gzip", "br" };

    private static final String[] noFormats = new String[]{  };

    @Test
    public void testFirefoxContentEncodingWithBrotliPreference() {
        QuotedQualityCSV values = new QuotedQualityCSV(QuotedQualityCSVTest.preferBrotli);
        values.addValue("gzip, deflate, br");
        MatcherAssert.assertThat(values, Matchers.contains("br", "gzip", "deflate"));
    }

    @Test
    public void testFirefoxContentEncodingWithGzipPreference() {
        QuotedQualityCSV values = new QuotedQualityCSV(QuotedQualityCSVTest.preferGzip);
        values.addValue("gzip, deflate, br");
        MatcherAssert.assertThat(values, Matchers.contains("gzip", "br", "deflate"));
    }

    @Test
    public void testFirefoxContentEncodingWithNoPreference() {
        QuotedQualityCSV values = new QuotedQualityCSV(QuotedQualityCSVTest.noFormats);
        values.addValue("gzip, deflate, br");
        MatcherAssert.assertThat(values, Matchers.contains("gzip", "deflate", "br"));
    }

    @Test
    public void testChromeContentEncodingWithBrotliPreference() {
        QuotedQualityCSV values = new QuotedQualityCSV(QuotedQualityCSVTest.preferBrotli);
        values.addValue("gzip, deflate, sdch, br");
        MatcherAssert.assertThat(values, Matchers.contains("br", "gzip", "deflate", "sdch"));
    }

    @Test
    public void testComplexEncodingWithGzipPreference() {
        QuotedQualityCSV values = new QuotedQualityCSV(QuotedQualityCSVTest.preferGzip);
        values.addValue("gzip;q=0.9, identity;q=0.1, *;q=0.01, deflate;q=0.9, sdch;q=0.7, br;q=0.9");
        MatcherAssert.assertThat(values, Matchers.contains("gzip", "br", "deflate", "sdch", "identity", "*"));
    }

    @Test
    public void testComplexEncodingWithBrotliPreference() {
        QuotedQualityCSV values = new QuotedQualityCSV(QuotedQualityCSVTest.preferBrotli);
        values.addValue("gzip;q=0.9, identity;q=0.1, *;q=0, deflate;q=0.9, sdch;q=0.7, br;q=0.99");
        MatcherAssert.assertThat(values, Matchers.contains("br", "gzip", "deflate", "sdch", "identity"));
    }

    @Test
    public void testStarEncodingWithGzipPreference() {
        QuotedQualityCSV values = new QuotedQualityCSV(QuotedQualityCSVTest.preferGzip);
        values.addValue("br, *");
        MatcherAssert.assertThat(values, Matchers.contains("*", "br"));
    }

    @Test
    public void testStarEncodingWithBrotliPreference() {
        QuotedQualityCSV values = new QuotedQualityCSV(QuotedQualityCSVTest.preferBrotli);
        values.addValue("gzip, *");
        MatcherAssert.assertThat(values, Matchers.contains("*", "gzip"));
    }

    @Test
    public void testSameQuality() {
        QuotedQualityCSV values = new QuotedQualityCSV();
        values.addValue("one;q=0.5,two;q=0.5,three;q=0.5");
        MatcherAssert.assertThat(values.getValues(), Matchers.contains("one", "two", "three"));
    }

    @Test
    public void testNoQuality() {
        QuotedQualityCSV values = new QuotedQualityCSV();
        values.addValue("one,two;,three;x=y");
        MatcherAssert.assertThat(values.getValues(), Matchers.contains("one", "two", "three;x=y"));
    }

    @Test
    public void testQuality() {
        List<String> results = new ArrayList<>();
        QuotedQualityCSV values = new QuotedQualityCSV() {
            @Override
            protected void parsedValue(StringBuffer buffer) {
                results.add(("parsedValue: " + (buffer.toString())));
                super.parsedValue(buffer);
            }

            @Override
            protected void parsedParam(StringBuffer buffer, int valueLength, int paramName, int paramValue) {
                String param = buffer.substring(paramName, buffer.length());
                results.add(("parsedParam: " + param));
                super.parsedParam(buffer, valueLength, paramName, paramValue);
            }
        };
        // The provided string is not legal according to some RFCs ( not a token because of = and not a parameter because not preceded by ; )
        // The string is legal according to RFC7239 which allows for just parameters (called forwarded-pairs)
        values.addValue("p=0.5,q=0.5");
        // The QuotedCSV implementation is lenient and adopts the later interpretation and thus sees q=0.5 and p=0.5 both as parameters
        MatcherAssert.assertThat(results, Matchers.contains("parsedValue: ", "parsedParam: p=0.5", "parsedValue: ", "parsedParam: q=0.5"));
        // However the QuotedQualityCSV only handles the q parameter and that is consumed from the parameter string.
        MatcherAssert.assertThat(values, Matchers.contains("p=0.5", ""));
    }
}

