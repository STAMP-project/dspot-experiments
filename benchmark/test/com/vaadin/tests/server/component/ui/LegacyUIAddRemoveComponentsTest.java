package com.vaadin.tests.server.component.ui;


import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import org.junit.Assert;
import org.junit.Test;


public class LegacyUIAddRemoveComponentsTest {
    private static class TestUI extends LegacyWindow {
        @Override
        protected void init(VaadinRequest request) {
        }
    }

    @Test
    public void addComponent() {
        LegacyUIAddRemoveComponentsTest.TestUI ui = new LegacyUIAddRemoveComponentsTest.TestUI();
        Component c = new Label("abc");
        ui.addComponent(c);
        Assert.assertSame(c.getParent(), iterator().next());
        Assert.assertSame(c, iterator().next());
        Assert.assertEquals(1, getComponentCount());
        Assert.assertEquals(1, getComponentCount());
    }

    @Test
    public void removeComponent() {
        LegacyUIAddRemoveComponentsTest.TestUI ui = new LegacyUIAddRemoveComponentsTest.TestUI();
        Component c = new Label("abc");
        ui.addComponent(c);
        ui.removeComponent(c);
        Assert.assertEquals(getContent(), iterator().next());
        Assert.assertFalse(iterator().hasNext());
        Assert.assertEquals(1, getComponentCount());
        Assert.assertEquals(0, getComponentCount());
    }

    @Test
    public void replaceComponent() {
        LegacyUIAddRemoveComponentsTest.TestUI ui = new LegacyUIAddRemoveComponentsTest.TestUI();
        Component c = new Label("abc");
        Component d = new Label("def");
        ui.addComponent(c);
        ui.replaceComponent(c, d);
        Assert.assertSame(d.getParent(), iterator().next());
        Assert.assertSame(d, iterator().next());
        Assert.assertEquals(1, getComponentCount());
        Assert.assertEquals(1, getComponentCount());
    }
}

