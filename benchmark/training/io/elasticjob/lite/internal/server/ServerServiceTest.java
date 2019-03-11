/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
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
 * </p>
 */
package io.elasticjob.lite.internal.server;


import ServerStatus.DISABLED;
import io.elasticjob.lite.internal.schedule.JobRegistry;
import io.elasticjob.lite.internal.schedule.JobScheduleController;
import io.elasticjob.lite.internal.storage.JobNodeStorage;
import io.elasticjob.lite.reg.base.CoordinatorRegistryCenter;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class ServerServiceTest {
    @Mock
    private CoordinatorRegistryCenter regCenter;

    @Mock
    private JobScheduleController jobScheduleController;

    @Mock
    private JobNodeStorage jobNodeStorage;

    private ServerService serverService;

    @Test
    public void assertPersistOnlineForInstanceShutdown() {
        JobRegistry.getInstance().shutdown("test_job");
        serverService.persistOnline(false);
        Mockito.verify(jobNodeStorage, Mockito.times(0)).fillJobNode("servers/127.0.0.1", DISABLED.name());
    }

    @Test
    public void assertPersistOnlineForDisabledServer() {
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        serverService.persistOnline(false);
        Mockito.verify(jobNodeStorage).fillJobNode("servers/127.0.0.1", DISABLED.name());
        JobRegistry.getInstance().shutdown("test_job");
    }

    @Test
    public void assertPersistOnlineForEnabledServer() {
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        serverService.persistOnline(true);
        Mockito.verify(jobNodeStorage).fillJobNode("servers/127.0.0.1", "");
        JobRegistry.getInstance().shutdown("test_job");
    }

    @Test
    public void assertHasAvailableServers() {
        Mockito.when(jobNodeStorage.getJobNodeChildrenKeys("servers")).thenReturn(Arrays.asList("127.0.0.1", "127.0.0.2", "127.0.0.3"));
        Mockito.when(jobNodeStorage.getJobNodeData("servers/127.0.0.1")).thenReturn(DISABLED.name());
        Mockito.when(jobNodeStorage.getJobNodeChildrenKeys("instances")).thenReturn(Collections.singletonList("127.0.0.3@-@0"));
        Assert.assertTrue(serverService.hasAvailableServers());
    }

    @Test
    public void assertHasNotAvailableServers() {
        Mockito.when(jobNodeStorage.getJobNodeChildrenKeys("servers")).thenReturn(Arrays.asList("127.0.0.1", "127.0.0.2"));
        Mockito.when(jobNodeStorage.getJobNodeData("servers/127.0.0.2")).thenReturn(DISABLED.name());
        Mockito.when(jobNodeStorage.getJobNodeChildrenKeys("instances")).thenReturn(Arrays.asList("127.0.0.2@-@0", "127.0.0.2@-@1"));
        Assert.assertFalse(serverService.hasAvailableServers());
    }

    @Test
    public void assertIsNotAvailableServerWhenDisabled() {
        Mockito.when(jobNodeStorage.getJobNodeData("servers/127.0.0.1")).thenReturn(DISABLED.name());
        Assert.assertFalse(serverService.isAvailableServer("127.0.0.1"));
    }

    @Test
    public void assertIsNotAvailableServerWithoutOnlineInstances() {
        Mockito.when(jobNodeStorage.getJobNodeChildrenKeys("instances")).thenReturn(Collections.singletonList("127.0.0.2@-@0"));
        Assert.assertFalse(serverService.isAvailableServer("127.0.0.1"));
    }

    @Test
    public void assertIsAvailableServer() {
        Mockito.when(jobNodeStorage.getJobNodeChildrenKeys("instances")).thenReturn(Collections.singletonList("127.0.0.1@-@0"));
        Assert.assertTrue(serverService.isAvailableServer("127.0.0.1"));
    }

    @Test
    public void assertIsNotEnableServer() {
        Mockito.when(jobNodeStorage.getJobNodeData("servers/127.0.0.1")).thenReturn(DISABLED.name());
        Assert.assertFalse(serverService.isEnableServer("127.0.0.1"));
    }

    @Test
    public void assertIsEnableServer() {
        Assert.assertTrue(serverService.isEnableServer("127.0.0.1"));
    }
}

