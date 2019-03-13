/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.service.component;


import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.service.component.instance.ComponentInstance;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for ComponentRestartPolicy implementations.
 */
public class TestComponentRestartPolicy {
    @Test
    public void testAlwaysRestartPolicy() throws Exception {
        AlwaysRestartPolicy alwaysRestartPolicy = AlwaysRestartPolicy.getInstance();
        Component component = Mockito.mock(Component.class);
        Mockito.when(component.getNumReadyInstances()).thenReturn(1);
        Mockito.when(component.getNumDesiredInstances()).thenReturn(2);
        ComponentInstance instance = Mockito.mock(ComponentInstance.class);
        Mockito.when(instance.getComponent()).thenReturn(component);
        ContainerStatus containerStatus = Mockito.mock(ContainerStatus.class);
        Assert.assertEquals(true, alwaysRestartPolicy.isLongLived());
        Assert.assertEquals(true, alwaysRestartPolicy.allowUpgrades());
        Assert.assertEquals(false, alwaysRestartPolicy.hasCompleted(component));
        Assert.assertEquals(false, alwaysRestartPolicy.hasCompletedSuccessfully(component));
        Assert.assertEquals(true, alwaysRestartPolicy.shouldRelaunchInstance(instance, containerStatus));
        Assert.assertEquals(false, alwaysRestartPolicy.isReadyForDownStream(component));
    }

    @Test
    public void testNeverRestartPolicy() throws Exception {
        NeverRestartPolicy restartPolicy = NeverRestartPolicy.getInstance();
        Component component = Mockito.mock(Component.class);
        Mockito.when(component.getNumSucceededInstances()).thenReturn(new Long(1));
        Mockito.when(component.getNumFailedInstances()).thenReturn(new Long(2));
        Mockito.when(component.getNumDesiredInstances()).thenReturn(3);
        Mockito.when(component.getNumReadyInstances()).thenReturn(3);
        ComponentInstance instance = Mockito.mock(ComponentInstance.class);
        Mockito.when(instance.getComponent()).thenReturn(component);
        ContainerStatus containerStatus = Mockito.mock(ContainerStatus.class);
        Assert.assertEquals(false, restartPolicy.isLongLived());
        Assert.assertEquals(false, restartPolicy.allowUpgrades());
        Assert.assertEquals(true, restartPolicy.hasCompleted(component));
        Assert.assertEquals(false, restartPolicy.hasCompletedSuccessfully(component));
        Assert.assertEquals(false, restartPolicy.shouldRelaunchInstance(instance, containerStatus));
        Assert.assertEquals(true, restartPolicy.isReadyForDownStream(component));
    }

    @Test
    public void testOnFailureRestartPolicy() throws Exception {
        OnFailureRestartPolicy restartPolicy = OnFailureRestartPolicy.getInstance();
        Component component = Mockito.mock(Component.class);
        Mockito.when(component.getNumSucceededInstances()).thenReturn(new Long(3));
        Mockito.when(component.getNumFailedInstances()).thenReturn(new Long(0));
        Mockito.when(component.getNumDesiredInstances()).thenReturn(3);
        Mockito.when(component.getNumReadyInstances()).thenReturn(3);
        ComponentInstance instance = Mockito.mock(ComponentInstance.class);
        Mockito.when(instance.getComponent()).thenReturn(component);
        ContainerStatus containerStatus = Mockito.mock(ContainerStatus.class);
        Mockito.when(containerStatus.getExitStatus()).thenReturn(0);
        Assert.assertEquals(false, restartPolicy.isLongLived());
        Assert.assertEquals(false, restartPolicy.allowUpgrades());
        Assert.assertEquals(true, restartPolicy.hasCompleted(component));
        Assert.assertEquals(true, restartPolicy.hasCompletedSuccessfully(component));
        Assert.assertEquals(false, restartPolicy.shouldRelaunchInstance(instance, containerStatus));
        Assert.assertEquals(true, restartPolicy.isReadyForDownStream(component));
        Mockito.when(component.getNumSucceededInstances()).thenReturn(new Long(2));
        Mockito.when(component.getNumFailedInstances()).thenReturn(new Long(1));
        Mockito.when(component.getNumDesiredInstances()).thenReturn(3);
        Assert.assertEquals(false, restartPolicy.hasCompleted(component));
        Assert.assertEquals(false, restartPolicy.hasCompletedSuccessfully(component));
        Mockito.when(containerStatus.getExitStatus()).thenReturn((-1000));
        Assert.assertEquals(true, restartPolicy.shouldRelaunchInstance(instance, containerStatus));
        Assert.assertEquals(true, restartPolicy.isReadyForDownStream(component));
    }
}

