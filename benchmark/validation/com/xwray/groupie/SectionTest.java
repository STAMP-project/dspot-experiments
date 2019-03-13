package com.xwray.groupie;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class SectionTest {
    @Mock
    GroupAdapter groupAdapter;

    final int footerSize = 5;

    Group footer = new DummyGroup() {
        @Override
        public int getItemCount() {
            return footerSize;
        }
    };

    final int headerSize = 2;

    Group header = new DummyGroup() {
        @Override
        public int getItemCount() {
            return headerSize;
        }
    };

    final int placeholderSize = 3;

    Group placeholder = new DummyGroup() {
        @Override
        public int getItemCount() {
            return placeholderSize;
        }
    };

    Group emptyGroup = new DummyGroup() {
        @Override
        public int getItemCount() {
            return 0;
        }
    };

    @Test
    public void settingFooterNotifiesFooterAdded() {
        Section section = new Section();
        section.setHeader(header);
        section.add(new DummyItem());
        section.registerGroupDataObserver(groupAdapter);
        section.setFooter(footer);
        Mockito.verify(groupAdapter).onItemRangeInserted(section, ((headerSize) + 1), footerSize);
    }

    @Test
    public void removingFooterNotifiesPreviousFooterRemoved() {
        Section section = new Section();
        section.setHeader(header);
        section.add(new DummyItem());
        section.setFooter(footer);
        section.registerGroupDataObserver(groupAdapter);
        section.removeFooter();
        Mockito.verify(groupAdapter).onItemRangeRemoved(section, ((headerSize) + 1), footerSize);
    }

    @Test(expected = NullPointerException.class)
    public void settingNullFooterThrowsNullPointerException() {
        Section section = new Section();
        section.setFooter(null);
    }

    @Test
    public void footerCountIs0WhenThereIsNoFooter() {
        Section section = new Section();
        section.removeFooter();
        Assert.assertEquals(0, section.getItemCount());
    }

    @Test
    public void footerCountIsSizeOfFooter() {
        Section section = new Section();
        section.setFooter(footer);
        Assert.assertEquals(footerSize, section.getItemCount());
    }

    @Test
    public void settingHeaderNotifiesHeaderAdded() {
        Section section = new Section();
        section.registerGroupDataObserver(groupAdapter);
        section.setHeader(header);
        Mockito.verify(groupAdapter).onItemRangeInserted(section, 0, headerSize);
    }

    @Test
    public void removingHeaderNotifiesPreviousHeaderRemoved() {
        Section section = new Section();
        section.registerGroupDataObserver(groupAdapter);
        section.setHeader(header);
        section.removeHeader();
        Mockito.verify(groupAdapter).onItemRangeRemoved(section, 0, headerSize);
    }

    @Test(expected = NullPointerException.class)
    public void settingNullHeaderThrowsNullPointerException() {
        Section section = new Section();
        section.setFooter(null);
    }

    @Test
    public void headerCountIs0WhenThereIsNoHeader() {
        Section section = new Section();
        section.removeHeader();
        Assert.assertEquals(0, section.getItemCount());
    }

    @Test
    public void headerCountIsSizeOfHeader() {
        Section section = new Section();
        section.setHeader(header);
        Assert.assertEquals(headerSize, section.getItemCount());
    }

    @Test
    public void getGroup() {
        Section section = new Section();
        Item item = new DummyItem();
        section.add(item);
        Assert.assertEquals(0, section.getPosition(item));
    }

    @Test
    public void getPositionReturnsNegativeIfItemNotPresent() {
        Section section = new Section();
        Item item = new DummyItem();
        Assert.assertEquals((-1), section.getPosition(item));
    }

    @Test
    public void constructorSetsListenerOnChildren() {
        List<Group> children = new ArrayList<>();
        Item item = Mockito.mock(Item.class);
        children.add(item);
        Section section = new Section(null, children);
        Mockito.verify(item).registerGroupDataObserver(section);
    }

    @Test
    public void setPlaceholderOnEmptySectionAddsPlaceholder() {
        Section section = new Section();
        section.setHeader(header);
        section.setFooter(footer);
        section.registerGroupDataObserver(groupAdapter);
        section.setPlaceholder(placeholder);
        Mockito.verify(groupAdapter).onItemRangeInserted(section, headerSize, placeholderSize);
    }

    @Test
    public void getGroupReturnsPlaceholder() {
        Section section = new Section();
        section.setHeader(header);
        section.setFooter(footer);
        section.setPlaceholder(placeholder);
        Assert.assertEquals(placeholder, section.getGroup(1));
    }

    @Test
    public void setPlaceholderOnNonEmptySectionDoesNotAddPlaceholder() {
        Section section = new Section();
        section.setHeader(header);
        section.setFooter(footer);
        section.add(new DummyItem());
        section.registerGroupDataObserver(groupAdapter);
        section.setPlaceholder(placeholder);
        Mockito.verify(groupAdapter, Mockito.never()).onItemRangeInserted(ArgumentMatchers.any(Section.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
    }

    @Test
    public void placeholderIsIncludedInItemCountIfBodyIsEmpty() {
        Section section = new Section();
        section.setHeader(header);
        section.setFooter(footer);
        section.setPlaceholder(placeholder);
        Assert.assertEquals((((headerSize) + (placeholderSize)) + (footerSize)), section.getItemCount());
    }

    @Test
    public void placeholderIsNotIncludedInItemCountIfBodyHasContent() {
        Section section = new Section();
        section.setHeader(header);
        section.setFooter(footer);
        section.setPlaceholder(placeholder);
        section.add(new DummyItem());
        Assert.assertEquals((((headerSize) + (footerSize)) + 1), section.getItemCount());
    }

    @Test
    public void addEmptyBodyContentDoesNotRemovePlaceholder() {
        Section section = new Section();
        section.registerGroupDataObserver(groupAdapter);
        section.setPlaceholder(placeholder);
        section.add(emptyGroup);
        Mockito.verify(groupAdapter, Mockito.never()).onItemRangeRemoved(ArgumentMatchers.any(Section.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
    }

    @Test
    public void addBodyContentRemovesPlaceholder() {
        Section section = new Section();
        section.registerGroupDataObserver(groupAdapter);
        section.setPlaceholder(placeholder);
        section.add(new DummyItem());
        Mockito.verify(groupAdapter).onItemRangeRemoved(section, 0, placeholderSize);
    }

    @Test
    public void removeAllBodyContentAddsPlaceholder() {
        Section section = new Section();
        section.setPlaceholder(placeholder);
        Item item = new DummyItem();
        section.add(item);
        section.registerGroupDataObserver(groupAdapter);
        section.remove(item);
        Mockito.verify(groupAdapter).onItemRangeInserted(section, 0, placeholderSize);
    }

    @Test
    public void removeAllBodyContentByModifyingAChildGroupAddsPlaceholder() {
        Section section = new Section();
        section.setPlaceholder(placeholder);
        Section childGroup = new Section();
        Item childItem = new DummyItem();
        childGroup.add(childItem);
        section.add(childGroup);
        section.registerGroupDataObserver(groupAdapter);
        childGroup.remove(childItem);
        Mockito.verify(groupAdapter).onItemRangeInserted(section, 0, placeholderSize);
    }

    @Test
    public void removePlaceholderNotifies() {
        Section section = new Section();
        section.setHeader(header);
        section.setFooter(footer);
        section.setPlaceholder(placeholder);
        section.registerGroupDataObserver(groupAdapter);
        section.removePlaceholder();
        Mockito.verify(groupAdapter).onItemRangeRemoved(section, headerSize, placeholderSize);
    }

    @Test
    public void setHideWhenEmptyRemovesAnExistingPlaceholder() {
        Section section = new Section();
        section.setPlaceholder(placeholder);
        section.registerGroupDataObserver(groupAdapter);
        section.setHideWhenEmpty(true);
        Mockito.verify(groupAdapter).onItemRangeRemoved(section, 0, placeholderSize);
    }

    @Test
    public void replacingAnExistingPlaceholderNotifiesChange() {
        Section section = new Section();
        section.setPlaceholder(placeholder);
        section.registerGroupDataObserver(groupAdapter);
        final int newPlaceholderSize = 1;
        Group newPlaceholder = new DummyGroup() {
            @Override
            public int getItemCount() {
                return newPlaceholderSize;
            }
        };
        section.setPlaceholder(newPlaceholder);
        Mockito.verify(groupAdapter).onItemRangeRemoved(section, 0, placeholderSize);
        Mockito.verify(groupAdapter).onItemRangeInserted(section, 0, newPlaceholderSize);
    }

    @Test
    public void setHeaderAddsHeader() {
        Section section = new Section();
        section.registerGroupDataObserver(groupAdapter);
        section.setHeader(header);
        Mockito.verify(groupAdapter).onItemRangeInserted(section, 0, headerSize);
    }

    @Test
    public void removeHeaderRemovesHeader() {
        Section section = new Section();
        section.setHeader(header);
        section.registerGroupDataObserver(groupAdapter);
        section.removeHeader();
        Mockito.verify(groupAdapter).onItemRangeRemoved(section, 0, headerSize);
    }

    @Test
    public void setFooterAddsFooter() {
        Section section = new Section();
        section.registerGroupDataObserver(groupAdapter);
        section.setFooter(footer);
        Mockito.verify(groupAdapter).onItemRangeInserted(section, 0, footerSize);
    }

    @Test
    public void removeFooterRemovesFooter() {
        Section section = new Section();
        section.setFooter(footer);
        section.registerGroupDataObserver(groupAdapter);
        section.removeFooter();
        Mockito.verify(groupAdapter).onItemRangeRemoved(section, 0, footerSize);
    }

    @Test
    public void setHideWhenEmptyRemovesExistingHeaderAndFooter() {
        Section section = new Section();
        section.setHeader(header);
        section.setFooter(footer);
        section.registerGroupDataObserver(groupAdapter);
        section.setHideWhenEmpty(true);
        Mockito.verify(groupAdapter).onItemRangeRemoved(section, 0, ((headerSize) + (footerSize)));
    }

    @Test
    public void setHideWhenEmptyRemovesExistingHeaderFooterAndPlaceholder() {
        Section section = new Section();
        section.setHeader(header);
        section.setFooter(footer);
        section.setPlaceholder(placeholder);
        section.registerGroupDataObserver(groupAdapter);
        section.setHideWhenEmpty(true);
        Mockito.verify(groupAdapter).onItemRangeRemoved(section, 0, (((headerSize) + (footerSize)) + (placeholderSize)));
    }

    @Test
    public void setHideWhenEmptyFalseAddsExistingHeaderAndFooter() {
        Section section = new Section();
        section.setHeader(header);
        section.setFooter(footer);
        section.setHideWhenEmpty(true);
        section.registerGroupDataObserver(groupAdapter);
        section.setHideWhenEmpty(false);
        Mockito.verify(groupAdapter).onItemRangeInserted(section, 0, headerSize);
        Mockito.verify(groupAdapter).onItemRangeInserted(section, headerSize, footerSize);
    }

    @Test
    public void itemCountIsZeroWhenSetHideWhenEmptyTrue() {
        Section section = new Section();
        section.setHeader(header);
        section.setPlaceholder(placeholder);
        section.setFooter(footer);
        section.setHideWhenEmpty(true);
        Assert.assertEquals(0, section.getItemCount());
    }

    @Test
    public void groupCountIsHeaderFooterAndChildrenWhenNonEmpty() {
        Section section = new Section();
        section.setHeader(header);
        section.setPlaceholder(placeholder);
        section.setFooter(footer);
        section.add(new DummyItem());
        section.add(new DummyItem());
        Assert.assertEquals(4, section.getGroupCount());
    }

    @Test
    public void groupCountIsHeaderFooterAndPlaceholderWhenEmpty() {
        Section section = new Section();
        section.setHeader(header);
        section.setPlaceholder(placeholder);
        section.setFooter(footer);
        Assert.assertEquals(3, section.getGroupCount());
    }

    @Test
    public void groupCountIsZeroWhenEmptyAndSetHideWhenEmpty() {
        Section section = new Section();
        section.setHeader(header);
        section.setPlaceholder(placeholder);
        section.setFooter(footer);
        section.setHideWhenEmpty(true);
        Assert.assertEquals(0, section.getGroupCount());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void whenSectionIsEmptyAndSetHideWhenEmptyGetGroupThrowsException() {
        Section section = new Section();
        section.setHeader(header);
        section.setPlaceholder(placeholder);
        section.setFooter(footer);
        section.setHideWhenEmpty(true);
        section.getGroup(0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void addAllAtNonZeroPositionWhenEmptyThrowIndexOutOfBoundsException() {
        final Section section = new Section();
        section.registerGroupDataObserver(groupAdapter);
        section.addAll(1, Arrays.asList(new DummyItem(), new DummyItem()));
    }

    @Test
    public void addAllAtPositionWhenEmptyNotifiesAdapterAtIndexZero() {
        final Section section = new Section();
        section.registerGroupDataObserver(groupAdapter);
        section.addAll(0, Arrays.asList(new DummyItem(), new DummyItem()));
        Mockito.verify(groupAdapter).onItemRangeInserted(section, 0, 2);
    }

    @Test
    public void addAllAtPositionWhenNonEmptyNotifiesAdapterAtCorrectIndex() {
        final Section section = new Section(Arrays.asList(new DummyItem(), new DummyItem()));
        section.registerGroupDataObserver(groupAdapter);
        section.addAll(2, Arrays.asList(new DummyItem(), new DummyItem(), new DummyItem()));
        Mockito.verify(groupAdapter).onItemRangeInserted(section, 2, 3);
    }

    @Test
    public void addAllAtPositionWithEmptyNestedGroupNotifiesAdapterAtZeroIndex() {
        final Section nestedSection = new Section();
        final Section section = new Section();
        section.add(nestedSection);
        section.registerGroupDataObserver(groupAdapter);
        section.addAll(1, Arrays.asList(new DummyItem(), new DummyItem(), new DummyItem()));
        Mockito.verify(groupAdapter).onItemRangeInserted(section, 0, 3);
    }

    @Test
    public void addAllAtPositionFrontWithNestedGroupNotifiesAdapterAtCorrectIndex() {
        final List<DummyItem> nestedItems = Arrays.asList(new DummyItem(), new DummyItem());
        final Section nestedSection = new Section(nestedItems);
        final Section section = new Section();
        section.add(nestedSection);
        section.registerGroupDataObserver(groupAdapter);
        section.addAll(0, Arrays.asList(new DummyItem(), new DummyItem(), new DummyItem()));
        Mockito.verify(groupAdapter).onItemRangeInserted(section, 0, 3);
    }

    @Test
    public void addAllAtPositionMiddleWithNestedGroupNotifiesAdapterAtCorrectIndex() {
        final Section nestedSection1 = new Section(Arrays.asList(new DummyItem(), new DummyItem()));
        final Section nestedSection2 = new Section(Arrays.asList(new DummyItem(), new DummyItem()));
        final Section section = new Section(Arrays.asList(nestedSection1, nestedSection2));
        section.registerGroupDataObserver(groupAdapter);
        section.addAll(1, Arrays.asList(new DummyItem(), new DummyItem(), new DummyItem()));
        Mockito.verify(groupAdapter).onItemRangeInserted(section, 2, 3);
    }

    @Test
    public void addAllAtPositionEndWithNestedGroupNotifiesAdapterAtCorrectIndex() {
        final List<DummyItem> nestedItems = Arrays.asList(new DummyItem(), new DummyItem());
        final Section nestedSection = new Section(nestedItems);
        final Section section = new Section();
        section.add(nestedSection);
        section.registerGroupDataObserver(groupAdapter);
        section.addAll(1, Arrays.asList(new DummyItem(), new DummyItem()));
        Mockito.verify(groupAdapter).onItemRangeInserted(section, 2, 2);
    }

    @Test
    public void addItemToNestedSectionNotifiesAtCorrectIndex() throws Exception {
        final Section rootSection = new Section();
        rootSection.registerGroupDataObserver(groupAdapter);
        groupAdapter.add(rootSection);
        final Section nestedSection1 = new Section(Arrays.asList(new DummyItem(), new DummyItem(), new DummyItem()));
        rootSection.add(nestedSection1);
        final Section nestedSection2 = new Section();
        rootSection.add(nestedSection2);
        Mockito.reset(groupAdapter);
        nestedSection2.add(new DummyItem());
        Mockito.verify(groupAdapter).onItemRangeInserted(rootSection, 3, 1);
    }

    @Test
    public void addGroupToNestedSectionNotifiesAtCorrectIndex() throws Exception {
        final Section rootSection = new Section();
        rootSection.registerGroupDataObserver(groupAdapter);
        groupAdapter.add(rootSection);
        final Section nestedSection1 = new Section(Arrays.asList(new DummyItem(), new DummyItem(), new DummyItem()));
        rootSection.add(nestedSection1);
        final Section nestedSection2 = new Section(Arrays.asList(new DummyItem(), new DummyItem()));
        Mockito.reset(groupAdapter);
        rootSection.add(nestedSection2);
        Mockito.verify(groupAdapter).onItemRangeInserted(rootSection, 3, 2);
    }

    @Test
    public void addGroupToNestedSectionWithHeaderNotifiesAtCorrectIndex() throws Exception {
        final Section rootSection = new Section();
        rootSection.setHeader(new DummyItem());
        rootSection.registerGroupDataObserver(groupAdapter);
        groupAdapter.add(rootSection);
        final Section nestedSection1 = new Section(Arrays.asList(new DummyItem(), new DummyItem(), new DummyItem()));
        rootSection.add(nestedSection1);
        final Section nestedSection2 = new Section(Arrays.asList(new DummyItem(), new DummyItem()));
        Mockito.reset(groupAdapter);
        rootSection.add(nestedSection2);
        Mockito.verify(groupAdapter).onItemRangeInserted(rootSection, 4, 2);
    }

    @Test
    public void insertGroupToNestedSectionNotifiesAtCorrectIndex() throws Exception {
        final Section rootSection = new Section();
        rootSection.registerGroupDataObserver(groupAdapter);
        groupAdapter.add(rootSection);
        final Section nestedSection1 = new Section(Arrays.asList(new DummyItem(), new DummyItem()));
        rootSection.add(nestedSection1);
        final Section nestedSection2 = new Section(Arrays.asList(new DummyItem(), new DummyItem(), new DummyItem()));
        rootSection.add(nestedSection2);
        final Section nestedSection3 = new Section(Arrays.asList(new DummyItem(), new DummyItem()));
        Mockito.reset(groupAdapter);
        rootSection.add(1, nestedSection3);
        Mockito.verify(groupAdapter).onItemRangeInserted(rootSection, 2, 2);
    }

    @Test
    public void removeGroupFromNestedSectionNotifiesAtCorrectIndex() throws Exception {
        final Section rootSection = new Section();
        rootSection.registerGroupDataObserver(groupAdapter);
        groupAdapter.add(rootSection);
        final Section nestedSection1 = new Section(Arrays.asList(new DummyItem(), new DummyItem(), new DummyItem()));
        rootSection.add(nestedSection1);
        final Section nestedSection2 = new Section(Arrays.asList(new DummyItem(), new DummyItem()));
        rootSection.add(nestedSection2);
        Mockito.reset(groupAdapter);
        rootSection.remove(nestedSection2);
        Mockito.verify(groupAdapter).onItemRangeRemoved(rootSection, 3, 2);
    }

    @Test
    public void removeAllGroupFromNestedSectionNotifiesAtCorrectIndex() throws Exception {
        final Section rootSection = new Section();
        rootSection.registerGroupDataObserver(groupAdapter);
        groupAdapter.add(rootSection);
        final Section nestedSection1 = new Section(Arrays.asList(new DummyItem(), new DummyItem(), new DummyItem()));
        rootSection.add(nestedSection1);
        final Section nestedSection2 = new Section(Arrays.asList(new DummyItem(), new DummyItem()));
        rootSection.add(nestedSection2);
        Mockito.reset(groupAdapter);
        rootSection.removeAll(Collections.singletonList(nestedSection2));
        Mockito.verify(groupAdapter).onItemRangeRemoved(rootSection, 3, 2);
    }

    @Test
    public void removeAllUnorderedGroupsFromNestedSectionNotifiesAtCorrectIndexes() throws Exception {
        final Section rootSection = new Section();
        rootSection.registerGroupDataObserver(groupAdapter);
        groupAdapter.add(rootSection);
        final Section nestedSection1 = new Section(Arrays.asList(new DummyItem(), new DummyItem(), new DummyItem()));
        rootSection.add(nestedSection1);
        final Section nestedSection2 = new Section(Collections.singletonList(new DummyItem()));
        rootSection.add(nestedSection2);
        final Section nestedSection3 = new Section(Arrays.asList(new DummyItem(), new DummyItem()));
        rootSection.add(nestedSection3);
        Mockito.reset(groupAdapter);
        rootSection.removeAll(Arrays.asList(nestedSection2, nestedSection3, nestedSection1));
        final InOrder adapterCalls = Mockito.inOrder(groupAdapter, groupAdapter, groupAdapter);
        adapterCalls.verify(groupAdapter).onItemRangeRemoved(rootSection, 3, 1);
        adapterCalls.verify(groupAdapter).onItemRangeRemoved(rootSection, 3, 2);
        adapterCalls.verify(groupAdapter).onItemRangeRemoved(rootSection, 0, 3);
    }

    @Test
    public void updateGroupChangesRange() {
        List<Item> children = new ArrayList<Item>();
        children.add(new AlwaysUpdatingItem(1));
        children.add(new AlwaysUpdatingItem(2));
        Section group = new Section();
        group.setHeader(new DummyItem());
        group.registerGroupDataObserver(groupAdapter);
        group.update(children);
        Mockito.verify(groupAdapter).onItemRangeInserted(group, 1, 2);
        Mockito.verifyNoMoreInteractions(groupAdapter);
        group.update(children);
        Mockito.verify(groupAdapter).onItemRangeChanged(group, 1, 2, null);
        Mockito.verifyNoMoreInteractions(groupAdapter);
    }

    @Test
    public void notifyChangeInAnItemCausesParentToNotifyChange() {
        List<Item> children = new ArrayList<Item>();
        Item item = new DummyItem();
        children.add(item);
        Section group = new Section();
        group.setHeader(new DummyItem());
        group.update(children);
        group.registerGroupDataObserver(groupAdapter);
        item.notifyChanged();
        Mockito.verify(groupAdapter).onItemChanged(group, 1);
    }

    @Test
    public void updateWithTheSameItemAndSameContentsDoesNotNotifyChange() {
        List<Item> children = new ArrayList<Item>();
        Item item = new ContentUpdatingItem(1, "contents");
        children.add(item);
        Section group = new Section();
        group.setHeader(new DummyItem());
        group.update(children);
        group.registerGroupDataObserver(groupAdapter);
        group.update(children);
        Mockito.verifyNoMoreInteractions(groupAdapter);
    }

    @Test
    public void updateWithTheSameItemButDifferentContentsNotifiesChange() {
        Item oldItem = new ContentUpdatingItem(1, "contents");
        Section group = new Section();
        group.setHeader(new DummyItem());
        group.update(Collections.singletonList(oldItem));
        group.registerGroupDataObserver(groupAdapter);
        Item newItem = new ContentUpdatingItem(1, "new contents");
        group.update(Collections.singletonList(newItem));
        Mockito.verify(groupAdapter).onItemRangeChanged(group, 1, 1, null);
    }

    @Test
    public void updateWithADifferentItemNotifiesRemoveAndAdd() {
        Item oldItem = new ContentUpdatingItem(1, "contents");
        Section group = new Section();
        group.setHeader(new DummyItem());
        group.update(Collections.singletonList(oldItem));
        group.registerGroupDataObserver(groupAdapter);
        Item newItem = new ContentUpdatingItem(2, "contents");
        group.update(Collections.singletonList(newItem));
        Mockito.verify(groupAdapter).onItemRangeRemoved(group, 1, 1);
        Mockito.verify(groupAdapter).onItemRangeInserted(group, 1, 1);
    }

    @Test
    public void updateWithANestedGroupsNotifiesRemoveAndAdd() {
        Item oldItem = new ContentUpdatingItem(1, "contents");
        Section group = new Section();
        group.setHeader(new DummyItem());
        group.update(Collections.singletonList(oldItem));
        group.registerGroupDataObserver(groupAdapter);
        Item newItem = new ContentUpdatingItem(2, "new contents");
        Section newGroup = new Section();
        newGroup.add(newItem);
        group.update(Collections.singletonList(newGroup));
        Mockito.verify(groupAdapter).onItemRangeRemoved(group, 1, 1);
        Mockito.verify(groupAdapter).onItemRangeInserted(group, 1, 1);
    }

    @Test
    public void updateGroupWithPlaceholderNotifiesRemovePlaceholderAndInsert() {
        List<Item> children = new ArrayList<Item>();
        children.add(new AlwaysUpdatingItem(1));
        children.add(new AlwaysUpdatingItem(2));
        Section group = new Section();
        group.setHeader(new DummyItem());
        group.setPlaceholder(new DummyItem());
        group.registerGroupDataObserver(groupAdapter);
        group.update(children);
        Mockito.verify(groupAdapter).onItemRangeRemoved(group, 1, 1);
        Mockito.verify(groupAdapter).onItemRangeInserted(group, 1, 2);
        Mockito.verifyNoMoreInteractions(groupAdapter);
    }

    @Test
    public void updateGroupToEmptyWithPlaceholderNotifiesRemoveAndInsertPlaceholder() {
        List<Item> children = new ArrayList<Item>();
        children.add(new AlwaysUpdatingItem(1));
        children.add(new AlwaysUpdatingItem(2));
        Section group = new Section();
        group.setHeader(new DummyItem());
        group.setPlaceholder(new DummyItem());
        group.update(children);
        group.registerGroupDataObserver(groupAdapter);
        group.update(new ArrayList<Group>());
        Mockito.verify(groupAdapter).onItemRangeRemoved(group, 1, 2);
        Mockito.verify(groupAdapter).onItemRangeInserted(group, 1, 1);
        Mockito.verifyNoMoreInteractions(groupAdapter);
    }
}

