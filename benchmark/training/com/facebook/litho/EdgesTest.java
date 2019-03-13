/**
 * Copyright 2019-present Facebook, Inc.
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


import YogaConstants.UNDEFINED;
import YogaEdge.ALL;
import YogaEdge.BOTTOM;
import YogaEdge.LEFT;
import YogaEdge.RIGHT;
import YogaEdge.TOP;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.yoga.YogaConstants;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ComponentsTestRunner.class)
public class EdgesTest {
    private Edges mEdges;

    @Test
    public void testInsertingOneEdgeMultipleTimes() {
        mEdges.set(TOP, 1);
        mEdges.set(TOP, 2);
        mEdges.set(TOP, 3);
        mEdges.set(TOP, 4);
        mEdges.set(TOP, 5);
        long bits = ~0;
        bits &= ~(((long) (15)) << ((TOP.intValue()) * 4));
        bits |= ((long) (0)) << ((TOP.intValue()) * 4);
        assertThat(getEdgesToValuesIndex()).isEqualTo(bits);
        assertThat(getValuesArray().length).isEqualTo(2);
        assertThat(getValuesArray()[0]).isEqualTo(5);
        assertThat(YogaConstants.isUndefined(getValuesArray()[1])).isTrue();
    }

    @Test
    public void testUnsettingAnEdge() {
        mEdges.set(TOP, 1);
        mEdges.set(TOP, 2);
        mEdges.set(TOP, UNDEFINED);
        long bits = ~0;
        assertThat(getEdgesToValuesIndex()).isEqualTo(bits);
        assertThat(getValuesArray().length).isEqualTo(2);
        assertThat(YogaConstants.isUndefined(getValuesArray()[0])).isTrue();
        assertThat(YogaConstants.isUndefined(getValuesArray()[1])).isTrue();
    }

    @Test
    public void testUnsettingNotTheFirstEdge() {
        mEdges.set(TOP, 1);
        mEdges.set(LEFT, 2);
        mEdges.set(LEFT, UNDEFINED);
        long bits = ~0;
        bits &= ~(((long) (15)) << ((TOP.intValue()) * 4));
        bits |= ((long) (0)) << ((TOP.intValue()) * 4);
        assertThat(getEdgesToValuesIndex()).isEqualTo(bits);
        assertThat(getValuesArray().length).isEqualTo(2);
        assertThat(getValuesArray()[0]).isEqualTo(1);
        assertThat(getValuesArray()[1]).isNaN();
    }

    @Test
    public void testSettingMultipleEdgesIncreasesTheArray() {
        mEdges.set(TOP, 1);
        mEdges.set(LEFT, 2);
        mEdges.set(ALL, 5);
        long bits = ~0;
        bits &= ~(((long) (15)) << ((TOP.intValue()) * 4));
        bits &= ~(((long) (15)) << ((LEFT.intValue()) * 4));
        bits &= ~(((long) (15)) << ((ALL.intValue()) * 4));
        bits |= ((long) (0)) << ((TOP.intValue()) * 4);
        bits |= ((long) (1)) << ((LEFT.intValue()) * 4);
        bits |= ((long) (2)) << ((ALL.intValue()) * 4);
        assertThat(getEdgesToValuesIndex()).isEqualTo(bits);
        assertThat(getValuesArray().length).isEqualTo(4);
        assertThat(getValuesArray()[0]).isEqualTo(1);
        assertThat(getValuesArray()[1]).isEqualTo(2);
        assertThat(getValuesArray()[2]).isEqualTo(5);
        assertThat(getValuesArray()[3]).isNaN();
    }

    @Test
    public void testUnsettingAndSettingNewEdgesReusesArraySpace() {
        mEdges.set(TOP, 1);
        mEdges.set(LEFT, 2);
        mEdges.set(ALL, 5);
        mEdges.set(LEFT, UNDEFINED);
        mEdges.set(BOTTOM, 4);
        long bits = ~0;
        bits &= ~(((long) (15)) << ((TOP.intValue()) * 4));
        bits &= ~(((long) (15)) << ((ALL.intValue()) * 4));
        bits &= ~(((long) (15)) << ((BOTTOM.intValue()) * 4));
        bits |= ((long) (0)) << ((TOP.intValue()) * 4);
        bits |= ((long) (1)) << ((BOTTOM.intValue()) * 4);
        bits |= ((long) (2)) << ((ALL.intValue()) * 4);
        assertThat(getEdgesToValuesIndex()).isEqualTo(bits);
        assertThat(getValuesArray().length).isEqualTo(4);
        assertThat(getValuesArray()[0]).isEqualTo(1);
        assertThat(getValuesArray()[1]).isEqualTo(4);
        assertThat(getValuesArray()[2]).isEqualTo(5);
        assertThat(getValuesArray()[3]).isNaN();
    }

    @Test
    public void testAliasesAndResolveGetter() {
        mEdges.set(ALL, 10);
        assertThat(mEdges.getRaw(LEFT)).isNaN();
        assertThat(mEdges.getRaw(TOP)).isNaN();
        assertThat(mEdges.getRaw(RIGHT)).isNaN();
        assertThat(mEdges.getRaw(BOTTOM)).isNaN();
        assertThat(mEdges.getRaw(ALL)).isEqualTo(10);
        assertThat(mEdges.get(LEFT)).isEqualTo(10);
        assertThat(mEdges.get(TOP)).isEqualTo(10);
        assertThat(mEdges.get(RIGHT)).isEqualTo(10);
        assertThat(mEdges.get(BOTTOM)).isEqualTo(10);
        assertThat(mEdges.get(ALL)).isEqualTo(10);
    }
}

