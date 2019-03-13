package com.vaadin.tests.actions;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class ActionsOnDetachedComponentsTest extends MultiBrowserTest {
    @Test
    public void shortcutActionOnDetachedComponentShouldNotBeHandled() throws InterruptedException {
        Actions k = new Actions(driver);
        k.sendKeys("a").perform();
        k.sendKeys("a").perform();
        sleep(500);
        assertElementNotPresent(By.id("layer-A"));
        assertElementPresent(By.id("layer-B"));
        Assert.assertThat(getLogRow(0), CoreMatchers.endsWith("btn-A"));
        Assert.assertThat(getLogRow(1), CoreMatchers.not(CoreMatchers.endsWith("btn-B")));
    }

    @Test
    public void actionOnDetachedComponentShouldNotBeHandled() throws InterruptedException {
        TableElement table = $(TableElement.class).first();
        table.getRow(0).contextClick();
        // Find the opened menu
        WebElement menu = findElement(By.className("v-contextmenu"));
        WebElement menuitem = menu.findElement(By.xpath("//*[text() = 'Table action']"));
        Actions doubleClick = new Actions(getDriver());
        doubleClick.doubleClick(menuitem).build().perform();
        assertElementNotPresent(By.id("layer-A"));
        assertElementPresent(By.id("layer-B"));
        Assert.assertThat(getLogRow(0), CoreMatchers.endsWith("tableAction"));
        Assert.assertThat(getLogRow(1), CoreMatchers.not(CoreMatchers.endsWith("tableAction")));
    }
}

