package com.vaadin.tests.server.componentcontainer;


import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractIndexedLayoutTestBase {
    private Layout layout;

    @Test
    public void testAddRemoveComponent() {
        Label c1 = new Label();
        Label c2 = new Label();
        layout.addComponent(c1);
        Assert.assertEquals(c1, getComponent(0));
        Assert.assertEquals(1, getComponentCount());
        layout.addComponent(c2);
        Assert.assertEquals(c1, getComponent(0));
        Assert.assertEquals(c2, getComponent(1));
        Assert.assertEquals(2, getComponentCount());
        layout.removeComponent(c1);
        Assert.assertEquals(c2, getComponent(0));
        Assert.assertEquals(1, getComponentCount());
        layout.removeComponent(c2);
        Assert.assertEquals(0, getComponentCount());
    }

    @Test
    public void testGetComponentIndex() {
        Label c1 = new Label();
        Label c2 = new Label();
        layout.addComponent(c1);
        Assert.assertEquals(0, getComponentIndex(c1));
        layout.addComponent(c2);
        Assert.assertEquals(0, getComponentIndex(c1));
        Assert.assertEquals(1, getComponentIndex(c2));
        layout.removeComponent(c1);
        Assert.assertEquals(0, getComponentIndex(c2));
        layout.removeComponent(c2);
        Assert.assertEquals((-1), getComponentIndex(c2));
        Assert.assertEquals((-1), getComponentIndex(c1));
    }

    @Test
    public void testGetComponent() {
        Label c1 = new Label();
        Label c2 = new Label();
        layout.addComponent(c1);
        Assert.assertEquals(c1, getComponent(0));
        layout.addComponent(c2);
        Assert.assertEquals(c1, getComponent(0));
        Assert.assertEquals(c2, getComponent(1));
        layout.removeComponent(c1);
        Assert.assertEquals(c2, getComponent(0));
        layout.removeComponent(c2);
        try {
            getComponent(0);
            Assert.fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }
}

