package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class GridClickableRenderersTest extends MultiBrowserTest {
    @Test
    public void clickableRenderersPresent() {
        openTestURL();
        Assert.assertTrue(isElementPresent(By.className("v-nativebutton")));
        Assert.assertTrue(isElementPresent(By.className("gwt-Image")));
    }

    @Test
    public void buttonRendererReturnsCorrectItem() {
        openTestURL();
        List<WebElement> findElements = findElements(By.className("v-nativebutton"));
        WebElement firstRowTextButton = findElements.get(0);
        WebElement firstRowHtmlButton = findElements.get(1);
        Assert.assertEquals("button 1 text", firstRowTextButton.getText());
        // If it was rendered as text, getText() would return the markup also
        Assert.assertEquals("button 1 html", firstRowHtmlButton.getText());
        WebElement secondRowTextButton = findElements.get(3);
        WebElement secondRowHtmlButton = findElements.get(4);
        Assert.assertEquals("button 2 text", secondRowTextButton.getText());
        // If it was rendered as text, getText() would return the markup also
        Assert.assertEquals("button 2 html", secondRowHtmlButton.getText());
        LabelElement label = $(LabelElement.class).get(1);
        firstRowTextButton.click();
        Assert.assertEquals("first row clicked", label.getText());
        secondRowTextButton.click();
        Assert.assertEquals("second row clicked", label.getText());
    }

    @Test
    public void checkBoxRendererClick() {
        openTestURL();
        WebElement firstRowButton = findElements(By.className("v-nativebutton")).get(2);
        WebElement secondRowButton = findElements(By.className("v-nativebutton")).get(5);
        LabelElement label = $(LabelElement.class).get(2);
        firstRowButton.click();
        Assert.assertEquals("first row false", label.getText());
        secondRowButton.click();
        Assert.assertEquals("second row true", label.getText());
    }
}

