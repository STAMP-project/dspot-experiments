/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;


import XMLRenderer.ENCODING;
import org.junit.Test;


public class XMLRendererTest extends AbstractRendererTst {
    @Test
    public void testXMLEscapingWithUTF8() throws Exception {
        Renderer renderer = getRenderer();
        renderer.setProperty(ENCODING, "UTF-8");
        verifyXmlEscaping(renderer, "\ud801\udc1c");
    }

    @Test
    public void testXMLEscapingWithoutUTF8() throws Exception {
        Renderer renderer = getRenderer();
        renderer.setProperty(ENCODING, "ISO-8859-1");
        verifyXmlEscaping(renderer, "&#x1041c;");
    }
}

