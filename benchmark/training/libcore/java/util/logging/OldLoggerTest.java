/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.util.logging;


import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import junit.framework.TestCase;


public class OldLoggerTest extends TestCase {
    private static final String VALID_RESOURCE_BUNDLE = "bundles/java/util/logging/res";

    private static final String INVALID_RESOURCE_BUNDLE = "impossible_not_existing";

    private static final String VALID_KEY = "LOGGERTEST";

    private static final String VALID_VALUE = "Test_ZH_CN";

    public void testGetLoggerWithRes_InvalidResourceBundle() {
        TestCase.assertNull(LogManager.getLogManager().getLogger("testMissingResourceException"));
        TestCase.assertNotNull(LogManager.getLogManager().getLogger(""));
        // The root logger always exists TODO
        try {
            Logger.getLogger("", OldLoggerTest.INVALID_RESOURCE_BUNDLE);
            TestCase.fail();
        } catch (MissingResourceException expected) {
        }
    }

    public void testGlobalLogger() {
        TestCase.assertNull(Logger.global.getFilter());
        TestCase.assertEquals(0, Logger.global.getHandlers().length);
        TestCase.assertNull(Logger.global.getLevel());
        TestCase.assertEquals("global", Logger.global.getName());
        TestCase.assertNull(Logger.global.getParent().getParent());
        TestCase.assertNull(Logger.global.getResourceBundle());
        TestCase.assertNull(Logger.global.getResourceBundleName());
        TestCase.assertTrue(Logger.global.getUseParentHandlers());
        TestCase.assertSame(Logger.global, Logger.getLogger("global"));
        TestCase.assertSame(Logger.global, LogManager.getLogManager().getLogger("global"));
        TestCase.assertSame(Logger.global, Logger.getGlobal());
    }

    public void testConstructor_Normal() {
        OldLoggerTest.MockLogger mlog = new OldLoggerTest.MockLogger("myname", OldLoggerTest.VALID_RESOURCE_BUNDLE);
        TestCase.assertNull(mlog.getFilter());
        TestCase.assertEquals(0, mlog.getHandlers().length);
        TestCase.assertNull(mlog.getLevel());
        TestCase.assertEquals("myname", mlog.getName());
        TestCase.assertNull(mlog.getParent());
        ResourceBundle rb = mlog.getResourceBundle();
        TestCase.assertEquals(OldLoggerTest.VALID_VALUE, rb.getString(OldLoggerTest.VALID_KEY));
        TestCase.assertEquals(mlog.getResourceBundleName(), OldLoggerTest.VALID_RESOURCE_BUNDLE);
        TestCase.assertTrue(mlog.getUseParentHandlers());
    }

    public void testConstructor_Null() {
        OldLoggerTest.MockLogger mlog = new OldLoggerTest.MockLogger(null, null);
        TestCase.assertNull(mlog.getFilter());
        TestCase.assertEquals(0, mlog.getHandlers().length);
        TestCase.assertNull(mlog.getLevel());
        TestCase.assertNull(mlog.getName());
        TestCase.assertNull(mlog.getParent());
        TestCase.assertNull(mlog.getResourceBundle());
        TestCase.assertNull(mlog.getResourceBundleName());
        TestCase.assertTrue(mlog.getUseParentHandlers());
    }

    public void testConstructor_InvalidName() {
        OldLoggerTest.MockLogger mlog = new OldLoggerTest.MockLogger("...#$%%^&&()-_+=!@~./,[]{};:\'\\\"?|", null);
        TestCase.assertEquals("...#$%%^&&()-_+=!@~./,[]{};:\'\\\"?|", mlog.getName());
    }

    /* Test constructor with empty name. */
    public void testConstructor_EmptyName() {
        OldLoggerTest.MockLogger mlog = new OldLoggerTest.MockLogger("", null);
        TestCase.assertEquals("", mlog.getName());
    }

    public void testConstructor_InvalidResourceBundle() {
        try {
            new OldLoggerTest.MockLogger("testConstructor_InvalidResourceBundle", OldLoggerTest.INVALID_RESOURCE_BUNDLE);
            TestCase.fail("Should throw MissingResourceException!");
        } catch (MissingResourceException expected) {
        }
    }

    public void testGetLogger_Null() {
        try {
            Logger.getLogger(null, null);
            TestCase.fail("Should throw NullPointerException!");
        } catch (NullPointerException expected) {
        }
    }

    public void testGetLogger_WithParent() {
        TestCase.assertNull(LogManager.getLogManager().getLogger("testGetLogger_WithParent_ParentLogger"));
        // get root of hierarchy
        Logger root = Logger.getLogger("");
        // create the parent logger
        Logger pLog = Logger.getLogger("testGetLogger_WithParent_ParentLogger", OldLoggerTest.VALID_RESOURCE_BUNDLE);
        pLog.setLevel(Level.CONFIG);
        pLog.addHandler(new OldLoggerTest.MockHandler());
        pLog.setFilter(new OldLoggerTest.MockFilter());
        pLog.setUseParentHandlers(false);
        // check root parent
        TestCase.assertEquals("testGetLogger_WithParent_ParentLogger", pLog.getName());
        TestCase.assertSame(pLog.getParent(), root);
        // child part
        TestCase.assertNull(LogManager.getLogManager().getLogger("testGetLogger_WithParent_ParentLogger.child"));
        // create the child logger
        Logger child = Logger.getLogger("testGetLogger_WithParent_ParentLogger.child");
        TestCase.assertNull(child.getFilter());
        TestCase.assertEquals(0, child.getHandlers().length);
        TestCase.assertNull(child.getLevel());
        TestCase.assertEquals("testGetLogger_WithParent_ParentLogger.child", child.getName());
        TestCase.assertSame(child.getParent(), pLog);
        TestCase.assertNull(child.getResourceBundle());
        TestCase.assertNull(child.getResourceBundleName());
        TestCase.assertTrue(child.getUseParentHandlers());
        // create not valid child
        Logger notChild = Logger.getLogger("testGetLogger_WithParent_ParentLogger1.child");
        TestCase.assertNull(notChild.getFilter());
        TestCase.assertEquals(0, notChild.getHandlers().length);
        TestCase.assertNull(notChild.getLevel());
        TestCase.assertEquals("testGetLogger_WithParent_ParentLogger1.child", notChild.getName());
        TestCase.assertNotSame(notChild.getParent(), pLog);
        TestCase.assertNull(notChild.getResourceBundle());
        TestCase.assertNull(notChild.getResourceBundleName());
        TestCase.assertTrue(notChild.getUseParentHandlers());
        // verify two level root.parent
        TestCase.assertEquals("testGetLogger_WithParent_ParentLogger.child", child.getName());
        TestCase.assertSame(child.getParent().getParent(), root);
        // create three level child
        Logger childOfChild = Logger.getLogger("testGetLogger_WithParent_ParentLogger.child.child");
        TestCase.assertNull(childOfChild.getFilter());
        TestCase.assertEquals(0, childOfChild.getHandlers().length);
        TestCase.assertSame(child.getParent().getParent(), root);
        TestCase.assertNull(childOfChild.getLevel());
        TestCase.assertEquals("testGetLogger_WithParent_ParentLogger.child.child", childOfChild.getName());
        TestCase.assertSame(childOfChild.getParent(), child);
        TestCase.assertSame(childOfChild.getParent().getParent(), pLog);
        TestCase.assertSame(childOfChild.getParent().getParent().getParent(), root);
        TestCase.assertNull(childOfChild.getResourceBundle());
        TestCase.assertNull(childOfChild.getResourceBundleName());
        TestCase.assertTrue(childOfChild.getUseParentHandlers());
        // abnormal case : lookup to root parent in a hierarchy without a logger
        // parent created between
        TestCase.assertEquals("testGetLogger_WithParent_ParentLogger1.child", notChild.getName());
        TestCase.assertSame(child.getParent().getParent(), root);
        TestCase.assertNotSame(child.getParent(), root);
        // abnormal cases
        TestCase.assertNotSame(root.getParent(), root);
        Logger twoDot = Logger.getLogger("..");
        TestCase.assertSame(twoDot.getParent(), root);
    }

    public static class MockLogger extends Logger {
        public MockLogger(String name, String resourceBundleName) {
            super(name, resourceBundleName);
        }
    }

    public static class MockHandler extends Handler {
        public void close() {
        }

        public void flush() {
        }

        public void publish(LogRecord record) {
        }
    }

    public static class MockFilter implements Filter {
        public boolean isLoggable(LogRecord record) {
            return false;
        }
    }
}

