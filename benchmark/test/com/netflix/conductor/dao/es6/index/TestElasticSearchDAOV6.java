package com.netflix.conductor.dao.es6.index;


import Workflow.WorkflowStatus;
import Workflow.WorkflowStatus.COMPLETED;
import Workflow.WorkflowStatus.FAILED;
import Workflow.WorkflowStatus.RUNNING;
import com.netflix.conductor.common.metadata.events.EventExecution;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskExecLog;
import com.netflix.conductor.common.run.TaskSummary;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.common.run.WorkflowSummary;
import com.netflix.conductor.core.events.queue.Message;
import com.netflix.conductor.elasticsearch.ElasticSearchConfiguration;
import com.netflix.conductor.elasticsearch.EmbeddedElasticSearch;
import com.netflix.conductor.support.TestUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Supplier;
import org.elasticsearch.client.Client;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;


public class TestElasticSearchDAOV6 {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyyMMww");

    private static final String INDEX_PREFIX = "conductor";

    private static final String WORKFLOW_DOC_TYPE = "workflow";

    private static final String TASK_DOC_TYPE = "task";

    private static final String MSG_DOC_TYPE = "message";

    private static final String EVENT_DOC_TYPE = "event";

    private static final String LOG_INDEX_PREFIX = "task_log";

    private static ElasticSearchConfiguration configuration;

    private static Client elasticSearchClient;

    private static ElasticSearchDAOV6 indexDAO;

    private static EmbeddedElasticSearch embeddedElasticSearch;

    @Test
    public void assertInitialSetup() {
        TestElasticSearchDAOV6.SIMPLE_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
        String workflowIndex = ((TestElasticSearchDAOV6.INDEX_PREFIX) + "_") + (TestElasticSearchDAOV6.WORKFLOW_DOC_TYPE);
        String taskIndex = ((TestElasticSearchDAOV6.INDEX_PREFIX) + "_") + (TestElasticSearchDAOV6.TASK_DOC_TYPE);
        String taskLogIndex = ((((TestElasticSearchDAOV6.INDEX_PREFIX) + "_") + (TestElasticSearchDAOV6.LOG_INDEX_PREFIX)) + "_") + (TestElasticSearchDAOV6.SIMPLE_DATE_FORMAT.format(new Date()));
        String messageIndex = ((((TestElasticSearchDAOV6.INDEX_PREFIX) + "_") + (TestElasticSearchDAOV6.MSG_DOC_TYPE)) + "_") + (TestElasticSearchDAOV6.SIMPLE_DATE_FORMAT.format(new Date()));
        String eventIndex = ((((TestElasticSearchDAOV6.INDEX_PREFIX) + "_") + (TestElasticSearchDAOV6.EVENT_DOC_TYPE)) + "_") + (TestElasticSearchDAOV6.SIMPLE_DATE_FORMAT.format(new Date()));
        Assert.assertTrue("Index 'conductor_workflow' should exist", indexExists("conductor_workflow"));
        Assert.assertTrue("Index 'conductor_task' should exist", indexExists("conductor_task"));
        Assert.assertTrue((("Index '" + taskLogIndex) + "' should exist"), indexExists(taskLogIndex));
        Assert.assertTrue((("Index '" + messageIndex) + "' should exist"), indexExists(messageIndex));
        Assert.assertTrue((("Index '" + eventIndex) + "' should exist"), indexExists(eventIndex));
        Assert.assertTrue("Mapping 'workflow' for index 'conductor' should exist", doesMappingExist(workflowIndex, TestElasticSearchDAOV6.WORKFLOW_DOC_TYPE));
        Assert.assertTrue("Mapping 'task' for index 'conductor' should exist", doesMappingExist(taskIndex, TestElasticSearchDAOV6.TASK_DOC_TYPE));
    }

    @Test
    public void shouldIndexWorkflow() {
        Workflow workflow = TestUtils.loadWorkflowSnapshot("workflow");
        WorkflowSummary summary = new WorkflowSummary(workflow);
        TestElasticSearchDAOV6.indexDAO.indexWorkflow(workflow);
        assertWorkflowSummary(workflow.getWorkflowId(), summary);
    }

    @Test
    public void shouldIndexWorkflowAsync() throws Exception {
        Workflow workflow = TestUtils.loadWorkflowSnapshot("workflow");
        WorkflowSummary summary = new WorkflowSummary(workflow);
        TestElasticSearchDAOV6.indexDAO.asyncIndexWorkflow(workflow).get();
        assertWorkflowSummary(workflow.getWorkflowId(), summary);
    }

    @Test
    public void shouldRemoveWorkflow() {
        Workflow workflow = TestUtils.loadWorkflowSnapshot("workflow");
        TestElasticSearchDAOV6.indexDAO.indexWorkflow(workflow);
        // wait for workflow to be indexed
        List<String> workflows = tryFindResults(() -> searchWorkflows(workflow.getWorkflowId()), 1);
        Assert.assertEquals(1, workflows.size());
        TestElasticSearchDAOV6.indexDAO.removeWorkflow(workflow.getWorkflowId());
        workflows = tryFindResults(() -> searchWorkflows(workflow.getWorkflowId()), 0);
        Assert.assertTrue("Workflow was not removed.", workflows.isEmpty());
    }

    @Test
    public void shouldAsyncRemoveWorkflow() throws Exception {
        Workflow workflow = TestUtils.loadWorkflowSnapshot("workflow");
        TestElasticSearchDAOV6.indexDAO.indexWorkflow(workflow);
        // wait for workflow to be indexed
        List<String> workflows = tryFindResults(() -> searchWorkflows(workflow.getWorkflowId()), 1);
        Assert.assertEquals(1, workflows.size());
        TestElasticSearchDAOV6.indexDAO.asyncRemoveWorkflow(workflow.getWorkflowId()).get();
        workflows = tryFindResults(() -> searchWorkflows(workflow.getWorkflowId()), 0);
        Assert.assertTrue("Workflow was not removed.", workflows.isEmpty());
    }

    @Test
    public void shouldUpdateWorkflow() {
        Workflow workflow = TestUtils.loadWorkflowSnapshot("workflow");
        WorkflowSummary summary = new WorkflowSummary(workflow);
        TestElasticSearchDAOV6.indexDAO.indexWorkflow(workflow);
        TestElasticSearchDAOV6.indexDAO.updateWorkflow(workflow.getWorkflowId(), new String[]{ "status" }, new Object[]{ WorkflowStatus.COMPLETED });
        summary.setStatus(COMPLETED);
        assertWorkflowSummary(workflow.getWorkflowId(), summary);
    }

    @Test
    public void shouldAsyncUpdateWorkflow() throws Exception {
        Workflow workflow = TestUtils.loadWorkflowSnapshot("workflow");
        WorkflowSummary summary = new WorkflowSummary(workflow);
        TestElasticSearchDAOV6.indexDAO.indexWorkflow(workflow);
        TestElasticSearchDAOV6.indexDAO.asyncUpdateWorkflow(workflow.getWorkflowId(), new String[]{ "status" }, new Object[]{ WorkflowStatus.FAILED }).get();
        summary.setStatus(FAILED);
        assertWorkflowSummary(workflow.getWorkflowId(), summary);
    }

    @Test
    public void shouldIndexTask() {
        Workflow workflow = TestUtils.loadWorkflowSnapshot("workflow");
        Task task = workflow.getTasks().get(0);
        TaskSummary summary = new TaskSummary(task);
        TestElasticSearchDAOV6.indexDAO.indexTask(task);
        List<String> tasks = tryFindResults(() -> searchTasks(workflow));
        Assert.assertEquals(summary.getTaskId(), tasks.get(0));
    }

    @Test
    public void shouldIndexTaskAsync() throws Exception {
        Workflow workflow = TestUtils.loadWorkflowSnapshot("workflow");
        Task task = workflow.getTasks().get(0);
        TaskSummary summary = new TaskSummary(task);
        TestElasticSearchDAOV6.indexDAO.asyncIndexTask(task).get();
        List<String> tasks = tryFindResults(() -> searchTasks(workflow));
        Assert.assertEquals(summary.getTaskId(), tasks.get(0));
    }

    @Test
    public void shouldAddTaskExecutionLogs() {
        List<TaskExecLog> logs = new ArrayList<>();
        String taskId = uuid();
        logs.add(createLog(taskId, "log1"));
        logs.add(createLog(taskId, "log2"));
        logs.add(createLog(taskId, "log3"));
        TestElasticSearchDAOV6.indexDAO.addTaskExecutionLogs(logs);
        List<TaskExecLog> indexedLogs = tryFindResults(() -> TestElasticSearchDAOV6.indexDAO.getTaskExecutionLogs(taskId), 3);
        Assert.assertEquals(3, indexedLogs.size());
        Assert.assertTrue("Not all logs was indexed", indexedLogs.containsAll(logs));
    }

    @Test
    public void shouldAddTaskExecutionLogsAsync() throws Exception {
        List<TaskExecLog> logs = new ArrayList<>();
        String taskId = uuid();
        logs.add(createLog(taskId, "log1"));
        logs.add(createLog(taskId, "log2"));
        logs.add(createLog(taskId, "log3"));
        TestElasticSearchDAOV6.indexDAO.asyncAddTaskExecutionLogs(logs).get();
        List<TaskExecLog> indexedLogs = tryFindResults(() -> TestElasticSearchDAOV6.indexDAO.getTaskExecutionLogs(taskId), 3);
        Assert.assertEquals(3, indexedLogs.size());
        Assert.assertTrue("Not all logs was indexed", indexedLogs.containsAll(logs));
    }

    @Test
    public void shouldAddMessage() {
        String queue = "queue";
        Message message1 = new Message(uuid(), "payload1", null);
        Message message2 = new Message(uuid(), "payload2", null);
        TestElasticSearchDAOV6.indexDAO.addMessage(queue, message1);
        TestElasticSearchDAOV6.indexDAO.addMessage(queue, message2);
        List<Message> indexedMessages = tryFindResults(() -> TestElasticSearchDAOV6.indexDAO.getMessages(queue), 2);
        Assert.assertEquals(2, indexedMessages.size());
        Assert.assertTrue("Not all messages was indexed", indexedMessages.containsAll(Arrays.asList(message1, message2)));
    }

    @Test
    public void shouldAddEventExecution() {
        String event = "event";
        EventExecution execution1 = createEventExecution(event);
        EventExecution execution2 = createEventExecution(event);
        TestElasticSearchDAOV6.indexDAO.addEventExecution(execution1);
        TestElasticSearchDAOV6.indexDAO.addEventExecution(execution2);
        List<EventExecution> indexedExecutions = tryFindResults(() -> TestElasticSearchDAOV6.indexDAO.getEventExecutions(event), 2);
        Assert.assertEquals(2, indexedExecutions.size());
        Assert.assertTrue("Not all event executions was indexed", indexedExecutions.containsAll(Arrays.asList(execution1, execution2)));
    }

    @Test
    public void shouldAsyncAddEventExecution() throws Exception {
        String event = "event2";
        EventExecution execution1 = createEventExecution(event);
        EventExecution execution2 = createEventExecution(event);
        TestElasticSearchDAOV6.indexDAO.asyncAddEventExecution(execution1).get();
        TestElasticSearchDAOV6.indexDAO.asyncAddEventExecution(execution2).get();
        List<EventExecution> indexedExecutions = tryFindResults(() -> TestElasticSearchDAOV6.indexDAO.getEventExecutions(event), 2);
        Assert.assertEquals(2, indexedExecutions.size());
        Assert.assertTrue("Not all event executions was indexed", indexedExecutions.containsAll(Arrays.asList(execution1, execution2)));
    }

    @Test
    public void shouldAddIndexPrefixToIndexTemplate() throws Exception {
        String json = TestUtils.loadJsonResource("expected_template_task_log");
        String content = TestElasticSearchDAOV6.indexDAO.loadTypeMappingSource("/template_task_log.json");
        Assert.assertEquals(json, content);
    }

    @Test
    public void shouldSearchRecentRunningWorkflows() throws Exception {
        Workflow oldWorkflow = TestUtils.loadWorkflowSnapshot("workflow");
        oldWorkflow.setStatus(RUNNING);
        oldWorkflow.setUpdateTime(new DateTime().minusHours(2).toDate().getTime());
        Workflow recentWorkflow = TestUtils.loadWorkflowSnapshot("workflow");
        recentWorkflow.setStatus(RUNNING);
        recentWorkflow.setUpdateTime(new DateTime().minusHours(1).toDate().getTime());
        Workflow tooRecentWorkflow = TestUtils.loadWorkflowSnapshot("workflow");
        tooRecentWorkflow.setStatus(RUNNING);
        tooRecentWorkflow.setUpdateTime(new DateTime().toDate().getTime());
        TestElasticSearchDAOV6.indexDAO.indexWorkflow(oldWorkflow);
        TestElasticSearchDAOV6.indexDAO.indexWorkflow(recentWorkflow);
        TestElasticSearchDAOV6.indexDAO.indexWorkflow(tooRecentWorkflow);
        Thread.sleep(1000);
        List<String> ids = TestElasticSearchDAOV6.indexDAO.searchRecentRunningWorkflows(2, 1);
        Assert.assertEquals(1, ids.size());
        Assert.assertEquals(recentWorkflow.getWorkflowId(), ids.get(0));
    }
}

