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
package io.elasticjob.lite.lifecycle.internal.operate;


import com.google.common.base.Optional;
import io.elasticjob.lite.lifecycle.api.JobOperateAPI;
import io.elasticjob.lite.reg.base.CoordinatorRegistryCenter;
import java.util.Arrays;
import java.util.Collections;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class JobOperateAPIImplTest {
    private JobOperateAPI jobOperateAPI;

    @Mock
    private CoordinatorRegistryCenter regCenter;

    @Test
    public void assertTriggerWithJobName() {
        Mockito.when(regCenter.getChildrenKeys("/test_job/servers")).thenReturn(Arrays.asList("ip1", "ip2"));
        Mockito.when(regCenter.getChildrenKeys("/test_job/instances")).thenReturn(Arrays.asList("ip1@-@defaultInstance", "ip2@-@defaultInstance"));
        jobOperateAPI.trigger(Optional.of("test_job"), Optional.<String>absent());
        Mockito.verify(regCenter).getChildrenKeys("/test_job/instances");
        Mockito.verify(regCenter).persist("/test_job/instances/ip1@-@defaultInstance", "TRIGGER");
        Mockito.verify(regCenter).persist("/test_job/instances/ip2@-@defaultInstance", "TRIGGER");
    }

    @Test
    public void assertDisableWithJobNameAndServerIp() {
        jobOperateAPI.disable(Optional.of("test_job"), Optional.of("localhost"));
        Mockito.verify(regCenter).persist("/test_job/servers/localhost", "DISABLED");
    }

    @Test
    public void assertDisableWithJobName() {
        Mockito.when(regCenter.getChildrenKeys("/test_job/servers")).thenReturn(Arrays.asList("ip1", "ip2"));
        jobOperateAPI.disable(Optional.of("test_job"), Optional.<String>absent());
        Mockito.verify(regCenter).getChildrenKeys("/test_job/servers");
        Mockito.verify(regCenter).persist("/test_job/servers/ip1", "DISABLED");
        Mockito.verify(regCenter).persist("/test_job/servers/ip2", "DISABLED");
    }

    @Test
    public void assertDisableWithServerIp() {
        Mockito.when(regCenter.getChildrenKeys("/")).thenReturn(Arrays.asList("test_job1", "test_job2"));
        Mockito.when(regCenter.isExisted("/test_job1/servers/localhost")).thenReturn(true);
        Mockito.when(regCenter.isExisted("/test_job2/servers/localhost")).thenReturn(true);
        jobOperateAPI.disable(Optional.<String>absent(), Optional.of("localhost"));
        Mockito.verify(regCenter).getChildrenKeys("/");
        Mockito.verify(regCenter).persist("/test_job1/servers/localhost", "DISABLED");
        Mockito.verify(regCenter).persist("/test_job2/servers/localhost", "DISABLED");
    }

    @Test
    public void assertEnableWithJobNameAndServerIp() {
        jobOperateAPI.enable(Optional.of("test_job"), Optional.of("localhost"));
        Mockito.verify(regCenter).persist("/test_job/servers/localhost", "");
    }

    @Test
    public void assertEnableWithJobName() {
        Mockito.when(regCenter.getChildrenKeys("/test_job/servers")).thenReturn(Arrays.asList("ip1", "ip2"));
        jobOperateAPI.enable(Optional.of("test_job"), Optional.<String>absent());
        Mockito.verify(regCenter).getChildrenKeys("/test_job/servers");
        Mockito.verify(regCenter).persist("/test_job/servers/ip1", "");
        Mockito.verify(regCenter).persist("/test_job/servers/ip2", "");
    }

    @Test
    public void assertEnableWithServerIp() {
        Mockito.when(regCenter.getChildrenKeys("/")).thenReturn(Arrays.asList("test_job1", "test_job2"));
        Mockito.when(regCenter.isExisted("/test_job1/servers/localhost")).thenReturn(true);
        Mockito.when(regCenter.isExisted("/test_job2/servers/localhost")).thenReturn(true);
        jobOperateAPI.enable(Optional.<String>absent(), Optional.of("localhost"));
        Mockito.verify(regCenter).getChildrenKeys("/");
        Mockito.verify(regCenter).persist("/test_job1/servers/localhost", "");
        Mockito.verify(regCenter).persist("/test_job2/servers/localhost", "");
    }

    @Test
    public void assertShutdownWithJobNameAndServerIp() {
        Mockito.when(regCenter.getChildrenKeys("/test_job/servers")).thenReturn(Collections.singletonList("localhost"));
        Mockito.when(regCenter.getChildrenKeys("/test_job/instances")).thenReturn(Collections.singletonList("localhost@-@defaultInstance"));
        jobOperateAPI.shutdown(Optional.of("test_job"), Optional.of("localhost"));
        Mockito.verify(regCenter).remove("/test_job/instances/localhost@-@defaultInstance");
    }

    @Test
    public void assertShutdownWithJobName() {
        Mockito.when(regCenter.getChildrenKeys("/test_job/servers")).thenReturn(Arrays.asList("ip1", "ip2"));
        Mockito.when(regCenter.getChildrenKeys("/test_job/instances")).thenReturn(Arrays.asList("ip1@-@defaultInstance", "ip2@-@defaultInstance"));
        jobOperateAPI.shutdown(Optional.of("test_job"), Optional.<String>absent());
        Mockito.verify(regCenter).getChildrenKeys("/test_job/instances");
        Mockito.verify(regCenter).remove("/test_job/instances/ip1@-@defaultInstance");
    }

    @Test
    public void assertShutdownWithServerIp() {
        Mockito.when(regCenter.getChildrenKeys("/")).thenReturn(Arrays.asList("test_job1", "test_job2"));
        Mockito.when(regCenter.getChildrenKeys("/test_job1/instances")).thenReturn(Collections.singletonList("localhost@-@defaultInstance"));
        Mockito.when(regCenter.getChildrenKeys("/test_job2/instances")).thenReturn(Collections.singletonList("localhost@-@defaultInstance"));
        jobOperateAPI.shutdown(Optional.<String>absent(), Optional.of("localhost"));
        Mockito.verify(regCenter).getChildrenKeys("/");
        Mockito.verify(regCenter).remove("/test_job1/instances/localhost@-@defaultInstance");
        Mockito.verify(regCenter).remove("/test_job2/instances/localhost@-@defaultInstance");
    }

    @Test
    public void assertRemoveWithJobNameAndServerIp() {
        Mockito.when(regCenter.getChildrenKeys("/test_job/servers")).thenReturn(Arrays.asList("ip1", "ip2"));
        jobOperateAPI.remove(Optional.of("test_job"), Optional.of("ip1"));
        Mockito.verify(regCenter).remove("/test_job/servers/ip1");
        Assert.assertFalse(regCenter.isExisted("/test_job/servers/ip1"));
    }

    @Test
    public void assertRemoveWithJobName() {
        Mockito.when(regCenter.isExisted("/test_job")).thenReturn(true);
        Mockito.when(regCenter.getChildrenKeys("/test_job/servers")).thenReturn(Arrays.asList("ip1", "ip2"));
        jobOperateAPI.remove(Optional.of("test_job"), Optional.<String>absent());
        Mockito.verify(regCenter).getChildrenKeys("/test_job/servers");
        Mockito.verify(regCenter).remove("/test_job/servers/ip1");
        Mockito.verify(regCenter).remove("/test_job/servers/ip2");
        Assert.assertFalse(regCenter.isExisted("/test_job/servers/ip1"));
        Assert.assertFalse(regCenter.isExisted("/test_job/servers/ip2"));
        TestCase.assertTrue(regCenter.isExisted("/test_job"));
    }

    @Test
    public void assertRemoveWithServerIp() {
        Mockito.when(regCenter.getChildrenKeys("/")).thenReturn(Arrays.asList("test_job1", "test_job2"));
        Mockito.when(regCenter.getChildrenKeys("/test_job1/servers")).thenReturn(Arrays.asList("ip1", "ip2"));
        Mockito.when(regCenter.getChildrenKeys("/test_job2/servers")).thenReturn(Arrays.asList("ip1", "ip2"));
        jobOperateAPI.remove(Optional.<String>absent(), Optional.of("ip1"));
        Assert.assertFalse(regCenter.isExisted("/test_job1/servers/ip1"));
        Assert.assertFalse(regCenter.isExisted("/test_job2/servers/ip1"));
    }
}

