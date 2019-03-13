package org.kairosdb.rollup;


import com.google.common.collect.ImmutableList;
import java.util.List;
import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kairosdb.core.HostManager;
import org.kairosdb.core.datastore.TimeUnit;
import org.kairosdb.core.exception.DatastoreException;
import org.mockito.Mock;


public class AssignmentManagerTest extends RollupTestBase {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private HostManager mockHostManager;

    private RollupTaskStatusStore statusStore = new RollupTaskStatusStoreImpl(fakeServiceKeyStore);

    private BalancingAlgorithm balancingAlgorithm = new ScoreBalancingAlgorithm();

    private AssignmentManager manager;

    @Test
    public void testConstructor_guid_null_invalid() throws RollUpException {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("guid cannot be null or empty");
        new AssignmentManager(null, taskStore, assignmentStore, statusStore, mockExecutionService, mockHostManager, balancingAlgorithm, 10);
    }

    @Test
    public void testConstructor_hostname_empty_invalid() throws RollUpException {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("guid cannot be null or empty");
        new AssignmentManager("", taskStore, assignmentStore, statusStore, mockExecutionService, mockHostManager, balancingAlgorithm, 10);
    }

    @Test
    public void testConstructor_taskStore_null_invalid() throws RollUpException {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("taskStore cannot be null");
        new AssignmentManager("guid", null, assignmentStore, statusStore, mockExecutionService, mockHostManager, balancingAlgorithm, 10);
    }

    @Test
    public void testConstructor_assignmentStore_null_invalid() throws RollUpException {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("assignmentStore cannot be null");
        new AssignmentManager("guid", taskStore, null, statusStore, mockExecutionService, mockHostManager, balancingAlgorithm, 10);
    }

    @Test
    public void testConstructor_statusStore_null_invalid() throws RollUpException {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("statusStore cannot be null");
        new AssignmentManager("guid", taskStore, assignmentStore, null, mockExecutionService, mockHostManager, balancingAlgorithm, 10);
    }

    @Test
    public void testConstructor_executor_null_invalid() throws RollUpException {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("executorService cannot be null");
        new AssignmentManager("guid", taskStore, assignmentStore, statusStore, null, mockHostManager, balancingAlgorithm, 10);
    }

    @Test
    public void testConstructor_balancing_null_invalid() throws RollUpException {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("balancing cannot be null");
        new AssignmentManager("guid", taskStore, assignmentStore, statusStore, mockExecutionService, mockHostManager, null, 10);
    }

    @Test
    public void test_removeAssignmentForDeletedTasks() throws DatastoreException, RollUpException {
        setupActiveHosts(RollupTestBase.LOCAL_HOST);
        addTasks(RollupTestBase.TASK1, RollupTestBase.TASK2, RollupTestBase.TASK3);
        addStatuses(RollupTestBase.TASK1, RollupTestBase.TASK2, RollupTestBase.TASK3);
        TestCase.assertNotNull(statusStore.read(RollupTestBase.TASK1.getId()));
        TestCase.assertNotNull(statusStore.read(RollupTestBase.TASK2.getId()));
        TestCase.assertNotNull(statusStore.read(RollupTestBase.TASK3.getId()));
        manager.checkAssignmentChanges();
        // Remove task
        removeTasks(RollupTestBase.TASK2);
        manager.checkAssignmentChanges();
        MatcherAssert.assertThat(getAssignedHost(RollupTestBase.TASK1.getId()), CoreMatchers.equalTo(RollupTestBase.LOCAL_HOST));
        TestCase.assertNull(getAssignedHost(RollupTestBase.TASK2.getId()));
        MatcherAssert.assertThat(getAssignedHost(RollupTestBase.TASK3.getId()), CoreMatchers.equalTo(RollupTestBase.LOCAL_HOST));
        TestCase.assertNull(statusStore.read(RollupTestBase.TASK2.getId()));
    }

    @Test
    public void test_reassignTasksForInactiveHosts() throws DatastoreException, RollUpException {
        setupActiveHosts(RollupTestBase.LOCAL_HOST, "hostname1", "hostname2");
        addTasks(RollupTestBase.TASK1, RollupTestBase.TASK2, RollupTestBase.TASK3);
        manager.checkAssignmentChanges();
        // All servers are assigned 1 task
        List<Long> serverAssignmentCounts = getServerAssignmentCounts(assignmentStore.getAssignments());
        MatcherAssert.assertThat(serverAssignmentCounts.size(), CoreMatchers.equalTo(3));
        MatcherAssert.assertThat(serverAssignmentCounts.get(0), CoreMatchers.equalTo(1L));
        MatcherAssert.assertThat(serverAssignmentCounts.get(1), CoreMatchers.equalTo(1L));
        MatcherAssert.assertThat(serverAssignmentCounts.get(2), CoreMatchers.equalTo(1L));
        setupActiveHosts(RollupTestBase.LOCAL_HOST, "hostname1");
        manager.checkAssignmentChanges();
        // One server is assigned 1 task and the other 2 tasks
        serverAssignmentCounts = getServerAssignmentCounts(assignmentStore.getAssignments());
        MatcherAssert.assertThat(serverAssignmentCounts.size(), CoreMatchers.equalTo(2));
        TestCase.assertTrue(serverAssignmentCounts.contains(1L));
        TestCase.assertTrue(serverAssignmentCounts.contains(2L));
    }

    @Test
    public void test_addUnassignedTasks() throws DatastoreException, RollUpException {
        setupActiveHosts(RollupTestBase.LOCAL_HOST);
        addTasks(RollupTestBase.TASK1, RollupTestBase.TASK2, RollupTestBase.TASK3);
        manager.checkAssignmentChanges();
        // Add Task
        addTasks(RollupTestBase.TASK4);
        manager.checkAssignmentChanges();
        MatcherAssert.assertThat(assignmentStore.getAssignments().get(RollupTestBase.TASK4.getId()), CoreMatchers.equalTo(RollupTestBase.LOCAL_HOST));
    }

    @Test
    public void test_addUnassignedAndRemoveReasignedAndUnassignRemovedTasks() throws DatastoreException, RollUpException {
        setupActiveHosts(RollupTestBase.LOCAL_HOST, "hostname1", "hostname2");
        addTasks(RollupTestBase.TASK1, RollupTestBase.TASK2, RollupTestBase.TASK3);
        assignmentStore.setAssignment(RollupTestBase.TASK1.getId(), RollupTestBase.LOCAL_HOST);
        assignmentStore.setAssignment(RollupTestBase.TASK2.getId(), "hostname1");
        assignmentStore.setAssignment(RollupTestBase.TASK3.getId(), "hostname2");
        manager.checkAssignmentChanges();
        // Remove and Add task
        removeTasks(RollupTestBase.TASK2);
        setupActiveHosts(RollupTestBase.LOCAL_HOST, "hostname1");
        addTasks(RollupTestBase.TASK4);
        manager.checkAssignmentChanges();
        // One server is assigned 1 task and the other 2 tasks
        List<Long> serverAssignmentCounts = getServerAssignmentCounts(assignmentStore.getAssignments());
        MatcherAssert.assertThat(serverAssignmentCounts.size(), CoreMatchers.equalTo(2));
        TestCase.assertTrue(serverAssignmentCounts.contains(1L));
        TestCase.assertTrue(serverAssignmentCounts.contains(2L));
        TestCase.assertNotNull(assignmentStore.getAssignments().get(RollupTestBase.TASK1.getId()));
        TestCase.assertNull(assignmentStore.getAssignments().get(RollupTestBase.TASK2.getId()));
        TestCase.assertNotNull(assignmentStore.getAssignments().get(RollupTestBase.TASK3.getId()));
        TestCase.assertNotNull(assignmentStore.getAssignments().get(RollupTestBase.TASK4.getId()));
    }

    @Test
    public void test_rebalance() throws DatastoreException, RollUpException {
        setupActiveHosts(RollupTestBase.LOCAL_HOST, "hostname1", "hostname2");
        addTasks(RollupTestBase.TASK1, RollupTestBase.TASK2, RollupTestBase.TASK3, RollupTestBase.TASK4, RollupTestBase.TASK5);
        assignmentStore.setAssignment(RollupTestBase.TASK1.getId(), "hostname1");
        assignmentStore.setAssignment(RollupTestBase.TASK2.getId(), "hostname2");
        assignmentStore.setAssignment(RollupTestBase.TASK3.getId(), "hostname1");
        assignmentStore.setAssignment(RollupTestBase.TASK4.getId(), "hostname2");
        assignmentStore.setAssignment(RollupTestBase.TASK5.getId(), "hostname1");
        manager.checkAssignmentChanges();
        // One server is assigned 1 task and the other 2 have 2 tasks
        List<Long> serverAssignmentCounts = getServerAssignmentCounts(assignmentStore.getAssignments());
        MatcherAssert.assertThat(serverAssignmentCounts.size(), CoreMatchers.equalTo(3));
        TestCase.assertTrue(serverAssignmentCounts.contains(1L));
        TestCase.assertTrue(serverAssignmentCounts.contains(2L));
        TestCase.assertTrue(serverAssignmentCounts.contains(2L));
    }

    @Test
    public void test_score() {
        RollupTask task1 = new RollupTask("1", new org.kairosdb.core.datastore.Duration(1, TimeUnit.SECONDS), ImmutableList.of(new Rollup()));
        RollupTask task2 = new RollupTask("2", new org.kairosdb.core.datastore.Duration(10, TimeUnit.SECONDS), ImmutableList.of(new Rollup()));
        RollupTask task3 = new RollupTask("3", new org.kairosdb.core.datastore.Duration(1, TimeUnit.MINUTES), ImmutableList.of(new Rollup()));
        RollupTask task4 = new RollupTask("4", new org.kairosdb.core.datastore.Duration(1, TimeUnit.HOURS), ImmutableList.of(new Rollup()));
        RollupTask task5 = new RollupTask("5", new org.kairosdb.core.datastore.Duration(60, TimeUnit.MINUTES), ImmutableList.of(new Rollup()));
        RollupTask task6 = new RollupTask("6", new org.kairosdb.core.datastore.Duration(60, TimeUnit.SECONDS), ImmutableList.of(new Rollup()));
        RollupTask task7 = new RollupTask("7", new org.kairosdb.core.datastore.Duration(60, TimeUnit.MONTHS), ImmutableList.of(new Rollup()));
        RollupTask task8 = new RollupTask("8", new org.kairosdb.core.datastore.Duration(60, TimeUnit.YEARS), ImmutableList.of(new Rollup()));
        MatcherAssert.assertThat(AssignmentManager.score(task1), CoreMatchers.equalTo(120L));
        MatcherAssert.assertThat(AssignmentManager.score(task2), CoreMatchers.equalTo(111L));
        MatcherAssert.assertThat(AssignmentManager.score(task3), CoreMatchers.equalTo(60L));
        MatcherAssert.assertThat(AssignmentManager.score(task4), CoreMatchers.equalTo(1L));
        MatcherAssert.assertThat(AssignmentManager.score(task5), CoreMatchers.equalTo(1L));
        MatcherAssert.assertThat(AssignmentManager.score(task6), CoreMatchers.equalTo(61L));
        MatcherAssert.assertThat(AssignmentManager.score(task7), CoreMatchers.equalTo(1L));
        MatcherAssert.assertThat(AssignmentManager.score(task8), CoreMatchers.equalTo(1L));
    }
}

