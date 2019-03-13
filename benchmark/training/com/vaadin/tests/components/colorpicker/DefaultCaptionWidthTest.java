package com.vaadin.tests.components.colorpicker;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;


/**
 * Test for default caption behavior in color picker.
 *
 * @author Vaadin Ltd
 */
public class DefaultCaptionWidthTest extends MultiBrowserTest {
    @Test
    public void setDefaultCaption_sizeAndCaptionAreNotSet_pickerGetsStyle() {
        checkStylePresence(true);
    }

    @Test
    public void setDefaultCaption_explicitSizeIsSet_pickerNoCaptionStyle() {
        findElement(By.className("set-width")).click();
        checkStylePresence(false);
    }

    @Test
    public void setDefaultCaption_explicitCaptionIsSet_pickerNoCaptionStyle() {
        findElement(By.className("set-caption")).click();
        checkStylePresence(false);
    }
}

