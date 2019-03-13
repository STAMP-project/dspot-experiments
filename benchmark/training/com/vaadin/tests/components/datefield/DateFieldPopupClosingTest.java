package com.vaadin.tests.components.datefield;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


public class DateFieldPopupClosingTest extends MultiBrowserTest {
    @Test
    public void testDateFieldPopupClosingLongClick() throws IOException, InterruptedException {
        openTestURL();
        fastClickDateDatePickerButton();
        assertThatPopupIsVisible();
        longClickDateDatePickerButton();
        assertThatPopupIsInvisible();
    }
}

