package com.vaadin.tests.components.datefield;


import com.vaadin.testbench.elements.AbstractDateFieldElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class DateFieldWhenChangingValueAndEnablingParentTest extends SingleBrowserTest {
    @Test
    public void ensureCorrectStateAfterEnabling() {
        openTestURL();
        $(CheckBoxElement.class).first().click();
        assertState($(AbstractDateFieldElement.class).id("DATEFIELD_ENABLED"), true, true);
        assertState($(AbstractDateFieldElement.class).id("DATEFIELD_DISABLED"), false, false);
        assertState($(DateFieldElement.class).id("DATEFIELD_ENABLED_ENABLED"), true, true);
        assertState($(DateFieldElement.class).id("DATEFIELD_ENABLED_DISABLED"), true, false);
        // disabling widget should always disable input
        assertState($(DateFieldElement.class).id("DATEFIELD_DISABLED_ENABLED"), false, false);
        assertState($(DateFieldElement.class).id("DATEFIELD_DISABLED_DISABLED"), false, false);
    }
}

