/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.agera.rvadapter;


import RecyclerView.NO_ID;
import RecyclerView.ViewHolder;
import android.support.annotation.NonNull;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public final class RepositoryPresenterTest {
    @Test
    public void shouldReturnDefaultItemId() throws Exception {
        Assert.assertThat(getItemId(new Object(), 0), Matchers.is(NO_ID));
    }

    @Test
    public void shouldHaveDefaultRecycleImplementation() throws Exception {
        final View view = new View(RuntimeEnvironment.application);
        final RecyclerView.ViewHolder viewHolder = new RecyclerView.ViewHolder(view) {};
        new RepositoryPresenterTest.TestRepositoryPresenter().recycle(viewHolder);
    }

    @Test
    public void shouldHaveDefaultGetUpdatesImplementation() throws Exception {
        final boolean returnValue = getUpdates(new Object(), new Object(), Mockito.mock(ListUpdateCallback.class));
        Assert.assertThat(returnValue, Matchers.is(false));
    }

    private static final class TestRepositoryPresenter extends RepositoryPresenter<Object> {
        @Override
        public int getItemCount(@NonNull
        final Object data) {
            return 0;
        }

        @Override
        public int getLayoutResId(@NonNull
        final Object data, final int index) {
            return 0;
        }

        @Override
        public void bind(@NonNull
        final Object data, final int index, @NonNull
        final RecyclerView.ViewHolder holder) {
        }
    }
}

