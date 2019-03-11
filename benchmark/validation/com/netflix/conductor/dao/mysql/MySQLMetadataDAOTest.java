package com.netflix.conductor.dao.mysql;


import EventHandler.Action;
import EventHandler.Action.Type.start_workflow;
import TaskDef.RetryLogic.FIXED;
import TaskDef.TimeoutPolicy.ALERT_ONLY;
import com.netflix.conductor.common.metadata.events.EventHandler;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.core.execution.ApplicationException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@SuppressWarnings("Duplicates")
@RunWith(JUnit4.class)
public class MySQLMetadataDAOTest {
    private MySQLDAOTestUtil testUtil;

    private MySQLMetadataDAO dao;

    @Rule
    public TestName name = new TestName();

    @Test(expected = ApplicationException.class)
    public void testDuplicate() throws Exception {
        WorkflowDef def = new WorkflowDef();
        def.setName("testDuplicate");
        def.setVersion(1);
        dao.create(def);
        dao.create(def);
    }

    @Test
    public void testWorkflowDefOperations() throws Exception {
        WorkflowDef def = new WorkflowDef();
        def.setName("test");
        def.setVersion(1);
        def.setDescription("description");
        def.setCreatedBy("unit_test");
        def.setCreateTime(1L);
        def.setOwnerApp("ownerApp");
        def.setUpdatedBy("unit_test2");
        def.setUpdateTime(2L);
        dao.create(def);
        List<WorkflowDef> all = dao.getAll();
        Assert.assertNotNull(all);
        Assert.assertEquals(1, all.size());
        Assert.assertEquals("test", all.get(0).getName());
        Assert.assertEquals(1, all.get(0).getVersion());
        WorkflowDef found = dao.get("test", 1).get();
        Assert.assertTrue(EqualsBuilder.reflectionEquals(def, found));
        def.setVersion(2);
        dao.create(def);
        all = dao.getAll();
        Assert.assertNotNull(all);
        Assert.assertEquals(2, all.size());
        Assert.assertEquals("test", all.get(0).getName());
        Assert.assertEquals(1, all.get(0).getVersion());
        found = dao.getLatest(def.getName()).get();
        Assert.assertEquals(def.getName(), found.getName());
        Assert.assertEquals(def.getVersion(), found.getVersion());
        Assert.assertEquals(2, found.getVersion());
        all = dao.getAllLatest();
        Assert.assertNotNull(all);
        Assert.assertEquals(1, all.size());
        Assert.assertEquals("test", all.get(0).getName());
        Assert.assertEquals(2, all.get(0).getVersion());
        all = dao.getAllVersions(def.getName());
        Assert.assertNotNull(all);
        Assert.assertEquals(2, all.size());
        Assert.assertEquals("test", all.get(0).getName());
        Assert.assertEquals("test", all.get(1).getName());
        Assert.assertEquals(1, all.get(0).getVersion());
        Assert.assertEquals(2, all.get(1).getVersion());
        def.setDescription("updated");
        dao.update(def);
        found = dao.get(def.getName(), def.getVersion()).get();
        Assert.assertEquals(def.getDescription(), found.getDescription());
        List<String> allnames = dao.findAll();
        Assert.assertNotNull(allnames);
        Assert.assertEquals(1, allnames.size());
        Assert.assertEquals(def.getName(), allnames.get(0));
        dao.removeWorkflowDef("test", 1);
        Optional<WorkflowDef> deleted = dao.get("test", 1);
        Assert.assertFalse(deleted.isPresent());
    }

    @Test
    public void testTaskDefOperations() throws Exception {
        TaskDef def = new TaskDef("taskA");
        def.setDescription("description");
        def.setCreatedBy("unit_test");
        def.setCreateTime(1L);
        def.setInputKeys(Arrays.asList("a", "b", "c"));
        def.setOutputKeys(Arrays.asList("01", "o2"));
        def.setOwnerApp("ownerApp");
        def.setRetryCount(3);
        def.setRetryDelaySeconds(100);
        def.setRetryLogic(FIXED);
        def.setTimeoutPolicy(ALERT_ONLY);
        def.setUpdatedBy("unit_test2");
        def.setUpdateTime(2L);
        dao.createTaskDef(def);
        TaskDef found = dao.getTaskDef(def.getName());
        Assert.assertTrue(EqualsBuilder.reflectionEquals(def, found));
        def.setDescription("updated description");
        dao.updateTaskDef(def);
        found = dao.getTaskDef(def.getName());
        Assert.assertTrue(EqualsBuilder.reflectionEquals(def, found));
        Assert.assertEquals("updated description", found.getDescription());
        for (int i = 0; i < 9; i++) {
            TaskDef tdf = new TaskDef(("taskA" + i));
            dao.createTaskDef(tdf);
        }
        List<TaskDef> all = dao.getAllTaskDefs();
        Assert.assertNotNull(all);
        Assert.assertEquals(10, all.size());
        Set<String> allnames = all.stream().map(TaskDef::getName).collect(Collectors.toSet());
        Assert.assertEquals(10, allnames.size());
        List<String> sorted = allnames.stream().sorted().collect(Collectors.toList());
        Assert.assertEquals(def.getName(), sorted.get(0));
        for (int i = 0; i < 9; i++) {
            Assert.assertEquals(((def.getName()) + i), sorted.get((i + 1)));
        }
        for (int i = 0; i < 9; i++) {
            dao.removeTaskDef(((def.getName()) + i));
        }
        all = dao.getAllTaskDefs();
        Assert.assertNotNull(all);
        Assert.assertEquals(1, all.size());
        Assert.assertEquals(def.getName(), all.get(0).getName());
    }

    @Test(expected = ApplicationException.class)
    public void testRemoveTaskDef() throws Exception {
        dao.removeTaskDef(("test" + (UUID.randomUUID().toString())));
    }

    @Test
    public void testEventHandlers() {
        String event1 = "SQS::arn:account090:sqstest1";
        String event2 = "SQS::arn:account090:sqstest2";
        EventHandler eh = new EventHandler();
        eh.setName(UUID.randomUUID().toString());
        eh.setActive(false);
        EventHandler.Action action = new EventHandler.Action();
        action.setAction(start_workflow);
        action.setStart_workflow(new EventHandler.StartWorkflow());
        action.getStart_workflow().setName("workflow_x");
        eh.getActions().add(action);
        eh.setEvent(event1);
        dao.addEventHandler(eh);
        List<EventHandler> all = dao.getEventHandlers();
        Assert.assertNotNull(all);
        Assert.assertEquals(1, all.size());
        Assert.assertEquals(eh.getName(), all.get(0).getName());
        Assert.assertEquals(eh.getEvent(), all.get(0).getEvent());
        List<EventHandler> byEvents = dao.getEventHandlersForEvent(event1, true);
        Assert.assertNotNull(byEvents);
        Assert.assertEquals(0, byEvents.size());// event is marked as in-active

        eh.setActive(true);
        eh.setEvent(event2);
        dao.updateEventHandler(eh);
        all = dao.getEventHandlers();
        Assert.assertNotNull(all);
        Assert.assertEquals(1, all.size());
        byEvents = dao.getEventHandlersForEvent(event1, true);
        Assert.assertNotNull(byEvents);
        Assert.assertEquals(0, byEvents.size());
        byEvents = dao.getEventHandlersForEvent(event2, true);
        Assert.assertNotNull(byEvents);
        Assert.assertEquals(1, byEvents.size());
    }
}

