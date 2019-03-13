/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.util;


import FindClass.A_CREATE;
import FindClass.A_LOAD;
import FindClass.A_PRINTRESOURCE;
import FindClass.A_RESOURCE;
import FindClass.E_CREATE_FAILED;
import FindClass.E_LOAD_FAILED;
import FindClass.E_NOT_FOUND;
import FindClass.E_USAGE;
import FindClass.SUCCESS;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test the find class logic
 */
public class TestFindClass extends Assert {
    private static final Logger LOG = LoggerFactory.getLogger(TestFindClass.class);

    public static final String LOG4J_PROPERTIES = "log4j.properties";

    @Test
    public void testUsage() throws Throwable {
        run(E_USAGE, "org.apache.hadoop.util.TestFindClass");
    }

    @Test
    public void testFindsResource() throws Throwable {
        run(SUCCESS, A_RESOURCE, "org/apache/hadoop/util/TestFindClass.class");
    }

    @Test
    public void testFailsNoSuchResource() throws Throwable {
        run(E_NOT_FOUND, A_RESOURCE, "org/apache/hadoop/util/ThereIsNoSuchClass.class");
    }

    @Test
    public void testLoadFindsSelf() throws Throwable {
        run(SUCCESS, A_LOAD, "org.apache.hadoop.util.TestFindClass");
    }

    @Test
    public void testLoadFailsNoSuchClass() throws Throwable {
        run(E_NOT_FOUND, A_LOAD, "org.apache.hadoop.util.ThereIsNoSuchClass");
    }

    @Test
    public void testLoadWithErrorInStaticInit() throws Throwable {
        run(E_LOAD_FAILED, A_LOAD, "org.apache.hadoop.util.TestFindClass$FailInStaticInit");
    }

    @Test
    public void testCreateHandlesBadToString() throws Throwable {
        run(SUCCESS, A_CREATE, "org.apache.hadoop.util.TestFindClass$BadToStringClass");
    }

    @Test
    public void testCreatesClass() throws Throwable {
        run(SUCCESS, A_CREATE, "org.apache.hadoop.util.TestFindClass");
    }

    @Test
    public void testCreateFailsInStaticInit() throws Throwable {
        run(E_LOAD_FAILED, A_CREATE, "org.apache.hadoop.util.TestFindClass$FailInStaticInit");
    }

    @Test
    public void testCreateFailsInConstructor() throws Throwable {
        run(E_CREATE_FAILED, A_CREATE, "org.apache.hadoop.util.TestFindClass$FailInConstructor");
    }

    @Test
    public void testCreateFailsNoEmptyConstructor() throws Throwable {
        run(E_CREATE_FAILED, A_CREATE, "org.apache.hadoop.util.TestFindClass$NoEmptyConstructor");
    }

    @Test
    public void testLoadPrivateClass() throws Throwable {
        run(SUCCESS, A_LOAD, "org.apache.hadoop.util.TestFindClass$PrivateClass");
    }

    @Test
    public void testCreateFailsPrivateClass() throws Throwable {
        run(E_CREATE_FAILED, A_CREATE, "org.apache.hadoop.util.TestFindClass$PrivateClass");
    }

    @Test
    public void testCreateFailsInPrivateConstructor() throws Throwable {
        run(E_CREATE_FAILED, A_CREATE, "org.apache.hadoop.util.TestFindClass$PrivateConstructor");
    }

    @Test
    public void testLoadFindsLog4J() throws Throwable {
        run(SUCCESS, A_RESOURCE, TestFindClass.LOG4J_PROPERTIES);
    }

    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    @Test
    public void testPrintLog4J() throws Throwable {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);
        FindClass.setOutputStreams(out, System.err);
        run(SUCCESS, A_PRINTRESOURCE, TestFindClass.LOG4J_PROPERTIES);
        // here the content should be done
        out.flush();
        String body = baos.toString("UTF8");
        TestFindClass.LOG.info((((TestFindClass.LOG4J_PROPERTIES) + " =\n") + body));
        Assert.assertTrue(body.contains("Apache"));
    }

    /**
     * trigger a divide by zero fault in the static init
     */
    public static class FailInStaticInit {
        static {
            int x = 0;
            int y = 1 / x;
        }
    }

    /**
     * trigger a divide by zero fault in the constructor
     */
    public static class FailInConstructor {
        public FailInConstructor() {
            int x = 0;
            int y = 1 / x;
        }
    }

    /**
     * A class with no parameterless constructor -expect creation to fail
     */
    public static class NoEmptyConstructor {
        public NoEmptyConstructor(String text) {
        }
    }

    /**
     * This has triggers an NPE in the toString() method; checks the logging
     * code handles this.
     */
    public static class BadToStringClass {
        public BadToStringClass() {
        }

        @Override
        public String toString() {
            throw new NullPointerException("oops");
        }
    }

    /**
     * This has a private constructor
     * -creating it will trigger an IllegalAccessException
     */
    public static class PrivateClass {
        private PrivateClass() {
        }
    }

    /**
     * This has a private constructor
     * -creating it will trigger an IllegalAccessException
     */
    public static class PrivateConstructor {
        private PrivateConstructor() {
        }
    }
}

