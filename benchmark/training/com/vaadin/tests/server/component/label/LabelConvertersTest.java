package com.vaadin.tests.server.component.label;


import com.vaadin.server.VaadinSession;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.MethodProperty;
import com.vaadin.v7.ui.Label;
import org.junit.Assert;
import org.junit.Test;


public class LabelConvertersTest {
    @Test
    public void testLabelSetDataSourceLaterOn() {
        Person p = Person.createTestPerson1();
        Label l = new Label("My label");
        Assert.assertEquals("My label", l.getValue());
        Assert.assertNull(l.getConverter());
        l.setPropertyDataSource(new MethodProperty<String>(p, "firstName"));
        Assert.assertEquals(p.getFirstName(), l.getValue());
        p.setFirstName("123");
        Assert.assertEquals("123", l.getValue());
    }

    @Test
    public void testIntegerDataSource() {
        VaadinSession.setCurrent(new AlwaysLockedVaadinSession(null));
        Label l = new Label("Foo");
        Property ds = new MethodProperty<Integer>(Person.createTestPerson1(), "age");
        l.setPropertyDataSource(ds);
        Assert.assertEquals(String.valueOf(Person.createTestPerson1().getAge()), l.getValue());
    }

    @Test
    public void testSetValueWithDataSource() {
        try {
            MethodProperty<String> property = new MethodProperty<String>(Person.createTestPerson1(), "firstName");
            Label l = new Label(property);
            l.setValue("Foo");
            Assert.fail("setValue should throw an exception when a data source is set");
        } catch (Exception e) {
        }
    }

    @Test
    public void testLabelWithoutDataSource() {
        Label l = new Label("My label");
        Assert.assertEquals("My label", l.getValue());
        Assert.assertNull(l.getConverter());
        Assert.assertNull(l.getPropertyDataSource());
        l.setValue("New value");
        Assert.assertEquals("New value", l.getValue());
        Assert.assertNull(l.getConverter());
        Assert.assertNull(l.getPropertyDataSource());
    }
}

