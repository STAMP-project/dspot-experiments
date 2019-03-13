/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.web;


import Status.READY_TO_COMMIT;
import Status.ROLLED_BACK;
import Status.RUNNING;
import java.util.Date;
import org.geoserver.taskmanager.AbstractWicketTaskManagerTest;
import org.geoserver.taskmanager.data.Batch;
import org.geoserver.taskmanager.data.BatchRun;
import org.geoserver.taskmanager.data.Run;
import org.geoserver.taskmanager.data.TaskManagerDao;
import org.geoserver.taskmanager.data.TaskManagerFactory;
import org.geoserver.taskmanager.schedule.BatchJobService;
import org.geoserver.taskmanager.util.TaskManagerDataUtil;
import org.geoserver.taskmanager.util.TaskManagerTaskUtil;
import org.junit.Test;


public class BatchRunTest extends AbstractWicketTaskManagerTest {
    protected TaskManagerFactory fac;

    protected TaskManagerDao dao;

    private TaskManagerDataUtil util;

    private TaskManagerTaskUtil tutil;

    private BatchJobService bjservice;

    private Batch batch;

    @Test
    public void testRunStop() throws InterruptedException {
        tester.startPage(new BatchesPage());
        tester.assertRenderedPage(BatchesPage.class);
        tester.clickLink("batchesPanel:form:batchesPanel:listContainer:items:1:itemProperties:6:component:link");
        Thread.sleep(500);
        tester.clickLink("batchesPanel:refresh");
        tester.assertModelValue("batchesPanel:form:batchesPanel:listContainer:items:2:itemProperties:7:component", RUNNING);
        tester.clickLink("batchesPanel:form:batchesPanel:listContainer:items:2:itemProperties:7:component:link");
        tester.assertRenderedPage(BatchRunsPage.class);
        tester.assertModelValue("form:runsPanel:listContainer:items:1:itemProperties:2:component", RUNNING);
        tester.clickLink("form:runsPanel:listContainer:items:1:itemProperties:0:component:link");
        tester.assertRenderedPage(BatchRunPage.class);
        tester.assertModelValue("runPanel:listContainer:items:1:itemProperties:3:component", READY_TO_COMMIT);
        tester.assertModelValue("runPanel:listContainer:items:2:itemProperties:3:component", RUNNING);
        tester.clickLink("close");
        tester.assertRenderedPage(BatchRunsPage.class);
        tester.clickLink("form:runsPanel:listContainer:items:1:itemProperties:3:component:link");
        BatchRun br = dao.initHistory(batch).getBatchRuns().get(0);
        while (!((br = dao.reload(br)).getStatus().isClosed())) {
            Thread.sleep(100);
        } 
        tester.clickLink("refresh");
        tester.assertModelValue("form:runsPanel:listContainer:items:2:itemProperties:2:component", ROLLED_BACK);
        tester.clickLink("form:runsPanel:listContainer:items:2:itemProperties:0:component:link");
        tester.assertRenderedPage(BatchRunPage.class);
        tester.assertModelValue("runPanel:listContainer:items:1:itemProperties:3:component", ROLLED_BACK);
        tester.assertModelValue("runPanel:listContainer:items:2:itemProperties:3:component", ROLLED_BACK);
        tester.clickLink("close");
        tester.assertRenderedPage(BatchRunsPage.class);
        tester.clickLink("close");
        tester.assertRenderedPage(BatchesPage.class);
        tester.clickLink("batchesPanel:refresh");
        tester.assertModelValue("batchesPanel:form:batchesPanel:listContainer:items:3:itemProperties:7:component", ROLLED_BACK);
    }

    @Test
    public void testEmptyBatchRun() throws InterruptedException {
        BatchRun br1 = fac.createBatchRun();
        Run run = fac.createRun();
        run.setStart(new Date());
        run.setStatus(RUNNING);
        run.setBatchRun(br1);
        br1.getRuns().add(run);
        br1.setBatch(batch);
        br1 = dao.save(br1);
        BatchRun br2 = fac.createBatchRun();
        br2.setBatch(batch);
        dao.save(br2);
        tester.startPage(new BatchesPage());
        tester.assertRenderedPage(BatchesPage.class);
        tester.assertModelValue("batchesPanel:form:batchesPanel:listContainer:items:1:itemProperties:7:component", RUNNING);
        tester.clickLink("batchesPanel:form:batchesPanel:listContainer:items:1:itemProperties:7:component:link");
        tester.assertRenderedPage(BatchRunsPage.class);
        tester.assertModelValue("form:runsPanel:listContainer:items:1:itemProperties:2:component", RUNNING);
    }
}

