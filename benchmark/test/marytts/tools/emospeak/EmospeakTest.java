/**
 * Copyright 2000-2006 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of MARY TTS.
 *
 * MARY TTS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package marytts.tools.emospeak;


import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.junit.Test;
import org.w3c.dom.Document;


/**
 *
 *
 * @author Marc Schr&ouml;der
 */
public class EmospeakTest {
    @Test
    public void testTransform() throws Exception {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        System.err.println(("Using XSL processor " + (tFactory.getClass().getName())));
        StreamSource stylesheetStream = new StreamSource(EmoTransformer.class.getResourceAsStream("emotion-to-mary.xsl"));
        Templates stylesheet = tFactory.newTemplates(stylesheetStream);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
        Transformer transformer = stylesheet.newTransformer();
        Document emotion = docBuilder.parse(EmospeakTest.class.getResourceAsStream("emotion.xml"));
        DOMSource domSource = new DOMSource(emotion);
        StringWriter sw = new StringWriter();
        StreamResult streamResult = new StreamResult(sw);
        transformer.transform(domSource, streamResult);
        String maryxmlString = sw.toString();
        System.out.println(("Converted to maryxml: " + maryxmlString));
    }
}

