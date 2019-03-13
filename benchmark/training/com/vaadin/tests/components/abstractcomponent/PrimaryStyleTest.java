package com.vaadin.tests.components.abstractcomponent;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class PrimaryStyleTest extends MultiBrowserTest {
    @Test
    public void testStyleNames() {
        openTestURL();
        // Verify the initial class names for all three components.
        List<WebElement> initialElements = driver.findElements(By.className("initial-state"));
        MatcherAssert.assertThat(initialElements, hasSize(3));
        // Click on a button that updates the styles.
        $(ButtonElement.class).id("update-button").click();
        // Verify that the class names where updated as expected.
        List<WebElement> updatedElements = driver.findElements(By.className("updated-correctly"));
        MatcherAssert.assertThat(updatedElements, hasSize(initialElements.size()));
    }
}

