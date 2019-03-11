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


import junit.framework.TestCase;
import org.w3c.dom.Document;


/**
 * Test the parsing of the XML declaration, plus the additional document fields
 * captured during parsing.
 */
public class DeclarationTest extends TestCase {
    private String systemIdA;

    private Document documentA;

    private String systemIdB;

    private Document documentB;

    /**
     * XML parsers are advised of the document's character set via two channels:
     * via the declaration and also the document's input source. To test that
     * each of these winds up in the correct location in the document model, we
     * supply different names for each. This is only safe because for the subset
     * of characters in the document, the character sets are equivalent.
     */
    public void testGetInputEncoding() throws Exception {
        TestCase.assertEquals("US-ASCII", documentA.getInputEncoding());
        TestCase.assertEquals("ISO-8859-1", documentB.getInputEncoding());
    }

    public void testGetXmlEncoding() throws Exception {
        String message = "This implementation doesn't parse the encoding from the XML declaration";
        TestCase.assertEquals(message, "ISO-8859-1", documentA.getXmlEncoding());
        TestCase.assertEquals(message, "US-ASCII", documentB.getXmlEncoding());
    }

    public void testGetXmlVersion() throws Exception {
        String message = "This implementation doesn't parse the version from the XML declaration";
        TestCase.assertEquals(message, "1.0", documentA.getXmlVersion());
        TestCase.assertEquals(message, "1.1", documentB.getXmlVersion());
    }

    public void testGetXmlStandalone() throws Exception {
        String message = "This implementation doesn't parse standalone from the XML declaration";
        TestCase.assertEquals(message, false, documentA.getXmlStandalone());
        TestCase.assertEquals(message, true, documentB.getXmlStandalone());
    }

    public void testGetDocumentUri() throws Exception {
        TestCase.assertEquals(systemIdA, documentA.getDocumentURI());
        TestCase.assertEquals(systemIdB, documentB.getDocumentURI());
    }
}

