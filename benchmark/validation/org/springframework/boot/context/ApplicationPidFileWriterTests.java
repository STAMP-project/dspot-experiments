/**
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.context;


import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;


/**
 * Tests for {@link ApplicationPidFileWriter}.
 *
 * @author Jakub Kubrynski
 * @author Dave Syer
 * @author Phillip Webb
 * @author Tomasz Przybyla
 */
public class ApplicationPidFileWriterTests {
    private static final ApplicationPreparedEvent EVENT = new ApplicationPreparedEvent(new SpringApplication(), new String[]{  }, Mockito.mock(ConfigurableApplicationContext.class));

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void createPidFile() throws Exception {
        File file = this.temporaryFolder.newFile();
        ApplicationPidFileWriter listener = new ApplicationPidFileWriter(file);
        listener.onApplicationEvent(ApplicationPidFileWriterTests.EVENT);
        assertThat(contentOf(file)).isNotEmpty();
    }

    @Test
    public void overridePidFile() throws Exception {
        File file = this.temporaryFolder.newFile();
        System.setProperty("PIDFILE", this.temporaryFolder.newFile().getAbsolutePath());
        ApplicationPidFileWriter listener = new ApplicationPidFileWriter(file);
        listener.onApplicationEvent(ApplicationPidFileWriterTests.EVENT);
        assertThat(contentOf(new File(System.getProperty("PIDFILE")))).isNotEmpty();
    }

    @Test
    public void overridePidFileWithSpring() throws Exception {
        File file = this.temporaryFolder.newFile();
        SpringApplicationEvent event = createPreparedEvent("spring.pid.file", file.getAbsolutePath());
        ApplicationPidFileWriter listener = new ApplicationPidFileWriter();
        listener.onApplicationEvent(event);
        assertThat(contentOf(file)).isNotEmpty();
    }

    @Test
    public void tryEnvironmentPreparedEvent() throws Exception {
        File file = this.temporaryFolder.newFile();
        SpringApplicationEvent event = createEnvironmentPreparedEvent("spring.pid.file", file.getAbsolutePath());
        ApplicationPidFileWriter listener = new ApplicationPidFileWriter();
        listener.onApplicationEvent(event);
        assertThat(contentOf(file)).isEmpty();
        listener.setTriggerEventType(ApplicationEnvironmentPreparedEvent.class);
        listener.onApplicationEvent(event);
        assertThat(contentOf(file)).isNotEmpty();
    }

    @Test
    public void tryReadyEvent() throws Exception {
        File file = this.temporaryFolder.newFile();
        SpringApplicationEvent event = createReadyEvent("spring.pid.file", file.getAbsolutePath());
        ApplicationPidFileWriter listener = new ApplicationPidFileWriter();
        listener.onApplicationEvent(event);
        assertThat(contentOf(file)).isEmpty();
        listener.setTriggerEventType(ApplicationReadyEvent.class);
        listener.onApplicationEvent(event);
        assertThat(contentOf(file)).isNotEmpty();
    }

    @Test
    public void withNoEnvironment() throws Exception {
        File file = this.temporaryFolder.newFile();
        ApplicationPidFileWriter listener = new ApplicationPidFileWriter(file);
        listener.setTriggerEventType(ApplicationStartingEvent.class);
        listener.onApplicationEvent(new ApplicationStartingEvent(new SpringApplication(), new String[]{  }));
        assertThat(contentOf(file)).isNotEmpty();
    }

    @Test
    public void continueWhenPidFileIsReadOnly() throws Exception {
        File file = this.temporaryFolder.newFile();
        file.setReadOnly();
        ApplicationPidFileWriter listener = new ApplicationPidFileWriter(file);
        listener.onApplicationEvent(ApplicationPidFileWriterTests.EVENT);
        assertThat(contentOf(file)).isEmpty();
    }

    @Test
    public void throwWhenPidFileIsReadOnly() throws Exception {
        File file = this.temporaryFolder.newFile();
        file.setReadOnly();
        System.setProperty("PID_FAIL_ON_WRITE_ERROR", "true");
        ApplicationPidFileWriter listener = new ApplicationPidFileWriter(file);
        assertThatIllegalStateException().isThrownBy(() -> listener.onApplicationEvent(EVENT)).withMessageContaining("Cannot create pid file");
    }

    @Test
    public void throwWhenPidFileIsReadOnlyWithSpring() throws Exception {
        File file = this.temporaryFolder.newFile();
        file.setReadOnly();
        SpringApplicationEvent event = createPreparedEvent("spring.pid.fail-on-write-error", "true");
        ApplicationPidFileWriter listener = new ApplicationPidFileWriter(file);
        assertThatIllegalStateException().isThrownBy(() -> listener.onApplicationEvent(event)).withMessageContaining("Cannot create pid file");
    }
}

