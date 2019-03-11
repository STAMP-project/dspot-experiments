/**
 * Copyright 2015 MovingBlocks
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
package org.terasology.rendering.nui.widgets.browser.data.html;


import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.terasology.rendering.nui.widgets.browser.data.DocumentData;
import org.terasology.rendering.nui.widgets.browser.data.ParagraphData;
import org.terasology.rendering.nui.widgets.browser.data.basic.FlowParagraphData;
import org.xml.sax.SAXException;


public class HTMLParserTest {
    private HTMLParser htmlParser = new HTMLParser(( name, bold) -> null);

    @Test
    public void testParseEmptyDocument() throws IOException, ParserConfigurationException, SAXException {
        DocumentData documentData = htmlParser.parseHTMLDocument("<body></body>");
        Assert.assertEquals(0, documentData.getParagraphs().size());
    }

    @Test(expected = HTMLParseException.class)
    public void testParseUnfinishedBody() throws IOException, ParserConfigurationException, SAXException {
        DocumentData documentData = htmlParser.parseHTMLDocument("<body>");
    }

    @Test
    public void testParseSimpleParagraph() throws IOException, ParserConfigurationException, SAXException {
        DocumentData documentData = htmlParser.parseHTMLDocument("<body><p>Text</p></body>");
        Assert.assertEquals(1, documentData.getParagraphs().size());
        ParagraphData paragraph = documentData.getParagraphs().iterator().next();
        Assert.assertTrue((paragraph instanceof FlowParagraphData));
    }

    @Test
    public void testParseTwoParagraphs() throws IOException, ParserConfigurationException, SAXException {
        DocumentData documentData = htmlParser.parseHTMLDocument("<body><p>Text</p><p>Second</p></body>");
        Assert.assertEquals(2, documentData.getParagraphs().size());
    }

    @Test(expected = HTMLParseException.class)
    public void testParseUnfinishedParagraph() throws IOException, ParserConfigurationException, SAXException {
        DocumentData documentData = htmlParser.parseHTMLDocument("<body><p>Text</body>");
    }

    @Test(expected = HTMLParseException.class)
    public void testParseTextOutsideParagraph() throws IOException, ParserConfigurationException, SAXException {
        DocumentData documentData = htmlParser.parseHTMLDocument("<body>Text</body>");
    }
}

