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
import com.facebook.litho.WorkingRangeContainer.RangeTuple;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(ComponentsTestRunner.class)
public class WorkingRangeContainerTest {
    private static final String NAME = "workingRangeName";

    private WorkingRangeContainer mWorkingRangeContainer;

    private WorkingRange mWorkingRange;

    private Component mComponent;

    private Component mComponent2;

    @Test
    public void testRegisterWorkingRange() {
        mWorkingRangeContainer.registerWorkingRange(WorkingRangeContainerTest.NAME, mWorkingRange, mComponent);
        final Map<String, RangeTuple> workingRanges = mWorkingRangeContainer.getWorkingRangesForTestOnly();
        assertThat(workingRanges.size()).isEqualTo(1);
        final String key = workingRanges.keySet().iterator().next();
        assertThat(key).isEqualTo((((WorkingRangeContainerTest.NAME) + "_") + (mWorkingRange.hashCode())));
        final RangeTuple rangeTuple = workingRanges.get(key);
        assertThat(rangeTuple.mWorkingRange).isEqualTo(mWorkingRange);
        assertThat(rangeTuple.mComponents.size()).isEqualTo(1);
        assertThat(rangeTuple.mComponents.get(0)).isEqualTo(mComponent);
    }

    @Test
    public void testIsEnteredRange() {
        RangeTuple rangeTuple = new RangeTuple(WorkingRangeContainerTest.NAME, mWorkingRange, mComponent);
        WorkingRange workingRange = rangeTuple.mWorkingRange;
        assertThat(WorkingRangeContainer.isEnteringRange(workingRange, 0, 0, 1, 0, 1)).isEqualTo(true);
        assertThat(WorkingRangeContainer.isEnteringRange(workingRange, 0, 1, 2, 1, 2)).isEqualTo(false);
    }

    @Test
    public void testIsExitedRange() {
        RangeTuple rangeTuple = new RangeTuple(WorkingRangeContainerTest.NAME, mWorkingRange, mComponent);
        WorkingRange workingRange = rangeTuple.mWorkingRange;
        assertThat(WorkingRangeContainer.isExitingRange(workingRange, 0, 0, 1, 0, 1)).isEqualTo(false);
        assertThat(WorkingRangeContainer.isExitingRange(workingRange, 0, 1, 2, 1, 2)).isEqualTo(true);
    }

    @Test
    public void testDispatchOnExitedRangeIfNeeded() {
        WorkingRangeContainerTest.TestWorkingRange workingRange = new WorkingRangeContainerTest.TestWorkingRange();
        mWorkingRangeContainer.registerWorkingRange(WorkingRangeContainerTest.NAME, workingRange, mComponent);
        WorkingRangeContainerTest.TestWorkingRange workingRange2 = new WorkingRangeContainerTest.TestWorkingRange();
        mWorkingRangeContainer.registerWorkingRange(WorkingRangeContainerTest.NAME, workingRange2, mComponent2);
        final WorkingRangeStatusHandler statusHandler = new WorkingRangeStatusHandler();
        statusHandler.setStatus(WorkingRangeContainerTest.NAME, mComponent, STATUS_IN_RANGE);
        Mockito.doNothing().when(mComponent).dispatchOnExitedRange(ArgumentMatchers.isA(String.class));
        statusHandler.setStatus(WorkingRangeContainerTest.NAME, mComponent2, STATUS_OUT_OF_RANGE);
        Mockito.doNothing().when(mComponent2).dispatchOnExitedRange(ArgumentMatchers.isA(String.class));
        mWorkingRangeContainer.dispatchOnExitedRangeIfNeeded(statusHandler);
        Mockito.verify(mComponent, Mockito.times(1)).dispatchOnExitedRange(WorkingRangeContainerTest.NAME);
        Mockito.verify(mComponent2, Mockito.times(0)).dispatchOnExitedRange(WorkingRangeContainerTest.NAME);
    }

    private static class TestWorkingRange implements WorkingRange {
        boolean isExitRangeCalled = false;

        @Override
        public boolean shouldEnterRange(int position, int firstVisibleIndex, int lastVisibleIndex, int firstFullyVisibleIndex, int lastFullyVisibleIndex) {
            return WorkingRangeContainerTest.TestWorkingRange.isInRange(position, firstVisibleIndex, lastVisibleIndex);
        }

        @Override
        public boolean shouldExitRange(int position, int firstVisibleIndex, int lastVisibleIndex, int firstFullyVisibleIndex, int lastFullyVisibleIndex) {
            isExitRangeCalled = true;
            return !(WorkingRangeContainerTest.TestWorkingRange.isInRange(position, firstVisibleIndex, lastVisibleIndex));
        }

        private static boolean isInRange(int position, int firstVisibleIndex, int lastVisibleIndex) {
            return (position >= firstVisibleIndex) && (position <= lastVisibleIndex);
        }
    }
}

