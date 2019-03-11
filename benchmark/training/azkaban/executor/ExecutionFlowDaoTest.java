/**
 * Copyright 2017 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.executor;


import Status.FAILED;
import Status.KILLED;
import Status.PREPARING;
import Status.SUCCEEDED;
import azkaban.db.DatabaseOperator;
import azkaban.project.ProjectLoader;
import azkaban.test.executions.ExecutionsTestUtil;
import azkaban.utils.Pair;
import azkaban.utils.Props;
import azkaban.utils.TestUtils;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTimeUtils;
import org.junit.Test;


public class ExecutionFlowDaoTest {
    private static final Duration RECENTLY_FINISHED_LIFETIME = Duration.ofMinutes(1);

    private static final Duration FLOW_FINISHED_TIME = Duration.ofMinutes(2);

    private static final Props props = new Props();

    private static DatabaseOperator dbOperator;

    private ExecutionFlowDao executionFlowDao;

    private ExecutorDao executorDao;

    private AssignExecutorDao assignExecutor;

    private FetchActiveFlowDao fetchActiveFlowDao;

    private ExecutionJobDao executionJobDao;

    private ProjectLoader loader;

    @Test
    public void testUploadAndFetchExecutionFlows() throws Exception {
        final ExecutableFlow flow = createTestFlow();
        this.executionFlowDao.uploadExecutableFlow(flow);
        final ExecutableFlow fetchFlow = this.executionFlowDao.fetchExecutableFlow(flow.getExecutionId());
        assertThat(flow).isNotSameAs(fetchFlow);
        assertTwoFlowSame(flow, fetchFlow);
    }

    @Test
    public void testUpdateExecutableFlow() throws Exception {
        final ExecutableFlow flow = createTestFlow();
        this.executionFlowDao.uploadExecutableFlow(flow);
        final ExecutableFlow fetchFlow = this.executionFlowDao.fetchExecutableFlow(flow.getExecutionId());
        fetchFlow.setEndTime(System.currentTimeMillis());
        fetchFlow.setStatus(SUCCEEDED);
        this.executionFlowDao.updateExecutableFlow(fetchFlow);
        final ExecutableFlow fetchFlow2 = this.executionFlowDao.fetchExecutableFlow(flow.getExecutionId());
        assertTwoFlowSame(fetchFlow, fetchFlow2);
    }

    @Test
    public void fetchFlowHistory() throws Exception {
        final ExecutableFlow flow = createTestFlow();
        this.executionFlowDao.uploadExecutableFlow(flow);
        final List<ExecutableFlow> flowList1 = this.executionFlowDao.fetchFlowHistory(0, 2);
        assertThat(flowList1.size()).isEqualTo(1);
        final List<ExecutableFlow> flowList2 = this.executionFlowDao.fetchFlowHistory(flow.getProjectId(), flow.getId(), 0, 2);
        assertThat(flowList2.size()).isEqualTo(1);
        final ExecutableFlow fetchFlow = this.executionFlowDao.fetchExecutableFlow(flow.getExecutionId());
        assertTwoFlowSame(flowList1.get(0), flowList2.get(0));
        assertTwoFlowSame(flowList1.get(0), fetchFlow);
    }

    @Test
    public void fetchFlowHistoryWithStartTime() throws Exception {
        class DateUtil {
            private long dateStrToLong(final String dateStr) throws ParseException {
                final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                final Date d = f.parse(dateStr);
                final long milliseconds = d.getTime();
                return milliseconds;
            }
        }
        final DateUtil dateUtil = new DateUtil();
        final ExecutableFlow flow1 = createTestFlow();
        flow1.setStartTime(dateUtil.dateStrToLong("2018-09-01 10:00:00"));
        this.executionFlowDao.uploadExecutableFlow(flow1);
        final ExecutableFlow flow2 = createTestFlow();
        flow2.setStartTime(dateUtil.dateStrToLong("2018-09-01 09:00:00"));
        this.executionFlowDao.uploadExecutableFlow(flow2);
        final ExecutableFlow flow3 = createTestFlow();
        flow3.setStartTime(dateUtil.dateStrToLong("2018-09-01 09:00:00"));
        this.executionFlowDao.uploadExecutableFlow(flow3);
        final ExecutableFlow flow4 = createTestFlow();
        flow4.setStartTime(dateUtil.dateStrToLong("2018-09-01 08:00:00"));
        this.executionFlowDao.uploadExecutableFlow(flow4);
        final List<ExecutableFlow> flowList = this.executionFlowDao.fetchFlowHistory(flow1.getProjectId(), flow1.getFlowId(), dateUtil.dateStrToLong("2018-09-01 09:00:00"));
        final List<ExecutableFlow> expected = new ArrayList<>();
        expected.add(flow1);
        expected.add(flow2);
        expected.add(flow3);
        assertThat(flowList).hasSize(3);
        for (int i = 0; i < (flowList.size()); i++) {
            assertTwoFlowSame(flowList.get(i), expected.get(i));
        }
    }

    @Test
    public void testAdvancedFilter() throws Exception {
        createTestProject();
        final ExecutableFlow flow = createTestFlow();
        this.executionFlowDao.uploadExecutableFlow(flow);
        final List<ExecutableFlow> flowList1 = this.executionFlowDao.fetchFlowHistory("exectest1", "", "", 0, (-1), (-1), 0, 16);
        assertThat(flowList1.size()).isEqualTo(1);
        final ExecutableFlow fetchFlow = this.executionFlowDao.fetchExecutableFlow(flow.getExecutionId());
        assertTwoFlowSame(flowList1.get(0), fetchFlow);
    }

    @Test
    public void testFetchRecentlyFinishedFlows() throws Exception {
        final ExecutableFlow flow1 = createTestFlow();
        this.executionFlowDao.uploadExecutableFlow(flow1);
        flow1.setStatus(SUCCEEDED);
        flow1.setEndTime(System.currentTimeMillis());
        this.executionFlowDao.updateExecutableFlow(flow1);
        // Flow just finished. Fetch recently finished flows immediately. Should get it.
        final List<ExecutableFlow> flows = this.executionFlowDao.fetchRecentlyFinishedFlows(ExecutionFlowDaoTest.RECENTLY_FINISHED_LIFETIME);
        assertThat(flows.size()).isEqualTo(1);
        assertTwoFlowSame(flow1, flows.get(0));
    }

    @Test
    public void testFetchEmptyRecentlyFinishedFlows() throws Exception {
        final ExecutableFlow flow1 = createTestFlow();
        this.executionFlowDao.uploadExecutableFlow(flow1);
        flow1.setStatus(SUCCEEDED);
        flow1.setEndTime(DateTimeUtils.currentTimeMillis());
        this.executionFlowDao.updateExecutableFlow(flow1);
        // Todo jamiesjc: use java8.java.time api instead of jodatime
        // Mock flow finished time to be 2 min ago.
        DateTimeUtils.setCurrentMillisOffset((-(ExecutionFlowDaoTest.FLOW_FINISHED_TIME.toMillis())));
        flow1.setEndTime(DateTimeUtils.currentTimeMillis());
        this.executionFlowDao.updateExecutableFlow(flow1);
        // Fetch recently finished flows within 1 min. Should be empty.
        final List<ExecutableFlow> flows = this.executionFlowDao.fetchRecentlyFinishedFlows(ExecutionFlowDaoTest.RECENTLY_FINISHED_LIFETIME);
        assertThat(flows.size()).isEqualTo(0);
    }

    @Test
    public void testFetchQueuedFlows() throws Exception {
        final ExecutableFlow flow = createTestFlow();
        flow.setStatus(PREPARING);
        this.executionFlowDao.uploadExecutableFlow(flow);
        final ExecutableFlow flow2 = TestUtils.createTestExecutableFlow("exectest1", "exec2");
        flow2.setStatus(PREPARING);
        this.executionFlowDao.uploadExecutableFlow(flow2);
        final List<Pair<ExecutionReference, ExecutableFlow>> fetchedQueuedFlows = this.executionFlowDao.fetchQueuedFlows();
        assertThat(fetchedQueuedFlows.size()).isEqualTo(2);
        final Pair<ExecutionReference, ExecutableFlow> fetchedFlow1 = fetchedQueuedFlows.get(0);
        final Pair<ExecutionReference, ExecutableFlow> fetchedFlow2 = fetchedQueuedFlows.get(1);
        assertTwoFlowSame(flow, fetchedFlow1.getSecond());
        assertTwoFlowSame(flow2, fetchedFlow2.getSecond());
    }

    @Test
    public void testAssignAndUnassignExecutor() throws Exception {
        final String host = "localhost";
        final int port = 12345;
        final Executor executor = this.executorDao.addExecutor(host, port);
        final ExecutableFlow flow = TestUtils.createTestExecutableFlow("exectest1", "exec1");
        this.executionFlowDao.uploadExecutableFlow(flow);
        this.assignExecutor.assignExecutor(executor.getId(), flow.getExecutionId());
        final Executor fetchExecutor = this.executorDao.fetchExecutorByExecutionId(flow.getExecutionId());
        assertThat(fetchExecutor).isEqualTo(executor);
        this.assignExecutor.unassignExecutor(flow.getExecutionId());
        assertThat(this.executorDao.fetchExecutorByExecutionId(flow.getExecutionId())).isNull();
    }

    /* Test exception when assigning a non-existent executor to a flow */
    @Test
    public void testAssignExecutorInvalidExecutor() throws Exception {
        final ExecutableFlow flow = TestUtils.createTestExecutableFlow("exectest1", "exec1");
        this.executionFlowDao.uploadExecutableFlow(flow);
        // Since we haven't inserted any executors, 1 should be non-existent executor id.
        assertThatThrownBy(() -> this.assignExecutor.assignExecutor(1, flow.getExecutionId())).isInstanceOf(ExecutorManagerException.class).hasMessageContaining("non-existent executor");
    }

    /* Test exception when assigning an executor to a non-existent flow execution */
    @Test
    public void testAssignExecutorInvalidExecution() throws Exception {
        final String host = "localhost";
        final int port = 12345;
        final Executor executor = this.executorDao.addExecutor(host, port);
        // Make 99 a random non-existent execution id.
        assertThatThrownBy(() -> this.assignExecutor.assignExecutor(executor.getId(), 99)).isInstanceOf(ExecutorManagerException.class).hasMessageContaining("non-existent execution");
    }

    @Test
    public void testFetchActiveFlowsExecutorAssigned() throws Exception {
        final List<ExecutableFlow> flows = createExecutions();
        final Map<Integer, Pair<ExecutionReference, ExecutableFlow>> activeFlows = this.fetchActiveFlowDao.fetchActiveFlows();
        assertFound(activeFlows, flows.get(0), true);
        assertNotFound(activeFlows, flows.get(1), "Returned a queued execution");
        assertFound(activeFlows, flows.get(2), true);
        assertNotFound(activeFlows, flows.get(3), "Returned an execution with a finished status");
        assertFound(activeFlows, flows.get(4), false);
        assertTwoFlowSame(activeFlows.get(flows.get(0).getExecutionId()).getSecond(), flows.get(0));
    }

    @Test
    public void testFetchUnfinishedFlows() throws Exception {
        final List<ExecutableFlow> flows = createExecutions();
        final Map<Integer, Pair<ExecutionReference, ExecutableFlow>> unfinishedFlows = this.fetchActiveFlowDao.fetchUnfinishedFlows();
        assertFound(unfinishedFlows, flows.get(0), true);
        assertFound(unfinishedFlows, flows.get(1), false);
        assertFound(unfinishedFlows, flows.get(2), true);
        assertNotFound(unfinishedFlows, flows.get(3), "Returned an execution with a finished status");
        assertFound(unfinishedFlows, flows.get(4), false);
        assertTwoFlowSame(unfinishedFlows.get(flows.get(0).getExecutionId()).getSecond(), flows.get(0));
    }

    @Test
    public void testFetchUnfinishedFlowsMetadata() throws Exception {
        final List<ExecutableFlow> flows = createExecutions();
        final Map<Integer, Pair<ExecutionReference, ExecutableFlow>> unfinishedFlows = this.fetchActiveFlowDao.fetchUnfinishedFlowsMetadata();
        assertFound(unfinishedFlows, flows.get(0), true);
        assertFound(unfinishedFlows, flows.get(1), false);
        assertFound(unfinishedFlows, flows.get(2), true);
        assertNotFound(unfinishedFlows, flows.get(3), "Returned an execution with a finished status");
        assertFound(unfinishedFlows, flows.get(4), false);
        assertTwoFlowSame(unfinishedFlows.get(flows.get(0).getExecutionId()).getSecond(), flows.get(0), false);
        assertTwoFlowSame(unfinishedFlows.get(flows.get(1).getExecutionId()).getSecond(), flows.get(1), false);
        assertTwoFlowSame(unfinishedFlows.get(flows.get(2).getExecutionId()).getSecond(), flows.get(2), false);
    }

    @Test
    public void testFetchActiveFlowByExecId() throws Exception {
        final List<ExecutableFlow> flows = createExecutions();
        assertTwoFlowSame(this.fetchActiveFlowDao.fetchActiveFlowByExecId(flows.get(0).getExecutionId()).getSecond(), flows.get(0));
        assertThat(this.fetchActiveFlowDao.fetchActiveFlowByExecId(flows.get(1).getExecutionId())).isNull();
        assertTwoFlowSame(this.fetchActiveFlowDao.fetchActiveFlowByExecId(flows.get(2).getExecutionId()).getSecond(), flows.get(2));
        assertThat(this.fetchActiveFlowDao.fetchActiveFlowByExecId(flows.get(3).getExecutionId())).isNull();
        assertTwoFlowSame(this.fetchActiveFlowDao.fetchActiveFlowByExecId(flows.get(4).getExecutionId()).getSecond(), flows.get(4));
    }

    @Test
    public void testFetchActiveFlowsStatusChanged() throws Exception {
        final ExecutableFlow flow1 = TestUtils.createTestExecutableFlow("exectest1", "exec1");
        this.executionFlowDao.uploadExecutableFlow(flow1);
        final Executor executor = this.executorDao.addExecutor("test", 1);
        this.assignExecutor.assignExecutor(executor.getId(), flow1.getExecutionId());
        Map<Integer, Pair<ExecutionReference, ExecutableFlow>> activeFlows1 = this.fetchActiveFlowDao.fetchActiveFlows();
        assertThat(activeFlows1.containsKey(flow1.getExecutionId())).isTrue();
        // When flow status becomes SUCCEEDED/KILLED/FAILED, it should not be in active state
        flow1.setStatus(SUCCEEDED);
        this.executionFlowDao.updateExecutableFlow(flow1);
        activeFlows1 = this.fetchActiveFlowDao.fetchActiveFlows();
        assertThat(activeFlows1.containsKey(flow1.getExecutionId())).isFalse();
        flow1.setStatus(KILLED);
        this.executionFlowDao.updateExecutableFlow(flow1);
        activeFlows1 = this.fetchActiveFlowDao.fetchActiveFlows();
        assertThat(activeFlows1.containsKey(flow1.getExecutionId())).isFalse();
        flow1.setStatus(FAILED);
        this.executionFlowDao.updateExecutableFlow(flow1);
        activeFlows1 = this.fetchActiveFlowDao.fetchActiveFlows();
        assertThat(activeFlows1.containsKey(flow1.getExecutionId())).isFalse();
    }

    @Test
    public void testUploadAndFetchExecutableNode() throws Exception {
        final ExecutableFlow flow = TestUtils.createTestExecutableFlow("exectest1", "exec1");
        flow.setExecutionId(10);
        final File jobFile = ExecutionsTestUtil.getFlowFile("exectest1", "job10.job");
        final Props props = new Props(null, jobFile);
        props.put("test", "test2");
        final ExecutableNode oldNode = flow.getExecutableNode("job10");
        oldNode.setStartTime(System.currentTimeMillis());
        this.executionJobDao.uploadExecutableNode(oldNode, props);
        final ExecutableJobInfo info = this.executionJobDao.fetchJobInfo(10, "job10", 0);
        assertThat(flow.getEndTime()).isEqualTo(info.getEndTime());
        assertThat(flow.getProjectId()).isEqualTo(info.getProjectId());
        assertThat(flow.getVersion()).isEqualTo(info.getVersion());
        assertThat(flow.getFlowId()).isEqualTo(info.getFlowId());
        assertThat(oldNode.getId()).isEqualTo(info.getJobId());
        assertThat(oldNode.getStatus()).isEqualTo(info.getStatus());
        assertThat(oldNode.getStartTime()).isEqualTo(info.getStartTime());
        // Fetch props
        final Props outputProps = new Props();
        outputProps.put("hello", "output");
        oldNode.setOutputProps(outputProps);
        oldNode.setEndTime(System.currentTimeMillis());
        this.executionJobDao.updateExecutableNode(oldNode);
        final Props fInputProps = this.executionJobDao.fetchExecutionJobInputProps(10, "job10");
        final Props fOutputProps = this.executionJobDao.fetchExecutionJobOutputProps(10, "job10");
        final Pair<Props, Props> inOutProps = this.executionJobDao.fetchExecutionJobProps(10, "job10");
        assertThat(fInputProps.get("test")).isEqualTo("test2");
        assertThat(fOutputProps.get("hello")).isEqualTo("output");
        assertThat(inOutProps.getFirst().get("test")).isEqualTo("test2");
        assertThat(inOutProps.getSecond().get("hello")).isEqualTo("output");
    }

    @Test
    public void testSelectAndUpdateExecution() throws Exception {
        final ExecutableFlow flow = TestUtils.createTestExecutableFlow("exectest1", "exec1");
        flow.setExecutionId(1);
        this.executionFlowDao.uploadExecutableFlow(flow);
        final Executor executor = this.executorDao.addExecutor("localhost", 12345);
        assertThat(this.executionFlowDao.selectAndUpdateExecution(executor.getId(), true)).isEqualTo(flow.getExecutionId());
        assertThat(this.executorDao.fetchExecutorByExecutionId(flow.getExecutionId())).isEqualTo(executor);
    }
}

