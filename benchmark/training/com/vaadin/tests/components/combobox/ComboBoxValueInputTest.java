package com.vaadin.tests.components.combobox;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * Tests ComboBox inputs and selections.
 *
 * @author Vaadin Ltd
 */
public class ComboBoxValueInputTest extends MultiBrowserTest {
    @Test
    public void defaultComboBoxClearsInputOnInvalidValue() {
        ComboBoxElement comboBox = getComboBox("default");
        MatcherAssert.assertThat(getComboBoxValue(comboBox), CoreMatchers.is(""));
        comboBox.selectByText("Value 1");
        sendKeysToComboBox(comboBox, "abc");
        removeFocusFromComboBoxes();
        MatcherAssert.assertThat(getComboBoxValue(comboBox), CoreMatchers.is("Value 1"));
        assertThatComboBoxSuggestionsAreHidden(comboBox);
    }

    @Test
    public void comboBoxWithPromptClearsInputOnInvalidValue() {
        ComboBoxElement comboBox = getComboBox("default-prompt");
        MatcherAssert.assertThat(getComboBoxValue(comboBox), CoreMatchers.is("Please select"));
        comboBox.selectByText("Value 2");
        sendKeysToComboBox(comboBox, "def");
        removeFocusFromComboBoxes();
        MatcherAssert.assertThat(getComboBoxValue(comboBox), CoreMatchers.is("Value 2"));
        assertThatComboBoxSuggestionsAreHidden(comboBox);
    }

    @Test
    public void comboBoxWithNullItemClearsInputOnInvalidValue() {
        ComboBoxElement comboBox = getComboBox("null");
        MatcherAssert.assertThat(getComboBoxValue(comboBox), CoreMatchers.is("Null item"));
        sendKeysToComboBox(comboBox, "ghi");
        removeFocusFromComboBoxes();
        MatcherAssert.assertThat(getComboBoxValue(comboBox), CoreMatchers.is("Null item"));
        assertThatComboBoxSuggestionsAreHidden(comboBox);
    }

    @Test
    public void comboBoxWithNullItemAndPromptClearsInputOnInvalidValue() {
        ComboBoxElement comboBox = getComboBox("null-prompt");
        MatcherAssert.assertThat(getComboBoxValue(comboBox), CoreMatchers.is("Null item"));
        sendKeysToComboBox(comboBox, "jkl");
        removeFocusFromComboBoxes();
        MatcherAssert.assertThat(getComboBoxValue(comboBox), CoreMatchers.is("Null item"));
        assertThatComboBoxSuggestionsAreHidden(comboBox);
    }

    @Test
    public void comboBoxWithFilteringOffClearsInputOnInvalidValue() {
        ComboBoxElement comboBox = getComboBox("filtering-off");
        MatcherAssert.assertThat(getComboBoxValue(comboBox), CoreMatchers.is(""));
        // selectByText doesn't work when filtering is off.
        comboBox.openPopup();
        List<WebElement> filteredItems = findElements(By.className("gwt-MenuItem"));
        filteredItems.get(1).click();
        sendKeysToComboBox(comboBox, "mnop");
        removeFocusFromComboBoxes();
        MatcherAssert.assertThat(getComboBoxValue(comboBox), CoreMatchers.is("Value 1"));
        assertThatComboBoxSuggestionsAreHidden(comboBox);
    }
}

