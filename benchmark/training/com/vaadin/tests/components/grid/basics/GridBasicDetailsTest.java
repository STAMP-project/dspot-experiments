package com.vaadin.tests.components.grid.basics;


import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.AbstractTB3Test;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;


public class GridBasicDetailsTest extends GridBasicsTest {
    /**
     * The reason to why last item details wasn't selected is that since it will
     * exist only after the viewport has been scrolled into view, we wouldn't be
     * able to scroll that particular details row into view, making tests
     * awkward with two scroll commands back to back.
     */
    private static final int ALMOST_LAST_INDEX = 995;

    private static final String[] OPEN_ALMOST_LAST_ITEM_DETAILS = new String[]{ "Component", "Details", "Open " + (GridBasicDetailsTest.ALMOST_LAST_INDEX) };

    private static final String[] OPEN_FIRST_ITEM_DETAILS = new String[]{ "Component", "Details", "Open First" };

    private static final String[] TOGGLE_FIRST_ITEM_DETAILS = new String[]{ "Component", "Details", "Toggle First" };

    private static final String[] DETAILS_GENERATOR_NULL = new String[]{ "Component", "Details", "Generators", "NULL" };

    private static final String[] DETAILS_GENERATOR_WATCHING = new String[]{ "Component", "Details", "Generators", "\"Watching\"" };

    private static final String[] DETAILS_GENERATOR_PERSISTING = new String[]{ "Component", "Details", "Generators", "Persisting" };

    private static final String[] CHANGE_HIERARCHY = new String[]{ "Component", "Details", "Generators", "- Change Component" };

    @Test(expected = NoSuchElementException.class)
    public void openWithNoGenerator() {
        try {
            getGridElement().getDetails(0);
            Assert.fail("Expected NoSuchElementException");
        } catch (NoSuchElementException ignore) {
            // expected
        }
        try {
            selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);
        } catch (NoSuchElementException e) {
            Assert.fail("Unable to set up details.");
        }
        getGridElement().getDetails(0);
    }

    @Test
    public void openVisiblePopulatedDetails() {
        selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_WATCHING);
        selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);
        Assert.assertNotNull("details should've populated", getGridElement().getDetails(0).findElement(By.className("v-widget")));
    }

    @Test(expected = NoSuchElementException.class)
    public void closeVisiblePopulatedDetails() {
        try {
            selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_WATCHING);
            selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);
            selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);
        } catch (NoSuchElementException e) {
            Assert.fail("Unable to set up details.");
        }
        getGridElement().getDetails(0);
    }

    @Test
    public void openDetailsOutsideOfActiveRange() throws InterruptedException {
        getGridElement().scroll(10000);
        selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_WATCHING);
        selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);
        getGridElement().scroll(0);
        Assert.assertNotNull("details should've been opened", getGridElement().getDetails(0));
    }

    @Test(expected = NoSuchElementException.class)
    public void closeDetailsOutsideOfActiveRange() {
        try {
            selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);
            getGridElement().scroll(10000);
            selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);
            getGridElement().scroll(0);
        } catch (NoSuchElementException e) {
            Assert.fail("Unable to set up details.");
        }
        getGridElement().getDetails(0);
    }

    @Test
    public void componentIsVisibleClientSide() {
        selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_WATCHING);
        selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);
        TestBenchElement details = getGridElement().getDetails(0);
        Assert.assertNotNull("No widget detected inside details", details.findElement(By.className("v-widget")));
    }

    @Test
    public void openingDetailsTwice() {
        selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_WATCHING);
        selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);// open

        selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);// close

        selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);// open

        TestBenchElement details = getGridElement().getDetails(0);
        Assert.assertNotNull("No widget detected inside details", details.findElement(By.className("v-widget")));
    }

    @Test(expected = NoSuchElementException.class)
    public void scrollingDoesNotCreateAFloodOfDetailsRows() {
        try {
            selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_WATCHING);
            // scroll somewhere to hit uncached rows
            getGridElement().scrollToRow(101);
        } catch (NoSuchElementException e) {
            Assert.fail("Unable to set up details.");
        }
        // this should throw
        getGridElement().getDetails(100);
    }

    @Test
    public void openingDetailsOutOfView() {
        getGridElement().scrollToRow(500);
        selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_WATCHING);
        selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);
        getGridElement().scrollToRow(0);
        // if this fails, it'll fail before the assertNotNull
        Assert.assertNotNull("unexpected null details row", getGridElement().getDetails(0));
    }

    @Test
    public void togglingAVisibleDetailsRowWithOneRoundtrip() {
        selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_WATCHING);
        selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);// open

        Assert.assertTrue("Unexpected generator content", getGridElement().getDetails(0).getText().endsWith("(0)"));
        selectMenuPath(GridBasicDetailsTest.TOGGLE_FIRST_ITEM_DETAILS);
        Assert.assertTrue("New component was not displayed in the client", getGridElement().getDetails(0).getText().endsWith("(1)"));
    }

    @Test
    public void almostLastItemIdIsRendered() {
        selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_WATCHING);
        selectMenuPath(GridBasicDetailsTest.OPEN_ALMOST_LAST_ITEM_DETAILS);
        scrollGridVerticallyTo(100000);
        TestBenchElement details = getGridElement().getDetails(GridBasicDetailsTest.ALMOST_LAST_INDEX);
        Assert.assertNotNull(details);
        Assert.assertTrue("Unexpected details content", details.getText().endsWith(((GridBasicDetailsTest.ALMOST_LAST_INDEX) + " (0)")));
    }

    @Test
    public void persistingChangesWorkInDetails() {
        selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_PERSISTING);
        selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);
        Assert.assertEquals("One", getGridElement().getDetails(0).getText());
        selectMenuPath(GridBasicDetailsTest.CHANGE_HIERARCHY);
        Assert.assertEquals("Two", getGridElement().getDetails(0).getText());
    }

    @Test
    public void persistingChangesWorkInDetailsWhileOutOfView() {
        selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_PERSISTING);
        selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);
        Assert.assertEquals("One", getGridElement().getDetails(0).getText());
        scrollGridVerticallyTo(10000);
        selectMenuPath(GridBasicDetailsTest.CHANGE_HIERARCHY);
        scrollGridVerticallyTo(0);
        Assert.assertEquals("Two", getGridElement().getDetails(0).getText());
    }

    @Test
    public void persistingChangesWorkInDetailsWhenNotAttached() {
        selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_PERSISTING);
        selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);
        Assert.assertEquals("One", getGridElement().getDetails(0).getText());
        selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);
        Assert.assertFalse("Details should be detached", getGridElement().isElementPresent(By.vaadin("#details[0]")));
        selectMenuPath(GridBasicDetailsTest.CHANGE_HIERARCHY);
        selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);
        Assert.assertEquals("Two", getGridElement().getDetails(0).getText());
    }

    @Test
    public void swappingDetailsGenerators_noDetailsShown() {
        selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_WATCHING);
        selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_NULL);
        Assert.assertFalse("Got some errors", $(NotificationElement.class).exists());
    }

    @Test
    public void swappingDetailsGenerators_shownDetails() {
        selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_PERSISTING);
        selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);
        Assert.assertTrue("Details should contain 'One' at first", getGridElement().getDetails(0).getText().contains("One"));
        selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_WATCHING);
        Assert.assertFalse("Details should contain 'Watching' after swapping generator", getGridElement().getDetails(0).getText().contains("Watching"));
    }

    @Test
    public void swappingDetailsGenerators_whileDetailsScrolledOut_showNever() {
        scrollGridVerticallyTo(1000);
        selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);
        selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_WATCHING);
        Assert.assertFalse("Got some errors", $(NotificationElement.class).exists());
    }

    @Test
    public void swappingDetailsGenerators_whileDetailsScrolledOut_showAfter() {
        scrollGridVerticallyTo(1000);
        selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_PERSISTING);
        selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);
        selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_WATCHING);
        scrollGridVerticallyTo(0);
        Assert.assertFalse("Got some errors", $(NotificationElement.class).exists());
        Assert.assertNotNull("Could not find a details", getGridElement().getDetails(0));
    }

    @Test
    public void swappingDetailsGenerators_whileDetailsScrolledOut_showBefore() {
        selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_PERSISTING);
        selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);
        selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_WATCHING);
        scrollGridVerticallyTo(1000);
        Assert.assertFalse("Got some errors", $(NotificationElement.class).exists());
        Assert.assertNotNull("Could not find a details", getGridElement().getDetails(0));
    }

    @Test
    public void swappingDetailsGenerators_whileDetailsScrolledOut_showBeforeAndAfter() {
        selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_PERSISTING);
        selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);
        selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_WATCHING);
        scrollGridVerticallyTo(1000);
        scrollGridVerticallyTo(0);
        Assert.assertFalse("Got some errors", $(NotificationElement.class).exists());
        Assert.assertNotNull("Could not find a details", getGridElement().getDetails(0));
    }

    @Test
    public void noAssertErrorsOnEmptyDetailsAndScrollDown() {
        selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);
        scrollGridVerticallyTo(500);
        Assert.assertFalse(logContainsText("AssertionError"));
    }

    @Test
    public void noAssertErrorsOnPopulatedDetailsAndScrollDown() {
        selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_WATCHING);
        selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);
        scrollGridVerticallyTo(500);
        Assert.assertFalse(logContainsText("AssertionError"));
    }

    @Test
    public void testOpenDetailsWithItemClickHandler() {
        selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_PERSISTING);
        selectMenuPath("Component", "State", "Item click listener");
        Assert.assertTrue("No item click listener registered", logContainsText("Registered an item click listener."));
        getGridElement().getCell(0, 1).click();
        Assert.assertTrue("Details should open on click", getGridElement().getDetails(0).getText().contains("One"));
        Assert.assertTrue("Item click listener should log itself", logContainsText("Item click on row 0, Column 'Column 1'"));
        selectMenuPath("Component", "State", "Item click listener");
        Assert.assertTrue("No removal of item click listener logged", logContainsText("Removed an item click listener."));
        getGridElement().getCell(0, 1).click();
        Assert.assertTrue("Details should remain open, no item click listener to hide it", getGridElement().getDetails(0).getText().contains("One"));
    }

    @Test
    public void detailsSizeCorrectAfterScrolling() {
        selectMenuPath(GridBasicDetailsTest.DETAILS_GENERATOR_PERSISTING);
        selectMenuPath(GridBasicDetailsTest.OPEN_FIRST_ITEM_DETAILS);
        // Scroll to request next range
        getGridElement().scrollToRow(21);
        getGridElement().scrollToRow(0);
        AbstractTB3Test.assertGreater("Details row should have correct height", getGridElement().getDetails(0).getSize().getHeight(), 30);
        // Scroll outside of cached rows
        getGridElement().scrollToRow(101);
        getGridElement().scrollToRow(0);
        AbstractTB3Test.assertGreater("Details row should have correct height", getGridElement().getDetails(0).getSize().getHeight(), 30);
    }
}

