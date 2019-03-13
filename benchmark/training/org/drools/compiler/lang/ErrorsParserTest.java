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


import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.lang.dsl.DefaultExpander;
import org.junit.Assert;
import org.junit.Test;


public class ErrorsParserTest {
    @Test
    public void testNotBindindShouldBarf() throws Exception {
        final DRLParser parser = parseResource("not_with_binding_error.drl");
        parser.compilationUnit();
        Assert.assertTrue(parser.hasErrors());
    }

    @Test
    public void testExpanderErrorsAfterExpansion() throws Exception {
        String name = "expander_post_errors.dslr";
        Expander expander = new DefaultExpander();
        String expanded = expander.expand(this.getReader(name));
        DRLParser parser = parse(name, expanded);
        parser.compilationUnit();
        Assert.assertTrue(parser.hasErrors());
        Assert.assertEquals(1, parser.getErrors().size());
        DroolsParserException err = ((DroolsParserException) (parser.getErrors().get(0)));
        Assert.assertEquals(6, err.getLineNumber());
    }

    @Test
    public void testInvalidSyntax_Catches() throws Exception {
        DRLParser parser = parseResource("invalid_syntax.drl");
        parser.compilationUnit();
        Assert.assertTrue(parser.hasErrors());
    }

    @Test
    public void testMultipleErrors() throws Exception {
        DRLParser parser = parseResource("multiple_errors.drl");
        parser.compilationUnit();
        Assert.assertTrue(parser.hasErrors());
        Assert.assertEquals(2, parser.getErrors().size());
    }

    @Test
    public void testPackageGarbage() throws Exception {
        DRLParser parser = parseResource("package_garbage.drl");
        parser.compilationUnit();
        Assert.assertTrue(parser.hasErrors());
    }

    @Test
    public void testEvalWithSemicolon() throws Exception {
        DRLParser parser = parseResource("eval_with_semicolon.drl");
        parser.compilationUnit();
        Assert.assertTrue(parser.hasErrors());
        Assert.assertEquals(1, parser.getErrorMessages().size());
        Assert.assertEquals("ERR 102", parser.getErrors().get(0).getErrorCode());
    }

    @Test
    public void testLexicalError() throws Exception {
        DRLParser parser = parseResource("lex_error.drl");
        parser.compilationUnit();
        Assert.assertTrue(parser.hasErrors());
    }

    @Test
    public void testTempleteError() throws Exception {
        DRLParser parser = parseResource("template_test_error.drl");
        parser.compilationUnit();
        Assert.assertTrue(parser.hasErrors());
    }

    @Test
    public void testErrorMessageForMisplacedParenthesis() throws Exception {
        final DRLParser parser = parseResource("misplaced_parenthesis.drl");
        parser.compilationUnit();
        Assert.assertTrue("Parser should have raised errors", parser.hasErrors());
        Assert.assertEquals(1, parser.getErrors().size());
        Assert.assertEquals("ERR 102", parser.getErrors().get(0).getErrorCode());
    }

    @Test
    public void testNPEOnParser() throws Exception {
        final DRLParser parser = parseResource("npe_on_parser.drl");
        parser.compilationUnit();
        Assert.assertTrue("Parser should have raised errors", parser.hasErrors());
        Assert.assertEquals(1, parser.getErrors().size());
        Assert.assertTrue(parser.getErrors().get(0).getErrorCode().equals("ERR 102"));
    }

    @Test
    public void testCommaMisuse() throws Exception {
        final DRLParser parser = parseResource("comma_misuse.drl");
        try {
            parser.compilationUnit();
            Assert.assertTrue("Parser should have raised errors", parser.hasErrors());
        } catch (NullPointerException npe) {
            Assert.fail("Should not raise NPE");
        }
    }
}

