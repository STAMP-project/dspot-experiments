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


import java.util.Date;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class StageHistoryTest {
    private StageInstanceModels stageHistory;

    private static final String STAGE_FT = "ft";

    private static final String STAGE_UT = "ut";

    private static final String STAGE_RELEASE = "release";

    private static final Date EARLIEAR_DATE = new Date(1000000000);

    private static final Date DATE = new Date(2000000000);

    @Test
    public void hasStageTest() throws Exception {
        Assert.assertThat(stageHistory.hasStage(StageHistoryTest.STAGE_FT), Matchers.is(true));
        Assert.assertThat(stageHistory.hasStage("notExisting"), Matchers.is(false));
        Assert.assertThat(stageHistory.hasStage(null), Matchers.is(false));
        Assert.assertThat(stageHistory.hasStage(""), Matchers.is(false));
    }

    @Test
    public void nextStageTest() throws Exception {
        Assert.assertThat(stageHistory.nextStageName(StageHistoryTest.STAGE_UT), Matchers.is(StageHistoryTest.STAGE_FT));
        Assert.assertThat(stageHistory.nextStageName(StageHistoryTest.STAGE_FT), Matchers.is(StageHistoryTest.STAGE_RELEASE));
        Assert.assertThat(stageHistory.nextStageName(StageHistoryTest.STAGE_RELEASE), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(stageHistory.nextStageName("notExisting"), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(stageHistory.nextStageName(""), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(stageHistory.nextStageName(null), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldNotHaveDateForEmptyHistory() {
        Assert.assertThat(new StageInstanceModels().getScheduledDate(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldReturnEarliestDate() {
        StageInstanceModels history = new StageInstanceModels();
        history.add(new StageHistoryTest.StageHistoryItemStub(StageHistoryTest.EARLIEAR_DATE));
        history.add(new StageHistoryTest.StageHistoryItemStub(StageHistoryTest.DATE));
        Assert.assertThat(history.getScheduledDate(), Matchers.is(StageHistoryTest.EARLIEAR_DATE));
    }

    @Test
    public void shouldNotCountNullStageIn() {
        StageInstanceModels history = new StageInstanceModels();
        history.add(new StageHistoryTest.NullStageHistoryItemStub(StageHistoryTest.EARLIEAR_DATE));
        history.add(new StageHistoryTest.StageHistoryItemStub(StageHistoryTest.DATE));
        Assert.assertThat(history.getScheduledDate(), Matchers.is(StageHistoryTest.DATE));
    }

    private class StageHistoryItemStub extends StageInstanceModel {
        private final Date date;

        public StageHistoryItemStub(Date date) {
            this.date = date;
        }

        public Date getScheduledDate() {
            return date;
        }
    }

    private class NullStageHistoryItemStub extends NullStageHistoryItem {
        private final Date date;

        public NullStageHistoryItemStub(Date date) {
            super("");
            this.date = date;
        }

        public Date getScheduledDate() {
            return date;
        }
    }
}

