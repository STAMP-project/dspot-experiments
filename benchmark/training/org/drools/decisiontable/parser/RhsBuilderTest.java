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
package org.drools.decisiontable.parser;


import ActionType.Code;
import org.junit.Assert;
import org.junit.Test;


public class RhsBuilderTest {
    @Test
    public void testConsBuilding() {
        RhsBuilder builder = new RhsBuilder(Code.ACTION, 9, 1, "foo");
        builder.addTemplate(10, 1, "setFoo($param)");
        builder.addCellValue(10, 1, "42");
        Assert.assertEquals("foo.setFoo(42);", builder.getResult());
        builder.clearValues();
        builder.addCellValue(10, 1, "33");
        Assert.assertEquals("foo.setFoo(33);", builder.getResult());
    }

    @Test
    public void testClassicMode() {
        RhsBuilder builder = new RhsBuilder(Code.ACTION, 9, 1, "");
        builder.addTemplate(10, 1, "p.setSomething($param);");
        builder.addTemplate(10, 2, "drools.clearAgenda();");
        builder.addCellValue(12, 1, "42");
        Assert.assertEquals("p.setSomething(42);", builder.getResult());
        builder.addCellValue(12, 2, "Y");
        Assert.assertEquals("p.setSomething(42);\ndrools.clearAgenda();", builder.getResult());
    }

    @Test
    public void testMetadata() {
        RhsBuilder builder = new RhsBuilder(Code.METADATA, 9, 1, "");
        builder.addTemplate(10, 1, "Author($param)");
        builder.addCellValue(12, 1, "A. U. Thor");
        Assert.assertEquals("Author(A. U. Thor)", builder.getResult());
        builder.clearValues();
        builder.addCellValue(13, 1, "P. G. Wodehouse");
        Assert.assertEquals("Author(P. G. Wodehouse)", builder.getResult());
    }

    @Test
    public void testEmptyCellData() {
        RhsBuilder builder = new RhsBuilder(Code.ACTION, 9, 1, "Foo");
        builder.addTemplate(10, 1, "p.setSomething($param);");
        Assert.assertFalse(builder.hasValues());
    }
}

