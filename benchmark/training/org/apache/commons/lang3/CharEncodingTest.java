/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3;


import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests CharEncoding.
 *
 * @see CharEncoding
 */
@SuppressWarnings("deprecation")
public class CharEncodingTest {
    /**
     * The class can be instantiated.
     */
    @Test
    public void testConstructor() {
        new CharEncoding();
    }

    @Test
    public void testMustBeSupportedJava1_3_1_and_above() {
        this.assertSupportedEncoding(CharEncoding.ISO_8859_1);
        this.assertSupportedEncoding(CharEncoding.US_ASCII);
        this.assertSupportedEncoding(CharEncoding.UTF_16);
        this.assertSupportedEncoding(CharEncoding.UTF_16BE);
        this.assertSupportedEncoding(CharEncoding.UTF_16LE);
        this.assertSupportedEncoding(CharEncoding.UTF_8);
    }

    @Test
    public void testSupported() {
        Assertions.assertTrue(CharEncoding.isSupported("UTF8"));
        Assertions.assertTrue(CharEncoding.isSupported("UTF-8"));
        Assertions.assertTrue(CharEncoding.isSupported("ASCII"));
    }

    @Test
    public void testNotSupported() {
        Assertions.assertFalse(CharEncoding.isSupported(null));
        Assertions.assertFalse(CharEncoding.isSupported(""));
        Assertions.assertFalse(CharEncoding.isSupported(" "));
        Assertions.assertFalse(CharEncoding.isSupported("\t\r\n"));
        Assertions.assertFalse(CharEncoding.isSupported("DOESNOTEXIST"));
        Assertions.assertFalse(CharEncoding.isSupported("this is not a valid encoding name"));
    }

    @Test
    public void testStandardCharsetsEquality() {
        Assertions.assertEquals(StandardCharsets.ISO_8859_1.name(), CharEncoding.ISO_8859_1);
        Assertions.assertEquals(StandardCharsets.US_ASCII.name(), CharEncoding.US_ASCII);
        Assertions.assertEquals(StandardCharsets.UTF_8.name(), CharEncoding.UTF_8);
        Assertions.assertEquals(StandardCharsets.UTF_16.name(), CharEncoding.UTF_16);
        Assertions.assertEquals(StandardCharsets.UTF_16BE.name(), CharEncoding.UTF_16BE);
        Assertions.assertEquals(StandardCharsets.UTF_16LE.name(), CharEncoding.UTF_16LE);
    }
}

