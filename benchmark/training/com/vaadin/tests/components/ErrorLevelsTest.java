package com.vaadin.tests.components;


import StyleConstants.STYLE_NAME_ERROR_INDICATOR;
import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.AccordionElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.FormLayoutElement;
import com.vaadin.testbench.elements.LinkElement;
import com.vaadin.testbench.elements.NativeButtonElement;
import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.testbench.elements.TwinColSelectElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import java.util.List;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class ErrorLevelsTest extends SingleBrowserTest {
    private ComboBoxElement errorLevelSelector;

    @Test
    public void testErrorIndicatorsClassName() {
        ErrorLevel errorLevel = ErrorLevel.WARNING;
        selectErrorLevel(errorLevel);
        List<WebElement> errorIndicators = findElements(By.className(STYLE_NAME_ERROR_INDICATOR));
        for (WebElement errorIndicator : errorIndicators) {
            assertHasRightClassNames(errorIndicator, STYLE_NAME_ERROR_INDICATOR, errorLevel);
        }
    }

    @Test
    public void testComponentsClassName() {
        ErrorLevel errorLevel = ErrorLevel.WARNING;
        selectErrorLevel(errorLevel);
        // Button
        ButtonElement buttonElement = $(ButtonElement.class).first();
        assertHasRightClassNames(buttonElement, "v-button-error", errorLevel);
        // Native button
        NativeButtonElement nativeButtonElement = $(NativeButtonElement.class).first();
        assertHasRightClassNames(nativeButtonElement, "v-nativebutton-error", errorLevel);
        // Link
        LinkElement linkElement = $(LinkElement.class).first();
        assertHasRightClassNames(linkElement, "v-link-error", errorLevel);
        // Combo box
        ComboBoxElement comboBoxElement = $(ComboBoxElement.class).get(1);
        assertHasRightClassNames(comboBoxElement, "v-filterselect-error", errorLevel);
        // Date field
        DateFieldElement dateFieldElement = $(DateFieldElement.class).first();
        assertHasRightClassNames(dateFieldElement, "v-datefield-error", errorLevel);
        // Checkbox
        CheckBoxElement checkBoxElement = $(CheckBoxElement.class).first();
        assertHasRightClassNames(checkBoxElement, "v-checkbox-error", errorLevel);
        // Tab sheet
        TabSheetElement tabSheetElement = $(TabSheetElement.class).first();
        assertHasRightClassNames(tabSheetElement, "v-tabsheet-error", errorLevel);
        // Accordion
        AccordionElement accordionElement = $(AccordionElement.class).first();
        assertHasRightClassNames(accordionElement, "v-accordion-error", errorLevel);
        // Form layout
        FormLayoutElement formLayoutElement = $(FormLayoutElement.class).first();
        assertHasRightClassNames(formLayoutElement, "v-formlayout-error", errorLevel);
        // Panel
        PanelElement panelElement = $(PanelElement.class).first();
        assertHasRightClassNames(panelElement, "v-panel-error", errorLevel);
        // Twin col select
        TwinColSelectElement twinColSelectElement = $(TwinColSelectElement.class).first();
        assertHasRightClassNames(twinColSelectElement, "v-select-twincol-error", errorLevel);
    }
}

