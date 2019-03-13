package com.vaadin.tests.components.combobox;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class ComboBoxSuggestionPageLengthTest extends MultiBrowserTest {
    @Test
    public void testSuggestionsPageLength0() {
        openTestURL();
        WebElement textboxPageLength0 = $(ComboBoxElement.class).first().findElement(By.tagName("input"));
        textboxPageLength0.sendKeys("c");
        assertSuggestions("abc", "cde");
    }

    @Test
    public void testSuggestionsPageLength2() {
        openTestURL();
        WebElement textboxPageLength2 = $(ComboBoxElement.class).get(1).findElement(By.tagName("input"));
        textboxPageLength2.sendKeys("e");
        assertSuggestions("cde", "efg");
    }
}

