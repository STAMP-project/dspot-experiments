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
import Computer.EXTENDED_READ;
import Jenkins.READ;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;

import static Matcher.failedWith;
import static Matcher.hasNoErrorOutput;
import static Matcher.hasNoStandardOutput;
import static Matcher.succeeded;


public class GetNodeCommandTest {
    private CLICommandInvoker command;

    @Rule
    public final JenkinsRule j = new JenkinsRule();

    @Test
    public void getNodeShouldFailWithoutComputerReadPermission() throws Exception {
        j.createSlave("MySlave", null, null);
        final CLICommandInvoker.Result result = command.authorizedTo(READ).invokeWithArgs("MySlave");
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: user is missing the Agent/ExtendedRead permission"));
        MatcherAssert.assertThat(result, failedWith(6));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
    }

    @Test
    public void getNodeShouldYieldConfigXml() throws Exception {
        j.createSlave("MySlave", null, null);
        final CLICommandInvoker.Result result = command.authorizedTo(EXTENDED_READ, READ).invokeWithArgs("MySlave");
        MatcherAssert.assertThat(result.stdout(), Matchers.startsWith("<?xml version=\"1.1\" encoding=\"UTF-8\"?>"));
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("<name>MySlave</name>"));
        MatcherAssert.assertThat(result, hasNoErrorOutput());
        MatcherAssert.assertThat(result, succeeded());
    }

    @Test
    public void getNodeShouldFailIfNodeDoesNotExist() throws Exception {
        final CLICommandInvoker.Result result = command.authorizedTo(EXTENDED_READ, READ).invokeWithArgs("MySlave");
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such node 'MySlave'"));
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
    }

    @Issue("SECURITY-281")
    @Test
    public void getNodeShouldFailForMaster() throws Exception {
        CLICommandInvoker.Result result = command.authorizedTo(EXTENDED_READ, READ).invokeWithArgs("");
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("No such node ''"));
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        result = command.authorizedTo(EXTENDED_READ, READ).invokeWithArgs("(master)");
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("No such node '(master)'"));
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
    }
}

