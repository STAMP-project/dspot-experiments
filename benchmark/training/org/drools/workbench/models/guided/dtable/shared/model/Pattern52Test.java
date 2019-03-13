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


import java.util.List;
import org.drools.workbench.models.datamodel.rule.CEPWindow;
import org.junit.Assert;
import org.junit.Test;


public class Pattern52Test extends ColumnTestBase {
    private Pattern52 column1;

    private Pattern52 column2;

    @Test
    public void testDiffEmpty() {
        checkDiffEmpty(column1, column2);
    }

    @Test
    public void testDiffFactType() {
        column1.setFactType("Fact1");
        column2.setFactType("Fact2");
        checkSingleDiff(Pattern52.FIELD_FACT_TYPE, "Fact1", "Fact2", column1, column2);
    }

    @Test
    public void testDiffBoundName() {
        column1.setBoundName("$var1");
        column2.setBoundName("$var2");
        checkSingleDiff(Pattern52.FIELD_BOUND_NAME, "$var1", "$var2", column1, column2);
    }

    @Test
    public void testDiffNegated() {
        column1.setNegated(false);
        column2.setNegated(true);
        checkSingleDiff(Pattern52.FIELD_IS_NEGATED, false, true, column1, column2);
    }

    @Test
    public void testDiffWindow() {
        CEPWindow window1 = new CEPWindow();
        window1.setOperator("dummyOp1");
        column1.setWindow(window1);
        CEPWindow window2 = new CEPWindow();
        window2.setOperator("dummyOp2");
        column2.setWindow(window2);
        checkSingleDiff(Pattern52.FIELD_WINDOW, window1, window2, column1, column2);
    }

    @Test
    public void testDiffEntryPoint() {
        column1.setEntryPointName("entryPoint1");
        column2.setEntryPointName("entryPoint2");
        checkSingleDiff(Pattern52.FIELD_ENTRY_POINT_NAME, "entryPoint1", "entryPoint2", column1, column2);
    }

    @Test
    public void testDiffAll() {
        column1.setFactType("Fact1");
        column1.setBoundName("$var1");
        column1.setNegated(false);
        CEPWindow window1 = new CEPWindow();
        window1.setOperator("dummyOp1");
        column1.setWindow(window1);
        column1.setEntryPointName("entryPoint1");
        column2.setFactType("Fact2");
        column2.setBoundName("$var2");
        column2.setNegated(true);
        CEPWindow window2 = new CEPWindow();
        window2.setOperator("dummyOp2");
        column2.setWindow(window2);
        column2.setEntryPointName("entryPoint2");
        List<BaseColumnFieldDiff> diff = column1.diff(column2);
        Assert.assertNotNull(diff);
        Assert.assertEquals(5, diff.size());
        Assert.assertEquals(Pattern52.FIELD_FACT_TYPE, diff.get(0).getFieldName());
        Assert.assertEquals("Fact1", diff.get(0).getOldValue());
        Assert.assertEquals("Fact2", diff.get(0).getValue());
        Assert.assertEquals(Pattern52.FIELD_BOUND_NAME, diff.get(1).getFieldName());
        Assert.assertEquals("$var1", diff.get(1).getOldValue());
        Assert.assertEquals("$var2", diff.get(1).getValue());
        Assert.assertEquals(Pattern52.FIELD_IS_NEGATED, diff.get(2).getFieldName());
        Assert.assertEquals(false, diff.get(2).getOldValue());
        Assert.assertEquals(true, diff.get(2).getValue());
        Assert.assertEquals(Pattern52.FIELD_WINDOW, diff.get(3).getFieldName());
        Assert.assertEquals(window1, diff.get(3).getOldValue());
        Assert.assertEquals(window2, diff.get(3).getValue());
        Assert.assertEquals(Pattern52.FIELD_ENTRY_POINT_NAME, diff.get(4).getFieldName());
        Assert.assertEquals("entryPoint1", diff.get(4).getOldValue());
        Assert.assertEquals("entryPoint2", diff.get(4).getValue());
    }

    @Test
    public void testCloneColumn() {
        Pattern52 clone = column1.clonePattern();
        Assert.assertEquals(column1.getFactType(), clone.getFactType());
        Assert.assertEquals(column1.getBoundName(), clone.getBoundName());
        Assert.assertEquals(column1.getWindow(), clone.getWindow());
        Assert.assertEquals(column1.getEntryPointName(), clone.getEntryPointName());
        Assert.assertEquals(column1.isNegated(), clone.isNegated());
    }
}

