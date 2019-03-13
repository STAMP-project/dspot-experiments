package com.vaadin.v7.ui;


import org.junit.Assert;
import org.junit.Test;


public class AbstractLegacyComponentTest {
    AbstractLegacyComponent component = new AbstractLegacyComponent() {};

    @Test
    public void testImmediate() {
        Assert.assertTrue("Component should be immediate by default", component.isImmediate());
        component.setImmediate(false);
        Assert.assertFalse("Explicitly non-immediate component should not be immediate", component.isImmediate());
    }
}

