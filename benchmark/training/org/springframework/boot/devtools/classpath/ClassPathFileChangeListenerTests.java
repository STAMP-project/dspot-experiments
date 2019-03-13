/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.devtools.classpath;


import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.devtools.filewatch.FileSystemWatcher;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;


/**
 * Tests for {@link ClassPathFileChangeListener}.
 *
 * @author Phillip Webb
 */
public class ClassPathFileChangeListenerTests {
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private ClassPathRestartStrategy restartStrategy;

    @Mock
    private FileSystemWatcher fileSystemWatcher;

    @Captor
    private ArgumentCaptor<ApplicationEvent> eventCaptor;

    @Test
    public void eventPublisherMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new ClassPathFileChangeListener(null, this.restartStrategy, this.fileSystemWatcher)).withMessageContaining("EventPublisher must not be null");
    }

    @Test
    public void restartStrategyMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new ClassPathFileChangeListener(this.eventPublisher, null, this.fileSystemWatcher)).withMessageContaining("RestartStrategy must not be null");
    }

    @Test
    public void sendsEventWithoutRestart() {
        testSendsEvent(false);
        Mockito.verify(this.fileSystemWatcher, Mockito.never()).stop();
    }

    @Test
    public void sendsEventWithRestart() {
        testSendsEvent(true);
        Mockito.verify(this.fileSystemWatcher).stop();
    }
}

