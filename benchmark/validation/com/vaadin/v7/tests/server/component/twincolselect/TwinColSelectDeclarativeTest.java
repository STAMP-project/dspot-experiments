package com.vaadin.v7.tests.server.component.twincolselect;


import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.v7.ui.TwinColSelect;
import org.junit.Test;


/**
 * Test cases for reading the properties of selection components.
 *
 * @author Vaadin Ltd
 */
public class TwinColSelectDeclarativeTest extends DeclarativeTestBase<TwinColSelect> {
    @Test
    public void testReadBasic() {
        testRead(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void testWriteBasic() {
        testWrite(stripOptionTags(getBasicDesign()), getBasicExpected());
    }

    @Test
    public void testReadEmpty() {
        testRead("<vaadin7-twin-col-select />", new TwinColSelect());
    }

    @Test
    public void testWriteEmpty() {
        testWrite("<vaadin7-twin-col-select />", new TwinColSelect());
    }
}

