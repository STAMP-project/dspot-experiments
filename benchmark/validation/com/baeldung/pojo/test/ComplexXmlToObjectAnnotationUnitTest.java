package com.baeldung.pojo.test;


import com.baeldung.complex.pojo.ContactDetails;
import com.baeldung.complex.pojo.Customer;
import com.thoughtworks.xstream.XStream;
import java.io.FileReader;
import org.junit.Assert;
import org.junit.Test;


public class ComplexXmlToObjectAnnotationUnitTest {
    private XStream xstream = null;

    @Test
    public void convertXmlToObjectFromFile() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        FileReader reader = new FileReader(classLoader.getResource("data-file-alias-field-complex.xml").getFile());
        Customer customer = ((Customer) (xstream.fromXML(reader)));
        Assert.assertNotNull(customer);
        Assert.assertNotNull(customer.getContactDetailsList());
    }

    @Test
    public void convertXmlToObjectAttributeFromFile() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        FileReader reader = new FileReader(classLoader.getResource("data-file-alias-field-complex.xml").getFile());
        Customer customer = ((Customer) (xstream.fromXML(reader)));
        Assert.assertNotNull(customer);
        Assert.assertNotNull(customer.getContactDetailsList());
        for (ContactDetails contactDetails : customer.getContactDetailsList()) {
            Assert.assertNotNull(contactDetails.getContactType());
        }
    }
}

