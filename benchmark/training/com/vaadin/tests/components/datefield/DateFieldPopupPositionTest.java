package com.vaadin.tests.components.datefield;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


/**
 * Test for date field popup calendar position.
 *
 * @author Vaadin Ltd
 */
public abstract class DateFieldPopupPositionTest extends MultiBrowserTest {
    @Test
    public void testPopupPosition() {
        openTestURL();
        int height = (getFieldBottom()) + 150;
        adjustBrowserWindow(height);
        openPopup();
        checkPopupPosition();
    }
}

