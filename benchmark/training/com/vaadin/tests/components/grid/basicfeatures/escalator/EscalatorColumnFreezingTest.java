package com.vaadin.tests.components.grid.basicfeatures.escalator;


import com.vaadin.tests.components.grid.basicfeatures.EscalatorBasicClientFeaturesTest;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class EscalatorColumnFreezingTest extends EscalatorBasicClientFeaturesTest {
    private static final Pattern TRANSFORM_PATTERN = // @formatter:off
    // any start of the string
    Pattern.compile((".*" + // non-capturing group for "webkitTransform: " or "transform: "
    ((("(?:webkitT|t)ransform: " + // non-capturing group for "translate" or "translate3d"
    "translate(?:3d)?") + // capturing the digits in e.g "(100px,"
    "\\((\\d+)px,") + // any end of the string
    ".*")), Pattern.CASE_INSENSITIVE);

    // @formatter:on
    private static final Pattern LEFT_PATTERN = Pattern.compile(".*left: (\\d+)px.*", Pattern.CASE_INSENSITIVE);

    private static final int NO_FREEZE = -1;

    @Test
    public void testNoFreeze() {
        openTestURL();
        populate();
        WebElement bodyCell = getBodyCell(0, 0);
        Assert.assertFalse(EscalatorColumnFreezingTest.isFrozen(bodyCell));
        Assert.assertEquals(EscalatorColumnFreezingTest.NO_FREEZE, EscalatorColumnFreezingTest.getFrozenScrollCompensation(bodyCell));
    }

    @Test
    public void testOneFreeze() {
        openTestURL();
        populate();
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.FROZEN_COLUMNS, EscalatorBasicClientFeaturesTest.FREEZE_1_COLUMN);
        int scrollPx = 60;
        scrollHorizontallyTo(scrollPx);
        WebElement bodyCell = getBodyCell(0, 0);
        Assert.assertTrue(EscalatorColumnFreezingTest.isFrozen(bodyCell));
        Assert.assertEquals(scrollPx, EscalatorColumnFreezingTest.getFrozenScrollCompensation(bodyCell));
    }

    @Test
    public void testFreezeToggle() {
        openTestURL();
        populate();
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.FROZEN_COLUMNS, EscalatorBasicClientFeaturesTest.FREEZE_1_COLUMN);
        scrollHorizontallyTo(100);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.FROZEN_COLUMNS, EscalatorBasicClientFeaturesTest.FREEZE_0_COLUMNS);
        WebElement bodyCell = getBodyCell(0, 0);
        Assert.assertFalse(EscalatorColumnFreezingTest.isFrozen(bodyCell));
        Assert.assertEquals(EscalatorColumnFreezingTest.NO_FREEZE, EscalatorColumnFreezingTest.getFrozenScrollCompensation(bodyCell));
    }
}

