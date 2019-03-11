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


import android.util.Log;
import androidx.recyclerview.widget.AdapterHelper.UpdateOp;
import androidx.test.filters.SmallTest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
@SmallTest
public class OpReorderTest {
    private static final String TAG = "OpReorderTest";

    List<UpdateOp> mUpdateOps = new ArrayList<UpdateOp>();

    List<OpReorderTest.Item> mAddedItems = new ArrayList<OpReorderTest.Item>();

    List<OpReorderTest.Item> mRemovedItems = new ArrayList<OpReorderTest.Item>();

    Set<UpdateOp> mRecycledOps = new HashSet<UpdateOp>();

    static Random random = new Random(System.nanoTime());

    OpReorderer mOpReorderer = new OpReorderer(new OpReorderer.Callback() {
        @Override
        public UpdateOp obtainUpdateOp(int cmd, int startPosition, int itemCount, Object payload) {
            return new UpdateOp(cmd, startPosition, itemCount, payload);
        }

        @Override
        public void recycleUpdateOp(UpdateOp op) {
            mRecycledOps.add(op);
        }
    });

    int itemCount = 10;

    int updatedItemCount = 0;

    @Test
    public void testMoveRemoved() throws Exception {
        setup(10);
        mv(3, 8);
        rm(7, 3);
        process();
    }

    @Test
    public void testMoveRemove() throws Exception {
        setup(10);
        mv(3, 8);
        rm(3, 5);
        process();
    }

    @Test
    public void test1() {
        setup(10);
        mv(3, 5);
        rm(3, 4);
        process();
    }

    @Test
    public void test2() {
        setup(5);
        mv(1, 3);
        rm(1, 1);
        process();
    }

    @Test
    public void test3() {
        setup(5);
        mv(0, 4);
        rm(2, 1);
        process();
    }

    @Test
    public void test4() {
        setup(5);
        mv(3, 0);
        rm(3, 1);
        process();
    }

    @Test
    public void test5() {
        setup(10);
        mv(8, 1);
        rm(6, 3);
        process();
    }

    @Test
    public void test6() {
        setup(5);
        mv(1, 3);
        rm(0, 3);
        process();
    }

    @Test
    public void test7() {
        setup(5);
        mv(3, 4);
        rm(3, 1);
        process();
    }

    @Test
    public void test8() {
        setup(5);
        mv(4, 3);
        rm(3, 1);
        process();
    }

    @Test
    public void test9() {
        setup(5);
        mv(2, 0);
        rm(2, 2);
        process();
    }

    @Test
    public void testRandom() throws Exception {
        for (int i = 0; i < 150; i++) {
            try {
                cleanState();
                setup(50);
                for (int j = 0; j < 50; j++) {
                    randOp(nextInt(OpReorderTest.random, nextInt(OpReorderTest.random, 4)));
                }
                Log.d(OpReorderTest.TAG, ("running random test " + i));
                process();
            } catch (Throwable t) {
                throw new Exception((((t.getMessage()) + "\n") + (opsToString(mUpdateOps))));
            }
        }
    }

    @Test
    public void testRandomMoveRemove() throws Exception {
        for (int i = 0; i < 1000; i++) {
            try {
                cleanState();
                setup(5);
                orderedRandom(UpdateOp.MOVE, UpdateOp.REMOVE);
                process();
            } catch (Throwable t) {
                throw new Exception((((t.getMessage()) + "\n") + (opsToString(mUpdateOps))));
            }
        }
    }

    @Test
    public void testRandomMoveAdd() throws Exception {
        for (int i = 0; i < 1000; i++) {
            try {
                cleanState();
                setup(5);
                orderedRandom(UpdateOp.MOVE, UpdateOp.ADD);
                process();
            } catch (Throwable t) {
                throw new Exception((((t.getMessage()) + "\n") + (opsToString(mUpdateOps))));
            }
        }
    }

    @Test
    public void testRandomMoveUpdate() throws Exception {
        for (int i = 0; i < 1000; i++) {
            try {
                cleanState();
                setup(5);
                orderedRandom(UpdateOp.MOVE, UpdateOp.UPDATE);
                process();
            } catch (Throwable t) {
                throw new Exception((((t.getMessage()) + "\n") + (opsToString(mUpdateOps))));
            }
        }
    }

    @Test
    public void testSwapMoveRemove_1() {
        mv(10, 15);
        rm(2, 3);
        swapMoveRemove(mUpdateOps, 0);
        Assert.assertEquals(2, mUpdateOps.size());
        Assert.assertEquals(mv(7, 12), mUpdateOps.get(1));
        Assert.assertEquals(rm(2, 3), mUpdateOps.get(0));
    }

    @Test
    public void testSwapMoveRemove_2() {
        mv(3, 8);
        rm(4, 2);
        swapMoveRemove(mUpdateOps, 0);
        Assert.assertEquals(2, mUpdateOps.size());
        Assert.assertEquals(rm(5, 2), mUpdateOps.get(0));
        Assert.assertEquals(mv(3, 6), mUpdateOps.get(1));
    }

    @Test
    public void testSwapMoveRemove_3() {
        mv(3, 8);
        rm(3, 2);
        swapMoveRemove(mUpdateOps, 0);
        Assert.assertEquals(2, mUpdateOps.size());
        Assert.assertEquals(rm(4, 2), mUpdateOps.get(0));
        Assert.assertEquals(mv(3, 6), mUpdateOps.get(1));
    }

    @Test
    public void testSwapMoveRemove_4() {
        mv(3, 8);
        rm(2, 3);
        swapMoveRemove(mUpdateOps, 0);
        Assert.assertEquals(3, mUpdateOps.size());
        Assert.assertEquals(rm(4, 2), mUpdateOps.get(0));
        Assert.assertEquals(rm(2, 1), mUpdateOps.get(1));
        Assert.assertEquals(mv(2, 5), mUpdateOps.get(2));
    }

    @Test
    public void testSwapMoveRemove_5() {
        mv(3, 0);
        rm(2, 3);
        swapMoveRemove(mUpdateOps, 0);
        Assert.assertEquals(3, mUpdateOps.size());
        Assert.assertEquals(rm(4, 1), mUpdateOps.get(0));
        Assert.assertEquals(rm(1, 2), mUpdateOps.get(1));
        Assert.assertEquals(mv(1, 0), mUpdateOps.get(2));
    }

    @Test
    public void testSwapMoveRemove_6() {
        mv(3, 10);
        rm(2, 3);
        swapMoveRemove(mUpdateOps, 0);
        Assert.assertEquals(3, mUpdateOps.size());
        Assert.assertEquals(rm(4, 2), mUpdateOps.get(0));
        Assert.assertEquals(rm(2, 1), mUpdateOps.get(1));
    }

    @Test
    public void testSwapMoveRemove_7() {
        mv(3, 2);
        rm(6, 2);
        swapMoveRemove(mUpdateOps, 0);
        Assert.assertEquals(2, mUpdateOps.size());
        Assert.assertEquals(rm(6, 2), mUpdateOps.get(0));
        Assert.assertEquals(mv(3, 2), mUpdateOps.get(1));
    }

    @Test
    public void testSwapMoveRemove_8() {
        mv(3, 4);
        rm(3, 1);
        swapMoveRemove(mUpdateOps, 0);
        Assert.assertEquals(1, mUpdateOps.size());
        Assert.assertEquals(rm(4, 1), mUpdateOps.get(0));
    }

    @Test
    public void testSwapMoveRemove_9() {
        mv(3, 4);
        rm(4, 1);
        swapMoveRemove(mUpdateOps, 0);
        Assert.assertEquals(1, mUpdateOps.size());
        Assert.assertEquals(rm(3, 1), mUpdateOps.get(0));
    }

    @Test
    public void testSwapMoveRemove_10() {
        mv(1, 3);
        rm(0, 3);
        swapMoveRemove(mUpdateOps, 0);
        Assert.assertEquals(2, mUpdateOps.size());
        Assert.assertEquals(rm(2, 2), mUpdateOps.get(0));
        Assert.assertEquals(rm(0, 1), mUpdateOps.get(1));
    }

    @Test
    public void testSwapMoveRemove_11() {
        mv(3, 8);
        rm(7, 3);
        swapMoveRemove(mUpdateOps, 0);
        Assert.assertEquals(2, mUpdateOps.size());
        Assert.assertEquals(rm(3, 1), mUpdateOps.get(0));
        Assert.assertEquals(rm(7, 2), mUpdateOps.get(1));
    }

    @Test
    public void testSwapMoveRemove_12() {
        mv(1, 3);
        rm(2, 1);
        swapMoveRemove(mUpdateOps, 0);
        Assert.assertEquals(2, mUpdateOps.size());
        Assert.assertEquals(rm(3, 1), mUpdateOps.get(0));
        Assert.assertEquals(mv(1, 2), mUpdateOps.get(1));
    }

    @Test
    public void testSwapMoveRemove_13() {
        mv(1, 3);
        rm(1, 2);
        swapMoveRemove(mUpdateOps, 0);
        Assert.assertEquals(1, mUpdateOps.size());
        Assert.assertEquals(rm(2, 2), mUpdateOps.get(1));
    }

    @Test
    public void testSwapMoveRemove_14() {
        mv(4, 2);
        rm(3, 1);
        swapMoveRemove(mUpdateOps, 0);
        Assert.assertEquals(2, mUpdateOps.size());
        Assert.assertEquals(rm(2, 1), mUpdateOps.get(0));
        Assert.assertEquals(mv(2, 3), mUpdateOps.get(1));
    }

    @Test
    public void testSwapMoveRemove_15() {
        mv(4, 2);
        rm(3, 2);
        swapMoveRemove(mUpdateOps, 0);
        Assert.assertEquals(1, mUpdateOps.size());
        Assert.assertEquals(rm(2, 2), mUpdateOps.get(0));
    }

    @Test
    public void testSwapMoveRemove_16() {
        mv(2, 3);
        rm(1, 2);
        swapMoveRemove(mUpdateOps, 0);
        Assert.assertEquals(2, mUpdateOps.size());
        Assert.assertEquals(rm(3, 1), mUpdateOps.get(0));
        Assert.assertEquals(rm(1, 1), mUpdateOps.get(1));
    }

    @Test
    public void testSwapMoveUpdate_0() {
        mv(1, 3);
        up(1, 2);
        swapMoveUpdate(mUpdateOps, 0);
        Assert.assertEquals(2, mUpdateOps.size());
        Assert.assertEquals(up(2, 2), mUpdateOps.get(0));
        Assert.assertEquals(mv(1, 3), mUpdateOps.get(1));
    }

    @Test
    public void testSwapMoveUpdate_1() {
        mv(0, 2);
        up(0, 4);
        swapMoveUpdate(mUpdateOps, 0);
        Assert.assertEquals(3, mUpdateOps.size());
        Assert.assertEquals(up(0, 1), mUpdateOps.get(0));
        Assert.assertEquals(up(1, 3), mUpdateOps.get(1));
        Assert.assertEquals(mv(0, 2), mUpdateOps.get(2));
    }

    @Test
    public void testSwapMoveUpdate_2() {
        mv(2, 0);
        up(1, 3);
        swapMoveUpdate(mUpdateOps, 0);
        Assert.assertEquals(3, mUpdateOps.size());
        Assert.assertEquals(up(3, 1), mUpdateOps.get(0));
        Assert.assertEquals(up(0, 2), mUpdateOps.get(1));
        Assert.assertEquals(mv(2, 0), mUpdateOps.get(2));
    }

    static class Item {
        static int idCounter = 0;

        int id;

        int version;

        Item(int id, int version) {
            this.id = id;
            this.version = version;
        }

        static OpReorderTest.Item create() {
            return new OpReorderTest.Item(((OpReorderTest.Item.idCounter)++), 1);
        }

        static OpReorderTest.Item clone(OpReorderTest.Item other) {
            return new OpReorderTest.Item(other.id, other.version);
        }

        public static void assertIdentical(String logPrefix, OpReorderTest.Item item1, OpReorderTest.Item item2) {
            Assert.assertEquals(((((logPrefix + "\n") + item1) + " vs ") + item2), item1.id, item2.id);
            Assert.assertEquals(((((logPrefix + "\n") + item1) + " vs ") + item2), item1.version, item2.version);
        }

        @Override
        public String toString() {
            return (((("Item{" + "id=") + (id)) + ", version=") + (version)) + '}';
        }
    }
}

