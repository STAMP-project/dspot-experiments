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
import com.facebook.litho.testing.TestLayoutComponent;
import com.facebook.litho.testing.TestNullLayoutComponent;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.testing.util.InlineLayoutSpec;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ComponentsTestRunner.class)
public class LayoutStateCalculateVisibilityOutputsTest {
    @Test
    public void testNoUnnecessaryVisibilityOutputs() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestDrawableComponent.create(c).visibleHandler(c.newEventHandler(1)))).child(TestDrawableComponent.create(c).invisibleHandler(c.newEventHandler(2))).child(TestDrawableComponent.create(c)).build();
            }
        };
        LayoutState layoutState = LayoutStateCalculateVisibilityOutputsTest.calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getVisibilityOutputCount()).isEqualTo(2);
    }

    @Test
    public void testNoUnnecessaryVisibilityOutputsWithFullImpression() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestDrawableComponent.create(c).visibleHandler(c.newEventHandler(1)))).child(TestDrawableComponent.create(c).fullImpressionHandler(c.newEventHandler(3))).child(TestDrawableComponent.create(c)).build();
            }
        };
        LayoutState layoutState = LayoutStateCalculateVisibilityOutputsTest.calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getVisibilityOutputCount()).isEqualTo(2);
    }

    @Test
    public void testNoUnnecessaryVisibilityOutputsWithFocused() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestDrawableComponent.create(c).visibleHandler(c.newEventHandler(1)))).child(TestDrawableComponent.create(c).focusedHandler(c.newEventHandler(4))).child(TestDrawableComponent.create(c)).build();
            }
        };
        LayoutState layoutState = LayoutStateCalculateVisibilityOutputsTest.calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getVisibilityOutputCount()).isEqualTo(2);
    }

    @Test
    public void testVisibilityOutputsForDelegateComponents() {
        final boolean isDelegate = true;
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(TestLayoutComponent.create(c, 0, 0, true, true, false, isDelegate).visibleHandler(c.newEventHandler(1))).wrapInView().build();
            }
        };
        LayoutState layoutState = LayoutStateCalculateVisibilityOutputsTest.calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getVisibilityOutputCount()).isEqualTo(1);
    }

    @Test
    public void testLayoutOutputsForDeepLayoutSpecs() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestLayoutComponent.create(c).visibleHandler(c.newEventHandler(1))).invisibleHandler(c.newEventHandler(2))).child(Column.create(c).child(TestLayoutComponent.create(c).invisibleHandler(c.newEventHandler(1))).visibleHandler(c.newEventHandler(2))).wrapInView().build();
            }
        };
        LayoutState layoutState = LayoutStateCalculateVisibilityOutputsTest.calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(350, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY));
        // Check total layout outputs.
        assertThat(layoutState.getVisibilityOutputCount()).isEqualTo(4);
        // Check number of Components with VisibleEvent handlers.
        int visibleHandlerCount = 0;
        for (int i = 0; i < (layoutState.getVisibilityOutputCount()); i++) {
            if ((layoutState.getVisibilityOutputAt(i).getVisibleEventHandler()) != null) {
                visibleHandlerCount += 1;
            }
        }
        assertThat(visibleHandlerCount).isEqualTo(2);
    }

    @Test
    public void testLayoutOutputsForForceWrappedComponent() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c).visibleHandler(c.newEventHandler(1)).wrapInView()).build();
            }
        };
        final LayoutState layoutState = LayoutStateCalculateVisibilityOutputsTest.calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getVisibilityOutputCount()).isEqualTo(1);
    }

    @Test
    public void testLayoutOutputForRootWithNullLayout() {
        final Component componentWithNullLayout = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return null;
            }
        };
        LayoutState layoutState = LayoutStateCalculateVisibilityOutputsTest.calculateLayoutState(application, componentWithNullLayout, (-1), SizeSpec.makeSizeSpec(350, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY));
        assertThat(layoutState.getVisibilityOutputCount()).isEqualTo(0);
    }

    @Test
    public void testLayoutComponentForNestedTreeChildWithNullLayout() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).paddingPx(ALL, 2).child(new TestNullLayoutComponent()).invisibleHandler(c.newEventHandler(2)).build();
            }
        };
        LayoutState layoutState = LayoutStateCalculateVisibilityOutputsTest.calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(350, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY));
        assertThat(layoutState.getVisibilityOutputCount()).isEqualTo(1);
    }
}

