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


import com.facebook.litho.stats.LithoStats;
import com.facebook.litho.testing.helper.ComponentTestHelper;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.testing.util.InlineLayoutSpec;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.shadows.ShadowLooper;


@RunWith(ComponentsTestRunner.class)
public class StateUpdatesTest {
    private static final int LIFECYCLE_TEST_ID = 1;

    private static final int INITIAL_COUNT_STATE_VALUE = 4;

    private int mWidthSpec;

    private int mHeightSpec;

    private static class TestStateUpdate implements ComponentLifecycle.StateUpdate {
        @Override
        public void updateState(StateContainer stateContainer) {
            StateUpdatesTest.TestStateContainer stateContainerImpl = ((StateUpdatesTest.TestStateContainer) (stateContainer));
            stateContainerImpl.mCount = (stateContainerImpl.mCount) + 1;
        }
    }

    static class TestComponent extends Component {
        private final StateUpdatesTest.TestStateContainer mStateContainer;

        private StateUpdatesTest.TestComponent shallowCopy;

        private int mId;

        private static final AtomicInteger sIdGenerator = new AtomicInteger(0);

        public TestComponent() {
            super("TestComponent");
            mStateContainer = new StateUpdatesTest.TestStateContainer();
            mId = StateUpdatesTest.TestComponent.sIdGenerator.getAndIncrement();
        }

        @Override
        public boolean isEquivalentTo(Component other) {
            return (this) == other;
        }

        @Override
        int getTypeId() {
            return StateUpdatesTest.LIFECYCLE_TEST_ID;
        }

        @Override
        protected boolean hasState() {
            return true;
        }

        @Override
        protected void createInitialState(ComponentContext c) {
            mStateContainer.mCount = StateUpdatesTest.INITIAL_COUNT_STATE_VALUE;
        }

        @Override
        protected void transferState(StateContainer prevStateContainer, StateContainer nextStateContainer) {
            StateUpdatesTest.TestStateContainer prevStateContainerImpl = ((StateUpdatesTest.TestStateContainer) (prevStateContainer));
            StateUpdatesTest.TestStateContainer nextStateContainerImpl = ((StateUpdatesTest.TestStateContainer) (nextStateContainer));
            nextStateContainerImpl.mCount = prevStateContainerImpl.mCount;
        }

        int getCount() {
            return mStateContainer.mCount;
        }

        @Override
        synchronized void markLayoutStarted() {
            // No-op because we override makeShallowCopy below :(
        }

        @Override
        public Component makeShallowCopy() {
            return this;
        }

        @Override
        Component makeShallowCopyWithNewId() {
            shallowCopy = ((StateUpdatesTest.TestComponent) (super.makeShallowCopy()));
            shallowCopy.mId = StateUpdatesTest.TestComponent.sIdGenerator.getAndIncrement();
            if (getScopedContext().isNestedTreeResolutionExperimentEnabled()) {
                shallowCopy.setGlobalKey(getGlobalKey());
            }
            return shallowCopy;
        }

        StateUpdatesTest.TestComponent getComponentForStateUpdate() {
            if ((shallowCopy) == null) {
                return this;
            }
            return shallowCopy.getComponentForStateUpdate();
        }

        @Override
        protected int getId() {
            return mId;
        }

        @Override
        protected StateContainer getStateContainer() {
            return mStateContainer;
        }
    }

    static class TestStateContainer implements StateContainer {
        protected int mCount;
    }

    private static final String mLogTag = "logTag";

    private ShadowLooper mLayoutThreadShadowLooper;

    private ComponentContext mContext;

    private StateUpdatesTest.TestComponent mTestComponent;

    private ComponentTree mComponentTree;

    private ComponentsLogger mComponentsLogger;

    private LithoView mLithoView;

    @Test
    public void testNoCrashOnSameComponentKey() {
        final Component child1 = new StateUpdatesTest.TestComponent();
        child1.setKey("key");
        final Component child2 = new StateUpdatesTest.TestComponent();
        child2.setKey("key");
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(child1).child(child2).build();
            }
        };
        final LithoView lithoView = new LithoView(mContext);
        lithoView.setComponent(component);
        lithoView.onAttachedToWindow();
        ComponentTestHelper.measureAndLayout(lithoView);
    }

    @Test
    public void testNoCrashOnSameComponentKeyNestedContainers() {
        final Component child1 = new StateUpdatesTest.TestComponent();
        child1.setKey("key");
        final Component child2 = new StateUpdatesTest.TestComponent();
        child2.setKey("key");
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return Column.create(c).child(Column.create(c).child(child1)).child(Column.create(c).child(child2)).build();
            }
        };
        final LithoView lithoView = new LithoView(mContext);
        lithoView.setComponent(component);
        lithoView.onAttachedToWindow();
        ComponentTestHelper.measureAndLayout(lithoView);
    }

    @Test
    public void testKeepInitialStateValues() {
        StateUpdatesTest.TestStateContainer previousStateContainer = ((StateUpdatesTest.TestStateContainer) (getStateContainersMap().get(getGlobalKey())));
        assertThat(previousStateContainer).isNotNull();
        assertThat(previousStateContainer.mCount).isEqualTo(StateUpdatesTest.INITIAL_COUNT_STATE_VALUE);
    }

    @Test
    public void testKeepUpdatedStateValue() {
        mComponentTree.updateStateAsync(getGlobalKey(), new StateUpdatesTest.TestStateUpdate(), "test");
        mLayoutThreadShadowLooper.runToEndOfTasks();
        StateUpdatesTest.TestStateContainer previousStateContainer = ((StateUpdatesTest.TestStateContainer) (getStateContainersMap().get(getGlobalKey())));
        assertThat(previousStateContainer).isNotNull();
        assertThat(previousStateContainer.mCount).isEqualTo(((StateUpdatesTest.INITIAL_COUNT_STATE_VALUE) + 1));
    }

    @Test
    public void testClearUnusedStateContainers() {
        mComponentTree.updateStateSync(getGlobalKey(), new StateUpdatesTest.TestStateUpdate(), "test");
        assertThat(getStateContainersMap().keySet().size()).isEqualTo(1);
        assertThat(getStateContainersMap().keySet().contains(getGlobalKey())).isTrue();
        final Component child1 = new StateUpdatesTest.TestComponent();
        child1.setKey("key");
        mLithoView.setComponent(child1);
        mLithoView.onAttachedToWindow();
        ComponentTestHelper.measureAndLayout(mLithoView);
        mComponentTree.updateStateSync(child1.getGlobalKey(), new StateUpdatesTest.TestStateUpdate(), "test");
        assertThat(getStateContainersMap().keySet().size()).isEqualTo(1);
        assertThat(getStateContainersMap().keySet().contains("key")).isTrue();
    }

    @Test
    public void testClearAppliedStateUpdates() {
        mComponentTree.updateStateAsync(getGlobalKey(), new StateUpdatesTest.TestStateUpdate(), "test");
        assertThat(getPendingStateUpdatesForComponent(mTestComponent)).hasSize(1);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(getPendingStateUpdatesForComponent(mTestComponent.getComponentForStateUpdate())).isNull();
    }

    @Test
    public void testEnqueueStateUpdate() {
        mComponentTree.updateStateAsync(getGlobalKey(), new StateUpdatesTest.TestStateUpdate(), "test");
        assertThat(getPendingStateUpdatesForComponent(mTestComponent)).hasSize(1);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        mComponentTree.updateStateAsync(getGlobalKey(), new StateUpdatesTest.TestStateUpdate(), "test");
        assertThat(((StateUpdatesTest.TestStateContainer) (getStateContainersMap().get(getGlobalKey()))).mCount).isEqualTo(((StateUpdatesTest.INITIAL_COUNT_STATE_VALUE) + 1));
        assertThat(getPendingStateUpdatesForComponent(mTestComponent.getComponentForStateUpdate())).hasSize(1);
    }

    @Test
    public void testEnqueueStateUpdate_withExperiment() throws Exception {
        setup(true);
        mComponentTree.updateStateAsync(getGlobalKey(), new StateUpdatesTest.TestStateUpdate(), "test");
        assertThat(getPendingStateUpdatesForComponent(mTestComponent)).hasSize(1);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        mComponentTree.updateStateAsync(getGlobalKey(), new StateUpdatesTest.TestStateUpdate(), "test");
        assertThat(((StateUpdatesTest.TestStateContainer) (getStateContainersMap().get(getGlobalKey()))).mCount).isEqualTo(((StateUpdatesTest.INITIAL_COUNT_STATE_VALUE) + 1));
        assertThat(getPendingStateUpdatesForComponent(mTestComponent.getComponentForStateUpdate())).hasSize(1);
    }

    @Test
    public void testEnqueueStateUpdate_withExperiment_checkAppliedStateUpdate() throws Exception {
        setup(true);
        mComponentTree.updateStateAsync(getGlobalKey(), new StateUpdatesTest.TestStateUpdate(), "test");
        assertThat(getPendingStateUpdatesForComponent(mTestComponent)).hasSize(1);
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(getScopedContext().getStateHandler().getAppliedStateUpdates()).hasSize(1);
    }

    @Test
    public void testSetInitialStateValue() {
        assertThat(mTestComponent.getCount()).isEqualTo(StateUpdatesTest.INITIAL_COUNT_STATE_VALUE);
    }

    @Test
    public void testUpdateState() {
        mComponentTree.updateStateAsync(getGlobalKey(), new StateUpdatesTest.TestStateUpdate(), "test");
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(mTestComponent.getComponentForStateUpdate().getCount()).isEqualTo(((StateUpdatesTest.INITIAL_COUNT_STATE_VALUE) + 1));
    }

    @Test
    public void testTransferState() {
        mComponentTree.updateStateAsync(getGlobalKey(), new StateUpdatesTest.TestStateUpdate(), "test");
        mLayoutThreadShadowLooper.runToEndOfTasks();
        mComponentTree.setSizeSpec(mWidthSpec, mHeightSpec);
        assertThat(mTestComponent.getComponentForStateUpdate().getCount()).isEqualTo(((StateUpdatesTest.INITIAL_COUNT_STATE_VALUE) + 1));
    }

    @Test
    public void testTransferAndUpdateState() {
        mComponentTree.updateStateAsync(getGlobalKey(), new StateUpdatesTest.TestStateUpdate(), "test");
        mLayoutThreadShadowLooper.runToEndOfTasks();
        mComponentTree.updateStateAsync(getGlobalKey(), new StateUpdatesTest.TestStateUpdate(), "test");
        mLayoutThreadShadowLooper.runToEndOfTasks();
        assertThat(mTestComponent.getComponentForStateUpdate().getCount()).isEqualTo(((StateUpdatesTest.INITIAL_COUNT_STATE_VALUE) + 2));
    }

    @Test
    public void testStateStatsTest() {
        final long before = LithoStats.getStateUpdates();
        mComponentTree.updateStateAsync(getGlobalKey(), new StateUpdatesTest.TestStateUpdate(), "test");
        mLayoutThreadShadowLooper.runToEndOfTasks();
        final long after = LithoStats.getStateUpdates();
        assertThat((after - before)).isEqualTo(1);
    }

    @Test
    public void testSyncStateStatsWhenAsyncTest() {
        final long before = LithoStats.getStateUpdatesSync();
        mComponentTree.updateStateAsync(getGlobalKey(), new StateUpdatesTest.TestStateUpdate(), "test");
        mLayoutThreadShadowLooper.runToEndOfTasks();
        final long after = LithoStats.getStateUpdatesSync();
        assertThat((after - before)).isEqualTo(0);
    }

    @Test
    public void testSyncStateStatsWhenSyncTest() {
        final long before = LithoStats.getStateUpdatesSync();
        mComponentTree.updateStateSync(getGlobalKey(), new StateUpdatesTest.TestStateUpdate(), "test");
        mLayoutThreadShadowLooper.runToEndOfTasks();
        final long after = LithoStats.getStateUpdatesSync();
        assertThat((after - before)).isEqualTo(1);
    }
}

