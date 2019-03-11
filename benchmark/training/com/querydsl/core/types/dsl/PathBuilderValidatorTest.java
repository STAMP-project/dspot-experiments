package com.querydsl.core.types.dsl;


import PathBuilderValidator.DEFAULT;
import PathBuilderValidator.FIELDS;
import PathBuilderValidator.PROPERTIES;
import java.util.Collection;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class PathBuilderValidatorTest {
    public static class Customer {
        String name;

        Collection<Integer> collection;

        Map<String, Integer> map;
    }

    public static class ExtendedCustomer extends PathBuilderValidatorTest.Customer {}

    public static class Project {
        public String getName() {
            return "";
        }

        public Collection<Integer> getCollection() {
            return null;
        }

        public Map<String, Integer> getMap() {
            return null;
        }
    }

    public static class ExtendedProject extends PathBuilderValidatorTest.Project {
        public boolean isStarted() {
            return true;
        }
    }

    @Test
    public void defaults() {
        Assert.assertEquals(String.class, DEFAULT.validate(PathBuilderValidatorTest.Customer.class, "name", String.class));
        Assert.assertEquals(String.class, DEFAULT.validate(PathBuilderValidatorTest.ExtendedCustomer.class, "name", String.class));
        Assert.assertEquals(String.class, DEFAULT.validate(PathBuilderValidatorTest.Project.class, "name", String.class));
        Assert.assertEquals(String.class, DEFAULT.validate(PathBuilderValidatorTest.ExtendedProject.class, "name", String.class));
    }

    @Test
    public void fields() {
        Assert.assertEquals(String.class, FIELDS.validate(PathBuilderValidatorTest.Customer.class, "name", String.class));
        Assert.assertEquals(String.class, FIELDS.validate(PathBuilderValidatorTest.ExtendedCustomer.class, "name", String.class));
        Assert.assertEquals(Integer.class, FIELDS.validate(PathBuilderValidatorTest.Customer.class, "collection", Collection.class));
        Assert.assertEquals(Integer.class, FIELDS.validate(PathBuilderValidatorTest.Customer.class, "map", Map.class));
        Assert.assertNull(FIELDS.validate(PathBuilderValidatorTest.Project.class, "name", String.class));
        Assert.assertNull(FIELDS.validate(PathBuilderValidatorTest.ExtendedProject.class, "name", String.class));
    }

    @Test
    public void properties() {
        Assert.assertNull(PROPERTIES.validate(PathBuilderValidatorTest.Customer.class, "name", String.class));
        Assert.assertNull(PROPERTIES.validate(PathBuilderValidatorTest.ExtendedCustomer.class, "name", String.class));
        Assert.assertEquals(String.class, PROPERTIES.validate(PathBuilderValidatorTest.Project.class, "name", String.class));
        Assert.assertEquals(String.class, PROPERTIES.validate(PathBuilderValidatorTest.ExtendedProject.class, "name", String.class));
        Assert.assertEquals(Boolean.class, PROPERTIES.validate(PathBuilderValidatorTest.ExtendedProject.class, "started", Boolean.class));
        Assert.assertEquals(Integer.class, PROPERTIES.validate(PathBuilderValidatorTest.Project.class, "collection", Collection.class));
        Assert.assertEquals(Integer.class, PROPERTIES.validate(PathBuilderValidatorTest.Project.class, "map", Map.class));
    }
}

