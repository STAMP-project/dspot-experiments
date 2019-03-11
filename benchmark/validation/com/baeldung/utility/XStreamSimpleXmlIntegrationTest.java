package com.baeldung.utility;


import com.baeldung.pojo.Customer;
import com.thoughtworks.xstream.XStream;
import org.junit.Assert;
import org.junit.Test;


public class XStreamSimpleXmlIntegrationTest {
    private Customer customer;

    private String dataXml;

    private XStream xstream;

    @Test
    public void testClassAliasedAnnotation() {
        Assert.assertNotEquals((-1), dataXml.indexOf("<customer>"));
    }

    @Test
    public void testFieldAliasedAnnotation() {
        Assert.assertNotEquals((-1), dataXml.indexOf("<fn>"));
    }

    @Test
    public void testImplicitCollection() {
        Assert.assertEquals((-1), dataXml.indexOf("contactDetailsList"));
    }

    @Test
    public void testDateFieldFormating() {
        Assert.assertEquals("14-02-1986", dataXml.substring(((dataXml.indexOf("<dob>")) + 5), dataXml.indexOf("</dob>")));
    }

    @Test
    public void testOmitField() {
        Assert.assertEquals((-1), dataXml.indexOf("lastName"));
    }
}

