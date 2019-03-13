package com.vaadin.tests.components.combobox;


import Keys.ARROW_UP;
import Keys.ENTER;
import Keys.TAB;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class ComboBoxEmptyItemsKeyboardNavigationTest extends SingleBrowserTest {
    @Test
    public void navigatingUpOnAnEmptyMenuDoesntThrowErrors() {
        setDebug(true);
        openTestURL();
        ComboBoxElement combobox = $(ComboBoxElement.class).first();
        combobox.sendKeys("a", ARROW_UP);
        List<WebElement> errors = findElements(By.className("SEVERE"));
        MatcherAssert.assertThat(errors, empty());
    }

    @Test
    public void selectingUsingEnterInAnEmptyMenu() {
        setDebug(true);
        openTestURL();
        ComboBoxElement combobox = $(ComboBoxElement.class).first();
        combobox.sendKeys("a", ENTER);
        List<WebElement> errors = findElements(By.className("SEVERE"));
        MatcherAssert.assertThat(errors, empty());
        assertPopupClosed(combobox);
    }

    @Test
    public void selectingUsingTabInAnEmptyMenu() {
        setDebug(true);
        openTestURL();
        ComboBoxElement combobox = $(ComboBoxElement.class).first();
        // The joy of testing, one tab should be enough but is not (it is
        // locally), two tabs does the trick for PhantomJS on the cluster...
        combobox.sendKeys("abc", TAB, TAB);
        List<WebElement> errors = findElements(By.className("SEVERE"));
        MatcherAssert.assertThat(errors, empty());
        assertPopupClosed(combobox);
    }
}

