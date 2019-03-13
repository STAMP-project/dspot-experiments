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
package org.drools.compiler.lang;


import DRL6Lexer.LEFT_PAREN;
import DRL6Lexer.RIGHT_PAREN;
import ExprConstraintDescr.Type.NAMED;
import ExprConstraintDescr.Type.POSITIONAL;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import junit.framework.TestCase;
import org.antlr.runtime.ANTLRStringStream;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.AccumulateDescr.AccumulateFunctionCallDescr;
import org.drools.compiler.lang.descr.AccumulateImportDescr;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.AttributeDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.BehaviorDescr;
import org.drools.compiler.lang.descr.CollectDescr;
import org.drools.compiler.lang.descr.EntryPointDeclarationDescr;
import org.drools.compiler.lang.descr.EntryPointDescr;
import org.drools.compiler.lang.descr.EvalDescr;
import org.drools.compiler.lang.descr.ExistsDescr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.ForallDescr;
import org.drools.compiler.lang.descr.FromDescr;
import org.drools.compiler.lang.descr.FunctionDescr;
import org.drools.compiler.lang.descr.GlobalDescr;
import org.drools.compiler.lang.descr.ImportDescr;
import org.drools.compiler.lang.descr.MVELExprDescr;
import org.drools.compiler.lang.descr.NotDescr;
import org.drools.compiler.lang.descr.OrDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.QueryDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeFieldDescr;
import org.drools.compiler.lang.descr.WindowDeclarationDescr;
import org.junit.Test;
import org.kie.internal.builder.conf.LanguageLevelOption;


public class RuleParserTest extends TestCase {
    private DRLParser parser;

    @Test
    public void testPackage_OneSegment() throws Exception {
        final String packageName = ((String) (parse("packageStatement", "package foo")));
        TestCase.assertEquals("foo", packageName);
    }

    @Test
    public void testPackage_MultipleSegments() throws Exception {
        final String packageName = ((String) (parse("packageStatement", "package foo.bar.baz;")));
        TestCase.assertEquals("foo.bar.baz", packageName);
    }

    @Test
    public void testPackage() throws Exception {
        final String source = "package foo.bar.baz";
        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL6);
        final PackageDescr pkg = parser.parse(new StringReader(source));
        TestCase.assertFalse(parser.hasErrors());
        TestCase.assertEquals("foo.bar.baz", pkg.getName());
    }

    @Test
    public void testPackageWithError() throws Exception {
        final String source = "package 12 foo.bar.baz";
        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL6);
        final PackageDescr pkg = parser.parse(true, new StringReader(source));
        TestCase.assertTrue(parser.hasErrors());
        TestCase.assertEquals("foo.bar.baz", pkg.getName());
    }

    @Test
    public void testPackageWithError2() throws Exception {
        final String source = "package 12 12312 231";
        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL6);
        final PackageDescr pkg = parser.parse(true, new StringReader(source));
        TestCase.assertTrue(parser.hasErrors());
        TestCase.assertEquals("", pkg.getName());
    }

    @Test
    public void testCompilationUnit() throws Exception {
        final String source = "package foo; import com.foo.Bar; import com.foo.Baz;";
        PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", source)));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        TestCase.assertEquals("foo", pkg.getName());
        TestCase.assertEquals(2, pkg.getImports().size());
        ImportDescr impdescr = pkg.getImports().get(0);
        TestCase.assertEquals("com.foo.Bar", impdescr.getTarget());
        TestCase.assertEquals(source.indexOf(("import " + (impdescr.getTarget()))), impdescr.getStartCharacter());
        TestCase.assertEquals(((source.indexOf(("import " + (impdescr.getTarget())))) + (("import " + (impdescr.getTarget())).length())), impdescr.getEndCharacter());
        impdescr = pkg.getImports().get(1);
        TestCase.assertEquals("com.foo.Baz", impdescr.getTarget());
        TestCase.assertEquals(source.indexOf(("import " + (impdescr.getTarget()))), impdescr.getStartCharacter());
        TestCase.assertEquals(((source.indexOf(("import " + (impdescr.getTarget())))) + (("import " + (impdescr.getTarget())).length())), impdescr.getEndCharacter());
    }

    @Test
    public void testFunctionImport() throws Exception {
        final String source = "package foo\n" + ((("import function java.lang.Math.max\n" + "import function java.lang.Math.min;\n") + "import foo.bar.*\n") + "import baz.Baz");
        PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", source)));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        TestCase.assertEquals("foo", pkg.getName());
        TestCase.assertEquals(2, pkg.getImports().size());
        ImportDescr impdescr = pkg.getImports().get(0);
        TestCase.assertEquals("foo.bar.*", impdescr.getTarget());
        TestCase.assertEquals(source.indexOf(("import " + (impdescr.getTarget()))), impdescr.getStartCharacter());
        TestCase.assertEquals(((source.indexOf(("import " + (impdescr.getTarget())))) + (("import " + (impdescr.getTarget())).length())), impdescr.getEndCharacter());
        impdescr = pkg.getImports().get(1);
        TestCase.assertEquals("baz.Baz", impdescr.getTarget());
        TestCase.assertEquals(source.indexOf(("import " + (impdescr.getTarget()))), impdescr.getStartCharacter());
        TestCase.assertEquals(((source.indexOf(("import " + (impdescr.getTarget())))) + (("import " + (impdescr.getTarget())).length())), impdescr.getEndCharacter());
        TestCase.assertEquals(2, pkg.getFunctionImports().size());
        impdescr = pkg.getFunctionImports().get(0);
        TestCase.assertEquals("java.lang.Math.max", impdescr.getTarget());
        TestCase.assertEquals(source.indexOf(("import function " + (impdescr.getTarget()))), impdescr.getStartCharacter());
        TestCase.assertEquals(((source.indexOf(("import function " + (impdescr.getTarget())))) + (("import function " + (impdescr.getTarget())).length())), impdescr.getEndCharacter());
        impdescr = pkg.getFunctionImports().get(1);
        TestCase.assertEquals("java.lang.Math.min", impdescr.getTarget());
        TestCase.assertEquals(source.indexOf(("import function " + (impdescr.getTarget()))), impdescr.getStartCharacter());
        TestCase.assertEquals(((source.indexOf(("import function " + (impdescr.getTarget())))) + (("import function " + (impdescr.getTarget())).length())), impdescr.getEndCharacter());
    }

    @Test
    public void testGlobal1() throws Exception {
        final String source = "package foo.bar.baz\n" + (("import com.foo.Bar\n" + "global java.util.List<java.util.Map<String,Integer>> aList;\n") + "global Integer aNumber");
        PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", source)));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        TestCase.assertEquals("foo.bar.baz", pkg.getName());
        TestCase.assertEquals(1, pkg.getImports().size());
        ImportDescr impdescr = pkg.getImports().get(0);
        TestCase.assertEquals("com.foo.Bar", impdescr.getTarget());
        TestCase.assertEquals(source.indexOf(("import " + (impdescr.getTarget()))), impdescr.getStartCharacter());
        TestCase.assertEquals(((source.indexOf(("import " + (impdescr.getTarget())))) + (("import " + (impdescr.getTarget())).length())), impdescr.getEndCharacter());
        TestCase.assertEquals(2, pkg.getGlobals().size());
        GlobalDescr global = pkg.getGlobals().get(0);
        TestCase.assertEquals("java.util.List<java.util.Map<String,Integer>>", global.getType());
        TestCase.assertEquals("aList", global.getIdentifier());
        TestCase.assertEquals(source.indexOf(("global " + (global.getType()))), global.getStartCharacter());
        TestCase.assertEquals(((source.indexOf(((("global " + (global.getType())) + " ") + (global.getIdentifier())))) + (((("global " + (global.getType())) + " ") + (global.getIdentifier())).length())), global.getEndCharacter());
        global = pkg.getGlobals().get(1);
        TestCase.assertEquals("Integer", global.getType());
        TestCase.assertEquals("aNumber", global.getIdentifier());
        TestCase.assertEquals(source.indexOf(("global " + (global.getType()))), global.getStartCharacter());
        TestCase.assertEquals(((source.indexOf(((("global " + (global.getType())) + " ") + (global.getIdentifier())))) + (((("global " + (global.getType())) + " ") + (global.getIdentifier())).length())), global.getEndCharacter());
    }

    @Test
    public void testGlobal() throws Exception {
        PackageDescr pack = ((PackageDescr) (parseResource("compilationUnit", "globals.drl")));
        TestCase.assertEquals(1, pack.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pack.getRules().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
        TestCase.assertEquals(1, pack.getImports().size());
        TestCase.assertEquals(2, pack.getGlobals().size());
        final GlobalDescr foo = ((GlobalDescr) (pack.getGlobals().get(0)));
        TestCase.assertEquals("java.lang.String", foo.getType());
        TestCase.assertEquals("foo", foo.getIdentifier());
        final GlobalDescr bar = ((GlobalDescr) (pack.getGlobals().get(1)));
        TestCase.assertEquals("java.lang.Integer", bar.getType());
        TestCase.assertEquals("bar", bar.getIdentifier());
    }

    @Test
    public void testFunctionImport2() throws Exception {
        PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "test_FunctionImport.drl")));
        TestCase.assertEquals(2, pkg.getFunctionImports().size());
        TestCase.assertEquals("abd.def.x", getTarget());
        TestCase.assertFalse(((getStartCharacter()) == (-1)));
        TestCase.assertFalse(((getEndCharacter()) == (-1)));
        TestCase.assertEquals("qed.wah.*", getTarget());
        TestCase.assertFalse(((getStartCharacter()) == (-1)));
        TestCase.assertFalse(((getEndCharacter()) == (-1)));
    }

    @Test
    public void testFromComplexAcessor() throws Exception {
        String source = "rule \"Invalid customer id\" ruleflow-group \"validate\" lock-on-active true \n" + ((((((" when \n" + "     o: Order( ) \n") + "     not( Customer( ) from customerService.getCustomer(o.getCustomerId()) ) \n") + " then \n") + "     System.err.println(\"Invalid customer id found!\"); \n") + "     o.addError(\"Invalid customer id\"); \n") + "end \n");
        PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", source)));
        TestCase.assertFalse(parser.getErrorMessages().toString(), parser.hasErrors());
        RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals("Invalid customer id", rule.getName());
        TestCase.assertEquals(2, getDescrs().size());
        NotDescr not = ((NotDescr) (getDescrs().get(1)));
        PatternDescr customer = ((PatternDescr) (not.getDescrs().get(0)));
        TestCase.assertEquals("Customer", customer.getObjectType());
        TestCase.assertEquals("customerService.getCustomer(o.getCustomerId())", getDataSource().getText());
    }

    @Test
    public void testFromWithInlineList() throws Exception {
        String source = "rule XYZ \n" + ((((((" when \n" + " o: Order( ) \n") + " not( Number( ) from [1, 2, 3] ) \n") + " then \n") + " System.err.println(\"Invalid customer id found!\"); \n") + " o.addError(\"Invalid customer id\"); \n") + "end \n");
        PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", source)));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals("XYZ", rule.getName());
        PatternDescr number = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("[1, 2, 3]", getDataSource().toString());
    }

    @Test
    public void testFromWithInlineListMethod() throws Exception {
        String source = "rule XYZ \n" + ((((((" when \n" + " o: Order( ) \n") + " Number( ) from [1, 2, 3].sublist(1, 2) \n") + " then \n") + " System.err.println(\"Invalid customer id found!\"); \n") + " o.addError(\"Invalid customer id\"); \n") + "end \n");
        PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", source)));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals("XYZ", rule.getName());
        TestCase.assertFalse(parser.hasErrors());
        PatternDescr number = ((PatternDescr) (getDescrs().get(1)));
        TestCase.assertEquals("[1, 2, 3].sublist(1, 2)", getDataSource().toString());
    }

    @Test
    public void testFromWithInlineListIndex() throws Exception {
        String source = "rule XYZ \n" + ((((((" when \n" + " o: Order( ) \n") + " Number( ) from [1, 2, 3][1] \n") + " then \n") + " System.err.println(\"Invalid customer id found!\"); \n") + " o.addError(\"Invalid customer id\"); \n") + "end \n");
        PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", source)));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals("XYZ", rule.getName());
        TestCase.assertFalse(parser.hasErrors());
        PatternDescr number = ((PatternDescr) (getDescrs().get(1)));
        TestCase.assertEquals("[1, 2, 3][1]", getDataSource().toString());
    }

    @Test
    public void testRuleWithoutEnd() throws Exception {
        String source = "rule \"Invalid customer id\" \n" + (((" when \n" + " o: Order( ) \n") + " then \n") + " System.err.println(\"Invalid customer id found!\"); \n");
        parse("compilationUnit", source);
        TestCase.assertTrue(parser.hasErrors());
    }

    @Test
    public void testOrWithSpecialBind() throws Exception {
        String source = "rule \"A and (B or C or D)\" \n" + ((((((("    when \n" + "        pdo1 : ParametricDataObject( paramID == 101, stringValue == \"1000\" ) and \n") + "        pdo2 :(ParametricDataObject( paramID == 101, stringValue == \"1001\" ) or \n") + "               ParametricDataObject( paramID == 101, stringValue == \"1002\" ) or \n") + "               ParametricDataObject( paramID == 101, stringValue == \"1003\" )) \n") + "    then \n") + "        System.out.println( \"Rule: A and (B or C or D) Fired. pdo1: \" + pdo1 +  \" pdo2: \"+ pdo2); \n") + "end\n");
        PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", source)));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        RuleDescr rule = pkg.getRules().get(0);
        AndDescr lhs = rule.getLhs();
        TestCase.assertEquals(2, lhs.getDescrs().size());
        PatternDescr pdo1 = ((PatternDescr) (lhs.getDescrs().get(0)));
        TestCase.assertEquals("pdo1", pdo1.getIdentifier());
        OrDescr or = ((OrDescr) (getDescrs().get(1)));
        TestCase.assertEquals(3, or.getDescrs().size());
        for (BaseDescr pdo2 : or.getDescrs()) {
            TestCase.assertEquals("pdo2", getIdentifier());
        }
    }

    @Test
    public void testCompatibleRestriction() throws Exception {
        String source = "package com.sample  rule test  when  Test( ( text == null || text2 matches \"\" ) )  then  end";
        PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", source)));
        TestCase.assertEquals("com.sample", pkg.getName());
        RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals("test", rule.getName());
        ExprConstraintDescr expr = ((ExprConstraintDescr) (getDescrs().get(0)));
        TestCase.assertEquals("( text == null || text2 matches \"\" )", expr.getText());
    }

    @Test
    public void testSimpleConstraint() throws Exception {
        String source = "package com.sample  rule test  when  Cheese( type == 'stilton', price > 10 )  then  end";
        PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", source)));
        TestCase.assertEquals("com.sample", pkg.getName());
        RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals("test", rule.getName());
        TestCase.assertEquals(1, getDescrs().size());
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        AndDescr constraint = ((AndDescr) (pattern.getConstraint()));
        TestCase.assertEquals(2, constraint.getDescrs().size());
        TestCase.assertEquals("type == \"stilton\"", constraint.getDescrs().get(0).toString());
        TestCase.assertEquals("price > 10", constraint.getDescrs().get(1).toString());
    }

    @Test
    public void testStringEscapes() throws Exception {
        String source = "package com.sample  rule test  when  Cheese( type matches \"\\..*\\\\.\" )  then  end";
        PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", source)));
        TestCase.assertEquals("com.sample", pkg.getName());
        RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals("test", rule.getName());
        TestCase.assertEquals(1, getDescrs().size());
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        AndDescr constraint = ((AndDescr) (pattern.getConstraint()));
        TestCase.assertEquals(1, constraint.getDescrs().size());
        TestCase.assertEquals("type matches \"\\..*\\\\.\"", constraint.getDescrs().get(0).toString());
    }

    @Test
    public void testDialect() throws Exception {
        final String source = "dialect 'mvel'";
        PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", source)));
        AttributeDescr attr = ((AttributeDescr) (pkg.getAttributes().get(0)));
        TestCase.assertEquals("dialect", attr.getName());
        TestCase.assertEquals("mvel", attr.getValue());
    }

    @Test
    public void testDialect2() throws Exception {
        final String source = "dialect \"mvel\"";
        PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", source)));
        AttributeDescr attr = pkg.getAttributes().get(0);
        TestCase.assertEquals("dialect", attr.getName());
        TestCase.assertEquals("mvel", attr.getValue());
    }

    @Test
    public void testEmptyRule() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "empty_rule.drl")));
        TestCase.assertNotNull(rule);
        TestCase.assertEquals("empty", rule.getName());
        TestCase.assertNotNull(rule.getLhs());
        TestCase.assertNotNull(rule.getConsequence());
    }

    @Test
    public void testKeywordCollisions() throws Exception {
        PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "eol_funny_business.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        TestCase.assertEquals(1, pkg.getRules().size());
    }

    @Test
    public void testTernaryExpression() throws Exception {
        PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "ternary_expression.drl")));
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(1, pkg.getRules().size());
        assertEqualsIgnoreWhitespace("if (speed > speedLimit ? true : false;) pullEmOver();", ((String) (rule.getConsequence())));
    }

    // public void FIXME_testLatinChars() throws Exception {
    // final DrlParser parser = new DrlParser();
    // final Reader drl = new InputStreamReader( this.getClass().getResourceAsStream( "latin-sample.dslr" ) );
    // final Reader dsl = new InputStreamReader( this.getClass().getResourceAsStream( "latin.dsl" ) );
    // 
    // final PackageDescr pkg = parser.parse( drl,
    // dsl );
    // 
    // // MN: will get some errors due to the char encoding on my FC5 install
    // // others who use the right encoding may not see this, feel free to
    // // uncomment
    // // the following assertion.
    // assertFalse( parser.hasErrors() );
    // 
    // assertEquals( "br.com.auster.drools.sample",
    // pkg.getName() );
    // assertEquals( 1,
    // pkg.getRules().size() );
    // 
    // }
    // 
    @Test
    public void testFunctionWithArrays() throws Exception {
        PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "function_arrays.drl")));
        TestCase.assertEquals("foo", pkg.getName());
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        assertEqualsIgnoreWhitespace("yourFunction(new String[3] {\"a\",\"b\",\"c\"});", ((String) (rule.getConsequence())));
        final FunctionDescr func = ((FunctionDescr) (pkg.getFunctions().get(0)));
        TestCase.assertEquals("String[]", func.getReturnType());
        TestCase.assertEquals("args[]", func.getParameterNames().get(0));
        TestCase.assertEquals("String", func.getParameterTypes().get(0));
    }

    @Test
    public void testAlmostEmptyRule() throws Exception {
        PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "almost_empty_rule.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        TestCase.assertNotNull(pkg);
        RuleDescr rule = pkg.getRules().get(0);
        TestCase.assertEquals("almost_empty", rule.getName());
        TestCase.assertNotNull(rule.getLhs());
        TestCase.assertEquals("", ((String) (rule.getConsequence())).trim());
    }

    @Test
    public void testQuotedStringNameRule() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "quoted_string_name_rule.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        TestCase.assertNotNull(rule);
        TestCase.assertEquals("quoted string name", rule.getName());
        TestCase.assertNotNull(rule.getLhs());
        TestCase.assertEquals("", ((String) (rule.getConsequence())).trim());
    }

    @Test
    public void testNoLoop() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "no-loop.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        TestCase.assertNotNull(rule);
        TestCase.assertEquals("rule1", rule.getName());
        final AttributeDescr att = ((AttributeDescr) (rule.getAttributes().get("no-loop")));
        TestCase.assertEquals("false", att.getValue());
        TestCase.assertEquals("no-loop", att.getName());
    }

    @Test
    public void testAutofocus() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "autofocus.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        TestCase.assertNotNull(rule);
        TestCase.assertEquals("rule1", rule.getName());
        final AttributeDescr att = ((AttributeDescr) (rule.getAttributes().get("auto-focus")));
        TestCase.assertEquals("true", att.getValue());
        TestCase.assertEquals("auto-focus", att.getName());
    }

    @Test
    public void testRuleFlowGroup() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "ruleflowgroup.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        TestCase.assertNotNull(rule);
        TestCase.assertEquals("rule1", rule.getName());
        final AttributeDescr att = ((AttributeDescr) (rule.getAttributes().get("ruleflow-group")));
        TestCase.assertEquals("a group", att.getValue());
        TestCase.assertEquals("ruleflow-group", att.getName());
    }

    @Test
    public void testConsequenceWithDeclaration() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "declaration-in-consequence.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        TestCase.assertNotNull(rule);
        TestCase.assertEquals("myrule", rule.getName());
        final String expected = "int i = 0; i = 1; i / 1; i == 1; i(i); i = \'i\'; i.i.i; i\\i; i<i; i>i; i=\"i\";  ++i;" + ((((("i++; --i; i--; i += i; i -= i; i *= i; i /= i;" + "int i = 5;") + "for(int j; j<i; ++j) {") + "System.out.println(j);}") + "Object o = new String(\"Hello\");") + "String s = (String) o;");
        assertEqualsIgnoreWhitespace(expected, ((String) (rule.getConsequence())));
        TestCase.assertTrue(((((String) (rule.getConsequence())).indexOf("++")) > 0));
        TestCase.assertTrue(((((String) (rule.getConsequence())).indexOf("--")) > 0));
        TestCase.assertTrue(((((String) (rule.getConsequence())).indexOf("+=")) > 0));
        TestCase.assertTrue(((((String) (rule.getConsequence())).indexOf("==")) > 0));
        // System.out.println(( String ) rule.getConsequence());
        // note, need to assert that "i++" is preserved as is, no extra spaces.
    }

    @Test
    public void testRuleParseLhs() throws Exception {
        final String text = "rule X when Person(age < 42, location==\"atlanta\") \nor\nPerson(name==\"bob\") then end";
        RuleDescr rule = ((RuleDescr) (parse("rule", text)));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        TestCase.assertNotNull(rule);
        AndDescr lhs = rule.getLhs();
        TestCase.assertEquals(1, lhs.getDescrs().size());
        TestCase.assertEquals(2, getDescrs().size());
    }

    @Test
    public void testRuleParseLhsWithStringQuotes() throws Exception {
        final String text = "rule X when Person( location==\"atlanta\\\"\") then end\n";
        RuleDescr rule = ((RuleDescr) (parse("rule", text)));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        TestCase.assertNotNull(rule);
        AndDescr lhs = rule.getLhs();
        ExprConstraintDescr constr = ((ExprConstraintDescr) (getDescrs().get(0)));
        TestCase.assertEquals("location==\"atlanta\\\"\"", constr.getText());
    }

    @Test
    public void testRuleParseLhsWithStringQuotes2() throws Exception {
        final String text = "rule X when Cheese( $x: type, type == \"s\\tti\\\"lto\\nn\" ) then end\n";
        RuleDescr rule = ((RuleDescr) (parse("rule", text)));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        TestCase.assertNotNull(rule);
        AndDescr lhs = rule.getLhs();
        ExprConstraintDescr constr = ((ExprConstraintDescr) (getDescrs().get(1)));
        TestCase.assertEquals("type == \"s\\tti\\\"lto\\nn\"", constr.getText());
    }

    @Test
    public void testLiteralBoolAndNegativeNumbersRule() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "literal_bool_and_negative.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        TestCase.assertNotNull(rule);
        TestCase.assertEquals("simple_rule", rule.getName());
        TestCase.assertNotNull(rule.getLhs());
        assertEqualsIgnoreWhitespace("cons();", ((String) (rule.getConsequence())));
        final AndDescr lhs = rule.getLhs();
        TestCase.assertEquals(3, lhs.getDescrs().size());
        PatternDescr pattern = ((PatternDescr) (lhs.getDescrs().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
        AndDescr fieldAnd = ((AndDescr) (pattern.getConstraint()));
        ExprConstraintDescr fld = ((ExprConstraintDescr) (fieldAnd.getDescrs().get(0)));
        TestCase.assertEquals("bar == false", fld.getExpression());
        pattern = ((PatternDescr) (lhs.getDescrs().get(1)));
        TestCase.assertEquals(1, getDescrs().size());
        fieldAnd = ((AndDescr) (pattern.getConstraint()));
        fld = ((ExprConstraintDescr) (fieldAnd.getDescrs().get(0)));
        TestCase.assertEquals("boo > -42", fld.getText());
        pattern = ((PatternDescr) (lhs.getDescrs().get(2)));
        TestCase.assertEquals(1, getDescrs().size());
        fieldAnd = ((AndDescr) (pattern.getConstraint()));
        fld = ((ExprConstraintDescr) (fieldAnd.getDescrs().get(0)));
        TestCase.assertEquals("boo > -42.42", fld.getText());
    }

    @Test
    public void testChunkWithoutParens() throws Exception {
        String input = "( foo )";
        createParser(new ANTLRStringStream(input));
        String returnData = parser.chunk(LEFT_PAREN, RIGHT_PAREN, (-1));
        TestCase.assertEquals("foo", returnData);
    }

    @Test
    public void testChunkWithParens() throws Exception {
        String input = "(fnord())";
        createParser(new ANTLRStringStream(input));
        String returnData = parser.chunk(LEFT_PAREN, RIGHT_PAREN, (-1));
        TestCase.assertEquals("fnord()", returnData);
    }

    @Test
    public void testChunkWithParensAndQuotedString() throws Exception {
        String input = "( fnord( \"cheese\" ) )";
        createParser(new ANTLRStringStream(input));
        String returnData = parser.chunk(LEFT_PAREN, RIGHT_PAREN, (-1));
        TestCase.assertEquals("fnord( \"cheese\" )", returnData);
    }

    @Test
    public void testChunkWithRandomCharac5ters() throws Exception {
        String input = "( %*9dkj)";
        createParser(new ANTLRStringStream(input));
        String returnData = parser.chunk(LEFT_PAREN, RIGHT_PAREN, (-1));
        TestCase.assertEquals("%*9dkj", returnData);
    }

    @Test
    public void testEmptyPattern() throws Exception {
        PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "test_EmptyPattern.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr ruleDescr = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals("simple rule", ruleDescr.getName());
        TestCase.assertNotNull(ruleDescr.getLhs());
        TestCase.assertEquals(1, getDescrs().size());
        final PatternDescr patternDescr = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals(0, getDescrs().size());// this

        TestCase.assertEquals("Cheese", patternDescr.getObjectType());
    }

    @Test
    public void testSimpleMethodCallWithFrom() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "test_SimpleMethodCallWithFrom.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        final PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        final FromDescr from = ((FromDescr) (pattern.getSource()));
        final MVELExprDescr method = ((MVELExprDescr) (from.getDataSource()));
        TestCase.assertEquals("something.doIt( foo,bar,42,\"hello\",[ a : \"b\", \"something\" : 42, \"a\" : foo, x : [x:y]],\"end\", [a, \"b\", 42] )", method.getExpression());
    }

    @Test
    public void testSimpleFunctionCallWithFrom() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "test_SimpleFunctionCallWithFrom.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        final PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        final FromDescr from = ((FromDescr) (pattern.getSource()));
        final MVELExprDescr func = ((MVELExprDescr) (from.getDataSource()));
        TestCase.assertEquals("doIt( foo,bar,42,\"hello\",[ a : \"b\", \"something\" : 42, \"a\" : foo, x : [x:y]],\"end\", [a, \"b\", 42] )", func.getExpression());
    }

    @Test
    public void testSimpleAccessorWithFrom() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "test_SimpleAccessorWithFrom.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        final PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        final FromDescr from = ((FromDescr) (pattern.getSource()));
        final MVELExprDescr accessor = ((MVELExprDescr) (from.getDataSource()));
        TestCase.assertEquals("something.doIt", accessor.getExpression());
    }

    @Test
    public void testSimpleAccessorAndArgWithFrom() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "test_SimpleAccessorArgWithFrom.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        final PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        final FromDescr from = ((FromDescr) (pattern.getSource()));
        final MVELExprDescr accessor = ((MVELExprDescr) (from.getDataSource()));
        TestCase.assertEquals("something.doIt[\"key\"]", accessor.getExpression());
    }

    @Test
    public void testComplexChainedAcessor() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "test_ComplexChainedCallWithFrom.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        final PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        final FromDescr from = ((FromDescr) (pattern.getSource()));
        final MVELExprDescr accessor = ((MVELExprDescr) (from.getDataSource()));
        TestCase.assertEquals("doIt1( foo,bar,42,\"hello\",[ a : \"b\"], [a, \"b\", 42] ).doIt2(bar, [a, \"b\", 42]).field[\"key\"]", accessor.getExpression());
    }

    @Test
    public void testFrom() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "from.drl")));
        TestCase.assertFalse(parser.getErrorMessages().toString(), parser.hasErrors());
        TestCase.assertNotNull(rule);
        TestCase.assertEquals("using_from", rule.getName());
        TestCase.assertEquals(9, getDescrs().size());
    }

    @Test
    public void testSimpleRule() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "simple_rule.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        TestCase.assertNotNull(rule);
        TestCase.assertEquals("simple_rule", rule.getName());
        TestCase.assertEquals(22, rule.getConsequenceLine());
        TestCase.assertEquals(2, rule.getConsequencePattern());
        final AndDescr lhs = rule.getLhs();
        TestCase.assertNotNull(lhs);
        TestCase.assertEquals(3, lhs.getDescrs().size());
        // Check first pattern
        final PatternDescr first = ((PatternDescr) (lhs.getDescrs().get(0)));
        TestCase.assertEquals("foo3", first.getIdentifier());
        TestCase.assertEquals("Bar", first.getObjectType());
        TestCase.assertEquals(1, getDescrs().size());
        AndDescr fieldAnd = ((AndDescr) (first.getConstraint()));
        ExprConstraintDescr constraint = ((ExprConstraintDescr) (fieldAnd.getDescrs().get(0)));
        TestCase.assertNotNull(constraint);
        TestCase.assertEquals("a==3", constraint.getExpression());
        // Check second pattern
        final PatternDescr second = ((PatternDescr) (lhs.getDescrs().get(1)));
        TestCase.assertEquals("foo4", second.getIdentifier());
        TestCase.assertEquals("Bar", second.getObjectType());
        // no constraints, only a binding
        fieldAnd = ((AndDescr) (second.getConstraint()));
        TestCase.assertEquals(1, fieldAnd.getDescrs().size());
        final ExprConstraintDescr binding = ((ExprConstraintDescr) (getDescrs().get(0)));
        TestCase.assertEquals("a4:a==4", binding.getExpression());
        // Check third pattern
        final PatternDescr third = ((PatternDescr) (lhs.getDescrs().get(2)));
        TestCase.assertNull(third.getIdentifier());
        TestCase.assertEquals("Baz", third.getObjectType());
        assertEqualsIgnoreWhitespace(("if ( a == b ) { " + (((("  assert( foo3 );" + "} else {") + "  retract( foo4 );") + "}") + "  System.out.println( a4 );")), ((String) (rule.getConsequence())));
    }

    @Test
    public void testRestrictionsMultiple() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "restrictions_test.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        TestCase.assertNotNull(rule);
        assertEqualsIgnoreWhitespace("consequence();", ((String) (rule.getConsequence())));
        TestCase.assertEquals("simple_rule", rule.getName());
        TestCase.assertEquals(2, getDescrs().size());
        // The first pattern, with 2 restrictions on a single field (plus a
        // connective)
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("Person", pattern.getObjectType());
        TestCase.assertEquals(1, getDescrs().size());
        AndDescr and = ((AndDescr) (pattern.getConstraint()));
        ExprConstraintDescr fld = ((ExprConstraintDescr) (and.getDescrs().get(0)));
        TestCase.assertEquals("age > 30 && < 40", fld.getExpression());
        // the second col, with 2 fields, the first with 2 restrictions, the
        // second field with one
        pattern = ((PatternDescr) (getDescrs().get(1)));
        TestCase.assertEquals("Vehicle", pattern.getObjectType());
        TestCase.assertEquals(2, getDescrs().size());
        and = ((AndDescr) (pattern.getConstraint()));
        fld = ((ExprConstraintDescr) (and.getDescrs().get(0)));
        TestCase.assertEquals("type == \"sedan\" || == \"wagon\"", fld.getExpression());
        // now the second field
        fld = ((ExprConstraintDescr) (and.getDescrs().get(1)));
        TestCase.assertEquals("age < 3", fld.getExpression());
    }

    @Test
    public void testLineNumberInAST() throws Exception {
        // also see testSimpleExpander to see how this works with an expander
        // (should be the same).
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "simple_rule.drl")));
        TestCase.assertNotNull(rule);
        TestCase.assertEquals("simple_rule", rule.getName());
        TestCase.assertEquals(22, rule.getConsequenceLine());
        TestCase.assertEquals(2, rule.getConsequencePattern());
        final AndDescr lhs = rule.getLhs();
        TestCase.assertNotNull(lhs);
        TestCase.assertEquals(3, lhs.getDescrs().size());
        // Check first pattern
        final PatternDescr first = ((PatternDescr) (lhs.getDescrs().get(0)));
        TestCase.assertEquals("foo3", first.getIdentifier());
        TestCase.assertEquals("Bar", first.getObjectType());
        TestCase.assertEquals(1, getDescrs().size());
        // Check second pattern
        final PatternDescr second = ((PatternDescr) (lhs.getDescrs().get(1)));
        TestCase.assertEquals("foo4", second.getIdentifier());
        TestCase.assertEquals("Bar", second.getObjectType());
        final PatternDescr third = ((PatternDescr) (lhs.getDescrs().get(2)));
        TestCase.assertEquals("Baz", third.getObjectType());
        TestCase.assertEquals(19, first.getLine());
        TestCase.assertEquals(20, second.getLine());
        TestCase.assertEquals(21, third.getLine());
    }

    @Test
    public void testLineNumberIncludingCommentsInRHS() throws Exception {
        PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "test_CommentLineNumbersInConsequence.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        final String rhs = ((String) (getConsequence()));
        String expected = "\\s*//woot$\\s*first$\\s*$\\s*//$\\s*$\\s*/\\* lala$\\s*$\\s*\\*/$\\s*second$\\s*";
        TestCase.assertTrue(Pattern.compile(expected, ((Pattern.DOTALL) | (Pattern.MULTILINE))).matcher(rhs).matches());
    }

    @Test
    public void testLhsSemicolonDelim() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "lhs_semicolon_delim.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        TestCase.assertNotNull(rule);
        TestCase.assertEquals("simple_rule", rule.getName());
        final AndDescr lhs = rule.getLhs();
        TestCase.assertNotNull(lhs);
        TestCase.assertEquals(3, lhs.getDescrs().size());
        // System.err.println( lhs.getDescrs() );
        // Check first pattern
        final PatternDescr first = ((PatternDescr) (lhs.getDescrs().get(0)));
        TestCase.assertEquals("foo3", first.getIdentifier());
        TestCase.assertEquals("Bar", first.getObjectType());
        TestCase.assertEquals(1, getDescrs().size());
        // LiteralDescr constraint = (LiteralDescr) first.getDescrs().get( 0 );
        AndDescr and = ((AndDescr) (first.getConstraint()));
        ExprConstraintDescr fld = ((ExprConstraintDescr) (and.getDescrs().get(0)));
        TestCase.assertNotNull(fld);
        TestCase.assertEquals("a==3", fld.getExpression());
        // Check second pattern
        final PatternDescr second = ((PatternDescr) (lhs.getDescrs().get(1)));
        TestCase.assertEquals("foo4", second.getIdentifier());
        TestCase.assertEquals("Bar", second.getObjectType());
        TestCase.assertEquals(1, second.getDescrs().size());
        final ExprConstraintDescr fieldBindingDescr = ((ExprConstraintDescr) (second.getDescrs().get(0)));
        TestCase.assertEquals("a4:a==4", fieldBindingDescr.getExpression());
        // Check third pattern
        final PatternDescr third = ((PatternDescr) (lhs.getDescrs().get(2)));
        TestCase.assertNull(third.getIdentifier());
        TestCase.assertEquals("Baz", third.getObjectType());
        assertEqualsIgnoreWhitespace(("if ( a == b ) { " + (((("  assert( foo3 );" + "} else {") + "  retract( foo4 );") + "}") + "  System.out.println( a4 );")), ((String) (rule.getConsequence())));
    }

    @Test
    public void testNotNode() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "rule_not.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        TestCase.assertNotNull(rule);
        TestCase.assertEquals("simple_rule", rule.getName());
        final AndDescr lhs = rule.getLhs();
        TestCase.assertEquals(1, lhs.getDescrs().size());
        final NotDescr not = ((NotDescr) (lhs.getDescrs().get(0)));
        TestCase.assertEquals(1, not.getDescrs().size());
        final PatternDescr pattern = ((PatternDescr) (not.getDescrs().get(0)));
        TestCase.assertEquals("Cheese", pattern.getObjectType());
        TestCase.assertEquals(1, getDescrs().size());
        final AndDescr and = ((AndDescr) (pattern.getConstraint()));
        final ExprConstraintDescr fld = ((ExprConstraintDescr) (and.getDescrs().get(0)));
        TestCase.assertEquals("type == \"stilton\"", fld.getExpression());
    }

    @Test
    public void testNotExistWithBrackets() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "not_exist_with_brackets.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertNotNull(rule);
        TestCase.assertEquals("simple_rule", rule.getName());
        final AndDescr lhs = rule.getLhs();
        TestCase.assertEquals(2, lhs.getDescrs().size());
        final NotDescr not = ((NotDescr) (lhs.getDescrs().get(0)));
        TestCase.assertEquals(1, not.getDescrs().size());
        final PatternDescr pattern = ((PatternDescr) (not.getDescrs().get(0)));
        TestCase.assertEquals("Cheese", pattern.getObjectType());
        final ExistsDescr ex = ((ExistsDescr) (lhs.getDescrs().get(1)));
        TestCase.assertEquals(1, ex.getDescrs().size());
        final PatternDescr exPattern = ((PatternDescr) (ex.getDescrs().get(0)));
        TestCase.assertEquals("Foo", exPattern.getObjectType());
    }

    @Test
    public void testSimpleQuery() throws Exception {
        final QueryDescr query = ((QueryDescr) (parseResource("query", "simple_query.drl")));
        TestCase.assertNotNull(query);
        TestCase.assertEquals("simple_query", query.getName());
        final AndDescr lhs = query.getLhs();
        TestCase.assertNotNull(lhs);
        TestCase.assertEquals(3, lhs.getDescrs().size());
        // Check first pattern
        final PatternDescr first = ((PatternDescr) (lhs.getDescrs().get(0)));
        TestCase.assertEquals("foo3", first.getIdentifier());
        TestCase.assertEquals("Bar", first.getObjectType());
        TestCase.assertEquals(1, getDescrs().size());
        AndDescr and = ((AndDescr) (first.getConstraint()));
        ExprConstraintDescr fld = ((ExprConstraintDescr) (and.getDescrs().get(0)));
        TestCase.assertNotNull(fld);
        TestCase.assertEquals("a==3", fld.getExpression());
        // Check second pattern
        final PatternDescr second = ((PatternDescr) (lhs.getDescrs().get(1)));
        TestCase.assertEquals("foo4", second.getIdentifier());
        TestCase.assertEquals("Bar", second.getObjectType());
        TestCase.assertEquals(1, second.getDescrs().size());
        // check it has field bindings.
        final ExprConstraintDescr bindingDescr = ((ExprConstraintDescr) (second.getDescrs().get(0)));
        TestCase.assertEquals("a4:a==4", bindingDescr.getExpression());
    }

    @Test
    public void testQueryRuleMixed() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "query_and_rule.drl")));
        TestCase.assertEquals(4, pkg.getRules().size());// as queries are rules

        RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals("bar", rule.getName());
        QueryDescr query = ((QueryDescr) (pkg.getRules().get(1)));
        TestCase.assertEquals("simple_query", query.getName());
        rule = ((RuleDescr) (pkg.getRules().get(2)));
        TestCase.assertEquals("bar2", rule.getName());
        query = ((QueryDescr) (pkg.getRules().get(3)));
        TestCase.assertEquals("simple_query2", query.getName());
    }

    @Test
    public void testMultipleRules() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "multiple_rules.drl")));
        final List<RuleDescr> rules = pkg.getRules();
        TestCase.assertEquals(2, rules.size());
        final RuleDescr rule0 = rules.get(0);
        TestCase.assertEquals("Like Stilton", rule0.getName());
        final RuleDescr rule1 = rules.get(1);
        TestCase.assertEquals("Like Cheddar", rule1.getName());
        // checkout the first rule
        AndDescr lhs = rule1.getLhs();
        TestCase.assertNotNull(lhs);
        TestCase.assertEquals(1, lhs.getDescrs().size());
        assertEqualsIgnoreWhitespace("System.out.println(\"I like \" + t);", ((String) (rule0.getConsequence())));
        // Check first pattern
        PatternDescr first = ((PatternDescr) (lhs.getDescrs().get(0)));
        TestCase.assertEquals("Cheese", first.getObjectType());
        // checkout the second rule
        lhs = rule1.getLhs();
        TestCase.assertNotNull(lhs);
        TestCase.assertEquals(1, lhs.getDescrs().size());
        assertEqualsIgnoreWhitespace("System.out.println(\"I like \" + t);", ((String) (rule1.getConsequence())));
        // Check first pattern
        first = ((PatternDescr) (lhs.getDescrs().get(0)));
        TestCase.assertEquals("Cheese", first.getObjectType());
    }

    @Test
    public void testExpanderLineSpread() throws Exception {
        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL6);
        final PackageDescr pkg = parser.parse(this.getReader("expander_spread_lines.dslr"), this.getReader("complex.dsl"));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
        final OrDescr or = ((OrDescr) (getDescrs().get(0)));
        TestCase.assertEquals(2, or.getDescrs().size());
        TestCase.assertNotNull(((String) (rule.getConsequence())));
    }

    @Test
    public void testExpanderMultipleConstraints() throws Exception {
        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL6);
        final PackageDescr pkg = parser.parse(this.getReader("expander_multiple_constraints.dslr"), this.getReader("multiple_constraints.dsl"));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(2, getDescrs().size());
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("Person", pattern.getObjectType());
        TestCase.assertEquals(2, getDescrs().size());
        TestCase.assertEquals("age < 42", getExpression());
        TestCase.assertEquals("location==atlanta", getExpression());
        pattern = ((PatternDescr) (getDescrs().get(1)));
        TestCase.assertEquals("Bar", pattern.getObjectType());
        TestCase.assertNotNull(((String) (rule.getConsequence())));
    }

    @Test
    public void testExpanderMultipleConstraintsFlush() throws Exception {
        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL6);
        // this is similar to the other test, but it requires a flush to add the
        // constraints
        final PackageDescr pkg = parser.parse(this.getReader("expander_multiple_constraints_flush.dslr"), this.getReader("multiple_constraints.dsl"));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
        final PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("Person", pattern.getObjectType());
        TestCase.assertEquals(2, getDescrs().size());
        TestCase.assertEquals("age < 42", getExpression());
        TestCase.assertEquals("location==atlanta", getExpression());
        TestCase.assertNotNull(((String) (rule.getConsequence())));
    }

    // @Test public void testExpanderUnExpandableErrorLines() throws Exception {
    // 
    // //stubb expander
    // final ExpanderResolver res = new ExpanderResolver() {
    // public Expander get(String name,
    // String config) {
    // return new Expander() {
    // public String expand(String scope,
    // String pattern) {
    // if ( pattern.startsWith( "Good" ) ) {
    // return pattern;
    // } else {
    // throw new IllegalArgumentException( "whoops" );
    // }
    // 
    // }
    // };
    // }
    // };
    // 
    // final DRLParser parser = parseResource( "expander_line_errors.dslr" );
    // parser.setExpanderResolver( res );
    // parser.compilationUnit();
    // assertTrue( parser.hasErrors() );
    // 
    // final List messages = parser.getErrorMessages();
    // assertEquals( messages.size(),
    // parser.getErrors().size() );
    // 
    // assertEquals( 4,
    // parser.getErrors().size() );
    // assertEquals( ExpanderException.class,
    // parser.getErrors().get( 0 ).getClass() );
    // assertEquals( 8,
    // ((RecognitionException) parser.getErrors().get( 0 )).line );
    // assertEquals( 10,
    // ((RecognitionException) parser.getErrors().get( 1 )).line );
    // assertEquals( 12,
    // ((RecognitionException) parser.getErrors().get( 2 )).line );
    // assertEquals( 13,
    // ((RecognitionException) parser.getErrors().get( 3 )).line );
    // 
    // final PackageDescr pack = parser.getPackageDescr();
    // assertNotNull( pack );
    // 
    // final ExpanderException ex = (ExpanderException) parser.getErrors().get(
    // 0 );
    // assertTrue( ex.getMessage().indexOf( "whoops" ) > -1 );
    // 
    // }
    @Test
    public void testBasicBinding() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "basic_binding.drl")));
        final RuleDescr ruleDescr = ((RuleDescr) (pkg.getRules().get(0)));
        final AndDescr lhs = ruleDescr.getLhs();
        TestCase.assertEquals(1, lhs.getDescrs().size());
        final PatternDescr cheese = ((PatternDescr) (lhs.getDescrs().get(0)));
        TestCase.assertEquals("Cheese", cheese.getObjectType());
        TestCase.assertEquals(1, getDescrs().size());
        final ExprConstraintDescr fieldBinding = ((ExprConstraintDescr) (cheese.getDescrs().get(0)));
        TestCase.assertEquals("$type:type", fieldBinding.getExpression());
    }

    @Test
    public void testBoundVariables() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "bindings.drl")));
        final RuleDescr ruleDescr = ((RuleDescr) (pkg.getRules().get(0)));
        final AndDescr lhs = ruleDescr.getLhs();
        TestCase.assertEquals(2, lhs.getDescrs().size());
        final PatternDescr cheese = ((PatternDescr) (lhs.getDescrs().get(0)));
        TestCase.assertEquals("Cheese", cheese.getObjectType());
        TestCase.assertEquals(1, cheese.getDescrs().size());
        ExprConstraintDescr fieldBinding = ((ExprConstraintDescr) (cheese.getDescrs().get(0)));
        TestCase.assertEquals("$type : type == \"stilton\"", fieldBinding.getExpression());
        final PatternDescr person = ((PatternDescr) (lhs.getDescrs().get(1)));
        TestCase.assertEquals(2, person.getDescrs().size());
        fieldBinding = ((ExprConstraintDescr) (person.getDescrs().get(0)));
        TestCase.assertEquals("$name : name == \"bob\"", fieldBinding.getExpression());
        ExprConstraintDescr fld = ((ExprConstraintDescr) (person.getDescrs().get(1)));
        TestCase.assertEquals("likes == $type", fld.getExpression());
    }

    @Test
    public void testOrNesting() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "or_nesting.drl")));
        TestCase.assertNotNull(pkg);
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals("simple_rule", rule.getName());
        TestCase.assertEquals(1, getDescrs().size());
        final OrDescr or = ((OrDescr) (getDescrs().get(0)));
        TestCase.assertEquals(2, or.getDescrs().size());
        final PatternDescr first = ((PatternDescr) (or.getDescrs().get(0)));
        TestCase.assertEquals("Person", first.getObjectType());
        final AndDescr and = ((AndDescr) (or.getDescrs().get(1)));
        TestCase.assertEquals(2, and.getDescrs().size());
        final PatternDescr left = ((PatternDescr) (and.getDescrs().get(0)));
        TestCase.assertEquals("Person", left.getObjectType());
        final PatternDescr right = ((PatternDescr) (and.getDescrs().get(1)));
        TestCase.assertEquals("Cheese", right.getObjectType());
    }

    /**
     * Test that explicit "&&", "||" works as expected
     */
    @Test
    public void testAndOrRules() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "and_or_rule.drl")));
        TestCase.assertNotNull(pkg);
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals("simple_rule", rule.getName());
        // we will have 3 children under the main And node
        final AndDescr and = rule.getLhs();
        TestCase.assertEquals(3, and.getDescrs().size());
        PatternDescr left = ((PatternDescr) (and.getDescrs().get(0)));
        PatternDescr right = ((PatternDescr) (and.getDescrs().get(1)));
        TestCase.assertEquals("Person", left.getObjectType());
        TestCase.assertEquals("Cheese", right.getObjectType());
        TestCase.assertEquals(1, getDescrs().size());
        ExprConstraintDescr fld = ((ExprConstraintDescr) (getDescrs().get(0)));
        TestCase.assertEquals("name == \"mark\"", fld.getExpression());
        TestCase.assertEquals(1, getDescrs().size());
        fld = ((ExprConstraintDescr) (getDescrs().get(0)));
        TestCase.assertEquals("type == \"stilton\"", fld.getExpression());
        // now the "||" part
        final OrDescr or = ((OrDescr) (and.getDescrs().get(2)));
        TestCase.assertEquals(2, or.getDescrs().size());
        left = ((PatternDescr) (or.getDescrs().get(0)));
        right = ((PatternDescr) (or.getDescrs().get(1)));
        TestCase.assertEquals("Person", left.getObjectType());
        TestCase.assertEquals("Cheese", right.getObjectType());
        TestCase.assertEquals(1, getDescrs().size());
        fld = ((ExprConstraintDescr) (getDescrs().get(0)));
        TestCase.assertEquals("name == \"mark\"", fld.getExpression());
        TestCase.assertEquals(1, getDescrs().size());
        fld = ((ExprConstraintDescr) (getDescrs().get(0)));
        TestCase.assertEquals("type == \"stilton\"", fld.getExpression());
        assertEqualsIgnoreWhitespace("System.out.println( \"Mark and Michael\" );", ((String) (rule.getConsequence())));
    }

    /**
     * test basic foo : Fact() || Fact() stuff
     */
    @Test
    public void testOrWithBinding() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "or_binding.drl")));
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(2, getDescrs().size());
        final OrDescr or = ((OrDescr) (getDescrs().get(0)));
        TestCase.assertEquals(2, or.getDescrs().size());
        final PatternDescr leftPattern = ((PatternDescr) (or.getDescrs().get(0)));
        TestCase.assertEquals("Person", leftPattern.getObjectType());
        TestCase.assertEquals("foo", leftPattern.getIdentifier());
        final PatternDescr rightPattern = ((PatternDescr) (or.getDescrs().get(1)));
        TestCase.assertEquals("Person", rightPattern.getObjectType());
        TestCase.assertEquals("foo", rightPattern.getIdentifier());
        final PatternDescr cheeseDescr = ((PatternDescr) (getDescrs().get(1)));
        TestCase.assertEquals("Cheese", cheeseDescr.getObjectType());
        TestCase.assertEquals(null, cheeseDescr.getIdentifier());
        assertEqualsIgnoreWhitespace("System.out.println( \"Mark and Michael\" + bar );", ((String) (rule.getConsequence())));
    }

    /**
     * test basic foo : Fact() || Fact() stuff binding to an "or"
     */
    @Test
    public void testOrBindingComplex() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "or_binding_complex.drl")));
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
        TestCase.assertEquals(1, getDescrs().size());
        final OrDescr or = ((OrDescr) (getDescrs().get(0)));
        TestCase.assertEquals(2, or.getDescrs().size());
        // first fact
        final PatternDescr firstFact = ((PatternDescr) (or.getDescrs().get(0)));
        TestCase.assertEquals("Person", firstFact.getObjectType());
        TestCase.assertEquals("foo", firstFact.getIdentifier());
        // second "option"
        final PatternDescr secondFact = ((PatternDescr) (or.getDescrs().get(1)));
        TestCase.assertEquals("Person", secondFact.getObjectType());
        TestCase.assertEquals(1, getDescrs().size());
        TestCase.assertEquals("foo", secondFact.getIdentifier());
        assertEqualsIgnoreWhitespace("System.out.println( \"Mark and Michael\" + bar );", ((String) (rule.getConsequence())));
    }

    @Test
    public void testOrBindingWithBrackets() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "or_binding_with_brackets.drl")));
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
        TestCase.assertEquals(1, getDescrs().size());
        final OrDescr or = ((OrDescr) (getDescrs().get(0)));
        TestCase.assertEquals(2, or.getDescrs().size());
        // first fact
        final PatternDescr firstFact = ((PatternDescr) (or.getDescrs().get(0)));
        TestCase.assertEquals("Person", firstFact.getObjectType());
        TestCase.assertEquals("foo", firstFact.getIdentifier());
        // second "option"
        final PatternDescr secondFact = ((PatternDescr) (or.getDescrs().get(0)));
        TestCase.assertEquals("Person", secondFact.getObjectType());
        TestCase.assertEquals("foo", secondFact.getIdentifier());
        assertEqualsIgnoreWhitespace("System.out.println( \"Mark and Michael\" + bar );", ((String) (rule.getConsequence())));
    }

    /**
     *
     */
    @Test
    public void testBracketsPrecedence() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "brackets_precedence.drl")));
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        final AndDescr rootAnd = ((AndDescr) (rule.getLhs()));
        TestCase.assertEquals(2, rootAnd.getDescrs().size());
        final OrDescr leftOr = ((OrDescr) (rootAnd.getDescrs().get(0)));
        TestCase.assertEquals(2, leftOr.getDescrs().size());
        final NotDescr not = ((NotDescr) (leftOr.getDescrs().get(0)));
        final PatternDescr foo1 = ((PatternDescr) (not.getDescrs().get(0)));
        TestCase.assertEquals("Foo", foo1.getObjectType());
        final PatternDescr foo2 = ((PatternDescr) (leftOr.getDescrs().get(1)));
        TestCase.assertEquals("Foo", foo2.getObjectType());
        final OrDescr rightOr = ((OrDescr) (rootAnd.getDescrs().get(1)));
        TestCase.assertEquals(2, rightOr.getDescrs().size());
        final PatternDescr shoes = ((PatternDescr) (rightOr.getDescrs().get(0)));
        TestCase.assertEquals("Shoes", shoes.getObjectType());
        final PatternDescr butt = ((PatternDescr) (rightOr.getDescrs().get(1)));
        TestCase.assertEquals("Butt", butt.getObjectType());
    }

    @Test
    public void testEvalMultiple() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "eval_multiple.drl")));
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(4, getDescrs().size());
        final EvalDescr eval = ((EvalDescr) (getDescrs().get(0)));
        assertEqualsIgnoreWhitespace("abc(\"foo\") + 5", ((String) (eval.getContent())));
        final PatternDescr pattern = ((PatternDescr) (getDescrs().get(1)));
        TestCase.assertEquals("Foo", pattern.getObjectType());
    }

    @Test
    public void testWithEval() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "with_eval.drl")));
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(3, getDescrs().size());
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("Foo", pattern.getObjectType());
        pattern = ((PatternDescr) (getDescrs().get(1)));
        TestCase.assertEquals("Bar", pattern.getObjectType());
        final EvalDescr eval = ((EvalDescr) (getDescrs().get(2)));
        assertEqualsIgnoreWhitespace("abc(\"foo\")", ((String) (eval.getContent())));
        assertEqualsIgnoreWhitespace("Kapow", ((String) (rule.getConsequence())));
    }

    @Test
    public void testWithRetval() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "with_retval.drl")));
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
        final PatternDescr col = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
        TestCase.assertEquals("Foo", col.getObjectType());
        final ExprConstraintDescr fld = ((ExprConstraintDescr) (getDescrs().get(0)));
        TestCase.assertEquals("name== (a + b)", fld.getExpression());
    }

    @Test
    public void testWithPredicate() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "with_predicate.drl")));
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
        final PatternDescr col = ((PatternDescr) (getDescrs().get(0)));
        AndDescr and = ((AndDescr) (col.getConstraint()));
        TestCase.assertEquals(2, and.getDescrs().size());
        final ExprConstraintDescr field = ((ExprConstraintDescr) (col.getDescrs().get(0)));
        final ExprConstraintDescr pred = ((ExprConstraintDescr) (and.getDescrs().get(1)));
        TestCase.assertEquals("$age2:age", field.getExpression());
        assertEqualsIgnoreWhitespace("$age2 == $age1+2", pred.getExpression());
    }

    @Test
    public void testNotWithConstraint() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "not_with_constraint.drl")));
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(2, getDescrs().size());
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        final ExprConstraintDescr fieldBinding = ((ExprConstraintDescr) (pattern.getDescrs().get(0)));
        TestCase.assertEquals("$likes:like", fieldBinding.getExpression());
        final NotDescr not = ((NotDescr) (getDescrs().get(1)));
        pattern = ((PatternDescr) (not.getDescrs().get(0)));
        final ExprConstraintDescr fld = ((ExprConstraintDescr) (getDescrs().get(0)));
        TestCase.assertEquals("type == $likes", fld.getExpression());
    }

    @Test
    public void testFunctions() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "functions.drl")));
        TestCase.assertEquals(2, pkg.getRules().size());
        final List<FunctionDescr> functions = pkg.getFunctions();
        TestCase.assertEquals(2, functions.size());
        FunctionDescr func = functions.get(0);
        TestCase.assertEquals("functionA", func.getName());
        TestCase.assertEquals("String", func.getReturnType());
        TestCase.assertEquals(2, func.getParameterNames().size());
        TestCase.assertEquals(2, func.getParameterTypes().size());
        TestCase.assertEquals(19, func.getLine());
        TestCase.assertEquals(0, func.getColumn());
        TestCase.assertEquals("String", func.getParameterTypes().get(0));
        TestCase.assertEquals("s", func.getParameterNames().get(0));
        TestCase.assertEquals("Integer", func.getParameterTypes().get(1));
        TestCase.assertEquals("i", func.getParameterNames().get(1));
        assertEqualsIgnoreWhitespace("foo();", func.getBody());
        func = functions.get(1);
        TestCase.assertEquals("functionB", func.getName());
        assertEqualsIgnoreWhitespace("bar();", func.getText());
    }

    @Test
    public void testComment() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "comment.drl")));
        TestCase.assertNotNull(pkg);
        TestCase.assertEquals("foo.bar", pkg.getName());
    }

    @Test
    public void testAttributes() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "rule_attributes.drl")));
        TestCase.assertEquals("simple_rule", rule.getName());
        assertEqualsIgnoreWhitespace("bar();", ((String) (rule.getConsequence())));
        final Map<String, AttributeDescr> attrs = rule.getAttributes();
        TestCase.assertEquals(6, attrs.size());
        AttributeDescr at = ((AttributeDescr) (attrs.get("salience")));
        TestCase.assertEquals("salience", at.getName());
        TestCase.assertEquals("42", at.getValue());
        at = ((AttributeDescr) (attrs.get("agenda-group")));
        TestCase.assertEquals("agenda-group", at.getName());
        TestCase.assertEquals("my_group", at.getValue());
        at = ((AttributeDescr) (attrs.get("no-loop")));
        TestCase.assertEquals("no-loop", at.getName());
        TestCase.assertEquals("true", at.getValue());
        at = ((AttributeDescr) (attrs.get("duration")));
        TestCase.assertEquals("duration", at.getName());
        TestCase.assertEquals("42", at.getValue());
        at = ((AttributeDescr) (attrs.get("activation-group")));
        TestCase.assertEquals("activation-group", at.getName());
        TestCase.assertEquals("my_activation_group", at.getValue());
        at = ((AttributeDescr) (attrs.get("lock-on-active")));
        TestCase.assertEquals("lock-on-active", at.getName());
        TestCase.assertEquals("true", at.getValue());
    }

    @Test
    public void testAttributes2() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "rule_attributes2.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        List<RuleDescr> rules = pkg.getRules();
        TestCase.assertEquals(3, rules.size());
        RuleDescr rule = rules.get(0);
        TestCase.assertEquals("rule1", rule.getName());
        Map<String, AttributeDescr> attrs = rule.getAttributes();
        TestCase.assertEquals(2, attrs.size());
        AttributeDescr at = ((AttributeDescr) (attrs.get("salience")));
        TestCase.assertEquals("salience", at.getName());
        TestCase.assertEquals("(42)", at.getValue());
        at = ((AttributeDescr) (attrs.get("agenda-group")));
        TestCase.assertEquals("agenda-group", at.getName());
        TestCase.assertEquals("my_group", at.getValue());
        rule = rules.get(1);
        TestCase.assertEquals("rule2", rule.getName());
        attrs = rule.getAttributes();
        TestCase.assertEquals(2, attrs.size());
        at = ((AttributeDescr) (attrs.get("salience")));
        TestCase.assertEquals("salience", at.getName());
        TestCase.assertEquals("(Integer.MIN_VALUE)", at.getValue());
        at = ((AttributeDescr) (attrs.get("no-loop")));
        TestCase.assertEquals("no-loop", at.getName());
        rule = rules.get(2);
        TestCase.assertEquals("rule3", rule.getName());
        attrs = rule.getAttributes();
        TestCase.assertEquals(2, attrs.size());
        at = ((AttributeDescr) (attrs.get("enabled")));
        TestCase.assertEquals("enabled", at.getName());
        TestCase.assertEquals("(Boolean.TRUE)", at.getValue());
        at = ((AttributeDescr) (attrs.get("activation-group")));
        TestCase.assertEquals("activation-group", at.getName());
        TestCase.assertEquals("my_activation_group", at.getValue());
    }

    @Test
    public void testAttributeRefract() throws Exception {
        final String source = "rule Test refract when Person() then end";
        PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", source)));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals("Test", rule.getName());
        Map<String, AttributeDescr> attributes = rule.getAttributes();
        TestCase.assertEquals(1, attributes.size());
        AttributeDescr refract = attributes.get("refract");
        TestCase.assertNotNull(refract);
        TestCase.assertEquals("true", refract.getValue());
    }

    @Test
    public void testEnabledExpression() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "rule_enabled_expression.drl")));
        TestCase.assertEquals("simple_rule", rule.getName());
        assertEqualsIgnoreWhitespace("bar();", ((String) (rule.getConsequence())));
        final Map<String, AttributeDescr> attrs = rule.getAttributes();
        TestCase.assertEquals(3, attrs.size());
        AttributeDescr at = ((AttributeDescr) (attrs.get("enabled")));
        TestCase.assertEquals("enabled", at.getName());
        TestCase.assertEquals("( 1 + 1 == 2 )", at.getValue());
        at = ((AttributeDescr) (attrs.get("salience")));
        TestCase.assertEquals("salience", at.getName());
        TestCase.assertEquals("( 1+2 )", at.getValue());
        at = ((AttributeDescr) (attrs.get("lock-on-active")));
        TestCase.assertEquals("lock-on-active", at.getName());
        TestCase.assertEquals("true", at.getValue());
    }

    @Test
    public void testDurationExpression() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "rule_duration_expression.drl")));
        TestCase.assertEquals("simple_rule", rule.getName());
        assertEqualsIgnoreWhitespace("bar();", ((String) (rule.getConsequence())));
        final Map<String, AttributeDescr> attrs = rule.getAttributes();
        TestCase.assertEquals(2, attrs.size());
        AttributeDescr at = ((AttributeDescr) (attrs.get("duration")));
        TestCase.assertEquals("duration", at.getName());
        TestCase.assertEquals("1h30m", at.getValue());
        at = ((AttributeDescr) (attrs.get("lock-on-active")));
        TestCase.assertEquals("lock-on-active", at.getName());
        TestCase.assertEquals("true", at.getValue());
    }

    @Test
    public void testCalendars() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "rule_calendars_attribute.drl")));
        TestCase.assertEquals("simple_rule", rule.getName());
        assertEqualsIgnoreWhitespace("bar();", ((String) (rule.getConsequence())));
        final Map<String, AttributeDescr> attrs = rule.getAttributes();
        TestCase.assertEquals(2, attrs.size());
        AttributeDescr at = ((AttributeDescr) (attrs.get("calendars")));
        TestCase.assertEquals("calendars", at.getName());
        TestCase.assertEquals("[ \"cal1\" ]", at.getValue());
        at = ((AttributeDescr) (attrs.get("lock-on-active")));
        TestCase.assertEquals("lock-on-active", at.getName());
        TestCase.assertEquals("true", at.getValue());
    }

    @Test
    public void testCalendars2() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "rule_calendars_attribute2.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        TestCase.assertEquals("simple_rule", rule.getName());
        assertEqualsIgnoreWhitespace("bar();", ((String) (rule.getConsequence())));
        final Map<String, AttributeDescr> attrs = rule.getAttributes();
        TestCase.assertEquals(2, attrs.size());
        AttributeDescr at = ((AttributeDescr) (attrs.get("calendars")));
        TestCase.assertEquals("calendars", at.getName());
        TestCase.assertEquals("[ \"cal 1\", \"cal 2\", \"cal 3\" ]", at.getValue());
        at = ((AttributeDescr) (attrs.get("lock-on-active")));
        TestCase.assertEquals("lock-on-active", at.getName());
        TestCase.assertEquals("true", at.getValue());
    }

    @Test
    public void testAttributes_alternateSyntax() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "rule_attributes_alt.drl")));
        TestCase.assertEquals("simple_rule", rule.getName());
        assertEqualsIgnoreWhitespace("bar();", ((String) (rule.getConsequence())));
        final Map<String, AttributeDescr> attrs = rule.getAttributes();
        TestCase.assertEquals(6, attrs.size());
        AttributeDescr at = ((AttributeDescr) (attrs.get("salience")));
        TestCase.assertEquals("salience", at.getName());
        TestCase.assertEquals("42", at.getValue());
        at = ((AttributeDescr) (attrs.get("agenda-group")));
        TestCase.assertEquals("agenda-group", at.getName());
        TestCase.assertEquals("my_group", at.getValue());
        at = ((AttributeDescr) (attrs.get("no-loop")));
        TestCase.assertEquals("no-loop", at.getName());
        TestCase.assertEquals("true", at.getValue());
        at = ((AttributeDescr) (attrs.get("lock-on-active")));
        TestCase.assertEquals("lock-on-active", at.getName());
        TestCase.assertEquals("true", at.getValue());
        at = ((AttributeDescr) (attrs.get("duration")));
        TestCase.assertEquals("duration", at.getName());
        TestCase.assertEquals("42", at.getValue());
        at = ((AttributeDescr) (attrs.get("activation-group")));
        TestCase.assertEquals("activation-group", at.getName());
        TestCase.assertEquals("my_activation_group", at.getValue());
    }

    @Test
    public void testEnumeration() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "enumeration.drl")));
        TestCase.assertEquals("simple_rule", rule.getName());
        TestCase.assertEquals(1, getDescrs().size());
        final PatternDescr col = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("Foo", col.getObjectType());
        TestCase.assertEquals(1, getDescrs().size());
        final ExprConstraintDescr fld = ((ExprConstraintDescr) (getDescrs().get(0)));
        TestCase.assertEquals("bar == Foo.BAR", fld.getExpression());
    }

    @Test
    public void testExtraLhsNewline() throws Exception {
        parseResource("compilationUnit", "extra_lhs_newline.drl");
    }

    @Test
    public void testSoundsLike() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "soundslike_operator.drl")));
        RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        PatternDescr pat = ((PatternDescr) (getDescrs().get(0)));
        pat.getConstraint();
    }

    @Test
    public void testPackageAttributes() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "package_attributes.drl")));
        AttributeDescr at = ((AttributeDescr) (pkg.getAttributes().get(0)));
        TestCase.assertEquals("agenda-group", at.getName());
        TestCase.assertEquals("x", at.getValue());
        at = ((AttributeDescr) (pkg.getAttributes().get(1)));
        TestCase.assertEquals("dialect", at.getName());
        TestCase.assertEquals("java", at.getValue());
        TestCase.assertEquals(2, pkg.getRules().size());
        TestCase.assertEquals(2, pkg.getImports().size());
        RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals("bar", rule.getName());
        at = ((AttributeDescr) (rule.getAttributes().get("agenda-group")));
        TestCase.assertEquals("agenda-group", at.getName());
        TestCase.assertEquals("x", at.getValue());
        at = ((AttributeDescr) (rule.getAttributes().get("dialect")));
        TestCase.assertEquals("dialect", at.getName());
        TestCase.assertEquals("java", at.getValue());
        rule = ((RuleDescr) (pkg.getRules().get(1)));
        TestCase.assertEquals("baz", rule.getName());
        at = ((AttributeDescr) (rule.getAttributes().get("dialect")));
        TestCase.assertEquals("dialect", at.getName());
        TestCase.assertEquals("mvel", at.getValue());
        at = ((AttributeDescr) (rule.getAttributes().get("agenda-group")));
        TestCase.assertEquals("agenda-group", at.getName());
        TestCase.assertEquals("x", at.getValue());
    }

    @Test
    public void testStatementOrdering1() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "statement_ordering_1.drl")));
        TestCase.assertEquals(2, pkg.getRules().size());
        TestCase.assertEquals("foo", ((RuleDescr) (pkg.getRules().get(0))).getName());
        TestCase.assertEquals("bar", ((RuleDescr) (pkg.getRules().get(1))).getName());
        TestCase.assertEquals(2, pkg.getFunctions().size());
        TestCase.assertEquals("cheeseIt", ((FunctionDescr) (pkg.getFunctions().get(0))).getName());
        TestCase.assertEquals("uncheeseIt", ((FunctionDescr) (pkg.getFunctions().get(1))).getName());
        TestCase.assertEquals(4, pkg.getImports().size());
        TestCase.assertEquals("im.one", getTarget());
        TestCase.assertEquals("im.two", getTarget());
        TestCase.assertEquals("im.three", getTarget());
        TestCase.assertEquals("im.four", getTarget());
    }

    @Test
    public void testRuleNamesStartingWithNumbers() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "rule_names_number_prefix.drl")));
        TestCase.assertEquals(2, pkg.getRules().size());
        TestCase.assertEquals("1. Do Stuff!", ((RuleDescr) (pkg.getRules().get(0))).getName());
        TestCase.assertEquals("2. Do More Stuff!", ((RuleDescr) (pkg.getRules().get(1))).getName());
    }

    @Test
    public void testEvalWithNewline() throws Exception {
        parseResource("compilationUnit", "eval_with_newline.drl");
    }

    @Test
    public void testEndPosition() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "test_EndPosition.drl")));
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        final PatternDescr col = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals(21, col.getLine());
        TestCase.assertEquals(23, col.getEndLine());
    }

    @Test
    public void testQualifiedClassname() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "qualified_classname.drl")));
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        final PatternDescr p = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("com.cheeseco.Cheese", p.getObjectType());
    }

    @Test
    public void testAccumulate() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "accumulate.drl")));
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
        final PatternDescr outPattern = ((PatternDescr) (getDescrs().get(0)));
        final AccumulateDescr accum = ((AccumulateDescr) (outPattern.getSource()));
        assertEqualsIgnoreWhitespace("int x = 0 ;", accum.getInitCode());
        assertEqualsIgnoreWhitespace("x++;", accum.getActionCode());
        TestCase.assertNull(accum.getReverseCode());
        assertEqualsIgnoreWhitespace("new Integer(x)", accum.getResultCode());
        TestCase.assertFalse(accum.isExternalFunction());
        final PatternDescr pattern = ((PatternDescr) (accum.getInputPattern()));
        TestCase.assertEquals("Person", pattern.getObjectType());
    }

    @Test
    public void testAccumulateWithBindings() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "accumulate_with_bindings.drl")));
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
        final PatternDescr outPattern = ((PatternDescr) (getDescrs().get(0)));
        final AccumulateDescr accum = ((AccumulateDescr) (outPattern.getSource()));
        assertEqualsIgnoreWhitespace("$counter", outPattern.getIdentifier());
        assertEqualsIgnoreWhitespace("int x = 0 ;", accum.getInitCode());
        assertEqualsIgnoreWhitespace("x++;", accum.getActionCode());
        assertEqualsIgnoreWhitespace("new Integer(x)", accum.getResultCode());
        final PatternDescr pattern = ((PatternDescr) (accum.getInputPattern()));
        TestCase.assertEquals("Person", pattern.getObjectType());
    }

    @Test
    public void testCollect() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "collect.drl")));
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
        final PatternDescr outPattern = ((PatternDescr) (getDescrs().get(0)));
        final CollectDescr collect = ((CollectDescr) (outPattern.getSource()));
        final PatternDescr pattern = ((PatternDescr) (collect.getInputPattern()));
        TestCase.assertEquals("Person", pattern.getObjectType());
    }

    @Test
    public void testPredicate2() throws Exception {
        // predicates are also prefixed by the eval keyword
        final RuleDescr rule = ((RuleDescr) (parse("rule", "rule X when Foo(eval( $var.equals(\"xyz\") )) then end")));
        final PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        final List<?> constraints = pattern.getConstraint().getDescrs();
        TestCase.assertEquals(1, constraints.size());
        final ExprConstraintDescr predicate = ((ExprConstraintDescr) (constraints.get(0)));
        TestCase.assertEquals("eval( $var.equals(\"xyz\") )", predicate.getExpression());
    }

    @Test
    public void testEscapedStrings() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "escaped-string.drl")));
        TestCase.assertNotNull(rule);
        TestCase.assertEquals("test_Quotes", rule.getName());
        final String expected = "String s = \"\\\"\\n\\t\\\\\";";
        assertEqualsIgnoreWhitespace(expected, ((String) (rule.getConsequence())));
    }

    @Test
    public void testNestedCEs() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "nested_conditional_elements.drl")));
        TestCase.assertNotNull(rule);
        final AndDescr root = rule.getLhs();
        final NotDescr not1 = ((NotDescr) (root.getDescrs().get(0)));
        final AndDescr and1 = ((AndDescr) (not1.getDescrs().get(0)));
        final PatternDescr state = ((PatternDescr) (and1.getDescrs().get(0)));
        final NotDescr not2 = ((NotDescr) (and1.getDescrs().get(1)));
        final AndDescr and2 = ((AndDescr) (not2.getDescrs().get(0)));
        final PatternDescr person = ((PatternDescr) (and2.getDescrs().get(0)));
        final PatternDescr cheese = ((PatternDescr) (and2.getDescrs().get(1)));
        final PatternDescr person2 = ((PatternDescr) (root.getDescrs().get(1)));
        final OrDescr or = ((OrDescr) (root.getDescrs().get(2)));
        final PatternDescr cheese2 = ((PatternDescr) (or.getDescrs().get(0)));
        final PatternDescr cheese3 = ((PatternDescr) (or.getDescrs().get(1)));
        TestCase.assertEquals(state.getObjectType(), "State");
        TestCase.assertEquals(person.getObjectType(), "Person");
        TestCase.assertEquals(cheese.getObjectType(), "Cheese");
        TestCase.assertEquals(person2.getObjectType(), "Person");
        TestCase.assertEquals(cheese2.getObjectType(), "Cheese");
        TestCase.assertEquals(cheese3.getObjectType(), "Cheese");
    }

    @Test
    public void testForall() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "forall.drl")));
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
        final ForallDescr forall = ((ForallDescr) (getDescrs().get(0)));
        TestCase.assertEquals(2, forall.getDescrs().size());
        final PatternDescr pattern = forall.getBasePattern();
        TestCase.assertEquals("Person", pattern.getObjectType());
        final List<BaseDescr> remaining = forall.getRemainingPatterns();
        TestCase.assertEquals(1, remaining.size());
        final PatternDescr cheese = ((PatternDescr) (remaining.get(0)));
        TestCase.assertEquals("Cheese", cheese.getObjectType());
    }

    @Test
    public void testForallWithFrom() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "forallwithfrom.drl")));
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
        final ForallDescr forall = ((ForallDescr) (getDescrs().get(0)));
        TestCase.assertEquals(2, forall.getDescrs().size());
        final PatternDescr pattern = forall.getBasePattern();
        TestCase.assertEquals("Person", pattern.getObjectType());
        TestCase.assertEquals("$village", getDataSource().toString());
        final List<BaseDescr> remaining = forall.getRemainingPatterns();
        TestCase.assertEquals(1, remaining.size());
        final PatternDescr cheese = ((PatternDescr) (remaining.get(0)));
        TestCase.assertEquals("Cheese", cheese.getObjectType());
        TestCase.assertEquals("$cheesery", getDataSource().toString());
    }

    @Test
    public void testMemberof() throws Exception {
        final String text = "rule X when Country( $cities : city )\nPerson( city memberOf $cities )\n then end";
        AndDescr descrs = getLhs();
        TestCase.assertEquals(2, descrs.getDescrs().size());
        PatternDescr pat = ((PatternDescr) (descrs.getDescrs().get(1)));
        ExprConstraintDescr fieldConstr = ((ExprConstraintDescr) (getDescrs().get(0)));
        TestCase.assertEquals("city memberOf $cities", fieldConstr.getExpression());
    }

    @Test
    public void testNotMemberof() throws Exception {
        final String text = "rule X when Country( $cities : city )\nPerson( city not memberOf $cities ) then end\n";
        AndDescr descrs = getLhs();
        TestCase.assertEquals(2, descrs.getDescrs().size());
        PatternDescr pat = ((PatternDescr) (descrs.getDescrs().get(1)));
        ExprConstraintDescr fieldConstr = ((ExprConstraintDescr) (getDescrs().get(0)));
        TestCase.assertEquals("city not memberOf $cities", fieldConstr.getExpression());
    }

    @Test
    public void testInOperator() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "in_operator_test.drl")));
        TestCase.assertNotNull(rule);
        assertEqualsIgnoreWhitespace("consequence();", ((String) (rule.getConsequence())));
        TestCase.assertEquals("simple_rule", rule.getName());
        TestCase.assertEquals(2, getDescrs().size());
        // The first pattern, with 2 restrictions on a single field (plus a
        // connective)
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("Person", pattern.getObjectType());
        TestCase.assertEquals(1, getDescrs().size());
        ExprConstraintDescr fld = ((ExprConstraintDescr) (getDescrs().get(0)));
        TestCase.assertEquals("age > 30 && < 40", fld.getExpression());
        // the second col, with 2 fields, the first with 2 restrictions, the
        // second field with one
        pattern = ((PatternDescr) (getDescrs().get(1)));
        TestCase.assertEquals("Vehicle", pattern.getObjectType());
        TestCase.assertEquals(2, getDescrs().size());
        fld = ((ExprConstraintDescr) (getDescrs().get(0)));
        TestCase.assertEquals("type in ( \"sedan\", \"wagon\" )", fld.getExpression());
        // now the second field
        fld = ((ExprConstraintDescr) (getDescrs().get(1)));
        TestCase.assertEquals("age < 3", fld.getExpression());
    }

    @Test
    public void testNotInOperator() throws Exception {
        final RuleDescr rule = ((RuleDescr) (parseResource("rule", "notin_operator_test.drl")));
        TestCase.assertNotNull(rule);
        assertEqualsIgnoreWhitespace("consequence();", ((String) (rule.getConsequence())));
        TestCase.assertEquals("simple_rule", rule.getName());
        TestCase.assertEquals(2, getDescrs().size());
        // The first pattern, with 2 restrictions on a single field (plus a
        // connective)
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("Person", pattern.getObjectType());
        TestCase.assertEquals(1, getDescrs().size());
        ExprConstraintDescr fld = ((ExprConstraintDescr) (getDescrs().get(0)));
        TestCase.assertEquals("age > 30 && < 40", fld.getExpression());
        // the second col, with 2 fields, the first with 2 restrictions, the
        // second field with one
        pattern = ((PatternDescr) (getDescrs().get(1)));
        TestCase.assertEquals("Vehicle", pattern.getObjectType());
        TestCase.assertEquals(2, getDescrs().size());
        fld = ((ExprConstraintDescr) (getDescrs().get(0)));
        TestCase.assertEquals("type not in ( \"sedan\", \"wagon\" )", fld.getExpression());
        // now the second field
        fld = ((ExprConstraintDescr) (getDescrs().get(1)));
        TestCase.assertEquals("age < 3", fld.getExpression());
    }

    @Test
    public void testCheckOrDescr() throws Exception {
        final String text = "rule X when Person( eval( age == 25 ) || ( eval( name.equals( \"bob\" ) ) && eval( age == 30 ) ) ) then end";
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals(1, pattern.getDescrs().size());
        TestCase.assertEquals(pattern.getConstraint().getClass(), AndDescr.class);
        TestCase.assertEquals(ExprConstraintDescr.class, getDescrs().get(0).getClass());
    }

    @Test
    public void testConstraintAndConnective() throws Exception {
        final String text = "rule X when Person( age < 42 && location==\"atlanta\") then end";
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals(1, pattern.getDescrs().size());
        ExprConstraintDescr fcd = ((ExprConstraintDescr) (pattern.getDescrs().get(0)));
        TestCase.assertEquals("age < 42 && location==\"atlanta\"", fcd.getExpression());
    }

    @Test
    public void testConstraintOrConnective() throws Exception {
        final String text = "rule X when Person( age < 42 || location==\"atlanta\") then end";
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals(1, pattern.getDescrs().size());
        ExprConstraintDescr fcd = ((ExprConstraintDescr) (pattern.getDescrs().get(0)));
        TestCase.assertEquals("age < 42 || location==\"atlanta\"", fcd.getExpression());
    }

    @Test
    public void testRestrictions() throws Exception {
        final String text = "rule X when Foo( bar > 1 || == 1 ) then end\n";
        AndDescr descrs = ((AndDescr) (((RuleDescr) (parse("rule", text))).getLhs()));
        TestCase.assertEquals(1, descrs.getDescrs().size());
        PatternDescr pat = ((PatternDescr) (descrs.getDescrs().get(0)));
        ExprConstraintDescr fieldConstr = ((ExprConstraintDescr) (getDescrs().get(0)));
        TestCase.assertEquals("bar > 1 || == 1", fieldConstr.getExpression());
    }

    @Test
    public void testSemicolon() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "semicolon.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        TestCase.assertEquals("org.drools.compiler", pkg.getName());
        TestCase.assertEquals(1, pkg.getGlobals().size());
        TestCase.assertEquals(3, pkg.getRules().size());
        final RuleDescr rule1 = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(2, getDescrs().size());
        final RuleDescr query1 = ((RuleDescr) (pkg.getRules().get(1)));
        TestCase.assertEquals(3, getDescrs().size());
        final RuleDescr rule2 = ((RuleDescr) (pkg.getRules().get(2)));
        TestCase.assertEquals(2, getDescrs().size());
    }

    @Test
    public void testEval() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "eval_parsing.drl")));
        TestCase.assertEquals("org.drools.compiler", pkg.getName());
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule1 = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
    }

    @Test
    public void testAccumulateReverse() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "accumulateReverse.drl")));
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
        final PatternDescr out = ((PatternDescr) (getDescrs().get(0)));
        final AccumulateDescr accum = ((AccumulateDescr) (out.getSource()));
        assertEqualsIgnoreWhitespace("int x = 0 ;", accum.getInitCode());
        assertEqualsIgnoreWhitespace("x++;", accum.getActionCode());
        assertEqualsIgnoreWhitespace("x--;", accum.getReverseCode());
        assertEqualsIgnoreWhitespace("new Integer(x)", accum.getResultCode());
        TestCase.assertFalse(accum.isExternalFunction());
        final PatternDescr pattern = ((PatternDescr) (accum.getInputPattern()));
        TestCase.assertEquals("Person", pattern.getObjectType());
    }

    @Test
    public void testAccumulateExternalFunction() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "accumulateExternalFunction.drl")));
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
        final PatternDescr out = ((PatternDescr) (getDescrs().get(0)));
        final AccumulateDescr accum = ((AccumulateDescr) (out.getSource()));
        assertEqualsIgnoreWhitespace("$age", accum.getFunctions().get(0).getParams()[0]);
        assertEqualsIgnoreWhitespace("average", accum.getFunctions().get(0).getFunction());
        TestCase.assertTrue(accum.isExternalFunction());
        final PatternDescr pattern = ((PatternDescr) (accum.getInputPattern()));
        TestCase.assertEquals("Person", pattern.getObjectType());
    }

    @Test
    public void testCollectWithNestedFrom() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "collect_with_nested_from.drl")));
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
        final PatternDescr out = ((PatternDescr) (getDescrs().get(0)));
        final CollectDescr collect = ((CollectDescr) (out.getSource()));
        PatternDescr person = ((PatternDescr) (collect.getInputPattern()));
        TestCase.assertEquals("Person", person.getObjectType());
        final CollectDescr collect2 = ((CollectDescr) (person.getSource()));
        final PatternDescr people = collect2.getInputPattern();
        TestCase.assertEquals("People", people.getObjectType());
    }

    @Test
    public void testAccumulateWithNestedFrom() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "accumulate_with_nested_from.drl")));
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
        final PatternDescr out = ((PatternDescr) (getDescrs().get(0)));
        final AccumulateDescr accumulate = ((AccumulateDescr) (out.getSource()));
        PatternDescr person = ((PatternDescr) (accumulate.getInputPattern()));
        TestCase.assertEquals("Person", person.getObjectType());
        final CollectDescr collect2 = ((CollectDescr) (person.getSource()));
        final PatternDescr people = collect2.getInputPattern();
        TestCase.assertEquals("People", people.getObjectType());
    }

    @Test
    public void testAccumulateMultipleFunctions() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "accumulateMultipleFunctions.drl")));
        TestCase.assertEquals(1, pkg.getRules().size());
        RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
        PatternDescr out = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("Object", out.getObjectType());
        AccumulateDescr accum = ((AccumulateDescr) (out.getSource()));
        TestCase.assertTrue(accum.isExternalFunction());
        List<AccumulateFunctionCallDescr> functions = accum.getFunctions();
        TestCase.assertEquals(3, functions.size());
        TestCase.assertEquals("average", functions.get(0).getFunction());
        TestCase.assertEquals("$a1", functions.get(0).getBind());
        TestCase.assertEquals("$price", functions.get(0).getParams()[0]);
        TestCase.assertEquals("min", functions.get(1).getFunction());
        TestCase.assertEquals("$m1", functions.get(1).getBind());
        TestCase.assertEquals("$price", functions.get(1).getParams()[0]);
        TestCase.assertEquals("max", functions.get(2).getFunction());
        TestCase.assertEquals("$M1", functions.get(2).getBind());
        TestCase.assertEquals("$price", functions.get(2).getParams()[0]);
        final PatternDescr pattern = ((PatternDescr) (accum.getInputPattern()));
        TestCase.assertEquals("Cheese", pattern.getObjectType());
    }

    @Test
    public void testAccumulateMnemonic() throws Exception {
        String drl = "package org.drools.compiler\n" + ((((("rule \"Accumulate 1\"\n" + "when\n") + "     acc( Cheese( $price : price ),\n") + "          $a1 : average( $price ) )\n") + "then\n") + "end\n");
        final PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", drl)));
        TestCase.assertEquals(1, pkg.getRules().size());
        RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
        PatternDescr out = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("Object", out.getObjectType());
        AccumulateDescr accum = ((AccumulateDescr) (out.getSource()));
        TestCase.assertTrue(accum.isExternalFunction());
        List<AccumulateFunctionCallDescr> functions = accum.getFunctions();
        TestCase.assertEquals(1, functions.size());
        TestCase.assertEquals("average", functions.get(0).getFunction());
        TestCase.assertEquals("$a1", functions.get(0).getBind());
        TestCase.assertEquals("$price", functions.get(0).getParams()[0]);
        final PatternDescr pattern = ((PatternDescr) (accum.getInputPattern()));
        TestCase.assertEquals("Cheese", pattern.getObjectType());
    }

    @Test
    public void testAccumulateMnemonic2() throws Exception {
        String drl = "package org.drools.compiler\n" + ((((("rule \"Accumulate 1\"\n" + "when\n") + "     Number() from acc( Cheese( $price : price ),\n") + "                        average( $price ) )\n") + "then\n") + "end\n");
        final PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", drl)));
        TestCase.assertEquals(1, pkg.getRules().size());
        RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
        PatternDescr out = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("Number", out.getObjectType());
        AccumulateDescr accum = ((AccumulateDescr) (out.getSource()));
        TestCase.assertTrue(accum.isExternalFunction());
        List<AccumulateFunctionCallDescr> functions = accum.getFunctions();
        TestCase.assertEquals(1, functions.size());
        TestCase.assertEquals("average", functions.get(0).getFunction());
        TestCase.assertEquals("$price", functions.get(0).getParams()[0]);
        final PatternDescr pattern = ((PatternDescr) (accum.getInputPattern()));
        TestCase.assertEquals("Cheese", pattern.getObjectType());
    }

    @Test
    public void testImportAccumulate() throws Exception {
        String drl = "package org.drools.compiler\n" + (((((((("import acc foo.Bar baz\n" + "import accumulate foo.Bar2 baz2\n") + "rule \"Accumulate 1\"\n") + "when\n") + "     acc( Cheese( $price : price ),\n") + "          $v1 : baz( $price ), \n") + "          $v2 : baz2( $price ) )\n") + "then\n") + "end\n");
        final PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", drl)));
        TestCase.assertEquals(2, pkg.getAccumulateImports().size());
        AccumulateImportDescr imp = ((AccumulateImportDescr) (pkg.getAccumulateImports().get(0)));
        TestCase.assertEquals("foo.Bar", imp.getTarget());
        TestCase.assertEquals("baz", imp.getFunctionName());
        imp = ((AccumulateImportDescr) (pkg.getAccumulateImports().get(1)));
        TestCase.assertEquals("foo.Bar2", imp.getTarget());
        TestCase.assertEquals("baz2", imp.getFunctionName());
        TestCase.assertEquals(1, pkg.getRules().size());
        RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
        PatternDescr out = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("Object", out.getObjectType());
        AccumulateDescr accum = ((AccumulateDescr) (out.getSource()));
        TestCase.assertTrue(accum.isExternalFunction());
        List<AccumulateFunctionCallDescr> functions = accum.getFunctions();
        TestCase.assertEquals(2, functions.size());
        TestCase.assertEquals("baz", functions.get(0).getFunction());
        TestCase.assertEquals("$v1", functions.get(0).getBind());
        TestCase.assertEquals("$price", functions.get(0).getParams()[0]);
        TestCase.assertEquals("baz2", functions.get(1).getFunction());
        TestCase.assertEquals("$v2", functions.get(1).getBind());
        TestCase.assertEquals("$price", functions.get(1).getParams()[0]);
        final PatternDescr pattern = ((PatternDescr) (accum.getInputPattern()));
        TestCase.assertEquals("Cheese", pattern.getObjectType());
    }

    @Test
    public void testAccumulateMultipleFunctionsConstraint() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "accumulateMultipleFunctionsConstraint.drl")));
        TestCase.assertEquals(1, pkg.getRules().size());
        RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
        PatternDescr out = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("Object", out.getObjectType());
        TestCase.assertEquals(2, getDescrs().size());
        TestCase.assertEquals("$a1 > 10 && $M1 <= 100", getDescrs().get(0).toString());
        TestCase.assertEquals("$m1 == 5", getDescrs().get(1).toString());
        AccumulateDescr accum = ((AccumulateDescr) (out.getSource()));
        TestCase.assertTrue(accum.isExternalFunction());
        List<AccumulateFunctionCallDescr> functions = accum.getFunctions();
        TestCase.assertEquals(3, functions.size());
        TestCase.assertEquals("average", functions.get(0).getFunction());
        TestCase.assertEquals("$a1", functions.get(0).getBind());
        TestCase.assertEquals("$price", functions.get(0).getParams()[0]);
        TestCase.assertEquals("min", functions.get(1).getFunction());
        TestCase.assertEquals("$m1", functions.get(1).getBind());
        TestCase.assertEquals("$price", functions.get(1).getParams()[0]);
        TestCase.assertEquals("max", functions.get(2).getFunction());
        TestCase.assertEquals("$M1", functions.get(2).getBind());
        TestCase.assertEquals("$price", functions.get(2).getParams()[0]);
        final PatternDescr pattern = ((PatternDescr) (accum.getInputPattern()));
        TestCase.assertEquals("Cheese", pattern.getObjectType());
    }

    @Test
    public void testOrCE() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "or_ce.drl")));
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(2, getDescrs().size());
        final PatternDescr person = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("Person", person.getObjectType());
        TestCase.assertEquals("$p", person.getIdentifier());
        final OrDescr or = ((OrDescr) (getDescrs().get(1)));
        TestCase.assertEquals(2, or.getDescrs().size());
        final PatternDescr cheese1 = ((PatternDescr) (or.getDescrs().get(0)));
        TestCase.assertEquals("Cheese", cheese1.getObjectType());
        TestCase.assertEquals("$c", cheese1.getIdentifier());
        final PatternDescr cheese2 = ((PatternDescr) (or.getDescrs().get(1)));
        TestCase.assertEquals("Cheese", cheese2.getObjectType());
        TestCase.assertNull(cheese2.getIdentifier());
    }

    @Test
    public void testRuleSingleLine() throws Exception {
        final String text = "rule \"another test\" salience 10 when eval( true ) then System.out.println(1); end";
        RuleDescr rule = ((RuleDescr) (parse("rule", text)));
        TestCase.assertEquals("another test", rule.getName());
        TestCase.assertEquals("System.out.println(1); ", rule.getConsequence());
    }

    @Test
    public void testRuleTwoLines() throws Exception {
        final String text = "rule \"another test\" salience 10 when eval( true ) then System.out.println(1);\n end";
        RuleDescr rule = ((RuleDescr) (parse("rule", text)));
        TestCase.assertEquals("another test", rule.getName());
        TestCase.assertEquals("System.out.println(1);\n ", rule.getConsequence());
    }

    @Test
    public void testRuleParseLhs3() throws Exception {
        final String text = "rule X when (or\nnot Person()\n(and Cheese()\nMeat()\nWine())) then end";
        AndDescr pattern = getLhs();
        TestCase.assertEquals(1, pattern.getDescrs().size());
        OrDescr or = ((OrDescr) (pattern.getDescrs().get(0)));
        TestCase.assertEquals(2, or.getDescrs().size());
        NotDescr not = ((NotDescr) (or.getDescrs().get(0)));
        AndDescr and = ((AndDescr) (or.getDescrs().get(1)));
        TestCase.assertEquals(1, not.getDescrs().size());
        PatternDescr person = ((PatternDescr) (not.getDescrs().get(0)));
        TestCase.assertEquals("Person", person.getObjectType());
        TestCase.assertEquals(3, and.getDescrs().size());
        PatternDescr cheese = ((PatternDescr) (and.getDescrs().get(0)));
        TestCase.assertEquals("Cheese", cheese.getObjectType());
        PatternDescr meat = ((PatternDescr) (and.getDescrs().get(1)));
        TestCase.assertEquals("Meat", meat.getObjectType());
        PatternDescr wine = ((PatternDescr) (and.getDescrs().get(2)));
        TestCase.assertEquals("Wine", wine.getObjectType());
    }

    @Test
    public void testAccumulateMultiPattern() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "accumulate_multi_pattern.drl")));
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(1, getDescrs().size());
        final PatternDescr outPattern = ((PatternDescr) (getDescrs().get(0)));
        final AccumulateDescr accum = ((AccumulateDescr) (outPattern.getSource()));
        assertEqualsIgnoreWhitespace("$counter", outPattern.getIdentifier());
        assertEqualsIgnoreWhitespace("int x = 0 ;", accum.getInitCode());
        assertEqualsIgnoreWhitespace("x++;", accum.getActionCode());
        assertEqualsIgnoreWhitespace("new Integer(x)", accum.getResultCode());
        final AndDescr and = ((AndDescr) (accum.getInput()));
        TestCase.assertEquals(2, and.getDescrs().size());
        final PatternDescr person = ((PatternDescr) (and.getDescrs().get(0)));
        final PatternDescr cheese = ((PatternDescr) (and.getDescrs().get(1)));
        TestCase.assertEquals("Person", person.getObjectType());
        TestCase.assertEquals("Cheese", cheese.getObjectType());
    }

    @Test
    public void testPluggableOperators() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "pluggable_operators.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        TestCase.assertEquals(1, pkg.getRules().size());
        final RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals(5, getDescrs().size());
        final PatternDescr eventA = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("$a", eventA.getIdentifier());
        TestCase.assertEquals("EventA", eventA.getObjectType());
        final PatternDescr eventB = ((PatternDescr) (getDescrs().get(1)));
        TestCase.assertEquals("$b", eventB.getIdentifier());
        TestCase.assertEquals("EventB", eventB.getObjectType());
        TestCase.assertEquals(1, getDescrs().size());
        TestCase.assertEquals(1, getDescrs().size());
        final ExprConstraintDescr fcdB = ((ExprConstraintDescr) (getDescrs().get(0)));
        TestCase.assertEquals("this after[1,10] $a || this not after[15,20] $a", fcdB.getExpression());
        final PatternDescr eventC = ((PatternDescr) (getDescrs().get(2)));
        TestCase.assertEquals("$c", eventC.getIdentifier());
        TestCase.assertEquals("EventC", eventC.getObjectType());
        TestCase.assertEquals(1, getDescrs().size());
        final ExprConstraintDescr fcdC = ((ExprConstraintDescr) (getDescrs().get(0)));
        TestCase.assertEquals("this finishes $b", fcdC.getExpression());
        final PatternDescr eventD = ((PatternDescr) (getDescrs().get(3)));
        TestCase.assertEquals("$d", eventD.getIdentifier());
        TestCase.assertEquals("EventD", eventD.getObjectType());
        TestCase.assertEquals(1, getDescrs().size());
        final ExprConstraintDescr fcdD = ((ExprConstraintDescr) (getDescrs().get(0)));
        TestCase.assertEquals("this not starts $a", fcdD.getExpression());
        final PatternDescr eventE = ((PatternDescr) (getDescrs().get(4)));
        TestCase.assertEquals("$e", eventE.getIdentifier());
        TestCase.assertEquals("EventE", eventE.getObjectType());
        TestCase.assertEquals(1, getDescrs().size());
        ExprConstraintDescr fcdE = ((ExprConstraintDescr) (getDescrs().get(0)));
        TestCase.assertEquals("this not before[1, 10] $b || after[1, 10] $c && this after[1, 5] $d", fcdE.getExpression());
    }

    @Test
    public void testRuleMetadata() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "Rule_with_Metadata.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        // @fooAttribute(barValue)
        // @fooAtt2(barVal2)
        RuleDescr rule = pkg.getRules().get(0);
        TestCase.assertTrue(rule.getAnnotationNames().contains("fooMeta1"));
        TestCase.assertEquals("barVal1", rule.getAnnotation("fooMeta1").getValue());
        TestCase.assertTrue(rule.getAnnotationNames().contains("fooMeta2"));
        TestCase.assertEquals("barVal2", rule.getAnnotation("fooMeta2").getValue());
        assertEqualsIgnoreWhitespace("System.out.println(\"Consequence\");", ((String) (rule.getConsequence())));
    }

    @Test
    public void testRuleExtends() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "Rule_with_Extends.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        RuleDescr rule = pkg.getRules().get(0);
        TestCase.assertTrue(((rule.getParentName()) != null));
        TestCase.assertEquals("rule1", rule.getParentName());
        AndDescr lhs = rule.getLhs();
        TestCase.assertNotNull(lhs);
        TestCase.assertEquals(1, lhs.getDescrs().size());
        PatternDescr pattern = ((PatternDescr) (lhs.getDescrs().get(0)));
        TestCase.assertEquals("foo", pattern.getObjectType());
        TestCase.assertEquals("$foo", pattern.getIdentifier());
    }

    @Test
    public void testTypeDeclarationWithFields() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "declare_type_with_fields.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        List<TypeDeclarationDescr> td = pkg.getTypeDeclarations();
        TestCase.assertEquals(3, td.size());
        TypeDeclarationDescr d = td.get(0);
        TestCase.assertEquals("SomeFact", d.getTypeName());
        TestCase.assertEquals(2, d.getFields().size());
        TestCase.assertTrue(d.getFields().containsKey("name"));
        TestCase.assertTrue(d.getFields().containsKey("age"));
        TypeFieldDescr f = d.getFields().get("name");
        TestCase.assertEquals("String", f.getPattern().getObjectType());
        f = d.getFields().get("age");
        TestCase.assertEquals("Integer", f.getPattern().getObjectType());
        d = td.get(1);
        TestCase.assertEquals("AnotherFact", d.getTypeName());
        TypeDeclarationDescr type = td.get(2);
        TestCase.assertEquals("Person", type.getTypeName());
        TestCase.assertEquals("fact", type.getAnnotation("role").getValue());
        TestCase.assertEquals("\"Models a person\"", type.getAnnotation("doc").getValue("descr"));
        TestCase.assertEquals("\"Bob\"", type.getAnnotation("doc").getValue("author"));
        TestCase.assertEquals("Calendar.getInstance().getDate()", type.getAnnotation("doc").getValue("date"));
        TestCase.assertEquals(2, type.getFields().size());
        TypeFieldDescr field = type.getFields().get("name");
        TestCase.assertEquals("name", field.getFieldName());
        TestCase.assertEquals("String", field.getPattern().getObjectType());
        TestCase.assertEquals("\"John Doe\"", field.getInitExpr());
        TestCase.assertEquals("50", field.getAnnotation("length").getValue("max"));
        TestCase.assertNotNull(field.getAnnotation("key"));
        field = type.getFields().get("age");
        TestCase.assertEquals("age", field.getFieldName());
        TestCase.assertEquals("int", field.getPattern().getObjectType());
        TestCase.assertEquals("-1", field.getInitExpr());
        TestCase.assertEquals("0", field.getAnnotation("ranged").getValue("min"));
        TestCase.assertEquals("150", field.getAnnotation("ranged").getValue("max"));
        TestCase.assertEquals("-1", field.getAnnotation("ranged").getValue("unknown"));
    }

    @Test
    public void testRuleWithLHSNesting() throws Exception {
        final PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "Rule_with_nested_LHS.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        RuleDescr rule = pkg.getRules().get(0);
        TestCase.assertEquals("test", rule.getName());
        AndDescr lhs = rule.getLhs();
        TestCase.assertNotNull(lhs);
        TestCase.assertEquals(2, lhs.getDescrs().size());
        PatternDescr a = ((PatternDescr) (lhs.getDescrs().get(0)));
        TestCase.assertEquals("A", a.getObjectType());
        OrDescr or = ((OrDescr) (lhs.getDescrs().get(1)));
        TestCase.assertEquals(3, or.getDescrs().size());
        AndDescr and1 = ((AndDescr) (or.getDescrs().get(0)));
        TestCase.assertEquals(2, and1.getDescrs().size());
        PatternDescr b = ((PatternDescr) (and1.getDescrs().get(0)));
        PatternDescr c = ((PatternDescr) (and1.getDescrs().get(1)));
        TestCase.assertEquals("B", b.getObjectType());
        TestCase.assertEquals("C", c.getObjectType());
        AndDescr and2 = ((AndDescr) (or.getDescrs().get(1)));
        TestCase.assertEquals(2, and2.getDescrs().size());
        PatternDescr d = ((PatternDescr) (and2.getDescrs().get(0)));
        PatternDescr e = ((PatternDescr) (and2.getDescrs().get(1)));
        TestCase.assertEquals("D", d.getObjectType());
        TestCase.assertEquals("E", e.getObjectType());
        AndDescr and3 = ((AndDescr) (or.getDescrs().get(2)));
        TestCase.assertEquals(2, and3.getDescrs().size());
        PatternDescr f = ((PatternDescr) (and3.getDescrs().get(0)));
        PatternDescr g = ((PatternDescr) (and3.getDescrs().get(1)));
        TestCase.assertEquals("F", f.getObjectType());
        TestCase.assertEquals("G", g.getObjectType());
    }

    @Test
    public void testEntryPoint() throws Exception {
        final String text = "rule X when StockTick( symbol==\"ACME\") from entry-point StreamA then end";
        PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", text)));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        RuleDescr rule = pkg.getRules().get(0);
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals(1, pattern.getDescrs().size());
        ExprConstraintDescr fcd = ((ExprConstraintDescr) (pattern.getDescrs().get(0)));
        TestCase.assertEquals("symbol==\"ACME\"", fcd.getExpression());
        TestCase.assertNotNull(pattern.getSource());
        EntryPointDescr entry = ((EntryPointDescr) (pattern.getSource()));
        TestCase.assertEquals("StreamA", entry.getEntryId());
    }

    @Test
    public void testEntryPoint2() throws Exception {
        final String text = "rule X when StockTick( symbol==\"ACME\") from entry-point \"StreamA\" then end";
        PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", text)));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        RuleDescr rule = pkg.getRules().get(0);
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals(1, pattern.getDescrs().size());
        ExprConstraintDescr fcd = ((ExprConstraintDescr) (pattern.getDescrs().get(0)));
        TestCase.assertEquals("symbol==\"ACME\"", fcd.getExpression());
        TestCase.assertNotNull(pattern.getSource());
        EntryPointDescr entry = ((EntryPointDescr) (pattern.getSource()));
        TestCase.assertEquals("StreamA", entry.getEntryId());
    }

    @Test
    public void testSlidingWindow() throws Exception {
        final String text = "rule X when StockTick( symbol==\"ACME\") over window:length(10) then end";
        PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", text)));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        RuleDescr rule = pkg.getRules().get(0);
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals(1, pattern.getDescrs().size());
        ExprConstraintDescr fcd = ((ExprConstraintDescr) (pattern.getDescrs().get(0)));
        TestCase.assertEquals("symbol==\"ACME\"", fcd.getExpression());
        List<BehaviorDescr> behaviors = pattern.getBehaviors();
        TestCase.assertNotNull(behaviors);
        TestCase.assertEquals(1, behaviors.size());
        BehaviorDescr descr = behaviors.get(0);
        TestCase.assertEquals("window", descr.getType());
        TestCase.assertEquals("length", descr.getSubType());
        TestCase.assertEquals("10", descr.getParameters().get(0));
    }

    @Test
    public void testRuleOldSyntax1() throws Exception {
        final String source = "rule \"Test\" when ( not $r :LiteralRestriction( operator == Operator.EQUAL ) ) then end";
        PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", source)));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals("Test", rule.getName());
        TestCase.assertEquals(1, getDescrs().size());
        TestCase.assertEquals(1, getDescrs().size());
        NotDescr notDescr = ((NotDescr) (getDescrs().get(0)));
        PatternDescr patternDescr = ((PatternDescr) (notDescr.getDescrs().get(0)));
        TestCase.assertEquals("$r", patternDescr.getIdentifier());
        TestCase.assertEquals(1, patternDescr.getDescrs().size());
        ExprConstraintDescr fieldConstraintDescr = ((ExprConstraintDescr) (patternDescr.getDescrs().get(0)));
        TestCase.assertEquals("operator == Operator.EQUAL", fieldConstraintDescr.getExpression());
    }

    @Test
    public void testRuleOldSyntax2() throws Exception {
        final String source = "rule \"Test\" when ( $r :LiteralRestriction( operator == Operator.EQUAL ) ) then end";
        PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", source)));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        RuleDescr rule = ((RuleDescr) (pkg.getRules().get(0)));
        TestCase.assertEquals("Test", rule.getName());
        TestCase.assertEquals(1, getDescrs().size());
        PatternDescr patternDescr = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("$r", patternDescr.getIdentifier());
        TestCase.assertEquals(1, patternDescr.getDescrs().size());
        ExprConstraintDescr fieldConstraintDescr = ((ExprConstraintDescr) (patternDescr.getDescrs().get(0)));
        TestCase.assertEquals("operator == Operator.EQUAL", fieldConstraintDescr.getExpression());
    }

    @Test
    public void testTypeWithMetaData() throws Exception {
        PackageDescr pkg = ((PackageDescr) (parseResource("compilationUnit", "type_with_meta.drl")));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        final List<TypeDeclarationDescr> declarations = pkg.getTypeDeclarations();
        TestCase.assertEquals(3, declarations.size());
    }

    @Test
    public void testNullConstraints() throws Exception {
        final String text = "rule X when Person( name == null ) then end";
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals(1, pattern.getDescrs().size());
        ExprConstraintDescr fcd = ((ExprConstraintDescr) (pattern.getDescrs().get(0)));
        TestCase.assertEquals("name == null", fcd.getExpression());
        TestCase.assertEquals(0, fcd.getPosition());
        TestCase.assertEquals(NAMED, fcd.getType());
    }

    @Test
    public void testPositionalConstraintsOnly() throws Exception {
        final String text = "rule X when Person( \"Mark\", 42; ) then end";
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals(2, pattern.getDescrs().size());
        ExprConstraintDescr fcd = ((ExprConstraintDescr) (pattern.getDescrs().get(0)));
        TestCase.assertEquals("\"Mark\"", fcd.getExpression());
        TestCase.assertEquals(0, fcd.getPosition());
        TestCase.assertEquals(POSITIONAL, fcd.getType());
        fcd = ((ExprConstraintDescr) (pattern.getDescrs().get(1)));
        TestCase.assertEquals("42", fcd.getExpression());
        TestCase.assertEquals(1, fcd.getPosition());
        TestCase.assertEquals(POSITIONAL, fcd.getType());
    }

    @Test
    public void testIsQuery() throws Exception {
        final String text = "rule X when ?person( \"Mark\", 42; ) then end";
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertTrue(pattern.isQuery());
        TestCase.assertEquals(2, pattern.getDescrs().size());
        ExprConstraintDescr fcd = ((ExprConstraintDescr) (pattern.getDescrs().get(0)));
        TestCase.assertEquals("\"Mark\"", fcd.getExpression());
        TestCase.assertEquals(0, fcd.getPosition());
        TestCase.assertEquals(POSITIONAL, fcd.getType());
        fcd = ((ExprConstraintDescr) (pattern.getDescrs().get(1)));
        TestCase.assertEquals("42", fcd.getExpression());
        TestCase.assertEquals(1, fcd.getPosition());
        TestCase.assertEquals(POSITIONAL, fcd.getType());
    }

    @Test
    public void testFromFollowedByQuery() throws Exception {
        // the 'from' expression requires a ";" to disambiguate the "?"
        // prefix for queries from the ternary operator "? :"
        final String text = "rule X when Cheese() from $cheesery ?person( \"Mark\", 42; ) then end";
        RuleDescr rule = ((RuleDescr) (parse("rule", text)));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("Cheese", pattern.getObjectType());
        TestCase.assertEquals("from $cheesery", pattern.getSource().getText());
        TestCase.assertFalse(pattern.isQuery());
        pattern = ((PatternDescr) (getDescrs().get(1)));
        TestCase.assertEquals("person", pattern.getObjectType());
        TestCase.assertTrue(pattern.isQuery());
    }

    @Test
    public void testFromWithTernaryFollowedByQuery() throws Exception {
        // the 'from' expression requires a ";" to disambiguate the "?"
        // prefix for queries from the ternary operator "? :"
        final String text = "rule X when Cheese() from (isFull ? $cheesery : $market) ?person( \"Mark\", 42; ) then end";
        RuleDescr rule = ((RuleDescr) (parse("rule", text)));
        TestCase.assertFalse(parser.getErrors().toString(), parser.hasErrors());
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("Cheese", pattern.getObjectType());
        TestCase.assertEquals("from (isFull ? $cheesery : $market)", pattern.getSource().getText());
        TestCase.assertFalse(pattern.isQuery());
        pattern = ((PatternDescr) (getDescrs().get(1)));
        TestCase.assertEquals("person", pattern.getObjectType());
        TestCase.assertTrue(pattern.isQuery());
    }

    @Test
    public void testMultiValueAnnotationsBackwardCompatibility() throws Exception {
        // multiple values with no keys are parsed as a single value
        final String text = "rule X @ann1( val1, val2 ) @ann2( \"val1\", \"val2\" ) when then end";
        RuleDescr rule = ((RuleDescr) (parse("rule", text)));
        AnnotationDescr ann = rule.getAnnotation("ann1");
        TestCase.assertNotNull(ann);
        TestCase.assertEquals("val1, val2", ann.getValue());
        ann = rule.getAnnotation("ann2");
        TestCase.assertNotNull(ann);
        TestCase.assertEquals("\"val1\", \"val2\"", ann.getValue());
    }

    @Test
    public void testPositionalsAndNamedConstraints() throws Exception {
        final String text = "rule X when Person( \"Mark\", 42; location == \"atlanta\" ) then end";
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals(3, pattern.getDescrs().size());
        ExprConstraintDescr fcd = ((ExprConstraintDescr) (pattern.getDescrs().get(0)));
        TestCase.assertEquals("\"Mark\"", fcd.getExpression());
        TestCase.assertEquals(0, fcd.getPosition());
        TestCase.assertEquals(POSITIONAL, fcd.getType());
        fcd = ((ExprConstraintDescr) (pattern.getDescrs().get(1)));
        TestCase.assertEquals("42", fcd.getExpression());
        TestCase.assertEquals(1, fcd.getPosition());
        TestCase.assertEquals(POSITIONAL, fcd.getType());
        fcd = ((ExprConstraintDescr) (pattern.getDescrs().get(2)));
        TestCase.assertEquals("location == \"atlanta\"", fcd.getExpression());
        TestCase.assertEquals(2, fcd.getPosition());
        TestCase.assertEquals(NAMED, fcd.getType());
    }

    @Test
    public void testUnificationBinding() throws Exception {
        final String text = "rule X when $p := Person( $name := name, $loc : location ) then end";
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("$p", pattern.getIdentifier());
        TestCase.assertTrue(pattern.isUnification());
        TestCase.assertEquals(2, pattern.getDescrs().size());
        ExprConstraintDescr bindingDescr = ((ExprConstraintDescr) (pattern.getDescrs().get(0)));
        TestCase.assertEquals("$name := name", bindingDescr.getExpression());
        bindingDescr = ((ExprConstraintDescr) (pattern.getDescrs().get(1)));
        TestCase.assertEquals("$loc : location", bindingDescr.getExpression());
    }

    @Test
    public void testBigLiterals() throws Exception {
        final String text = "rule X when Primitives( bigInteger == (10I), " + (("                        bigDecimal == (10B), " + "                        bigInteger < 50I, ") + "                        bigDecimal < 50B ) then end");
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals(4, pattern.getDescrs().size());
        ExprConstraintDescr ecd = ((ExprConstraintDescr) (pattern.getDescrs().get(0)));
        TestCase.assertEquals("bigInteger == (10I)", ecd.getExpression());
        ecd = ((ExprConstraintDescr) (pattern.getDescrs().get(1)));
        TestCase.assertEquals("bigDecimal == (10B)", ecd.getExpression());
        ecd = ((ExprConstraintDescr) (pattern.getDescrs().get(2)));
        TestCase.assertEquals("bigInteger < 50I", ecd.getExpression());
        ecd = ((ExprConstraintDescr) (pattern.getDescrs().get(3)));
        TestCase.assertEquals("bigDecimal < 50B", ecd.getExpression());
    }

    @Test
    public void testBindingComposite() throws Exception {
        final String text = "rule X when Person( $name : name == \"Bob\" || $loc : location == \"Montreal\" ) then end";
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("Person", pattern.getObjectType());
        TestCase.assertFalse(pattern.isUnification());
        // assertEquals( 2,
        // pattern.getDescrs().size() );
        // BindingDescr bindingDescr = pattern.getDescrs().get( 0 );
        // assertEquals( "$name",
        // bindingDescr.getVariable() );
        // assertEquals( "name",
        // bindingDescr.getExpression() );
        // assertFalse( bindingDescr.isUnification() );
        // 
        // bindingDescr = pattern.getDescrs().get( 1 );
        // assertEquals( "$loc",
        // bindingDescr.getVariable() );
        // assertEquals( "location",
        // bindingDescr.getExpression() );
        // assertFalse( bindingDescr.isUnification() );
        // embedded bindings are extracted at compile time
        List<?> constraints = pattern.getDescrs();
        TestCase.assertEquals(1, constraints.size());
        TestCase.assertEquals("$name : name == \"Bob\" || $loc : location == \"Montreal\"", getExpression());
    }

    @Test
    public void testBindingCompositeWithMethods() throws Exception {
        final String text = "rule X when Person( $name : name.toUpperCase() == \"Bob\" || $loc : location[0].city == \"Montreal\" ) then end";
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("Person", pattern.getObjectType());
        TestCase.assertFalse(pattern.isUnification());
        // assertEquals( 2,
        // pattern.getDescrs().size() );
        // BindingDescr bindingDescr = pattern.getDescrs().get( 0 );
        // assertEquals( "$name",
        // bindingDescr.getVariable() );
        // assertEquals( "name.toUpperCase()",
        // bindingDescr.getExpression() );
        // assertFalse( bindingDescr.isUnification() );
        // 
        // bindingDescr = pattern.getDescrs().get( 1 );
        // assertEquals( "$loc",
        // bindingDescr.getVariable() );
        // assertEquals( "location[0].city",
        // bindingDescr.getExpression() );
        // assertFalse( bindingDescr.isUnification() );
        // embedded bindings are extracted at compile time
        List<?> constraints = pattern.getDescrs();
        TestCase.assertEquals(1, constraints.size());
        TestCase.assertEquals("$name : name.toUpperCase() == \"Bob\" || $loc : location[0].city == \"Montreal\"", getExpression());
    }

    @Test
    public void testPluggableOperators2() throws Exception {
        final String text = "rule \"tt\"\n" + (((("    dialect \"mvel\"\n" + "when\n") + "    exists (TelephoneCall( this finishes [1m] \"25-May-2011\" ))\n") + "then\n") + "end");
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("TelephoneCall", pattern.getObjectType());
        ExprConstraintDescr constr = ((ExprConstraintDescr) (getDescrs().get(0)));
        TestCase.assertEquals("this finishes [1m] \"25-May-2011\"", constr.getText());
    }

    @Test
    public void testInlineEval() throws Exception {
        final String text = "rule \"inline eval\"\n" + ((("when\n" + "    Person( eval( name.startsWith(\"b\") && name.finishesWith(\"b\")) )\n") + "then\n") + "end");
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("Person", pattern.getObjectType());
        ExprConstraintDescr constr = ((ExprConstraintDescr) (getDescrs().get(0)));
        TestCase.assertEquals("eval( name.startsWith(\"b\") && name.finishesWith(\"b\"))", constr.getText());
    }

    @Test
    public void testInfinityLiteral() throws Exception {
        final String text = "rule \"infinity\"\n" + ((("when\n" + "    StockTick( this after[-*,*] $another )\n") + "then\n") + "end");
        PatternDescr pattern = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertEquals("StockTick", pattern.getObjectType());
        ExprConstraintDescr constr = ((ExprConstraintDescr) (getDescrs().get(0)));
        TestCase.assertEquals("this after[-*,*] $another", constr.getText());
    }

    @Test
    public void testEntryPointDeclaration() throws Exception {
        final String text = "package org.drools\n" + ((("declare entry-point eventStream\n" + "    @source(\"jndi://queues/events\")\n") + "    @foo( true )\n") + "end");
        PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", text)));
        TestCase.assertEquals("org.drools", pkg.getName());
        TestCase.assertEquals(1, pkg.getEntryPointDeclarations().size());
        EntryPointDeclarationDescr epd = pkg.getEntryPointDeclarations().iterator().next();
        TestCase.assertEquals("eventStream", epd.getEntryPointId());
        TestCase.assertEquals(2, epd.getAnnotations().size());
        TestCase.assertEquals("\"jndi://queues/events\"", epd.getAnnotation("source").getValue());
        TestCase.assertEquals("true", epd.getAnnotation("foo").getValue());
    }

    @Test
    public void testWindowDeclaration() throws Exception {
        final String text = "package org.drools\n" + ((((("declare window Ticks\n" + "    @doc(\"last 10 stock ticks\")\n") + "    $s : StockTick( source == \"NYSE\" )\n") + "        over window:length( 10, $s.symbol )\n") + "        from entry-point stStream\n") + "end");
        PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", text)));
        TestCase.assertEquals("org.drools", pkg.getName());
        TestCase.assertEquals(1, pkg.getWindowDeclarations().size());
        WindowDeclarationDescr wdd = pkg.getWindowDeclarations().iterator().next();
        TestCase.assertEquals("Ticks", wdd.getName());
        TestCase.assertEquals(1, wdd.getAnnotations().size());
        TestCase.assertEquals("\"last 10 stock ticks\"", wdd.getAnnotation("doc").getValue());
        PatternDescr pd = wdd.getPattern();
        TestCase.assertNotNull(pd);
        TestCase.assertEquals("$s", pd.getIdentifier());
        TestCase.assertEquals("StockTick", pd.getObjectType());
        TestCase.assertEquals("stStream", pd.getSource().getText());
        TestCase.assertEquals(1, pd.getBehaviors().size());
        BehaviorDescr bd = pd.getBehaviors().get(0);
        TestCase.assertEquals("window", bd.getType());
        TestCase.assertEquals("length", bd.getSubType());
        TestCase.assertEquals(2, bd.getParameters().size());
        TestCase.assertEquals("10", bd.getParameters().get(0));
        TestCase.assertEquals("$s.symbol", bd.getParameters().get(1));
    }

    @Test
    public void testWindowUsage() throws Exception {
        final String text = "package org.drools\n" + (((("rule X\n" + "when\n") + "    StockTick() from window Y\n") + "then\n") + "end\n");
        PackageDescr pkg = ((PackageDescr) (parse("compilationUnit", text)));
        TestCase.assertEquals("org.drools", pkg.getName());
        TestCase.assertEquals(1, pkg.getRules().size());
        RuleDescr rd = pkg.getRules().get(0);
        TestCase.assertEquals("X", rd.getName());
        TestCase.assertEquals(1, getDescrs().size());
        PatternDescr pd = ((PatternDescr) (getDescrs().get(0)));
        TestCase.assertNotNull(pd);
        TestCase.assertEquals("StockTick", pd.getObjectType());
        TestCase.assertEquals("Y", pd.getSource().getText());
    }
}

