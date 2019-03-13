/**
 * Copyright 2014 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mortar;


import android.os.Bundle;
import mortar.bundler.BundleService;
import mortar.bundler.BundleServiceRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


// Robolectric allows us to use Bundles.
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class PresenterTest {
    static class SomeView {}

    MortarScope root;

    MortarScope activityScope;

    class ChildPresenter extends Presenter<PresenterTest.SomeView> {
        final String payload;

        boolean loaded;

        ChildPresenter(String payload) {
            this.payload = payload;
        }

        @Override
        protected BundleService extractBundleService(PresenterTest.SomeView view) {
            return BundleService.getBundleService(activityScope);
        }

        @Override
        protected void onSave(Bundle savedInstanceState) {
            savedInstanceState.putString("key", payload);
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            if (savedInstanceState != null) {
                assertThat(savedInstanceState.getString("key")).isEqualTo(payload);
                loaded = true;
            }
        }
    }

    class ParentPresenter extends Presenter<PresenterTest.SomeView> {
        @Override
        protected BundleService extractBundleService(PresenterTest.SomeView view) {
            return BundleService.getBundleService(activityScope);
        }

        // The child presenters are anonymous inner classes but of the same
        // type. This is like the case where a presenter manages two
        // popup presenters for two different dialogs.
        PresenterTest.ChildPresenter childOne = new PresenterTest.ChildPresenter("one") {};

        PresenterTest.ChildPresenter childTwo = new PresenterTest.ChildPresenter("two") {};

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            childOne.takeView(getView());
            childTwo.takeView(getView());
        }

        @Override
        public void dropView(PresenterTest.SomeView view) {
            childTwo.dropView(view);
            childOne.dropView(view);
            super.dropView(view);
        }
    }

    @Test
    public void childPresentersGetTheirOwnBundles() {
        BundleServiceRunner bundleServiceRunner = BundleServiceRunner.getBundleServiceRunner(activityScope);
        bundleServiceRunner.onCreate(null);
        PresenterTest.ParentPresenter presenter = new PresenterTest.ParentPresenter();
        PresenterTest.SomeView view = new PresenterTest.SomeView();
        takeView(view);
        Bundle bundle = new Bundle();
        bundleServiceRunner.onSaveInstanceState(bundle);
        presenter.dropView(view);
        bundleServiceRunner.onCreate(bundle);
        takeView(view);
        /**
         * Assertions in {@link ChildPresenter#onLoad(android.os.Bundle)} are the real test,
         * but let's check that the were run
         */
        assertThat(presenter.childOne.loaded).isTrue();
        assertThat(presenter.childTwo.loaded).isTrue();
    }

    class SimplePresenter extends Presenter<PresenterTest.SomeView> {
        MortarScope registered;

        MortarScope destroyed;

        boolean loaded;

        Object droppedView;

        @Override
        protected void onEnterScope(MortarScope scope) {
            registered = scope;
        }

        @Override
        protected BundleService extractBundleService(PresenterTest.SomeView view) {
            return BundleService.getBundleService(activityScope);
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            loaded = true;
        }

        @Override
        public void dropView(PresenterTest.SomeView view) {
            droppedView = view;
            super.dropView(view);
        }

        @Override
        protected void onExitScope() {
            destroyed = activityScope;
        }
    }

    /**
     * https://github.com/square/mortar/issues/59
     */
    @Test
    public void onLoadOnlyOncePerView() {
        PresenterTest.SimplePresenter presenter = new PresenterTest.SimplePresenter();
        PresenterTest.SomeView view = new PresenterTest.SomeView();
        takeView(view);
        assertThat(presenter.loaded).isTrue();
        presenter.loaded = false;
        BundleServiceRunner.getBundleServiceRunner(activityScope).onCreate(null);
        assertThat(presenter.loaded).isFalse();
    }

    @Test
    public void newViewNewLoad() {
        PresenterTest.SimplePresenter presenter = new PresenterTest.SimplePresenter();
        PresenterTest.SomeView viewOne = new PresenterTest.SomeView();
        takeView(viewOne);
        assertThat(presenter.loaded).isTrue();
        presenter.loaded = false;
        PresenterTest.SomeView viewTwo = new PresenterTest.SomeView();
        takeView(viewTwo);
        assertThat(presenter.loaded).isTrue();
    }

    @Test
    public void dropRetakeReload() {
        PresenterTest.SimplePresenter presenter = new PresenterTest.SimplePresenter();
        PresenterTest.SomeView view = new PresenterTest.SomeView();
        takeView(view);
        assertThat(presenter.loaded).isTrue();
        presenter.dropView(view);
        presenter.loaded = false;
        takeView(view);
        assertThat(presenter.loaded).isTrue();
    }

    /**
     * When takeView clobbers an existing view, dropView should be called. (We could
     * drop this requirement if dropView were final, see https://github.com/square/mortar/issues/52)
     */
    @Test
    public void autoDropCallsDrop() {
        PresenterTest.SimplePresenter presenter = new PresenterTest.SimplePresenter();
        PresenterTest.SomeView viewOne = new PresenterTest.SomeView();
        PresenterTest.SomeView viewTwo = new PresenterTest.SomeView();
        takeView(viewOne);
        takeView(viewTwo);
        assertThat(presenter.droppedView).isSameAs(viewOne);
    }

    @Test
    public void onRegisteredIsFired() {
        PresenterTest.SimplePresenter presenter = new PresenterTest.SimplePresenter();
        PresenterTest.SomeView viewOne = new PresenterTest.SomeView();
        takeView(viewOne);
        assertThat(presenter.registered).isSameAs(activityScope);
    }

    @Test
    public void onRegisteredIsDebounced() {
        PresenterTest.SimplePresenter presenter = new PresenterTest.SimplePresenter();
        PresenterTest.SomeView viewOne = new PresenterTest.SomeView();
        takeView(viewOne);
        presenter.dropView(viewOne);
        presenter.registered = null;
        takeView(viewOne);
        assertThat(presenter.registered).isNull();
    }

    @Test
    public void onExitIsFired() {
        PresenterTest.SimplePresenter presenter = new PresenterTest.SimplePresenter();
        PresenterTest.SomeView viewOne = new PresenterTest.SomeView();
        takeView(viewOne);
        activityScope.destroy();
        assertThat(presenter.destroyed).isSameAs(activityScope);
    }
}

