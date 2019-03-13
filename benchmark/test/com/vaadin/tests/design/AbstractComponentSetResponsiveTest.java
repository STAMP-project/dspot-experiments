package com.vaadin.tests.design;


import ContentMode.HTML;
import com.vaadin.ui.Label;
import org.junit.Test;


public class AbstractComponentSetResponsiveTest extends DeclarativeTestBase<Label> {
    @Test
    public void testResponsiveFlag() {
        Label label = new Label();
        label.setContentMode(HTML);
        label.setResponsive(true);
        String design = "<vaadin-label responsive />";
        testWrite(design, label);
        testRead(design, label);
    }
}

