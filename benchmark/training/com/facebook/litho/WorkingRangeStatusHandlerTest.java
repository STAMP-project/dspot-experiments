/**
 * Copyright 2014-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.litho;


import WorkingRangeStatusHandler.STATUS_IN_RANGE;
import WorkingRangeStatusHandler.STATUS_OUT_OF_RANGE;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ComponentsTestRunner.class)
public class WorkingRangeStatusHandlerTest {
    private static final String NAME = "workingRangeName";

    private static final String GLOBAL_KEY = "globalKey";

    private WorkingRangeStatusHandler mWorkingRangeStateHandler;

    private Component mComponent;

    @Test
    public void testIsNotInRange() {
        mWorkingRangeStateHandler.setStatus(WorkingRangeStatusHandlerTest.NAME, mComponent, STATUS_OUT_OF_RANGE);
        boolean notInRange = !(mWorkingRangeStateHandler.isInRange(WorkingRangeStatusHandlerTest.NAME, mComponent));
        assertThat(notInRange).isEqualTo(true);
    }

    @Test
    public void testIsInRange() {
        mWorkingRangeStateHandler.setStatus(WorkingRangeStatusHandlerTest.NAME, mComponent, STATUS_IN_RANGE);
        boolean inRange = mWorkingRangeStateHandler.isInRange(WorkingRangeStatusHandlerTest.NAME, mComponent);
        assertThat(inRange).isEqualTo(true);
    }

    @Test
    public void testSetEnteredRangeState() {
        mWorkingRangeStateHandler.setStatus(WorkingRangeStatusHandlerTest.NAME, mComponent, STATUS_OUT_OF_RANGE);
        boolean notInRange = !(mWorkingRangeStateHandler.isInRange(WorkingRangeStatusHandlerTest.NAME, mComponent));
        assertThat(notInRange).isEqualTo(true);
        mWorkingRangeStateHandler.setEnteredRangeStatus(WorkingRangeStatusHandlerTest.NAME, mComponent);
        boolean inRange = mWorkingRangeStateHandler.isInRange(WorkingRangeStatusHandlerTest.NAME, mComponent);
        assertThat(inRange).isEqualTo(true);
    }

    @Test
    public void testSetExitedRangeState() {
        mWorkingRangeStateHandler.setStatus(WorkingRangeStatusHandlerTest.NAME, mComponent, STATUS_IN_RANGE);
        boolean inRange = mWorkingRangeStateHandler.isInRange(WorkingRangeStatusHandlerTest.NAME, mComponent);
        assertThat(inRange).isEqualTo(true);
        mWorkingRangeStateHandler.setExitedRangeStatus(WorkingRangeStatusHandlerTest.NAME, mComponent);
        boolean notInRange = !(mWorkingRangeStateHandler.isInRange(WorkingRangeStatusHandlerTest.NAME, mComponent));
        assertThat(notInRange).isEqualTo(true);
    }
}

