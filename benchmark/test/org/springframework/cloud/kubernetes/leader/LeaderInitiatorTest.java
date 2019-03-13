/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.kubernetes.leader;


import java.time.Duration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Gytis Trikleris
 */
@RunWith(MockitoJUnitRunner.class)
public class LeaderInitiatorTest {
    @Mock
    private LeaderProperties mockLeaderProperties;

    @Mock
    private LeadershipController mockLeadershipController;

    @Mock
    private LeaderRecordWatcher mockLeaderRecordWatcher;

    @Mock
    private PodReadinessWatcher mockPodReadinessWatcher;

    @Mock
    private Runnable mockRunnable;

    private LeaderInitiator leaderInitiator;

    @Test
    public void testIsAutoStartup() {
        BDDMockito.given(this.mockLeaderProperties.isAutoStartup()).willReturn(true);
        assertThat(this.leaderInitiator.isAutoStartup()).isTrue();
    }

    @Test
    public void shouldStart() throws InterruptedException {
        BDDMockito.given(this.mockLeaderProperties.getUpdatePeriod()).willReturn(Duration.ofMillis(1L));
        this.leaderInitiator.start();
        assertThat(this.leaderInitiator.isRunning()).isTrue();
        Mockito.verify(this.mockLeaderRecordWatcher).start();
        Mockito.verify(this.mockPodReadinessWatcher).start();
        Thread.sleep(10);
        Mockito.verify(this.mockLeadershipController, VerificationModeFactory.atLeastOnce()).update();
    }

    @Test
    public void shouldStartOnlyOnce() {
        BDDMockito.given(this.mockLeaderProperties.getUpdatePeriod()).willReturn(Duration.ofMillis(10000L));
        this.leaderInitiator.start();
        this.leaderInitiator.start();
        Mockito.verify(this.mockLeaderRecordWatcher).start();
    }

    @Test
    public void shouldStop() {
        BDDMockito.given(this.mockLeaderProperties.getUpdatePeriod()).willReturn(Duration.ofMillis(10000L));
        this.leaderInitiator.start();
        this.leaderInitiator.stop();
        assertThat(this.leaderInitiator.isRunning()).isFalse();
        Mockito.verify(this.mockLeaderRecordWatcher).stop();
        Mockito.verify(this.mockPodReadinessWatcher).start();
        Mockito.verify(this.mockLeadershipController).revoke();
    }

    @Test
    public void shouldStopOnlyOnce() {
        BDDMockito.given(this.mockLeaderProperties.getUpdatePeriod()).willReturn(Duration.ofMillis(10000L));
        this.leaderInitiator.start();
        this.leaderInitiator.stop();
        this.leaderInitiator.stop();
        Mockito.verify(this.mockLeaderRecordWatcher).stop();
    }

    @Test
    public void shouldStopAndExecuteCallback() {
        BDDMockito.given(this.mockLeaderProperties.getUpdatePeriod()).willReturn(Duration.ofMillis(10000L));
        this.leaderInitiator.start();
        this.leaderInitiator.stop(this.mockRunnable);
        assertThat(this.leaderInitiator.isRunning()).isFalse();
        Mockito.verify(this.mockLeaderRecordWatcher).stop();
        Mockito.verify(this.mockPodReadinessWatcher).start();
        Mockito.verify(this.mockLeadershipController).revoke();
        Mockito.verify(this.mockRunnable).run();
    }
}

