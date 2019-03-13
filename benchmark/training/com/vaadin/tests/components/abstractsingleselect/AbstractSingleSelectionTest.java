package com.vaadin.tests.components.abstractsingleselect;


import com.vaadin.testbench.elements.AbstractSingleSelectElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.tests.tb3.ParameterizedTB3Runner;
import com.vaadin.tests.tb3.SingleBrowserTest;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.NoSuchElementException;


@RunWith(ParameterizedTB3Runner.class)
public class AbstractSingleSelectionTest extends SingleBrowserTest {
    private static final Map<String, Class<? extends AbstractSingleSelectElement>> elementClasses = new LinkedHashMap<>();

    private String elementClassName;

    @Test
    public void testSelectNull() {
        $(NativeSelectElement.class).first().selectByText(elementClassName);
        assertInitial();
        $(ButtonElement.class).caption("Deselect").first().click();
        AbstractSingleSelectElement selectElement = getSelectElement();
        // TODO: TB API behavior should be unified.
        if (selectElement instanceof RadioButtonGroupElement) {
            Assert.assertNull("No value should be selected", selectElement.getValue());
        } else
            if (selectElement instanceof ComboBoxElement) {
                Assert.assertTrue("No value should be selected", selectElement.getValue().isEmpty());
            } else {
                // NativeSelectElement throws if no value is selected.
                try {
                    selectElement.getValue();
                    Assert.fail("No value should be selected");
                } catch (NoSuchElementException e) {
                    // All is fine.
                }
            }

    }

    @Test
    public void testSelectOnClientAndRefresh() {
        $(NativeSelectElement.class).first().selectByText(elementClassName);
        assertInitial();
        AbstractSingleSelectElement select = getSelectElement();
        select.selectByText("Baz");
        Assert.assertEquals("Value should change", "Baz", select.getValue());
        $(ButtonElement.class).caption("Refresh").first().click();
        Assert.assertEquals("Value should stay the same through refreshAll", "Baz", select.getValue());
    }

    @Test
    public void testSelectOnClientAndResetValueOnServer() {
        $(NativeSelectElement.class).first().selectByText(elementClassName);
        assertInitial();
        AbstractSingleSelectElement select = getSelectElement();
        select.selectByText("Baz");
        Assert.assertEquals("Value should change", "Baz", select.getValue());
        $(ButtonElement.class).caption("Select Bar").first().click();
        Assert.assertEquals("Original value should be selected again", "Bar", select.getValue());
    }

    @Test
    public void testSelectOnClientAndResetValueOnServerInListener() {
        $(NativeSelectElement.class).first().selectByText(elementClassName);
        assertInitial();
        AbstractSingleSelectElement rbg = getSelectElement();
        rbg.selectByText("Reset");
        // Selecting "Reset" selects "Bar" on server. Value was initially "Bar"
        Assert.assertEquals("Original value should be selected again", "Bar", rbg.getValue());
    }
}

