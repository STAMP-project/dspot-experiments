package io.github.luizgrp.sectionedrecyclerviewadapter;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/* Unit tests for SectionParameters */
@SuppressWarnings({ "PMD.MethodNamingConventions" })
public class SectionParametersTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void constructor_withValidParameters_throwsNoExceptions() {
        // Given
        final int itemId = 1;
        final int footerId = 3;
        final int loadingId = 5;
        final int emptyId = 6;
        // When
        SectionParameters sectionParameters = SectionParameters.builder().itemResourceId(itemId).headerViewWillBeProvided().footerResourceId(footerId).failedViewWillBeProvided().loadingResourceId(loadingId).emptyResourceId(emptyId).build();
        // Then
        Assert.assertThat(sectionParameters.itemResourceId, CoreMatchers.is(itemId));
        Assert.assertThat(sectionParameters.itemViewWillBeProvided, CoreMatchers.is(false));
        Assert.assertThat(sectionParameters.headerResourceId, CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(sectionParameters.headerViewWillBeProvided, CoreMatchers.is(true));
        Assert.assertThat(sectionParameters.footerResourceId, CoreMatchers.is(footerId));
        Assert.assertThat(sectionParameters.footerViewWillBeProvided, CoreMatchers.is(false));
        Assert.assertThat(sectionParameters.failedResourceId, CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(sectionParameters.failedViewWillBeProvided, CoreMatchers.is(true));
        Assert.assertThat(sectionParameters.loadingResourceId, CoreMatchers.is(loadingId));
        Assert.assertThat(sectionParameters.loadingViewWillBeProvided, CoreMatchers.is(false));
        Assert.assertThat(sectionParameters.emptyResourceId, CoreMatchers.is(emptyId));
        Assert.assertThat(sectionParameters.emptyViewWillBeProvided, CoreMatchers.is(false));
    }

    @Test
    public void legacyConstructor_withValidParameters_throwsNoExceptions() {
        // Given
        final int itemId = 1;
        final int footerId = 3;
        final int loadingId = 5;
        // When
        // intended for test
        @SuppressWarnings("deprecation")
        SectionParameters sectionParameters = new SectionParameters.Builder(itemId).headerViewWillBeProvided().footerResourceId(footerId).failedViewWillBeProvided().loadingResourceId(loadingId).emptyViewWillBeProvided().build();
        // Then
        Assert.assertThat(sectionParameters.itemResourceId, CoreMatchers.is(itemId));
        Assert.assertThat(sectionParameters.itemViewWillBeProvided, CoreMatchers.is(false));
        Assert.assertThat(sectionParameters.headerResourceId, CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(sectionParameters.headerViewWillBeProvided, CoreMatchers.is(true));
        Assert.assertThat(sectionParameters.footerResourceId, CoreMatchers.is(footerId));
        Assert.assertThat(sectionParameters.footerViewWillBeProvided, CoreMatchers.is(false));
        Assert.assertThat(sectionParameters.failedResourceId, CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(sectionParameters.failedViewWillBeProvided, CoreMatchers.is(true));
        Assert.assertThat(sectionParameters.loadingResourceId, CoreMatchers.is(loadingId));
        Assert.assertThat(sectionParameters.loadingViewWillBeProvided, CoreMatchers.is(false));
        Assert.assertThat(sectionParameters.emptyResourceId, CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(sectionParameters.emptyViewWillBeProvided, CoreMatchers.is(true));
    }

    @Test
    public void constructor_withConflictingItemParameters_throwsException() {
        // Given
        final int itemId = 1;
        final int footerId = 3;
        final int loadingId = 5;
        final int emptyId = 6;
        // Expect exception
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("itemResourceId and itemViewWillBeProvided cannot both be set");
        // When
        SectionParameters.builder().itemResourceId(itemId).itemViewWillBeProvided().headerViewWillBeProvided().footerResourceId(footerId).failedViewWillBeProvided().loadingResourceId(loadingId).emptyResourceId(emptyId).build();
    }

    @Test
    public void constructor_withoutMandatoryItemParameter_throwsException() {
        // Given
        final int footerId = 3;
        final int loadingId = 5;
        final int emptyId = 6;
        // Expect exception
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Either itemResourceId or itemViewWillBeProvided must be set");
        // When
        SectionParameters.builder().headerViewWillBeProvided().footerResourceId(footerId).failedViewWillBeProvided().loadingResourceId(loadingId).emptyResourceId(emptyId).build();
    }

    @Test
    public void constructor_withConflictingFooterParameters_throwsException() {
        // Given
        final int itemId = 1;
        final int footerId = 3;
        final int loadingId = 5;
        final int emptyId = 6;
        // Expect exception
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("footerResourceId and footerViewWillBeProvided cannot both be set");
        // When
        SectionParameters.builder().itemResourceId(itemId).headerViewWillBeProvided().footerResourceId(footerId).footerViewWillBeProvided().failedViewWillBeProvided().loadingResourceId(loadingId).emptyResourceId(emptyId).build();
    }
}

