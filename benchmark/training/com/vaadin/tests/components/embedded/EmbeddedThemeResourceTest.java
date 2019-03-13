package com.vaadin.tests.components.embedded;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.EmbeddedElement;
import com.vaadin.testbench.elements.ImageElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


/**
 * Tests that {@link Embedded} uses correct theme when the theme is set with
 * {@link #setTheme(String)}, and also updates correctly if theme is changed
 * later. {@link Image} is used as the baseline for correct behavior.
 *
 * @author Vaadin Ltd
 */
public class EmbeddedThemeResourceTest extends SingleBrowserTest {
    @Test
    public void testInitialTheme() {
        EmbeddedElement embedded = $(EmbeddedElement.class).first();
        ImageElement image = $(ImageElement.class).first();
        final String initial = image.getAttribute("src");
        Assert.assertFalse("ThemeResource image source uses default theme instead of set theme.", initial.contains("/reindeer/"));
        MatcherAssert.assertThat("Embedded and Image aren't using the same source for the image despite sharing the ThemeResource.", embedded.findElement(By.tagName("img")).getAttribute("src"), Matchers.is(initial));
    }

    @Test
    public void testUpdatedTheme() {
        final String initial = $(ImageElement.class).first().getAttribute("src");
        // update theme
        $(ButtonElement.class).first().click();
        waitForThemeToChange("reindeer");
        EmbeddedElement embedded = $(EmbeddedElement.class).first();
        // Re fetch as theme change creates new elements
        final ImageElement image = $(ImageElement.class).first();
        waitUntil(new org.openqa.selenium.support.ui.ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return !(initial.equals(image.getAttribute("src")));
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return ("image source to be updated (was: " + initial) + ")";
            }
        });
        Assert.assertTrue("ThemeResource image source didn't update correctly.", image.getAttribute("src").contains("/reindeer/"));
        MatcherAssert.assertThat("Embedded and Image aren't using the same source for the image despite sharing the ThemeResource.", embedded.findElement(By.tagName("img")).getAttribute("src"), Matchers.is(image.getAttribute("src")));
    }
}

