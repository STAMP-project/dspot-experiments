package com.vaadin.v7.tests.server.component.abstractfield;


import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.Slider;
import org.junit.Assert;
import org.junit.Test;


public class FieldDefaultValuesTest {
    @Test
    public void testFieldsHaveDefaultValueAfterClear() throws Exception {
        for (Field<?> field : FieldDefaultValuesTest.createFields()) {
            Object originalValue = field.getValue();
            field.clear();
            Object clearedValue = field.getValue();
            Assert.assertEquals(("Expected to get default value after clearing " + (field.getClass().getName())), originalValue, clearedValue);
        }
    }

    @Test
    public void testFieldsAreEmptyAfterClear() throws Exception {
        int count = 0;
        for (Field<?> field : FieldDefaultValuesTest.createFields()) {
            count++;
            field.clear();
            if (field instanceof Slider) {
                Assert.assertFalse("Slider should not be empty even after being cleared", field.isEmpty());
            } else {
                Assert.assertTrue(((field.getClass().getName()) + " should be empty after being cleared"), field.isEmpty());
            }
        }
        Assert.assertTrue((count > 0));
    }
}

