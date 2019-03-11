/**
 * The MIT License (MIT) Copyright (c) 2014 karumi Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions: The above copyright notice and this permission
 * notice shall be included in all copies or substantial portions of the Software. THE SOFTWARE
 * IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.karumi.rosie.view;


import android.R.layout;
import com.karumi.rosie.RobolectricTest;
import org.junit.Test;
import org.mockito.Mockito;


public class RosieFragmentTest extends RobolectricTest {
    private static final int ANY_LAYOUT = layout.list_content;

    @Test
    public void shouldCallInitializePresenterAfterOnViewCreatedMethod() {
        RosieFragmentTest.TestFragment testFragment = startFragment();
        Mockito.verify(testFragment.presenter).initialize();
    }

    @Test
    public void shouldCallUpdatePresenterWhenFragmentResume() {
        RosieFragmentTest.TestFragment testFragment = startFragment();
        Mockito.verify(testFragment.presenter).update();
    }

    @Test
    public void shouldCallPausePresenterWhenFragmentPause() {
        RosieFragmentTest.TestFragment testFragment = startFragment();
        onPause();
        Mockito.verify(testFragment.presenter).pause();
    }

    @Test
    public void shouldCallDestroyPresenterWhenFragmentDestroy() {
        RosieFragmentTest.TestFragment testFragment = startFragment();
        onDestroy();
        Mockito.verify(testFragment.presenter).destroy();
    }

    public static class TestFragment extends RosieFragment {
        @Presenter
        RosiePresenter presenter = Mockito.mock(RosiePresenter.class);

        public TestFragment() {
        }

        @Override
        protected boolean shouldInjectFragment() {
            return false;
        }

        @Override
        protected int getLayoutId() {
            return RosieFragmentTest.ANY_LAYOUT;
        }
    }
}

