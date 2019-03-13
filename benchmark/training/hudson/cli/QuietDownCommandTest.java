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
import hudson.model.QueueTest;
import hudson.util.OneShotEvent;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static Matcher.failedWith;
import static Matcher.hasNoStandardOutput;
import static Matcher.succeededSilently;


public class QuietDownCommandTest {
    private CLICommandInvoker command;

    private static final QueueTest.TestFlyweightTask task = new QueueTest.TestFlyweightTask(new AtomicInteger(), null);

    @Rule
    public final JenkinsRule j = new JenkinsRule();

    @Test
    public void quietDownShouldFailWithoutAdministerPermission() throws Exception {
        final CLICommandInvoker.Result result = command.authorizedTo(READ).invoke();
        MatcherAssert.assertThat(result, failedWith(6));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: user is missing the Overall/Administer permission"));
    }

    @Test
    public void quietDownShouldSuccess() throws Exception {
        final CLICommandInvoker.Result result = command.authorizedTo(READ, ADMINISTER).invoke();
        MatcherAssert.assertThat(result, succeededSilently());
        assertJenkinsInQuietMode();
    }

    @Test
    public void quietDownShouldSuccessWithBlock() throws Exception {
        final CLICommandInvoker.Result result = command.authorizedTo(READ, ADMINISTER).invokeWithArgs("-block");
        MatcherAssert.assertThat(result, succeededSilently());
        assertJenkinsInQuietMode();
    }

    @Test
    public void quietDownShouldSuccessWithTimeout() throws Exception {
        final CLICommandInvoker.Result result = command.authorizedTo(READ, ADMINISTER).invokeWithArgs("-timeout", "0");
        MatcherAssert.assertThat(result, succeededSilently());
        assertJenkinsInQuietMode();
    }

    @Test
    public void quietDownShouldSuccessWithBlockAndTimeout() throws Exception {
        final CLICommandInvoker.Result result = command.authorizedTo(READ, ADMINISTER).invokeWithArgs("-block", "-timeout", "0");
        MatcherAssert.assertThat(result, succeededSilently());
        assertJenkinsInQuietMode();
    }

    @Test
    public void quietDownShouldFailWithEmptyTimeout() throws Exception {
        final CLICommandInvoker.Result result = command.authorizedTo(READ, ADMINISTER).invokeWithArgs("-timeout");
        MatcherAssert.assertThat(result, failedWith(2));
        MatcherAssert.assertThat(result, hasNoStandardOutput());
        MatcherAssert.assertThat(result.stderr(), Matchers.containsString("ERROR: Option \"-timeout\" takes an operand"));
    }

    @Test
    public void quietDownShouldSuccessOnAlreadyQuietDownedJenkins() throws Exception {
        j.jenkins.getActiveInstance().doQuietDown();
        assertJenkinsInQuietMode();
        final CLICommandInvoker.Result result = command.authorizedTo(READ, ADMINISTER).invoke();
        MatcherAssert.assertThat(result, succeededSilently());
        assertJenkinsInQuietMode();
    }

    @Test
    public void quietDownShouldSuccessWithBlockOnAlreadyQuietDownedJenkins() throws Exception {
        j.jenkins.getActiveInstance().doQuietDown(true, 0);
        assertJenkinsInQuietMode();
        final CLICommandInvoker.Result result = command.authorizedTo(READ, ADMINISTER).invokeWithArgs("-block");
        MatcherAssert.assertThat(result, succeededSilently());
        assertJenkinsInQuietMode();
    }

    @Test
    public void quietDownShouldSuccessWithBlockAndTimeoutOnAlreadyQuietDownedJenkins() throws Exception {
        j.jenkins.getActiveInstance().doQuietDown(true, 0);
        assertJenkinsInQuietMode();
        final long time_before = System.currentTimeMillis();
        final CLICommandInvoker.Result result = command.authorizedTo(READ, ADMINISTER).invokeWithArgs("-block", "-timeout", "20000");
        MatcherAssert.assertThat(result, succeededSilently());
        MatcherAssert.assertThat(((System.currentTimeMillis()) < (time_before + 20000)), Matchers.equalTo(true));
        assertJenkinsInQuietMode();
    }

    // 
    // Scenario - quiet-down is called without block when executor is running
    // Result - CLI call result is available immediately, execution won't be affected
    // 
    @Test
    public void quietDownShouldSuccessAndRunningExecutor() throws Exception {
        final FreeStyleProject project = j.createFreeStyleProject("aProject");
        final OneShotEvent finish = new OneShotEvent();
        final Future<FreeStyleBuild> build = OnlineNodeCommandTest.startBlockingAndFinishingBuild(project, finish);
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        final CLICommandInvoker.Result result = command.authorizedTo(READ, ADMINISTER).invoke();
        MatcherAssert.assertThat(result, succeededSilently());
        assertJenkinsInQuietMode();
        finish.signal();
        build.get();
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        MatcherAssert.assertThat(project.isBuilding(), Matchers.equalTo(false));
        j.assertBuildStatusSuccess(build);
        assertJenkinsInQuietMode();
    }

    // 
    // Scenario - quiet-down is called with block when executor is running
    // Expected result - CLI call is blocked indefinitely, execution won't be affected
    // 
    @Test
    public void quietDownShouldSuccessWithBlockAndRunningExecutor() throws Exception {
        final FreeStyleProject project = j.createFreeStyleProject("aProject");
        final ExecutorService threadPool = Executors.newSingleThreadExecutor();
        final OneShotEvent beforeCli = new OneShotEvent();
        final OneShotEvent finish = new OneShotEvent();
        final Future<FreeStyleBuild> build = OnlineNodeCommandTest.startBlockingAndFinishingBuild(project, finish);
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        boolean timeoutOccurred = false;
        final FutureTask exec_task = new FutureTask(new Callable() {
            public Object call() {
                assertJenkinsNotInQuietMode();
                beforeCli.signal();
                final CLICommandInvoker.Result result = command.authorizedTo(READ, ADMINISTER).invokeWithArgs("-block");
                Assert.fail("Should never return from previous CLI call!");
                return null;
            }
        });
        try {
            threadPool.submit(exec_task);
            beforeCli.block();
            assertJenkinsInQuietMode();
            exec_task.get(10, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            timeoutOccurred = true;
        }
        if (!timeoutOccurred)
            Assert.fail("Missing timeout for CLI call");

        finish.signal();
        build.get();
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        MatcherAssert.assertThat(project.isBuilding(), Matchers.equalTo(false));
        j.assertBuildStatusSuccess(build);
        exec_task.cancel(true);
        assertJenkinsInQuietMode();
    }

    // 
    // Scenario - quiet-down is called with block and zero timeout when executor is running
    // Expected result - CLI call is blocked indefinitely, execution won't be affected
    // 
    @Test
    public void quietDownShouldSuccessWithBlockAndZeroTimeoutAndRunningExecutor() throws Exception {
        final FreeStyleProject project = j.createFreeStyleProject("aProject");
        final ExecutorService threadPool = Executors.newSingleThreadExecutor();
        final OneShotEvent beforeCli = new OneShotEvent();
        final OneShotEvent finish = new OneShotEvent();
        final Future<FreeStyleBuild> build = OnlineNodeCommandTest.startBlockingAndFinishingBuild(project, finish);
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        boolean timeoutOccurred = false;
        final FutureTask exec_task = new FutureTask(new Callable() {
            public Object call() {
                assertJenkinsNotInQuietMode();
                beforeCli.signal();
                final CLICommandInvoker.Result result = command.authorizedTo(READ, ADMINISTER).invokeWithArgs("-block", "-timeout", "0");
                Assert.fail("Should never return from previous CLI call!");
                return null;
            }
        });
        try {
            threadPool.submit(exec_task);
            beforeCli.block();
            assertJenkinsInQuietMode();
            exec_task.get(10, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            timeoutOccurred = true;
        }
        if (!timeoutOccurred)
            Assert.fail("Missing timeout for CLI call");

        finish.signal();
        build.get();
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        MatcherAssert.assertThat(project.isBuilding(), Matchers.equalTo(false));
        j.assertBuildStatusSuccess(build);
        exec_task.cancel(true);
        assertJenkinsInQuietMode();
    }

    // 
    // Scenario - quiet-down is called with block and a timeout when executor is running
    // Expected result - CLI call return after TIMEOUT seconds, execution won't be affected
    // 
    @Test
    public void quietDownShouldSuccessWithBlockPlusExpiredTimeoutAndRunningExecutor() throws Exception {
        final int TIMEOUT = 5000;
        final FreeStyleProject project = j.createFreeStyleProject("aProject");
        final ExecutorService threadPool = Executors.newSingleThreadExecutor();
        final OneShotEvent beforeCli = new OneShotEvent();
        final OneShotEvent finish = new OneShotEvent();
        final Future<FreeStyleBuild> build = OnlineNodeCommandTest.startBlockingAndFinishingBuild(project, finish);
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        final FutureTask exec_task = new FutureTask(new Callable() {
            public Object call() {
                assertJenkinsNotInQuietMode();
                final long time_before = System.currentTimeMillis();
                beforeCli.signal();
                final CLICommandInvoker.Result result = command.authorizedTo(READ, ADMINISTER).invokeWithArgs("-block", "-timeout", Integer.toString(TIMEOUT));
                MatcherAssert.assertThat(result, succeededSilently());
                MatcherAssert.assertThat(((System.currentTimeMillis()) > (time_before + TIMEOUT)), Matchers.equalTo(true));
                assertJenkinsInQuietMode();
                return null;
            }
        });
        threadPool.submit(exec_task);
        beforeCli.block();
        assertJenkinsInQuietMode();
        try {
            exec_task.get((2 * TIMEOUT), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            Assert.fail("Blocking call didn't finish after timeout!");
        }
        MatcherAssert.assertThat(exec_task.isDone(), Matchers.equalTo(true));
        finish.signal();
        build.get();
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        MatcherAssert.assertThat(project.isBuilding(), Matchers.equalTo(false));
        j.assertBuildStatusSuccess(build);
        assertJenkinsInQuietMode();
    }

    // 
    // Scenario - quiet-down is called with block and a timeout when executor is running
    // Expected result - CLI call shouldn't return (killed by other thread), execution won't be affected
    // 
    @Test
    public void quietDownShouldSuccessWithBlockPlusNonExpiredTimeoutAndRunningExecutor() throws Exception {
        final int TIMEOUT = 5000;
        final FreeStyleProject project = j.createFreeStyleProject("aProject");
        final ExecutorService threadPool = Executors.newSingleThreadExecutor();
        final OneShotEvent beforeCli = new OneShotEvent();
        final OneShotEvent finish = new OneShotEvent();
        final Future<FreeStyleBuild> build = OnlineNodeCommandTest.startBlockingAndFinishingBuild(project, finish);
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        boolean timeoutOccurred = false;
        final FutureTask exec_task = new FutureTask(new Callable() {
            public Object call() {
                assertJenkinsNotInQuietMode();
                beforeCli.signal();
                final CLICommandInvoker.Result result = command.authorizedTo(READ, ADMINISTER).invokeWithArgs("-block", "-timeout", Integer.toString((2 * TIMEOUT)));
                Assert.fail("Blocking call shouldn't finish, should be killed by called thread!");
                return null;
            }
        });
        threadPool.submit(exec_task);
        beforeCli.block();
        assertJenkinsInQuietMode();
        final boolean timeout_occured = false;
        try {
            exec_task.get(TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            timeoutOccurred = true;
        }
        if (!timeoutOccurred)
            Assert.fail("Missing timeout for CLI call");

        finish.signal();
        build.get();
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        MatcherAssert.assertThat(project.isBuilding(), Matchers.equalTo(false));
        j.assertBuildStatusSuccess(build);
        assertJenkinsInQuietMode();
    }

    // 
    // Scenario - quiet-down is called with block when executor is finishing
    // Expected result - CLI call finish and the execution too
    // 
    @Test
    public void quietDownShouldSuccessWithBlockAndFinishingExecutor() throws Exception {
        final FreeStyleProject project = j.createFreeStyleProject("aProject");
        final ExecutorService threadPool = Executors.newSingleThreadExecutor();
        final OneShotEvent beforeCli = new OneShotEvent();
        final OneShotEvent finish = new OneShotEvent();
        final Future<FreeStyleBuild> build = OnlineNodeCommandTest.startBlockingAndFinishingBuild(project, finish);
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        boolean timeoutOccurred = false;
        final FutureTask exec_task = new FutureTask(new Callable() {
            public Object call() {
                assertJenkinsNotInQuietMode();
                final long time_before = System.currentTimeMillis();
                beforeCli.signal();
                final CLICommandInvoker.Result result = command.authorizedTo(READ, ADMINISTER).invokeWithArgs("-block");
                MatcherAssert.assertThat(result, succeededSilently());
                MatcherAssert.assertThat(((System.currentTimeMillis()) > (time_before + 1000)), Matchers.equalTo(true));
                assertJenkinsInQuietMode();
                return null;
            }
        });
        threadPool.submit(exec_task);
        beforeCli.block();
        assertJenkinsInQuietMode();
        finish.signal();
        build.get();
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        MatcherAssert.assertThat(project.isBuilding(), Matchers.equalTo(false));
        j.assertBuildStatusSuccess(build);
        assertJenkinsInQuietMode();
        QuietDownCommandTest.get(exec_task);
        assertJenkinsInQuietMode();
    }

    // 
    // Scenario - quiet-down is called with block and timeout when executor is finishing
    // Expected result - CLI call finish and the execution too
    // 
    @Test
    public void quietDownShouldSuccessWithBlockAndNonExpiredTimeoutAndFinishingExecutor() throws Exception {
        final int TIMEOUT = 5000;
        final FreeStyleProject project = j.createFreeStyleProject("aProject");
        final ExecutorService threadPool = Executors.newSingleThreadExecutor();
        final OneShotEvent beforeCli = new OneShotEvent();
        final OneShotEvent finish = new OneShotEvent();
        final Future<FreeStyleBuild> build = OnlineNodeCommandTest.startBlockingAndFinishingBuild(project, finish);
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        final FutureTask exec_task = new FutureTask(new Callable() {
            public Object call() {
                assertJenkinsNotInQuietMode();
                final long time_before = System.currentTimeMillis();
                beforeCli.signal();
                final CLICommandInvoker.Result result = command.authorizedTo(READ, ADMINISTER).invokeWithArgs("-block", "-timeout", Integer.toString(TIMEOUT));
                MatcherAssert.assertThat(result, succeededSilently());
                MatcherAssert.assertThat(((System.currentTimeMillis()) > (time_before + 1000)), Matchers.equalTo(true));
                MatcherAssert.assertThat(((System.currentTimeMillis()) < (time_before + TIMEOUT)), Matchers.equalTo(true));
                assertJenkinsInQuietMode();
                return null;
            }
        });
        threadPool.submit(exec_task);
        beforeCli.block();
        assertJenkinsInQuietMode();
        finish.signal();
        build.get();
        MatcherAssert.assertThat(getBuilds(), Matchers.hasSize(1));
        MatcherAssert.assertThat(project.isBuilding(), Matchers.equalTo(false));
        j.assertBuildStatusSuccess(build);
        assertJenkinsInQuietMode();
        QuietDownCommandTest.get(exec_task);
    }
}

