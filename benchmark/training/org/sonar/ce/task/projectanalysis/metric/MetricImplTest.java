/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.ce.task.projectanalysis.metric;


import Metric.MetricType;
import Metric.MetricType.FLOAT;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class MetricImplTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final int SOME_ID = 42;

    private static final String SOME_KEY = "key";

    private static final String SOME_NAME = "name";

    @Test(expected = NullPointerException.class)
    public void constructor_throws_NPE_if_key_arg_is_null() {
        new MetricImpl(MetricImplTest.SOME_ID, null, MetricImplTest.SOME_NAME, MetricType.BOOL);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_throws_NPE_if_name_arg_is_null() {
        new MetricImpl(MetricImplTest.SOME_ID, MetricImplTest.SOME_KEY, null, MetricType.BOOL);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_throws_NPE_if_valueType_arg_is_null() {
        new MetricImpl(MetricImplTest.SOME_ID, MetricImplTest.SOME_KEY, MetricImplTest.SOME_NAME, null);
    }

    @Test
    public void constructor_throws_IAE_if_bestValueOptimized_is_true_but_bestValue_is_null() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("A BestValue must be specified if Metric is bestValueOptimized");
        new MetricImpl(MetricImplTest.SOME_ID, MetricImplTest.SOME_KEY, MetricImplTest.SOME_NAME, MetricType.INT, 1, null, true);
    }

    @Test
    public void verify_getters() {
        MetricImpl metric = new MetricImpl(MetricImplTest.SOME_ID, MetricImplTest.SOME_KEY, MetricImplTest.SOME_NAME, MetricType.FLOAT);
        assertThat(metric.getId()).isEqualTo(MetricImplTest.SOME_ID);
        assertThat(metric.getKey()).isEqualTo(MetricImplTest.SOME_KEY);
        assertThat(metric.getName()).isEqualTo(MetricImplTest.SOME_NAME);
        assertThat(metric.getType()).isEqualTo(FLOAT);
    }

    @Test
    public void equals_uses_only_key() {
        MetricImpl expected = new MetricImpl(MetricImplTest.SOME_ID, MetricImplTest.SOME_KEY, MetricImplTest.SOME_NAME, MetricType.FLOAT);
        assertThat(new MetricImpl(MetricImplTest.SOME_ID, MetricImplTest.SOME_KEY, MetricImplTest.SOME_NAME, MetricType.FLOAT)).isEqualTo(expected);
        assertThat(new MetricImpl(MetricImplTest.SOME_ID, MetricImplTest.SOME_KEY, MetricImplTest.SOME_NAME, MetricType.STRING)).isEqualTo(expected);
        assertThat(new MetricImpl(MetricImplTest.SOME_ID, MetricImplTest.SOME_KEY, MetricImplTest.SOME_NAME, MetricType.STRING, null, 0.0, true)).isEqualTo(expected);
        assertThat(new MetricImpl(MetricImplTest.SOME_ID, MetricImplTest.SOME_KEY, MetricImplTest.SOME_NAME, MetricType.STRING, null, null, false)).isEqualTo(expected);
        assertThat(new MetricImpl(MetricImplTest.SOME_ID, "some other key", MetricImplTest.SOME_NAME, MetricType.FLOAT)).isNotEqualTo(expected);
    }

    @Test
    public void hashcode_uses_only_key() {
        int expected = new MetricImpl(MetricImplTest.SOME_ID, MetricImplTest.SOME_KEY, MetricImplTest.SOME_NAME, MetricType.FLOAT).hashCode();
        assertThat(new MetricImpl(MetricImplTest.SOME_ID, MetricImplTest.SOME_KEY, "some other name", MetricType.FLOAT).hashCode()).isEqualTo(expected);
        assertThat(new MetricImpl(MetricImplTest.SOME_ID, MetricImplTest.SOME_KEY, "some other name", MetricType.BOOL).hashCode()).isEqualTo(expected);
    }

    @Test
    public void all_fields_are_displayed_in_toString() {
        assertThat(new MetricImpl(MetricImplTest.SOME_ID, MetricImplTest.SOME_KEY, MetricImplTest.SOME_NAME, MetricType.FLOAT, 1, 951.0, true).toString()).isEqualTo("MetricImpl{id=42, key=key, name=name, type=FLOAT, bestValue=951.0, bestValueOptimized=true}");
    }
}

