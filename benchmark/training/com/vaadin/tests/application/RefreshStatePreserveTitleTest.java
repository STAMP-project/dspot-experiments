package com.vaadin.tests.application;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class RefreshStatePreserveTitleTest extends MultiBrowserTest {
    @Test
    public void testReloadingPageDoesNotResetTitle() throws Exception {
        openTestURL();
        assertTitleText();
        openTestURL();
        assertTitleText();
    }
}

