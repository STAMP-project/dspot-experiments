package com.vaadin.tests.components.combobox;


import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


@SuppressWarnings("serial")
public class ComboSelectedValueBeyondTheFirstDropdownPageTest extends MultiBrowserTest {
    @Test
    public void valueOnSecondPageIsSelected() {
        openTestURL();
        ComboBoxElement comboBoxWebElement = $(ComboBoxElement.class).first();
        comboBoxWebElement.openNextPage();
        comboBoxWebElement.selectByText("Item 19");
        MatcherAssert.assertThat($(LabelElement.class).id("value").getText(), Matchers.is("Item 19"));
    }
}

