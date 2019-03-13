/**
 * Copyright (C) 2008 The Android Open Source Project
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


/**
 * Tests for functionality of class Integer to parse integers.
 */
public class OldAndroidParseIntTest extends TestCase {
    public void testParseInt() throws Exception {
        TestCase.assertEquals(0, Integer.parseInt("0", 10));
        TestCase.assertEquals(473, Integer.parseInt("473", 10));
        TestCase.assertEquals(0, Integer.parseInt("-0", 10));
        TestCase.assertEquals((-255), Integer.parseInt("-FF", 16));
        TestCase.assertEquals(102, Integer.parseInt("1100110", 2));
        TestCase.assertEquals(2147483647, Integer.parseInt("2147483647", 10));
        TestCase.assertEquals(-2147483648, Integer.parseInt("-2147483648", 10));
        try {
            Integer.parseInt("2147483648", 10);
            TestCase.fail();
        } catch (NumberFormatException e) {
            // ok
        }
        try {
            Integer.parseInt("-2147483649", 10);
            TestCase.fail();
        } catch (NumberFormatException e) {
            // ok
        }
        // One digit too many
        try {
            Integer.parseInt("21474836470", 10);
            TestCase.fail();
        } catch (NumberFormatException e) {
            // ok
        }
        try {
            Integer.parseInt("-21474836480", 10);
            TestCase.fail();
        } catch (NumberFormatException e) {
            // ok
        }
        try {
            Integer.parseInt("21474836471", 10);
            TestCase.fail();
        } catch (NumberFormatException e) {
            // ok
        }
        try {
            Integer.parseInt("-21474836481", 10);
            TestCase.fail();
        } catch (NumberFormatException e) {
            // ok
        }
        try {
            Integer.parseInt("214748364710", 10);
            TestCase.fail();
        } catch (NumberFormatException e) {
            // ok
        }
        try {
            Integer.parseInt("-214748364811", 10);
            TestCase.fail();
        } catch (NumberFormatException e) {
            // ok
        }
        try {
            Integer.parseInt("99", 8);
            TestCase.fail();
        } catch (NumberFormatException e) {
            // ok
        }
        try {
            Integer.parseInt("Kona", 10);
            TestCase.fail();
        } catch (NumberFormatException e) {
            // ok
        }
        TestCase.assertEquals(411787, Integer.parseInt("Kona", 27));
    }
}

