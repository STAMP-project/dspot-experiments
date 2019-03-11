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


import java.util.Arrays;
import junit.framework.TestCase;
import org.xmlpull.v1.XmlPullParser;


/**
 * Test doctype handling in pull parsers.
 */
public abstract class PullParserDtdTest extends TestCase {
    private static final int READ_BUFFER_SIZE = 8192;

    /**
     * Android's Expat pull parser permits parameter entities to be declared,
     * but it doesn't permit such entities to be used.
     */
    public void testDeclaringParameterEntities() throws Exception {
        String xml = "<!DOCTYPE foo [" + ("  <!ENTITY % a \"android\">" + "]><foo></foo>");
        XmlPullParser parser = newPullParser(xml);
        while ((parser.next()) != (XmlPullParser.END_DOCUMENT)) {
        } 
    }

    public void testUsingParameterEntitiesInDtds() throws Exception {
        assertParseFailure(("<!DOCTYPE foo [" + (("  <!ENTITY % a \"android\">" + "  <!ENTITY b \"%a;\">") + "]><foo></foo>")));
    }

    public void testUsingParameterInDocuments() throws Exception {
        assertParseFailure(("<!DOCTYPE foo [" + ("  <!ENTITY % a \"android\">" + "]><foo>&a;</foo>")));
    }

    public void testGeneralAndParameterEntityWithTheSameName() throws Exception {
        String xml = "<!DOCTYPE foo [" + (("  <!ENTITY a \"aaa\">" + "  <!ENTITY % a \"bbb\">") + "]><foo>&a;</foo>");
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals("aaa", parser.getText());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    public void testInternalEntities() throws Exception {
        String xml = "<!DOCTYPE foo [" + ("  <!ENTITY a \"android\">" + "]><foo>&a;</foo>");
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals("android", parser.getText());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    public void testExternalDtdIsSilentlyIgnored() throws Exception {
        String xml = "<!DOCTYPE foo SYSTEM \"http://127.0.0.1:1/no-such-file.dtd\"><foo></foo>";
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("foo", parser.getName());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals("foo", parser.getName());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    public void testExternalAndInternalDtd() throws Exception {
        String xml = "<!DOCTYPE foo SYSTEM \"http://127.0.0.1:1/no-such-file.dtd\" [" + ("  <!ENTITY a \"android\">" + "]><foo>&a;</foo>");
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals("android", parser.getText());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    public void testInternalEntitiesAreParsed() throws Exception {
        String xml = "<!DOCTYPE foo [" + ("  <!ENTITY a \"&#38;#65;\">"// &#38; expands to '&', &#65; expands to 'A'
         + "]><foo>&a;</foo>");
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals("A", parser.getText());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    public void testFoolishlyRecursiveInternalEntities() throws Exception {
        String xml = "<!DOCTYPE foo [" + ("  <!ENTITY a \"&#38;#38;#38;#38;\">"// expand &#38; to '&' only twice
         + "]><foo>&a;</foo>");
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals("&#38;#38;", parser.getText());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    /**
     * Test that the output of {@code &#38;} is parsed, but {@code &amp;} isn't.
     * http://www.w3.org/TR/2008/REC-xml-20081126/#sec-entexpand
     */
    public void testExpansionOfEntityAndCharacterReferences() throws Exception {
        String xml = "<!DOCTYPE foo [" + ((("<!ENTITY example \"<p>An ampersand (&#38;#38;) may be escaped\n" + "numerically (&#38;#38;#38;) or with a general entity\n") + "(&amp;amp;).</p>\" >") + "]><foo>&example;</foo>");
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("p", parser.getName());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals(("An ampersand (&) may be escaped\n" + ("numerically (&#38;) or with a general entity\n" + "(&amp;).")), parser.getText());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals("p", parser.getName());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    public void testGeneralEntitiesAreStoredUnresolved() throws Exception {
        String xml = "<!DOCTYPE foo [" + (("<!ENTITY b \"&a;\" >" + "<!ENTITY a \"android\" >") + "]><foo>&a;</foo>");
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals("android", parser.getText());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    public void testStructuredEntityAndNextToken() throws Exception {
        String xml = "<!DOCTYPE foo [<!ENTITY bb \"<bar>baz<!--quux--></bar>\">]><foo>a&bb;c</foo>";
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.DOCDECL, parser.nextToken());
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.nextToken());
        TestCase.assertEquals("foo", parser.getName());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.nextToken());
        TestCase.assertEquals("a", parser.getText());
        TestCase.assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
        TestCase.assertEquals("bb", parser.getName());
        TestCase.assertEquals("", parser.getText());
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.nextToken());
        TestCase.assertEquals("bar", parser.getName());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.nextToken());
        TestCase.assertEquals("baz", parser.getText());
        TestCase.assertEquals(XmlPullParser.COMMENT, parser.nextToken());
        TestCase.assertEquals("quux", parser.getText());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.nextToken());
        TestCase.assertEquals("bar", parser.getName());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.nextToken());
        TestCase.assertEquals("c", parser.getText());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    /**
     * Android's Expat replaces external entities with the empty string.
     */
    public void testUsingExternalEntities() throws Exception {
        String xml = "<!DOCTYPE foo [" + ("  <!ENTITY a SYSTEM \"http://localhost:1/no-such-file.xml\">" + "]><foo>&a;</foo>");
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        // &a; is dropped!
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    /**
     * Android's ExpatPullParser replaces missing entities with the empty string
     * when an external DTD is declared.
     */
    public void testExternalDtdAndMissingEntity() throws Exception {
        String xml = "<!DOCTYPE foo SYSTEM \"http://127.0.0.1:1/no-such-file.dtd\">" + "<foo>&a;</foo>";
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    public void testExternalIdIsCaseSensitive() throws Exception {
        // The spec requires 'SYSTEM' in upper case
        assertParseFailure(("<!DOCTYPE foo [" + ("  <!ENTITY a system \"http://localhost:1/no-such-file.xml\">" + "]><foo/>")));
    }

    /**
     * Use a DTD to specify that {@code <foo>} only contains {@code <bar>} tags.
     * Validating parsers react to this by dropping whitespace between the two
     * tags.
     */
    public void testDtdDoesNotInformIgnorableWhitespace() throws Exception {
        String xml = "<!DOCTYPE foo [\n" + ((("  <!ELEMENT foo (bar)*>\n" + "  <!ELEMENT bar ANY>\n") + "]>") + "<foo>  \n  <bar></bar>  \t  </foo>");
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("foo", parser.getName());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals("  \n  ", parser.getText());
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("bar", parser.getName());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals("bar", parser.getName());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals("  \t  ", parser.getText());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    public void testEmptyDoesNotInformIgnorableWhitespace() throws Exception {
        String xml = "<!DOCTYPE foo [\n" + (("  <!ELEMENT foo EMPTY>\n" + "]>") + "<foo>  \n  </foo>");
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("foo", parser.getName());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals("  \n  ", parser.getText());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    /**
     * Test that the parser doesn't expand the entity attributes.
     */
    public void testAttributeOfTypeEntity() throws Exception {
        String xml = "<!DOCTYPE foo [\n" + ((((("  <!ENTITY a \"android\">" + "  <!ELEMENT foo ANY>\n") + "  <!ATTLIST foo\n") + "    bar ENTITY #IMPLIED>") + "]>") + "<foo bar=\"a\"></foo>");
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("foo", parser.getName());
        TestCase.assertEquals("a", parser.getAttributeValue(null, "bar"));
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    public void testTagStructureNotValidated() throws Exception {
        String xml = "<!DOCTYPE foo [\n" + (((("  <!ELEMENT foo (bar)*>\n" + "  <!ELEMENT bar ANY>\n") + "  <!ELEMENT baz ANY>\n") + "]>") + "<foo><bar/><bar/><baz/></foo>");
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("foo", parser.getName());
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("bar", parser.getName());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("bar", parser.getName());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("baz", parser.getName());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    public void testAttributeDefaultValues() throws Exception {
        String xml = "<!DOCTYPE foo [\n" + (((((("  <!ATTLIST bar\n" + "    baz (a|b|c)  \"c\">") + "]>") + "<foo>") + "<bar/>") + "<bar baz=\"a\"/>") + "</foo>");
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("bar", parser.getName());
        TestCase.assertEquals("c", parser.getAttributeValue(null, "baz"));
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("bar", parser.getName());
        TestCase.assertEquals("a", parser.getAttributeValue(null, "baz"));
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    public void testAttributeDefaultValueEntitiesExpanded() throws Exception {
        String xml = "<!DOCTYPE foo [\n" + ((((("  <!ENTITY g \"ghi\">" + "  <!ELEMENT foo ANY>\n") + "  <!ATTLIST foo\n") + "    bar CDATA \"abc &amp; def &g; jk\">") + "]>") + "<foo></foo>");
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("foo", parser.getName());
        TestCase.assertEquals("abc & def ghi jk", parser.getAttributeValue(null, "bar"));
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    public void testAttributeDefaultValuesAndNamespaces() throws Exception {
        String xml = "<!DOCTYPE foo [\n" + ((("  <!ATTLIST foo\n" + "    bar:a CDATA \"android\">") + "]>") + "<foo xmlns:bar='http://bar'></foo>");
        XmlPullParser parser = newPullParser(xml);
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("foo", parser.getName());
        // In Expat, namespaces don't apply to default attributes
        int index = indexOfAttributeWithName(parser, "bar:a");
        TestCase.assertEquals("", parser.getAttributeNamespace(index));
        TestCase.assertEquals("bar:a", parser.getAttributeName(index));
        TestCase.assertEquals("android", parser.getAttributeValue(index));
        TestCase.assertEquals("CDATA", parser.getAttributeType(index));
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    public void testAttributeEntitiesExpandedEagerly() throws Exception {
        assertParseFailure(("<!DOCTYPE foo [\n" + ((((("  <!ELEMENT foo ANY>\n" + "  <!ATTLIST foo\n") + "    bar CDATA \"abc &amp; def &g; jk\">") + "  <!ENTITY g \"ghi\">") + "]>") + "<foo></foo>")));
    }

    public void testRequiredAttributesOmitted() throws Exception {
        String xml = "<!DOCTYPE foo [\n" + (((("  <!ELEMENT foo ANY>\n" + "  <!ATTLIST foo\n") + "    bar (a|b|c) #REQUIRED>") + "]>") + "<foo></foo>");
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("foo", parser.getName());
        TestCase.assertEquals(null, parser.getAttributeValue(null, "bar"));
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
    }

    public void testFixedAttributesWithConflictingValues() throws Exception {
        String xml = "<!DOCTYPE foo [\n" + (((("  <!ELEMENT foo ANY>\n" + "  <!ATTLIST foo\n") + "    bar (a|b|c) #FIXED \"c\">") + "]>") + "<foo bar=\"a\"></foo>");
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("foo", parser.getName());
        TestCase.assertEquals("a", parser.getAttributeValue(null, "bar"));
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
    }

    public void testParsingNotations() throws Exception {
        String xml = "<!DOCTYPE foo [\n" + ((((((("  <!NOTATION type-a PUBLIC \"application/a\"> \n" + "  <!NOTATION type-b PUBLIC \"image/b\">\n") + "  <!NOTATION type-c PUBLIC \"-//W3C//DTD SVG 1.1//EN\"\n") + "     \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\"> \n") + "  <!ENTITY file          SYSTEM \"d.xml\">\n") + "  <!ENTITY fileWithNdata SYSTEM \"e.bin\" NDATA type-b>\n") + "]>") + "<foo type=\"type-a\"/>");
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("foo", parser.getName());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
    }

    public void testCommentsInDoctype() throws Exception {
        String xml = "<!DOCTYPE foo [" + ("  <!-- ' -->" + "]><foo>android</foo>");
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals("android", parser.getText());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    public void testDoctypeNameOnly() throws Exception {
        String xml = "<!DOCTYPE foo><foo></foo>";
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals("foo", parser.getName());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals("foo", parser.getName());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    public void testVeryLongEntities() throws Exception {
        String a = repeat('a', ((PullParserDtdTest.READ_BUFFER_SIZE) + 1));
        String b = repeat('b', ((PullParserDtdTest.READ_BUFFER_SIZE) + 1));
        String c = repeat('c', ((PullParserDtdTest.READ_BUFFER_SIZE) + 1));
        String xml = ((((((((((((("<!DOCTYPE foo [\n" + "  <!ENTITY ") + a) + "  \"d &") + b) + "; e\">") + "  <!ENTITY ") + b) + "  \"f ") + c) + " g\">") + "]>") + "<foo>h &") + a) + "; i</foo>";
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals((("h d f " + c) + " g e i"), parser.getText());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    public void testManuallyRegisteredEntitiesWithDoctypeParsing() throws Exception {
        String xml = "<foo>&a;</foo>";
        XmlPullParser parser = newPullParser(xml);
        try {
            parser.defineEntityReplacementText("a", "android");
            TestCase.fail();
        } catch (UnsupportedOperationException expected) {
        } catch (IllegalStateException expected) {
        }
    }

    public void testDoctypeWithNextToken() throws Exception {
        String xml = "<!DOCTYPE foo [<!ENTITY bb \"bar baz\">]><foo>a&bb;c</foo>";
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.DOCDECL, parser.nextToken());
        TestCase.assertEquals(" foo [<!ENTITY bb \"bar baz\">]", parser.getText());
        TestCase.assertNull(parser.getName());
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.nextToken());
        TestCase.assertEquals(XmlPullParser.TEXT, parser.next());
        TestCase.assertEquals("abar bazc", parser.getText());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    public void testDoctypeSpansBuffers() throws Exception {
        char[] doctypeChars = new char[(PullParserDtdTest.READ_BUFFER_SIZE) + 1];
        Arrays.fill(doctypeChars, 'x');
        String doctypeBody = (" foo [<!--" + (new String(doctypeChars))) + "-->]";
        String xml = ("<!DOCTYPE" + doctypeBody) + "><foo/>";
        XmlPullParser parser = newPullParser(xml);
        TestCase.assertEquals(XmlPullParser.DOCDECL, parser.nextToken());
        TestCase.assertEquals(doctypeBody, parser.getText());
        TestCase.assertEquals(XmlPullParser.START_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_TAG, parser.next());
        TestCase.assertEquals(XmlPullParser.END_DOCUMENT, parser.next());
    }

    public void testDoctypeInDocumentElement() throws Exception {
        assertParseFailure("<foo><!DOCTYPE foo></foo>");
    }

    public void testDoctypeAfterDocumentElement() throws Exception {
        assertParseFailure("<foo/><!DOCTYPE foo>");
    }
}

