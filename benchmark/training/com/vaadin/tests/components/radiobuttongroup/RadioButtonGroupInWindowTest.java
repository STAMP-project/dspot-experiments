package com.vaadin.tests.components.radiobuttongroup;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class RadioButtonGroupInWindowTest extends MultiBrowserTest {
    @Test
    public void radioButtonGroup_setFirstItemInWindow_valueShouldBeSet() {
        testSetDayInRadioButtonGroup("Monday");
    }

    @Test
    public void radioButtonGroup_setLastItemInWindow_valueShouldBeSet() {
        testSetDayInRadioButtonGroup("Sunday");
    }

    @Test
    public void radioButtonGroup_setMiddleItemInWindow_valueShouldBeSet() {
        testSetDayInRadioButtonGroup("Thursday");
    }
}

