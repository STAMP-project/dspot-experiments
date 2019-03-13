package com.vaadin.tests.themes.valo;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class FormLayoutInsideTableTest extends MultiBrowserTest {
    @Test
    public void nestedItemHasBorderTop() {
        openTestURL();
        List<WebElement> formLayoutRows = findElements(By.cssSelector("tr.v-formlayout-row"));
        WebElement secondNestedRow = formLayoutRows.get(1);
        WebElement td = secondNestedRow.findElement(By.tagName("td"));
        MatcherAssert.assertThat(td.getCssValue("border-top-width"), CoreMatchers.is("1px"));
    }
}

