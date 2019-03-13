package com.stephentuso.welcome;


import Color.BLUE;
import Color.RED;
import R.color.wel_default_background_color;
import WelcomeConfiguration.Builder;
import WelcomeUtils.NO_COLOR_SET;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Created by stephentuso on 10/19/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class PagesTest extends ConfigurationTest {
    @DrawableRes
    private static final int DRAWABLE_RES = 24;

    @LayoutRes
    private static final int LAYOUT_RES = 25;

    private static final String DEFAULT_TITLE_TF = "default_title_typeface";

    private static final String DEFAULT_HEADER_TF = "default_header_typeface";

    private static final String DEFAULT_DESCRIPTION_TF = "default_description_typeface";

    Builder builder;

    @Test
    public void testBackgroundColor() {
        WelcomePage page = new BasicPage(PagesTest.DRAWABLE_RES, "Title", "Description");
        Assert.assertFalse(page.backgroundIsSet());
        page.background(wel_default_background_color);
        Assert.assertTrue(page.backgroundIsSet());
        Assert.assertEquals(new BackgroundColor(ConfigurationTest.DEFAULT_COLOR), page.getBackground(context));
        page.background(new BackgroundColor(ConfigurationTest.DEFAULT_COLOR));
        Assert.assertTrue(page.backgroundIsSet());
        Assert.assertEquals(new BackgroundColor(ConfigurationTest.DEFAULT_COLOR), page.getBackground(context));
    }

    @Test
    public void titlePage() {
        TitlePage page = new TitlePage(PagesTest.DRAWABLE_RES, "Title");
        Assert.assertEquals(PagesTest.DRAWABLE_RES, page.getDrawableResId());
        Assert.assertEquals("Title", page.getTitle());
        // Defaults
        Assert.assertTrue(page.getShowParallax());
        Assert.assertNull(page.getTitleTypefacePath());
        Assert.assertEquals(NO_COLOR_SET, page.getTitleColor());
        page.setup(builder.build());
        Assert.assertEquals(PagesTest.DEFAULT_TITLE_TF, page.getTitleTypefacePath());
        page.parallax(false).titleTypeface("title_typeface");
        Assert.assertFalse(page.getShowParallax());
        Assert.assertEquals("title_typeface", page.getTitleTypefacePath());
        page.titleColor(RED);
        Assert.assertEquals(RED, page.getTitleColor());
        page.titleColorResource(context, wel_default_background_color);
        Assert.assertEquals(ConfigurationTest.DEFAULT_COLOR, page.getTitleColor());
        page.setup(builder.build());
        Assert.assertEquals("title_typeface", page.getTitleTypefacePath());
    }

    @Test
    public void basicPage() {
        BasicPage page = new BasicPage(PagesTest.DRAWABLE_RES, "Title", "Description");
        Assert.assertEquals(PagesTest.DRAWABLE_RES, page.getDrawableResId());
        Assert.assertEquals("Title", page.getTitle());
        Assert.assertEquals("Description", page.getDescription());
        // Defaults
        Assert.assertTrue(page.getShowParallax());
        Assert.assertNull(page.getHeaderTypefacePath());
        Assert.assertNull(page.getDescriptionTypefacePath());
        Assert.assertEquals(NO_COLOR_SET, page.getHeaderColor());
        page.setup(builder.build());
        Assert.assertEquals(PagesTest.DEFAULT_HEADER_TF, page.getHeaderTypefacePath());
        Assert.assertEquals(PagesTest.DEFAULT_DESCRIPTION_TF, page.getDescriptionTypefacePath());
        page.parallax(false).headerTypeface("header_typeface").descriptionTypeface("description_typeface");
        Assert.assertFalse(page.getShowParallax());
        Assert.assertEquals("header_typeface", page.getHeaderTypefacePath());
        Assert.assertEquals("description_typeface", page.getDescriptionTypefacePath());
        page.headerColor(RED);
        Assert.assertEquals(RED, page.getHeaderColor());
        page.headerColorResource(context, wel_default_background_color);
        Assert.assertEquals(ConfigurationTest.DEFAULT_COLOR, page.getHeaderColor());
        page.descriptionColor(BLUE);
        Assert.assertEquals(BLUE, page.getDescriptionColor());
        page.descriptionColorResource(context, wel_default_background_color);
        Assert.assertEquals(ConfigurationTest.DEFAULT_COLOR, page.getDescriptionColor());
        page.setup(builder.build());
        Assert.assertEquals("header_typeface", page.getHeaderTypefacePath());
        Assert.assertEquals("description_typeface", page.getDescriptionTypefacePath());
    }

    @Test
    public void parallaxPage() {
        ParallaxPage page = new ParallaxPage(PagesTest.LAYOUT_RES, "Title", "Description");
        Assert.assertEquals(PagesTest.LAYOUT_RES, page.getLayoutResId());
        Assert.assertEquals("Title", page.getTitle());
        Assert.assertEquals("Description", page.getDescription());
        // Defaults
        Assert.assertEquals(0.2, page.getFirstParallaxFactor(), 0.001);
        Assert.assertEquals(1.0, page.getLastParallaxFactor(), 0.001);
        Assert.assertFalse(page.getParallaxRecursive());
        Assert.assertNull(page.getHeaderTypefacePath());
        Assert.assertNull(page.getDescriptionTyefacePath());
        Assert.assertEquals(NO_COLOR_SET, page.getHeaderColor());
        page.setup(builder.build());
        Assert.assertEquals(PagesTest.DEFAULT_HEADER_TF, page.getHeaderTypefacePath());
        Assert.assertEquals(PagesTest.DEFAULT_DESCRIPTION_TF, page.getDescriptionTyefacePath());
        page.firstParallaxFactor((-0.4F)).lastParallaxFactor(2.0F).recursive(true).headerTypeface("header_typeface").descriptionTypefacePath("description_typeface");
        Assert.assertEquals((-0.4F), page.getFirstParallaxFactor(), 0.001);
        Assert.assertEquals(2.0F, page.getLastParallaxFactor(), 0.001);
        Assert.assertTrue(page.getParallaxRecursive());
        Assert.assertEquals("header_typeface", page.getHeaderTypefacePath());
        Assert.assertEquals("description_typeface", page.getDescriptionTyefacePath());
        page.headerColor(RED);
        Assert.assertEquals(RED, page.getHeaderColor());
        page.headerColorResource(context, wel_default_background_color);
        Assert.assertEquals(ConfigurationTest.DEFAULT_COLOR, page.getHeaderColor());
        page.descriptionColor(BLUE);
        Assert.assertEquals(BLUE, page.getDescriptionColor());
        page.descriptionColorResource(context, wel_default_background_color);
        Assert.assertEquals(ConfigurationTest.DEFAULT_COLOR, page.getDescriptionColor());
        page.setup(builder.build());
        Assert.assertEquals("header_typeface", page.getHeaderTypefacePath());
        Assert.assertEquals("description_typeface", page.getDescriptionTyefacePath());
    }

    @Test
    public void fullscreenParallaxPage() {
        FullscreenParallaxPage page = new FullscreenParallaxPage(PagesTest.LAYOUT_RES);
        Assert.assertEquals(PagesTest.LAYOUT_RES, page.getLayoutResId());
        // Defaults
        Assert.assertEquals(0.2, page.getFirstParallaxFactor(), 0.001);
        Assert.assertEquals(1.0, page.getLastParallaxFactor(), 0.001);
        Assert.assertFalse(page.getParallaxRecursive());
        page.firstParallaxFactor(0.6F).lastParallaxFactor(1.8F).recursive(true);
        Assert.assertEquals(0.6F, page.getFirstParallaxFactor(), 0.001);
        Assert.assertEquals(1.8F, page.getLastParallaxFactor(), 0.001);
        Assert.assertTrue(page.getParallaxRecursive());
    }
}

