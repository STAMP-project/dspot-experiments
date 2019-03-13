package com.baeldung.xml;


import java.io.File;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class JaxenDemoUnitTest {
    final String fileName = "src/test/resources/example_jaxen.xml";

    JaxenDemo jaxenDemo;

    @Test
    public void getFirstLevelNodeListTest() {
        jaxenDemo = new JaxenDemo(new File(fileName));
        List<?> list = jaxenDemo.getAllTutorial();
        Assert.assertNotNull(list);
        Assert.assertTrue(((list.size()) == 4));
    }
}

