package io.github.luizgrp.sectionedrecyclerviewadapter;


import RecyclerView.ViewHolder;
import SectionParameters.Builder;
import State.EMPTY;
import State.FAILED;
import State.LOADING;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.Spy;

import static SectionedRecyclerViewAdapter.VIEW_TYPE_EMPTY;
import static SectionedRecyclerViewAdapter.VIEW_TYPE_FAILED;
import static SectionedRecyclerViewAdapter.VIEW_TYPE_FOOTER;
import static SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER;
import static SectionedRecyclerViewAdapter.VIEW_TYPE_ITEM_LOADED;
import static SectionedRecyclerViewAdapter.VIEW_TYPE_LOADING;


/* Unit tests for SectionedRecyclerViewAdapter, testing specifically whether the right get*View(), get*ViewHolder() and
onBind*ViewHolder() functions are called.
 */
@SuppressWarnings({ "PMD.MethodNamingConventions" })
public class SectionBindingTest {
    private static final int ITEMS_QTY = 10;

    private SectionedRecyclerViewAdapter sectionAdapter;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Spy
    private SectionBindingTest.SectionImpl dummySection = new SectionBindingTest.SectionImpl();// This one is first, so that the section of interest is second.


    @Spy
    private SectionBindingTest.SectionImpl section = new SectionBindingTest.SectionImpl();

    @Test
    public void onCreateViewHolder_withItemResourceId_calls_getItemViewHolder() {
        // When
        // noinspection ConstantConditions
        sectionAdapter.onCreateViewHolder(null, (6 + (VIEW_TYPE_ITEM_LOADED)));
        // Then
        getItemView(ArgumentMatchers.any(ViewGroup.class));
        Mockito.verify(dummySection, Mockito.never()).getItemViewHolder(ArgumentMatchers.any(View.class));
        getItemView(ArgumentMatchers.any(ViewGroup.class));
        Mockito.verify(section, Mockito.times(1)).getItemViewHolder(ArgumentMatchers.argThat(SectionBindingTest.hasTag((-1))));
    }

    @Test
    public void onCreateViewHolder_withItemViewProvided_calls_getItemView() {
        // Given
        Section providingSection = Mockito.spy(new SectionBindingTest.SectionImpl(SectionParameters.builder().itemViewWillBeProvided()));
        sectionAdapter.addSection(providingSection);// Third section, view types 12-17

        Mockito.doReturn(SectionBindingTest.dummyViewWithTag(42)).when(providingSection).getItemView(null);
        // When
        // noinspection ConstantConditions
        sectionAdapter.onCreateViewHolder(null, (12 + (VIEW_TYPE_ITEM_LOADED)));
        // Then
        getItemView(ArgumentMatchers.any(ViewGroup.class));
        Mockito.verify(section, Mockito.never()).getItemViewHolder(ArgumentMatchers.any(View.class));
        Mockito.verify(providingSection, Mockito.times(1)).getItemView(null);
        Mockito.verify(providingSection, Mockito.times(1)).getItemViewHolder(ArgumentMatchers.argThat(SectionBindingTest.hasTag(42)));
    }

    @Test
    public void onCreateViewHolder_withItemViewProvided_throws_when_getItemView_returns_null() {
        // Given
        Section providingSection = Mockito.spy(new SectionBindingTest.SectionImpl(SectionParameters.builder().itemViewWillBeProvided()));
        sectionAdapter.addSection(providingSection);// Third section, view types 12-17

        Mockito.doReturn(null).when(providingSection).getItemView(null);
        // Expect exception
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Section.getItemView() returned null");
        // When
        // noinspection ConstantConditions
        sectionAdapter.onCreateViewHolder(null, (12 + (VIEW_TYPE_ITEM_LOADED)));
    }

    @Test
    public void onCreateViewHolder_withHeaderResourceId_calls_getHeaderViewHolder() {
        // Given
        Section headedSection = Mockito.spy(new SectionBindingTest.SectionImpl(SectionParameters.builder().itemResourceId((-1)).headerResourceId((-2))));
        sectionAdapter.addSection(headedSection);
        // When
        // noinspection ConstantConditions
        sectionAdapter.onCreateViewHolder(null, (12 + (VIEW_TYPE_HEADER)));
        // Then
        getHeaderView(ArgumentMatchers.any(ViewGroup.class));
        getHeaderViewHolder(ArgumentMatchers.any(View.class));
        Mockito.verify(headedSection, Mockito.never()).getHeaderView(ArgumentMatchers.any(ViewGroup.class));
        Mockito.verify(headedSection, Mockito.times(1)).getHeaderViewHolder(ArgumentMatchers.argThat(SectionBindingTest.hasTag((-2))));
    }

    @Test
    public void onCreateViewHolder_withHeaderViewProvided_calls_getHeaderView() {
        // Given
        Section providingSection = Mockito.spy(new SectionBindingTest.SectionImpl(SectionParameters.builder().itemResourceId((-1)).headerViewWillBeProvided()));
        sectionAdapter.addSection(providingSection);// Third section, view types 12-17

        Mockito.doReturn(SectionBindingTest.dummyViewWithTag(42)).when(providingSection).getHeaderView(null);
        // When
        // noinspection ConstantConditions
        sectionAdapter.onCreateViewHolder(null, (12 + (VIEW_TYPE_HEADER)));
        // Then
        Mockito.verify(providingSection, Mockito.times(1)).getHeaderView(null);
        Mockito.verify(providingSection, Mockito.times(1)).getHeaderViewHolder(ArgumentMatchers.argThat(SectionBindingTest.hasTag(42)));
    }

    @Test
    public void onCreateViewHolder_withHeaderViewProvided_throws_when_getHeaderView_returns_null() {
        // Given
        Section providingSection = Mockito.spy(new SectionBindingTest.SectionImpl(SectionParameters.builder().itemResourceId((-1)).headerViewWillBeProvided()));
        sectionAdapter.addSection(providingSection);// Third section, view types 12-17

        Mockito.doReturn(null).when(providingSection).getHeaderView(null);
        // Expect exception
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Section.getHeaderView() returned null");
        // When
        // noinspection ConstantConditions
        sectionAdapter.onCreateViewHolder(null, (12 + (VIEW_TYPE_HEADER)));
    }

    @Test
    public void onCreateViewHolder_withFooterResourceId_calls_getFooterViewHolder() {
        // Given
        Section footedSection = Mockito.spy(new SectionBindingTest.SectionImpl(SectionParameters.builder().itemResourceId((-1)).footerResourceId((-3))));
        sectionAdapter.addSection(footedSection);
        // When
        // noinspection ConstantConditions
        sectionAdapter.onCreateViewHolder(null, (12 + (VIEW_TYPE_FOOTER)));
        // Then
        getFooterView(ArgumentMatchers.any(ViewGroup.class));
        getFooterViewHolder(ArgumentMatchers.any(View.class));
        Mockito.verify(footedSection, Mockito.never()).getFooterView(ArgumentMatchers.any(ViewGroup.class));
        Mockito.verify(footedSection, Mockito.times(1)).getFooterViewHolder(ArgumentMatchers.argThat(SectionBindingTest.hasTag((-3))));
    }

    @Test
    public void onCreateViewHolder_withFooterViewProvided_calls_getFooterView() {
        // Given
        Section providingSection = Mockito.spy(new SectionBindingTest.SectionImpl(SectionParameters.builder().itemResourceId((-1)).footerViewWillBeProvided()));
        sectionAdapter.addSection(providingSection);// Third section, view types 12-17

        Mockito.doReturn(SectionBindingTest.dummyViewWithTag(42)).when(providingSection).getFooterView(null);
        // When
        // noinspection ConstantConditions
        sectionAdapter.onCreateViewHolder(null, (12 + (VIEW_TYPE_FOOTER)));
        // Then
        Mockito.verify(providingSection, Mockito.times(1)).getFooterView(null);
        Mockito.verify(providingSection, Mockito.times(1)).getFooterViewHolder(ArgumentMatchers.argThat(SectionBindingTest.hasTag(42)));
    }

    @Test
    public void onCreateViewHolder_withFooterViewProvided_throws_when_getFooterView_returns_null() {
        // Given
        Section providingSection = Mockito.spy(new SectionBindingTest.SectionImpl(SectionParameters.builder().itemResourceId((-1)).footerViewWillBeProvided()));
        sectionAdapter.addSection(providingSection);// Third section, view types 12-17

        Mockito.doReturn(null).when(providingSection).getFooterView(null);
        // Expect exception
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Section.getFooterView() returned null");
        // When
        // noinspection ConstantConditions
        sectionAdapter.onCreateViewHolder(null, (12 + (VIEW_TYPE_FOOTER)));
    }

    @Test
    public void onCreateViewHolder_withLoadingResourceId_calls_getLoadingViewHolder() {
        // Given
        Section resourceSection = Mockito.spy(new SectionBindingTest.SectionImpl(SectionParameters.builder().itemResourceId((-1)).loadingResourceId((-5))));
        sectionAdapter.addSection(resourceSection);// Third section, view types 12-17

        // When
        // noinspection ConstantConditions
        sectionAdapter.onCreateViewHolder(null, (12 + (VIEW_TYPE_LOADING)));
        // Then
        Mockito.verify(resourceSection, Mockito.never()).getLoadingView(ArgumentMatchers.any(ViewGroup.class));
        Mockito.verify(resourceSection, Mockito.times(1)).getLoadingViewHolder(ArgumentMatchers.argThat(SectionBindingTest.hasTag((-5))));
    }

    @Test
    public void onCreateViewHolder_withLoadingViewProvided_calls_getLoadingView() {
        // Given
        Section providingSection = Mockito.spy(new SectionBindingTest.SectionImpl(SectionParameters.builder().itemResourceId((-1)).loadingViewWillBeProvided()));
        sectionAdapter.addSection(providingSection);// Third section, view types 12-17

        Mockito.doReturn(SectionBindingTest.dummyViewWithTag(42)).when(providingSection).getLoadingView(null);
        // When
        // noinspection ConstantConditions
        sectionAdapter.onCreateViewHolder(null, (12 + (VIEW_TYPE_LOADING)));
        // Then
        Mockito.verify(providingSection, Mockito.times(1)).getLoadingView(null);
        Mockito.verify(providingSection, Mockito.times(1)).getLoadingViewHolder(ArgumentMatchers.argThat(SectionBindingTest.hasTag(42)));
    }

    @Test
    public void onCreateViewHolder_withLoadingViewProvided_throws_when_getLoadingView_returns_null() {
        // Given
        Section providingSection = Mockito.spy(new SectionBindingTest.SectionImpl(SectionParameters.builder().itemResourceId((-1)).loadingViewWillBeProvided()));
        sectionAdapter.addSection(providingSection);// Third section, view types 12-17

        Mockito.doReturn(null).when(providingSection).getLoadingView(null);
        // Expect exception
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Section.getLoadingView() returned null");
        // When
        // noinspection ConstantConditions
        sectionAdapter.onCreateViewHolder(null, (12 + (VIEW_TYPE_LOADING)));
    }

    @Test
    public void onCreateViewHolder_withFailedResourceId_calls_getFailedViewHolder() {
        // Given
        Section resourceSection = Mockito.spy(new SectionBindingTest.SectionImpl(SectionParameters.builder().itemResourceId((-1)).loadingResourceId((-5)).failedResourceId((-6))));
        sectionAdapter.addSection(resourceSection);// Third section, view types 12-17

        // When
        // noinspection ConstantConditions
        sectionAdapter.onCreateViewHolder(null, (12 + (VIEW_TYPE_FAILED)));
        // Then
        Mockito.verify(resourceSection, Mockito.never()).getFailedView(ArgumentMatchers.any(ViewGroup.class));
        Mockito.verify(resourceSection, Mockito.times(1)).getFailedViewHolder(ArgumentMatchers.argThat(SectionBindingTest.hasTag((-6))));
    }

    @Test
    public void onCreateViewHolder_withFailedViewProvided_calls_getFailedView() {
        // Given
        Section providingSection = Mockito.spy(new SectionBindingTest.SectionImpl(SectionParameters.builder().itemResourceId((-1)).failedViewWillBeProvided()));
        sectionAdapter.addSection(providingSection);// Third section, view types 12-17

        Mockito.doReturn(SectionBindingTest.dummyViewWithTag(42)).when(providingSection).getFailedView(null);
        // When
        // noinspection ConstantConditions
        sectionAdapter.onCreateViewHolder(null, (12 + (VIEW_TYPE_FAILED)));
        // Then
        Mockito.verify(providingSection, Mockito.times(1)).getFailedView(null);
        Mockito.verify(providingSection, Mockito.times(1)).getFailedViewHolder(ArgumentMatchers.argThat(SectionBindingTest.hasTag(42)));
    }

    @Test
    public void onCreateViewHolder_withFailedViewProvided_throws_when_getFailedView_returns_null() {
        // Given
        Section providingSection = Mockito.spy(new SectionBindingTest.SectionImpl(SectionParameters.builder().itemResourceId((-1)).failedViewWillBeProvided()));
        sectionAdapter.addSection(providingSection);// Third section, view types 12-17

        Mockito.doReturn(null).when(providingSection).getFailedView(null);
        // Expect exception
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Section.getFailedView() returned null");
        // When
        // noinspection ConstantConditions
        sectionAdapter.onCreateViewHolder(null, (12 + (VIEW_TYPE_FAILED)));
    }

    @Test
    public void onCreateViewHolder_withEmptyResourceId_calls_getEmptyViewHolder() {
        // When
        // noinspection ConstantConditions
        sectionAdapter.onCreateViewHolder(null, (6 + (VIEW_TYPE_EMPTY)));
        // Then
        getEmptyView(ArgumentMatchers.any(ViewGroup.class));
        Mockito.verify(section, Mockito.times(1)).getEmptyViewHolder(ArgumentMatchers.argThat(SectionBindingTest.hasTag((-1))));
    }

    @Test
    public void onCreateViewHolder_withEmptyViewProvided_calls_getEmptyView() {
        // Given
        Section providingSection = Mockito.spy(new SectionBindingTest.SectionImpl(SectionParameters.builder().itemResourceId((-1)).emptyViewWillBeProvided()));
        sectionAdapter.addSection(providingSection);// Third section, view types 12-17

        Mockito.doReturn(SectionBindingTest.dummyViewWithTag(42)).when(providingSection).getEmptyView(null);
        // When
        // noinspection ConstantConditions
        sectionAdapter.onCreateViewHolder(null, (12 + (VIEW_TYPE_EMPTY)));
        // Then
        Mockito.verify(providingSection, Mockito.times(1)).getEmptyView(null);
        Mockito.verify(providingSection, Mockito.times(1)).getEmptyViewHolder(ArgumentMatchers.argThat(SectionBindingTest.hasTag(42)));
    }

    @Test
    public void onCreateViewHolder_withEmptyViewProvided_throws_when_getEmptyView_returns_null() {
        // Given
        Section providingSection = Mockito.spy(new SectionBindingTest.SectionImpl(SectionParameters.builder().itemResourceId((-1)).emptyViewWillBeProvided()));
        sectionAdapter.addSection(providingSection);// Third section, view types 12-17

        Mockito.doReturn(null).when(providingSection).getEmptyView(null);
        // Expect exception
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Section.getEmptyView() returned null");
        // When
        // noinspection ConstantConditions
        sectionAdapter.onCreateViewHolder(null, (12 + (VIEW_TYPE_EMPTY)));
    }

    @Test
    public void onBindViewHolder_withSection_isCalled() {
        // When
        // Section - Items [10-19]
        // noinspection ConstantConditions
        sectionAdapter.onBindViewHolder(null, 10);
        // Then
        Mockito.verify(section, Mockito.times(1)).onBindItemViewHolder(null, 0);
    }

    @Test
    public void onBindLoadingViewHolder_withSection_isCalled() {
        // Given
        section.setState(LOADING);
        // When
        // Section - Loading [10]
        // noinspection ConstantConditions
        sectionAdapter.onBindViewHolder(null, 10);
        // Then
        onBindLoadingViewHolder(null);
    }

    @Test
    public void onBindFailedViewHolder_withSection_isCalled() {
        // Given
        section.setState(FAILED);
        // When
        // Section - Failed [10]
        // noinspection ConstantConditions
        sectionAdapter.onBindViewHolder(null, 10);
        // Then
        onBindFailedViewHolder(null);
    }

    @Test
    public void onBindEmptyViewHolder_withSection_isCalled() {
        // Given
        section.setState(EMPTY);
        // When
        // Section - Empty [10]
        // noinspection ConstantConditions
        sectionAdapter.onBindViewHolder(null, 10);
        // Then
        onBindEmptyViewHolder(null);
    }

    @Test
    public void onBindViewHolder_withHeadedSection_isCalled() {
        // Given
        Section headedSection = Mockito.spy(new SectionBindingTest.SectionImpl(SectionParameters.builder().itemResourceId((-1)).headerResourceId((-1))));
        sectionAdapter.addSection(headedSection);// Third section, items 10-19

        // When
        // HeadedSection - Header [20]
        // noinspection ConstantConditions
        sectionAdapter.onBindViewHolder(null, 20);
        // HeadedSection - Items [21-30]
        // noinspection ConstantConditions
        sectionAdapter.onBindViewHolder(null, 22);
        // Then
        Mockito.verify(headedSection, Mockito.times(1)).onBindHeaderViewHolder(null);
        Mockito.verify(headedSection, Mockito.times(1)).onBindItemViewHolder(null, (22 - 21));
    }

    @Test
    public void onBindViewHolder_withFootedSection_isCalled() {
        // Given
        Section footedSection = Mockito.spy(new SectionBindingTest.SectionImpl(SectionParameters.builder().itemResourceId((-1)).footerViewWillBeProvided()));
        sectionAdapter.addSection(footedSection);// Third section, items 10-19

        // When
        // FootedSection - Items [20-29]
        // noinspection ConstantConditions
        sectionAdapter.onBindViewHolder(null, 25);
        // FootedSection - Footer [30]
        // noinspection ConstantConditions
        sectionAdapter.onBindViewHolder(null, 30);
        // Then
        Mockito.verify(footedSection, Mockito.times(1)).onBindItemViewHolder(null, (25 - 20));
        Mockito.verify(footedSection, Mockito.times(1)).onBindFooterViewHolder(null);
    }

    @Test
    public void onBindViewHolder_withHeadedFootedSection_isCalled() {
        // Given
        Section headedFootedSection = Mockito.spy(new SectionBindingTest.SectionImpl(SectionParameters.builder().itemViewWillBeProvided().headerViewWillBeProvided().footerResourceId((-1))));
        sectionAdapter.addSection(headedFootedSection);// Third section, items 10-19

        // When
        // HeadedFootedSection - Header [20]
        // noinspection ConstantConditions
        sectionAdapter.onBindViewHolder(null, 20);
        // HeadedFootedSection - Items [21-30]
        // noinspection ConstantConditions
        sectionAdapter.onBindViewHolder(null, 21);
        // noinspection ConstantConditions
        sectionAdapter.onBindViewHolder(null, 28);
        // HeadedFootedSection - Footer [31]
        // noinspection ConstantConditions
        sectionAdapter.onBindViewHolder(null, 31);
        // Then
        Mockito.verify(headedFootedSection, Mockito.times(1)).onBindHeaderViewHolder(null);
        Mockito.verify(headedFootedSection, Mockito.times(1)).onBindItemViewHolder(null, 0);
        Mockito.verify(headedFootedSection, Mockito.times(1)).onBindItemViewHolder(null, (28 - 21));
        Mockito.verify(headedFootedSection, Mockito.times(1)).onBindFooterViewHolder(null);
    }

    private static class SectionImpl extends Section {
        SectionImpl(SectionParameters.Builder builder) {
            super(builder.build());
        }

        SectionImpl() {
            super(SectionParameters.builder().itemResourceId((-1)).emptyResourceId((-1)).failedViewWillBeProvided().loadingViewWillBeProvided().build());
        }

        @Override
        public int getContentItemsTotal() {
            return SectionBindingTest.ITEMS_QTY;
        }

        @Override
        public ViewHolder getItemViewHolder(View view) {
            return null;
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        }
    }

    /**
     * Override the inflate method in order to avoid mocking the LayoutInflater.
     */
    private static class AdapterImpl extends SectionedRecyclerViewAdapter {
        @Override
        View inflate(int layoutResourceId, ViewGroup parent) {
            return SectionBindingTest.dummyViewWithTag(layoutResourceId);
        }
    }
}

