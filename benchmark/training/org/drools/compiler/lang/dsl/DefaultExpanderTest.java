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
package org.drools.compiler.lang.dsl;


import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import org.drools.compiler.lang.ExpanderException;
import org.junit.Assert;
import org.junit.Test;


public class DefaultExpanderTest {
    private static final String NL = System.getProperty("line.separator");

    private DSLMappingFile file = null;

    private DSLTokenizedMappingFile tokenizedFile = null;

    private DefaultExpander expander = null;

    @Test
    public void testAddDSLMapping() {
        this.expander.addDSLMapping(this.file.getMapping());
        // should not raise any exception
    }

    @Test
    public void testANTLRAddDSLMapping() {
        this.expander.addDSLMapping(this.tokenizedFile.getMapping());
        // should not raise any exception
    }

    @Test
    public void testRegexp() throws Exception {
        this.expander.addDSLMapping(this.file.getMapping());
        final Reader rules = new InputStreamReader(this.getClass().getResourceAsStream("test_expansion.dslr"));
        final String result = this.expander.expand(rules);
    }

    @Test
    public void testANTLRRegexp() throws Exception {
        this.expander.addDSLMapping(this.tokenizedFile.getMapping());
        final Reader rules = new InputStreamReader(this.getClass().getResourceAsStream("test_expansion.dslr"));
        final String result = this.expander.expand(rules);
    }

    @Test
    public void testExpandParts() throws Exception {
        DSLMappingFile file = new DSLTokenizedMappingFile();
        String dsl = ("[when]foo=Foo()" + (DefaultExpanderTest.NL)) + "[then]bar {num}=baz({num});";
        file.parseAndLoad(new StringReader(dsl));
        Assert.assertEquals(0, file.getErrors().size());
        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping(file.getMapping());
        // System.err.println(ex.expand( "rule 'x' " + NL + " when " + NL + " foo " + NL + " then " + NL + " end" ));
    }

    @Test
    public void testExpandKeyword() throws Exception {
        DSLMappingFile file = new DSLTokenizedMappingFile();
        String dsl = "[keyword]key {param}=Foo( attr=={param} )";
        file.parseAndLoad(new StringReader(dsl));
        Assert.assertEquals(0, file.getErrors().size());
        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping(file.getMapping());
        String source = ((((((((("rule x" + (DefaultExpanderTest.NL)) + "when") + (DefaultExpanderTest.NL)) + " key 1 ") + (DefaultExpanderTest.NL)) + " key 2 ") + (DefaultExpanderTest.NL)) + "then") + (DefaultExpanderTest.NL)) + "end";
        String drl = ex.expand(source);
        System.out.println(drl);
        Assert.assertTrue(drl.contains("attr==1"));
        Assert.assertTrue(drl.contains("attr==2"));
        // System.err.println(ex.expand( "rule 'x' " + NL + " when " + NL + " foo " + NL + " then " + NL + " end" ));
    }

    @Test
    public void testANTLRExpandParts() throws Exception {
        DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
        String dsl = ("[when]foo=Foo()" + (DefaultExpanderTest.NL)) + "[then]bar {num}=baz({num});";
        file.parseAndLoad(new StringReader(dsl));
        Assert.assertEquals(0, file.getErrors().size());
        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping(file.getMapping());
        // System.err.println(ex.expand( "rule 'x' " + NL + " when " + NL + " foo " + NL + " then " + NL + " end" ));
    }

    @Test
    public void testExpandFailure() throws Exception {
        DSLMappingFile file = new DSLTokenizedMappingFile();
        String dsl = ("[when]foo=Foo()" + (DefaultExpanderTest.NL)) + "[then]bar {num}=baz({num});";
        file.parseAndLoad(new StringReader(dsl));
        Assert.assertEquals(0, file.getErrors().size());
        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping(file.getMapping());
        String source = ((((((((((("rule 'q'" + (DefaultExpanderTest.NL)) + "agenda-group 'x'") + (DefaultExpanderTest.NL)) + "when") + (DefaultExpanderTest.NL)) + "    foo  ") + (DefaultExpanderTest.NL)) + "then") + (DefaultExpanderTest.NL)) + "    bar 42") + (DefaultExpanderTest.NL)) + "end";
        String drl = ex.expand(source);
        Assert.assertFalse(ex.hasErrors());
        ex = new DefaultExpander();
        ex.addDSLMapping(file.getMapping());
        source = ((((((((("rule 'q' agenda-group 'x'" + (DefaultExpanderTest.NL)) + "when") + (DefaultExpanderTest.NL)) + "    foos ") + (DefaultExpanderTest.NL)) + "then") + (DefaultExpanderTest.NL)) + "    bar 42") + (DefaultExpanderTest.NL)) + " end";
        drl = ex.expand(source);
        // System.out.println( drl );
        Assert.assertTrue(ex.hasErrors());
        Assert.assertEquals(1, ex.getErrors().size());
        // System.err.println(( (ExpanderException) ex.getErrors().get( 0 )).getMessage());
    }

    @Test
    public void testANTLRExpandFailure() throws Exception {
        DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
        String dsl = ("[when]foo=Foo()" + (DefaultExpanderTest.NL)) + "[then]bar {num}=baz({num});";
        file.parseAndLoad(new StringReader(dsl));
        Assert.assertEquals(0, file.getErrors().size());
        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping(file.getMapping());
        String source = ((((((((((("rule 'q'" + (DefaultExpanderTest.NL)) + "agenda-group 'x'") + (DefaultExpanderTest.NL)) + "when") + (DefaultExpanderTest.NL)) + "    foo  ") + (DefaultExpanderTest.NL)) + "then") + (DefaultExpanderTest.NL)) + "    bar 42") + (DefaultExpanderTest.NL)) + "end";
        String drl = ex.expand(source);
        Assert.assertFalse(ex.hasErrors());
        ex = new DefaultExpander();
        ex.addDSLMapping(file.getMapping());
        source = ((((((((("rule 'q' agenda-group 'x'" + (DefaultExpanderTest.NL)) + "when") + (DefaultExpanderTest.NL)) + "    foos ") + (DefaultExpanderTest.NL)) + "then") + (DefaultExpanderTest.NL)) + "    bar 42") + (DefaultExpanderTest.NL)) + " end";
        drl = ex.expand(source);
        // System.out.println( drl );
        Assert.assertTrue(ex.hasErrors());
        Assert.assertEquals(1, ex.getErrors().size());
        // System.err.println(( (ExpanderException) ex.getErrors().get( 0 )).getMessage());
    }

    @Test
    public void testExpandWithKeywordClashes() throws Exception {
        DSLMappingFile file = new DSLTokenizedMappingFile();
        String dsl = ("[when]Invoke rule executor=ruleExec: RuleExecutor()" + (DefaultExpanderTest.NL)) + "[then]Execute rule \"{id}\"=ruleExec.ExecuteSubRule( new Long({id}));";
        file.parseAndLoad(new StringReader(dsl));
        Assert.assertEquals(0, file.getErrors().size());
        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping(file.getMapping());
        String source = (((((((((((("package something;" + (DefaultExpanderTest.NL)) + (DefaultExpanderTest.NL)) + "rule \"1\"") + (DefaultExpanderTest.NL)) + "when") + (DefaultExpanderTest.NL)) + "    Invoke rule executor") + (DefaultExpanderTest.NL)) + "then") + (DefaultExpanderTest.NL)) + "    Execute rule \"5\"") + (DefaultExpanderTest.NL)) + "end";
        String expected = ((((((((((((("package something;" + (DefaultExpanderTest.NL)) + (DefaultExpanderTest.NL)) + "rule \"1\"") + (DefaultExpanderTest.NL)) + "when") + (DefaultExpanderTest.NL)) + "   ruleExec: RuleExecutor()") + (DefaultExpanderTest.NL)) + "then") + (DefaultExpanderTest.NL)) + "   ruleExec.ExecuteSubRule( new Long(5));") + (DefaultExpanderTest.NL)) + "end") + (DefaultExpanderTest.NL);
        String drl = ex.expand(source);
        // System.out.println("["+drl+"]" );
        // System.out.println("["+expected+"]" );
        Assert.assertFalse(ex.hasErrors());
        equalsIgnoreWhiteSpace(expected, drl);
    }

    @Test
    public void testANTLRExpandWithKeywordClashes() throws Exception {
        DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
        String dsl = ("[when]Invoke rule executor=ruleExec: RuleExecutor()" + (DefaultExpanderTest.NL)) + "[then]Execute rule \"{id}\"=ruleExec.ExecuteSubRule( new Long({id}));";
        file.parseAndLoad(new StringReader(dsl));
        Assert.assertEquals(0, file.getErrors().size());
        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping(file.getMapping());
        String source = (((((((((((("package something;" + (DefaultExpanderTest.NL)) + (DefaultExpanderTest.NL)) + "rule \"1\"") + (DefaultExpanderTest.NL)) + "when") + (DefaultExpanderTest.NL)) + "    Invoke rule executor") + (DefaultExpanderTest.NL)) + "then") + (DefaultExpanderTest.NL)) + "    Execute rule \"5\"") + (DefaultExpanderTest.NL)) + "end";
        String expected = (((((((((((("package something;" + (DefaultExpanderTest.NL)) + (DefaultExpanderTest.NL)) + "rule \"1\"") + (DefaultExpanderTest.NL)) + "when") + (DefaultExpanderTest.NL)) + "    ruleExec: RuleExecutor()") + (DefaultExpanderTest.NL)) + "then") + (DefaultExpanderTest.NL)) + "    ruleExec.ExecuteSubRule( new Long(5));") + (DefaultExpanderTest.NL)) + "end";
        String drl = ex.expand(source);
        // System.out.println("["+drl+"]" );
        // System.out.println("["+expected+"]" );
        Assert.assertFalse(ex.hasErrors());
        Assert.assertEquals(expected, drl);
    }

    @Test
    public void testLineNumberError() throws Exception {
        DSLMappingFile file = new DSLTokenizedMappingFile();
        String dsl = ("[when]foo=Foo()" + (DefaultExpanderTest.NL)) + "[then]bar {num}=baz({num});";
        file.parseAndLoad(new StringReader(dsl));
        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping(file.getMapping());
        String source = ((((((((((((("rule 'q'" + (DefaultExpanderTest.NL)) + "agenda-group 'x'") + (DefaultExpanderTest.NL)) + "when") + (DefaultExpanderTest.NL)) + "    __  ") + (DefaultExpanderTest.NL)) + "then") + (DefaultExpanderTest.NL)) + "    bar 42") + (DefaultExpanderTest.NL)) + "\tgoober") + (DefaultExpanderTest.NL)) + "end";
        ex.expand(source);
        Assert.assertTrue(ex.hasErrors());
        Assert.assertEquals(2, ex.getErrors().size());
        ExpanderException err = ((ExpanderException) (ex.getErrors().get(0)));
        Assert.assertEquals(4, err.getLine());
        err = ((ExpanderException) (ex.getErrors().get(1)));
        Assert.assertEquals(7, err.getLine());
    }

    @Test
    public void testANTLRLineNumberError() throws Exception {
        DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
        String dsl = ("[when]foo=Foo()" + (DefaultExpanderTest.NL)) + "[then]bar {num}=baz({num});";
        file.parseAndLoad(new StringReader(dsl));
        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping(file.getMapping());
        String source = ((((((((((((("rule 'q'" + (DefaultExpanderTest.NL)) + "agenda-group 'x'") + (DefaultExpanderTest.NL)) + "when") + (DefaultExpanderTest.NL)) + "    __  ") + (DefaultExpanderTest.NL)) + "then") + (DefaultExpanderTest.NL)) + "    bar 42") + (DefaultExpanderTest.NL)) + "\tgoober") + (DefaultExpanderTest.NL)) + "end";
        ex.expand(source);
        Assert.assertTrue(ex.hasErrors());
        Assert.assertEquals(2, ex.getErrors().size());
        ExpanderException err = ((ExpanderException) (ex.getErrors().get(0)));
        Assert.assertEquals(4, err.getLine());
        err = ((ExpanderException) (ex.getErrors().get(1)));
        Assert.assertEquals(7, err.getLine());
    }

    @Test
    public void testANTLREnumExpand() throws Exception {
        DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
        String dsl = "[when]When the credit rating is {rating:ENUM:Applicant.creditRating} = applicant:Applicant(credit=={rating})";
        file.parseAndLoad(new StringReader(dsl));
        Assert.assertEquals(0, file.getErrors().size());
        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping(file.getMapping());
        String source = ((((((((("rule \"TestNewDslSetup\"" + (DefaultExpanderTest.NL)) + "dialect \"mvel\"") + (DefaultExpanderTest.NL)) + "when") + (DefaultExpanderTest.NL)) + "When the credit rating is AA") + (DefaultExpanderTest.NL)) + "then ") + (DefaultExpanderTest.NL)) + "end";
        // String source="rule \"TestNewDslSetup\"" + NL+
        // "dialect \"mvel\"" + NL+
        // "when" + NL+
        // "When the credit rating is OK" + NL+
        // "then" + NL+
        // "end" + NL;
        String drl = ex.expand(source);
        String expected = ((((((((("rule \"TestNewDslSetup\"" + (DefaultExpanderTest.NL)) + "dialect \"mvel\"") + (DefaultExpanderTest.NL)) + "when") + (DefaultExpanderTest.NL)) + "applicant:Applicant(credit==AA)") + (DefaultExpanderTest.NL)) + "then  ") + (DefaultExpanderTest.NL)) + "end";
        Assert.assertFalse(ex.getErrors().toString(), ex.hasErrors());
        Assert.assertEquals(expected, drl);
        // System.err.println(ex.expand( "rule 'x' " + NL + " when " + NL + " foo " + NL + " then " + NL + " end" ));
    }

    @Test
    public void testExpandComplex() throws Exception {
        String source = (((((((((((((("rule \"R\"" + (DefaultExpanderTest.NL)) + "dialect \"mvel\"") + (DefaultExpanderTest.NL)) + "when") + (DefaultExpanderTest.NL)) + "There is an TestObject") + (DefaultExpanderTest.NL)) + "-startDate is before 01-Jul-2011") + (DefaultExpanderTest.NL)) + "-endDate is after 01-Jul-2011") + (DefaultExpanderTest.NL)) + "then") + (DefaultExpanderTest.NL)) + "end") + (DefaultExpanderTest.NL);
        String expected = (((((((((("rule \"R\"" + (DefaultExpanderTest.NL)) + "dialect \"mvel\"") + (DefaultExpanderTest.NL)) + "when") + (DefaultExpanderTest.NL)) + "TestObject(startDate>DateUtils.parseDate(\"01-Jul-2011\"), endDate>DateUtils.parseDate(\"01-Jul-2011\"))") + (DefaultExpanderTest.NL)) + "then") + (DefaultExpanderTest.NL)) + "end") + (DefaultExpanderTest.NL);
        checkExpansion(source, expected);
    }

    @Test
    public void testDontExpandCommentedLines() throws Exception {
        String source = (((((((((((((("rule \"R\"" + (DefaultExpanderTest.NL)) + "dialect \"mvel\"") + (DefaultExpanderTest.NL)) + "when") + (DefaultExpanderTest.NL)) + "// There is an TestObject") + (DefaultExpanderTest.NL)) + "// -startDate is before 01-Jul-2011") + (DefaultExpanderTest.NL)) + "// -endDate is after 01-Jul-2011") + (DefaultExpanderTest.NL)) + "then") + (DefaultExpanderTest.NL)) + "end") + (DefaultExpanderTest.NL);
        String expected = (((((((((((((("rule \"R\"" + (DefaultExpanderTest.NL)) + "dialect \"mvel\"") + (DefaultExpanderTest.NL)) + "when") + (DefaultExpanderTest.NL)) + "// There is an TestObject") + (DefaultExpanderTest.NL)) + "// -startDate is before 01-Jul-2011") + (DefaultExpanderTest.NL)) + "// -endDate is after 01-Jul-2011") + (DefaultExpanderTest.NL)) + "then") + (DefaultExpanderTest.NL)) + "end") + (DefaultExpanderTest.NL);
        checkExpansion(source, expected);
    }

    @Test
    public void testDontExpandCommentedBlocks() throws Exception {
        String source = (((((((((((((((((("rule \"R\"" + (DefaultExpanderTest.NL)) + "dialect \"mvel\"") + (DefaultExpanderTest.NL)) + "when") + (DefaultExpanderTest.NL)) + "/*") + (DefaultExpanderTest.NL)) + "There is an TestObject") + (DefaultExpanderTest.NL)) + "-startDate is before 01-Jul-2011") + (DefaultExpanderTest.NL)) + "-endDate is after 01-Jul-2011") + (DefaultExpanderTest.NL)) + "*/") + (DefaultExpanderTest.NL)) + "then") + (DefaultExpanderTest.NL)) + "end") + (DefaultExpanderTest.NL);
        String expected = (((((((((("rule \"R\"" + (DefaultExpanderTest.NL)) + "dialect \"mvel\"") + (DefaultExpanderTest.NL)) + "when") + (DefaultExpanderTest.NL)) + "") + (DefaultExpanderTest.NL)) + "then") + (DefaultExpanderTest.NL)) + "end") + (DefaultExpanderTest.NL);
        checkExpansion(source, expected);
    }

    @Test
    public void testExpandQuery() throws Exception {
        DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
        String dsl = ((("[when]There is a person=Person()" + (DefaultExpanderTest.NL)) + "[when]- {field:\\w*} {operator} {value:\\d*}={field} {operator} {value}") + (DefaultExpanderTest.NL)) + "[when]is greater than=>";
        String source = (((((("query \"isMature\"" + (DefaultExpanderTest.NL)) + "There is a person") + (DefaultExpanderTest.NL)) + "- age is greater than 18") + (DefaultExpanderTest.NL)) + "end") + (DefaultExpanderTest.NL);
        String expected = (((("query \"isMature\"" + (DefaultExpanderTest.NL)) + "Person(age  >  18)") + (DefaultExpanderTest.NL)) + "end") + (DefaultExpanderTest.NL);
        file.parseAndLoad(new StringReader(dsl));
        Assert.assertEquals(0, file.getErrors().size());
        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping(file.getMapping());
        String drl = ex.expand(source);
        Assert.assertFalse(ex.hasErrors());
        Assert.assertEquals(expected, drl);
    }

    @Test
    public void testExpandExpr() throws Exception {
        DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
        String dsl = "[when]Name of Applicant {nameVar:CF:Applicant.age}= System.out.println({nameVar})";
        String source = ((((((((("rule \"test rule for custom form in DSL\"" + (DefaultExpanderTest.NL)) + "     dialect \"mvel\"") + (DefaultExpanderTest.NL)) + "     when") + (DefaultExpanderTest.NL)) + "         Name of Applicant Bojan Oklahoma and NJ,Andrew AMW Test") + (DefaultExpanderTest.NL)) + "     then") + (DefaultExpanderTest.NL)) + "end";
        String expected = ((((((((("rule \"test rule for custom form in DSL\"" + (DefaultExpanderTest.NL)) + "     dialect \"mvel\"") + (DefaultExpanderTest.NL)) + "     when") + (DefaultExpanderTest.NL)) + "         System.out.println(Bojan Oklahoma and NJ,Andrew AMW Test)") + (DefaultExpanderTest.NL)) + "     then") + (DefaultExpanderTest.NL)) + "end";
        file.parseAndLoad(new StringReader(dsl));
        Assert.assertEquals(0, file.getErrors().size());
        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping(file.getMapping());
        String drl = ex.expand(source);
        Assert.assertFalse(ex.hasErrors());
        Assert.assertEquals(expected, drl);
    }

    @Test(timeout = 1000)
    public void testExpandInfiniteLoop() throws Exception {
        // DROOLS-73
        DSLMappingFile file = new DSLTokenizedMappingFile();
        String dsl = "[when]Foo with {var} bars=Foo( bars == {var} )";
        file.parseAndLoad(new StringReader(dsl));
        Assert.assertEquals(0, file.getErrors().size());
        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping(file.getMapping());
        String source = (((((((("rule 'dsl rule'" + (DefaultExpanderTest.NL)) + "when") + (DefaultExpanderTest.NL)) + " Foo with {var} bars") + (DefaultExpanderTest.NL)) + "then") + (DefaultExpanderTest.NL)) + (DefaultExpanderTest.NL)) + "end";
        ex.expand(source);
        Assert.assertFalse(ex.hasErrors());
    }

    @Test
    public void testEqualSignInTernaryOp() throws Exception {
        // BZ-1013960
        String source = (((((((((((((((((((((((((((("declare Person" + (DefaultExpanderTest.NL)) + "    age : int") + (DefaultExpanderTest.NL)) + "    name : String") + (DefaultExpanderTest.NL)) + "end") + (DefaultExpanderTest.NL)) + "") + (DefaultExpanderTest.NL)) + "rule \"Your First Rule\"") + (DefaultExpanderTest.NL)) + "    when") + (DefaultExpanderTest.NL)) + "        There is a Person") + (DefaultExpanderTest.NL)) + "            - with a negative age") + (DefaultExpanderTest.NL)) + "            - with a positive age") + (DefaultExpanderTest.NL)) + "            - with a zero age") + (DefaultExpanderTest.NL)) + "    then") + (DefaultExpanderTest.NL)) + "        print \"Your First Rule\"") + (DefaultExpanderTest.NL)) + "") + (DefaultExpanderTest.NL)) + "end") + (DefaultExpanderTest.NL);
        String dsl = (((((((("[when][]There is an? {entity}=${entity!lc}: {entity!ucfirst}()" + (DefaultExpanderTest.NL)) + "[when][]- with an? {attr} greater than {amount}={attr} > {amount!num}") + (DefaultExpanderTest.NL)) + "[then]print \"{text}\"=System.out.println(\"{text}\");") + (DefaultExpanderTest.NL)) + "") + (DefaultExpanderTest.NL)) + "[when]- with a {what} {attr}={attr} {what!zero?==0/!=0}") + (DefaultExpanderTest.NL);
        String expected = (((((((((((((((((((((("declare Person" + (DefaultExpanderTest.NL)) + "    age : int") + (DefaultExpanderTest.NL)) + "    name : String") + (DefaultExpanderTest.NL)) + "end") + (DefaultExpanderTest.NL)) + "") + (DefaultExpanderTest.NL)) + "rule \"Your First Rule\"") + (DefaultExpanderTest.NL)) + "    when") + (DefaultExpanderTest.NL)) + "        $person: Person(age  !=0, age  !=0, age  ==0)") + (DefaultExpanderTest.NL)) + "    then") + (DefaultExpanderTest.NL)) + "        System.out.println(\"Your First Rule\");") + (DefaultExpanderTest.NL)) + "") + (DefaultExpanderTest.NL)) + "end") + (DefaultExpanderTest.NL);
        DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
        file.parseAndLoad(new StringReader(dsl));
        Assert.assertEquals(file.getErrors().toString(), 0, file.getErrors().size());
        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping(file.getMapping());
        String drl = ex.expand(source);
        Assert.assertFalse(ex.hasErrors());
        Assert.assertEquals(expected, drl);
    }

    @Test
    public void testDotInPattern() throws Exception {
        // BZ-1013960
        String source = (((((((((((("import org.drools.compiler.Person;" + (DefaultExpanderTest.NL)) + "global java.util.List list") + (DefaultExpanderTest.NL)) + "rule R1") + (DefaultExpanderTest.NL)) + "when") + (DefaultExpanderTest.NL)) + "then") + (DefaultExpanderTest.NL)) + "Log X") + (DefaultExpanderTest.NL)) + "end") + (DefaultExpanderTest.NL);
        String dsl = "[then]Log {message:.}=list.add(\"{message}\");";
        String expected = (((((((((((("import org.drools.compiler.Person;" + (DefaultExpanderTest.NL)) + "global java.util.List list") + (DefaultExpanderTest.NL)) + "rule R1") + (DefaultExpanderTest.NL)) + "when") + (DefaultExpanderTest.NL)) + "then") + (DefaultExpanderTest.NL)) + "list.add(\"X\");") + (DefaultExpanderTest.NL)) + "end") + (DefaultExpanderTest.NL);
        DSLTokenizedMappingFile file = new DSLTokenizedMappingFile();
        file.parseAndLoad(new StringReader(dsl));
        Assert.assertEquals(file.getErrors().toString(), 0, file.getErrors().size());
        DefaultExpander ex = new DefaultExpander();
        ex.addDSLMapping(file.getMapping());
        String drl = ex.expand(source);
        Assert.assertFalse(ex.hasErrors());
        Assert.assertEquals(expected, drl);
    }
}

