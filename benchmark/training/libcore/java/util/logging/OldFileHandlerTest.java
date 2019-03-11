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


import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import junit.framework.TestCase;


public class OldFileHandlerTest extends TestCase {
    static LogManager manager = LogManager.getLogManager();

    static final Properties props = new Properties();

    static final String className = OldFileHandlerTest.class.getName();

    static final String SEP = File.separator;

    String HOMEPATH;

    String TEMPPATH;

    FileHandler handler;

    LogRecord r;

    public void testFileHandler() throws Exception {
        TestCase.assertEquals("character encoding is non equal to actual value", "iso-8859-1", handler.getEncoding());
        TestCase.assertNotNull("Filter is null", handler.getFilter());
        TestCase.assertNotNull("Formatter is null", handler.getFormatter());
        TestCase.assertEquals("is non equal to actual value", Level.FINE, handler.getLevel());
        TestCase.assertNotNull("ErrorManager is null", handler.getErrorManager());
        handler.publish(r);
        handler.close();
        // output 3 times, and all records left
        // append mode is true
        for (int i = 0; i < 3; i++) {
            handler = new FileHandler();
            handler.publish(r);
            handler.close();
        }
        assertFileContent((((TEMPPATH) + (OldFileHandlerTest.SEP)) + "log"), "java0.test.0", new LogRecord[]{ r, null, r, null, r, null, r }, new OldFileHandlerTest.MockFormatter());
    }

    public void testFileHandler_1params() throws Exception {
        handler = new FileHandler("%t/log/string");
        TestCase.assertEquals("character encoding is non equal to actual value", "iso-8859-1", handler.getEncoding());
        TestCase.assertNotNull("Filter is null", handler.getFilter());
        TestCase.assertNotNull("Formatter is null", handler.getFormatter());
        TestCase.assertEquals("is non equal to actual value", Level.FINE, handler.getLevel());
        TestCase.assertNotNull("ErrorManager is null", handler.getErrorManager());
        handler.publish(r);
        handler.close();
        // output 3 times, and all records left
        // append mode is true
        for (int i = 0; i < 3; i++) {
            handler = new FileHandler("%t/log/string");
            handler.publish(r);
            handler.close();
        }
        assertFileContent((((TEMPPATH) + (OldFileHandlerTest.SEP)) + "log"), "/string", new LogRecord[]{ r, null, r, null, r, null, r }, new OldFileHandlerTest.MockFormatter());
        // test if unique ids not specified, it will append at the end
        // no generation number is used
        FileHandler h = new FileHandler("%t/log/string");
        FileHandler h2 = new FileHandler("%t/log/string");
        FileHandler h3 = new FileHandler("%t/log/string");
        FileHandler h4 = new FileHandler("%t/log/string");
        h.publish(r);
        h2.publish(r);
        h3.publish(r);
        h4.publish(r);
        h.close();
        h2.close();
        h3.close();
        h4.close();
        assertFileContent((((TEMPPATH) + (OldFileHandlerTest.SEP)) + "log"), "string", h.getFormatter());
        assertFileContent((((TEMPPATH) + (OldFileHandlerTest.SEP)) + "log"), "string.1", h.getFormatter());
        assertFileContent((((TEMPPATH) + (OldFileHandlerTest.SEP)) + "log"), "string.2", h.getFormatter());
        assertFileContent((((TEMPPATH) + (OldFileHandlerTest.SEP)) + "log"), "string.3", h.getFormatter());
        // default is append mode
        FileHandler h6 = new FileHandler("%t/log/string%u.log");
        h6.publish(r);
        h6.close();
        FileHandler h7 = new FileHandler("%t/log/string%u.log");
        h7.publish(r);
        h7.close();
        try {
            assertFileContent((((TEMPPATH) + (OldFileHandlerTest.SEP)) + "log"), "string0.log", h.getFormatter());
            TestCase.fail("should assertion failed");
        } catch (Error e) {
        }
        File file = new File((((TEMPPATH) + (OldFileHandlerTest.SEP)) + "log"));
        TestCase.assertTrue("length list of file is incorrect", ((file.list().length) <= 2));
        // test unique ids
        FileHandler h8 = new FileHandler("%t/log/%ustring%u.log");
        h8.publish(r);
        FileHandler h9 = new FileHandler("%t/log/%ustring%u.log");
        h9.publish(r);
        h9.close();
        h8.close();
        assertFileContent((((TEMPPATH) + (OldFileHandlerTest.SEP)) + "log"), "0string0.log", h.getFormatter());
        assertFileContent((((TEMPPATH) + (OldFileHandlerTest.SEP)) + "log"), "1string1.log", h.getFormatter());
        file = new File((((TEMPPATH) + (OldFileHandlerTest.SEP)) + "log"));
        TestCase.assertTrue("length list of file is incorrect", ((file.list().length) <= 2));
        try {
            new FileHandler("");
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testFileHandler_2params() throws Exception {
        boolean append = false;
        do {
            append = !append;
            handler = new FileHandler("%t/log/string", append);
            TestCase.assertEquals("character encoding is non equal to actual value", "iso-8859-1", handler.getEncoding());
            TestCase.assertNotNull("Filter is null", handler.getFilter());
            TestCase.assertNotNull("Formatter is null", handler.getFormatter());
            TestCase.assertEquals("is non equal to actual value", Level.FINE, handler.getLevel());
            TestCase.assertNotNull("ErrorManager is null", handler.getErrorManager());
            handler.publish(r);
            handler.close();
            // output 3 times, and all records left
            // append mode is true
            for (int i = 0; i < 3; i++) {
                handler = new FileHandler("%t/log/string", append);
                handler.publish(r);
                handler.close();
            }
            if (append) {
                assertFileContent((((TEMPPATH) + (OldFileHandlerTest.SEP)) + "log"), "/string", new LogRecord[]{ r, null, r, null, r, null, r }, new OldFileHandlerTest.MockFormatter());
            } else {
                assertFileContent((((TEMPPATH) + (OldFileHandlerTest.SEP)) + "log"), "/string", new LogRecord[]{ r }, new OldFileHandlerTest.MockFormatter());
            }
        } while (append );
        try {
            new FileHandler("", true);
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testFileHandler_3params() throws Exception {
        int limit = 120;
        int count = 1;
        handler = new FileHandler("%t/log/string", limit, count);
        TestCase.assertEquals("character encoding is non equal to actual value", "iso-8859-1", handler.getEncoding());
        TestCase.assertNotNull("Filter is null", handler.getFilter());
        TestCase.assertNotNull("Formatter is null", handler.getFormatter());
        TestCase.assertEquals("is non equal to actual value", Level.FINE, handler.getLevel());
        TestCase.assertNotNull("ErrorManager is null", handler.getErrorManager());
        handler.publish(r);
        handler.close();
        // output 3 times, and all records left
        // append mode is true
        for (int i = 0; i < 3; i++) {
            handler = new FileHandler("%t/log/string", limit, count);
            handler.publish(r);
            handler.close();
        }
        assertFileContent((((TEMPPATH) + (OldFileHandlerTest.SEP)) + "log"), "/string", new LogRecord[]{ r, null, r, null, r, null, r }, new OldFileHandlerTest.MockFormatter());
        try {
            new FileHandler("", limit, count);
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            new FileHandler("%t/log/string", (-1), count);
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            new FileHandler("%t/log/string", limit, 0);
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testFileHandler_4params() throws Exception {
        int limit = 120;
        int count = 1;
        boolean append = false;
        do {
            append = !append;
            handler = new FileHandler("%t/log/string", limit, count, append);
            TestCase.assertEquals("character encoding is non equal to actual value", "iso-8859-1", handler.getEncoding());
            TestCase.assertNotNull("Filter is null", handler.getFilter());
            TestCase.assertNotNull("Formatter is null", handler.getFormatter());
            TestCase.assertEquals("is non equal to actual value", Level.FINE, handler.getLevel());
            TestCase.assertNotNull("ErrorManager is null", handler.getErrorManager());
            handler.publish(r);
            handler.close();
            // output 3 times, and all records left
            // append mode is true
            for (int i = 0; i < 3; i++) {
                handler = new FileHandler("%t/log/string", limit, count, append);
                handler.publish(r);
                handler.close();
            }
            if (append) {
                assertFileContent((((TEMPPATH) + (OldFileHandlerTest.SEP)) + "log"), "/string", new LogRecord[]{ r, null, r, null, r, null, r }, new OldFileHandlerTest.MockFormatter());
            } else {
                assertFileContent((((TEMPPATH) + (OldFileHandlerTest.SEP)) + "log"), "/string", new LogRecord[]{ r }, new OldFileHandlerTest.MockFormatter());
            }
        } while (append );
        try {
            new FileHandler("", limit, count, true);
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            new FileHandler("%t/log/string", (-1), count, false);
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            new FileHandler("%t/log/string", limit, 0, true);
            TestCase.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    // This test fails on RI. Doesn't parse special pattern \"%t/%h."
    public void testInvalidParams() throws IOException {
        // %t and %p parsing can add file separator automatically
        // bad directory, IOException, append
        try {
            new FileHandler("%t/baddir/multi%g", true);
            TestCase.fail("should throw IO exception");
        } catch (IOException e) {
        }
        File file = new File((((((TEMPPATH) + (OldFileHandlerTest.SEP)) + "baddir") + (OldFileHandlerTest.SEP)) + "multi0"));
        TestCase.assertFalse(file.exists());
        try {
            new FileHandler("%t/baddir/multi%g", false);
            TestCase.fail("should throw IO exception");
        } catch (IOException e) {
        }
        file = new File((((((TEMPPATH) + (OldFileHandlerTest.SEP)) + "baddir") + (OldFileHandlerTest.SEP)) + "multi0"));
        TestCase.assertFalse(file.exists());
        try {
            new FileHandler("%t/baddir/multi%g", 12, 4);
            TestCase.fail("should throw IO exception");
        } catch (IOException e) {
        }
        file = new File((((((TEMPPATH) + (OldFileHandlerTest.SEP)) + "baddir") + (OldFileHandlerTest.SEP)) + "multi0"));
        TestCase.assertFalse(file.exists());
        try {
            new FileHandler("%t/java%u", (-1), (-1));
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testPublish() throws Exception {
        LogRecord[] r = new LogRecord[]{ new LogRecord(Level.CONFIG, "msg__"), new LogRecord(Level.WARNING, "message"), new LogRecord(Level.INFO, "message for"), new LogRecord(Level.FINE, "message for test") };
        for (int i = 0; i < (r.length); i++) {
            handler = new FileHandler("%t/log/stringPublish");
            handler.publish(r[i]);
            handler.close();
            assertFileContent((((TEMPPATH) + (OldFileHandlerTest.SEP)) + "log"), "stringPublish", new LogRecord[]{ r[i] }, handler.getFormatter());
        }
    }

    public void testClose() throws Exception {
        FileHandler h = new FileHandler("%t/log/stringPublish");
        h.publish(r);
        h.close();
        assertFileContent((((TEMPPATH) + (OldFileHandlerTest.SEP)) + "log"), "stringPublish", h.getFormatter());
    }

    /* mock classes */
    public static class MockFilter implements Filter {
        public boolean isLoggable(LogRecord record) {
            return !(record.getMessage().equals("false"));
        }
    }

    public static class MockFormatter extends Formatter {
        public String format(LogRecord r) {
            if (null == r) {
                return "";
            }
            return (r.getMessage()) + " by MockFormatter\n";
        }

        public String getTail(Handler h) {
            return "tail\n";
        }

        public String getHead(Handler h) {
            return "head\n";
        }
    }
}

