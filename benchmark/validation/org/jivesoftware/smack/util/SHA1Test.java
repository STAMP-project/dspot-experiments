/**
 * Copyright 2003-2007 Jive Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.smack.util;


import org.junit.Assert;
import org.junit.Test;


/**
 * A test case for the SHA1 class.
 */
public class SHA1Test {
    @Test
    public void testHash() {
        // Test null
        // @TODO - should the StringUtils.hash(String) method be fixed to handle null input?
        try {
            SHA1.hex(((String) (null)));
            Assert.fail();
        } catch (NullPointerException npe) {
            Assert.assertTrue(true);
        }
        // Test empty String
        String result = SHA1.hex("");
        Assert.assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", result);
        // Test a known hash
        String adminInHash = "d033e22ae348aeb5660fc2140aec35850c4da997";
        result = SHA1.hex("admin");
        Assert.assertEquals(adminInHash, result);
        // Test a random String - make sure all resulting characters are valid hash characters
        // and that the returned string is 32 characters long.
        String random = "jive software blah and stuff this is pretty cool";
        result = SHA1.hex(random);
        Assert.assertTrue(isValidHash(result));
        // Test junk input:
        String junk = "\n\n\t\b\r!@(!)^(#)@+_-\u2031\u09291\u00a9\u00bd\u0394\u00f8";
        result = SHA1.hex(junk);
        Assert.assertTrue(isValidHash(result));
    }

    /* ----- Utility methods and vars ----- */
    private final String HASH_CHARS = "0123456789abcdef";
}

