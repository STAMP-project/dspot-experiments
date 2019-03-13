package com.vaadin.tests.components.datefield;


import com.vaadin.tests.tb3.MultiBrowserTest;
import junit.framework.TestCase;
import org.junit.Test;


public class DateTimeFieldAfterReadOnlyTest extends MultiBrowserTest {
    @Test
    public void readOnlyDateFieldPopupShouldNotOpen() {
        openTestURL();
        toggleReadOnly();
        openPopup();
        TestCase.assertEquals(2, numberOfSelectsField());
    }
}

