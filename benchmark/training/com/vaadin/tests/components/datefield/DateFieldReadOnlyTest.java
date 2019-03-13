package com.vaadin.tests.components.datefield;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


public class DateFieldReadOnlyTest extends MultiBrowserTest {
    @Test
    public void readOnlyDateFieldPopupShouldNotOpen() throws IOException, InterruptedException {
        openTestURL();
        compareScreen("initial-date");
        toggleReadOnly();
        openPopup();
        compareScreen("readwrite-popup-date");
        closePopup();
        toggleReadOnly();
        compareScreen("readonly-date");
    }
}

