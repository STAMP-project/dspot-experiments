/**
 * Copyright 2007 ZXing authors
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
package com.google.zxing.client.result;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link WifiParsedResult}.
 *
 * @author Vikram Aggarwal
 */
public final class WifiParsedResultTestCase extends Assert {
    @Test
    public void testNoPassword() {
        WifiParsedResultTestCase.doTest("WIFI:S:NoPassword;P:;T:;;", "NoPassword", null, "nopass");
        WifiParsedResultTestCase.doTest("WIFI:S:No Password;P:;T:;;", "No Password", null, "nopass");
    }

    @Test
    public void testWep() {
        WifiParsedResultTestCase.doTest("WIFI:S:TenChars;P:0123456789;T:WEP;;", "TenChars", "0123456789", "WEP");
        WifiParsedResultTestCase.doTest("WIFI:S:TenChars;P:abcde56789;T:WEP;;", "TenChars", "abcde56789", "WEP");
        // Non hex should not fail at this level
        WifiParsedResultTestCase.doTest("WIFI:S:TenChars;P:hellothere;T:WEP;;", "TenChars", "hellothere", "WEP");
        // Escaped semicolons
        WifiParsedResultTestCase.doTest("WIFI:S:Ten\\;\\;Chars;P:0123456789;T:WEP;;", "Ten;;Chars", "0123456789", "WEP");
        // Escaped colons
        WifiParsedResultTestCase.doTest("WIFI:S:Ten\\:\\:Chars;P:0123456789;T:WEP;;", "Ten::Chars", "0123456789", "WEP");
        // TODO(vikrama) Need a test for SB as well.
    }

    /**
     * Put in checks for the length of the password for wep.
     */
    @Test
    public void testWpa() {
        WifiParsedResultTestCase.doTest("WIFI:S:TenChars;P:wow;T:WPA;;", "TenChars", "wow", "WPA");
        WifiParsedResultTestCase.doTest("WIFI:S:TenChars;P:space is silent;T:WPA;;", "TenChars", "space is silent", "WPA");
        WifiParsedResultTestCase.doTest("WIFI:S:TenChars;P:hellothere;T:WEP;;", "TenChars", "hellothere", "WEP");
        // Escaped semicolons
        WifiParsedResultTestCase.doTest("WIFI:S:TenChars;P:hello\\;there;T:WEP;;", "TenChars", "hello;there", "WEP");
        // Escaped colons
        WifiParsedResultTestCase.doTest("WIFI:S:TenChars;P:hello\\:there;T:WEP;;", "TenChars", "hello:there", "WEP");
    }

    @Test
    public void testEscape() {
        WifiParsedResultTestCase.doTest("WIFI:T:WPA;S:test;P:my_password\\\\;;", "test", "my_password\\", "WPA");
    }
}

