/**
 * This file is part of the Heritrix web crawler (crawler.archive.org).
 *
 *  Licensed to the Internet Archive (IA) by one or more individual
 *  contributors.
 *
 *  The IA licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.archive.util;


import junit.framework.TestCase;


/**
 * JUnit test suite for PaddingStringBuffer
 *
 * @author <a href="mailto:me@jamesc.net">James Casey</a>
 * @version $Id$
 */
public class PaddingStringBufferTest extends TestCase {
    /**
     * Create a new PaddingStringBufferTest object
     *
     * @param testName
     * 		the name of the test
     */
    public PaddingStringBufferTest(final String testName) {
        super(testName);
    }

    /**
     * first check that padTo works ok, since all depends on it
     */
    public void testPadTo() {
        PaddingStringBuffer retBuf;
        TestCase.assertEquals("nothing in buffer", "", buf.toString());
        retBuf = buf.padTo(5);
        TestCase.assertEquals("retBuf same as buf", retBuf, buf);
        TestCase.assertEquals("5 spaces", "     ", buf.toString());
        // now do a smaller value - nothing should happen
        buf.padTo(4);
        TestCase.assertEquals("5 spaces", "     ", buf.toString());
        // now pad tro a greater length
        buf.padTo(10);
        TestCase.assertEquals("10 spaces", "          ", buf.toString());
    }

    /**
     * test that append(String) works correctly
     */
    public void testAppendString() {
        // a buf to hold the return buffer
        PaddingStringBuffer retBuf;
        TestCase.assertEquals("nothing in buffer", "", buf.toString());
        retBuf = buf.append("foo");
        TestCase.assertEquals("foo in buffer", "foo", buf.toString());
        TestCase.assertEquals("retBuf good", retBuf.toString(), buf.toString());
        retBuf = buf.append("bar");
        TestCase.assertEquals("foobar in buffer", "foobar", buf.toString());
        TestCase.assertEquals("retBuf good", retBuf.toString(), buf.toString());
    }

    /**
     * check the reset method clears the buffer
     */
    public void testReset() {
        // append something into the buffer
        TestCase.assertEquals("nothing in buffer", "", buf.toString());
        buf.append("foo");
        TestCase.assertEquals("buffer is 'foo'", "foo", buf.toString());
        buf.reset();
        TestCase.assertEquals("nothing in buffer after reset", "", buf.toString());
    }

    /**
     * test the raAppend(String) works in the simple cases
     */
    public void testRaAppend() {
        // a buf to hold the return buffer
        PaddingStringBuffer retBuf;
        TestCase.assertEquals("nothing in buffer", "", buf.toString());
        retBuf = buf.raAppend(5, "foo");
        TestCase.assertEquals("foo in buffer", "  foo", buf.toString());
        TestCase.assertEquals("retBuf good", retBuf.toString(), buf.toString());
        retBuf = buf.raAppend(9, "bar");
        TestCase.assertEquals("foobar in buffer", "  foo bar", buf.toString());
        TestCase.assertEquals("retBuf good", retBuf.toString(), buf.toString());
        // now check with out-of-range columns - should just append
        buf = new PaddingStringBuffer();
        buf.raAppend((-1), "foo");
        TestCase.assertEquals("no padding for -1", "foo", buf.toString());
        buf = new PaddingStringBuffer();
        buf.raAppend(0, "foo");
        TestCase.assertEquals("no padding for 0", "foo", buf.toString());
    }

    /**
     * test the newline()
     */
    public void testNewline() {
        TestCase.assertEquals("nothing should be in the buffer", "", buf.toString());
        buf.newline();
        TestCase.assertTrue("should contain newline", ((buf.toString().indexOf('\n')) != (-1)));
        TestCase.assertEquals("line position should be 0", 0, buf.linePos);
    }

    /**
     * check what happens when we right append, but the string is longer
     * than the space
     */
    public void testRaAppendWithTooLongString() {
        buf.raAppend(3, "foobar");
        TestCase.assertEquals("no padding when padding col less than string length", "foobar", buf.toString());
        buf.reset();
    }

    /**
     * check it all works with the length == the length of the string
     */
    public void testRaAppendWithExactLengthString() {
        buf.raAppend(6, "foobar");
        buf.raAppend(12, "foobar");
        TestCase.assertEquals("no padding with exact length string", "foobarfoobar", buf.toString());
    }

    /**
     * check that append(int) works
     */
    public void testAppendInt() {
        buf.append(((int) (1)));
        TestCase.assertEquals("buffer is '1'", "1", buf.toString());
        buf.append(((int) (234)));
        TestCase.assertEquals("buffer is '1234'", "1234", buf.toString());
    }

    /**
     * check that raAppend(int) works
     */
    public void testRaAppendInt() {
        // right-append '1' to column 5
        buf.raAppend(5, ((int) (1)));
        TestCase.assertEquals("buf is '    1'", "    1", buf.toString());
        // try appending a too-long int
        buf.raAppend(6, ((int) (123)));
        TestCase.assertEquals("'123' appended", "    1123", buf.toString());
    }

    /**
     * check that  append(long) works
     */
    public void testAppendLong() {
        buf.append(((long) (1)));
        TestCase.assertEquals("buffer is '1'", "1", buf.toString());
        buf.append(((long) (234)));
        TestCase.assertEquals("buffer is '1234'", "1234", buf.toString());
    }

    /**
     * check that raAppend(long) works
     */
    public void testRaAppendLong() {
        // right-append '1' to column 5
        buf.raAppend(5, ((long) (1)));
        TestCase.assertEquals("buf is '    1'", "    1", buf.toString());
        // try appending a too-long int
        buf.raAppend(6, ((long) (123)));
        TestCase.assertEquals("'123' appended", "    1123", buf.toString());
    }

    /**
     * a temp buffer for testing with
     */
    private PaddingStringBuffer buf;
}

