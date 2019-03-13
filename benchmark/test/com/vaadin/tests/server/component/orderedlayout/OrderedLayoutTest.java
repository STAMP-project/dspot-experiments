package com.vaadin.tests.server.component.orderedlayout;


import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import org.junit.Test;


public class OrderedLayoutTest {
    @Test
    public void testVLIteration() {
        testIndexing(new VerticalLayout(), 10);
    }

    @Test
    public void testHLIteration() {
        testIndexing(new HorizontalLayout(), 12);
    }
}

