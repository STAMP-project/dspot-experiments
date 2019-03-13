package org.kairosdb.rollup;


import RollUpTasksStoreImpl.OLD_FILENAME;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kairosdb.core.exception.DatastoreException;
import org.kairosdb.core.http.rest.QueryException;


public class RollUpTasksStoreImplTest extends RollupTestBase {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private RollUpTasksStore store;

    @Test
    public void test_writeRead() throws DatastoreException, RollUpException {
        addTasks(RollupTestBase.TASK1, RollupTestBase.TASK2, RollupTestBase.TASK3);
        store.write(ImmutableList.of(RollupTestBase.TASK1, RollupTestBase.TASK2, RollupTestBase.TASK3, RollupTestBase.TASK4));
        Map<String, RollupTask> tasks = store.read();
        Assert.assertThat(tasks.size(), CoreMatchers.equalTo(4));
        Assert.assertThat(tasks, hasEntry(RollupTestBase.TASK1.getId(), RollupTestBase.TASK1));
        Assert.assertThat(tasks, hasEntry(RollupTestBase.TASK2.getId(), RollupTestBase.TASK2));
        Assert.assertThat(tasks, hasEntry(RollupTestBase.TASK3.getId(), RollupTestBase.TASK3));
        Assert.assertThat(tasks, hasEntry(RollupTestBase.TASK4.getId(), RollupTestBase.TASK4));
    }

    @Test
    public void test_listIds() throws DatastoreException, RollUpException {
        addTasks(RollupTestBase.TASK1, RollupTestBase.TASK2, RollupTestBase.TASK3);
        store.write(ImmutableList.of(RollupTestBase.TASK1, RollupTestBase.TASK2, RollupTestBase.TASK3, RollupTestBase.TASK4));
        Set<String> ids = store.listIds();
        Assert.assertThat(ids.size(), CoreMatchers.equalTo(4));
        Assert.assertThat(ids, CoreMatchers.hasItem(RollupTestBase.TASK1.getId()));
        Assert.assertThat(ids, CoreMatchers.hasItem(RollupTestBase.TASK2.getId()));
        Assert.assertThat(ids, CoreMatchers.hasItem(RollupTestBase.TASK3.getId()));
        Assert.assertThat(ids, CoreMatchers.hasItem(RollupTestBase.TASK4.getId()));
    }

    @Test
    public void test_remove() throws DatastoreException, RollUpException {
        addTasks(RollupTestBase.TASK1, RollupTestBase.TASK2, RollupTestBase.TASK3, RollupTestBase.TASK4);
        store.write(ImmutableList.of(RollupTestBase.TASK1, RollupTestBase.TASK2, RollupTestBase.TASK3, RollupTestBase.TASK4));
        Map<String, RollupTask> tasks = store.read();
        Assert.assertThat(tasks.size(), CoreMatchers.equalTo(4));
        Assert.assertThat(tasks, hasEntry(RollupTestBase.TASK1.getId(), RollupTestBase.TASK1));
        Assert.assertThat(tasks, hasEntry(RollupTestBase.TASK2.getId(), RollupTestBase.TASK2));
        Assert.assertThat(tasks, hasEntry(RollupTestBase.TASK3.getId(), RollupTestBase.TASK3));
        Assert.assertThat(tasks, hasEntry(RollupTestBase.TASK4.getId(), RollupTestBase.TASK4));
        store.remove(RollupTestBase.TASK2.getId());
        store.remove(RollupTestBase.TASK3.getId());
        tasks = store.read();
        Assert.assertThat(tasks.size(), CoreMatchers.equalTo(2));
        Assert.assertThat(tasks, hasEntry(RollupTestBase.TASK1.getId(), RollupTestBase.TASK1));
        Assert.assertThat(tasks, hasEntry(RollupTestBase.TASK4.getId(), RollupTestBase.TASK4));
    }

    @Test
    public void test_import() throws IOException, QueryException, RollUpException {
        String oldFormat = Resources.toString(Resources.getResource("rollup_old_format.config"), Charsets.UTF_8);
        String[] lines = oldFormat.split("\n");
        List<RollupTask> oldTasks = new ArrayList<>();
        for (String line : lines) {
            oldTasks.add(queryParser.parseRollupTask(line));
        }
        Path path = Paths.get(OLD_FILENAME);
        try {
            Files.write(path, oldFormat.getBytes());
            Assert.assertTrue(Files.exists(path));
            RollUpTasksStoreImpl store = new RollUpTasksStoreImpl(fakeServiceKeyStore, queryParser);
            Map<String, RollupTask> tasks = store.read();
            Assert.assertThat(tasks.size(), CoreMatchers.equalTo(2));
            for (RollupTask oldTask : oldTasks) {
                Assert.assertThat(tasks.values(), CoreMatchers.hasItem(oldTask));
            }
        } finally {
            if (Files.exists(path)) {
                Files.delete(path);
            }
        }
    }

    @Test
    public void test_import_oldFileNotExists() throws IOException, QueryException, RollUpException {
        Path path = Paths.get(OLD_FILENAME);
        TestCase.assertFalse(Files.exists(path));
        RollUpTasksStoreImpl store = new RollUpTasksStoreImpl(fakeServiceKeyStore, queryParser);
        Map<String, RollupTask> tasks = store.read();
        Assert.assertThat(tasks.size(), CoreMatchers.equalTo(0));
    }
}

