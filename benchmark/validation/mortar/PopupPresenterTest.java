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


import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import mortar.bundler.BundleServiceRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


// Robolectric allows us to use Bundles.
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class PopupPresenterTest {
    static class TestPopupPresenter extends PopupPresenter<Parcelable, String> {
        String result;

        TestPopupPresenter() {
        }

        TestPopupPresenter(String customStateKey) {
            super(customStateKey);
        }

        @Override
        protected void onPopupResult(String result) {
            this.result = result;
        }
    }

    static final boolean WITH_FLOURISH = true;

    static final boolean WITHOUT_FLOURISH = false;

    @Mock
    Popup<Parcelable, String> view;

    @Mock
    Context context;

    MortarScope root;

    MortarScope activityScope;

    PopupPresenterTest.TestPopupPresenter presenter;

    @Test
    public void takeViewDoesNotShowView() {
        presenter.takeView(view);
        Mockito.verify(view, Mockito.never()).show(ArgumentMatchers.any(Parcelable.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(PopupPresenterTest.TestPopupPresenter.class));
    }

    @Test
    public void showAfterTakeViewShowsView() {
        presenter.takeView(view);
        Parcelable info = Mockito.mock(Parcelable.class);
        presenter.show(info);
        Mockito.verify(view).show(ArgumentMatchers.same(info), ArgumentMatchers.eq(PopupPresenterTest.WITH_FLOURISH), ArgumentMatchers.same(presenter));
    }

    @Test
    public void dismissAfterShowDismissesView() {
        presenter.takeView(view);
        show(Mockito.mock(Parcelable.class));
        Mockito.when(view.isShowing()).thenReturn(true);
        dismiss();
        Mockito.verify(view).dismiss(ArgumentMatchers.eq(PopupPresenterTest.WITH_FLOURISH));
    }

    @Test
    public void dismissWithViewNotShowingDoesNotDismissView() {
        presenter.takeView(view);
        show(Mockito.mock(Parcelable.class));
        Mockito.when(view.isShowing()).thenReturn(false);
        dismiss();
        Mockito.verify(view, Mockito.never()).dismiss(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void dismissWithoutShowDoesNotDismissView() {
        presenter.takeView(view);
        dismiss();
        Mockito.verify(view, Mockito.never()).dismiss(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void showingReturnsInfo() {
        Parcelable info = Mockito.mock(Parcelable.class);
        presenter.show(info);
        assertThat(showing()).isSameAs(info);
    }

    @Test
    public void dismissClearsInfo() {
        show(Mockito.mock(Parcelable.class));
        dismiss();
        assertThat(showing()).isNull();
    }

    @Test
    public void showTwiceWithSameInfoDebounces() {
        presenter.takeView(view);
        Parcelable info = Mockito.mock(Parcelable.class);
        presenter.show(info);
        presenter.show(info);
        Mockito.verify(view).show(ArgumentMatchers.same(info), ArgumentMatchers.anyBoolean(), ArgumentMatchers.same(presenter));
    }

    @Test
    public void destroyDismissesWithoutFlourish() {
        presenter.takeView(view);
        Mockito.when(view.isShowing()).thenReturn(true);
        activityScope.destroy();
        Mockito.verify(view).dismiss(ArgumentMatchers.eq(PopupPresenterTest.WITHOUT_FLOURISH));
    }

    @Test
    public void takeViewRestoresPopup() {
        presenter.takeView(view);
        Parcelable info = Mockito.mock(Parcelable.class);
        presenter.show(info);
        Bundle state = new Bundle();
        BundleServiceRunner.getBundleServiceRunner(activityScope).onSaveInstanceState(state);
        newProcess();
        BundleServiceRunner.getBundleServiceRunner(activityScope).onCreate(state);
        presenter = new PopupPresenterTest.TestPopupPresenter();
        presenter.takeView(view);
        Mockito.verify(view).show(ArgumentMatchers.same(info), ArgumentMatchers.eq(PopupPresenterTest.WITHOUT_FLOURISH), ArgumentMatchers.same(presenter));
    }

    @Test
    public void customStateKeyAvoidsStateMixing() {
        String customStateKey1 = "presenter1";
        PopupPresenterTest.TestPopupPresenter presenter1 = new PopupPresenterTest.TestPopupPresenter(customStateKey1);
        presenter1.takeView(view);
        Bundle info1 = new Bundle();
        info1.putString("key", "data1");
        presenter1.show(info1);
        String customStateKey2 = "presenter2";
        PopupPresenterTest.TestPopupPresenter presenter2 = new PopupPresenterTest.TestPopupPresenter(customStateKey2);
        presenter2.takeView(view);
        Bundle info2 = new Bundle();
        info2.putString("key", "data2");
        presenter2.show(info2);
        Bundle state = new Bundle();
        BundleServiceRunner.getBundleServiceRunner(activityScope).onSaveInstanceState(state);
        newProcess();
        BundleServiceRunner.getBundleServiceRunner(activityScope).onCreate(state);
        presenter1 = new PopupPresenterTest.TestPopupPresenter(customStateKey1);
        presenter1.takeView(view);
        assertThat(showing()).isEqualTo(info1).isNotEqualTo(info2);
        presenter2 = new PopupPresenterTest.TestPopupPresenter(customStateKey2);
        presenter2.takeView(view);
        assertThat(showing()).isEqualTo(info2).isNotEqualTo(info1);
    }

    @Test
    public void onDismissedClearsInfo() {
        show(Mockito.mock(Parcelable.class));
        onDismissed("");
        assertThat(showing()).isNull();
    }

    @Test
    public void onDismissedDeliversResult() {
        show(Mockito.mock(Parcelable.class));
        onDismissed("result");
        assertThat(presenter.result).isEqualTo("result");
    }
}

