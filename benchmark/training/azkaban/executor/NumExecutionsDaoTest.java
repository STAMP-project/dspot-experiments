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
package azkaban.executor;


import Status.FAILED;
import Status.PREPARING;
import Status.RUNNING;
import azkaban.db.DatabaseOperator;
import azkaban.utils.TestUtils;
import org.junit.Test;


public class NumExecutionsDaoTest {
    private static DatabaseOperator dbOperator;

    private NumExecutionsDao numExecutionsDao;

    private ExecutionFlowDao executionFlowDao;

    @Test
    public void testFetchNumExecutableFlows() throws Exception {
        final ExecutableFlow flow1 = TestUtils.createTestExecutableFlow("exectest1", "exec1");
        flow1.setStatus(PREPARING);
        this.executionFlowDao.uploadExecutableFlow(flow1);
        final ExecutableFlow flow2 = TestUtils.createTestExecutableFlow("exectest1", "exec2");
        flow2.setStatus(RUNNING);
        this.executionFlowDao.uploadExecutableFlow(flow2);
        final ExecutableFlow flow2b = TestUtils.createTestExecutableFlow("exectest1", "exec2");
        flow2b.setStatus(FAILED);
        this.executionFlowDao.uploadExecutableFlow(flow2b);
        final int count = this.numExecutionsDao.fetchNumExecutableFlows();
        assertThat(count).isEqualTo(3);
        final int flow2Count = this.numExecutionsDao.fetchNumExecutableFlows(1, "derived-member-data-2");
        assertThat(flow2Count).isEqualTo(2);
    }

    @Test
    public void testFetchNumExecutableNodes() throws Exception {
        // This test will be filled up after execution_jobs test completes.
    }
}

