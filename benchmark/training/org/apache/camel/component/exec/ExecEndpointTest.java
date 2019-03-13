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


import ExecEndpoint.NO_TIMEOUT;
import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.util.UnsafeUriCharactersEncoder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


/**
 * Test the configuration of {@link ExecEndpoint}
 */
@ContextConfiguration
public class ExecEndpointTest extends AbstractJUnit4SpringContextTests {
    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ExecBinding customBinding;

    @Autowired
    private ExecCommandExecutor customExecutor;

    private Component component;

    @Test
    @DirtiesContext
    public void testValidComponentDescriptor() {
        Assert.assertNotNull("The Camel Exec component can not be resolved", component);
    }

    @Test
    @DirtiesContext
    public void testCreateEndpointDefaultConf() throws Exception {
        ExecEndpoint e = createExecEndpoint("exec:test");
        Assert.assertTrue(("The Camel Exec component must create instances of " + (ExecEndpoint.class.getSimpleName())), (e instanceof ExecEndpoint));
        Assert.assertNull(e.getArgs());
        Assert.assertNull(e.getWorkingDir());
        Assert.assertNull(e.getOutFile());
        Assert.assertEquals(ExecEndpoint.NO_TIMEOUT, e.getTimeout());
        Assert.assertEquals("test", e.getExecutable());
        Assert.assertNotNull(e.getBinding());
    }

    @Test
    @DirtiesContext
    public void testCreateEndpointDefaultNoTimeout() throws Exception {
        ExecEndpoint e = createExecEndpoint("exec:test");
        Assert.assertEquals(NO_TIMEOUT, e.getTimeout());
    }

    @Test
    @DirtiesContext
    public void testCreateEndpointCustomBinding() throws Exception {
        ExecEndpoint e = createExecEndpoint("exec:test?binding=#customBinding");
        Assert.assertSame("Expected is the custom customBinding reference from the application context", customBinding, e.getBinding());
    }

    @Test
    @DirtiesContext
    public void testCreateEndpointCustomCommandExecutor() throws Exception {
        ExecEndpoint e = createExecEndpoint("exec:test?commandExecutor=#customExecutor");
        Assert.assertSame("Expected is the custom customExecutor reference from the application context", customExecutor, e.getCommandExecutor());
    }

    @Test
    @DirtiesContext
    public void testCreateEndpointWithArgs() throws Exception {
        String args = "arg1 arg2 arg3";
        // Need to properly encode the URI
        ExecEndpoint e = createExecEndpoint(("exec:test?args=" + (args.replaceAll(" ", "+"))));
        Assert.assertEquals(args, e.getArgs());
    }

    @Test
    @DirtiesContext
    public void testCreateEndpointWithArgs2() throws Exception {
        String args = "arg1 \"arg2 \" arg3";
        ExecEndpoint e = createExecEndpoint(("exec:test?args=" + (UnsafeUriCharactersEncoder.encode(args))));
        Assert.assertEquals(args, e.getArgs());
    }

    @Test
    @DirtiesContext
    public void testCreateEndpointWithArgs3() throws Exception {
        String args = "RAW(arg1+arg2 arg3)";
        // Just avoid URI encoding by using the RAW()
        ExecEndpoint e = createExecEndpoint(("exec:test?args=" + args));
        Assert.assertEquals("arg1+arg2 arg3", e.getArgs());
    }

    @Test
    @DirtiesContext
    public void testCreateEndpointWithTimeout() throws Exception {
        long timeout = 1999999L;
        ExecEndpoint e = createExecEndpoint(("exec:test?timeout=" + timeout));
        Assert.assertEquals(timeout, e.getTimeout());
    }

    @Test
    @DirtiesContext
    public void testCreateEndpointWithOutFile() throws Exception {
        String outFile = "output.txt";
        ExecEndpoint e = createExecEndpoint(("exec:test?outFile=" + outFile));
        Assert.assertEquals(outFile, e.getOutFile());
    }

    @Test
    @DirtiesContext
    public void testCreateEndpointWithWorkingDir() throws Exception {
        String workingDir = "/workingdir";
        ExecEndpoint e = createExecEndpoint(("exec:test?workingDir=" + workingDir));
        Assert.assertEquals(workingDir, e.getWorkingDir());
    }

    @Test
    @DirtiesContext
    public void testCreateEndpointEscapedWorkingDir() throws Exception {
        String cmd = "temp.exe";
        String dir = "\"c:/program files/wokr/temp\"";
        String uri = (("exec:" + cmd) + "?workingDir=") + dir;
        ExecEndpoint endpoint = createExecEndpoint(UnsafeUriCharactersEncoder.encode(uri));
        Assert.assertEquals(cmd, endpoint.getExecutable());
        Assert.assertNull(endpoint.getArgs());
        Assert.assertEquals(dir, endpoint.getWorkingDir());
    }

    @Test
    @DirtiesContext
    public void testCreateEndpointEscapedCommand() throws Exception {
        String executable = "C:/Program Files/test/text.exe";
        String uri = "exec:" + executable;
        ExecEndpoint endpoint = createExecEndpoint(UnsafeUriCharactersEncoder.encode(uri));
        Assert.assertNull(endpoint.getArgs());
        Assert.assertNull(endpoint.getWorkingDir());
        Assert.assertEquals(executable, endpoint.getExecutable());
    }

    @Test
    @DirtiesContext
    public void testCreateEndpointComposite() throws Exception {
        String workingDir = "/workingdir";
        String argsEscaped = "arg1 arg2 \"arg 3\"";
        long timeout = 10000L;
        String uri = (((("exec:executable.exe?workingDir=" + workingDir) + "&timeout=") + timeout) + "&args=") + argsEscaped;
        ExecEndpoint e = createExecEndpoint(UnsafeUriCharactersEncoder.encode(uri));
        Assert.assertEquals(workingDir, e.getWorkingDir());
        Assert.assertEquals(argsEscaped, e.getArgs());
        Assert.assertEquals(timeout, e.getTimeout());
    }

    @Test
    @DirtiesContext
    public void testCreateEndpointComposite2() throws Exception {
        String workingDir = "/workingdir";
        String outFile = "target/outfile.xml";
        long timeout = 10000;
        StringBuilder builder = new StringBuilder();
        builder.append("exec:executable.exe").append(("?workingDir=" + workingDir)).append(("&timeout=" + timeout));
        builder.append(("&outFile=" + outFile));
        builder.append("&commandExecutor=#customExecutor&binding=#customBinding");
        ExecEndpoint e = createExecEndpoint(UnsafeUriCharactersEncoder.encode(builder.toString()));
        Assert.assertEquals(workingDir, e.getWorkingDir());
        Assert.assertEquals(timeout, e.getTimeout());
        Assert.assertEquals(outFile, e.getOutFile());
        Assert.assertSame(customBinding, e.getBinding());
        Assert.assertSame(customExecutor, e.getCommandExecutor());
    }
}

