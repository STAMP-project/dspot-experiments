package com.vaadin.tests.components.datefield;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class PopupTimeClosingWithEscTest extends MultiBrowserTest {
    @Test
    public void testPopupClosing() {
        openTestURL();
        testPopupClosing("second");
        testPopupClosing("minute");
        testPopupClosing("hour");
        testPopupClosing("month");
    }
}

