package io.github.luizgrp.sectionedrecyclerviewadapter;


import State.EMPTY;
import State.FAILED;
import State.LOADING;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/* Unit tests for Section */
@SuppressWarnings({ "PMD.MethodNamingConventions" })
public class SectionTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void constructor_withSectionParameters_constructsCorrectSection() {
        // Given
        final int itemId = 1;
        final int headerId = 2;
        final int footerId = 3;
        final int failedId = 4;
        final int loadingId = 5;
        final int emptyId = 6;
        @SuppressWarnings("ResourceType")
        SectionParameters sectionParameters = SectionParameters.builder().itemResourceId(itemId).headerResourceId(headerId).footerResourceId(footerId).failedResourceId(failedId).loadingResourceId(loadingId).emptyResourceId(emptyId).build();
        // When
        Section section = getSection(sectionParameters);
        // Then
        Assert.assertThat(section.getItemResourceId(), CoreMatchers.is(itemId));
        Assert.assertThat(section.isItemViewWillBeProvided(), CoreMatchers.is(false));
        Assert.assertThat(section.getHeaderResourceId(), CoreMatchers.is(headerId));
        Assert.assertThat(section.isHeaderViewWillBeProvided(), CoreMatchers.is(false));
        Assert.assertThat(section.getFooterResourceId(), CoreMatchers.is(footerId));
        Assert.assertThat(section.isFooterViewWillBeProvided(), CoreMatchers.is(false));
        Assert.assertThat(section.getFailedResourceId(), CoreMatchers.is(failedId));
        Assert.assertThat(section.isFailedViewWillBeProvided(), CoreMatchers.is(false));
        Assert.assertThat(section.getLoadingResourceId(), CoreMatchers.is(loadingId));
        Assert.assertThat(section.isLoadingViewWillBeProvided(), CoreMatchers.is(false));
        Assert.assertThat(section.getEmptyResourceId(), CoreMatchers.is(emptyId));
        Assert.assertThat(section.isEmptyViewWillBeProvided(), CoreMatchers.is(false));
        Assert.assertThat(section.hasHeader(), CoreMatchers.is(true));
        Assert.assertThat(section.hasFooter(), CoreMatchers.is(true));
    }

    @Test
    public void constructor_withProvidedSectionParameters_constructsCorrectSection() {
        // Given
        final int emptyId = 6;
        @SuppressWarnings("ResourceType")
        SectionParameters sectionParameters = SectionParameters.builder().itemViewWillBeProvided().headerViewWillBeProvided().loadingViewWillBeProvided().emptyResourceId(emptyId).build();
        // When
        Section section = getSection(sectionParameters);
        // Then
        Assert.assertThat(section.getItemResourceId(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(section.isItemViewWillBeProvided(), CoreMatchers.is(true));
        Assert.assertThat(section.getHeaderResourceId(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(section.isHeaderViewWillBeProvided(), CoreMatchers.is(true));
        Assert.assertThat(section.getFooterResourceId(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(section.isFooterViewWillBeProvided(), CoreMatchers.is(false));
        Assert.assertThat(section.getFailedResourceId(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(section.isFailedViewWillBeProvided(), CoreMatchers.is(false));
        Assert.assertThat(section.getLoadingResourceId(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(section.isLoadingViewWillBeProvided(), CoreMatchers.is(true));
        Assert.assertThat(section.getEmptyResourceId(), CoreMatchers.is(emptyId));
        Assert.assertThat(section.isEmptyViewWillBeProvided(), CoreMatchers.is(false));
        Assert.assertThat(section.hasHeader(), CoreMatchers.is(true));
        Assert.assertThat(section.hasFooter(), CoreMatchers.is(false));
    }

    @Test
    public void setState_withValidLoadingResId_succeeds() {
        // Given
        final int itemId = 1;
        final int loadingId = 2;
        @SuppressWarnings("ResourceType")
        SectionParameters sectionParameters = SectionParameters.builder().itemResourceId(itemId).loadingResourceId(loadingId).build();
        Section section = getSection(sectionParameters);
        // When
        section.setState(LOADING);
        // Then
        Assert.assertThat(section.getState(), CoreMatchers.is(LOADING));
    }

    @Test
    public void setState_withLoadingViewProvided_succeeds() {
        // Given
        final int itemId = 1;
        @SuppressWarnings("ResourceType")
        SectionParameters sectionParameters = SectionParameters.builder().itemResourceId(itemId).loadingViewWillBeProvided().build();
        Section section = getSection(sectionParameters);
        // When
        section.setState(LOADING);
        // Then
        Assert.assertThat(section.getState(), CoreMatchers.is(LOADING));
    }

    @Test
    public void setState_withMissingLoadingParameter_throwsException() {
        // Given
        final int itemId = 1;
        @SuppressWarnings("ResourceType")
        SectionParameters sectionParameters = SectionParameters.builder().itemResourceId(itemId).build();
        Section section = getSection(sectionParameters);
        // Expect exception
        expectedException.expect(IllegalStateException.class);
        // When
        section.setState(LOADING);
    }

    @Test
    public void setState_withMissingFailedResId_throwsException() {
        // Given
        final int itemId = 1;
        @SuppressWarnings("ResourceType")
        SectionParameters sectionParameters = SectionParameters.builder().itemResourceId(itemId).build();
        Section section = getSection(sectionParameters);
        // Expect exception
        expectedException.expect(IllegalStateException.class);
        // When
        section.setState(FAILED);
    }

    @Test
    public void setState_withEmptyFailedResId_throwsException() {
        // Given
        final int itemId = 1;
        @SuppressWarnings("ResourceType")
        SectionParameters sectionParameters = SectionParameters.builder().itemResourceId(itemId).build();
        Section section = getSection(sectionParameters);
        // Expect exception
        expectedException.expect(IllegalStateException.class);
        // When
        section.setState(EMPTY);
    }
}

