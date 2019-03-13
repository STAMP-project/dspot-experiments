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
package com.thoughtworks.go.plugin.infra.service;


import Level.WARN;
import ch.qos.logback.core.FileAppender;
import com.googlecode.junit.ext.checkers.OSChecker;
import com.thoughtworks.go.util.LogFixture;
import com.thoughtworks.go.util.SystemEnvironment;
import java.nio.charset.Charset;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class DefaultPluginLoggingServiceIntegrationTest {
    private static OSChecker WINDOWS = new OSChecker(OSChecker.WINDOWS);

    private DefaultPluginLoggingService pluginLoggingService;

    private Map<Integer, String> plugins;

    private SystemEnvironment systemEnvironment;

    @Test
    public void shouldNotLogPluginMessagesToRootLogger() throws Exception {
        try (LogFixture fixture = logFixtureForRootLogger(Level.INFO)) {
            DefaultPluginLoggingService service = new DefaultPluginLoggingService(systemEnvironment);
            service.info(pluginID(1), "LoggingClass", "this-message-should-not-go-to-root-logger");
            String failureMessage = "Expected no messages to be logged to root logger. Found: " + (fixture.getFormattedMessages());
            Assert.assertThat(failureMessage, fixture.getFormattedMessages().size(), Matchers.is(0));
        }
    }

    @Test
    public void shouldGetLogLocationFromRootLoggerFileAppender() throws Exception {
        DefaultPluginLoggingService service = new DefaultPluginLoggingService(systemEnvironment);
        DefaultPluginLoggingService spy = Mockito.spy(service);
        spy.info(pluginID(1), "LoggingClass", "message");
        Mockito.verify(spy).getCurrentLogDirectory();
    }

    @Test
    public void shouldGetCurrentLogDirectoryByLookingAtFileAppenderOfRootLogger() throws Exception {
        if (DefaultPluginLoggingServiceIntegrationTest.WINDOWS.satisfy()) {
            return;
        }
        FileAppender fileAppender = new FileAppender();
        fileAppender.setFile("/var/log/go-server/go-server.log");
        DefaultPluginLoggingService service = Mockito.spy(new DefaultPluginLoggingService(systemEnvironment));
        Mockito.doReturn(fileAppender).when(service).getGoServerLogFileAppender();
        String currentLogDirectory = service.getCurrentLogDirectory();
        Assert.assertThat(currentLogDirectory, Matchers.is("/var/log/go-server"));
    }

    @Test
    public void shouldNotLogDebugMessagesByDefaultSinceTheDefaultLoggingLevelIsInfo() throws Exception {
        pluginLoggingService.debug(pluginID(1), "LoggingClass", "message");
        Assert.assertThat(FileUtils.readFileToString(pluginLog(1), Charset.defaultCharset()), Matchers.is(""));
    }

    @Test
    public void shouldLogNonDebugMessagesByDefaultSinceTheDefaultLoggingLevelIsInfo() throws Exception {
        pluginLoggingService.info(pluginID(1), "LoggingClass", "info");
        pluginLoggingService.warn(pluginID(1), "LoggingClass", "warn");
        pluginLoggingService.error(pluginID(1), "LoggingClass", "error");
        assertNumberOfMessagesInLog(pluginLog(1), 3);
        assertMessageInLog(pluginLog(1), "INFO", "LoggingClass", "info");
        assertMessageInLog(pluginLog(1), "WARN", "LoggingClass", "warn");
        assertMessageInLog(pluginLog(1), "ERROR", "LoggingClass", "error");
    }

    @Test
    public void shouldLogThrowableDetailsAlongwithMessage() throws Exception {
        Throwable throwable = new RuntimeException("oops");
        throwable.setStackTrace(new StackTraceElement[]{ new StackTraceElement("class", "method", "field", 20) });
        pluginLoggingService.error(pluginID(1), "LoggingClass", "error", throwable);
        assertMessageInLog(pluginLog(1), "ERROR", "LoggingClass", "error", "java\\.lang\\.RuntimeException:\\soops[\\s\\S]*at\\sclass\\.method\\(field:20\\)[\\s\\S]*$");
    }

    @Test
    public void shouldUsePluginLogFileForAllLogMessagesOfASinglePlugin() throws Exception {
        pluginLoggingService.info(pluginID(1), "LoggingClass", "info1");
        pluginLoggingService.warn(pluginID(1), "SomeOtherClass", "info2");
        assertNumberOfMessagesInLog(pluginLog(1), 2);
        assertMessageInLog(pluginLog(1), "INFO", "LoggingClass", "info1");
        assertMessageInLog(pluginLog(1), "WARN", "SomeOtherClass", "info2");
    }

    @Test
    public void shouldLogMessagesOfDifferentPluginsToTheirOwnLogFiles() throws Exception {
        pluginLoggingService.info(pluginID(1), "LoggingClass", "info1");
        pluginLoggingService.info(pluginID(2), "SomeOtherClass", "info2");
        assertNumberOfMessagesInLog(pluginLog(1), 1);
        assertMessageInLog(pluginLog(1), "INFO", "LoggingClass", "info1");
        assertNumberOfMessagesInLog(pluginLog(2), 1);
        assertMessageInLog(pluginLog(2), "INFO", "SomeOtherClass", "info2");
    }

    @Test(timeout = 10 * 1000)
    public void shouldAllowLoggingAcrossMultipleThreadsAndPlugins() throws Exception {
        Thread thread1 = createThreadFor(pluginID(1), "1");
        Thread thread2 = createThreadFor(pluginID(2), "2");
        Thread thread3 = createThreadFor(pluginID(1), "3");
        Thread thread4 = createThreadFor(pluginID(2), "4");
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread1.join();
        thread2.join();
        thread3.join();
        thread4.join();
        assertNumberOfMessagesInLog(pluginLog(1), 200);
        assertNumberOfMessagesInLog(pluginLog(2), 200);
    }

    @Test
    public void shouldAllowSettingLoggingLevelPerPlugin() throws Exception {
        Mockito.when(systemEnvironment.pluginLoggingLevel(pluginID(1))).thenReturn(WARN);
        pluginLoggingService.debug(pluginID(1), "LoggingClass", "debug");
        pluginLoggingService.info(pluginID(1), "LoggingClass", "info");
        pluginLoggingService.warn(pluginID(1), "LoggingClass", "warn");
        pluginLoggingService.error(pluginID(1), "SomeOtherClass", "error");
        assertNumberOfMessagesInLog(pluginLog(1), 2);
        assertMessageInLog(pluginLog(1), "WARN", "LoggingClass", "warn");
        assertMessageInLog(pluginLog(1), "ERROR", "SomeOtherClass", "error");
    }
}

