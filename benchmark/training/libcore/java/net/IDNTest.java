/**
 * Copyright (C) 2010 The Android Open Source Project
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
package libcore.java.net;


import java.net.IDN;
import junit.framework.TestCase;


public class IDNTest extends TestCase {
    public void test_toUnicode_failures() {
        // This is short enough to work...
        TestCase.assertEquals("b\u00fccher", IDN.toUnicode(IDNTest.makePunyString(0)));
        // This is too long, and the RI just returns the input string...
        String longInput = IDNTest.makePunyString(512);
        TestCase.assertEquals(longInput, IDN.toUnicode(longInput));
    }
}

