/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.template.parser;


import java.util.List;
import org.drools.core.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;


public class RuleTemplateTest {
    @Test
    public void testSetContents() {
        RuleTemplate rt = new RuleTemplate("rt1", getTemplateContainer());
        rt.setContents("Test template");
        Assert.assertEquals("Test template\n", rt.getContents());
    }

    @Test
    public void testAddColumn() {
        RuleTemplate rt = new RuleTemplate("rt1", getTemplateContainer());
        rt.addColumn("StandardColumn");
        rt.addColumn("!NotColumn");
        rt.addColumn("ColumnCondition == \"test\"");
        rt.addColumn("!NotColumnCondition == \"test2\"");
        rt.addColumn("ArrayColumnCondition[0] == \"test2\"");
        List<TemplateColumn> columns = rt.getColumns();
        Assert.assertEquals(5, columns.size());
        TemplateColumn column1 = ((TemplateColumn) (columns.get(0)));
        Assert.assertEquals("StandardColumn", column1.getName());
        Assert.assertFalse(column1.isNotCondition());
        Assert.assertTrue(StringUtils.isEmpty(column1.getCondition()));
        TemplateColumn column2 = ((TemplateColumn) (columns.get(1)));
        Assert.assertEquals("NotColumn", column2.getName());
        Assert.assertTrue(column2.isNotCondition());
        Assert.assertTrue(StringUtils.isEmpty(column2.getCondition()));
        TemplateColumn column3 = ((TemplateColumn) (columns.get(2)));
        Assert.assertEquals("ColumnCondition", column3.getName());
        Assert.assertFalse(column3.isNotCondition());
        Assert.assertEquals("== \"test\"", column3.getCondition());
        TemplateColumn column4 = ((TemplateColumn) (columns.get(3)));
        Assert.assertEquals("NotColumnCondition", column4.getName());
        Assert.assertTrue(column4.isNotCondition());
        Assert.assertEquals("== \"test2\"", column4.getCondition());
    }
}

