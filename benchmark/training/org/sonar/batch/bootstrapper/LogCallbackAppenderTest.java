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
package org.sonar.batch.bootstrapper;


import Level.DEBUG;
import Level.ERROR;
import Level.INFO;
import Level.OFF;
import Level.TRACE;
import Level.WARN;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.Test;
import org.mockito.Mockito;


public class LogCallbackAppenderTest {
    private LogOutput listener;

    private LogCallbackAppender appender;

    private ILoggingEvent event;

    @Test
    public void testLevelTranslation() {
        testMessage("test", INFO, LogOutput.Level.INFO);
        testMessage("test", DEBUG, LogOutput.Level.DEBUG);
        testMessage("test", ERROR, LogOutput.Level.ERROR);
        testMessage("test", TRACE, LogOutput.Level.TRACE);
        testMessage("test", WARN, LogOutput.Level.WARN);
        // this should never happen
        testMessage("test", OFF, LogOutput.Level.DEBUG);
    }

    @Test
    public void testChangeTarget() {
        listener = Mockito.mock(LogOutput.class);
        appender.setTarget(listener);
        testLevelTranslation();
    }
}

