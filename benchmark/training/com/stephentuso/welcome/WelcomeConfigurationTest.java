package com.stephentuso.welcome;


import Color.WHITE;
import R.color.wel_white;
import WelcomeConfiguration.BottomLayout.BUTTON_BAR;
import WelcomeConfiguration.BottomLayout.STANDARD.resId;
import WelcomeConfiguration.Builder;
import android.R.anim.fade_out;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Created by stephentuso on 10/6/16.
 *
 * Tests for WelcomeConfiguration and WelcomeConfiguration.Builder
 */
@RunWith(MockitoJUnitRunner.class)
public class WelcomeConfigurationTest extends ConfigurationTest {
    private Builder builder1;// Will be initialized with one page


    private Builder builder2;// Will have no pages


    // Tests
    @Test
    public void testContext() {
        Assert.assertTrue(((builder1.build().getContext()) == (context)));
    }

    @Test(expected = IllegalStateException.class)
    public void testZeroPagesThrows() {
        builder2.build();
    }

    @Test
    public void testSwipeToDismiss() {
        builder1.swipeToDismiss(true);
        setApiLevel(10);
        Assert.assertFalse(builder1.build().getSwipeToDismiss());
        setApiLevel(11);
        Assert.assertTrue(builder1.build().getSwipeToDismiss());
        setApiLevel(18);
        Assert.assertTrue(builder1.build().getSwipeToDismiss());
        setRtl(true);
        Assert.assertTrue(builder1.build().getSwipeToDismiss());
        builder1.swipeToDismiss(false);
        Assert.assertFalse(builder1.build().getSwipeToDismiss());
    }

    @Test
    public void testCanSkip() {
        builder1.canSkip(true);
        Assert.assertTrue(builder1.build().getCanSkip());
        builder1.canSkip(false);
        Assert.assertFalse(builder1.build().getCanSkip());
    }

    @Test
    public void testBackButtonSkips() {
        builder1.backButtonSkips(true);
        Assert.assertTrue(builder1.build().getBackButtonSkips());
        builder1.backButtonSkips(false);
        Assert.assertFalse(builder1.build().getBackButtonSkips());
    }

    @Test
    public void testBackButtonNavigatesPages() {
        builder1.backButtonNavigatesPages(true);
        Assert.assertTrue(builder1.build().getBackButtonNavigatesPages());
        builder1.backButtonNavigatesPages(false);
        Assert.assertFalse(builder1.build().getBackButtonNavigatesPages());
    }

    @Test
    public void testAnimateButtons() {
        builder1.animateButtons(true);
        Assert.assertTrue(builder1.build().getAnimateButtons());
        builder1.animateButtons(false);
        Assert.assertFalse(builder1.build().getAnimateButtons());
    }

    @Test
    public void testUseCustomDoneButton() {
        builder1.useCustomDoneButton(true);
        Assert.assertTrue(builder1.build().getUseCustomDoneButton());
        builder1.useCustomDoneButton(false);
        Assert.assertFalse(builder1.build().getUseCustomDoneButton());
    }

    @Test
    public void testShowNextButton() {
        builder1.showNextButton(true);
        Assert.assertTrue(builder1.build().getShowNextButton());
        builder1.showNextButton(false);
        Assert.assertFalse(builder1.build().getShowNextButton());
    }

    @Test
    public void testShowPrevButton() {
        builder1.showPrevButton(true);
        Assert.assertTrue(builder1.build().getShowPrevButton());
        builder1.showPrevButton(false);
        Assert.assertFalse(builder1.build().getShowPrevButton());
    }

    @Test
    public void testShowActionBarBackButton() {
        builder1.showActionBarBackButton(true);
        Assert.assertTrue(builder1.build().getShowActionBarBackButton());
        builder1.showActionBarBackButton(false);
        Assert.assertFalse(builder1.build().getShowActionBarBackButton());
    }

    @Test
    public void testSkipButtonTypeface() {
        builder1.skipButtonTypefacePath("skipButton");
        Assert.assertEquals("skipButton", builder1.build().getSkipButtonTypefacePath());
    }

    @Test
    public void testDoneButtonTypeface() {
        builder1.doneButtonTypefacePath("doneButton");
        Assert.assertEquals("doneButton", builder1.build().getDoneButtonTypefacePath());
    }

    @Test
    public void testDefaultTitleTypefacePath() {
        builder1.defaultTitleTypefacePath("title");
        Assert.assertEquals("title", builder1.build().getDefaultTitleTypefacePath());
    }

    @Test
    public void testDefaultHeaderTypefacePath() {
        builder1.defaultHeaderTypefacePath("header");
        Assert.assertEquals("header", builder1.build().getDefaultHeaderTypefacePath());
    }

    @Test
    public void testDefaultDescriptionTypefacePath() {
        builder1.defaultDescriptionTypefacePath("description");
        Assert.assertEquals("description", builder1.build().getDefaultDescriptionTypefacePath());
    }

    @Test
    public void testExitAnimation() {
        builder1.exitAnimation(fade_out);
        Assert.assertEquals(fade_out, builder1.build().getExitAnimation());
    }

    @Test
    public void testPageIndexFunctions() {
        builder2.page(newBlankPage()).page(newBlankPage()).page(newBlankPage()).swipeToDismiss(false);
        // SwipeToDismiss false
        // Not rtl
        Assert.assertEquals(0, builder2.build().firstPageIndex());
        Assert.assertEquals(2, builder2.build().lastPageIndex());
        Assert.assertEquals(2, builder2.build().lastViewablePageIndex());
        // Rtl
        setRtl(true);
        Assert.assertEquals(2, builder2.build().firstPageIndex());
        Assert.assertEquals(0, builder2.build().lastPageIndex());
        Assert.assertEquals(0, builder2.build().lastViewablePageIndex());
        // SwipeToDismiss true
        builder2.swipeToDismiss(true);
        // rtl false
        setRtl(false);
        Assert.assertEquals(0, builder2.build().firstPageIndex());
        Assert.assertEquals(3, builder2.build().lastPageIndex());
        Assert.assertEquals(2, builder2.build().lastViewablePageIndex());
        // Rtl true
        setRtl(true);
        Assert.assertEquals(3, builder2.build().firstPageIndex());
        Assert.assertEquals(0, builder2.build().lastPageIndex());
        Assert.assertEquals(1, builder2.build().lastViewablePageIndex());
    }

    @Test
    public void testPageFunctions() {
        final Fragment fragment1 = new Fragment();
        final Fragment fragment2 = new Fragment();
        final Fragment fragment3 = new Fragment();
        builder2.page(new FragmentWelcomePage() {
            @Override
            protected Fragment fragment() {
                return fragment1;
            }
        }).page(new FragmentWelcomePage() {
            @Override
            protected Fragment fragment() {
                return fragment2;
            }
        }).page(new FragmentWelcomePage() {
            @Override
            protected Fragment fragment() {
                return fragment3;
            }
        });
        // Test page order
        Assert.assertTrue(((builder2.build().createFragment(0)) == fragment1));
        Assert.assertTrue(((builder2.build().createFragment(1)) == fragment2));
        setRtl(true);
        Assert.assertTrue(((builder2.build().createFragment(0)) == fragment3));
        Assert.assertTrue(((builder2.build().createFragment(1)) == fragment2));
        setRtl(false);
        // Test page count
        builder2.swipeToDismiss(true);
        Assert.assertEquals(3, builder2.build().viewablePageCount());
        Assert.assertEquals(4, builder2.build().pageCount());
        Assert.assertEquals(4, builder2.build().getPages().size());
        builder2.swipeToDismiss(false);
        Assert.assertEquals(3, builder2.build().viewablePageCount());
        Assert.assertEquals(3, builder2.build().pageCount());
        Assert.assertEquals(3, builder2.build().getPages().size());
    }

    @Test
    public void testCreateGetFragment() {
        final Fragment fragment1 = new Fragment();
        builder2.page(new FragmentWelcomePage() {
            @Override
            protected Fragment fragment() {
                return fragment1;
            }
        });
        WelcomeConfiguration config = builder2.build();
        Assert.assertTrue(((config.getFragment(0)) == null));
        Assert.assertTrue(((config.createFragment(0)) == fragment1));
        Assert.assertTrue(((config.getFragment(0)) == fragment1));
    }

    @Test
    public void testBottomLayout() {
        Assert.assertEquals(resId, builder1.build().getBottomLayoutResId());
        builder1.bottomLayout(BUTTON_BAR);
        Assert.assertEquals(WelcomeConfiguration.BottomLayout.BUTTON_BAR.resId, builder1.build().getBottomLayoutResId());
    }

    @Test
    public void testInitDefaultBackgroundColor() {
        // TODO: Improve this
        Assert.assertEquals(ConfigurationTest.DEFAULT_COLOR, builder1.build().getDefaultBackgroundColor().value());
        setApiLevel(22);
        Assert.assertEquals(ConfigurationTest.DEFAULT_COLOR, builder1.build().getDefaultBackgroundColor().value());
    }

    @Test
    public void testDefaultBackgroundColor() {
        builder2.defaultBackgroundColor(wel_white).page(newBlankPage());
        WelcomeConfiguration config = builder2.build();
        Assert.assertEquals(WHITE, config.getDefaultBackgroundColor().value());
        Assert.assertEquals(WHITE, config.getBackgroundColors()[0].value());
        builder2.defaultBackgroundColor(new BackgroundColor(Color.WHITE)).page(newBlankPage());
        config = builder2.build();
        Assert.assertEquals(WHITE, config.getDefaultBackgroundColor().value());
        Assert.assertEquals(WHITE, config.getBackgroundColors()[1].value());
    }

    @Test
    public void testBackgroundColors() {
        int[] colors = new int[]{ Color.RED, Color.GREEN, Color.BLUE };
        for (int color : colors) {
            builder2.page(newBlankPage().background(new BackgroundColor(color)));
        }
        // False swipeToDismiss
        builder2.swipeToDismiss(false);
        BackgroundColor[] bgColors = builder2.build().getBackgroundColors();
        Assert.assertEquals(colors.length, bgColors.length);
        for (int i = 0; i < (colors.length); i++) {
            Assert.assertEquals(colors[i], bgColors[i].value());
        }
        setRtl(true);
        BackgroundColor[] reversedBg = builder2.build().getBackgroundColors();
        Assert.assertEquals(colors.length, reversedBg.length);
        int maxIndex = (colors.length) - 1;
        for (int i = maxIndex; i >= 0; i--) {
            Assert.assertEquals(colors[(maxIndex - i)], reversedBg[i].value());
        }
        // True swipeToDismiss - extra page should match color of last page
        setRtl(false);
        builder2.swipeToDismiss(true);
        bgColors = builder2.build().getBackgroundColors();
        Assert.assertEquals(((colors.length) + 1), bgColors.length);
        for (int i = 0; i < (bgColors.length); i++) {
            int j = i;
            if (j == (colors.length))
                j--;

            Assert.assertEquals(colors[j], bgColors[i].value());
        }
        setRtl(true);
        reversedBg = builder2.build().getBackgroundColors();
        Assert.assertEquals(((colors.length) + 1), reversedBg.length);
        maxIndex = (reversedBg.length) - 1;
        for (int i = maxIndex; i >= 0; i--) {
            int j = maxIndex - i;
            if (j == (colors.length)) {
                j--;
            }
            Assert.assertEquals(colors[j], reversedBg[i].value());
        }
    }
}

