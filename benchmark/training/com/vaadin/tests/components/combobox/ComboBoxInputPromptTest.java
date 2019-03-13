package com.vaadin.tests.components.combobox;


import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class ComboBoxInputPromptTest extends MultiBrowserTest {
    @Test
    public void promptIsHiddenForDisabledAndReadonly() {
        openTestURL();
        ComboBoxElement normalComboBox = getComboBoxWithCaption("Normal");
        ComboBoxElement disabledComboBox = getComboBoxWithCaption("Disabled");
        ComboBoxElement readOnlyComboBox = getComboBoxWithCaption("Read-only");
        MatcherAssert.assertThat(getInputPromptValue(normalComboBox), Matchers.is("Normal input prompt"));
        MatcherAssert.assertThat(getInputPromptValue(disabledComboBox), Matchers.isEmptyString());
        MatcherAssert.assertThat(getInputPromptValue(readOnlyComboBox), Matchers.isEmptyString());
        toggleDisabledAndReadonly();
        MatcherAssert.assertThat(getInputPromptValue(disabledComboBox), Matchers.is("Disabled input prompt"));
        MatcherAssert.assertThat(getInputPromptValue(readOnlyComboBox), Matchers.is("Read-only input prompt"));
        toggleDisabledAndReadonly();
        MatcherAssert.assertThat(getInputPromptValue(disabledComboBox), Matchers.isEmptyString());
        MatcherAssert.assertThat(getInputPromptValue(readOnlyComboBox), Matchers.isEmptyString());
    }
}

