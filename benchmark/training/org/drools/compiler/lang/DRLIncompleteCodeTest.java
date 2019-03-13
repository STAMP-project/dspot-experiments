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
package org.drools.compiler.lang;


import Location.LOCATION_LHS_INSIDE_CONDITION_END;
import Location.LOCATION_RHS;
import org.antlr.runtime.RecognitionException;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.lang.descr.PackageDescr;
import org.junit.Assert;
import org.junit.Test;
import org.kie.internal.builder.conf.LanguageLevelOption;


public class DRLIncompleteCodeTest {
    @Test
    public void testIncompleteCode2() throws RecognitionException, DroolsParserException {
        String input = "rule MyRule when Class ( property memberOf collection ";
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr descr = parser.parse(true, input);
        Assert.assertNotNull(descr);
        Assert.assertEquals(LOCATION_LHS_INSIDE_CONDITION_END, getLastIntegerValue(parser.getEditorSentences().get(0).getContent()));
    }

    @Test
    public void testIncompleteCode3() throws RecognitionException, DroolsParserException {
        String input = "rule MyRule when Class ( property > somevalue ) then end query MyQuery Class ( property == collection ) end ";
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr descr = parser.parse(true, input);
        Assert.assertNotNull(descr);
        Assert.assertEquals("MyRule", descr.getRules().get(0).getName());
        Assert.assertNotNull(descr);
        Assert.assertEquals("MyQuery", descr.getRules().get(1).getName());
        Assert.assertEquals(LOCATION_RHS, getLastIntegerValue(parser.getEditorSentences().get(0).getContent()));
    }

    @Test
    public void testIncompleteCode4() throws RecognitionException, DroolsParserException {
        String input = "package a.b.c import a.b.c.*" + (" rule MyRule when Class ( property == collection ) then end " + " query MyQuery Class ( property == collection ) end ");
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr descr = parser.parse(true, input);
        Assert.assertEquals("a.b.c", descr.getNamespace());
        Assert.assertEquals("a.b.c.*", descr.getImports().get(0).getTarget());
        Assert.assertNotNull(descr);
        Assert.assertEquals("MyRule", descr.getRules().get(0).getName());
        Assert.assertNotNull(descr);
        Assert.assertEquals("MyQuery", descr.getRules().get(1).getName());
    }

    @Test
    public void testIncompleteCode5() throws RecognitionException, DroolsParserException {
        String input = "package a.b.c import a.b.c.*" + (" rule MyRule when Class ( property memberOf collection ) then end " + " query MyQuery Class ( property memberOf collection ) end ");
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr descr = parser.parse(true, input);
        Assert.assertNotNull(descr);
    }

    @Test
    public void testIncompleteCode6() throws RecognitionException, DroolsParserException {
        String input = "packe 1111.111 import a.b.c.*" + (" rule MyRule when Class ( property memberOf collection ) then end " + " query MyQuery Class ( property memberOf collection ) end ");
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr descr = parser.parse(true, input);
        Assert.assertNotNull(descr);
    }

    @Test
    public void testIncompleteCode7() throws RecognitionException, DroolsParserException {
        String input = "package a.b.c imrt a.b.c.*" + (" rule MyRule when Class ( property memberOf collection ) then end " + " query MyQuery Class ( property memberOf collection ) end ");
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr descr = parser.parse(true, input);
        Assert.assertNotNull(descr);
    }

    @Test
    public void testIncompleteCode8() throws RecognitionException, DroolsParserException {
        String input = "package a.b.c import a.1111.c.*" + (" rule MyRule when Class ( property memberOf collection ) then end " + " query MyQuery Class ( property memberOf collection ) end ");
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr descr = parser.parse(true, input);
        System.out.println(parser.getErrors());
        Assert.assertEquals("a.b.c", descr.getNamespace());
        // FIXME: assertEquals(2, descr.getRules().size());
        Assert.assertEquals(true, parser.hasErrors());
    }

    @Test
    public void testIncompleteCode11() throws RecognitionException, DroolsParserException {
        String input = "package a.b.c import a.b.c.*" + (" rule MyRule when Class ( property memberOf collection ) then end " + " qzzzzuery MyQuery Class ( property ");
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr descr = parser.parse(true, input);
        Assert.assertEquals("a.b.c", descr.getNamespace());
        Assert.assertEquals("a.b.c.*", descr.getImports().get(0).getTarget());
        Assert.assertNotNull(descr);
        Assert.assertEquals("MyRule", descr.getRules().get(0).getName());
    }

    @Test
    public void testIncompleteCode12() throws RecognitionException, DroolsParserException {
        String input = "package a.b.c " + (((((("import a.b.c.* " + "rule MyRule") + "  when ") + "    m: Message(  ) ") + "    ") + "  then") + "end ");
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr descr = parser.parse(true, input);
        Assert.assertNotNull(descr);
        Assert.assertEquals("a.b.c", descr.getNamespace());
        Assert.assertEquals("a.b.c.*", descr.getImports().get(0).getTarget());
    }

    @Test
    public void testIncompleteCode13() throws RecognitionException, DroolsParserException {
        String input = "package com.sample " + ((((("import com.sample.DroolsTest.Message; " + "rule \"Hello World\"") + "  when ") + "  then") + "     \\\" ") + "end ");
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr descr = parser.parse(true, input);
        Assert.assertNotNull(descr);
    }
}

