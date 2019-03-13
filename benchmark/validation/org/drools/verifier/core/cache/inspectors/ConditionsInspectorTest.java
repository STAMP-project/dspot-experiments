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
package org.drools.verifier.core.cache.inspectors;


import org.drools.verifier.core.cache.inspectors.condition.ConditionsInspectorMultiMap;
import org.drools.verifier.core.checks.AnalyzerConfigurationMock;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.ObjectField;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ConditionsInspectorTest {
    Field field;

    private AnalyzerConfigurationMock configurationMock;

    @Test
    public void testSubsume001() throws Exception {
        final ConditionsInspectorMultiMap a = getConditions(new org.drools.verifier.core.cache.inspectors.condition.ComparableConditionInspector<Integer>(new org.drools.verifier.core.index.model.FieldCondition(field, Mockito.mock(Column.class), "==", new org.drools.verifier.core.index.keys.Values(1), configurationMock), configurationMock));
        final ConditionsInspectorMultiMap b = getConditions(new org.drools.verifier.core.cache.inspectors.condition.ComparableConditionInspector<Integer>(new org.drools.verifier.core.index.model.FieldCondition(field, Mockito.mock(Column.class), "==", new org.drools.verifier.core.index.keys.Values(1), configurationMock), configurationMock));
        Assert.assertTrue(a.subsumes(b));
        Assert.assertTrue(b.subsumes(a));
    }

    @Test
    public void testSubsume002() throws Exception {
        final ConditionsInspectorMultiMap a = getConditions(new org.drools.verifier.core.cache.inspectors.condition.ComparableConditionInspector<Integer>(new org.drools.verifier.core.index.model.FieldCondition(field, Mockito.mock(Column.class), "==", new org.drools.verifier.core.index.keys.Values(1), configurationMock), configurationMock));
        final ConditionsInspectorMultiMap b = getConditions(new org.drools.verifier.core.cache.inspectors.condition.ComparableConditionInspector<Integer>(new org.drools.verifier.core.index.model.FieldCondition(field, Mockito.mock(Column.class), "==", new org.drools.verifier.core.index.keys.Values(1), configurationMock), configurationMock), new org.drools.verifier.core.cache.inspectors.condition.ComparableConditionInspector<Integer>(new org.drools.verifier.core.index.model.FieldCondition(new Field(Mockito.mock(ObjectField.class), "Person", "Integer", "balance", configurationMock), Mockito.mock(Column.class), "==", new org.drools.verifier.core.index.keys.Values(111111111), configurationMock), configurationMock));
        Assert.assertFalse(a.subsumes(b));
        Assert.assertTrue(b.subsumes(a));
    }

    @Test
    public void testSubsume003() throws Exception {
        final Field nameField = new Field(new ObjectField("Person", "String", "name", configurationMock), "Person", "String", "name", configurationMock);
        final Field lastNameField = new Field(new ObjectField("Person", "String", "lastName", configurationMock), "Person", "String", "lastName", configurationMock);
        final ConditionsInspectorMultiMap a = getConditions(new org.drools.verifier.core.cache.inspectors.condition.ComparableConditionInspector<String>(new org.drools.verifier.core.index.model.FieldCondition(nameField, Mockito.mock(Column.class), "==", new org.drools.verifier.core.index.keys.Values("Toni"), configurationMock), configurationMock));
        final ConditionsInspectorMultiMap b = getConditions(new org.drools.verifier.core.cache.inspectors.condition.ComparableConditionInspector<Integer>(new org.drools.verifier.core.index.model.FieldCondition(field, Mockito.mock(Column.class), "==", new org.drools.verifier.core.index.keys.Values(12), configurationMock), configurationMock), new org.drools.verifier.core.cache.inspectors.condition.ComparableConditionInspector<String>(new org.drools.verifier.core.index.model.FieldCondition(nameField, Mockito.mock(Column.class), "==", new org.drools.verifier.core.index.keys.Values("Toni"), configurationMock), configurationMock), new org.drools.verifier.core.cache.inspectors.condition.ComparableConditionInspector<String>(new org.drools.verifier.core.index.model.FieldCondition(lastNameField, Mockito.mock(Column.class), "==", new org.drools.verifier.core.index.keys.Values("Rikkola"), configurationMock), configurationMock));
        Assert.assertFalse(a.subsumes(b));
        Assert.assertTrue(b.subsumes(a));
    }
}

