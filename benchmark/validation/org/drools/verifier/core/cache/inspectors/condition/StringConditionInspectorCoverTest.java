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
public class StringConditionInspectorCoverTest {
    private final Values<String> value1;

    private final Values<String> value2;

    private final String operator;

    private final boolean covers;

    private final Field field;

    public StringConditionInspectorCoverTest(Values<String> value1, String operator, Values<String> value2, boolean covers) {
        this.field = Mockito.mock(Field.class);
        this.operator = operator;
        this.value1 = value1;
        this.value2 = value2;
        this.covers = covers;
    }

    @Test
    public void parametrizedTest() {
        StringConditionInspector a = getCondition(value1, operator);
        Assert.assertEquals(getAssertDescription(a, covers, ((String) (value2.iterator().next()))), covers, a.covers(value2.iterator().next()));
    }
}

