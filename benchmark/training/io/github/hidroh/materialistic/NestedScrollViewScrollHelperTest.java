/**
 * Copyright (c) 2016 Ha Duy Trung
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
package io.github.hidroh.materialistic;


import KeyDelegate.NestedScrollViewHelper;
import View.FOCUS_DOWN;
import View.FOCUS_UP;
import android.support.v4.widget.NestedScrollView;
import io.github.hidroh.materialistic.test.TestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(TestRunner.class)
public class NestedScrollViewScrollHelperTest {
    private NestedScrollView scrollView;

    private NestedScrollViewHelper helper;

    @Test
    public void testScrollToTop() {
        helper.scrollToTop();
        Mockito.verify(scrollView).smoothScrollTo(ArgumentMatchers.eq(0), ArgumentMatchers.eq(0));
    }

    @Test
    public void testScrollToNext() {
        helper.scrollToNext();
        Mockito.verify(scrollView).pageScroll(ArgumentMatchers.eq(FOCUS_DOWN));
    }

    @Test
    public void testScrollToPrevious() {
        helper.scrollToPrevious();
        Mockito.verify(scrollView).pageScroll(FOCUS_UP);
    }
}

