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
import java.util.Properties;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.MemoryHandler;
import junit.framework.TestCase;


public class OldMemoryHandlerTest extends TestCase {
    static final LogManager manager = LogManager.getLogManager();

    static final Properties props = new Properties();

    static final String baseClassName = OldMemoryHandlerTest.class.getName();

    MemoryHandler handler;

    public void testIsLoggable() {
        TestCase.assertTrue(handler.isLoggable(new LogRecord(Level.INFO, "1")));
        TestCase.assertTrue(handler.isLoggable(new LogRecord(Level.WARNING, "2")));
        TestCase.assertTrue(handler.isLoggable(new LogRecord(Level.SEVERE, "3")));
    }

    public void testMemoryHandler() throws IOException {
        TestCase.assertNotNull("Filter should not be null", handler.getFilter());
        TestCase.assertNotNull("Formatter should not be null", handler.getFormatter());
        TestCase.assertNull("character encoding should be null", handler.getEncoding());
        TestCase.assertNotNull("ErrorManager should not be null", handler.getErrorManager());
        TestCase.assertEquals("Level should be FINE", Level.FINE, handler.getLevel());
        TestCase.assertEquals("Level should be WARNING", Level.WARNING, handler.getPushLevel());
    }

    public static class MockFilter implements Filter {
        public boolean isLoggable(LogRecord record) {
            return !(record.getMessage().equals("false"));
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
}

