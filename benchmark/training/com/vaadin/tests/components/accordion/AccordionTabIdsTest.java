package com.vaadin.tests.components.accordion;


import AccordionTabIds.FIRST_TAB_ID;
import AccordionTabIds.FIRST_TAB_MESSAGE;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * Test for Accordion: Tab.setId should be propagated to client side tabs.
 *
 * @author Vaadin Ltd
 */
public class AccordionTabIdsTest extends MultiBrowserTest {
    @Test
    public void testGeTabByIds() {
        openTestURL();
        ButtonElement setIdButton = $(ButtonElement.class).first();
        ButtonElement clearIdbutton = $(ButtonElement.class).get(1);
        WebElement firstItem = driver.findElement(By.id(FIRST_TAB_ID));
        WebElement label = $(LabelElement.class).context(firstItem).first();
        Assert.assertEquals(FIRST_TAB_MESSAGE, label.getText());
        clearIdbutton.click();
        Assert.assertEquals("", firstItem.getAttribute("id"));
        setIdButton.click();
        Assert.assertEquals(FIRST_TAB_ID, firstItem.getAttribute("id"));
    }
}

