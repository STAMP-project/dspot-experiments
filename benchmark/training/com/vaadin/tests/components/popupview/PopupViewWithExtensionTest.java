package com.vaadin.tests.components.popupview;


import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * Popup view with extension should not throw an exception.
 *
 * @since 7.2
 * @author Vaadin Ltd
 */
public class PopupViewWithExtensionTest extends MultiBrowserTest {
    @Test
    public void testPopupView() {
        setDebug(true);
        openTestURL();
        WebElement view = driver.findElement(By.className("v-popupview"));
        view.click();
        assertNoErrorNotifications();
    }
}

