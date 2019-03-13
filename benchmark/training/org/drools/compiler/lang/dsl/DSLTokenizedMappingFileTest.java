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
import DSLMappingEntry.EMPTY_METADATA;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import org.junit.Assert;
import org.junit.Test;


public class DSLTokenizedMappingFileTest {
    // Due to a bug in JDK 5, a workaround for zero-widht lookbehind has to be used.
    // JDK works correctly with "(?<=^|\\W)"
    private static final String lookbehind = "(?:(?<=^)|(?<=\\W))";

    private static final String NL = System.getProperty("line.separator");

    private DSLMappingFile file = null;

    private final String filename = "test_metainfo.dsl";

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
    public void testParseFileWithBrackets() {
        String file = "[when]ATTRIBUTE \"{attr}\" IS IN [{list}]=Attribute( {attr} in ({list}) )";
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
            Assert.assertEquals(((DSLTokenizedMappingFileTest.lookbehind) + "ATTRIBUTE\\s+\"(.*?)\"\\s+IS\\s+IN\\s+[(.*?)](?=\\W|$)"), entry.getKeyPattern().toString());
            // Attribute( {attr} in ({list}) )
            Assert.assertEquals("Attribute( {attr} in ({list}) )", entry.getValuePattern());
        } catch (final IOException e) {
            e.printStackTrace();
            Assert.fail("Should not raise exception ");
        }
    }

    @Test
    public void testParseFileWithEscaptedBrackets() {
        String file = "[when]ATTRIBUTE \"{attr}\" IS IN \\[{list}\\]=Attribute( {attr} in ({list}) )";
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
            Assert.assertEquals(((DSLTokenizedMappingFileTest.lookbehind) + "ATTRIBUTE\\s+\"(.*?)\"\\s+IS\\s+IN\\s+\\[(.*?)\\](?=\\W|$)"), entry.getKeyPattern().toString());
            // Attribute( {attr} in ({list}) )
            Assert.assertEquals("Attribute( {attr} in ({list}) )", entry.getValuePattern());
        } catch (final IOException e) {
            e.printStackTrace();
            Assert.fail("Should not raise exception ");
        }
    }

    @Test
    public void testParseFileWithEscapes() {
        String file = ((((((("[then]TEST=System.out.println(\"DO_SOMETHING\");" + (DSLTokenizedMappingFileTest.NL)) + "") + "[when]code {code1} occurs and sum of all digit not equal \\( {code2} \\+ {code3} \\)=AAAA( cd1 == {code1}, cd2 != ( {code2} + {code3} ))") + (DSLTokenizedMappingFileTest.NL)) + "") + "[when]code {code1} occurs=BBBB") + (DSLTokenizedMappingFileTest.NL)) + "";
        try {
            final Reader reader = new StringReader(file);
            this.file = new DSLTokenizedMappingFile();
            final boolean parsingResult = this.file.parseAndLoad(reader);
            reader.close();
            Assert.assertTrue(this.file.getErrors().toString(), parsingResult);
            Assert.assertTrue(this.file.getErrors().isEmpty());
            final String LHS = "code 1041 occurs and sum of all digit not equal ( 1034 + 1035 )";
            final String rule = ((((((((((("rule \"x\"" + (DSLTokenizedMappingFileTest.NL)) + "when") + (DSLTokenizedMappingFileTest.NL)) + "") + LHS) + "") + (DSLTokenizedMappingFileTest.NL)) + "then") + (DSLTokenizedMappingFileTest.NL)) + "TEST") + (DSLTokenizedMappingFileTest.NL)) + "end";
            DefaultExpander de = new DefaultExpander();
            de.addDSLMapping(this.file.getMapping());
            final String ruleAfterExpansion = de.expand(rule);
            final String expected = ((((((((("rule \"x\"" + (DSLTokenizedMappingFileTest.NL)) + "when") + (DSLTokenizedMappingFileTest.NL)) + "AAAA( cd1 == 1041, cd2 != ( 1034 + 1035 ))") + (DSLTokenizedMappingFileTest.NL)) + "then") + (DSLTokenizedMappingFileTest.NL)) + "System.out.println(\"DO_SOMETHING\");") + (DSLTokenizedMappingFileTest.NL)) + "end";
            Assert.assertEquals(expected, ruleAfterExpansion);
        } catch (final IOException e) {
            e.printStackTrace();
            Assert.fail("Should not raise exception ");
        }
    }

    @Test
    public void testParseFileWithEscaptedEquals() {
        String file = "[when]something:\\={value}=Attribute( something == \"{value}\" )";
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
            Assert.assertEquals(((DSLTokenizedMappingFileTest.lookbehind) + "something:\\=(.*?)$"), entry.getKeyPattern().toString());
            Assert.assertEquals("Attribute( something == \"{value}\" )", entry.getValuePattern());
        } catch (final IOException e) {
            e.printStackTrace();
            Assert.fail("Should not raise exception ");
        }
    }
}

