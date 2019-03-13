/**
 * (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.util;


import ParameterType.BOOLEAN;
import ValidationErrorType.INVALID_PARAM;
import ValidationErrorType.INVALID_VALUE;
import ValidationErrorType.MISSING;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.geoserver.taskmanager.AbstractTaskManagerTest;
import org.geoserver.taskmanager.beans.DummyAction;
import org.geoserver.taskmanager.beans.DummyTaskTypeImpl;
import org.geoserver.taskmanager.beans.TestTaskTypeImpl;
import org.geoserver.taskmanager.data.Batch;
import org.geoserver.taskmanager.data.Configuration;
import org.geoserver.taskmanager.data.Parameter;
import org.geoserver.taskmanager.data.Task;
import org.geoserver.taskmanager.data.TaskManagerDao;
import org.geoserver.taskmanager.data.TaskManagerFactory;
import org.geoserver.taskmanager.schedule.BatchContext;
import org.geoserver.taskmanager.schedule.ParameterType;
import org.geoserver.taskmanager.schedule.TaskContext;
import org.geoserver.taskmanager.schedule.TaskException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 *
 *
 * @author Niels Charlier
 */
public class TaskManagerTaskUtilTest extends AbstractTaskManagerTest {
    private static final String ATT_DELAY = "delay";

    private static final String ATT_FAIL = "fail";

    private static final String ATT_DUMMY = "dummy";

    @Autowired
    private TaskManagerDao dao;

    @Autowired
    private TaskManagerFactory fac;

    @Autowired
    private TaskManagerDataUtil util;

    @Autowired
    private TaskManagerTaskUtil taskUtil;

    private Configuration config;

    private Task task1;

    private Task task2;

    @Test
    public void testCreateContext() throws TaskException {
        TaskContext context = taskUtil.createContext(task1);
        Assert.assertEquals(task1, context.getTask());
        Assert.assertNotNull(context.getParameterValues());
        Assert.assertNull(context.getBatchContext());
        BatchContext bContext = taskUtil.createContext(fac.createBatchRun());
        bContext.put("foo", "bar");
        context = taskUtil.createContext(task1, bContext);
        Assert.assertEquals(task1, context.getTask());
        Assert.assertNotNull(context.getParameterValues());
        Assert.assertNotNull(context.getBatchContext());
        Assert.assertNotNull(context.getBatchContext().getBatchRun());
        Assert.assertEquals("bar", context.getBatchContext().get("foo"));
    }

    @Test
    public void testBatchContext() throws TaskException {
        final AtomicInteger counter = new AtomicInteger(0);
        BatchContext bContext = taskUtil.createContext(fac.createBatchRun());
        bContext.put("foo", "bar");
        bContext.get("foo", new BatchContext.Dependency() {
            @Override
            public void revert() throws TaskException {
                counter.set(((counter.get()) + 1));
            }
        });
        bContext.get("foo", new BatchContext.Dependency() {
            @Override
            public void revert() throws TaskException {
                counter.set(((counter.get()) + 2));
            }
        });
        bContext.delete("foo");
        Assert.assertEquals(3, counter.get());
    }

    @Test
    public void testGetActionsForAttribute() {
        List<String> actions = taskUtil.getActionsForAttribute(config.getAttributes().get(TaskManagerTaskUtilTest.ATT_FAIL), config);
        Assert.assertEquals(1, actions.size());
        Assert.assertEquals(DummyAction.NAME, actions.get(0));
    }

    @Test
    public void testGetAndUpdateDomains() {
        Map<String, List<String>> domains = taskUtil.getDomains(config);
        Assert.assertEquals(3, domains.size());
        Assert.assertEquals(Lists.newArrayList("true"), domains.get(TaskManagerTaskUtilTest.ATT_FAIL));
        Assert.assertEquals(Lists.newArrayList("dummy"), domains.get(TaskManagerTaskUtilTest.ATT_DUMMY));
        util.setConfigurationAttribute(config, TaskManagerTaskUtilTest.ATT_FAIL, "true");
        taskUtil.updateDependentDomains(config.getAttributes().get(TaskManagerTaskUtilTest.ATT_FAIL), config, domains);
        Assert.assertEquals(Lists.newArrayList("crash", "test", "dummy"), domains.get(TaskManagerTaskUtilTest.ATT_DUMMY));
        util.setConfigurationAttribute(config, TaskManagerTaskUtilTest.ATT_FAIL, "false");
        taskUtil.updateDomains(config, domains, Collections.singleton(TaskManagerTaskUtilTest.ATT_DUMMY));
        Assert.assertEquals(Lists.newArrayList("dummy"), domains.get(TaskManagerTaskUtilTest.ATT_DUMMY));
    }

    @Test
    public void testGetParameterValues() throws TaskException {
        Map<String, Object> map = taskUtil.getParameterValues(task1);
        Assert.assertTrue(((map.get(TestTaskTypeImpl.PARAM_FAIL)) instanceof Boolean));
        Assert.assertTrue(((map.get(TestTaskTypeImpl.PARAM_DELAY)) instanceof Integer));
    }

    @Test
    public void testGetTypesForAttribute() {
        Set<ParameterType> types = taskUtil.getTypesForAttribute(config.getAttributes().get(TaskManagerTaskUtilTest.ATT_FAIL), config);
        Assert.assertEquals(2, types.size());
        Assert.assertTrue(types.contains(BOOLEAN));
    }

    @Test
    public void testInitTask() {
        Task t = taskUtil.initTask("Dummy", "newDummyTask");
        Assert.assertEquals("newDummyTask", t.getName());
        Assert.assertEquals(2, t.getParameters().size());
        Assert.assertEquals("${param1}", t.getParameters().get(DummyTaskTypeImpl.PARAM1).getValue());
        Assert.assertEquals("${param2}", t.getParameters().get(DummyTaskTypeImpl.PARAM2).getValue());
    }

    @Test
    public void testFixTask() {
        Task t = taskUtil.initTask("Dummy", "newDummyTask");
        t.getParameters().remove(DummyTaskTypeImpl.PARAM2);
        Assert.assertEquals(1, t.getParameters().size());
        Assert.assertEquals("${param1}", t.getParameters().get(DummyTaskTypeImpl.PARAM1).getValue());
        taskUtil.fixTask(t);
        Assert.assertEquals(2, t.getParameters().size());
        Assert.assertEquals("${param1}", t.getParameters().get(DummyTaskTypeImpl.PARAM1).getValue());
        Assert.assertEquals("${param2}", t.getParameters().get(DummyTaskTypeImpl.PARAM2).getValue());
    }

    @Test
    public void testCopyTask() {
        Task t = taskUtil.copyTask(task1, "copiedTask");
        Assert.assertEquals("copiedTask", t.getName());
        Assert.assertEquals(task1.getType(), t.getType());
        Assert.assertEquals(task1.getParameters().size(), t.getParameters().size());
        for (Parameter par : task1.getParameters().values()) {
            Assert.assertEquals(par.getValue(), t.getParameters().get(par.getName()).getValue());
        }
    }

    @Test
    public void testValidate() {
        util.setConfigurationAttribute(config, TaskManagerTaskUtilTest.ATT_FAIL, "true");
        Assert.assertTrue(taskUtil.validate(config).isEmpty());
        util.setConfigurationAttribute(config, TaskManagerTaskUtilTest.ATT_DELAY, "boo");
        util.setConfigurationAttribute(config, TaskManagerTaskUtilTest.ATT_DUMMY, null);
        util.setTaskParameter(task1, "foo", "bar");
        List<ValidationError> errors = taskUtil.validate(config);
        Assert.assertFalse(errors.isEmpty());
        Assert.assertEquals(3, errors.size());
        Assert.assertEquals(INVALID_VALUE, errors.get(0).getType());
        Assert.assertEquals(INVALID_PARAM, errors.get(1).getType());
        Assert.assertEquals(MISSING, errors.get(2).getType());
    }

    @Test
    public void testCanCleanup() {
        Assert.assertFalse(taskUtil.canCleanup(task1));
        Assert.assertTrue(taskUtil.canCleanup(config));
        Assert.assertTrue(taskUtil.canCleanup(task2));
        config.getTasks().remove("task2");
        Assert.assertFalse(taskUtil.canCleanup(config));
        // (cleanup itself is suffiently tested in other tests
    }

    @Test
    public void testOrderTasksForCleanup() {
        Task task3 = fac.createTask();
        task3.setName("task3");
        task3.setType(TestTaskTypeImpl.NAME);
        util.addTaskToConfiguration(config, task3);
        Task task4 = fac.createTask();
        task4.setName("task4");
        task4.setType(TestTaskTypeImpl.NAME);
        util.addTaskToConfiguration(config, task4);
        Batch init = fac.createBatch();
        init.setName("@Initialize");
        util.addBatchToConfiguration(config, init);
        util.addBatchElement(init, task3);
        config = dao.save(config);
        task1 = config.getTasks().get("task1");
        task2 = config.getTasks().get("task2");
        task3 = config.getTasks().get("task3");
        task4 = config.getTasks().get("task4");
        Batch batch1 = fac.createBatch();
        batch1.setName("batch1");
        util.addBatchToConfiguration(config, batch1);
        util.addBatchElement(batch1, task1);
        util.addBatchElement(batch1, task4);
        Batch batch2 = fac.createBatch();
        batch2.setName("batch2");
        util.addBatchToConfiguration(config, batch2);
        util.addBatchElement(batch2, task2);
        util.addBatchElement(batch2, task1);
        config = dao.save(config);
        List<Task> orderedTasks = taskUtil.orderTasksForCleanup(config);
        Assert.assertEquals(4, orderedTasks.size());
        Assert.assertEquals("task4", orderedTasks.get(0).getName());
        Assert.assertEquals("task1", orderedTasks.get(1).getName());
        Assert.assertEquals("task2", orderedTasks.get(2).getName());
        Assert.assertEquals("task3", orderedTasks.get(3).getName());
    }

    @Test
    public void testGetDependantRawValues() {
        List<String> rawValues = taskUtil.getDependentRawValues(DummyAction.NAME, config.getAttributes().get(TaskManagerTaskUtilTest.ATT_DUMMY), config);
        Assert.assertEquals(1, rawValues.size());
        Assert.assertEquals("false", rawValues.get(0));
    }
}

