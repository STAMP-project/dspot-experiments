/**
 * The MIT License
 *
 * Copyright 2013 Jesse Glick.
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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * In its own suite to minimize the chance of mucking about with other tests.
 */
public class XStream2EncodingTest {
    @SuppressWarnings("deprecation")
    @Test
    public void toXMLUnspecifiedEncoding() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XStream2 xs = new XStream2();
        String msg = "k chyb?";
        xs.toXML(new XStream2EncodingTest.Thing(msg), baos);
        byte[] ambiguousXml = baos.toByteArray();
        XStream2EncodingTest.Thing t = ((XStream2EncodingTest.Thing) (xs.fromXML(new ByteArrayInputStream(ambiguousXml))));
        Assert.assertThat(t.field, CoreMatchers.not(msg));
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        baos2.write("<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n".getBytes("UTF-8"));
        baos2.write(ambiguousXml);
        t = ((XStream2EncodingTest.Thing) (xs.fromXML(new ByteArrayInputStream(ambiguousXml))));
        Assert.assertThat(t.field, CoreMatchers.not(msg));
    }

    @Test
    public void toXMLUTF8() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XStream2 xs = new XStream2();
        String msg = "k chyb?";
        xs.toXMLUTF8(new XStream2EncodingTest.Thing(msg), baos);
        byte[] unspecifiedData = baos.toByteArray();
        XStream2EncodingTest.Thing t = ((XStream2EncodingTest.Thing) (xs.fromXML(new ByteArrayInputStream(unspecifiedData))));
        Assert.assertThat(t.field, CoreMatchers.is(msg));
    }

    public static class Thing {
        public final String field;

        Thing(String field) {
            this.field = field;
        }
    }
}

