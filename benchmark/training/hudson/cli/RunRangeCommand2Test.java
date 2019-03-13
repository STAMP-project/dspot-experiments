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
import hudson.Functions;
import hudson.model.FreeStyleProject;
import hudson.model.labels.LabelAtom;
import hudson.tasks.BatchFile;
import hudson.tasks.Shell;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
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
public class RunRangeCommand2Test {
    private CLICommandInvoker command;

    @Rule
    public final JenkinsRule j = new JenkinsRule();

    @Test
    public void dummyRangeShouldFailIfJobNameIsEmptyOnEmptyJenkins() throws Exception {
        j.createFreeStyleProject("aProject").scheduleBuild2(0).get();
        MatcherAssert.assertThat(getBuilds().size(), Matchers.equalTo(1));
        CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ).invokeWithArgs("", "1");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such job ''"));
    }

    @Test
    public void dummyRangeShouldFailIfJobNameIsSpaceOnEmptyJenkins() throws Exception {
        j.createFreeStyleProject("aProject").scheduleBuild2(0).get();
        MatcherAssert.assertThat(getBuilds().size(), Matchers.equalTo(1));
        CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ).invokeWithArgs(" ", "1");
        MatcherAssert.assertThat(result, failedWith(3));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: No such job ' '"));
    }

    @Test
    public void dummyRangeShouldSuccessEvenTheBuildIsRunning() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        project.getBuildersList().add((Functions.isWindows() ? new BatchFile("echo 1\r\nping -n 10 127.0.0.1 >nul") : new Shell("echo 1\nsleep 10s")));
        MatcherAssert.assertThat("Job wasn't scheduled properly", project.scheduleBuild(0), Matchers.equalTo(true));
        // Wait until classProject is started (at least 1s)
        while (!(project.isBuilding())) {
            System.out.println("Waiting for build to start and sleep 1s...");
            Thread.sleep(1000);
        } 
        // Wait for the first sleep
        if (!(project.getBuildByNumber(1).getLog().contains("echo 1"))) {
            Thread.sleep(1000);
        }
        final CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ).invokeWithArgs("aProject", "1");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: 1" + (System.lineSeparator()))));
    }

    @Test
    public void dummyRangeShouldSuccessEvenTheBuildIsStuckInTheQueue() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("aProject");
        project.getBuildersList().add(new Shell("echo 1\nsleep 10s"));
        project.setAssignedLabel(new LabelAtom("never_created"));
        MatcherAssert.assertThat("Job wasn't scheduled properly", project.scheduleBuild(0), Matchers.equalTo(true));
        Thread.sleep(1000);
        MatcherAssert.assertThat("Job wasn't scheduled properly - it isn't in the queue", project.isInQueue(), Matchers.equalTo(true));
        MatcherAssert.assertThat("Job wasn't scheduled properly - it is running on non-exist node", project.isBuilding(), Matchers.equalTo(false));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, Job.READ).invokeWithArgs("aProject", "1");
        MatcherAssert.assertThat(result, succeeded());
        MatcherAssert.assertThat(result.stdout(), Matchers.containsString(("Builds: " + (System.lineSeparator()))));
    }
}

