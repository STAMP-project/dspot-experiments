package com.vaadin.tests.components.textfield;


import TextFieldTestCursorPosition.CURSOR_POS_TF;
import TextFieldTestCursorPosition.RANGE_LENGTH_TF;
import TextFieldTestCursorPosition.valueLength;
import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static TextFieldTestCursorPosition.valueLength;


public class TextFieldTestCursorPositionTest extends MultiBrowserTest {
    private WebElement textFieldCheckCursor;

    private WebElement textFieldCheckRang;

    @Test
    public void testSelection() {
        openTestURL();
        textFieldCheckCursor = findElement(By.id(CURSOR_POS_TF));
        textFieldCheckRang = findElement(By.id(RANGE_LENGTH_TF));
        // Range selected correctly
        setSelectionRange();
        assertSelection(((valueLength) / 2), valueLength, textFieldCheckRang);
        // Cursor position
        setCursorPosition();
        assertCursorPosition(valueLength);
    }
}

