package com.vaadin.v7.data.util;


import com.vaadin.v7.data.Property;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.Assert;
import org.junit.Test;


public class PropertyDescriptorTest {
    @Test
    public void testMethodPropertyDescriptorSerialization() throws Exception {
        PropertyDescriptor[] pds = Introspector.getBeanInfo(NestedMethodPropertyTest.Person.class).getPropertyDescriptors();
        MethodPropertyDescriptor<NestedMethodPropertyTest.Person> descriptor = null;
        for (PropertyDescriptor pd : pds) {
            if ("name".equals(pd.getName())) {
                descriptor = new MethodPropertyDescriptor<NestedMethodPropertyTest.Person>(pd.getName(), String.class, pd.getReadMethod(), pd.getWriteMethod());
                break;
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(descriptor);
        @SuppressWarnings("unchecked")
        VaadinPropertyDescriptor<NestedMethodPropertyTest.Person> descriptor2 = ((VaadinPropertyDescriptor<NestedMethodPropertyTest.Person>) (new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())).readObject()));
        Property<?> property = descriptor2.createProperty(new NestedMethodPropertyTest.Person("John", null));
        Assert.assertEquals("John", property.getValue());
    }

    @Test
    public void testSimpleNestedPropertyDescriptorSerialization() throws Exception {
        NestedPropertyDescriptor<NestedMethodPropertyTest.Person> pd = new NestedPropertyDescriptor<NestedMethodPropertyTest.Person>("name", NestedMethodPropertyTest.Person.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(pd);
        @SuppressWarnings("unchecked")
        VaadinPropertyDescriptor<NestedMethodPropertyTest.Person> pd2 = ((VaadinPropertyDescriptor<NestedMethodPropertyTest.Person>) (new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())).readObject()));
        Property<?> property = pd2.createProperty(new NestedMethodPropertyTest.Person("John", null));
        Assert.assertEquals("John", property.getValue());
    }

    @Test
    public void testNestedPropertyDescriptorSerialization() throws Exception {
        NestedPropertyDescriptor<NestedMethodPropertyTest.Person> pd = new NestedPropertyDescriptor<NestedMethodPropertyTest.Person>("address.street", NestedMethodPropertyTest.Person.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(pd);
        @SuppressWarnings("unchecked")
        VaadinPropertyDescriptor<NestedMethodPropertyTest.Person> pd2 = ((VaadinPropertyDescriptor<NestedMethodPropertyTest.Person>) (new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())).readObject()));
        Property<?> property = pd2.createProperty(new NestedMethodPropertyTest.Person("John", null));
        Assert.assertNull(property.getValue());
    }

    @Test
    public void testMethodPropertyDescriptorWithPrimitivePropertyType() throws Exception {
        MethodPropertyDescriptor<NestedMethodPropertyTest.Person> pd = new MethodPropertyDescriptor<NestedMethodPropertyTest.Person>("age", int.class, NestedMethodPropertyTest.Person.class.getMethod("getAge"), NestedMethodPropertyTest.Person.class.getMethod("setAge", int.class));
        Assert.assertEquals(Integer.class, pd.getPropertyType());
    }
}

