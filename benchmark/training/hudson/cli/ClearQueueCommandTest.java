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
import Jenkins.ADMINISTER;
import Jenkins.READ;
import hudson.model.FreeStyleProject;
import hudson.model.labels.LabelAtom;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static Matcher.failedWith;
import static Matcher.hasNoStandardOutput;
import static Matcher.succeededSilently;


public class ClearQueueCommandTest {
    private CLICommandInvoker command;

    @Rule
    public final JenkinsRule j = new JenkinsRule();

    @Test
    public void clearQueueShouldFailWithoutAdministerPermission() throws Exception {
        final CLICommandInvoker.Result result = command.authorizedTo(READ).invoke();
        MatcherAssert.assertThat(result, failedWith(6));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("user is missing the Overall/Administer permission"));
    }

    @Test
    public void clearQueueShouldSucceedOnEmptyQueue() throws Exception {
        MatcherAssert.assertThat(j.jenkins.getQueue().isEmpty(), Matchers.equalTo(true));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, ADMINISTER).invoke();
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(j.jenkins.getQueue().isEmpty(), Matchers.equalTo(true));
    }

    @Test
    public void clearQueueShouldSucceed() throws Exception {
        MatcherAssert.assertThat(j.jenkins.getQueue().isEmpty(), Matchers.equalTo(true));
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        project.setAssignedLabel(new LabelAtom("never_created"));
        project.scheduleBuild2(0);
        MatcherAssert.assertThat(j.jenkins.getQueue().isEmpty(), Matchers.equalTo(false));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, ADMINISTER).invoke();
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(j.jenkins.getQueue().isEmpty(), Matchers.equalTo(true));
    }
}

