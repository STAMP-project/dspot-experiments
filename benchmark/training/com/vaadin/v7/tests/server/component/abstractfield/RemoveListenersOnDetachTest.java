package com.vaadin.v7.tests.server.component.abstractfield;


import Property.ReadOnlyStatusChangeEvent;
import Property.ValueChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;
import com.vaadin.ui.UI;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import com.vaadin.v7.ui.AbstractField;
import org.junit.Assert;
import org.junit.Test;


public class RemoveListenersOnDetachTest {
    int numValueChanges = 0;

    int numReadOnlyChanges = 0;

    AbstractField field = new AbstractField() {
        private final VaadinSession application = new AlwaysLockedVaadinSession(null);

        private UI uI = new UI() {
            @Override
            protected void init(VaadinRequest request) {
            }

            @Override
            public VaadinSession getSession() {
                return application;
            }
        };

        @Override
        public Class<?> getType() {
            return String.class;
        }

        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            super.valueChange(event);
            (numValueChanges)++;
        }

        @Override
        public void readOnlyStatusChange(Property.ReadOnlyStatusChangeEvent event) {
            super.readOnlyStatusChange(event);
            (numReadOnlyChanges)++;
        }

        @Override
        public UI getUI() {
            return uI;
        }

        @Override
        public VaadinSession getSession() {
            return application;
        }
    };

    Property<String> property = new com.vaadin.v7.data.util.AbstractProperty<String>() {
        @Override
        public String getValue() {
            return null;
        }

        @Override
        public void setValue(String newValue) throws ConversionException, ReadOnlyException {
            fireValueChange();
        }

        @Override
        public Class<String> getType() {
            return String.class;
        }
    };

    @Test
    public void testAttachDetach() {
        field.setPropertyDataSource(property);
        property.setValue(null);
        property.setReadOnly(true);
        Assert.assertEquals(1, numValueChanges);
        Assert.assertEquals(1, numReadOnlyChanges);
        field.attach();
        property.setValue(null);
        property.setReadOnly(false);
        Assert.assertEquals(2, numValueChanges);
        Assert.assertEquals(2, numReadOnlyChanges);
        field.detach();
        property.setValue(null);
        property.setReadOnly(true);
        Assert.assertEquals(2, numValueChanges);
        Assert.assertEquals(2, numReadOnlyChanges);
    }
}

