/**
 * Copyright (C) 2017 The Android Open Source Project
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
package androidx.lifecycle;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class ViewModelStoreTest {
    @Test
    public void testClear() {
        ViewModelStore store = new ViewModelStore();
        ViewModelStoreTest.TestViewModel viewModel1 = new ViewModelStoreTest.TestViewModel();
        ViewModelStoreTest.TestViewModel viewModel2 = new ViewModelStoreTest.TestViewModel();
        ViewModelStoreTest.TestViewModel mockViewModel = Mockito.mock(ViewModelStoreTest.TestViewModel.class);
        store.put("a", viewModel1);
        store.put("b", viewModel2);
        store.put("mock", mockViewModel);
        MatcherAssert.assertThat(viewModel1.mCleared, CoreMatchers.is(false));
        MatcherAssert.assertThat(viewModel2.mCleared, CoreMatchers.is(false));
        store.clear();
        MatcherAssert.assertThat(viewModel1.mCleared, CoreMatchers.is(true));
        MatcherAssert.assertThat(viewModel2.mCleared, CoreMatchers.is(true));
        Mockito.verify(mockViewModel).onCleared();
        Mockito.verifyNoMoreInteractions(mockViewModel);
        MatcherAssert.assertThat(store.get("a"), CoreMatchers.nullValue());
        MatcherAssert.assertThat(store.get("b"), CoreMatchers.nullValue());
    }

    static class TestViewModel extends ViewModel {
        boolean mCleared = false;

        @Override
        protected void onCleared() {
            mCleared = true;
        }
    }
}

