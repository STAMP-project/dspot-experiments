package com.baeldung.xml;


import java.io.File;
import java.util.List;
import org.jdom2.Element;
import org.junit.Assert;
import org.junit.Test;


public class JDomParserUnitTest {
    final String fileName = "src/test/resources/example_jdom.xml";

    JDomParser parser;

    @Test
    public void getFirstElementListTest() {
        parser = new JDomParser(new File(fileName));
        List<Element> firstList = parser.getAllTitles();
        Assert.assertNotNull(firstList);
        Assert.assertTrue(((firstList.size()) == 4));
        Assert.assertTrue(firstList.get(0).getAttributeValue("type").equals("java"));
    }

    @Test
    public void getElementByIdTest() {
        parser = new JDomParser(new File(fileName));
        Element el = parser.getNodeById("03");
        String type = el.getAttributeValue("type");
        Assert.assertEquals("android", type);
    }
}

