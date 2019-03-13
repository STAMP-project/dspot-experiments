/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.exec;


import java.io.InputStream;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.io.IOUtils;
import org.junit.Test;


/**
 * Tests the functionality of the {@link org.apache.camel.component.exec.ExecComponent}, executing<br>
 * <i>java org.apache.camel.component.exec.ExecutableJavaProgram</i> <br>
 * command. <b>Note, that the tests assume, that the JAVA_HOME system variable
 * is set.</b> This is a more credible assumption, than assuming that java is in
 * the path, because the Maven scripts build the path to java with the JAVA_HOME
 * environment variable.
 *
 * @see {@link org.apache.camel.component.exec.ExecutableJavaProgram}
 */
public class ExecJavaProcessRecipientListTest extends CamelTestSupport {
    private static final String EXECUTABLE_PROGRAM_ARG = ExecutableJavaProgram.class.getName();

    @Produce(uri = "direct:input")
    private ProducerTemplate producerTemplate;

    @EndpointInject(uri = "mock:output")
    private MockEndpoint output;

    @Test
    public void testExecJavaProcessExitCode0() throws Exception {
        output.setExpectedMessageCount(1);
        output.expectedHeaderReceived(ExecBinding.EXEC_EXIT_VALUE, 0);
        sendExchange(ExecutableJavaProgram.EXIT_WITH_VALUE_0, ExecEndpoint.NO_TIMEOUT);
        output.assertIsSatisfied();
    }

    @Test
    public void testExecJavaProcessExitCode1Direct() throws Exception {
        Exchange out = sendExchange("direct:direct", ExecutableJavaProgram.EXIT_WITH_VALUE_1, ExecEndpoint.NO_TIMEOUT);
        assertNotNull(out);
        assertEquals(1, out.getIn().getHeader(ExecBinding.EXEC_EXIT_VALUE));
    }

    @Test
    public void testExecJavaProcessExitCode1() throws Exception {
        output.setExpectedMessageCount(1);
        output.expectedHeaderReceived(ExecBinding.EXEC_EXIT_VALUE, 1);
        sendExchange(ExecutableJavaProgram.EXIT_WITH_VALUE_1, ExecEndpoint.NO_TIMEOUT);
        output.assertIsSatisfied();
    }

    @Test
    public void testExecJavaProcessStdout() throws Exception {
        String commandArgument = ExecutableJavaProgram.PRINT_IN_STDOUT;
        output.setExpectedMessageCount(1);
        output.expectedHeaderReceived(ExecBinding.EXEC_EXIT_VALUE, 0);
        Exchange e = sendExchange(commandArgument, ExecEndpoint.NO_TIMEOUT);
        ExecResult inBody = e.getIn().getBody(ExecResult.class);
        output.assertIsSatisfied();
        assertEquals(ExecutableJavaProgram.PRINT_IN_STDOUT, IOUtils.toString(inBody.getStdout()));
    }

    @Test
    public void testConvertResultToString() throws Exception {
        String commandArgument = ExecutableJavaProgram.PRINT_IN_STDOUT;
        output.setExpectedMessageCount(1);
        Exchange e = sendExchange(commandArgument, ExecEndpoint.NO_TIMEOUT);
        output.assertIsSatisfied();
        String out = e.getIn().getBody(String.class);
        assertEquals(ExecutableJavaProgram.PRINT_IN_STDOUT, out);
    }

    @Test
    public void testByteArrayInputStreamIsResetInConverter() throws Exception {
        String commandArgument = ExecutableJavaProgram.PRINT_IN_STDOUT;
        output.setExpectedMessageCount(1);
        Exchange e = sendExchange(commandArgument, ExecEndpoint.NO_TIMEOUT);
        String out1 = e.getIn().getBody(String.class);
        // the second conversion should not need a reset, this is handled
        // in the type converter.
        String out2 = e.getIn().getBody(String.class);
        output.assertIsSatisfied();
        assertEquals(ExecutableJavaProgram.PRINT_IN_STDOUT, out1);
        assertEquals(out1, out2);
    }

    @Test
    public void testIfStdoutIsNullStderrIsReturnedInConverter() throws Exception {
        // this will be printed
        String commandArgument = ExecutableJavaProgram.PRINT_IN_STDERR;
        output.setExpectedMessageCount(1);
        Exchange e = sendExchange(commandArgument, ExecEndpoint.NO_TIMEOUT, null, true);
        ExecResult body = e.getIn().getBody(ExecResult.class);
        output.assertIsSatisfied();
        assertNull("the test executable must not print anything in stdout", body.getStdout());
        assertNotNull("the test executable must print in stderr", body.getStderr());
        // the converter must fall back to the stderr, because stdout is null
        String stderr = e.getIn().getBody(String.class);
        assertEquals(ExecutableJavaProgram.PRINT_IN_STDERR, stderr);
    }

    @Test
    public void testStdoutIsNull() throws Exception {
        // this will be printed
        String commandArgument = ExecutableJavaProgram.PRINT_IN_STDERR;
        output.setExpectedMessageCount(1);
        Exchange e = sendExchange(commandArgument, ExecEndpoint.NO_TIMEOUT, null, false);
        ExecResult body = e.getIn().getBody(ExecResult.class);
        output.assertIsSatisfied();
        assertNull("the test executable must not print anything in stdout", body.getStdout());
        assertNotNull("the test executable must print in stderr", body.getStderr());
        // the converter must fall back to the stderr, because stdout is null
        String out = e.getIn().getBody(String.class);
        assertEquals("Should be empty", "", out);
    }

    @Test
    public void testConvertResultToInputStream() throws Exception {
        String commandArgument = ExecutableJavaProgram.PRINT_IN_STDOUT;
        output.setExpectedMessageCount(1);
        Exchange e = sendExchange(commandArgument, ExecEndpoint.NO_TIMEOUT);
        output.assertIsSatisfied();
        InputStream out = e.getIn().getBody(InputStream.class);
        assertEquals(ExecutableJavaProgram.PRINT_IN_STDOUT, IOUtils.toString(out));
    }

    @Test
    public void testConvertResultToByteArray() throws Exception {
        String commandArgument = ExecutableJavaProgram.PRINT_IN_STDOUT;
        output.setExpectedMessageCount(1);
        Exchange e = sendExchange(commandArgument, ExecEndpoint.NO_TIMEOUT);
        output.assertIsSatisfied();
        byte[] out = e.getIn().getBody(byte[].class);
        assertNotNull(out);
        assertEquals(ExecutableJavaProgram.PRINT_IN_STDOUT, new String(out));
    }

    /**
     * Test print in stdout from threads.
     */
    @Test
    public void testExecJavaProcessThreads() throws Exception {
        output.setExpectedMessageCount(1);
        Exchange exchange = sendExchange(ExecutableJavaProgram.THREADS, ExecEndpoint.NO_TIMEOUT);
        String err = IOUtils.toString(exchange.getIn().getHeader(ExecBinding.EXEC_STDERR, InputStream.class));
        ExecResult result = exchange.getIn().getBody(ExecResult.class);
        String[] outs = IOUtils.toString(result.getStdout()).split(IOUtils.LINE_SEPARATOR);
        String[] errs = err.split(IOUtils.LINE_SEPARATOR);
        output.assertIsSatisfied();
        assertEquals(ExecutableJavaProgram.LINES_TO_PRINT_FROM_EACH_THREAD, outs.length);
        assertEquals(ExecutableJavaProgram.LINES_TO_PRINT_FROM_EACH_THREAD, errs.length);
    }

    /**
     * Test if the process will be terminate in about a second
     */
    @Test
    public void testExecJavaProcessTimeout() throws Exception {
        int killAfterMillis = 1000;
        output.setExpectedMessageCount(1);
        // add some tolerance
        output.setResultMinimumWaitTime(800);
        // max (the test program sleeps 60 000)
        output.setResultWaitTime(30000);
        sendExchange(ExecutableJavaProgram.SLEEP_WITH_TIMEOUT, killAfterMillis);
        output.assertIsSatisfied();
    }

    /**
     * Test reading of input lines from the executable's stdin
     */
    @Test
    public void testExecJavaProcessInputLines() throws Exception {
        final StringBuilder builder = new StringBuilder();
        int lines = 10;
        for (int t = 1; t < lines; t++) {
            builder.append((("Line" + t) + (IOUtils.LINE_SEPARATOR)));
        }
        String whiteSpaceSeparatedLines = builder.toString();
        String expected = builder.toString();
        Exchange e = sendExchange(ExecutableJavaProgram.READ_INPUT_LINES_AND_PRINT_THEM, 20000, whiteSpaceSeparatedLines, false);
        ExecResult inBody = e.getIn().getBody(ExecResult.class);
        assertEquals(expected, IOUtils.toString(inBody.getStdout()));
    }
}

