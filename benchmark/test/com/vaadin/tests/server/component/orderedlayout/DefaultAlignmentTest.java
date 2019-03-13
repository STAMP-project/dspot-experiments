package com.vaadin.tests.server.component.orderedlayout;


import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import org.junit.Test;


public class DefaultAlignmentTest {
    private VerticalLayout verticalLayout;

    private HorizontalLayout horizontalLayout;

    @Test
    public void testDefaultAlignmentVerticalLayout() {
        testDefaultAlignment(verticalLayout);
    }

    @Test
    public void testDefaultAlignmentHorizontalLayout() {
        testDefaultAlignment(horizontalLayout);
    }

    @Test
    public void testAlteredDefaultAlignmentVerticalLayout() {
        testAlteredDefaultAlignment(verticalLayout);
    }

    @Test
    public void testAlteredDefaultAlignmentHorizontalLayout() {
        testAlteredDefaultAlignment(horizontalLayout);
    }
}

