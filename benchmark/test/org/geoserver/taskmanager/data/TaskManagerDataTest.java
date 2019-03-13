/**
 * (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.data;


import Status.COMMITTED;
import Status.COMMITTING;
import Status.NOT_COMMITTED;
import Status.READY_TO_COMMIT;
import java.util.Collections;
import java.util.Date;
import org.geoserver.taskmanager.AbstractTaskManagerTest;
import org.geoserver.taskmanager.util.TaskManagerDataUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Test data methods.
 *
 * @author Niels Charlier
 */
public class TaskManagerDataTest extends AbstractTaskManagerTest {
    @Autowired
    private TaskManagerDao dao;

    @Autowired
    private TaskManagerFactory fac;

    @Autowired
    private TaskManagerDataUtil util;

    private Configuration config;

    private Batch batch;

    @Test
    public void testBatchElement() {
        Assert.assertEquals(Collections.singletonList(config.getTasks().get("task")), dao.getTasksAvailableForBatch(batch));
        Task task = config.getTasks().get("task");
        BatchElement el = util.addBatchElement(batch, task);
        batch = dao.save(batch);
        Assert.assertEquals(1, batch.getElements().size());
        Assert.assertEquals(Collections.emptyList(), dao.getTasksAvailableForBatch(batch));
        el = batch.getElements().get(0);
        // soft delete
        dao.remove(el);
        batch = dao.init(batch);
        Assert.assertTrue(batch.getElements().isEmpty());
        Assert.assertEquals(Collections.singletonList(config.getTasks().get("task")), dao.getTasksAvailableForBatch(batch));
        BatchElement el2 = util.addBatchElement(batch, task);
        Assert.assertEquals(el.getId(), el2.getId());
        batch = dao.save(batch);
        Assert.assertEquals(1, batch.getElements().size());
        Assert.assertEquals(Collections.emptyList(), dao.getTasksAvailableForBatch(batch));
        // hard delete
        dao.delete(batch.getElements().get(0));
        batch = dao.init(batch);
        Assert.assertTrue(batch.getElements().isEmpty());
        el2 = util.addBatchElement(batch, task);
        Assert.assertFalse(el.getId().equals(el2.getId()));
    }

    @Test
    public void testCloneConfiguration() {
        Task task = config.getTasks().get("task");
        Assert.assertEquals(0, task.getBatchElements().size());
        BatchElement el = util.addBatchElement(batch, task);
        batch = dao.save(batch);
        el = batch.getElements().get(0);
        config = dao.init(config);
        task = config.getTasks().get("task");
        Assert.assertEquals(1, task.getBatchElements().size());
        Assert.assertEquals(dao.reload(el), task.getBatchElements().get(0));
        Configuration config2 = dao.copyConfiguration("my_config");
        config2.setName("my_config2");
        config2 = dao.save(config2);
        task = config2.getTasks().get("task");
        Assert.assertEquals(0, task.getBatchElements().size());
        dao.delete(config2);
        batch.setConfiguration(config);
        batch = dao.save(batch);
        config2 = dao.copyConfiguration("my_config");
        config2.setName("my_config2");
        config2 = dao.save(config2);
        task = config2.getTasks().get("task");
        Assert.assertEquals(1, task.getBatchElements().size());
        Assert.assertFalse(config2.getBatches().get("my_batch").isEnabled());
        dao.delete(config2);
    }

    @Test
    public void testBatchRun() {
        Assert.assertTrue(dao.getCurrentBatchRuns(batch).isEmpty());
        BatchRun br = fac.createBatchRun();
        br.setBatch(batch);
        Run run = fac.createRun();
        run.setBatchRun(br);
        run.setStart(new Date(1000));
        run.setEnd(new Date(2000));
        run.setStatus(READY_TO_COMMIT);
        br.getRuns().add(run);
        run = fac.createRun();
        run.setBatchRun(br);
        run.setStart(new Date(2000));
        run.setEnd(new Date(3000));
        run.setStatus(COMMITTING);
        run.setMessage("foo");
        br.getRuns().add(run);
        run = fac.createRun();
        run.setBatchRun(br);
        run.setStart(new Date(3000));
        run.setEnd(new Date(4000));
        run.setStatus(COMMITTED);
        br.getRuns().add(run);
        Assert.assertEquals(COMMITTING, br.getStatus());
        br = dao.save(br);
        Assert.assertEquals(1, dao.getCurrentBatchRuns(batch).size());
        br = util.closeBatchRun(br, "foo");
        Assert.assertEquals(0, dao.getCurrentBatchRuns(batch).size());
        Assert.assertEquals(new Date(1000), br.getStart());
        Assert.assertEquals(new Date(4000), br.getEnd());
        Assert.assertEquals("foo", br.getMessage());
        Assert.assertEquals(NOT_COMMITTED, br.getStatus());
    }
}

