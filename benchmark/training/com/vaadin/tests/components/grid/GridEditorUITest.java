package com.vaadin.tests.components.grid;


import Keys.ESCAPE;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


@TestCategory("grid")
public class GridEditorUITest extends MultiBrowserTest {
    @Test
    public void testEditor() {
        Assert.assertFalse("Sanity check", isElementPresent(PasswordFieldElement.class));
        openEditor(5);
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ESCAPE).perform();
        openEditor(10);
        Assert.assertTrue("Editor should be opened with a password field", isElementPresent(PasswordFieldElement.class));
        Assert.assertFalse("Notification was present", isElementPresent(NotificationElement.class));
    }

    @Test
    public void savingResetsSortingIndicator() {
        GridCellElement headerCell = getHeaderCell(0, 0);
        headerCell.click();
        openEditor(1);
        saveEditor();
        MatcherAssert.assertThat(headerCell.getAttribute("class"), CoreMatchers.not(CoreMatchers.containsString("sort-")));
    }
}

