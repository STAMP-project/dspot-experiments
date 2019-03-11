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
package hudson.cli;


import CLICommandInvoker.Result;
import Jenkins.READ;
import Run.UPDATE;
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


/**
 *
 *
 * @author pjanouse
 */
public class SetBuildDescriptionCommandTest {
    private CLICommandInvoker command;

    @Rule
    public final JenkinsRule j = new JenkinsRule();

    @Test
    public void setBuildDescriptionShouldFailWithoutJobReadPermission() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        project.getBuildersList().add(createScriptBuilder("echo 1"));
        MatcherAssert.assertThat(project.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 1"));
        final CLICommandInvoker.Result result = command.authorizedTo(READ).invokeWithArgs("aProject", "1", "test");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such job 'aProject'"));
    }

    @Test
    public void setBuildDescriptionShouldFailWithoutRunUpdatePermission1() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        project.getBuildersList().add(createScriptBuilder("echo 1"));
        MatcherAssert.assertThat(project.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 1"));
        final CLICommandInvoker.Result result = command.authorizedTo(Job.READ, READ).invokeWithArgs("aProject", "1", "test");
        MatcherAssert.assertThat(result, failedWith(6));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: user is missing the Run/Update permission"));
    }

    @Test
    public void setBuildDescriptionShouldSucceed() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        project.getBuildersList().add(createScriptBuilder("echo 1"));
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        MatcherAssert.assertThat(build.getLog(), Matchers.containsString("echo 1"));
        MatcherAssert.assertThat(build.getDescription(), Matchers.equalTo(null));
        CLICommandInvoker.Result result = command.authorizedTo(UPDATE, Job.READ, READ).invokeWithArgs("aProject", "1", "test");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(build.getDescription(), Matchers.equalTo("test"));
        result = command.authorizedTo(UPDATE, Job.READ, READ).invokeWithArgs("aProject", "1", "");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(build.getDescription(), Matchers.equalTo(""));
        result = command.authorizedTo(UPDATE, Job.READ, READ).invokeWithArgs("aProject", "1", " ");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(build.getDescription(), Matchers.equalTo(" "));
    }

    @Test
    public void setBuildDescriptionShouldFailIfJobDoesNotExist() throws Exception {
        final CLICommandInvoker.Result result = command.authorizedTo(UPDATE, Job.READ, READ).invokeWithArgs("never_created");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such job 'never_created'"));
    }

    @Test
    public void setBuildDescriptionShouldFailIfJobDoesNotExistButNearExists() throws Exception {
        j.createFreeStyleProject("never_created");
        final CLICommandInvoker.Result result = command.authorizedTo(UPDATE, Job.READ, READ).invokeWithArgs("never_created1");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such job 'never_created1'; perhaps you meant 'never_created'?"));
    }

    @Test
    public void setBuildDescriptionShouldFailIfBuildDoesNotExist() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        project.getBuildersList().add(createScriptBuilder("echo 1"));
        MatcherAssert.assertThat(project.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 1"));
        final CLICommandInvoker.Result result = command.authorizedTo(Job.READ, READ).invokeWithArgs("aProject", "2", "test");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such build #2"));
    }
}

