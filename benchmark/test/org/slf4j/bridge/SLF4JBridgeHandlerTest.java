/**
 * Copyright (c) 2004-2011 QOS.ch
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
package org.slf4j.bridge;


import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Assert;
import org.junit.Test;

import static java.util.logging.Logger.getLogger;
import static org.apache.log4j.Level.DEBUG;
import static org.apache.log4j.Level.ERROR;
import static org.apache.log4j.Level.INFO;
import static org.apache.log4j.Level.TRACE;
import static org.apache.log4j.Level.WARN;


public class SLF4JBridgeHandlerTest {
    static String LOGGER_NAME = "yay";

    ListAppender listAppender = new ListAppender();

    Logger log4jRoot;

    java.util.logging.Logger julLogger = java.util.logging.Logger.getLogger("yay");

    @Test
    public void testSmoke() {
        SLF4JBridgeHandler.install();
        String msg = "msg";
        julLogger.info(msg);
        Assert.assertEquals(1, listAppender.list.size());
        LoggingEvent le = ((LoggingEvent) (listAppender.list.get(0)));
        Assert.assertEquals(SLF4JBridgeHandlerTest.LOGGER_NAME, le.getLoggerName());
        Assert.assertEquals(msg, le.getMessage());
        // get the location info in the event.
        // Note that this must have been computed previously
        // within an appender for the following assertion to
        // work properly
        LocationInfo li = le.getLocationInformation();
        System.out.println(li.fullInfo);
        Assert.assertEquals("SLF4JBridgeHandlerTest.java", li.getFileName());
        Assert.assertEquals("testSmoke", li.getMethodName());
    }

    @Test
    public void testLevels() {
        SLF4JBridgeHandler.install();
        String msg = "msg";
        julLogger.setLevel(Level.ALL);
        julLogger.finest(msg);
        julLogger.finer(msg);
        julLogger.fine(msg);
        julLogger.info(msg);
        julLogger.warning(msg);
        julLogger.severe(msg);
        Assert.assertEquals(6, listAppender.list.size());
        int i = 0;
        assertLevel((i++), TRACE);
        assertLevel((i++), DEBUG);
        assertLevel((i++), DEBUG);
        assertLevel((i++), INFO);
        assertLevel((i++), WARN);
        assertLevel((i++), ERROR);
    }

    @Test
    public void testLogWithResourceBundle() {
        SLF4JBridgeHandler.install();
        String resourceBundleName = "org.slf4j.bridge.testLogStrings";
        ResourceBundle bundle = ResourceBundle.getBundle(resourceBundleName);
        String resourceKey = "resource_key";
        String expectedMsg = bundle.getString(resourceKey);
        String msg = resourceKey;
        java.util.logging.Logger julResourceBundleLogger = getLogger("yay", resourceBundleName);
        julResourceBundleLogger.info(msg);
        Assert.assertEquals(1, listAppender.list.size());
        LoggingEvent le = ((LoggingEvent) (listAppender.list.get(0)));
        Assert.assertEquals(SLF4JBridgeHandlerTest.LOGGER_NAME, le.getLoggerName());
        Assert.assertEquals(expectedMsg, le.getMessage());
    }

    @Test
    public void testLogWithResourceBundleWithParameters() {
        SLF4JBridgeHandler.install();
        String resourceBundleName = "org.slf4j.bridge.testLogStrings";
        ResourceBundle bundle = ResourceBundle.getBundle(resourceBundleName);
        java.util.logging.Logger julResourceBundleLogger = getLogger("foo", resourceBundleName);
        String resourceKey1 = "resource_key_1";
        String expectedMsg1 = bundle.getString(resourceKey1);
        julResourceBundleLogger.info(resourceKey1);// 1st log

        String resourceKey2 = "resource_key_2";
        Object[] params2 = new Object[]{ "foo", "bar" };
        String expectedMsg2 = MessageFormat.format(bundle.getString(resourceKey2), params2);
        julResourceBundleLogger.log(Level.INFO, resourceKey2, params2);// 2nd log

        String resourceKey3 = "invalidKey {0}";
        Object[] params3 = new Object[]{ "John" };
        String expectedMsg3 = MessageFormat.format(resourceKey3, params3);
        julResourceBundleLogger.log(Level.INFO, resourceKey3, params3);// 3rd log

        julLogger.log(Level.INFO, resourceKey3, params3);// 4th log

        Assert.assertEquals(4, listAppender.list.size());
        LoggingEvent le = null;
        le = ((LoggingEvent) (listAppender.list.get(0)));
        Assert.assertEquals("foo", le.getLoggerName());
        Assert.assertEquals(expectedMsg1, le.getMessage());
        le = ((LoggingEvent) (listAppender.list.get(1)));
        Assert.assertEquals("foo", le.getLoggerName());
        Assert.assertEquals(expectedMsg2, le.getMessage());
        le = ((LoggingEvent) (listAppender.list.get(2)));
        Assert.assertEquals("foo", le.getLoggerName());
        Assert.assertEquals(expectedMsg3, le.getMessage());
        le = ((LoggingEvent) (listAppender.list.get(3)));
        Assert.assertEquals("yay", le.getLoggerName());
        Assert.assertEquals(expectedMsg3, le.getMessage());
    }

    @Test
    public void testLogWithPlaceholderNoParameters() {
        SLF4JBridgeHandler.install();
        String msg = "msg {non-number-string}";
        julLogger.logp(Level.INFO, "SLF4JBridgeHandlerTest", "testLogWithPlaceholderNoParameters", msg, new Object[0]);
        Assert.assertEquals(1, listAppender.list.size());
        LoggingEvent le = ((LoggingEvent) (listAppender.list.get(0)));
        Assert.assertEquals(SLF4JBridgeHandlerTest.LOGGER_NAME, le.getLoggerName());
        Assert.assertEquals(msg, le.getMessage());
    }

    // See http://jira.qos.ch/browse/SLF4J-337
    @Test
    public void illFormattedInputShouldBeReturnedAsIs() {
        SLF4JBridgeHandler.install();
        String msg = "foo {18=bad} {0}";
        julLogger.log(Level.INFO, msg, "ignored parameter due to IllegalArgumentException");
        Assert.assertEquals(1, listAppender.list.size());
        LoggingEvent le = ((LoggingEvent) (listAppender.list.get(0)));
        Assert.assertEquals(msg, le.getMessage());
    }

    @Test
    public void withNullMessage() {
        SLF4JBridgeHandler.install();
        String msg = null;
        julLogger.log(Level.INFO, msg);
        Assert.assertEquals(1, listAppender.list.size());
        LoggingEvent le = ((LoggingEvent) (listAppender.list.get(0)));
        Assert.assertEquals("", le.getMessage());
    }
}

