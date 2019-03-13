package com.vaadin.tests.components.link;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.regex.Pattern;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static LinkInsideDisabledContainer.CLICK_COUNT_TEXT;


public class LinkInsideDisabledContainerTest extends MultiBrowserTest {
    private static final Pattern CLICK_MATCHER = Pattern.compile(((CLICK_COUNT_TEXT) + "(\\d+)"));

    @Test
    public void clickOnEnabledLinkInEnabledContainerShouldPerformAction() throws InterruptedException {
        clickLink();
        Assert.assertTrue(isLinkEnabled());
        Assert.assertThat(clicksOnLink(), CoreMatchers.is(1));
        clickLink();
        Assert.assertThat(clicksOnLink(), CoreMatchers.is(2));
    }

    @Test
    public void clickOnEnabledLinkInDisabledContainerShouldNotPerformAction() throws InterruptedException {
        disableContainer();
        clickLink();
        Assert.assertFalse(isLinkEnabled());
        Assert.assertThat(clicksOnLink(), CoreMatchers.is(0));
    }

    @Test
    public void linkShouldMaintainDisabledStatusWhenTogglingContainerEnabledStatus() throws InterruptedException {
        toggleLinkEnabledStatus();
        clickLink();
        Assert.assertFalse(isLinkEnabled());
        Assert.assertThat(clicksOnLink(), CoreMatchers.is(0));
        disableContainer();
        clickLink();
        Assert.assertFalse(isLinkEnabled());
        Assert.assertThat(clicksOnLink(), CoreMatchers.is(0));
        enableContainer();
        clickLink();
        Assert.assertFalse(isLinkEnabled());
        Assert.assertThat(clicksOnLink(), CoreMatchers.is(0));
    }

    @Test
    public void linkShouldMaintainEnabledStatusWhenTogglingContainerEnabledStatus() throws InterruptedException {
        clickLink();
        Assert.assertTrue(isLinkEnabled());
        Assert.assertThat(clicksOnLink(), CoreMatchers.is(1));
        disableContainer();
        clickLink();
        Assert.assertFalse(isLinkEnabled());
        Assert.assertThat(clicksOnLink(), CoreMatchers.is(1));
        enableContainer();
        clickLink();
        Assert.assertTrue(isLinkEnabled());
        Assert.assertThat(clicksOnLink(), CoreMatchers.is(2));
    }
}

