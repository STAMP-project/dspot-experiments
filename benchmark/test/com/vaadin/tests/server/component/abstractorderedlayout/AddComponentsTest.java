package com.vaadin.tests.server.component.abstractorderedlayout;


import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.junit.Assert;
import org.junit.Test;


public class AddComponentsTest {
    Component[] children = new Component[]{ new Label("A"), new Label("B"), new Label("C"), new Label("D") };

    @Test
    public void moveComponentsBetweenLayouts() {
        AbstractOrderedLayout layout1 = new HorizontalLayout();
        AbstractOrderedLayout layout2 = new VerticalLayout();
        layout1.addComponent(children[0]);
        layout1.addComponent(children[1]);
        layout2.addComponent(children[2]);
        layout2.addComponent(children[3]);
        layout2.addComponent(children[1], 1);
        assertOrder(layout1, new int[]{ 0 });
        assertOrder(layout2, new int[]{ 2, 1, 3 });
        layout1.addComponent(children[3], 0);
        assertOrder(layout1, new int[]{ 3, 0 });
        assertOrder(layout2, new int[]{ 2, 1 });
        layout2.addComponent(children[0]);
        assertOrder(layout1, new int[]{ 3 });
        assertOrder(layout2, new int[]{ 2, 1, 0 });
        layout1.addComponentAsFirst(children[1]);
        assertOrder(layout1, new int[]{ 1, 3 });
        assertOrder(layout2, new int[]{ 2, 0 });
    }

    @Test
    public void shuffleChildComponents() {
        shuffleChildComponents(new HorizontalLayout());
        shuffleChildComponents(new VerticalLayout());
    }

    @Test
    public void testConstructorsWithComponents() {
        AbstractOrderedLayout layout1 = new HorizontalLayout(children);
        assertOrder(layout1, new int[]{ 0, 1, 2, 3 });
        shuffleChildComponents(layout1);
        AbstractOrderedLayout layout2 = new VerticalLayout(children);
        assertOrder(layout2, new int[]{ 0, 1, 2, 3 });
        shuffleChildComponents(layout2);
    }

    @Test
    public void testAddComponents() {
        HorizontalLayout layout1 = new HorizontalLayout();
        layout1.addComponents(children);
        assertOrder(layout1, new int[]{ 0, 1, 2, 3 });
        Label extra = new Label("Extra");
        layout1.addComponents(extra);
        Assert.assertSame(extra, layout1.getComponent(4));
        layout1.removeAllComponents();
        layout1.addComponents(children[3], children[2], children[1], children[0]);
        assertOrder(layout1, new int[]{ 3, 2, 1, 0 });
        VerticalLayout layout2 = new VerticalLayout(children);
        layout2.addComponents(children);
        assertOrder(layout2, new int[]{ 0, 1, 2, 3 });
        layout2.addComponents(extra);
        Assert.assertSame(extra, layout2.getComponent(4));
        layout2.removeAllComponents();
        layout2.addComponents(children[3], children[2], children[1], children[0]);
        assertOrder(layout2, new int[]{ 3, 2, 1, 0 });
    }
}

