/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.tests.java.net;


import java.net.IDN;
import junit.framework.TestCase;


public class IDNTest extends TestCase {
    /**
     * {@link java.net.IDN#toASCII(String)}
     *
     * @since 1.6
     */
    public void test_ToASCII_LString() {
        try {
            IDN.toASCII(null);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        TestCase.assertEquals("www.xn--gwtq9nb2a.jp", IDN.toASCII("www.\u65e5\u672c\u5e73.jp"));
        TestCase.assertEquals("www.xn--vckk7bxa0eza9ezc9d.com", IDN.toASCII("www.\u30cf\u30f3\u30c9\u30dc\u30fc\u30eb\u30b5\u30e0\u30ba.com"));
        TestCase.assertEquals("www.xn--frgbolaget-q5a.nu", IDN.toASCII("www.f\u00e4rgbolaget.nu"));
        TestCase.assertEquals("www.xn--bcher-kva.de", IDN.toASCII("www.b\u00fccher.de"));
        TestCase.assertEquals("www.xn--brndendekrlighed-vobh.com", IDN.toASCII("www.br\u00e6ndendek\u00e6rlighed.com"));
        TestCase.assertEquals("www.xn--rksmrgs-5wao1o.se", IDN.toASCII("www.r\u00e4ksm\u00f6rg\u00e5s.se"));
        TestCase.assertEquals("www.xn--9d0bm53a3xbzui.com", IDN.toASCII("www.\uc608\ube44\uad50\uc0ac.com"));
        TestCase.assertEquals("xn--lck1c3crb1723bpq4a.com", IDN.toASCII("\u7406\u5bb9\u30ca\u30ab\u30e0\u30e9.com"));
        TestCase.assertEquals("xn--l8je6s7a45b.org", IDN.toASCII("\u3042\u30fc\u308b\u3044\u3093.org"));
        TestCase.assertEquals("www.xn--frjestadsbk-l8a.net", IDN.toASCII("www.f\u00e4rjestadsbk.net"));
        TestCase.assertEquals("www.xn--mkitorppa-v2a.edu", IDN.toASCII("www.m\u00e4kitorppa.edu"));
    }

    /**
     * {@link java.net.IDN#toASCII(String, int)}
     *
     * @since 1.6
     */
    public void test_ToASCII_LString_I() {
        try {
            IDN.toASCII("www.br\u00e6ndendek\u00e6rlighed.com", IDN.USE_STD3_ASCII_RULES);
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            IDN.toASCII("www.r\u00e4ksm\u00f6rg\u00e5s.se", IDN.USE_STD3_ASCII_RULES);
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            IDN.toASCII("www.f\u00e4rjestadsbk.net", ((IDN.ALLOW_UNASSIGNED) | (IDN.USE_STD3_ASCII_RULES)));
        } catch (IllegalArgumentException e) {
            // expected
        }
        TestCase.assertEquals("www.xn--gwtq9nb2a.jp", IDN.toASCII("www.\u65e5\u672c\u5e73.jp", 0));
        TestCase.assertEquals("www.xn--vckk7bxa0eza9ezc9d.com", IDN.toASCII("www.\u30cf\u30f3\u30c9\u30dc\u30fc\u30eb\u30b5\u30e0\u30ba.com", 0));
        TestCase.assertEquals("www.xn--frgbolaget-q5a.nu", IDN.toASCII("www.f\u00e4rgbolaget.nu", IDN.ALLOW_UNASSIGNED));
        TestCase.assertEquals("www.xn--bcher-kva.de", IDN.toASCII("www.b\u00fccher.de", IDN.ALLOW_UNASSIGNED));
        TestCase.assertEquals("www.google.com", IDN.toASCII("www.google.com", IDN.USE_STD3_ASCII_RULES));
    }

    /**
     * {@link java.net.IDN#toUnicode(String)}
     *
     * @since 1.6
     */
    public void test_ToUnicode_LString() {
        try {
            IDN.toUnicode(null);
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        TestCase.assertEquals("", IDN.toUnicode(""));
        TestCase.assertEquals("www.bcher.de", IDN.toUnicode("www.bcher.de"));
        TestCase.assertEquals("www.b\u00fccher.de", IDN.toUnicode("www.b\u00fccher.de"));
        TestCase.assertEquals("www.\u65e5\u672c\u5e73.jp", IDN.toUnicode("www.\u65e5\u672c\u5e73.jp"));
        TestCase.assertEquals("www.\u65e5\u672c\u5e73.jp", IDN.toUnicode("www\uff0exn--gwtq9nb2a\uff61jp"));
        TestCase.assertEquals("www.\u65e5\u672c\u5e73.jp", IDN.toUnicode("www.xn--gwtq9nb2a.jp"));
    }

    /**
     * {@link java.net.IDN#toUnicode(String, int)}
     *
     * @since 1.6
     */
    public void test_ToUnicode_LString_I() {
        TestCase.assertEquals("", IDN.toUnicode("", IDN.ALLOW_UNASSIGNED));
        TestCase.assertEquals("www.f\u00e4rgbolaget.nu", IDN.toUnicode("www.f\u00e4rgbolaget.nu", IDN.USE_STD3_ASCII_RULES));
        TestCase.assertEquals("www.r\u00e4ksm\u00f6rg\u00e5s.nu", IDN.toUnicode("www.r\u00e4ksm\u00f6rg\u00e5s\u3002nu", IDN.USE_STD3_ASCII_RULES));
        // RI bug. It cannot parse "www.xn--gwtq9nb2a.jp" when
        // USE_STD3_ASCII_RULES is set.
        TestCase.assertEquals("www.\u65e5\u672c\u5e73.jp", IDN.toUnicode("www\uff0exn--gwtq9nb2a\uff61jp", IDN.USE_STD3_ASCII_RULES));
    }
}

