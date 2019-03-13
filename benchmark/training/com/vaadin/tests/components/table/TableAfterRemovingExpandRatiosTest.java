package com.vaadin.tests.components.table;


import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * Tests checks that column width is restored after removing expand ratios.
 *
 * @author Vaadin Ltd
 */
public class TableAfterRemovingExpandRatiosTest extends MultiBrowserTest {
    private WebElement initialHeader;

    private WebElement expandedHeader;

    private WebElement expandButton;

    private WebElement unExpandButton;

    @Test
    public void testRemovingExpandRatios() {
        clickAndWait(expandButton);
        Assert.assertThat("Column widths should not be equal after expanding", initialHeader.getSize().getWidth(), CoreMatchers.not(expandedHeader.getSize().getWidth()));
        clickAndWait(unExpandButton);
        Assert.assertThat("Column widths should be equal after unexpanding", initialHeader.getSize().getWidth(), CoreMatchers.is(expandedHeader.getSize().getWidth()));
    }

    @Test
    public void testRemovingExpandRatiosAfterAddingNewItem() {
        WebElement addItemButton = getDriver().findElement(By.id("add-button"));
        clickAndWait(expandButton);
        clickAndWait(addItemButton);
        clickAndWait(unExpandButton);
        Assert.assertThat("Column widths should be equal after adding item and unexpanding", initialHeader.getSize().getWidth(), CoreMatchers.is(expandedHeader.getSize().getWidth()));
    }
}

