/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.guided.dtable.shared.model;


import java.util.Arrays;
import java.util.List;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.junit.Assert;
import org.junit.Test;


public class BRLConditionColumnTest extends ColumnTestBase {
    private BRLConditionColumn column1;

    private BRLConditionColumn column2;

    @Test
    public void testDiffEmpty() {
        checkDiffEmpty(column1, column2);
    }

    @Test
    public void testDiffDefinitions() {
        List<IPattern> definition1 = Arrays.asList(new FactPattern("Mock1"));
        column1.setDefinition(definition1);
        List<IPattern> definition2 = Arrays.asList(new FactPattern("Mock2"));
        column2.setDefinition(definition2);
        checkSingleDiff(BRLConditionColumn.FIELD_DEFINITION, definition1, definition2, column1, column2);
    }

    @Test
    public void testDiffChildColumns() {
        List<BRLConditionVariableColumn> childColumns1 = Arrays.asList(new BRLConditionVariableColumn("var1", "FieldType1"));
        column1.setChildColumns(childColumns1);
        List<BRLConditionVariableColumn> childColumns2 = Arrays.asList(new BRLConditionVariableColumn("var2", "FieldType2"));
        column2.setChildColumns(childColumns2);
        List<BaseColumnFieldDiff> diff = column1.diff(column2);
        Assert.assertNotNull(diff);
        Assert.assertEquals(2, diff.size());
        Assert.assertEquals(ConditionCol52.FIELD_FIELD_TYPE, diff.get(0).getFieldName());
        Assert.assertEquals("FieldType1", diff.get(0).getOldValue());
        Assert.assertEquals("FieldType2", diff.get(0).getValue());
        Assert.assertEquals(BRLConditionVariableColumn.FIELD_VAR_NAME, diff.get(1).getFieldName());
        Assert.assertEquals("var1", diff.get(1).getOldValue());
        Assert.assertEquals("var2", diff.get(1).getValue());
    }

    @Test
    public void testDiffAll() {
        List<IPattern> definition1 = Arrays.asList(new FactPattern("Mock1"));
        column1.setDefinition(definition1);
        List<BRLConditionVariableColumn> childColumns1 = Arrays.asList(new BRLConditionVariableColumn("var1", "FieldType1"));
        column1.setChildColumns(childColumns1);
        column1.setHeader("header1");
        column1.setHideColumn(false);
        List<IPattern> definition2 = Arrays.asList(new FactPattern("Mock2"));
        column2.setDefinition(definition2);
        List<BRLConditionVariableColumn> childColumns2 = Arrays.asList(new BRLConditionVariableColumn("var2", "FieldType2"));
        column2.setChildColumns(childColumns2);
        column2.setHeader("header2");
        column2.setHideColumn(true);
        List<BaseColumnFieldDiff> diff = column1.diff(column2);
        Assert.assertNotNull(diff);
        Assert.assertEquals(DTColumnConfig52.FIELD_HIDE_COLUMN, diff.get(0).getFieldName());
        Assert.assertEquals(false, diff.get(0).getOldValue());
        Assert.assertEquals(true, diff.get(0).getValue());
        Assert.assertEquals(DTColumnConfig52.FIELD_HEADER, diff.get(1).getFieldName());
        Assert.assertEquals("header1", diff.get(1).getOldValue());
        Assert.assertEquals("header2", diff.get(1).getValue());
        Assert.assertEquals(BRLConditionColumn.FIELD_DEFINITION, diff.get(2).getFieldName());
        Assert.assertEquals(definition1, diff.get(2).getOldValue());
        Assert.assertEquals(definition2, diff.get(2).getValue());
        Assert.assertEquals(ConditionCol52.FIELD_FIELD_TYPE, diff.get(3).getFieldName());
        Assert.assertEquals("FieldType1", diff.get(3).getOldValue());
        Assert.assertEquals("FieldType2", diff.get(3).getValue());
        Assert.assertEquals(BRLConditionVariableColumn.FIELD_VAR_NAME, diff.get(4).getFieldName());
        Assert.assertEquals("var1", diff.get(4).getOldValue());
        Assert.assertEquals("var2", diff.get(4).getValue());
    }
}

