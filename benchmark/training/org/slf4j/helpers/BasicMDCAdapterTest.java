/**
 * Copyright (c) 2004-2013 QOS.ch, Copyright (C) 2015 Google Inc.
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
package org.slf4j.helpers;


import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.spi.MDCAdapter;


/**
 * Tests for {@link BasicMDCAdapter}
 *
 * @author Lukasz Cwik
 */
public class BasicMDCAdapterTest {
    MDCAdapter mdc = new BasicMDCAdapter();

    @Test
    public void testSettingAndGettingWithMDC() {
        Assert.assertNull(mdc.get("testKey"));
        mdc.put("testKey", "testValue");
        Assert.assertEquals(mdc.get("testKey"), "testValue");
    }

    @Test
    public void testOverwritingAKeyInMDC() {
        Assert.assertNull(mdc.get("testKey"));
        mdc.put("testKey", "testValue");
        mdc.put("testKey", "differentTestValue");
        Assert.assertEquals(mdc.get("testKey"), "differentTestValue");
    }

    @Test
    public void testClearingMDC() {
        mdc.put("testKey", "testValue");
        Assert.assertFalse(mdc.getCopyOfContextMap().isEmpty());
        mdc.clear();
        Assert.assertNull(mdc.getCopyOfContextMap());
    }

    @Test
    public void testGetCopyOfContextMapFromMDC() {
        mdc.put("testKey", "testValue");
        Map<String, String> copy = mdc.getCopyOfContextMap();
        mdc.put("anotherTestKey", "anotherTestValue");
        Assert.assertFalse(((copy.size()) == (mdc.getCopyOfContextMap().size())));
    }

    @Test
    public void testMDCInheritsValuesFromParentThread() throws Exception {
        mdc.put("parentKey", "parentValue");
        runAndWait(new Runnable() {
            public void run() {
                mdc.put("childKey", "childValue");
                Assert.assertEquals("parentValue", mdc.get("parentKey"));
            }
        });
    }

    @Test
    public void testMDCDoesntGetValuesFromChildThread() throws Exception {
        mdc.put("parentKey", "parentValue");
        runAndWait(new Runnable() {
            public void run() {
                mdc.put("childKey", "childValue");
            }
        });
        Assert.assertEquals("parentValue", mdc.get("parentKey"));
        Assert.assertNull(mdc.get("childKey"));
    }

    @Test
    public void testMDCChildThreadCanOverwriteParentThread() throws Exception {
        mdc.put("sharedKey", "parentValue");
        runAndWait(new Runnable() {
            public void run() {
                Assert.assertEquals("parentValue", mdc.get("sharedKey"));
                mdc.put("sharedKey", "childValue");
                Assert.assertEquals("childValue", mdc.get("sharedKey"));
            }
        });
        Assert.assertEquals("parentValue", mdc.get("sharedKey"));
    }

    /**
     * A {@link UncaughtExceptionHandler} that records whether the thread threw an exception.
     */
    private static class RecordingExceptionHandler implements Thread.UncaughtExceptionHandler {
        private Throwable exception;

        public void uncaughtException(Thread t, Throwable e) {
            exception = e;
        }

        boolean hadException() {
            return (exception) != null;
        }

        String getMessage() {
            return (exception) != null ? exception.getMessage() : "";
        }
    }
}

