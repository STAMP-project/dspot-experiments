package com.vaadin.tests.components.slider;


import com.vaadin.tests.tb3.AbstractTB3Test;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class SliderFeedbackTest extends MultiBrowserTest {
    @Test
    public void testValueGreaterThanMaxInt() {
        openTestURL();
        WebElement handle = findElement(By.className("v-slider-handle"));
        new org.openqa.selenium.interactions.Actions(driver).dragAndDropBy(handle, 400, 0).perform();
        testBench().waitForVaadin();
        double value = Double.valueOf(findElement(By.className("v-slider-feedback")).getText());
        // Allow for some tolerance due to, you guessed it, IE8
        AbstractTB3Test.assertLessThan("Unexpected feedback value {1} < {0}", 5.05E11, value);
        AbstractTB3Test.assertGreater("Unexpected feedback value {1} > {0}", 5.1E11, value);
    }
}

