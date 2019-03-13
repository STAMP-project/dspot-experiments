package com.vaadin.tests.components.datefield;


import InlineDateFieldReadOnly.HOUR;
import InlineDateFieldReadOnly.MIN;
import InlineDateFieldReadOnly.SEC;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class InlineDateFieldReadOnlyTest extends MultiBrowserTest {
    @Test
    public void minutesAndSecondsAreRenderedCorrectly() {
        openTestURL();
        WebElement divTime = findElement(By.className("v-datefield-time"));
        List<WebElement> labels = divTime.findElements(By.className("v-label"));
        TestCase.assertEquals(5, labels.size());
        // At positions 1 and 3 the delimeter label is set
        TestCase.assertEquals(HOUR, convertToInt(labels.get(0).getText()));
        TestCase.assertEquals(MIN, convertToInt(labels.get(2).getText()));
        TestCase.assertEquals(SEC, convertToInt(labels.get(4).getText()));
    }
}

