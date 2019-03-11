package com.bignerdranch.expandablerecyclerview;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.view.ViewGroup;
import com.bignerdranch.expandablerecyclerview.model.Parent;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ExpandableRecyclerAdapterTest {
    private ExpandableRecyclerAdapterTest.TestExpandableRecyclerAdapter mExpandableRecyclerAdapter;

    private List<Parent<Object>> mBaseParents;

    private AdapterDataObserver mDataObserver;

    @Test
    public void adapterCorrectlyInitializesExpandedParents() {
        Assert.assertEquals(25, getItemCount());
    }

    @Test
    public void adapterCorrectlyInitializesExpandedParentsWhenAllParentItemsAreInitiallyCollapsed() {
        for (Parent parent : mBaseParents) {
            Mockito.when(parent.isInitiallyExpanded()).thenReturn(false);
        }
        mExpandableRecyclerAdapter = new ExpandableRecyclerAdapterTest.TestExpandableRecyclerAdapter(mBaseParents);
        Assert.assertEquals(10, getItemCount());
    }

    @Test
    public void adapterCorrectlyInitializesExpandedParentsWhenAllParentItemsAreInitiallyExpanded() {
        for (Parent parent : mBaseParents) {
            Mockito.when(parent.isInitiallyExpanded()).thenReturn(true);
        }
        mExpandableRecyclerAdapter = new ExpandableRecyclerAdapterTest.TestExpandableRecyclerAdapter(mBaseParents);
        Assert.assertEquals(40, getItemCount());
    }

    @Test
    public void collapsingExpandedParentWithIndexRemovesChildren() {
        Assert.assertEquals(25, getItemCount());
        verifyParentItemsMatch(mBaseParents.get(0), true, 0);
        verifyParentItemsMatch(mBaseParents.get(1), false, 4);
        collapseParent(0);
        Mockito.verify(mDataObserver).onItemRangeRemoved(1, 3);
        Assert.assertEquals(22, getItemCount());
        verifyParentItemsMatch(mBaseParents.get(0), false, 0);
        verifyParentItemsMatch(mBaseParents.get(1), false, 1);
    }

    @Test
    public void collapsingExpandedParentWithObjectRemovesChildren() {
        Assert.assertEquals(25, getItemCount());
        verifyParentItemsMatch(mBaseParents.get(0), true, 0);
        verifyParentItemsMatch(mBaseParents.get(1), false, 4);
        Parent<Object> firstParent = mBaseParents.get(0);
        mExpandableRecyclerAdapter.collapseParent(firstParent);
        Mockito.verify(mDataObserver).onItemRangeRemoved(1, 3);
        Assert.assertEquals(22, getItemCount());
        verifyParentItemsMatch(mBaseParents.get(0), false, 0);
        verifyParentItemsMatch(mBaseParents.get(1), false, 1);
    }

    @Test
    public void collapsingCollapsedParentWithIndexHasNoEffect() {
        Assert.assertEquals(25, getItemCount());
        verifyParentItemsMatch(mBaseParents.get(9), false, 24);
        collapseParent(9);
        Mockito.verify(mDataObserver, Mockito.never()).onItemRangeRemoved(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Assert.assertEquals(25, getItemCount());
        verifyParentItemsMatch(mBaseParents.get(9), false, 24);
    }

    @Test
    public void collapsingCollapsedParentWithObjectHasNoEffect() {
        Assert.assertEquals(25, getItemCount());
        verifyParentItemsMatch(mBaseParents.get(9), false, 24);
        mExpandableRecyclerAdapter.collapseParent(mBaseParents.get(9));
        Mockito.verify(mDataObserver, Mockito.never()).onItemRangeRemoved(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Assert.assertEquals(25, getItemCount());
        verifyParentItemsMatch(mBaseParents.get(9), false, 24);
    }

    @Test
    public void expandingParentWithIndexAddsChildren() {
        Assert.assertEquals(25, getItemCount());
        verifyParentItemsMatch(mBaseParents.get(9), false, 24);
        expandParent(9);
        Mockito.verify(mDataObserver).onItemRangeInserted(25, 3);
        Assert.assertEquals(28, getItemCount());
        verifyParentItemsMatch(mBaseParents.get(9), true, 24);
    }

    @Test
    public void expandingParentWithObjectAddsChildren() {
        Assert.assertEquals(25, getItemCount());
        verifyParentItemsMatch(mBaseParents.get(9), false, 24);
        mExpandableRecyclerAdapter.expandParent(mBaseParents.get(9));
        Mockito.verify(mDataObserver).onItemRangeInserted(25, 3);
        Assert.assertEquals(28, getItemCount());
        verifyParentItemsMatch(mBaseParents.get(9), true, 24);
    }

    @Test
    public void expandingExpandedParentWithIndexHasNoEffect() {
        Assert.assertEquals(25, getItemCount());
        verifyParentItemsMatch(mBaseParents.get(0), true, 0);
        expandParent(0);
        Mockito.verify(mDataObserver, Mockito.never()).onItemRangeInserted(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Assert.assertEquals(25, getItemCount());
        verifyParentItemsMatch(mBaseParents.get(0), true, 0);
    }

    @Test
    public void expandingExpandedWithObjectParentHasNoEffect() {
        Assert.assertEquals(25, getItemCount());
        verifyParentItemsMatch(mBaseParents.get(0), true, 0);
        mExpandableRecyclerAdapter.expandParent(mBaseParents.get(0));
        Mockito.verify(mDataObserver, Mockito.never()).onItemRangeInserted(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Assert.assertEquals(25, getItemCount());
        verifyParentItemsMatch(mBaseParents.get(0), true, 0);
    }

    @Test
    public void notifyParentInsertedWithInitiallyCollapsedItem() {
        Parent<Object> originalFirstItem = mBaseParents.get(0);
        Assert.assertEquals(25, getItemCount());
        verifyParentItemsMatch(originalFirstItem, true, 0);
        Parent<Object> insertedItem = generateParent(false, 2);
        Mockito.when(insertedItem.isInitiallyExpanded()).thenReturn(false);
        mBaseParents.add(0, insertedItem);
        notifyParentInserted(0);
        Mockito.verify(mDataObserver).onItemRangeInserted(0, 1);
        Assert.assertEquals(26, getItemCount());
        verifyParentItemsMatch(insertedItem, false, 0);
        verifyParentItemsMatch(originalFirstItem, true, 1);
    }

    @Test
    public void notifyParentInsertedWithInitiallyExpandedItem() {
        Parent<Object> originalLastParent = mBaseParents.get(9);
        Parent<Object> insertedItem = generateParent(true, 3);
        Assert.assertEquals(25, getItemCount());
        verifyParentItemsMatch(originalLastParent, false, 24);
        mBaseParents.add(insertedItem);
        notifyParentInserted(10);
        Mockito.verify(mDataObserver).onItemRangeInserted(25, 4);
        Assert.assertEquals(29, getItemCount());
        verifyParentItemsMatch(originalLastParent, false, 24);
        verifyParentItemsMatch(insertedItem, true, 25);
    }

    @Test
    public void notifyParentRangeInsertedMidList() {
        Parent<Object> firstInsertedItem = generateParent(true, 3);
        Parent<Object> secondInsertedItem = generateParent(false, 2);
        Assert.assertEquals(25, getItemCount());
        mBaseParents.add(4, firstInsertedItem);
        mBaseParents.add(5, secondInsertedItem);
        notifyParentRangeInserted(4, 2);
        Mockito.verify(mDataObserver).onItemRangeInserted(10, 5);
        Assert.assertEquals(30, getItemCount());
        verifyParentItemsMatch(firstInsertedItem, true, 10);
        verifyParentItemsMatch(secondInsertedItem, false, 14);
    }

    @Test
    public void notifyParentRangeInsertedEndList() {
        Parent<Object> firstInsertedItem = generateParent(true, 3);
        Parent<Object> secondInsertedItem = generateParent(false, 2);
        Assert.assertEquals(25, getItemCount());
        mBaseParents.add(firstInsertedItem);
        mBaseParents.add(secondInsertedItem);
        notifyParentRangeInserted(10, 2);
        Mockito.verify(mDataObserver).onItemRangeInserted(25, 5);
        Assert.assertEquals(30, getItemCount());
        verifyParentItemsMatch(firstInsertedItem, true, 25);
        verifyParentItemsMatch(secondInsertedItem, false, 29);
    }

    @Test
    public void notifyParentRemovedOnExpandedItem() {
        Parent<Object> removedItem = mBaseParents.get(0);
        Parent<Object> originalSecondItem = mBaseParents.get(1);
        Assert.assertEquals(25, getItemCount());
        verifyParentItemsMatch(removedItem, true, 0);
        verifyParentItemsMatch(originalSecondItem, false, 4);
        mBaseParents.remove(0);
        notifyParentRemoved(0);
        Mockito.verify(mDataObserver).onItemRangeRemoved(0, 4);
        Assert.assertEquals(21, getItemCount());
        verifyParentItemsMatch(originalSecondItem, false, 0);
    }

    @Test
    public void notifyParentRemovedOnCollapsedItem() {
        Parent<Object> removedItem = mBaseParents.get(9);
        Parent<Object> originalSecondToLastItem = mBaseParents.get(8);
        Assert.assertEquals(25, getItemCount());
        verifyParentItemsMatch(removedItem, false, 24);
        verifyParentItemsMatch(originalSecondToLastItem, true, 20);
        mBaseParents.remove(9);
        notifyParentRemoved(9);
        Mockito.verify(mDataObserver).onItemRangeRemoved(24, 1);
        Assert.assertEquals(24, getItemCount());
        verifyParentItemsMatch(originalSecondToLastItem, true, 20);
    }

    @Test
    public void notifyParentRangeRemoved() {
        Parent<Object> firstRemovedItem = mBaseParents.get(7);
        Parent<Object> secondRemovedItem = mBaseParents.get(8);
        Assert.assertEquals(25, getItemCount());
        verifyParentItemsMatch(firstRemovedItem, false, 19);
        verifyParentItemsMatch(secondRemovedItem, true, 20);
        mBaseParents.remove(7);
        mBaseParents.remove(7);
        mExpandableRecyclerAdapter.notifyParentRangeRemoved(7, 2);
        Mockito.verify(mDataObserver).onItemRangeRemoved(19, 5);
        Assert.assertEquals(20, getItemCount());
        verifyParentItemsMatch(mBaseParents.get(7), false, 19);
    }

    @Test
    public void notifyParentChanged() {
        Parent<Object> changedParent = generateParent(false, 3);
        Assert.assertEquals(25, getItemCount());
        verifyParentItemsMatch(mBaseParents.get(4), true, 10);
        mBaseParents.set(4, changedParent);
        mExpandableRecyclerAdapter.notifyParentChanged(4);
        Mockito.verify(mDataObserver).onItemRangeChanged(10, 4, null);
        verifyParentItemsMatch(changedParent, true, 10);
    }

    @Test
    public void notifyParentRangeChanged() {
        Parent<Object> firstChangedParent = generateParent(false, 3);
        Parent<Object> secondChangedParent = generateParent(false, 3);
        Assert.assertEquals(25, getItemCount());
        verifyParentItemsMatch(mBaseParents.get(4), true, 10);
        verifyParentItemsMatch(mBaseParents.get(5), false, 14);
        mBaseParents.set(4, firstChangedParent);
        mBaseParents.set(5, secondChangedParent);
        mExpandableRecyclerAdapter.notifyParentRangeChanged(4, 2);
        Mockito.verify(mDataObserver).onItemRangeChanged(10, 5, null);
        verifyParentItemsMatch(firstChangedParent, true, 10);
        verifyParentItemsMatch(secondChangedParent, false, 14);
    }

    @Test
    public void notifyParentMovedCollapsedParent() {
        Assert.assertEquals(25, getItemCount());
        verifyParentItemsMatch(mBaseParents.get(5), false, 14);
        Parent<Object> movedParent = mBaseParents.remove(5);
        mBaseParents.add(movedParent);
        notifyParentMoved(5, 9);
        Assert.assertEquals(25, getItemCount());
        verifyParentItemsMatch(movedParent, false, 24);
    }

    @Test
    public void notifyParentMovedExpandedParent() {
        Assert.assertEquals(25, getItemCount());
        verifyParentItemsMatch(mBaseParents.get(6), true, 15);
        Parent<Object> movedParent = mBaseParents.remove(6);
        mBaseParents.add(8, movedParent);
        notifyParentMoved(6, 8);
        Assert.assertEquals(25, getItemCount());
        verifyParentItemsMatch(movedParent, true, 20);
    }

    @Test
    public void notifyParentDataSetChangedWithExpansionPreservationAllCollapsed() {
        collapseAllParents();
        Parent<Object> movedParent = mBaseParents.remove(0);
        mBaseParents.add(movedParent);
        Parent<Object> newParent = generateParent(true, 1);
        mBaseParents.add(3, newParent);
        notifyParentDataSetChanged(true);
        Mockito.verify(mDataObserver).onChanged();
        Assert.assertEquals(12, getItemCount());
        verifyParentItemsMatch(mBaseParents.get(0), false, 0);
        verifyParentItemsMatch(newParent, true, 3);
        verifyParentItemsMatch(mBaseParents.get(9), false, 10);
        verifyParentItemsMatch(movedParent, false, 11);
    }

    @Test
    public void notifyParentDataSetChangedWithoutExpansionPreservationAllCollapsed() {
        collapseAllParents();
        Parent<Object> movedParent = mBaseParents.remove(0);
        mBaseParents.add(movedParent);
        Parent<Object> newParent = generateParent(true, 1);
        mBaseParents.add(3, newParent);
        notifyParentDataSetChanged(false);
        Mockito.verify(mDataObserver).onChanged();
        Assert.assertEquals(27, getItemCount());
        verifyParentItemsMatch(mBaseParents.get(0), false, 0);
        verifyParentItemsMatch(newParent, true, 6);
        verifyParentItemsMatch(mBaseParents.get(9), false, 22);
        verifyParentItemsMatch(mBaseParents.get(10), true, 23);
    }

    @Test
    public void notifyParentDataSetWithExpansionPreservationChangedNoChanges() {
        Assert.assertEquals(25, getItemCount());
        notifyParentDataSetChanged(true);
        Mockito.verify(mDataObserver).onChanged();
        Assert.assertEquals(25, getItemCount());
        int flatIndex = 0;
        for (Parent<Object> baseParent : mBaseParents) {
            verifyParentItemsMatch(baseParent, baseParent.isInitiallyExpanded(), flatIndex);
            flatIndex++;
            if (baseParent.isInitiallyExpanded()) {
                flatIndex += baseParent.getChildList().size();
            }
        }
    }

    @Test
    public void notifyParentDataSetWithoutExpansionPreservationChangedNoChanges() {
        Assert.assertEquals(25, getItemCount());
        notifyParentDataSetChanged(false);
        Mockito.verify(mDataObserver).onChanged();
        Assert.assertEquals(25, getItemCount());
        int flatIndex = 0;
        for (Parent<Object> baseParent : mBaseParents) {
            verifyParentItemsMatch(baseParent, baseParent.isInitiallyExpanded(), flatIndex);
            flatIndex++;
            if (baseParent.isInitiallyExpanded()) {
                flatIndex += baseParent.getChildList().size();
            }
        }
    }

    private static class TestExpandableRecyclerAdapter extends ExpandableRecyclerAdapter<Parent<Object>, Object, ParentViewHolder, ChildViewHolder> {
        public TestExpandableRecyclerAdapter(@NonNull
        List<Parent<Object>> parentList) {
            super(parentList);
        }

        @NonNull
        @Override
        public ParentViewHolder onCreateParentViewHolder(@NonNull
        ViewGroup parentViewGroup, int viewType) {
            return null;
        }

        @Override
        public ChildViewHolder onCreateChildViewHolder(@NonNull
        ViewGroup childViewGroup, int viewType) {
            return null;
        }

        @Override
        public void onBindParentViewHolder(@NonNull
        ParentViewHolder parentViewHolder, int parentPosition, @NonNull
        Parent<Object> parent) {
        }

        @Override
        public void onBindChildViewHolder(@NonNull
        ChildViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull
        Object child) {
        }
    }
}

