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


import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import junit.framework.TestCase;


public class OldLogManagerTest extends TestCase {
    private static final String FOO = "LogManagerTestFoo";

    LogManager mockManager;

    LogManager manager = LogManager.getLogManager();

    Properties props;

    private static String className = OldLogManagerTest.class.getName();

    static Handler handler = null;

    public void testLogManager() {
        class TestLogManager extends LogManager {
            public TestLogManager() {
                super();
            }
        }
        TestLogManager tlm = new TestLogManager();
        TestCase.assertNotNull(tlm.toString());
    }

    /* test for method public Logger getLogger(String name)
    test covers following use cases:
    case 1: test default and valid value
    case 2: test throw NullPointerException
    case 3: test bad name
    case 4: check correct tested value
     */
    public void testGetLogger() throws Exception {
        // case 1: test default and valid value
        Logger log = new OldLogManagerTest.MockLogger(OldLogManagerTest.FOO, null);
        Logger foo = mockManager.getLogger(OldLogManagerTest.FOO);
        TestCase.assertNull("Logger should be null", foo);
        TestCase.assertTrue("logger wasn't registered successfully", mockManager.addLogger(log));
        foo = mockManager.getLogger(OldLogManagerTest.FOO);
        TestCase.assertSame("two loggers not refer to the same object", foo, log);
        TestCase.assertNull("logger foo should not haven parent", foo.getParent());
        // case 2: test throw NullPointerException
        try {
            mockManager.getLogger(null);
            TestCase.fail("get null should throw NullPointerException");
        } catch (NullPointerException e) {
        }
        // case 3: test bad name
        TestCase.assertNull("LogManager should not have logger with unforeseen name", mockManager.getLogger("bad name"));
        // case 4: check correct tested value
        Enumeration<String> enumar = mockManager.getLoggerNames();
        int i = 0;
        while (enumar.hasMoreElements()) {
            String name = enumar.nextElement();
            i++;
            TestCase.assertEquals("name logger should be equal to foreseen name", OldLogManagerTest.FOO, name);
        } 
        TestCase.assertEquals("LogManager should contain one element", 1, i);
    }

    /* test for method public Logger getLogger(String name) */
    public void testGetLogger_duplicateName() throws Exception {
        // test duplicate name
        // add logger with duplicate name has no effect
        mockManager.reset();
        Logger foo2 = new OldLogManagerTest.MockLogger(OldLogManagerTest.FOO, null);
        Logger foo3 = new OldLogManagerTest.MockLogger(OldLogManagerTest.FOO, null);
        mockManager.addLogger(foo2);
        TestCase.assertSame(foo2, mockManager.getLogger(OldLogManagerTest.FOO));
        mockManager.addLogger(foo3);
        TestCase.assertSame(foo2, mockManager.getLogger(OldLogManagerTest.FOO));
        Enumeration<String> enumar2 = mockManager.getLoggerNames();
        int i = 0;
        while (enumar2.hasMoreElements()) {
            enumar2.nextElement();
            i++;
        } 
        TestCase.assertEquals(1, i);
    }

    /* test for method public Logger getLogger(String name) */
    public void testGetLogger_hierarchy() throws Exception {
        // test hierarchy
        Logger foo = new OldLogManagerTest.MockLogger("testGetLogger_hierachy.foo", null);
        // but for non-mock LogManager, foo's parent should be root
        TestCase.assertTrue(manager.addLogger(foo));
        TestCase.assertSame(manager.getLogger(""), manager.getLogger("testGetLogger_hierachy.foo").getParent());
    }

    /* test for method public Logger getLogger(String name) */
    public void testGetLogger_nameSpace() throws Exception {
        // test name with space
        Logger foo = new OldLogManagerTest.MockLogger(OldLogManagerTest.FOO, null);
        Logger fooBeforeSpace = new OldLogManagerTest.MockLogger(((OldLogManagerTest.FOO) + " "), null);
        Logger fooAfterSpace = new OldLogManagerTest.MockLogger((" " + (OldLogManagerTest.FOO)), null);
        Logger fooWithBothSpace = new OldLogManagerTest.MockLogger(((" " + (OldLogManagerTest.FOO)) + " "), null);
        TestCase.assertTrue(mockManager.addLogger(foo));
        TestCase.assertTrue(mockManager.addLogger(fooBeforeSpace));
        TestCase.assertTrue(mockManager.addLogger(fooAfterSpace));
        TestCase.assertTrue(mockManager.addLogger(fooWithBothSpace));
        TestCase.assertSame(foo, mockManager.getLogger(OldLogManagerTest.FOO));
        TestCase.assertSame(fooBeforeSpace, mockManager.getLogger(((OldLogManagerTest.FOO) + " ")));
        TestCase.assertSame(fooAfterSpace, mockManager.getLogger((" " + (OldLogManagerTest.FOO))));
        TestCase.assertSame(fooWithBothSpace, mockManager.getLogger(((" " + (OldLogManagerTest.FOO)) + " ")));
    }

    /* test for method public void checkAccess() throws SecurityException */
    public void testCheckAccess() {
        try {
            manager.checkAccess();
        } catch (SecurityException e) {
            TestCase.fail("securityException should not be thrown");
        }
    }

    public void testReadConfiguration() throws IOException, SecurityException {
        OldLogManagerTest.MockConfigLogManager lm = new OldLogManagerTest.MockConfigLogManager();
        TestCase.assertFalse(lm.isCalled);
        lm.readConfiguration();
        TestCase.assertTrue(lm.isCalled);
    }

    public void testReadConfigurationInputStream_IOException_1parm() throws SecurityException {
        try {
            mockManager.readConfiguration(new OldLogManagerTest.MockInputStream());
            TestCase.fail("should throw IOException");
        } catch (IOException expected) {
        }
    }

    public static class MockInputStream extends InputStream {
        @Override
        public int read() throws IOException {
            throw new IOException();
        }
    }

    public static class MockLogger extends Logger {
        public MockLogger(String name, String rbName) {
            super(name, rbName);
        }
    }

    public static class MockLogManager extends LogManager {}

    public static class MockConfigLogManager extends LogManager {
        public boolean isCalled = false;

        public void readConfiguration(InputStream ins) throws IOException {
            isCalled = true;
            super.readConfiguration(ins);
        }
    }

    public static class MockHandler extends Handler {
        static int number = 0;

        public MockHandler() {
            addNumber();
        }

        private synchronized void addNumber() {
            (OldLogManagerTest.MockHandler.number)++;
        }

        public void close() {
            minusNumber();
        }

        private synchronized void minusNumber() {
            (OldLogManagerTest.MockHandler.number)--;
        }

        public void flush() {
        }

        public void publish(LogRecord record) {
        }
    }
}

