/**
 * The MIT License
 *
 * Copyright 2013 Red Hat, Inc.
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
package hudson.cli;


import CLICommandInvoker.Result;
import Computer.CREATE;
import Jenkins.READ;
import hudson.model.Computer;
import hudson.model.Node;
import hudson.model.Slave;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static Matcher.failedWith;
import static Matcher.hasNoStandardOutput;
import static Matcher.succeededSilently;


public class CreateNodeCommandTest {
    private CLICommandInvoker command;

    @Rule
    public final JenkinsRule j = new JenkinsRule();

    @Test
    public void createNodeShouldFailWithoutComputerCreatePermission() throws Exception {
        final CLICommandInvoker.Result result = command.authorizedTo(READ).withStdin(Computer.class.getResourceAsStream("node.xml")).invoke();
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: user is missing the Agent/Create permission"));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result, failedWith(6));
    }

    @Test
    public void createNode() throws Exception {
        final CLICommandInvoker.Result result = command.authorizedTo(CREATE, READ).withStdin(Computer.class.getResourceAsStream("node.xml")).invoke();
        MatcherAssert.assertThat(result, succeededSilently());
        final Slave updatedSlave = ((Slave) (j.jenkins.getNode("SlaveFromXML")));
        MatcherAssert.assertThat(updatedSlave.getNodeName(), Matchers.equalTo("SlaveFromXML"));
        MatcherAssert.assertThat(updatedSlave.getNumExecutors(), Matchers.equalTo(42));
        MatcherAssert.assertThat(updatedSlave.getUserId(), Matchers.equalTo(command.user().getId()));
    }

    @Test
    public void createNodeSpecifyingNameExplicitly() throws Exception {
        final CLICommandInvoker.Result result = command.authorizedTo(CREATE, READ).withStdin(Computer.class.getResourceAsStream("node.xml")).invokeWithArgs("CustomSlaveName");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat("A slave with original name should not exist", j.jenkins.getNode("SlaveFromXml"), Matchers.nullValue());
        final Slave updatedSlave = ((Slave) (j.jenkins.getNode("CustomSlaveName")));
        MatcherAssert.assertThat(updatedSlave.getNodeName(), Matchers.equalTo("CustomSlaveName"));
        MatcherAssert.assertThat(updatedSlave.getNumExecutors(), Matchers.equalTo(42));
        MatcherAssert.assertThat(updatedSlave.getUserId(), Matchers.equalTo(command.user().getId()));
    }

    @Test
    public void createNodeSpecifyingDifferentNameExplicitly() throws Exception {
        final Node originalSlave = j.createSlave("SlaveFromXml", null, null);
        final CLICommandInvoker.Result result = command.authorizedTo(CREATE, READ).withStdin(Computer.class.getResourceAsStream("node.xml")).invokeWithArgs("CustomSlaveName");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat("A slave with original name should be left untouched", j.jenkins.getNode("SlaveFromXml"), Matchers.equalTo(originalSlave));
        final Slave updatedSlave = ((Slave) (j.jenkins.getNode("CustomSlaveName")));
        MatcherAssert.assertThat(updatedSlave.getNodeName(), Matchers.equalTo("CustomSlaveName"));
        MatcherAssert.assertThat(updatedSlave.getNumExecutors(), Matchers.equalTo(42));
        MatcherAssert.assertThat(updatedSlave.getUserId(), Matchers.equalTo(command.user().getId()));
    }

    @Test
    public void createNodeShouldFailIfNodeAlreadyExist() throws Exception {
        j.createSlave("SlaveFromXML", null, null);
        final CLICommandInvoker.Result result = command.authorizedTo(CREATE, READ).withStdin(Computer.class.getResourceAsStream("node.xml")).invoke();
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Node 'SlaveFromXML' already exists"));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result, failedWith(4));
    }

    @Test
    public void createNodeShouldFailIfNodeAlreadyExistWhenNameSpecifiedExplicitly() throws Exception {
        j.createSlave("ExistingSlave", null, null);
        final CLICommandInvoker.Result result = command.authorizedTo(CREATE, READ).withStdin(Computer.class.getResourceAsStream("node.xml")).invokeWithArgs("ExistingSlave");
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Node 'ExistingSlave' already exists"));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result, failedWith(4));
    }
}

