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


import BaseSingleFieldConstraint.TYPE_PREDICATE;
import BaseSingleFieldConstraint.TYPE_RET_VALUE;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ConditionCol52Test extends ColumnTestBase {
    private ConditionCol52.ConditionCol52 column1;

    private ConditionCol52.ConditionCol52 column2;

    @Test
    public void testDiffEmpty() {
        checkDiffEmpty(column1, column2);
    }

    @Test
    public void testDiffFactField() {
        column1.setFactField("field1");
        column2.setFactField("field2");
        checkSingleDiff(FIELD_FACT_FIELD, "field1", "field2", column1, column2);
    }

    @Test
    public void testDiffFieldType() {
        column1.setFieldType("Type1");
        column2.setFieldType("Type2");
        checkSingleDiff(FIELD_FIELD_TYPE, "Type1", "Type2", column1, column2);
    }

    @Test
    public void testDiffOperator() {
        column1.setOperator("<");
        column2.setOperator(">");
        checkSingleDiff(FIELD_OPERATOR, "<", ">", column1, column2);
    }

    @Test
    public void testDiffValueList() {
        column1.setValueList("v,a,l,u,e");
        column2.setValueList("l,i,s,t");
        checkSingleDiff(FIELD_VALUE_LIST, "v,a,l,u,e", "l,i,s,t", column1, column2);
    }

    @Test
    public void testDiffBinding() {
        column1.setBinding("$var1");
        column2.setBinding("$var2");
        checkSingleDiff(FIELD_BINDING, "$var1", "$var2", column1, column2);
    }

    @Test
    public void testDiffConstraintType() {
        column1.setConstraintValueType(TYPE_PREDICATE);
        column2.setConstraintValueType(TYPE_RET_VALUE);
        checkSingleDiff(FIELD_CONSTRAINT_VALUE_TYPE, TYPE_PREDICATE, TYPE_RET_VALUE, column1, column2);
    }

    @Test
    public void testDiffDefaultValueOriginalValueIsNull() {
        column1.setDefaultValue(null);
        column2.setDefaultValue(new DTCellValue52("default"));
        checkSingleDiff(DTColumnConfig52.FIELD_DEFAULT_VALUE, null, "default", column1, column2);
    }

    @Test
    public void testDiffDefaultValueNewValueIsNull() {
        column1.setDefaultValue(new DTCellValue52("default"));
        column2.setDefaultValue(null);
        checkSingleDiff(DTColumnConfig52.FIELD_DEFAULT_VALUE, "default", null, column1, column2);
    }

    @Test
    public void testDiffAll() {
        column1.setFactField("field1");
        column1.setFieldType("Type1");
        column1.setOperator("<");
        column1.setValueList("v,a,l,u,e");
        column1.setBinding("$var1");
        column1.setConstraintValueType(TYPE_PREDICATE);
        column1.setHeader("header1");
        column1.setHideColumn(false);
        column1.setDefaultValue(new DTCellValue52("default1"));
        column2.setFactField("field2");
        column2.setFieldType("Type2");
        column2.setOperator(">");
        column2.setValueList("l,i,s,t");
        column2.setBinding("$var2");
        column2.setConstraintValueType(TYPE_RET_VALUE);
        column2.setHeader("header2");
        column2.setHideColumn(true);
        column2.setDefaultValue(new DTCellValue52("default2"));
        List<BaseColumnFieldDiff> diff = column1.diff(column2);
        Assert.assertNotNull(diff);
        Assert.assertEquals(9, diff.size());
        Assert.assertEquals(DTColumnConfig52.FIELD_HIDE_COLUMN, diff.get(0).getFieldName());
        Assert.assertEquals(false, diff.get(0).getOldValue());
        Assert.assertEquals(true, diff.get(0).getValue());
        Assert.assertEquals(DTColumnConfig52.FIELD_DEFAULT_VALUE, diff.get(1).getFieldName());
        Assert.assertEquals("default1", diff.get(1).getOldValue());
        Assert.assertEquals("default2", diff.get(1).getValue());
        Assert.assertEquals(DTColumnConfig52.FIELD_HEADER, diff.get(2).getFieldName());
        Assert.assertEquals("header1", diff.get(2).getOldValue());
        Assert.assertEquals("header2", diff.get(2).getValue());
        Assert.assertEquals(FIELD_FACT_FIELD, diff.get(3).getFieldName());
        Assert.assertEquals("field1", diff.get(3).getOldValue());
        Assert.assertEquals("field2", diff.get(3).getValue());
        Assert.assertEquals(FIELD_FIELD_TYPE, diff.get(4).getFieldName());
        Assert.assertEquals("Type1", diff.get(4).getOldValue());
        Assert.assertEquals("Type2", diff.get(4).getValue());
        Assert.assertEquals(FIELD_OPERATOR, diff.get(5).getFieldName());
        Assert.assertEquals("<", diff.get(5).getOldValue());
        Assert.assertEquals(">", diff.get(5).getValue());
        Assert.assertEquals(FIELD_VALUE_LIST, diff.get(6).getFieldName());
        Assert.assertEquals("v,a,l,u,e", diff.get(6).getOldValue());
        Assert.assertEquals("l,i,s,t", diff.get(6).getValue());
        Assert.assertEquals(FIELD_BINDING, diff.get(7).getFieldName());
        Assert.assertEquals("$var1", diff.get(7).getOldValue());
        Assert.assertEquals("$var2", diff.get(7).getValue());
        Assert.assertEquals(FIELD_CONSTRAINT_VALUE_TYPE, diff.get(8).getFieldName());
        Assert.assertEquals(TYPE_PREDICATE, diff.get(8).getOldValue());
        Assert.assertEquals(TYPE_RET_VALUE, diff.get(8).getValue());
    }
}

