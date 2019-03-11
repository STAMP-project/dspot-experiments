/**
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.recyclerview.widget;


import androidx.test.filters.SmallTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static SortedList.INVALID_POSITION;


@RunWith(JUnit4.class)
@SmallTest
public class SortedListTest {
    SortedList<SortedListTest.Item> mList;

    List<SortedListTest.Pair> mAdditions = new ArrayList<>();

    List<SortedListTest.Pair> mRemovals = new ArrayList<>();

    List<SortedListTest.Pair> mMoves = new ArrayList<>();

    List<SortedListTest.Pair> mUpdates = new ArrayList<>();

    private boolean mPayloadChanges = false;

    List<SortedListTest.PayloadChange> mPayloadUpdates = new ArrayList<>();

    Queue<SortedListTest.AssertListStateRunnable> mCallbackRunnables;

    List<SortedListTest.Event> mEvents = new ArrayList<>();

    private SortedList.Callback<SortedListTest.Item> mCallback;

    SortedListTest.InsertedCallback<SortedListTest.Item> mInsertedCallback;

    SortedListTest.ChangedCallback<SortedListTest.Item> mChangedCallback;

    private Comparator<? super SortedListTest.Item> sItemComparator = new Comparator<SortedListTest.Item>() {
        @Override
        public int compare(SortedListTest.Item o1, SortedListTest.Item o2) {
            return mCallback.compare(o1, o2);
        }
    };

    private abstract class InsertedCallback<T> {
        public abstract void onInserted(int position, int count);
    }

    private abstract class ChangedCallback<T> {
        public abstract void onChanged(int position, int count);
    }

    @Test
    public void testValidMethodsDuringOnInsertedCallbackFromEmptyList() {
        final SortedListTest.Item[] items = new SortedListTest.Item[]{ new SortedListTest.Item(0), new SortedListTest.Item(1), new SortedListTest.Item(2) };
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        mInsertedCallback = new SortedListTest.InsertedCallback<SortedListTest.Item>() {
            @Override
            public void onInserted(int position, int count) {
                for (int i = 0; i < count; i++) {
                    Assert.assertEquals(mList.get(i), items[i]);
                    Assert.assertEquals(mList.indexOf(items[i]), i);
                    atomicInteger.incrementAndGet();
                }
            }
        };
        mList.add(items[0]);
        mList.clear();
        mList.addAll(items, false);
        Assert.assertEquals(4, atomicInteger.get());
    }

    @Test
    public void testEmpty() {
        Assert.assertEquals("empty", mList.size(), 0);
    }

    @Test
    public void testAdd() {
        SortedListTest.Item item = new SortedListTest.Item(1);
        Assert.assertEquals(insert(item), 0);
        Assert.assertEquals(size(), 1);
        Assert.assertTrue(mAdditions.contains(new SortedListTest.Pair(0, 1)));
        SortedListTest.Item item2 = new SortedListTest.Item(2);
        item2.cmpField = (item.cmpField) + 1;
        Assert.assertEquals(insert(item2), 1);
        Assert.assertEquals(size(), 2);
        Assert.assertTrue(mAdditions.contains(new SortedListTest.Pair(1, 1)));
        SortedListTest.Item item3 = new SortedListTest.Item(3);
        item3.cmpField = (item.cmpField) - 1;
        mAdditions.clear();
        Assert.assertEquals(insert(item3), 0);
        Assert.assertEquals(size(), 3);
        Assert.assertTrue(mAdditions.contains(new SortedListTest.Pair(0, 1)));
    }

    @Test
    public void testAddDuplicate() {
        SortedListTest.Item item = new SortedListTest.Item(1);
        SortedListTest.Item item2 = new SortedListTest.Item(item.id);
        insert(item);
        Assert.assertEquals(0, insert(item2));
        Assert.assertEquals(1, size());
        Assert.assertEquals(1, mAdditions.size());
        Assert.assertEquals(0, mUpdates.size());
    }

    @Test
    public void testRemove() {
        SortedListTest.Item item = new SortedListTest.Item(1);
        Assert.assertFalse(remove(item));
        Assert.assertEquals(0, mRemovals.size());
        insert(item);
        Assert.assertTrue(remove(item));
        Assert.assertEquals(1, mRemovals.size());
        Assert.assertTrue(mRemovals.contains(new SortedListTest.Pair(0, 1)));
        Assert.assertEquals(0, size());
        Assert.assertFalse(remove(item));
        Assert.assertEquals(1, mRemovals.size());
    }

    @Test
    public void testRemove2() {
        SortedListTest.Item item = new SortedListTest.Item(1);
        SortedListTest.Item item2 = new SortedListTest.Item(2, 1, 1);
        insert(item);
        Assert.assertFalse(remove(item2));
        Assert.assertEquals(0, mRemovals.size());
    }

    @Test
    public void clearTest() {
        insert(new SortedListTest.Item(1));
        insert(new SortedListTest.Item(2));
        Assert.assertEquals(2, mList.size());
        mList.clear();
        Assert.assertEquals(0, mList.size());
        insert(new SortedListTest.Item(3));
        Assert.assertEquals(1, mList.size());
    }

    @Test
    public void testBatch() {
        mList.beginBatchedUpdates();
        for (int i = 0; i < 5; i++) {
            mList.add(new SortedListTest.Item(i));
        }
        Assert.assertEquals(0, mAdditions.size());
        mList.endBatchedUpdates();
        Assert.assertTrue(mAdditions.contains(new SortedListTest.Pair(0, 5)));
    }

    @Test
    public void testRandom() throws Throwable {
        Random random = new Random(System.nanoTime());
        List<SortedListTest.Item> copy = new ArrayList<SortedListTest.Item>();
        StringBuilder log = new StringBuilder();
        int id = 1;
        try {
            for (int i = 0; i < 10000; i++) {
                switch (random.nextInt(3)) {
                    case 0 :
                        // ADD
                        SortedListTest.Item item = new SortedListTest.Item((id++));
                        copy.add(item);
                        insert(item);
                        log.append("add ").append(item).append("\n");
                        break;
                    case 1 :
                        // REMOVE
                        if ((copy.size()) > 0) {
                            int index = random.nextInt(mList.size());
                            item = mList.get(index);
                            log.append("remove ").append(item).append("\n");
                            Assert.assertTrue(copy.remove(item));
                            Assert.assertTrue(mList.remove(item));
                        }
                        break;
                    case 2 :
                        // UPDATE
                        if ((copy.size()) > 0) {
                            int index = random.nextInt(mList.size());
                            item = mList.get(index);
                            // TODO this cannot work
                            SortedListTest.Item newItem = new SortedListTest.Item(item.id, item.cmpField, random.nextInt(1000));
                            while ((newItem.data) == (item.data)) {
                                newItem.data = random.nextInt(1000);
                            } 
                            log.append("update ").append(item).append(" to ").append(newItem).append("\n");
                            int itemIndex = mList.add(newItem);
                            copy.remove(item);
                            copy.add(newItem);
                            Assert.assertSame(mList.get(itemIndex), newItem);
                            Assert.assertNotSame(mList.get(index), item);
                        }
                        break;
                    case 3 :
                        // UPDATE AT
                        if ((copy.size()) > 0) {
                            int index = random.nextInt(mList.size());
                            item = mList.get(index);
                            SortedListTest.Item newItem = new SortedListTest.Item(item.id, random.nextInt(), random.nextInt());
                            mList.updateItemAt(index, newItem);
                            copy.remove(item);
                            copy.add(newItem);
                            log.append("update at ").append(index).append(" ").append(item).append(" to ").append(newItem).append("\n");
                        }
                }
                int lastCmp = Integer.MIN_VALUE;
                for (int index = 0; index < (copy.size()); index++) {
                    Assert.assertFalse(((mList.indexOf(copy.get(index))) == (INVALID_POSITION)));
                    Assert.assertTrue(((mList.get(index).cmpField) >= lastCmp));
                    lastCmp = mList.get(index).cmpField;
                    Assert.assertTrue(copy.contains(mList.get(index)));
                }
                for (int index = 0; index < (mList.size()); index++) {
                    Assert.assertNotNull(mList.mData[index]);
                }
                for (int index = mList.size(); index < (mList.mData.length); index++) {
                    Assert.assertNull(mList.mData[index]);
                }
            }
        } catch (Throwable t) {
            Collections.sort(copy, sItemComparator);
            log.append("Items:\n");
            for (SortedListTest.Item item : copy) {
                log.append(item).append("\n");
            }
            log.append("SortedList:\n");
            for (int i = 0; i < (mList.size()); i++) {
                log.append(mList.get(i)).append("\n");
            }
            throw new Throwable((" \nlog:\n" + (log.toString())), t);
        }
    }

    @Test
    public void testAddAllMerge() throws Throwable {
        mList.addAll(new SortedListTest.Item[0]);
        assertIntegrity(0, "addAll, empty list, empty input");
        Assert.assertEquals(0, mAdditions.size());
        // Add first 5 even numbers. Test adding to an empty list.
        mList.addAll(SortedListTest.createItems(0, 8, 2));
        assertIntegrity(5, "addAll, empty list, non-empty input");
        Assert.assertEquals(1, mAdditions.size());
        Assert.assertTrue(mAdditions.contains(new SortedListTest.Pair(0, 5)));
        mList.addAll(new SortedListTest.Item[0]);
        assertIntegrity(5, "addAll, non-empty list, empty input");
        Assert.assertEquals(1, mAdditions.size());
        // Add 5 more even numbers, shuffled (test pre-sorting).
        mList.addAll(SortedListTest.shuffle(SortedListTest.createItems(10, 18, 2)));
        assertIntegrity(10, "addAll, shuffled input");
        Assert.assertEquals(2, mAdditions.size());
        Assert.assertTrue(mAdditions.contains(new SortedListTest.Pair(5, 5)));
        // Add 5 more even numbers, reversed (test pre-sorting).
        mList.addAll(SortedListTest.shuffle(SortedListTest.createItems(28, 20, (-2))));
        assertIntegrity(15, "addAll, reversed input");
        Assert.assertEquals(3, mAdditions.size());
        Assert.assertTrue(mAdditions.contains(new SortedListTest.Pair(10, 5)));
        // Add first 10 odd numbers.
        // Test the merge when the new items run out first.
        mList.addAll(SortedListTest.createItems(1, 19, 2));
        assertIntegrity(25, "addAll, merging in the middle");
        Assert.assertEquals(13, mAdditions.size());
        for (int i = 1; i <= 19; i += 2) {
            Assert.assertTrue(mAdditions.contains(new SortedListTest.Pair(i, 1)));
        }
        // Add 10 more odd numbers.
        // Test the merge when the old items run out first.
        mList.addAll(SortedListTest.createItems(21, 39, 2));
        assertIntegrity(35, "addAll, merging at the end");
        Assert.assertEquals(18, mAdditions.size());
        for (int i = 21; i <= 27; i += 2) {
            Assert.assertTrue(mAdditions.contains(new SortedListTest.Pair(i, 1)));
        }
        Assert.assertTrue(mAdditions.contains(new SortedListTest.Pair(29, 6)));
        // Add 5 more even numbers.
        mList.addAll(SortedListTest.createItems(30, 38, 2));
        assertIntegrity(40, "addAll, merging more");
        Assert.assertEquals(23, mAdditions.size());
        for (int i = 30; i <= 38; i += 2) {
            Assert.assertTrue(mAdditions.contains(new SortedListTest.Pair(i, 1)));
        }
        Assert.assertEquals(0, mMoves.size());
        Assert.assertEquals(0, mUpdates.size());
        Assert.assertEquals(0, mRemovals.size());
        assertSequentialOrder();
    }

    @Test
    public void testAddAllUpdates() throws Throwable {
        // Add first 5 even numbers.
        SortedListTest.Item[] evenItems = SortedListTest.createItems(0, 8, 2);
        for (SortedListTest.Item item : evenItems) {
            item.data = 1;
        }
        mList.addAll(evenItems);
        Assert.assertEquals(5, size());
        Assert.assertEquals(1, mAdditions.size());
        Assert.assertTrue(mAdditions.contains(new SortedListTest.Pair(0, 5)));
        Assert.assertEquals(0, mUpdates.size());
        SortedListTest.Item[] sameEvenItems = SortedListTest.createItems(0, 8, 2);
        for (SortedListTest.Item item : sameEvenItems) {
            item.data = 1;
        }
        mList.addAll(sameEvenItems);
        Assert.assertEquals(1, mAdditions.size());
        Assert.assertEquals(0, mUpdates.size());
        SortedListTest.Item[] newEvenItems = SortedListTest.createItems(0, 8, 2);
        for (SortedListTest.Item item : newEvenItems) {
            item.data = 2;
        }
        mList.addAll(newEvenItems);
        Assert.assertEquals(5, size());
        Assert.assertEquals(1, mAdditions.size());
        Assert.assertEquals(1, mUpdates.size());
        Assert.assertTrue(mUpdates.contains(new SortedListTest.Pair(0, 5)));
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(2, mList.get(i).data);
        }
        // Add all numbers from 0 to 9
        SortedListTest.Item[] sequentialItems = SortedListTest.createItems(0, 9, 1);
        for (SortedListTest.Item item : sequentialItems) {
            item.data = 3;
        }
        mList.addAll(sequentialItems);
        // Odd numbers should have been added.
        Assert.assertEquals(6, mAdditions.size());
        for (int i = 0; i < 5; i++) {
            Assert.assertTrue(mAdditions.contains(new SortedListTest.Pair(((i * 2) + 1), 1)));
        }
        // All even items should have been updated.
        Assert.assertEquals(6, mUpdates.size());
        for (int i = 0; i < 5; i++) {
            Assert.assertTrue(mUpdates.contains(new SortedListTest.Pair((i * 2), 1)));
        }
        Assert.assertEquals(10, size());
        // All items should have the latest data value.
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(3, mList.get(i).data);
        }
        Assert.assertEquals(0, mMoves.size());
        Assert.assertEquals(0, mRemovals.size());
        assertSequentialOrder();
    }

    @Test
    public void testAddAllWithDuplicates() throws Throwable {
        final int maxCmpField = 5;
        final int idsPerCmpField = 10;
        final int maxUniqueId = maxCmpField * idsPerCmpField;
        final int maxGeneration = 5;
        SortedListTest.Item[] items = new SortedListTest.Item[maxUniqueId * maxGeneration];
        int index = 0;
        for (int generation = 0; generation < maxGeneration; generation++) {
            int uniqueId = 0;
            for (int cmpField = 0; cmpField < maxCmpField; cmpField++) {
                for (int id = 0; id < idsPerCmpField; id++) {
                    SortedListTest.Item item = new SortedListTest.Item((uniqueId++), cmpField, generation);
                    items[(index++)] = item;
                }
            }
        }
        mList.addAll(items);
        assertIntegrity(maxUniqueId, "addAll with duplicates");
        // Check that the most recent items have made it to the list.
        for (int i = 0; i != (size()); i++) {
            SortedListTest.Item item = mList.get(i);
            Assert.assertEquals((maxGeneration - 1), item.data);
        }
    }

    @Test
    public void testAddAllFast() throws Throwable {
        mList.addAll(new SortedListTest.Item[0], true);
        assertIntegrity(0, "addAll(T[],boolean), empty list, with empty input");
        Assert.assertEquals(0, mAdditions.size());
        mList.addAll(SortedListTest.createItems(0, 9, 1), true);
        assertIntegrity(10, "addAll(T[],boolean), empty list, non-empty input");
        Assert.assertEquals(1, mAdditions.size());
        Assert.assertTrue(mAdditions.contains(new SortedListTest.Pair(0, 10)));
        mList.addAll(new SortedListTest.Item[0], true);
        Assert.assertEquals(1, mAdditions.size());
        assertIntegrity(10, "addAll(T[],boolean), non-empty list, empty input");
        mList.addAll(SortedListTest.createItems(10, 19, 1), true);
        Assert.assertEquals(2, mAdditions.size());
        Assert.assertTrue(mAdditions.contains(new SortedListTest.Pair(10, 10)));
        assertIntegrity(20, "addAll(T[],boolean), non-empty list, non-empty input");
    }

    @Test
    public void testAddAllCollection() throws Throwable {
        Collection<SortedListTest.Item> itemList = new ArrayList<SortedListTest.Item>();
        for (int i = 0; i < 5; i++) {
            itemList.add(new SortedListTest.Item(i));
        }
        mList.addAll(itemList);
        Assert.assertEquals(1, mAdditions.size());
        Assert.assertTrue(mAdditions.contains(new SortedListTest.Pair(0, itemList.size())));
        assertIntegrity(itemList.size(), "addAll on collection");
    }

    @Test
    public void testAddAllStableSort() {
        int id = 0;
        SortedListTest.Item item = new SortedListTest.Item((id++), 0, 0);
        mList.add(item);
        // Create a few items with the same sort order.
        SortedListTest.Item[] items = new SortedListTest.Item[3];
        for (int i = 0; i < 3; i++) {
            items[i] = new SortedListTest.Item((id++), item.cmpField, 0);
            Assert.assertEquals(0, mCallback.compare(item, items[i]));
        }
        mList.addAll(items);
        Assert.assertEquals((1 + (items.length)), size());
        // Check that the order has been preserved.
        for (int i = 0; i < (size()); i++) {
            Assert.assertEquals(i, mList.get(i).id);
        }
    }

    @Test
    public void testAddAllAccessFromCallbacks() {
        // Add first 5 even numbers.
        SortedListTest.Item[] evenItems = SortedListTest.createItems(0, 8, 2);
        for (SortedListTest.Item item : evenItems) {
            item.data = 1;
        }
        mInsertedCallback = new SortedListTest.InsertedCallback<SortedListTest.Item>() {
            @Override
            public void onInserted(int position, int count) {
                Assert.assertEquals(0, position);
                Assert.assertEquals(5, count);
                for (int i = 0; i < count; i++) {
                    Assert.assertEquals((i * 2), mList.get(i).id);
                }
                assertIntegrity(5, (((("onInserted(" + position) + ", ") + count) + ")"));
            }
        };
        mList.addAll(evenItems);
        Assert.assertEquals(1, mAdditions.size());
        Assert.assertEquals(0, mUpdates.size());
        // Add all numbers from 0 to 9. This should trigger 5 change and 5 insert notifications.
        SortedListTest.Item[] sequentialItems = SortedListTest.createItems(0, 9, 1);
        for (SortedListTest.Item item : sequentialItems) {
            item.data = 2;
        }
        mChangedCallback = new SortedListTest.ChangedCallback<SortedListTest.Item>() {
            int expectedSize = 5;

            @Override
            public void onChanged(int position, int count) {
                Assert.assertEquals(1, count);
                Assert.assertEquals(position, mList.get(position).id);
                assertIntegrity((++(expectedSize)), (("onChanged(" + position) + ")"));
            }
        };
        mInsertedCallback = new SortedListTest.InsertedCallback<SortedListTest.Item>() {
            int expectedSize = 5;

            @Override
            public void onInserted(int position, int count) {
                Assert.assertEquals(1, count);
                Assert.assertEquals(position, mList.get(position).id);
                assertIntegrity((++(expectedSize)), (("onInserted(" + position) + ")"));
            }
        };
        mList.addAll(sequentialItems);
        Assert.assertEquals(6, mAdditions.size());
        Assert.assertEquals(5, mUpdates.size());
    }

    @Test
    public void testModificationFromCallbackThrows() {
        final SortedListTest.Item extraItem = new SortedListTest.Item(0);
        SortedListTest.Item[] items = SortedListTest.createItems(1, 5, 2);
        for (SortedListTest.Item item : items) {
            item.data = 1;
        }
        mList.addAll(items);
        mInsertedCallback = new SortedListTest.InsertedCallback<SortedListTest.Item>() {
            @Override
            public void onInserted(int position, int count) {
                try {
                    mList.add(new SortedListTest.Item(1));
                    Assert.fail("add must throw from within a callback");
                } catch (IllegalStateException e) {
                }
                try {
                    mList.addAll(SortedListTest.createItems(0, 0, 1));
                    Assert.fail("addAll must throw from within a callback");
                } catch (IllegalStateException e) {
                }
                try {
                    mList.addAll(SortedListTest.createItems(0, 0, 1), true);
                    Assert.fail("addAll(T[],boolean) must throw from within a callback");
                } catch (IllegalStateException e) {
                }
                try {
                    mList.remove(extraItem);
                    Assert.fail("remove must throw from within a callback");
                } catch (IllegalStateException e) {
                }
                try {
                    mList.removeItemAt(0);
                    Assert.fail("removeItemAt must throw from within a callback");
                } catch (IllegalStateException e) {
                }
                try {
                    mList.updateItemAt(0, extraItem);
                    Assert.fail("updateItemAt must throw from within a callback");
                } catch (IllegalStateException e) {
                }
                try {
                    mList.recalculatePositionOfItemAt(0);
                    Assert.fail("recalculatePositionOfItemAt must throw from within a callback");
                } catch (IllegalStateException e) {
                }
                try {
                    mList.clear();
                    Assert.fail("recalculatePositionOfItemAt must throw from within a callback");
                } catch (IllegalStateException e) {
                }
            }
        };
        // Make sure that the last one notification is change, so that the above callback is
        // not called from endBatchUpdates when the nested alls are actually OK.
        items = SortedListTest.createItems(1, 5, 1);
        for (SortedListTest.Item item : items) {
            item.data = 2;
        }
        mList.addAll(items);
        assertIntegrity(5, "Modification from callback");
    }

    @Test
    public void testAddAllOutsideBatchedUpdates() {
        mList.add(new SortedListTest.Item(1));
        Assert.assertEquals(1, mAdditions.size());
        mList.add(new SortedListTest.Item(2));
        Assert.assertEquals(2, mAdditions.size());
        mList.addAll(new SortedListTest.Item(3), new SortedListTest.Item(4));
        Assert.assertEquals(3, mAdditions.size());
        mList.add(new SortedListTest.Item(5));
        Assert.assertEquals(4, mAdditions.size());
        mList.add(new SortedListTest.Item(6));
        Assert.assertEquals(5, mAdditions.size());
    }

    @Test
    public void testAddAllInsideBatchedUpdates() {
        mList.beginBatchedUpdates();
        mList.add(new SortedListTest.Item(1));
        Assert.assertEquals(0, mAdditions.size());
        mList.add(new SortedListTest.Item(2));
        Assert.assertEquals(0, mAdditions.size());
        mList.addAll(new SortedListTest.Item(3), new SortedListTest.Item(4));
        Assert.assertEquals(0, mAdditions.size());
        mList.add(new SortedListTest.Item(5));
        Assert.assertEquals(0, mAdditions.size());
        mList.add(new SortedListTest.Item(6));
        Assert.assertEquals(0, mAdditions.size());
        mList.endBatchedUpdates();
        Assert.assertEquals(1, mAdditions.size());
        Assert.assertTrue(mAdditions.contains(new SortedListTest.Pair(0, 6)));
    }

    @Test
    public void testAddExistingItemCallsChangeWithPayload() {
        mList.addAll(new SortedListTest.Item(1), new SortedListTest.Item(2), new SortedListTest.Item(3));
        mPayloadChanges = true;
        // add an item with the same id but a new data field i.e. send an update
        final SortedListTest.Item twoUpdate = new SortedListTest.Item(2);
        twoUpdate.data = 1337;
        mList.add(twoUpdate);
        Assert.assertEquals(1, mPayloadUpdates.size());
        final SortedListTest.PayloadChange update = mPayloadUpdates.get(0);
        Assert.assertEquals(1, update.position);
        Assert.assertEquals(1, update.count);
        Assert.assertEquals(1337, update.payload);
        Assert.assertEquals(3, size());
    }

    @Test
    public void testUpdateItemCallsChangeWithPayload() {
        mList.addAll(new SortedListTest.Item(1), new SortedListTest.Item(2), new SortedListTest.Item(3));
        mPayloadChanges = true;
        // add an item with the same id but a new data field i.e. send an update
        final SortedListTest.Item twoUpdate = new SortedListTest.Item(2);
        twoUpdate.data = 1337;
        mList.updateItemAt(1, twoUpdate);
        Assert.assertEquals(1, mPayloadUpdates.size());
        final SortedListTest.PayloadChange update = mPayloadUpdates.get(0);
        Assert.assertEquals(1, update.position);
        Assert.assertEquals(1, update.count);
        Assert.assertEquals(1337, update.payload);
        Assert.assertEquals(3, size());
        Assert.assertEquals(1337, mList.get(1).data);
    }

    @Test
    public void testAddMultipleExistingItemCallsChangeWithPayload() {
        mList.addAll(new SortedListTest.Item(1), new SortedListTest.Item(2), new SortedListTest.Item(3));
        mPayloadChanges = true;
        // add two items with the same ids but a new data fields i.e. send two updates
        final SortedListTest.Item twoUpdate = new SortedListTest.Item(2);
        twoUpdate.data = 222;
        final SortedListTest.Item threeUpdate = new SortedListTest.Item(3);
        threeUpdate.data = 333;
        mList.addAll(twoUpdate, threeUpdate);
        Assert.assertEquals(2, mPayloadUpdates.size());
        final SortedListTest.PayloadChange update1 = mPayloadUpdates.get(0);
        Assert.assertEquals(1, update1.position);
        Assert.assertEquals(1, update1.count);
        Assert.assertEquals(222, update1.payload);
        final SortedListTest.PayloadChange update2 = mPayloadUpdates.get(1);
        Assert.assertEquals(2, update2.position);
        Assert.assertEquals(1, update2.count);
        Assert.assertEquals(333, update2.payload);
        Assert.assertEquals(3, size());
    }

    @Test
    public void replaceAll_mayModifyInputFalse_doesNotModify() {
        mList.addAll(new SortedListTest.Item(1), new SortedListTest.Item(2));
        SortedListTest.Item replacement0 = new SortedListTest.Item(4);
        SortedListTest.Item replacement1 = new SortedListTest.Item(3);
        SortedListTest.Item[] replacements = new SortedListTest.Item[]{ replacement0, replacement1 };
        mList.replaceAll(replacements, false);
        Assert.assertSame(replacement0, replacements[0]);
        Assert.assertSame(replacement1, replacements[1]);
    }

    @Test
    public void replaceAll_varArgs_isEquivalentToDefault() {
        mList.addAll(new SortedListTest.Item(1), new SortedListTest.Item(2));
        SortedListTest.Item replacement0 = new SortedListTest.Item(3);
        SortedListTest.Item replacement1 = new SortedListTest.Item(4);
        mList.replaceAll(replacement0, replacement1);
        Assert.assertEquals(mList.get(0), replacement0);
        Assert.assertEquals(mList.get(1), replacement1);
        Assert.assertEquals(2, mList.size());
    }

    @Test
    public void replaceAll_collection_isEquivalentToDefaultWithMayModifyInputFalse() {
        mList.addAll(new SortedListTest.Item(1), new SortedListTest.Item(2));
        SortedListTest.Item replacement0 = new SortedListTest.Item(4);
        SortedListTest.Item replacement1 = new SortedListTest.Item(3);
        List<SortedListTest.Item> replacements = new ArrayList<>();
        replacements.add(replacement0);
        replacements.add(replacement1);
        mList.replaceAll(replacements);
        Assert.assertEquals(mList.get(0), replacement1);
        Assert.assertEquals(mList.get(1), replacement0);
        Assert.assertSame(replacements.get(0), replacement0);
        Assert.assertSame(replacements.get(1), replacement1);
        Assert.assertEquals(2, mList.size());
    }

    @Test
    public void replaceAll_callsChangeWithPayload() {
        mList.addAll(new SortedListTest.Item(1), new SortedListTest.Item(2), new SortedListTest.Item(3));
        mPayloadChanges = true;
        final SortedListTest.Item twoUpdate = new SortedListTest.Item(2);
        twoUpdate.data = 222;
        final SortedListTest.Item threeUpdate = new SortedListTest.Item(3);
        threeUpdate.data = 333;
        mList.replaceAll(twoUpdate, threeUpdate);
        Assert.assertEquals(2, mPayloadUpdates.size());
        final SortedListTest.PayloadChange update1 = mPayloadUpdates.get(0);
        Assert.assertEquals(0, update1.position);
        Assert.assertEquals(1, update1.count);
        Assert.assertEquals(222, update1.payload);
        final SortedListTest.PayloadChange update2 = mPayloadUpdates.get(1);
        Assert.assertEquals(1, update2.position);
        Assert.assertEquals(1, update2.count);
        Assert.assertEquals(333, update2.payload);
    }

    @Test
    public void replaceAll_totallyEquivalentData_worksCorrectly() {
        SortedListTest.Item[] items1 = SortedListTest.createItemsFromInts(1, 2, 3);
        SortedListTest.Item[] items2 = SortedListTest.createItemsFromInts(1, 2, 3);
        mList.addAll(items1);
        mEvents.clear();
        mList.replaceAll(items2);
        Assert.assertEquals(0, mEvents.size());
        Assert.assertTrue(sortedListEquals(mList, items2));
    }

    @Test
    public void replaceAll_removalsAndAdds1_worksCorrectly() {
        SortedListTest.Item[] items1 = SortedListTest.createItemsFromInts(1, 3, 5);
        SortedListTest.Item[] items2 = SortedListTest.createItemsFromInts(2, 4);
        mList.addAll(items1);
        mEvents.clear();
        mCallbackRunnables = new LinkedList<>();
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(SortedListTest.createItemsFromInts(2, 3, 5)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(SortedListTest.createItemsFromInts(2, 5)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(SortedListTest.createItemsFromInts(2, 4, 5)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mList.replaceAll(items2);
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 0, 1), mEvents.get(0));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 0, 1), mEvents.get(1));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 1, 1), mEvents.get(2));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 1, 1), mEvents.get(3));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 2, 1), mEvents.get(4));
        Assert.assertEquals(5, mEvents.size());
        Assert.assertTrue(sortedListEquals(mList, items2));
        Assert.assertTrue(mCallbackRunnables.isEmpty());
    }

    @Test
    public void replaceAll_removalsAndAdds2_worksCorrectly() {
        SortedListTest.Item[] items1 = SortedListTest.createItemsFromInts(2, 4);
        SortedListTest.Item[] items2 = SortedListTest.createItemsFromInts(1, 3, 5);
        mList.addAll(items1);
        mEvents.clear();
        mCallbackRunnables = new LinkedList<>();
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(SortedListTest.createItemsFromInts(1, 4)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(SortedListTest.createItemsFromInts(1, 3, 4)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(SortedListTest.createItemsFromInts(1, 3)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mList.replaceAll(items2);
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 0, 1), mEvents.get(0));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 1, 1), mEvents.get(1));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 1, 1), mEvents.get(2));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 2, 1), mEvents.get(3));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 2, 1), mEvents.get(4));
        Assert.assertEquals(5, mEvents.size());
        Assert.assertTrue(sortedListEquals(mList, items2));
        Assert.assertTrue(mCallbackRunnables.isEmpty());
    }

    @Test
    public void replaceAll_removalsAndAdds3_worksCorrectly() {
        SortedListTest.Item[] items1 = SortedListTest.createItemsFromInts(1, 3, 5);
        SortedListTest.Item[] items2 = SortedListTest.createItemsFromInts(2, 3, 4);
        mList.addAll(items1);
        mEvents.clear();
        mCallbackRunnables = new LinkedList<>();
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(SortedListTest.createItemsFromInts(2, 3, 5)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(SortedListTest.createItemsFromInts(2, 3, 4, 5)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mList.replaceAll(items2);
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 0, 1), mEvents.get(0));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 0, 1), mEvents.get(1));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 2, 1), mEvents.get(2));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 3, 1), mEvents.get(3));
        Assert.assertEquals(4, mEvents.size());
        Assert.assertTrue(sortedListEquals(mList, items2));
        Assert.assertTrue(mCallbackRunnables.isEmpty());
    }

    @Test
    public void replaceAll_removalsAndAdds4_worksCorrectly() {
        SortedListTest.Item[] items1 = SortedListTest.createItemsFromInts(2, 3, 4);
        SortedListTest.Item[] items2 = SortedListTest.createItemsFromInts(1, 3, 5);
        mList.addAll(items1);
        mEvents.clear();
        mCallbackRunnables = new LinkedList<>();
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(SortedListTest.createItemsFromInts(1, 3, 4)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(SortedListTest.createItemsFromInts(1, 3)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mList.replaceAll(items2);
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 0, 1), mEvents.get(0));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 1, 1), mEvents.get(1));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 2, 1), mEvents.get(2));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 2, 1), mEvents.get(3));
        Assert.assertEquals(4, mEvents.size());
        Assert.assertTrue(sortedListEquals(mList, items2));
        Assert.assertTrue(mCallbackRunnables.isEmpty());
    }

    @Test
    public void replaceAll_removalsAndAdds5_worksCorrectly() {
        SortedListTest.Item[] items1 = SortedListTest.createItemsFromInts(1, 2, 3);
        SortedListTest.Item[] items2 = SortedListTest.createItemsFromInts(3, 4, 5);
        mList.addAll(items1);
        mEvents.clear();
        mCallbackRunnables = new LinkedList<>();
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mList.replaceAll(items2);
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 0, 2), mEvents.get(0));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 1, 2), mEvents.get(1));
        Assert.assertEquals(2, mEvents.size());
        Assert.assertTrue(sortedListEquals(mList, items2));
        Assert.assertTrue(mCallbackRunnables.isEmpty());
    }

    @Test
    public void replaceAll_removalsAndAdds6_worksCorrectly() {
        SortedListTest.Item[] items1 = SortedListTest.createItemsFromInts(3, 4, 5);
        SortedListTest.Item[] items2 = SortedListTest.createItemsFromInts(1, 2, 3);
        mList.addAll(items1);
        mEvents.clear();
        mCallbackRunnables = new LinkedList<>();
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mList.replaceAll(items2);
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 0, 2), mEvents.get(0));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 3, 2), mEvents.get(1));
        Assert.assertEquals(2, mEvents.size());
        Assert.assertTrue(sortedListEquals(mList, items2));
        Assert.assertTrue(mCallbackRunnables.isEmpty());
    }

    @Test
    public void replaceAll_move1_worksCorrectly() {
        SortedListTest.Item[] items1 = SortedListTest.createItemsFromInts(1, 2, 3);
        SortedListTest.Item[] items2 = new SortedListTest.Item[]{ new SortedListTest.Item(2), new SortedListTest.Item(3), new SortedListTest.Item(1, 4, 1) };
        mList.addAll(items1);
        mEvents.clear();
        mCallbackRunnables = new LinkedList<>();
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mList.replaceAll(items2);
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 0, 1), mEvents.get(0));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 2, 1), mEvents.get(1));
        Assert.assertEquals(2, mEvents.size());
        Assert.assertTrue(sortedListEquals(mList, items2));
        Assert.assertTrue(mCallbackRunnables.isEmpty());
    }

    @Test
    public void replaceAll_move2_worksCorrectly() {
        SortedListTest.Item[] items1 = SortedListTest.createItemsFromInts(1, 2, 3);
        SortedListTest.Item[] items2 = new SortedListTest.Item[]{ new SortedListTest.Item(3, 0, 3), new SortedListTest.Item(1), new SortedListTest.Item(2) };
        mList.addAll(items1);
        mEvents.clear();
        mCallbackRunnables = new LinkedList<>();
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mList.replaceAll(items2);
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 0, 1), mEvents.get(0));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 3, 1), mEvents.get(1));
        Assert.assertEquals(2, mEvents.size());
        Assert.assertTrue(sortedListEquals(mList, items2));
        Assert.assertTrue(mCallbackRunnables.isEmpty());
    }

    @Test
    public void replaceAll_move3_worksCorrectly() {
        SortedListTest.Item[] items1 = SortedListTest.createItemsFromInts(1, 3, 5, 7, 9);
        SortedListTest.Item[] items2 = new SortedListTest.Item[]{ new SortedListTest.Item(3, 0, 3), new SortedListTest.Item(1), new SortedListTest.Item(5), new SortedListTest.Item(9), new SortedListTest.Item(7, 10, 7) };
        mList.addAll(items1);
        mEvents.clear();
        mCallbackRunnables = new LinkedList<>();
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(new SortedListTest.Item(3, 0, 3), new SortedListTest.Item(1), new SortedListTest.Item(5), new SortedListTest.Item(7), new SortedListTest.Item(9)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(new SortedListTest.Item(3, 0, 3), new SortedListTest.Item(1), new SortedListTest.Item(5), new SortedListTest.Item(9)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mList.replaceAll(items2);
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 0, 1), mEvents.get(0));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 2, 1), mEvents.get(1));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 3, 1), mEvents.get(2));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 4, 1), mEvents.get(3));
        Assert.assertEquals(4, mEvents.size());
        Assert.assertTrue(sortedListEquals(mList, items2));
        Assert.assertTrue(mCallbackRunnables.isEmpty());
    }

    @Test
    public void replaceAll_move4_worksCorrectly() {
        SortedListTest.Item[] items1 = SortedListTest.createItemsFromInts(1, 3, 5, 7, 9);
        SortedListTest.Item[] items2 = new SortedListTest.Item[]{ new SortedListTest.Item(3), new SortedListTest.Item(1, 4, 1), new SortedListTest.Item(5), new SortedListTest.Item(9, 6, 9), new SortedListTest.Item(7) };
        mList.addAll(items1);
        mEvents.clear();
        mCallbackRunnables = new LinkedList<>();
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(new SortedListTest.Item(3), new SortedListTest.Item(1, 4, 1), new SortedListTest.Item(5), new SortedListTest.Item(7), new SortedListTest.Item(9)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(new SortedListTest.Item(3), new SortedListTest.Item(1, 4, 1), new SortedListTest.Item(5), new SortedListTest.Item(9, 6, 9), new SortedListTest.Item(7), new SortedListTest.Item(9)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mList.replaceAll(items2);
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 0, 1), mEvents.get(0));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 1, 1), mEvents.get(1));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 3, 1), mEvents.get(2));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 5, 1), mEvents.get(3));
        Assert.assertEquals(4, mEvents.size());
        Assert.assertTrue(sortedListEquals(mList, items2));
        Assert.assertTrue(mCallbackRunnables.isEmpty());
    }

    @Test
    public void replaceAll_move5_worksCorrectly() {
        SortedListTest.Item[] items1 = SortedListTest.createItemsFromInts(1, 3, 5, 7, 9);
        SortedListTest.Item[] items2 = new SortedListTest.Item[]{ new SortedListTest.Item(9, 1, 9), new SortedListTest.Item(7, 3, 7), new SortedListTest.Item(5), new SortedListTest.Item(3, 7, 3), new SortedListTest.Item(1, 9, 1) };
        mList.addAll(items1);
        mEvents.clear();
        mCallbackRunnables = new LinkedList<>();
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(new SortedListTest.Item(9, 1, 9), new SortedListTest.Item(3), new SortedListTest.Item(5), new SortedListTest.Item(7), new SortedListTest.Item(9)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(new SortedListTest.Item(9, 1, 9), new SortedListTest.Item(5), new SortedListTest.Item(7), new SortedListTest.Item(9)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(new SortedListTest.Item(9, 1, 9), new SortedListTest.Item(7, 3, 7), new SortedListTest.Item(5), new SortedListTest.Item(7), new SortedListTest.Item(9)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(new SortedListTest.Item(9, 1, 9), new SortedListTest.Item(7, 3, 7), new SortedListTest.Item(5), new SortedListTest.Item(9)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(new SortedListTest.Item(9, 1, 9), new SortedListTest.Item(7, 3, 7), new SortedListTest.Item(5), new SortedListTest.Item(3, 7, 3), new SortedListTest.Item(9)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(new SortedListTest.Item(9, 1, 9), new SortedListTest.Item(7, 3, 7), new SortedListTest.Item(5), new SortedListTest.Item(3, 7, 3)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mList.replaceAll(items2);
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 0, 1), mEvents.get(0));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 0, 1), mEvents.get(1));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 1, 1), mEvents.get(2));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 1, 1), mEvents.get(3));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 3, 1), mEvents.get(4));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 3, 1), mEvents.get(5));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 4, 1), mEvents.get(6));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 4, 1), mEvents.get(7));
        Assert.assertEquals(8, mEvents.size());
        Assert.assertTrue(sortedListEquals(mList, items2));
        Assert.assertTrue(mCallbackRunnables.isEmpty());
    }

    @Test
    public void replaceAll_orderSameItemDifferent_worksCorrectly() {
        SortedListTest.Item[] items1 = new SortedListTest.Item[]{ new SortedListTest.Item(1), new SortedListTest.Item(2, 3, 2), new SortedListTest.Item(5) };
        SortedListTest.Item[] items2 = new SortedListTest.Item[]{ new SortedListTest.Item(1), new SortedListTest.Item(4, 3, 4), new SortedListTest.Item(5) };
        mList.addAll(items1);
        mEvents.clear();
        mCallbackRunnables = new LinkedList<>();
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mList.replaceAll(items2);
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 1, 1), mEvents.get(0));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 1, 1), mEvents.get(1));
        Assert.assertEquals(2, mEvents.size());
        Assert.assertTrue(sortedListEquals(mList, items2));
        Assert.assertTrue(mCallbackRunnables.isEmpty());
    }

    @Test
    public void replaceAll_orderSameItemSameContentsDifferent_worksCorrectly() {
        SortedListTest.Item[] items1 = new SortedListTest.Item[]{ new SortedListTest.Item(1), new SortedListTest.Item(3, 3, 2), new SortedListTest.Item(5) };
        SortedListTest.Item[] items2 = new SortedListTest.Item[]{ new SortedListTest.Item(1), new SortedListTest.Item(3, 3, 4), new SortedListTest.Item(5) };
        mList.addAll(items1);
        mEvents.clear();
        mCallbackRunnables = new LinkedList<>();
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mList.replaceAll(items2);
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.CHANGE, 1, 1), mEvents.get(0));
        Assert.assertEquals(1, mEvents.size());
        Assert.assertTrue(sortedListEquals(mList, items2));
        Assert.assertTrue(mCallbackRunnables.isEmpty());
    }

    @Test
    public void replaceAll_allTypesOfChanges1_worksCorrectly() {
        SortedListTest.Item[] items1 = SortedListTest.createItemsFromInts(2, 5, 6);
        SortedListTest.Item[] items2 = new SortedListTest.Item[]{ new SortedListTest.Item(1), new SortedListTest.Item(3, 2, 3), new SortedListTest.Item(6, 6, 7) };
        mList.addAll(items1);
        mEvents.clear();
        mCallbackRunnables = new LinkedList<>();
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(SortedListTest.createItemsFromInts(1, 5, 6)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(new SortedListTest.Item(1), new SortedListTest.Item(3, 2, 3), new SortedListTest.Item(5), new SortedListTest.Item(6)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(new SortedListTest.Item(1), new SortedListTest.Item(3, 2, 3), new SortedListTest.Item(6)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mList.replaceAll(items2);
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 0, 1), mEvents.get(0));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 1, 1), mEvents.get(1));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 1, 1), mEvents.get(2));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 2, 1), mEvents.get(3));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.CHANGE, 2, 1), mEvents.get(4));
        Assert.assertEquals(5, mEvents.size());
        Assert.assertTrue(sortedListEquals(mList, items2));
        Assert.assertTrue(mCallbackRunnables.isEmpty());
    }

    @Test
    public void replaceAll_allTypesOfChanges2_worksCorrectly() {
        SortedListTest.Item[] items1 = SortedListTest.createItemsFromInts(1, 4, 6);
        SortedListTest.Item[] items2 = new SortedListTest.Item[]{ new SortedListTest.Item(1, 1, 2), new SortedListTest.Item(3), new SortedListTest.Item(5, 4, 5) };
        mList.addAll(items1);
        mEvents.clear();
        mCallbackRunnables = new LinkedList<>();
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(new SortedListTest.Item(1, 1, 2), new SortedListTest.Item(3), new SortedListTest.Item(4), new SortedListTest.Item(6)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(new SortedListTest.Item(1, 1, 2), new SortedListTest.Item(3), new SortedListTest.Item(6)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(new SortedListTest.Item(1, 1, 2), new SortedListTest.Item(3), new SortedListTest.Item(5, 4, 5), new SortedListTest.Item(6)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mList.replaceAll(items2);
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.CHANGE, 0, 1), mEvents.get(0));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 1, 1), mEvents.get(1));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 2, 1), mEvents.get(2));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 2, 1), mEvents.get(3));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 3, 1), mEvents.get(4));
        Assert.assertEquals(5, mEvents.size());
        Assert.assertTrue(sortedListEquals(mList, items2));
        Assert.assertTrue(mCallbackRunnables.isEmpty());
    }

    @Test
    public void replaceAll_allTypesOfChanges3_worksCorrectly() {
        SortedListTest.Item[] items1 = SortedListTest.createItemsFromInts(1, 2);
        SortedListTest.Item[] items2 = new SortedListTest.Item[]{ new SortedListTest.Item(2, 2, 3), new SortedListTest.Item(3, 2, 4), new SortedListTest.Item(5) };
        mList.addAll(items1);
        mEvents.clear();
        mCallbackRunnables = new LinkedList<>();
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(new SortedListTest.Item(2, 2, 3)));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mList.replaceAll(items2);
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 0, 1), mEvents.get(0));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.CHANGE, 0, 1), mEvents.get(1));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 1, 2), mEvents.get(2));
        Assert.assertEquals(3, mEvents.size());
        Assert.assertTrue(sortedListEquals(mList, items2));
        Assert.assertTrue(mCallbackRunnables.isEmpty());
    }

    @Test
    public void replaceAll_newItemsAreIdentical_resultIsDeduped() {
        SortedListTest.Item[] items = SortedListTest.createItemsFromInts(1, 1);
        mList.replaceAll(items);
        Assert.assertEquals(new SortedListTest.Item(1), mList.get(0));
        Assert.assertEquals(1, mList.size());
    }

    @Test
    public void replaceAll_newItemsUnsorted_resultIsSorted() {
        SortedListTest.Item[] items = SortedListTest.createItemsFromInts(2, 1);
        mList.replaceAll(items);
        Assert.assertEquals(new SortedListTest.Item(1), mList.get(0));
        Assert.assertEquals(new SortedListTest.Item(2), mList.get(1));
        Assert.assertEquals(2, mList.size());
    }

    @Test
    public void replaceAll_calledAfterBeginBatchedUpdates_worksCorrectly() {
        SortedListTest.Item[] items1 = SortedListTest.createItemsFromInts(1, 2, 3);
        SortedListTest.Item[] items2 = SortedListTest.createItemsFromInts(4, 5, 6);
        mList.addAll(items1);
        mEvents.clear();
        mCallbackRunnables = new LinkedList<>();
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mCallbackRunnables.add(new SortedListTest.AssertListStateRunnable(items2));
        mList.beginBatchedUpdates();
        mList.replaceAll(items2);
        mList.endBatchedUpdates();
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.REMOVE, 0, 3), mEvents.get(0));
        Assert.assertEquals(new SortedListTest.Event(SortedListTest.TYPE.ADD, 0, 3), mEvents.get(1));
        Assert.assertEquals(2, mEvents.size());
        Assert.assertTrue(sortedListEquals(mList, items2));
        Assert.assertTrue(mCallbackRunnables.isEmpty());
    }

    static class Item {
        final int id;

        int cmpField;

        int data;

        Item(int allFields) {
            this(allFields, allFields, allFields);
        }

        Item(int id, int compField, int data) {
            this.id = id;
            this.cmpField = compField;
            this.data = data;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            SortedListTest.Item item = ((SortedListTest.Item) (o));
            return (((id) == (item.id)) && ((cmpField) == (item.cmpField))) && ((data) == (item.data));
        }

        @Override
        public String toString() {
            return ((((("Item(id=" + (id)) + ", cmpField=") + (cmpField)) + ", data=") + (data)) + ')';
        }
    }

    private static final class Pair {
        final int first;

        final int second;

        public Pair(int first) {
            this.first = first;
            this.second = Integer.MIN_VALUE;
        }

        public Pair(int first, int second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            SortedListTest.Pair pair = ((SortedListTest.Pair) (o));
            if ((first) != (pair.first)) {
                return false;
            }
            if ((second) != (pair.second)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result = first;
            result = (31 * result) + (second);
            return result;
        }
    }

    private enum TYPE {

        ADD,
        REMOVE,
        MOVE,
        CHANGE;}

    private final class AssertListStateRunnable implements Runnable {
        private SortedListTest.Item[] mExpectedItems;

        AssertListStateRunnable(SortedListTest.Item... expectedItems) {
            this.mExpectedItems = expectedItems;
        }

        @Override
        public void run() {
            try {
                Assert.assertEquals(mExpectedItems.length, mList.size());
                for (int i = (mExpectedItems.length) - 1; i >= 0; i--) {
                    Assert.assertEquals(mExpectedItems[i], mList.get(i));
                    Assert.assertEquals(i, mList.indexOf(mExpectedItems[i]));
                }
            } catch (AssertionError assertionError) {
                throw new AssertionError((((((assertionError.getMessage()) + "\nExpected: ") + (Arrays.toString(mExpectedItems))) + "\nActual: ") + (SortedListTest.sortedListToString(mList))));
            }
        }
    }

    private static final class Event {
        private final SortedListTest.TYPE mType;

        private final int mVal1;

        private final int mVal2;

        Event(SortedListTest.TYPE type, int val1, int val2) {
            this.mType = type;
            this.mVal1 = val1;
            this.mVal2 = val2;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            SortedListTest.Event that = ((SortedListTest.Event) (o));
            return (((mType) == (that.mType)) && ((mVal1) == (that.mVal1))) && ((mVal2) == (that.mVal2));
        }

        @Override
        public String toString() {
            return ((((("Event(" + (mType)) + ", ") + (mVal1)) + ", ") + (mVal2)) + ")";
        }
    }

    private static final class PayloadChange {
        public final int position;

        public final int count;

        public final Object payload;

        PayloadChange(int position, int count, Object payload) {
            this.position = position;
            this.count = count;
            this.payload = payload;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            SortedListTest.PayloadChange payloadChange = ((SortedListTest.PayloadChange) (o));
            if ((position) != (payloadChange.position))
                return false;

            if ((count) != (payloadChange.count))
                return false;

            return (payload) != null ? payload.equals(payloadChange.payload) : (payloadChange.payload) == null;
        }

        @Override
        public int hashCode() {
            int result = position;
            result = (31 * result) + (count);
            result = (31 * result) + ((payload) != null ? payload.hashCode() : 0);
            return result;
        }
    }
}

