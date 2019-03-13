package com.nispok.snackbar;


import Color.BLUE;
import Color.GREEN;
import Snackbar.SnackbarDuration.LENGTH_LONG;
import Snackbar.SnackbarDuration.LENGTH_SHORT;
import SnackbarType.MULTI_LINE;
import SnackbarType.SINGLE_LINE;
import android.content.Context;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class SnackbarBuilderTest {
    private Snackbar mSnackbar;

    private Context mContext;

    @Test
    public void testSnackbarShouldBeSingleLineByDefault() {
        mSnackbar = Snackbar.with(mContext);
        Assert.assertEquals(SINGLE_LINE, mSnackbar.getType());
    }

    @Test
    public void testSnackbarTypeCanBeChanged() {
        mSnackbar = Snackbar.with(mContext).type(MULTI_LINE);
        Assert.assertEquals(MULTI_LINE, mSnackbar.getType());
    }

    @Test
    public void testSnackbarShouldHaveLongLengthDurationSetByDefault() {
        mSnackbar = Snackbar.with(mContext);
        Assert.assertEquals(LENGTH_LONG.getDuration(), mSnackbar.getDuration());
    }

    @Test
    public void testSnackbarDurationCanBeChanged() {
        mSnackbar = Snackbar.with(mContext).duration(LENGTH_SHORT);
        Assert.assertEquals(LENGTH_SHORT.getDuration(), mSnackbar.getDuration());
    }

    @Test
    public void testSnackbarTextColorCanBeChanged() {
        mSnackbar = Snackbar.with(mContext).textColor(GREEN);
        Assert.assertEquals(GREEN, mSnackbar.getTextColor());
    }

    @Test
    public void testSnackbarActionColorCanBeChanged() {
        mSnackbar = Snackbar.with(mContext).actionColor(BLUE);
        Assert.assertEquals(BLUE, mSnackbar.getActionColor());
    }

    @Test
    public void testSnackbarAnimationShouldBeEnabledByDefault() {
        mSnackbar = Snackbar.with(mContext);
        Assert.assertTrue(mSnackbar.isAnimated());
    }

    @Test
    public void testSnackbarAnimationCanBeDisabled() {
        mSnackbar = Snackbar.with(mContext).animation(false);
        Assert.assertFalse(mSnackbar.isAnimated());
    }

    @Test
    public void testSnackbarCanBeCreatedWithMultipleCustomConfiguration() {
        mSnackbar = Snackbar.with(mContext).color(BLUE).textColor(GREEN).text("Aloha!").actionLabel("Action").type(MULTI_LINE).duration(LENGTH_SHORT);
        Assert.assertEquals(BLUE, mSnackbar.getColor());
        Assert.assertEquals(GREEN, mSnackbar.getTextColor());
        Assert.assertEquals("Aloha!", mSnackbar.getText());
        Assert.assertEquals("Action", mSnackbar.getActionLabel());
        Assert.assertEquals(MULTI_LINE, mSnackbar.getType());
        Assert.assertEquals(LENGTH_SHORT.getDuration(), mSnackbar.getDuration());
    }

    /* @Test
    public void testSnackbarCanBeCreatedWithColorResources() {
    mSnackbar = Snackbar.with(mContext)
    .colorResource(R.color.sb__action_bg_color)
    .textColorResource(R.color.sb__text_color)
    .actionColorResource(R.color.sb__action_bg_color);

    Resources res = mContext.getResources();
    int color = res.getColor(R.color.sb__action_bg_color);
    int textColor = res.getColor(R.color.sb__text_color);
    int actionColor = res.getColor(R.color.sb__action_bg_color);

    assertEquals(color, mSnackbar.getColor());
    assertEquals(textColor, mSnackbar.getTextColor());
    assertEquals(actionColor, mSnackbar.getActionColor());
    }
     */
    @Test
    public void testSnackbarCanBeCreatedWithCustomDuration() {
        mSnackbar = Snackbar.with(mContext).duration(1000L);
        Assert.assertEquals(1000L, mSnackbar.getDuration());
    }

    @Test
    public void testSnackbarWithCustomDurationOverrideSnackbarDuration() {
        mSnackbar = Snackbar.with(mContext).duration(1000L).duration(LENGTH_LONG);
        Assert.assertEquals(1000L, mSnackbar.getDuration());
    }

    @Test
    public void testSnackbarCanBeSetToNotDismissOnActionClicked() {
        mSnackbar = Snackbar.with(mContext).dismissOnActionClicked(false);
        Assert.assertFalse(mSnackbar.shouldDismissOnActionClicked());
    }

    @Test
    public void testSnackbarShouldBeDismissedOnActionClickedByDefault() {
        mSnackbar = Snackbar.with(mContext);
        Assert.assertTrue(mSnackbar.shouldDismissOnActionClicked());
    }

    @Test
    public void testSnackbarCustomDurationMustBeGreaterThanZero() {
        mSnackbar = Snackbar.with(mContext).duration(0);
        Assert.assertFalse(((mSnackbar.getDuration()) == 0));
    }
}

