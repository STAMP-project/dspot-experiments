/**
 * Copyright (C) 2007 The Android Open Source Project
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


import javax.xml.parsers.DocumentBuilder;
import junit.framework.TestCase;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;


public class SimpleBuilderTest extends TestCase {
    private DocumentBuilder builder;

    public void testGoodFile1() throws Exception {
        Document document = builder.parse(getClass().getResourceAsStream("/SimpleBuilderTest.xml"));
        Element root = document.getDocumentElement();
        TestCase.assertNotNull(root);
        TestCase.assertEquals("http://www.foo.bar", root.getNamespaceURI());
        TestCase.assertEquals("t", root.getPrefix());
        TestCase.assertEquals("stuff", root.getLocalName());
        NodeList list = root.getElementsByTagName("nestedStuff");
        TestCase.assertNotNull(list);
        TestCase.assertEquals(list.getLength(), 4);
        Element one = ((Element) (list.item(0)));
        Element two = ((Element) (list.item(1)));
        Element three = ((Element) (list.item(2)));
        Element four = ((Element) (list.item(3)));
        TestCase.assertEquals("This space intentionally left blank.", getTextContent(one));
        TestCase.assertEquals("Nothing to see here - please get along!", getTextContent(two));
        TestCase.assertEquals("Rent this space!", getTextContent(three));
        TestCase.assertEquals("", getTextContent(four));
        TestCase.assertEquals("eins", one.getAttribute("one"));
        TestCase.assertEquals("zwei", two.getAttribute("two"));
        TestCase.assertEquals("drei", three.getAttribute("three"));
        TestCase.assertEquals("vier", four.getAttribute("t:four"));
        TestCase.assertEquals("vier", four.getAttributeNS("http://www.foo.bar", "four"));
        list = document.getChildNodes();
        TestCase.assertNotNull(list);
        String proinst = "";
        String comment = "";
        for (int i = 0; i < (list.getLength()); i++) {
            Node node = list.item(i);
            if (node instanceof ProcessingInstruction) {
                proinst = proinst + (node.getNodeValue());
            } else
                if (node instanceof Comment) {
                    comment = comment + (node.getNodeValue());
                }

        }
        TestCase.assertEquals("The quick brown fox jumps over the lazy dog.", proinst);
        TestCase.assertEquals(" Fragile!  Handle me with care! ", comment);
    }

    public void testGoodFile2() throws Exception {
        Document document = builder.parse(getClass().getResourceAsStream("/staffNS.xml"));
        Element root = document.getDocumentElement();
        TestCase.assertNotNull(root);
    }
}

