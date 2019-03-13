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
import CancellationCause.NONE;
import Status.CANCELLED;
import Status.CANCELLING;
import Status.RUNNING;
import Status.SUCCEEDED;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Test;


public class TriggerInstanceTest {
    @Test
    public void testTriggerInstanceStartTime() throws Exception {
        final List<DependencyInstance> dependencyInstanceList = new ArrayList<>();
        Date expectedStartTime = TriggerInstanceTest.getDate(2, 2, 2);
        dependencyInstanceList.add(createTestDependencyInstance(SUCCEEDED, NONE, TriggerInstanceTest.getDate(2, 2, 2), TriggerInstanceTest.getDate(2, 2, 3)));
        TriggerInstance ti = null;
        ti = new TriggerInstance("1", null, "1", 1, "test", dependencyInstanceList, (-1), null);
        assertThat(ti.getStartTime()).isEqualTo(expectedStartTime.getTime());
        dependencyInstanceList.clear();
        ti = new TriggerInstance("1", null, "1", 1, "test", dependencyInstanceList, (-1), null);
        assertThat(ti.getStartTime()).isEqualTo(0);
        dependencyInstanceList.clear();
        expectedStartTime = TriggerInstanceTest.getDate(2, 2, 2);
        dependencyInstanceList.add(createTestDependencyInstance(SUCCEEDED, NONE, TriggerInstanceTest.getDate(2, 2, 4), TriggerInstanceTest.getDate(2, 2, 3)));
        dependencyInstanceList.add(createTestDependencyInstance(SUCCEEDED, NONE, TriggerInstanceTest.getDate(2, 2, 3), TriggerInstanceTest.getDate(2, 2, 3)));
        dependencyInstanceList.add(createTestDependencyInstance(SUCCEEDED, NONE, TriggerInstanceTest.getDate(2, 2, 2), TriggerInstanceTest.getDate(2, 2, 3)));
        ti = new TriggerInstance("1", null, "1", 1, "test", dependencyInstanceList, (-1), null);
        assertThat(ti.getStartTime()).isEqualTo(expectedStartTime.getTime());
    }

    @Test
    public void testTriggerInstanceEndTime() throws Exception {
        final List<DependencyInstance> dependencyInstanceList = new ArrayList<>();
        Date expectedEndTime = TriggerInstanceTest.getDate(3, 2, 3);
        dependencyInstanceList.add(createTestDependencyInstance(SUCCEEDED, NONE, TriggerInstanceTest.getDate(2, 2, 2), TriggerInstanceTest.getDate(3, 2, 3)));
        dependencyInstanceList.add(createTestDependencyInstance(SUCCEEDED, NONE, TriggerInstanceTest.getDate(2, 2, 2), TriggerInstanceTest.getDate(2, 2, 3)));
        TriggerInstance ti = null;
        ti = new TriggerInstance("1", null, "1", 1, "test", dependencyInstanceList, (-1), null);
        assertThat(ti.getEndTime()).isEqualTo(expectedEndTime.getTime());
        dependencyInstanceList.clear();
        dependencyInstanceList.add(createTestDependencyInstance(RUNNING, NONE, TriggerInstanceTest.getDate(2, 2, 2), null));
        ti = new TriggerInstance("1", null, "1", 1, "test", dependencyInstanceList, (-1), null);
        assertThat(ti.getEndTime()).isEqualTo(0);
        dependencyInstanceList.clear();
        expectedEndTime = TriggerInstanceTest.getDate(3, 2, 3);
        dependencyInstanceList.add(createTestDependencyInstance(SUCCEEDED, NONE, TriggerInstanceTest.getDate(2, 2, 3), null));
        dependencyInstanceList.add(createTestDependencyInstance(SUCCEEDED, NONE, TriggerInstanceTest.getDate(3, 2, 2), null));
        dependencyInstanceList.add(createTestDependencyInstance(SUCCEEDED, NONE, TriggerInstanceTest.getDate(2, 2, 2), expectedEndTime));
        ti = new TriggerInstance("1", null, "1", 1, "test", dependencyInstanceList, (-1), null);
        assertThat(ti.getEndTime()).isEqualTo(expectedEndTime.getTime());
        dependencyInstanceList.clear();
        ti = new TriggerInstance("1", null, "1", 1, "test", dependencyInstanceList, (-1), null);
        assertThat(ti.getEndTime()).isEqualTo(0);
        dependencyInstanceList.clear();
    }

    @Test
    public void testTriggerInstanceRunningStatus() throws Exception {
        final List<DependencyInstance> dependencyInstanceList = new ArrayList<>();
        TriggerInstance ti = null;
        dependencyInstanceList.add(createTestDependencyInstance(RUNNING, MANUAL));
        dependencyInstanceList.add(createTestDependencyInstance(SUCCEEDED, NONE));
        dependencyInstanceList.add(createTestDependencyInstance(SUCCEEDED, NONE));
        ti = new TriggerInstance("1", null, "1", 1, "test", dependencyInstanceList, (-1), null);
        assertThat(ti.getStatus()).isEqualTo(RUNNING);
        dependencyInstanceList.clear();
        dependencyInstanceList.add(createTestDependencyInstance(RUNNING, MANUAL));
        ti = new TriggerInstance("1", null, "1", 1, "test", dependencyInstanceList, (-1), null);
        assertThat(ti.getStatus()).isEqualTo(RUNNING);
        dependencyInstanceList.clear();
    }

    @Test
    public void testTriggerInstanceSucceededStatus() throws Exception {
        final List<DependencyInstance> dependencyInstanceList = new ArrayList<>();
        TriggerInstance ti = null;
        dependencyInstanceList.add(createTestDependencyInstance(SUCCEEDED, NONE));
        dependencyInstanceList.add(createTestDependencyInstance(SUCCEEDED, NONE));
        dependencyInstanceList.add(createTestDependencyInstance(SUCCEEDED, NONE));
        ti = new TriggerInstance("1", null, "1", 1, "test", dependencyInstanceList, (-1), null);
        assertThat(ti.getStatus()).isEqualTo(SUCCEEDED);
        dependencyInstanceList.clear();
        ti = new TriggerInstance("1", null, "1", 1, "test", dependencyInstanceList, (-1), null);
        assertThat(ti.getStatus()).isEqualTo(SUCCEEDED);
        dependencyInstanceList.clear();
    }

    @Test
    public void testTriggerInstanceCancellingStatus() throws Exception {
        final List<DependencyInstance> dependencyInstanceList = new ArrayList<>();
        TriggerInstance ti = null;
        dependencyInstanceList.add(createTestDependencyInstance(CANCELLING, MANUAL));
        dependencyInstanceList.add(createTestDependencyInstance(CANCELLED, MANUAL));
        dependencyInstanceList.add(createTestDependencyInstance(SUCCEEDED, NONE));
        dependencyInstanceList.add(createTestDependencyInstance(SUCCEEDED, NONE));
        dependencyInstanceList.add(createTestDependencyInstance(SUCCEEDED, NONE));
        ti = new TriggerInstance("1", null, "1", 1, "test", dependencyInstanceList, (-1), null);
        assertThat(ti.getStatus()).isEqualTo(CANCELLING);
        dependencyInstanceList.clear();
        dependencyInstanceList.add(createTestDependencyInstance(RUNNING, MANUAL));
        dependencyInstanceList.add(createTestDependencyInstance(CANCELLED, MANUAL));
        dependencyInstanceList.add(createTestDependencyInstance(SUCCEEDED, NONE));
        dependencyInstanceList.add(createTestDependencyInstance(SUCCEEDED, NONE));
        dependencyInstanceList.add(createTestDependencyInstance(SUCCEEDED, NONE));
        ti = new TriggerInstance("1", null, "1", 1, "test", dependencyInstanceList, (-1), null);
        assertThat(ti.getStatus()).isEqualTo(CANCELLING);
        dependencyInstanceList.clear();
        dependencyInstanceList.add(createTestDependencyInstance(CANCELLED, MANUAL));
        dependencyInstanceList.add(createTestDependencyInstance(CANCELLING, NONE));
        dependencyInstanceList.add(createTestDependencyInstance(RUNNING, NONE));
        ti = new TriggerInstance("1", null, "1", 1, "test", dependencyInstanceList, (-1), null);
        assertThat(ti.getStatus()).isEqualTo(CANCELLING);
        dependencyInstanceList.clear();
        dependencyInstanceList.add(createTestDependencyInstance(RUNNING, MANUAL));
        dependencyInstanceList.add(createTestDependencyInstance(CANCELLING, MANUAL));
        dependencyInstanceList.add(createTestDependencyInstance(SUCCEEDED, NONE));
        ti = new TriggerInstance("1", null, "1", 1, "test", dependencyInstanceList, (-1), null);
        assertThat(ti.getStatus()).isEqualTo(CANCELLING);
        dependencyInstanceList.clear();
        dependencyInstanceList.add(createTestDependencyInstance(CANCELLED, MANUAL));
        dependencyInstanceList.add(createTestDependencyInstance(RUNNING, NONE));
        ti = new TriggerInstance("1", null, "1", 1, "test", dependencyInstanceList, (-1), null);
        assertThat(ti.getStatus()).isEqualTo(CANCELLING);
        dependencyInstanceList.clear();
    }

    @Test
    public void testTriggerInstanceCancelledStatus() throws Exception {
        final List<DependencyInstance> dependencyInstanceList = new ArrayList<>();
        TriggerInstance ti = null;
        dependencyInstanceList.add(createTestDependencyInstance(CANCELLED, MANUAL));
        dependencyInstanceList.add(createTestDependencyInstance(CANCELLED, MANUAL));
        dependencyInstanceList.add(createTestDependencyInstance(SUCCEEDED, NONE));
        dependencyInstanceList.add(createTestDependencyInstance(SUCCEEDED, NONE));
        dependencyInstanceList.add(createTestDependencyInstance(SUCCEEDED, NONE));
        ti = new TriggerInstance("1", null, "1", 1, "test", dependencyInstanceList, (-1), null);
        assertThat(ti.getStatus()).isEqualTo(CANCELLED);
        dependencyInstanceList.clear();
        dependencyInstanceList.add(createTestDependencyInstance(CANCELLED, MANUAL));
        dependencyInstanceList.add(createTestDependencyInstance(CANCELLED, MANUAL));
        ti = new TriggerInstance("1", null, "1", 1, "test", dependencyInstanceList, (-1), null);
        assertThat(ti.getStatus()).isEqualTo(CANCELLED);
        dependencyInstanceList.clear();
    }
}

