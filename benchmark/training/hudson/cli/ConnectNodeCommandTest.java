/**
 * The MIT License
 *
 * Copyright 2015 Red Hat, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
/**
 *
 *
 * @author pjanouse
 */
package hudson.cli;


import CLICommandInvoker.Result;
import Computer.CONNECT;
import Jenkins.READ;
import hudson.model.Computer;
import hudson.slaves.DumbSlave;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static CLICommand.CLI_LISTPARAM_SUMMARY_ERROR_TEXT;
import static Matcher.failedWith;
import static Matcher.hasNoStandardOutput;
import static Matcher.succeededSilently;


public class ConnectNodeCommandTest {
    private CLICommandInvoker command;

    @Rule
    public final JenkinsRule j = new JenkinsRule();

    @Test
    public void connectNodeShouldFailWithoutComputerConnectPermission() throws Exception {
        j.createSlave("aNode", "", null);
        final CLICommandInvoker.Result result = command.authorizedTo(READ).invokeWithArgs("aNode");
        MatcherAssert.assertThat(result, failedWith(6));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: user is missing the Agent/Connect permission"));
        MatcherAssert.assertThat(result.stderr(), Matchers.not(Matchers.containsString(("ERROR: " + (CLI_LISTPARAM_SUMMARY_ERROR_TEXT)))));
    }

    @Test
    public void connectNodeShouldFailIfNodeDoesNotExist() throws Exception {
        final CLICommandInvoker.Result result = command.authorizedTo(CONNECT, READ).invokeWithArgs("never_created");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such agent \"never_created\" exists."));
        MatcherAssert.assertThat(result.stderr(), Matchers.not(Matchers.containsString(("ERROR: " + (CLI_LISTPARAM_SUMMARY_ERROR_TEXT)))));
    }

    @Test
    public void connectNodeShouldSucceed() throws Exception {
        DumbSlave slave = j.createSlave("aNode", "", null);
        CLICommandInvoker.Result result = command.authorizedTo(CONNECT, READ).invokeWithArgs("aNode");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(true));
        result = command.authorizedTo(CONNECT, READ).invokeWithArgs("aNode");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(true));
        slave.toComputer().disconnect();
        slave.toComputer().waitUntilOffline();
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(false));
        MatcherAssert.assertThat(slave.toComputer().isOffline(), Matchers.equalTo(true));
        result = command.authorizedTo(CONNECT, READ).invokeWithArgs("aNode");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(true));
    }

    @Test
    public void connectNodeShouldSucceedWithForce() throws Exception {
        DumbSlave slave = j.createSlave("aNode", "", null);
        slave.toComputer().connect(false).get();// avoid a race condition in the test

        CLICommandInvoker.Result result = command.authorizedTo(CONNECT, READ).invokeWithArgs("-f", "aNode");
        MatcherAssert.assertThat(slave.toComputer().getLog(), result, succeededSilently());
        MatcherAssert.assertThat(slave.toComputer().getLog(), slave.toComputer().isOnline(), Matchers.equalTo(true));
        result = command.authorizedTo(CONNECT, READ).invokeWithArgs("-f", "aNode");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(true));
        slave.toComputer().disconnect();
        slave.toComputer().waitUntilOffline();
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(false));
        MatcherAssert.assertThat(slave.toComputer().isOffline(), Matchers.equalTo(true));
        result = command.authorizedTo(CONNECT, READ).invokeWithArgs("-f", "aNode");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(true));
    }

    @Test
    public void connectNodeManyShouldSucceed() throws Exception {
        DumbSlave slave1 = j.createSlave("aNode1", "", null);
        DumbSlave slave2 = j.createSlave("aNode2", "", null);
        DumbSlave slave3 = j.createSlave("aNode3", "", null);
        final CLICommandInvoker.Result result = command.authorizedTo(CONNECT, READ).invokeWithArgs("aNode1", "aNode2", "aNode3");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(slave1.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave2.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave3.toComputer().isOnline(), Matchers.equalTo(true));
    }

    @Test
    public void connectNodeManyShouldFailIfANodeDoesNotExist() throws Exception {
        DumbSlave slave1 = j.createSlave("aNode1", "", null);
        DumbSlave slave2 = j.createSlave("aNode2", "", null);
        final CLICommandInvoker.Result result = command.authorizedTo(CONNECT, READ).invokeWithArgs("aNode1", "aNode2", "never_created");
        MatcherAssert.assertThat(result, failedWith(5));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("never_created: No such agent \"never_created\" exists. Did you mean \"aNode1\"?"));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString(("ERROR: " + (CLI_LISTPARAM_SUMMARY_ERROR_TEXT))));
        MatcherAssert.assertThat(slave1.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave2.toComputer().isOnline(), Matchers.equalTo(true));
    }

    @Test
    public void connectNodeManyShouldSucceedEvenANodeIsSpecifiedTwice() throws Exception {
        DumbSlave slave1 = j.createSlave("aNode1", "", null);
        DumbSlave slave2 = j.createSlave("aNode2", "", null);
        final CLICommandInvoker.Result result = command.authorizedTo(CONNECT, READ).invokeWithArgs("aNode1", "aNode2", "aNode1");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(slave1.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave2.toComputer().isOnline(), Matchers.equalTo(true));
    }

    @Test
    public void connectNodeShouldSucceedOnMaster() throws Exception {
        final Computer masterComputer = j.jenkins.getActiveInstance().getComputer("");
        CLICommandInvoker.Result result = command.authorizedTo(CONNECT, READ).invokeWithArgs("");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(masterComputer.isOnline(), Matchers.equalTo(true));
        result = command.authorizedTo(CONNECT, READ).invokeWithArgs("");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(masterComputer.isOnline(), Matchers.equalTo(true));
    }
}

