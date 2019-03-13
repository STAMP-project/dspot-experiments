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


import Watcher.Action.ADDED;
import io.fabric8.kubernetes.api.model.DoneablePod;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.PodResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Gytis Trikleris
 */
@RunWith(MockitoJUnitRunner.class)
public class PodReadinessWatcherTest {
    private static final String POD_NAME = "test-pod";

    @Mock
    private LeadershipController mockLeadershipController;

    @Mock
    private KubernetesClient mockKubernetesClient;

    @Mock
    private MixedOperation<Pod, PodList, DoneablePod, PodResource<Pod, DoneablePod>> mockPodsOperation;

    @Mock
    private PodResource<Pod, DoneablePod> mockPodResource;

    @Mock
    private Pod mockPod;

    @Mock
    private PodStatus mockPodStatus;

    @Mock
    private Watch mockWatch;

    @Mock
    private KubernetesClientException mockKubernetesClientException;

    private PodReadinessWatcher watcher;

    @Test
    public void shouldStartOnce() {
        this.watcher.start();
        this.watcher.start();
        Mockito.verify(this.mockPodResource).watch(this.watcher);
    }

    @Test
    public void shouldStopOnce() {
        this.watcher.start();
        this.watcher.stop();
        this.watcher.stop();
        Mockito.verify(this.mockWatch).close();
    }

    @Test
    public void shouldHandleEventWithStateChange() {
        BDDMockito.given(this.mockPodResource.isReady()).willReturn(true);
        BDDMockito.given(this.mockPod.getStatus()).willReturn(this.mockPodStatus);
        this.watcher.start();
        this.watcher.eventReceived(ADDED, this.mockPod);
        Mockito.verify(this.mockLeadershipController).update();
    }

    @Test
    public void shouldIgnoreEventIfStateDoesNotChange() {
        BDDMockito.given(this.mockPod.getStatus()).willReturn(this.mockPodStatus);
        this.watcher.start();
        this.watcher.eventReceived(ADDED, this.mockPod);
        Mockito.verify(this.mockLeadershipController, Mockito.times(0)).update();
    }

    @Test
    public void shouldHandleClose() {
        this.watcher.onClose(this.mockKubernetesClientException);
        Mockito.verify(this.mockPodResource).watch(this.watcher);
    }

    @Test
    public void shouldIgnoreCloseWithoutCause() {
        this.watcher.onClose(null);
        Mockito.verify(this.mockPodResource, Mockito.times(0)).watch(this.watcher);
    }
}

