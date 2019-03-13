/**
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Kohsuke Kawaguchi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.util;


import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class LineEndNormalizingWriterTest {
    @Test
    public void test1() throws IOException {
        StringWriter sw = new StringWriter();
        Writer w = new LineEndNormalizingWriter(sw);
        w.write("abc\r\ndef\r");
        w.write("\n");
        Assert.assertEquals(sw.toString(), "abc\r\ndef\r\n");
    }

    @Test
    public void test2() throws IOException {
        StringWriter sw = new StringWriter();
        Writer w = new LineEndNormalizingWriter(sw);
        w.write("abc\ndef\n");
        w.write("\n");
        Assert.assertEquals(sw.toString(), "abc\r\ndef\r\n\r\n");
    }

    @Test
    public void test3() throws IOException {
        StringWriter sw = new StringWriter();
        Writer w = new LineEndNormalizingWriter(sw);
        w.write("\r\n\n");
        Assert.assertEquals(sw.toString(), "\r\n\r\n");
    }
}

