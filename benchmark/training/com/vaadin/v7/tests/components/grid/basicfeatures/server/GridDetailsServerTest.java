package com.vaadin.v7.tests.components.grid.basicfeatures.server;


import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;


public class GridDetailsServerTest extends GridBasicFeaturesTest {
    /**
     * The reason to why last item details wasn't selected is that since it will
     * exist only after the viewport has been scrolled into view, we wouldn't be
     * able to scroll that particular details row into view, making tests
     * awkward with two scroll commands back to back.
     */
    private static final int ALMOST_LAST_INDEX = 995;

    private static final String[] OPEN_ALMOST_LAST_ITEM_DETAILS = new String[]{ "Component", "Details", "Open " + (GridDetailsServerTest.ALMOST_LAST_INDEX) };

    private static final String[] OPEN_FIRST_ITEM_DETAILS = new String[]{ "Component", "Details", "Open firstItemId" };

    private static final String[] TOGGLE_FIRST_ITEM_DETAILS = new String[]{ "Component", "Details", "Toggle firstItemId" };

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
        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);
        getGridElement().getDetails(0);
    }

    @Test
    public void openVisiblePopulatedDetails() {
        selectMenuPath(GridDetailsServerTest.DETAILS_GENERATOR_WATCHING);
        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);
        Assert.assertNotNull("details should've populated", getGridElement().getDetails(0).findElement(By.className("v-widget")));
    }

    @Test(expected = NoSuchElementException.class)
    public void closeVisiblePopulatedDetails() {
        selectMenuPath(GridDetailsServerTest.DETAILS_GENERATOR_WATCHING);
        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);
        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);
        getGridElement().getDetails(0);
    }

    @Test
    public void openDetailsOutsideOfActiveRange() throws InterruptedException {
        getGridElement().scroll(10000);
        selectMenuPath(GridDetailsServerTest.DETAILS_GENERATOR_WATCHING);
        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);
        getGridElement().scroll(0);
        Thread.sleep(50);
        Assert.assertNotNull("details should've been opened", getGridElement().getDetails(0));
    }

    @Test(expected = NoSuchElementException.class)
    public void closeDetailsOutsideOfActiveRange() {
        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);
        getGridElement().scroll(10000);
        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);
        getGridElement().scroll(0);
        getGridElement().getDetails(0);
    }

    @Test
    public void componentIsVisibleClientSide() {
        selectMenuPath(GridDetailsServerTest.DETAILS_GENERATOR_WATCHING);
        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);
        TestBenchElement details = getGridElement().getDetails(0);
        Assert.assertNotNull("No widget detected inside details", details.findElement(By.className("v-widget")));
    }

    @Test
    public void openingDetailsTwice() {
        selectMenuPath(GridDetailsServerTest.DETAILS_GENERATOR_WATCHING);
        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);// open

        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);// close

        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);// open

        TestBenchElement details = getGridElement().getDetails(0);
        Assert.assertNotNull("No widget detected inside details", details.findElement(By.className("v-widget")));
    }

    @Test(expected = NoSuchElementException.class)
    public void scrollingDoesNotCreateAFloodOfDetailsRows() {
        selectMenuPath(GridDetailsServerTest.DETAILS_GENERATOR_WATCHING);
        // scroll somewhere to hit uncached rows
        getGridElement().scrollToRow(101);
        // this should throw
        getGridElement().getDetails(100);
    }

    @Test
    public void openingDetailsOutOfView() {
        getGridElement().scrollToRow(500);
        selectMenuPath(GridDetailsServerTest.DETAILS_GENERATOR_WATCHING);
        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);
        getGridElement().scrollToRow(0);
        // if this fails, it'll fail before the assertNotNull
        Assert.assertNotNull("unexpected null details row", getGridElement().getDetails(0));
    }

    @Test
    public void togglingAVisibleDetailsRowWithOneRoundtrip() {
        selectMenuPath(GridDetailsServerTest.DETAILS_GENERATOR_WATCHING);
        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);// open

        Assert.assertTrue("Unexpected generator content", getGridElement().getDetails(0).getText().endsWith("(0)"));
        selectMenuPath(GridDetailsServerTest.TOGGLE_FIRST_ITEM_DETAILS);
        Assert.assertTrue("New component was not displayed in the client", getGridElement().getDetails(0).getText().endsWith("(1)"));
    }

    @Test
    public void almostLastItemIdIsRendered() {
        selectMenuPath(GridDetailsServerTest.DETAILS_GENERATOR_WATCHING);
        selectMenuPath(GridDetailsServerTest.OPEN_ALMOST_LAST_ITEM_DETAILS);
        scrollGridVerticallyTo(100000);
        TestBenchElement details = getGridElement().getDetails(GridDetailsServerTest.ALMOST_LAST_INDEX);
        Assert.assertNotNull(details);
        Assert.assertTrue("Unexpected details content", details.getText().endsWith(((GridDetailsServerTest.ALMOST_LAST_INDEX) + " (0)")));
    }

    @Test
    public void persistingChangesWorkInDetails() {
        selectMenuPath(GridDetailsServerTest.DETAILS_GENERATOR_PERSISTING);
        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);
        Assert.assertEquals("One", getGridElement().getDetails(0).getText());
        selectMenuPath(GridDetailsServerTest.CHANGE_HIERARCHY);
        Assert.assertEquals("Two", getGridElement().getDetails(0).getText());
    }

    @Test
    public void persistingChangesWorkInDetailsWhileOutOfView() {
        selectMenuPath(GridDetailsServerTest.DETAILS_GENERATOR_PERSISTING);
        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);
        Assert.assertEquals("One", getGridElement().getDetails(0).getText());
        scrollGridVerticallyTo(10000);
        selectMenuPath(GridDetailsServerTest.CHANGE_HIERARCHY);
        scrollGridVerticallyTo(0);
        Assert.assertEquals("Two", getGridElement().getDetails(0).getText());
    }

    @Test
    public void persistingChangesWorkInDetailsWhenNotAttached() {
        selectMenuPath(GridDetailsServerTest.DETAILS_GENERATOR_PERSISTING);
        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);
        Assert.assertEquals("One", getGridElement().getDetails(0).getText());
        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);
        Assert.assertFalse("Details should be detached", getGridElement().isElementPresent(By.vaadin("#details[0]")));
        selectMenuPath(GridDetailsServerTest.CHANGE_HIERARCHY);
        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);
        Assert.assertEquals("Two", getGridElement().getDetails(0).getText());
    }

    @Test
    public void swappingDetailsGenerators_noDetailsShown() {
        selectMenuPath(GridDetailsServerTest.DETAILS_GENERATOR_WATCHING);
        selectMenuPath(GridDetailsServerTest.DETAILS_GENERATOR_NULL);
        Assert.assertFalse("Got some errors", $(NotificationElement.class).exists());
    }

    @Test
    public void swappingDetailsGenerators_shownDetails() {
        selectMenuPath(GridDetailsServerTest.DETAILS_GENERATOR_PERSISTING);
        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);
        Assert.assertTrue("Details should contain 'One' at first", getGridElement().getDetails(0).getText().contains("One"));
        selectMenuPath(GridDetailsServerTest.DETAILS_GENERATOR_WATCHING);
        Assert.assertFalse("Details should contain 'Watching' after swapping generator", getGridElement().getDetails(0).getText().contains("Watching"));
    }

    @Test
    public void swappingDetailsGenerators_whileDetailsScrolledOut_showNever() {
        scrollGridVerticallyTo(1000);
        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);
        selectMenuPath(GridDetailsServerTest.DETAILS_GENERATOR_WATCHING);
        Assert.assertFalse("Got some errors", $(NotificationElement.class).exists());
    }

    @Test
    public void swappingDetailsGenerators_whileDetailsScrolledOut_showAfter() {
        scrollGridVerticallyTo(1000);
        selectMenuPath(GridDetailsServerTest.DETAILS_GENERATOR_PERSISTING);
        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);
        selectMenuPath(GridDetailsServerTest.DETAILS_GENERATOR_WATCHING);
        scrollGridVerticallyTo(0);
        Assert.assertFalse("Got some errors", $(NotificationElement.class).exists());
        Assert.assertNotNull("Could not find a details", getGridElement().getDetails(0));
    }

    @Test
    public void swappingDetailsGenerators_whileDetailsScrolledOut_showBefore() {
        selectMenuPath(GridDetailsServerTest.DETAILS_GENERATOR_PERSISTING);
        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);
        selectMenuPath(GridDetailsServerTest.DETAILS_GENERATOR_WATCHING);
        scrollGridVerticallyTo(1000);
        Assert.assertFalse("Got some errors", $(NotificationElement.class).exists());
        Assert.assertNotNull("Could not find a details", getGridElement().getDetails(0));
    }

    @Test
    public void swappingDetailsGenerators_whileDetailsScrolledOut_showBeforeAndAfter() {
        selectMenuPath(GridDetailsServerTest.DETAILS_GENERATOR_PERSISTING);
        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);
        selectMenuPath(GridDetailsServerTest.DETAILS_GENERATOR_WATCHING);
        scrollGridVerticallyTo(1000);
        scrollGridVerticallyTo(0);
        Assert.assertFalse("Got some errors", $(NotificationElement.class).exists());
        Assert.assertNotNull("Could not find a details", getGridElement().getDetails(0));
    }

    @Test
    public void noAssertErrorsOnEmptyDetailsAndScrollDown() {
        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);
        scrollGridVerticallyTo(500);
        Assert.assertFalse(logContainsText("AssertionError"));
    }

    @Test
    public void noAssertErrorsOnPopulatedDetailsAndScrollDown() {
        selectMenuPath(GridDetailsServerTest.DETAILS_GENERATOR_WATCHING);
        selectMenuPath(GridDetailsServerTest.OPEN_FIRST_ITEM_DETAILS);
        scrollGridVerticallyTo(500);
        Assert.assertFalse(logContainsText("AssertionError"));
    }
}

