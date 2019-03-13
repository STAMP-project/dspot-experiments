package com.vaadin.tests.fieldgroup;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.SingleBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class BasicCrudGridTest extends SingleBrowserTest {
    @Test
    public void fieldsInitiallyEmpty() {
        openTestURL();
        List<TextFieldElement> textFields = getFieldsLayout().$(TextFieldElement.class).all();
        for (TextFieldElement e : textFields) {
            Assert.assertEquals("TextField should be empty", "", e.getValue());
        }
    }

    @Test
    public void fieldsClearedOnDeselect() {
        Assume.assumeFalse("PhantomJS has issues with this test", BrowserUtil.isPhantomJS(getDesiredCapabilities()));
        openTestURL();
        // Select row
        $(GridElement.class).first().getCell(2, 2).click();
        List<TextFieldElement> textFields = getFieldsLayout().$(TextFieldElement.class).all();
        for (TextFieldElement e : textFields) {
            Assert.assertNotEquals("TextField should not be empty", "", e.getValue());
        }
        // Deselect row
        $(GridElement.class).first().getCell(2, 2).click();
        for (TextFieldElement e : textFields) {
            Assert.assertEquals("TextField should be empty", "", e.getValue());
        }
    }
}

