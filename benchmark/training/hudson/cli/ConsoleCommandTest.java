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


import Item.BUILD;
import Jenkins.READ;
import Result.SUCCESS;
import hudson.Functions;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.model.labels.LabelAtom;
import hudson.tasks.BatchFile;
import hudson.tasks.Shell;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;

import static Matcher.failedWith;
import static Matcher.hasNoStandardOutput;
import static Matcher.succeeded;


/**
 * Tests for CLI command {@link hudson.cli.ConsoleCommand console}
 *
 * @author pjanouse
 */
public class ConsoleCommandTest {
    private CLICommandInvoker command;

    @Rule
    public final JenkinsRule j = new JenkinsRule();

    @Test
    public void consoleShouldFailWithoutJobReadPermission() throws Exception {
        j.createFreeStyleProject("aProject");
        final CLICommandInvoker.Result result = command.authorizedTo(READ).invokeWithArgs("aProject");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such job 'aProject'"));
    }

    @Issue("JENKINS-52181")
    @Test
    public void consoleShouldBeAccessibleForUserWithRead() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        if (Functions.isWindows()) {
            project.getBuildersList().add(new BatchFile("echo 1"));
        } else {
            project.getBuildersList().add(new Shell("echo 1"));
        }
        MatcherAssert.assertThat(project.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 1"));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ).invokeWithArgs("aProject");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("echo 1"));
    }

    @Test
    public void consoleShouldFailWhenProjectDoesNotExist() throws Exception {
        final CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ, BUILD).invokeWithArgs("never_created");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such job 'never_created'"));
    }

    @Test
    public void consoleShouldFailWhenLastBuildDoesNotdExist() throws Exception {
        j.createFreeStyleProject("aProject");
        final CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ, BUILD).invokeWithArgs("aProject");
        MatcherAssert.assertThat(result, failedWith(4));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Permalink lastBuild produced no build"));
    }

    @Test
    public void consoleShouldFailWhenRequestedBuildDoesNotExist() throws Exception {
        j.createFreeStyleProject("aProject");
        final CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ, BUILD).invokeWithArgs("aProject", "1");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such build #1"));
    }

    @Test
    public void consoleShouldFailWhenRequestedInvalidBuildNumber() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ, BUILD).invokeWithArgs("aProject", "1a");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Not sure what you meant by \"1a\""));
        project.getBuildersList().add(new Shell("echo 1"));
        project.scheduleBuild2(0).get();
        result = command.authorizedTo(READ, Job.READ, BUILD).invokeWithArgs("aProject", "1a");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Not sure what you meant by \"1a\". Did you mean \"lastBuild\"?"));
    }

    @Test
    public void consoleShouldSuccessWithLastBuild() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        if (Functions.isWindows()) {
            project.getBuildersList().add(new BatchFile("echo 1"));
        } else {
            project.getBuildersList().add(new Shell("echo 1"));
        }
        MatcherAssert.assertThat(project.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 1"));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ, BUILD).invokeWithArgs("aProject");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("echo 1"));
    }

    @Test
    public void consoleShouldSuccessWithSpecifiedBuildNumber() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        if (Functions.isWindows()) {
            project.getBuildersList().add(new BatchFile("echo %BUILD_NUMBER%"));
        } else {
            project.getBuildersList().add(new Shell("echo ${BUILD_NUMBER}"));
        }
        MatcherAssert.assertThat(project.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 1"));
        MatcherAssert.assertThat(project.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 2"));
        MatcherAssert.assertThat(project.scheduleBuild2(0).get().getLog(), Matchers.containsString("echo 3"));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ, BUILD).invokeWithArgs("aProject", "2");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("echo 2"));
    }

    @Test
    public void consoleShouldSuccessWithFollow() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        // TODO: do we really want to sleep for 10 seconds?
        if (Functions.isWindows()) {
            project.getBuildersList().add(new BatchFile(("echo start - %BUILD_NUMBER%\r\n" + "ping -n 10 127.0.0.1 >nul\r\necho after sleep - %BUILD_NUMBER%")));
        } else {
            project.getBuildersList().add(new Shell(("echo start - ${BUILD_NUMBER}\nsleep 10s\n" + "echo after sleep - ${BUILD_NUMBER}")));
        }
        if (!(project.scheduleBuild(0))) {
            Assert.fail("Job wasn't scheduled properly");
        }
        // Wait until project is started (at least 1s)
        while (!(project.isBuilding())) {
            System.out.println("Waiting for build to start and sleep 1s...");
            Thread.sleep(1000);
        } 
        // Wait for the first message
        if (!(project.getBuildByNumber(1).getLog().contains("start - 1"))) {
            Thread.sleep(1000);
        }
        MatcherAssert.assertThat(project.getBuildByNumber(1).getLog(), Matchers.containsString("start - 1"));
        MatcherAssert.assertThat(project.getBuildByNumber(1).getLog(), Matchers.not(Matchers.containsString("after sleep - 1")));
        CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ, BUILD).invokeWithArgs("aProject", "1");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("start - 1"));
        MatcherAssert.assertThat(result.stdout(), Matchers.not(Matchers.containsString("after sleep - 1")));
        result = command.authorizedTo(READ, Job.READ, BUILD).invokeWithArgs("aProject", "1", "-f");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("after sleep - 1"));
        MatcherAssert.assertThat(project.getBuildByNumber(1).isBuilding(), Matchers.equalTo(false));
        MatcherAssert.assertThat(project.getBuildByNumber(1).getResult(), Matchers.equalTo(SUCCESS));
        MatcherAssert.assertThat(project.getBuildByNumber(1).getLog(), Matchers.containsString("after sleep - 1"));
    }

    @Test
    public void consoleShouldSuccessWithLastNLines() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        if (Functions.isWindows()) {
            project.getBuildersList().add(new BatchFile("echo 1\r\necho 2\r\necho 3\r\necho 4\r\necho 5"));
        } else {
            project.getBuildersList().add(new Shell("echo 1\necho 2\necho 3\necho 4\necho 5"));
        }
        project.scheduleBuild2(0).get();
        MatcherAssert.assertThat(project.getBuildByNumber(1).getLog(), Matchers.containsString("echo 1"));
        MatcherAssert.assertThat(project.getBuildByNumber(1).getLog(), Matchers.containsString("echo 5"));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ, BUILD).invokeWithArgs("aProject", "1", "-n", (Functions.isWindows() ? "5" : "4"));
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.not(Matchers.containsString("echo 1")));
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("echo 5"));
    }

    @Test
    public void consoleShouldSuccessWithLastNLinesAndFollow() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        // TODO: do we really want to sleep for 10 seconds?
        if (Functions.isWindows()) {
            // the ver >NUL is to reset ERRORLEVEL so we don't fail (ping causes the error)
            project.getBuildersList().add(new BatchFile(("echo 1\r\necho 2\r\necho 3\r\necho 4\r\necho 5\r\n" + "ping -n 10 127.0.0.1 >nul\r\necho 6\r\necho 7\r\necho 8\r\necho 9")));
        } else {
            project.getBuildersList().add(new Shell(("echo 1\necho 2\necho 3\necho 4\necho 5\n" + ("sleep 10s\n" + "echo 6\necho 7\necho 8\necho 9"))));
        }
        if (!(project.scheduleBuild(0))) {
            Assert.fail("Job wasn't scheduled properly");
        }
        // Wait until project is started (at least 1s)
        while (!(project.isBuilding())) {
            System.out.println("Waiting for build to start and sleep 1s...");
            Thread.sleep(1000);
        } 
        // Wait for the first sleep
        if (!(project.getBuildByNumber(1).getLog().contains("echo 5"))) {
            Thread.sleep(1000);
        }
        MatcherAssert.assertThat(project.getBuildByNumber(1).getLog(), Matchers.containsString("echo 5"));
        MatcherAssert.assertThat(project.getBuildByNumber(1).getLog(), Matchers.not(Matchers.containsString("echo 6")));
        CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ, BUILD).invokeWithArgs("aProject", "1", "-f", "-n", (Functions.isWindows() ? "5" : "4"));
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.not(Matchers.containsString("echo 1")));
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("echo 5"));
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("echo 6"));
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("echo 9"));
        MatcherAssert.assertThat(project.getBuildByNumber(1).isBuilding(), Matchers.equalTo(false));
        MatcherAssert.assertThat(project.getBuildByNumber(1).getResult(), Matchers.equalTo(SUCCESS));
        MatcherAssert.assertThat(project.getBuildByNumber(1).getLog(), Matchers.containsString("echo 9"));
    }

    @Test
    public void consoleShouldFailIfTheBuildIsStuckInTheQueue() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        project.getBuildersList().add(new Shell("echo 1\nsleep 10s"));
        project.setAssignedLabel(new LabelAtom("never_created"));
        MatcherAssert.assertThat("Job wasn't scheduled properly", project.scheduleBuild(0), Matchers.equalTo(true));
        Thread.sleep(1000);
        MatcherAssert.assertThat("Job wasn't scheduled properly - it isn't in the queue", project.isInQueue(), Matchers.equalTo(true));
        MatcherAssert.assertThat("Job wasn't scheduled properly - it is running on non-exist node", project.isBuilding(), Matchers.equalTo(false));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ, BUILD).invokeWithArgs("aProject", "1");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such build #1"));
    }
}

