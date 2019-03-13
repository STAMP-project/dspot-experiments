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


import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import org.drools.decisiontable.parser.xls.PropertiesSheetListener.CaseInsensitiveMap;
import org.drools.template.model.Condition;
import org.drools.template.model.Consequence;
import org.drools.template.model.DRLOutput;
import org.drools.template.model.Import;
import org.drools.template.model.Rule;
import org.drools.template.parser.DecisionTableParseException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test an excel file.
 *
 * Assumes it has a sheet called "Decision Tables" with a rule table identified
 * by a "RuleTable" cell
 */
public class RuleWorksheetParseTest {
    @Test
    public void testBasicWorkbookProperties() throws Exception {
        final InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream("/data/BasicWorkbook.xls");
        final RuleSheetListener listener = RuleWorksheetParseTest.getRuleSheetListener(stream);
        final CaseInsensitiveMap props = listener.getProperties();
        Assert.assertNotNull(props);
        Assert.assertEquals("myruleset", props.getSingleProperty("RuleSet"));
        Assert.assertEquals("someMisc", props.getSingleProperty("misc"));
        /* System.out.println("Here are the global properties...");
        listener.getProperties().list(System.out);
         */
    }

    @Test
    public void testComplexWorkbookProperties() throws Exception {
        final InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream("/data/ComplexWorkbook.xls");
        final RuleSheetListener listener = RuleWorksheetParseTest.getRuleSheetListener(stream);
        final CaseInsensitiveMap props = listener.getProperties();
        Assert.assertNotNull(props);
        final String ruleSetName = props.getSingleProperty("RuleSet");
        Assert.assertEquals("ruleSetName", ruleSetName);
    }

    @Test
    public void testWorkbookParse() throws Exception {
        final InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream("/data/BasicWorkbook.xls");
        final RuleSheetListener listener = RuleWorksheetParseTest.getRuleSheetListener(stream);
        final Package ruleset = listener.getRuleSet();
        Assert.assertNotNull(ruleset);
        final Rule firstRule = ((Rule) (getRules().get(0)));
        Assert.assertNotNull(firstRule.getSalience());
        Assert.assertTrue(((Integer.parseInt(firstRule.getSalience())) > 0));
        // System.out.println(ruleset.toXML());
        Assert.assertEquals("myruleset", ruleset.getName());
        Assert.assertEquals(3, getImports().size());
        Assert.assertEquals(6, getRules().size());
        // check imports
        Import imp = ((Import) (getImports().get(0)));
        Assert.assertEquals("blah.class1", imp.getClassName());
        imp = ((Import) (getImports().get(1)));
        Assert.assertEquals("blah.class2", imp.getClassName());
        imp = ((Import) (getImports().get(2)));
        Assert.assertEquals("lah.di.dah", imp.getClassName());
        // check rules
        Rule rule = ((Rule) (getRules().get(0)));
        Condition cond = ((Condition) (rule.getConditions().get(0)));
        Assert.assertEquals("Foo(myObject.getColour().equals(red), myObject.size () > 12\\\")", cond.getSnippet());
        Consequence cons = ((Consequence) (rule.getConsequences().get(0)));
        Assert.assertNotNull(cons);
        Assert.assertEquals("myObject.setIsValid(Y);", cons.getSnippet());
        rule = ((Rule) (getRules().get(5)));
        cond = ((Condition) (rule.getConditions().get(1)));
        Assert.assertEquals("myObject.size () > 7", cond.getSnippet());
        cons = ((Consequence) (rule.getConsequences().get(0)));
        Assert.assertEquals("myObject.setIsValid(10-Jul-1974)", cons.getSnippet());
    }

    private RuleSheetListener listener;

    private int row;

    /**
     * Duplications of several columns are not permitted: NO-LOOP/U.
     */
    @Test
    public void testTooManyColumnsNoLoop() {
        try {
            makeRuleSet();
            makeRuleTable();
            makeRow(11, "C", "C", "A", "U", "U");
            listener.finishSheet();
            Assert.fail("should have failed");
        } catch (DecisionTableParseException e) {
            String badCell = RuleSheetParserUtil.rc2name(11, 5);
            System.err.println(e.getMessage());
            Assert.assertTrue(e.getMessage().contains(badCell));
        }
    }

    /**
     * Duplications of several columns are not permitted : PRIORITY/P.
     */
    @Test
    public void testTooManyColumnsPriority() {
        try {
            makeRuleSet();
            makeRuleTable();
            makeRow(11, "C", "C", "A", "PRIORITY", "P");
            listener.finishSheet();
            Assert.fail("should have failed");
        } catch (DecisionTableParseException e) {
            String badCell = RuleSheetParserUtil.rc2name(11, 5);
            System.err.println(e.getMessage());
            Assert.assertTrue(e.getMessage().contains(badCell));
        }
    }

    /**
     * Column headers must be valid.
     */
    @Test
    public void testBadColumnHeader() {
        try {
            makeRuleSet();
            makeRuleTable();
            makeRow(11, "Condition", "CONDITION", "A", "SMURF", "P");
            listener.finishSheet();
            Assert.fail("should have failed");
        } catch (DecisionTableParseException e) {
            String badCell = RuleSheetParserUtil.rc2name(11, 4);
            System.err.println(e.getMessage());
            Assert.assertTrue(e.getMessage().contains(badCell));
        }
    }

    /**
     * Must have a type for pattern below a condition, not a snippet.
     */
    @Test
    public void testMissingCondition() {
        try {
            makeRuleSet();
            makeRuleTable();
            makeRow(11, "C", "C", "C", "A", "A");
            makeRow(12, "attr == $param", "attr == $param", "attr == $param", "action();", "action();");
            listener.finishSheet();
            Assert.fail("should have failed");
        } catch (DecisionTableParseException e) {
            String badCell = RuleSheetParserUtil.rc2name(12, 1);
            System.err.println(e.getMessage());
            Assert.assertTrue(e.getMessage().contains(badCell));
        }
    }

    /**
     * Must have a code snippet in a condition.
     */
    @Test
    public void testMissingCodeSnippetCondition() {
        try {
            makeRuleSet();
            makeRuleTable();
            makeRow(11, "C", "C", "C", "A", "A");
            makeRow(12, "Foo", "Foo", "Foo");
            makeRow(13, "attr == $param", "attr == $param", "", "action();", "action();");
            makeRow(15, "1", "2", "3", "", "");
            listener.finishSheet();
            Assert.fail("should have failed");
        } catch (DecisionTableParseException e) {
            String badCell = RuleSheetParserUtil.rc2name(13, 3);
            System.err.println(e.getMessage());
            Assert.assertTrue(e.getMessage().contains(badCell));
        }
    }

    /**
     * Spurious code snippet.
     */
    @Test
    public void testSpuriousCodeSnippet() {
        try {
            makeRuleSet();
            makeRuleTable();
            makeRow(11, "C", "C", "A");
            makeRow(12, "Foo", "Foo");
            makeRow(13, "attr == $param", "attr == $param", "action();", "attr > $param");
            makeRow(15, "1", "2", "");
            listener.finishSheet();
            Assert.fail("should have failed");
        } catch (DecisionTableParseException e) {
            String badCell = RuleSheetParserUtil.rc2name(13, 4);
            System.err.println(e.getMessage());
            Assert.assertTrue(e.getMessage().contains(badCell));
        }
    }

    /**
     * Incorrect priority - not numeric
     */
    @Test
    public void testIncorrectPriority() {
        try {
            makeRuleSet();
            makeRuleTable();
            makeRow(11, "C", "A", "P");
            makeRow(12, "Foo", "Foo");
            makeRow(13, "attr == $param", "x");
            makeRow(15, "1", "show()", "12E");
            listener.finishSheet();
            Assert.fail("should have failed");
        } catch (DecisionTableParseException e) {
            String badCell = RuleSheetParserUtil.rc2name(15, 3);
            System.err.println(e.getMessage());
            Assert.assertTrue(e.getMessage().contains(badCell));
        }
    }

    /**
     * Must not have snippet for attribute
     */
    @Test
    public void testSnippetForAttribute() {
        try {
            makeRuleSet();
            makeRuleTable();
            makeRow(11, "C", "A", "G");
            makeRow(12, "Foo", "Foo");
            makeRow(13, "attr == $param", "x", "XXX");
            makeRow(15, "1", "show()", "10");
            listener.finishSheet();
            Assert.fail("should have failed");
        } catch (DecisionTableParseException e) {
            String badCell = RuleSheetParserUtil.rc2name(13, 3);
            System.err.println(e.getMessage());
            Assert.assertTrue(e.getMessage().contains(badCell));
        }
    }

    /**
     * Check correct rendering of string-valued attribute
     */
    @Test
    public void testRuleAttributeRendering() {
        makeRuleSet();
        makeRuleTable();
        makeRow(11, "C", "A", "G");
        makeRow(12, "Foo", "Foo");
        makeRow(13, "attr == $param", "x");
        makeRow(15, "1", "show()", "foo bar");
        makeRow(16, "2", "list()", "\"10\" group\"");
        listener.finishSheet();
        Package p = listener.getRuleSet();
        DRLOutput dout = new DRLOutput();
        p.renderDRL(dout);
        String drl = dout.getDRL();
        // System.out.println( drl );
        Assert.assertTrue(drl.contains("agenda-group \"foo bar\""));
        Assert.assertTrue(drl.contains("agenda-group \"10\\\" group\""));
    }

    /**
     * Duplicate package level attribute
     */
    @Test
    public void testDuplicatePackageAttribute() {
        try {
            makeRuleSet();
            makeAttribute("agenda-group", "agroup");// B3, C3

            makeAttribute("agenda-group", "bgroup");// B3. B4

            makeRuleTable();
            makeRow(11, "C", "A", "P");
            makeRow(12, "Foo", "Foo");
            makeRow(13, "attr == $param", "x");
            makeRow(15, "1", "show()", "10");
            listener.finishSheet();
            Package p = listener.getRuleSet();
            DRLOutput dout = new DRLOutput();
            p.renderDRL(dout);
            String drl = dout.getDRL();
            // System.out.println( drl );
            Assert.fail("should have failed");
        } catch (DecisionTableParseException e) {
            System.err.println(e.getMessage());
            Assert.assertTrue(e.getMessage().contains("C3, C4"));
        }
    }

    /**
     * Check correct rendering of package level attributes
     */
    @Test
    public void testPackageAttributeRendering() {
        makeRuleSet();
        makeAttribute("NO-LOOP", "true");
        makeAttribute("agenda-group", "agroup");
        makeRuleTable();
        makeRow(11, "C", "A", "P");
        makeRow(12, "foo:Foo", "foo");
        makeRow(13, "attr == $param", "x($param)");
        makeRow(15, "1", "1", "100");
        listener.finishSheet();
        Package p = listener.getRuleSet();
        DRLOutput dout = new DRLOutput();
        p.renderDRL(dout);
        String drl = dout.getDRL();
        // System.out.println( drl );
        Assert.assertTrue(drl.contains("no-loop true"));
        Assert.assertTrue(drl.contains("agenda-group \"agroup\""));
    }

    /**
     * Must have a code snippet in an action.
     */
    @Test
    public void testMissingCodeSnippetAction() {
        try {
            makeRuleSet();
            makeRuleTable();
            makeRow(11, "C", "A");
            makeRow(12, "foo: Foo", "Bar()");
            makeRow(13, "attr == $param");
            makeRow(15, "1", "1");
            makeRow(16, "2", "2");
            listener.finishSheet();
            Package p = listener.getRuleSet();
            DRLOutput dout = new DRLOutput();
            p.renderDRL(dout);
            String drl = dout.getDRL();
            System.out.println(drl);
            Assert.fail("should have failed");
        } catch (DecisionTableParseException e) {
            String badCell = RuleSheetParserUtil.rc2name(13, 2);
            System.err.println(e.getMessage());
            Assert.assertTrue(e.getMessage().contains(badCell));
        }
    }

    @Test
    public void testMetadata() {
        makeRuleSet();
        makeRuleTable();
        makeRow(11, "C", "A", "@", "@");
        makeRow(12, "foo: Foo", "foo");
        makeRow(13, "attr == $param", "goaway($param)", "Author($param)", "Version($1-$2)");
        makeRow(15, "1", "1", "J.W.Goethe", "3,14");
        makeRow(16, "2", "2", "", "");
        listener.finishSheet();
        Package p = listener.getRuleSet();
        DRLOutput dout = new DRLOutput();
        p.renderDRL(dout);
        String drl = dout.getDRL();
        Assert.assertTrue(drl.contains("@Author(J.W.Goethe)"));
        Assert.assertTrue(drl.contains("@Version(3-14)"));
        Assert.assertFalse(drl.contains("@Author()"));
        Assert.assertFalse(drl.contains("@Version(-)"));
    }

    @Test
    public void testQuoteEscapingEnabled() throws Exception {
        final InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream("/data/QuoteEscapeEnabledWorkbook.xls");
        final RuleSheetListener listener = RuleWorksheetParseTest.getRuleSheetListener(stream);
        final Package ruleset = listener.getRuleSet();
        Assert.assertNotNull(ruleset);
        DRLOutput dout = new DRLOutput();
        ruleset.renderDRL(dout);
        String drl = dout.getDRL();
        System.out.println(drl);
        // check rules
        Rule rule = getRules().get(0);
        Condition cond = rule.getConditions().get(0);
        Assert.assertEquals("Foo(myObject.getColour().equals(red), myObject.size () > 12\\\")", cond.getSnippet());
    }

    @Test
    public void testQuoteEscapingDisabled() throws Exception {
        final InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream("/data/QuoteEscapeDisabledWorkbook.xls");
        final RuleSheetListener listener = RuleWorksheetParseTest.getRuleSheetListener(stream);
        final Package ruleset = listener.getRuleSet();
        Assert.assertNotNull(ruleset);
        DRLOutput dout = new DRLOutput();
        ruleset.renderDRL(dout);
        String drl = dout.getDRL();
        System.out.println(drl);
        // check rules
        Rule rule = ((Rule) (getRules().get(0)));
        Condition cond = ((Condition) (rule.getConditions().get(0)));
        Assert.assertEquals("Foo(myObject.getColour().equals(red), myObject.size () > \"12\")", cond.getSnippet());
        rule = getRules().get(1);
        cond = rule.getConditions().get(0);
        Assert.assertEquals("Foo(myObject.getColour().equals(blue), myObject.size () > 12\")", cond.getSnippet());
    }

    @Test
    public void testSalienceRange() throws Exception {
        // DROOLS-1225
        final InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream("/data/SalienceRangeWorkbook.xls");
        final RuleSheetListener listener = RuleWorksheetParseTest.getRuleSheetListener(stream);
        final Package ruleset = listener.getRuleSet();
        Assert.assertNotNull(ruleset);
        DRLOutput dout = new DRLOutput();
        ruleset.renderDRL(dout);
        String drl = dout.getDRL();
        System.out.println(drl);
        // check rules
        List<Rule> rules = ruleset.getRules();
        Assert.assertEquals("10000", rules.get(0).getSalience());
        Assert.assertEquals("9999", rules.get(1).getSalience());
    }

    @Test
    public void testSalienceOutOfRange() throws Exception {
        // DROOLS-1225
        final InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream("/data/SalienceOutOfRangeWorkbook.xls");
        try {
            final RuleSheetListener listener = RuleWorksheetParseTest.getRuleSheetListener(stream);
            Assert.fail("should have failed");
        } catch (DecisionTableParseException e) {
        }
    }

    /**
     * See if it can cope with odd shaped rule table, including missing
     * conditions. Also is not "sequential".
     */
    @Test
    public void testComplexWorksheetMissingConditionsInLocaleEnUs() throws Exception {
        Locale originalDefaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
        doComplexWorksheetMissingConditions();
        Locale.setDefault(originalDefaultLocale);
    }

    @Test
    public void testNumericDisabled() throws Exception {
        // DROOLS-1378
        final InputStream stream = RuleWorksheetParseTest.class.getResourceAsStream("/data/NumericDisabled.xls");
        final RuleSheetListener listener = RuleWorksheetParseTest.getRuleSheetListener(stream);
        final Package ruleset = listener.getRuleSet();
        Assert.assertNotNull(ruleset);
        DRLOutput dout = new DRLOutput();
        ruleset.renderDRL(dout);
        String drl = dout.getDRL();
        System.out.println(drl);
        // check rules
        Rule rule = ((Rule) (getRules().get(0)));
        Condition cond = ((Condition) (rule.getConditions().get(0)));
        Assert.assertEquals("Cheese(price == 6600)", cond.getSnippet());
    }
}

