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
public class BooleanConditionInspectorTest {
    private final boolean value1;

    private final boolean value2;

    private final String operator1;

    private final String operator2;

    private final boolean conflictExpected;

    private final Field field;

    public BooleanConditionInspectorTest(String operator1, boolean value1, String operator2, boolean value2, boolean conflictExpected) {
        this.field = Mockito.mock(Field.class);
        this.value1 = value1;
        this.value2 = value2;
        this.operator1 = operator1;
        this.operator2 = operator2;
        this.conflictExpected = conflictExpected;
    }

    @Test
    public void parametrizedConflictTest() {
        BooleanConditionInspector a = getCondition(value1, operator1);
        BooleanConditionInspector b = getCondition(value2, operator2);
        Assert.assertEquals(getAssertDescription(a, b, conflictExpected, "conflict"), conflictExpected, a.conflicts(b));
        Assert.assertEquals(getAssertDescription(b, a, conflictExpected, "conflict"), conflictExpected, b.conflicts(a));
    }

    @Test
    public void parametrizedRedundancyTest() {
        BooleanConditionInspector a = getCondition(value1, operator1);
        BooleanConditionInspector b = getCondition(value2, operator2);
        Assert.assertEquals(getAssertDescription(a, b, (!(conflictExpected)), "be redundant"), (!(conflictExpected)), a.isRedundant(b));
        Assert.assertEquals(getAssertDescription(b, a, (!(conflictExpected)), "be redundant"), (!(conflictExpected)), b.isRedundant(a));
    }

    @Test
    public void parametrizedOverlapTest() {
        BooleanConditionInspector a = getCondition(value1, operator1);
        BooleanConditionInspector b = getCondition(value2, operator2);
        Assert.assertEquals(getAssertDescription(a, b, (!(conflictExpected)), "overlap"), (!(conflictExpected)), a.overlaps(b));
        Assert.assertEquals(getAssertDescription(b, a, (!(conflictExpected)), "overlap"), (!(conflictExpected)), b.overlaps(a));
    }

    @Test
    public void parametrizedSubsumptionTest() {
        BooleanConditionInspector a = getCondition(value1, operator1);
        BooleanConditionInspector b = getCondition(value2, operator2);
        Assert.assertEquals(getAssertDescription(a, b, (!(conflictExpected)), "be subsuming"), (!(conflictExpected)), a.subsumes(b));
        Assert.assertEquals(getAssertDescription(b, a, (!(conflictExpected)), "be subsuming"), (!(conflictExpected)), b.subsumes(a));
    }
}

