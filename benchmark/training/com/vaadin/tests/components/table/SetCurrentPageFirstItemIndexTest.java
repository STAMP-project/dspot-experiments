package com.vaadin.tests.components.table;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Ignore;
import org.junit.Test;


// Enable after #15286 is fixed.
@Ignore
public class SetCurrentPageFirstItemIndexTest extends MultiBrowserTest {
    @Test
    public void currentPageIndexChangesTwice() {
        openTestURL();
        ButtonElement button = $(ButtonElement.class).first();
        button.click();// change to 20

        button.click();// change to 5

        // When failing, the index stays on 20.
        assertThatRowIsVisible(5);
    }
}

