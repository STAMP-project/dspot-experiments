package com.vaadin.tests.components.window;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class WindowShouldRemoveActionHandlerTest extends MultiBrowserTest {
    @Test
    public void testRemovingActionHandlers() {
        openTestURL();
        addActionHandler();
        addAnotherActionHandler();
        assertState("An UI with 2 action handlers");
        addActionHandler();
        assertState("An UI with 3 action handlers");
        removeActionHandler();
        removeActionHandler();
        assertState("An UI with 3 action handlers - Removed handler - Removed handler");
        addActionHandler();
        assertState("An UI with 2 action handlers");
    }
}

