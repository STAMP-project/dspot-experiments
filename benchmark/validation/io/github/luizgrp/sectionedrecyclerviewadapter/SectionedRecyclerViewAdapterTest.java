package io.github.luizgrp.sectionedrecyclerviewadapter;


import SectionedRecyclerViewAdapter.VIEW_TYPE_EMPTY;
import SectionedRecyclerViewAdapter.VIEW_TYPE_FAILED;
import SectionedRecyclerViewAdapter.VIEW_TYPE_FOOTER;
import SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER;
import SectionedRecyclerViewAdapter.VIEW_TYPE_ITEM_LOADED;
import SectionedRecyclerViewAdapter.VIEW_TYPE_LOADING;
import State.EMPTY;
import State.FAILED;
import State.LOADING;
import io.github.luizgrp.sectionedrecyclerviewadapter.testdoubles.stub.HeadedFootedStatelessSectionStub;
import io.github.luizgrp.sectionedrecyclerviewadapter.testdoubles.stub.SectionStub;
import io.github.luizgrp.sectionedrecyclerviewadapter.testdoubles.stub.StatelessSectionStub;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static SectionedRecyclerViewAdapter.VIEW_TYPE_EMPTY;
import static SectionedRecyclerViewAdapter.VIEW_TYPE_FAILED;
import static SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER;
import static SectionedRecyclerViewAdapter.VIEW_TYPE_ITEM_LOADED;
import static SectionedRecyclerViewAdapter.VIEW_TYPE_LOADING;


/* Unit tests for SectionedRecyclerViewAdapter */
@SuppressWarnings({ "PMD.MethodNamingConventions" })
public class SectionedRecyclerViewAdapterTest {
    private static final int ITEMS_QTY = 10;

    private static final String SECTION_TAG = "tag";

    private SectionedRecyclerViewAdapter sectionAdapter;

    @Test(expected = IndexOutOfBoundsException.class)
    public void getPositionInSection_withEmptyAdapter_throwsException() {
        // When
        sectionAdapter.getPositionInSection(0);
    }

    @Test
    public void getPositionInSection_withAdapterWithInvisibleSection_returnsCorrectPosition() {
        // Given
        addHeadedFootedStatelessSectionStubToAdapter();
        addInvisibleStatelessSectionStubToAdapter();
        addHeadedFootedStatelessSectionStubToAdapter();
        // When
        int result = sectionAdapter.getPositionInSection(13);
        int result2 = sectionAdapter.getPositionInSection(22);
        // Then
        Assert.assertThat(result, Is.is(0));
        Assert.assertThat(result2, Is.is(9));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSectionPositionUsingTag_withEmptyAdapter_throwsException() {
        // When
        sectionAdapter.getSectionPosition(SectionedRecyclerViewAdapterTest.SECTION_TAG);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSectionPositionUsingTag_withInvalidTag_throwsException() {
        // Given
        addStatelessSectionStubToAdapter();
        addStatelessSectionStubToAdapter();
        // When
        sectionAdapter.getSectionPosition(SectionedRecyclerViewAdapterTest.SECTION_TAG);
    }

    @Test
    public void getSectionPositionUsingTag_callsGetSectionPositionUsingSection() {
        // Given
        SectionedRecyclerViewAdapter spySectionedRecyclerViewAdapter = Mockito.spy(SectionedRecyclerViewAdapter.class);
        SectionStub sectionStub = new SectionStub(SectionedRecyclerViewAdapterTest.ITEMS_QTY);
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterTest.SECTION_TAG, sectionStub);
        // When
        spySectionedRecyclerViewAdapter.getSectionPosition(SectionedRecyclerViewAdapterTest.SECTION_TAG);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).getSectionPosition(sectionStub);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSectionPositionUsingSection_withEmptyAdapter_throwsException() {
        // When
        sectionAdapter.getSectionPosition(new StatelessSectionStub(SectionedRecyclerViewAdapterTest.ITEMS_QTY));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSectionPositionUsingSection_withInvalidSection_throwsException() {
        // Given
        addStatelessSectionStubToAdapter();
        addStatelessSectionStubToAdapter();
        // When
        sectionAdapter.getSectionPosition(new StatelessSectionStub(SectionedRecyclerViewAdapterTest.ITEMS_QTY));
    }

    @Test
    public void getSectionPositionUsingSection_withAdapterWithInvisibleSection_returnsCorrectPosition() {
        // Given
        addStatelessSectionStubToAdapter();
        addInvisibleStatelessSectionStubToAdapter();
        StatelessSectionStub statelessSectionStub = new StatelessSectionStub(SectionedRecyclerViewAdapterTest.ITEMS_QTY);
        sectionAdapter.addSection(statelessSectionStub);
        // When
        int result = sectionAdapter.getSectionPosition(statelessSectionStub);
        // Then
        Assert.assertThat(result, Is.is(10));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void onBindViewHolder_withEmptyAdapter_throwsException() {
        // When
        // noinspection ConstantConditions
        sectionAdapter.onBindViewHolder(null, 0);
    }

    @Test
    public void addSectionUsingSection_withEmptyAdapter_succeeds() {
        // Given
        Section section = new StatelessSectionStub(SectionedRecyclerViewAdapterTest.ITEMS_QTY);
        // When
        String result = sectionAdapter.addSection(section);
        // Then
        Assert.assertThat(sectionAdapter.getSection(result), Is.is(section));
        Assert.assertThat(sectionAdapter.getCopyOfSectionsMap().get(result), Is.is(section));
    }

    @Test
    public void addSectionUsingTag_withEmptyAdapter_succeeds() {
        // Given
        Section section = new StatelessSectionStub(SectionedRecyclerViewAdapterTest.ITEMS_QTY);
        // When
        sectionAdapter.addSection(SectionedRecyclerViewAdapterTest.SECTION_TAG, section);
        // Then
        Assert.assertThat(sectionAdapter.getSection(SectionedRecyclerViewAdapterTest.SECTION_TAG), Is.is(section));
        Assert.assertThat(sectionAdapter.getCopyOfSectionsMap().get(SectionedRecyclerViewAdapterTest.SECTION_TAG), Is.is(section));
    }

    @Test
    public void getSectionWithTag_withRemovedSection_returnsNull() {
        // Given
        sectionAdapter.addSection(SectionedRecyclerViewAdapterTest.SECTION_TAG, new StatelessSectionStub(SectionedRecyclerViewAdapterTest.ITEMS_QTY));
        sectionAdapter.removeSection(SectionedRecyclerViewAdapterTest.SECTION_TAG);
        // When
        Section result = sectionAdapter.getSection(SectionedRecyclerViewAdapterTest.SECTION_TAG);
        // Then
        Assert.assertNull(result);
    }

    @Test
    public void getSectionWithTag_withEmptyAdapter_returnsNull() {
        // When
        Section result = sectionAdapter.getSection(SectionedRecyclerViewAdapterTest.SECTION_TAG);
        // Then
        Assert.assertNull(result);
    }

    @Test
    public void removeSectionWithTag_withEmptyAdapter_failsSilently() {
        // When
        sectionAdapter.removeSection(SectionedRecyclerViewAdapterTest.SECTION_TAG);
        // Then
        Assert.assertTrue(sectionAdapter.getCopyOfSectionsMap().isEmpty());
    }

    @Test
    public void removeSectionWithTag_withInvalidTag_doesNotRemoveAnything() {
        // Given
        addFourStatelessSectionsAndFourSectionsToAdapter();
        // When
        sectionAdapter.removeSection(SectionedRecyclerViewAdapterTest.SECTION_TAG);
        // Then
        Assert.assertThat(sectionAdapter.getCopyOfSectionsMap().size(), Is.is(8));
        Assert.assertNull(sectionAdapter.getSection(SectionedRecyclerViewAdapterTest.SECTION_TAG));
    }

    @Test
    public void removeSectionWithTag_withAdapterWithManySections_succeeds() {
        // Given
        addFourStatelessSectionsAndFourSectionsToAdapter();
        sectionAdapter.addSection(SectionedRecyclerViewAdapterTest.SECTION_TAG, new StatelessSectionStub(SectionedRecyclerViewAdapterTest.ITEMS_QTY));
        // When
        sectionAdapter.removeSection(SectionedRecyclerViewAdapterTest.SECTION_TAG);
        // Then
        Assert.assertThat(sectionAdapter.getCopyOfSectionsMap().size(), Is.is(8));
        Assert.assertNull(sectionAdapter.getSection(SectionedRecyclerViewAdapterTest.SECTION_TAG));
    }

    @Test
    public void removeSection_withEmptyAdapter_failsSilently() {
        // When
        sectionAdapter.removeSection(new SectionStub(SectionedRecyclerViewAdapterTest.ITEMS_QTY));
        // Then
        Assert.assertTrue(sectionAdapter.getCopyOfSectionsMap().isEmpty());
    }

    @Test
    public void removeSection_withInvalidSection_doesNotRemoveAnything() {
        // Given
        addFourStatelessSectionsAndFourSectionsToAdapter();
        // When
        sectionAdapter.removeSection(new SectionStub(SectionedRecyclerViewAdapterTest.ITEMS_QTY));
        // Then
        Assert.assertThat(sectionAdapter.getCopyOfSectionsMap().size(), Is.is(8));
    }

    @Test
    public void removeSection_withAdapterWithManySections_succeeds() {
        // Given
        addFourStatelessSectionsAndFourSectionsToAdapter();
        final StatelessSectionStub section = new StatelessSectionStub(SectionedRecyclerViewAdapterTest.ITEMS_QTY);
        sectionAdapter.addSection(SectionedRecyclerViewAdapterTest.SECTION_TAG, section);
        // When
        sectionAdapter.removeSection(section);
        // Then
        Assert.assertThat(sectionAdapter.getCopyOfSectionsMap().size(), Is.is(8));
        Assert.assertNull(sectionAdapter.getSection(SectionedRecyclerViewAdapterTest.SECTION_TAG));
    }

    @Test
    public void getItemCount_withEmptyAdapter_isZero() {
        // When
        int result = sectionAdapter.getItemCount();
        // Then
        Assert.assertThat(result, Is.is(0));
    }

    @Test
    public void getItemCount_withAdapterWithInvisibleSection_returnsCorrectQuantity() {
        // Given
        addStatelessSectionStubToAdapter();
        addInvisibleStatelessSectionStubToAdapter();
        // When
        int result = sectionAdapter.getItemCount();
        // Then
        Assert.assertThat(result, Is.is(SectionedRecyclerViewAdapterTest.ITEMS_QTY));
    }

    @Test
    public void getCopyOfSectionsMap_withEmptyAdapter_isEmpty() {
        // When
        boolean result = sectionAdapter.getCopyOfSectionsMap().isEmpty();
        // Then
        Assert.assertTrue(result);
    }

    @Test
    public void getCopyOfSectionsMap_withAdapterWithInvisibleSection_hasCorrectSize() {
        // Given
        addStatelessSectionStubToAdapter();
        addInvisibleStatelessSectionStubToAdapter();
        // When
        int result = sectionAdapter.getCopyOfSectionsMap().size();
        // Then
        Assert.assertThat(result, Is.is(2));
    }

    @Test
    public void getSection_withEmptyAdapter_isNull() {
        // When
        Section result = sectionAdapter.getSection(SectionedRecyclerViewAdapterTest.SECTION_TAG);
        // Then
        Assert.assertNull(result);
    }

    @Test
    public void getSection_withAdapterWithManySections_returnsCorrectSection() {
        // Given
        addStatelessSectionStubToAdapter();
        Section section = new StatelessSectionStub(SectionedRecyclerViewAdapterTest.ITEMS_QTY);
        sectionAdapter.addSection(SectionedRecyclerViewAdapterTest.SECTION_TAG, section);
        addStatelessSectionStubToAdapter();
        // When
        Section result = sectionAdapter.getSection(SectionedRecyclerViewAdapterTest.SECTION_TAG);
        // Then
        Assert.assertThat(result, Is.is(section));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getSectionForPosition_withEmptyAdapter_throwsException() {
        // When
        sectionAdapter.getSectionForPosition(0);
    }

    @Test
    public void getSectionForPosition_withAdapterWithManySections_returnsCorrectSection() {
        // Given
        Section sectionStub1 = addStatelessSectionStubToAdapter();
        addInvisibleStatelessSectionStubToAdapter();
        Section sectionStub2 = addHeadedFootedStatelessSectionStubToAdapter();
        Section sectionStub3 = addStatelessSectionStubToAdapter();
        // When
        Section result = sectionAdapter.getSectionForPosition(0);
        Section result2 = sectionAdapter.getSectionForPosition(9);
        Section result3 = sectionAdapter.getSectionForPosition(10);
        Section result4 = sectionAdapter.getSectionForPosition(11);
        Section result5 = sectionAdapter.getSectionForPosition(20);
        Section result6 = sectionAdapter.getSectionForPosition(21);
        Section result7 = sectionAdapter.getSectionForPosition(22);
        Section result8 = sectionAdapter.getSectionForPosition(31);
        // Then
        Assert.assertThat(result, Is.is(sectionStub1));
        Assert.assertThat(result2, Is.is(sectionStub1));
        Assert.assertThat(result3, Is.is(sectionStub2));
        Assert.assertThat(result4, Is.is(sectionStub2));
        Assert.assertThat(result5, Is.is(sectionStub2));
        Assert.assertThat(result6, Is.is(sectionStub2));
        Assert.assertThat(result7, Is.is(sectionStub3));
        Assert.assertThat(result8, Is.is(sectionStub3));
    }

    @Test
    public void getSectionItemViewTypeForAdapterViewType_withViewTypesFrom0to5() {
        // Given
        int viewTypeHeader = 0;
        int viewTypeFooter = 1;
        int viewTypeItemLoaded = 2;
        int viewTypeLoading = 3;
        int viewTypeFailed = 4;
        int viewTypeEmpty = 5;
        // When
        int resultHeader = sectionAdapter.getSectionItemViewTypeForAdapterViewType(viewTypeHeader);
        int resultFooter = sectionAdapter.getSectionItemViewTypeForAdapterViewType(viewTypeFooter);
        int resultItemLoaded = sectionAdapter.getSectionItemViewTypeForAdapterViewType(viewTypeItemLoaded);
        int resultLoading = sectionAdapter.getSectionItemViewTypeForAdapterViewType(viewTypeLoading);
        int resultFailed = sectionAdapter.getSectionItemViewTypeForAdapterViewType(viewTypeFailed);
        int resultEmpty = sectionAdapter.getSectionItemViewTypeForAdapterViewType(viewTypeEmpty);
        // Then
        Assert.assertThat(resultHeader, Is.is(VIEW_TYPE_HEADER));
        Assert.assertThat(resultFooter, Is.is(VIEW_TYPE_FOOTER));
        Assert.assertThat(resultItemLoaded, Is.is(VIEW_TYPE_ITEM_LOADED));
        Assert.assertThat(resultLoading, Is.is(VIEW_TYPE_LOADING));
        Assert.assertThat(resultFailed, Is.is(VIEW_TYPE_FAILED));
        Assert.assertThat(resultEmpty, Is.is(VIEW_TYPE_EMPTY));
    }

    @Test
    public void getSectionItemViewTypeForAdapterViewType_withViewTypesFrom12to17() {
        // Given
        int viewTypeHeader = 12;
        int viewTypeFooter = 13;
        int viewTypeItemLoaded = 14;
        int viewTypeLoading = 15;
        int viewTypeFailed = 16;
        int viewTypeEmpty = 17;
        // When
        int resultHeader = sectionAdapter.getSectionItemViewTypeForAdapterViewType(viewTypeHeader);
        int resultFooter = sectionAdapter.getSectionItemViewTypeForAdapterViewType(viewTypeFooter);
        int resultItemLoaded = sectionAdapter.getSectionItemViewTypeForAdapterViewType(viewTypeItemLoaded);
        int resultLoading = sectionAdapter.getSectionItemViewTypeForAdapterViewType(viewTypeLoading);
        int resultFailed = sectionAdapter.getSectionItemViewTypeForAdapterViewType(viewTypeFailed);
        int resultEmpty = sectionAdapter.getSectionItemViewTypeForAdapterViewType(viewTypeEmpty);
        // Then
        Assert.assertThat(resultHeader, Is.is(VIEW_TYPE_HEADER));
        Assert.assertThat(resultFooter, Is.is(VIEW_TYPE_FOOTER));
        Assert.assertThat(resultItemLoaded, Is.is(VIEW_TYPE_ITEM_LOADED));
        Assert.assertThat(resultLoading, Is.is(VIEW_TYPE_LOADING));
        Assert.assertThat(resultFailed, Is.is(VIEW_TYPE_FAILED));
        Assert.assertThat(resultEmpty, Is.is(VIEW_TYPE_EMPTY));
    }

    @Test
    public void getSectionItemViewTypeForAdapterViewType() {
        // Given
        int viewTypeHeader = VIEW_TYPE_HEADER;
        int viewTypeItemLoaded = VIEW_TYPE_ITEM_LOADED;
        int viewTypeLoading = VIEW_TYPE_LOADING;
        int viewTypeFailed = VIEW_TYPE_FAILED;
        int viewTypeEmpty = VIEW_TYPE_EMPTY;
        // When
        int resultHeader = sectionAdapter.getSectionItemViewTypeForAdapterViewType(viewTypeHeader);
        int resultItemLoaded = sectionAdapter.getSectionItemViewTypeForAdapterViewType(viewTypeItemLoaded);
        int resultLoading = sectionAdapter.getSectionItemViewTypeForAdapterViewType(viewTypeLoading);
        int resultFailed = sectionAdapter.getSectionItemViewTypeForAdapterViewType(viewTypeFailed);
        int resultEmpty = sectionAdapter.getSectionItemViewTypeForAdapterViewType(viewTypeEmpty);
        // Then
        Assert.assertThat(resultHeader, Is.is(VIEW_TYPE_HEADER));
        Assert.assertThat(resultItemLoaded, Is.is(VIEW_TYPE_ITEM_LOADED));
        Assert.assertThat(resultLoading, Is.is(VIEW_TYPE_LOADING));
        Assert.assertThat(resultFailed, Is.is(VIEW_TYPE_FAILED));
        Assert.assertThat(resultEmpty, Is.is(VIEW_TYPE_EMPTY));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getSectionItemViewType_withEmptyAdapter_throwsException() {
        // When
        sectionAdapter.getSectionItemViewType(0);
    }

    @Test
    public void getSectionItemViewType_withAdapterWithManySections_returnsCorrectValuesForHeadedFootedStatelessSection() {
        // Given
        addFourStatelessSectionsAndFourSectionsToAdapter();
        // When
        int viewTypeHeader = sectionAdapter.getSectionItemViewType(32);
        int viewTypeItemStart = sectionAdapter.getSectionItemViewType(33);
        int viewTypeItemEnd = sectionAdapter.getSectionItemViewType(42);
        int viewTypeFooter = sectionAdapter.getSectionItemViewType(43);
        // Then
        Assert.assertThat(viewTypeHeader, Is.is(VIEW_TYPE_HEADER));
        Assert.assertThat(viewTypeItemStart, Is.is(VIEW_TYPE_ITEM_LOADED));
        Assert.assertThat(viewTypeItemEnd, Is.is(VIEW_TYPE_ITEM_LOADED));
        Assert.assertThat(viewTypeFooter, Is.is(VIEW_TYPE_FOOTER));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getItemViewType_withEmptyAdapter_throwsException() {
        // When
        sectionAdapter.getItemViewType(0);
    }

    @Test
    public void getItemViewType_withAdapterWithManySections_returnsCorrectValuesForHeadedFootedStatelessSection() {
        // Given
        addFourStatelessSectionsAndFourSectionsToAdapter();
        // When
        int viewTypeHeader = sectionAdapter.getItemViewType(32);
        int viewTypeItemStart = sectionAdapter.getItemViewType(33);
        int viewTypeItemEnd = sectionAdapter.getItemViewType(42);
        int viewTypeFooter = sectionAdapter.getItemViewType(43);
        // Then
        Assert.assertThat(viewTypeHeader, Is.is(18));
        Assert.assertThat(viewTypeItemStart, Is.is(20));
        Assert.assertThat(viewTypeItemEnd, Is.is(20));
        Assert.assertThat(viewTypeFooter, Is.is(19));
    }

    @Test
    public void getItemViewType_withAdapterWithManySections_returnsCorrectValuesForSectionWithLoadingState() {
        // Given
        addStatelessSectionStubToAdapter();
        addHeadedStatelessSectionStubToAdapter();
        addFootedStatelessSectionStubToAdapter();
        addHeadedFootedStatelessSectionStubToAdapter();
        Section section = addSectionStubToAdapter();
        section.setState(LOADING);
        addHeadedSectionStubToAdapter();
        addFootedSectionStubToAdapter();
        addHeadedFootedSectionStubToAdapter();
        // When
        int viewTypeLoading = sectionAdapter.getItemViewType(44);
        // Then
        Assert.assertThat(viewTypeLoading, Is.is(27));
    }

    @Test
    public void getItemViewType_withAdapterWithManySections_returnsCorrectValuesForSectionWithFailedState() {
        // Given
        addStatelessSectionStubToAdapter();
        addHeadedStatelessSectionStubToAdapter();
        addFootedStatelessSectionStubToAdapter();
        addHeadedFootedStatelessSectionStubToAdapter();
        Section section = addSectionStubToAdapter();
        section.setState(FAILED);
        addHeadedSectionStubToAdapter();
        addFootedSectionStubToAdapter();
        addHeadedFootedSectionStubToAdapter();
        // When
        int viewTypeFailed = sectionAdapter.getItemViewType(44);
        // Then
        Assert.assertThat(viewTypeFailed, Is.is(28));
    }

    @Test
    public void getItemViewType_withAdapterWithManySections_returnsCorrectValuesForSectionWithEmptyState() {
        // Given
        addStatelessSectionStubToAdapter();
        addHeadedStatelessSectionStubToAdapter();
        addFootedStatelessSectionStubToAdapter();
        addHeadedFootedStatelessSectionStubToAdapter();
        Section section = addSectionStubToAdapter();
        section.setState(EMPTY);
        addHeadedSectionStubToAdapter();
        addFootedSectionStubToAdapter();
        addHeadedFootedSectionStubToAdapter();
        // When
        int viewTypeEmpty = sectionAdapter.getItemViewType(44);
        // Then
        Assert.assertThat(viewTypeEmpty, Is.is(29));
    }

    @Test
    public void onCreateViewHolder_withEmptyAdapter_returnsNull() {
        // When
        @SuppressWarnings("ConstantConditions")
        Object result = sectionAdapter.onCreateViewHolder(null, 0);
        // Then
        Assert.assertNull(result);
    }

    @Test(expected = NullPointerException.class)
    public void onCreateViewHolder_withStatelessSection_throwsExceptionForHeader() {
        // Given
        addStatelessSectionStubToAdapter();
        // noinspection ConstantConditions
        sectionAdapter.onCreateViewHolder(null, VIEW_TYPE_HEADER);
    }

    @Test(expected = NullPointerException.class)
    public void onCreateViewHolder_withStatelessSection_throwsExceptionForFooter() {
        // Given
        addStatelessSectionStubToAdapter();
        // When
        // noinspection ConstantConditions
        sectionAdapter.onCreateViewHolder(null, VIEW_TYPE_FOOTER);
    }

    @Test(expected = NullPointerException.class)
    public void onCreateViewHolder_withStatelessSection_throwsExceptionForLoading() {
        // Given
        addStatelessSectionStubToAdapter();
        // When
        // noinspection ConstantConditions
        sectionAdapter.onCreateViewHolder(null, VIEW_TYPE_LOADING);
    }

    @Test(expected = NullPointerException.class)
    public void onCreateViewHolder_withStatelessSection_throwsExceptionForFailed() {
        // Given
        addStatelessSectionStubToAdapter();
        // When
        // noinspection ConstantConditions
        sectionAdapter.onCreateViewHolder(null, VIEW_TYPE_FAILED);
    }

    @Test
    public void removeAllSections_withAdapterWithManySections_succeeds() {
        // Given
        addFourStatelessSectionsAndFourSectionsToAdapter();
        // When
        sectionAdapter.removeAllSections();
        // Then
        Assert.assertThat(sectionAdapter.getItemCount(), Is.is(0));
        Assert.assertTrue(sectionAdapter.getCopyOfSectionsMap().isEmpty());
    }

    @Test
    public void getPositionInAdapterUsingTag_withAdapterWithManySections_returnsCorrectAdapterPosition() {
        // Given
        SectionedRecyclerViewAdapter spySectionedRecyclerViewAdapter = Mockito.spy(SectionedRecyclerViewAdapter.class);
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemInserted(ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterTest.ITEMS_QTY));
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterTest.SECTION_TAG, new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterTest.ITEMS_QTY));
        // When
        int result = spySectionedRecyclerViewAdapter.getPositionInAdapter(SectionedRecyclerViewAdapterTest.SECTION_TAG, 0);
        // Then
        Assert.assertThat(result, Is.is(11));
    }

    @Test
    public void getPositionInAdapterUsingSection_withAdapterWithManySections_returnsCorrectAdapterPosition() {
        // Given
        SectionedRecyclerViewAdapter spySectionedRecyclerViewAdapter = Mockito.spy(SectionedRecyclerViewAdapter.class);
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemInserted(ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterTest.ITEMS_QTY));
        HeadedFootedStatelessSectionStub headedFootedStatelessSectionStub = new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterTest.ITEMS_QTY);
        spySectionedRecyclerViewAdapter.addSection(headedFootedStatelessSectionStub);
        // When
        int result = spySectionedRecyclerViewAdapter.getPositionInAdapter(headedFootedStatelessSectionStub, 0);
        // Then
        Assert.assertThat(result, Is.is(11));
    }

    @Test
    public void getHeaderPositionInAdapterUsingTag_withAdapterWithManySections_returnsCorrectAdapterHeaderPosition() {
        // Given
        sectionAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterTest.ITEMS_QTY));
        sectionAdapter.addSection(SectionedRecyclerViewAdapterTest.SECTION_TAG, new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterTest.ITEMS_QTY));
        // When
        int result = sectionAdapter.getHeaderPositionInAdapter(SectionedRecyclerViewAdapterTest.SECTION_TAG);
        // Then
        Assert.assertThat(result, Is.is(10));
    }

    @Test(expected = IllegalStateException.class)
    public void getHeaderPositionInAdapterUsingTag_withAdapterWithManySections_throwsIllegalStateException() {
        sectionAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterTest.ITEMS_QTY));
        sectionAdapter.addSection(SectionedRecyclerViewAdapterTest.SECTION_TAG, new StatelessSectionStub(SectionedRecyclerViewAdapterTest.ITEMS_QTY));
        // When
        sectionAdapter.getHeaderPositionInAdapter(SectionedRecyclerViewAdapterTest.SECTION_TAG);
    }

    @Test
    public void getFooterPositionInAdapterUsingTag_withAdapterWithManySections_returnsCorrectAdapterFooterPosition() {
        // Given
        sectionAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterTest.ITEMS_QTY));
        sectionAdapter.addSection(SectionedRecyclerViewAdapterTest.SECTION_TAG, new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterTest.ITEMS_QTY));
        // When
        int result = sectionAdapter.getFooterPositionInAdapter(SectionedRecyclerViewAdapterTest.SECTION_TAG);
        // Then
        Assert.assertThat(result, Is.is(21));
    }

    @Test(expected = IllegalStateException.class)
    public void getFooterPositionInAdapterUsingTag_withAdapterWithManySections_throwsIllegalStateException() {
        sectionAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterTest.ITEMS_QTY));
        sectionAdapter.addSection(SectionedRecyclerViewAdapterTest.SECTION_TAG, new StatelessSectionStub(SectionedRecyclerViewAdapterTest.ITEMS_QTY));
        // When
        sectionAdapter.getFooterPositionInAdapter(SectionedRecyclerViewAdapterTest.SECTION_TAG);
    }
}

