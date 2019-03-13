/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.harmony.luni.tests.java.net;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.UnsupportedCharsetException;
import junit.framework.TestCase;


public class URLEncoderTest extends TestCase {
    /**
     * URLEncoder#encode(String, String)
     */
    public void test_encodeLjava_lang_StringLjava_lang_String() throws Exception {
        // Regression for HARMONY-24
        try {
            URLEncoder.encode("str", "unknown_enc");
            TestCase.fail("Assert 0: Should throw UEE for invalid encoding");
        } catch (UnsupportedEncodingException e) {
        } catch (UnsupportedCharsetException e) {
            // expected
        }
        // Regression for HARMONY-1233
        try {
            URLEncoder.encode(null, "harmony");
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException expected) {
        } catch (UnsupportedCharsetException expected) {
        }
    }

    // http://b/11571917
    public void test11571917() throws Exception {
        TestCase.assertEquals("%82%A0", URLEncoder.encode("?", "Shift_JIS"));
        TestCase.assertEquals("%82%A9", URLEncoder.encode("?", "Shift_JIS"));
        TestCase.assertEquals("%97%43", URLEncoder.encode("?", "Shift_JIS"));
        TestCase.assertEquals("%24", URLEncoder.encode("$", "Shift_JIS"));
        TestCase.assertEquals("%E3%81%8B", URLEncoder.encode("?", "UTF-8"));
        TestCase.assertEquals("%82%A0%82%A9%97%43%24%E3%81%8B", (((((URLEncoder.encode("?", "Shift_JIS")) + (URLEncoder.encode("?", "Shift_JIS"))) + (URLEncoder.encode("?", "Shift_JIS"))) + (URLEncoder.encode("$", "Shift_JIS"))) + (URLEncoder.encode("?", "UTF-8"))));
    }
}

