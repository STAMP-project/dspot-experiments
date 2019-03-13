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


import android.graphics.Rect;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ComponentsTestRunner.class)
public class VisibilityOutputTest {
    private static final int LIFECYCLE_TEST_ID = 1;

    private static final int LEVEL_TEST = 1;

    private static final int SEQ_TEST = 1;

    private static final int MAX_LEVEL_TEST = 255;

    private static final int MAX_SEQ_TEST = 65535;

    private Component mComponent;

    private VisibilityOutput mVisibilityOutput;

    @Test
    public void testPositionAndSizeSet() {
        mVisibilityOutput.setBounds(0, 1, 3, 4);
        assertThat(mVisibilityOutput.getBounds().left).isEqualTo(0);
        assertThat(mVisibilityOutput.getBounds().top).isEqualTo(1);
        assertThat(mVisibilityOutput.getBounds().right).isEqualTo(3);
        assertThat(mVisibilityOutput.getBounds().bottom).isEqualTo(4);
    }

    @Test
    public void testRectBoundsSet() {
        Rect bounds = new Rect(0, 1, 3, 4);
        mVisibilityOutput.setBounds(bounds);
        assertThat(mVisibilityOutput.getBounds().left).isEqualTo(0);
        assertThat(mVisibilityOutput.getBounds().top).isEqualTo(1);
        assertThat(mVisibilityOutput.getBounds().right).isEqualTo(3);
        assertThat(mVisibilityOutput.getBounds().bottom).isEqualTo(4);
    }

    @Test
    public void testHandlersSet() {
        EventHandler visibleHandler = new EventHandler(null, 1);
        EventHandler invisibleHandler = new EventHandler(null, 2);
        EventHandler focusedHandler = new EventHandler(null, 3);
        EventHandler unfocusedHandler = new EventHandler(null, 4);
        EventHandler fullImpressionHandler = new EventHandler(null, 5);
        mVisibilityOutput.setVisibleEventHandler(visibleHandler);
        mVisibilityOutput.setInvisibleEventHandler(invisibleHandler);
        mVisibilityOutput.setFocusedEventHandler(focusedHandler);
        mVisibilityOutput.setUnfocusedEventHandler(unfocusedHandler);
        mVisibilityOutput.setFullImpressionEventHandler(fullImpressionHandler);
        assertThat(visibleHandler).isSameAs(mVisibilityOutput.getVisibleEventHandler());
        assertThat(invisibleHandler).isSameAs(mVisibilityOutput.getInvisibleEventHandler());
        assertThat(focusedHandler).isSameAs(mVisibilityOutput.getFocusedEventHandler());
        assertThat(unfocusedHandler).isSameAs(mVisibilityOutput.getUnfocusedEventHandler());
        assertThat(fullImpressionHandler).isSameAs(mVisibilityOutput.getFullImpressionEventHandler());
    }

    @Test
    public void testStableIdCalculation() {
        mVisibilityOutput.setComponent(mComponent);
        long stableId = LayoutStateOutputIdCalculator.calculateVisibilityOutputId(mVisibilityOutput, VisibilityOutputTest.LEVEL_TEST, VisibilityOutputTest.SEQ_TEST);
        long stableIdSeq2 = LayoutStateOutputIdCalculator.calculateVisibilityOutputId(mVisibilityOutput, ((VisibilityOutputTest.LEVEL_TEST) + 1), ((VisibilityOutputTest.SEQ_TEST) + 1));
        assertThat(Long.toBinaryString(stableId)).isEqualTo("100000001000000000000000001");
        assertThat(Long.toBinaryString(stableIdSeq2)).isEqualTo("100000010000000000000000010");
    }

    @Test
    public void testGetIdLevel() {
        mVisibilityOutput.setComponent(mComponent);
        mVisibilityOutput.setId(LayoutStateOutputIdCalculator.calculateVisibilityOutputId(mVisibilityOutput, VisibilityOutputTest.LEVEL_TEST, VisibilityOutputTest.SEQ_TEST));
        assertThat(VisibilityOutputTest.LEVEL_TEST).isEqualTo(LayoutStateOutputIdCalculator.getLevelFromId(mVisibilityOutput.getId()));
        mVisibilityOutput.setId(LayoutStateOutputIdCalculator.calculateVisibilityOutputId(mVisibilityOutput, VisibilityOutputTest.MAX_LEVEL_TEST, VisibilityOutputTest.SEQ_TEST));
        assertThat(VisibilityOutputTest.MAX_LEVEL_TEST).isEqualTo(LayoutStateOutputIdCalculator.getLevelFromId(mVisibilityOutput.getId()));
    }

    @Test
    public void testGetIdSequence() {
        mVisibilityOutput.setComponent(mComponent);
        mVisibilityOutput.setId(LayoutStateOutputIdCalculator.calculateVisibilityOutputId(mVisibilityOutput, VisibilityOutputTest.LEVEL_TEST, VisibilityOutputTest.SEQ_TEST));
        assertThat(VisibilityOutputTest.SEQ_TEST).isEqualTo(LayoutStateOutputIdCalculator.getSequenceFromId(mVisibilityOutput.getId()));
        mVisibilityOutput.setId(LayoutStateOutputIdCalculator.calculateVisibilityOutputId(mVisibilityOutput, VisibilityOutputTest.LEVEL_TEST, VisibilityOutputTest.MAX_SEQ_TEST));
        assertThat(VisibilityOutputTest.MAX_SEQ_TEST).isEqualTo(LayoutStateOutputIdCalculator.getSequenceFromId(mVisibilityOutput.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void levelOutOfRangeTest() {
        mVisibilityOutput.setComponent(mComponent);
        mVisibilityOutput.setId(LayoutStateOutputIdCalculator.calculateVisibilityOutputId(mVisibilityOutput, ((VisibilityOutputTest.MAX_LEVEL_TEST) + 1), VisibilityOutputTest.SEQ_TEST));
    }

    @Test(expected = IllegalArgumentException.class)
    public void sequenceOutOfRangeTest() {
        mVisibilityOutput.setComponent(mComponent);
        mVisibilityOutput.setId(LayoutStateOutputIdCalculator.calculateVisibilityOutputId(mVisibilityOutput, VisibilityOutputTest.LEVEL_TEST, ((VisibilityOutputTest.MAX_SEQ_TEST) + 1)));
    }
}

