package io.elasticjob.lite.internal.instance;


import InstanceOperation.TRIGGER;
import Type.NODE_ADDED;
import Type.NODE_UPDATED;
import io.elasticjob.lite.internal.schedule.JobRegistry;
import io.elasticjob.lite.internal.schedule.JobScheduleController;
import io.elasticjob.lite.internal.storage.JobNodeStorage;
import io.elasticjob.lite.reg.base.CoordinatorRegistryCenter;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class TriggerListenerManagerTest {
    @Mock
    private CoordinatorRegistryCenter regCenter;

    @Mock
    private JobNodeStorage jobNodeStorage;

    @Mock
    private InstanceService instanceService;

    @Mock
    private JobScheduleController jobScheduleController;

    private TriggerListenerManager triggerListenerManager;

    @Test
    public void assertStart() {
        triggerListenerManager.start();
        Mockito.verify(jobNodeStorage).addDataListener(ArgumentMatchers.<TreeCacheListener>any());
    }

    @Test
    public void assertNotTriggerWhenIsNotTriggerOperation() {
        triggerListenerManager.new JobTriggerStatusJobListener().dataChanged("/test_job/instances/127.0.0.1@-@0", NODE_UPDATED, "");
        Mockito.verify(instanceService, Mockito.times(0)).clearTriggerFlag();
    }

    @Test
    public void assertNotTriggerWhenIsNotLocalInstancePath() {
        triggerListenerManager.new JobTriggerStatusJobListener().dataChanged("/test_job/instances/127.0.0.2@-@0", NODE_UPDATED, TRIGGER.name());
        Mockito.verify(instanceService, Mockito.times(0)).clearTriggerFlag();
    }

    @Test
    public void assertNotTriggerWhenIsNotUpdate() {
        triggerListenerManager.new JobTriggerStatusJobListener().dataChanged("/test_job/instances/127.0.0.1@-@0", NODE_ADDED, TRIGGER.name());
        Mockito.verify(instanceService, Mockito.times(0)).clearTriggerFlag();
    }

    @Test
    public void assertTriggerWhenJobScheduleControllerIsNull() {
        triggerListenerManager.new JobTriggerStatusJobListener().dataChanged("/test_job/instances/127.0.0.1@-@0", NODE_UPDATED, TRIGGER.name());
        Mockito.verify(instanceService).clearTriggerFlag();
        Mockito.verify(jobScheduleController, Mockito.times(0)).triggerJob();
    }

    @Test
    public void assertTriggerWhenJobIsRunning() {
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        JobRegistry.getInstance().setJobRunning("test_job", true);
        triggerListenerManager.new JobTriggerStatusJobListener().dataChanged("/test_job/instances/127.0.0.1@-@0", NODE_UPDATED, TRIGGER.name());
        Mockito.verify(instanceService).clearTriggerFlag();
        Mockito.verify(jobScheduleController, Mockito.times(0)).triggerJob();
        JobRegistry.getInstance().setJobRunning("test_job", false);
        JobRegistry.getInstance().shutdown("test_job");
    }

    @Test
    public void assertTriggerWhenJobIsNotRunning() {
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        triggerListenerManager.new JobTriggerStatusJobListener().dataChanged("/test_job/instances/127.0.0.1@-@0", NODE_UPDATED, TRIGGER.name());
        Mockito.verify(instanceService).clearTriggerFlag();
        Mockito.verify(jobScheduleController).triggerJob();
        JobRegistry.getInstance().shutdown("test_job");
    }
}

