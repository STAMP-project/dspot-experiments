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
package org.drools.compiler.compiler.xml.rules;


import java.io.InputStreamReader;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.compiler.xml.XmlPackageReader;
import org.drools.compiler.lang.DrlDumper;
import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.ExistsDescr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.ForallDescr;
import org.drools.compiler.lang.descr.FromDescr;
import org.drools.compiler.lang.descr.FunctionDescr;
import org.drools.compiler.lang.descr.GlobalDescr;
import org.drools.compiler.lang.descr.MVELExprDescr;
import org.drools.compiler.lang.descr.NotDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.QueryDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;


public class XmlPackageReaderTest extends CommonTestMethodBase {
    @Test
    public void testParseFrom() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseFrom.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        Assert.assertNotNull(packageDescr);
        RuleDescr obj = ((RuleDescr) (packageDescr.getRules().get(0)));
        PatternDescr patterndescr = ((PatternDescr) (getDescrs().get(0)));
        FromDescr from = ((FromDescr) (patterndescr.getSource()));
        MVELExprDescr accessordescriptor = ((MVELExprDescr) (from.getDataSource()));
        Assert.assertEquals("cheesery.getCheeses(i+4)", accessordescriptor.getExpression());
        Assert.assertEquals(patterndescr.getObjectType(), "Cheese");
        Assert.assertEquals(patterndescr.getIdentifier(), "cheese");
    }

    @Test
    public void testAccumulate() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseAccumulate.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        Assert.assertNotNull(packageDescr);
        RuleDescr obj = ((RuleDescr) (packageDescr.getRules().get(0)));
        Object patternobj = getDescrs().get(0);
        Assert.assertTrue((patternobj instanceof PatternDescr));
        final PatternDescr patterncheese = ((PatternDescr) (patternobj));
        Assert.assertEquals(patterncheese.getIdentifier(), "cheese");
        Assert.assertEquals(patterncheese.getObjectType(), "Cheese");
        AccumulateDescr accumulatedescr = ((AccumulateDescr) (patterncheese.getSource()));
        Assert.assertEquals("total += $cheese.getPrice();", accumulatedescr.getActionCode());
        Assert.assertEquals("int total = 0;", accumulatedescr.getInitCode());
        Assert.assertEquals("new Integer( total ) );", accumulatedescr.getResultCode());
        patternobj = getDescrs().get(1);
        Assert.assertTrue((patternobj instanceof PatternDescr));
        final PatternDescr patternmax = ((PatternDescr) (patternobj));
        Assert.assertEquals(patternmax.getIdentifier(), "max");
        Assert.assertEquals(patternmax.getObjectType(), "Number");
        accumulatedescr = ((AccumulateDescr) (patternmax.getSource()));
        Assert.assertTrue(accumulatedescr.isExternalFunction());
        Assert.assertEquals("max", accumulatedescr.getFunctions().get(0).getFunction());
        Assert.assertNull(accumulatedescr.getInitCode());
        Assert.assertNull(accumulatedescr.getActionCode());
        Assert.assertNull(accumulatedescr.getResultCode());
        Assert.assertNull(accumulatedescr.getReverseCode());
    }

    @Test
    public void testAccumulateMultiPattern() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseAccumulate.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        Assert.assertNotNull(packageDescr);
        RuleDescr obj = ((RuleDescr) (packageDescr.getRules().get(1)));
        Object patternobj = getDescrs().get(0);
        Assert.assertTrue((patternobj instanceof PatternDescr));
        final PatternDescr patterncheese = ((PatternDescr) (patternobj));
        Assert.assertEquals(patterncheese.getIdentifier(), "cheese");
        Assert.assertEquals(patterncheese.getObjectType(), "Cheese");
        AccumulateDescr accumulatedescr = ((AccumulateDescr) (patterncheese.getSource()));
        Assert.assertEquals("total += $cheese.getPrice();", accumulatedescr.getActionCode());
        Assert.assertEquals("int total = 0;", accumulatedescr.getInitCode());
        Assert.assertEquals("new Integer( total ) );", accumulatedescr.getResultCode());
        AndDescr anddescr = ((AndDescr) (accumulatedescr.getInput()));
        List descrlist = anddescr.getDescrs();
        PatternDescr[] listpattern = ((PatternDescr[]) (descrlist.toArray(new PatternDescr[descrlist.size()])));
        Assert.assertEquals(getObjectType(), "Milk");
        Assert.assertEquals(getObjectType(), "Cup");
    }

    @Test
    public void testParseForall() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseForall.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        Assert.assertNotNull(packageDescr);
        RuleDescr obj = ((RuleDescr) (packageDescr.getRules().get(0)));
        ForallDescr forall = ((ForallDescr) (getDescrs().get(0)));
        List forallPaterns = forall.getDescrs();
        PatternDescr pattarnState = ((PatternDescr) (forallPaterns.get(0)));
        PatternDescr personState = ((PatternDescr) (forallPaterns.get(1)));
        PatternDescr cheeseState = ((PatternDescr) (forallPaterns.get(2)));
        Assert.assertEquals(pattarnState.getObjectType(), "State");
        Assert.assertEquals(personState.getObjectType(), "Person");
        Assert.assertEquals(cheeseState.getObjectType(), "Cheese");
    }

    @Test
    public void testParseExists() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseExists.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        Assert.assertNotNull(packageDescr);
        RuleDescr obj = ((RuleDescr) (packageDescr.getRules().get(0)));
        Object existdescr = getDescrs().get(0);
        Assert.assertTrue((existdescr instanceof ExistsDescr));
        Object patternDescriptor = getDescrs().get(0);
        Assert.assertTrue((patternDescriptor instanceof PatternDescr));
        Assert.assertEquals(getObjectType(), "Person");
        Object notDescr = getDescrs().get(1);
        Assert.assertEquals(notDescr.getClass().getName(), NotDescr.class.getName());
        existdescr = getDescrs().get(0);
        patternDescriptor = getDescrs().get(0);
        Assert.assertTrue((patternDescriptor instanceof PatternDescr));
        Assert.assertEquals(getObjectType(), "Cheese");
    }

    @Test
    public void testParseCollect() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseCollect.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        String expected = StringUtils.readFileAsString(new InputStreamReader(getClass().getResourceAsStream("test_ParseCollect.drl")));
        String expectedWithoutHeader = removeLicenseHeader(expected);
        String actual = new DrlDumper().dump(packageDescr);
        Assertions.assertThat(expectedWithoutHeader).isEqualToIgnoringWhitespace(actual);
    }

    @Test
    public void testParsePackageName() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParsePackageName.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        Assert.assertNotNull(packageDescr);
        Assert.assertEquals("com.sample", packageDescr.getName());
    }

    @Test
    public void testParseImport() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseImport.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        Assert.assertNotNull(packageDescr);
        Assert.assertEquals("com.sample", packageDescr.getName());
        final List imports = packageDescr.getImports();
        Assert.assertEquals(2, imports.size());
        Assert.assertEquals("java.util.HashMap", getTarget());
        Assert.assertEquals("org.drools.compiler.*", getTarget());
        final List functionImport = packageDescr.getFunctionImports();
        Assert.assertEquals("org.drools.function", getTarget());
    }

    @Test
    public void testParseGlobal() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseGlobal.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        Assert.assertNotNull(packageDescr);
        Assert.assertEquals("com.sample", packageDescr.getName());
        final List imports = packageDescr.getImports();
        Assert.assertEquals(2, imports.size());
        Assert.assertEquals("java.util.HashMap", getTarget());
        Assert.assertEquals("org.drools.compiler.*", getTarget());
        final List globals = packageDescr.getGlobals();
        Assert.assertEquals(2, globals.size());
        final GlobalDescr x = ((GlobalDescr) (globals.get(0)));
        final GlobalDescr yada = ((GlobalDescr) (globals.get(1)));
        Assert.assertEquals("com.sample.X", x.getType());
        Assert.assertEquals("x", x.getIdentifier());
        Assert.assertEquals("com.sample.Yada", yada.getType());
        Assert.assertEquals("yada", yada.getIdentifier());
    }

    @Test
    public void testParseFunction() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseFunction.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        Assert.assertNotNull(packageDescr);
        Assert.assertEquals("com.sample", packageDescr.getName());
        final List imports = packageDescr.getImports();
        Assert.assertEquals(2, imports.size());
        Assert.assertEquals("java.util.HashMap", getTarget());
        Assert.assertEquals("org.drools.compiler.*", getTarget());
        final List globals = packageDescr.getGlobals();
        Assert.assertEquals(2, globals.size());
        final GlobalDescr x = ((GlobalDescr) (globals.get(0)));
        final GlobalDescr yada = ((GlobalDescr) (globals.get(1)));
        Assert.assertEquals("com.sample.X", x.getType());
        Assert.assertEquals("x", x.getIdentifier());
        Assert.assertEquals("com.sample.Yada", yada.getType());
        Assert.assertEquals("yada", yada.getIdentifier());
        final FunctionDescr functionDescr = ((FunctionDescr) (packageDescr.getFunctions().get(0)));
        final List names = functionDescr.getParameterNames();
        Assert.assertEquals("foo", names.get(0));
        Assert.assertEquals("bada", names.get(1));
        final List types = functionDescr.getParameterTypes();
        Assert.assertEquals("Bar", types.get(0));
        Assert.assertEquals("Bing", types.get(1));
        Assert.assertEquals("System.out.println(\"hello world\");", functionDescr.getText().trim());
    }

    @Test
    public void testParseRule() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseRule.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        String expected = StringUtils.readFileAsString(new InputStreamReader(getClass().getResourceAsStream("test_ParseRule.drl")));
        // remove license header as that one is not stored in the XML
        String expectedWithoutHeader = removeLicenseHeader(expected);
        System.out.println(expectedWithoutHeader);
        String actual = new DrlDumper().dump(packageDescr);
        Assertions.assertThat(expectedWithoutHeader).isEqualToIgnoringWhitespace(actual);
    }

    @Test
    public void testParseSimpleRule() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_SimpleRule1.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        Assert.assertNotNull(packageDescr);
        Assert.assertEquals("com.sample", packageDescr.getName());
        final List imports = packageDescr.getImports();
        Assert.assertEquals(2, imports.size());
        Assert.assertEquals("java.util.List", getTarget());
        Assert.assertEquals("org.drools.compiler.Person", getTarget());
        RuleDescr ruleDescr = ((RuleDescr) (packageDescr.getRules().get(0)));
        Assert.assertEquals("simple_rule1", ruleDescr.getName());
        AndDescr lhs = ruleDescr.getLhs();
        PatternDescr patternDescr = ((PatternDescr) (lhs.getDescrs().get(0)));
        Assert.assertEquals("Person", patternDescr.getObjectType());
        ExprConstraintDescr expr = ((ExprConstraintDescr) (getDescrs().get(0)));
        Assert.assertEquals("name == \"darth\"", expr.getExpression());
        ruleDescr = ((RuleDescr) (packageDescr.getRules().get(1)));
        Assert.assertEquals("simple_rule2", ruleDescr.getName());
        lhs = ruleDescr.getLhs();
        patternDescr = ((PatternDescr) (lhs.getDescrs().get(0)));
        Assert.assertEquals("Person", patternDescr.getObjectType());
        expr = ((ExprConstraintDescr) (getDescrs().get(0)));
        Assert.assertEquals("age == 35 || == -3.5", expr.getExpression());
        ruleDescr = ((RuleDescr) (packageDescr.getRules().get(2)));
        Assert.assertEquals("simple_rule3", ruleDescr.getName());
        lhs = ruleDescr.getLhs();
        patternDescr = ((PatternDescr) (lhs.getDescrs().get(0)));
        Assert.assertEquals("Person", patternDescr.getObjectType());
        expr = ((ExprConstraintDescr) (getDescrs().get(0)));
        Assert.assertEquals("age == 35 || (!= 7.0 && != -70)", expr.getExpression());
        ruleDescr = ((RuleDescr) (packageDescr.getRules().get(3)));
        Assert.assertEquals("simple_rule3", ruleDescr.getName());
        lhs = ruleDescr.getLhs();
        patternDescr = ((PatternDescr) (lhs.getDescrs().get(1)));
        Assert.assertEquals("Person", patternDescr.getObjectType());
        expr = ((ExprConstraintDescr) (getDescrs().get(0)));
        Assert.assertEquals("name == $s", expr.getExpression());
        ruleDescr = ((RuleDescr) (packageDescr.getRules().get(4)));
        Assert.assertEquals("simple_rule4", ruleDescr.getName());
        lhs = ruleDescr.getLhs();
        patternDescr = ((PatternDescr) (lhs.getDescrs().get(1)));
        Assert.assertEquals("Person", patternDescr.getObjectType());
        expr = ((ExprConstraintDescr) (getDescrs().get(0)));
        Assert.assertEquals("(name == $s) || (age == 35 || (!= 7.0 && != -70))", expr.getExpression());
        ruleDescr = ((RuleDescr) (packageDescr.getRules().get(5)));
        Assert.assertEquals("simple_rule5", ruleDescr.getName());
        lhs = ruleDescr.getLhs();
        patternDescr = ((PatternDescr) (lhs.getDescrs().get(1)));
        Assert.assertEquals("Person", patternDescr.getObjectType());
        expr = ((ExprConstraintDescr) (getDescrs().get(0)));
        Assert.assertEquals("(name == $s) || ((age != 34) && (age != 37) && (name != \"yoda\"))", expr.getExpression());
    }

    @Test
    public void testParseLhs() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseLhs.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        String expected = StringUtils.readFileAsString(new InputStreamReader(getClass().getResourceAsStream("test_ParseLhs.drl")));
        String expectedWithoutHeader = removeLicenseHeader(expected);
        String actual = new DrlDumper().dump(packageDescr);
        Assertions.assertThat(expectedWithoutHeader).isEqualToIgnoringWhitespace(actual);
    }

    @Test
    public void testParseRhs() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseRhs.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        Assert.assertNotNull(packageDescr);
        Assert.assertEquals("com.sample", packageDescr.getName());
        final List imports = packageDescr.getImports();
        Assert.assertEquals(2, imports.size());
        Assert.assertEquals("java.util.HashMap", getTarget());
        Assert.assertEquals("org.drools.compiler.*", getTarget());
        final List globals = packageDescr.getGlobals();
        Assert.assertEquals(2, globals.size());
        final GlobalDescr x = ((GlobalDescr) (globals.get(0)));
        final GlobalDescr yada = ((GlobalDescr) (globals.get(1)));
        Assert.assertEquals("com.sample.X", x.getType());
        Assert.assertEquals("x", x.getIdentifier());
        Assert.assertEquals("com.sample.Yada", yada.getType());
        Assert.assertEquals("yada", yada.getIdentifier());
        final FunctionDescr functionDescr = ((FunctionDescr) (packageDescr.getFunctions().get(0)));
        final List names = functionDescr.getParameterNames();
        Assert.assertEquals("foo", names.get(0));
        Assert.assertEquals("bada", names.get(1));
        final List types = functionDescr.getParameterTypes();
        Assert.assertEquals("Bar", types.get(0));
        Assert.assertEquals("Bing", types.get(1));
        Assert.assertEquals("System.out.println(\"hello world\");", functionDescr.getText().trim());
        final RuleDescr ruleDescr = ((RuleDescr) (packageDescr.getRules().get(0)));
        Assert.assertEquals("my rule", ruleDescr.getName());
        final String consequence = ((String) (ruleDescr.getConsequence()));
        Assert.assertNotNull(consequence);
        Assert.assertEquals("System.out.println( \"hello\" );", consequence.trim());
    }

    @Test
    public void testParseQuery() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseQuery.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        Assert.assertNotNull(packageDescr);
        Assert.assertEquals("com.sample", packageDescr.getName());
        final List imports = packageDescr.getImports();
        Assert.assertEquals(2, imports.size());
        Assert.assertEquals("java.util.HashMap", getTarget());
        Assert.assertEquals("org.drools.compiler.*", getTarget());
        final List globals = packageDescr.getGlobals();
        Assert.assertEquals(2, globals.size());
        final GlobalDescr x = ((GlobalDescr) (globals.get(0)));
        final GlobalDescr yada = ((GlobalDescr) (globals.get(1)));
        Assert.assertEquals("com.sample.X", x.getType());
        Assert.assertEquals("x", x.getIdentifier());
        Assert.assertEquals("com.sample.Yada", yada.getType());
        Assert.assertEquals("yada", yada.getIdentifier());
        final FunctionDescr functionDescr = ((FunctionDescr) (packageDescr.getFunctions().get(0)));
        final List names = functionDescr.getParameterNames();
        Assert.assertEquals("foo", names.get(0));
        Assert.assertEquals("bada", names.get(1));
        final List types = functionDescr.getParameterTypes();
        Assert.assertEquals("Bar", types.get(0));
        Assert.assertEquals("Bing", types.get(1));
        Assert.assertEquals("System.out.println(\"hello world\");", functionDescr.getText().trim());
        final QueryDescr queryDescr = ((QueryDescr) (packageDescr.getRules().get(0)));
        Assert.assertEquals("my query", queryDescr.getName());
        final AndDescr lhs = queryDescr.getLhs();
        Assert.assertEquals(1, lhs.getDescrs().size());
        final PatternDescr patternDescr = ((PatternDescr) (lhs.getDescrs().get(0)));
        Assert.assertEquals("Foo", patternDescr.getObjectType());
    }
}

