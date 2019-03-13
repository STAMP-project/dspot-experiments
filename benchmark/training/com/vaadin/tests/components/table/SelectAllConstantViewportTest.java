package com.vaadin.tests.components.table;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class SelectAllConstantViewportTest extends MultiBrowserTest {
    @Test
    public void testViewportUnchanged() throws IOException {
        openTestURL();
        CheckBoxElement checkbox = $(CheckBoxElement.class).first();
        WebElement row = $(TableElement.class).first().getCell(190, 0);
        final WebElement scrollPositionDisplay = getDriver().findElement(By.className("v-table-scrollposition"));
        waitUntilNot(( input) -> scrollPositionDisplay.isDisplayed(), 10);
        int rowLocation = row.getLocation().getY();
        // use click x,y with non-zero offset to actually toggle the checkbox.
        // (#13763)
        checkbox.click(5, 5);
        int newRowLocation = row.getLocation().getY();
        MatcherAssert.assertThat(newRowLocation, CoreMatchers.is(rowLocation));
    }
}

