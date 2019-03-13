package com.vaadin.tests.components.window;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class ExtraWindowShownTest extends MultiBrowserTest {
    @Test
    public void testNoExtraWindowAfterClosing() throws Exception {
        openTestURL();
        openWindow();
        closeWindow();
        assertNoWindow();
        openWindow();
        closeWindow();
        assertNoWindow();
    }
}

