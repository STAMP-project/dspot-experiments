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
package androidx.lifecycle;


import java.io.Closeable;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ViewModelTest {
    static class CloseableImpl implements Closeable {
        boolean mWasClosed;

        @Override
        public void close() {
            mWasClosed = true;
        }
    }

    class ViewModel extends androidx.lifecycle.ViewModel {}

    @Test
    public void testCloseableTag() {
        ViewModelTest.ViewModel vm = new ViewModelTest.ViewModel();
        ViewModelTest.CloseableImpl impl = new ViewModelTest.CloseableImpl();
        vm.setTagIfAbsent("totally_not_coroutine_context", impl);
        clear();
        Assert.assertTrue(impl.mWasClosed);
    }

    @Test
    public void testCloseableTagAlreadyClearedVM() {
        ViewModelTest.ViewModel vm = new ViewModelTest.ViewModel();
        clear();
        ViewModelTest.CloseableImpl impl = new ViewModelTest.CloseableImpl();
        vm.setTagIfAbsent("key", impl);
        Assert.assertTrue(impl.mWasClosed);
    }

    @Test
    public void testAlreadyAssociatedKey() {
        ViewModelTest.ViewModel vm = new ViewModelTest.ViewModel();
        MatcherAssert.assertThat(setTagIfAbsent("key", "first"), CoreMatchers.is("first"));
        MatcherAssert.assertThat(setTagIfAbsent("key", "second"), CoreMatchers.is("first"));
    }
}

