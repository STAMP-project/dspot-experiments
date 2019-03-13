package com.vaadin.tests.components.grid.basicfeatures.escalator;


import WidgetUtil.PIXEL_EPSILON;
import com.vaadin.shared.Range;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.components.grid.basicfeatures.EscalatorBasicClientFeaturesTest;
import com.vaadin.tests.tb3.AbstractTB3Test;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


@SuppressWarnings("boxing")
public class EscalatorSpacerTest extends EscalatorBasicClientFeaturesTest {
    // @formatter:off
    // separate strings made so that eclipse can show the concatenated string by hovering the mouse over the constant
    // translate3d(0px, 40px, 123px);
    // translate3d(24px, 15.251px, 0);
    // translate(0, 40px);
    private static final String TRANSLATE_VALUE_REGEX = "translate(?:3d|)"// "translate" or "translate3d"
     + (((((((((((((((((((("\\("// literal "("
     + "(")// start capturing the x argument
     + "[0-9]+")// the integer part of the value
     + "(?:")// start of the subpixel part of the value
     + "\\.[0-9]")// if we have a period, there must be at least one number after it
     + "[0-9]*")// any amount of accuracy afterwards is fine
     + ")?")// the subpixel part is optional
     + ")") + "(?:px)?")// we don't care if the values are suffixed by "px" or not.
     + ", ") + "(")// start capturing the y argument
     + "[0-9]+")// the integer part of the value
     + "(?:")// start of the subpixel part of the value
     + "\\.[0-9]")// if we have a period, there must be at least one number after it
     + "[0-9]*")// any amount of accuracy afterwards is fine
     + ")?")// the subpixel part is optional
     + ")") + "(?:px)?")// we don't care if the values are suffixed by "px" or not.
     + "(?:, .*?)?")// the possible z argument, uninteresting (translate doesn't have one, translate3d does)
     + "\\)")// literal ")"
     + ";?");// optional ending semicolon


    // 40px;
    // 12.34px
    private static final String PIXEL_VALUE_REGEX = "("// capture the pixel value
     + ((((((("[0-9]+"// the pixel argument
     + "(?:")// start of the subpixel part of the value
     + "\\.[0-9]")// if we have a period, there must be at least one number after it
     + "[0-9]*")// any amount of accuracy afterwards is fine
     + ")?")// the subpixel part is optional
     + ")") + "(?:px)?")// optional "px" string
     + ";?");// optional semicolon


    // @formatter:on
    // also matches "-webkit-transform";
    private static final Pattern TRANSFORM_CSS_PATTERN = Pattern.compile("transform: (.*?);");

    private static final Pattern TOP_CSS_PATTERN = Pattern.compile("top: ([0-9]+(?:\\.[0-9]+)?(?:px)?);?", Pattern.CASE_INSENSITIVE);

    private static final Pattern LEFT_CSS_PATTERN = Pattern.compile("left: ([0-9]+(?:\\.[0-9]+)?(?:px)?);?", Pattern.CASE_INSENSITIVE);

    private static final Pattern TRANSLATE_VALUE_PATTERN = Pattern.compile(EscalatorSpacerTest.TRANSLATE_VALUE_REGEX);

    private static final Pattern PIXEL_VALUE_PATTERN = Pattern.compile(EscalatorSpacerTest.PIXEL_VALUE_REGEX, Pattern.CASE_INSENSITIVE);

    @Test
    public void openVisibleSpacer() {
        Assert.assertFalse("No spacers should be shown at the start", spacersAreFoundInDom());
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_1, EscalatorBasicClientFeaturesTest.SET_100PX);
        Assert.assertNotNull("Spacer should be shown after setting it", getSpacer(1));
    }

    @Test
    public void closeVisibleSpacer() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_1, EscalatorBasicClientFeaturesTest.SET_100PX);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_1, EscalatorBasicClientFeaturesTest.REMOVE);
        Assert.assertNull("Spacer should not exist after removing it", getSpacer(1));
    }

    @Test
    public void spacerPushesVisibleRowsDown() {
        double oldTop = EscalatorSpacerTest.getElementTop(getBodyRow(2));
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_1, EscalatorBasicClientFeaturesTest.SET_100PX);
        double newTop = EscalatorSpacerTest.getElementTop(getBodyRow(2));
        AbstractTB3Test.assertGreater("Row below a spacer was not pushed down", newTop, oldTop);
    }

    @Test
    public void addingRowAboveSpacerPushesItDown() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.REMOVE_ALL_ROWS);
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.ADD_ONE_ROW_TO_BEGINNING);
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.ADD_ONE_ROW_TO_BEGINNING);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_1, EscalatorBasicClientFeaturesTest.SET_100PX);
        double oldTop = EscalatorSpacerTest.getElementTop(getSpacer(1));
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.ADD_ONE_ROW_TO_BEGINNING);
        double newTop = EscalatorSpacerTest.getElementTop(getSpacer(2));
        AbstractTB3Test.assertGreater((((("Spacer should've been pushed down (oldTop: " + oldTop) + ", newTop: ") + newTop) + ")"), newTop, oldTop);
    }

    @Test
    public void addingRowBelowSpacerDoesNotPushItDown() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.REMOVE_ALL_ROWS);
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.ADD_ONE_ROW_TO_BEGINNING);
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.ADD_ONE_ROW_TO_BEGINNING);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_1, EscalatorBasicClientFeaturesTest.SET_100PX);
        double oldTop = EscalatorSpacerTest.getElementTop(getSpacer(1));
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.ADD_ONE_ROW_TO_END);
        double newTop = EscalatorSpacerTest.getElementTop(getSpacer(1));
        Assert.assertEquals("Spacer should've not been pushed down", newTop, oldTop, PIXEL_EPSILON);
    }

    @Test
    public void addingRowBelowSpacerIsActuallyRenderedBelowWhenEscalatorIsEmpty() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.REMOVE_ALL_ROWS);
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.ADD_ONE_ROW_TO_BEGINNING);
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.ADD_ONE_ROW_TO_BEGINNING);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_1, EscalatorBasicClientFeaturesTest.SET_100PX);
        double spacerTop = EscalatorSpacerTest.getElementTop(getSpacer(1));
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.ADD_ONE_ROW_TO_END);
        double rowTop = EscalatorSpacerTest.getElementTop(getBodyRow(2));
        Assert.assertEquals("Next row should've been rendered below the spacer", (spacerTop + 100), rowTop, PIXEL_EPSILON);
    }

    @Test
    public void addSpacerAtBottomThenScrollThere() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_99, EscalatorBasicClientFeaturesTest.SET_100PX);
        scrollVerticallyTo(999999);
        Assert.assertFalse("Did not expect a notification", $(NotificationElement.class).exists());
    }

    @Test
    public void scrollToBottomThenAddSpacerThere() {
        scrollVerticallyTo(999999);
        long oldBottomScrollTop = getScrollTop();
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_99, EscalatorBasicClientFeaturesTest.SET_100PX);
        Assert.assertEquals(("Adding a spacer underneath the current viewport should " + "not scroll anywhere"), oldBottomScrollTop, getScrollTop());
        Assert.assertFalse("Got an unexpected notification", $(NotificationElement.class).exists());
        scrollVerticallyTo(999999);
        Assert.assertFalse("Got an unexpected notification", $(NotificationElement.class).exists());
        AbstractTB3Test.assertGreater(("Adding a spacer should've made the scrollbar scroll " + "further"), getScrollTop(), oldBottomScrollTop);
    }

    @Test
    public void removingRowAboveSpacerMovesSpacerUp() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_1, EscalatorBasicClientFeaturesTest.SET_100PX);
        WebElement spacer = getSpacer(1);
        double originalElementTop = EscalatorSpacerTest.getElementTop(spacer);
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.REMOVE_ONE_ROW_FROM_BEGINNING);
        AbstractTB3Test.assertLessThan("spacer should've moved up", EscalatorSpacerTest.getElementTop(spacer), originalElementTop);
        Assert.assertNull(("No spacer for row 1 should be found after removing the " + "top row"), getSpacer(1));
    }

    @Test
    public void removingSpacedRowRemovesSpacer() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_1, EscalatorBasicClientFeaturesTest.SET_100PX);
        Assert.assertTrue("Spacer should've been found in the DOM", spacersAreFoundInDom());
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.REMOVE_ONE_ROW_FROM_BEGINNING);
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.REMOVE_ONE_ROW_FROM_BEGINNING);
        Assert.assertFalse(("No spacers should be in the DOM after removing " + "associated spacer"), spacersAreFoundInDom());
    }

    @Test
    public void spacersAreFixedInViewport_firstFreezeThenScroll() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.FROZEN_COLUMNS, EscalatorBasicClientFeaturesTest.FREEZE_1_COLUMN);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_1, EscalatorBasicClientFeaturesTest.SET_100PX);
        Assert.assertEquals(("Spacer's left position should've been 0 at the " + "beginning"), 0.0, EscalatorSpacerTest.getElementLeft(getSpacer(1)), PIXEL_EPSILON);
        int scrollTo = 10;
        scrollHorizontallyTo(scrollTo);
        Assert.assertEquals((((("Spacer's left position should've been " + scrollTo) + " after scrolling ") + scrollTo) + "px"), scrollTo, EscalatorSpacerTest.getElementLeft(getSpacer(1)), PIXEL_EPSILON);
    }

    @Test
    public void spacersAreFixedInViewport_firstScrollThenFreeze() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.FROZEN_COLUMNS, EscalatorBasicClientFeaturesTest.FREEZE_1_COLUMN);
        int scrollTo = 10;
        scrollHorizontallyTo(scrollTo);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_1, EscalatorBasicClientFeaturesTest.SET_100PX);
        Assert.assertEquals((((("Spacer's left position should've been " + scrollTo) + " after scrolling ") + scrollTo) + "px"), scrollTo, EscalatorSpacerTest.getElementLeft(getSpacer(1)), PIXEL_EPSILON);
    }

    @Test
    public void addingMinusOneSpacerDoesNotScrollWhenScrolledAtTop() {
        scrollVerticallyTo(5);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_MINUS1, EscalatorBasicClientFeaturesTest.SET_100PX);
        Assert.assertEquals("No scroll adjustment should've happened when adding the -1 spacer", 5, getScrollTop());
    }

    @Test
    public void removingMinusOneSpacerScrolls() {
        scrollVerticallyTo(5);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_MINUS1, EscalatorBasicClientFeaturesTest.SET_100PX);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_MINUS1, EscalatorBasicClientFeaturesTest.REMOVE);
        Assert.assertEquals(("Scroll adjustment should've happened when removing the " + "-1 spacer"), 0, getScrollTop());
    }

    @Test
    public void scrollToRowWorksProperlyWithSpacers() throws Exception {
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_MINUS1, EscalatorBasicClientFeaturesTest.SET_100PX);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_1, EscalatorBasicClientFeaturesTest.SET_100PX);
        /* we check for row -3 instead of -1, because escalator has two rows
        buffered underneath the footer
         */
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.SCROLL_TO, EscalatorBasicClientFeaturesTest.ROW_75);
        Thread.sleep(500);
        Assert.assertEquals("Row 75: 0,75", getBodyCell((-3), 0).getText());
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.SCROLL_TO, EscalatorBasicClientFeaturesTest.ROW_25);
        Thread.sleep(500);
        try {
            Assert.assertEquals("Row 25: 0,25", getBodyCell(0, 0).getText());
        } catch (ComparisonFailure retryForIE10andIE11) {
            /* This seems to be some kind of subpixel/off-by-one-pixel error.
            Everything's scrolled correctly, but Escalator still loads one
            row above to the DOM, underneath the header. It's there, but it's
            not visible. We'll allow for that one pixel error.
             */
            Assert.assertEquals("Row 24: 0,24", getBodyCell(0, 0).getText());
        }
    }

    @Test
    public void scrollToSpacerFromAbove() throws Exception {
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_50, EscalatorBasicClientFeaturesTest.SET_100PX);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_50, EscalatorBasicClientFeaturesTest.SCROLL_HERE_ANY_0PADDING);
        // Browsers might vary with a few pixels.
        Range allowableScrollRange = Range.between(765, 780);
        int scrollTop = ((int) (getScrollTop()));
        Assert.assertTrue(((("Scroll position was not " + allowableScrollRange) + ", but ") + scrollTop), allowableScrollRange.contains(scrollTop));
    }

    @Test
    public void scrollToSpacerFromBelow() throws Exception {
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_50, EscalatorBasicClientFeaturesTest.SET_100PX);
        scrollVerticallyTo(999999);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_50, EscalatorBasicClientFeaturesTest.SCROLL_HERE_ANY_0PADDING);
        // Browsers might vary with a few pixels.
        Range allowableScrollRange = Range.between(1015, 1025);
        int scrollTop = ((int) (getScrollTop()));
        Assert.assertTrue(((("Scroll position was not " + allowableScrollRange) + ", but ") + scrollTop), allowableScrollRange.contains(scrollTop));
    }

    @Test
    public void scrollToSpacerAlreadyInViewport() throws Exception {
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_50, EscalatorBasicClientFeaturesTest.SET_100PX);
        scrollVerticallyTo(1000);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_50, EscalatorBasicClientFeaturesTest.SCROLL_HERE_ANY_0PADDING);
        Assert.assertEquals(getScrollTop(), 1000);
    }

    @Test
    public void scrollToRowAndSpacerFromAbove() throws Exception {
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_50, EscalatorBasicClientFeaturesTest.SET_100PX);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_50, EscalatorBasicClientFeaturesTest.SCROLL_HERE_SPACERBELOW_ANY_0PADDING);
        // Browsers might vary with a few pixels.
        Range allowableScrollRange = Range.between(765, 780);
        int scrollTop = ((int) (getScrollTop()));
        Assert.assertTrue(((("Scroll position was not " + allowableScrollRange) + ", but ") + scrollTop), allowableScrollRange.contains(scrollTop));
    }

    @Test
    public void scrollToRowAndSpacerFromBelow() throws Exception {
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_50, EscalatorBasicClientFeaturesTest.SET_100PX);
        scrollVerticallyTo(999999);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_50, EscalatorBasicClientFeaturesTest.SCROLL_HERE_SPACERBELOW_ANY_0PADDING);
        // Browsers might vary with a few pixels.
        Range allowableScrollRange = Range.between(995, 1005);
        int scrollTop = ((int) (getScrollTop()));
        Assert.assertTrue(((("Scroll position was not " + allowableScrollRange) + ", but ") + scrollTop), allowableScrollRange.contains(scrollTop));
    }

    @Test
    public void scrollToRowAndSpacerAlreadyInViewport() throws Exception {
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_50, EscalatorBasicClientFeaturesTest.SET_100PX);
        scrollVerticallyTo(950);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_50, EscalatorBasicClientFeaturesTest.SCROLL_HERE_SPACERBELOW_ANY_0PADDING);
        Assert.assertEquals(getScrollTop(), 950);
    }

    @Test
    public void domCanBeSortedWithFocusInSpacer() throws InterruptedException {
        // Firefox behaves badly with focus-related tests - skip it.
        if (BrowserUtil.isFirefox(getDesiredCapabilities())) {
            return;
        }
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.FOCUSABLE_UPDATER);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_1, EscalatorBasicClientFeaturesTest.SET_100PX);
        WebElement inputElement = getEscalator().findElement(By.tagName("input"));
        inputElement.click();
        scrollVerticallyTo(30);
        // Sleep needed because of all the JS we're doing, and to let
        // the DOM reordering to take place.
        Thread.sleep(500);
        Assert.assertFalse("Error message detected", $(NotificationElement.class).exists());
    }

    @Test
    public void spacersAreInsertedInCorrectDomPosition() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_1, EscalatorBasicClientFeaturesTest.SET_100PX);
        WebElement tbody = getEscalator().findElement(By.tagName("tbody"));
        WebElement spacer = getChild(tbody, 2);
        String cssClass = spacer.getAttribute("class");
        Assert.assertTrue((("element index 2 was not a spacer (class=\"" + cssClass) + "\")"), cssClass.contains("-spacer"));
    }

    @Test
    public void spacersAreInCorrectDomPositionAfterScroll() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_1, EscalatorBasicClientFeaturesTest.SET_100PX);
        scrollVerticallyTo(32);// roughly one row's worth

        WebElement tbody = getEscalator().findElement(By.tagName("tbody"));
        WebElement spacer = getChild(tbody, 1);
        String cssClass = spacer.getAttribute("class");
        Assert.assertTrue((("element index 1 was not a spacer (class=\"" + cssClass) + "\")"), cssClass.contains("-spacer"));
    }

    @Test
    public void spacerScrolledIntoViewGetsFocus() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.FOCUSABLE_UPDATER);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_50, EscalatorBasicClientFeaturesTest.SET_100PX);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_50, EscalatorBasicClientFeaturesTest.SCROLL_HERE_ANY_0PADDING);
        tryToTabIntoFocusUpdaterElement();
        Assert.assertEquals("input", getFocusedElement().getTagName());
    }

    @Test
    public void spacerScrolledOutOfViewDoesNotGetFocus() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.FOCUSABLE_UPDATER);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_1, EscalatorBasicClientFeaturesTest.SET_100PX);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_50, EscalatorBasicClientFeaturesTest.SCROLL_HERE_ANY_0PADDING);
        tryToTabIntoFocusUpdaterElement();
        Assert.assertNotEquals("input", getFocusedElement().getTagName());
    }

    @Test
    public void spacerOpenedInViewGetsFocus() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.FOCUSABLE_UPDATER);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_1, EscalatorBasicClientFeaturesTest.SET_100PX);
        tryToTabIntoFocusUpdaterElement();
        WebElement focusedElement = getFocusedElement();
        Assert.assertEquals("input", focusedElement.getTagName());
    }

    @Test
    public void spacerOpenedOutOfViewDoesNotGetFocus() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.FOCUSABLE_UPDATER);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_50, EscalatorBasicClientFeaturesTest.SET_100PX);
        tryToTabIntoFocusUpdaterElement();
        Assert.assertNotEquals("input", getFocusedElement().getTagName());
    }

    @Test
    public void spacerOpenedInViewAndScrolledOutAndBackAgainGetsFocus() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.FOCUSABLE_UPDATER);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_1, EscalatorBasicClientFeaturesTest.SET_100PX);
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.SCROLL_TO, EscalatorBasicClientFeaturesTest.ROW_50);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_1, EscalatorBasicClientFeaturesTest.SCROLL_HERE_ANY_0PADDING);
        tryToTabIntoFocusUpdaterElement();
        Assert.assertEquals("input", getFocusedElement().getTagName());
    }

    @Test
    public void spacerOpenedOutOfViewAndScrolledInAndBackAgainDoesNotGetFocus() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.FOCUSABLE_UPDATER);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_50, EscalatorBasicClientFeaturesTest.SET_100PX);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.SPACERS, EscalatorBasicClientFeaturesTest.ROW_50, EscalatorBasicClientFeaturesTest.SCROLL_HERE_ANY_0PADDING);
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.SCROLL_TO, EscalatorBasicClientFeaturesTest.ROW_0);
        tryToTabIntoFocusUpdaterElement();
        Assert.assertNotEquals("input", getFocusedElement().getTagName());
    }
}

