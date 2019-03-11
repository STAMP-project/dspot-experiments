package com.baeldung.pojo.test;


import com.baeldung.pojo.Customer;
import com.baeldung.utility.SimpleDataGeneration;
import com.thoughtworks.xstream.XStream;
import java.io.FileReader;
import org.junit.Assert;
import org.junit.Test;


public class XmlToObjectIntegrationTest {
    private XStream xstream = null;

    @Test
    public void convertXmlToObjectFromFile() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        FileReader reader = new FileReader(classLoader.getResource("data-file.xml").getFile());
        Customer customer = ((Customer) (xstream.fromXML(reader)));
        Assert.assertNotNull(customer);
    }

    @Test
    public void convertXmlToObjectFromString() {
        Customer customer = SimpleDataGeneration.generateData();
        String dataXml = xstream.toXML(customer);
        Customer convertedCustomer = ((Customer) (xstream.fromXML(dataXml)));
        Assert.assertNotNull(convertedCustomer);
    }
}

