package com.vaadin.tests.components.grid.basics;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class RefreshDataProviderTest extends MultiBrowserTest {
    @Test
    public void updateFirstRow() {
        openTestURL();
        findElement(By.id("update")).click();
        WebElement first = findElement(By.tagName("td"));
        Assert.assertEquals("UI component is not refreshed after update in data", "Updated coordinates", first.getText());
    }

    @Test
    public void addFirstRow() {
        openTestURL();
        findElement(By.id("add")).click();
        WebElement first = findElement(By.tagName("td"));
        Assert.assertEquals("UI component is not refreshed after add new data", "Added", first.getText());
    }

    @Test
    public void removeFirstRow() {
        openTestURL();
        WebElement first = findElement(By.tagName("td"));
        String old = first.getText();
        first = findElement(By.id("remove"));
        Assert.assertNotEquals("UI component is not refreshed after removal", old, first.getText());
    }
}

