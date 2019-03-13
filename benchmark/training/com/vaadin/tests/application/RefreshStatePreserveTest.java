package com.vaadin.tests.application;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class RefreshStatePreserveTest extends MultiBrowserTest {
    private static String UI_ID_TEXT = "UI id: 0";

    @Test
    public void testPreserveState() throws Exception {
        openTestURL();
        assertCorrectState();
        // URL needs to be different or some browsers don't count it as history
        openTestURL("debug");
        assertCorrectState();
        executeScript("history.back()");
        assertCorrectState();
    }
}

