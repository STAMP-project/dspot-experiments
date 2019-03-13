package com.vaadin.tests.dd;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.UIElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


/**
 * Test for interrupting drag-and-drop.
 *
 * @author Vaadin Ltd
 */
public class DDInterruptTest extends MultiBrowserTest {
    private UIElement ui;

    @Test
    public void testRegularDragging() {
        dragElement();
        assertNoNotifications();
        assertDragged(true);
    }

    @Test
    public void testTriggeredDragging() {
        $(ButtonElement.class).first().click();
        waitUntilTriggered(true);
        dragElement();
        waitUntilTriggered(false);
        assertNoNotifications();
        assertDragged(false);
    }
}

