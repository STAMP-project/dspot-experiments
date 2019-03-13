package com.vaadin.tests.components.slider;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class SliderResizeTest extends MultiBrowserTest {
    @Test
    public void resizeSlider() throws IOException {
        openTestURL();
        // Verify the starting position.
        Assert.assertEquals("488px", getSliderHandlePosition());
        // Click on the button that reduces the layout width by 200px.
        driver.findElement(By.className("v-button")).click();
        // Assert that the slider handle was also moved.
        Assert.assertEquals("288px", getSliderHandlePosition());
    }
}

