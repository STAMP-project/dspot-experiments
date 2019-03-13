/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.log;


import Log4Json.DATE;
import Log4Json.EXCEPTION_CLASS;
import Log4Json.LEVEL;
import Log4Json.NAME;
import Log4Json.STACK;
import Log4Json.TIME;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import java.io.IOException;
import java.io.StringWriter;
import java.net.NoRouteToHostException;
import java.util.Enumeration;
import java.util.Vector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.util.Time;
import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.HierarchyEventListener;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.ThrowableInformation;
import org.junit.Assert;
import org.junit.Test;


public class TestLog4Json {
    private static final Log LOG = LogFactory.getLog(TestLog4Json.class);

    @Test
    public void testConstruction() throws Throwable {
        Log4Json l4j = new Log4Json();
        String outcome = l4j.toJson(new StringWriter(), "name", 0, "DEBUG", "thread1", "hello, world", null).toString();
        println("testConstruction", outcome);
    }

    @Test
    public void testException() throws Throwable {
        Exception e = new NoRouteToHostException("that box caught fire 3 years ago");
        ThrowableInformation ti = new ThrowableInformation(e);
        Log4Json l4j = new Log4Json();
        long timeStamp = Time.now();
        String outcome = l4j.toJson(new StringWriter(), "testException", timeStamp, "INFO", "quoted\"", "new line\n and {}", ti).toString();
        println("testException", outcome);
    }

    @Test
    public void testNestedException() throws Throwable {
        Exception e = new NoRouteToHostException("that box caught fire 3 years ago");
        Exception ioe = new IOException("Datacenter problems", e);
        ThrowableInformation ti = new ThrowableInformation(ioe);
        Log4Json l4j = new Log4Json();
        long timeStamp = Time.now();
        String outcome = l4j.toJson(new StringWriter(), "testNestedException", timeStamp, "INFO", "quoted\"", "new line\n and {}", ti).toString();
        println("testNestedException", outcome);
        ContainerNode rootNode = Log4Json.parse(outcome);
        assertEntryEquals(rootNode, LEVEL, "INFO");
        assertEntryEquals(rootNode, NAME, "testNestedException");
        assertEntryEquals(rootNode, TIME, timeStamp);
        assertEntryEquals(rootNode, EXCEPTION_CLASS, ioe.getClass().getName());
        JsonNode node = assertNodeContains(rootNode, STACK);
        Assert.assertTrue(("Not an array: " + node), node.isArray());
        node = assertNodeContains(rootNode, DATE);
        Assert.assertTrue(("Not a string: " + node), node.isTextual());
        // rather than try and make assertions about the format of the text
        // message equalling another ISO date, this test asserts that the hypen
        // and colon characters are in the string.
        String dateText = node.textValue();
        Assert.assertTrue(("No '-' in " + dateText), dateText.contains("-"));
        Assert.assertTrue(("No '-' in " + dateText), dateText.contains(":"));
    }

    /**
     * Create a log instance and and log to it
     *
     * @throws Throwable
     * 		if it all goes wrong
     */
    @Test
    public void testLog() throws Throwable {
        String message = "test message";
        Throwable throwable = null;
        String json = logOut(message, throwable);
        println("testLog", json);
    }

    /**
     * Create a log instance and and log to it
     *
     * @throws Throwable
     * 		if it all goes wrong
     */
    @Test
    public void testLogExceptions() throws Throwable {
        String message = "test message";
        Throwable inner = new IOException("Directory / not found");
        Throwable throwable = new IOException("startup failure", inner);
        String json = logOut(message, throwable);
        println("testLogExceptions", json);
    }

    /**
     * This test logger avoids integrating with the main runtimes Logger hierarchy
     * in ways the reader does not want to know.
     */
    private static class TestLogger extends Logger {
        private TestLogger(String name, LoggerRepository repo) {
            super(name);
            repository = repo;
            setLevel(Level.INFO);
        }
    }

    public static class TestLoggerRepository implements LoggerRepository {
        @Override
        public void addHierarchyEventListener(HierarchyEventListener listener) {
        }

        @Override
        public boolean isDisabled(int level) {
            return false;
        }

        @Override
        public void setThreshold(Level level) {
        }

        @Override
        public void setThreshold(String val) {
        }

        @Override
        public void emitNoAppenderWarning(Category cat) {
        }

        @Override
        public Level getThreshold() {
            return Level.ALL;
        }

        @Override
        public Logger getLogger(String name) {
            return new TestLog4Json.TestLogger(name, this);
        }

        @Override
        public Logger getLogger(String name, LoggerFactory factory) {
            return new TestLog4Json.TestLogger(name, this);
        }

        @Override
        public Logger getRootLogger() {
            return new TestLog4Json.TestLogger("root", this);
        }

        @Override
        public Logger exists(String name) {
            return null;
        }

        @Override
        public void shutdown() {
        }

        @Override
        public Enumeration getCurrentLoggers() {
            return new Vector().elements();
        }

        @Override
        public Enumeration getCurrentCategories() {
            return new Vector().elements();
        }

        @Override
        public void fireAddAppenderEvent(Category logger, Appender appender) {
        }

        @Override
        public void resetConfiguration() {
        }
    }
}

