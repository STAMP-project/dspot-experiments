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
package hudson.cli;


import CLICommandInvoker.Result;
import Job.DELETE;
import Job.READ;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static CLICommand.CLI_LISTPARAM_SUMMARY_ERROR_TEXT;
import static Matcher.failedWith;
import static Matcher.hasNoStandardOutput;
import static Matcher.succeededSilently;


/**
 *
 *
 * @author pjanouse
 */
public class DeleteJobCommandTest {
    private CLICommandInvoker command;

    @Rule
    public final JenkinsRule j = new JenkinsRule();

    @Test
    public void deleteJobShouldFailWithoutJobDeletePermission() throws IOException {
        j.createFreeStyleProject("aProject");
        final CLICommandInvoker.Result result = command.authorizedTo(READ, Jenkins.READ).invokeWithArgs("aProject");
        MatcherAssert.assertThat(result, failedWith(6));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: user is missing the Job/Delete permission"));
    }

    @Test
    public void deleteJobShouldFailWithoutJobReadPermission() throws IOException {
        j.createFreeStyleProject("aProject");
        final CLICommandInvoker.Result result = command.authorizedTo(DELETE, Jenkins.READ).invokeWithArgs("aProject");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such job 'aProject'"));
    }

    @Test
    public void deleteJobShouldSucceed() throws Exception {
        j.createFreeStyleProject("aProject");
        final CLICommandInvoker.Result result = command.authorizedTo(READ, DELETE, Jenkins.READ).invokeWithArgs("aProject");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(j.jenkins.getItem("aProject"), Matchers.nullValue());
    }

    @Test
    public void deleteJobShouldFailIfJobDoesNotExist() {
        final CLICommandInvoker.Result result = command.authorizedTo(READ, DELETE, Jenkins.READ).invokeWithArgs("never_created");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such job 'never_created'"));
    }

    @Test
    public void deleteJobManyShouldSucceed() throws Exception {
        j.createFreeStyleProject("aProject1");
        j.createFreeStyleProject("aProject2");
        j.createFreeStyleProject("aProject3");
        final CLICommandInvoker.Result result = command.authorizedTo(READ, DELETE, Jenkins.READ).invokeWithArgs("aProject1", "aProject2", "aProject3");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(j.jenkins.getItem("aProject1"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getItem("aProject2"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getItem("aProject3"), Matchers.nullValue());
    }

    @Test
    public void deleteJobManyShouldFailIfFirstJobDoesNotExist() throws Exception {
        j.createFreeStyleProject("aProject1");
        j.createFreeStyleProject("aProject2");
        final CLICommandInvoker.Result result = command.authorizedTo(READ, DELETE, Jenkins.READ).invokeWithArgs("never_created", "aProject1", "aProject2");
        MatcherAssert.assertThat(result, failedWith(5));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("never_created: No such job 'never_created'"));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString(("ERROR: " + (CLI_LISTPARAM_SUMMARY_ERROR_TEXT))));
        MatcherAssert.assertThat(j.jenkins.getItem("aProject1"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getItem("aProject2"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getItem("never_created"), Matchers.nullValue());
    }

    @Test
    public void deleteJobManyShouldFailIfMiddleJobDoesNotExist() throws Exception {
        j.createFreeStyleProject("aProject1");
        j.createFreeStyleProject("aProject2");
        final CLICommandInvoker.Result result = command.authorizedTo(READ, DELETE, Jenkins.READ).invokeWithArgs("aProject1", "never_created", "aProject2");
        MatcherAssert.assertThat(result, failedWith(5));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("never_created: No such job 'never_created'"));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString(("ERROR: " + (CLI_LISTPARAM_SUMMARY_ERROR_TEXT))));
        MatcherAssert.assertThat(j.jenkins.getItem("aProject1"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getItem("aProject2"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getItem("never_created"), Matchers.nullValue());
    }

    @Test
    public void deleteJobManyShouldFailIfLastJobDoesNotExist() throws Exception {
        j.createFreeStyleProject("aProject1");
        j.createFreeStyleProject("aProject2");
        final CLICommandInvoker.Result result = command.authorizedTo(READ, DELETE, Jenkins.READ).invokeWithArgs("aProject1", "aProject2", "never_created");
        MatcherAssert.assertThat(result, failedWith(5));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("never_created: No such job 'never_created'"));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString(("ERROR: " + (CLI_LISTPARAM_SUMMARY_ERROR_TEXT))));
        MatcherAssert.assertThat(j.jenkins.getItem("aProject1"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getItem("aProject2"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getItem("never_created"), Matchers.nullValue());
    }

    @Test
    public void deleteJobManyShouldFailIfMoreJobsDoNotExist() throws Exception {
        j.createFreeStyleProject("aProject1");
        j.createFreeStyleProject("aProject2");
        final CLICommandInvoker.Result result = command.authorizedTo(READ, DELETE, Jenkins.READ).invokeWithArgs("aProject1", "never_created1", "never_created2", "aProject2");
        MatcherAssert.assertThat(result, failedWith(5));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("never_created1: No such job 'never_created1'"));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("never_created2: No such job 'never_created2'"));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString(("ERROR: " + (CLI_LISTPARAM_SUMMARY_ERROR_TEXT))));
        MatcherAssert.assertThat(j.jenkins.getItem("aProject1"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getItem("aProject2"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getItem("never_created1"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getItem("never_created2"), Matchers.nullValue());
    }

    @Test
    public void deleteJobManyShouldSucceedEvenAJobIsSpecifiedTwice() throws Exception {
        j.createFreeStyleProject("aProject1");
        j.createFreeStyleProject("aProject2");
        final CLICommandInvoker.Result result = command.authorizedTo(READ, DELETE, Jenkins.READ).invokeWithArgs("aProject1", "aProject2", "aProject1");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(j.jenkins.getItem("aProject1"), Matchers.nullValue());
        MatcherAssert.assertThat(j.jenkins.getItem("aProject2"), Matchers.nullValue());
    }
}

