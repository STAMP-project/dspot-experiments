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
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Computer;
import hudson.model.Descriptor;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Node;
import hudson.remoting.VirtualChannel;
import hudson.slaves.DumbSlave;
import hudson.tasks.Builder;
import hudson.util.OneShotEvent;
import java.io.IOException;
import java.util.concurrent.Future;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestExtension;

import static CLICommand.CLI_LISTPARAM_SUMMARY_ERROR_TEXT;
import static Matcher.failedWith;
import static Matcher.hasNoStandardOutput;
import static Matcher.succeededSilently;


public class OnlineNodeCommandTest {
    private CLICommandInvoker command;

    @Rule
    public final JenkinsRule j = new JenkinsRule();

    @Test
    public void onlineNodeShouldFailWithoutComputerConnectPermission() throws Exception {
        j.createSlave("aNode", "", null);
        final CLICommandInvoker.Result result = command.authorizedTo(READ).invokeWithArgs("aNode");
        MatcherAssert.assertThat(result, failedWith(6));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: user is missing the Agent/Connect permission"));
    }

    @Test
    public void onlineNodeShouldFailIfNodeDoesNotExist() throws Exception {
        final CLICommandInvoker.Result result = command.authorizedTo(CONNECT, READ).invokeWithArgs("never_created");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such agent \"never_created\" exists."));
    }

    @Test
    public void onlineNodeShouldSucceed() throws Exception {
        DumbSlave slave = j.createSlave("aNode", "", null);
        final CLICommandInvoker.Result result = command.authorizedTo(CONNECT, READ).invokeWithArgs("aNode");
        MatcherAssert.assertThat(result, succeededSilently());
        if (slave.toComputer().isConnecting()) {
            System.out.println("Waiting until going online is in progress...");
            slave.toComputer().waitUntilOnline();
        }
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(true));
    }

    @Test
    public void onlineNodeShouldSucceedOnOnlineNode() throws Exception {
        DumbSlave slave = j.createSlave("aNode", "", null);
        if (slave.toComputer().isConnecting()) {
            System.out.println("Waiting until going online is in progress...");
            slave.toComputer().waitUntilOnline();
        }
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(true));
        final CLICommandInvoker.Result result = command.authorizedTo(CONNECT, READ).invokeWithArgs("aNode");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(true));
    }

    @Test
    public void onlineNodeShouldSucceedOnOfflineNode() throws Exception {
        DumbSlave slave = j.createSlave("aNode", "", null);
        if (slave.toComputer().isConnecting()) {
            System.out.println("Waiting until going online is in progress...");
            slave.toComputer().waitUntilOnline();
        }
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(true));
        slave.toComputer().setTemporarilyOffline(true);
        slave.toComputer().waitUntilOffline();
        MatcherAssert.assertThat(slave.toComputer().isOffline(), Matchers.equalTo(true));
        final CLICommandInvoker.Result result = command.authorizedTo(CONNECT, READ).invokeWithArgs("aNode");
        MatcherAssert.assertThat(result, succeededSilently());
        if (slave.toComputer().isConnecting()) {
            System.out.println("Waiting until going online is in progress...");
            slave.toComputer().waitUntilOnline();
        }
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(true));
    }

    @Test
    public void onlineNodeShouldSucceedOnDisconnectedNode() throws Exception {
        DumbSlave slave = j.createSlave("aNode", "", null);
        if (slave.toComputer().isConnecting()) {
            System.out.println("Waiting until going online is in progress...");
            slave.toComputer().waitUntilOnline();
        }
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(true));
        slave.toComputer().disconnect();
        slave.toComputer().waitUntilOffline();
        MatcherAssert.assertThat(slave.toComputer().isOffline(), Matchers.equalTo(true));
        final CLICommandInvoker.Result result = command.authorizedTo(CONNECT, READ).invokeWithArgs("aNode");
        MatcherAssert.assertThat(result, succeededSilently());
        if (slave.toComputer().isConnecting()) {
            System.out.println("Waiting until going online is in progress...");
            slave.toComputer().waitUntilOnline();
        }
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(false));
    }

    @Test
    public void onlineNodeShouldSucceedOnDisconnectingNode() throws Exception {
        DumbSlave slave = j.createSlave("aNode", "", null);
        if (slave.toComputer().isConnecting()) {
            System.out.println("Waiting until going online is in progress...");
            slave.toComputer().waitUntilOnline();
        }
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(true));
        slave.toComputer().disconnect();
        final CLICommandInvoker.Result result = command.authorizedTo(CONNECT, READ).invokeWithArgs("aNode");
        MatcherAssert.assertThat(result, succeededSilently());
        if (slave.toComputer().isConnecting()) {
            System.out.println("Waiting until going online is in progress...");
            slave.toComputer().waitUntilOnline();
        }
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(false));
    }

    @Test
    public void onlineNodeShouldSucceedOnBuildingOfflineNode() throws Exception {
        final OneShotEvent finish = new OneShotEvent();
        DumbSlave slave = j.createSlave("aNode", "", null);
        if (!(slave.toComputer().isOnline())) {
            System.out.println("Waiting until going online is in progress...");
            slave.toComputer().waitUntilOnline();
        }
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        project.setAssignedNode(slave);
        final Future<FreeStyleBuild> build = OnlineNodeCommandTest.startBlockingAndFinishingBuild(project, finish);
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        slave.toComputer().setTemporarilyOffline(true);
        slave.toComputer().waitUntilOffline();
        MatcherAssert.assertThat(slave.toComputer().isOffline(), Matchers.equalTo(true));
        final CLICommandInvoker.Result result = command.authorizedTo(CONNECT, READ).invokeWithArgs("aNode");
        MatcherAssert.assertThat(result, succeededSilently());
        if (slave.toComputer().isConnecting()) {
            System.out.println("Waiting until going online is in progress...");
            slave.toComputer().waitUntilOnline();
        }
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        MatcherAssert.assertThat(project.isBuilding(), Matchers.equalTo(true));
        finish.signal();
        build.get();
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        MatcherAssert.assertThat(project.isBuilding(), Matchers.equalTo(false));
        j.assertBuildStatusSuccess(build);
    }

    @Test
    public void onlineNodeManyShouldSucceed() throws Exception {
        DumbSlave slave1 = j.createSlave("aNode1", "", null);
        DumbSlave slave2 = j.createSlave("aNode2", "", null);
        DumbSlave slave3 = j.createSlave("aNode3", "", null);
        final CLICommandInvoker.Result result = command.authorizedTo(CONNECT, READ).invokeWithArgs("aNode1", "aNode2", "aNode3");
        MatcherAssert.assertThat(result, succeededSilently());
        if (slave1.toComputer().isConnecting()) {
            System.out.println("Waiting until aNode1 going online is in progress...");
            slave1.toComputer().waitUntilOnline();
        }
        if (slave2.toComputer().isConnecting()) {
            System.out.println("Waiting until aNode2 going online is in progress...");
            slave2.toComputer().waitUntilOnline();
        }
        if (slave3.toComputer().isConnecting()) {
            System.out.println("Waiting until aNode3 going online is in progress...");
            slave3.toComputer().waitUntilOnline();
        }
        MatcherAssert.assertThat(slave1.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave2.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave3.toComputer().isOnline(), Matchers.equalTo(true));
    }

    @Test
    public void onlineNodeManyShouldFailIfANodeDoesNotExist() throws Exception {
        DumbSlave slave1 = j.createSlave("aNode1", "", null);
        DumbSlave slave2 = j.createSlave("aNode2", "", null);
        final CLICommandInvoker.Result result = command.authorizedTo(CONNECT, READ).invokeWithArgs("aNode1", "aNode2", "never_created");
        MatcherAssert.assertThat(result, failedWith(5));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("never_created: No such agent \"never_created\" exists. Did you mean \"aNode1\"?"));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString(("ERROR: " + (CLI_LISTPARAM_SUMMARY_ERROR_TEXT))));
        if (slave1.toComputer().isConnecting()) {
            System.out.println("Waiting until aNode1 going online is in progress...");
            slave1.toComputer().waitUntilOnline();
        }
        if (slave2.toComputer().isConnecting()) {
            System.out.println("Waiting until aNode2 going online is in progress...");
            slave2.toComputer().waitUntilOnline();
        }
        MatcherAssert.assertThat(slave1.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave2.toComputer().isOnline(), Matchers.equalTo(true));
    }

    @Test
    public void onlineNodeManyShouldSucceedEvenANodeIsSpecifiedTwice() throws Exception {
        DumbSlave slave1 = j.createSlave("aNode1", "", null);
        DumbSlave slave2 = j.createSlave("aNode2", "", null);
        final CLICommandInvoker.Result result = command.authorizedTo(CONNECT, READ).invokeWithArgs("aNode1", "aNode2", "aNode1");
        MatcherAssert.assertThat(result, succeededSilently());
        if (slave1.toComputer().isConnecting()) {
            System.out.println("Waiting until aNode1 going online is in progress...");
            slave1.toComputer().waitUntilOnline();
        }
        if (slave2.toComputer().isConnecting()) {
            System.out.println("Waiting until aNode2 going online is in progress...");
            slave2.toComputer().waitUntilOnline();
        }
        MatcherAssert.assertThat(slave1.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave2.toComputer().isOnline(), Matchers.equalTo(true));
    }

    @Test
    public void onlineNodeShouldSucceedOnMaster() throws Exception {
        final Computer masterComputer = j.jenkins.getActiveInstance().getComputer("");
        CLICommandInvoker.Result result = command.authorizedTo(CONNECT, READ).invokeWithArgs("");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(masterComputer.isOnline(), Matchers.equalTo(true));
        result = command.authorizedTo(CONNECT, READ).invokeWithArgs("");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(masterComputer.isOnline(), Matchers.equalTo(true));
    }

    private static final class BlockingAndFinishingBuilder extends Builder {
        private final OneShotEvent block;

        private final OneShotEvent finish;

        private BlockingAndFinishingBuilder(OneShotEvent block, OneShotEvent finish) {
            this.block = block;
            this.finish = finish;
        }

        @Override
        public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
            VirtualChannel channel = launcher.getChannel();
            Node node = build.getBuiltOn();
            block.signal();// we are safe to be interrupted

            for (; ;) {
                // Go out if we should finish
                if (finish.isSignaled())
                    break;

                // Keep using the channel
                channel.call(node.getClockDifferenceCallable());
                Thread.sleep(100);
            }
            return true;
        }

        @TestExtension("disconnectCause")
        public static class DescriptorImpl extends Descriptor<Builder> {}
    }
}

