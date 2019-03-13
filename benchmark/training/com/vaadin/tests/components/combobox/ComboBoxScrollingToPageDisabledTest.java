package com.vaadin.tests.components.combobox;


import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * When pressed down key, while positioned on the last item - should show next
 * page and focus on the first item of the next page.
 */
public class ComboBoxScrollingToPageDisabledTest extends MultiBrowserTest {
    @Test
    public void checkValueIsVisible() throws InterruptedException {
        ComboBoxElement combo = $(ComboBoxElement.class).first();
        Assert.assertEquals("Item 50", combo.getText());
    }

    @Test
    public void checkLastValueIsVisible() throws InterruptedException {
        ComboBoxElement combo = $(ComboBoxElement.class).first();
        combo.selectByText("Item 99");
        // this shouldn't clear the selection
        combo.openPopup();
        // close popup
        $(LabelElement.class).first().click();
        Assert.assertEquals("Item 99", combo.getText());
    }
}

