package com.vaadin.tests.components.grid;


import Keys.SHIFT;
import Keys.SPACE;
import Keys.TAB;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;


public class GridComponentsTest extends MultiBrowserTest {
    @Test
    public void testReuseTextFieldOnScroll() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        editTextFieldInCell(grid, 0, 1);
        // Scroll out of view port
        grid.getRow(900);
        // Scroll back
        grid.getRow(0);
        WebElement textField = grid.getCell(0, 1).findElement(By.tagName("input"));
        Assert.assertEquals("TextField value was reset", "Foo", textField.getAttribute("value"));
        Assert.assertTrue("No mention in the log", logContainsText("1. Reusing old text field for: Row 0"));
    }

    @Test
    public void testReuseTextFieldOnSelect() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        editTextFieldInCell(grid, 1, 1);
        // Select row
        grid.getCell(1, 0).click();
        WebElement textField = grid.getCell(1, 1).findElement(By.tagName("input"));
        Assert.assertEquals("TextField value was reset", "Foo", textField.getAttribute("value"));
        Assert.assertTrue("No mention in the log", logContainsText("1. Reusing old text field for: Row 1"));
    }

    @Test
    public void testReplaceData() {
        openTestURL();
        assertRowExists(5, "Row 5");
        $(ButtonElement.class).caption("Reset data").first().click();
        assertRowExists(5, "Row 1005");
    }

    @Test
    public void testTextFieldSize() {
        openTestURL();
        GridCellElement cell = $(GridElement.class).first().getCell(0, 1);
        int cellWidth = cell.getSize().getWidth();
        int fieldWidth = cell.findElement(By.tagName("input")).getSize().getWidth();
        // padding left and right, +1 to fix sub pixel issues
        int padding = (18 * 2) + 1;
        int extraSpace = Math.abs((fieldWidth - cellWidth));
        Assert.assertTrue(((("Too much unused space in cell. Expected: " + padding) + " Actual: ") + extraSpace), (extraSpace <= padding));
    }

    @Test
    public void testRow5() {
        openTestURL();
        assertRowExists(5, "Row 5");
    }

    @Test
    public void testRow0() {
        openTestURL();
        assertRowExists(0, "Row 0");
        Assert.assertEquals("Grid row height is not what it should be", 40, $(GridElement.class).first().getRow(0).getSize().getHeight());
    }

    @Test
    public void testRow999() {
        openTestURL();
        assertRowExists(999, "Row 999");
    }

    @Test
    public void testRow30() {
        openTestURL();
        Stream.of(30, 130, 230, 330).forEach(this::assertNoButton);
        IntStream.range(300, 310).forEach(this::assertNoButton);
    }

    @Test(expected = AssertionError.class)
    public void testRow31() {
        openTestURL();
        // There is a button on row 31. This should fail.
        assertNoButton(31);
    }

    @Test
    public void testHeaders() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        GridCellElement headerCell = grid.getHeaderCell(0, 0);
        Assert.assertTrue("First header should contain a Label", headerCell.isElementPresent(LabelElement.class));
        Assert.assertEquals("Label", headerCell.$(LabelElement.class).first().getText());
        Assert.assertFalse("Second header should not contain a component", grid.getHeaderCell(0, 1).isElementPresent(LabelElement.class));
        Assert.assertEquals("Other Components", grid.getHeaderCell(0, 1).getText());
    }

    @Test
    public void testSelectRowByClickingLabel() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        Assert.assertFalse("Row should not be initially selected", grid.getRow(0).isSelected());
        $(LabelElement.class).first().click(10, 10, new Keys[0]);
        Assert.assertTrue("Row should be selected", grid.getRow(0).isSelected());
    }

    @Test
    public void testRowNotSelectedFromHeaderOrFooter() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        $(LabelElement.class).first().click(10, 10, new Keys[0]);
        Assert.assertTrue("Row 4 should be selected", grid.getRow(4).isSelected());
        TextFieldElement headerTextField = $(TextFieldElement.class).first();
        headerTextField.sendKeys(SPACE);
        Assert.assertFalse("Row 1 should not be selected", grid.getRow(1).isSelected());
        Assert.assertTrue("Row 4 should still be selected", grid.getRow(4).isSelected());
        TextFieldElement footerTextField = $(TextFieldElement.class).first();
        footerTextField.sendKeys(SPACE);
        Assert.assertFalse("Row 0 should not be selected", grid.getRow(0).isSelected());
        Assert.assertTrue("Row 4 should still be selected", grid.getRow(4).isSelected());
    }

    @Test
    public void testTabNavigation() {
        Assume.assumeFalse("Firefox has issues with Shift", BrowserUtil.isFirefox(getDesiredCapabilities()));
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        WebElement resizeHandle = grid.getHeaderCell(0, 0).findElement(By.cssSelector("div.v-grid-column-resize-handle"));
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(resizeHandle).clickAndHold().moveByOffset(440, 0).release().perform();
        // Scroll to end
        grid.getCell(0, 2);
        int scrollMax = getScrollLeft(grid);
        Assert.assertTrue("Width of the grid too narrow, no scroll bar", (scrollMax > 0));
        // Scroll to start
        grid.getHorizontalScroller().scrollLeft(0);
        Assert.assertEquals("Grid should scrolled to the start for this part of the test..", 0, getScrollLeft(grid));
        // Focus TextField in second column
        WebElement textField = grid.getCell(0, 1).findElement(By.tagName("input"));
        textField.click();
        // Navigate to currently out of viewport Button
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(TAB).perform();
        Assert.assertEquals("Grid should be scrolled to the end", scrollMax, getScrollLeft(grid));
        // Navigate back to fully visible TextField
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(Keys.chord(SHIFT, TAB)).perform();
        Assert.assertEquals("Grid should not scroll when focusing the text field again. ", scrollMax, getScrollLeft(grid));
        // Navigate to out of viewport TextField in Header
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(Keys.chord(SHIFT, TAB)).perform();
        Assert.assertEquals("Focus should be in TextField in Header", "headerField", getFocusedElement().getAttribute("id"));
        Assert.assertEquals("Grid should've scrolled back to start.", 0, getScrollLeft(grid));
        // Focus button in last visible row of Grid
        grid.getCell(6, 2).findElement(By.id("row_6")).click();
        // Navigate to currently out of viewport TextField on Row 7
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(TAB).perform();
        int scrollTopRow7 = Integer.parseInt(grid.getVerticalScroller().getAttribute("scrollTop"));
        Assert.assertTrue("Grid should be scrolled to show row 7", (scrollTopRow7 > 0));
        // Navigate to currently out of viewport TextField on Row 8
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(TAB, TAB).perform();
        Assert.assertTrue("Grid should be scrolled to show row 7", ((Integer.parseInt(grid.getVerticalScroller().getAttribute("scrollTop"))) > scrollTopRow7));
        // Focus button in last row of Grid
        grid.getCell(999, 2).findElement(By.id("row_999")).click();
        // Navigate to out of viewport TextField in Footer
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(TAB).perform();
        Assert.assertEquals("Focus should be in TextField in Footer", "footerField", getFocusedElement().getAttribute("id"));
        Assert.assertEquals("Grid should've scrolled horizontally back to start.", 0, getScrollLeft(grid));
    }
}

