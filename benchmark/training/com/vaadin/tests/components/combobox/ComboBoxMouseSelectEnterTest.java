package com.vaadin.tests.components.combobox;


import Keys.DOWN;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class ComboBoxMouseSelectEnterTest extends MultiBrowserTest {
    private ComboBoxElement comboBoxElement;

    @Test
    public void enterSetsValueSelectedByMouseOver() {
        comboBoxElement.openPopup();
        comboBoxElement.sendKeys(DOWN, DOWN);
        String selectedItemText = findElement(By.className("gwt-MenuItem-selected")).getText();
        MatcherAssert.assertThat("Item selected by arrows should be a1", selectedItemText, CoreMatchers.is("a1"));
        new org.openqa.selenium.interactions.Actions(driver).moveToElement(getWebElementForItem("a5")).build().perform();
        comboBoxElement.sendKeys(getReturn());
        MatcherAssert.assertThat("Item selected by mouse should be a5", comboBoxElement.getText(), CoreMatchers.is("a5"));
        checkLabelValue("a5");
    }
}

