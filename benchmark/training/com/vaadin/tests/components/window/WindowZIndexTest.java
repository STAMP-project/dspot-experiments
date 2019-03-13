package com.vaadin.tests.components.window;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class WindowZIndexTest extends MultiBrowserTest {
    @Test
    public void removingUpdatesZIndices() throws IOException {
        openTestURL();
        WebElement addButton = driver.findElement(By.xpath("//span[contains(text(),'Add window')]"));
        WebElement closeButton = driver.findElement(By.xpath("//span[contains(text(),'Close window')]"));
        addButton.click();
        addButton.click();
        addButton.click();
        addButton.click();
        addButton.click();
        closeButton.click();
        closeButton.click();
        closeButton.click();
        addButton.click();
        addButton.click();
        addButton.click();
        addButton.click();
        compareScreen("stacked");
        WebElement window4 = driver.findElement(By.xpath("//*[contains(text(), 'Window 4')]"));
        new org.openqa.selenium.interactions.Actions(driver).moveToElement(window4, getXOffset(window4, 1), getYOffset(window4, 9)).click().perform();
        compareScreen("win4-on-top");
    }
}

