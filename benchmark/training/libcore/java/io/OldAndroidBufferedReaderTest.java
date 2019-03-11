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
package libcore.java.io;


import java.io.BufferedReader;
import java.io.StringReader;
import junit.framework.TestCase;


/**
 * Tests to verify that simple functionality works for BufferedReaders.
 */
public class OldAndroidBufferedReaderTest extends TestCase {
    public void testBufferedReader() throws Exception {
        String str = "AbCdEfGhIjKlMnOpQrStUvWxYz";
        StringReader aa = new StringReader(str);
        StringReader ba = new StringReader(str);
        StringReader ca = new StringReader(str);
        StringReader da = new StringReader(str);
        BufferedReader a = new BufferedReader(aa, 5);
        try {
            TestCase.assertEquals(str, OldAndroidBufferedReaderTest.read(a));
        } finally {
            a.close();
        }
        BufferedReader b = new BufferedReader(ba, 15);
        try {
            TestCase.assertEquals("AbCdEfGhIj", OldAndroidBufferedReaderTest.read(b, 10));
        } finally {
            b.close();
        }
        BufferedReader c = new BufferedReader(ca);
        try {
            TestCase.assertEquals("bdfhjlnprtvxz", OldAndroidBufferedReaderTest.skipRead(c));
        } finally {
            c.close();
        }
        BufferedReader d = new BufferedReader(da);
        try {
            TestCase.assertEquals("AbCdEfGdEfGhIjKlMnOpQrStUvWxYz", OldAndroidBufferedReaderTest.markRead(d, 3, 4));
        } finally {
            d.close();
        }
    }
}

