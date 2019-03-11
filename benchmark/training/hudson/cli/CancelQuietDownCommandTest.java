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
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.util.OneShotEvent;
import java.util.concurrent.Future;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static Matcher.failedWith;
import static Matcher.hasNoStandardOutput;
import static Matcher.succeededSilently;


public class CancelQuietDownCommandTest {
    private CLICommandInvoker command;

    @Rule
    public final JenkinsRule j = new JenkinsRule();

    @Test
    public void cancelQuietDownShouldFailWithoutAdministerPermission() throws Exception {
        final CLICommandInvoker.Result result = command.authorizedTo(READ).invoke();
        MatcherAssert.assertThat(result, failedWith(6));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: user is missing the Overall/Administer permission"));
    }

    @Test
    public void cancelQuietDownShouldSuccessOnNoQuietDownedJenkins() throws Exception {
        final CLICommandInvoker.Result result = command.authorizedTo(READ, ADMINISTER).invoke();
        MatcherAssert.assertThat(result, succeededSilently());
        QuietDownCommandTest.assertJenkinsNotInQuietMode(j);
    }

    @Test
    public void cancelQuietDownShouldSuccessOnQuietDownedJenkins() throws Exception {
        j.jenkins.getActiveInstance().doQuietDown();
        QuietDownCommandTest.assertJenkinsInQuietMode(j);
        final CLICommandInvoker.Result result = command.authorizedTo(READ, ADMINISTER).invoke();
        MatcherAssert.assertThat(result, succeededSilently());
        QuietDownCommandTest.assertJenkinsNotInQuietMode(j);
    }

    // 
    // Scenario - cancel-quiet-down is called when executor is running on non-quiet-down Jenkins
    // Result - CLI call result is available immediately, execution won't be affected
    // 
    @Test
    public void cancelQuietDownShouldSuccessOnNoQuietDownedJenkinsAndRunningExecutor() throws Exception {
        final FreeStyleProject project = j.createFreeStyleProject("aProject");
        final OneShotEvent finish = new OneShotEvent();
        Future<FreeStyleBuild> build = OnlineNodeCommandTest.startBlockingAndFinishingBuild(project, finish);
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, ADMINISTER).invoke();
        MatcherAssert.assertThat(result, succeededSilently());
        QuietDownCommandTest.assertJenkinsNotInQuietMode(j);
        finish.signal();
        build.get();
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        MatcherAssert.assertThat(project.isBuilding(), Matchers.equalTo(false));
        j.assertBuildStatusSuccess(build);
        QuietDownCommandTest.assertJenkinsNotInQuietMode(j);
        build = OnlineNodeCommandTest.startBlockingAndFinishingBuild(project, finish);
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(2));
        MatcherAssert.assertThat(project.isBuilding(), Matchers.equalTo(true));
        finish.signal();
        build.get();
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(2));
        MatcherAssert.assertThat(project.isBuilding(), Matchers.equalTo(false));
        j.assertBuildStatusSuccess(build);
        QuietDownCommandTest.assertJenkinsNotInQuietMode(j);
    }

    // 
    // Scenario - cancel-quiet-down is called when executor is running on quiet-down Jenkins
    // Result - CLI call result is available immediately, execution won't be affected
    // 
    @Test
    public void cancelQuietDownShouldSuccessOnQuietDownedJenkinsAndRunningExecutor() throws Exception {
        final FreeStyleProject project = j.createFreeStyleProject("aProject");
        final OneShotEvent finish = new OneShotEvent();
        Future<FreeStyleBuild> build = OnlineNodeCommandTest.startBlockingAndFinishingBuild(project, finish);
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        MatcherAssert.assertThat(project.isBuilding(), Matchers.equalTo(true));
        j.jenkins.getActiveInstance().doQuietDown();
        QuietDownCommandTest.assertJenkinsInQuietMode(j);
        final CLICommandInvoker.Result result = command.authorizedTo(READ, ADMINISTER).invoke();
        MatcherAssert.assertThat(result, succeededSilently());
        QuietDownCommandTest.assertJenkinsNotInQuietMode(j);
        finish.signal();
        build.get();
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        MatcherAssert.assertThat(project.isBuilding(), Matchers.equalTo(false));
        j.assertBuildStatusSuccess(build);
        QuietDownCommandTest.assertJenkinsNotInQuietMode(j);
        build = OnlineNodeCommandTest.startBlockingAndFinishingBuild(project, finish);
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(2));
        MatcherAssert.assertThat(project.isBuilding(), Matchers.equalTo(true));
        finish.signal();
        build.get();
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(2));
        MatcherAssert.assertThat(project.isBuilding(), Matchers.equalTo(false));
        j.assertBuildStatusSuccess(build);
        QuietDownCommandTest.assertJenkinsNotInQuietMode(j);
    }
}

