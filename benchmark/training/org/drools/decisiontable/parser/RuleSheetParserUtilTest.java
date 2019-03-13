/**
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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
package org.drools.decisiontable.parser;


import java.util.ArrayList;
import java.util.List;
import org.drools.template.model.Global;
import org.drools.template.model.Import;
import org.drools.template.parser.DecisionTableParseException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Nuff said...
 */
public class RuleSheetParserUtilTest {
    @Test
    public void testRuleName() {
        final String row = "  RuleTable       This is my rule name";
        final String result = RuleSheetParserUtil.getRuleName(row);
        Assert.assertEquals("This is my rule name", result);
    }

    @Test
    public void testIsStringMeaningTrue() {
        Assert.assertTrue(RuleSheetParserUtil.isStringMeaningTrue("true"));
        Assert.assertTrue(RuleSheetParserUtil.isStringMeaningTrue("TRUE"));
        Assert.assertTrue(RuleSheetParserUtil.isStringMeaningTrue("yes"));
        Assert.assertTrue(RuleSheetParserUtil.isStringMeaningTrue("oN"));
        Assert.assertFalse(RuleSheetParserUtil.isStringMeaningTrue("no"));
        Assert.assertFalse(RuleSheetParserUtil.isStringMeaningTrue("false"));
        Assert.assertFalse(RuleSheetParserUtil.isStringMeaningTrue(null));
    }

    @Test
    public void testListImports() {
        List<String> cellVals = null;
        List<Import> list = RuleSheetParserUtil.getImportList(cellVals);
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());
        cellVals = new ArrayList<String>();
        cellVals.add("");
        Assert.assertEquals(0, RuleSheetParserUtil.getImportList(cellVals).size());
        cellVals.add(0, "com.something.Yeah, com.something.No,com.something.yeah.*");
        list = RuleSheetParserUtil.getImportList(cellVals);
        Assert.assertEquals(3, list.size());
        Assert.assertEquals("com.something.Yeah", list.get(0).getClassName());
        Assert.assertEquals("com.something.No", list.get(1).getClassName());
        Assert.assertEquals("com.something.yeah.*", list.get(2).getClassName());
    }

    @Test
    public void testListVariables() {
        List<String> varCells = new ArrayList<String>();
        varCells.add("Var1 var1, Var2 var2,Var3 var3");
        final List<Global> varList = RuleSheetParserUtil.getVariableList(varCells);
        Assert.assertNotNull(varList);
        Assert.assertEquals(3, varList.size());
        Global var = varList.get(0);
        Assert.assertEquals("Var1", var.getClassName());
        var = varList.get(2);
        Assert.assertEquals("Var3", var.getClassName());
        Assert.assertEquals("var3", var.getIdentifier());
    }

    @Test
    public void testBadVariableFormat() {
        List<String> varCells = new ArrayList<String>();
        varCells.add("class1, object2");
        try {
            RuleSheetParserUtil.getVariableList(varCells);
            Assert.fail("should not work");
        } catch (final DecisionTableParseException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testRowColumnToCellNAme() {
        String cellName = RuleSheetParserUtil.rc2name(0, 0);
        Assert.assertEquals("A1", cellName);
        cellName = RuleSheetParserUtil.rc2name(0, 10);
        Assert.assertEquals("K1", cellName);
        cellName = RuleSheetParserUtil.rc2name(0, 42);
        Assert.assertEquals("AQ1", cellName);
        cellName = RuleSheetParserUtil.rc2name(9, 27);
        Assert.assertEquals("AB10", cellName);
        cellName = RuleSheetParserUtil.rc2name(99, 53);
        Assert.assertEquals("BB100", cellName);
    }
}

