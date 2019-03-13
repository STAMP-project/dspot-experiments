package com.vaadin.v7.tests.components.grid;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class GridWidgetRendererChangeTest extends SingleBrowserTest {
    @Test
    public void testChangeWidgetRenderer() {
        setDebug(true);
        openTestURL();
        selectMenuPath("Component", "Change first renderer");
        assertNoErrorNotifications();
        selectMenuPath("Component", "Change first renderer");
        assertNoErrorNotifications();
        // First renderer OK
        selectMenuPath("Component", "Change second renderer");
        assertNoErrorNotifications();
        selectMenuPath("Component", "Change second renderer");
        assertNoErrorNotifications();
    }
}

