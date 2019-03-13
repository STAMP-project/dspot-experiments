package com.vaadin.tests.components.combobox;


import Keys.ARROW_LEFT;
import Keys.ARROW_RIGHT;
import Keys.END;
import Keys.HOME;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class ComboBoxCaretNavigationTest extends SingleBrowserTest {
    @Test
    public void testHomeAndEndKeys() {
        ComboBoxElement comboBox = $(ComboBoxElement.class).first();
        String text = comboBox.getPopupSuggestions().get(1);
        comboBox.selectByText(text);
        comboBox.sendKeys(HOME);
        assertCaretPosition("Home key didn't work well.", 0, comboBox);
        comboBox.sendKeys(END);
        assertCaretPosition("End key didn't work well.", text.length(), comboBox);
    }

    @Test
    public void testLeftAndRightKeys() {
        ComboBoxElement comboBox = $(ComboBoxElement.class).first();
        String text = comboBox.getPopupSuggestions().get(1);
        comboBox.selectByText(text);
        comboBox.sendKeys(ARROW_LEFT);
        assertCaretPosition("Left Arrow key didn't work well.", ((text.length()) - 1), comboBox);
        comboBox.sendKeys(ARROW_RIGHT);
        assertCaretPosition("Right Arrow key didn't work well.", text.length(), comboBox);
    }

    @Test
    public void testHomeAndRightKeys() {
        ComboBoxElement comboBox = $(ComboBoxElement.class).first();
        String text = comboBox.getPopupSuggestions().get(1);
        comboBox.selectByText(text);
        comboBox.sendKeys(HOME);
        assertCaretPosition("Home key didn't work well.", 0, comboBox);
        comboBox.sendKeys(ARROW_RIGHT);
        assertCaretPosition("Right Arrow key didn't work well.", 1, comboBox);
    }

    @Test
    public void testLeftAndEndKeys() {
        ComboBoxElement comboBox = $(ComboBoxElement.class).first();
        String text = comboBox.getPopupSuggestions().get(1);
        comboBox.selectByText(text);
        comboBox.sendKeys(ARROW_LEFT);
        assertCaretPosition("Left Arrow key didn't work well.", ((text.length()) - 1), comboBox);
        comboBox.sendKeys(END);
        assertCaretPosition("End key didn't work well.", text.length(), comboBox);
    }
}

