/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.core.util.logs;


import LoggerLevel.DEBUG;
import LoggerLevel.ERROR;
import LoggerLevel.INFO;
import LoggerLevel.TRACE;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sonar.api.utils.log.LogTester;
import org.sonar.api.utils.log.Loggers;


@RunWith(DataProviderRunner.class)
public class DefaultProfilerTest {
    @Rule
    public LogTester tester = new LogTester();

    Profiler underTest = Profiler.create(Loggers.get("DefaultProfilerTest"));

    @Test
    public void test_levels() {
        // info by default
        assertThat(underTest.isDebugEnabled()).isFalse();
        assertThat(underTest.isTraceEnabled()).isFalse();
        tester.setLevel(DEBUG);
        assertThat(underTest.isDebugEnabled()).isTrue();
        assertThat(underTest.isTraceEnabled()).isFalse();
        tester.setLevel(TRACE);
        assertThat(underTest.isDebugEnabled()).isTrue();
        assertThat(underTest.isTraceEnabled()).isTrue();
    }

    @Test
    public void start_writes_no_log_even_if_there_is_context() {
        underTest.addContext("a_string", "bar");
        underTest.addContext("null_value", null);
        underTest.addContext("an_int", 42);
        underTest.start();
        // do not write context as there's no message
        assertThat(tester.logs()).isEmpty();
    }

    @Test
    public void startInfo_writes_log_with_context_appended_when_there_is_a_message() {
        DefaultProfilerTest.addSomeContext(underTest);
        underTest.startInfo("Foo");
        assertThat(tester.logs(INFO)).containsOnly("Foo | a_string=bar | an_int=42 | after_start=true");
    }

    @Test
    public void startDebug_writes_log_with_context_appended_when_there_is_a_message() {
        tester.setLevel(DEBUG);
        DefaultProfilerTest.addSomeContext(underTest);
        underTest.startDebug("Foo");
        assertThat(tester.logs(DEBUG)).containsOnly("Foo | a_string=bar | an_int=42 | after_start=true");
    }

    @Test
    public void startTrace_writes_log_with_context_appended_when_there_is_a_message() {
        tester.setLevel(TRACE);
        DefaultProfilerTest.addSomeContext(underTest);
        underTest.startTrace("Foo");
        assertThat(tester.logs(TRACE)).containsOnly("Foo | a_string=bar | an_int=42 | after_start=true");
    }

    @Test
    public void stopError_adds_context_after_time_by_default() {
        DefaultProfilerTest.addSomeContext(underTest);
        underTest.start().stopError("Rules registered");
        assertThat(tester.logs()).hasSize(1);
        assertThat(tester.logs(ERROR).get(0)).startsWith("Rules registered | time=").endsWith("ms | a_string=bar | an_int=42 | after_start=true");
    }

    @Test
    public void stopInfo_adds_context_after_time_by_default() {
        DefaultProfilerTest.addSomeContext(underTest);
        underTest.start().stopInfo("Rules registered");
        assertThat(tester.logs()).hasSize(1);
        assertThat(tester.logs(INFO).get(0)).startsWith("Rules registered | time=").endsWith("ms | a_string=bar | an_int=42 | after_start=true");
    }

    @Test
    public void stopTrace_adds_context_after_time_by_default() {
        tester.setLevel(TRACE);
        DefaultProfilerTest.addSomeContext(underTest);
        underTest.start().stopTrace("Rules registered");
        assertThat(tester.logs()).hasSize(1);
        assertThat(tester.logs(TRACE).get(0)).startsWith("Rules registered | time=").endsWith("ms | a_string=bar | an_int=42 | after_start=true");
    }

    @Test
    public void stopError_adds_context_before_time_if_logTimeLast_is_true() {
        DefaultProfilerTest.addSomeContext(underTest);
        underTest.logTimeLast(true);
        underTest.start().stopError("Rules registered");
        assertThat(tester.logs()).hasSize(1);
        assertThat(tester.logs(ERROR).get(0)).startsWith("Rules registered | a_string=bar | an_int=42 | after_start=true | time=").endsWith("ms");
    }

    @Test
    public void stopInfo_adds_context_before_time_if_logTimeLast_is_true() {
        DefaultProfilerTest.addSomeContext(underTest);
        underTest.logTimeLast(true);
        underTest.start().stopInfo("Rules registered");
        assertThat(tester.logs()).hasSize(1);
        assertThat(tester.logs(INFO).get(0)).startsWith("Rules registered | a_string=bar | an_int=42 | after_start=true | time=").endsWith("ms");
    }

    @Test
    public void stopTrace_adds_context_before_time_if_logTimeLast_is_true() {
        tester.setLevel(TRACE);
        DefaultProfilerTest.addSomeContext(underTest);
        underTest.logTimeLast(true);
        underTest.start().stopTrace("Rules registered");
        assertThat(tester.logs()).hasSize(1);
        assertThat(tester.logs(TRACE).get(0)).startsWith("Rules registered | a_string=bar | an_int=42 | after_start=true | time=").endsWith("ms");
    }

    @Test
    public void stopInfo_clears_context() {
        DefaultProfilerTest.addSomeContext(underTest);
        underTest.logTimeLast(true);
        underTest.start().stopInfo("Foo");
        underTest.start().stopInfo("Bar");
        assertThat(tester.logs()).hasSize(2);
        List<String> logs = tester.logs(INFO);
        assertThat(logs.get(0)).startsWith("Foo | a_string=bar | an_int=42 | after_start=true | time=").endsWith("ms");
        assertThat(logs.get(1)).startsWith("Bar | time=").endsWith("ms");
    }

    @Test
    public void stopDebug_clears_context() {
        tester.setLevel(DEBUG);
        DefaultProfilerTest.addSomeContext(underTest);
        underTest.logTimeLast(true);
        underTest.start().stopDebug("Foo");
        underTest.start().stopDebug("Bar");
        assertThat(tester.logs()).hasSize(2);
        List<String> logs = tester.logs(DEBUG);
        assertThat(logs.get(0)).startsWith("Foo | a_string=bar | an_int=42 | after_start=true | time=").endsWith("ms");
        assertThat(logs.get(1)).startsWith("Bar | time=").endsWith("ms");
    }

    @Test
    public void stopTrace_clears_context() {
        tester.setLevel(TRACE);
        DefaultProfilerTest.addSomeContext(underTest);
        underTest.logTimeLast(true);
        underTest.start().stopTrace("Foo");
        underTest.start().stopTrace("Bar");
        assertThat(tester.logs()).hasSize(2);
        List<String> logs = tester.logs(TRACE);
        assertThat(logs.get(0)).startsWith("Foo | a_string=bar | an_int=42 | after_start=true | time=").endsWith("ms");
        assertThat(logs.get(1)).startsWith("Bar | time=").endsWith("ms");
    }

    @Test
    public void empty_message() {
        underTest.addContext("foo", "bar");
        underTest.startInfo("");
        assertThat(tester.logs()).containsOnly("foo=bar");
        underTest.addContext("after_start", true);
        underTest.stopInfo("");
        assertThat(tester.logs()).hasSize(2);
        assertThat(tester.logs().get(1)).startsWith("time=").endsWith("ms | foo=bar | after_start=true");
    }

    @Test
    public void fail_if_stop_without_message() {
        underTest.start();
        try {
            underTest.stopInfo();
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Profiler#stopXXX() can't be called without any message defined in start methods");
        }
    }

    @Test
    public void fail_if_stop_without_start() {
        try {
            underTest.stopDebug("foo");
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Profiler must be started before being stopped");
        }
    }

    @Test
    public void hasContext() {
        assertThat(underTest.hasContext("foo")).isFalse();
        underTest.addContext("foo", "bar");
        assertThat(underTest.hasContext("foo")).isTrue();
        underTest.addContext("foo", null);
        assertThat(underTest.hasContext("foo")).isFalse();
    }
}

