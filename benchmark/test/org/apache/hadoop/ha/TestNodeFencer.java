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


import com.google.common.collect.Lists;
import java.util.List;
import org.apache.hadoop.conf.Configured;
import org.junit.Assert;
import org.junit.Test;


public class TestNodeFencer {
    private HAServiceTarget MOCK_TARGET;

    // Fencer shell commands that always return true on Unix and Windows
    // respectively. Lacking the POSIX 'true' command on Windows, we use
    // the batch command 'rem'.
    private static String FENCER_TRUE_COMMAND_UNIX = "shell(true)";

    private static String FENCER_TRUE_COMMAND_WINDOWS = "shell(rem)";

    @Test
    public void testSingleFencer() throws BadFencingConfigurationException {
        NodeFencer fencer = TestNodeFencer.setupFencer(((TestNodeFencer.AlwaysSucceedFencer.class.getName()) + "(foo)"));
        Assert.assertTrue(fencer.fence(MOCK_TARGET));
        Assert.assertEquals(1, TestNodeFencer.AlwaysSucceedFencer.fenceCalled);
        Assert.assertSame(MOCK_TARGET, TestNodeFencer.AlwaysSucceedFencer.fencedSvc);
        Assert.assertEquals("foo", TestNodeFencer.AlwaysSucceedFencer.callArgs.get(0));
    }

    @Test
    public void testMultipleFencers() throws BadFencingConfigurationException {
        NodeFencer fencer = TestNodeFencer.setupFencer(((((TestNodeFencer.AlwaysSucceedFencer.class.getName()) + "(foo)\n") + (TestNodeFencer.AlwaysSucceedFencer.class.getName())) + "(bar)\n"));
        Assert.assertTrue(fencer.fence(MOCK_TARGET));
        // Only one call, since the first fencer succeeds
        Assert.assertEquals(1, TestNodeFencer.AlwaysSucceedFencer.fenceCalled);
        Assert.assertEquals("foo", TestNodeFencer.AlwaysSucceedFencer.callArgs.get(0));
    }

    @Test
    public void testWhitespaceAndCommentsInConfig() throws BadFencingConfigurationException {
        NodeFencer fencer = TestNodeFencer.setupFencer(((((("\n" + (" # the next one will always fail\n" + " ")) + (TestNodeFencer.AlwaysFailFencer.class.getName())) + "(foo) # <- fails\n") + (TestNodeFencer.AlwaysSucceedFencer.class.getName())) + "(bar) \n"));
        Assert.assertTrue(fencer.fence(MOCK_TARGET));
        // One call to each, since top fencer fails
        Assert.assertEquals(1, TestNodeFencer.AlwaysFailFencer.fenceCalled);
        Assert.assertSame(MOCK_TARGET, TestNodeFencer.AlwaysFailFencer.fencedSvc);
        Assert.assertEquals(1, TestNodeFencer.AlwaysSucceedFencer.fenceCalled);
        Assert.assertSame(MOCK_TARGET, TestNodeFencer.AlwaysSucceedFencer.fencedSvc);
        Assert.assertEquals("foo", TestNodeFencer.AlwaysFailFencer.callArgs.get(0));
        Assert.assertEquals("bar", TestNodeFencer.AlwaysSucceedFencer.callArgs.get(0));
    }

    @Test
    public void testArglessFencer() throws BadFencingConfigurationException {
        NodeFencer fencer = TestNodeFencer.setupFencer(TestNodeFencer.AlwaysSucceedFencer.class.getName());
        Assert.assertTrue(fencer.fence(MOCK_TARGET));
        // One call to each, since top fencer fails
        Assert.assertEquals(1, TestNodeFencer.AlwaysSucceedFencer.fenceCalled);
        Assert.assertSame(MOCK_TARGET, TestNodeFencer.AlwaysSucceedFencer.fencedSvc);
        Assert.assertEquals(null, TestNodeFencer.AlwaysSucceedFencer.callArgs.get(0));
    }

    @Test
    public void testShortNameShell() throws BadFencingConfigurationException {
        NodeFencer fencer = TestNodeFencer.setupFencer(TestNodeFencer.getFencerTrueCommand());
        Assert.assertTrue(fencer.fence(MOCK_TARGET));
    }

    @Test
    public void testShortNameSsh() throws BadFencingConfigurationException {
        NodeFencer fencer = TestNodeFencer.setupFencer("sshfence");
        Assert.assertFalse(fencer.fence(MOCK_TARGET));
    }

    @Test
    public void testShortNameSshWithUser() throws BadFencingConfigurationException {
        NodeFencer fencer = TestNodeFencer.setupFencer("sshfence(user)");
        Assert.assertFalse(fencer.fence(MOCK_TARGET));
    }

    @Test
    public void testShortNameSshWithPort() throws BadFencingConfigurationException {
        NodeFencer fencer = TestNodeFencer.setupFencer("sshfence(:123)");
        Assert.assertFalse(fencer.fence(MOCK_TARGET));
    }

    @Test
    public void testShortNameSshWithUserPort() throws BadFencingConfigurationException {
        NodeFencer fencer = TestNodeFencer.setupFencer("sshfence(user:123)");
        Assert.assertFalse(fencer.fence(MOCK_TARGET));
    }

    /**
     * Mock fencing method that always returns true
     */
    public static class AlwaysSucceedFencer extends Configured implements FenceMethod {
        static int fenceCalled = 0;

        static HAServiceTarget fencedSvc;

        static List<String> callArgs = Lists.newArrayList();

        @Override
        public boolean tryFence(HAServiceTarget target, String args) {
            TestNodeFencer.AlwaysSucceedFencer.fencedSvc = target;
            TestNodeFencer.AlwaysSucceedFencer.callArgs.add(args);
            (TestNodeFencer.AlwaysSucceedFencer.fenceCalled)++;
            return true;
        }

        @Override
        public void checkArgs(String args) {
        }

        public static HAServiceTarget getLastFencedService() {
            return TestNodeFencer.AlwaysSucceedFencer.fencedSvc;
        }
    }

    /**
     * Identical mock to above, except always returns false
     */
    public static class AlwaysFailFencer extends Configured implements FenceMethod {
        static int fenceCalled = 0;

        static HAServiceTarget fencedSvc;

        static List<String> callArgs = Lists.newArrayList();

        @Override
        public boolean tryFence(HAServiceTarget target, String args) {
            TestNodeFencer.AlwaysFailFencer.fencedSvc = target;
            TestNodeFencer.AlwaysFailFencer.callArgs.add(args);
            (TestNodeFencer.AlwaysFailFencer.fenceCalled)++;
            return false;
        }

        @Override
        public void checkArgs(String args) {
        }
    }
}

