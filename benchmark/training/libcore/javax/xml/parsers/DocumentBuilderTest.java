/**
 * Copyright (C) 2009 The Android Open Source Project
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
package libcore.javax.xml.parsers;


import junit.framework.TestCase;
import org.xml.sax.SAXParseException;


public class DocumentBuilderTest extends TestCase {
    // http://code.google.com/p/android/issues/detail?id=2607
    public void test_characterReferences() throws Exception {
        TestCase.assertEquals("aAb", firstChildTextOf(domOf("<p>a&#65;b</p>")));
        TestCase.assertEquals("aAb", firstChildTextOf(domOf("<p>a&#x41;b</p>")));
        TestCase.assertEquals("a\u00fcb", firstChildTextOf(domOf("<p>a&#252;b</p>")));
        TestCase.assertEquals("a\u00fcb", firstChildTextOf(domOf("<p>a&#xfc;b</p>")));
    }

    // http://code.google.com/p/android/issues/detail?id=2607
    public void test_predefinedEntities() throws Exception {
        TestCase.assertEquals("a<b", firstChildTextOf(domOf("<p>a&lt;b</p>")));
        TestCase.assertEquals("a>b", firstChildTextOf(domOf("<p>a&gt;b</p>")));
        TestCase.assertEquals("a&b", firstChildTextOf(domOf("<p>a&amp;b</p>")));
        TestCase.assertEquals("a'b", firstChildTextOf(domOf("<p>a&apos;b</p>")));
        TestCase.assertEquals("a\"b", firstChildTextOf(domOf("<p>a&quot;b</p>")));
    }

    // http://code.google.com/p/android/issues/detail?id=2487
    public void test_cdata_attributes() throws Exception {
        TestCase.assertEquals("hello & world", attrOf(firstElementOf(domOf("<?xml version=\"1.0\"?><root attr=\"hello &amp; world\" />"))));
        try {
            domOf("<?xml version=\"1.0\"?><root attr=\"hello <![CDATA[ some-cdata ]]> world\" />");
            TestCase.fail("SAXParseException not thrown");
        } catch (SAXParseException ex) {
            // Expected.
        }
        TestCase.assertEquals("hello <![CDATA[ some-cdata ]]> world", attrOf(firstElementOf(domOf("<?xml version=\"1.0\"?><root attr=\"hello &lt;![CDATA[ some-cdata ]]&gt; world\" />"))));
        TestCase.assertEquals("hello <![CDATA[ some-cdata ]]> world", attrOf(firstElementOf(domOf("<?xml version=\"1.0\"?><root attr=\"hello &lt;![CDATA[ some-cdata ]]> world\" />"))));
    }

    // http://code.google.com/p/android/issues/detail?id=2487
    public void test_cdata_body() throws Exception {
        TestCase.assertEquals("hello & world", firstChildTextOf(domOf("<?xml version=\"1.0\"?><root>hello &amp; world</root>")));
        TestCase.assertEquals("hello  some-cdata  world", firstChildTextOf(domOf("<?xml version=\"1.0\"?><root>hello <![CDATA[ some-cdata ]]> world</root>")));
        TestCase.assertEquals("hello <![CDATA[ some-cdata ]]> world", firstChildTextOf(domOf("<?xml version=\"1.0\"?><root>hello &lt;![CDATA[ some-cdata ]]&gt; world</root>")));
        try {
            domOf("<?xml version=\"1.0\"?><root>hello &lt;![CDATA[ some-cdata ]]> world</root>");
            TestCase.fail("SAXParseException not thrown");
        } catch (SAXParseException ex) {
            // Expected.
        }
    }
}

