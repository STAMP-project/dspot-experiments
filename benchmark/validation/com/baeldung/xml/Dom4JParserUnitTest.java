package com.baeldung.xml;


import java.io.File;
import java.util.List;
import org.dom4j.Element;
import org.dom4j.Node;
import org.junit.Assert;
import org.junit.Test;


public class Dom4JParserUnitTest {
    final String fileName = "src/test/resources/example_dom4j.xml";

    Dom4JParser parser;

    @Test
    public void getRootElementTest() {
        parser = new Dom4JParser(new File(fileName));
        Element root = parser.getRootElement();
        Assert.assertNotNull(root);
        Assert.assertTrue(((root.elements().size()) == 4));
    }

    @Test
    public void getFirstElementListTest() {
        parser = new Dom4JParser(new File(fileName));
        List<Element> firstList = parser.getFirstElementList();
        Assert.assertNotNull(firstList);
        Assert.assertTrue(((firstList.size()) == 4));
        Assert.assertTrue(firstList.get(0).attributeValue("type").equals("java"));
    }

    @Test
    public void getElementByIdTest() {
        parser = new Dom4JParser(new File(fileName));
        Node element = parser.getNodeById("03");
        String type = element.valueOf("@type");
        Assert.assertEquals("android", type);
    }

    @Test
    public void getElementsListByTitleTest() {
        parser = new Dom4JParser(new File(fileName));
        Node element = parser.getElementsListByTitle("XML");
        Assert.assertEquals("java", element.valueOf("@type"));
        Assert.assertEquals("02", element.valueOf("@tutId"));
        Assert.assertEquals("XML", element.selectSingleNode("title").getText());
        Assert.assertEquals("title", element.selectSingleNode("title").getName());
    }

    @Test
    public void generateModifiedDocumentTest() {
        parser = new Dom4JParser(new File(fileName));
        parser.generateModifiedDocument();
        File generatedFile = new File("src/test/resources/example_dom4j_updated.xml");
        Assert.assertTrue(generatedFile.exists());
        parser.setFile(generatedFile);
        Node element = parser.getNodeById("02");
        Assert.assertEquals("XML updated", element.selectSingleNode("title").getText());
    }

    @Test
    public void generateNewDocumentTest() {
        parser = new Dom4JParser(new File(fileName));
        parser.generateNewDocument();
        File newFile = new File("src/test/resources/example_dom4j_new.xml");
        Assert.assertTrue(newFile.exists());
        parser.setFile(newFile);
        Node element = parser.getNodeById("01");
        Assert.assertEquals("XML with Dom4J", element.selectSingleNode("title").getText());
    }
}

