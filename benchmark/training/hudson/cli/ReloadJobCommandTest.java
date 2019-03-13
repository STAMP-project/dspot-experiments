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
import Job.CONFIGURE;
import Job.READ;
import hudson.model.FreeStyleProject;
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
public class ReloadJobCommandTest {
    private CLICommandInvoker command;

    @Rule
    public final JenkinsRule j = new JenkinsRule();

    @Test
    public void reloadJobShouldFailWithoutJobConfigurePermission() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        project.getBuildersList().add(createScriptBuilder("echo 1"));
        MatcherAssert.assertThat(project.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 1"));
        changeProjectOnTheDisc(project, "echo 1", "echo 2");
        final CLICommandInvoker.Result result = command.authorizedTo(READ, Jenkins.READ).invokeWithArgs("aProject");
        MatcherAssert.assertThat(result, failedWith(6));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: user is missing the Job/Configure permission"));
        MatcherAssert.assertThat(project.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 1"));
    }

    @Test
    public void reloadJobShouldFailWithoutJobReadPermission() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        project.getBuildersList().add(createScriptBuilder("echo 1"));
        MatcherAssert.assertThat(project.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 1"));
        changeProjectOnTheDisc(project, "echo 1", "echo 2");
        final CLICommandInvoker.Result result = command.authorizedTo(CONFIGURE, Jenkins.READ).invokeWithArgs("aProject");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such item ?aProject? exists."));
        MatcherAssert.assertThat(project.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 1"));
    }

    @Test
    public void reloadJobShouldSucceed() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        project.getBuildersList().add(createScriptBuilder("echo 1"));
        MatcherAssert.assertThat(project.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 1"));
        changeProjectOnTheDisc(project, "echo 1", "echo 2");
        final CLICommandInvoker.Result result = command.authorizedTo(READ, CONFIGURE, Jenkins.READ).invokeWithArgs("aProject");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(project.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 2"));
    }

    @Test
    public void reloadJobShouldFailIfJobDoesNotExist() throws Exception {
        final CLICommandInvoker.Result result = command.authorizedTo(READ, CONFIGURE, Jenkins.READ).invokeWithArgs("never_created");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such item ?never_created? exists."));
    }

    @Test
    public void reloadJobShouldFailIfJobDoesNotExistButNearExists() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("never_created");
        final CLICommandInvoker.Result result = command.authorizedTo(READ, CONFIGURE, Jenkins.READ).invokeWithArgs("never_created1");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such item ?never_created1? exists. Perhaps you meant ?never_created??"));
    }

    @Test
    public void reloadJobManyShouldSucceed() throws Exception {
        FreeStyleProject project1 = j.createFreeStyleProject("aProject1");
        project1.getBuildersList().add(createScriptBuilder("echo 1"));
        FreeStyleProject project2 = j.createFreeStyleProject("aProject2");
        project2.getBuildersList().add(createScriptBuilder("echo 1"));
        FreeStyleProject project3 = j.createFreeStyleProject("aProject3");
        project3.getBuildersList().add(createScriptBuilder("echo 1"));
        MatcherAssert.assertThat(project1.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 1"));
        MatcherAssert.assertThat(project2.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 1"));
        MatcherAssert.assertThat(project3.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 1"));
        changeProjectOnTheDisc(project1, "echo 1", "echo 2");
        changeProjectOnTheDisc(project2, "echo 1", "echo 2");
        changeProjectOnTheDisc(project3, "echo 1", "echo 2");
        final CLICommandInvoker.Result result = command.authorizedTo(READ, CONFIGURE, Jenkins.READ).invokeWithArgs("aProject1", "aProject2", "aProject3");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(project1.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 2"));
        MatcherAssert.assertThat(project2.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 2"));
        MatcherAssert.assertThat(project3.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 2"));
    }

    @Test
    public void reloadJobManyShouldFailIfFirstJobDoesNotExist() throws Exception {
        FreeStyleProject project1 = j.createFreeStyleProject("aProject1");
        project1.getBuildersList().add(createScriptBuilder("echo 1"));
        FreeStyleProject project2 = j.createFreeStyleProject("aProject2");
        project2.getBuildersList().add(createScriptBuilder("echo 1"));
        MatcherAssert.assertThat(project1.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 1"));
        MatcherAssert.assertThat(project2.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 1"));
        changeProjectOnTheDisc(project1, "echo 1", "echo 2");
        changeProjectOnTheDisc(project2, "echo 1", "echo 2");
        final CLICommandInvoker.Result result = command.authorizedTo(READ, CONFIGURE, Jenkins.READ).invokeWithArgs("never_created", "aProject1", "aProject2");
        MatcherAssert.assertThat(result, failedWith(5));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("never_created: No such item ?never_created? exists."));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString(("ERROR: " + (CLI_LISTPARAM_SUMMARY_ERROR_TEXT))));
        MatcherAssert.assertThat(project1.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 2"));
        MatcherAssert.assertThat(project2.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 2"));
    }

    @Test
    public void reloadJobManyShouldFailIfMiddleJobDoesNotExist() throws Exception {
        FreeStyleProject project1 = j.createFreeStyleProject("aProject1");
        project1.getBuildersList().add(createScriptBuilder("echo 1"));
        FreeStyleProject project2 = j.createFreeStyleProject("aProject2");
        project2.getBuildersList().add(createScriptBuilder("echo 1"));
        MatcherAssert.assertThat(project1.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 1"));
        MatcherAssert.assertThat(project2.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 1"));
        changeProjectOnTheDisc(project1, "echo 1", "echo 2");
        changeProjectOnTheDisc(project2, "echo 1", "echo 2");
        final CLICommandInvoker.Result result = command.authorizedTo(READ, CONFIGURE, Jenkins.READ).invokeWithArgs("aProject1", "never_created", "aProject2");
        MatcherAssert.assertThat(result, failedWith(5));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("never_created: No such item ?never_created? exists."));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString(("ERROR: " + (CLI_LISTPARAM_SUMMARY_ERROR_TEXT))));
        MatcherAssert.assertThat(project1.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 2"));
        MatcherAssert.assertThat(project2.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 2"));
    }

    @Test
    public void reloadJobManyShouldFailIfLastJobDoesNotExist() throws Exception {
        FreeStyleProject project1 = j.createFreeStyleProject("aProject1");
        project1.getBuildersList().add(createScriptBuilder("echo 1"));
        FreeStyleProject project2 = j.createFreeStyleProject("aProject2");
        project2.getBuildersList().add(createScriptBuilder("echo 1"));
        MatcherAssert.assertThat(project1.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 1"));
        MatcherAssert.assertThat(project2.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 1"));
        changeProjectOnTheDisc(project1, "echo 1", "echo 2");
        changeProjectOnTheDisc(project2, "echo 1", "echo 2");
        final CLICommandInvoker.Result result = command.authorizedTo(READ, CONFIGURE, Jenkins.READ).invokeWithArgs("aProject1", "aProject2", "never_created");
        MatcherAssert.assertThat(result, failedWith(5));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("never_created: No such item ?never_created? exists."));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString(("ERROR: " + (CLI_LISTPARAM_SUMMARY_ERROR_TEXT))));
        MatcherAssert.assertThat(project1.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 2"));
        MatcherAssert.assertThat(project2.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 2"));
    }

    @Test
    public void reloadJobManyShouldFailIfMoreJobsDoNotExist() throws Exception {
        FreeStyleProject project1 = j.createFreeStyleProject("aProject1");
        project1.getBuildersList().add(createScriptBuilder("echo 1"));
        FreeStyleProject project2 = j.createFreeStyleProject("aProject2");
        project2.getBuildersList().add(createScriptBuilder("echo 1"));
        MatcherAssert.assertThat(project1.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 1"));
        MatcherAssert.assertThat(project2.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 1"));
        changeProjectOnTheDisc(project1, "echo 1", "echo 2");
        changeProjectOnTheDisc(project2, "echo 1", "echo 2");
        final CLICommandInvoker.Result result = command.authorizedTo(READ, CONFIGURE, Jenkins.READ).invokeWithArgs("aProject1", "never_created1", "never_created2", "aProject2");
        MatcherAssert.assertThat(result, failedWith(5));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("never_created1: No such item ?never_created1? exists."));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("never_created2: No such item ?never_created2? exists."));
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString(("ERROR: " + (CLI_LISTPARAM_SUMMARY_ERROR_TEXT))));
        MatcherAssert.assertThat(project1.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 2"));
        MatcherAssert.assertThat(project2.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 2"));
    }

    @Test
    public void reloadJobManyShouldSucceedEvenAJobIsSpecifiedTwice() throws Exception {
        FreeStyleProject project1 = j.createFreeStyleProject("aProject1");
        project1.getBuildersList().add(createScriptBuilder("echo 1"));
        FreeStyleProject project2 = j.createFreeStyleProject("aProject2");
        project2.getBuildersList().add(createScriptBuilder("echo 1"));
        MatcherAssert.assertThat(project1.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 1"));
        MatcherAssert.assertThat(project2.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 1"));
        changeProjectOnTheDisc(project1, "echo 1", "echo 2");
        changeProjectOnTheDisc(project2, "echo 1", "echo 2");
        final CLICommandInvoker.Result result = command.authorizedTo(READ, CONFIGURE, Jenkins.READ).invokeWithArgs("aProject1", "aProject2", "aProject1");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(project1.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 2"));
        MatcherAssert.assertThat(project2.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 2"));
    }
}

