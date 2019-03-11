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


import ComponentTree.NewLayoutStateReadyListener;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.shadows.ShadowLooper;


@RunWith(ComponentsTestRunner.class)
public class ComponentTreeNewLayoutStateReadyListenerTest {
    private int mWidthSpec;

    private int mWidthSpec2;

    private int mHeightSpec;

    private int mHeightSpec2;

    private Component mComponent;

    private ShadowLooper mLayoutThreadShadowLooper;

    private ComponentContext mContext;

    private ComponentTree mComponentTree;

    private NewLayoutStateReadyListener mListener;

    @Test
    public void testListenerInvokedForSetRoot() {
        mComponentTree.setNewLayoutStateReadyListener(mListener);
        mComponentTree.setRootAndSizeSpec(mComponent, mWidthSpec, mHeightSpec);
        Mockito.verify(mListener).onNewLayoutStateReady(mComponentTree);
    }

    @Test
    public void testListenerInvokedForSetRootAsync() {
        mComponentTree.setNewLayoutStateReadyListener(mListener);
        mComponentTree.setSizeSpecAsync(mWidthSpec, mHeightSpec);
        Mockito.verify(mListener, Mockito.never()).onNewLayoutStateReady(ArgumentMatchers.any(ComponentTree.class));
        // Now the background thread run the queued task.
        mLayoutThreadShadowLooper.runToEndOfTasks();
        Mockito.verify(mListener).onNewLayoutStateReady(mComponentTree);
    }

    @Test
    public void testListenerInvokedOnlyOnceForMultipleSetRootAsync() {
        mComponentTree.setNewLayoutStateReadyListener(mListener);
        mComponentTree.setSizeSpecAsync(mWidthSpec, mHeightSpec);
        mComponentTree.setSizeSpecAsync(mWidthSpec2, mHeightSpec2);
        Mockito.verify(mListener, Mockito.never()).onNewLayoutStateReady(ArgumentMatchers.any(ComponentTree.class));
        // Now the background thread run the queued task.
        mLayoutThreadShadowLooper.runToEndOfTasks();
        Mockito.verify(mListener).onNewLayoutStateReady(mComponentTree);
    }

    @Test
    public void testListenerInvokedForSetRootAsyncWhenAttached() {
        mComponentTree.setNewLayoutStateReadyListener(mListener);
        mComponentTree.setSizeSpecAsync(mWidthSpec, mHeightSpec);
        mComponentTree.setLithoView(new LithoView(mContext));
        mComponentTree.attach();
        Mockito.verify(mListener, Mockito.never()).onNewLayoutStateReady(ArgumentMatchers.any(ComponentTree.class));
        // Now the background thread run the queued task.
        mLayoutThreadShadowLooper.runToEndOfTasks();
        Mockito.verify(mListener).onNewLayoutStateReady(mComponentTree);
    }

    @Test
    public void testListenerInvokedForMeasure() {
        mComponentTree.setNewLayoutStateReadyListener(mListener);
        mComponentTree.setLithoView(new LithoView(mContext));
        mComponentTree.attach();
        mComponentTree.setSizeSpec(mWidthSpec, mHeightSpec);
        Mockito.verify(mListener).onNewLayoutStateReady(mComponentTree);
        Mockito.reset(mListener);
        mComponentTree.measure(mWidthSpec2, mHeightSpec2, new int[]{ 0, 0 }, false);
        Mockito.verify(mListener).onNewLayoutStateReady(mComponentTree);
    }

    @Test
    public void testListenerNotInvokedWhenMeasureDoesntComputeALayout() {
        mComponentTree.setNewLayoutStateReadyListener(mListener);
        mComponentTree.setLithoView(new LithoView(mContext));
        mComponentTree.attach();
        mComponentTree.setSizeSpec(mWidthSpec, mHeightSpec);
        Mockito.verify(mListener).onNewLayoutStateReady(mComponentTree);
        Mockito.reset(mListener);
        mComponentTree.measure(mWidthSpec, mHeightSpec, new int[]{ 0, 0 }, false);
        Mockito.verify(mListener, Mockito.never()).onNewLayoutStateReady(mComponentTree);
    }

    @Test
    public void testListenerNotInvokedWhenNewMeasureSpecsAreCompatible() {
        mComponentTree.setLithoView(new LithoView(mContext));
        mComponentTree.attach();
        mComponentTree.setSizeSpec(mWidthSpec, mHeightSpec);
        mComponentTree.setNewLayoutStateReadyListener(mListener);
        mComponentTree.setSizeSpec(mWidthSpec, mHeightSpec);
        Mockito.verify(mListener, Mockito.never()).onNewLayoutStateReady(mComponentTree);
    }
}

