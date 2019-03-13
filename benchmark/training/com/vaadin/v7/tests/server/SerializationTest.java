package com.vaadin.v7.tests.server;


import com.vaadin.server.VaadinSession;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.data.validator.RegexpValidator;
import com.vaadin.v7.ui.Form;
import java.io.Serializable;
import org.junit.Assert;
import org.junit.Test;


public class SerializationTest {
    @Test
    public void testValidators() throws Exception {
        RegexpValidator validator = new RegexpValidator(".*", "Error");
        validator.validate("aaa");
        RegexpValidator validator2 = SerializationTest.serializeAndDeserialize(validator);
        validator2.validate("aaa");
    }

    @Test
    public void testForm() throws Exception {
        Form f = new Form();
        String propertyId = "My property";
        f.addItemProperty(propertyId, new com.vaadin.v7.data.util.MethodProperty<Object>(new SerializationTest.Data(), "dummyGetterAndSetter"));
        f.replaceWithSelect(propertyId, new Object[]{ "a", "b", null }, new String[]{ "Item a", "ITem b", "Null item" });
        SerializationTest.serializeAndDeserialize(f);
    }

    @Test
    public void testIndedexContainerItemIds() throws Exception {
        IndexedContainer ic = new IndexedContainer();
        ic.addContainerProperty("prop1", String.class, null);
        Object id = ic.addItem();
        ic.getItem(id).getItemProperty("prop1").setValue("1");
        Item item2 = ic.addItem("item2");
        item2.getItemProperty("prop1").setValue("2");
        SerializationTest.serializeAndDeserialize(ic);
    }

    @Test
    public void testMethodPropertyGetter() throws Exception {
        com.vaadin.v7.data.util.MethodProperty<?> mp = new com.vaadin.v7.data.util.MethodProperty<Object>(new SerializationTest.Data(), "dummyGetter");
        SerializationTest.serializeAndDeserialize(mp);
    }

    @Test
    public void testMethodPropertyGetterAndSetter() throws Exception {
        com.vaadin.v7.data.util.MethodProperty<?> mp = new com.vaadin.v7.data.util.MethodProperty<Object>(new SerializationTest.Data(), "dummyGetterAndSetter");
        SerializationTest.serializeAndDeserialize(mp);
    }

    @Test
    public void testMethodPropertyInt() throws Exception {
        com.vaadin.v7.data.util.MethodProperty<?> mp = new com.vaadin.v7.data.util.MethodProperty<Object>(new SerializationTest.Data(), "dummyInt");
        SerializationTest.serializeAndDeserialize(mp);
    }

    @Test
    public void testVaadinSession() throws Exception {
        VaadinSession session = new VaadinSession(null);
        session = SerializationTest.serializeAndDeserialize(session);
        Assert.assertNotNull("Pending access queue was not recreated after deserialization", session.getPendingAccessQueue());
    }

    public static class Data implements Serializable {
        private String dummyGetter;

        private String dummyGetterAndSetter;

        private int dummyInt;

        public String getDummyGetterAndSetter() {
            return dummyGetterAndSetter;
        }

        public void setDummyGetterAndSetter(String dummyGetterAndSetter) {
            this.dummyGetterAndSetter = dummyGetterAndSetter;
        }

        public int getDummyInt() {
            return dummyInt;
        }

        public void setDummyInt(int dummyInt) {
            this.dummyInt = dummyInt;
        }

        public String getDummyGetter() {
            return dummyGetter;
        }
    }
}

