package com.baeldung.xml.jibx;


import java.io.FileNotFoundException;
import java.io.InputStream;
import junit.framework.Assert;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.junit.Test;


public class CustomerIntegrationTest {
    @Test
    public void whenUnmarshalXML_ThenFieldsAreMapped() throws FileNotFoundException, JiBXException {
        IBindingFactory bfact = BindingDirectory.getFactory(Customer.class);
        IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("Customer1.xml");
        Customer customer = ((Customer) (uctx.unmarshalDocument(inputStream, null)));
        Assert.assertEquals("Stefan Jaegar", customer.getPerson().getName());
        Assert.assertEquals("Davos Dorf", customer.getCity());
    }

    @Test
    public void WhenUnmarshal_ThenMappingInherited() throws FileNotFoundException, JiBXException {
        IBindingFactory bfact = BindingDirectory.getFactory(Customer.class);
        IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("Customer1.xml");
        Customer customer = ((Customer) (uctx.unmarshalDocument(inputStream, null)));
        Assert.assertEquals(12345, customer.getPerson().getCustomerId());
    }

    @Test
    public void WhenUnmarshal_ThenPhoneMappingRead() throws FileNotFoundException, JiBXException {
        IBindingFactory bfact = BindingDirectory.getFactory(Customer.class);
        IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("Customer1.xml");
        Customer customer = ((Customer) (uctx.unmarshalDocument(inputStream, null)));
        Assert.assertEquals("234678", customer.getHomePhone().getNumber());
    }
}

