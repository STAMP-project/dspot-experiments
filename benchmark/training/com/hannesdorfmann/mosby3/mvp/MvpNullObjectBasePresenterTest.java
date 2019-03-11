/**
 * Copyright 2017 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hannesdorfmann.mosby3.mvp;


import com.hannesdorfmann.mosby3.mvp.test.data.TestData;
import com.hannesdorfmann.mosby3.mvp.test.presenter.ParameterlessConstructorMvpPresenter;
import com.hannesdorfmann.mosby3.mvp.test.presenter.SubMvpPresenter;
import com.hannesdorfmann.mosby3.mvp.test.presenter.SubParameterlessConstructorMvpPresenter;
import com.hannesdorfmann.mosby3.mvp.test.presenter.UselessGenericParamsMvpPresenter;
import com.hannesdorfmann.mosby3.mvp.test.view.SubMvpView;
import com.hannesdorfmann.mosby3.mvp.test.view.TestMvpView;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Hannes Dorfmann
 */
public class MvpNullObjectBasePresenterTest {
    @Test
    public void throwsExceptionWhenInteractingWithPresenterButNoViewHasEverAttached() {
        MvpNullObjectBasePresenter<MvpView> presenter = new MvpNullObjectBasePresenter<MvpView>() {};
        try {
            presenter.getView();
            Assert.fail("Exception should be thrown");
        } catch (IllegalStateException e) {
            // Expected exception
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void uselessGenericsParamsPresenter() {
        TestMvpView view = newTestView();
        UselessGenericParamsMvpPresenter presenter = new UselessGenericParamsMvpPresenter<>();
        testPickingCorrectViewInterface(presenter);
        testAttachDetachView(presenter, view);
        presenter.attachView(view);
        presenter.viewShowThat();
        presenter.detachView();
        presenter.destroy();
        presenter.viewShowThat();
    }

    @Test
    public void constructorGenericParameterless() {
        ParameterlessConstructorMvpPresenter<TestData> presenter = new ParameterlessConstructorMvpPresenter<>();
        TestMvpView view = newTestView();
        testPickingCorrectViewInterface(presenter);
        testAttachDetachView(presenter, view);
        presenter.attachView(view);
        presenter.viewShowThat();
        presenter.detachView();
        presenter.destroy();
        presenter.viewShowThat();
    }

    @Test
    public void constructorDirectlyBaseClass() {
        MvpNullObjectBasePresenter<TestMvpView> presenter = new MvpNullObjectBasePresenter<TestMvpView>() {};
        TestMvpView view = newTestView();
        testPickingCorrectViewInterface(presenter);
        testAttachDetachView(presenter, view);
    }

    @Test
    public void constructorSubClass() {
        SubParameterlessConstructorMvpPresenter presenter = new SubParameterlessConstructorMvpPresenter();
        TestMvpView view = newTestView();
        testPickingCorrectViewInterface(presenter);
        testAttachDetachView(presenter, view);
        presenter.attachView(view);
        presenter.viewShowThat();
        detachView();
        destroy();
        presenter.viewShowThat();
    }

    @Test
    public void subviewInterface() {
        SubMvpPresenter presenter = new SubMvpPresenter();
        SubMvpView view = newSubTestView();
        testAttachDetachView(presenter, view);
        attachView(view);
        presenter.invokeShowThat();
        detachView();
        destroy();
        presenter.invokeShowThat();
    }
}

