/**
 * Copyright 2014-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.litho.sections.common;


import DataDiffSection.Builder;
import com.facebook.litho.EventHandler;
import com.facebook.litho.HasEventDispatcher;
import com.facebook.litho.sections.Section;
import com.facebook.litho.sections.SectionContext;
import com.facebook.litho.sections.SectionTree;
import com.facebook.litho.specmodels.internal.ImmutableList;
import com.facebook.litho.testing.sections.TestGroupSection;
import com.facebook.litho.testing.sections.TestTarget;
import com.facebook.litho.testing.sections.TestTarget.Operation;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;


/**
 * Tests {@link DataDiffSectionSpec}
 */
@RunWith(ComponentsTestRunner.class)
public class DataDiffSectionSpecTest {
    private SectionContext mSectionContext;

    private SectionTree mSectionTree;

    private TestTarget mTestTarget;

    @Mock
    public EventHandler<OnCheckIsSameItemEvent> mIsSameItemEventEventHandler;

    @Mock
    public HasEventDispatcher mHasEventDispatcher;

    @Test
    public void testSetRoot() {
        final List<String> data = DataDiffSectionSpecTest.generateData(100);
        final TestGroupSection section = TestGroupSection.create(mSectionContext).data(data).build();
        mSectionTree.setRoot(section);
        final List<Operation> executedOperations = mTestTarget.getOperations();
        assertThat(executedOperations.size()).isEqualTo(1);
        final Operation operation = executedOperations.get(0);
        assertRangeOperation(operation, INSERT_RANGE, 0, 100);
        DataDiffSectionSpecTest.assertOperation(operation, INSERT_RANGE, 0, (-1), 100, null, data);
    }

    @Test
    public void testAppendData() {
        final List<String> oldData = DataDiffSectionSpecTest.generateData(100);
        mSectionTree.setRoot(TestGroupSection.create(mSectionContext).data(oldData).build());
        List<Operation> executedOperations = mTestTarget.getOperations();
        assertThat(executedOperations.size()).isEqualTo(1);
        final Operation operation = executedOperations.get(0);
        assertRangeOperation(operation, INSERT_RANGE, 0, 100);
        DataDiffSectionSpecTest.assertOperation(operation, INSERT_RANGE, 0, (-1), 100, null, oldData);
        mTestTarget.clear();
        final List<String> newData = DataDiffSectionSpecTest.generateData(200);
        mSectionTree.setRoot(TestGroupSection.create(mSectionContext).data(newData).build());
        executedOperations = mTestTarget.getOperations();
        assertThat(executedOperations.size()).isEqualTo(1);
        final Operation newOperation = executedOperations.get(0);
        assertRangeOperation(newOperation, INSERT_RANGE, 100, 100);
        DataDiffSectionSpecTest.assertOperation(newOperation, INSERT_RANGE, 100, (-1), 100, null, newData.subList(100, 200));
    }

    @Test
    public void testInsertData() {
        final List<String> oldData = DataDiffSectionSpecTest.generateData(100);
        mSectionTree.setRoot(TestGroupSection.create(mSectionContext).data(oldData).build());
        List<Operation> executedOperations = mTestTarget.getOperations();
        assertThat(executedOperations.size()).isEqualTo(1);
        final Operation operation = executedOperations.get(0);
        assertRangeOperation(operation, TestTarget.INSERT_RANGE, 0, 100);
        DataDiffSectionSpecTest.assertOperation(operation, INSERT_RANGE, 0, (-1), 100, null, oldData);
        mTestTarget.clear();
        final List<String> newData = DataDiffSectionSpecTest.generateData(100);
        newData.add(6, "new item");
        newData.add(9, "new item");
        newData.add(12, "new item");
        mSectionTree.setRoot(TestGroupSection.create(mSectionContext).data(newData).build());
        executedOperations = mTestTarget.getOperations();
        assertThat(executedOperations.size()).isEqualTo(3);
        final Operation newOperation1 = executedOperations.get(0);
        DataDiffSectionSpecTest.assertOperation(newOperation1, INSERT, 10, (-1), 1, null, "new item");
        final Operation newOperation2 = executedOperations.get(1);
        DataDiffSectionSpecTest.assertOperation(newOperation2, INSERT, 8, (-1), 1, null, "new item");
        final Operation newOperation3 = executedOperations.get(2);
        DataDiffSectionSpecTest.assertOperation(newOperation3, INSERT, 6, (-1), 1, null, "new item");
    }

    @Test
    public void testMoveData() {
        final List<String> oldData = DataDiffSectionSpecTest.generateData(3);
        mSectionTree.setRoot(TestGroupSection.create(mSectionContext).data(oldData).build());
        List<Operation> executedOperations = mTestTarget.getOperations();
        assertThat(executedOperations.size()).isEqualTo(1);
        final Operation operation = executedOperations.get(0);
        assertRangeOperation(operation, TestTarget.INSERT_RANGE, 0, 3);
        DataDiffSectionSpecTest.assertOperation(operation, INSERT_RANGE, 0, (-1), 3, null, oldData);
        mTestTarget.clear();
        List<String> newData = new ArrayList<>();
        for (int i = 2; i >= 0; i--) {
            newData.add(Integer.toString(i));
        }
        mSectionTree.setRoot(TestGroupSection.create(mSectionContext).data(newData).build());
        executedOperations = mTestTarget.getOperations();
        assertThat(executedOperations.size()).isEqualTo(2);
        final Operation newOperation1 = executedOperations.get(0);
        DataDiffSectionSpecTest.assertOperation(newOperation1, MOVE, 1, 0, 1, "1", "1");
        final Operation newOperation2 = executedOperations.get(1);
        DataDiffSectionSpecTest.assertOperation(newOperation2, MOVE, 2, 0, 1, "2", "2");
    }

    @Test
    public void testRemoveRangeData() {
        final List<String> oldData = DataDiffSectionSpecTest.generateData(100);
        mSectionTree.setRoot(TestGroupSection.create(mSectionContext).data(oldData).build());
        List<Operation> executedOperations = mTestTarget.getOperations();
        assertThat(executedOperations.size()).isEqualTo(1);
        final Operation operation = executedOperations.get(0);
        assertRangeOperation(operation, TestTarget.INSERT_RANGE, 0, 100);
        DataDiffSectionSpecTest.assertOperation(operation, INSERT_RANGE, 0, (-1), 100, null, oldData);
        mTestTarget.clear();
        final List<String> newData = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            newData.add(("" + i));
        }
        for (int i = 90; i < 100; i++) {
            newData.add(("" + i));
        }
        // data = [0...49, 90...99]
        mSectionTree.setRoot(TestGroupSection.create(mSectionContext).data(newData).build());
        executedOperations = mTestTarget.getOperations();
        assertThat(1).isEqualTo(executedOperations.size());
        final Operation newOperation1 = executedOperations.get(0);
        assertRangeOperation(newOperation1, DELETE_RANGE, 50, 40);
        final List<String> deletedData = new ArrayList<>();
        for (int i = 50; i < 90; i++) {
            deletedData.add(("" + i));
        }
        DataDiffSectionSpecTest.assertOperation(newOperation1, DELETE_RANGE, 50, (-1), 40, deletedData, null);
    }

    @Test
    public void testRemoveData() throws Exception {
        final List<String> oldData = DataDiffSectionSpecTest.generateData(100);
        mSectionTree.setRoot(TestGroupSection.create(mSectionContext).data(oldData).build());
        List<Operation> executedOperations = mTestTarget.getOperations();
        assertThat(executedOperations.size()).isEqualTo(1);
        final Operation operation = executedOperations.get(0);
        assertRangeOperation(operation, INSERT_RANGE, 0, 100);
        DataDiffSectionSpecTest.assertOperation(operation, INSERT_RANGE, 0, (-1), 100, null, oldData);
        mTestTarget.clear();
        final List<String> newData = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            newData.add(("" + i));
        }
        newData.remove(9);
        newData.remove(91);
        mSectionTree.setRoot(TestGroupSection.create(mSectionContext).data(newData).build());
        executedOperations = mTestTarget.getOperations();
        assertThat(2).isEqualTo(executedOperations.size());
        DataDiffSectionSpecTest.assertOperation(executedOperations.get(0), DELETE, 92, (-1), 1, ImmutableList.of("92"), null);
        DataDiffSectionSpecTest.assertOperation(executedOperations.get(1), DELETE, 9, (-1), 1, ImmutableList.of("9"), null);
    }

    @Test
    public void testUpdateData() {
        final List<String> oldData = DataDiffSectionSpecTest.generateData(100);
        mSectionTree.setRoot(TestGroupSection.create(mSectionContext).data(oldData).build());
        List<Operation> executedOperations = mTestTarget.getOperations();
        assertThat(executedOperations.size()).isEqualTo(1);
        final Operation operation = executedOperations.get(0);
        assertRangeOperation(operation, INSERT_RANGE, 0, 100);
        DataDiffSectionSpecTest.assertOperation(operation, INSERT_RANGE, 0, (-1), 100, null, oldData);
        mTestTarget.clear();
        List<String> newData = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            newData.add(("different " + i));
        }
        mSectionTree.setRoot(TestGroupSection.create(mSectionContext).isSameItemComparator(new Comparator() {
            @Override
            public int compare(Object lhs, Object rhs) {
                return 0;
            }
        }).isSameContentComparator(new Comparator() {
            @Override
            public int compare(Object lhs, Object rhs) {
                return -1;
            }
        }).data(newData).build());
        executedOperations = mTestTarget.getOperations();
        assertThat(executedOperations.size()).isEqualTo(1);
        final Operation newOperation1 = executedOperations.get(0);
        assertRangeOperation(newOperation1, UPDATE_RANGE, 0, 100);
        DataDiffSectionSpecTest.assertOperation(newOperation1, UPDATE_RANGE, 0, (-1), 100, oldData, newData);
    }

    @Test
    public void testComplexOperations1() {
        final List<String> oldData = ImmutableList.of("a", "b", "c", "d", "e");
        mSectionTree.setRoot(TestGroupSection.create(mSectionContext).data(oldData).build());
        mTestTarget.clear();
        final List<String> newData = ImmutableList.of("f", "a", "g", "b", "c", "d", "e*");
        mSectionTree.setRoot(TestGroupSection.create(mSectionContext).isSameItemComparator(new Comparator() {
            @Override
            public int compare(Object lhs, Object rhs) {
                String left = ((String) (lhs));
                String right = ((String) (rhs));
                return (left.charAt(0)) - (right.charAt(0));
            }
        }).isSameContentComparator(new Comparator() {
            @Override
            public int compare(Object lhs, Object rhs) {
                return -1;
            }
        }).data(newData).build());
        final List<Operation> executedOperations = mTestTarget.getOperations();
        assertThat(executedOperations.size()).isEqualTo(3);
        DataDiffSectionSpecTest.assertOperation(executedOperations.get(0), UPDATE, 4, (-1), 1, "e", "e*");
        DataDiffSectionSpecTest.assertOperation(executedOperations.get(1), INSERT, 1, (-1), 1, null, "g");
        DataDiffSectionSpecTest.assertOperation(executedOperations.get(2), INSERT, 0, (-1), 1, null, "f");
    }

    @Test
    public void testComplexOperations2() {
        final List<String> oldData = ImmutableList.of("a", "b", "c", "d", "e");
        mSectionTree.setRoot(TestGroupSection.create(mSectionContext).data(oldData).build());
        mTestTarget.clear();
        final List<String> newData = ImmutableList.of("f", "a", "g", "e*", "d", "b*");
        mSectionTree.setRoot(TestGroupSection.create(mSectionContext).isSameItemComparator(new Comparator() {
            @Override
            public int compare(Object lhs, Object rhs) {
                String left = ((String) (lhs));
                String right = ((String) (rhs));
                return (left.charAt(0)) - (right.charAt(0));
            }
        }).isSameContentComparator(new Comparator() {
            @Override
            public int compare(Object lhs, Object rhs) {
                return -1;
            }
        }).data(newData).build());
        final List<Operation> executedOperations = mTestTarget.getOperations();
        assertThat(executedOperations.size()).isEqualTo(7);
        DataDiffSectionSpecTest.assertOperation(executedOperations.get(0), DELETE, 2, (-1), 1, "c", null);
        DataDiffSectionSpecTest.assertOperation(executedOperations.get(1), UPDATE, 1, (-1), 1, "b", "b*");
        DataDiffSectionSpecTest.assertOperation(executedOperations.get(2), MOVE, 2, 1, 1, "d", "d");
        DataDiffSectionSpecTest.assertOperation(executedOperations.get(3), MOVE, 3, 1, 1, "e", "e");
        DataDiffSectionSpecTest.assertOperation(executedOperations.get(4), UPDATE, 1, (-1), 1, "e", "e*");
        DataDiffSectionSpecTest.assertOperation(executedOperations.get(5), INSERT, 1, (-1), 1, null, "g");
        DataDiffSectionSpecTest.assertOperation(executedOperations.get(6), INSERT, 0, (-1), 1, null, "f");
    }

    @Test
    public void testShuffledDataWithUpdates() {
        final List<String> oldData = DataDiffSectionSpecTest.generateData(40);
        Collections.shuffle(oldData);
        mSectionTree.setRoot(TestGroupSection.create(mSectionContext).data(oldData).build());
        List<Operation> executedOperations = mTestTarget.getOperations();
        assertThat(executedOperations.size()).isEqualTo(1);
        assertRangeOperation(executedOperations.get(0), TestTarget.INSERT_RANGE, 0, 40);
        mTestTarget.clear();
        final List<String> newData = DataDiffSectionSpecTest.generateData(20);
        Collections.shuffle(newData);
        mSectionTree.setRoot(TestGroupSection.create(mSectionContext).isSameItemComparator(new Comparator() {
            @Override
            public int compare(Object lhs, Object rhs) {
                String left = ((String) (lhs));
                String right = ((String) (rhs));
                return left.compareTo(right);
            }
        }).isSameContentComparator(new Comparator() {
            @Override
            public int compare(Object lhs, Object rhs) {
                return -1;
            }
        }).data(newData).build());
        executedOperations = mTestTarget.getOperations();
        assertBulkOperations(executedOperations, 0, 20, 20);
    }

    @Test
    public void testShuffledData() {
        final List<String> oldData = DataDiffSectionSpecTest.generateData(40);
        Collections.shuffle(oldData);
        mSectionTree.setRoot(TestGroupSection.create(mSectionContext).data(oldData).build());
        List<Operation> executedOperations = mTestTarget.getOperations();
        assertThat(executedOperations.size()).isEqualTo(1);
        final Operation operation = executedOperations.get(0);
        assertRangeOperation(operation, INSERT_RANGE, 0, 40);
        DataDiffSectionSpecTest.assertOperation(operation, INSERT_RANGE, 0, (-1), 40, null, oldData);
        mTestTarget.clear();
        final List<String> newData = DataDiffSectionSpecTest.generateData(20);
        Collections.shuffle(newData);
        mSectionTree.setRoot(TestGroupSection.create(mSectionContext).isSameItemComparator(new Comparator() {
            @Override
            public int compare(Object lhs, Object rhs) {
                String left = ((String) (lhs));
                String right = ((String) (rhs));
                return left.compareTo(right);
            }
        }).data(newData).build());
        executedOperations = mTestTarget.getOperations();
        assertBulkOperations(executedOperations, 0, 0, 20);
    }

    @Test
    public void testTrimmingHeadEqualInstancesOnly() {
        ArrayList<String> previousData = new ArrayList<>();
        ArrayList<String> nextData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String item = "" + i;
            previousData.add(item);
            nextData.add(item);
        }
        for (int i = 0; i < 10; i++) {
            previousData.add(("" + i));
            nextData.add(("" + i));
        }
        DataDiffSection.Builder builder = DataDiffSection.<String>create(mSectionContext).data(previousData).renderEventHandler(null);
        mSectionContext = SectionContext.withScope(mSectionContext, builder.build());
        final DataDiffSectionSpec.Callback<String> callback = /* trimHeadAndTail */
        /* trimSameInstancesOnly */
        new DataDiffSectionSpec.Callback(mSectionContext, previousData, nextData, true, true);
        assertThat(callback.getTrimmedHeadItemsCount()).isEqualTo(10);
        assertThat(callback.getOldListSize()).isEqualTo(10);
        assertThat(callback.getNewListSize()).isEqualTo(10);
    }

    @Test
    public void testTrimmingTailEqualInstancesOnly() {
        ArrayList<String> previousData = new ArrayList<>();
        ArrayList<String> nextData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            previousData.add(("" + i));
            nextData.add(("" + i));
        }
        for (int i = 0; i < 10; i++) {
            String item = "" + i;
            previousData.add(item);
            nextData.add(item);
        }
        DataDiffSection.Builder builder = DataDiffSection.<String>create(mSectionContext).data(previousData).renderEventHandler(null);
        mSectionContext = SectionContext.withScope(mSectionContext, builder.build());
        final DataDiffSectionSpec.Callback<String> callback = /* trimHeadAndTail */
        /* trimSameInstancesOnly */
        new DataDiffSectionSpec.Callback(mSectionContext, previousData, nextData, true, true);
        assertThat(callback.getTrimmedHeadItemsCount()).isEqualTo(0);
        assertThat(callback.getOldListSize()).isEqualTo(10);
        assertThat(callback.getNewListSize()).isEqualTo(10);
    }

    @Test
    public void testTrimmingHeadAndTailEqualInstancesOnly() {
        ArrayList<String> previousData = new ArrayList<>();
        ArrayList<String> nextData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String item = "" + i;
            previousData.add(item);
            nextData.add(item);
        }
        for (int i = 0; i < 10; i++) {
            previousData.add(("" + i));
            nextData.add(("" + i));
        }
        for (int i = 0; i < 10; i++) {
            String item = "" + i;
            previousData.add(item);
            nextData.add(item);
        }
        DataDiffSection.Builder builder = DataDiffSection.<String>create(mSectionContext).data(previousData).renderEventHandler(null);
        mSectionContext = SectionContext.withScope(mSectionContext, builder.build());
        final DataDiffSectionSpec.Callback<String> callback = /* trimHeadAndTail */
        /* trimSameInstancesOnly */
        new DataDiffSectionSpec.Callback(mSectionContext, previousData, nextData, true, true);
        assertThat(callback.getTrimmedHeadItemsCount()).isEqualTo(10);
        assertThat(callback.getOldListSize()).isEqualTo(10);
        assertThat(callback.getNewListSize()).isEqualTo(10);
    }

    @Test
    public void testTrimmingWithComparisonHandlers() {
        ArrayList<String> previousData = new ArrayList<>();
        ArrayList<String> nextData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            previousData.add(i, ("*" + i));
            nextData.add(i, ("*" + i));
        }
        for (int i = 10; i < 20; i++) {
            previousData.add(i, ("#" + i));
            nextData.add(i, ("#" + i));
        }
        Section dispatcher = TestGroupSection.create(mSectionContext).data(nextData).isSameItemComparator(new Comparator() {
            @Override
            public int compare(Object lhs, Object rhs) {
                String prev = ((String) (lhs));
                String next = ((String) (rhs));
                return (prev.contains("*")) && (next.contains("*")) ? 0 : 1;
            }
        }).build();
        mSectionContext = SectionContext.withSectionTree(mSectionContext, mSectionTree);
        mSectionContext = SectionContext.withScope(mSectionContext, dispatcher);
        dispatcher.setScopedContext(mSectionContext);
        EventHandler eh = TestGroupSection.onCheckIsSameItem(mSectionContext);
        EventHandler same = new EventHandler(mHasEventDispatcher, eh.id, new Object[]{ mSectionContext });
        same.mHasEventDispatcher = dispatcher;
        DataDiffSection builder = DataDiffSection.<String>create(mSectionContext).data(previousData).onCheckIsSameItemEventHandler(same).renderEventHandler(null).build();
        mSectionContext = SectionContext.withSectionTree(mSectionContext, mSectionTree);
        mSectionContext = SectionContext.withScope(mSectionContext, builder);
        builder.setScopedContext(mSectionContext);
        final DataDiffSectionSpec.Callback<String> callback = /* trimHeadAndTail */
        /* trimSameInstancesOnly */
        new DataDiffSectionSpec.Callback(mSectionContext, previousData, nextData, true, false);
        assertThat(callback.getTrimmedHeadItemsCount()).isEqualTo(10);
        assertThat(callback.getOldListSize()).isEqualTo(10);
        assertThat(callback.getNewListSize()).isEqualTo(10);
    }

    @Test
    public void testAppendDataTrimming() {
        mSectionTree.setRoot(TestGroupSection.create(mSectionContext).data(DataDiffSectionSpecTest.generateData(100)).trimHeadAndTail(true).trimSameInstancesOnly(true).build());
        List<Operation> executedOperations = mTestTarget.getOperations();
        assertThat(executedOperations.size()).isEqualTo(1);
        assertRangeOperation(executedOperations.get(0), TestTarget.INSERT_RANGE, 0, 100);
        mTestTarget.clear();
        mSectionTree.setRoot(TestGroupSection.create(mSectionContext).data(DataDiffSectionSpecTest.generateData(200)).build());
        executedOperations = mTestTarget.getOperations();
        assertThat(executedOperations.size()).isEqualTo(1);
        assertRangeOperation(executedOperations.get(0), TestTarget.INSERT_RANGE, 100, 100);
    }

    @Test
    public void testLogTag() {
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            data.add(String.valueOf(i));
        }
        DataDiffSection section = DataDiffSection.<String>create(mSectionContext).data(data).renderEventHandler(null).build();
        assertThat(section.getLogTag()).isEqualTo(section.getClass().getSimpleName());
    }
}

