package com.vaadin.tests.components.datefield;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class DisabledInlineDateTimeFieldTest extends MultiBrowserTest {
    @Test
    public void testDisabled() {
        openTestURL();
        testNextMonthControls(".v-disabled");
        testDaySelection(".v-disabled");
    }

    @Test
    public void testReadOnly() {
        openTestURL();
        testNextMonthControls(".v-readonly");
        testDaySelection(".v-readonly");
    }
}

