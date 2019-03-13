package com.vaadin.tests.components.combobox;


import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class ComboBoxItemStyleGeneratorTest extends SingleBrowserTest {
    @Test
    public void testItemStyleGenerator() {
        openTestURL();
        ComboBoxElement comboBox = $(ComboBoxElement.class).first();
        selectMenuPath("Component", "Features", "Item style generator", "Bold fives");
        comboBox.openPopup();
        List<WebElement> boldItems = findElements(By.className("v-filterselect-item-bold"));
        Assert.assertEquals(1, boldItems.size());
        Assert.assertEquals("Item 5", boldItems.get(0).getText());
        selectMenuPath("Component", "Features", "Item style generator", "-");
        boldItems = findElements(By.className("v-filterselect-item-bold"));
        Assert.assertEquals(0, boldItems.size());
    }
}

