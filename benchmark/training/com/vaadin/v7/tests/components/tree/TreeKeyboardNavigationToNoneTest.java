package com.vaadin.v7.tests.components.tree;


import Keys.ARROW_DOWN;
import Keys.ARROW_UP;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


/**
 * Test for keyboard navigation in tree in case when there are no items to
 * navigate.
 *
 * @author Vaadin Ltd
 */
public class TreeKeyboardNavigationToNoneTest extends MultiBrowserTest {
    @Test
    public void navigateUpForTheFirstItem() {
        sendKey(ARROW_UP);
        checkNotificationErrorAbsence("first");
    }

    @Test
    public void navigateDownForTheLastItem() {
        $(ButtonElement.class).first().click();
        sendKey(ARROW_DOWN);
        checkNotificationErrorAbsence("last");
    }
}

