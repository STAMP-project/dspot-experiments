package com.baeldung.xml;


import javax.xml.parsers.DocumentBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XercesDomUnitTest {
    private final String FILE_NAME = "src/test/resources/example_jdom.xml";

    private final String OUTPUT_DOM = "src/test/resources/Xerces_dom.xml";

    private Document doc;

    private DocumentBuilder builder;

    @Test
    public void whenGetElementByTag_thenSuccess() {
        NodeList nodeList = doc.getElementsByTagName("tutorial");
        Node first = nodeList.item(0);
        Assert.assertEquals(4, nodeList.getLength());
        Assert.assertEquals(Node.ELEMENT_NODE, first.getNodeType());
        Assert.assertEquals("tutorial", first.getNodeName());
    }

    @Test
    public void whenGetFirstElementAttributes_thenSuccess() {
        Node first = doc.getElementsByTagName("tutorial").item(0);
        NamedNodeMap attrList = first.getAttributes();
        Assert.assertEquals(2, attrList.getLength());
        Assert.assertEquals("tutId", attrList.item(0).getNodeName());
        Assert.assertEquals("01", attrList.item(0).getNodeValue());
        Assert.assertEquals("type", attrList.item(1).getNodeName());
        Assert.assertEquals("java", attrList.item(1).getNodeValue());
    }

    @Test
    public void whenTraverseChildNodes_thenSuccess() {
        Node first = doc.getElementsByTagName("tutorial").item(0);
        NodeList nodeList = first.getChildNodes();
        int n = nodeList.getLength();
        Node current;
        for (int i = 0; i < n; i++) {
            current = nodeList.item(i);
            if ((current.getNodeType()) == (Node.ELEMENT_NODE)) {
                System.out.println((((current.getNodeName()) + ": ") + (current.getTextContent())));
            }
        }
    }

    @Test
    public void whenModifyElementAttribute_thenModified() {
        NodeList nodeList = doc.getElementsByTagName("tutorial");
        Element first = ((Element) (nodeList.item(0)));
        Assert.assertEquals("java", first.getAttribute("type"));
        first.setAttribute("type", "other");
        Assert.assertEquals("other", first.getAttribute("type"));
    }

    @Test
    public void whenCreateNewDocument_thenCreated() throws Exception {
        Document newDoc = builder.newDocument();
        Element root = newDoc.createElement("users");
        newDoc.appendChild(root);
        Element first = newDoc.createElement("user");
        root.appendChild(first);
        first.setAttribute("id", "1");
        Element email = newDoc.createElement("email");
        email.appendChild(newDoc.createTextNode("john@example.com"));
        first.appendChild(email);
        Assert.assertEquals(1, newDoc.getChildNodes().getLength());
        Assert.assertEquals("users", newDoc.getChildNodes().item(0).getNodeName());
        printDom(newDoc);
        saveDomToFile(newDoc, OUTPUT_DOM);
    }
}

