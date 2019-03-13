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


import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Field;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;


@RunWith(Parameterized.class)
public class StringConditionInspectorSubsumptionTest {
    private final Values value1;

    private final Values value2;

    private final String operator1;

    private final String operator2;

    private final boolean aSubsumesB;

    private final boolean bSubsumesA;

    private final Field field;

    public StringConditionInspectorSubsumptionTest(String operator1, Values value1, String operator2, Values value2, boolean aSubsumesB, boolean bSubsumesA) {
        this.field = Mockito.mock(Field.class);
        this.value1 = value1;
        this.value2 = value2;
        this.operator1 = operator1;
        this.operator2 = operator2;
        this.aSubsumesB = aSubsumesB;
        this.bSubsumesA = bSubsumesA;
    }

    @Test
    public void testASubsumesB() {
        StringConditionInspector a = getCondition(value1, operator1);
        StringConditionInspector b = getCondition(value2, operator2);
        Assert.assertEquals(getAssertDescription(a, b, aSubsumesB), aSubsumesB, a.subsumes(b));
    }

    @Test
    public void testBSubsumesA() {
        StringConditionInspector a = getCondition(value1, operator1);
        StringConditionInspector b = getCondition(value2, operator2);
        Assert.assertEquals(getAssertDescription(b, a, bSubsumesA), bSubsumesA, b.subsumes(a));
    }
}

