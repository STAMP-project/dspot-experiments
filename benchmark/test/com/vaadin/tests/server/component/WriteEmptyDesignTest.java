package com.vaadin.tests.server.component;


import com.vaadin.ui.Component;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.Test;


/**
 * Test cases for checking that writing a component hierarchy with null root
 * produces an html document that has no elements in the html body.
 */
public class WriteEmptyDesignTest {
    @Test
    public void testWriteComponent() throws IOException {
        OutputStream os = new ByteArrayOutputStream();
        Design.write(((Component) (null)), os);
        checkHtml(os.toString());
    }

    @Test
    public void testWriteContext() throws IOException {
        OutputStream os = new ByteArrayOutputStream();
        DesignContext ctx = new DesignContext();
        ctx.setRootComponent(null);
        Design.write(ctx, os);
        checkHtml(os.toString());
    }
}

