package com.netflix.config;


import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * User: michaels@netflix.com
 * Date: 11/30/15
 * Time: 2:35 PM
 */
public class DynamicPropertyCachedPrimitiveTest {
    @Test
    public void testUpdatingValueAlsoUpdatesCachedPrimitive_Boolean() {
        // Create property with initial value.
        String propName = "testprop1";
        ConfigurationManager.getConfigInstance().setProperty(propName, "true");
        DynamicBooleanProperty prop = DynamicPropertyFactory.getInstance().getBooleanProperty(propName, false);
        Assert.assertTrue(prop.get());
        Assert.assertTrue(prop.getValue().booleanValue());
        // Change the value of the property.
        ConfigurationManager.getConfigInstance().setProperty(propName, "false");
        Assert.assertFalse(prop.get());
        Assert.assertFalse(prop.getValue().booleanValue());
        // And change back again.
        ConfigurationManager.getConfigInstance().setProperty(propName, "true");
        Assert.assertTrue(prop.get());
        Assert.assertTrue(prop.getValue().booleanValue());
        // Remove the prop value, should change to default value.
        ConfigurationManager.getConfigInstance().clearProperty(propName);
        Assert.assertFalse(prop.get());
        Assert.assertFalse(prop.getValue().booleanValue());
    }

    @Test
    public void testUpdatingValueAlsoUpdatesCachedPrimitive_Int() {
        // Create property with initial value.
        String propName = "testprop2";
        ConfigurationManager.getConfigInstance().setProperty(propName, "1");
        DynamicIntProperty prop = DynamicPropertyFactory.getInstance().getIntProperty(propName, 0);
        TestCase.assertEquals(1, prop.get());
        TestCase.assertEquals(1, prop.getValue().intValue());
        // Change the value of the property.
        ConfigurationManager.getConfigInstance().setProperty(propName, "2");
        TestCase.assertEquals(2, prop.get());
        TestCase.assertEquals(2, prop.getValue().intValue());
        // Remove the prop value, should change to default value.
        ConfigurationManager.getConfigInstance().clearProperty(propName);
        TestCase.assertEquals(0, prop.get());
        TestCase.assertEquals(0, prop.getValue().intValue());
    }

    @Test
    public void testUpdatingValueAlsoUpdatesCachedPrimitive_Long() {
        // Create property with initial value.
        String propName = "testprop3";
        ConfigurationManager.getConfigInstance().setProperty(propName, "1");
        DynamicLongProperty prop = DynamicPropertyFactory.getInstance().getLongProperty(propName, 0);
        TestCase.assertEquals(1, prop.get());
        TestCase.assertEquals(1, prop.getValue().longValue());
        // Change the value of the property.
        ConfigurationManager.getConfigInstance().setProperty(propName, "2");
        TestCase.assertEquals(2, prop.get());
        TestCase.assertEquals(2, prop.getValue().longValue());
        // Remove the prop value, should change to default value.
        ConfigurationManager.getConfigInstance().clearProperty(propName);
        TestCase.assertEquals(0, prop.get());
        TestCase.assertEquals(0, prop.getValue().longValue());
    }

    @Test
    public void testUpdatingValueAlsoUpdatesCachedPrimitive_Double() {
        // Create property with initial value.
        String propName = "testprop3";
        ConfigurationManager.getConfigInstance().setProperty(propName, "1.1");
        DynamicDoubleProperty prop = DynamicPropertyFactory.getInstance().getDoubleProperty(propName, 0.5);
        TestCase.assertEquals(1.1, prop.get());
        TestCase.assertEquals(1.1, prop.getValue().doubleValue());
        // Change the value of the property.
        ConfigurationManager.getConfigInstance().setProperty(propName, "2.2");
        TestCase.assertEquals(2.2, prop.get());
        TestCase.assertEquals(2.2, prop.getValue().doubleValue());
        // Remove the prop value, should change to default value.
        ConfigurationManager.getConfigInstance().clearProperty(propName);
        TestCase.assertEquals(0.5, prop.get());
        TestCase.assertEquals(0.5, prop.getValue().doubleValue());
    }

    @Test
    public void testUpdatingValueAlsoUpdatesCachedPrimitive_Float() {
        // Create property with initial value.
        String propName = "testprop4";
        ConfigurationManager.getConfigInstance().setProperty(propName, "1.1");
        DynamicFloatProperty prop = DynamicPropertyFactory.getInstance().getFloatProperty(propName, 0.5F);
        TestCase.assertEquals(1.1F, prop.get());
        TestCase.assertEquals(1.1F, prop.getValue().floatValue());
        // Change the value of the property.
        ConfigurationManager.getConfigInstance().setProperty(propName, "2.2");
        TestCase.assertEquals(2.2F, prop.get());
        TestCase.assertEquals(2.2F, prop.getValue().floatValue());
        // Remove the prop value, should change to default value.
        ConfigurationManager.getConfigInstance().clearProperty(propName);
        TestCase.assertEquals(0.5F, prop.get());
        TestCase.assertEquals(0.5F, prop.getValue().floatValue());
    }
}

