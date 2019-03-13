package com.vaadin.tests.components.combobox;


import Keys.DOWN;
import Keys.ENTER;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;
import org.openqa.selenium.interactions.Actions;


public class ComboBoxBorderTest extends MultiBrowserTest {
    @Test
    public void testComboBoxArrow() throws IOException {
        openTestURL();
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.openPopup();
        cb.sendKeys(DOWN, ENTER);
        Actions actions = new Actions(getDriver());
        actions.moveToElement($(LabelElement.class).first()).perform();
        compareScreen("arrow");
    }
}

