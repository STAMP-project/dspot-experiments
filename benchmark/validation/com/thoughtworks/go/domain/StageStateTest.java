/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.domain;


import StageState.Building;
import StageState.Cancelled;
import StageState.Failed;
import StageState.Failing;
import StageState.Passed;
import StageState.Unknown;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Test;


public class StageStateTest {
    private Set<StageState> testedStates;

    private Set<StageResult> testedResults;

    @Test
    public void shouldNotReturnActiveStatusIfCancelled() {
        isActiveShouldBe(false, Cancelled, Passed, Unknown, Failed);
        isActiveShouldBe(true, Building, Failing);
    }

    @Test
    public void shouldMapStageStatesToStageResults() {
        assertStageResultIs(StageResult.Cancelled, Cancelled);
        assertStageResultIs(StageResult.Unknown, Building, Unknown);
        assertStageResultIs(StageResult.Passed, Passed);
        assertStageResultIs(StageResult.Failed, Failing, Failed);
        shouldCoverAllResults();
    }

    @Test
    public void shouldConvertToCctrayStatus() throws Exception {
        assertCCTrayStatus(Passed, Matchers.is("Success"));
        assertCCTrayStatus(Failed, Matchers.is("Failure"));
        assertCCTrayStatus(Building, Matchers.is("Success"));
        assertCCTrayStatus(Unknown, Matchers.is("Success"));
        assertCCTrayStatus(Cancelled, Matchers.is("Failure"));
        assertCCTrayStatus(Failing, Matchers.is("Failure"));
    }

    @Test
    public void shouldConvertToCctrayActivity() throws Exception {
        assertCCTrayActivity(Passed, Matchers.is("Sleeping"));
        assertCCTrayActivity(Failed, Matchers.is("Sleeping"));
        assertCCTrayActivity(Building, Matchers.is("Building"));
        assertCCTrayActivity(Unknown, Matchers.is("Sleeping"));
        assertCCTrayActivity(Cancelled, Matchers.is("Sleeping"));
        assertCCTrayActivity(Failing, Matchers.is("Building"));
    }

    @Test
    public void shouldReturnStatusAsCompletedForPassesFailedCancelled() {
        assertStatus(Passed, Matchers.is("Completed"));
        assertStatus(Failed, Matchers.is("Completed"));
        assertStatus(Cancelled, Matchers.is("Completed"));
        assertStatus(Building, Matchers.is("Building"));
        assertStatus(Unknown, Matchers.is("Unknown"));
        assertStatus(Failing, Matchers.is("Failing"));
    }
}

