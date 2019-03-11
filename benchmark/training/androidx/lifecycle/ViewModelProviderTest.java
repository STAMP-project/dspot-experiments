/**
 * Copyright 2017 The Android Open Source Project
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


import ViewModelProvider.KeyedFactory;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ViewModelProviderTest {
    private ViewModelProvider mViewModelProvider;

    @Test
    public void twoViewModelsWithSameKey() throws Throwable {
        String key = "the_key";
        ViewModelProviderTest.ViewModel1 vm1 = mViewModelProvider.get(key, ViewModelProviderTest.ViewModel1.class);
        MatcherAssert.assertThat(vm1.mCleared, CoreMatchers.is(false));
        ViewModelProviderTest.ViewModel2 vw2 = mViewModelProvider.get(key, ViewModelProviderTest.ViewModel2.class);
        MatcherAssert.assertThat(vw2, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(vm1.mCleared, CoreMatchers.is(true));
    }

    @Test
    public void localViewModel() throws Throwable {
        class VM extends ViewModelProviderTest.ViewModel1 {}
        try {
            mViewModelProvider.get(VM.class);
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void twoViewModels() {
        ViewModelProviderTest.ViewModel1 model1 = mViewModelProvider.get(ViewModelProviderTest.ViewModel1.class);
        ViewModelProviderTest.ViewModel2 model2 = mViewModelProvider.get(ViewModelProviderTest.ViewModel2.class);
        MatcherAssert.assertThat(mViewModelProvider.get(ViewModelProviderTest.ViewModel1.class), CoreMatchers.is(model1));
        MatcherAssert.assertThat(mViewModelProvider.get(ViewModelProviderTest.ViewModel2.class), CoreMatchers.is(model2));
    }

    @Test
    public void testOwnedBy() {
        final ViewModelStore store = new ViewModelStore();
        ViewModelStoreOwner owner = new ViewModelStoreOwner() {
            @NonNull
            @Override
            public ViewModelStore getViewModelStore() {
                return store;
            }
        };
        ViewModelProvider provider = new ViewModelProvider(owner, new NewInstanceFactory());
        ViewModelProviderTest.ViewModel1 viewModel = provider.get(ViewModelProviderTest.ViewModel1.class);
        MatcherAssert.assertThat(viewModel, CoreMatchers.is(provider.get(ViewModelProviderTest.ViewModel1.class)));
    }

    @Test
    public void testKeyedFactory() {
        final ViewModelStore store = new ViewModelStore();
        ViewModelStoreOwner owner = new ViewModelStoreOwner() {
            @NonNull
            @Override
            public ViewModelStore getViewModelStore() {
                return store;
            }
        };
        ViewModelProvider.KeyedFactory keyed = new ViewModelProvider.KeyedFactory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull
            String key, @NonNull
            Class<T> modelClass) {
                MatcherAssert.assertThat(key, CoreMatchers.is("customkey"));
                return ((T) (new ViewModelProviderTest.ViewModel1()));
            }
        };
        ViewModelProvider provider = new ViewModelProvider(owner, keyed);
        provider.get("customkey", ViewModelProviderTest.ViewModel1.class);
    }

    public static class ViewModel1 extends ViewModel {
        boolean mCleared;

        @Override
        protected void onCleared() {
            mCleared = true;
        }
    }

    public static class ViewModel2 extends ViewModel {}
}

