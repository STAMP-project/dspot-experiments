package com.vaadin.tests.server.componentcontainer;


import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import org.junit.Assert;
import org.junit.Test;


public class HorizontalLayoutTest extends AbstractIndexedLayoutTestBase {
    @Test
    public void addAndExpand_basicCase() {
        Button b1 = new Button();
        Button b2 = new Button();
        Button b3 = new Button();
        HorizontalLayout layout = getLayout();
        layout.addComponents(b3, b2);
        layout.addComponentsAndExpand(b1, b2);
        Assert.assertEquals(3, layout.getComponentCount());
        Assert.assertSame(b3, layout.getComponent(0));
        Assert.assertSame(b1, layout.getComponent(1));
        Assert.assertSame(b2, layout.getComponent(2));
        Assert.assertEquals(0, layout.getExpandRatio(b3), 0);
        Assert.assertEquals(1, layout.getExpandRatio(b1), 0);
        Assert.assertEquals(1, layout.getExpandRatio(b2), 0);
        Assert.assertEquals((-1), b3.getWidth(), 0);
        Assert.assertEquals(100, b1.getWidth(), 0);
        Assert.assertEquals(100, b2.getWidth(), 0);
    }

    @Test
    public void addAndExpand_undefinedHeightUpdated() {
        HorizontalLayout layout = getLayout();
        Assert.assertEquals((-1), layout.getWidth(), 0);
        layout.addComponentsAndExpand();
        Assert.assertEquals(100, layout.getWidth(), 0);
    }

    @Test
    public void addAndExpand_definedHeightPreserved() {
        HorizontalLayout layout = getLayout();
        layout.setWidth("150px");
        layout.addComponentsAndExpand();
        Assert.assertEquals(150, layout.getWidth(), 0);
    }
}

