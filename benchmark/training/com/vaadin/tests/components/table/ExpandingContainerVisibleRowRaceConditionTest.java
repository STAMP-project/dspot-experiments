package com.vaadin.tests.components.table;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class ExpandingContainerVisibleRowRaceConditionTest extends MultiBrowserTest {
    private static final int ROW_HEIGHT = 20;

    @Test
    public void testScrollingWorksWithoutJumpingWhenItemSetChangeOccurs() {
        openTestURL();
        sleep(1000);
        WebElement table = vaadinElementById(ExpandingContainerVisibleRowRaceCondition.TABLE);
        assertFirstRowIdIs("ROW #120");
        testBenchElement(table.findElement(By.className("v-scrollable"))).scroll((320 * (ExpandingContainerVisibleRowRaceConditionTest.ROW_HEIGHT)));
        sleep(1000);
        assertRowIdIsInThePage("ROW #330");
        assertScrollPositionIsNotVisible();
    }
}

