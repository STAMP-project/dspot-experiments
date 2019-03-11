package com.baeldung.pojo.test;


import com.baeldung.annotation.pojo.Customer;
import com.thoughtworks.xstream.XStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.junit.Assert;
import org.junit.Test;


public class XmlToObjectAnnotationIntegrationTest {
    private XStream xstream = null;

    @Test
    public void convertXmlToObjectFromFile() throws FileNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();
        FileReader reader = new FileReader(classLoader.getResource("data-file-alias-field.xml").getFile());
        Customer customer = ((Customer) (xstream.fromXML(reader)));
        Assert.assertNotNull(customer);
        Assert.assertNotNull(customer.getFirstName());
    }
}

