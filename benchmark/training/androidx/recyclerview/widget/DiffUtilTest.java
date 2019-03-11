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


import DiffUtil.Callback;
import androidx.annotation.Nullable;
import androidx.test.filters.SmallTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
@SmallTest
public class DiffUtilTest {
    private static Random sRand = new Random(System.nanoTime());

    private List<DiffUtilTest.Item> mBefore = new ArrayList<>();

    private List<DiffUtilTest.Item> mAfter = new ArrayList<>();

    private StringBuilder mLog = new StringBuilder();

    private Callback mCallback = new DiffUtil.Callback() {
        @Override
        public int getOldListSize() {
            return mBefore.size();
        }

        @Override
        public int getNewListSize() {
            return mAfter.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemIndex, int newItemIndex) {
            return (mBefore.get(oldItemIndex).id) == (mAfter.get(newItemIndex).id);
        }

        @Override
        public boolean areContentsTheSame(int oldItemIndex, int newItemIndex) {
            MatcherAssert.assertThat(mBefore.get(oldItemIndex).id, CoreMatchers.equalTo(mAfter.get(newItemIndex).id));
            return mBefore.get(oldItemIndex).data.equals(mAfter.get(newItemIndex).data);
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemIndex, int newItemIndex) {
            MatcherAssert.assertThat(mBefore.get(oldItemIndex).id, CoreMatchers.equalTo(mAfter.get(newItemIndex).id));
            MatcherAssert.assertThat(mBefore.get(oldItemIndex).data, CoreMatchers.not(CoreMatchers.equalTo(mAfter.get(newItemIndex).data)));
            return mAfter.get(newItemIndex).payload;
        }
    };

    @Rule
    public TestWatcher mLogOnExceptionWatcher = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            System.err.println(mLog.toString());
        }
    };

    @Test
    public void testNoChange() {
        initWithSize(5);
        check();
    }

    @Test
    public void testAddItems() {
        initWithSize(2);
        add(1);
        check();
    }

    @Test
    public void testGen2() {
        initWithSize(5);
        add(5);
        delete(3);
        delete(1);
        check();
    }

    @Test
    public void testGen3() {
        initWithSize(5);
        add(0);
        delete(1);
        delete(3);
        check();
    }

    @Test
    public void testGen4() {
        initWithSize(5);
        add(5);
        add(1);
        add(4);
        add(4);
        check();
    }

    @Test
    public void testGen5() {
        initWithSize(5);
        delete(0);
        delete(2);
        add(0);
        add(2);
        check();
    }

    @Test
    public void testGen6() {
        initWithSize(2);
        delete(0);
        delete(0);
        check();
    }

    @Test
    public void testGen7() {
        initWithSize(3);
        move(2, 0);
        delete(2);
        add(2);
        check();
    }

    @Test
    public void testGen8() {
        initWithSize(3);
        delete(1);
        add(0);
        move(2, 0);
        check();
    }

    @Test
    public void testGen9() {
        initWithSize(2);
        add(2);
        move(0, 2);
        check();
    }

    @Test
    public void testGen10() {
        initWithSize(3);
        move(0, 1);
        move(1, 2);
        add(0);
        check();
    }

    @Test
    public void testGen11() {
        initWithSize(4);
        move(2, 0);
        move(2, 3);
        check();
    }

    @Test
    public void testGen12() {
        initWithSize(4);
        move(3, 0);
        move(2, 1);
        check();
    }

    @Test
    public void testGen13() {
        initWithSize(4);
        move(3, 2);
        move(0, 3);
        check();
    }

    @Test
    public void testGen14() {
        initWithSize(4);
        move(3, 2);
        add(4);
        move(0, 4);
        check();
    }

    @Test
    public void testAdd1() {
        initWithSize(1);
        add(1);
        check();
    }

    @Test
    public void testMove1() {
        initWithSize(3);
        move(0, 2);
        check();
    }

    @Test
    public void tmp() {
        initWithSize(4);
        move(0, 2);
        check();
    }

    @Test
    public void testUpdate1() {
        initWithSize(3);
        update(2);
        check();
    }

    @Test
    public void testUpdate2() {
        initWithSize(2);
        add(1);
        update(1);
        update(2);
        check();
    }

    @Test
    public void testDisableMoveDetection() {
        initWithSize(5);
        move(0, 4);
        List<DiffUtilTest.Item> applied = applyUpdates(mBefore, DiffUtil.calculateDiff(mCallback, false));
        MatcherAssert.assertThat(applied.size(), CoreMatchers.is(5));
        MatcherAssert.assertThat(applied.get(4).newItem, CoreMatchers.is(true));
        MatcherAssert.assertThat(applied.contains(mBefore.get(0)), CoreMatchers.is(false));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void convertOldPositionToNew_tooSmall() {
        initWithSize(2);
        update(2);
        DiffUtil.calculateDiff(mCallback).convertOldPositionToNew((-1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void convertOldPositionToNew_tooLarge() {
        initWithSize(2);
        update(2);
        DiffUtil.calculateDiff(mCallback).convertOldPositionToNew(2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void convertNewPositionToOld_tooSmall() {
        initWithSize(2);
        update(2);
        DiffUtil.calculateDiff(mCallback).convertNewPositionToOld((-1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void convertNewPositionToOld_tooLarge() {
        initWithSize(2);
        update(2);
        DiffUtil.calculateDiff(mCallback).convertNewPositionToOld(2);
    }

    static class Item {
        static long idCounter = 0;

        final long id;

        final boolean newItem;

        boolean changed = false;

        String payload;

        String data = UUID.randomUUID().toString();

        public Item(boolean newItem) {
            id = (DiffUtilTest.Item.idCounter)++;
            this.newItem = newItem;
        }

        public Item(DiffUtilTest.Item other) {
            id = other.id;
            newItem = other.newItem;
            changed = other.changed;
            payload = other.payload;
            data = other.data;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            DiffUtilTest.Item item = ((DiffUtilTest.Item) (o));
            if ((id) != (item.id))
                return false;

            if ((newItem) != (item.newItem))
                return false;

            if ((changed) != (item.changed))
                return false;

            if ((payload) != null ? !(payload.equals(item.payload)) : (item.payload) != null) {
                return false;
            }
            return data.equals(item.data);
        }

        @Override
        public int hashCode() {
            int result = ((int) ((id) ^ ((id) >>> 32)));
            result = (31 * result) + (newItem ? 1 : 0);
            result = (31 * result) + (changed ? 1 : 0);
            result = (31 * result) + ((payload) != null ? payload.hashCode() : 0);
            result = (31 * result) + (data.hashCode());
            return result;
        }

        @Override
        public String toString() {
            return (((((((((((("Item{" + "id=") + (id)) + ", newItem=") + (newItem)) + ", changed=") + (changed)) + ", payload='") + (payload)) + '\'') + ", data='") + (data)) + '\'') + '}';
        }
    }
}

