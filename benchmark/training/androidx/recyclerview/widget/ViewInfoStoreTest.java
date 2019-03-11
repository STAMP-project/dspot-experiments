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


import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView.ItemAnimator.ItemHolderInfo;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.test.filters.SmallTest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
@SmallTest
public class ViewInfoStoreTest {
    ViewInfoStore mStore;

    ViewInfoStoreTest.LoggingProcessCallback mCallback;

    @Test
    public void addOverridePre() {
        RecyclerView.ViewHolder vh = new ViewInfoStoreTest.MockViewHolder();
        ViewInfoStoreTest.MockInfo info = new ViewInfoStoreTest.MockInfo();
        mStore.addToPreLayout(vh, info);
        ViewInfoStoreTest.MockInfo info2 = new ViewInfoStoreTest.MockInfo();
        mStore.addToPreLayout(vh, info2);
        Assert.assertSame(info2, find(vh, InfoRecord.FLAG_PRE));
    }

    @Test
    public void addOverridePost() {
        RecyclerView.ViewHolder vh = new ViewInfoStoreTest.MockViewHolder();
        ViewInfoStoreTest.MockInfo info = new ViewInfoStoreTest.MockInfo();
        mStore.addToPostLayout(vh, info);
        ViewInfoStoreTest.MockInfo info2 = new ViewInfoStoreTest.MockInfo();
        mStore.addToPostLayout(vh, info2);
        Assert.assertSame(info2, find(vh, InfoRecord.FLAG_POST));
    }

    @Test
    public void addRemoveAndReAdd() {
        RecyclerView.ViewHolder vh = new ViewInfoStoreTest.MockViewHolder();
        ViewInfoStoreTest.MockInfo pre = new ViewInfoStoreTest.MockInfo();
        mStore.addToPreLayout(vh, pre);
        ViewInfoStoreTest.MockInfo post1 = new ViewInfoStoreTest.MockInfo();
        mStore.addToPostLayout(vh, post1);
        mStore.onViewDetached(vh);
        mStore.addToDisappearedInLayout(vh);
    }

    @Test
    public void addToPreLayout() {
        RecyclerView.ViewHolder vh = new ViewInfoStoreTest.MockViewHolder();
        ViewInfoStoreTest.MockInfo info = new ViewInfoStoreTest.MockInfo();
        mStore.addToPreLayout(vh, info);
        Assert.assertSame(info, find(vh, InfoRecord.FLAG_PRE));
        Assert.assertTrue(mStore.isInPreLayout(vh));
        mStore.removeViewHolder(vh);
        Assert.assertFalse(mStore.isInPreLayout(vh));
    }

    @Test
    public void addToPostLayout() {
        RecyclerView.ViewHolder vh = new ViewInfoStoreTest.MockViewHolder();
        ViewInfoStoreTest.MockInfo info = new ViewInfoStoreTest.MockInfo();
        mStore.addToPostLayout(vh, info);
        Assert.assertSame(info, find(vh, InfoRecord.FLAG_POST));
        mStore.removeViewHolder(vh);
        Assert.assertNull(find(vh, InfoRecord.FLAG_POST));
    }

    @Test
    public void popFromPreLayout() {
        Assert.assertEquals(0, sizeOf(InfoRecord.FLAG_PRE));
        RecyclerView.ViewHolder vh = new ViewInfoStoreTest.MockViewHolder();
        ViewInfoStoreTest.MockInfo info = new ViewInfoStoreTest.MockInfo();
        mStore.addToPreLayout(vh, info);
        Assert.assertSame(info, mStore.popFromPreLayout(vh));
        Assert.assertNull(mStore.popFromPreLayout(vh));
    }

    @Test
    public void addToOldChangeHolders() {
        RecyclerView.ViewHolder vh = new ViewInfoStoreTest.MockViewHolder();
        mStore.addToOldChangeHolders(1, vh);
        Assert.assertSame(vh, mStore.getFromOldChangeHolders(1));
        mStore.removeViewHolder(vh);
        Assert.assertNull(mStore.getFromOldChangeHolders(1));
    }

    @Test
    public void appearListTests() {
        RecyclerView.ViewHolder vh = new ViewInfoStoreTest.MockViewHolder();
        RecyclerView.ItemAnimator.ItemHolderInfo info = new ViewInfoStoreTest.MockInfo();
        mStore.addToAppearedInPreLayoutHolders(vh, info);
        Assert.assertEquals(1, sizeOf(InfoRecord.FLAG_APPEAR));
        RecyclerView.ViewHolder vh2 = new ViewInfoStoreTest.MockViewHolder();
        mStore.addToAppearedInPreLayoutHolders(vh2, info);
        Assert.assertEquals(2, sizeOf(InfoRecord.FLAG_APPEAR));
        mStore.removeViewHolder(vh2);
        Assert.assertEquals(1, sizeOf(InfoRecord.FLAG_APPEAR));
    }

    @Test
    public void disappearListTest() {
        RecyclerView.ViewHolder vh = new ViewInfoStoreTest.MockViewHolder();
        mStore.addToDisappearedInLayout(vh);
        Assert.assertEquals(1, sizeOf(InfoRecord.FLAG_DISAPPEARED));
        mStore.addToDisappearedInLayout(vh);
        Assert.assertEquals(1, sizeOf(InfoRecord.FLAG_DISAPPEARED));
        RecyclerView.ViewHolder vh2 = new ViewInfoStoreTest.MockViewHolder();
        mStore.addToDisappearedInLayout(vh2);
        Assert.assertEquals(2, sizeOf(InfoRecord.FLAG_DISAPPEARED));
        mStore.removeViewHolder(vh2);
        Assert.assertEquals(1, sizeOf(InfoRecord.FLAG_DISAPPEARED));
        mStore.removeFromDisappearedInLayout(vh);
        Assert.assertEquals(0, sizeOf(InfoRecord.FLAG_DISAPPEARED));
    }

    @Test
    public void processAppear() {
        ViewHolder vh = new ViewInfoStoreTest.MockViewHolder();
        ViewInfoStoreTest.MockInfo info = new ViewInfoStoreTest.MockInfo();
        mStore.addToPostLayout(vh, info);
        mStore.process(mCallback);
        Assert.assertEquals(new Pair(null, info), mCallback.appeared.get(vh));
        Assert.assertTrue(mCallback.disappeared.isEmpty());
        Assert.assertTrue(mCallback.unused.isEmpty());
        Assert.assertTrue(mCallback.persistent.isEmpty());
    }

    @Test
    public void processDisappearNormal() {
        ViewHolder vh = new ViewInfoStoreTest.MockViewHolder();
        ViewInfoStoreTest.MockInfo info = new ViewInfoStoreTest.MockInfo();
        mStore.addToPreLayout(vh, info);
        mStore.process(mCallback);
        Assert.assertEquals(new Pair(info, null), mCallback.disappeared.get(vh));
        Assert.assertTrue(mCallback.appeared.isEmpty());
        Assert.assertTrue(mCallback.unused.isEmpty());
        Assert.assertTrue(mCallback.persistent.isEmpty());
    }

    @Test
    public void processDisappearMissingLayout() {
        ViewHolder vh = new ViewInfoStoreTest.MockViewHolder();
        ViewInfoStoreTest.MockInfo info = new ViewInfoStoreTest.MockInfo();
        mStore.addToPreLayout(vh, info);
        mStore.addToDisappearedInLayout(vh);
        mStore.process(mCallback);
        Assert.assertEquals(new Pair(info, null), mCallback.disappeared.get(vh));
        Assert.assertTrue(mCallback.appeared.isEmpty());
        Assert.assertTrue(mCallback.unused.isEmpty());
        Assert.assertTrue(mCallback.persistent.isEmpty());
    }

    @Test
    public void processDisappearMoveOut() {
        ViewHolder vh = new ViewInfoStoreTest.MockViewHolder();
        ViewInfoStoreTest.MockInfo pre = new ViewInfoStoreTest.MockInfo();
        ViewInfoStoreTest.MockInfo post = new ViewInfoStoreTest.MockInfo();
        mStore.addToPreLayout(vh, pre);
        mStore.addToDisappearedInLayout(vh);
        mStore.addToPostLayout(vh, post);
        mStore.process(mCallback);
        Assert.assertEquals(new Pair(pre, post), mCallback.disappeared.get(vh));
        Assert.assertTrue(mCallback.appeared.isEmpty());
        Assert.assertTrue(mCallback.unused.isEmpty());
        Assert.assertTrue(mCallback.persistent.isEmpty());
    }

    @Test
    public void processDisappearAppear() {
        ViewHolder vh = new ViewInfoStoreTest.MockViewHolder();
        ViewInfoStoreTest.MockInfo pre = new ViewInfoStoreTest.MockInfo();
        ViewInfoStoreTest.MockInfo post = new ViewInfoStoreTest.MockInfo();
        mStore.addToPreLayout(vh, pre);
        mStore.addToDisappearedInLayout(vh);
        mStore.addToPostLayout(vh, post);
        mStore.removeFromDisappearedInLayout(vh);
        mStore.process(mCallback);
        Assert.assertTrue(mCallback.disappeared.isEmpty());
        Assert.assertTrue(mCallback.appeared.isEmpty());
        Assert.assertTrue(mCallback.unused.isEmpty());
        Assert.assertEquals(mCallback.persistent.get(vh), new Pair(pre, post));
    }

    @Test
    public void processAppearAndDisappearInPostLayout() {
        ViewHolder vh = new ViewInfoStoreTest.MockViewHolder();
        ViewInfoStoreTest.MockInfo info1 = new ViewInfoStoreTest.MockInfo();
        mStore.addToPostLayout(vh, info1);
        mStore.addToDisappearedInLayout(vh);
        mStore.process(mCallback);
        Assert.assertTrue(mCallback.disappeared.isEmpty());
        Assert.assertTrue(mCallback.appeared.isEmpty());
        Assert.assertTrue(mCallback.persistent.isEmpty());
        Assert.assertSame(mCallback.unused.get(0), vh);
    }

    static class MockViewHolder extends RecyclerView.ViewHolder {
        public MockViewHolder() {
            super(new View(null));
        }
    }

    static class MockInfo extends RecyclerView.ItemAnimator.ItemHolderInfo {}

    private static class LoggingProcessCallback implements ViewInfoStore.ProcessCallback {
        final Map<ViewHolder, Pair<ItemHolderInfo, ItemHolderInfo>> disappeared = new HashMap<>();

        final Map<ViewHolder, Pair<ItemHolderInfo, ItemHolderInfo>> appeared = new HashMap<>();

        final Map<ViewHolder, Pair<ItemHolderInfo, ItemHolderInfo>> persistent = new HashMap<>();

        final List<ViewHolder> unused = new ArrayList<>();

        @Override
        public void processDisappeared(ViewHolder viewHolder, ItemHolderInfo preInfo, @Nullable
        ItemHolderInfo postInfo) {
            Assert.assertNotNull(preInfo);
            Assert.assertFalse(disappeared.containsKey(viewHolder));
            disappeared.put(viewHolder, new Pair(preInfo, postInfo));
        }

        @Override
        public void processAppeared(ViewHolder viewHolder, @Nullable
        ItemHolderInfo preInfo, @NonNull
        ItemHolderInfo info) {
            Assert.assertNotNull(info);
            Assert.assertFalse(appeared.containsKey(viewHolder));
            appeared.put(viewHolder, new Pair(preInfo, info));
        }

        @Override
        public void processPersistent(ViewHolder viewHolder, @NonNull
        ItemHolderInfo preInfo, @NonNull
        ItemHolderInfo postInfo) {
            Assert.assertFalse(persistent.containsKey(viewHolder));
            Assert.assertNotNull(preInfo);
            Assert.assertNotNull(postInfo);
            persistent.put(viewHolder, new Pair(preInfo, postInfo));
        }

        @Override
        public void unused(ViewHolder holder) {
            unused.add(holder);
        }
    }
}

