package com.baeldung.snakeyaml;


import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;


public class YAMLToJavaDeserialisationUnitTest {
    @Test
    public void whenLoadYAMLDocument_thenLoadCorrectMap() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("yaml/customer.yaml");
        Map<String, Object> obj = yaml.load(inputStream);
        Assert.assertEquals("John", obj.get("firstName"));
        Assert.assertEquals("Doe", obj.get("lastName"));
        Assert.assertEquals(20, obj.get("age"));
    }

    @Test
    public void whenLoadYAMLDocumentWithTopLevelClass_thenLoadCorrectJavaObject() {
        Yaml yaml = new Yaml(new Constructor(Customer.class));
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("yaml/customer.yaml");
        Customer customer = yaml.load(inputStream);
        Assert.assertEquals("John", customer.getFirstName());
        Assert.assertEquals("Doe", customer.getLastName());
        Assert.assertEquals(20, customer.getAge());
    }

    @Test
    public void whenLoadYAMLDocumentWithAssumedClass_thenLoadCorrectJavaObject() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("yaml/customer_with_type.yaml");
        Customer customer = yaml.load(inputStream);
        Assert.assertEquals("John", customer.getFirstName());
        Assert.assertEquals("Doe", customer.getLastName());
        Assert.assertEquals(20, customer.getAge());
    }

    @Test
    public void whenLoadYAML_thenLoadCorrectImplicitTypes() {
        Yaml yaml = new Yaml();
        Map<Object, Object> document = yaml.load("3.0: 2018-07-22");
        Assert.assertNotNull(document);
        Assert.assertEquals(1, document.size());
        Assert.assertTrue(document.containsKey(3.0));
        Assert.assertTrue(((document.get(3.0)) instanceof Date));
    }

    @Test
    public void whenLoadYAMLDocumentWithTopLevelClass_thenLoadCorrectJavaObjectWithNestedObjects() {
        Yaml yaml = new Yaml(new Constructor(Customer.class));
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("yaml/customer_with_contact_details_and_address.yaml");
        Customer customer = yaml.load(inputStream);
        Assert.assertNotNull(customer);
        Assert.assertEquals("John", customer.getFirstName());
        Assert.assertEquals("Doe", customer.getLastName());
        Assert.assertEquals(31, customer.getAge());
        Assert.assertNotNull(customer.getContactDetails());
        Assert.assertEquals(2, customer.getContactDetails().size());
        Assert.assertEquals("mobile", customer.getContactDetails().get(0).getType());
        Assert.assertEquals(123456789, customer.getContactDetails().get(0).getNumber());
        Assert.assertEquals("landline", customer.getContactDetails().get(1).getType());
        Assert.assertEquals(456786868, customer.getContactDetails().get(1).getNumber());
        Assert.assertNotNull(customer.getHomeAddress());
        Assert.assertEquals("Xyz, DEF Street", customer.getHomeAddress().getLine());
    }

    @Test
    public void whenLoadYAMLDocumentWithTypeDescription_thenLoadCorrectJavaObjectWithCorrectGenericType() {
        Constructor constructor = new Constructor(Customer.class);
        TypeDescription customTypeDescription = new TypeDescription(Customer.class);
        customTypeDescription.addPropertyParameters("contactDetails", Contact.class);
        constructor.addTypeDescription(customTypeDescription);
        Yaml yaml = new Yaml(constructor);
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("yaml/customer_with_contact_details.yaml");
        Customer customer = yaml.load(inputStream);
        Assert.assertNotNull(customer);
        Assert.assertEquals("John", customer.getFirstName());
        Assert.assertEquals("Doe", customer.getLastName());
        Assert.assertEquals(31, customer.getAge());
        Assert.assertNotNull(customer.getContactDetails());
        Assert.assertEquals(2, customer.getContactDetails().size());
        Assert.assertEquals("mobile", customer.getContactDetails().get(0).getType());
        Assert.assertEquals("landline", customer.getContactDetails().get(1).getType());
    }

    @Test
    public void whenLoadMultipleYAMLDocuments_thenLoadCorrectJavaObjects() {
        Yaml yaml = new Yaml(new Constructor(Customer.class));
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("yaml/customers.yaml");
        int count = 0;
        for (Object object : yaml.loadAll(inputStream)) {
            count++;
            Assert.assertTrue((object instanceof Customer));
        }
        Assert.assertEquals(2, count);
    }
}

