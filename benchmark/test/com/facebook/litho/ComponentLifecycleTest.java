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


import MountType.DRAWABLE;
import com.facebook.infer.annotation.OkToExtend;
import com.facebook.litho.ComponentLifecycle.MountType;
import com.facebook.litho.config.ComponentsConfiguration;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.yoga.YogaMeasureFunction;
import com.facebook.yoga.YogaMeasureOutput;
import com.facebook.yoga.YogaNode;
import junit.framework.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;


/**
 * Tests {@link ComponentLifecycle}
 */
@PrepareForTest({ DefaultInternalNode.class, DiffNode.class, LayoutState.class })
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "androidx.*", "android.*" })
@RunWith(ComponentsTestRunner.class)
public class ComponentLifecycleTest {
    @Rule
    public PowerMockRule mPowerMockRule = new PowerMockRule();

    private static final int A_HEIGHT = 11;

    private static final int A_WIDTH = 12;

    private int mNestedTreeWidthSpec;

    private int mNestedTreeHeightSpec;

    private DefaultInternalNode mNode;

    private YogaNode mYogaNode;

    private DiffNode mDiffNode;

    private ComponentContext mContext;

    private boolean mPreviousOnErrorConfig;

    @Test
    public void testCreateLayoutWithNullComponentWithLayoutSpecCannotMeasure() {
        Component component = setUpSpyLayoutSpecWithNullLayout();
        component.createLayout(mContext, false);
        Mockito.verify(component).onCreateLayout(mContext);
        Mockito.verify(component, Mockito.never()).onPrepare(mContext);
    }

    @Test
    public void testCreateLayoutWithNullComponentWithLayoutSpecCanMeasure() {
        Component component = setUpSpyLayoutSpecWithNullLayout();
        component.createLayout(mContext, false);
        Mockito.verify(component).onCreateLayout(mContext);
        Mockito.verify(component, Mockito.never()).onPrepare(mContext);
    }

    @Test
    public void testCreateLayoutWithNullComponentWithMountSpecCannotMeasure() {
        Component component = setUpSpyLayoutSpecWithNullLayout();
        component.createLayout(mContext, false);
        Mockito.verify(component).onCreateLayout(mContext);
        Mockito.verify(component, Mockito.never()).onPrepare(mContext);
    }

    @Test
    public void testCreateLayoutWithNullComponentWithMountSpecCanMeasure() {
        Component component = setUpSpyLayoutSpecWithNullLayout();
        component.createLayout(mContext, false);
        Mockito.verify(component).onCreateLayout(mContext);
        Mockito.verify(component, Mockito.never()).onPrepare(mContext);
    }

    @Test
    public void testCreateLayoutAndResolveNestedTreeWithMountSpecCannotMeasure() {
        Component component = /* isMountSpec */
        /* canMeasure */
        setUpSpyComponentForCreateLayout(true, false);
        component.createLayout(mContext, true);
        Mockito.verify(component).onCreateLayout(mContext);
        Mockito.verify(mNode).appendComponent(component);
        Mockito.verify(mNode, Mockito.never()).setMeasureFunction(ArgumentMatchers.any(YogaMeasureFunction.class));
        Mockito.verify(component).onPrepare(mContext);
    }

    @Test
    public void testCreateLayoutAndDontResolveNestedTreeWithMountSpecCannotMeasure() {
        Component component = /* isMountSpec */
        /* canMeasure */
        setUpSpyComponentForCreateLayout(true, false);
        component.createLayout(mContext, false);
        Mockito.verify(component).onCreateLayout(mContext);
        Mockito.verify(mNode).appendComponent(component);
        Mockito.verify(mNode, Mockito.never()).setMeasureFunction(ArgumentMatchers.any(YogaMeasureFunction.class));
        Mockito.verify(component).onPrepare(mContext);
    }

    @Test
    public void testCreateLayoutAndResolveNestedTreeWithMountSpecCanMeasure() {
        Component component = /* isMountSpec */
        /* canMeasure */
        setUpSpyComponentForCreateLayout(true, true);
        component.createLayout(mContext, true);
        Mockito.verify(component).onCreateLayout(mContext);
        Mockito.verify(mNode).appendComponent(component);
        Mockito.verify(mNode).setMeasureFunction(ArgumentMatchers.any(YogaMeasureFunction.class));
        Mockito.verify(component).onPrepare(mContext);
    }

    @Test
    public void testCreateLayoutAndDontResolveNestedTreeWithMountSpecCanMeasure() {
        Component component = /* isMountSpec */
        /* canMeasure */
        setUpSpyComponentForCreateLayout(true, true);
        component.createLayout(mContext, false);
        Mockito.verify(component).onCreateLayout(mContext);
        Mockito.verify(mNode).appendComponent(component);
        Mockito.verify(mNode).setMeasureFunction(ArgumentMatchers.any(YogaMeasureFunction.class));
        Mockito.verify(component).onPrepare(mContext);
    }

    @Test
    public void testCreateLayoutAndResolveNestedTreeWithLayoutSpecCannotMeasure() {
        Component component = /* isMountSpec */
        /* canMeasure */
        setUpSpyComponentForCreateLayout(false, false);
        component.createLayout(mContext, true);
        Mockito.verify(component).onCreateLayout(mContext);
        Mockito.verify(mNode).appendComponent(component);
        Mockito.verify(mNode, Mockito.never()).setMeasureFunction(ArgumentMatchers.any(YogaMeasureFunction.class));
        Mockito.verify(component).onPrepare(mContext);
    }

    @Test
    public void testCreateLayoutAndDontResolveNestedTreeWithLayoutSpecCannotMeasure() {
        Component component = /* isMountSpec */
        /* canMeasure */
        setUpSpyComponentForCreateLayout(false, false);
        component.createLayout(mContext, false);
        Mockito.verify(component).onCreateLayout(mContext);
        Mockito.verify(mNode).appendComponent(component);
        Mockito.verify(mNode, Mockito.never()).setMeasureFunction(ArgumentMatchers.any(YogaMeasureFunction.class));
        Mockito.verify(component).onPrepare(mContext);
    }

    @Test
    public void testCreateLayoutAndResolveNestedTreeWithLayoutSpecCanMeasure() {
        Component component = /* isMountSpec */
        /* canMeasure */
        setUpSpyComponentForCreateLayout(false, true);
        mContext.setWidthSpec(mNestedTreeWidthSpec);
        mContext.setHeightSpec(mNestedTreeHeightSpec);
        component.createLayout(mContext, true);
        Mockito.verify(component).onCreateLayoutWithSizeSpec(mContext, mNestedTreeWidthSpec, mNestedTreeHeightSpec);
        Mockito.verify(mNode).appendComponent(component);
        Mockito.verify(mNode, Mockito.never()).setMeasureFunction(ArgumentMatchers.any(YogaMeasureFunction.class));
        Mockito.verify(component).onPrepare(mContext);
    }

    @Test
    public void testCreateLayoutAndDontResolveNestedTreeWithLayoutSpecCanMeasure() {
        Component component = /* isMountSpec */
        /* canMeasure */
        setUpSpyComponentForCreateLayout(false, true);
        component.createLayout(mContext, false);
        PowerMockito.verifyStatic();
        // Calling here to verify static call.
        DefaultInternalNode.createInternalNode(mContext);
        Mockito.verify(component, Mockito.never()).onCreateLayout(ArgumentMatchers.any(ComponentContext.class));
        Mockito.verify(component, Mockito.never()).onCreateLayoutWithSizeSpec(ArgumentMatchers.any(ComponentContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.verify(mNode).appendComponent(component);
        Mockito.verify(mNode).setMeasureFunction(ArgumentMatchers.any(YogaMeasureFunction.class));
        Mockito.verify(component, Mockito.never()).onPrepare(ArgumentMatchers.any(ComponentContext.class));
    }

    @Test
    public void testOnShouldCreateLayoutWithNewSizeSpec_FirstCall() {
        ComponentsConfiguration.isNestedTreeResolutionExperimentEnabled = true;
        ComponentsConfiguration.enableShouldCreateLayoutWithNewSizeSpec = true;
        Component component;
        component = new ComponentLifecycleTest.SpyComponentBuilder().setNode(mNode).canMeasure(true).isMountSpec(false).hasState(true).isLayoutSpecWithSizeSpecCheck(true).build(mContext);
        component.createLayout(mContext, true);
        // onShouldCreateLayoutWithNewSizeSpec should not be called the first time
        Mockito.verify(component, Mockito.never()).onShouldCreateLayoutWithNewSizeSpec(ArgumentMatchers.any(ComponentContext.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.verify(component).onCreateLayoutWithSizeSpec(mContext, mContext.getWidthSpec(), mContext.getHeightSpec());
        ComponentsConfiguration.enableShouldCreateLayoutWithNewSizeSpec = false;
        ComponentsConfiguration.isNestedTreeResolutionExperimentEnabled = false;
    }

    @Test
    public void testOnShouldCreateLayoutWithNewSizeSpec_shouldUseCache() {
        ComponentsConfiguration.isNestedTreeResolutionExperimentEnabled = true;
        ComponentsConfiguration.enableShouldCreateLayoutWithNewSizeSpec = true;
        Component component;
        InternalNode holder = mock(InternalNode.class);
        InternalNode resolved = mock(InternalNode.class);
        component = new ComponentLifecycleTest.SpyComponentBuilder().setNode(mNode).canMeasure(true).isMountSpec(false).isLayoutSpecWithSizeSpecCheck(true).hasState(true).build(mContext);
        Mockito.when(holder.getRootComponent()).thenReturn(component);
        Mockito.when(LayoutState.resolveNestedTree(mContext, holder, 100, 100)).thenCallRealMethod();
        Mockito.when(LayoutState.createAndMeasureTreeForComponent(mContext, component, holder, 100, 100, null)).thenReturn(resolved);
        // call resolve nested tree 1st time
        InternalNode result = LayoutState.resolveNestedTree(mContext, holder, 100, 100);
        PowerMockito.verifyStatic();
        // it should call create and measure
        LayoutState.createAndMeasureTreeForComponent(mContext, component, holder, 100, 100, null);
        // should return nested tree next time
        Mockito.when(holder.getNestedTree()).thenReturn(result);
        // should use previous layout in next call
        doReturn(true).when(component).canUsePreviousLayout(ArgumentMatchers.any(ComponentContext.class));
        // call resolve nested tree 1st time
        LayoutState.resolveNestedTree(mContext, holder, 100, 100);
        // no new invocation of create
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        LayoutState.createAndMeasureTreeForComponent(mContext, component, holder, 100, 100, null);
        // should only measure
        PowerMockito.verifyStatic(VerificationModeFactory.times(1));
        LayoutState.remeasureTree(resolved, 100, 100);
        ComponentsConfiguration.enableShouldCreateLayoutWithNewSizeSpec = false;
        ComponentsConfiguration.isNestedTreeResolutionExperimentEnabled = false;
    }

    @Test
    public void testOnShouldCreateLayoutWithNewSizeSpec_shouldNotUseCache() {
        ComponentsConfiguration.isNestedTreeResolutionExperimentEnabled = true;
        ComponentsConfiguration.enableShouldCreateLayoutWithNewSizeSpec = true;
        Component component;
        InternalNode holder = mock(InternalNode.class);
        InternalNode resolved = mock(InternalNode.class);
        component = new ComponentLifecycleTest.SpyComponentBuilder().setNode(mNode).canMeasure(true).isMountSpec(false).isLayoutSpecWithSizeSpecCheck(true).hasState(true).build(mContext);
        Mockito.when(holder.getRootComponent()).thenReturn(component);
        Mockito.when(LayoutState.resolveNestedTree(mContext, holder, 100, 100)).thenCallRealMethod();
        Mockito.when(LayoutState.createAndMeasureTreeForComponent(mContext, component, holder, 100, 100, null)).thenReturn(resolved);
        // call resolve nested tree 1st time
        InternalNode result = LayoutState.resolveNestedTree(mContext, holder, 100, 100);
        PowerMockito.verifyStatic();
        // it should call create and measure
        LayoutState.createAndMeasureTreeForComponent(mContext, component, holder, 100, 100, null);
        // should return nested tree next time
        Mockito.when(holder.getNestedTree()).thenReturn(result);
        // should use previous layout in next call
        doReturn(false).when(component).canUsePreviousLayout(ArgumentMatchers.any(ComponentContext.class));
        // call resolve nested tree 1st time
        LayoutState.resolveNestedTree(mContext, holder, 100, 100);
        // a new invocation of create
        PowerMockito.verifyStatic(VerificationModeFactory.times(2));
        LayoutState.createAndMeasureTreeForComponent(mContext, component, holder, 100, 100, null);
        ComponentsConfiguration.enableShouldCreateLayoutWithNewSizeSpec = false;
        ComponentsConfiguration.isNestedTreeResolutionExperimentEnabled = false;
    }

    @Test
    public void testOnMeasureNotOverriden() {
        Component component = setUpSpyComponentForCreateLayout(true, true);
        YogaMeasureFunction measureFunction = getMeasureFunction(component);
        try {
            measureFunction.measure(mYogaNode, 0, EXACTLY, 0, EXACTLY);
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isExactlyInstanceOf(IllegalStateException.class);
            assertThat(e.getMessage()).contains("canMeasure()");
        }
    }

    @Test
    public void testMountSpecYogaMeasureOutputNotSet() {
        Component component = new ComponentLifecycleTest.TestMountSpecWithEmptyOnMeasure(mNode);
        YogaMeasureFunction measureFunction = getMeasureFunction(component);
        try {
            measureFunction.measure(mYogaNode, 0, EXACTLY, 0, EXACTLY);
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isExactlyInstanceOf(IllegalStateException.class);
            assertThat(e.getMessage()).contains("MeasureOutput not set");
        }
    }

    @Test
    public void testMountSpecYogaMeasureOutputSet() {
        Component component = new ComponentLifecycleTest.TestMountSpecSettingSizesInOnMeasure(mNode);
        YogaMeasureFunction measureFunction = getMeasureFunction(component);
        long output = measureFunction.measure(mYogaNode, 0, EXACTLY, 0, EXACTLY);
        assertThat(YogaMeasureOutput.getWidth(output)).isEqualTo(ComponentLifecycleTest.A_WIDTH);
        assertThat(YogaMeasureOutput.getHeight(output)).isEqualTo(ComponentLifecycleTest.A_HEIGHT);
    }

    @Test
    public void testLayoutSpecMeasureResolveNestedTree() {
        Component component = /* isMountSpec */
        /* canMeasure */
        setUpSpyComponentForCreateLayout(false, true);
        YogaMeasureFunction measureFunction = getMeasureFunction(component);
        final int nestedTreeWidth = 20;
        final int nestedTreeHeight = 25;
        InternalNode nestedTree = mock(InternalNode.class);
        Mockito.when(nestedTree.getWidth()).thenReturn(nestedTreeWidth);
        Mockito.when(nestedTree.getHeight()).thenReturn(nestedTreeHeight);
        Mockito.when(LayoutState.resolveNestedTree(ArgumentMatchers.eq(mContext), ArgumentMatchers.eq(mNode), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(nestedTree);
        Mockito.when(mNode.getContext()).thenReturn(mContext);
        long output = measureFunction.measure(mYogaNode, 0, EXACTLY, 0, EXACTLY);
        PowerMockito.verifyStatic();
        LayoutState.resolveNestedTree(ArgumentMatchers.eq(mContext), ArgumentMatchers.eq(mNode), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        assertThat(YogaMeasureOutput.getWidth(output)).isEqualTo(nestedTreeWidth);
        assertThat(YogaMeasureOutput.getHeight(output)).isEqualTo(nestedTreeHeight);
    }

    @Test
    public void testLayoutSpecMeasureResolveNestedTree_withExperiment() {
        Component component = /* isMountSpec */
        /* canMeasure */
        setUpSpyComponentForCreateLayout(false, true);
        YogaMeasureFunction measureFunction = getMeasureFunction(component);
        final int nestedTreeWidth = 20;
        final int nestedTreeHeight = 25;
        InternalNode nestedTree = mock(InternalNode.class);
        Mockito.when(nestedTree.getWidth()).thenReturn(nestedTreeWidth);
        Mockito.when(nestedTree.getHeight()).thenReturn(nestedTreeHeight);
        Mockito.when(LayoutState.resolveNestedTree(ArgumentMatchers.eq(mContext), ArgumentMatchers.eq(mNode), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(nestedTree);
        Mockito.when(mNode.getContext()).thenReturn(mContext);
        Mockito.when(mContext.isNestedTreeResolutionExperimentEnabled()).thenReturn(true);
        Mockito.when(mNode.getParent()).thenReturn(mNode);
        Mockito.when(mNode.getContext()).thenReturn(mContext);
        long output = measureFunction.measure(mYogaNode, 0, EXACTLY, 0, EXACTLY);
        PowerMockito.verifyStatic();
        LayoutState.resolveNestedTree(ArgumentMatchers.eq(mContext), ArgumentMatchers.eq(mNode), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        assertThat(YogaMeasureOutput.getWidth(output)).isEqualTo(nestedTreeWidth);
        assertThat(YogaMeasureOutput.getHeight(output)).isEqualTo(nestedTreeHeight);
    }

    @OkToExtend
    private static class TestBaseComponent extends Component {
        private final boolean mCanMeasure;

        private final MountType mMountType;

        private final InternalNode mNode;

        private final boolean mIsLayoutSpecWithSizeSpecCheck;

        private final boolean mHasState;

        TestBaseComponent(boolean canMeasure, MountType mountType, InternalNode node, boolean isLayoutSpecWithSizeSpecCheck, boolean hasState) {
            super("TestBaseComponent");
            mCanMeasure = canMeasure;
            mMountType = mountType;
            mNode = node;
            mIsLayoutSpecWithSizeSpecCheck = isLayoutSpecWithSizeSpecCheck;
            mHasState = hasState;
        }

        @Override
        public boolean isEquivalentTo(Component other) {
            return (this) == other;
        }

        @Override
        protected Component onCreateLayout(ComponentContext c) {
            return this;
        }

        @Override
        protected Component onCreateLayoutWithSizeSpec(ComponentContext c, int widthSpec, int heightSpec) {
            return this;
        }

        @Override
        protected ComponentLayout resolve(ComponentContext c) {
            return mNode;
        }

        @Override
        protected boolean canMeasure() {
            return mCanMeasure;
        }

        @Override
        public MountType getMountType() {
            return mMountType;
        }

        @Override
        protected boolean hasState() {
            return mHasState;
        }

        @Override
        protected boolean isLayoutSpecWithSizeSpecCheck() {
            return mIsLayoutSpecWithSizeSpecCheck;
        }

        @Override
        protected boolean canUsePreviousLayout(ComponentContext context) {
            return super.canUsePreviousLayout(context);
        }
    }

    static class SpyComponentBuilder {
        private boolean mCanMeasure = false;

        private MountType mMountType = MountType.NONE;

        private InternalNode mNode = null;

        private boolean mIsLayoutSpecWithSizeSpecCheck = false;

        private boolean mHasState = false;

        ComponentLifecycleTest.SpyComponentBuilder canMeasure(boolean canMeasure) {
            this.mCanMeasure = canMeasure;
            return this;
        }

        ComponentLifecycleTest.SpyComponentBuilder isMountSpec(boolean isMountSpec) {
            this.mMountType = (isMountSpec) ? MountType.DRAWABLE : MountType.NONE;
            return this;
        }

        ComponentLifecycleTest.SpyComponentBuilder setNode(InternalNode node) {
            this.mNode = node;
            return this;
        }

        ComponentLifecycleTest.SpyComponentBuilder isLayoutSpecWithSizeSpecCheck(boolean isLayoutSpecWithSizeSpecCheck) {
            this.mIsLayoutSpecWithSizeSpecCheck = isLayoutSpecWithSizeSpecCheck;
            return this;
        }

        ComponentLifecycleTest.SpyComponentBuilder hasState(boolean hasState) {
            this.mHasState = hasState;
            return this;
        }

        Component build(ComponentContext context) {
            return ComponentLifecycleTest.createSpyComponent(context, new ComponentLifecycleTest.TestBaseComponent(mCanMeasure, mMountType, mNode, mIsLayoutSpecWithSizeSpecCheck, mHasState));
        }
    }

    private static class TestMountSpecWithEmptyOnMeasure extends ComponentLifecycleTest.TestBaseComponent {
        TestMountSpecWithEmptyOnMeasure(InternalNode node) {
            super(true, DRAWABLE, node, false, false);
        }

        @Override
        protected void onMeasure(ComponentContext c, ComponentLayout layout, int widthSpec, int heightSpec, Size size) {
        }
    }

    private static class TestMountSpecSettingSizesInOnMeasure extends ComponentLifecycleTest.TestMountSpecWithEmptyOnMeasure {
        TestMountSpecSettingSizesInOnMeasure(InternalNode node) {
            super(node);
        }

        @Override
        protected void onMeasure(ComponentContext context, ComponentLayout layout, int widthSpec, int heightSpec, Size size) {
            size.width = ComponentLifecycleTest.A_WIDTH;
            size.height = ComponentLifecycleTest.A_HEIGHT;
        }
    }
}

