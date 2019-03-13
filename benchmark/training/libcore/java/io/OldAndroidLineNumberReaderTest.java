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


import java.io.LineNumberReader;
import java.io.StringReader;
import junit.framework.Assert;
import junit.framework.TestCase;


/**
 * Checks basic functionality for LineNumberReader.
 */
public class OldAndroidLineNumberReaderTest extends TestCase {
    public void testLineNumberReader() throws Exception {
        String str = "AbCdEfGhIjKlM\nOpQrStUvWxYz";
        StringReader aa = new StringReader(str);
        StringReader ba = new StringReader(str);
        StringReader ca = new StringReader(str);
        StringReader da = new StringReader(str);
        StringReader ea = new StringReader(str);
        LineNumberReader a = new LineNumberReader(aa);
        try {
            TestCase.assertEquals(0, a.getLineNumber());
            Assert.assertEquals(str, OldAndroidLineNumberReaderTest.read(a));
            TestCase.assertEquals(1, a.getLineNumber());
            a.setLineNumber(5);
            TestCase.assertEquals(5, a.getLineNumber());
        } finally {
            a.close();
        }
        LineNumberReader b = new LineNumberReader(ba);
        try {
            Assert.assertEquals("AbCdEfGhIj", OldAndroidLineNumberReaderTest.read(b, 10));
        } finally {
            b.close();
        }
        LineNumberReader c = new LineNumberReader(ca);
        try {
            Assert.assertEquals("bdfhjl\nprtvxz", OldAndroidLineNumberReaderTest.skipRead(c));
        } finally {
            c.close();
        }
        LineNumberReader d = new LineNumberReader(da);
        try {
            Assert.assertEquals("AbCdEfGdEfGhIjKlM\nOpQrStUvWxYz", OldAndroidLineNumberReaderTest.markRead(d, 3, 4));
        } finally {
            d.close();
        }
        LineNumberReader e = new LineNumberReader(ea);
        try {
            TestCase.assertEquals("AbCdEfGhIjKlM", e.readLine());
        } finally {
            e.close();
        }
    }
}

