package com.vaadin.tests.elements.combobox;


import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;

import static ComboBoxInputNotAllowed.ITEM_LAST_WITH_PARENTHESIS;
import static ComboBoxInputNotAllowed.ITEM_ON_FIRST_PAGE;
import static ComboBoxInputNotAllowed.ITEM_ON_LAST_PAGE;
import static ComboBoxInputNotAllowed.ITEM_ON_SECOND_PAGE;


public class ComboBoxInputNotAllowedTest extends MultiBrowserTest {
    @Test
    public void selectByTextComboBoxWithTextInputDisabled() {
        openTestURL();
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        String[] optionsToTest = new String[]{ ITEM_ON_FIRST_PAGE, ITEM_ON_SECOND_PAGE, ITEM_ON_LAST_PAGE, ITEM_LAST_WITH_PARENTHESIS, ITEM_ON_FIRST_PAGE };
        for (String option : optionsToTest) {
            cb.selectByText(option);
            Assert.assertEquals(("Value is now: " + option), $(LabelElement.class).last().getText());
            Assert.assertEquals(option, cb.getValue());
        }
    }
}

