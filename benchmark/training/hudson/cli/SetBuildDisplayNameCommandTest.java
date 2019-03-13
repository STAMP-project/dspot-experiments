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
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static Matcher.failedWith;
import static Matcher.hasNoStandardOutput;
import static Matcher.succeededSilently;


public class SetBuildDisplayNameCommandTest {
    private CLICommandInvoker command;

    @Rule
    public final JenkinsRule j = new JenkinsRule();

    @Test
    public void referencingBuildThatDoesNotExistsShouldFail() throws Exception {
        j.createFreeStyleProject("project");
        final CLICommandInvoker.Result result = command.invokeWithArgs("project", "42", "DisplayName");
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Build #42 does not exist"));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result, failedWith(3));
    }

    @Test
    public void setDescriptionSuccessfully() throws Exception {
        FreeStyleProject job = j.createFreeStyleProject("project");
        FreeStyleBuild build = job.scheduleBuild2(0).get();
        final CLICommandInvoker.Result result = command.invokeWithArgs("project", "1", "DisplayName");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(build.getDisplayName(), Matchers.equalTo("DisplayName"));
    }
}

