package com.vaadin.tests.elements.combobox;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


/**
 * Validates ComboBox.selectByText(String s) works properly if input String s
 * contains parentheses
 */
public class SelectByTextTest extends MultiBrowserTest {
    @Test
    public void selectByParenthesesOnly() {
        selectAndAssertValue("(");
    }

    @Test
    public void selectByStartingParentheses() {
        selectAndAssertValue("(Value");
    }

    @Test
    public void selectByFinishingParentheses() {
        selectAndAssertValue("Value(");
    }

    @Test
    public void selectByRegularParentheses() {
        selectAndAssertValue("Value(i)");
    }

    @Test
    public void selectByComplexParenthesesCase() {
        selectAndAssertValue("((Test ) selectByTest() method(with' parentheses)((");
    }

    @Test
    public void selectSharedPrefixOption() {
        for (String text : new String[]{ "Value 2", "Value 22", "Value 222" }) {
            selectAndAssertValue(text);
        }
    }
}

