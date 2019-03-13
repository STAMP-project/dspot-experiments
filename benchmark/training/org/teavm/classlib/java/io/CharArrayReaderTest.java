/**
 * Copyright 2017 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.io;


import java.io.CharArrayReader;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class CharArrayReaderTest {
    char[] hw = new char[]{ 'H', 'e', 'l', 'l', 'o', 'W', 'o', 'r', 'l', 'd' };

    CharArrayReader cr;

    @Test
    public void constructor$C() throws IOException {
        cr = new CharArrayReader(hw);
        Assert.assertTrue("Failed to create reader", cr.ready());
    }

    @Test
    public void constructor$CII() throws IOException {
        cr = new CharArrayReader(hw, 5, 5);
        Assert.assertTrue("Failed to create reader", cr.ready());
        int c = cr.read();
        Assert.assertTrue((("Created incorrect reader--returned '" + ((char) (c))) + "' instead of 'W'"), (c == 'W'));
    }

    @Test
    public void close() {
        cr = new CharArrayReader(hw);
        cr.close();
        try {
            cr.read();
            Assert.fail("Failed to throw exception on read from closed stream");
        } catch (IOException e) {
            // Expected
        }
        // No-op
        cr.close();
    }

    @Test
    public void markI() throws IOException {
        cr = new CharArrayReader(hw);
        cr.skip(5L);
        cr.mark(100);
        cr.read();
        cr.reset();
        Assert.assertEquals("Failed to mark correct position", 'W', cr.read());
    }

    @Test
    public void markSupported() {
        cr = new CharArrayReader(hw);
        Assert.assertTrue("markSupported returned false", cr.markSupported());
    }

    @Test
    public void read() throws IOException {
        cr = new CharArrayReader(hw);
        Assert.assertEquals("Read returned incorrect char", 'H', cr.read());
        cr = new CharArrayReader(new char[]{ '\u8765' });
        Assert.assertTrue("Incorrect double byte char", ((cr.read()) == '\u8765'));
    }

    @Test
    public void read$CII() throws IOException {
        char[] c = new char[11];
        cr = new CharArrayReader(hw);
        cr.read(c, 1, 10);
        Assert.assertTrue("Read returned incorrect chars", new String(c, 1, 10).equals(new String(hw, 0, 10)));
    }

    @Test
    public void ready() throws IOException {
        cr = new CharArrayReader(hw);
        Assert.assertTrue("ready returned false", cr.ready());
        cr.skip(1000);
        Assert.assertTrue("ready returned true", (!(cr.ready())));
        cr.close();
        try {
            cr.ready();
            Assert.fail("No exception 1");
        } catch (IOException e) {
            // expected
        }
        try {
            cr = new CharArrayReader(hw);
            cr.close();
            cr.ready();
            Assert.fail("No exception 2");
        } catch (IOException e) {
            // expected
        }
    }

    @Test
    public void reset() throws IOException {
        cr = new CharArrayReader(hw);
        cr.skip(5L);
        cr.mark(100);
        cr.read();
        cr.reset();
        Assert.assertEquals("Reset failed to return to marker position", 'W', cr.read());
        // Regression for HARMONY-4357
        String str = "offsetHello world!";
        char[] data = new char[str.length()];
        str.getChars(0, str.length(), data, 0);
        int offsetLength = 6;
        int length = (data.length) - offsetLength;
        CharArrayReader reader = new CharArrayReader(data, offsetLength, length);
        reader.reset();
        for (int i = 0; i < length; i++) {
            Assert.assertEquals(data[(offsetLength + i)], ((char) (reader.read())));
        }
    }

    @Test
    public void skipJ() throws IOException {
        cr = new CharArrayReader(hw);
        long skipped = cr.skip(5L);
        Assert.assertEquals("Failed to skip correct number of chars", 5L, skipped);
        Assert.assertEquals("Skip skipped wrong chars", 'W', cr.read());
    }
}

