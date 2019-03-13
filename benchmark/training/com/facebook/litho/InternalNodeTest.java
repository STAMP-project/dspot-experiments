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


import ComponentsLogger.LogLevel.WARNING;
import YogaAlign.AUTO;
import android.graphics.drawable.ColorDrawable;
import androidx.core.view.ViewCompat;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.widget.Text;
import com.facebook.yoga.YogaNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;


@RunWith(ComponentsTestRunner.class)
public class InternalNodeTest {
    private static final int LIFECYCLE_TEST_ID = 1;

    private static class TestComponent extends Component {
        protected TestComponent() {
            super("TestComponent");
        }

        @Override
        public boolean isEquivalentTo(Component other) {
            return (this) == other;
        }

        @Override
        int getTypeId() {
            return InternalNodeTest.LIFECYCLE_TEST_ID;
        }
    }

    @Test
    public void testLayoutDirectionFlag() {
        final InternalNode node = InternalNodeTest.acquireInternalNode();
        node.layoutDirection(INHERIT);
        assertThat(InternalNodeTest.isFlagSet(node, "PFLAG_LAYOUT_DIRECTION_IS_SET")).isTrue();
        InternalNodeTest.clearFlag(node, "PFLAG_LAYOUT_DIRECTION_IS_SET");
        InternalNodeTest.assertEmptyFlags(node);
    }

    @Test
    public void testAlignSelfFlag() {
        final InternalNode node = InternalNodeTest.acquireInternalNode();
        node.alignSelf(STRETCH);
        assertThat(InternalNodeTest.isFlagSet(node, "PFLAG_ALIGN_SELF_IS_SET")).isTrue();
        InternalNodeTest.clearFlag(node, "PFLAG_ALIGN_SELF_IS_SET");
        InternalNodeTest.assertEmptyFlags(node);
    }

    @Test
    public void testPositionTypeFlag() {
        final InternalNode node = InternalNodeTest.acquireInternalNode();
        node.positionType(ABSOLUTE);
        assertThat(InternalNodeTest.isFlagSet(node, "PFLAG_POSITION_TYPE_IS_SET")).isTrue();
        InternalNodeTest.clearFlag(node, "PFLAG_POSITION_TYPE_IS_SET");
        InternalNodeTest.assertEmptyFlags(node);
    }

    @Test
    public void testFlexFlag() {
        final InternalNode node = InternalNodeTest.acquireInternalNode();
        node.flex(1.5F);
        assertThat(InternalNodeTest.isFlagSet(node, "PFLAG_FLEX_IS_SET")).isTrue();
        InternalNodeTest.clearFlag(node, "PFLAG_FLEX_IS_SET");
        InternalNodeTest.assertEmptyFlags(node);
    }

    @Test
    public void testFlexGrowFlag() {
        final InternalNode node = InternalNodeTest.acquireInternalNode();
        node.flexGrow(1.5F);
        assertThat(InternalNodeTest.isFlagSet(node, "PFLAG_FLEX_GROW_IS_SET")).isTrue();
        InternalNodeTest.clearFlag(node, "PFLAG_FLEX_GROW_IS_SET");
        InternalNodeTest.assertEmptyFlags(node);
    }

    @Test
    public void testFlexShrinkFlag() {
        final InternalNode node = InternalNodeTest.acquireInternalNode();
        node.flexShrink(1.5F);
        assertThat(InternalNodeTest.isFlagSet(node, "PFLAG_FLEX_SHRINK_IS_SET")).isTrue();
        InternalNodeTest.clearFlag(node, "PFLAG_FLEX_SHRINK_IS_SET");
        InternalNodeTest.assertEmptyFlags(node);
    }

    @Test
    public void testFlexBasisFlag() {
        final InternalNode node = InternalNodeTest.acquireInternalNode();
        node.flexBasisPx(1);
        assertThat(InternalNodeTest.isFlagSet(node, "PFLAG_FLEX_BASIS_IS_SET")).isTrue();
        InternalNodeTest.clearFlag(node, "PFLAG_FLEX_BASIS_IS_SET");
        InternalNodeTest.assertEmptyFlags(node);
    }

    @Test
    public void testImportantForAccessibilityFlag() {
        final InternalNode node = InternalNodeTest.acquireInternalNode();
        node.importantForAccessibility(ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_AUTO);
        assertThat(InternalNodeTest.isFlagSet(node, "PFLAG_IMPORTANT_FOR_ACCESSIBILITY_IS_SET")).isTrue();
        InternalNodeTest.clearFlag(node, "PFLAG_IMPORTANT_FOR_ACCESSIBILITY_IS_SET");
        InternalNodeTest.assertEmptyFlags(node);
    }

    @Test
    public void testDuplicateParentStateFlag() {
        final InternalNode node = InternalNodeTest.acquireInternalNode();
        node.duplicateParentState(false);
        assertThat(InternalNodeTest.isFlagSet(node, "PFLAG_DUPLICATE_PARENT_STATE_IS_SET")).isTrue();
        InternalNodeTest.clearFlag(node, "PFLAG_DUPLICATE_PARENT_STATE_IS_SET");
        InternalNodeTest.assertEmptyFlags(node);
    }

    @Test
    public void testMarginFlag() {
        final InternalNode node = InternalNodeTest.acquireInternalNode();
        node.marginPx(ALL, 3);
        assertThat(InternalNodeTest.isFlagSet(node, "PFLAG_MARGIN_IS_SET")).isTrue();
        InternalNodeTest.clearFlag(node, "PFLAG_MARGIN_IS_SET");
        InternalNodeTest.assertEmptyFlags(node);
    }

    @Test
    public void testPaddingFlag() {
        final InternalNode node = InternalNodeTest.acquireInternalNode();
        node.paddingPx(ALL, 3);
        assertThat(InternalNodeTest.isFlagSet(node, "PFLAG_PADDING_IS_SET")).isTrue();
        InternalNodeTest.clearFlag(node, "PFLAG_PADDING_IS_SET");
        InternalNodeTest.assertEmptyFlags(node);
    }

    @Test
    public void testPositionFlag() {
        final InternalNode node = InternalNodeTest.acquireInternalNode();
        node.positionPx(ALL, 3);
        assertThat(InternalNodeTest.isFlagSet(node, "PFLAG_POSITION_IS_SET")).isTrue();
        InternalNodeTest.clearFlag(node, "PFLAG_POSITION_IS_SET");
        InternalNodeTest.assertEmptyFlags(node);
    }

    @Test
    public void testWidthFlag() {
        final InternalNode node = InternalNodeTest.acquireInternalNode();
        node.widthPx(4);
        assertThat(InternalNodeTest.isFlagSet(node, "PFLAG_WIDTH_IS_SET")).isTrue();
        InternalNodeTest.clearFlag(node, "PFLAG_WIDTH_IS_SET");
        InternalNodeTest.assertEmptyFlags(node);
    }

    @Test
    public void testMinWidthFlag() {
        final InternalNode node = InternalNodeTest.acquireInternalNode();
        node.minWidthPx(4);
        assertThat(InternalNodeTest.isFlagSet(node, "PFLAG_MIN_WIDTH_IS_SET")).isTrue();
        InternalNodeTest.clearFlag(node, "PFLAG_MIN_WIDTH_IS_SET");
        InternalNodeTest.assertEmptyFlags(node);
    }

    @Test
    public void testMaxWidthFlag() {
        final InternalNode node = InternalNodeTest.acquireInternalNode();
        node.maxWidthPx(4);
        assertThat(InternalNodeTest.isFlagSet(node, "PFLAG_MAX_WIDTH_IS_SET")).isTrue();
        InternalNodeTest.clearFlag(node, "PFLAG_MAX_WIDTH_IS_SET");
        InternalNodeTest.assertEmptyFlags(node);
    }

    @Test
    public void testHeightFlag() {
        final InternalNode node = InternalNodeTest.acquireInternalNode();
        node.heightPx(4);
        assertThat(InternalNodeTest.isFlagSet(node, "PFLAG_HEIGHT_IS_SET")).isTrue();
        InternalNodeTest.clearFlag(node, "PFLAG_HEIGHT_IS_SET");
        InternalNodeTest.assertEmptyFlags(node);
    }

    @Test
    public void testMinHeightFlag() {
        final InternalNode node = InternalNodeTest.acquireInternalNode();
        node.minHeightPx(4);
        assertThat(InternalNodeTest.isFlagSet(node, "PFLAG_MIN_HEIGHT_IS_SET")).isTrue();
        InternalNodeTest.clearFlag(node, "PFLAG_MIN_HEIGHT_IS_SET");
        InternalNodeTest.assertEmptyFlags(node);
    }

    @Test
    public void testMaxHeightFlag() {
        final InternalNode node = InternalNodeTest.acquireInternalNode();
        node.maxHeightPx(4);
        assertThat(InternalNodeTest.isFlagSet(node, "PFLAG_MAX_HEIGHT_IS_SET")).isTrue();
        InternalNodeTest.clearFlag(node, "PFLAG_MAX_HEIGHT_IS_SET");
        InternalNodeTest.assertEmptyFlags(node);
    }

    @Test
    public void testBackgroundFlag() {
        final InternalNode node = InternalNodeTest.acquireInternalNode();
        node.backgroundColor(-65536);
        assertThat(InternalNodeTest.isFlagSet(node, "PFLAG_BACKGROUND_IS_SET")).isTrue();
        InternalNodeTest.clearFlag(node, "PFLAG_BACKGROUND_IS_SET");
        InternalNodeTest.assertEmptyFlags(node);
    }

    @Test
    public void testForegroundFlag() {
        final InternalNode node = InternalNodeTest.acquireInternalNode();
        node.foregroundColor(-65536);
        assertThat(InternalNodeTest.isFlagSet(node, "PFLAG_FOREGROUND_IS_SET")).isTrue();
        InternalNodeTest.clearFlag(node, "PFLAG_FOREGROUND_IS_SET");
        InternalNodeTest.assertEmptyFlags(node);
    }

    @Test
    public void testAspectRatioFlag() {
        final InternalNode node = InternalNodeTest.acquireInternalNode();
        node.aspectRatio(1);
        assertThat(InternalNodeTest.isFlagSet(node, "PFLAG_ASPECT_RATIO_IS_SET")).isTrue();
        InternalNodeTest.clearFlag(node, "PFLAG_ASPECT_RATIO_IS_SET");
        InternalNodeTest.assertEmptyFlags(node);
    }

    @Test
    public void testTransitionKeyFlag() {
        final InternalNode node = InternalNodeTest.acquireInternalNode();
        node.transitionKey("key");
        assertThat(InternalNodeTest.isFlagSet(node, "PFLAG_TRANSITION_KEY_IS_SET")).isTrue();
        InternalNodeTest.clearFlag(node, "PFLAG_TRANSITION_KEY_IS_SET");
        InternalNodeTest.assertEmptyFlags(node);
    }

    @Test
    public void setNestedTreeDoesntTransferLayoutDirectionIfExplicitlySetOnNestedNode() {
        InternalNode holderNode = InternalNodeTest.acquireInternalNode();
        InternalNode nestedTree = InternalNodeTest.acquireInternalNode();
        nestedTree.layoutDirection(RTL);
        holderNode.calculateLayout();
        holderNode.setNestedTree(nestedTree);
        assertThat(InternalNodeTest.isFlagSet(holderNode, "PFLAG_LAYOUT_DIRECTION_IS_SET")).isFalse();
        assertThat(holderNode.getStyleDirection()).isEqualTo(INHERIT);
        assertThat(nestedTree.getStyleDirection()).isEqualTo(RTL);
    }

    @Test
    public void testCopyIntoTrasferLayoutDirectionIfNotSetOnTheHolderOrOnTheNestedTree() {
        InternalNode holderNode = InternalNodeTest.acquireInternalNode();
        InternalNode nestedTree = InternalNodeTest.acquireInternalNode();
        holderNode.calculateLayout();
        holderNode.copyInto(nestedTree);
        assertThat(InternalNodeTest.isFlagSet(holderNode, "PFLAG_LAYOUT_DIRECTION_IS_SET")).isFalse();
        assertThat(InternalNodeTest.isFlagSet(nestedTree, "PFLAG_LAYOUT_DIRECTION_IS_SET")).isTrue();
    }

    @Test
    public void testCopyIntoNestedTreeTransferLayoutDirectionIfExplicitlySetOnHolderNode() {
        InternalNode holderNode = InternalNodeTest.acquireInternalNode();
        InternalNode nestedTree = InternalNodeTest.acquireInternalNode();
        holderNode.layoutDirection(RTL);
        holderNode.calculateLayout();
        holderNode.copyInto(nestedTree);
        assertThat(nestedTree.getStyleDirection()).isEqualTo(RTL);
    }

    @Test
    public void testCopyIntoNodeSetFlags() {
        InternalNode orig = InternalNodeTest.acquireInternalNode();
        InternalNode dest = InternalNodeTest.acquireInternalNode();
        orig.importantForAccessibility(0);
        orig.duplicateParentState(true);
        orig.background(new ColorDrawable());
        orig.foreground(null);
        orig.visibleHandler(null);
        orig.focusedHandler(null);
        orig.fullImpressionHandler(null);
        orig.invisibleHandler(null);
        orig.unfocusedHandler(null);
        orig.visibilityChangedHandler(null);
        orig.copyInto(dest);
        assertThat(InternalNodeTest.isFlagSet(dest, "PFLAG_IMPORTANT_FOR_ACCESSIBILITY_IS_SET")).isTrue();
        assertThat(InternalNodeTest.isFlagSet(dest, "PFLAG_DUPLICATE_PARENT_STATE_IS_SET")).isTrue();
        assertThat(InternalNodeTest.isFlagSet(dest, "PFLAG_BACKGROUND_IS_SET")).isTrue();
        assertThat(InternalNodeTest.isFlagSet(dest, "PFLAG_FOREGROUND_IS_SET")).isTrue();
        assertThat(InternalNodeTest.isFlagSet(dest, "PFLAG_VISIBLE_HANDLER_IS_SET")).isTrue();
        assertThat(InternalNodeTest.isFlagSet(dest, "PFLAG_FOCUSED_HANDLER_IS_SET")).isTrue();
        assertThat(InternalNodeTest.isFlagSet(dest, "PFLAG_FULL_IMPRESSION_HANDLER_IS_SET")).isTrue();
        assertThat(InternalNodeTest.isFlagSet(dest, "PFLAG_INVISIBLE_HANDLER_IS_SET")).isTrue();
        assertThat(InternalNodeTest.isFlagSet(dest, "PFLAG_UNFOCUSED_HANDLER_IS_SET")).isTrue();
        assertThat(InternalNodeTest.isFlagSet(dest, "PFLAG_VISIBLE_RECT_CHANGED_HANDLER_IS_SET")).isTrue();
    }

    @Test
    public void testPaddingIsSetFromDrawable() {
        YogaNode yogaNode = Mockito.mock(YogaNode.class);
        InternalNode node = new DefaultInternalNode(new ComponentContext(RuntimeEnvironment.application), yogaNode);
        node.backgroundRes(background_with_padding);
        assertThat(InternalNodeTest.isFlagSet(node, "PFLAG_PADDING_IS_SET")).isTrue();
        Mockito.verify(yogaNode).setPadding(LEFT, 48);
        Mockito.verify(yogaNode).setPadding(TOP, 0);
        Mockito.verify(yogaNode).setPadding(RIGHT, 0);
        Mockito.verify(yogaNode).setPadding(BOTTOM, 0);
    }

    @Test
    public void testPaddingIsNotSetFromDrawable() {
        InternalNode node = InternalNodeTest.acquireInternalNode();
        node.backgroundRes(background_without_padding);
        assertThat(InternalNodeTest.isFlagSet(node, "PFLAG_PADDING_IS_SET")).isFalse();
    }

    @Test
    public void testComponentCreateAndRetrieveCachedLayout() {
        final ComponentContext c = new ComponentContext(application);
        final int unspecifiedSizeSpec = SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED);
        final int exactSizeSpec = SizeSpec.makeSizeSpec(50, SizeSpec.EXACTLY);
        final Component textComponent = Text.create(c).textSizePx(16).text("test").build();
        final Size textSize = new Size();
        textComponent.measure(c, exactSizeSpec, unspecifiedSizeSpec, textSize);
        assertThat(textComponent.getCachedLayout()).isNotNull();
        InternalNode cachedLayout = textComponent.getCachedLayout();
        assertThat(cachedLayout).isNotNull();
        assertThat(cachedLayout.getLastWidthSpec()).isEqualTo(exactSizeSpec);
        assertThat(cachedLayout.getLastHeightSpec()).isEqualTo(unspecifiedSizeSpec);
        textComponent.clearCachedLayout();
        assertThat(textComponent.getCachedLayout()).isNull();
    }

    @Test
    public void testContextSpecificComponentAssertionPasses() {
        InternalNodeUtils.assertContextSpecificStyleNotSet(InternalNodeTest.acquireInternalNode());
    }

    @Test
    public void testContextSpecificComponentAssertionFailFormatting() {
        final ComponentsLogger componentsLogger = Mockito.mock(ComponentsLogger.class);
        final PerfEvent perfEvent = Mockito.mock(PerfEvent.class);
        Mockito.when(componentsLogger.newPerformanceEvent(ArgumentMatchers.anyInt())).thenReturn(perfEvent);
        InternalNode node = InternalNodeTest.acquireInternalNodeWithLogger(componentsLogger);
        node.alignSelf(AUTO);
        node.flex(1.0F);
        InternalNodeUtils.assertContextSpecificStyleNotSet(node);
        Mockito.verify(componentsLogger).emitMessage(WARNING, "You should not set alignSelf, flex to a root layout in Column");
    }
}

