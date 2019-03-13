package com.vaadin.v7.tests.server.component.abstractfield;


import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.Property.ReadOnlyStatusChangeEvent;
import com.vaadin.v7.data.Property.ReadOnlyStatusChangeListener;
import com.vaadin.v7.ui.TextField;
import org.junit.Assert;
import org.junit.Test;


public class AbstractFieldReadOnlyTest {
    Person paulaBean = new Person("Paula", "Brilliant", "paula@brilliant.com", 34, Sex.FEMALE, new com.vaadin.tests.data.bean.Address("Paula street 1", 12345, "P-town", Country.FINLAND));

    @Test
    public void testReadOnlyProperty() {
        TextField tf = new TextField();
        tf.setPropertyDataSource(new com.vaadin.v7.data.util.MethodProperty<String>(paulaBean, "firstName"));
        Assert.assertFalse(tf.isReadOnly());
        tf.getPropertyDataSource().setReadOnly(true);
        Assert.assertTrue(tf.isReadOnly());
    }

    @Test
    public void testReadOnlyEventFromProperty() {
        final Label valueStore = new Label("");
        TextField tf = new TextField();
        tf.addReadOnlyStatusChangeListener(new ReadOnlyStatusChangeListener() {
            @Override
            public void readOnlyStatusChange(ReadOnlyStatusChangeEvent event) {
                valueStore.setValue("event received!");
            }
        });
        tf.setPropertyDataSource(new com.vaadin.v7.data.util.MethodProperty<String>(paulaBean, "firstName"));
        Assert.assertTrue(valueStore.getValue().isEmpty());
        tf.getPropertyDataSource().setReadOnly(true);
        Assert.assertFalse(valueStore.getValue().isEmpty());
    }
}

