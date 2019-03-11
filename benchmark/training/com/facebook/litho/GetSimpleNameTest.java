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


import com.facebook.litho.testing.TestDrawableComponent;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ComponentsTestRunner.class)
public class GetSimpleNameTest {
    private static class TestWrapperComponent extends Component {
        private final Component mDelegate;

        protected TestWrapperComponent(Component delegate) {
            super("TestWrapper");
            mDelegate = delegate;
        }

        @Override
        protected Component getSimpleNameDelegate() {
            return mDelegate;
        }
    }

    private ComponentContext mContext;

    @Test
    public void testGetSimpleName() {
        TestDrawableComponent testComponent = TestDrawableComponent.create(mContext).build();
        assertThat(testComponent.getSimpleName()).isEqualTo("TestDrawableComponent");
    }

    @Test
    public void testGetSimpleNameWithOneWrapper() {
        TestDrawableComponent inner = TestDrawableComponent.create(mContext).build();
        GetSimpleNameTest.TestWrapperComponent wrapper = new GetSimpleNameTest.TestWrapperComponent(inner);
        assertThat(getSimpleName()).isEqualTo("TestWrapper(TestDrawableComponent)");
    }

    @Test
    public void testGetSimpleNameWithMultipleWrapper() {
        TestDrawableComponent inner = TestDrawableComponent.create(mContext).build();
        GetSimpleNameTest.TestWrapperComponent wrapper = new GetSimpleNameTest.TestWrapperComponent(inner);
        GetSimpleNameTest.TestWrapperComponent wrapper2 = new GetSimpleNameTest.TestWrapperComponent(wrapper);
        GetSimpleNameTest.TestWrapperComponent wrapper3 = new GetSimpleNameTest.TestWrapperComponent(wrapper2);
        assertThat(getSimpleName()).isEqualTo("TestWrapper(TestDrawableComponent)");
    }
}

