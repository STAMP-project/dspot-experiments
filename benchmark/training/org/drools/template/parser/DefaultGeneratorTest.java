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


import org.junit.Assert;
import org.junit.Test;


public class DefaultGeneratorTest {
    private DefaultGenerator g;

    @Test
    public void testSelectTemplate() {
        g.generate("rt2", new Row());
        String drl = g.getDrl();
        Assert.assertEquals("Test template 2\n\n", drl);
    }

    @Test
    public void testGenerate() {
        g.generate("rt2", new Row());
        g.generate("rt1", new Row());
        String drl = g.getDrl();
        Assert.assertEquals("Test template 2\n\nTest template 1\n\n", drl);
    }

    @Test
    public void testAddColumns() {
        Column[] columns = new Column[]{ new StringColumn("col1"), new StringColumn("col2") };
        Row r = new Row(1, columns);
        r.getCell(0).setValue("value1");
        r.getCell(1).setValue("value2");
        // Row r = new Row(1);
        // r.addCell(new StringCell(r, new StringColumn("col1"), "value1"));
        // r.addCell(new StringCell(r, new StringColumn("col2"), "value2"));
        g.generate("rt3", r);
        String drl = g.getDrl();
        Assert.assertEquals("1 value1 value2\n\n", drl);
    }
}

