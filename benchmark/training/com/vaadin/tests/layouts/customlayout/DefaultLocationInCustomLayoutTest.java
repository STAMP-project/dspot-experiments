package com.vaadin.tests.layouts.customlayout;


import DefaultLocationInCustomLayout.BUTTON_ID;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.openqa.selenium.By;


public class DefaultLocationInCustomLayoutTest extends MultiBrowserTest {
    @Test
    public void buttonExistsInLayout() {
        openTestURL();
        // We don't use TestBench's ElementQuery here because we need to check
        // the DOM for buttons existence.
        MatcherAssert.assertThat(driver.findElements(By.id(BUTTON_ID)).size(), CoreMatchers.is(1));
    }
}

