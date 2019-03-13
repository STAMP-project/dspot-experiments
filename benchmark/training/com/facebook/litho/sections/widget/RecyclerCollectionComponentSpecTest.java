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
package com.facebook.litho.sections.widget;


import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.LithoView;
import com.facebook.litho.sections.common.SingleComponentSection;
import com.facebook.litho.testing.ComponentsRule;
import com.facebook.litho.testing.assertj.ComponentConditions;
import com.facebook.litho.testing.assertj.LithoAssertions;
import com.facebook.litho.testing.assertj.LithoViewSubComponentDeepExtractor;
import com.facebook.litho.testing.helper.ComponentTestHelper;
import com.facebook.litho.testing.state.StateUpdatesTestHelper;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.testing.viewtree.ViewTree;
import com.facebook.litho.testing.viewtree.ViewTreeAssert;
import com.facebook.litho.widget.Text;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests {@link RecyclerCollectionComponentSpec}
 */
@RunWith(ComponentsTestRunner.class)
public class RecyclerCollectionComponentSpecTest {
    @Rule
    public ComponentsRule componentsRule = new ComponentsRule();

    private ComponentContext mComponentContext;

    private Component mLoadingComponent;

    private Component mEmptyComponent;

    private Component mErrorComponent;

    private Component mRecyclerCollectionComponent;

    private Component mContentComponent;

    @Test
    public void testNothingShown() throws Exception {
        mRecyclerCollectionComponent = RecyclerCollectionComponent.create(mComponentContext).loadingComponent(mLoadingComponent).errorComponent(mErrorComponent).recyclerConfiguration(new ListRecyclerConfiguration()).section(SingleComponentSection.create(new com.facebook.litho.sections.SectionContext(mComponentContext)).component(Text.create(mComponentContext).text("content").build()).build()).build();
        LithoAssertions.assertThat(mComponentContext, mRecyclerCollectionComponent).withStateUpdate(new StateUpdatesTestHelper.StateUpdater() {
            @Override
            public void performStateUpdate(ComponentContext c) {
                RecyclerCollectionComponent.updateLoadingState(c, LoadingState.EMPTY);
            }
        }).doesNotHave(LithoViewSubComponentDeepExtractor.deepSubComponentWith(anyOf(ComponentConditions.textEquals("loading"), ComponentConditions.textEquals("content"), ComponentConditions.textEquals("empty"), ComponentConditions.textEquals("error"))));
    }

    @Test
    public void testEmpty() throws Exception {
        LithoView view = StateUpdatesTestHelper.getViewAfterStateUpdate(mComponentContext, mRecyclerCollectionComponent, new StateUpdatesTestHelper.StateUpdater() {
            @Override
            public void performStateUpdate(ComponentContext context) {
                RecyclerCollectionComponent.updateLoadingState(context, LoadingState.EMPTY);
            }
        });
        ViewTreeAssert.assertThat(ViewTree.of(view)).doesNotHaveVisibleText("loading").hasVisibleText("content").hasVisibleText("empty").doesNotHaveVisibleText("error");
    }

    @Test
    public void testError() throws Exception {
        LithoView view = StateUpdatesTestHelper.getViewAfterStateUpdate(mComponentContext, mRecyclerCollectionComponent, new StateUpdatesTestHelper.StateUpdater() {
            @Override
            public void performStateUpdate(ComponentContext context) {
                RecyclerCollectionComponent.updateLoadingState(context, LoadingState.ERROR);
            }
        });
        ViewTreeAssert.assertThat(ViewTree.of(view)).doesNotHaveVisibleText("loading").hasVisibleText("content").doesNotHaveVisibleText("empty").hasVisibleText("error");
    }

    @Test
    public void testLoaded() throws Exception {
        LithoView view = StateUpdatesTestHelper.getViewAfterStateUpdate(mComponentContext, mRecyclerCollectionComponent, new StateUpdatesTestHelper.StateUpdater() {
            @Override
            public void performStateUpdate(ComponentContext context) {
                RecyclerCollectionComponent.updateLoadingState(context, LoadingState.LOADED);
            }
        });
        ViewTreeAssert.assertThat(ViewTree.of(view)).doesNotHaveVisibleText("loading").hasVisibleText("content").doesNotHaveVisibleText("empty").doesNotHaveVisibleText("error");
    }

    @Test
    public void testLoading() throws Exception {
        LithoView view = StateUpdatesTestHelper.getViewAfterStateUpdate(mComponentContext, mRecyclerCollectionComponent, new StateUpdatesTestHelper.StateUpdater() {
            @Override
            public void performStateUpdate(ComponentContext context) {
                RecyclerCollectionComponent.updateLoadingState(context, LoadingState.LOADING);
            }
        });
        ViewTreeAssert.assertThat(ViewTree.of(view)).hasVisibleText("loading").hasVisibleText("content").doesNotHaveVisibleText("empty").doesNotHaveVisibleText("error");
    }

    @Test
    public void testInitialState() throws Exception {
        LithoView view = ComponentTestHelper.mountComponent(mComponentContext, mRecyclerCollectionComponent);
        ViewTreeAssert.assertThat(ViewTree.of(view)).hasVisibleText("loading").hasVisibleText("content").doesNotHaveVisibleText("empty").doesNotHaveVisibleText("error");
    }
}

