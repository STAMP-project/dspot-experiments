package com.baeldung.xml;


import com.baeldung.xml.binding.Tutorial;
import java.io.File;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class StaxParserUnitTest {
    final String fileName = "src/test/resources/example_stax.xml";

    StaxParser parser;

    @Test
    public void getAllTutorialsTest() {
        parser = new StaxParser(new File(fileName));
        List<Tutorial> tutorials = parser.getAllTutorial();
        Assert.assertNotNull(tutorials);
        Assert.assertTrue(((tutorials.size()) == 4));
        Assert.assertTrue(tutorials.get(0).getType().equalsIgnoreCase("java"));
    }
}

