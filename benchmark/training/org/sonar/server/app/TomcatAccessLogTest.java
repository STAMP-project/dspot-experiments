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
package org.sonar.server.app;


import TomcatAccessLog.LifecycleLogger;
import java.util.Properties;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.startup.Tomcat;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.utils.log.Logger;
import org.sonar.process.Props;


public class TomcatAccessLogTest {
    TomcatAccessLog underTest = new TomcatAccessLog();

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void enable_access_logs_by_Default() throws Exception {
        Tomcat tomcat = Mockito.mock(Tomcat.class, Mockito.RETURNS_DEEP_STUBS);
        Props props = new Props(new Properties());
        props.set(PATH_LOGS.getKey(), temp.newFolder().getAbsolutePath());
        underTest.configure(tomcat, props);
        Mockito.verify(tomcat.getHost().getPipeline()).addValve(ArgumentMatchers.any(ProgrammaticLogbackValve.class));
    }

    @Test
    public void log_when_started_and_stopped() {
        Logger logger = Mockito.mock(Logger.class);
        TomcatAccessLog.LifecycleLogger listener = new TomcatAccessLog.LifecycleLogger(logger);
        LifecycleEvent event = new LifecycleEvent(Mockito.mock(Lifecycle.class), "before_init", null);
        listener.lifecycleEvent(event);
        Mockito.verifyZeroInteractions(logger);
        event = new LifecycleEvent(Mockito.mock(Lifecycle.class), "after_start", null);
        listener.lifecycleEvent(event);
        Mockito.verify(logger).debug("Tomcat is started");
        event = new LifecycleEvent(Mockito.mock(Lifecycle.class), "after_destroy", null);
        listener.lifecycleEvent(event);
        Mockito.verify(logger).debug("Tomcat is stopped");
    }
}

