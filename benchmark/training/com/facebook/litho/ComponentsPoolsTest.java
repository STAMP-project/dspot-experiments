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


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.util.ActivityController;


@RunWith(ComponentsTestRunner.class)
public class ComponentsPoolsTest {
    private static final int POOL_SIZE = 2;

    private final Component mLifecycle = new Component("Lifecycle") {
        @Override
        int getTypeId() {
            return 1;
        }

        @Override
        protected int poolSize() {
            return ComponentsPoolsTest.POOL_SIZE;
        }

        @Override
        public View onCreateMountContent(Context context) {
            return mNewMountContent;
        }
    };

    private final Component mLifecycleWithEmptyPoolSize = new Component("LifecycleWithEmptyPoolSize") {
        @Override
        int getTypeId() {
            return 2;
        }

        @Override
        protected int poolSize() {
            return 0;
        }

        @Override
        public View onCreateMountContent(Context context) {
            return mNewMountContent;
        }
    };

    private Context mContext1;

    private Context mContext2;

    private ActivityController<Activity> mActivityController;

    private Activity mActivity;

    private ColorDrawable mMountContent;

    private View mNewMountContent;

    @Test
    public void testAcquireMountContentWithSameContext() {
        assertThat(ComponentsPools.acquireMountContent(mContext1, mLifecycle)).isSameAs(mNewMountContent);
        ComponentsPools.release(mContext1, mLifecycle, mMountContent);
        assertThat(mMountContent).isSameAs(ComponentsPools.acquireMountContent(mContext1, mLifecycle));
    }

    @Test
    public void testAcquireMountContentWithSameRootContext() {
        assertThat(ComponentsPools.acquireMountContent(mContext1, mLifecycle)).isSameAs(mNewMountContent);
        ComponentsPools.release(mContext1, mLifecycle, mMountContent);
        assertThat(ComponentsPools.acquireMountContent(mContext2, mLifecycle)).isSameAs(mNewMountContent);
    }

    @Test
    public void testReleaseMountContentForDestroyedContextDoesNothing() {
        // Assert pooling was working before
        assertThat(ComponentsPools.acquireMountContent(mActivity, mLifecycle)).isSameAs(mNewMountContent);
        ComponentsPools.release(mActivity, mLifecycle, mMountContent);
        assertThat(mMountContent).isSameAs(ComponentsPools.acquireMountContent(mActivity, mLifecycle));
        // Now destroy it and assert pooling no longer works
        mActivityController.destroy();
        ComponentsPools.release(mActivity, mLifecycle, mMountContent);
        assertThat(ComponentsPools.acquireMountContent(mActivity, mLifecycle)).isSameAs(mNewMountContent);
    }

    @Test
    public void testDestroyingActivityDoesNotAffectPoolingOfOtherContexts() {
        mActivityController.destroy();
        ComponentsPools.onContextDestroyed(mActivity);
        ComponentsPools.release(mContext1, mLifecycle, mMountContent);
        assertThat(ComponentsPools.acquireMountContent(mContext1, mLifecycle)).isSameAs(mMountContent);
    }

    @Test
    public void testPreallocateContent() {
        assertThat(ComponentsPools.acquireMountContent(mContext1, mLifecycle)).isSameAs(mNewMountContent);
        ComponentsPools.maybePreallocateContent(mContext1, mLifecycle);
        // Change the content that's returned when we create new mount content to make sure we're
        // getting the one from preallocating above.
        mNewMountContent = new View(mContext1);
        assertThat(ComponentsPools.acquireMountContent(mContext1, mLifecycle)).isNotSameAs(mNewMountContent);
    }

    @Test
    public void testDoNotPreallocateContentBeyondPoolSize() {
        for (int i = 0; i < (ComponentsPoolsTest.POOL_SIZE); i++) {
            ComponentsPools.maybePreallocateContent(mContext1, mLifecycle);
            ComponentsPools.acquireMountContent(mContext1, mLifecycle);
        }
        ComponentsPools.maybePreallocateContent(mContext1, mLifecycle);
        mNewMountContent = new View(mContext1);
        assertThat(ComponentsPools.acquireMountContent(mContext1, mLifecycle)).isSameAs(mNewMountContent);
    }

    @Test
    public void testAllocationsCountTowardsPreallocationLimit() {
        for (int i = 0; i < ((ComponentsPoolsTest.POOL_SIZE) - 1); i++) {
            ComponentsPools.maybePreallocateContent(mContext1, mLifecycle);
            ComponentsPools.acquireMountContent(mContext1, mLifecycle);
        }
        ComponentsPools.acquireMountContent(mContext1, mLifecycle);
        // Allocation limit should be hit now, so we shouldn't preallocate anything
        ComponentsPools.maybePreallocateContent(mContext1, mLifecycle);
        mNewMountContent = new View(mContext1);
        assertThat(ComponentsPools.acquireMountContent(mContext1, mLifecycle)).isSameAs(mNewMountContent);
    }

    @Test
    public void testReleaseAndAcquireWithNoPoolSize() {
        ComponentsPools.release(mContext1, mLifecycleWithEmptyPoolSize, mMountContent);
        assertThat(ComponentsPools.acquireMountContent(mContext1, mLifecycleWithEmptyPoolSize)).isSameAs(mNewMountContent);
    }

    @Test
    public void testPreallocateWithEmptyPoolSize() {
        ComponentsPools.maybePreallocateContent(mContext1, mLifecycleWithEmptyPoolSize);
        mNewMountContent = new View(mContext1);
        assertThat(ComponentsPools.acquireMountContent(mContext1, mLifecycleWithEmptyPoolSize)).isSameAs(mNewMountContent);
    }
}

