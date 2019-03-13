package com.vaadin.v7.data.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.junit.Assert;
import org.junit.Test;


public class NestedMethodPropertyTest {
    public static class Address implements Serializable {
        private String street;

        private int postalCodePrimitive;

        private Integer postalCodeObject;

        public Address(String street, int postalCode) {
            this.street = street;
            postalCodePrimitive = postalCode;
            postalCodeObject = postalCode;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getStreet() {
            return street;
        }

        public void setPostalCodePrimitive(int postalCodePrimitive) {
            this.postalCodePrimitive = postalCodePrimitive;
        }

        public int getPostalCodePrimitive() {
            return postalCodePrimitive;
        }

        public void setPostalCodeObject(Integer postalCodeObject) {
            this.postalCodeObject = postalCodeObject;
        }

        public Integer getPostalCodeObject() {
            return postalCodeObject;
        }

        // read-only boolean property
        public boolean isBoolean() {
            return true;
        }
    }

    public static class Person implements Serializable {
        private String name;

        private NestedMethodPropertyTest.Address address;

        private int age;

        public Person(String name, NestedMethodPropertyTest.Address address) {
            this.name = name;
            this.address = address;
        }

        public Person(String name, NestedMethodPropertyTest.Address address, int age) {
            this.name = name;
            this.address = address;
            this.age = age;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setAddress(NestedMethodPropertyTest.Address address) {
            this.address = address;
        }

        public NestedMethodPropertyTest.Address getAddress() {
            return address;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    public static class Team implements Serializable {
        private String name;

        private NestedMethodPropertyTest.Person manager;

        public Team(String name, NestedMethodPropertyTest.Person manager) {
            this.name = name;
            this.manager = manager;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setManager(NestedMethodPropertyTest.Person manager) {
            this.manager = manager;
        }

        public NestedMethodPropertyTest.Person getManager() {
            return manager;
        }
    }

    private NestedMethodPropertyTest.Address oldMill;

    private NestedMethodPropertyTest.Person joonas;

    private NestedMethodPropertyTest.Team vaadin;

    @Test
    public void testSingleLevelNestedSimpleProperty() {
        NestedMethodProperty<String> nameProperty = new NestedMethodProperty<String>(vaadin, "name");
        Assert.assertEquals(String.class, nameProperty.getType());
        Assert.assertEquals("Vaadin", nameProperty.getValue());
    }

    @Test
    public void testSingleLevelNestedObjectProperty() {
        NestedMethodProperty<NestedMethodPropertyTest.Person> managerProperty = new NestedMethodProperty<NestedMethodPropertyTest.Person>(vaadin, "manager");
        Assert.assertEquals(NestedMethodPropertyTest.Person.class, managerProperty.getType());
        Assert.assertEquals(joonas, managerProperty.getValue());
    }

    @Test
    public void testMultiLevelNestedProperty() {
        NestedMethodProperty<String> managerNameProperty = new NestedMethodProperty<String>(vaadin, "manager.name");
        NestedMethodProperty<NestedMethodPropertyTest.Address> addressProperty = new NestedMethodProperty<NestedMethodPropertyTest.Address>(vaadin, "manager.address");
        NestedMethodProperty<String> streetProperty = new NestedMethodProperty<String>(vaadin, "manager.address.street");
        NestedMethodProperty<Integer> postalCodePrimitiveProperty = new NestedMethodProperty<Integer>(vaadin, "manager.address.postalCodePrimitive");
        NestedMethodProperty<Integer> postalCodeObjectProperty = new NestedMethodProperty<Integer>(vaadin, "manager.address.postalCodeObject");
        NestedMethodProperty<Boolean> booleanProperty = new NestedMethodProperty<Boolean>(vaadin, "manager.address.boolean");
        Assert.assertEquals(String.class, managerNameProperty.getType());
        Assert.assertEquals("Joonas", managerNameProperty.getValue());
        Assert.assertEquals(NestedMethodPropertyTest.Address.class, addressProperty.getType());
        Assert.assertEquals(oldMill, addressProperty.getValue());
        Assert.assertEquals(String.class, streetProperty.getType());
        Assert.assertEquals("Ruukinkatu 2-4", streetProperty.getValue());
        Assert.assertEquals(Integer.class, postalCodePrimitiveProperty.getType());
        Assert.assertEquals(Integer.valueOf(20540), postalCodePrimitiveProperty.getValue());
        Assert.assertEquals(Integer.class, postalCodeObjectProperty.getType());
        Assert.assertEquals(Integer.valueOf(20540), postalCodeObjectProperty.getValue());
        Assert.assertEquals(Boolean.class, booleanProperty.getType());
        Assert.assertEquals(Boolean.TRUE, booleanProperty.getValue());
    }

    @Test
    public void testEmptyPropertyName() {
        try {
            new NestedMethodProperty<Object>(vaadin, "");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }
        try {
            new NestedMethodProperty<Object>(vaadin, " ");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }
    }

    @Test
    public void testInvalidPropertyName() {
        try {
            new NestedMethodProperty<Object>(vaadin, ".");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }
        try {
            new NestedMethodProperty<Object>(vaadin, ".manager");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }
        try {
            new NestedMethodProperty<Object>(vaadin, "manager.");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }
        try {
            new NestedMethodProperty<Object>(vaadin, "manager..name");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }
    }

    @Test
    public void testInvalidNestedPropertyName() {
        try {
            new NestedMethodProperty<Object>(vaadin, "member");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }
        try {
            new NestedMethodProperty<Object>(vaadin, "manager.pet");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }
        try {
            new NestedMethodProperty<Object>(vaadin, "manager.address.city");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // should get exception
        }
    }

    @Test
    public void testNullNestedProperty() {
        NestedMethodProperty<String> managerNameProperty = new NestedMethodProperty<String>(vaadin, "manager.name");
        NestedMethodProperty<String> streetProperty = new NestedMethodProperty<String>(vaadin, "manager.address.street");
        joonas.setAddress(null);
        Assert.assertNull(streetProperty.getValue());
        vaadin.setManager(null);
        Assert.assertNull(managerNameProperty.getValue());
        Assert.assertNull(streetProperty.getValue());
        vaadin.setManager(joonas);
        Assert.assertEquals("Joonas", managerNameProperty.getValue());
        Assert.assertNull(streetProperty.getValue());
    }

    @Test
    public void testMultiLevelNestedPropertySetValue() {
        NestedMethodProperty<String> managerNameProperty = new NestedMethodProperty<String>(vaadin, "manager.name");
        NestedMethodProperty<NestedMethodPropertyTest.Address> addressProperty = new NestedMethodProperty<NestedMethodPropertyTest.Address>(vaadin, "manager.address");
        NestedMethodProperty<String> streetProperty = new NestedMethodProperty<String>(vaadin, "manager.address.street");
        NestedMethodProperty<Integer> postalCodePrimitiveProperty = new NestedMethodProperty<Integer>(vaadin, "manager.address.postalCodePrimitive");
        NestedMethodProperty<Integer> postalCodeObjectProperty = new NestedMethodProperty<Integer>(vaadin, "manager.address.postalCodeObject");
        managerNameProperty.setValue("Joonas L");
        Assert.assertEquals("Joonas L", joonas.getName());
        streetProperty.setValue("Ruukinkatu");
        Assert.assertEquals("Ruukinkatu", oldMill.getStreet());
        postalCodePrimitiveProperty.setValue(0);
        postalCodeObjectProperty.setValue(1);
        Assert.assertEquals(0, oldMill.getPostalCodePrimitive());
        Assert.assertEquals(Integer.valueOf(1), oldMill.getPostalCodeObject());
        postalCodeObjectProperty.setValue(null);
        Assert.assertNull(oldMill.getPostalCodeObject());
        NestedMethodPropertyTest.Address address2 = new NestedMethodPropertyTest.Address("Other street", 12345);
        addressProperty.setValue(address2);
        Assert.assertEquals("Other street", streetProperty.getValue());
        NestedMethodPropertyTest.Address address3 = null;
        addressProperty.setValue(address3);
        Assert.assertEquals(null, addressProperty.getValue());
        streetProperty.setValue("Ruukinkatu");
        Assert.assertEquals(null, streetProperty.getValue());
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        NestedMethodProperty<String> streetProperty = new NestedMethodProperty<String>(vaadin, "manager.address.street");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(streetProperty);
        @SuppressWarnings("unchecked")
        NestedMethodProperty<String> property2 = ((NestedMethodProperty<String>) (new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())).readObject()));
        Assert.assertEquals("Ruukinkatu 2-4", property2.getValue());
    }

    @Test
    public void testSerializationWithIntermediateNull() throws IOException, ClassNotFoundException {
        vaadin.setManager(null);
        NestedMethodProperty<String> streetProperty = new NestedMethodProperty<String>(vaadin, "manager.address.street");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(streetProperty);
        @SuppressWarnings("unchecked")
        NestedMethodProperty<String> property2 = ((NestedMethodProperty<String>) (new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())).readObject()));
        Assert.assertNull(property2.getValue());
    }

    @Test
    public void testIsReadOnly() {
        NestedMethodProperty<String> streetProperty = new NestedMethodProperty<String>(vaadin, "manager.address.street");
        NestedMethodProperty<Boolean> booleanProperty = new NestedMethodProperty<Boolean>(vaadin, "manager.address.boolean");
        Assert.assertFalse(streetProperty.isReadOnly());
        Assert.assertTrue(booleanProperty.isReadOnly());
    }

    @Test
    public void testChangeInstance() {
        NestedMethodProperty<String> streetProperty = new NestedMethodProperty<String>(vaadin, "manager.address.street");
        NestedMethodPropertyTest.Address somewhere = new NestedMethodPropertyTest.Address("The street", 1234);
        NestedMethodPropertyTest.Person someone = new NestedMethodPropertyTest.Person("Someone", somewhere);
        NestedMethodPropertyTest.Team someteam = new NestedMethodPropertyTest.Team("The team", someone);
        streetProperty.setInstance(someteam);
        Assert.assertEquals("The street", streetProperty.getValue());
        Assert.assertEquals("Ruukinkatu 2-4", vaadin.getManager().getAddress().getStreet());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangeInstanceToIncompatible() {
        NestedMethodProperty<String> streetProperty = new NestedMethodProperty<String>(vaadin, "manager.address.street");
        streetProperty.setInstance("bar");
    }
}

