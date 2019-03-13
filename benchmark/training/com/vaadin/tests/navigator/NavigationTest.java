package com.vaadin.tests.navigator;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TableRowElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class NavigationTest extends SingleBrowserTest {
    @Test
    public void testNavigateToSameViewWithDifferentParameters() {
        openTestURL();
        ButtonElement listButton = $(ButtonElement.class).caption("Navigate to list").first();
        listButton.click();
        TableElement table = $(TableElement.class).first();
        Assert.assertEquals("Unexpected navigation message", "2. Navigated to ListView without params", getLogRow(0));
        Assert.assertFalse("Table should not have contents", table.isElementPresent(By.vaadin("#row[0]")));
        listButton.click();
        Assert.assertEquals("Should not navigate to same view again.", "2. Navigated to ListView without params", getLogRow(0));
        $(TextFieldElement.class).first().sendKeys("foo=1");
        listButton.click();
        Assert.assertEquals("Should not navigate to same view again.", "3. Navigated to ListView with params foo=1", getLogRow(0));
        Assert.assertTrue("Table should have content", table.isElementPresent(By.vaadin("#row[0]")));
        TableRowElement row = table.getRow(0);
        Assert.assertEquals("Unexpected row content", "foo", row.getCell(0).getText());
        Assert.assertEquals("Unexpected row content", "1", row.getCell(1).getText());
    }
}

