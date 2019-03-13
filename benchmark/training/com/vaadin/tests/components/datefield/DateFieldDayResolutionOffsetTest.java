package com.vaadin.tests.components.datefield;


import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


public class DateFieldDayResolutionOffsetTest extends MultiBrowserTest {
    @Test
    public void dateValueDoesNotHaveOffset() throws InterruptedException {
        openTestURL();
        openDatePicker();
        select2ndOfSeptember();
        LabelElement dateValue = $(LabelElement.class).id("dateValue");
        MatcherAssert.assertThat(dateValue.getText(), Is.is("09/02/2014 00:00:00"));
    }
}

