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
import Watcher.Action.DELETED;
import Watcher.Action.ERROR;
import Watcher.Action.MODIFIED;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.fabric8.kubernetes.api.model.DoneableConfigMap;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Gytis Trikleris
 */
@RunWith(MockitoJUnitRunner.class)
public class LeaderRecordWatcherTest {
    @Mock
    private LeaderProperties mockLeaderProperties;

    @Mock
    private LeadershipController mockLeadershipController;

    @Mock
    private KubernetesClient mockKubernetesClient;

    @Mock
    private MixedOperation<ConfigMap, ConfigMapList, DoneableConfigMap, Resource<ConfigMap, DoneableConfigMap>> mockConfigMapsOperation;

    @Mock
    private NonNamespaceOperation<ConfigMap, ConfigMapList, DoneableConfigMap, Resource<ConfigMap, DoneableConfigMap>> mockInNamespaceOperation;

    @Mock
    private Resource<ConfigMap, DoneableConfigMap> mockWithNameResource;

    @Mock
    private Watch mockWatch;

    @Mock
    private ConfigMap mockConfigMap;

    @Mock
    private KubernetesClientException mockKubernetesClientException;

    private LeaderRecordWatcher watcher;

    @Test
    public void shouldStartOnce() {
        this.watcher.start();
        this.watcher.start();
        Mockito.verify(this.mockWithNameResource).watch(this.watcher);
    }

    @Test
    public void shouldStopOnce() {
        this.watcher.start();
        this.watcher.stop();
        this.watcher.stop();
        Mockito.verify(this.mockWatch).close();
    }

    @Test
    public void shouldHandleEvent() {
        this.watcher.eventReceived(ADDED, this.mockConfigMap);
        this.watcher.eventReceived(DELETED, this.mockConfigMap);
        this.watcher.eventReceived(MODIFIED, this.mockConfigMap);
        Mockito.verify(this.mockLeadershipController, Mockito.times(3)).update();
    }

    @Test
    public void shouldIgnoreErrorEvent() {
        this.watcher.eventReceived(ERROR, this.mockConfigMap);
        Mockito.verify(this.mockLeadershipController, Mockito.times(0)).update();
    }

    @Test
    public void shouldHandleClose() {
        this.watcher.onClose(this.mockKubernetesClientException);
        Mockito.verify(this.mockWithNameResource).watch(this.watcher);
    }

    @Test
    public void shouldIgnoreCloseWithoutCause() {
        this.watcher.onClose(null);
        Mockito.verify(this.mockWithNameResource, Mockito.times(0)).watch(this.watcher);
    }
}

