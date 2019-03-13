package com.vaadin.tests.design;


import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class DesignTest {
    @Test
    public void readStream() throws FileNotFoundException {
        Component root = Design.read(getClass().getResourceAsStream("verticallayout-two-children.html"));
        VerticalLayout rootLayout = ((VerticalLayout) (root));
        Assert.assertEquals(VerticalLayout.class, root.getClass());
        Assert.assertEquals(2, rootLayout.getComponentCount());
        Assert.assertEquals(TextField.class, rootLayout.getComponent(0).getClass());
        Assert.assertEquals(Button.class, rootLayout.getComponent(1).getClass());
    }

    public static class MyVerticalLayout extends VerticalLayout {}

    @Test
    public void readWithSubClassRoot() throws FileNotFoundException {
        Design.read(getClass().getResourceAsStream("verticallayout-one-child.html"), new DesignTest.MyVerticalLayout());
    }

    @Test
    public void writeComponentToStream() throws IOException {
        HorizontalLayout root = new HorizontalLayout(new Button("OK"), new Button("Cancel"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Design.write(root, baos);
        Component newRoot = Design.read(new ByteArrayInputStream(baos.toByteArray()));
        assertHierarchyEquals(root, newRoot);
    }

    @Test
    public void writeDesignContextToStream() throws IOException {
        DesignContext dc = Design.read(getClass().getResourceAsStream("verticallayout-two-children.html"), null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Design.write(dc, baos);
        Component newRoot = Design.read(new ByteArrayInputStream(baos.toByteArray()));
        assertHierarchyEquals(dc.getRootComponent(), newRoot);
    }

    @Test(expected = DesignException.class)
    public void testDuplicateIds() throws FileNotFoundException {
        Design.read(getClass().getResourceAsStream("duplicate-ids.html"));
    }

    @Test(expected = DesignException.class)
    public void testDuplicateLocalIds() throws FileNotFoundException {
        Design.read(getClass().getResourceAsStream("duplicate-local-ids.html"));
    }
}

