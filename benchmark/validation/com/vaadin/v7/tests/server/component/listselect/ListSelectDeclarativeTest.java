package com.vaadin.v7.tests.server.component.listselect;


import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.v7.ui.ListSelect;
import org.junit.Test;


public class ListSelectDeclarativeTest extends DeclarativeTestBase<ListSelect> {
    @Test
    public void testReadWithOptions() {
        testRead(getWithOptionsDesign(), getWithOptionsExpected());
    }

    @Test
    public void testWriteWithOptions() {
        testWrite(stripOptionTags(getWithOptionsDesign()), getWithOptionsExpected());
    }

    @Test
    public void testReadBasic() {
        testRead(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void testWriteBasic() {
        testWrite(getBasicDesign(), getBasicExpected());
    }
}

