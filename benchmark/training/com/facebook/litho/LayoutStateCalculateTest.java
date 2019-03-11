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


import OutputUnitType.HOST;
import RuntimeEnvironment.application;
import SizeSpec.EXACTLY;
import SizeSpec.UNSPECIFIED;
import Transition.TransitionKeyType.GLOBAL;
import TransitionId.Type;
import YogaAlign.STRETCH;
import YogaEdge.ALL;
import android.graphics.Rect;
import androidx.core.view.ViewCompat;
import com.facebook.litho.config.ComponentsConfiguration;
import com.facebook.litho.testing.TestComponent;
import com.facebook.litho.testing.TestDrawableComponent;
import com.facebook.litho.testing.TestLayoutComponent;
import com.facebook.litho.testing.TestNullLayoutComponent;
import com.facebook.litho.testing.TestSizeDependentComponent;
import com.facebook.litho.testing.TestViewComponent;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.testing.util.InlineLayoutSpec;
import com.facebook.litho.widget.Text;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static MountItem.LAYOUT_FLAG_PHANTOM;


@RunWith(ComponentsTestRunner.class)
public class LayoutStateCalculateTest {
    @Test
    public void testNoUnnecessaryLayoutOutputsForLayoutSpecs() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestDrawableComponent.create(c))).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, EXACTLY), SizeSpec.makeSizeSpec(100, EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(2);
    }

    @Test
    public void testLayoutOutputsForRootInteractiveLayoutSpecs() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).wrapInView().build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(2);
    }

    @Test
    public void testLayoutOutputsForSpecsWithTouchExpansion() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c).widthPx(100).heightPx(10)).child(Row.create(c).viewTag(new Object()).child(TestDrawableComponent.create(c).widthPx(20).heightPx(90)).child(Column.create(c).child(TestDrawableComponent.create(c)).clickHandler(c.newEventHandler(1)).widthPx(50).heightPx(50).touchExpansionPx(ALL, 5))).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(6);
        final ViewNodeInfo viewNodeInfo = layoutState.getMountableOutputAt(4).getViewNodeInfo();
        assertThat(viewNodeInfo.getExpandedTouchBounds()).isEqualTo(new Rect(15, (-5), 75, 55));
        final NodeInfo nodeInfo = layoutState.getMountableOutputAt(4).getNodeInfo();
        assertThat(nodeInfo).isNotNull();
        assertThat(nodeInfo.getClickHandler()).isNotNull();
        assertThat(nodeInfo.getLongClickHandler()).isNull();
        assertThat(nodeInfo.getFocusChangeHandler()).isNull();
        assertThat(nodeInfo.getTouchHandler()).isNull();
    }

    @Test
    public void testLayoutOutputsForSpecsWithClickHandling() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestDrawableComponent.create(c)).clickHandler(c.newEventHandler(1))).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(3);
        final NodeInfo nodeInfo = layoutState.getMountableOutputAt(1).getNodeInfo();
        assertThat(nodeInfo).isNotNull();
        assertThat(nodeInfo.getClickHandler()).isNotNull();
        assertThat(nodeInfo.getLongClickHandler()).isNull();
        assertThat(nodeInfo.getFocusChangeHandler()).isNull();
        assertThat(nodeInfo.getTouchHandler()).isNull();
    }

    @Test
    public void testLayoutOutputsForSpecsWithLongClickHandling() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestDrawableComponent.create(c)).longClickHandler(c.newEventHandler(1))).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(3);
        final NodeInfo nodeInfo = layoutState.getMountableOutputAt(1).getNodeInfo();
        assertThat(nodeInfo).isNotNull();
        assertThat(nodeInfo.getClickHandler()).isNull();
        assertThat(nodeInfo.getLongClickHandler()).isNotNull();
        assertThat(nodeInfo.getFocusChangeHandler()).isNull();
        assertThat(nodeInfo.getTouchHandler()).isNull();
    }

    @Test
    public void testLayoutOutputsForSpecsWithFocusChangeHandling() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestDrawableComponent.create(c)).focusChangeHandler(c.newEventHandler(1))).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(3);
        final NodeInfo nodeInfo = layoutState.getMountableOutputAt(1).getNodeInfo();
        assertThat(nodeInfo).isNotNull();
        assertThat(nodeInfo.getClickHandler()).isNull();
        assertThat(nodeInfo.getLongClickHandler()).isNull();
        assertThat(nodeInfo.getFocusChangeHandler()).isNotNull();
        assertThat(nodeInfo.getTouchHandler()).isNull();
    }

    @Test
    public void testLayoutOutputsForSpecsWithInterceptTouchHandling() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestDrawableComponent.create(c)).interceptTouchHandler(c.newEventHandler(1))).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(3);
        final NodeInfo nodeInfo = layoutState.getMountableOutputAt(1).getNodeInfo();
        assertThat(nodeInfo).isNotNull();
        assertThat(nodeInfo.getClickHandler()).isNull();
        assertThat(nodeInfo.getLongClickHandler()).isNull();
        assertThat(nodeInfo.getInterceptTouchHandler()).isNotNull();
        assertThat(nodeInfo.getTouchHandler()).isNull();
    }

    @Test
    public void testLayoutOutputsForSpecsWithTouchHandling() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestDrawableComponent.create(c)).touchHandler(c.newEventHandler(1))).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(3);
        final NodeInfo nodeInfo = layoutState.getMountableOutputAt(1).getNodeInfo();
        assertThat(nodeInfo).isNotNull();
        assertThat(nodeInfo.getTouchHandler()).isNotNull();
        assertThat(nodeInfo.getClickHandler()).isNull();
        assertThat(nodeInfo.getLongClickHandler()).isNull();
        assertThat(nodeInfo.getFocusChangeHandler()).isNull();
    }

    @Test
    public void testLayoutOutputsForDeepLayoutSpecs() {
        final int paddingSize = 5;
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).backgroundColor(-65536).child(Row.create(c).justifyContent(SPACE_AROUND).alignItems(CENTER).positionType(ABSOLUTE).positionPx(LEFT, 50).positionPx(TOP, 50).positionPx(RIGHT, 200).positionPx(BOTTOM, 50).child(Text.create(c).text("textLeft1")).child(Text.create(c).text("textRight1")).paddingPx(ALL, paddingSize).wrapInView()).child(Row.create(c).justifyContent(SPACE_AROUND).alignItems(CENTER).positionType(ABSOLUTE).positionPx(LEFT, 200).positionPx(TOP, 50).positionPx(RIGHT, 50).positionPx(BOTTOM, 50).child(Text.create(c).text("textLeft2").wrapInView().paddingPx(ALL, paddingSize)).child(TestViewComponent.create(c).wrapInView())).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(350, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY));
        // Check total layout outputs.
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(8);
        // Check quantity of HostComponents.
        int totalHosts = 0;
        for (int i = 0; i < (layoutState.getMountableOutputCount()); i++) {
            final ComponentLifecycle lifecycle = LayoutStateCalculateTest.getComponentAt(layoutState, i);
            if (LayoutStateCalculateTest.isHostComponent(lifecycle)) {
                totalHosts++;
            }
        }
        assertThat(totalHosts).isEqualTo(3);
        // Check all the Layouts are in the correct position.
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 0))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 1)).isInstanceOf(DrawableComponent.class);
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 2))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 3)).isInstanceOf(Text.class);
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 4)).isInstanceOf(Text.class);
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 5))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 6)).isInstanceOf(Text.class);
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 7)).isInstanceOf(TestViewComponent.class);
        // Check the text within the TextComponents.
        assertThat(LayoutStateCalculateTest.getTextFromTextComponent(layoutState, 3)).isEqualTo("textLeft1");
        assertThat(LayoutStateCalculateTest.getTextFromTextComponent(layoutState, 4)).isEqualTo("textRight1");
        assertThat(LayoutStateCalculateTest.getTextFromTextComponent(layoutState, 6)).isEqualTo("textLeft2");
        final Rect textLayoutBounds = layoutState.getMountableOutputAt(6).getBounds();
        final Rect textBackgroundBounds = layoutState.getMountableOutputAt(5).getBounds();
        assertThat(((textLayoutBounds.left) - paddingSize)).isEqualTo(textBackgroundBounds.left);
        assertThat(((textLayoutBounds.top) - paddingSize)).isEqualTo(textBackgroundBounds.top);
        assertThat(((textLayoutBounds.right) + paddingSize)).isEqualTo(textBackgroundBounds.right);
        assertThat(((textLayoutBounds.bottom) + paddingSize)).isEqualTo(textBackgroundBounds.bottom);
    }

    @Test
    public void testLayoutOutputMountBounds() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).widthPx(30).heightPx(30).wrapInView().child(Column.create(c).widthPx(10).heightPx(10).marginPx(ALL, 10).wrapInView().child(TestDrawableComponent.create(c).widthPx(10).heightPx(10))).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(350, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY));
        final Rect mountBounds = new Rect();
        layoutState.getMountableOutputAt(0).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(0, 0, 30, 30));
        layoutState.getMountableOutputAt(1).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(10, 10, 20, 20));
        layoutState.getMountableOutputAt(2).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(0, 0, 10, 10));
    }

    @Test
    public void testLayoutOutputsForDeepLayoutSpecsWithBackground() {
        final int paddingSize = 5;
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).backgroundColor(-65536).child(Row.create(c).justifyContent(SPACE_AROUND).alignItems(CENTER).positionType(ABSOLUTE).positionPx(LEFT, 50).positionPx(TOP, 50).positionPx(RIGHT, 200).positionPx(BOTTOM, 50).child(Text.create(c).text("textLeft1")).child(Text.create(c).text("textRight1")).backgroundColor(-65536).foregroundColor(-65536).paddingPx(ALL, paddingSize).wrapInView()).child(Row.create(c).justifyContent(SPACE_AROUND).alignItems(CENTER).positionType(ABSOLUTE).positionPx(LEFT, 200).positionPx(TOP, 50).positionPx(RIGHT, 50).positionPx(BOTTOM, 50).child(Text.create(c).text("textLeft2").wrapInView().backgroundColor(-65536).paddingPx(ALL, paddingSize)).child(TestViewComponent.create(c).backgroundColor(-65536).foregroundColor(65535).paddingPx(ALL, paddingSize))).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(350, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY));
        // Account for Android version in the foreground. If >= M the foreground is part of the
        // ViewLayoutOutput otherwise it has its own LayoutOutput.
        final boolean foregroundHasOwnOutput = (SDK_INT) < (M);
        // Check total layout outputs.
        assertThat(layoutState.getMountableOutputCount()).isEqualTo((foregroundHasOwnOutput ? 12 : 11));
        // Check quantity of HostComponents.
        int totalHosts = 0;
        for (int i = 0; i < (layoutState.getMountableOutputCount()); i++) {
            final ComponentLifecycle lifecycle = LayoutStateCalculateTest.getComponentAt(layoutState, i);
            if (LayoutStateCalculateTest.isHostComponent(lifecycle)) {
                totalHosts++;
            }
        }
        assertThat(totalHosts).isEqualTo(3);
        // Check all the Layouts are in the correct position.
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 0))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 1)).isInstanceOf(DrawableComponent.class);
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 2))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 3)).isInstanceOf(DrawableComponent.class);
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 4)).isInstanceOf(Text.class);
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 5)).isInstanceOf(Text.class);
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 6)).isInstanceOf(DrawableComponent.class);
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 7))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 8)).isInstanceOf(DrawableComponent.class);
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 9)).isInstanceOf(Text.class);
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 10)).isInstanceOf(TestViewComponent.class);
        if (foregroundHasOwnOutput) {
            assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 11)).isInstanceOf(DrawableComponent.class);
        }
        // Check the text within the TextComponents.
        assertThat(LayoutStateCalculateTest.getTextFromTextComponent(layoutState, 4)).isEqualTo("textLeft1");
        assertThat(LayoutStateCalculateTest.getTextFromTextComponent(layoutState, 5)).isEqualTo("textRight1");
        assertThat(LayoutStateCalculateTest.getTextFromTextComponent(layoutState, 9)).isEqualTo("textLeft2");
        // Check that the backgrounds have the same size of the components to which they are associated
        assertThat(layoutState.getMountableOutputAt(3).getBounds()).isEqualTo(layoutState.getMountableOutputAt(2).getBounds());
        assertThat(layoutState.getMountableOutputAt(6).getBounds()).isEqualTo(layoutState.getMountableOutputAt(2).getBounds());
        final Rect textLayoutBounds = layoutState.getMountableOutputAt(9).getBounds();
        final Rect textBackgroundBounds = layoutState.getMountableOutputAt(8).getBounds();
        assertThat(((textLayoutBounds.left) - paddingSize)).isEqualTo(textBackgroundBounds.left);
        assertThat(((textLayoutBounds.top) - paddingSize)).isEqualTo(textBackgroundBounds.top);
        assertThat(((textLayoutBounds.right) + paddingSize)).isEqualTo(textBackgroundBounds.right);
        assertThat(((textLayoutBounds.bottom) + paddingSize)).isEqualTo(textBackgroundBounds.bottom);
        assertThat(layoutState.getMountableOutputAt(8).getBounds()).isEqualTo(layoutState.getMountableOutputAt(7).getBounds());
        final ViewNodeInfo viewNodeInfo = layoutState.getMountableOutputAt(10).getViewNodeInfo();
        assertThat(viewNodeInfo).isNotNull();
        assertThat(((viewNodeInfo.getBackground()) != null)).isTrue();
        if (foregroundHasOwnOutput) {
            assertThat(((viewNodeInfo.getForeground()) == null)).isTrue();
        } else {
            assertThat(((viewNodeInfo.getForeground()) != null)).isTrue();
        }
        assertThat(((viewNodeInfo.getPaddingLeft()) == paddingSize)).isTrue();
        assertThat(((viewNodeInfo.getPaddingTop()) == paddingSize)).isTrue();
        assertThat(((viewNodeInfo.getPaddingRight()) == paddingSize)).isTrue();
        assertThat(((viewNodeInfo.getPaddingBottom()) == paddingSize)).isTrue();
    }

    @Test
    public void testLayoutOutputsForMegaDeepLayoutSpecs() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).wrapInView()).child(Column.create(c).child(TestDrawableComponent.create(c).wrapInView()).child(Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).wrapInView()).wrapInView()).child(Column.create(c).child(TestDrawableComponent.create(c)).child(Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c))).wrapInView()).child(Column.create(c).child(TestDrawableComponent.create(c)).child(Column.create(c).child(TestDrawableComponent.create(c)).child(TestViewComponent.create(c))).wrapInView()).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(350, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY));
        // Check total layout outputs.
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(18);
        // Check quantity of HostComponents.
        int totalHosts = 0;
        for (int i = 0; i < (layoutState.getMountableOutputCount()); i++) {
            final ComponentLifecycle lifecycle = LayoutStateCalculateTest.getComponentAt(layoutState, i);
            if (LayoutStateCalculateTest.isHostComponent(lifecycle)) {
                totalHosts++;
            }
        }
        assertThat(totalHosts).isEqualTo(7);
        // Check all the Components match the right LayoutOutput positions.
        // Tree One.
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 0))).isTrue();
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 1))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 2)).isInstanceOf(TestDrawableComponent.class);
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 3)).isInstanceOf(TestDrawableComponent.class);
        // Tree Two.
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 4))).isTrue();
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 5))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 6)).isInstanceOf(TestDrawableComponent.class);
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 7))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 8)).isInstanceOf(TestDrawableComponent.class);
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 9)).isInstanceOf(TestDrawableComponent.class);
        // Tree Three.
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 10))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 11)).isInstanceOf(TestDrawableComponent.class);
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 12)).isInstanceOf(TestDrawableComponent.class);
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 13)).isInstanceOf(TestDrawableComponent.class);
        // Tree Four.
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 14))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 15)).isInstanceOf(TestDrawableComponent.class);
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 16)).isInstanceOf(TestDrawableComponent.class);
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 17)).isInstanceOf(TestViewComponent.class);
    }

    @Test
    public void testLayoutOutputStableIds() {
        final Component component1 = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestDrawableComponent.create(c)).contentDescription("cd0")).child(Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).contentDescription("cd1")).child(Column.create(c).child(TestDrawableComponent.create(c)).child(TestViewComponent.create(c)).contentDescription("cd2")).build();
            }
        };
        final Component component2 = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestDrawableComponent.create(c)).contentDescription("cd0")).child(Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).contentDescription("cd1")).child(Column.create(c).child(TestDrawableComponent.create(c)).child(TestViewComponent.create(c)).contentDescription("cd2")).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component1, (-1), SizeSpec.makeSizeSpec(350, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(20, SizeSpec.EXACTLY));
        final LayoutState sameComponentLayoutState = calculateLayoutState(application, component2, (-1), SizeSpec.makeSizeSpec(350, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(20, SizeSpec.EXACTLY));
        assertThat(sameComponentLayoutState.getMountableOutputCount()).isEqualTo(layoutState.getMountableOutputCount());
        for (int i = 0; i < (layoutState.getMountableOutputCount()); i++) {
            assertThat(sameComponentLayoutState.getMountableOutputAt(i).getId()).isEqualTo(layoutState.getMountableOutputAt(i).getId());
        }
    }

    @Test
    public void testLayoutOutputStableIdsForMegaDeepComponent() {
        final Component component1 = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).wrapInView()).child(Column.create(c).child(TestDrawableComponent.create(c).wrapInView()).child(Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).wrapInView()).wrapInView()).child(Column.create(c).child(TestDrawableComponent.create(c)).child(Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c))).wrapInView()).child(Column.create(c).child(TestDrawableComponent.create(c)).child(Column.create(c).child(TestDrawableComponent.create(c)).child(TestViewComponent.create(c))).wrapInView()).build();
            }
        };
        final Component component2 = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).wrapInView()).child(Column.create(c).child(TestDrawableComponent.create(c).wrapInView()).child(Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).wrapInView()).wrapInView()).child(Column.create(c).child(TestDrawableComponent.create(c)).child(Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c))).wrapInView()).child(Column.create(c).child(TestDrawableComponent.create(c)).child(Column.create(c).child(TestDrawableComponent.create(c)).child(TestViewComponent.create(c))).wrapInView()).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component1, (-1), SizeSpec.makeSizeSpec(350, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(20, SizeSpec.EXACTLY));
        final LayoutState sameComponentLayoutState = calculateLayoutState(application, component2, (-1), SizeSpec.makeSizeSpec(350, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(20, SizeSpec.EXACTLY));
        assertThat(sameComponentLayoutState.getMountableOutputCount()).isEqualTo(layoutState.getMountableOutputCount());
        for (int i = 0; i < (layoutState.getMountableOutputCount()); i++) {
            assertThat(sameComponentLayoutState.getMountableOutputAt(i).getId()).isEqualTo(layoutState.getMountableOutputAt(i).getId());
        }
    }

    @Test
    public void testPartiallyStableIds() {
        final Component component1 = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        };
        final Component component2 = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).child(Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c))).build();
            }
        };
        final LayoutState layoutState1 = calculateLayoutState(application, component1, (-1), SizeSpec.makeSizeSpec(350, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(20, SizeSpec.EXACTLY));
        final LayoutState layoutState2 = calculateLayoutState(application, component2, (-1), SizeSpec.makeSizeSpec(350, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(20, SizeSpec.EXACTLY));
        assertThat(layoutState2.getMountableOutputAt(0).getId()).isEqualTo(layoutState1.getMountableOutputAt(0).getId());
        assertThat(layoutState2.getMountableOutputAt(1).getId()).isEqualTo(layoutState1.getMountableOutputAt(1).getId());
        assertThat(layoutState1.getMountableOutputCount()).isEqualTo(3);
        assertThat(layoutState2.getMountableOutputCount()).isEqualTo(4);
    }

    @Test
    public void testDifferentIds() {
        final Component component1 = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).build();
            }
        };
        final Component component2 = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c).wrapInView()).child(Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).wrapInView()).build();
            }
        };
        final LayoutState layoutState1 = calculateLayoutState(application, component1, (-1), SizeSpec.makeSizeSpec(350, EXACTLY), SizeSpec.makeSizeSpec(20, EXACTLY));
        final LayoutState layoutState2 = calculateLayoutState(application, component2, (-1), SizeSpec.makeSizeSpec(350, EXACTLY), SizeSpec.makeSizeSpec(20, EXACTLY));
        assertThat(layoutState1.getMountableOutputAt(1).getId()).isNotEqualTo(layoutState2.getMountableOutputAt(1).getId());
    }

    @Test
    public void testLayoutOutputsWithInteractiveLayoutSpecAsLeafs() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).child(Column.create(c).child(TestLayoutComponent.create(c)).wrapInView()).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(350, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY));
        // Check total layout outputs.
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(3);
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 0))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 1)).isInstanceOf(TestDrawableComponent.class);
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 2))).isTrue();
    }

    @Test
    public void testNoMeasureOnNestedComponentWithSameSpecs() {
        final ComponentContext c = new ComponentContext(application);
        final Size size = new Size();
        final TestComponent innerComponent = TestDrawableComponent.create(c, 0, 0, false, true, true, false, false).build();
        final int widthSpec = SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY);
        final int heightSpec = SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY);
        innerComponent.measure(c, widthSpec, heightSpec, size);
        final InternalNode internalNode = getCachedLayout();
        internalNode.setLastWidthSpec(widthSpec);
        internalNode.setLastHeightSpec(heightSpec);
        internalNode.setLastMeasuredWidth(internalNode.getWidth());
        internalNode.setLastMeasuredHeight(internalNode.getHeight());
        innerComponent.resetInteractions();
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Row.create(c).child(innerComponent).widthPx(100).heightPx(100)).build();
            }
        };
        calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(innerComponent.wasMeasureCalled()).isFalse();
    }

    @Test
    public void testNoMeasureOnNestedComponentWithNewMeasureSpecExact() {
        final ComponentContext c = new ComponentContext(application);
        final Size size = new Size();
        final TestComponent innerComponent = TestDrawableComponent.create(c, 0, 0, false, true, true, false, false).build();
        final int widthSpec = SizeSpec.makeSizeSpec(100, SizeSpec.AT_MOST);
        final int heightSpec = SizeSpec.makeSizeSpec(100, SizeSpec.AT_MOST);
        innerComponent.measure(c, widthSpec, heightSpec, size);
        final InternalNode internalNode = getCachedLayout();
        internalNode.setLastWidthSpec(widthSpec);
        internalNode.setLastHeightSpec(heightSpec);
        internalNode.setLastMeasuredWidth(100);
        internalNode.setLastMeasuredHeight(100);
        innerComponent.resetInteractions();
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Row.create(c).child(innerComponent).widthPx(100).heightPx(100)).build();
            }
        };
        calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(innerComponent.wasMeasureCalled()).isFalse();
    }

    @Test
    public void testNoMeasureOnNestedComponentWithNewMeasureSpecOldUnspecified() {
        final ComponentContext c = new ComponentContext(application);
        final Size size = new Size();
        final TestComponent innerComponent = TestDrawableComponent.create(c, 0, 0, false, true, true, false, false).build();
        final int widthSpec = SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED);
        final int heightSpec = SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED);
        innerComponent.measure(c, widthSpec, heightSpec, size);
        final InternalNode internalNode = getCachedLayout();
        internalNode.setLastWidthSpec(widthSpec);
        internalNode.setLastHeightSpec(heightSpec);
        internalNode.setLastMeasuredWidth(99);
        internalNode.setLastMeasuredHeight(99);
        innerComponent.resetInteractions();
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(innerComponent).build();
            }
        };
        calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.AT_MOST), SizeSpec.makeSizeSpec(100, SizeSpec.AT_MOST));
        assertThat(innerComponent.wasMeasureCalled()).isFalse();
    }

    @Test
    public void testNoMeasureOnNestedComponentWithOldAndNewAtMost() {
        final ComponentContext c = new ComponentContext(application);
        final Size size = new Size();
        final TestComponent innerComponent = TestDrawableComponent.create(c, 0, 0, false, true, true, false, false).build();
        final int widthSpec = SizeSpec.makeSizeSpec(100, SizeSpec.AT_MOST);
        final int heightSpec = SizeSpec.makeSizeSpec(100, SizeSpec.AT_MOST);
        innerComponent.measure(c, widthSpec, heightSpec, size);
        final InternalNode internalNode = getCachedLayout();
        internalNode.setLastWidthSpec(widthSpec);
        internalNode.setLastHeightSpec(heightSpec);
        internalNode.setLastMeasuredWidth(50);
        internalNode.setLastMeasuredHeight(50);
        innerComponent.resetInteractions();
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Row.create(c).child(innerComponent).flexShrink(0)).build();
            }
        };
        calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(50, SizeSpec.AT_MOST), SizeSpec.makeSizeSpec(50, SizeSpec.AT_MOST));
        assertThat(innerComponent.wasMeasureCalled()).isFalse();
    }

    @Test
    public void testLayoutOutputsForTwiceNestedComponent() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(Column.create(c).child(TestDrawableComponent.create(c))).wrapInView()).child(Column.create(c).child(Column.create(c).child(TestDrawableComponent.create(c))).child(Column.create(c).child(TestDrawableComponent.create(c)))).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(5);
        final long hostMarkerRoot = layoutState.getMountableOutputAt(0).getId();
        final long hostMarkerOne = layoutState.getMountableOutputAt(1).getId();
        // First output is the inner host for the click handler
        assertThat(layoutState.getMountableOutputAt(1).getHostMarker()).isEqualTo(hostMarkerRoot);
        // Second output is the child of the inner host
        assertThat(layoutState.getMountableOutputAt(2).getHostMarker()).isEqualTo(hostMarkerOne);
        // Third and fourth outputs are children of the root view.
        assertThat(layoutState.getMountableOutputAt(3).getHostMarker()).isEqualTo(hostMarkerRoot);
        assertThat(layoutState.getMountableOutputAt(4).getHostMarker()).isEqualTo(hostMarkerRoot);
    }

    @Test
    public void testLayoutOutputsForComponentWithBackgrounds() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).backgroundColor(-65536).foregroundColor(-65536).child(TestDrawableComponent.create(c)).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(4);
        // First and third output are the background and the foreground
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 1)).isInstanceOf(DrawableComponent.class);
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 3)).isInstanceOf(DrawableComponent.class);
    }

    @Test
    public void testLayoutOutputsForNonComponentClickableNode() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestDrawableComponent.create(c)).wrapInView()).child(Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).wrapInView()).child(Column.create(c).child(TestDrawableComponent.create(c)).child(TestViewComponent.create(c)).wrapInView()).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(9);
        final long hostMarkerRoot = layoutState.getMountableOutputAt(0).getHostMarker();
        final long hostMarkerZero = layoutState.getMountableOutputAt(1).getHostMarker();
        final long hostMarkerTwo = layoutState.getMountableOutputAt(4).getHostMarker();
        final long hostMarkerThree = layoutState.getMountableOutputAt(7).getHostMarker();
        assertThat(layoutState.getMountableOutputAt(1).getHostMarker()).isEqualTo(hostMarkerRoot);
        assertThat(layoutState.getMountableOutputAt(3).getHostMarker()).isEqualTo(hostMarkerZero);
        assertThat(layoutState.getMountableOutputAt(5).getHostMarker()).isEqualTo(hostMarkerTwo);
        assertThat(layoutState.getMountableOutputAt(6).getHostMarker()).isEqualTo(hostMarkerZero);
        assertThat(layoutState.getMountableOutputAt(8).getHostMarker()).isEqualTo(hostMarkerThree);
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 0))).isTrue();
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 1))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 2)).isInstanceOf(TestDrawableComponent.class);
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 3))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 4)).isInstanceOf(TestDrawableComponent.class);
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 5)).isInstanceOf(TestDrawableComponent.class);
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 6))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 7)).isInstanceOf(TestDrawableComponent.class);
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 8)).isInstanceOf(TestViewComponent.class);
    }

    @Test
    public void testLayoutOutputsForNonComponentContentDescriptionNode() {
        enableAccessibility();
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestDrawableComponent.create(c)).contentDescription("cd0")).child(Column.create(c).child(TestDrawableComponent.create(c)).child(TestDrawableComponent.create(c)).contentDescription("cd1")).child(Column.create(c).child(TestDrawableComponent.create(c)).child(TestViewComponent.create(c)).contentDescription("cd2")).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(9);
        final long hostMarkerRoot = layoutState.getMountableOutputAt(0).getHostMarker();
        final long hostMarkerZero = layoutState.getMountableOutputAt(1).getHostMarker();
        final long hostMarkerTwo = layoutState.getMountableOutputAt(4).getHostMarker();
        final long hostMarkerThree = layoutState.getMountableOutputAt(7).getHostMarker();
        assertThat(layoutState.getMountableOutputAt(1).getHostMarker()).isEqualTo(hostMarkerRoot);
        assertThat(layoutState.getMountableOutputAt(3).getHostMarker()).isEqualTo(hostMarkerZero);
        assertThat(layoutState.getMountableOutputAt(5).getHostMarker()).isEqualTo(hostMarkerTwo);
        assertThat(layoutState.getMountableOutputAt(6).getHostMarker()).isEqualTo(hostMarkerZero);
        assertThat(layoutState.getMountableOutputAt(8).getHostMarker()).isEqualTo(hostMarkerThree);
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 0))).isTrue();
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 1))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 2)).isInstanceOf(TestDrawableComponent.class);
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 3))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 4)).isInstanceOf(TestDrawableComponent.class);
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 5)).isInstanceOf(TestDrawableComponent.class);
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 6))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 7)).isInstanceOf(TestDrawableComponent.class);
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 8)).isInstanceOf(TestViewComponent.class);
    }

    @Test
    public void testLayoutOutputsForFocusableOnRoot() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).focusable(true).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(2);
        final long hostMarkerZero = layoutState.getMountableOutputAt(0).getHostMarker();
        assertThat(layoutState.getMountableOutputAt(1).getHostMarker()).isEqualTo(hostMarkerZero);
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 0))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 1)).isInstanceOf(TestDrawableComponent.class);
        assertThat(layoutState.getMountableOutputAt(0).getNodeInfo().getFocusState()).isEqualTo(NodeInfo.FOCUS_SET_TRUE);
    }

    @Test
    public void testLayoutOutputsForFocusable() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestDrawableComponent.create(c)).focusable(true)).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(3);
        assertThat(layoutState.getMountableOutputAt(0).getNodeInfo()).isNull();
        assertThat(layoutState.getMountableOutputAt(1).getNodeInfo().getFocusState()).isEqualTo(NodeInfo.FOCUS_SET_TRUE);
    }

    @Test
    public void testLayoutOutputsForSelectedOnRoot() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).selected(true).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(2);
        final long hostMarkerZero = layoutState.getMountableOutputAt(0).getHostMarker();
        assertThat(layoutState.getMountableOutputAt(1).getHostMarker()).isEqualTo(hostMarkerZero);
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 0))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 1)).isInstanceOf(TestDrawableComponent.class);
        assertThat(layoutState.getMountableOutputAt(0).getNodeInfo().getSelectedState()).isEqualTo(NodeInfo.SELECTED_SET_TRUE);
    }

    @Test
    public void testLayoutOutputsForSelected() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestDrawableComponent.create(c)).focusable(true).selected(true)).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(3);
        assertThat(layoutState.getMountableOutputAt(0).getNodeInfo()).isNull();
        assertThat(layoutState.getMountableOutputAt(1).getNodeInfo().getSelectedState()).isEqualTo(NodeInfo.SELECTED_SET_TRUE);
    }

    @Test
    public void testLayoutOutputsForEnabledFalseDoesntWrap() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestDrawableComponent.create(c).enabled(false))).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(2);
        assertThat(layoutState.getMountableOutputAt(0).getNodeInfo()).isNull();
        assertThat(layoutState.getMountableOutputAt(0).getComponent().getSimpleName()).isEqualTo("HostComponent");
        assertThat(layoutState.getMountableOutputAt(1).getComponent().getSimpleName()).isEqualTo("TestDrawableComponent");
        assertThat(MountItem.isTouchableDisabled(layoutState.getMountableOutputAt(1).getFlags())).isTrue();
    }

    @Test
    public void testLayoutOutputsForEnabledFalseInInnerWrappedComponentDrawable() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestDrawableComponent.create(c).clickHandler(c.newEventHandler(1)).enabled(false))).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        // Because the TestDrawableComponent is disabled, we don't wrap it in a host.
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(2);
        assertThat(layoutState.getMountableOutputAt(0).getNodeInfo()).isNull();
        assertThat(layoutState.getMountableOutputAt(0).getComponent()).isInstanceOf(HostComponent.class);
        assertThat(layoutState.getMountableOutputAt(1).getComponent()).isInstanceOf(TestDrawableComponent.class);
        assertThat(MountItem.isTouchableDisabled(layoutState.getMountableOutputAt(1).getFlags())).isTrue();
    }

    @Test
    public void testLayoutOutputsForEnabledFalseInInnerComponentView() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(TestViewComponent.create(c).enabled(false))).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(2);
        assertThat(layoutState.getMountableOutputAt(0).getNodeInfo()).isNull();
        assertThat(layoutState.getMountableOutputAt(0).getComponent()).isInstanceOf(HostComponent.class);
        assertThat(layoutState.getMountableOutputAt(1).getComponent()).isInstanceOf(TestViewComponent.class);
        assertThat(layoutState.getMountableOutputAt(1).getNodeInfo().getEnabledState()).isEqualTo(NodeInfo.ENABLED_SET_FALSE);
    }

    @Test
    public void testLayoutOutputsForEnabledFalseApplyToDescendent() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).enabled(false).child(TestViewComponent.create(c).enabled(true)).child(TestDrawableComponent.create(c).clickHandler(c.newEventHandler(1))).child(TestDrawableComponent.create(c).enabled(false))).child(Column.create(c).child(TestViewComponent.create(c)).child(TestDrawableComponent.create(c))).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(6);
        assertThat(layoutState.getMountableOutputAt(0).getNodeInfo()).isNull();
        assertThat(layoutState.getMountableOutputAt(0).getComponent()).isInstanceOf(HostComponent.class);
        assertThat(layoutState.getMountableOutputAt(1).getComponent()).isInstanceOf(TestViewComponent.class);
        assertThat(layoutState.getMountableOutputAt(1).getNodeInfo().getEnabledState()).isEqualTo(NodeInfo.ENABLED_SET_FALSE);
        assertThat(layoutState.getMountableOutputAt(2).getComponent()).isInstanceOf(TestDrawableComponent.class);
        assertThat(layoutState.getMountableOutputAt(2).getNodeInfo()).isNull();
        assertThat(MountItem.isTouchableDisabled(layoutState.getMountableOutputAt(2).getFlags())).isTrue();
        assertThat(layoutState.getMountableOutputAt(3).getComponent()).isInstanceOf(TestDrawableComponent.class);
        assertThat(layoutState.getMountableOutputAt(3).getNodeInfo()).isNull();
        assertThat(MountItem.isTouchableDisabled(layoutState.getMountableOutputAt(3).getFlags())).isTrue();
        assertThat(layoutState.getMountableOutputAt(4).getComponent()).isInstanceOf(TestViewComponent.class);
        assertThat(layoutState.getMountableOutputAt(4).getNodeInfo()).isNull();
        assertThat(layoutState.getMountableOutputAt(5).getComponent()).isInstanceOf(TestDrawableComponent.class);
        assertThat(layoutState.getMountableOutputAt(5).getNodeInfo()).isNull();
        assertThat(MountItem.isTouchableDisabled(layoutState.getMountableOutputAt(5).getFlags())).isFalse();
    }

    @Test
    public void testLayoutOutputsForAccessibilityEnabled() {
        enableAccessibility();
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Row.create(c).alignItems(CENTER).paddingDip(ALL, 10).contentDescription("This is root view").child(TestDrawableComponent.create(c).widthDip(30).heightDip(30)).child(TestDrawableComponent.create(c, true, true, true, true, false).flex(1).flexBasisDip(0).backgroundColor(RED).marginDip(HORIZONTAL, 10)).child(Row.create(c).alignItems(CENTER).paddingDip(ALL, 10).contentDescription("This is a container").child(TestDrawableComponent.create(c).widthDip(30).heightDip(30).contentDescription("This is an image")).child(TestDrawableComponent.create(c, true, true, true, true, false).flex(1).flexBasisDip(0).marginDip(HORIZONTAL, 10))).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(10);
        final long hostMarkerRoot = layoutState.getMountableOutputAt(1).getHostMarker();
        final long hostMarkerOne = layoutState.getMountableOutputAt(3).getHostMarker();
        final long hostMarkerTwo = layoutState.getMountableOutputAt(6).getHostMarker();
        final long hostMarkerThree = layoutState.getMountableOutputAt(7).getHostMarker();
        final long hostMarkerFour = layoutState.getMountableOutputAt(9).getHostMarker();
        assertThat(layoutState.getMountableOutputAt(1).getHostMarker()).isEqualTo(hostMarkerRoot);
        assertThat(layoutState.getMountableOutputAt(3).getHostMarker()).isEqualTo(hostMarkerOne);
        assertThat(layoutState.getMountableOutputAt(4).getHostMarker()).isEqualTo(hostMarkerOne);
        assertThat(layoutState.getMountableOutputAt(6).getHostMarker()).isEqualTo(hostMarkerTwo);
        assertThat(layoutState.getMountableOutputAt(7).getHostMarker()).isEqualTo(hostMarkerThree);
        assertThat(layoutState.getMountableOutputAt(9).getHostMarker()).isEqualTo(hostMarkerFour);
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 0))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 1)).isInstanceOf(TestDrawableComponent.class);
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 2))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 3)).isInstanceOf(DrawableComponent.class);
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 4)).isInstanceOf(TestDrawableComponent.class);
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 5))).isTrue();
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 6))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 7)).isInstanceOf(TestDrawableComponent.class);
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 8))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 9)).isInstanceOf(TestDrawableComponent.class);
    }

    @Test
    public void testLayoutOutputsWithImportantForAccessibility() {
        enableAccessibility();
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).contentDescription("This is root view").child(TestDrawableComponent.create(c).widthDip(30).heightDip(30)).child(TestDrawableComponent.create(c, true, true, true, true, false).flex(1).flexBasisDip(0).backgroundColor(RED).marginDip(HORIZONTAL, 10).importantForAccessibility(ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO)).child(Row.create(c).alignItems(CENTER).paddingDip(ALL, 10).importantForAccessibility(ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS).child(TestDrawableComponent.create(c).widthDip(30).heightDip(30).importantForAccessibility(ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES).contentDescription("This is an image"))).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(7);
        final long hostMarkerRoot = layoutState.getMountableOutputAt(1).getHostMarker();
        final long hostMarkerOne = layoutState.getMountableOutputAt(5).getHostMarker();
        final long hostMarkerTwo = layoutState.getMountableOutputAt(6).getHostMarker();
        assertThat(layoutState.getMountableOutputAt(1).getHostMarker()).isEqualTo(hostMarkerRoot);
        assertThat(layoutState.getMountableOutputAt(2).getHostMarker()).isEqualTo(hostMarkerRoot);
        assertThat(layoutState.getMountableOutputAt(3).getHostMarker()).isEqualTo(hostMarkerRoot);
        assertThat(layoutState.getMountableOutputAt(4).getHostMarker()).isEqualTo(hostMarkerRoot);
        assertThat(layoutState.getMountableOutputAt(5).getHostMarker()).isEqualTo(hostMarkerOne);
        assertThat(layoutState.getMountableOutputAt(6).getHostMarker()).isEqualTo(hostMarkerTwo);
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 0))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 1)).isInstanceOf(TestDrawableComponent.class);
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 2)).isInstanceOf(DrawableComponent.class);
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 3)).isInstanceOf(TestDrawableComponent.class);
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 4))).isTrue();
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 5))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 6)).isInstanceOf(TestDrawableComponent.class);
        assertThat(ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_AUTO).isEqualTo(layoutState.getMountableOutputAt(0).getImportantForAccessibility());
        assertThat(ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_AUTO).isEqualTo(layoutState.getMountableOutputAt(1).getImportantForAccessibility());
        assertThat(ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO).isEqualTo(layoutState.getMountableOutputAt(2).getImportantForAccessibility());
        assertThat(ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO).isEqualTo(layoutState.getMountableOutputAt(3).getImportantForAccessibility());
        assertThat(ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS).isEqualTo(layoutState.getMountableOutputAt(4).getImportantForAccessibility());
        assertThat(ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES).isEqualTo(layoutState.getMountableOutputAt(5).getImportantForAccessibility());
        assertThat(ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES).isEqualTo(layoutState.getMountableOutputAt(6).getImportantForAccessibility());
    }

    @Test
    public void testLayoutOutputsForClickHandlerAndViewTagsOnRoot() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).clickHandler(c.newEventHandler(1)).viewTags(new android.util.SparseArray()).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(2);
        final long hostMarkerZero = layoutState.getMountableOutputAt(0).getHostMarker();
        assertThat(layoutState.getMountableOutputAt(1).getHostMarker()).isEqualTo(hostMarkerZero);
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 0))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 1)).isInstanceOf(TestDrawableComponent.class);
        final NodeInfo nodeInfo = layoutState.getMountableOutputAt(0).getNodeInfo();
        assertThat(nodeInfo).isNotNull();
        assertThat(nodeInfo.getClickHandler()).isNotNull();
        assertThat(nodeInfo.getViewTags()).isNotNull();
    }

    @Test
    public void testLayoutOutputsForLongClickHandlerAndViewTagsOnRoot() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).longClickHandler(c.newEventHandler(1)).viewTags(new android.util.SparseArray()).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(2);
        final long hostMarkerZero = layoutState.getMountableOutputAt(0).getHostMarker();
        assertThat(layoutState.getMountableOutputAt(1).getHostMarker()).isEqualTo(hostMarkerZero);
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 0))).isTrue();
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 1)).isInstanceOf(TestDrawableComponent.class);
        final NodeInfo nodeInfo = layoutState.getMountableOutputAt(0).getNodeInfo();
        assertThat(nodeInfo).isNotNull();
        assertThat(nodeInfo.getLongClickHandler()).isNotNull();
        assertThat(nodeInfo.getViewTags()).isNotNull();
    }

    @Test
    public void testLayoutOutputsForForceWrappedComponent() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c).wrapInView()).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(3);
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 0)).isInstanceOf(HostComponent.class);
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 1)).isInstanceOf(HostComponent.class);
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 2)).isInstanceOf(TestDrawableComponent.class);
    }

    @Test
    public void testLayoutOutputForRootNestedTreeComponent() {
        final LayoutState layoutState = calculateLayoutState(application, TestSizeDependentComponent.create(new ComponentContext(application)).setFixSizes(true).setDelegate(false).build(), (-1), SizeSpec.makeSizeSpec(350, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY));
        // Check total layout outputs.
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(4);
        final Rect mountBounds = new Rect();
        // Check host.
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 0))).isTrue();
        layoutState.getMountableOutputAt(0).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(0, 0, 350, 200));
        assertThat(layoutState.getMountableOutputAt(0).getHostMarker()).isEqualTo(0);
        // Check NestedTree
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 1)).isInstanceOf(DrawableComponent.class);
        layoutState.getMountableOutputAt(1).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(5, 5, 55, 55));
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 2)).isInstanceOf(TestDrawableComponent.class);
        layoutState.getMountableOutputAt(2).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(5, 5, 55, 55));
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 3)).isInstanceOf(TestViewComponent.class);
        layoutState.getMountableOutputAt(3).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(8, 58, 342, 78));
    }

    @Test
    public void testLayoutOutputForDelegateNestedTreeComponentDelegate() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).paddingPx(ALL, 2).child(TestSizeDependentComponent.create(c).setFixSizes(true).setDelegate(true).marginPx(ALL, 11)).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(350, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY));
        // Check total layout outputs.
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(3);
        final Rect mountBounds = new Rect();
        // Check host.
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 0))).isTrue();
        layoutState.getMountableOutputAt(0).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(0, 0, 350, 200));
        // Check NestedTree
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 1)).isInstanceOf(DrawableComponent.class);
        layoutState.getMountableOutputAt(1).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(13, 13, 63, 63));
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 2)).isInstanceOf(TestDrawableComponent.class);
        layoutState.getMountableOutputAt(2).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(13, 13, 63, 63));
    }

    @Test
    public void testLayoutOutputForDelegateNestedTreeComponent() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).paddingPx(ALL, 2).child(TestSizeDependentComponent.create(c).setFixSizes(true).setDelegate(false).marginPx(ALL, 11)).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(350, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY));
        // Check total layout outputs.
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(4);
        final Rect mountBounds = new Rect();
        // Check host.
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 0))).isTrue();
        layoutState.getMountableOutputAt(0).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(0, 0, 350, 200));
        assertThat(layoutState.getMountableOutputAt(0).getHostMarker()).isEqualTo(0);
        // Check NestedTree
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 1)).isInstanceOf(DrawableComponent.class);
        layoutState.getMountableOutputAt(1).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(18, 18, 68, 68));
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 2)).isInstanceOf(TestDrawableComponent.class);
        layoutState.getMountableOutputAt(2).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(18, 18, 68, 68));
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 3)).isInstanceOf(TestViewComponent.class);
        layoutState.getMountableOutputAt(3).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(21, 71, 329, 91));
    }

    @Test
    public void testLayoutOutputForRootWithDelegateNestedTreeComponent() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return TestSizeDependentComponent.create(c).setFixSizes(true).setDelegate(false).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(350, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY));
        // Check total layout outputs.
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(4);
        final Rect mountBounds = new Rect();
        // Check host.
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 0))).isTrue();
        layoutState.getMountableOutputAt(0).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(0, 0, 350, 200));
        assertThat(layoutState.getMountableOutputAt(0).getHostMarker()).isEqualTo(0);
        // Check NestedTree
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 1)).isInstanceOf(DrawableComponent.class);
        layoutState.getMountableOutputAt(1).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(5, 5, 55, 55));
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 2)).isInstanceOf(TestDrawableComponent.class);
        layoutState.getMountableOutputAt(2).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(5, 5, 55, 55));
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 3)).isInstanceOf(TestViewComponent.class);
        layoutState.getMountableOutputAt(3).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(8, 58, 342, 78));
    }

    @Test
    public void testLayoutOutputRootWithPaddingOverridingDelegateNestedTreeComponent() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                final Component nestedTreeRootComponent = TestSizeDependentComponent.create(c).setFixSizes(true).setDelegate(false).build();
                return Wrapper.create(c).delegate(nestedTreeRootComponent).paddingPx(ALL, 10).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(350, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY));
        // Check total layout outputs.
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(4);
        final Rect mountBounds = new Rect();
        // Check host.
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 0))).isTrue();
        layoutState.getMountableOutputAt(0).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(0, 0, 350, 200));
        assertThat(layoutState.getMountableOutputAt(0).getHostMarker()).isEqualTo(0);
        // Check NestedTree
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 1)).isInstanceOf(DrawableComponent.class);
        layoutState.getMountableOutputAt(1).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(10, 10, 60, 60));
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 2)).isInstanceOf(TestDrawableComponent.class);
        layoutState.getMountableOutputAt(2).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(10, 10, 60, 60));
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 3)).isInstanceOf(TestViewComponent.class);
        layoutState.getMountableOutputAt(3).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(13, 63, 337, 83));
    }

    @Test
    public void testLayoutOutputForRootWithNullLayout() {
        final Component componentWithNullLayout = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return null;
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, componentWithNullLayout, (-1), SizeSpec.makeSizeSpec(350, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(0);
    }

    @Test
    public void testLayoutComponentForNestedTreeChildWithNullLayout() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).paddingPx(ALL, 2).child(new TestNullLayoutComponent()).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(350, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(1);
        final Rect mountBounds = new Rect();
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 0))).isTrue();
        layoutState.getMountableOutputAt(0).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(0, 0, 350, 200));
    }

    @Test
    public void testMeasure() {
        final int width = 50;
        final int height = 30;
        final ComponentContext c = new ComponentContext(application);
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c).measuredWidth(width).measuredHeight(height)).build();
            }
        };
        final InternalNode node = LayoutState.createAndMeasureTreeForComponent(c, component, SizeSpec.makeSizeSpec(width, SizeSpec.AT_MOST), SizeSpec.makeSizeSpec(height, SizeSpec.AT_MOST));
        assertThat(node.getWidth()).isEqualTo(width);
        assertThat(node.getHeight()).isEqualTo(height);
        assertThat(node.getChildCount()).isEqualTo(1);
        assertThat(getWidth()).isEqualTo(width);
        assertThat(getHeight()).isEqualTo(height);
    }

    @Test
    public void testLayoutOutputWithCachedLayoutSpec() {
        final ComponentContext c = new ComponentContext(application);
        final int widthSpecContainer = SizeSpec.makeSizeSpec(300, SizeSpec.EXACTLY);
        final int heightSpec = SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED);
        final int horizontalPadding = 20;
        final int widthMeasuredComponent = SizeSpec.makeSizeSpec((((SizeSpec.getSize(widthSpecContainer)) - horizontalPadding) - horizontalPadding), SizeSpec.EXACTLY);
        final Component componentSpy = Mockito.spy(TestLayoutComponent.create(c, 0, 0, true, true, true, false).build());
        final Size sizeOutput = new Size();
        componentSpy.measure(c, widthMeasuredComponent, heightSpec, sizeOutput);
        // Reset the checks for layout release and clearing that happen during measurement
        Mockito.reset(componentSpy);
        Mockito.doReturn(componentSpy).when(componentSpy).makeShallowCopy();
        // Check the cached measured component tree
        assertThat(componentSpy.getCachedLayout()).isNotNull();
        final InternalNode cachedLayout = componentSpy.getCachedLayout();
        assertThat(cachedLayout.getChildCount()).isEqualTo(1);
        assertThat(getRootComponent()).isInstanceOf(TestDrawableComponent.class);
        // Now embed the measured component in another container and calculate a layout.
        final Component rootContainer = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).paddingPx(HORIZONTAL, horizontalPadding).child(componentSpy).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, rootContainer, (-1), widthSpecContainer, heightSpec);
        Mockito.verify(componentSpy, Mockito.times(1)).makeShallowCopy();
        // Make sure we reused the cached layout.
        Mockito.verify(componentSpy, Mockito.times(1)).clearCachedLayout();
        // Check total layout outputs.
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(2);
        final Rect mountBounds = new Rect();
        // Check host.
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 0))).isTrue();
        layoutState.getMountableOutputAt(0).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(0, 0, 300, sizeOutput.height));
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 1)).isInstanceOf(TestDrawableComponent.class);
        layoutState.getMountableOutputAt(1).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(20, 0, 280, 0));
        Mockito.validateMockitoUsage();
    }

    @Test
    public void testLayoutOutputWithCachedLayoutSpecWithMeasure() {
        final ComponentContext c = new ComponentContext(application);
        final int widthSpecContainer = SizeSpec.makeSizeSpec(300, SizeSpec.EXACTLY);
        final int heightSpec = SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED);
        final int horizontalPadding = 20;
        final int widthMeasuredComponent = SizeSpec.makeSizeSpec((((SizeSpec.getSize(widthSpecContainer)) - horizontalPadding) - horizontalPadding), SizeSpec.EXACTLY);
        final Component sizeDependentComponentSpy = Mockito.spy(TestSizeDependentComponent.create(c).setFixSizes(false).setDelegate(false).build());
        final Size sizeOutput = new Size();
        sizeDependentComponentSpy.measure(c, widthMeasuredComponent, heightSpec, sizeOutput);
        // Reset the checks for layout release and clearing that happen during measurement
        Mockito.reset(sizeDependentComponentSpy);
        Mockito.doReturn(sizeDependentComponentSpy).when(sizeDependentComponentSpy).makeShallowCopy();
        // Check the cached measured component tree
        assertThat(sizeDependentComponentSpy.getCachedLayout()).isNotNull();
        final InternalNode cachedLayout = sizeDependentComponentSpy.getCachedLayout();
        assertThat(cachedLayout.getChildCount()).isEqualTo(2);
        assertThat(getRootComponent()).isInstanceOf(TestDrawableComponent.class);
        assertThat(getRootComponent()).isInstanceOf(TestViewComponent.class);
        // Now embed the measured component in another container and calculate a layout.
        final Component rootContainer = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).flexShrink(0).paddingPx(HORIZONTAL, horizontalPadding).child(sizeDependentComponentSpy).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, rootContainer, (-1), widthSpecContainer, heightSpec);
        Mockito.verify(sizeDependentComponentSpy, Mockito.times(1)).makeShallowCopy();
        // Make sure we reused the cached layout.
        Mockito.verify(sizeDependentComponentSpy, Mockito.times(1)).clearCachedLayout();
        // Check total layout outputs.
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(4);
        final Rect mountBounds = new Rect();
        // Check host.
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 0))).isTrue();
        layoutState.getMountableOutputAt(0).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(0, 0, 300, sizeOutput.height));
        // Check NestedTree
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 1)).isInstanceOf(DrawableComponent.class);
        layoutState.getMountableOutputAt(1).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(25, 5, 275, 5));
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 2)).isInstanceOf(TestDrawableComponent.class);
        layoutState.getMountableOutputAt(2).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(25, 5, 275, 5));
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 3)).isInstanceOf(TestViewComponent.class);
        layoutState.getMountableOutputAt(3).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(28, 8, 272, 8));
        Mockito.validateMockitoUsage();
    }

    @Test
    public void testLayoutOutputWithCachedLayoutSpecDelegate() {
        final ComponentContext c = new ComponentContext(application);
        final int widthSpecContainer = SizeSpec.makeSizeSpec(300, SizeSpec.EXACTLY);
        final int heightSpec = SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED);
        final int horizontalPadding = 20;
        final int widthMeasuredComponent = SizeSpec.makeSizeSpec((((SizeSpec.getSize(widthSpecContainer)) - horizontalPadding) - horizontalPadding), SizeSpec.EXACTLY);
        final Component componentSpy = Mockito.spy(TestLayoutComponent.create(c, 0, 0, true, true, true, true).build());
        final Size sizeOutput = new Size();
        componentSpy.measure(c, widthMeasuredComponent, heightSpec, sizeOutput);
        // Reset the checks for layout release and clearing that happen during measurement
        Mockito.reset(componentSpy);
        Mockito.doReturn(componentSpy).when(componentSpy).makeShallowCopy();
        // Check the cached measured component tree
        assertThat(componentSpy.getCachedLayout()).isNotNull();
        final InternalNode cachedLayout = componentSpy.getCachedLayout();
        assertThat(cachedLayout.getChildCount()).isEqualTo(0);
        assertThat(cachedLayout.getRootComponent()).isInstanceOf(TestDrawableComponent.class);
        // Now embed the measured component in another container and calculate a layout.
        final Component rootContainer = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).paddingPx(HORIZONTAL, horizontalPadding).child(componentSpy).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, rootContainer, (-1), widthSpecContainer, heightSpec);
        Mockito.verify(componentSpy, Mockito.times(1)).makeShallowCopy();
        // Make sure we reused the cached layout.
        Mockito.verify(componentSpy, Mockito.times(1)).clearCachedLayout();
        // Check total layout outputs.
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(2);
        final Rect mountBounds = new Rect();
        // Check host.
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 0))).isTrue();
        layoutState.getMountableOutputAt(0).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(0, 0, 300, sizeOutput.height));
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 1)).isInstanceOf(TestDrawableComponent.class);
        layoutState.getMountableOutputAt(1).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(20, 0, 280, 0));
        Mockito.validateMockitoUsage();
    }

    @Test
    public void testLayoutOutputWithCachedLayoutSpecWithMeasureDelegate() {
        final ComponentContext c = new ComponentContext(application);
        final int widthSpecContainer = SizeSpec.makeSizeSpec(300, SizeSpec.EXACTLY);
        final int heightSpec = SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED);
        final int horizontalPadding = 20;
        final int widthMeasuredComponent = SizeSpec.makeSizeSpec((((SizeSpec.getSize(widthSpecContainer)) - horizontalPadding) - horizontalPadding), SizeSpec.EXACTLY);
        final Component sizeDependentComponentSpy = Mockito.spy(TestSizeDependentComponent.create(c).setFixSizes(false).setDelegate(true).build());
        final Size sizeOutput = new Size();
        sizeDependentComponentSpy.measure(c, widthMeasuredComponent, heightSpec, sizeOutput);
        // Reset the checks for layout release and clearing that happen during measurement
        Mockito.reset(sizeDependentComponentSpy);
        Mockito.doReturn(sizeDependentComponentSpy).when(sizeDependentComponentSpy).makeShallowCopy();
        // Check the cached measured component tree
        assertThat(sizeDependentComponentSpy.getCachedLayout()).isNotNull();
        final InternalNode cachedLayout = sizeDependentComponentSpy.getCachedLayout();
        assertThat(cachedLayout.getChildCount()).isEqualTo(0);
        assertThat(cachedLayout.getRootComponent()).isInstanceOf(TestDrawableComponent.class);
        // Now embed the measured component in another container and calculate a layout.
        final Component rootContainer = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).paddingPx(HORIZONTAL, horizontalPadding).child(sizeDependentComponentSpy).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, rootContainer, (-1), widthSpecContainer, heightSpec);
        Mockito.verify(sizeDependentComponentSpy, Mockito.times(1)).makeShallowCopy();
        // Make sure we reused the cached layout.
        Mockito.verify(sizeDependentComponentSpy, Mockito.times(1)).clearCachedLayout();
        // Check total layout outputs.
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(3);
        final Rect mountBounds = new Rect();
        // Check host.
        assertThat(LayoutStateCalculateTest.isHostComponent(LayoutStateCalculateTest.getComponentAt(layoutState, 0))).isTrue();
        layoutState.getMountableOutputAt(0).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(0, 0, 300, sizeOutput.height));
        // Check NestedTree
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 1)).isInstanceOf(DrawableComponent.class);
        layoutState.getMountableOutputAt(1).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(20, 0, 280, 0));
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 2)).isInstanceOf(TestDrawableComponent.class);
        layoutState.getMountableOutputAt(2).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(20, 0, 280, 0));
        Mockito.validateMockitoUsage();
    }

    @Test
    public void testNestedTreeComponentWithDoubleMeasurementsDoesntThrow() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Row.create(c).alignItems(STRETCH).paddingPx(ALL, 2).child(TestSizeDependentComponent.create(c).setFixSizes(true).setDelegate(false).marginPx(ALL, 11)).child(TestDrawableComponent.create(c).heightPx(200).widthPx(200)).build();
            }
        };
        calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(350, EXACTLY), SizeSpec.makeSizeSpec(0, UNSPECIFIED));
        // Testing that is not throwing an exception.
    }

    @Test
    public void testLayoutOutputForRootNestedTreeComponentWithAspectRatio() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(TestSizeDependentComponent.create(c).widthPx(100).aspectRatio(1)).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED), SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED));
        final Rect mountBounds = new Rect();
        layoutState.getMountableOutputAt(0).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(0, 0, 100, 100));
    }

    @Test
    public void testLayoutOutputForRootNestedTreeComponentWithPercentParentSizeDefined() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).alignItems(FLEX_START).widthPx(100).heightPx(100).child(TestSizeDependentComponent.create(c).widthPercent(50).heightPercent(50).backgroundColor(-65536)).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED), SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED));
        final Rect mountBounds = new Rect();
        layoutState.getMountableOutputAt(0).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(0, 0, 100, 100));
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 1)).isInstanceOf(DrawableComponent.class);
        layoutState.getMountableOutputAt(1).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(0, 0, 50, 50));
    }

    @Test
    public void testLayoutOutputForRootNestedTreeComponentWithPercent() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).alignItems(FLEX_START).child(TestSizeDependentComponent.create(c).setFixSizes(true).widthPercent(50).heightPercent(50).backgroundColor(-65536)).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED), SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED));
        final Rect mountBounds = new Rect();
        layoutState.getMountableOutputAt(0).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(0, 0, 60, 86));
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 1)).isInstanceOf(DrawableComponent.class);
        layoutState.getMountableOutputAt(1).getMountBounds(mountBounds);
        assertThat(mountBounds).isEqualTo(new Rect(0, 0, 60, 86));
    }

    @Test
    public void testLayoutOutputForRootNestedTreeComponentWithPercentPadding() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).alignItems(FLEX_START).widthPx(50).heightPx(50).child(TestSizeDependentComponent.create(c).setFixSizes(true).paddingPercent(ALL, 10).backgroundColor(-65536)).build();
            }
        };
        final InternalNode root = LayoutState.createAndMeasureTreeForComponent(new ComponentContext(application), component, SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED), SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED));
        assertThat(root.getChildAt(0).getNestedTree().getPaddingLeft()).isEqualTo(5);
        assertThat(root.getChildAt(0).getNestedTree().getPaddingTop()).isEqualTo(5);
        assertThat(root.getChildAt(0).getNestedTree().getPaddingRight()).isEqualTo(5);
        assertThat(root.getChildAt(0).getNestedTree().getPaddingBottom()).isEqualTo(5);
    }

    @Test
    public void testLayoutOutputsForComponentWithBorderColorNoBorderWidth() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).border(Border.create(c).color(ALL, GREEN).build()).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        // No layout output generated related with borders
        // if borderColor is supplied but not borderWidth.
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(2);
    }

    @Test
    public void testLayoutOutputsForComponentWithBorderWidthNoBorderColor() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).border(Border.create(c).widthPx(ALL, 10).build()).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        // No layout output generated related with borders
        // if borderWidth supplied but not borderColor.
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(2);
    }

    @Test
    public void testLayoutOutputsForComponentWithBorderWidthAllAndBorderColor() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).border(Border.create(c).widthPx(ALL, 10).color(ALL, GREEN).build()).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(3);
        // Output at index 1 is BorderColorDrawable component.
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 2)).isInstanceOf(DrawableComponent.class);
    }

    @Test
    public void testLayoutOutputsForComponentWithBorderWidthTopAndBorderColor() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(TestDrawableComponent.create(c)).border(Border.create(c).widthPx(TOP, 10).color(TOP, GREEN).build()).build();
            }
        };
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(3);
        // Output at index 1 is BorderColorDrawable component.
        assertThat(LayoutStateCalculateTest.getComponentAt(layoutState, 2)).isInstanceOf(DrawableComponent.class);
    }

    @Test
    public void testWillRenderLayoutsOnce() {
        ComponentContext c = new ComponentContext(application);
        final Component componentSpy = Mockito.spy(TestLayoutComponent.create(c, 0, 0, true, true, true, false).build());
        Component.willRender(c, componentSpy);
        final InternalNode cachedLayout = componentSpy.getLayoutCreatedInWillRenderForTesting();
        assertThat(cachedLayout).isNotNull();
        calculateLayoutState(c.getAndroidContext(), componentSpy, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        assertThat(componentSpy.getLayoutCreatedInWillRenderForTesting()).isNull();
        Mockito.verify(componentSpy, Mockito.times(1)).updateInternalChildState(ArgumentMatchers.any(ComponentContext.class));
    }

    @Test
    public void testResolveLayoutUsesWillRenderResult() {
        ComponentContext c = new ComponentContext(application);
        final Component component = TestLayoutComponent.create(c, 0, 0, true, true, true, false).build();
        Component.willRender(c, component);
        final InternalNode cachedLayout = component.getLayoutCreatedInWillRenderForTesting();
        assertThat(cachedLayout).isNotNull();
        InternalNode result = c.resolveLayout(component);
        assertThat(result).isEqualTo(cachedLayout);
        assertThat(component.getLayoutCreatedInWillRenderForTesting()).isNull();
    }

    @Test
    public void testNewLayoutBuilderUsesWillRenderResult() {
        ComponentContext c = new ComponentContext(application);
        final Component component = TestLayoutComponent.create(c, 0, 0, true, true, true, false).build();
        Component.willRender(c, component);
        final InternalNode cachedLayout = component.getLayoutCreatedInWillRenderForTesting();
        assertThat(cachedLayout).isNotNull();
        InternalNode result = c.newLayoutBuilder(component, 0, 0);
        assertThat(result).isEqualTo(cachedLayout);
        assertThat(component.getLayoutCreatedInWillRenderForTesting()).isNull();
    }

    @Test
    public void testCreateLayoutUsesWillRenderResult() {
        ComponentContext c = new ComponentContext(application);
        final Component component = TestLayoutComponent.create(c, 0, 0, true, true, true, false).build();
        Component.willRender(c, component);
        final InternalNode cachedLayout = component.getLayoutCreatedInWillRenderForTesting();
        assertThat(cachedLayout).isNotNull();
        InternalNode result = component.createLayout(c, false);
        assertThat(result).isEqualTo(cachedLayout);
        assertThat(component.getLayoutCreatedInWillRenderForTesting()).isNull();
    }

    @Test
    public void testWillRenderLayoutsOnceInColumn() {
        ComponentContext c = new ComponentContext(application);
        final Component componentSpy = Mockito.spy(TestLayoutComponent.create(c, 0, 0, true, true, true, false).build());
        final Component root = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                Component.willRender(c, componentSpy);
                return Column.create(c).child(componentSpy).build();
            }
        };
        calculateLayoutState(c.getAndroidContext(), root, (-1), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(100, SizeSpec.EXACTLY));
        Mockito.verify(componentSpy, Mockito.times(1)).updateInternalChildState(ArgumentMatchers.any(ComponentContext.class));
    }

    @Test
    public void testWillRenderTwiceDoesNotReCreateLayout() {
        ComponentContext c = new ComponentContext(application);
        final Component component = TestLayoutComponent.create(c, 0, 0, true, true, true, false).build();
        Component.willRender(c, component);
        final InternalNode cachedLayout = component.getLayoutCreatedInWillRenderForTesting();
        assertThat(cachedLayout).isNotNull();
        assertThat(Component.willRender(c, component)).isTrue();
        assertThat(component.getLayoutCreatedInWillRenderForTesting()).isEqualTo(cachedLayout);
    }

    @Test
    public void testPhantomLayoutOutputForTransitionKey() {
        ComponentsConfiguration.createPhantomLayoutOutputsForTransitions = true;
        final String transitionKey = "column";
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(final ComponentContext c) {
                return Column.create(c).child(Column.create(c).transitionKey(transitionKey).transitionKeyType(GLOBAL).child(TestDrawableComponent.create(c))).build();
            }
        };
        final TransitionId transitionId = new TransitionId(Type.GLOBAL, transitionKey, null);
        final LayoutState layoutState = calculateLayoutState(application, component, (-1), SizeSpec.makeSizeSpec(100, EXACTLY), SizeSpec.makeSizeSpec(100, EXACTLY));
        assertThat(layoutState.getMountableOutputCount()).isEqualTo(3);
        final LayoutOutput outputWithTransitionKey = layoutState.getLayoutOutputsForTransitionId(transitionId).get(HOST);
        assertThat((((outputWithTransitionKey.getFlags()) & (LAYOUT_FLAG_PHANTOM)) != 0)).isTrue();
        ComponentsConfiguration.createPhantomLayoutOutputsForTransitions = false;
    }
}

