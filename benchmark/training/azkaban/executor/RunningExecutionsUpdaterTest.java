package azkaban.executor;


import azkaban.alert.Alerter;
import azkaban.metrics.CommonMetrics;
import java.util.Collections;
import org.joda.time.DateTimeUtils;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class RunningExecutionsUpdaterTest {
    private static final int EXECUTION_ID_77 = 77;

    private static final ExecutorManagerException API_CALL_EXCEPTION = new ExecutorManagerException("Mocked API timeout");

    @Mock
    ExecutorManagerUpdaterStage updaterStage;

    @Mock
    AlerterHolder alerterHolder;

    @Mock
    CommonMetrics commonMetrics;

    @Mock
    ExecutorApiGateway apiGateway;

    @Mock
    ExecutionFinalizer executionFinalizer;

    @Mock
    private Alerter mailAlerter;

    @Mock
    private ExecutorLoader executorLoader;

    private ExecutableFlow execution;

    private RunningExecutions runningExecutions;

    private Executor activeExecutor;

    private RunningExecutionsUpdater updater;

    @Test
    public void updateExecutionsStillRunning() throws Exception {
        mockFlowStillRunning();
        this.updater.updateExecutions();
        verifyCallUpdateApi();
        Mockito.verifyZeroInteractions(this.executionFinalizer);
    }

    @Test
    public void updateExecutionsSucceeded() throws Exception {
        mockFlowSucceeded();
        this.updater.updateExecutions();
        verifyCallUpdateApi();
        verifyFinalizeFlow();
    }

    @Test
    public void updateExecutionsExecutorDoesNotExist() throws Exception {
        mockExecutorDoesNotExist();
        this.updater.updateExecutions();
        verifyFinalizeFlow();
    }

    @Test
    public void updateExecutionsFlowDoesNotExist() throws Exception {
        mockFlowDoesNotExist();
        this.updater.updateExecutions();
        verifyCallUpdateApi();
        verifyFinalizeFlow();
    }

    @Test
    public void updateExecutionsUpdateCallFails() throws Exception {
        mockUpdateCallFails();
        Mockito.when(this.executorLoader.fetchExecutor(ArgumentMatchers.anyInt())).thenReturn(this.activeExecutor);
        DateTimeUtils.setCurrentMillisFixed(System.currentTimeMillis());
        for (int i = 0; i < (this.updater.numErrorsBeforeUnresponsiveEmail); i++) {
            this.updater.updateExecutions();
            DateTimeUtils.setCurrentMillisFixed((((DateTimeUtils.currentTimeMillis()) + (this.updater.errorThreshold)) + 1L));
        }
        Mockito.verify(this.mailAlerter).alertOnFailedUpdate(this.activeExecutor, Collections.singletonList(this.execution), RunningExecutionsUpdaterTest.API_CALL_EXCEPTION);
        Mockito.verifyZeroInteractions(this.executionFinalizer);
    }

    /**
     * Should finalize execution if executor doesn't exist in the DB.
     */
    @Test
    public void updateExecutionsUpdateCallFailsExecutorDoesntExist() throws Exception {
        mockUpdateCallFails();
        Mockito.when(this.executorLoader.fetchExecutor(ArgumentMatchers.anyInt())).thenReturn(null);
        DateTimeUtils.setCurrentMillisFixed(System.currentTimeMillis());
        this.updater.updateExecutions();
        Mockito.verify(this.executionFinalizer).finalizeFlow(this.execution, "Not running on the assigned executor (any more)", null);
    }

    /**
     * Shouldn't finalize executions if executor's existence can't be checked.
     */
    @Test
    public void updateExecutionsUpdateCallFailsExecutorCheckThrows() throws Exception {
        mockUpdateCallFails();
        Mockito.when(this.executorLoader.fetchExecutor(ArgumentMatchers.anyInt())).thenThrow(new ExecutorManagerException("Mocked fetchExecutor failure"));
        DateTimeUtils.setCurrentMillisFixed(System.currentTimeMillis());
        this.updater.updateExecutions();
        Mockito.verifyZeroInteractions(this.executionFinalizer);
    }
}

