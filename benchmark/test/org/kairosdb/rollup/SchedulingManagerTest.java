package org.kairosdb.rollup;


import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.kairosdb.core.datastore.KairosDatastore;
import org.kairosdb.core.exception.KairosDBException;
import org.kairosdb.core.scheduler.KairosDBScheduler;
import org.kairosdb.eventbus.FilterEventBus;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.quartz.impl.JobDetailImpl;


public class SchedulingManagerTest extends RollupTestBase {
    private static final String SERVER_GUID = "12345";

    @Mock
    private KairosDatastore mockDatastore;

    @Mock
    private KairosDBScheduler mockScheduler;

    @Mock
    private FilterEventBus mockEventBus;

    @Mock
    private RollupTaskStatusStore mockStatusStore;

    private SchedulingManager manager;

    @Test
    public void testConstructor_taskStore_null_invalid() throws RollUpException {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("taskStore cannot be null");
        new SchedulingManager(null, assignmentStore, mockScheduler, mockDatastore, mockExecutionService, mockEventBus, mockStatusStore, 10, RollupTestBase.LOCAL_HOST, SchedulingManagerTest.SERVER_GUID);
    }

    @Test
    public void testConstructor_assignmentStore_null_invalid() throws RollUpException {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("assignmentStore cannot be null");
        new SchedulingManager(taskStore, null, mockScheduler, mockDatastore, mockExecutionService, mockEventBus, mockStatusStore, 10, "hostname", SchedulingManagerTest.SERVER_GUID);
    }

    @Test
    public void testConstructor_scheduler_null_invalid() throws RollUpException {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("scheduler cannot be null");
        new SchedulingManager(taskStore, assignmentStore, null, mockDatastore, mockExecutionService, mockEventBus, mockStatusStore, 10, "hostname", SchedulingManagerTest.SERVER_GUID);
    }

    @Test
    public void testConstructor_dataStore_null_invalid() throws RollUpException {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("dataStore cannot be null");
        new SchedulingManager(taskStore, assignmentStore, mockScheduler, null, mockExecutionService, mockEventBus, mockStatusStore, 10, "hostname", SchedulingManagerTest.SERVER_GUID);
    }

    @Test
    public void testConstructor_executor_null_invalid() throws RollUpException {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("executorService cannot be null");
        new SchedulingManager(taskStore, assignmentStore, mockScheduler, mockDatastore, null, mockEventBus, mockStatusStore, 10, "hostname", SchedulingManagerTest.SERVER_GUID);
    }

    @Test
    public void testConstructor_hostname_null_invalid() throws RollUpException {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("hostname cannot be null or empty");
        new SchedulingManager(taskStore, assignmentStore, mockScheduler, mockDatastore, mockExecutionService, mockEventBus, mockStatusStore, 10, null, SchedulingManagerTest.SERVER_GUID);
    }

    @Test
    public void testConstructor_hostname_empty_invalid() throws RollUpException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("hostname cannot be null or empty");
        new SchedulingManager(taskStore, assignmentStore, mockScheduler, mockDatastore, mockExecutionService, mockEventBus, mockStatusStore, 10, "", SchedulingManagerTest.SERVER_GUID);
    }

    @Test
    public void testConstructor_eventBus_null_invalid() throws RollUpException {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("eventBus cannot be null");
        new SchedulingManager(taskStore, assignmentStore, mockScheduler, mockDatastore, mockExecutionService, null, mockStatusStore, 10, "hostname", SchedulingManagerTest.SERVER_GUID);
    }

    @Test
    public void testConstructor_scheduleNewTasks() throws KairosDBException {
        addTasks(RollupTestBase.TASK1, RollupTestBase.TASK2, RollupTestBase.TASK3);
        assignmentStore.setAssignment(RollupTestBase.TASK1.getId(), SchedulingManagerTest.SERVER_GUID);
        assignmentStore.setAssignment(RollupTestBase.TASK2.getId(), SchedulingManagerTest.SERVER_GUID);
        assignmentStore.setAssignment(RollupTestBase.TASK3.getId(), SchedulingManagerTest.SERVER_GUID);
        manager.checkSchedulingChanges();
        Mockito.verify(mockScheduler, Mockito.times(1)).schedule(SchedulingManager.createJobDetail(RollupTestBase.TASK1, mockDatastore, RollupTestBase.LOCAL_HOST, mockEventBus, mockStatusStore), SchedulingManager.createTrigger(RollupTestBase.TASK1));
        Mockito.verify(mockScheduler, Mockito.times(1)).schedule(SchedulingManager.createJobDetail(RollupTestBase.TASK2, mockDatastore, RollupTestBase.LOCAL_HOST, mockEventBus, mockStatusStore), SchedulingManager.createTrigger(RollupTestBase.TASK2));
        Mockito.verify(mockScheduler, Mockito.times(1)).schedule(SchedulingManager.createJobDetail(RollupTestBase.TASK3, mockDatastore, RollupTestBase.LOCAL_HOST, mockEventBus, mockStatusStore), SchedulingManager.createTrigger(RollupTestBase.TASK3));
    }

    @Test
    public void testModifiedTasks() throws KairosDBException {
        assignmentStore.setAssignment(RollupTestBase.TASK1.getId(), SchedulingManagerTest.SERVER_GUID);
        assignmentStore.setAssignment(RollupTestBase.TASK2.getId(), SchedulingManagerTest.SERVER_GUID);
        assignmentStore.setAssignment(RollupTestBase.TASK3.getId(), SchedulingManagerTest.SERVER_GUID);
        addTasks(RollupTestBase.TASK1, RollupTestBase.TASK2, RollupTestBase.TASK3);
        manager.checkSchedulingChanges();
        // modify task
        RollupTask modifiedTask = new RollupTask(RollupTestBase.TASK2.getId(), RollupTestBase.TASK2.getName(), RollupTestBase.TASK2.getExecutionInterval(), RollupTestBase.TASK2.getRollups(), (((("{\"id\": " + (RollupTestBase.TASK2.getId())) + ",\"name\": \"") + (RollupTestBase.TASK2.getName())) + "\", \"execution_interval\": {\"value\": 1, \"unit\": \"hours\"}}"));
        modifiedTask.setLastModified(((System.currentTimeMillis()) + 10));
        removeTasks(RollupTestBase.TASK2);
        addTasks(modifiedTask);
        manager.checkSchedulingChanges();
        JobDetailImpl job = SchedulingManager.createJobDetail(RollupTestBase.TASK2, mockDatastore, RollupTestBase.LOCAL_HOST, mockEventBus, mockStatusStore);
        Mockito.verify(mockScheduler, Mockito.times(1)).cancel(job.getKey());
        Mockito.verify(mockScheduler, Mockito.times(2)).schedule(SchedulingManager.createJobDetail(RollupTestBase.TASK2, mockDatastore, RollupTestBase.LOCAL_HOST, mockEventBus, mockStatusStore), SchedulingManager.createTrigger(RollupTestBase.TASK2));
    }

    @Test
    public void testUnscheduleRemovedTasks() throws KairosDBException {
        assignmentStore.setAssignment(RollupTestBase.TASK1.getId(), SchedulingManagerTest.SERVER_GUID);
        assignmentStore.setAssignment(RollupTestBase.TASK2.getId(), SchedulingManagerTest.SERVER_GUID);
        assignmentStore.setAssignment(RollupTestBase.TASK3.getId(), SchedulingManagerTest.SERVER_GUID);
        addTasks(RollupTestBase.TASK1, RollupTestBase.TASK2, RollupTestBase.TASK3);
        manager.checkSchedulingChanges();
        // Remove task
        removeTasks(RollupTestBase.TASK2);
        manager.checkSchedulingChanges();
        JobDetailImpl job = SchedulingManager.createJobDetail(RollupTestBase.TASK2, mockDatastore, RollupTestBase.LOCAL_HOST, mockEventBus, mockStatusStore);
        Mockito.verify(mockScheduler, Mockito.times(1)).cancel(job.getKey());
        Mockito.verify(mockScheduler, Mockito.times(1)).schedule(SchedulingManager.createJobDetail(RollupTestBase.TASK2, mockDatastore, RollupTestBase.LOCAL_HOST, mockEventBus, mockStatusStore), SchedulingManager.createTrigger(RollupTestBase.TASK2));
    }

    @Test
    public void testUnschedulUnassignedTasks() throws KairosDBException {
        assignmentStore.setAssignment(RollupTestBase.TASK1.getId(), SchedulingManagerTest.SERVER_GUID);
        assignmentStore.setAssignment(RollupTestBase.TASK2.getId(), SchedulingManagerTest.SERVER_GUID);
        assignmentStore.setAssignment(RollupTestBase.TASK3.getId(), SchedulingManagerTest.SERVER_GUID);
        addTasks(RollupTestBase.TASK1, RollupTestBase.TASK2, RollupTestBase.TASK3);
        manager.checkSchedulingChanges();
        // Remove assignment task
        assignmentStore.removeAssignments(ImmutableSet.of(RollupTestBase.TASK2.getId()));
        manager.checkSchedulingChanges();
        JobDetailImpl job = SchedulingManager.createJobDetail(RollupTestBase.TASK2, mockDatastore, RollupTestBase.LOCAL_HOST, mockEventBus, mockStatusStore);
        Mockito.verify(mockScheduler, Mockito.times(1)).cancel(job.getKey());
        Mockito.verify(mockScheduler, Mockito.times(1)).schedule(SchedulingManager.createJobDetail(RollupTestBase.TASK2, mockDatastore, RollupTestBase.LOCAL_HOST, mockEventBus, mockStatusStore), SchedulingManager.createTrigger(RollupTestBase.TASK2));
    }
}

