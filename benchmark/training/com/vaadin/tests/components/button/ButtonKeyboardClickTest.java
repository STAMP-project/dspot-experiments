package com.vaadin.tests.components.button;


import Keys.ENTER;
import Keys.SPACE;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


/**
 * Test for availability (x,y) coordinates for button activated via keyboard.
 *
 * @author Vaadin Ltd
 */
public class ButtonKeyboardClickTest extends MultiBrowserTest {
    @Test
    public void testCoordinatesForClickedButtonViaSpace() {
        openTestURL();
        WebElement button = getDriver().findElement(By.className("v-button"));
        button.sendKeys(SPACE);
        checkCoordinates(button);
    }

    @Test
    public void testCoordinatesForClickedButtonViaEnter() {
        openTestURL();
        WebElement button = getDriver().findElement(By.className("v-button"));
        button.sendKeys(ENTER);
        checkCoordinates(button);
    }
}

