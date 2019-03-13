package com.vaadin.ui;


import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


public class CustomFieldTest {
    public static class TestCustomField extends CustomField<String> {
        private String value = "initial";

        private Button button;

        @Override
        public String getValue() {
            return value;
        }

        @Override
        protected Component initContent() {
            button = new Button("Content");
            return button;
        }

        @Override
        protected void doSetValue(String value) {
            this.value = value;
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void iterator() {
        CustomFieldTest.TestCustomField field = new CustomFieldTest.TestCustomField();
        // Needs to trigger initContent somehow as
        // iterator() can't do it even though it should...
        getContent();
        Iterator<Component> iterator = field.iterator();
        Assert.assertNotNull(iterator);
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(field.button, iterator.next());
        Assert.assertFalse(iterator.hasNext());
        iterator.next();
    }
}

