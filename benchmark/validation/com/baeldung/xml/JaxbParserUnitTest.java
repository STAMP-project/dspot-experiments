package com.baeldung.xml;


import com.baeldung.xml.binding.Tutorials;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;


public class JaxbParserUnitTest {
    final String fileName = "src/test/resources/example_jaxb.xml";

    JaxbParser parser;

    @Test
    public void getFullDocumentTest() {
        parser = new JaxbParser(new File(fileName));
        Tutorials tutorials = parser.getFullDocument();
        Assert.assertNotNull(tutorials);
        Assert.assertTrue(((tutorials.getTutorial().size()) == 4));
        Assert.assertTrue(tutorials.getTutorial().get(0).getType().equalsIgnoreCase("java"));
    }

    @Test
    public void createNewDocumentTest() {
        File newFile = new File("src/test/resources/example_jaxb_new.xml");
        parser = new JaxbParser(newFile);
        parser.createNewDocument();
        Assert.assertTrue(newFile.exists());
        Tutorials tutorials = parser.getFullDocument();
        Assert.assertNotNull(tutorials);
        Assert.assertTrue(((tutorials.getTutorial().size()) == 1));
        Assert.assertTrue(tutorials.getTutorial().get(0).getTitle().equalsIgnoreCase("XML with Jaxb"));
    }
}

