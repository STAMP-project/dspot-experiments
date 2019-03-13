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


import AdapterHelper.UpdateOp;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.test.filters.SmallTest;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
@SmallTest
public class AdapterHelperTest {
    private static final boolean DEBUG = false;

    private boolean mCollectLogs = false;

    private static final String TAG = "AHT";

    private List<AdapterHelperTest.MockViewHolder> mViewHolders;

    private AdapterHelper mAdapterHelper;

    private List<AdapterHelper.UpdateOp> mFirstPassUpdates;

    private List<AdapterHelper.UpdateOp> mSecondPassUpdates;

    AdapterHelperTest.TestAdapter mTestAdapter;

    private AdapterHelperTest.TestAdapter mPreProcessClone;

    // we clone adapter pre-process to run operations to see result
    private List<AdapterHelperTest.TestAdapter.Item> mPreLayoutItems;

    private StringBuilder mLog = new StringBuilder();

    @Rule
    public TestWatcher reportErrorLog = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            System.out.println(mLog.toString());
        }

        @Override
        protected void succeeded(Description description) {
        }
    };

    @Test
    public void testChangeAll() throws Exception {
        try {
            setupBasic(5, 0, 3);
            up(0, 5);
            mAdapterHelper.preProcess();
        } catch (Throwable t) {
            throw new Exception(mLog.toString());
        }
    }

    @Test
    public void testFindPositionOffsetInPreLayout() {
        setupBasic(50, 25, 10);
        rm(24, 5);
        mAdapterHelper.preProcess();
        // since 25 is invisible, we offset by one while checking
        Assert.assertEquals("find position for view 23", 23, mAdapterHelper.findPositionOffset(23));
        Assert.assertEquals("find position for view 24", (-1), mAdapterHelper.findPositionOffset(24));
        Assert.assertEquals("find position for view 25", (-1), mAdapterHelper.findPositionOffset(25));
        Assert.assertEquals("find position for view 26", (-1), mAdapterHelper.findPositionOffset(26));
        Assert.assertEquals("find position for view 27", (-1), mAdapterHelper.findPositionOffset(27));
        Assert.assertEquals("find position for view 28", 24, mAdapterHelper.findPositionOffset(28));
        Assert.assertEquals("find position for view 29", 25, mAdapterHelper.findPositionOffset(29));
    }

    @Test
    public void testNotifyAfterPre() {
        setupBasic(10, 2, 3);
        add(2, 1);
        mAdapterHelper.preProcess();
        add(3, 1);
        mAdapterHelper.consumeUpdatesInOnePass();
        mPreProcessClone.applyOps(mFirstPassUpdates, mTestAdapter);
        mPreProcessClone.applyOps(mSecondPassUpdates, mTestAdapter);
        assertAdaptersEqual(mTestAdapter, mPreProcessClone);
    }

    @Test
    public void testSinglePass() {
        setupBasic(10, 2, 3);
        add(2, 1);
        rm(1, 2);
        add(1, 5);
        mAdapterHelper.consumeUpdatesInOnePass();
        assertDispatch(0, 3);
    }

    @Test
    public void testDeleteVisible() {
        setupBasic(10, 2, 3);
        rm(2, 1);
        preProcess();
        assertDispatch(0, 1);
    }

    @Test
    public void testDeleteInvisible() {
        setupBasic(10, 3, 4);
        rm(2, 1);
        preProcess();
        assertDispatch(1, 0);
    }

    @Test
    public void testAddCount() {
        setupBasic(0, 0, 0);
        add(0, 1);
        Assert.assertEquals(1, mAdapterHelper.mPendingUpdates.size());
    }

    @Test
    public void testDeleteCount() {
        setupBasic(1, 0, 0);
        rm(0, 1);
        Assert.assertEquals(1, mAdapterHelper.mPendingUpdates.size());
    }

    @Test
    public void testAddProcess() {
        setupBasic(0, 0, 0);
        add(0, 1);
        preProcess();
        Assert.assertEquals(0, mAdapterHelper.mPendingUpdates.size());
    }

    @Test
    public void testAddRemoveSeparate() {
        setupBasic(10, 2, 2);
        add(6, 1);
        rm(5, 1);
        preProcess();
        assertDispatch(1, 1);
    }

    @Test
    public void testScenario1() {
        setupBasic(10, 3, 2);
        rm(4, 1);
        rm(3, 1);
        rm(3, 1);
        preProcess();
        assertDispatch(1, 2);
    }

    @Test
    public void testDivideDelete() {
        setupBasic(10, 3, 4);
        rm(2, 2);
        preProcess();
        assertDispatch(1, 1);
    }

    @Test
    public void testScenario2() {
        setupBasic(10, 3, 3);// 3-4-5

        add(4, 2);// 3 a b 4 5

        rm(0, 1);// (0) 3(2) a(3) b(4) 4(3) 5(4)

        rm(1, 3);// (1,2) (x) a(1) b(2) 4(3)

        preProcess();
        assertDispatch(2, 2);
    }

    @Test
    public void testScenario3() {
        setupBasic(10, 2, 2);
        rm(0, 5);
        preProcess();
        assertDispatch(2, 1);
        assertOps(mFirstPassUpdates, rmOp(0, 2), rmOp(2, 1));
        assertOps(mSecondPassUpdates, rmOp(0, 2));
    }

    // TODO test MOVE then remove items in between.
    // TODO test MOVE then remove it, make sure it is not dispatched
    @Test
    public void testScenario4() {
        setupBasic(5, 0, 5);
        // 0 1 2 3 4
        // 0 1 2 a b 3 4
        // 0 2 a b 3 4
        // 0 c d 2 a b 3 4
        // 0 c d 2 a 4
        // c d 2 a 4
        // pre: 0 1 2 3 4
        add(3, 2);
        rm(1, 1);
        add(1, 2);
        rm(5, 2);
        rm(0, 1);
        preProcess();
    }

    @Test
    public void testScenario5() {
        setupBasic(5, 0, 5);
        // 0 1 2 3 4
        // 0 1 2 a b 3 4
        // 0 1 b 3 4
        // pre: 0 1 2 3 4
        // pre w/ adap: 0 1 2 b 3 4
        add(3, 2);
        rm(2, 2);
        preProcess();
    }

    @Test
    public void testScenario6() {
        // setupBasic(47, 19, 24);
        // mv(11, 12);
        // add(24, 16);
        // rm(9, 3);
        setupBasic(10, 5, 3);
        mv(2, 3);
        add(6, 4);
        rm(4, 1);
        preProcess();
    }

    @Test
    public void testScenario8() {
        setupBasic(68, 51, 13);
        mv(22, 11);
        mv(22, 52);
        rm(37, 19);
        add(12, 38);
        preProcess();
    }

    @Test
    public void testScenario9() {
        setupBasic(44, 3, 7);
        add(7, 21);
        rm(31, 3);
        rm(32, 11);
        mv(29, 5);
        mv(30, 32);
        add(25, 32);
        rm(15, 66);
        preProcess();
    }

    @Test
    public void testScenario10() {
        setupBasic(14, 10, 3);
        rm(4, 4);
        add(5, 11);
        mv(5, 18);
        rm(2, 9);
        preProcess();
    }

    @Test
    public void testScenario11() {
        setupBasic(78, 3, 64);
        mv(34, 28);
        add(1, 11);
        rm(9, 74);
        preProcess();
    }

    @Test
    public void testScenario12() {
        setupBasic(38, 9, 7);
        rm(26, 3);
        mv(29, 15);
        rm(30, 1);
        preProcess();
    }

    @Test
    public void testScenario13() {
        setupBasic(49, 41, 3);
        rm(30, 13);
        add(4, 10);
        mv(3, 38);
        mv(20, 17);
        rm(18, 23);
        preProcess();
    }

    @Test
    public void testScenario14() {
        setupBasic(24, 3, 11);
        rm(2, 15);
        mv(2, 1);
        add(2, 34);
        add(11, 3);
        rm(10, 25);
        rm(13, 6);
        rm(4, 4);
        rm(6, 4);
        preProcess();
    }

    @Test
    public void testScenario15() {
        setupBasic(10, 8, 1);
        mv(6, 1);
        mv(1, 4);
        rm(3, 1);
        preProcess();
    }

    @Test
    public void testScenario16() {
        setupBasic(10, 3, 3);
        rm(2, 1);
        rm(1, 7);
        rm(0, 1);
        preProcess();
    }

    @Test
    public void testScenario17() {
        setupBasic(10, 8, 1);
        mv(1, 0);
        mv(5, 1);
        rm(1, 7);
        preProcess();
    }

    @Test
    public void testScenario18() {
        setupBasic(10, 1, 4);
        add(2, 11);
        rm(16, 1);
        add(3, 1);
        rm(9, 10);
        preProcess();
    }

    @Test
    public void testScenario19() {
        setupBasic(10, 8, 1);
        mv(9, 7);
        mv(9, 3);
        rm(5, 4);
        preProcess();
    }

    @Test
    public void testScenario20() {
        setupBasic(10, 7, 1);
        mv(9, 1);
        mv(3, 9);
        rm(7, 2);
        preProcess();
    }

    @Test
    public void testScenario21() {
        setupBasic(10, 5, 2);
        mv(1, 0);
        mv(9, 1);
        rm(2, 3);
        preProcess();
    }

    @Test
    public void testScenario22() {
        setupBasic(10, 7, 2);
        add(2, 16);
        mv(20, 9);
        rm(17, 6);
        preProcess();
    }

    @Test
    public void testScenario23() {
        setupBasic(10, 5, 3);
        mv(9, 6);
        add(4, 15);
        rm(21, 3);
        preProcess();
    }

    @Test
    public void testScenario24() {
        setupBasic(10, 1, 6);
        add(6, 5);
        mv(14, 6);
        rm(7, 6);
        preProcess();
    }

    @Test
    public void testScenario25() {
        setupBasic(10, 3, 4);
        mv(3, 9);
        rm(5, 4);
        preProcess();
    }

    @Test
    public void testScenario25a() {
        setupBasic(10, 3, 4);
        rm(6, 4);
        mv(3, 5);
        preProcess();
    }

    @Test
    public void testScenario26() {
        setupBasic(10, 4, 4);
        rm(3, 5);
        mv(2, 0);
        mv(1, 0);
        rm(1, 1);
        mv(0, 2);
        preProcess();
    }

    @Test
    public void testScenario27() {
        setupBasic(10, 0, 3);
        mv(9, 4);
        mv(8, 4);
        add(7, 6);
        rm(5, 5);
        preProcess();
    }

    @Test
    public void testScenerio28() {
        setupBasic(10, 4, 1);
        mv(8, 6);
        rm(8, 1);
        mv(7, 5);
        rm(3, 3);
        rm(1, 4);
        preProcess();
    }

    @Test
    public void testScenerio29() {
        setupBasic(10, 6, 3);
        mv(3, 6);
        up(6, 2);
        add(5, 5);
    }

    @Test
    public void testScenerio30() {
        mCollectLogs = true;
        setupBasic(10, 3, 1);
        rm(3, 2);
        rm(2, 5);
        preProcess();
    }

    @Test
    public void testScenerio31() {
        mCollectLogs = true;
        setupBasic(10, 3, 1);
        rm(3, 1);
        rm(2, 3);
        preProcess();
    }

    @Test
    public void testScenerio32() {
        setupBasic(10, 8, 1);
        add(9, 2);
        add(7, 39);
        up(0, 39);
        mv(36, 20);
        add(1, 48);
        mv(22, 98);
        mv(96, 29);
        up(36, 29);
        add(60, 36);
        add(127, 34);
        rm(142, 22);
        up(12, 69);
        up(116, 13);
        up(118, 19);
        mv(94, 69);
        up(98, 21);
        add(89, 18);
        rm(94, 70);
        up(71, 8);
        rm(54, 26);
        add(2, 20);
        mv(78, 84);
        mv(56, 2);
        mv(1, 79);
        rm(76, 7);
        rm(57, 12);
        rm(30, 27);
        add(24, 13);
        add(21, 5);
        rm(11, 27);
        rm(32, 1);
        up(0, 5);
        mv(14, 9);
        rm(15, 12);
        up(19, 1);
        rm(7, 1);
        mv(10, 4);
        up(4, 3);
        rm(16, 1);
        up(13, 5);
        up(2, 8);
        add(10, 19);
        add(15, 42);
        preProcess();
    }

    @Test
    public void testScenerio33() throws Throwable {
        try {
            mCollectLogs = true;
            setupBasic(10, 7, 1);
            mv(0, 6);
            up(0, 7);
            preProcess();
        } catch (Throwable t) {
            throw new Throwable((((t.getMessage()) + "\n") + (mLog.toString())));
        }
    }

    @Test
    public void testScenerio34() {
        setupBasic(10, 6, 1);
        mv(9, 7);
        rm(5, 2);
        up(4, 3);
        preProcess();
    }

    @Test
    public void testScenerio35() {
        setupBasic(10, 4, 4);
        mv(1, 4);
        up(2, 7);
        up(0, 1);
        preProcess();
    }

    @Test
    public void testScenerio36() {
        setupBasic(10, 7, 2);
        rm(4, 1);
        mv(1, 6);
        up(4, 4);
        preProcess();
    }

    @Test
    public void testScenerio37() throws Throwable {
        try {
            mCollectLogs = true;
            setupBasic(10, 5, 2);
            mv(3, 6);
            rm(4, 4);
            rm(3, 2);
            preProcess();
        } catch (Throwable t) {
            throw new Throwable((((t.getMessage()) + "\n") + (mLog.toString())));
        }
    }

    @Test
    public void testScenerio38() {
        setupBasic(10, 2, 2);
        add(0, 24);
        rm(26, 4);
        rm(1, 24);
        preProcess();
    }

    @Test
    public void testScenerio39() {
        setupBasic(10, 7, 1);
        mv(0, 2);
        rm(8, 1);
        rm(2, 6);
        preProcess();
    }

    @Test
    public void testScenerio40() {
        setupBasic(10, 5, 3);
        rm(5, 4);
        mv(0, 5);
        rm(2, 3);
        preProcess();
    }

    @Test
    public void testScenerio41() {
        setupBasic(10, 7, 2);
        mv(4, 9);
        rm(0, 6);
        rm(0, 1);
        preProcess();
    }

    @Test
    public void testScenerio42() {
        setupBasic(10, 6, 2);
        mv(5, 9);
        rm(5, 1);
        rm(2, 6);
        preProcess();
    }

    @Test
    public void testScenerio43() {
        setupBasic(10, 1, 6);
        mv(6, 8);
        rm(3, 5);
        up(3, 1);
        preProcess();
    }

    @Test
    public void testScenerio44() {
        setupBasic(10, 5, 2);
        mv(6, 4);
        mv(4, 1);
        rm(5, 3);
        preProcess();
    }

    @Test
    public void testScenerio45() {
        setupBasic(10, 4, 2);
        rm(1, 4);
        preProcess();
    }

    @Test
    public void testScenerio46() {
        setupBasic(10, 4, 3);
        up(6, 1);
        mv(8, 0);
        rm(2, 7);
        preProcess();
    }

    @Test
    public void testMoveAdded() {
        setupBasic(10, 2, 2);
        add(3, 5);
        mv(4, 2);
        preProcess();
    }

    @Test
    public void testPayloads() {
        setupBasic(10, 2, 2);
        up(3, 3, "payload");
        preProcess();
        assertOps(mFirstPassUpdates, upOp(4, 2, "payload"));
        assertOps(mSecondPassUpdates, upOp(3, 1, "payload"));
    }

    @Test
    public void testRandom() throws Throwable {
        mCollectLogs = true;
        Random random = new Random(System.nanoTime());
        for (int i = 0; i < 100; i++) {
            try {
                log(("running random test " + i));
                randomTest(random, Math.max(40, (10 + (nextInt(random, i)))));
            } catch (Throwable t) {
                throw new Throwable(((((("failure at random test " + i) + "\n") + (t.getMessage())) + "\n") + (mLog.toString())), t);
            }
        }
    }

    static class TestAdapter {
        List<AdapterHelperTest.TestAdapter.Item> mItems;

        final AdapterHelper mAdapterHelper;

        Queue<AdapterHelperTest.TestAdapter.Item> mPendingAdded;

        public TestAdapter(int initialCount, AdapterHelper container) {
            mItems = new ArrayList<>();
            mAdapterHelper = container;
            mPendingAdded = new LinkedList<>();
            for (int i = 0; i < initialCount; i++) {
                mItems.add(new AdapterHelperTest.TestAdapter.Item());
            }
        }

        public void add(int index, int count) {
            for (int i = 0; i < count; i++) {
                AdapterHelperTest.TestAdapter.Item item = new AdapterHelperTest.TestAdapter.Item();
                mPendingAdded.add(item);
                mItems.add((index + i), item);
            }
            mAdapterHelper.addUpdateOp(new AdapterHelper.UpdateOp(UpdateOp.ADD, index, count, null));
        }

        public void move(int from, int to) {
            mItems.add(to, mItems.remove(from));
            mAdapterHelper.addUpdateOp(new AdapterHelper.UpdateOp(UpdateOp.MOVE, from, to, null));
        }

        public void remove(int index, int count) {
            for (int i = 0; i < count; i++) {
                mItems.remove(index);
            }
            mAdapterHelper.addUpdateOp(new AdapterHelper.UpdateOp(UpdateOp.REMOVE, index, count, null));
        }

        public void update(int index, int count) {
            update(index, count, null);
        }

        public void update(int index, int count, Object payload) {
            for (int i = 0; i < count; i++) {
                mItems.get((index + i)).update(payload);
            }
            mAdapterHelper.addUpdateOp(new AdapterHelper.UpdateOp(UpdateOp.UPDATE, index, count, payload));
        }

        AdapterHelperTest.TestAdapter createCopy() {
            AdapterHelperTest.TestAdapter adapter = new AdapterHelperTest.TestAdapter(0, mAdapterHelper);
            adapter.mItems.addAll(mItems);
            return adapter;
        }

        void applyOps(List<AdapterHelper.UpdateOp> updates, AdapterHelperTest.TestAdapter dataSource) {
            for (AdapterHelper.UpdateOp op : updates) {
                switch (op.cmd) {
                    case UpdateOp.ADD :
                        for (int i = 0; i < (op.itemCount); i++) {
                            mItems.add(((op.positionStart) + i), dataSource.consumeNextAdded());
                        }
                        break;
                    case UpdateOp.REMOVE :
                        for (int i = 0; i < (op.itemCount); i++) {
                            mItems.remove(op.positionStart);
                        }
                        break;
                    case UpdateOp.UPDATE :
                        for (int i = 0; i < (op.itemCount); i++) {
                            mItems.get(((op.positionStart) + i)).handleUpdate(op.payload);
                        }
                        break;
                    case UpdateOp.MOVE :
                        mItems.add(op.itemCount, mItems.remove(op.positionStart));
                        break;
                }
            }
        }

        private AdapterHelperTest.TestAdapter.Item consumeNextAdded() {
            return mPendingAdded.remove();
        }

        public static class Item {
            private static AtomicInteger itemCounter = new AtomicInteger();

            @SuppressWarnings("unused")
            private final int id;

            private int mVersionCount = 0;

            private ArrayList<Object> mPayloads = new ArrayList<>();

            public Item() {
                id = AdapterHelperTest.TestAdapter.Item.itemCounter.incrementAndGet();
            }

            public void update(Object payload) {
                mPayloads.add(payload);
                (mVersionCount)++;
            }

            void handleUpdate(Object payload) {
                Assert.assertSame(payload, mPayloads.get(0));
                mPayloads.remove(0);
                (mVersionCount)--;
            }

            int getUpdateCount() {
                return mVersionCount;
            }
        }
    }

    static class MockViewHolder extends RecyclerView.ViewHolder {
        AdapterHelperTest.TestAdapter.Item mItem;

        MockViewHolder() {
            super(Mockito.mock(View.class));
        }

        @Override
        public String toString() {
            return (mItem) == null ? "null" : mItem.toString();
        }
    }
}

