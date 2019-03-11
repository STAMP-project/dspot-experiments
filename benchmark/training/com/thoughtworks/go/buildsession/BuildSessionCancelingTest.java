/**
 * Copyright 2016 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.buildsession;


import com.google.common.collect.Iterables;
import com.googlecode.junit.ext.JunitExtRunner;
import com.googlecode.junit.ext.RunIf;
import com.thoughtworks.go.domain.BuildCommand;
import com.thoughtworks.go.domain.JobResult;
import com.thoughtworks.go.javasysmon.wrapper.DefaultCurrentProcess;
import com.thoughtworks.go.junitext.EnhancedOSChecker;
import com.thoughtworks.go.util.ExceptionUtils;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JunitExtRunner.class)
public class BuildSessionCancelingTest extends BuildSessionBasedTestCase {
    @Test
    public void cancelLongRunningBuild() throws InterruptedException {
        final BuildSession buildSession = newBuildSession();
        Thread buildingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                buildSession.build(compose(BuildSessionBasedTestCase.execSleepScript(50), echo("build done")));
            }
        });
        buildingThread.start();
        waitUntilSubProcessExists(execSleepScriptProcessCommand(), true);
        Assert.assertTrue(buildInfo(), buildSession.cancel(30, TimeUnit.SECONDS));
        waitUntilSubProcessExists(execSleepScriptProcessCommand(), false);
        Assert.assertThat(buildInfo(), Iterables.getLast(statusReporter.results()), Matchers.is(JobResult.Cancelled));
        Assert.assertThat(buildInfo(), console.output(), Matchers.not(Matchers.containsString("build done")));
        buildingThread.join();
    }

    @Test
    public void cancelLongRunningTestCommand() throws InterruptedException {
        final BuildSession buildSession = newBuildSession();
        Thread buildingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                buildSession.build(compose(echo("after sleep").setTest(BuildSessionBasedTestCase.execSleepScript(50))));
            }
        });
        buildingThread.start();
        waitUntilSubProcessExists(execSleepScriptProcessCommand(), true);
        Assert.assertTrue(buildInfo(), buildSession.cancel(30, TimeUnit.SECONDS));
        waitUntilSubProcessExists(execSleepScriptProcessCommand(), false);
        Assert.assertThat(buildInfo(), Iterables.getLast(statusReporter.results()), Matchers.is(JobResult.Cancelled));
        Assert.assertThat(buildInfo(), console.output(), Matchers.not(Matchers.containsString("after sleep")));
        buildingThread.join();
    }

    @Test
    public void doubleCancelDoNothing() throws InterruptedException {
        final BuildSession buildSession = newBuildSession();
        Thread buildingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                buildSession.build(BuildSessionBasedTestCase.execSleepScript(50));
            }
        });
        Runnable cancel = new Runnable() {
            @Override
            public void run() {
                try {
                    buildSession.cancel(30, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    throw ExceptionUtils.bomb(e);
                }
            }
        };
        Thread cancelThread1 = new Thread(cancel);
        Thread cancelThread2 = new Thread(cancel);
        buildingThread.start();
        waitUntilSubProcessExists(execSleepScriptProcessCommand(), true);
        cancelThread1.start();
        cancelThread2.start();
        cancelThread1.join();
        cancelThread2.join();
        waitUntilSubProcessExists(execSleepScriptProcessCommand(), false);
        Assert.assertThat(buildInfo(), Iterables.getLast(statusReporter.results()), Matchers.is(JobResult.Cancelled));
        Assert.assertThat(buildInfo(), console.output(), Matchers.not(Matchers.containsString("after sleep")));
        buildingThread.join();
    }

    @Test
    public void cancelShouldProcessOnCancelCommandOfCommandThatIsRunning() throws InterruptedException {
        final BuildSession buildSession = newBuildSession();
        Thread buildingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                buildSession.build(compose(compose(BuildSessionBasedTestCase.execSleepScript(50).setOnCancel(echo("exec canceled")), echo("after sleep")).setOnCancel(echo("inner oncancel"))).setOnCancel(echo("outter oncancel")));
            }
        });
        buildingThread.start();
        waitUntilSubProcessExists(execSleepScriptProcessCommand(), true);
        Assert.assertTrue(buildInfo(), buildSession.cancel(30, TimeUnit.SECONDS));
        waitUntilSubProcessExists(execSleepScriptProcessCommand(), false);
        DefaultCurrentProcess currentProcess = new DefaultCurrentProcess();
        Assert.assertThat(currentProcess.immediateChildren(), Matchers.is(Matchers.empty()));
        Assert.assertThat(buildInfo(), Iterables.getLast(statusReporter.results()), Matchers.is(JobResult.Cancelled));
        Assert.assertThat(buildInfo(), console.output(), Matchers.not(Matchers.containsString("after sleep")));
        Assert.assertThat(buildInfo(), console.output(), Matchers.containsString("exec canceled"));
        Assert.assertThat(buildInfo(), console.output(), Matchers.containsString("inner oncancel"));
        Assert.assertThat(buildInfo(), console.output(), Matchers.containsString("outter oncancel"));
        buildingThread.join();
    }

    @Test
    @RunIf(value = EnhancedOSChecker.class, arguments = { DO_NOT_RUN_ON, WINDOWS })
    public void cancelTaskShouldBeProcessedBeforeKillChildProcess() throws InterruptedException {
        final BuildSession buildSession = newBuildSession();
        final BuildCommand printSubProcessCount = exec("/bin/bash", "-c", (("pgrep -P " + (new DefaultCurrentProcess().currentPid())) + " | wc -l"));
        Thread buildingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                buildSession.build(compose(compose(BuildSessionBasedTestCase.execSleepScript(50), echo("after sleep")).setOnCancel(printSubProcessCount)));
            }
        });
        buildingThread.start();
        waitUntilSubProcessExists(execSleepScriptProcessCommand(), true);
        Assert.assertTrue(buildInfo(), buildSession.cancel(30, TimeUnit.SECONDS));
        waitUntilSubProcessExists(execSleepScriptProcessCommand(), false);
        Assert.assertThat(Integer.parseInt(console.lastLine().trim()), Matchers.greaterThan(0));
        buildingThread.join();
    }
}

