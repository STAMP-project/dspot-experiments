package com.vaadin.v7.tests.components.grid.basicfeatures.client;


import ScrollDestination.ANY;
import ScrollDestination.END;
import ScrollDestination.START;
import com.vaadin.shared.Range;
import com.vaadin.testbench.By;
import com.vaadin.testbench.ElementQuery;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;


public class GridDetailsClientTest extends GridBasicClientFeaturesTest {
    private static final String[] SET_GENERATOR = new String[]{ "Component", "Row details", "Set generator" };

    private static final String[] SET_FAULTY_GENERATOR = new String[]{ "Component", "Row details", "Set faulty generator" };

    private static final String[] SET_EMPTY_GENERATOR = new String[]{ "Component", "Row details", "Set empty generator" };

    @Test(expected = NoSuchElementException.class)
    public void noDetailsByDefault() {
        Assert.assertNull("details for row 1 should not exist at the start", getGridElement().getDetails(1));
    }

    @Test(expected = NoSuchElementException.class)
    public void nullRendererDoesNotShowDetailsPlaceholder() {
        toggleDetailsFor(1);
        getGridElement().getDetails(1);
    }

    @Test
    public void applyRendererThenOpenDetails() {
        selectMenuPath(GridDetailsClientTest.SET_GENERATOR);
        toggleDetailsFor(1);
        TestBenchElement details = getGridElement().getDetails(1);
        Assert.assertTrue("Unexpected details content", details.getText().startsWith("Row: 1."));
    }

    @Test(expected = NoSuchElementException.class)
    public void openDetailsThenAppyRendererShouldNotShowDetails() {
        toggleDetailsFor(1);
        selectMenuPath(GridDetailsClientTest.SET_GENERATOR);
        getGridElement().getDetails(1);
    }

    @Test
    public void openHiddenDetailsThenScrollToIt() {
        try {
            getGridElement().getDetails(100);
            Assert.fail("details row for 100 was apparently found, while it shouldn't have been.");
        } catch (NoSuchElementException e) {
            // expected
        }
        selectMenuPath(GridDetailsClientTest.SET_GENERATOR);
        toggleDetailsFor(100);
        // scroll a bit beyond so we see below.
        getGridElement().scrollToRow(101);
        TestBenchElement details = getGridElement().getDetails(100);
        Assert.assertTrue("Unexpected details content", details.getText().startsWith("Row: 100."));
    }

    @Test
    public void errorUpdaterShowsErrorNotification() {
        Assert.assertFalse("No notifications should've been at the start", $(NotificationElement.class).exists());
        selectMenuPath(GridDetailsClientTest.SET_FAULTY_GENERATOR);
        toggleDetailsFor(1);
        ElementQuery<NotificationElement> notification = $(NotificationElement.class);
        Assert.assertTrue("Was expecting an error notification here", notification.exists());
        notification.first().close();
        Assert.assertEquals("The error details element should be empty", "", getGridElement().getDetails(1).getText());
    }

    @Test(expected = NoSuchElementException.class)
    public void detailsClosedWhenResettingGenerator() {
        selectMenuPath(GridDetailsClientTest.SET_GENERATOR);
        toggleDetailsFor(1);
        selectMenuPath(GridDetailsClientTest.SET_FAULTY_GENERATOR);
        getGridElement().getDetails(1);
    }

    @Test
    public void settingNewGeneratorStillWorksAfterError() {
        selectMenuPath(GridDetailsClientTest.SET_FAULTY_GENERATOR);
        toggleDetailsFor(1);
        $(NotificationElement.class).first().close();
        toggleDetailsFor(1);
        selectMenuPath(GridDetailsClientTest.SET_GENERATOR);
        toggleDetailsFor(1);
        Assert.assertNotEquals("New details should've been generated even after error", "", getGridElement().getDetails(1).getText());
    }

    @Test
    public void updaterRendersExpectedWidgets() {
        selectMenuPath(GridDetailsClientTest.SET_GENERATOR);
        toggleDetailsFor(1);
        TestBenchElement detailsElement = getGridElement().getDetails(1);
        Assert.assertNotNull(detailsElement.findElement(By.className("gwt-Label")));
        Assert.assertNotNull(detailsElement.findElement(By.className("gwt-Button")));
    }

    @Test
    public void widgetsInUpdaterWorkAsExpected() {
        selectMenuPath(GridDetailsClientTest.SET_GENERATOR);
        toggleDetailsFor(1);
        TestBenchElement detailsElement = getGridElement().getDetails(1);
        WebElement button = detailsElement.findElement(By.className("gwt-Button"));
        button.click();
        WebElement label = detailsElement.findElement(By.className("gwt-Label"));
        Assert.assertEquals("clicked", label.getText());
    }

    @Test
    public void emptyGenerator() {
        selectMenuPath(GridDetailsClientTest.SET_EMPTY_GENERATOR);
        toggleDetailsFor(1);
        Assert.assertEquals("empty generator did not produce an empty details row", "", getGridElement().getDetails(1).getText());
    }

    @Test(expected = NoSuchElementException.class)
    public void removeDetailsRow() {
        selectMenuPath(GridDetailsClientTest.SET_GENERATOR);
        toggleDetailsFor(1);
        toggleDetailsFor(1);
        getGridElement().getDetails(1);
    }

    @Test
    public void rowElementClassNames() {
        selectMenuPath(GridDetailsClientTest.SET_GENERATOR);
        toggleDetailsFor(0);
        toggleDetailsFor(1);
        List<WebElement> elements = getGridElement().findElements(By.className("v-grid-spacer"));
        Assert.assertEquals("v-grid-spacer", elements.get(0).getAttribute("class"));
        Assert.assertEquals("v-grid-spacer stripe", elements.get(1).getAttribute("class"));
    }

    @Test
    public void scrollDownToRowWithDetails() {
        selectMenuPath(GridDetailsClientTest.SET_GENERATOR);
        toggleDetailsFor(100);
        scrollToRow(100, ANY);
        Range validScrollRange = Range.between(1691, 1706);
        Assert.assertTrue(validScrollRange.contains(getGridVerticalScrollPos()));
    }

    @Test
    public void scrollUpToRowWithDetails() {
        selectMenuPath(GridDetailsClientTest.SET_GENERATOR);
        toggleDetailsFor(100);
        scrollGridVerticallyTo(999999);
        scrollToRow(100, ANY);
        Range validScrollRange = Range.between(1981, 2001);
        Assert.assertTrue(validScrollRange.contains(getGridVerticalScrollPos()));
    }

    @Test
    public void cannotScrollBeforeTop() {
        selectMenuPath(GridDetailsClientTest.SET_GENERATOR);
        toggleDetailsFor(1);
        scrollToRow(0, END);
        Assert.assertEquals(0, getGridVerticalScrollPos());
    }

    @Test
    public void cannotScrollAfterBottom() {
        selectMenuPath(GridDetailsClientTest.SET_GENERATOR);
        toggleDetailsFor(999);
        scrollToRow(999, START);
        Range expectedRange = Range.withLength(19671, 20);
        Assert.assertTrue(expectedRange.contains(getGridVerticalScrollPos()));
    }
}

