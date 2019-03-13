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
package org.drools.verifier.core.cache.inspectors.action;


import DataType.DataTypes;
import DataType.DataTypes.BOOLEAN;
import DataType.DataTypes.DATE;
import DataType.DataTypes.NUMERIC_INTEGER;
import DataType.DataTypes.STRING;
import java.util.Date;
import org.drools.verifier.core.checks.AnalyzerConfigurationMock;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.ObjectField;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ActionInspectorConflictResolverTest {
    private AnalyzerConfiguration configuration;

    @Test
    public void testRedundancy001() throws Exception {
        ActionInspector a = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "Person", "String", "name", new AnalyzerConfigurationMock()), STRING, "Toni");
        ActionInspector b = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "Person", "String", "name", new AnalyzerConfigurationMock()), STRING, "Toni");
        Assert.assertTrue(a.isRedundant(b));
        Assert.assertTrue(b.isRedundant(a));
    }

    @Test
    public void testRedundancy002() throws Exception {
        ActionInspector a = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "Person", "String", "name", new AnalyzerConfigurationMock()), STRING, "Toni");
        ActionInspector b = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "Person", "String", "name", new AnalyzerConfigurationMock()), STRING, "Rambo");
        Assert.assertFalse(a.isRedundant(b));
        Assert.assertFalse(b.isRedundant(a));
    }

    @Test
    public void testRedundancy003() throws Exception {
        ActionInspector a = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "org.test1.Person", "String", "name", new AnalyzerConfigurationMock()), STRING, "Toni");
        ActionInspector b = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "org.test2.Person", "String", "name", new AnalyzerConfigurationMock()), STRING, "Toni");
        Assert.assertFalse(a.isRedundant(b));
        Assert.assertFalse(b.isRedundant(a));
    }

    @Test
    public void testRedundancy004() throws Exception {
        ActionInspector a = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "Person", "Boolean", "isOldEnough", new AnalyzerConfigurationMock()), BOOLEAN, true);
        ActionInspector b = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "Person", "Boolean", "isOldEnough", new AnalyzerConfigurationMock()), STRING, "true");
        Assert.assertTrue(a.isRedundant(b));
        Assert.assertTrue(b.isRedundant(a));
    }

    @Test
    public void testRedundancy005() throws Exception {
        ActionInspector a = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "Person", "Boolean", "isOldEnough", new AnalyzerConfigurationMock()), BOOLEAN, true);
        ActionInspector b = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "Person", "Boolean", "isOldEnough", new AnalyzerConfigurationMock()), STRING, "false");
        Assert.assertFalse(a.isRedundant(b));
        Assert.assertFalse(b.isRedundant(a));
    }

    @Test
    public void testRedundancy006() throws Exception {
        ActionInspector a = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "Person", "Integer", "age", new AnalyzerConfigurationMock()), NUMERIC_INTEGER, 20);
        ActionInspector b = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "Person", "Integer", "age", new AnalyzerConfigurationMock()), STRING, "20");
        Assert.assertTrue(a.isRedundant(b));
        Assert.assertTrue(b.isRedundant(a));
    }

    @Test
    public void testRedundancy007() throws Exception {
        ActionInspector a = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "Person", "Integer", "age", new AnalyzerConfigurationMock()), NUMERIC_INTEGER, 20);
        ActionInspector b = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "Person", "Integer", "age", new AnalyzerConfigurationMock()), STRING, "10");
        Assert.assertFalse(a.isRedundant(b));
        Assert.assertFalse(b.isRedundant(a));
    }

    @Test
    public void testRedundancy008() throws Exception {
        Date date = new Date();
        ActionInspector a = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "Person", "Integer", "birthDay", new AnalyzerConfigurationMock()), DATE, date);
        ActionInspector b = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "Person", "Integer", "birthDay", new AnalyzerConfigurationMock()), STRING, format(date));
        Assert.assertTrue(a.isRedundant(b));
        Assert.assertTrue(b.isRedundant(a));
    }

    @Test
    public void testRedundancy009() throws Exception {
        Date value = new Date();
        ActionInspector a = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "Person", "Integer", "birthDay", new AnalyzerConfigurationMock()), DATE, value);
        ActionInspector b = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "Person", "Integer", "birthDay", new AnalyzerConfigurationMock()), STRING, "29-Dec-1981");
        Assert.assertFalse(a.isRedundant(b));
        Assert.assertFalse(b.isRedundant(a));
    }

    @Test
    public void testConflict001() throws Exception {
        ActionInspector a = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "Person", "String", "name", new AnalyzerConfigurationMock()), STRING, "Toni");
        ActionInspector b = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "Person", "String", "name", new AnalyzerConfigurationMock()), STRING, "Rambo");
        Assert.assertTrue(a.conflicts(b));
        Assert.assertTrue(b.conflicts(a));
    }

    @Test
    public void testConflict002() throws Exception {
        ActionInspector a = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "Person", "Boolean", "isOldEnough", new AnalyzerConfigurationMock()), BOOLEAN, true);
        ActionInspector b = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "Person", "Boolean", "isOldEnough", new AnalyzerConfigurationMock()), STRING, "false");
        Assert.assertTrue(a.conflicts(b));
        Assert.assertTrue(b.conflicts(a));
    }

    @Test
    public void testNoConflict001() throws Exception {
        ActionInspector a = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "Person", "String", "name", new AnalyzerConfigurationMock()), STRING, "Toni");
        ActionInspector b = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "Address", "String", "street", new AnalyzerConfigurationMock()), STRING, "Rambo");
        Assert.assertFalse(a.conflicts(b));
        Assert.assertFalse(b.conflicts(a));
    }

    @Test
    public void testNoConflict002() throws Exception {
        ActionInspector a = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "Person", "String", "name", new AnalyzerConfigurationMock()), STRING, "Toni");
        ActionInspector b = createSetActionInspector(new Field(Mockito.mock(ObjectField.class), "Person", "String", "name", new AnalyzerConfigurationMock()), STRING, "Toni");
        Assert.assertFalse(a.conflicts(b));
        Assert.assertFalse(b.conflicts(a));
    }

    @Test
    public void testNoConflict003() throws Exception {
        ActionInspector a = createSetActionInspector(new org.drools.verifier.core.index.model.FieldAction(new Field(Mockito.mock(ObjectField.class), "Person", "String", "name", new AnalyzerConfigurationMock()), Mockito.mock(Column.class), DataTypes.BOOLEAN, new Values(true), new AnalyzerConfigurationMock()));
        ActionInspector b = createSetActionInspector(new org.drools.verifier.core.index.model.FieldAction(new Field(Mockito.mock(ObjectField.class), "Person", "String", "name", new AnalyzerConfigurationMock()), Mockito.mock(Column.class), DataTypes.STRING, new Values(true), new AnalyzerConfigurationMock()));
        Assert.assertFalse(a.conflicts(b));
        Assert.assertFalse(b.conflicts(a));
    }
}

