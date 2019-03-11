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
package azkaban.flowtrigger;


import CancellationCause.MANUAL;
import Status.CANCELLED;
import azkaban.db.DatabaseOperator;
import azkaban.flowtrigger.database.FlowTriggerInstanceLoader;
import azkaban.project.FlowTrigger;
import azkaban.project.Project;
import azkaban.project.ProjectLoader;
import azkaban.project.ProjectManager;
import azkaban.utils.Props;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FlowTriggerInstanceLoaderTest {
    private static final Logger logger = LoggerFactory.getLogger(FlowTriggerInstanceLoaderTest.class);

    private static final String test_project_zip_dir = "flowtriggeryamltest";

    private static final String test_flow_file = "flow_trigger.flow";

    private static final int project_id = 123;

    private static final String project_name = "test";

    private static final int project_version = 3;

    private static final String flow_id = "flow_trigger";

    private static final int flow_version = 1;

    private static final Props props = new Props();

    private static final String submitUser = "uploadUser1";

    private static DatabaseOperator dbOperator;

    private static ProjectLoader projLoader;

    private static FlowTrigger flowTrigger;

    private static FlowTriggerInstanceLoader triggerInstLoader;

    private static Project project;

    private static ProjectManager projManager;

    @Test
    public void testUploadTriggerInstance() {
        final TriggerInstance expectedTriggerInst = this.createTriggerInstance(this.flowTrigger, this.flow_id, this.flow_version, this.submitUser, this.project, System.currentTimeMillis());
        this.triggerInstLoader.uploadTriggerInstance(expectedTriggerInst);
        final TriggerInstance actualTriggerInst = this.triggerInstLoader.getTriggerInstanceById(expectedTriggerInst.getId());
        assertThat(expectedTriggerInst.getFlowTrigger().toString()).isEqualToIgnoringWhitespace(actualTriggerInst.getFlowTrigger().toString());
        assertThat(expectedTriggerInst).isEqualToIgnoringGivenFields(actualTriggerInst, "depInstances", "flowTrigger");
        assertThat(expectedTriggerInst.getDepInstances()).usingElementComparatorIgnoringFields("triggerInstance", "context").containsAll(actualTriggerInst.getDepInstances()).hasSameSizeAs(actualTriggerInst.getDepInstances());
    }

    @Test
    public void testUpdateDependencyExecutionStatus() {
        final TriggerInstance expectedTriggerInst = this.createTriggerInstance(this.flowTrigger, this.flow_id, this.flow_version, this.submitUser, this.project, System.currentTimeMillis());
        this.triggerInstLoader.uploadTriggerInstance(expectedTriggerInst);
        for (final DependencyInstance depInst : expectedTriggerInst.getDepInstances()) {
            depInst.setStatus(CANCELLED);
            depInst.setEndTime(System.currentTimeMillis());
            depInst.setCancellationCause(MANUAL);
            this.triggerInstLoader.updateDependencyExecutionStatus(depInst);
        }
        final TriggerInstance actualTriggerInst = this.triggerInstLoader.getTriggerInstanceById(expectedTriggerInst.getId());
        assertTriggerInstancesEqual(actualTriggerInst, expectedTriggerInst, false);
    }

    @Test
    public void testGetIncompleteTriggerInstancesReturnsEmpty() {
        final List<TriggerInstance> all = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            all.add(this.createTriggerInstance(this.flowTrigger, this.flow_id, this.flow_version, this.submitUser, this.project, System.currentTimeMillis()));
            if (i <= 2) {
                finalizeTriggerInstanceWithCancelled(all.get(i));
            } else {
                finalizeTriggerInstanceWithSuccess(all.get(i), 1000);
            }
        }
        this.shuffleAndUpload(all);
        final List<TriggerInstance> actual = new ArrayList(this.triggerInstLoader.getIncompleteTriggerInstances());
        all.sort(Comparator.comparing(TriggerInstance::getId));
        actual.sort(Comparator.comparing(TriggerInstance::getId));
        assertThat(actual).isEmpty();
    }

    @Test
    public void testGetIncompleteTriggerInstances() {
        final List<TriggerInstance> allInstances = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            allInstances.add(this.createTriggerInstance(this.flowTrigger, this.flow_id, this.flow_version, this.submitUser, this.project, System.currentTimeMillis()));
        }
        finalizeTriggerInstanceWithCancelled(allInstances.get(0));
        finalizeTriggerInstanceWithSuccess(allInstances.get(1), 1000);
        // this trigger instance should still count as incomplete one since no flow execution has
        // been started
        finalizeTriggerInstanceWithSuccess(allInstances.get(2), (-1));
        this.shuffleAndUpload(allInstances);
        final List<TriggerInstance> expected = allInstances.subList(2, allInstances.size());
        final List<TriggerInstance> actual = new ArrayList(this.triggerInstLoader.getIncompleteTriggerInstances());
        assertTwoTriggerInstanceListsEqual(actual, expected, false, false);
    }

    @Test
    public void testGetRunningTriggerInstancesReturnsEmpty() throws InterruptedException {
        final List<TriggerInstance> all = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            all.add(this.createTriggerInstance(this.flowTrigger, this.flow_id, this.flow_version, this.submitUser, this.project, ((System.currentTimeMillis()) + (i * 10000))));
            finalizeTriggerInstanceWithSuccess(all.get(i), 1000);
        }
        this.shuffleAndUpload(all);
        final Collection<TriggerInstance> running = this.triggerInstLoader.getRunning();
        assertThat(running).isEmpty();
    }

    @Test
    public void testGetRunningTriggerInstances() throws InterruptedException {
        final List<TriggerInstance> all = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            all.add(this.createTriggerInstance(this.flowTrigger, this.flow_id, this.flow_version, this.submitUser, this.project, ((System.currentTimeMillis()) + (i * 10000))));
            if (i <= 3) {
                finalizeTriggerInstanceWithCancelled(all.get(i));
            } else
                if (i <= 6) {
                    finalizeTriggerInstanceWithSuccess(all.get(i), 1000);
                } else
                    if (i <= 9) {
                        finalizeTriggerInstanceWithCancelling(all.get(i));
                    }


            // sleep for a while to ensure endtime is different for each trigger instance
            Thread.sleep(100);
        }
        this.shuffleAndUpload(all);
        final List<TriggerInstance> finished = all.subList(7, all.size());
        final List<TriggerInstance> expected = new ArrayList(finished);
        expected.sort(Comparator.comparing(TriggerInstance::getStartTime));
        final Collection<TriggerInstance> running = this.triggerInstLoader.getRunning();
        assertTwoTriggerInstanceListsEqual(new ArrayList(running), expected, true, true);
    }

    @Test
    public void testUpdateAssociatedFlowExecId() {
        final TriggerInstance expectedTriggerInst = this.createTriggerInstance(this.flowTrigger, this.flow_id, this.flow_version, this.submitUser, this.project, System.currentTimeMillis());
        this.triggerInstLoader.uploadTriggerInstance(expectedTriggerInst);
        finalizeTriggerInstanceWithSuccess(expectedTriggerInst, 1000);
        expectedTriggerInst.getDepInstances().forEach(( depInst) -> this.triggerInstLoader.updateDependencyExecutionStatus(depInst));
        this.triggerInstLoader.updateAssociatedFlowExecId(expectedTriggerInst);
        final TriggerInstance actualTriggerInst = this.triggerInstLoader.getTriggerInstanceById(expectedTriggerInst.getId());
        assertTriggerInstancesEqual(actualTriggerInst, expectedTriggerInst, false);
    }

    @Test
    public void testGetRecentlyFinishedReturnsEmpty() {
        final List<TriggerInstance> all = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            all.add(this.createTriggerInstance(this.flowTrigger, this.flow_id, this.flow_version, this.submitUser, this.project, System.currentTimeMillis()));
        }
        this.shuffleAndUpload(all);
        final Collection<TriggerInstance> recentlyFinished = this.triggerInstLoader.getRecentlyFinished(10);
        assertThat(recentlyFinished).isEmpty();
    }

    @Test
    public void testGetTriggerInstancesStartTimeDesc() {
        final List<TriggerInstance> expected = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            expected.add(this.createTriggerInstance(this.flowTrigger, this.flow_id, this.flow_version, this.submitUser, this.project, ((System.currentTimeMillis()) + (i * 1000))));
        }
        this.shuffleAndUpload(expected);
        final Collection<TriggerInstance> actual = this.triggerInstLoader.getTriggerInstances(this.project_id, this.flow_id, 0, 10);
        expected.sort(( o1, o2) -> ((Long) (o2.getStartTime())).compareTo(o1.getStartTime()));
        assertTwoTriggerInstanceListsEqual(new ArrayList(actual), new ArrayList(expected), true, true);
    }

    @Test
    public void testGetEmptyTriggerInstancesStartTimeDesc() {
        final Collection<TriggerInstance> actual = this.triggerInstLoader.getTriggerInstances(this.project_id, this.flow_id, 0, 10);
        assertThat(actual).isEmpty();
    }

    @Test
    public void testDeleteOldTriggerInstances() throws InterruptedException {
        final List<TriggerInstance> all = new ArrayList<>();
        final long ts1 = System.currentTimeMillis();
        long ts2 = -1;
        long ts3 = -1;
        for (int i = 0; i < 30; i++) {
            all.add(this.createTriggerInstance(this.flowTrigger, this.flow_id, this.flow_version, this.submitUser, this.project, ((System.currentTimeMillis()) + (i * 10000))));
            if (i < 5) {
                finalizeTriggerInstanceWithSuccess(all.get(i), (-1));
            } else
                if (i <= 15) {
                    finalizeTriggerInstanceWithCancelled(all.get(i));
                } else
                    if (i <= 25) {
                        finalizeTriggerInstanceWithCancelling(all.get(i));
                    } else
                        if (i <= 27) {
                            finalizeTriggerInstanceWithSuccess(all.get(i), 1000);
                        }



            // sleep for a while to ensure end time is different for each trigger instance
            if (i == 3) {
                ts2 = System.currentTimeMillis();
            } else
                if (i == 12) {
                    ts3 = System.currentTimeMillis();
                }

            Thread.sleep(100);
        }
        this.shuffleAndUpload(all);
        assertThat(this.triggerInstLoader.deleteTriggerExecutionsFinishingOlderThan(ts1)).isEqualTo(0);
        assertThat(this.triggerInstLoader.deleteTriggerExecutionsFinishingOlderThan(ts2)).isEqualTo(0);
        assertThat(this.triggerInstLoader.deleteTriggerExecutionsFinishingOlderThan(ts3)).isEqualTo(16);
    }

    @Test
    public void testGetRecentlyFinished() throws InterruptedException {
        final List<TriggerInstance> all = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            all.add(this.createTriggerInstance(this.flowTrigger, this.flow_id, this.flow_version, this.submitUser, this.project, ((System.currentTimeMillis()) + (i * 10000))));
            if (i <= 3) {
                finalizeTriggerInstanceWithCancelled(all.get(i));
            } else
                if (i <= 6) {
                    finalizeTriggerInstanceWithSuccess(all.get(i), 1000);
                } else
                    if (i <= 9) {
                        finalizeTriggerInstanceWithCancelling(all.get(i));
                    }


            // sleep for a while to ensure endtime is different for each trigger instance
            Thread.sleep(100);
        }
        this.shuffleAndUpload(all);
        final List<TriggerInstance> finished = all.subList(0, 7);
        finished.sort(( o1, o2) -> ((Long) (o2.getEndTime())).compareTo(o1.getEndTime()));
        List<TriggerInstance> expected = new ArrayList(finished);
        expected.sort(Comparator.comparing(TriggerInstance::getStartTime));
        Collection<TriggerInstance> recentlyFinished = this.triggerInstLoader.getRecentlyFinished(10);
        assertTwoTriggerInstanceListsEqual(new ArrayList(recentlyFinished), expected, true, true);
        expected = new ArrayList(finished.subList(0, 3));
        expected.sort(Comparator.comparing(TriggerInstance::getStartTime));
        recentlyFinished = this.triggerInstLoader.getRecentlyFinished(3);
        assertTwoTriggerInstanceListsEqual(new ArrayList(recentlyFinished), expected, true, true);
        expected = new ArrayList(finished.subList(0, 1));
        recentlyFinished = this.triggerInstLoader.getRecentlyFinished(1);
        assertTwoTriggerInstanceListsEqual(new ArrayList(recentlyFinished), expected, true, true);
    }
}

