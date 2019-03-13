package com.vaadin.tests.components.textfield;


import SelectionAndCursorPosition.DEFAULT_TEXT;
import SelectionAndCursorPosition.TEXTFIELD_ID;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class SelectionAndCursorPositionTest extends SingleBrowserTest {
    private static final int DEFAULT_TEXT_LENGTH = DEFAULT_TEXT.length();

    private WebElement textField;

    @Test
    public void testSelection() {
        openTestURL();
        textField = findElement(By.id(TEXTFIELD_ID));
        // Select all
        getSelectAll().click();
        assertSelection(0, SelectionAndCursorPositionTest.DEFAULT_TEXT_LENGTH);
        // Select range
        setSelectionRange(10, 5);
        assertSelection(10, 5);
        // Test for index out of bounds
        setSelectionRange(0, SelectionAndCursorPositionTest.DEFAULT_TEXT_LENGTH);
        assertSelection(0, SelectionAndCursorPositionTest.DEFAULT_TEXT_LENGTH);
        setSelectionRange(0, ((SelectionAndCursorPositionTest.DEFAULT_TEXT_LENGTH) + 1));
        assertSelection(0, SelectionAndCursorPositionTest.DEFAULT_TEXT_LENGTH);
        setSelectionRange(1, SelectionAndCursorPositionTest.DEFAULT_TEXT_LENGTH);
        assertSelection(1, ((SelectionAndCursorPositionTest.DEFAULT_TEXT_LENGTH) - 1));
        setSelectionRange(((SelectionAndCursorPositionTest.DEFAULT_TEXT_LENGTH) - 1), 2);
        assertSelection(((SelectionAndCursorPositionTest.DEFAULT_TEXT_LENGTH) - 1), 1);
        // Cursor position
        setCursorPosition(0);
        assertCursorPosition(0);
    }
}

