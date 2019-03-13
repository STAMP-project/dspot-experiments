package com.vaadin.tests.server;


import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.v7.data.Property.ReadOnlyStatusChangeEvent;
import com.vaadin.v7.data.Property.ReadOnlyStatusChangeListener;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.data.util.AbstractProperty;
import org.junit.Test;


public class AbstractPropertyListenersTest extends AbstractListenerMethodsTestBase {
    @Test
    public void testValueChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(AbstractProperty.class, ValueChangeEvent.class, ValueChangeListener.class, new com.vaadin.v7.data.util.ObjectProperty<String>(""));
    }

    @Test
    public void testReadOnlyStatusChangeListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(AbstractProperty.class, ReadOnlyStatusChangeEvent.class, ReadOnlyStatusChangeListener.class, new com.vaadin.v7.data.util.ObjectProperty<String>(""));
    }
}

