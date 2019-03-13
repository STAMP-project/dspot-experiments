package com.vaadin.tests.server.component.customlayout;


import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import org.junit.Test;


/**
 * Tests declarative support for {@link CustomLayout}.
 *
 * @author Vaadin Ltd
 */
public class CustomLayoutDeclarativeTest extends DeclarativeTestBase<CustomLayout> {
    @Test
    public void testEmpty() {
        String design = "<vaadin-custom-layout>";
        CustomLayout expected = new CustomLayout();
        test(design, expected);
    }

    @Test
    public void testWithChildren() {
        String design = "<vaadin-custom-layout>"// 
         + (("<vaadin-button plain-text :location='b'></vaadin-button>"// 
         + "<vaadin-label plain-text :location='l'></vaadin-label>")// 
         + "</vaadin-custom-layout>");
        CustomLayout expected = new CustomLayout();
        expected.addComponent(new Button(), "b");
        expected.addComponent(new Label(), "l");
        test(design, expected);
    }

    @Test
    public void testWithOneChild() {
        String design = "<vaadin-custom-layout><vaadin-button plain-text></vaadin-button></vaadin-custom-layout>";
        CustomLayout expected = new CustomLayout();
        expected.addComponent(new Button());
        test(design, expected);
    }

    @Test
    public void testWithTemplate() {
        String design = "<vaadin-custom-layout template-name='template.html'></vaadin-custom-layout>";
        CustomLayout expected = new CustomLayout("template.html");
        test(design, expected);
    }

    @Test
    public void testWithDuplicateLocations() {
        String design = "<vaadin-custom-layout>"// 
         + (("<vaadin-button plain-text :location='foo'></vaadin-button>"// 
         + "<vaadin-label plain-text :location='foo'></vaadin-label>")// 
         + "</vaadin-custom-layout>");
        CustomLayout expected = new CustomLayout();
        expected.addComponent(new Button(), "foo");
        expected.addComponent(new Label(), "foo");
        testRead(design, expected);
        String written = "<vaadin-custom-layout>"// 
         + ("<vaadin-label plain-text :location='foo'></vaadin-label>"// 
         + "</vaadin-custom-layout>");
        testWrite(written, expected);
    }
}

