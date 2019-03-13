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


public class MetadataCol52Test extends ColumnTestBase {
    private MetadataCol52 column1;

    private MetadataCol52 column2;

    @Test
    public void testDiffEmpty() {
        checkDiffEmpty(column1, column2);
    }

    @Test
    public void testDiffMetadata() {
        column1.setMetadata("meta1");
        column2.setMetadata("meta2");
        checkSingleDiff(MetadataCol52.FIELD_METADATA, "meta1", "meta2", column1, column2);
    }

    @Test
    public void testDiffAll() {
        column1.setMetadata("meta1");
        column1.setHeader("header1");
        column1.setHideColumn(false);
        column1.setDefaultValue(new DTCellValue52("default1"));
        column2.setMetadata("meta2");
        column2.setHeader("header2");
        column2.setHideColumn(true);
        column2.setDefaultValue(new DTCellValue52("default2"));
        List<BaseColumnFieldDiff> diff = column1.diff(column2);
        Assert.assertNotNull(diff);
        Assert.assertEquals(4, diff.size());
        Assert.assertEquals(DTColumnConfig52.FIELD_HIDE_COLUMN, diff.get(0).getFieldName());
        Assert.assertEquals(false, diff.get(0).getOldValue());
        Assert.assertEquals(true, diff.get(0).getValue());
        Assert.assertEquals(DTColumnConfig52.FIELD_DEFAULT_VALUE, diff.get(1).getFieldName());
        Assert.assertEquals("default1", diff.get(1).getOldValue());
        Assert.assertEquals("default2", diff.get(1).getValue());
        Assert.assertEquals(DTColumnConfig52.FIELD_HEADER, diff.get(2).getFieldName());
        Assert.assertEquals("header1", diff.get(2).getOldValue());
        Assert.assertEquals("header2", diff.get(2).getValue());
        Assert.assertEquals(MetadataCol52.FIELD_METADATA, diff.get(3).getFieldName());
        Assert.assertEquals("meta1", diff.get(3).getOldValue());
        Assert.assertEquals("meta2", diff.get(3).getValue());
    }

    @Test
    public void testCloneColumn() {
        column1.setWidth(10);
        MetadataCol52 clone = column1.cloneColumn();
        Assert.assertEquals(column1.getMetadata(), clone.getMetadata());
        Assert.assertEquals(column1.getHeader(), clone.getHeader());
        Assert.assertEquals(column1.getWidth(), clone.getWidth());
        Assert.assertEquals(column1.isHideColumn(), clone.isHideColumn());
        Assert.assertEquals(column1.getDefaultValue(), clone.getDefaultValue());
    }
}

