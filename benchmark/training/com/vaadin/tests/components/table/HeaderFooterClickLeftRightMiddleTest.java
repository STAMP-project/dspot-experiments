package com.vaadin.tests.components.table;


import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;
import org.openqa.selenium.By;


/**
 * Tests Table Footer ClickListener
 *
 * @author Vaadin Ltd
 */
public class HeaderFooterClickLeftRightMiddleTest extends MultiBrowserTest {
    @Test
    public void testFooter() throws IOException {
        openTestURL();
        waitForElementPresent(By.className("v-table"));
        TableElement table = $(TableElement.class).first();
        table.getHeaderCell(0).click();
        assertAnyLogText("1. Click on header col1 using left");
        table.getHeaderCell(0).contextClick();
        assertAnyLogText("2. Click on header col1 using right");
        table.getHeaderCell(0).doubleClick();
        assertAnyLogText("4. Double click on header col1 using left", "5. Double click on header col1 using left");
        table.getFooterCell(1).click();
        assertAnyLogText("5. Click on footer col2 using left", "6. Click on footer col2 using left");
        table.getFooterCell(1).contextClick();
        assertAnyLogText("6. Click on footer col2 using right", "7. Click on footer col2 using right");
        table.getFooterCell(1).doubleClick();
        assertAnyLogText("8. Double click on footer col2 using left", "9. Double click on footer col2 using left", "10. Double click on footer col2 using left");
    }
}

