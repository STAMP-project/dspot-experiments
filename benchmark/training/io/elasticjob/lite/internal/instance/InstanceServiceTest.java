package io.elasticjob.lite.internal.instance;


import io.elasticjob.lite.api.strategy.JobInstance;
import io.elasticjob.lite.internal.server.ServerService;
import io.elasticjob.lite.internal.storage.JobNodeStorage;
import java.util.Arrays;
import java.util.Collections;
import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class InstanceServiceTest {
    @Mock
    private JobNodeStorage jobNodeStorage;

    @Mock
    private ServerService serverService;

    private InstanceService instanceService;

    @Test
    public void assertPersistOnline() {
        instanceService.persistOnline();
        Mockito.verify(jobNodeStorage).fillEphemeralJobNode("instances/127.0.0.1@-@0", "");
    }

    @Test
    public void assertRemoveInstance() {
        instanceService.removeInstance();
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("instances/127.0.0.1@-@0");
    }

    @Test
    public void assertClearTriggerFlag() {
        instanceService.clearTriggerFlag();
        jobNodeStorage.updateJobNode("instances/127.0.0.1@-@0", "");
    }

    @Test
    public void assertGetAvailableJobInstances() {
        Mockito.when(jobNodeStorage.getJobNodeChildrenKeys("instances")).thenReturn(Arrays.asList("127.0.0.1@-@0", "127.0.0.2@-@0"));
        Mockito.when(serverService.isEnableServer("127.0.0.1")).thenReturn(true);
        Assert.assertThat(instanceService.getAvailableJobInstances(), CoreMatchers.is(Collections.singletonList(new JobInstance("127.0.0.1@-@0"))));
    }

    @Test
    public void assertIsLocalJobInstanceExisted() {
        Mockito.when(jobNodeStorage.isJobNodeExisted("instances/127.0.0.1@-@0")).thenReturn(true);
        TestCase.assertTrue(instanceService.isLocalJobInstanceExisted());
    }
}

