/**
 * Copyright 2017 ThoughtWorks, Inc.
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
package com.thoughtworks.go.util;


import Level.ALL;
import com.thoughtworks.go.javasysmon.wrapper.DefaultCurrentProcess;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class SubprocessLoggerTest {
    private SubprocessLogger logger;

    @Test
    public void shouldNotLogAnythingWhenNoChildProcessesFound() {
        DefaultCurrentProcess currentProcess = Mockito.mock(DefaultCurrentProcess.class);
        logger = new SubprocessLogger(currentProcess);
        try (LogFixture log = LogFixture.logFixtureFor(SubprocessLogger.class, ALL)) {
            logger.run();
            String result;
            synchronized(log) {
                result = log.getLog();
            }
            Assert.assertThat(result, Matchers.is(""));
        }
    }

    @Test
    public void shouldLogDefaultMessageWhenNoMessageGiven() {
        logger = new SubprocessLogger(stubSysMon());
        String allLogs;
        try (LogFixture log = LogFixture.logFixtureFor(SubprocessLogger.class, ALL)) {
            logger.run();
            String result;
            synchronized(log) {
                result = log.getLog();
            }
            allLogs = result;
        }
        Assert.assertThat(allLogs, Matchers.containsString("Logged all subprocesses."));
    }

    @Test
    public void shouldLogAllTheRunningChildProcesses() {
        logger = new SubprocessLogger(stubSysMon());
        String allLogs;
        try (LogFixture log = LogFixture.logFixtureFor(SubprocessLogger.class, ALL)) {
            logger.registerAsExitHook("foo bar baz");
            logger.run();
            String result;
            synchronized(log) {
                result = log.getLog();
            }
            allLogs = result;
        }
        Assertions.assertThat(allLogs).isEqualToNormalizingNewlines(("WARN foo bar baz\n" + (("  101 name-1       100 owner-1       0Mb    0Mb 00:00:00 command-1              \n" + "  103 name-1a      100 owner-1       0Mb    0Mb 00:00:00 command-1a             \n") + "\n")));
        Assert.assertThat(allLogs, Matchers.not(Matchers.containsString("102")));
    }

    @Test
    public void shouldRegisterItselfAsExitHook() {
        logger = new SubprocessLogger(new DefaultCurrentProcess());
        logger.registerAsExitHook("foo");
        try {
            Runtime.getRuntime().addShutdownHook(logger.exitHook());
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Hook previously registered"));
        } finally {
            Runtime.getRuntime().removeShutdownHook(logger.exitHook());
        }
    }
}

