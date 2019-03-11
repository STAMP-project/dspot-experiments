/**
 * Copyright 2014-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.litho;


import ViewPager.OnPageChangeListener;
import android.content.Context;
import android.graphics.Rect;
import androidx.viewpager.widget.ViewPager;
import com.facebook.litho.testing.Whitebox;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@RunWith(ComponentsTestRunner.class)
public class ComponentTreeIncrementalMountLocalVisibleBoundsTest {
    private LithoView mLithoView;

    private ComponentTree mComponentTree;

    private final Rect mMountedRect = new Rect();

    @Test
    public void testGetLocalVisibleBounds() {
        Mockito.doAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                Rect rect = ((Rect) (invocation.getArguments()[0]));
                rect.set(new Rect(10, 5, 20, 15));
                return true;
            }
        }).when(mLithoView).getLocalVisibleRect(ArgumentMatchers.any(Rect.class));
        mComponentTree.incrementalMountComponent();
        assertThat(mMountedRect).isEqualTo(new Rect(10, 5, 20, 15));
    }

    @Test
    public void testViewPagerInHierarchy() {
        Mockito.doAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return false;
            }
        }).when(mLithoView).getLocalVisibleRect(ArgumentMatchers.any(Rect.class));
        ViewPager viewPager = Mockito.mock(ViewPager.class);
        Mockito.when(mLithoView.getParent()).thenReturn(viewPager);
        mComponentTree.attach();
        // This is set to null by mComponentTree.attach(), so set it again here.
        Whitebox.setInternalState(mComponentTree, "mMainThreadLayoutState", Mockito.mock(LayoutState.class));
        ArgumentCaptor<ViewPager.OnPageChangeListener> listenerArgumentCaptor = ArgumentCaptor.forClass(OnPageChangeListener.class);
        Mockito.verify(viewPager).addOnPageChangeListener(listenerArgumentCaptor.capture());
        Mockito.doAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                Rect rect = ((Rect) (invocation.getArguments()[0]));
                rect.set(new Rect(10, 5, 20, 15));
                return true;
            }
        }).when(mLithoView).getLocalVisibleRect(ArgumentMatchers.any(Rect.class));
        listenerArgumentCaptor.getValue().onPageScrolled(10, 10, 10);
        assertThat(mMountedRect).isEqualTo(new Rect(10, 5, 20, 15));
        mComponentTree.detach();
        ArgumentCaptor<Runnable> runnableArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(viewPager).postOnAnimation(runnableArgumentCaptor.capture());
        runnableArgumentCaptor.getValue().run();
        Mockito.verify(viewPager).removeOnPageChangeListener(listenerArgumentCaptor.getValue());
    }

    /**
     * Required in order to ensure that {@link LithoView#mount(LayoutState, Rect, boolean)} is mocked
     * correctly (it needs protected access to be mocked).
     */
    public static class TestLithoView extends LithoView {
        public TestLithoView(Context context) {
            super(context);
        }

        protected void mount(LayoutState layoutState, Rect currentVisibleArea, boolean processVisibilityOutputs) {
            super.mount(layoutState, currentVisibleArea, processVisibilityOutputs);
        }
    }
}

