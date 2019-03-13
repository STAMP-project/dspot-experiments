package com.vaadin.v7.tests.server.component.richtextarea;


import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.v7.ui.RichTextArea;
import org.junit.Test;


public class RichTextAreaDeclarativeTest extends DeclarativeTestBase<RichTextArea> {
    @Test
    public void testBasicRead() {
        testRead(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void testBasicWrite() {
        testWrite(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void testReadEmpty() {
        testRead("<vaadin7-rich-text-area />", new RichTextArea());
    }

    @Test
    public void testWriteEmpty() {
        testWrite("<vaadin7-rich-text-area />", new RichTextArea());
    }

    @Test
    public void testReadOnlyValue() {
        String design = "<vaadin7-rich-text-area readonly style-name='v-richtextarea-readonly'>Hello World!</vaadin7-text-area>";
        RichTextArea ta = new RichTextArea();
        ta.setValue("Hello World!");
        ta.setReadOnly(true);
        testRead(design, ta);
        testWrite(design, ta);
    }
}

