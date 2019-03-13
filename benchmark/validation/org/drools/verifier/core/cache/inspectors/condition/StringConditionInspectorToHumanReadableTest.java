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


import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class StringConditionInspectorToHumanReadableTest {
    private static final String FIELD_NAME = "name";

    private static final String VALUE = "someValue";

    private static final String IS_NOT_NULL = "!= null";

    private static final String IS_NULL = "== null";

    private final String operator;

    private AnalyzerConfiguration configuration;

    public StringConditionInspectorToHumanReadableTest(String operator) {
        this.operator = operator;
    }

    @Test
    public void testToHumanReadableString() {
        final StringConditionInspector inspector = getStringConditionInspector();
        if (StringConditionInspectorToHumanReadableTest.IS_NOT_NULL.matches(operator)) {
            Assert.assertEquals(String.format("%s %s", StringConditionInspectorToHumanReadableTest.FIELD_NAME, operator), inspector.toHumanReadableString());
        } else
            if (StringConditionInspectorToHumanReadableTest.IS_NULL.matches(operator)) {
                Assert.assertEquals(String.format("%s %s", StringConditionInspectorToHumanReadableTest.FIELD_NAME, operator), inspector.toHumanReadableString());
            } else {
                Assert.assertEquals(String.format("%s %s %s", StringConditionInspectorToHumanReadableTest.FIELD_NAME, operator, StringConditionInspectorToHumanReadableTest.VALUE), inspector.toHumanReadableString());
            }

    }
}

