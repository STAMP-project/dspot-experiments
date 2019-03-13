package com.vaadin.v7.tests.server.component.combobox;


import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.v7.ui.ComboBox;
import org.junit.Test;


public class ComboBoxDeclarativeTest extends DeclarativeTestBase<ComboBox> {
    @Test
    public void testReadOnlyWithOptionsRead() {
        testRead(getReadOnlyWithOptionsDesign(), getReadOnlyWithOptionsExpected());
    }

    @Test
    public void testReadOnlyWithOptionsWrite() {
        testWrite(stripOptionTags(getReadOnlyWithOptionsDesign()), getReadOnlyWithOptionsExpected());
    }

    @Test
    public void testBasicRead() {
        testRead(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void testBasicWrite() {
        testWrite(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void testReadOnlyValue() {
        String design = "<vaadin7-combo-box readonly value='foo'><option selected>foo</option></vaadin7-combo-box>";
        ComboBox comboBox = new ComboBox();
        comboBox.addItems("foo", "bar");
        comboBox.setValue("foo");
        comboBox.setReadOnly(true);
        testRead(design, comboBox);
        // Selects items are not written out by default
        String design2 = "<vaadin7-combo-box readonly></vaadin7-combo-box>";
        testWrite(design2, comboBox);
    }
}

