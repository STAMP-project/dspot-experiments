package com.baeldung.xml;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class DefaultParserUnitTest {
    final String fileName = "src/test/resources/example_default_parser.xml";

    final String fileNameSpace = "src/test/resources/example_default_parser_namespace.xml";

    DefaultParser parser;

    @Test
    public void getFirstLevelNodeListTest() {
        parser = new DefaultParser(new File(fileName));
        NodeList list = parser.getFirstLevelNodeList();
        Assert.assertNotNull(list);
        Assert.assertTrue(((list.getLength()) == 4));
    }

    @Test
    public void getNodeListByTitleTest() {
        parser = new DefaultParser(new File(fileName));
        NodeList list = parser.getNodeListByTitle("XML");
        for (int i = 0; (null != list) && (i < (list.getLength())); i++) {
            Node nod = list.item(i);
            Assert.assertEquals("java", nod.getAttributes().getNamedItem("type").getTextContent());
            Assert.assertEquals("02", nod.getAttributes().getNamedItem("tutId").getTextContent());
            Assert.assertEquals("XML", nod.getFirstChild().getTextContent());
            Assert.assertEquals("title", nod.getFirstChild().getNodeName());
            Assert.assertEquals("description", nod.getChildNodes().item(1).getNodeName());
            Assert.assertEquals("Introduction to XPath", nod.getChildNodes().item(1).getTextContent());
            Assert.assertEquals("author", nod.getLastChild().getNodeName());
            Assert.assertEquals("XMLAuthor", nod.getLastChild().getTextContent());
        }
    }

    @Test
    public void getNodeByIdTest() {
        parser = new DefaultParser(new File(fileName));
        Node node = parser.getNodeById("03");
        String type = node.getAttributes().getNamedItem("type").getNodeValue();
        Assert.assertEquals("android", type);
    }

    @Test
    public void getNodeListByDateTest() {
        parser = new DefaultParser(new File(fileName));
        NodeList list = parser.getNodeListByTitle("04022016");
        for (int i = 0; (null != list) && (i < (list.getLength())); i++) {
            Node nod = list.item(i);
            Assert.assertEquals("java", nod.getAttributes().getNamedItem("type").getTextContent());
            Assert.assertEquals("04", nod.getAttributes().getNamedItem("tutId").getTextContent());
            Assert.assertEquals("Spring", nod.getFirstChild().getTextContent());
            Assert.assertEquals("title", nod.getFirstChild().getNodeName());
            Assert.assertEquals("description", nod.getChildNodes().item(1).getNodeName());
            Assert.assertEquals("Introduction to Spring", nod.getChildNodes().item(1).getTextContent());
            Assert.assertEquals("author", nod.getLastChild().getNodeName());
            Assert.assertEquals("SpringAuthor", nod.getLastChild().getTextContent());
        }
    }

    @Test
    public void getNodeListWithNamespaceTest() {
        parser = new DefaultParser(new File(fileNameSpace));
        NodeList list = parser.getAllTutorials();
        Assert.assertNotNull(list);
        Assert.assertTrue(((list.getLength()) == 4));
    }
}

