/**
 * Copyright (C) 2011 The Android Open Source Project
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
package libcore.java.lang;


import junit.framework.TestCase;


public final class IntegralToStringTest extends TestCase {
    public void test_intToHexString() {
        TestCase.assertEquals("0", IntegralToString.intToHexString(0, false, 0));
        TestCase.assertEquals("0", IntegralToString.intToHexString(0, false, 1));
        TestCase.assertEquals("00", IntegralToString.intToHexString(0, false, 2));
        TestCase.assertEquals("1", IntegralToString.intToHexString(1, false, 0));
        TestCase.assertEquals("1", IntegralToString.intToHexString(1, false, 1));
        TestCase.assertEquals("01", IntegralToString.intToHexString(1, false, 2));
        TestCase.assertEquals("ffffffff", IntegralToString.intToHexString((-1), false, 0));
    }

    public void testBytesToHexString() {
        TestCase.assertEquals("abcdef", IntegralToString.bytesToHexString(new byte[]{ ((byte) (171)), ((byte) (205)), ((byte) (239)) }, false));
        TestCase.assertEquals("ABCDEF", IntegralToString.bytesToHexString(new byte[]{ ((byte) (171)), ((byte) (205)), ((byte) (239)) }, true));
    }
}

