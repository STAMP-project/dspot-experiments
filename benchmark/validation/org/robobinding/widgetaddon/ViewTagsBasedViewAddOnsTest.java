package org.robobinding.widgetaddon;


import android.view.View;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.robobinding.viewattribute.ViewTag;
import org.robobinding.viewattribute.ViewTags;


/**
 *
 *
 * @since 1.0
 * @author Cheng Wei
 */
@RunWith(MockitoJUnitRunner.class)
public class ViewTagsBasedViewAddOnsTest {
    @Mock
    private ViewAddOnFactories factories;

    @Test
    public void givenViewAddOnNotSetOnTag_whenGetMostSuitable_thenReturnNewViewAddOn() {
        View view = Mockito.mock(View.class);
        ViewAddOn newViewAddOn = Mockito.mock(ViewAddOn.class);
        Mockito.when(factories.createViewAddOn(view)).thenReturn(newViewAddOn);
        ViewTag<ViewAddOn> viewTag = viewTagHasValue(false);
        ViewTags<ViewAddOn> viewTags = viewTagsFor(view, viewTag);
        ViewTagsBasedViewAddOns viewAddOns = new ViewTagsBasedViewAddOns(factories, viewTags);
        ViewAddOn actual = viewAddOns.getMostSuitable(view);
        Assert.assertThat(actual, Matchers.is(newViewAddOn));
    }

    @Test
    public void givenViewAddOnSetOnTag_whenGetMostSuitable_thenReturnOldViewAddOn() {
        View view = Mockito.mock(View.class);
        ViewAddOn oldViewAddOn = Mockito.mock(ViewAddOn.class);
        ViewTag<ViewAddOn> viewTag = viewTagHasValue(true);
        Mockito.when(viewTag.get()).thenReturn(oldViewAddOn);
        ViewTags<ViewAddOn> viewTags = viewTagsFor(view, viewTag);
        ViewTagsBasedViewAddOns viewAddOns = new ViewTagsBasedViewAddOns(factories, viewTags);
        ViewAddOn actual = viewAddOns.getMostSuitable(view);
        Assert.assertThat(actual, Matchers.is(oldViewAddOn));
    }

    @Test
    public void givenViewNotSupportTag_whenGetMostSuitable_thenReturnNewViewAddOn() {
        Object viewNotSupportTag = Mockito.mock(Object.class);
        ViewAddOn newViewAddOn = Mockito.mock(ViewAddOn.class);
        Mockito.when(factories.createViewAddOn(viewNotSupportTag)).thenReturn(newViewAddOn);
        ViewTagsBasedViewAddOns viewAddOns = new ViewTagsBasedViewAddOns(factories, null);
        ViewAddOn actual = viewAddOns.getMostSuitable(viewNotSupportTag);
        Assert.assertThat(actual, Matchers.is(newViewAddOn));
    }
}

