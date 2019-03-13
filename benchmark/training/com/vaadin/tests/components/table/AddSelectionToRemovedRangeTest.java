package com.vaadin.tests.components.table;


import Keys.CONTROL;
import Keys.SHIFT;
import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;


public class AddSelectionToRemovedRangeTest extends MultiBrowserTest {
    @Test
    public void addAndRemoveItemToRemovedRange() throws IOException {
        openTestURL();
        List<WebElement> rows = driver.findElements(By.className("v-table-cell-wrapper"));
        WebElement rangeStart = rows.get(0);
        WebElement rangeEnd = rows.get(1);
        rangeStart.click();
        new org.openqa.selenium.interactions.Actions(driver).keyDown(SHIFT).perform();
        rangeEnd.click();
        new org.openqa.selenium.interactions.Actions(driver).keyUp(SHIFT).perform();
        driver.findElement(By.className("v-button")).click();
        WebElement extraRow = driver.findElements(By.className("v-table-cell-wrapper")).get(1);
        new org.openqa.selenium.interactions.Actions(driver).keyDown(CONTROL).click(extraRow).click(extraRow).keyUp(CONTROL).perform();
        driver.findElement(By.className("v-button")).click();
        try {
            driver.findElement(By.vaadin("Root/VNotification[0]"));
            Assert.fail("Notification is shown");
        } catch (NoSuchElementException e) {
            // All is well.
        }
    }
}

