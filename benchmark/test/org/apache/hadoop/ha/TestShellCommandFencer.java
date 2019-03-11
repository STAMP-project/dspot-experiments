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
package org.apache.hadoop.ha;


import ShellCommandFencer.LOG;
import com.google.common.collect.Lists;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ha.HAServiceProtocol.HAServiceState;
import org.apache.hadoop.util.Shell;
import org.apache.hadoop.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;

import static ShellCommandFencer.LOG;


public class TestShellCommandFencer {
    private ShellCommandFencer fencer = TestShellCommandFencer.createFencer();

    private static final HAServiceTarget TEST_TARGET = new DummyHAService(HAServiceState.ACTIVE, new InetSocketAddress("dummyhost", 1234));

    private static final Logger LOG = LOG;

    /**
     * Test that the exit code of the script determines
     * whether the fencer succeeded or failed
     */
    @Test
    public void testBasicSuccessFailure() {
        Assert.assertTrue(fencer.tryFence(TestShellCommandFencer.TEST_TARGET, "echo"));
        Assert.assertFalse(fencer.tryFence(TestShellCommandFencer.TEST_TARGET, "exit 1"));
        // bad path should also fail
        Assert.assertFalse(fencer.tryFence(TestShellCommandFencer.TEST_TARGET, "xxxxxxxxxxxx"));
    }

    @Test
    public void testCheckNoArgs() {
        try {
            Configuration conf = new Configuration();
            new NodeFencer(conf, "shell");
            Assert.fail("Didn't throw when passing no args to shell");
        } catch (BadFencingConfigurationException confe) {
            Assert.assertTrue(("Unexpected exception:" + (StringUtils.stringifyException(confe))), confe.getMessage().contains("No argument passed"));
        }
    }

    @Test
    public void testCheckParensNoArgs() {
        try {
            Configuration conf = new Configuration();
            new NodeFencer(conf, "shell()");
            Assert.fail("Didn't throw when passing no args to shell");
        } catch (BadFencingConfigurationException confe) {
            Assert.assertTrue(("Unexpected exception:" + (StringUtils.stringifyException(confe))), confe.getMessage().contains("Unable to parse line: 'shell()'"));
        }
    }

    /**
     * Test that lines on stdout get passed as INFO
     * level messages
     */
    @Test
    public void testStdoutLogging() {
        Assert.assertTrue(fencer.tryFence(TestShellCommandFencer.TEST_TARGET, "echo hello"));
        Mockito.verify(ShellCommandFencer.LOG).info(Mockito.endsWith("echo hello: hello"));
    }

    /**
     * Test that lines on stderr get passed as
     * WARN level log messages
     */
    @Test
    public void testStderrLogging() {
        Assert.assertTrue(fencer.tryFence(TestShellCommandFencer.TEST_TARGET, "echo hello>&2"));
        Mockito.verify(ShellCommandFencer.LOG).warn(Mockito.endsWith("echo hello>&2: hello"));
    }

    /**
     * Verify that the Configuration gets passed as
     * environment variables to the fencer.
     */
    @Test
    public void testConfAsEnvironment() {
        if (!(Shell.WINDOWS)) {
            fencer.tryFence(TestShellCommandFencer.TEST_TARGET, "echo $in_fencing_tests");
            Mockito.verify(ShellCommandFencer.LOG).info(Mockito.endsWith("echo $in...ing_tests: yessir"));
        } else {
            fencer.tryFence(TestShellCommandFencer.TEST_TARGET, "echo %in_fencing_tests%");
            Mockito.verify(ShellCommandFencer.LOG).info(Mockito.endsWith("echo %in...ng_tests%: yessir"));
        }
    }

    /**
     * Verify that information about the fencing target gets passed as
     * environment variables to the fencer.
     */
    @Test
    public void testTargetAsEnvironment() {
        if (!(Shell.WINDOWS)) {
            fencer.tryFence(TestShellCommandFencer.TEST_TARGET, "echo $target_host $target_port");
            Mockito.verify(ShellCommandFencer.LOG).info(Mockito.endsWith("echo $ta...rget_port: dummyhost 1234"));
        } else {
            fencer.tryFence(TestShellCommandFencer.TEST_TARGET, "echo %target_host% %target_port%");
            Mockito.verify(ShellCommandFencer.LOG).info(Mockito.endsWith("echo %ta...get_port%: dummyhost 1234"));
        }
    }

    /**
     * Test that we properly close off our input to the subprocess
     * such that it knows there's no tty connected. This is important
     * so that, if we use 'ssh', it won't try to prompt for a password
     * and block forever, for example.
     */
    @Test(timeout = 10000)
    public void testSubprocessInputIsClosed() {
        Assert.assertFalse(fencer.tryFence(TestShellCommandFencer.TEST_TARGET, "read"));
    }

    @Test
    public void testCommandAbbreviation() {
        Assert.assertEquals("a...f", ShellCommandFencer.abbreviate("abcdef", 5));
        Assert.assertEquals("abcdef", ShellCommandFencer.abbreviate("abcdef", 6));
        Assert.assertEquals("abcdef", ShellCommandFencer.abbreviate("abcdef", 7));
        Assert.assertEquals("a...g", ShellCommandFencer.abbreviate("abcdefg", 5));
        Assert.assertEquals("a...h", ShellCommandFencer.abbreviate("abcdefgh", 5));
        Assert.assertEquals("a...gh", ShellCommandFencer.abbreviate("abcdefgh", 6));
        Assert.assertEquals("ab...gh", ShellCommandFencer.abbreviate("abcdefgh", 7));
    }

    /**
     * An answer simply delegate some basic log methods to real LOG.
     */
    private static class LogAnswer implements Answer {
        private static final List<String> DELEGATE_METHODS = Lists.asList("error", new String[]{ "warn", "info", "debug", "trace" });

        @Override
        public Object answer(InvocationOnMock invocation) {
            String methodName = invocation.getMethod().getName();
            if (!(TestShellCommandFencer.LogAnswer.DELEGATE_METHODS.contains(methodName))) {
                return null;
            }
            try {
                String msg = invocation.getArguments()[0].toString();
                Method delegateMethod = TestShellCommandFencer.LOG.getClass().getMethod(methodName, msg.getClass());
                delegateMethod.invoke(TestShellCommandFencer.LOG, msg);
            } catch (Throwable e) {
                throw new IllegalStateException(("Unsupported delegate method: " + methodName));
            }
            return null;
        }
    }
}

