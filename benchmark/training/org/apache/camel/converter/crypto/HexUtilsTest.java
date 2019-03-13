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
package org.apache.camel.converter.crypto;


import org.apache.camel.test.junit4.TestSupport;
import org.junit.Test;


public class HexUtilsTest extends TestSupport {
    byte[] array = new byte[]{ ((byte) (1)), ((byte) (35)), ((byte) (69)), ((byte) (103)), ((byte) (137)), ((byte) (171)), ((byte) (205)), ((byte) (239)) };

    @Test
    public void testByteArrayToHex() throws Exception {
        assertEquals("0123456789abcdef", HexUtils.byteArrayToHexString(array));
    }

    @Test
    public void roundtripArray() {
        assertArrayEquals(array, HexUtils.hexToByteArray(HexUtils.byteArrayToHexString(array)));
    }

    @Test
    public void roundtrip() {
        String hexchars = "01234567890abcdefABCDEF";
        for (int x = 0; x < 100000; x++) {
            int r = ((int) ((Math.random()) * 50));
            StringBuilder b = new StringBuilder(r);
            for (int y = 0; y < r; y++) {
                b.append(hexchars.charAt(((int) ((Math.random()) * (hexchars.length())))));
            }
            String hexString = b.toString().toLowerCase();
            if (((b.length()) % 2) > 0) {
                // add the padded byte if odd
                assertEquals((hexString + '0'), HexUtils.byteArrayToHexString(HexUtils.hexToByteArray(hexString)));
            } else {
                assertEquals(hexString, HexUtils.byteArrayToHexString(HexUtils.hexToByteArray(hexString)));
            }
        }
    }
}

