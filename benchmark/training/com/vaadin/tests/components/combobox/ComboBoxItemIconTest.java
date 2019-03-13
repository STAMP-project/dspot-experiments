package com.vaadin.tests.components.combobox;


import Keys.ESCAPE;
import Keys.UP;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


@TestCategory("xvfb-test")
public class ComboBoxItemIconTest extends MultiBrowserTest {
    @Test
    public void testIconsInComboBox() throws Exception {
        openTestURL();
        ComboBoxElement firstCombo = $(ComboBoxElement.class).first();
        firstCombo.openPopup();
        compareScreen(firstCombo.getSuggestionPopup(), "first-combobox-open");
        // null item not on the list, so use index 1
        firstCombo.selectByText(firstCombo.getPopupSuggestions().get(1));
        compareScreen(firstCombo, "fi-hu-selected");
        ComboBoxElement secondCombo = $(ComboBoxElement.class).get(1);
        secondCombo.openPopup();
        compareScreen(secondCombo.getSuggestionPopup(), "second-combobox-open");
        secondCombo.selectByText(secondCombo.getPopupSuggestions().get(2));
        compareScreen(secondCombo, "fi-au-selected");
        ComboBoxElement thirdCombo = $(ComboBoxElement.class).get(2);
        thirdCombo.openPopup();
        compareScreen(thirdCombo.getSuggestionPopup(), "third-combobox-open");
        thirdCombo.selectByText(thirdCombo.getPopupSuggestions().get(3));
        compareScreen(thirdCombo, "classresource");
    }

    @Test
    public void iconResetOnSelectionCancelByEscape() {
        openTestURL();
        ComboBoxElement cb = $(ComboBoxElement.class).get(1);
        assertSelection(cb, "hu.gif", "Hungary");
        cb.openPopup();
        cb.sendKeys(UP);
        assertSelection(cb, "au.gif", "Australia");
        cb.sendKeys(ESCAPE);
        assertSelection(cb, "hu.gif", "Hungary");
    }

    @Test
    public void iconResetOnSelectionCancelByClickingOutside() {
        openTestURL();
        ComboBoxElement cb = $(ComboBoxElement.class).get(1);
        assertSelection(cb, "hu.gif", "Hungary");
        cb.openPopup();
        cb.sendKeys(UP);
        assertSelection(cb, "au.gif", "Australia");
        findElement(By.tagName("body")).click();
        assertSelection(cb, "hu.gif", "Hungary");
    }
}

