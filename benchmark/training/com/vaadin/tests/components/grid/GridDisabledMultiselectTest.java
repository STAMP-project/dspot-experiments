package com.vaadin.tests.components.grid;


import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class GridDisabledMultiselectTest extends MultiBrowserTest {
    @Test
    public void checkBoxesAreDisabledAfterModeChange() {
        disable();
        setMultiselect();
        MatcherAssert.assertThat(getSelectAllCheckBox().isEnabled(), Is.is(false));
        MatcherAssert.assertThat(getFirstSelectCheckBox().isEnabled(), Is.is(false));
    }

    @Test
    public void checkBoxesAreDisabledAfterDisabled() {
        setMultiselect();
        MatcherAssert.assertThat(getSelectAllCheckBox().isEnabled(), Is.is(true));
        MatcherAssert.assertThat(getFirstSelectCheckBox().isEnabled(), Is.is(true));
        disable();
        MatcherAssert.assertThat(getSelectAllCheckBox().isEnabled(), Is.is(false));
        MatcherAssert.assertThat(getFirstSelectCheckBox().isEnabled(), Is.is(false));
    }

    @Test
    public void parentSpanCannotBeClickedWhenDisabled() {
        setMultiselect();
        disable();
        WebElement firstCheckBoxSpan = findElements(By.cssSelector("span")).get(1);
        new org.openqa.selenium.interactions.Actions(driver).moveToElement(firstCheckBoxSpan, 1, 1).click().perform();
        MatcherAssert.assertThat(getFirstSelectCheckBox().isSelected(), Is.is(false));
    }
}

