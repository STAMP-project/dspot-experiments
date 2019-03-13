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


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import junit.framework.Assert;
import junit.framework.TestCase;


/**
 * Tests to verify that simple functionality works for BufferedInputStreams.
 */
public class OldAndroidBufferedInputStreamTest extends TestCase {
    public void testBufferedInputStream() throws Exception {
        String str = "AbCdEfGhIjKlM\nOpQrStUvWxYz";
        ByteArrayInputStream aa = new ByteArrayInputStream(str.getBytes());
        ByteArrayInputStream ba = new ByteArrayInputStream(str.getBytes());
        ByteArrayInputStream ca = new ByteArrayInputStream(str.getBytes());
        ByteArrayInputStream da = new ByteArrayInputStream(str.getBytes());
        ByteArrayInputStream ea = new ByteArrayInputStream(str.getBytes());
        BufferedInputStream a = new BufferedInputStream(aa, 6);
        try {
            Assert.assertEquals(str, OldAndroidBufferedInputStreamTest.read(a));
        } finally {
            a.close();
        }
        BufferedInputStream b = new BufferedInputStream(ba, 7);
        try {
            Assert.assertEquals("AbCdEfGhIj", OldAndroidBufferedInputStreamTest.read(b, 10));
        } finally {
            b.close();
        }
        BufferedInputStream c = new BufferedInputStream(ca, 9);
        try {
            TestCase.assertEquals("bdfhjl\nprtvxz", OldAndroidBufferedInputStreamTest.skipRead(c));
        } finally {
            c.close();
        }
        BufferedInputStream d = new BufferedInputStream(da, 9);
        try {
            TestCase.assertEquals('A', d.read());
            d.mark(15);
            TestCase.assertEquals('b', d.read());
            TestCase.assertEquals('C', d.read());
            d.reset();
            TestCase.assertEquals('b', d.read());
        } finally {
            d.close();
        }
        BufferedInputStream e = new BufferedInputStream(ea, 11);
        try {
            // test that we can ask for more than is present, and that we'll get
            // back only what is there.
            TestCase.assertEquals(str, OldAndroidBufferedInputStreamTest.read(e, 10000));
        } finally {
            e.close();
        }
    }
}

