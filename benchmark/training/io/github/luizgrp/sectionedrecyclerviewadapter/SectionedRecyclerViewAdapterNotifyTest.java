package io.github.luizgrp.sectionedrecyclerviewadapter;


import Section.State;
import Section.State.EMPTY;
import Section.State.LOADED;
import Section.State.LOADING;
import io.github.luizgrp.sectionedrecyclerviewadapter.testdoubles.stub.HeadedFootedSectionStub;
import io.github.luizgrp.sectionedrecyclerviewadapter.testdoubles.stub.HeadedFootedStatelessSectionStub;
import io.github.luizgrp.sectionedrecyclerviewadapter.testdoubles.stub.StatelessSectionStub;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.Spy;


@SuppressWarnings({ "PMD.MethodNamingConventions" })
public class SectionedRecyclerViewAdapterNotifyTest {
    private static final int ITEMS_QTY = 10;

    private static final String SECTION_TAG = "tag";

    @Spy
    private SectionedRecyclerViewAdapter spySectionedRecyclerViewAdapter;

    @Test
    public void notifyItemInsertedInSectionUsingTag_withAdapterWithManySections_callsSuperNotifyItemInserted() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemInserted(ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        // When
        spySectionedRecyclerViewAdapter.notifyItemInsertedInSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, 0);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemInserted(11);
    }

    @Test
    public void notifyItemInsertedInSectionUsingSection_withAdapterWithManySections_callsSuperNotifyItemInserted() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemInserted(ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        HeadedFootedStatelessSectionStub headedFootedStatelessSectionStub = new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        spySectionedRecyclerViewAdapter.addSection(headedFootedStatelessSectionStub);
        // When
        spySectionedRecyclerViewAdapter.notifyItemInsertedInSection(headedFootedStatelessSectionStub, 0);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemInserted(11);
    }

    @Test
    public void notifyAllItemsInsertedInSectionUsingTag_withAdapterWithManySections_callsSuperNotifyItemRangeInserted() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeInserted(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        // When
        spySectionedRecyclerViewAdapter.notifyAllItemsInsertedInSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeInserted(11, SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
    }

    @Test
    public void notifyAllItemsInsertedInSection_withAdapterWithManySections_callsSuperNotifyItemRangeInserted() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeInserted(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        HeadedFootedStatelessSectionStub headedFootedStatelessSectionStub = new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        spySectionedRecyclerViewAdapter.addSection(headedFootedStatelessSectionStub);
        // When
        spySectionedRecyclerViewAdapter.notifyAllItemsInsertedInSection(headedFootedStatelessSectionStub);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeInserted(11, SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
    }

    @Test
    public void notifyItemRangeInsertedInSectionUsingTag_withAdapterWithManySections_callsSuperNotifyItemRangeInserted() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeInserted(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        // When
        spySectionedRecyclerViewAdapter.notifyItemRangeInsertedInSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, 0, 4);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeInserted(11, 4);
    }

    @Test
    public void notifyItemRangeInsertedInSectionUsingSection_withAdapterWithManySections_callsSuperNotifyItemRangeInserted() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeInserted(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        HeadedFootedStatelessSectionStub headedFootedStatelessSectionStub = new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        spySectionedRecyclerViewAdapter.addSection(headedFootedStatelessSectionStub);
        // When
        spySectionedRecyclerViewAdapter.notifyItemRangeInsertedInSection(headedFootedStatelessSectionStub, 0, 4);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeInserted(11, 4);
    }

    @Test
    public void notifyItemRemovedFromSectionUsingTag_withAdapterWithManySections_callsSuperNotifyItemRemoved() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemRemoved(ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        // When
        spySectionedRecyclerViewAdapter.notifyItemRemovedFromSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, 0);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemRemoved(11);
    }

    @Test
    public void notifyItemRemovedFromSectionUsingSection_withAdapterWithManySections_callsSuperNotifyItemRemoved() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemRemoved(ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        HeadedFootedStatelessSectionStub headedFootedStatelessSectionStub = new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        spySectionedRecyclerViewAdapter.addSection(headedFootedStatelessSectionStub);
        // When
        spySectionedRecyclerViewAdapter.notifyItemRemovedFromSection(headedFootedStatelessSectionStub, 0);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemRemoved(11);
    }

    @Test
    public void notifyItemRangeRemovedInSectionUsingTag_withAdapterWithManySections_callsSuperNotifyItemRangeRemoved() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeRemoved(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        // When
        spySectionedRecyclerViewAdapter.notifyItemRangeRemovedFromSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, 0, 4);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeRemoved(11, 4);
    }

    @Test
    public void notifyItemRangeRemovedInSectionUsingSection_withAdapterWithManySections_callsSuperNotifyItemRangeRemoved() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeRemoved(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        HeadedFootedStatelessSectionStub headedFootedStatelessSectionStub = new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        spySectionedRecyclerViewAdapter.addSection(headedFootedStatelessSectionStub);
        // When
        spySectionedRecyclerViewAdapter.notifyItemRangeRemovedFromSection(headedFootedStatelessSectionStub, 0, 4);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeRemoved(11, 4);
    }

    @Test
    public void notifyItemChangedInSectionUsingTag_withAdapterWithManySections_callsSuperNotifyItemChangedInSection() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        // When
        spySectionedRecyclerViewAdapter.notifyItemChangedInSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, 0);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(11);
    }

    @Test
    public void notifyItemChangedInSectionUsingSection_withAdapterWithManySections_callsSuperNotifyItemChangedInSection() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        HeadedFootedStatelessSectionStub headedFootedStatelessSectionStub = new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        spySectionedRecyclerViewAdapter.addSection(headedFootedStatelessSectionStub);
        // When
        spySectionedRecyclerViewAdapter.notifyItemChangedInSection(headedFootedStatelessSectionStub, 0);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(11);
    }

    @Test
    public void notifyHeaderChangedInSectionUsingTag_withAdapterWithManySections_callsSuperNotifyItemChangedInSection() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        // When
        spySectionedRecyclerViewAdapter.notifyHeaderChangedInSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(10);
    }

    @Test(expected = IllegalStateException.class)
    public void notifyHeaderChangedInSectionUsingTag_withAdapterWithManySections_throwsIllegalStateException() {
        // Given
        SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();
        sectionAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        sectionAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        // When
        sectionAdapter.notifyHeaderChangedInSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG);
    }

    @Test
    public void notifyFooterChangedInSectionUsingTag_withAdapterWithManySections_callsSuperNotifyItemChangedInSection() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        // When
        spySectionedRecyclerViewAdapter.notifyFooterChangedInSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(21);
    }

    @Test(expected = IllegalStateException.class)
    public void notifyFooterChangedInSectionUsingTag_withAdapterWithManySections_throwsIllegalStateException() {
        // Given
        SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();
        sectionAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        sectionAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        // When
        sectionAdapter.notifyFooterChangedInSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG);
    }

    @Test
    public void notifyAllItemsChangedInSectionUsingTag_withAdapterWithManySections_callsSuperNotifyItemRangeChanged() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeChanged(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        // When
        spySectionedRecyclerViewAdapter.notifyAllItemsChangedInSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeChanged(11, SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
    }

    @Test
    public void notifyAllItemsChangedInSectionUsingSection_withAdapterWithManySections_callsSuperNotifyItemRangeChanged() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeChanged(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        HeadedFootedStatelessSectionStub headedFootedStatelessSectionStub = new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        spySectionedRecyclerViewAdapter.addSection(headedFootedStatelessSectionStub);
        // When
        spySectionedRecyclerViewAdapter.notifyAllItemsChangedInSection(headedFootedStatelessSectionStub);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeChanged(11, SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
    }

    @Test
    public void notifyItemRangeChangedInSectionUsingTag_withAdapterWithManySections_callsSuperNotifyItemRangeChanged() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeChanged(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        // When
        spySectionedRecyclerViewAdapter.notifyItemRangeChangedInSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, 0, 4);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeChanged(11, 4);
    }

    @Test
    public void notifyItemRangeChangedInSectionUsingSection_withAdapterWithManySections_callsSuperNotifyItemRangeChanged() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeChanged(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        HeadedFootedStatelessSectionStub headedFootedStatelessSectionStub = new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        spySectionedRecyclerViewAdapter.addSection(headedFootedStatelessSectionStub);
        // When
        spySectionedRecyclerViewAdapter.notifyItemRangeChangedInSection(headedFootedStatelessSectionStub, 0, 4);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeChanged(11, 4);
    }

    @Test
    public void notifyItemRangeChangedInSectionWithPayloadUsingTag_withAdapterWithManySections_callsSuperNotifyItemRangeChanged() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeChanged(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        // When
        spySectionedRecyclerViewAdapter.notifyItemRangeChangedInSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, 0, 4, null);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeChanged(11, 4, null);
    }

    @Test
    public void notifyItemRangeChangedInSectionWithPayloadUsingSection_withAdapterWithManySections_callsSuperNotifyItemRangeChanged() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeChanged(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        HeadedFootedStatelessSectionStub headedFootedStatelessSectionStub = new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        spySectionedRecyclerViewAdapter.addSection(headedFootedStatelessSectionStub);
        // When
        spySectionedRecyclerViewAdapter.notifyItemRangeChangedInSection(headedFootedStatelessSectionStub, 0, 4, null);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeChanged(11, 4, null);
    }

    @Test
    public void notifyItemMovedInSectionUsingTag_withAdapterWithManySections_callsSuperNotifyItemMoved() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemMoved(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        // When
        spySectionedRecyclerViewAdapter.notifyItemMovedInSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, 0, 4);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemMoved(11, 15);
    }

    @Test
    public void notifyItemMovedInSectionUsingSection_withAdapterWithManySections_callsSuperNotifyItemMoved() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemMoved(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        HeadedFootedStatelessSectionStub headedFootedStatelessSectionStub = new HeadedFootedStatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        spySectionedRecyclerViewAdapter.addSection(headedFootedStatelessSectionStub);
        // When
        spySectionedRecyclerViewAdapter.notifyItemMovedInSection(headedFootedStatelessSectionStub, 0, 4);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemMoved(11, 15);
    }

    @Test
    public void notifyNotLoadedStateChangedUsingTag_withAdapterWithManySections_callsNotifyItemChanged() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        HeadedFootedSectionStub headedFootedSectionStub = new HeadedFootedSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub);
        headedFootedSectionStub.setState(LOADING);
        // When
        Section.State previousState = getState();
        headedFootedSectionStub.setState(EMPTY);
        spySectionedRecyclerViewAdapter.notifyNotLoadedStateChanged(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, previousState);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(11);
    }

    @Test(expected = IllegalStateException.class)
    public void notifyNotLoadedStateChangedUsingTag_withNoStateChange_throwsException() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(ArgumentMatchers.anyInt());
        HeadedFootedSectionStub headedFootedSectionStub = new HeadedFootedSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub);
        headedFootedSectionStub.setState(LOADING);
        // When
        spySectionedRecyclerViewAdapter.notifyNotLoadedStateChanged(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub.getState());
    }

    @Test(expected = IllegalStateException.class)
    public void notifyNotLoadedStateChangedUsingTag_withLoadedAsPreviousState_throwsException() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(ArgumentMatchers.anyInt());
        HeadedFootedSectionStub headedFootedSectionStub = new HeadedFootedSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub);
        headedFootedSectionStub.setState(LOADING);
        // When
        spySectionedRecyclerViewAdapter.notifyNotLoadedStateChanged(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, LOADED);
    }

    @Test(expected = IllegalStateException.class)
    public void notifyNotLoadedStateChangedUsingTag_withLoadedAsCurrentState_throwsException() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(ArgumentMatchers.anyInt());
        HeadedFootedSectionStub headedFootedSectionStub = new HeadedFootedSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub);
        headedFootedSectionStub.setState(LOADING);
        // When
        Section.State previousState = getState();
        headedFootedSectionStub.setState(LOADED);
        spySectionedRecyclerViewAdapter.notifyNotLoadedStateChanged(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, previousState);
    }

    @Test
    public void notifyStateChangedToLoadedUsingTag_withAdapterWithManySections_callsNotifyItemChanged_callsNotifyItemInserted() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(ArgumentMatchers.anyInt());
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeInserted(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        HeadedFootedSectionStub headedFootedSectionStub = new HeadedFootedSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub);
        headedFootedSectionStub.setState(LOADING);
        // When
        Section.State previousState = getState();
        headedFootedSectionStub.setState(LOADED);
        spySectionedRecyclerViewAdapter.notifyStateChangedToLoaded(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, previousState);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(11);
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeInserted(12, 9);
    }

    @Test(expected = IllegalStateException.class)
    public void notifyStateChangedToLoadedUsingTag_withNoStateChange_throwsException() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(ArgumentMatchers.anyInt());
        HeadedFootedSectionStub headedFootedSectionStub = new HeadedFootedSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub);
        headedFootedSectionStub.setState(LOADED);
        // When
        spySectionedRecyclerViewAdapter.notifyStateChangedToLoaded(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub.getState());
    }

    @Test(expected = IllegalStateException.class)
    public void notifyStateChangedToLoadedUsingTag_withCurrentStateNotLoadedAndLoadedAsPreviousState_throwsException() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(ArgumentMatchers.anyInt());
        HeadedFootedSectionStub headedFootedSectionStub = new HeadedFootedSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub);
        headedFootedSectionStub.setState(LOADED);
        // When
        Section.State previousState = getState();
        headedFootedSectionStub.setState(EMPTY);
        spySectionedRecyclerViewAdapter.notifyStateChangedToLoaded(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, previousState);
    }

    @Test(expected = IllegalStateException.class)
    public void notifyStateChangedToLoadedUsingTag_withCurrentStateNotLoadedAndPreviousStateNotLoaded_throwsException() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(ArgumentMatchers.anyInt());
        HeadedFootedSectionStub headedFootedSectionStub = new HeadedFootedSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub);
        headedFootedSectionStub.setState(LOADING);
        // When
        Section.State previousState = getState();
        headedFootedSectionStub.setState(EMPTY);
        spySectionedRecyclerViewAdapter.notifyStateChangedToLoaded(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, previousState);
    }

    @Test
    public void notifyStateChangedToLoadedUsingTag_withContentItemsTotal0_callsNotifyItemRemoved() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemRemoved(ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        HeadedFootedSectionStub headedFootedSectionStub = new HeadedFootedSectionStub(0);
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub);
        headedFootedSectionStub.setState(LOADING);
        // When
        Section.State previousState = getState();
        headedFootedSectionStub.setState(LOADED);
        spySectionedRecyclerViewAdapter.notifyStateChangedToLoaded(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, previousState);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemRemoved(11);
    }

    @Test
    public void notifyStateChangedToLoadedUsingTag_withContentItemsTotal1_callsNotifyItemChanged() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        HeadedFootedSectionStub headedFootedSectionStub = new HeadedFootedSectionStub(1);
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub);
        headedFootedSectionStub.setState(LOADING);
        // When
        Section.State previousState = getState();
        headedFootedSectionStub.setState(LOADED);
        spySectionedRecyclerViewAdapter.notifyStateChangedToLoaded(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, previousState);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(11);
        Mockito.verify(spySectionedRecyclerViewAdapter, Mockito.never()).callSuperNotifyItemRangeInserted(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
    }

    @Test
    public void notifyStateChangedFromLoadedUsingTag_withAdapterWithManySections_callsNotifyItemRangeRemoved_callsNotifyItemChanged() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeRemoved(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        HeadedFootedSectionStub headedFootedSectionStub = new HeadedFootedSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub);
        headedFootedSectionStub.setState(LOADED);
        // When
        int previousContentItemsTotal = headedFootedSectionStub.getContentItemsTotal();
        headedFootedSectionStub.setState(EMPTY);
        spySectionedRecyclerViewAdapter.notifyStateChangedFromLoaded(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, previousContentItemsTotal);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeRemoved(12, 9);
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(11);
    }

    @Test(expected = IllegalStateException.class)
    public void notifyStateChangedFromLoadedUsingTag_withLoadedAsCurrentState_throwsException() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeRemoved(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(ArgumentMatchers.anyInt());
        HeadedFootedSectionStub headedFootedSectionStub = new HeadedFootedSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub);
        headedFootedSectionStub.setState(LOADED);
        // When
        spySectionedRecyclerViewAdapter.notifyStateChangedFromLoaded(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub.getContentItemsTotal());
    }

    @Test
    public void notifyStateChangedFromLoadedUsingTag_withPreviousContentItemsCount0_callsNotifyItemInserted() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemInserted(ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        HeadedFootedSectionStub headedFootedSectionStub = new HeadedFootedSectionStub(0);
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub);
        headedFootedSectionStub.setState(LOADED);
        // When
        int previousContentItemsTotal = headedFootedSectionStub.getContentItemsTotal();
        headedFootedSectionStub.setState(EMPTY);
        spySectionedRecyclerViewAdapter.notifyStateChangedFromLoaded(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, previousContentItemsTotal);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemInserted(11);
    }

    @Test
    public void notifyStateChangedFromLoadedUsingTag_withPreviousContentItemsCount1_callsNotifyItemChanged() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        HeadedFootedSectionStub headedFootedSectionStub = new HeadedFootedSectionStub(1);
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub);
        headedFootedSectionStub.setState(LOADED);
        // When
        int previousContentItemsTotal = headedFootedSectionStub.getContentItemsTotal();
        headedFootedSectionStub.setState(EMPTY);
        spySectionedRecyclerViewAdapter.notifyStateChangedFromLoaded(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, previousContentItemsTotal);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemChanged(11);
        Mockito.verify(spySectionedRecyclerViewAdapter, Mockito.never()).callSuperNotifyItemRangeRemoved(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
    }

    @Test
    public void notifyHeaderInsertedInSectionUsingTag_withAdapterWithManySections_callsSuperNotifyItemInserted() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemInserted(ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        HeadedFootedSectionStub headedFootedSectionStub = new HeadedFootedSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        setHasHeader(false);
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub);
        // When
        setHasHeader(true);
        spySectionedRecyclerViewAdapter.notifyHeaderInsertedInSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemInserted(10);
    }

    @Test
    public void notifyFooterInsertedInSectionUsingTag_withAdapterWithManySections_callsSuperNotifyItemInserted() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemInserted(ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        HeadedFootedSectionStub headedFootedSectionStub = new HeadedFootedSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        setHasFooter(false);
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub);
        // When
        setHasFooter(true);
        spySectionedRecyclerViewAdapter.notifyFooterInsertedInSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemInserted(21);
    }

    @Test
    public void notifyHeaderRemovedFromSectionUsingTag_withAdapterWithManySections_callsSuperNotifyItemRemoved() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemRemoved(ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        HeadedFootedSectionStub headedFootedSectionStub = new HeadedFootedSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        setHasHeader(true);
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub);
        // When
        setHasHeader(false);
        spySectionedRecyclerViewAdapter.notifyHeaderRemovedFromSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemRemoved(10);
    }

    @Test
    public void notifyFooterRemovedFromSectionUsingTag_withAdapterWithManySections_callsSuperNotifyItemRemoved() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemRemoved(ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        HeadedFootedSectionStub headedFootedSectionStub = new HeadedFootedSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        setHasFooter(true);
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub);
        // When
        setHasFooter(false);
        spySectionedRecyclerViewAdapter.notifyFooterRemovedFromSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemRemoved(21);
    }

    @Test(expected = IllegalStateException.class)
    public void notifySectionChangedToVisibleUsingTag_withInvisibleSection_throwsException() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeInserted(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        HeadedFootedSectionStub headedFootedSectionStub = new HeadedFootedSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        setVisible(false);
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub);
        // When
        spySectionedRecyclerViewAdapter.notifySectionChangedToVisible(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG);
    }

    @Test(expected = IllegalStateException.class)
    public void notifySectionChangedToInvisibleUsingTag_withVisibleSection_throwsException() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeRemoved(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        HeadedFootedSectionStub headedFootedSectionStub = new HeadedFootedSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        setVisible(true);
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub);
        // When
        int previousSectionPosition = spySectionedRecyclerViewAdapter.getSectionPosition(headedFootedSectionStub);
        spySectionedRecyclerViewAdapter.notifySectionChangedToInvisible(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, previousSectionPosition);
    }

    @Test
    public void notifySectionChangedToVisibleUsingTag_withAdapterWithManySections_callsSuperNotifyItemRangeInserted() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeInserted(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        HeadedFootedSectionStub headedFootedSectionStub = new HeadedFootedSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        setVisible(false);
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub);
        // When
        setVisible(true);
        spySectionedRecyclerViewAdapter.notifySectionChangedToVisible(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeInserted(10, 12);
    }

    @Test
    public void notifySectionChangedToInvisibleUsingTag_withAdapterWithManySections_callsSuperNotifyItemRangeInserted() {
        // Given
        Mockito.doNothing().when(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeRemoved(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        spySectionedRecyclerViewAdapter.addSection(new StatelessSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY));
        HeadedFootedSectionStub headedFootedSectionStub = new HeadedFootedSectionStub(SectionedRecyclerViewAdapterNotifyTest.ITEMS_QTY);
        setVisible(true);
        spySectionedRecyclerViewAdapter.addSection(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, headedFootedSectionStub);
        // When
        int previousSectionPosition = spySectionedRecyclerViewAdapter.getSectionPosition(headedFootedSectionStub);
        setVisible(false);
        spySectionedRecyclerViewAdapter.notifySectionChangedToInvisible(SectionedRecyclerViewAdapterNotifyTest.SECTION_TAG, previousSectionPosition);
        // Then
        Mockito.verify(spySectionedRecyclerViewAdapter).callSuperNotifyItemRangeRemoved(10, 12);
    }
}

