/**
 * Copyright (C) 2010 The Android Open Source Project
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
package libcore.xml;


import java.io.ByteArrayInputStream;
import java.io.StringReader;
import junit.framework.TestCase;
import org.xmlpull.v1.XmlPullParser;


public abstract class PullParserTest extends TestCase {
    public void testAttributeNoValueWithRelaxed() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setFeature("http://xmlpull.org/v1/doc/features.html#relaxed", true);
        parser.setInput(new StringReader("<input checked></input>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("input", parser.getName());
        TestCase.assertEquals("checked", parser.getAttributeName(0));
        TestCase.assertEquals("checked", parser.getAttributeValue(0));
    }

    public void testAttributeUnquotedValueWithRelaxed() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setFeature("http://xmlpull.org/v1/doc/features.html#relaxed", true);
        parser.setInput(new StringReader("<input checked=true></input>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("input", parser.getName());
        TestCase.assertEquals("checked", parser.getAttributeName(0));
        TestCase.assertEquals("true", parser.getAttributeValue(0));
    }

    public void testUnterminatedEntityWithRelaxed() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setFeature("http://xmlpull.org/v1/doc/features.html#relaxed", true);
        parser.setInput(new StringReader("<foo bar='A&W'>mac&cheese</foo>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("foo", parser.getName());
        TestCase.assertEquals("bar", parser.getAttributeName(0));
        TestCase.assertEquals("A&W", parser.getAttributeValue(0));
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals("mac&cheese", parser.getText());
    }

    public void testEntitiesAndNamespaces() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", true);
        parser.setInput(new StringReader("<foo:a xmlns:foo='http://foo' xmlns:bar='http://bar'><bar:b/></foo:a>"));
        testNamespace(parser);
    }

    public void testEntitiesAndNamespacesWithRelaxed() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", true);
        parser.setFeature("http://xmlpull.org/v1/doc/features.html#relaxed", true);
        parser.setInput(new StringReader("<foo:a xmlns:foo='http://foo' xmlns:bar='http://bar'><bar:b/></foo:a>"));
        testNamespace(parser);
    }

    public void testRegularNumericEntities() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo>&#65;</foo>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
        TestCase.assertEquals("#65", parser.getName());
        TestCase.assertEquals("A", parser.getText());
    }

    public void testNumericEntitiesLargerThanChar() throws Exception {
        assertParseFailure("<foo>&#2147483647; &#-2147483648;</foo>");
    }

    public void testNumericEntitiesLargerThanInt() throws Exception {
        assertParseFailure("<foo>&#2147483648;</foo>");
    }

    public void testCharacterReferenceOfHexUtf16Surrogates() throws Exception {
        testCharacterReferenceOfUtf16Surrogates("<foo>&#x10000; &#x10381; &#x10FFF0;</foo>");
    }

    public void testCharacterReferenceOfDecimalUtf16Surrogates() throws Exception {
        testCharacterReferenceOfUtf16Surrogates("<foo>&#65536; &#66433; &#1114096;</foo>");
    }

    public void testCharacterReferenceOfLastUtf16Surrogate() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo>&#x10FFFF;</foo>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals(new String(new int[]{ 1114111 }, 0, 1), parser.getText());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
    }

    public void testOmittedNumericEntities() throws Exception {
        assertParseFailure("<foo>&#;</foo>");
    }

    /**
     * Carriage returns followed by line feeds are silently discarded.
     */
    public void testCarriageReturnLineFeed() throws Exception {
        testLineEndings("\r\n<foo\r\na=\'b\r\nc\'\r\n>d\r\ne</foo\r\n>\r\n");
    }

    /**
     * Lone carriage returns are treated like newlines.
     */
    public void testLoneCarriageReturn() throws Exception {
        testLineEndings("\r<foo\ra=\'b\rc\'\r>d\re</foo\r>\r");
    }

    public void testLoneNewLine() throws Exception {
        testLineEndings("\n<foo\na=\'b\nc\'\n>d\ne</foo\n>\n");
    }

    public void testXmlDeclaration() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<?xml version='1.0' encoding='UTF-8' standalone='no'?><foo/>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.nextToken());
        TestCase.assertEquals("1.0", parser.getProperty("http://xmlpull.org/v1/doc/properties.html#xmldecl-version"));
        TestCase.assertEquals(Boolean.FALSE, parser.getProperty("http://xmlpull.org/v1/doc/properties.html#xmldecl-standalone"));
        TestCase.assertEquals("UTF-8", parser.getInputEncoding());
    }

    public void testXmlDeclarationExtraAttributes() throws Exception {
        assertParseFailure("<?xml version='1.0' encoding='UTF-8' standalone='no' a='b'?><foo/>");
    }

    public void testCustomEntitiesUsingNext() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo a='cd&aaaaaaaaaa;ef'>wx&aaaaaaaaaa;yz</foo>"));
        parser.defineEntityReplacementText("aaaaaaaaaa", "b");
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("cdbef", parser.getAttributeValue(0));
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals("wxbyz", parser.getText());
    }

    public void testCustomEntitiesUsingNextToken() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo a='cd&aaaaaaaaaa;ef'>wx&aaaaaaaaaa;yz</foo>"));
        parser.defineEntityReplacementText("aaaaaaaaaa", "b");
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.nextToken());
        TestCase.assertEquals("cdbef", parser.getAttributeValue(0));
        TestCase.assertEquals(XmlPullParser.TEXT, parser.nextToken());
        TestCase.assertEquals("wx", parser.getText());
        TestCase.assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
        TestCase.assertEquals("aaaaaaaaaa", parser.getName());
        TestCase.assertEquals("b", parser.getText());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.nextToken());
        TestCase.assertEquals("yz", parser.getText());
    }

    public void testCustomEntitiesAreNotEvaluated() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo a='&a;'>&a;</foo>"));
        parser.defineEntityReplacementText("a", "&amp; &a;");
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("&amp; &a;", parser.getAttributeValue(0));
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals("&amp; &a;", parser.getText());
    }

    public void testMissingEntities() throws Exception {
        assertParseFailure("<foo>&aaa;</foo>");
    }

    public void testMissingEntitiesWithRelaxed() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setFeature("http://xmlpull.org/v1/doc/features.html#relaxed", true);
        parser.setInput(new StringReader("<foo>&aaa;</foo>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals(null, parser.getName());
        TestCase.assertEquals(("Expected unresolved entities to be left in-place. The old parser " + "would resolve these to the empty string."), "&aaa;", parser.getText());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
    }

    public void testMissingEntitiesUsingNextToken() throws Exception {
        XmlPullParser parser = newPullParser();
        testMissingEntitiesUsingNextToken(parser);
    }

    public void testMissingEntitiesUsingNextTokenWithRelaxed() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setFeature("http://xmlpull.org/v1/doc/features.html#relaxed", true);
        testMissingEntitiesUsingNextToken(parser);
    }

    public void testEntityInAttributeUsingNextToken() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo bar=\"&amp;\"></foo>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.nextToken());
        TestCase.assertEquals("foo", parser.getName());
        TestCase.assertEquals("&", parser.getAttributeValue(null, "bar"));
    }

    public void testMissingEntitiesInAttributesUsingNext() throws Exception {
        assertParseFailure("<foo b='&aaa;'></foo>");
    }

    public void testMissingEntitiesInAttributesUsingNextWithRelaxed() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo b='&aaa;'></foo>"));
        parser.setFeature("http://xmlpull.org/v1/doc/features.html#relaxed", true);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.nextToken());
        TestCase.assertEquals(1, parser.getAttributeCount());
        TestCase.assertEquals("b", parser.getAttributeName(0));
        TestCase.assertEquals(("Expected unresolved entities to be left in-place. The old parser " + "would resolve these to the empty string."), "&aaa;", parser.getAttributeValue(0));
    }

    public void testMissingEntitiesInAttributesUsingNextToken() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo b='&aaa;'></foo>"));
        testMissingEntitiesInAttributesUsingNextToken(parser);
    }

    public void testMissingEntitiesInAttributesUsingNextTokenWithRelaxed() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo b='&aaa;'></foo>"));
        parser.setFeature("http://xmlpull.org/v1/doc/features.html#relaxed", true);
        testMissingEntitiesInAttributesUsingNextToken(parser);
    }

    public void testGreaterThanInText() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo>></foo>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals(">", parser.getText());
    }

    public void testGreaterThanInAttribute() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo a='>'></foo>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(">", parser.getAttributeValue(0));
    }

    public void testLessThanInText() throws Exception {
        assertParseFailure("<foo><</foo>");
    }

    public void testLessThanInAttribute() throws Exception {
        assertParseFailure("<foo a='<'></foo>");
    }

    public void testQuotesInAttribute() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo a=\'\"\' b=\"\'\"></foo>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("\"", parser.getAttributeValue(0));
        TestCase.assertEquals("'", parser.getAttributeValue(1));
    }

    public void testQuotesInText() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo>\" \'</foo>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals("\" \'", parser.getText());
    }

    public void testCdataDelimiterInAttribute() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo a=']]>'></foo>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("]]>", parser.getAttributeValue(0));
    }

    public void testCdataDelimiterInText() throws Exception {
        assertParseFailure("<foo>]]></foo>");
    }

    public void testUnexpectedEof() throws Exception {
        assertParseFailure("<foo><![C");
    }

    public void testUnexpectedSequence() throws Exception {
        assertParseFailure("<foo><![Cdata[bar]]></foo>");
    }

    public void testThreeDashCommentDelimiter() throws Exception {
        assertParseFailure("<foo><!--a---></foo>");
    }

    public void testTwoDashesInComment() throws Exception {
        assertParseFailure("<foo><!-- -- --></foo>");
    }

    public void testEmptyComment() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo><!----></foo>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.COMMENT, parser.nextToken());
        TestCase.assertEquals("", parser.getText());
    }

    /**
     * Close braces require lookaheads because we need to defend against "]]>".
     */
    public void testManyCloseBraces() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo>]]]]]]]]]]]]]]]]]]]]]]]</foo>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals("]]]]]]]]]]]]]]]]]]]]]]]", parser.getText());
    }

    public void testCommentUsingNext() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo>ab<!-- comment! -->cd</foo>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals("abcd", parser.getText());
    }

    public void testCommentUsingNextToken() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo>ab<!-- comment! -->cd</foo>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.nextToken());
        TestCase.assertEquals("ab", parser.getText());
        TestCase.assertEquals(XmlPullParser.COMMENT, parser.nextToken());
        TestCase.assertEquals(" comment! ", parser.getText());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.nextToken());
        TestCase.assertEquals("cd", parser.getText());
    }

    public void testCdataUsingNext() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo>ab<![CDATA[cdef]]gh&amp;i]]>jk</foo>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals("abcdef]]gh&amp;ijk", parser.getText());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
    }

    public void testCdataUsingNextToken() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo>ab<![CDATA[cdef]]gh&amp;i]]>jk</foo>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.nextToken());
        TestCase.assertEquals("ab", parser.getText());
        TestCase.assertEquals(XmlPullParser.CDSECT, parser.nextToken());
        TestCase.assertEquals("cdef]]gh&amp;i", parser.getText());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.nextToken());
        TestCase.assertEquals("jk", parser.getText());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.nextToken());
    }

    public void testEntityLooksLikeCdataClose() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo>&#93;&#93;></foo>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals("]]>", parser.getText());
    }

    public void testProcessingInstructionUsingNext() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo>ab<?cd efg hij?>kl</foo>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals("abkl", parser.getText());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
    }

    public void testProcessingInstructionUsingNextToken() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo>ab<?cd efg hij?>kl</foo>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.nextToken());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.nextToken());
        TestCase.assertEquals("ab", parser.getText());
        TestCase.assertEquals(XmlPullParser.PROCESSING_INSTRUCTION, parser.nextToken());
        TestCase.assertEquals("cd efg hij", parser.getText());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.nextToken());
        TestCase.assertEquals("kl", parser.getText());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
    }

    public void testWhitespaceUsingNextToken() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("  \n  <foo> \n </foo>   \n   "));
        TestCase.assertEquals(XmlPullParser.IGNORABLE_WHITESPACE, parser.nextToken());
        TestCase.assertEquals(true, parser.isWhitespace());
        TestCase.assertEquals("  \n  ", parser.getText());
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.nextToken());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.nextToken());
        TestCase.assertEquals(true, parser.isWhitespace());
        TestCase.assertEquals(" \n ", parser.getText());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.nextToken());
        TestCase.assertEquals(XmlPullParser.IGNORABLE_WHITESPACE, parser.nextToken());
        TestCase.assertEquals(true, parser.isWhitespace());
        TestCase.assertEquals("   \n   ", parser.getText());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.nextToken());
    }

    public void testLinesAndColumns() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader(("\n" + ((((("  <foo><bar a=\'\n" + "\' b=\'cde\'></bar\n") + "><!--\n") + "\n") + "--><baz/>fg\n") + "</foo>"))));
        TestCase.assertEquals("1,1", (((parser.getLineNumber()) + ",") + (parser.getColumnNumber())));
        TestCase.assertEquals(XmlPullParser.IGNORABLE_WHITESPACE, parser.nextToken());
        TestCase.assertEquals("2,3", (((parser.getLineNumber()) + ",") + (parser.getColumnNumber())));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.nextToken());
        TestCase.assertEquals("2,8", (((parser.getLineNumber()) + ",") + (parser.getColumnNumber())));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.nextToken());
        TestCase.assertEquals("3,11", (((parser.getLineNumber()) + ",") + (parser.getColumnNumber())));
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.nextToken());
        TestCase.assertEquals("4,2", (((parser.getLineNumber()) + ",") + (parser.getColumnNumber())));
        TestCase.assertEquals(XmlPullParser.COMMENT, parser.nextToken());
        TestCase.assertEquals("6,4", (((parser.getLineNumber()) + ",") + (parser.getColumnNumber())));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.nextToken());
        TestCase.assertEquals("6,10", (((parser.getLineNumber()) + ",") + (parser.getColumnNumber())));
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.nextToken());
        TestCase.assertEquals("6,10", (((parser.getLineNumber()) + ",") + (parser.getColumnNumber())));
        TestCase.assertEquals(XmlPullParser.TEXT, parser.nextToken());
        TestCase.assertEquals("7,1", (((parser.getLineNumber()) + ",") + (parser.getColumnNumber())));
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.nextToken());
        TestCase.assertEquals("7,7", (((parser.getLineNumber()) + ",") + (parser.getColumnNumber())));
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.nextToken());
        TestCase.assertEquals("7,7", (((parser.getLineNumber()) + ",") + (parser.getColumnNumber())));
    }

    public void testEmptyEntityReferenceUsingNext() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo>&empty;</foo>"));
        parser.defineEntityReplacementText("empty", "");
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
    }

    public void testEmptyEntityReferenceUsingNextToken() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo>&empty;</foo>"));
        parser.defineEntityReplacementText("empty", "");
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.nextToken());
        TestCase.assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
        TestCase.assertEquals("empty", parser.getName());
        TestCase.assertEquals("", parser.getText());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.nextToken());
    }

    public void testEmptyCdataUsingNext() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo><![CDATA[]]></foo>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
    }

    public void testEmptyCdataUsingNextToken() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo><![CDATA[]]></foo>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.CDSECT, parser.nextToken());
        TestCase.assertEquals("", parser.getText());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
    }

    public void testParseReader() throws Exception {
        String snippet = "<dagny dad=\"bob\">hello</dagny>";
        XmlPullParser parser = newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(new StringReader(snippet));
        PullParserTest.validate(parser);
    }

    public void testParseInputStream() throws Exception {
        String snippet = "<dagny dad=\"bob\">hello</dagny>";
        XmlPullParser parser = newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(new ByteArrayInputStream(snippet.getBytes()), "UTF-8");
        PullParserTest.validate(parser);
    }

    public void testNextAfterEndDocument() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo></foo>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    public void testNamespaces() throws Exception {
        String xml = "<one xmlns=\'ns:default\' xmlns:n1=\'ns:1\' a=\'b\'>\n" + ("  <n1:two c=\'d\' n1:e=\'f\' xmlns:n2=\'ns:2\'>text</n1:two>\n" + "</one>");
        XmlPullParser parser = newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        parser.setInput(new StringReader(xml));
        TestCase.assertEquals(0, parser.getDepth());
        TestCase.assertEquals(0, parser.getNamespaceCount(0));
        try {
            parser.getNamespaceCount(1);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            /* expected */
        }
        // one
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(1, parser.getDepth());
        checkNamespacesInOne(parser);
        // n1:two
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.nextTag());
        TestCase.assertEquals(2, parser.getDepth());
        checkNamespacesInTwo(parser);
        // Body of two.
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        // End of two.
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.nextTag());
        // Depth should still be 2.
        TestCase.assertEquals(2, parser.getDepth());
        // We should still be able to see the namespaces from two.
        checkNamespacesInTwo(parser);
        // End of one.
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.nextTag());
        // Depth should be back to 1.
        TestCase.assertEquals(1, parser.getDepth());
        // We can still see the namespaces in one.
        checkNamespacesInOne(parser);
        // We shouldn't be able to see the namespaces in two anymore.
        try {
            parser.getNamespaceCount(2);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            /* expected */
        }
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
        // We shouldn't be able to see the namespaces in one anymore.
        try {
            parser.getNamespaceCount(1);
            TestCase.fail();
        } catch (IndexOutOfBoundsException e) {
            /* expected */
        }
        TestCase.assertEquals(0, parser.getNamespaceCount(0));
    }

    public void testTextBeforeDocumentElement() throws Exception {
        assertParseFailure("not xml<foo/>");
    }

    public void testTextAfterDocumentElement() throws Exception {
        assertParseFailure("<foo/>not xml");
    }

    public void testTextNoDocumentElement() throws Exception {
        assertParseFailure("not xml");
    }

    public void testBomAndByteInput() throws Exception {
        byte[] xml = "\ufeff<?xml version=\'1.0\'?><input/>".getBytes("UTF-8");
        XmlPullParser parser = newPullParser();
        parser.setInput(new ByteArrayInputStream(xml), null);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("input", parser.getName());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals("input", parser.getName());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    public void testBomAndByteInputWithExplicitCharset() throws Exception {
        byte[] xml = "\ufeff<?xml version=\'1.0\'?><input/>".getBytes("UTF-8");
        XmlPullParser parser = newPullParser();
        parser.setInput(new ByteArrayInputStream(xml), "UTF-8");
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("input", parser.getName());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals("input", parser.getName());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    public void testBomAndCharacterInput() throws Exception {
        assertParseFailure("\ufeff<?xml version=\'1.0\'?><input/>");
    }

    // http://code.google.com/p/android/issues/detail?id=21425
    public void testNextTextAdvancesToEndTag() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo>bar</foo>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("bar", parser.nextText());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.getEventType());
    }

    public void testNextTag() throws Exception {
        XmlPullParser parser = newPullParser();
        parser.setInput(new StringReader("<foo> <bar></bar> </foo>"));
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.nextTag());
        TestCase.assertEquals("foo", parser.getName());
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.nextTag());
        TestCase.assertEquals("bar", parser.getName());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.nextTag());
        TestCase.assertEquals("bar", parser.getName());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.nextTag());
        TestCase.assertEquals("foo", parser.getName());
    }

    public void testEofInElementSpecRelaxed() throws Exception {
        assertRelaxedParseFailure("<!DOCTYPE foo [<!ELEMENT foo (unterminated");
    }

    public void testEofInAttributeValue() throws Exception {
        assertParseFailure("<!DOCTYPE foo [<!ATTLIST foo x y \"unterminated");
    }

    public void testEofInEntityValue() throws Exception {
        assertParseFailure("<!DOCTYPE foo [<!ENTITY aaa \"unterminated");
    }

    public void testEofInStartTagAttributeValue() throws Exception {
        assertParseFailure("<long foo=\"unterminated");
    }

    public void testEofInReadCharRelaxed() throws Exception {
        assertRelaxedParseFailure("<!DOCTYPE foo [<!ELEMENT foo ()");// EOF in read('>')

    }

    public void testEofAfterReadCharArrayRelaxed() throws Exception {
        assertRelaxedParseFailure("<!DOCTYPE foo [<!ELEMENT foo EMPTY");// EOF in read('>')

    }
}

