/**
 * Copyright (c) 2004-2012 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.slf4j.simple;


import SimpleLogger.CACHE_OUTPUT_STREAM_STRING_KEY;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Assert;
import org.junit.Test;

import static SimpleLogger.LOG_KEY_PREFIX;


public class SimpleLoggerTest {
    String A_KEY = (LOG_KEY_PREFIX) + "a";

    PrintStream original = System.out;

    ByteArrayOutputStream bout = new ByteArrayOutputStream();

    PrintStream replacement = new PrintStream(bout);

    @Test
    public void emptyLoggerName() {
        SimpleLogger simpleLogger = new SimpleLogger("a");
        Assert.assertEquals("info", simpleLogger.recursivelyComputeLevelString());
    }

    @Test
    public void offLevel() {
        System.setProperty(A_KEY, "off");
        SimpleLogger.init();
        SimpleLogger simpleLogger = new SimpleLogger("a");
        Assert.assertEquals("off", simpleLogger.recursivelyComputeLevelString());
        Assert.assertFalse(simpleLogger.isErrorEnabled());
    }

    @Test
    public void loggerNameWithNoDots_WithLevel() {
        SimpleLogger.init();
        SimpleLogger simpleLogger = new SimpleLogger("a");
        Assert.assertEquals("info", simpleLogger.recursivelyComputeLevelString());
    }

    @Test
    public void loggerNameWithOneDotShouldInheritFromParent() {
        SimpleLogger simpleLogger = new SimpleLogger("a.b");
        Assert.assertEquals("info", simpleLogger.recursivelyComputeLevelString());
    }

    @Test
    public void loggerNameWithNoDots_WithNoSetLevel() {
        SimpleLogger simpleLogger = new SimpleLogger("x");
        Assert.assertNull(simpleLogger.recursivelyComputeLevelString());
    }

    @Test
    public void loggerNameWithOneDot_NoSetLevel() {
        SimpleLogger simpleLogger = new SimpleLogger("x.y");
        Assert.assertNull(simpleLogger.recursivelyComputeLevelString());
    }

    @Test
    public void checkUseOfLastSystemStreamReference() {
        SimpleLogger.init();
        SimpleLogger simpleLogger = new SimpleLogger(this.getClass().getName());
        System.setErr(replacement);
        simpleLogger.info("hello");
        replacement.flush();
        Assert.assertTrue(bout.toString().contains((("INFO " + (this.getClass().getName())) + " - hello")));
    }

    @Test
    public void checkUseOfCachedOutputStream() {
        System.setErr(replacement);
        System.setProperty(CACHE_OUTPUT_STREAM_STRING_KEY, "true");
        SimpleLogger.init();
        SimpleLogger simpleLogger = new SimpleLogger(this.getClass().getName());
        // change reference to original before logging
        System.setErr(original);
        simpleLogger.info("hello");
        replacement.flush();
        Assert.assertTrue(bout.toString().contains((("INFO " + (this.getClass().getName())) + " - hello")));
    }
}

