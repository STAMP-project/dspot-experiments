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
import Jenkins.READ;
import hudson.model.ListView;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static Matcher.failedWith;
import static Matcher.hasNoErrorOutput;
import static Matcher.hasNoStandardOutput;
import static Matcher.succeeded;


public class GetViewCommandTest {
    private CLICommandInvoker command;

    @Rule
    public final JenkinsRule j = new JenkinsRule();

    @Test
    public void getViewShouldFailWithoutViewReadPermission() throws IOException {
        j.jenkins.addView(new ListView("aView"));
        final CLICommandInvoker.Result result = command.authorizedTo(READ).invokeWithArgs("aView");
        MatcherAssert.assertThat(result, failedWith(6));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: user is missing the View/Read permission"));
    }

    @Test
    public void getViewShouldYieldConfigXml() throws Exception {
        j.jenkins.addView(new ListView("aView"));
        final CLICommandInvoker.Result result = command.authorizedTo(View.READ, READ).invokeWithArgs("aView");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result, hasNoErrorOutput());
        MatcherAssert.assertThat(result.stdout(), Matchers.startsWith("<?xml version=\"1.1\" encoding=\"UTF-8\"?>"));
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("<name>aView</name>"));
    }

    @Test
    public void getViewShouldFailIfViewDoesNotExist() {
        final CLICommandInvoker.Result result = command.authorizedTo(View.READ, READ).invokeWithArgs("never_created");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No view named never_created inside view Jenkins"));
    }
}

