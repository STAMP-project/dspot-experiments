package com.vaadin.tests.server.component.gridlayout;


import Alignment.MIDDLE_CENTER;
import Alignment.TOP_LEFT;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import org.junit.Assert;
import org.junit.Test;


public class DefaultAlignmentTest {
    private GridLayout gridLayout;

    @Test
    public void testDefaultAlignment() {
        Label label = new Label("A label");
        TextField tf = new TextField("A TextField");
        gridLayout.addComponent(label);
        gridLayout.addComponent(tf);
        Assert.assertEquals(TOP_LEFT, gridLayout.getComponentAlignment(label));
        Assert.assertEquals(TOP_LEFT, gridLayout.getComponentAlignment(tf));
    }

    @Test
    public void testAlteredDefaultAlignment() {
        Label label = new Label("A label");
        TextField tf = new TextField("A TextField");
        gridLayout.setDefaultComponentAlignment(MIDDLE_CENTER);
        gridLayout.addComponent(label);
        gridLayout.addComponent(tf);
        Assert.assertEquals(MIDDLE_CENTER, gridLayout.getComponentAlignment(label));
        Assert.assertEquals(MIDDLE_CENTER, gridLayout.getComponentAlignment(tf));
    }
}

