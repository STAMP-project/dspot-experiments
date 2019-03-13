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
package org.apache.beam.runners.dataflow.worker.logging;


import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.LogRecord;
import org.apache.beam.runners.core.metrics.ExecutionStateTracker;
import org.apache.beam.runners.dataflow.worker.DataflowOperationContext.DataflowExecutionState;
import org.apache.beam.runners.dataflow.worker.NameContextsForTests;
import org.apache.beam.runners.dataflow.worker.TestOperationContext;
import org.apache.beam.runners.dataflow.worker.testing.RestoreDataflowLoggingMDC;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Supplier;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link DataflowWorkerLoggingHandler}.
 */
@RunWith(JUnit4.class)
public class DataflowWorkerLoggingHandlerTest {
    @Rule
    public TestRule restoreMDC = new RestoreDataflowLoggingMDC();

    // Typically \n or \r\n
    private static String escapedNewline = DataflowWorkerLoggingHandlerTest.escapeNewline();

    private static class FixedOutputStreamFactory implements Supplier<OutputStream> {
        private OutputStream[] streams;

        private int next = 0;

        public FixedOutputStreamFactory(OutputStream... streams) {
            this.streams = streams;
        }

        @Override
        public OutputStream get() {
            return streams[((next)++)];
        }
    }

    private final ExecutionStateTracker tracker = ExecutionStateTracker.newForTest();

    private Closeable trackerCleanup;

    @Test
    public void testOutputStreamRollover() throws IOException {
        DataflowWorkerLoggingMDC.setJobId("testJobId");
        ByteArrayOutputStream first = new ByteArrayOutputStream();
        ByteArrayOutputStream second = new ByteArrayOutputStream();
        LogRecord record = /* throwable */
        createLogRecord("test.message", null);
        String expected = ("{\"timestamp\":{\"seconds\":0,\"nanos\":1000000},\"severity\":\"INFO\"," + ("\"message\":\"test.message\",\"thread\":\"2\",\"job\":\"testJobId\"," + "\"logger\":\"LoggerName\"}")) + (System.lineSeparator());
        DataflowWorkerLoggingHandlerTest.FixedOutputStreamFactory factory = new DataflowWorkerLoggingHandlerTest.FixedOutputStreamFactory(first, second);
        DataflowWorkerLoggingHandler handler = /* sizelimit */
        new DataflowWorkerLoggingHandler(factory, ((expected.length()) + 1));
        // Using |expected|+1 for size limit means that we will rollover after writing 2 log messages.
        // We thus expect to see 2 messsages written to 'first' and 1 message to 'second',
        handler.publish(record);
        handler.publish(record);
        handler.publish(record);
        Assert.assertEquals((expected + expected), new String(first.toByteArray(), StandardCharsets.UTF_8));
        Assert.assertEquals(expected, new String(second.toByteArray(), StandardCharsets.UTF_8));
    }

    @Test
    public void testWithUnsetValuesInMDC() throws IOException {
        DataflowWorkerLoggingMDC.setJobId("testJobId");
        Assert.assertEquals((("{\"timestamp\":{\"seconds\":0,\"nanos\":1000000},\"severity\":\"INFO\"," + ("\"message\":\"test.message\",\"thread\":\"2\",\"job\":\"testJobId\"," + "\"logger\":\"LoggerName\"}")) + (System.lineSeparator())), DataflowWorkerLoggingHandlerTest.createJson(/* throwable */
        createLogRecord("test.message", null)));
    }

    @Test
    public void testWithAllValuesInMDC() throws IOException {
        DataflowExecutionState state = new TestOperationContext.TestDataflowExecutionState(NameContextsForTests.nameContextForTest(), "activity");
        tracker.enterState(state);
        String testJobId = "testJobId";
        String testStage = "testStage";
        String testWorkerId = "testWorkerId";
        String testWorkId = "testWorkId";
        DataflowWorkerLoggingMDC.setJobId(testJobId);
        DataflowWorkerLoggingMDC.setStageName(testStage);
        DataflowWorkerLoggingMDC.setWorkerId(testWorkerId);
        DataflowWorkerLoggingMDC.setWorkId(testWorkId);
        /* throwable */
        createLogRecord("test.message", null);
        Assert.assertEquals(String.format((("{\"timestamp\":{\"seconds\":0,\"nanos\":1000000},\"severity\":\"INFO\"," + (("\"message\":\"test.message\",\"thread\":\"2\",\"job\":\"%s\"," + "\"stage\":\"%s\",\"step\":\"%s\",\"worker\":\"%s\",") + "\"work\":\"%s\",\"logger\":\"LoggerName\"}")) + (System.lineSeparator())), testJobId, testStage, NameContextsForTests.USER_NAME, testWorkerId, testWorkId), DataflowWorkerLoggingHandlerTest.createJson(/* throwable */
        createLogRecord("test.message", null)));
    }

    @Test
    public void testWithMessage() throws IOException {
        DataflowWorkerLoggingMDC.setJobId("testJobId");
        DataflowWorkerLoggingMDC.setWorkerId("testWorkerId");
        DataflowWorkerLoggingMDC.setWorkId("testWorkId");
        Assert.assertEquals((("{\"timestamp\":{\"seconds\":0,\"nanos\":1000000},\"severity\":\"INFO\"," + ("\"message\":\"test.message\",\"thread\":\"2\",\"job\":\"testJobId\"," + "\"worker\":\"testWorkerId\",\"work\":\"testWorkId\",\"logger\":\"LoggerName\"}")) + (System.lineSeparator())), DataflowWorkerLoggingHandlerTest.createJson(/* throwable */
        createLogRecord("test.message", null)));
    }

    @Test
    public void testWithMessageRequiringJulFormatting() throws IOException {
        Assert.assertEquals((("{\"timestamp\":{\"seconds\":0,\"nanos\":1000000},\"severity\":\"INFO\"," + ("\"message\":\"test.message myFormatString\",\"thread\":\"2\"," + "\"logger\":\"LoggerName\"}")) + (System.lineSeparator())), DataflowWorkerLoggingHandlerTest.createJson(/* throwable */
        createLogRecord("test.message {0}", null, "myFormatString")));
    }

    @Test
    public void testWithMessageAndException() throws IOException {
        DataflowWorkerLoggingMDC.setJobId("testJobId");
        DataflowWorkerLoggingMDC.setWorkerId("testWorkerId");
        DataflowWorkerLoggingMDC.setWorkId("testWorkId");
        Assert.assertEquals((((((((((("{\"timestamp\":{\"seconds\":0,\"nanos\":1000000},\"severity\":\"INFO\"," + (("\"message\":\"test.message\",\"thread\":\"2\",\"job\":\"testJobId\"," + "\"worker\":\"testWorkerId\",\"work\":\"testWorkId\",\"logger\":\"LoggerName\",") + "\"exception\":\"java.lang.Throwable: exception.test.message")) + (DataflowWorkerLoggingHandlerTest.escapedNewline)) + "\\tat declaringClass1.method1(file1.java:1)") + (DataflowWorkerLoggingHandlerTest.escapedNewline)) + "\\tat declaringClass2.method2(file2.java:1)") + (DataflowWorkerLoggingHandlerTest.escapedNewline)) + "\\tat declaringClass3.method3(file3.java:1)") + (DataflowWorkerLoggingHandlerTest.escapedNewline)) + "\"}") + (System.lineSeparator())), DataflowWorkerLoggingHandlerTest.createJson(createLogRecord("test.message", createThrowable())));
    }

    @Test
    public void testWithException() throws IOException {
        DataflowWorkerLoggingMDC.setJobId("testJobId");
        DataflowWorkerLoggingMDC.setWorkerId("testWorkerId");
        DataflowWorkerLoggingMDC.setWorkId("testWorkId");
        Assert.assertEquals((((((((((("{\"timestamp\":{\"seconds\":0,\"nanos\":1000000},\"severity\":\"INFO\"," + (("\"thread\":\"2\",\"job\":\"testJobId\",\"worker\":\"testWorkerId\"," + "\"work\":\"testWorkId\",\"logger\":\"LoggerName\",") + "\"exception\":\"java.lang.Throwable: exception.test.message")) + (DataflowWorkerLoggingHandlerTest.escapedNewline)) + "\\tat declaringClass1.method1(file1.java:1)") + (DataflowWorkerLoggingHandlerTest.escapedNewline)) + "\\tat declaringClass2.method2(file2.java:1)") + (DataflowWorkerLoggingHandlerTest.escapedNewline)) + "\\tat declaringClass3.method3(file3.java:1)") + (DataflowWorkerLoggingHandlerTest.escapedNewline)) + "\"}") + (System.lineSeparator())), DataflowWorkerLoggingHandlerTest.createJson(/* message */
        createLogRecord(null, createThrowable())));
    }

    @Test
    public void testWithoutExceptionOrMessage() throws IOException {
        DataflowWorkerLoggingMDC.setJobId("testJobId");
        DataflowWorkerLoggingMDC.setWorkerId("testWorkerId");
        DataflowWorkerLoggingMDC.setWorkId("testWorkId");
        Assert.assertEquals((("{\"timestamp\":{\"seconds\":0,\"nanos\":1000000},\"severity\":\"INFO\"," + ("\"thread\":\"2\",\"job\":\"testJobId\",\"worker\":\"testWorkerId\"," + "\"work\":\"testWorkId\",\"logger\":\"LoggerName\"}")) + (System.lineSeparator())), DataflowWorkerLoggingHandlerTest.createJson(/* message */
        /* throwable */
        createLogRecord(null, null)));
    }

    @Test
    public void testBeamFnApiLogEntry() throws IOException {
        DataflowWorkerLoggingMDC.setJobId("testJobId");
        DataflowWorkerLoggingMDC.setWorkerId("testWorkerId");
        DataflowWorkerLoggingMDC.setWorkId("testWorkId");
        Assert.assertEquals((("{\"timestamp\":{\"seconds\":0,\"nanos\":1000000},\"severity\":\"INFO\"," + ("\"message\":\"test.message\",\"thread\":\"2\",\"job\":\"testJobId\"," + "\"worker\":\"testWorkerId\",\"work\":\"1\",\"logger\":\"LoggerName\"}")) + (System.lineSeparator())), DataflowWorkerLoggingHandlerTest.createJson(createLogEntry("test.message")));
    }

    @Test
    public void testBeamFnApiLogEntryWithTrace() throws IOException {
        DataflowWorkerLoggingMDC.setJobId("testJobId");
        DataflowWorkerLoggingMDC.setWorkerId("testWorkerId");
        DataflowWorkerLoggingMDC.setWorkId("testWorkId");
        Assert.assertEquals((("{\"timestamp\":{\"seconds\":0,\"nanos\":1000000},\"severity\":\"INFO\"," + (("\"message\":\"test.message\",\"thread\":\"2\",\"job\":\"testJobId\"," + "\"worker\":\"testWorkerId\",\"work\":\"1\",\"logger\":\"LoggerName\",") + "\"exception\":\"testTrace\"}")) + (System.lineSeparator())), DataflowWorkerLoggingHandlerTest.createJson(createLogEntry("test.message").toBuilder().setTrace("testTrace").build()));
    }
}

