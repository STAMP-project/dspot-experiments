/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.presentation.pipelinehistory;


import GoConstants.APPROVAL_MANUAL;
import GoConstants.APPROVAL_SUCCESS;
import JobResult.Passed;
import JobResult.Unknown;
import JobState.Assigned;
import JobState.Building;
import JobState.Completed;
import java.util.Date;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class StageInstanceModelTest {
    @Test
    public void shouldUnderstandPreviousStageState() {
        StageInstanceModel item = new StageInstanceModel("foo", "10", JobHistory.withJob("unit", Assigned, Unknown, new Date()));
        StageInstanceModel previous = new StageInstanceModel("foo", "1", JobHistory.withJob("unit", Completed, Passed, new Date()));
        Assert.assertThat(item.hasPreviousStage(), Matchers.is(false));
        item.setPreviousStage(previous);
        Assert.assertThat(item.hasPreviousStage(), Matchers.is(true));
        Assert.assertThat(item.getPreviousStage(), Matchers.is(previous));
    }

    @Test
    public void shouldBeAutoApproved() throws Exception {
        StageInstanceModel stageHistoryItem = new StageInstanceModel();
        stageHistoryItem.setApprovalType(APPROVAL_SUCCESS);
        Assert.assertThat(stageHistoryItem.isAutoApproved(), Matchers.is(true));
    }

    @Test
    public void shouldBeManualApproved() throws Exception {
        StageInstanceModel stageHistoryItem = new StageInstanceModel();
        stageHistoryItem.setApprovalType(APPROVAL_MANUAL);
        Assert.assertThat(stageHistoryItem.isAutoApproved(), Matchers.is(false));
    }

    @Test
    public void shouldReturnNullIfJobHistoryIsBlank() throws Exception {
        StageInstanceModel stageHistoryItem = new StageInstanceModel();
        stageHistoryItem.setBuildHistory(new JobHistory());
        Assert.assertThat(stageHistoryItem.getScheduledDate(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldReturnDateIfJobHistoryIsNotBlank() throws Exception {
        StageInstanceModel stageHistoryItem = new StageInstanceModel();
        JobHistory jobHistory = new JobHistory();
        Date date = new Date(1367472329111L);
        jobHistory.addJob("jobName", Building, Passed, date);
        stageHistoryItem.setBuildHistory(jobHistory);
        Assert.assertThat(stageHistoryItem.getScheduledDate(), Matchers.is(date));
    }
}

