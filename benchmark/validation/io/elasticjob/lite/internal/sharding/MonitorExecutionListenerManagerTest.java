package io.elasticjob.lite.internal.sharding;


import TreeCacheEvent.Type.NODE_ADDED;
import TreeCacheEvent.Type.NODE_UPDATED;
import io.elasticjob.lite.fixture.LiteJsonConstants;
import io.elasticjob.lite.internal.storage.JobNodeStorage;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class MonitorExecutionListenerManagerTest {
    @Mock
    private JobNodeStorage jobNodeStorage;

    @Mock
    private ExecutionService executionService;

    private final MonitorExecutionListenerManager monitorExecutionListenerManager = new MonitorExecutionListenerManager(null, "test_job");

    @Test
    public void assertMonitorExecutionSettingsChangedJobListenerWhenIsNotFailoverPath() {
        monitorExecutionListenerManager.new MonitorExecutionSettingsChangedJobListener().dataChanged("/test_job/other", NODE_ADDED, LiteJsonConstants.getJobJson());
        Mockito.verify(executionService, Mockito.times(0)).clearAllRunningInfo();
    }

    @Test
    public void assertMonitorExecutionSettingsChangedJobListenerWhenIsFailoverPathButNotUpdate() {
        monitorExecutionListenerManager.new MonitorExecutionSettingsChangedJobListener().dataChanged("/test_job/config", NODE_ADDED, "");
        Mockito.verify(executionService, Mockito.times(0)).clearAllRunningInfo();
    }

    @Test
    public void assertMonitorExecutionSettingsChangedJobListenerWhenIsFailoverPathAndUpdateButEnableFailover() {
        monitorExecutionListenerManager.new MonitorExecutionSettingsChangedJobListener().dataChanged("/test_job/config", NODE_UPDATED, LiteJsonConstants.getJobJson());
        Mockito.verify(executionService, Mockito.times(0)).clearAllRunningInfo();
    }

    @Test
    public void assertMonitorExecutionSettingsChangedJobListenerWhenIsFailoverPathAndUpdateButDisableFailover() {
        monitorExecutionListenerManager.new MonitorExecutionSettingsChangedJobListener().dataChanged("/test_job/config", NODE_UPDATED, LiteJsonConstants.getJobJsonWithMonitorExecution(false));
        Mockito.verify(executionService).clearAllRunningInfo();
    }
}

