/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.yarn.client;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import org.junit.Assert;
import org.junit.Test;


// The idea here is to set up a simulated client environment, then
// test each command. This is a big project.
public class TestClient {
    /**
     * Unchecked exception to allow capturing "exit" events without actually
     * exiting.
     */
    public static class SimulatedExitException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public int exitCode;

        public SimulatedExitException(int exitCode) {
            this.exitCode = exitCode;
        }
    }

    public static class TestContext extends ClientContext {
        public static ByteArrayOutputStream captureOut = new ByteArrayOutputStream();

        public static ByteArrayOutputStream captureErr = new ByteArrayOutputStream();

        public static void testInit() {
            init(new TestClient.TestContext());
            TestClient.TestContext.resetOutput();
        }

        @Override
        public void exit(int exitCode) {
            throw new TestClient.SimulatedExitException(exitCode);
        }

        public static void resetOutput() {
            try {
                out.flush();
                TestClient.TestContext.captureOut.reset();
                out = new PrintStream(TestClient.TestContext.captureOut, true, "UTF-8");
                err.flush();
                TestClient.TestContext.captureErr.reset();
                err = new PrintStream(TestClient.TestContext.captureErr, true, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
            }
        }

        public static String getOut() {
            out.flush();
            try {
                return TestClient.TestContext.captureOut.toString("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
            }
        }

        public static String getErr() {
            out.flush();
            try {
                return TestClient.TestContext.captureErr.toString("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * Test the basics of the DrillOnYarn app. Does not try any real commands, but
     * does check for the basic error conditions.
     */
    @Test
    public void testBasics() {
        TestClient.TestContext.testInit();
        // No arguments provided.
        try {
            DrillOnYarn.run(new String[]{  });
            Assert.fail();
        } catch (TestClient.SimulatedExitException e) {
            assert (e.exitCode) == (-1);
            Assert.assertTrue(TestClient.TestContext.getOut().contains("Usage: "));
            TestClient.TestContext.resetOutput();
        }
        // Bogus command
        try {
            DrillOnYarn.run(new String[]{ "bogus" });
            Assert.fail();
        } catch (TestClient.SimulatedExitException e) {
            assert (e.exitCode) == (-1);
            Assert.assertTrue(TestClient.TestContext.getOut().contains("Usage: "));
            TestClient.TestContext.resetOutput();
        }
    }
}

