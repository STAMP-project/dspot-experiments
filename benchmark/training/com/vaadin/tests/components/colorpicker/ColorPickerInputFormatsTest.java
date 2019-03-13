package com.vaadin.tests.components.colorpicker;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test legal color values according to
 * http://www.w3schools.com/cssref/css_colors_legal.asp
 */
public class ColorPickerInputFormatsTest extends MultiBrowserTest {
    @Test
    public void testRGBValue() throws Exception {
        openTestURL();
        setColorpickerValue("rgb(100,100,100)");
        Assert.assertEquals("#646464", getColorpickerValue());
    }

    @Test
    public void testRGBAValue() {
        openTestURL();
        setColorpickerValue("rgba(100,100,100, 0.5)");
        Assert.assertEquals("#646464", getColorpickerValue());
    }

    @Test
    public void testHSLValue() {
        openTestURL();
        setColorpickerValue("hsl(120,100%,50%)");
        Assert.assertEquals("#00ff00", getColorpickerValue());
    }

    @Test
    public void testHSLAValue() {
        openTestURL();
        setColorpickerValue("hsla(120,100%,50%, 0.3)");
        Assert.assertEquals("#00ff00", getColorpickerValue());
    }
}

