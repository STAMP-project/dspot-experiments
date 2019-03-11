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


import OutputUnitType.BACKGROUND;
import OutputUnitType.BORDER;
import OutputUnitType.CONTENT;
import OutputUnitType.FOREGROUND;
import OutputUnitType.HOST;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static OutputUnitType.CONTENT;


public class OutputUnitsAffinityGroupTest {
    @Rule
    public ExpectedException mExpectedException = ExpectedException.none();

    @Test
    public void testEmpty() {
        final OutputUnitsAffinityGroup<Object> group = new OutputUnitsAffinityGroup();
        assertThat(group.size()).isZero();
        assertThat(group.get(CONTENT)).isNull();
        assertThat(group.get(BACKGROUND)).isNull();
        assertThat(group.get(FOREGROUND)).isNull();
        assertThat(group.get(BORDER)).isNull();
        assertThat(group.get(HOST)).isNull();
    }

    @Test
    public void testAddingValid() {
        final OutputUnitsAffinityGroup<Object> group = new OutputUnitsAffinityGroup();
        final Object content = new Object();
        group.add(CONTENT, content);
        assertThat(group.size()).isEqualTo(1);
        assertThat(group.get(CONTENT)).isSameAs(content);
        assertThat(group.typeAt(0)).isEqualTo(CONTENT);
        assertThat(group.getAt(0)).isSameAs(content);
        final Object background = new Object();
        group.add(BACKGROUND, background);
        assertThat(group.size()).isEqualTo(2);
        assertThat(group.get(CONTENT)).isSameAs(content);
        assertThat(group.get(BACKGROUND)).isSameAs(background);
        final int type0 = group.typeAt(0);
        final int type1 = group.typeAt(1);
        assertThat(type0).isIn(CONTENT, BACKGROUND);
        assertThat(type1).isIn(CONTENT, BACKGROUND);
        assertThat(type0).isNotEqualTo(type1);
        final Object value0 = group.getAt(0);
        final Object value1 = group.getAt(1);
        if (type0 == (CONTENT)) {
            assertThat(value0).isSameAs(content);
            assertThat(value1).isSameAs(background);
        } else {
            assertThat(value0).isSameAs(background);
            assertThat(value1).isSameAs(content);
        }
        final Object border = new Object();
        group.add(BORDER, border);
        assertThat(group.size()).isEqualTo(3);
        assertThat(group.get(CONTENT)).isSameAs(content);
        assertThat(group.get(BACKGROUND)).isSameAs(background);
        assertThat(group.get(BORDER)).isSameAs(border);
    }

    @Test
    public void testAddingMultipleForSameType() {
        mExpectedException.expect(RuntimeException.class);
        mExpectedException.expectMessage("Already contains unit for type CONTENT");
        final OutputUnitsAffinityGroup<Object> group = new OutputUnitsAffinityGroup();
        group.add(CONTENT, new Object());
        group.add(CONTENT, new Object());
    }

    @Test
    public void testAddingNull() {
        mExpectedException.expect(IllegalArgumentException.class);
        mExpectedException.expectMessage("value should not be null");
        final OutputUnitsAffinityGroup<Object> group = new OutputUnitsAffinityGroup();
        group.add(FOREGROUND, null);
    }

    @Test
    public void testAddingHostToNotEmptyGroup() {
        mExpectedException.expect(RuntimeException.class);
        mExpectedException.expectMessage("OutputUnitType.HOST unit should be the only member of an OutputUnitsAffinityGroup");
        final OutputUnitsAffinityGroup<Object> group = new OutputUnitsAffinityGroup();
        group.add(CONTENT, new Object());
        group.add(HOST, new Object());
    }

    @Test
    public void testAddingToGroupThatContainsHost() {
        mExpectedException.expect(RuntimeException.class);
        mExpectedException.expectMessage("OutputUnitType.HOST unit should be the only member of an OutputUnitsAffinityGroup");
        final OutputUnitsAffinityGroup<Object> group = new OutputUnitsAffinityGroup();
        group.add(HOST, new Object());
        group.add(BACKGROUND, new Object());
    }

    @Test
    public void testIllegalRange() {
        mExpectedException.expect(IndexOutOfBoundsException.class);
        mExpectedException.expectMessage("index=2, size=2");
        final OutputUnitsAffinityGroup<Object> group = new OutputUnitsAffinityGroup();
        group.add(BACKGROUND, new Object());
        group.add(FOREGROUND, new Object());
        group.getAt(2);
    }
}

