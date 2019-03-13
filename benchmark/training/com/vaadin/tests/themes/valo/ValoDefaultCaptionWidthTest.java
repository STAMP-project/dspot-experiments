package com.vaadin.tests.themes.valo;


import com.vaadin.testbench.elements.ColorPickerElement;
import com.vaadin.tests.components.colorpicker.DefaultCaptionWidthTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for default caption behavior in color picker using Valo theme.
 *
 * @author Vaadin Ltd
 */
public class ValoDefaultCaptionWidthTest extends DefaultCaptionWidthTest {
    @Override
    @Test
    public void setDefaultCaption_sizeAndCaptionAreNotSet_pickerGetsStyle() {
        super.setDefaultCaption_sizeAndCaptionAreNotSet_pickerGetsStyle();
        int width = $(ColorPickerElement.class).first().getSize().getWidth();
        // Make sure that implicit width is less than one that will be
        // explicitly set by the test
        Assert.assertThat(("Width of color picker is overridden by " + "default caption feature"), width, Matchers.is(Matchers.lessThan(148)));
    }

    @Override
    @Test
    public void setDefaultCaption_explicitSizeIsSet_pickerNoCaptionStyle() {
        super.setDefaultCaption_explicitSizeIsSet_pickerNoCaptionStyle();
        int width = $(ColorPickerElement.class).first().getSize().getWidth();
        // Width should be 150px but let's just check that it's not which is
        // used when default caption is used and at least >= 150-1
        Assert.assertThat(("Width of color picker is overridden by " + "default caption feature"), width, Matchers.is(Matchers.greaterThan(149)));
    }
}

