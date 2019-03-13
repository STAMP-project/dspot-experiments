package com.vaadin.tests.components.ui;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class UIsInMultipleTabsTest extends MultiBrowserTest {
    @Test
    public void testPageReloadChangesUI() throws Exception {
        openTestURL();
        assertUI(1);
        openTestURL();
        assertUI(2);
        openTestURL("restartApplication");
        assertUI(1);
    }
}

