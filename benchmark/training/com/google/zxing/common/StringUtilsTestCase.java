/**
 * Copyright 2012 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.zxing.common;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link StringUtils}.
 */
public final class StringUtilsTestCase extends Assert {
    @Test
    public void testShortShiftJIS1() {
        // ??????
        StringUtilsTestCase.doTest(new byte[]{ ((byte) (139)), ((byte) (224)), ((byte) (139)), ((byte) (155)) }, "SJIS");
    }

    @Test
    public void testShortISO885911() {
        // b??d
        StringUtilsTestCase.doTest(new byte[]{ ((byte) (98)), ((byte) (229)), ((byte) (100)) }, "ISO-8859-1");
    }

    @Test
    public void testMixedShiftJIS1() {
        // Hello ???!
        StringUtilsTestCase.doTest(new byte[]{ ((byte) (72)), ((byte) (101)), ((byte) (108)), ((byte) (108)), ((byte) (111)), ((byte) (32)), ((byte) (139)), ((byte) (224)), ((byte) (33)) }, "SJIS");
    }
}

