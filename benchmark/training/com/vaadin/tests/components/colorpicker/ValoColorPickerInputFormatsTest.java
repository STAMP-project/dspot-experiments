package com.vaadin.tests.components.colorpicker;


import com.vaadin.testbench.elements.ColorPickerPreviewElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test legal color values according to
 * http://www.w3schools.com/cssref/css_colors_legal.asp
 */
public class ValoColorPickerInputFormatsTest extends MultiBrowserTest {
    private ColorPickerPreviewElement previewElement;

    @Test
    public void testRGBValue() throws Exception {
        setColorpickerValue("rgb(100 100 100)");
        Assert.assertEquals("#646464", previewElement.getColorFieldValue());
    }

    @Test
    public void testRGBAValue() {
        setColorpickerValue("rgba(100,100,100, 0.5)");
        Assert.assertEquals("#646464", previewElement.getColorFieldValue());
    }

    @Test
    public void testHSLValue() {
        setColorpickerValue("hsl(120,100%, 50%)");
        Assert.assertEquals("#00ff00", previewElement.getColorFieldValue());
    }

    @Test
    public void testHSLAValue() {
        setColorpickerValue("hsla(120, 0, 50%, 0.3)");
        Assert.assertEquals("#808080", previewElement.getColorFieldValue());
    }

    @Test
    public void testHexTextInputValidation() {
        // set valid hex value to ColorTextField
        setColorpickerValue("#AAbb33");
        Assert.assertFalse(previewElement.getColorFieldContainsErrors());
    }

    @Test
    public void testRGBTextInputValidation() {
        String rgbString = "rgb(255 10 0)";
        // set valid rgb value to ColorTextField
        setColorpickerValue(rgbString);
        Assert.assertFalse(previewElement.getColorFieldContainsErrors());
    }

    @Test
    public void testHSLTextInputValidation() {
        String hslString = "HSL(300, 60, 100)";
        setColorpickerValue(hslString);
        Assert.assertFalse(previewElement.getColorFieldContainsErrors());
    }

    @Test
    public void testHexTextInputValidationError() {
        // set invalid hex value to ColorTextField
        setColorpickerValue("#xyz");
        Assert.assertTrue(previewElement.getColorFieldContainsErrors());
    }

    @Test
    public void testRGBTextInputValidationError() {
        String rgbString = "rgb(300, 60, 90)";
        // set invalid rgb value to ColorTextField
        setColorpickerValue(rgbString);
        Assert.assertTrue(previewElement.getColorFieldContainsErrors());
    }

    @Test
    public void testRGBATextInputValidationError() {
        String rgbaString = "rgba(250, 0, 10, 6.0)";
        // set invalid rgba value to ColorTextField
        setColorpickerValue(rgbaString);
        Assert.assertTrue(previewElement.getColorFieldContainsErrors());
    }

    @Test
    public void testHSLTextInputValidationError() {
        String hslString = "hsl(370,60%,120%)";
        // set invalid hsl value to ColorTextField
        setColorpickerValue(hslString);
        Assert.assertTrue(previewElement.getColorFieldContainsErrors());
    }

    @Test
    public void testHSLATextInputValidationError() {
        String hslaString = "hsla(300, 50, 10, 1.1)";
        // set invalid hsla value to ColorTextField
        setColorpickerValue(hslaString);
        Assert.assertTrue(previewElement.getColorFieldContainsErrors());
    }

    @Test
    public void testFailedValidationResult() {
        // set invalid hex value to ColorTextField
        setColorpickerValue("#xyz");
        // verify there are errors
        Assert.assertTrue(previewElement.getColorFieldContainsErrors());
        // verify value has not been changed
        Assert.assertEquals(previewElement.getColorFieldValue(), "#xyz");
    }
}

