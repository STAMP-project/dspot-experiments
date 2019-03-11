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
package org.sonar.api.utils.log;


import LoggerLevel.DEBUG;
import LoggerLevel.ERROR;
import LoggerLevel.INFO;
import LoggerLevel.TRACE;
import LoggerLevel.WARN;
import java.io.PrintStream;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ConsoleLoggerTest {
    private PrintStream stream = Mockito.mock(PrintStream.class);

    private ConsoleLogger underTest = new ConsoleLogger(stream);

    @Rule
    public LogTester tester = new LogTester();

    @Test
    public void debug_enabled() {
        tester.setLevel(DEBUG);
        assertThat(underTest.isDebugEnabled()).isTrue();
        assertThat(underTest.isTraceEnabled()).isFalse();
        underTest.debug("message");
        underTest.debug("message {}", "foo");
        underTest.debug("message {} {}", "foo", "bar");
        underTest.debug("message {} {} {}", "foo", "bar", "baz");
        Mockito.verify(stream).println("DEBUG message");
        Mockito.verify(stream).println("DEBUG message foo");
        Mockito.verify(stream).println("DEBUG message foo bar");
        Mockito.verify(stream).println("DEBUG message foo bar baz");
        assertThat(tester.logs(DEBUG)).containsExactly("message", "message foo", "message foo bar", "message foo bar baz");
    }

    @Test
    public void debug_disabled() {
        tester.setLevel(INFO);
        assertThat(underTest.isDebugEnabled()).isFalse();
        assertThat(underTest.isTraceEnabled()).isFalse();
        underTest.debug("message");
        underTest.debug("message {}", "foo");
        underTest.debug("message {} {}", "foo", "bar");
        underTest.debug("message {} {} {}", "foo", "bar", "baz");
        Mockito.verifyZeroInteractions(stream);
    }

    @Test
    public void trace_enabled() {
        tester.setLevel(TRACE);
        assertThat(underTest.isDebugEnabled()).isTrue();
        assertThat(underTest.isTraceEnabled()).isTrue();
        underTest.trace("message");
        underTest.trace("message {}", "foo");
        underTest.trace("message {} {}", "foo", "bar");
        underTest.trace("message {} {} {}", "foo", "bar", "baz");
        Mockito.verify(stream, Mockito.times(4)).println(ArgumentMatchers.anyString());
    }

    @Test
    public void trace_disabled() {
        tester.setLevel(DEBUG);
        assertThat(underTest.isTraceEnabled()).isFalse();
        underTest.trace("message");
        underTest.trace("message {}", "foo");
        underTest.trace("message {} {}", "foo", "bar");
        underTest.trace("message {} {} {}", "foo", "bar", "baz");
        Mockito.verifyZeroInteractions(stream);
    }

    @Test
    public void log_info() {
        underTest.info("message");
        underTest.info("message {}", "foo");
        underTest.info("message {} {}", "foo", "bar");
        underTest.info("message {} {} {}", "foo", "bar", "baz");
        Mockito.verify(stream).println("INFO  message");
        Mockito.verify(stream).println("INFO  message foo");
        Mockito.verify(stream).println("INFO  message foo bar");
        Mockito.verify(stream).println("INFO  message foo bar baz");
        assertThat(tester.logs(INFO)).containsExactly("message", "message foo", "message foo bar", "message foo bar baz");
    }

    @Test
    public void log_warn() {
        Throwable throwable = Mockito.mock(Throwable.class);
        underTest.warn("message");
        underTest.warn("message {}", "foo");
        underTest.warn("message {} {}", "foo", "bar");
        underTest.warn("message {} {} {}", "foo", "bar", "baz");
        underTest.warn("message with exception", throwable);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(stream, Mockito.times(5)).println(captor.capture());
        for (String msg : captor.getAllValues()) {
            assertThat(msg).startsWith("WARN ");
        }
        Mockito.verify(throwable).printStackTrace();
        assertThat(tester.logs(WARN)).containsExactly("message", "message foo", "message foo bar", "message foo bar baz", "message with exception");
    }

    @Test
    public void log_error() {
        underTest.error("message");
        underTest.error("message {}", "foo");
        underTest.error("message {} {}", "foo", "bar");
        underTest.error("message {} {} {}", "foo", "bar", "baz");
        underTest.error("message with exception", new IllegalArgumentException());
        Mockito.verify(stream, Mockito.times(5)).println(ArgumentMatchers.startsWith("ERROR "));
        assertThat(tester.logs(ERROR)).containsExactly("message", "message foo", "message foo bar", "message foo bar baz", "message with exception");
    }

    @Test
    public void level_change_not_implemented_yet() {
        assertThat(underTest.setLevel(DEBUG)).isFalse();
    }
}

