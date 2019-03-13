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
package org.drools.decisiontable;


import DefaultRuleSheetListener.DECLARES_TAG;
import DefaultRuleSheetListener.ESCAPE_QUOTES_FLAG;
import DefaultRuleSheetListener.FUNCTIONS_TAG;
import DefaultRuleSheetListener.IMPORT_TAG;
import DefaultRuleSheetListener.QUERIES_TAG;
import DefaultRuleSheetListener.RULESET_TAG;
import DefaultRuleSheetListener.SEQUENTIAL_FLAG;
import DefaultRuleSheetListener.VARIABLES_TAG;
import InputType.CSV;
import InputType.XLS;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.drools.decisiontable.parser.DefaultRuleSheetListener;
import org.drools.decisiontable.parser.RuleMatrixSheetListener;
import org.drools.decisiontable.parser.RuleSheetParserUtil;
import org.drools.decisiontable.parser.xls.ExcelParser;
import org.drools.template.model.Global;
import org.drools.template.model.Import;
import org.drools.template.parser.DataListener;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;


/**
 * Some basic unit tests for converter utility. Note that some of this may still
 * use the drools 2.x syntax, as it is not compiled, only tested that it
 * generates DRL in the correct structure (not that the DRL itself is correct).
 */
public class SpreadsheetCompilerUnitTest {
    @Test
    public void testLoadFromClassPath() {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        String drl = converter.compile("/data/MultiSheetDST.xls", XLS);
        Assert.assertNotNull(drl);
        Assert.assertTrue(((drl.indexOf("rule \"How cool am I_12\"")) > (drl.indexOf("rule \"How cool am I_11\""))));
        Assert.assertTrue(((drl.indexOf("import example.model.User;")) > (-1)));
        Assert.assertTrue(((drl.indexOf("import example.model.Car;")) > (-1)));
        Assert.assertTrue(((drl.indexOf("package ")) > (-1)));
        InputStream ins = this.getClass().getResourceAsStream("/data/MultiSheetDST.xls");
        drl = converter.compile(false, ins, XLS);
        Assert.assertNotNull(drl);
        Assert.assertTrue(((drl.indexOf("rule \"How cool am I_12\"")) > 0));
        Assert.assertTrue(((drl.indexOf("import example.model.User;")) > (-1)));
        Assert.assertTrue(((drl.indexOf("import example.model.Car;")) > (-1)));
        Assert.assertTrue(((drl.indexOf("package ")) == (-1)));
    }

    @Test
    public void testMultilineCommentsInDescription() {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        final InputStream stream = this.getClass().getResourceAsStream("/data/Multiline comment example.xls");
        final String drl = converter.compile(stream, XLS);
        Assertions.assertThat(drl).containsSequence("/* Say", "Hello */", "/* Say", "Goobye */");
        Assertions.assertThat(drl).doesNotContain("// Say");
    }

    @Test
    public void testMultilineComplexCommentsInDescription() {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        final InputStream stream = this.getClass().getResourceAsStream("/data/Multiline comment example complex.xls");
        final String drl = converter.compile(stream, XLS);
        Assertions.assertThat(drl).containsSequence("/* Do these actions:", "- Print Greeting", "- Set params: {message:'bye cruel world', status:'bye'} */", "/* Print message: \"Bye!\"", "Author: james@company.org */");
        Assertions.assertThat(drl).doesNotContain("* - Print Greeting");
        Assertions.assertThat(drl).doesNotContain("* - Set params: {message:'bye cruel world', status:'bye'}");
        Assertions.assertThat(drl).doesNotContain("* Author: james@company.org");
    }

    @Test
    public void testLoadSpecificWorksheet() {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        final InputStream stream = this.getClass().getResourceAsStream("/data/MultiSheetDST.xls");
        final String drl = converter.compile(stream, "Another Sheet");
        Assert.assertNotNull(drl);
    }

    @Test
    public void testLoadCustomListener() {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        final InputStream stream = this.getClass().getResourceAsStream("/data/CustomWorkbook.xls");
        final String drl = converter.compile(stream, XLS, new RuleMatrixSheetListener());
        Assert.assertNotNull(drl);
        Assert.assertTrue(((drl.indexOf("\"matrix\"")) != (-1)));
        Assert.assertTrue(((drl.indexOf("$v : FundVisibility")) != (-1)));
        Assert.assertTrue(((drl.indexOf("FundType")) != (-1)));
        Assert.assertTrue(((drl.indexOf("Role")) != (-1)));
    }

    @Test
    public void testLoadCsv() {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        final InputStream stream = this.getClass().getResourceAsStream("/data/ComplexWorkbook.csv");
        final String drl = converter.compile(stream, CSV);
        Assert.assertNotNull(drl);
        // System.out.println( drl );
        Assert.assertTrue(((drl.indexOf("myObject.setIsValid(1, 2)")) > 0));
        Assert.assertTrue(((drl.indexOf("myObject.size () > 50")) > 0));
        Assert.assertTrue(((drl.indexOf("Foo(myObject.getColour().equals(red), myObject.size () > 1)")) > 0));
    }

    @Test
    public void testLoadBasicWithMergedCells() {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        final InputStream stream = this.getClass().getResourceAsStream("/data/BasicWorkbook.xls");
        final String drl = converter.compile(stream, XLS);
        Assert.assertNotNull(drl);
        System.out.println(drl);
        Pattern p = Pattern.compile(".*setIsValid\\(Y\\).*setIsValid\\(Y\\).*setIsValid\\(Y\\).*", ((Pattern.DOTALL) | (Pattern.MULTILINE)));
        Matcher m = p.matcher(drl);
        Assert.assertTrue(m.matches());
        Assert.assertTrue(((drl.indexOf("This is a function block")) > (-1)));
        Assert.assertTrue(((drl.indexOf("global Class1 obj1;")) > (-1)));
        Assert.assertTrue(((drl.indexOf("myObject.setIsValid(10-Jul-1974)")) > (-1)));
        Assert.assertTrue(((drl.indexOf("myObject.getColour().equals(blue)")) > (-1)));
        Assert.assertTrue(((drl.indexOf("Foo(myObject.getColour().equals(red), myObject.size () > 12\\\")")) > (-1)));
        Assert.assertTrue(((drl.indexOf("b: Bar() eval(myObject.size() < 3)")) > (-1)));
        Assert.assertTrue(((drl.indexOf("b: Bar() eval(myObject.size() < 9)")) > (-1)));
        Assert.assertTrue(((drl.indexOf("Foo(myObject.getColour().equals(red), myObject.size () > 1)")) < (drl.indexOf("b: Bar() eval(myObject.size() < 3)"))));
        Assert.assertTrue(((drl.indexOf("myObject.setIsValid(\"19-Jul-1992\")")) > (-1)));
    }

    @Test
    public void testDeclaresXLS() {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        String drl = converter.compile("DeclaresWorkbook.xls", XLS);
        Assert.assertNotNull(drl);
        Assert.assertTrue(((drl.indexOf("declare Smurf name : String end")) > (-1)));
    }

    @Test
    public void testDeclaresCSV() {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        String drl = converter.compile("DeclaresWorkbook.csv", CSV);
        Assert.assertNotNull(drl);
        Assert.assertTrue(((drl.indexOf("declare Smurf name : String end")) > (-1)));
    }

    @Test
    public void testAttributesXLS() {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        String drl = converter.compile("Attributes.xls", XLS);
        Assert.assertNotNull(drl);
        int rule1 = drl.indexOf("rule \"N1\"");
        Assert.assertFalse((rule1 == (-1)));
        Assert.assertTrue(((drl.indexOf("no-loop true", rule1)) > (-1)));
        Assert.assertTrue(((drl.indexOf("duration 100", rule1)) > (-1)));
        Assert.assertTrue(((drl.indexOf("salience 1", rule1)) > (-1)));
        Assert.assertTrue(((drl.indexOf("ruleflow-group \"RFG1\"", rule1)) > (-1)));
        Assert.assertTrue(((drl.indexOf("agenda-group \"AG1\"", rule1)) > (-1)));
        Assert.assertTrue(((drl.indexOf("timer (T1)", rule1)) > (-1)));
        Assert.assertTrue(((drl.indexOf("lock-on-active true", rule1)) > (-1)));
        Assert.assertTrue(((drl.indexOf("activation-group \"g1\"", rule1)) > (-1)));
        Assert.assertTrue(((drl.indexOf("auto-focus true", rule1)) > (-1)));
        Assert.assertTrue(((drl.indexOf("calendars \"CAL1\"", rule1)) > (-1)));
        Assert.assertTrue(((drl.indexOf("date-effective \"01-Jan-2007\"", rule1)) > (-1)));
        Assert.assertTrue(((drl.indexOf("date-expires \"31-Dec-2007\"", rule1)) > (-1)));
        int rule2 = drl.indexOf("rule \"N2\"");
        Assert.assertFalse((rule2 == (-1)));
        Assert.assertTrue(((drl.indexOf("no-loop false", rule2)) > (-1)));
        Assert.assertTrue(((drl.indexOf("duration 200", rule2)) > (-1)));
        Assert.assertTrue(((drl.indexOf("salience 2", rule2)) > (-1)));
        Assert.assertTrue(((drl.indexOf("ruleflow-group \"RFG2\"", rule2)) > (-1)));
        Assert.assertTrue(((drl.indexOf("agenda-group \"AG2\"", rule2)) > (-1)));
        Assert.assertTrue(((drl.indexOf("timer (T2)", rule2)) > (-1)));
        Assert.assertTrue(((drl.indexOf("lock-on-active false", rule2)) > (-1)));
        Assert.assertTrue(((drl.indexOf("activation-group \"g2\"", rule2)) > (-1)));
        Assert.assertTrue(((drl.indexOf("auto-focus false", rule2)) > (-1)));
        Assert.assertTrue(((drl.indexOf("calendars \"CAL2\"", rule2)) > (-1)));
        Assert.assertTrue(((drl.indexOf("date-effective \"01-Jan-2012\"", rule2)) > (-1)));
        Assert.assertTrue(((drl.indexOf("date-expires \"31-Dec-2015\"", rule2)) > (-1)));
    }

    @Test
    public void testPropertiesXLS() {
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final DefaultRuleSheetListener listener = new DefaultRuleSheetListener();
        listeners.add(listener);
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("Properties.xls");
        parser.parseFile(is);
        listener.getProperties();
        final String rulesetName = listener.getProperties().getSingleProperty(RULESET_TAG);
        Assert.assertNotNull(rulesetName);
        Assert.assertEquals("Properties", rulesetName);
        final List<Import> importList = RuleSheetParserUtil.getImportList(listener.getProperties().getProperty(IMPORT_TAG));
        Assert.assertNotNull(importList);
        Assert.assertEquals(1, importList.size());
        Assert.assertEquals("java.util.List", importList.get(0).getClassName());
        final List<Global> variableList = RuleSheetParserUtil.getVariableList(listener.getProperties().getProperty(VARIABLES_TAG));
        Assert.assertNotNull(variableList);
        Assert.assertEquals(1, variableList.size());
        Assert.assertEquals("java.util.List", variableList.get(0).getClassName());
        Assert.assertEquals("list", variableList.get(0).getIdentifier());
        final List<String> functions = listener.getProperties().getProperty(FUNCTIONS_TAG);
        Assert.assertNotNull(functions);
        Assert.assertEquals(1, functions.size());
        Assert.assertEquals("A function", functions.get(0));
        final List<String> queries = listener.getProperties().getProperty(QUERIES_TAG);
        Assert.assertNotNull(queries);
        Assert.assertEquals(1, queries.size());
        Assert.assertEquals("A query", queries.get(0));
        final List<String> declarations = listener.getProperties().getProperty(DECLARES_TAG);
        Assert.assertNotNull(declarations);
        Assert.assertEquals(1, declarations.size());
        Assert.assertEquals("A declared type", declarations.get(0));
        final String sequentialFlag = listener.getProperties().getSingleProperty(SEQUENTIAL_FLAG);
        Assert.assertNotNull(sequentialFlag);
        Assert.assertEquals("false", sequentialFlag);
        final String escapeQuotesFlag = listener.getProperties().getSingleProperty(ESCAPE_QUOTES_FLAG);
        Assert.assertNotNull(escapeQuotesFlag);
        Assert.assertEquals("false", escapeQuotesFlag);
    }

    @Test
    public void testPropertiesWithWhiteSpaceXLS() {
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final DefaultRuleSheetListener listener = new DefaultRuleSheetListener();
        listeners.add(listener);
        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("PropertiesWithWhiteSpace.xls");
        parser.parseFile(is);
        listener.getProperties();
        final String rulesetName = listener.getProperties().getSingleProperty(RULESET_TAG);
        Assert.assertNotNull(rulesetName);
        Assert.assertEquals("Properties", rulesetName);
        final List<Import> importList = RuleSheetParserUtil.getImportList(listener.getProperties().getProperty(IMPORT_TAG));
        Assert.assertNotNull(importList);
        Assert.assertEquals(1, importList.size());
        Assert.assertEquals("java.util.List", importList.get(0).getClassName());
        final List<Global> variableList = RuleSheetParserUtil.getVariableList(listener.getProperties().getProperty(VARIABLES_TAG));
        Assert.assertNotNull(variableList);
        Assert.assertEquals(1, variableList.size());
        Assert.assertEquals("java.util.List", variableList.get(0).getClassName());
        Assert.assertEquals("list", variableList.get(0).getIdentifier());
        final List<String> functions = listener.getProperties().getProperty(FUNCTIONS_TAG);
        Assert.assertNotNull(functions);
        Assert.assertEquals(1, functions.size());
        Assert.assertEquals("A function", functions.get(0));
        final List<String> queries = listener.getProperties().getProperty(QUERIES_TAG);
        Assert.assertNotNull(queries);
        Assert.assertEquals(1, queries.size());
        Assert.assertEquals("A query", queries.get(0));
        final List<String> declarations = listener.getProperties().getProperty(DECLARES_TAG);
        Assert.assertNotNull(declarations);
        Assert.assertEquals(1, declarations.size());
        Assert.assertEquals("A declared type", declarations.get(0));
        final String sequentialFlag = listener.getProperties().getSingleProperty(SEQUENTIAL_FLAG);
        Assert.assertNotNull(sequentialFlag);
        Assert.assertEquals("false", sequentialFlag);
        final String escapeQuotesFlag = listener.getProperties().getSingleProperty(ESCAPE_QUOTES_FLAG);
        Assert.assertNotNull(escapeQuotesFlag);
        Assert.assertEquals("false", escapeQuotesFlag);
    }

    @Test
    public void testProcessSheetForExtremeLowNumbers() {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        final InputStream stream = this.getClass().getResourceAsStream("/data/BasicWorkbook_with_low_values.xls");
        final String drl = converter.compile(stream, XLS);
        Assert.assertNotNull(drl);
        System.out.println(drl);
        // Should parse the correct number
        Assert.assertTrue(((drl.indexOf("myObject.size() < 0")) == (-1)));
        Assert.assertTrue(((drl.indexOf("myObject.size() < 8.0E-11")) > (-1)));
        Assert.assertTrue(((drl.indexOf("myObject.size() < 9.0E-7")) > (-1)));
        Assert.assertTrue(((drl.indexOf("myObject.size() < 3.0E-4")) > (-1)));
    }

    @Test
    public void testNegativeNumbers() throws Exception {
        KieBase kbase = readKnowledgeBase("/data/DT_WithNegativeNumbers.xls");
        KieSession ksession = kbase.newKieSession();
        try {
            SpreadsheetCompilerUnitTest.IntHolder i1 = new SpreadsheetCompilerUnitTest.IntHolder(1);
            SpreadsheetCompilerUnitTest.IntHolder i2 = new SpreadsheetCompilerUnitTest.IntHolder((-1));
            ksession.insert(i1);
            ksession.insert(i2);
            ksession.fireAllRules();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testOOXMLParseCellValue() {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        String drl = converter.compile("/data/BZ963584.xls", XLS);
        Assert.assertNotNull(drl);
    }

    @Test
    public void testNoConstraintsEmptyCells() {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        /**
         * +--------------+--------------+
         * | CONDITION    | CONDITION    |
         * +--------------+--------------+
         * |           Person            |
         * +--------------+--------------+
         * |	name      |    age       |
         * +--------------+--------------+
         * | <empty>      | 55           |
         * | Fred         | <empty>      |
         * | Fred         | 55           |
         * | <empty>      | <empty>      |
         * +--------------+--------------+
         */
        String drl = converter.compile("/data/NoConstraintsEmptyCells.xls", XLS);
        Assert.assertNotNull(drl);
        final String expected = "package Some_business_rules;\n" + ((((((((((((((((((("//generated from Decision Table\n" + "import org.drools.decisiontable.Person;\n") + "// rule values at C10, header at C5\n") + "rule \"Cheese fans_10\"\n") + "  when\n") + "    Person(age == \"55\")\n") + "  then\n") + "end\n") + "// rule values at C11, header at C5\n") + "rule \"Cheese fans_11\"\n") + "  when\n") + "    Person(name == \"Fred\")\n") + "  then\n") + "end\n") + "// rule values at C12, header at C5\n") + "rule \"Cheese fans_12\"\n") + "  when\n") + "    Person(name == \"Fred\", age == \"55\")\n") + "  then\n") + "end");
        // No Pattern generated when all constraints have empty values
        Assertions.assertThat(expected).isEqualToIgnoringWhitespace(drl);
    }

    @Test
    public void testNoConstraintsSpacesInCells() {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        /**
         * +--------------+--------------+
         * | CONDITION    | CONDITION    |
         * +--------------+--------------+
         * |           Person            |
         * +--------------+--------------+
         * |	name      |    age       |
         * +--------------+--------------+
         * | <spaces>     | 55           |
         * | Fred         | <spaces>     |
         * | Fred         | 55           |
         * | <spaces>     | <spaces>     |
         * +--------------+--------------+
         */
        String drl = converter.compile("/data/NoConstraintsSpacesInCells.xls", XLS);
        Assert.assertNotNull(drl);
        final String expected = "package Some_business_rules;\n" + ((((((((((((((((((("//generated from Decision Table\n" + "import org.drools.decisiontable.Person;\n") + "// rule values at C10, header at C5\n") + "rule \"Cheese fans_10\"\n") + "  when\n") + "    Person(age == \"55\")\n") + "  then\n") + "end\n") + "// rule values at C11, header at C5\n") + "rule \"Cheese fans_11\"\n") + "  when\n") + "    Person(name == \"Fred\")\n") + "  then\n") + "end\n") + "// rule values at C12, header at C5\n") + "rule \"Cheese fans_12\"\n") + "  when\n") + "    Person(name == \"Fred\", age == \"55\")\n") + "  then\n") + "end");
        // No Pattern generated when all constraints have empty values
        Assertions.assertThat(expected).isEqualToIgnoringWhitespace(drl);
    }

    @Test
    public void testNoConstraintsDelimitedSpacesInCells() {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        /**
         * +--------------+--------------+
         * | CONDITION    | CONDITION    |
         * +--------------+--------------+
         * |           Person            |
         * +--------------+--------------+
         * |	name      |    age       |
         * +--------------+--------------+
         * | "     "      | 55           |
         * | Fred         | "     "      |
         * | Fred         | 55           |
         * | "     "      | "     "      |
         * | ""           | 55           |
         * | Fred         | ""           |
         * +--------------+--------------+
         */
        String drl = converter.compile("/data/NoConstraintsDelimitedSpacesInCells.xls", XLS);
        Assert.assertNotNull(drl);
        final String expected = "package Some_business_rules;\n" + ((((((((((((((((((((((((((((((((((((("//generated from Decision Table\n" + "import org.drools.decisiontable.Person;\n") + "// rule values at C10, header at C5\n") + "rule \"Cheese fans_10\"\n") + "  when\n") + "    Person(name == \"     \", age == \"55\")\n") + "  then\n") + "end\n") + "// rule values at C11, header at C5\n") + "rule \"Cheese fans_11\"\n") + "  when\n") + "    Person(name == \"Fred\", age == \"\")\n") + "  then\n") + "end\n") + "// rule values at C12, header at C5\n") + "rule \"Cheese fans_12\"\n") + "  when\n") + "    Person(name == \"Fred\", age == \"55\")\n") + "  then\n") + "end\n") + "// rule values at C13, header at C5\n") + "rule \"Cheese fans_13\"\n") + "  when\n") + "    Person(name == \"     \", age == \"\")\n") + "  then\n") + "end\n") + "// rule values at C14, header at C5\n") + "rule \"Cheese fans_14\"\n") + "  when\n") + "    Person(name == \"\", age == \"55\")\n") + "  then\n") + "end\n") + "// rule values at C15, header at C5\n") + "rule \"Cheese fans_15\"\n") + "  when\n") + "    Person(name == \"Fred\", age == \"\")\n") + "  then\n") + "end");
        Assertions.assertThat(expected).isEqualToIgnoringWhitespace(drl);
    }

    @Test
    public void testForAllConstraintQuoteRemoval() {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        String drl = converter.compile("/data/ForAllConstraintQuoteRemoval.xls", XLS);
        Assert.assertNotNull(drl);
        final String expected = "package Connexis_Cash_Enrichment;\n" + ((((((((((((((((((((((((((((((((((((((((((("//generated from Decision Table\n" + "import com.brms.dto.fact.*;\n") + "dialect \"mvel\";\n") + "// rule values at C21, header at C16\n") + "rule \"enrichment_21\"\n") + "  when\n") + "    p:Payment(fileFormat == \"TOTO\")\n") + "  then\n") + "    System.out.println(true);\n") + "end\n") + "// rule values at C22, header at C16\n") + "rule \"enrichment_22\"\n") + "  when\n") + "    p:Payment(fileFormat == \"TOTO\")\n") + "  then\n") + "    System.out.println(true);\n") + "end\n") + "// rule values at C23, header at C16\n") + "rule \"enrichment_23\"\n") + "  when\n") + "    p:Payment(fileFormat == \"TOTO\" || fileFormat == \"TITI\" || fileFormat == \"TOR\")\n") + "  then\n") + "    System.out.println(true);\n") + "end\n") + "// rule values at C24, header at C16\n") + "rule \"enrichment_24\"\n") + "  when\n") + "    p:Payment(fileFormat == \"TOTO\" || fileFormat == \"TOR\")\n") + "  then\n") + "    System.out.println(true);\n") + "end\n") + "// rule values at C25, header at C16\n") + "rule \"enrichment_25\"\n") + "  when\n") + "    p:Payment(fileFormat == \"TITI\", isConsistencyCheckEnabled == \"true\")\n") + "  then\n") + "    System.out.println(true);\n") + "end\n") + "// rule values at C26, header at C16\n") + "rule \"enrichment_26\"\n") + "  when\n") + "  then\n") + "    System.out.println(false);\n") + "end\n");
        Assertions.assertThat(expected).isEqualToIgnoringWhitespace(drl);
    }

    public static class IntHolder {
        private int value;

        public IntHolder(int i) {
            value = i;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return ("IntHolder [value=" + (value)) + "]";
        }
    }

    @Test
    public void testFunctionCellMerged() {
        // BZ-1147402
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        String drl = converter.compile("/data/FunctionCellMerged.xls", XLS);
        Assert.assertNotNull(drl);
        Assert.assertTrue(drl.contains("function void test(){"));
    }

    @Test
    public void testMoreThan9InputParamSubstitution() throws Exception {
        // https://issues.jboss.org/browse/DROOLS-836
        final String EXPECTED_CONDITION = "eval ($objects: Object (id == a ||  == b ||  == c ||  == d ||  == e ||  == f ||  == g ||  == h ||  == i  ||  == j  ) )";
        final String EXPECTED_ACTION = "System.out.println(?test? + a  + b   + c  + d  + e  + f  + g + h  + i + j);";
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        String drl = converter.compile("/data/DROOLS-836.xls", XLS);
        Assert.assertNotNull(drl);
        Assert.assertTrue(drl.contains(EXPECTED_CONDITION));
        Assert.assertTrue(drl.contains(EXPECTED_ACTION));
    }

    @Test
    public void testDtableUsingExcelFunction() throws Exception {
        // DROOLS-887
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        String drl = converter.compile("/data/RuleNameUsingExcelFunction.xls", XLS);
        final String EXPECTED_RULE_NAME = "rule \"RULE_500\"";
        Assert.assertTrue(drl.contains(EXPECTED_RULE_NAME));
    }

    @Test
    public void checkLhsBuilderFixValue() {
        // https://issues.jboss.org/browse/DROOLS-1279
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        String drl = converter.compile("/data/DROOLS-1279.xls", XLS);
        Assert.assertNotNull(drl);
        final String expected = "package com.sample;\n" + (((((((((((((((((("//generated from Decision Table\n" + "import com.sample.DecisionTableTest.Message;\n") + "dialect \"mvel\"\n") + "// rule values at C12, header at C7\n") + "rule \"HelloWorld_12\"\n") + "  when\n") + "    m:Message(checktest in(\"AAA\"), status == \"Message.HELLO\")\n") + "  then\n") + "    System.out.println(m.getMessage());\n") + "    m.setMessage(\"Goodbye cruel world\");update(m);\n") + "    m.setChecktest(\"BBB\");update(m);\n") + "end\n") + "// rule values at C13, header at C7\n") + "rule \"HelloWorld_13\"\n") + "  when\n") + "    m:Message(checktest in(\"BBB\", \"CCC\"), status == \"Message.GOODBYE\")\n") + "  then\n") + "    System.out.println(m.getMessage());\n") + "end\n");
        Assertions.assertThat(expected).isEqualToIgnoringWhitespace(drl);
    }

    @Test
    public void testLhsOrder() {
        // DROOLS-3080
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        String drl = converter.compile("/data/LhsOrder.xls", XLS);
        Assertions.assertThat(Stream.of(drl.split("\n")).map(String::trim).toArray()).as("Lhs order is wrong").containsSequence("accumulate(Person(name == \"John\", $a : age); $max:max($a))", "$p:Person(name == \"John\", age == $max)");
    }
}

