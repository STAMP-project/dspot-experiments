package com.vaadin.v7.tests.server.component.datefield;


import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.v7.ui.PopupDateField;
import org.junit.Test;


/**
 * Tests the declarative support for implementations of {@link PopupDateField}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class PopupDateFieldDeclarativeTest extends DeclarativeTestBase<PopupDateField> {
    @Test
    public void readBasic() throws Exception {
        testRead(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void writeBasic() throws Exception {
        testRead(getBasicDesign(), getBasicExpected());
    }
}

