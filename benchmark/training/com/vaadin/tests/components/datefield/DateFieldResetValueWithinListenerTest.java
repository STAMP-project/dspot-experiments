package com.vaadin.tests.components.datefield;


import DateFieldResetValueWithinListener.beforeInitialValue;
import DateFieldResetValueWithinListener.initialValue;
import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class DateFieldResetValueWithinListenerTest extends MultiBrowserTest {
    @Test
    public void testValueReassigned() {
        openTestURL();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("d.M.yyyy", Locale.ENGLISH);
        String textBefore = findElement(By.tagName("input")).getAttribute("value");
        Assert.assertEquals(initialValue.format(format), textBefore);
        findElement(By.id("setValueButton")).click();
        String textAfter = findElement(By.tagName("input")).getAttribute("value");
        Assert.assertEquals(beforeInitialValue.format(format), textAfter);
    }
}

