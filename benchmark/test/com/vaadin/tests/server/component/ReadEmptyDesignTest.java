package com.vaadin.tests.server.component;


import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for checking that reading a design with no elements in the html
 * body produces null as the root component.
 */
public class ReadEmptyDesignTest {
    InputStream is;

    @Test
    public void testReadComponent() {
        Component root = Design.read(is);
        Assert.assertNull("The root component should be null.", root);
    }

    @Test
    public void testReadContext() {
        DesignContext ctx = Design.read(is, null);
        Assert.assertNotNull("The design context should not be null.", ctx);
        Assert.assertNull("The root component should be null.", ctx.getRootComponent());
    }

    @Test
    public void testReadContextWithRootParameter() {
        try {
            Component rootComponent = new VerticalLayout();
            DesignContext ctx = Design.read(is, rootComponent);
            Assert.fail("Reading a design with no elements should fail when a non-null root Component is specified.");
        } catch (DesignException e) {
            // This is the expected outcome, nothing to do.
        }
    }
}

