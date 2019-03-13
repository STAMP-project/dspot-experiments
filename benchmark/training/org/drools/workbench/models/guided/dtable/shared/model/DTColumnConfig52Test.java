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
import org.junit.Assert;
import org.junit.Test;


public class DTColumnConfig52Test extends ColumnTestBase {
    protected DTColumnConfig52 column1;

    protected DTColumnConfig52 column2;

    @Test
    public void testDiffEmpty() {
        checkDiffEmpty(column1, column2);
    }

    @Test
    public void testDiffHeader() {
        column1.setHeader("header1");
        column2.setHeader("header2");
        checkSingleDiff(DTColumnConfig52.FIELD_HEADER, "header1", "header2", column1, column2);
    }

    @Test
    public void testDiffHide() {
        column1.setHideColumn(false);
        column2.setHideColumn(true);
        checkSingleDiff(DTColumnConfig52.FIELD_HIDE_COLUMN, false, true, column1, column2);
    }

    @Test
    public void testDiffDefaultValue() {
        column1.setDefaultValue(new DTCellValue52("default1"));
        column2.setDefaultValue(new DTCellValue52(7));
        checkSingleDiff(DTColumnConfig52.FIELD_DEFAULT_VALUE, "default1", 7, column1, column2);
    }

    @Test
    public void testDiffAll() {
        column1.setHeader("header1");
        column1.setHideColumn(false);
        column1.setDefaultValue(new DTCellValue52("default1"));
        column2.setHeader("header2");
        column2.setHideColumn(true);
        column2.setDefaultValue(new DTCellValue52("default2"));
        List<BaseColumnFieldDiff> diff = column1.diff(column2);
        Assert.assertNotNull(diff);
        Assert.assertEquals(3, diff.size());
        Assert.assertEquals(DTColumnConfig52.FIELD_HIDE_COLUMN, diff.get(0).getFieldName());
        Assert.assertEquals(false, diff.get(0).getOldValue());
        Assert.assertEquals(true, diff.get(0).getValue());
        Assert.assertEquals(DTColumnConfig52.FIELD_DEFAULT_VALUE, diff.get(1).getFieldName());
        Assert.assertEquals("default1", diff.get(1).getOldValue());
        Assert.assertEquals("default2", diff.get(1).getValue());
        Assert.assertEquals(DTColumnConfig52.FIELD_HEADER, diff.get(2).getFieldName());
        Assert.assertEquals("header1", diff.get(2).getOldValue());
        Assert.assertEquals("header2", diff.get(2).getValue());
    }
}

