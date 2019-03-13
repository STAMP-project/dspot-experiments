package com.baeldung.pojo.test;


import com.baeldung.pojo.Customer;
import com.thoughtworks.xstream.XStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.junit.Assert;
import org.junit.Test;


public class XmlToObjectIgnoreFieldsIntegrationTest {
    private XStream xstream = null;

    @Test
    public void convertXmlToObjectFromFile() throws FileNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();
        FileReader reader = new FileReader(classLoader.getResource("data-file-ignore-field.xml").getFile());
        Customer customer = ((Customer) (xstream.fromXML(reader)));
        Assert.assertNotNull(customer);
        // System.out.println(customer);
    }
}

