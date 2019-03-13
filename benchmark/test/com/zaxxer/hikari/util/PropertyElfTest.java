package com.zaxxer.hikari.util;


import com.zaxxer.hikari.mocks.TestObject;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;


public class PropertyElfTest {
    @Test
    public void setTargetFromProperties() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("string", "aString");
        properties.setProperty("testObject", "com.zaxxer.hikari.mocks.TestObject");
        TestObject testObject = new TestObject();
        PropertyElf.setTargetFromProperties(testObject, properties);
        Assert.assertEquals("aString", testObject.getString());
        Assert.assertEquals(TestObject.class, testObject.getTestObject().getClass());
        Assert.assertNotSame(testObject, testObject.getTestObject());
    }

    @Test
    public void setTargetFromPropertiesNotAClass() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("string", "aString");
        properties.setProperty("testObject", "it is not a class");
        TestObject testObject = new TestObject();
        try {
            PropertyElf.setTargetFromProperties(testObject, properties);
            Assert.fail("Could never come here");
        } catch (RuntimeException e) {
            Assert.assertEquals("argument type mismatch", e.getCause().getMessage());
        }
    }
}

