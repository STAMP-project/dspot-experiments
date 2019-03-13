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


import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION_CODES;
import android.view.View;
import com.facebook.litho.testing.TestComponent;
import com.facebook.litho.testing.TestDrawableComponent;
import com.facebook.litho.testing.TestViewComponent;
import com.facebook.litho.testing.shadows.LayoutDirectionViewGroupShadow;
import com.facebook.litho.testing.shadows.LayoutDirectionViewShadow;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.testing.util.InlineLayoutSpec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@Config(manifest = Config.NONE, sdk = VERSION_CODES.LOLLIPOP, shadows = { LayoutDirectionViewShadow.class, LayoutDirectionViewGroupShadow.class })
@RunWith(ComponentsTestRunner.class)
public class LayoutDirectionTest {
    private ComponentContext mContext;

    /**
     * Test that view mount items are laid out in the correct positions for LTR and RTL layout
     * directions.
     */
    @Test
    public void testViewChildrenLayoutDirection() {
        final TestComponent child1 = TestViewComponent.create(mContext, true, true, true, false).build();
        final TestComponent child2 = TestViewComponent.create(mContext, true, true, true, false).build();
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Row.create(c).layoutDirection(LTR).child(Wrapper.create(c).delegate(child1).widthPx(10).heightPx(10)).child(Wrapper.create(c).delegate(child2).widthPx(10).heightPx(10)).build();
            }
        }, 20, 10);
        View view1 = lithoView.getChildAt(0);
        View view2 = lithoView.getChildAt(1);
        assertThat(new Rect(view1.getLeft(), view1.getTop(), view1.getRight(), view1.getBottom())).isEqualTo(new Rect(0, 0, 10, 10));
        assertThat(new Rect(view2.getLeft(), view2.getTop(), view2.getRight(), view2.getBottom())).isEqualTo(new Rect(10, 0, 20, 10));
        mountComponent(mContext, lithoView, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Row.create(c).layoutDirection(RTL).child(Wrapper.create(c).delegate(child1).widthPx(10).heightPx(10)).child(Wrapper.create(c).delegate(child2).widthPx(10).heightPx(10)).build();
            }
        }, 20, 10);
        view1 = lithoView.getChildAt(0);
        view2 = lithoView.getChildAt(1);
        assertThat(new Rect(view1.getLeft(), view1.getTop(), view1.getRight(), view1.getBottom())).isEqualTo(new Rect(10, 0, 20, 10));
        assertThat(new Rect(view2.getLeft(), view2.getTop(), view2.getRight(), view2.getBottom())).isEqualTo(new Rect(0, 0, 10, 10));
    }

    /**
     * Test that drawable mount items are laid out in the correct positions for LTR and RTL layout
     * directions.
     */
    @Test
    public void testDrawableChildrenLayoutDirection() {
        final TestComponent child1 = TestDrawableComponent.create(mContext).build();
        final TestComponent child2 = TestDrawableComponent.create(mContext).build();
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Row.create(c).layoutDirection(LTR).child(Wrapper.create(c).delegate(child1).widthPx(10).heightPx(10)).child(Wrapper.create(c).delegate(child2).widthPx(10).heightPx(10)).build();
            }
        }, 20, 10);
        Drawable drawable1 = lithoView.getDrawables().get(0);
        Drawable drawable2 = lithoView.getDrawables().get(1);
        assertThat(drawable1.getBounds()).isEqualTo(new Rect(0, 0, 10, 10));
        assertThat(drawable2.getBounds()).isEqualTo(new Rect(10, 0, 20, 10));
        mountComponent(mContext, lithoView, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Row.create(c).layoutDirection(RTL).child(Wrapper.create(c).delegate(child1).widthPx(10).heightPx(10)).child(Wrapper.create(c).delegate(child2).widthPx(10).heightPx(10)).build();
            }
        }, 20, 10);
        drawable1 = lithoView.getDrawables().get(0);
        drawable2 = lithoView.getDrawables().get(1);
        assertThat(drawable1.getBounds()).isEqualTo(new Rect(10, 0, 20, 10));
        assertThat(drawable2.getBounds()).isEqualTo(new Rect(0, 0, 10, 10));
    }

    /**
     * Test that layout direction is propagated properly throughout a component hierarchy. This is the
     * default behaviour of layout direction.
     */
    @Test
    public void testInheritLayoutDirection() {
        final TestComponent child1 = TestDrawableComponent.create(mContext).build();
        final TestComponent child2 = TestDrawableComponent.create(mContext).build();
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Row.create(c).layoutDirection(RTL).child(Row.create(c).wrapInView().child(Wrapper.create(c).delegate(child1).widthPx(10).heightPx(10)).child(Wrapper.create(c).delegate(child2).widthPx(10).heightPx(10))).build();
            }
        }, 20, 10);
        final ComponentHost host = ((ComponentHost) (lithoView.getChildAt(0)));
        final Drawable drawable1 = host.getDrawables().get(0);
        final Drawable drawable2 = host.getDrawables().get(1);
        assertThat(drawable1.getBounds()).isEqualTo(new Rect(10, 0, 20, 10));
        assertThat(drawable2.getBounds()).isEqualTo(new Rect(0, 0, 10, 10));
    }

    /**
     * Test that layout direction is correctly set on child components when it differs from the layout
     * direction of it's parent.
     */
    @Test
    public void testNestedComponentWithDifferentLayoutDirection() {
        final TestComponent child1 = TestDrawableComponent.create(mContext).build();
        final TestComponent child2 = TestDrawableComponent.create(mContext).build();
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Row.create(c).layoutDirection(RTL).child(Row.create(c).layoutDirection(LTR).wrapInView().child(Wrapper.create(c).delegate(child1).widthPx(10).heightPx(10)).child(Wrapper.create(c).delegate(child2).widthPx(10).heightPx(10))).build();
            }
        }, 20, 10);
        final ComponentHost host = ((ComponentHost) (lithoView.getChildAt(0)));
        final Drawable drawable1 = host.getDrawables().get(0);
        final Drawable drawable2 = host.getDrawables().get(1);
        assertThat(drawable1.getBounds()).isEqualTo(new Rect(0, 0, 10, 10));
        assertThat(drawable2.getBounds()).isEqualTo(new Rect(10, 0, 20, 10));
    }

    /**
     * Test that margins on START and END are correctly applied to the correct side of the component
     * depending upon the applied layout direction.
     */
    @Test
    public void testMargin() {
        final TestComponent child = TestDrawableComponent.create(mContext).build();
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).layoutDirection(LTR).child(Wrapper.create(c).delegate(child).widthPx(10).heightPx(10).marginPx(START, 10).marginPx(END, 20)).build();
            }
        }, 40, 10);
        Drawable drawable = lithoView.getDrawables().get(0);
        assertThat(drawable.getBounds()).isEqualTo(new Rect(10, 0, 20, 10));
        mountComponent(mContext, lithoView, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).layoutDirection(RTL).child(Wrapper.create(c).delegate(child).widthPx(10).heightPx(10).marginPx(START, 10).marginPx(END, 20)).build();
            }
        }, 40, 10);
        drawable = lithoView.getDrawables().get(0);
        assertThat(drawable.getBounds()).isEqualTo(new Rect(20, 0, 30, 10));
    }

    /**
     * Test that paddings on START and END are correctly applied to the correct side of the component
     * depending upon the applied layout direction.
     */
    @Test
    public void testPadding() {
        final TestComponent child = TestDrawableComponent.create(mContext).build();
        final LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).layoutDirection(LTR).paddingPx(START, 10).paddingPx(END, 20).child(Wrapper.create(c).delegate(child).widthPx(10).heightPx(10)).build();
            }
        }, 40, 10);
        Drawable drawable = lithoView.getDrawables().get(0);
        assertThat(drawable.getBounds()).isEqualTo(new Rect(10, 0, 20, 10));
        mountComponent(mContext, lithoView, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).layoutDirection(RTL).paddingPx(START, 10).paddingPx(END, 20).child(Wrapper.create(c).delegate(child).widthPx(10).heightPx(10)).build();
            }
        }, 40, 10);
        drawable = lithoView.getDrawables().get(0);
        assertThat(drawable.getBounds()).isEqualTo(new Rect(20, 0, 30, 10));
    }

    /**
     * Tests to make sure the layout direction set on the component tree is correctly propagated to
     * mounted views.
     */
    @Test
    public void testLayoutDirectionPropagation() {
        final TestComponent child = TestViewComponent.create(mContext).build();
        LithoView lithoView = mountComponent(mContext, new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).layoutDirection(RTL).child(child).build();
            }
        });
        final View childView = lithoView.getChildAt(0);
        assertThat(childView.getLayoutDirection()).isEqualTo(LAYOUT_DIRECTION_RTL);
    }
}

