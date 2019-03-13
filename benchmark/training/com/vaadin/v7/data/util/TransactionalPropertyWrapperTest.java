package com.vaadin.v7.data.util;


import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.ui.TextField;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test verifying that TransactionalPropertyWrapper removes it's listener from
 * wrapped Property
 *
 * @since 7.1.15
 * @author Vaadin Ltd
 */
public class TransactionalPropertyWrapperTest {
    @SuppressWarnings("serial")
    public class TestingProperty<T extends Object> extends ObjectProperty<Object> {
        private List<ValueChangeListener> listeners = new ArrayList<ValueChangeListener>();

        public TestingProperty(Object value) {
            super(value);
        }

        @Override
        public void addValueChangeListener(ValueChangeListener listener) {
            super.addValueChangeListener(listener);
            listeners.add(listener);
        }

        @Override
        public void removeValueChangeListener(ValueChangeListener listener) {
            super.removeValueChangeListener(listener);
            if (listeners.contains(listener)) {
                listeners.remove(listener);
            }
        }

        public boolean hasListeners() {
            return !(listeners.isEmpty());
        }
    }

    private final TextField nameField = new TextField("Name");

    private final TextField ageField = new TextField("Age");

    private final TextField unboundField = new TextField("No FieldGroup");

    private final TransactionalPropertyWrapperTest.TestingProperty<String> unboundProp = new TransactionalPropertyWrapperTest.TestingProperty<String>("Hello World");

    private final PropertysetItem item = new PropertysetItem();

    @Test
    public void fieldGroupBindAndUnbind() {
        item.addItemProperty("name", new TransactionalPropertyWrapperTest.TestingProperty<String>("Just some text"));
        item.addItemProperty("age", new TransactionalPropertyWrapperTest.TestingProperty<String>("42"));
        final FieldGroup binder = new FieldGroup(item);
        binder.setBuffered(false);
        for (int i = 0; i < 2; ++i) {
            binder.bind(nameField, "name");
            binder.bind(ageField, "age");
            unboundField.setPropertyDataSource(unboundProp);
            Assert.assertTrue("No listeners in Properties", fieldsHaveListeners(true));
            binder.unbind(nameField);
            binder.unbind(ageField);
            unboundField.setPropertyDataSource(null);
            Assert.assertTrue("Listeners in Properties after unbinding", fieldsHaveListeners(false));
        }
    }
}

