package com.vaadin.tests.components.ui;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class InitiallyEmptyFragmentTest extends MultiBrowserTest {
    @Test
    public void testNoFragmentChangeEventWhenInitiallyEmpty() throws Exception {
        openTestURL();
        /* There is no fragment change event when the fragment is initially
        empty
         */
        assertLogText(" ");
        executeScript("window.location.hash='bar'");
        assertLogText("1. Fragment changed from \"no event received\" to bar");
    }
}

