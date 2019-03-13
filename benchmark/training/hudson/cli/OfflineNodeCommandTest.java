/**
 * The MIT License
 *
 * Copyright 2016 Red Hat, Inc.
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
import Computer.DISCONNECT;
import Jenkins.READ;
import OfflineCause.ByCLI;
import hudson.model.Computer;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.slaves.DumbSlave;
import hudson.slaves.OfflineCause;
import hudson.util.OneShotEvent;
import java.util.concurrent.Future;
import jenkins.model.Jenkins;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static CLICommand.CLI_LISTPARAM_SUMMARY_ERROR_TEXT;
import static Matcher.failedWith;
import static Matcher.hasNoStandardOutput;
import static Matcher.succeededSilently;


public class OfflineNodeCommandTest {
    private CLICommandInvoker command;

    @Rule
    public final JenkinsRule j = new JenkinsRule();

    @Test
    public void offlineNodeShouldFailWithoutComputerDisconnectPermission() throws Exception {
        j.createSlave("aNode", "", null);
        final CLICommandInvoker.Result result = command.authorizedTo(READ).invokeWithArgs("aNode");
        MatcherAssert.assertThat(result, failedWith(6));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: user is missing the Agent/Disconnect permission"));
        MatcherAssert.assertThat(result.stderr(), Matchers.not(Matchers.containsString(("ERROR: " + (CLI_LISTPARAM_SUMMARY_ERROR_TEXT)))));
    }

    @Test
    public void offlineNodeShouldFailIfNodeDoesNotExist() throws Exception {
        final CLICommandInvoker.Result result = command.authorizedTo(DISCONNECT, READ).invokeWithArgs("never_created");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such agent \"never_created\" exists."));
        MatcherAssert.assertThat(result.stderr(), Matchers.not(Matchers.containsString(("ERROR: " + (CLI_LISTPARAM_SUMMARY_ERROR_TEXT)))));
    }

    @Test
    public void offlineNodeShouldSucceed() throws Exception {
        DumbSlave slave = j.createSlave("aNode", "", null);
        MatcherAssert.assertThat(slave.toComputer().getOfflineCause(), Matchers.equalTo(null));
        final CLICommandInvoker.Result result = command.authorizedTo(DISCONNECT, READ).invokeWithArgs("aNode");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(slave.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().isTemporarilyOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (slave.toComputer().getOfflineCause())).message, Matchers.equalTo(null));
    }

    @Test
    public void offlineNodeShouldSucceedOnOnlineNode() throws Exception {
        DumbSlave slave = j.createSlave("aNode", "", null);
        slave.toComputer().waitUntilOnline();
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().isTemporarilyOffline(), Matchers.equalTo(false));
        MatcherAssert.assertThat(slave.toComputer().getOfflineCause(), Matchers.equalTo(null));
        final CLICommandInvoker.Result result = command.authorizedTo(DISCONNECT, READ).invokeWithArgs("aNode");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(slave.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().isTemporarilyOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (slave.toComputer().getOfflineCause())).message, Matchers.equalTo(null));
    }

    @Test
    public void offlineNodeShouldSucceedOnOfflineNode() throws Exception {
        DumbSlave slave = j.createSlave("aNode", "", null);
        slave.toComputer().setTemporarilyOffline(true, null);
        MatcherAssert.assertThat(slave.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().isTemporarilyOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().getOfflineCause(), Matchers.equalTo(null));
        final CLICommandInvoker.Result result = command.authorizedTo(DISCONNECT, READ).invokeWithArgs("aNode");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(slave.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().isTemporarilyOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (slave.toComputer().getOfflineCause())).message, Matchers.equalTo(null));
    }

    @Test
    public void offlineNodeShouldSucceedOnDisconnectedNode() throws Exception {
        DumbSlave slave = j.createSlave("aNode", "", null);
        slave.toComputer().waitUntilOnline();
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().getOfflineCause(), Matchers.equalTo(null));
        slave.toComputer().disconnect();
        slave.toComputer().waitUntilOffline();
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(false));
        MatcherAssert.assertThat(slave.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().isTemporarilyOffline(), Matchers.equalTo(false));
        final CLICommandInvoker.Result result = command.authorizedTo(DISCONNECT, READ).invokeWithArgs("aNode");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(false));
        MatcherAssert.assertThat(slave.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().isTemporarilyOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (slave.toComputer().getOfflineCause())).message, Matchers.equalTo(null));
    }

    @Test
    public void offlineNodeShouldSucceedWithCause() throws Exception {
        DumbSlave slave = j.createSlave("aNode", "", null);
        MatcherAssert.assertThat(slave.toComputer().getOfflineCause(), Matchers.equalTo(null));
        final CLICommandInvoker.Result result = command.authorizedTo(DISCONNECT, READ).invokeWithArgs("aNode", "-m", "aCause");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(slave.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().isTemporarilyOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (slave.toComputer().getOfflineCause())).message, Matchers.equalTo("aCause"));
    }

    @Test
    public void offlineNodeShouldSucceedOnOnlineNodeWithCause() throws Exception {
        DumbSlave slave = j.createSlave("aNode", "", null);
        slave.toComputer().waitUntilOnline();
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().isTemporarilyOffline(), Matchers.equalTo(false));
        MatcherAssert.assertThat(slave.toComputer().getOfflineCause(), Matchers.equalTo(null));
        final CLICommandInvoker.Result result = command.authorizedTo(DISCONNECT, READ).invokeWithArgs("aNode", "-m", "aCause");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(slave.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().isTemporarilyOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (slave.toComputer().getOfflineCause())).message, Matchers.equalTo("aCause"));
    }

    @Test
    public void offlineNodeShouldSucceedOnOfflineNodeWithCause() throws Exception {
        DumbSlave slave = j.createSlave("aNode", "", null);
        slave.toComputer().setTemporarilyOffline(true, null);
        MatcherAssert.assertThat(slave.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().isTemporarilyOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().getOfflineCause(), Matchers.equalTo(null));
        final CLICommandInvoker.Result result = command.authorizedTo(DISCONNECT, READ).invokeWithArgs("aNode", "-m", "aCause");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(slave.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().isTemporarilyOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (slave.toComputer().getOfflineCause())).message, Matchers.equalTo("aCause"));
    }

    @Test
    public void offlineNodeShouldSucceedOnDisconnectedNodeWithCause() throws Exception {
        DumbSlave slave = j.createSlave("aNode", "", null);
        slave.toComputer().waitUntilOnline();
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().getOfflineCause(), Matchers.equalTo(null));
        slave.toComputer().disconnect();
        slave.toComputer().waitUntilOffline();
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(false));
        MatcherAssert.assertThat(slave.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().isTemporarilyOffline(), Matchers.equalTo(false));
        final CLICommandInvoker.Result result = command.authorizedTo(DISCONNECT, READ).invokeWithArgs("aNode", "-m", "aCause");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(slave.toComputer().isOnline(), Matchers.equalTo(false));
        MatcherAssert.assertThat(slave.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().isTemporarilyOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (slave.toComputer().getOfflineCause())).message, Matchers.equalTo("aCause"));
    }

    @Test
    public void offlineNodeShouldSucceedOnBuildingNode() throws Exception {
        final OneShotEvent finish = new OneShotEvent();
        DumbSlave slave = j.createSlave("aNode", "", null);
        slave.toComputer().waitUntilOnline();
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        project.setAssignedNode(slave);
        final Future<FreeStyleBuild> build = OnlineNodeCommandTest.startBlockingAndFinishingBuild(project, finish);
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        final CLICommandInvoker.Result result = command.authorizedTo(DISCONNECT, READ).invokeWithArgs("aNode");
        MatcherAssert.assertThat(result, succeededSilently());
        slave.toComputer().waitUntilOffline();
        MatcherAssert.assertThat(slave.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().isTemporarilyOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (slave.toComputer().getOfflineCause())).message, Matchers.equalTo(null));
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        MatcherAssert.assertThat(project.isBuilding(), Matchers.equalTo(true));
        finish.signal();
        build.get();
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        MatcherAssert.assertThat(project.isBuilding(), Matchers.equalTo(false));
        j.assertBuildStatusSuccess(build);
    }

    @Test
    public void offlineNodeShouldSucceedOnBuildingNodeWithCause() throws Exception {
        final OneShotEvent finish = new OneShotEvent();
        DumbSlave slave = j.createSlave("aNode", "", null);
        slave.toComputer().waitUntilOnline();
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        project.setAssignedNode(slave);
        final Future<FreeStyleBuild> build = OnlineNodeCommandTest.startBlockingAndFinishingBuild(project, finish);
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        final CLICommandInvoker.Result result = command.authorizedTo(DISCONNECT, READ).invokeWithArgs("aNode", "-m", "aCause");
        MatcherAssert.assertThat(result, succeededSilently());
        slave.toComputer().waitUntilOffline();
        MatcherAssert.assertThat(slave.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().isTemporarilyOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave.toComputer().getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (slave.toComputer().getOfflineCause())).message, Matchers.equalTo("aCause"));
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        MatcherAssert.assertThat(project.isBuilding(), Matchers.equalTo(true));
        finish.signal();
        build.get();
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        MatcherAssert.assertThat(project.isBuilding(), Matchers.equalTo(false));
        j.assertBuildStatusSuccess(build);
    }

    @Test
    public void offlineNodeManyShouldSucceed() throws Exception {
        DumbSlave slave1 = j.createSlave("aNode1", "", null);
        DumbSlave slave2 = j.createSlave("aNode2", "", null);
        DumbSlave slave3 = j.createSlave("aNode3", "", null);
        slave1.toComputer().waitUntilOnline();
        MatcherAssert.assertThat(slave1.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave1.toComputer().getOfflineCause(), Matchers.equalTo(null));
        slave2.toComputer().waitUntilOnline();
        MatcherAssert.assertThat(slave2.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave2.toComputer().getOfflineCause(), Matchers.equalTo(null));
        slave3.toComputer().waitUntilOnline();
        MatcherAssert.assertThat(slave3.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave3.toComputer().getOfflineCause(), Matchers.equalTo(null));
        final CLICommandInvoker.Result result = command.authorizedTo(DISCONNECT, READ).invokeWithArgs("aNode1", "aNode2", "aNode3");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(slave1.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave1.toComputer().getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (slave1.toComputer().getOfflineCause())).message, Matchers.equalTo(null));
        MatcherAssert.assertThat(slave2.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave2.toComputer().getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (slave2.toComputer().getOfflineCause())).message, Matchers.equalTo(null));
        MatcherAssert.assertThat(slave3.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave3.toComputer().getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (slave3.toComputer().getOfflineCause())).message, Matchers.equalTo(null));
    }

    @Test
    public void offlineNodeManyShouldSucceedWithCause() throws Exception {
        DumbSlave slave1 = j.createSlave("aNode1", "", null);
        DumbSlave slave2 = j.createSlave("aNode2", "", null);
        DumbSlave slave3 = j.createSlave("aNode3", "", null);
        slave1.toComputer().waitUntilOnline();
        MatcherAssert.assertThat(slave1.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave1.toComputer().getOfflineCause(), Matchers.equalTo(null));
        slave2.toComputer().waitUntilOnline();
        MatcherAssert.assertThat(slave2.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave2.toComputer().getOfflineCause(), Matchers.equalTo(null));
        slave3.toComputer().waitUntilOnline();
        MatcherAssert.assertThat(slave3.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave3.toComputer().getOfflineCause(), Matchers.equalTo(null));
        final CLICommandInvoker.Result result = command.authorizedTo(DISCONNECT, READ).invokeWithArgs("aNode1", "aNode2", "aNode3", "-m", "aCause");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(slave1.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave1.toComputer().getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (slave1.toComputer().getOfflineCause())).message, Matchers.equalTo("aCause"));
        MatcherAssert.assertThat(slave2.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave2.toComputer().getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (slave2.toComputer().getOfflineCause())).message, Matchers.equalTo("aCause"));
        MatcherAssert.assertThat(slave3.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave3.toComputer().getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (slave3.toComputer().getOfflineCause())).message, Matchers.equalTo("aCause"));
    }

    @Test
    public void offlineNodeManyShouldFailIfANodeDoesNotExist() throws Exception {
        DumbSlave slave1 = j.createSlave("aNode1", "", null);
        DumbSlave slave2 = j.createSlave("aNode2", "", null);
        slave1.toComputer().waitUntilOnline();
        MatcherAssert.assertThat(slave1.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave1.toComputer().getOfflineCause(), Matchers.equalTo(null));
        slave2.toComputer().waitUntilOnline();
        MatcherAssert.assertThat(slave2.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave2.toComputer().getOfflineCause(), Matchers.equalTo(null));
        final CLICommandInvoker.Result result = command.authorizedTo(DISCONNECT, READ).invokeWithArgs("aNode1", "aNode2", "never_created");
        MatcherAssert.assertThat(result, failedWith(5));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("never_created: No such agent \"never_created\" exists. Did you mean \"aNode1\"?"));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString(("ERROR: " + (CLI_LISTPARAM_SUMMARY_ERROR_TEXT))));
        MatcherAssert.assertThat(slave1.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave1.toComputer().getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (slave1.toComputer().getOfflineCause())).message, Matchers.equalTo(null));
        MatcherAssert.assertThat(slave2.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave2.toComputer().getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (slave2.toComputer().getOfflineCause())).message, Matchers.equalTo(null));
    }

    @Test
    public void offlineNodeManyShouldFailIfANodeDoesNotExistWithCause() throws Exception {
        DumbSlave slave1 = j.createSlave("aNode1", "", null);
        DumbSlave slave2 = j.createSlave("aNode2", "", null);
        slave1.toComputer().waitUntilOnline();
        MatcherAssert.assertThat(slave1.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave1.toComputer().getOfflineCause(), Matchers.equalTo(null));
        slave2.toComputer().waitUntilOnline();
        MatcherAssert.assertThat(slave2.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave2.toComputer().getOfflineCause(), Matchers.equalTo(null));
        final CLICommandInvoker.Result result = command.authorizedTo(DISCONNECT, READ).invokeWithArgs("aNode1", "aNode2", "never_created", "-m", "aCause");
        MatcherAssert.assertThat(result, failedWith(5));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("never_created: No such agent \"never_created\" exists. Did you mean \"aNode1\"?"));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString(("ERROR: " + (CLI_LISTPARAM_SUMMARY_ERROR_TEXT))));
        MatcherAssert.assertThat(slave1.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave1.toComputer().getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (slave1.toComputer().getOfflineCause())).message, Matchers.equalTo("aCause"));
        MatcherAssert.assertThat(slave2.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave2.toComputer().getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (slave2.toComputer().getOfflineCause())).message, Matchers.equalTo("aCause"));
    }

    @Test
    public void offlineNodeManyShouldSucceedEvenANodeIsSpecifiedTwice() throws Exception {
        DumbSlave slave1 = j.createSlave("aNode1", "", null);
        DumbSlave slave2 = j.createSlave("aNode2", "", null);
        slave1.toComputer().waitUntilOnline();
        MatcherAssert.assertThat(slave1.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave1.toComputer().getOfflineCause(), Matchers.equalTo(null));
        slave2.toComputer().waitUntilOnline();
        MatcherAssert.assertThat(slave2.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave2.toComputer().getOfflineCause(), Matchers.equalTo(null));
        final CLICommandInvoker.Result result = command.authorizedTo(DISCONNECT, READ).invokeWithArgs("aNode1", "aNode2", "aNode1");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(slave1.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave1.toComputer().getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (slave1.toComputer().getOfflineCause())).message, Matchers.equalTo(null));
        MatcherAssert.assertThat(slave2.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave2.toComputer().getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (slave2.toComputer().getOfflineCause())).message, Matchers.equalTo(null));
    }

    @Test
    public void offlineNodeManyShouldSucceedEvenANodeIsSpecifiedTwiceWithCause() throws Exception {
        DumbSlave slave1 = j.createSlave("aNode1", "", null);
        DumbSlave slave2 = j.createSlave("aNode2", "", null);
        slave1.toComputer().waitUntilOnline();
        MatcherAssert.assertThat(slave1.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave1.toComputer().getOfflineCause(), Matchers.equalTo(null));
        slave2.toComputer().waitUntilOnline();
        MatcherAssert.assertThat(slave2.toComputer().isOnline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave2.toComputer().getOfflineCause(), Matchers.equalTo(null));
        final CLICommandInvoker.Result result = command.authorizedTo(DISCONNECT, READ).invokeWithArgs("aNode1", "aNode2", "aNode1", "-m", "aCause");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(slave1.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave1.toComputer().getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (slave1.toComputer().getOfflineCause())).message, Matchers.equalTo("aCause"));
        MatcherAssert.assertThat(slave2.toComputer().isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(slave2.toComputer().getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (slave2.toComputer().getOfflineCause())).message, Matchers.equalTo("aCause"));
    }

    @Test
    public void offlineNodeShouldSucceedOnMaster() throws Exception {
        final Computer masterComputer = Jenkins.getActiveInstance().getComputer("");
        final CLICommandInvoker.Result result = command.authorizedTo(DISCONNECT, READ).invokeWithArgs("");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(masterComputer.isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(masterComputer.isTemporarilyOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(masterComputer.getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (masterComputer.getOfflineCause())).message, Matchers.equalTo(null));
    }

    @Test
    public void offlineNodeShouldSucceedOnMasterWithCause() throws Exception {
        final Computer masterComputer = Jenkins.getActiveInstance().getComputer("");
        final CLICommandInvoker.Result result = command.authorizedTo(DISCONNECT, READ).invokeWithArgs("", "-m", "aCause");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(masterComputer.isOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(masterComputer.isTemporarilyOffline(), Matchers.equalTo(true));
        MatcherAssert.assertThat(masterComputer.getOfflineCause(), Matchers.instanceOf(ByCLI.class));
        MatcherAssert.assertThat(((OfflineCause.ByCLI) (masterComputer.getOfflineCause())).message, Matchers.equalTo("aCause"));
    }
}

