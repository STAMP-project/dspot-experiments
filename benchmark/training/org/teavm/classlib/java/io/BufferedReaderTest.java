/**
 * Copyright 2014 Alexey Andreev.
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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class BufferedReaderTest {
    @Test
    public void readsCharacters() throws IOException {
        String str = "foo bar baz";
        BufferedReader reader = new BufferedReader(new StringReader(str));
        char[] chars = new char[100];
        int charsRead = reader.read(chars);
        Assert.assertEquals(str.length(), charsRead);
        Assert.assertEquals(str.charAt(0), chars[0]);
        Assert.assertEquals(str.charAt((charsRead - 1)), chars[(charsRead - 1)]);
        Assert.assertEquals(0, chars[charsRead]);
    }

    @Test
    public void readsCharactersOneByOne() throws IOException {
        String str = "foo";
        BufferedReader reader = new BufferedReader(new StringReader(str));
        Assert.assertEquals('f', reader.read());
        Assert.assertEquals('o', reader.read());
        Assert.assertEquals('o', reader.read());
        Assert.assertEquals((-1), reader.read());
        Assert.assertEquals((-1), reader.read());
    }

    @Test
    public void readsLine() throws IOException {
        String str = "foo\nbar\rbaz\r\nA\n\nB";
        BufferedReader reader = new BufferedReader(new StringReader(str));
        Assert.assertEquals("foo", reader.readLine());
        Assert.assertEquals("bar", reader.readLine());
        Assert.assertEquals("baz", reader.readLine());
        Assert.assertEquals("A", reader.readLine());
        Assert.assertEquals("", reader.readLine());
        Assert.assertEquals("B", reader.readLine());
        Assert.assertNull(reader.readLine());
        Assert.assertNull(reader.readLine());
    }

    @Test
    public void fillsBuffer() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; ++i) {
            sb.append(((char) (i)));
        }
        BufferedReader reader = new BufferedReader(new StringReader(sb.toString()), 101);
        char[] buffer = new char[500];
        reader.read(buffer);
        Assert.assertEquals(0, buffer[0]);
        Assert.assertEquals(1, buffer[1]);
        Assert.assertEquals(499, buffer[499]);
    }

    @Test
    public void leavesMark() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; ++i) {
            sb.append(((char) (i)));
        }
        BufferedReader reader = new BufferedReader(new StringReader(sb.toString()), 100);
        reader.skip(50);
        reader.mark(70);
        reader.skip(60);
        reader.reset();
        char[] buffer = new char[150];
        int charsRead = reader.read(buffer);
        Assert.assertEquals(150, charsRead);
        Assert.assertEquals(50, buffer[0]);
        Assert.assertEquals(51, buffer[1]);
        Assert.assertEquals(199, buffer[149]);
    }
}

