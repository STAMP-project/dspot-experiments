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


import DSLMappingEntry.CONDITION;
import DSLMappingEntry.CONSEQUENCE;
import DSLMappingEntry.EMPTY_METADATA;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import org.junit.Assert;
import org.junit.Test;


public class DSLMappingFileTest {
    private DSLMappingFile file = null;

    private final String filename = "test_metainfo.dsl";

    private static final String NL = System.getProperty("line.separator");

    @Test
    public void testParseFile() {
        try {
            final Reader reader = new InputStreamReader(this.getClass().getResourceAsStream(this.filename));
            this.file = new DSLTokenizedMappingFile();
            final boolean parsingResult = this.file.parseAndLoad(reader);
            reader.close();
            Assert.assertTrue(this.file.getErrors().toString(), parsingResult);
            Assert.assertTrue(this.file.getErrors().isEmpty());
            Assert.assertEquals(31, this.file.getMapping().getEntries().size());
        } catch (final IOException e) {
            e.printStackTrace();
            Assert.fail("Should not raise exception ");
        }
    }

    @Test
    public void testParseFileWithEscaptedBrackets() {
        String file = "[when][]ATTRIBUTE \"{attr}\" IS IN \\[{list}\\]=Attribute( {attr} in ({list}) )";
        try {
            final Reader reader = new StringReader(file);
            this.file = new DSLTokenizedMappingFile();
            final boolean parsingResult = this.file.parseAndLoad(reader);
            reader.close();
            Assert.assertTrue(this.file.getErrors().toString(), parsingResult);
            Assert.assertTrue(this.file.getErrors().isEmpty());
            Assert.assertEquals(1, this.file.getMapping().getEntries().size());
            DSLMappingEntry entry = ((DSLMappingEntry) (this.file.getMapping().getEntries().get(0)));
            Assert.assertEquals(CONDITION, entry.getSection());
            Assert.assertEquals(EMPTY_METADATA, entry.getMetaData());
            Assert.assertEquals("ATTRIBUTE \"{attr}\" IS IN \\[{list}\\]", entry.getMappingKey());
            Assert.assertEquals("Attribute( {attr} in ({list}) )", entry.getMappingValue());
        } catch (final IOException e) {
            e.printStackTrace();
            Assert.fail("Should not raise exception ");
        }
    }

    @Test
    public void testParseFileWithEscaptedCurlyBrackets() {
        String file = "[consequence][$policy]Add surcharge {surcharge} to Policy=modify(policy) \\{price = {surcharge}\\}";
        try {
            final Reader reader = new StringReader(file);
            this.file = new DSLTokenizedMappingFile();
            final boolean parsingResult = this.file.parseAndLoad(reader);
            reader.close();
            Assert.assertTrue(this.file.getErrors().toString(), parsingResult);
            Assert.assertTrue(this.file.getErrors().isEmpty());
            Assert.assertEquals(1, this.file.getMapping().getEntries().size());
            DSLMappingEntry entry = ((DSLMappingEntry) (this.file.getMapping().getEntries().get(0)));
            Assert.assertEquals(CONSEQUENCE, entry.getSection());
            Assert.assertEquals("$policy", entry.getMetaData().toString());
            Assert.assertEquals("Add surcharge {surcharge} to Policy", entry.getMappingKey());
            Assert.assertEquals("modify(policy) \\{price = {surcharge}\\}", entry.getMappingValue());
            String input = ((((((((("rule x" + (DSLMappingFileTest.NL)) + "when") + (DSLMappingFileTest.NL)) + "then") + (DSLMappingFileTest.NL)) + "Add surcharge 300 to Policy") + (DSLMappingFileTest.NL)) + "end") + (DSLMappingFileTest.NL)) + "";
            String expected = ((((((((("rule x" + (DSLMappingFileTest.NL)) + "when") + (DSLMappingFileTest.NL)) + "then") + (DSLMappingFileTest.NL)) + "modify(policy) {price = 300}") + (DSLMappingFileTest.NL)) + "end") + (DSLMappingFileTest.NL)) + "";
            DefaultExpander de = new DefaultExpander();
            de.addDSLMapping(this.file.getMapping());
            final String result = de.expand(input);
            // String result = entry.getKeyPattern().matcher( input ).replaceAll( entry.getValuePattern() );
            Assert.assertEquals(expected, result);
        } catch (final IOException e) {
            e.printStackTrace();
            Assert.fail("Should not raise exception ");
        }
    }

    /**
     * Right now this test fails because there is no RHS for the rule. It connects the "then" and "end" to "thenend".
     */
    @Test
    public void testNoRHS() {
        String file = ((((((("[then]TEST=System.out.println(\"DO_SOMETHING\");" + (DSLMappingFileTest.NL)) + "") + "[when]code {code1} occurs and sum of all digit not equal \\( {code2} \\+ {code3} \\)=AAAA( cd1 == {code1}, cd2 != ( {code2} + {code3} ))") + (DSLMappingFileTest.NL)) + "") + "[when]code {code1} occurs=BBBB") + (DSLMappingFileTest.NL)) + "";
        try {
            final Reader reader = new StringReader(file);
            this.file = new DSLTokenizedMappingFile();
            final boolean parsingResult = this.file.parseAndLoad(reader);
            reader.close();
            Assert.assertTrue(this.file.getErrors().toString(), parsingResult);
            Assert.assertTrue(this.file.getErrors().isEmpty());
            final String LHS = "code 1041 occurs and sum of all digit not equal ( 1034 + 1035 )";
            final String rule = ((((((((("rule \"x\"" + (DSLMappingFileTest.NL)) + "when") + (DSLMappingFileTest.NL)) + "") + LHS) + "") + (DSLMappingFileTest.NL)) + "then") + (DSLMappingFileTest.NL)) + "end";
            DefaultExpander de = new DefaultExpander();
            de.addDSLMapping(this.file.getMapping());
            final String ruleAfterExpansion = de.expand(rule);
            final String expected = ((((((("rule \"x\"" + (DSLMappingFileTest.NL)) + "when") + (DSLMappingFileTest.NL)) + "AAAA( cd1 == 1041, cd2 != ( 1034 + 1035 ))") + (DSLMappingFileTest.NL)) + "then") + (DSLMappingFileTest.NL)) + "end";
            Assert.assertEquals(expected, ruleAfterExpansion);
        } catch (final IOException e) {
            e.printStackTrace();
            Assert.fail("Should not raise exception ");
        }
    }

    @Test
    public void testParseFileWithEscapes() {
        String file = ((((((("[then]TEST=System.out.println(\"DO_SOMETHING\");" + (DSLMappingFileTest.NL)) + "") + "[when]code {code1} occurs and sum of all digit not equal \\( {code2} \\+ {code3} \\)=AAAA( cd1 == {code1}, cd2 != ( {code2} + {code3} ))") + (DSLMappingFileTest.NL)) + "") + "[when]code {code1} occurs=BBBB") + (DSLMappingFileTest.NL)) + "";
        try {
            final Reader reader = new StringReader(file);
            this.file = new DSLTokenizedMappingFile();
            final boolean parsingResult = this.file.parseAndLoad(reader);
            reader.close();
            Assert.assertTrue(this.file.getErrors().toString(), parsingResult);
            Assert.assertTrue(this.file.getErrors().isEmpty());
            final String LHS = "code 1041 occurs and sum of all digit not equal ( 1034 + 1035 )";
            final String rule = ((((((((((("rule \"x\"" + (DSLMappingFileTest.NL)) + "when") + (DSLMappingFileTest.NL)) + "") + LHS) + "") + (DSLMappingFileTest.NL)) + "then") + (DSLMappingFileTest.NL)) + "TEST") + (DSLMappingFileTest.NL)) + "end";
            DefaultExpander de = new DefaultExpander();
            de.addDSLMapping(this.file.getMapping());
            final String ruleAfterExpansion = de.expand(rule);
            final String expected = ((((((((("rule \"x\"" + (DSLMappingFileTest.NL)) + "when") + (DSLMappingFileTest.NL)) + "AAAA( cd1 == 1041, cd2 != ( 1034 + 1035 ))") + (DSLMappingFileTest.NL)) + "then") + (DSLMappingFileTest.NL)) + "System.out.println(\"DO_SOMETHING\");") + (DSLMappingFileTest.NL)) + "end";
            Assert.assertEquals(expected, ruleAfterExpansion);
        } catch (final IOException e) {
            e.printStackTrace();
            Assert.fail("Should not raise exception ");
        }
    }

    @Test
    public void testEnum() {
        String file = "[when][]ATTRIBUTE {attr:ENUM:Attribute.value} in {list}=Attribute( {attr} in ({list}) )";
        try {
            final Reader reader = new StringReader(file);
            this.file = new DSLTokenizedMappingFile();
            final boolean parsingResult = this.file.parseAndLoad(reader);
            reader.close();
            Assert.assertTrue(this.file.getErrors().toString(), parsingResult);
            Assert.assertTrue(this.file.getErrors().isEmpty());
            Assert.assertEquals(1, this.file.getMapping().getEntries().size());
            DSLMappingEntry entry = ((DSLMappingEntry) (this.file.getMapping().getEntries().get(0)));
            Assert.assertEquals(CONDITION, entry.getSection());
            Assert.assertEquals(EMPTY_METADATA, entry.getMetaData());
            System.out.println(entry.getValuePattern());
            System.out.println(entry.getVariables());
            Assert.assertEquals("ATTRIBUTE {attr:ENUM:Attribute.value} in {list}", entry.getMappingKey());
            Assert.assertEquals("Attribute( {attr} in ({list}) )", entry.getMappingValue());
            Assert.assertEquals("(?:(?<=^)|(?<=\\W))ATTRIBUTE\\s+(.*?)\\s+in\\s+(.*?)$", entry.getKeyPattern().toString());
        } catch (final IOException e) {
            e.printStackTrace();
            Assert.fail("Should not raise exception ");
        }
    }
}

