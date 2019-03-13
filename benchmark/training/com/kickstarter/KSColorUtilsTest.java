package com.kickstarter;


import com.kickstarter.libs.utils.KSColorUtils;
import junit.framework.TestCase;
import org.junit.Test;


public class KSColorUtilsTest extends KSRobolectricTestCase {
    @Test
    public void testSetAlpha() {
        TestCase.assertEquals(16777215, KSColorUtils.setAlpha(-1, 0));
        TestCase.assertEquals(-1, KSColorUtils.setAlpha(16777215, 255));
        TestCase.assertEquals(181193932, KSColorUtils.setAlpha(13421772, 10));
        TestCase.assertEquals(181193932, KSColorUtils.setAlpha(-3355444, 10));
    }

    @Test
    public void testDarken() {
        TestCase.assertEquals(-14829733, KSColorUtils.darken(-13902221, 0.2F));
    }

    @Test
    public void testDarkenWithAlpha() {
        TestCase.assertEquals(-870467749, KSColorUtils.darken(-869540237, 0.2F));
    }

    @Test
    public void testLighten() {
        TestCase.assertEquals(-11147889, KSColorUtils.lighten(-13902221, 0.2F));
    }

    @Test
    public void testLightenWithAlpha() {
        TestCase.assertEquals(-866785905, KSColorUtils.lighten(-869540237, 0.2F));
    }

    @Test
    public void testArtIsLight() {
        TestCase.assertTrue(KSColorUtils.isLight(-16981));
    }

    @Test
    public void testComicsIsLight() {
        TestCase.assertTrue(KSColorUtils.isLight(-1160));
    }

    @Test
    public void testCraftsIsNotLight() {
        TestCase.assertFalse(KSColorUtils.isLight(-32340));
    }

    @Test
    public void testDanceIsNotLight() {
        TestCase.assertFalse(KSColorUtils.isLight(-5859847));
    }

    @Test
    public void testDesignIsNotLight() {
        TestCase.assertFalse(KSColorUtils.isLight(-14200065));
    }

    @Test
    public void testFashionIsNotLight() {
        TestCase.assertFalse(KSColorUtils.isLight(-24618));
    }

    @Test
    public void testFilmAndVideoIsNotLight() {
        TestCase.assertFalse(KSColorUtils.isLight(-42642));
    }

    @Test
    public void testFoodIsNotLight() {
        TestCase.assertFalse(KSColorUtils.isLight(-51646));
    }

    @Test
    public void testGamesIsNotLight() {
        TestCase.assertFalse(KSColorUtils.isLight(-16725589));
    }

    @Test
    public void testJournalismIsNotLight() {
        TestCase.assertFalse(KSColorUtils.isLight(-15549206));
    }

    @Test
    public void testMusicIsLight() {
        TestCase.assertTrue(KSColorUtils.isLight(-5898285));
    }

    @Test
    public void testPhotographyIsNotLight() {
        TestCase.assertFalse(KSColorUtils.isLight(-16718875));
    }

    @Test
    public void testPublishingIsLight() {
        TestCase.assertTrue(KSColorUtils.isLight(-1909552));
    }

    @Test
    public void testTechnologyIsNotLight() {
        TestCase.assertFalse(KSColorUtils.isLight(-10250500));
    }

    @Test
    public void testTheaterIsNotLight() {
        TestCase.assertFalse(KSColorUtils.isLight(-33441));
    }

    @Test
    public void testBlackIsDark() {
        TestCase.assertTrue(KSColorUtils.isDark(-16777216));
    }

    @Test
    public void testWhiteIsNotDark() {
        TestCase.assertFalse(KSColorUtils.isDark(-1));
    }
}

