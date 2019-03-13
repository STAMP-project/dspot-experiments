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
import Run.DELETE;
import hudson.Functions;
import hudson.model.ExecutorTest;
import hudson.model.FreeStyleProject;
import hudson.model.labels.LabelAtom;
import hudson.tasks.Shell;
import jenkins.model.Jenkins;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static Matcher.failedWith;
import static Matcher.hasNoStandardOutput;
import static Matcher.succeeded;


/**
 *
 *
 * @author pjanouse
 */
public class DeleteBuildsCommandTest {
    private CLICommandInvoker command;

    @Rule
    public final JenkinsRule j = new JenkinsRule();

    @Test
    public void deleteBuildsShouldFailWithoutJobReadPermission() throws Exception {
        j.createFreeStyleProject("aProject").scheduleBuild2(0).get();
        final CLICommandInvoker.Result result = command.authorizedTo(READ).invokeWithArgs("aProject", "1");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such job 'aProject'"));
    }

    @Test
    public void deleteBuildsShouldFailWithoutRunDeletePermission() throws Exception {
        j.createFreeStyleProject("aProject").scheduleBuild2(0).get();
        final CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ).invokeWithArgs("aProject", "1");
        MatcherAssert.assertThat(result, failedWith(6));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: user is missing the Run/Delete permission"));
    }

    @Test
    public void deleteBuildsShouldFailIfJobDoesNotExist() throws Exception {
        final CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ, DELETE).invokeWithArgs("never_created", "1");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such job 'never_created'"));
    }

    @Test
    public void deleteBuildsShouldFailIfJobNameIsEmpty() throws Exception {
        j.createFreeStyleProject("aProject").scheduleBuild2(0).get();
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ, DELETE).invokeWithArgs("", "1");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such job ''; perhaps you meant 'aProject'?"));
    }

    @Test
    public void deleteBuildsShouldSuccess() throws Exception {
        j.createFreeStyleProject("aProject").scheduleBuild2(0).get();
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ, DELETE).invokeWithArgs("aProject", "1");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("Deleted 1 builds"));
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(0));
    }

    @Test
    public void deleteBuildsShouldSuccessIfBuildDoesNotExist() throws Exception {
        j.createFreeStyleProject("aProject").scheduleBuild2(0).get();
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ, DELETE).invokeWithArgs("aProject", "2");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("Deleted 0 builds"));
    }

    @Test
    public void deleteBuildsShouldSuccessIfBuildNumberZeroSpecified() throws Exception {
        j.createFreeStyleProject("aProject").scheduleBuild2(0).get();
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ, DELETE).invokeWithArgs("aProject", "0");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("Deleted 0 builds"));
    }

    @Test
    public void deleteBuildsShouldSuccessEvenTheBuildIsRunning() throws Exception {
        Assume.assumeFalse("You can't delete files that are in use on Windows", Functions.isWindows());
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        ExecutorTest.startBlockingBuild(project);
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ, DELETE).invokeWithArgs("aProject", "1");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("Deleted 1 builds"));
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(0));
        MatcherAssert.assertThat(project.isBuilding(), Matchers.equalTo(false));
    }

    @Test
    public void deleteBuildsShouldSuccessEvenTheBuildIsStuckInTheQueue() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        project.getBuildersList().add(new Shell("echo 1"));
        project.setAssignedLabel(new LabelAtom("never_created"));
        MatcherAssert.assertThat("Job wasn't scheduled properly", project.scheduleBuild(0), Matchers.equalTo(true));
        Thread.sleep(1000);
        MatcherAssert.assertThat("Job wasn't scheduled properly - it isn't in the queue", project.isInQueue(), Matchers.equalTo(true));
        MatcherAssert.assertThat("Job wasn't scheduled properly - it is running on non-exist node", project.isBuilding(), Matchers.equalTo(false));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ, DELETE).invokeWithArgs("aProject", "1");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("Deleted 0 builds"));
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(0));
        MatcherAssert.assertThat(isBuilding(), Matchers.equalTo(false));
        MatcherAssert.assertThat(isInQueue(), Matchers.equalTo(true));
        Jenkins.getInstance().getQueue().cancel(project);
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(0));
        MatcherAssert.assertThat(isBuilding(), Matchers.equalTo(false));
        MatcherAssert.assertThat(isInQueue(), Matchers.equalTo(false));
    }

    @Test
    public void deleteBuildsManyShouldSuccess() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        j.buildAndAssertSuccess(project);
        j.buildAndAssertSuccess(project);
        j.buildAndAssertSuccess(project);
        j.buildAndAssertSuccess(project);
        j.buildAndAssertSuccess(project);
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(5));
        CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ, DELETE).invokeWithArgs("aProject", "1,2");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("Deleted 2 builds"));
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(3));
        result = command.authorizedTo(READ, Job.READ, DELETE).invokeWithArgs("aProject", "3-5");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("Deleted 3 builds"));
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(0));
    }

    @Test
    public void deleteBuildsManyShouldSuccessEvenABuildIsSpecifiedTwice() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        j.buildAndAssertSuccess(project);
        j.buildAndAssertSuccess(project);
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(2));
        CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ, DELETE).invokeWithArgs("aProject", "1,1");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("Deleted 1 builds"));
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        result = command.authorizedTo(READ, Job.READ, DELETE).invokeWithArgs("aProject", "1-1,1-2,2-2");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("Deleted 1 builds"));
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(0));
    }

    @Test
    public void deleteBuildsManyShouldSuccessEvenLastBuildDoesNotExist() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        j.buildAndAssertSuccess(project);
        j.buildAndAssertSuccess(project);
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(2));
        CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ, DELETE).invokeWithArgs("aProject", "1,3");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("Deleted 1 builds"));
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        result = command.authorizedTo(READ, Job.READ, DELETE).invokeWithArgs("aProject", "2-3");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("Deleted 1 builds"));
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(0));
    }

    @Test
    public void deleteBuildsManyShouldSuccessEvenMiddleBuildDoesNotExist() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        j.buildAndAssertSuccess(project);
        j.buildAndAssertSuccess(project);
        j.buildAndAssertSuccess(project);
        j.buildAndAssertSuccess(project);
        j.buildAndAssertSuccess(project);
        j.buildAndAssertSuccess(project);
        project.getBuildByNumber(2).delete();
        project.getBuildByNumber(5).delete();
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(4));
        CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ, DELETE).invokeWithArgs("aProject", "1,2,3");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("Deleted 2 builds"));
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(2));
        result = command.authorizedTo(READ, Job.READ, DELETE).invokeWithArgs("aProject", "4-6");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("Deleted 2 builds"));
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(0));
    }

    @Test
    public void deleteBuildsManyShouldSuccessEvenFirstBuildDoesNotExist() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        j.buildAndAssertSuccess(project);
        j.buildAndAssertSuccess(project);
        j.buildAndAssertSuccess(project);
        project.getBuildByNumber(1).delete();
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(2));
        CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ, DELETE).invokeWithArgs("aProject", "1,2");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("Deleted 1 builds"));
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        result = command.authorizedTo(READ, Job.READ, DELETE).invokeWithArgs("aProject", "2-3");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("Deleted 1 builds"));
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(0));
    }

    @Test
    public void deleteBuildsManyShouldSuccessEvenTheFirstAndLastBuildDoesNotExist() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        j.buildAndAssertSuccess(project);
        j.buildAndAssertSuccess(project);
        j.buildAndAssertSuccess(project);
        j.buildAndAssertSuccess(project);
        j.buildAndAssertSuccess(project);
        project.getBuildByNumber(1).delete();
        project.getBuildByNumber(3).delete();
        project.getBuildByNumber(5).delete();
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(2));
        CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ, DELETE).invokeWithArgs("aProject", "1,2,3");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("Deleted 1 builds"));
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        result = command.authorizedTo(READ, Job.READ, DELETE).invokeWithArgs("aProject", "3-5");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString("Deleted 1 builds"));
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(0));
    }
}

