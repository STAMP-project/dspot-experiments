/**
 * Copyright 2018-present Facebook, Inc.
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


import com.facebook.litho.testing.TestDrawableComponent;
import com.facebook.litho.testing.TestWrappedComponentPropSpec;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.shadows.ShadowLooper;


@RunWith(ComponentsTestRunner.class)
public class ComponentPropThreadSafetyTest {
    private ComponentContext mContext;

    private ShadowLooper mLayoutThreadShadowLooper;

    @Test
    public void testThreadSafeConcurrentPropComponentAccess() {
        TestDrawableComponent testComponent = Mockito.spy(TestDrawableComponent.create(mContext).build());
        final TestWrappedComponentPropSpec.ComponentWrapper wrapper = new TestWrappedComponentPropSpec.ComponentWrapper(testComponent);
        final Component root = com.facebook.litho.testing.TestWrappedComponentProp.create(mContext).wrapper(wrapper).build();
        final ComponentTree componentTree = ComponentTree.create(mContext, root).build();
        componentTree.setRootAndSizeSpec(com.facebook.litho.testing.TestWrappedComponentProp.create(mContext).wrapper(wrapper).build(), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), null);
        componentTree.setRootAndSizeSpecAsync(com.facebook.litho.testing.TestWrappedComponentProp.create(mContext).wrapper(wrapper).build(), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        mLayoutThreadShadowLooper.runToEndOfTasks();
        Mockito.verify(testComponent).makeShallowCopy();
    }

    @Test
    public void testThreadSafeConcurrentPropListComponentAccess() {
        TestDrawableComponent testComponent = Mockito.spy(TestDrawableComponent.create(mContext).build());
        List<Component> componentList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            componentList.add(testComponent);
        }
        final Component root = com.facebook.litho.testing.TestWrappedComponentProp.create(mContext).componentList(componentList).build();
        final ComponentTree componentTree = ComponentTree.create(mContext, root).build();
        componentTree.setRootAndSizeSpec(com.facebook.litho.testing.TestWrappedComponentProp.create(mContext).componentList(componentList).build(), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), null);
        componentTree.setRootAndSizeSpecAsync(com.facebook.litho.testing.TestWrappedComponentProp.create(mContext).componentList(componentList).build(), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        mLayoutThreadShadowLooper.runToEndOfTasks();
        Mockito.verify(testComponent, Mockito.times(19)).makeShallowCopy();
    }
}

