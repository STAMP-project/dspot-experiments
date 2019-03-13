/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.verifier.core.cache.inspectors.condition;


import org.drools.verifier.core.index.model.Field;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;


@RunWith(Parameterized.class)
public class DoubleComparableConditionInspectorCoverTest {
    private final Comparable conditionValue;

    private final Comparable value;

    private final String conditionOperator;

    private final boolean coverExpected;

    private final Field field;

    public DoubleComparableConditionInspectorCoverTest(Comparable conditionValue, String conditionOperator, Comparable value, boolean coverExpected) {
        this.field = Mockito.mock(Field.class);
        this.conditionValue = conditionValue;
        this.value = value;
        this.conditionOperator = conditionOperator;
        this.coverExpected = coverExpected;
    }

    @Test
    public void parametrizedTest() {
        ComparableConditionInspector a = getCondition(conditionValue, conditionOperator);
        Assert.assertEquals(getAssertDescription(a, value, coverExpected), coverExpected, a.covers(value));
    }
}

