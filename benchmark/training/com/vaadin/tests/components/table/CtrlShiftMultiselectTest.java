package com.vaadin.tests.components.table;


import Keys.SHIFT;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


public class CtrlShiftMultiselectTest extends MultiBrowserTest {
    @Test
    public void testSelectionRangeDragging() throws IOException {
        openTestURL();
        clickRow(3);
        new org.openqa.selenium.interactions.Actions(driver).keyDown(SHIFT).perform();
        clickRow(8);
        new org.openqa.selenium.interactions.Actions(driver).keyUp(SHIFT).perform();
        dragRows(5, 700, 0);
        compareScreen("draggedMultipleRows");
        release().perform();
    }
}

