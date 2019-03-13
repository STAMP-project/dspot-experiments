package com.vaadin.tests.components;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


/**
 *
 *
 * @author Vaadin Ltd
 */
public abstract class HasValueRequiredIndicatorTest extends MultiBrowserTest {
    @Test
    public void requiredIndicatorVisible() {
        openTestURL();
        List<WebElement> layouts = findElements(By.className("vaadin-layout"));
        Assert.assertFalse(layouts.isEmpty());
        layouts.stream().forEach(this::checkRequiredIndicator);
    }
}

