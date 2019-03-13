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
package com.hannesdorfmann.mosby3.mvp.delegate;


import ActivityMvpViewStateDelegateImpl.KEY_MOSBY_VIEW_ID;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.hannesdorfmann.mosby3.mvp.viewstate.ViewState;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;


/**
 *
 *
 * @author Hannes Dorfmann
 */
public class ActivityMvpViewStateDelegateImplTestNew {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private MvpView view;

    private MvpPresenter<MvpView> presenter;

    private PartialMvpViewStateDelegateCallbackImpl callback;

    private Activity activity;

    private Application application;

    private ViewState<MvpView> viewState;

    @Test
    public void appStartWithScreenOrientationChangeAndFinallyFinishing() {
        ActivityMvpViewStateDelegateImpl<MvpView, MvpPresenter<MvpView>, ViewState<MvpView>> delegate = new ActivityMvpViewStateDelegateImpl(activity, callback, true);
        startActivity(delegate, null, 1, 1, 1, 1, 1, 0, null, 0, 1, 0);
        Bundle bundle = BundleMocker.create();
        finishActivity(delegate, bundle, 1, 0, true, false);
        startActivity(delegate, bundle, 1, 2, 2, 1, 2, 1, true, 1, 1, 1);
        finishActivity(delegate, bundle, 2, 1, false, true);
    }

    @Test
    public void appStartFinishing() {
        ActivityMvpViewStateDelegateImpl<MvpView, MvpPresenter<MvpView>, ViewState<MvpView>> delegate = new ActivityMvpViewStateDelegateImpl(activity, callback, true);
        startActivity(delegate, null, 1, 1, 1, 1, 1, 0, null, 0, 1, 0);
        Bundle bundle = BundleMocker.create();
        finishActivity(delegate, bundle, 1, 1, false, true);
    }

    @Test
    public void dontKeepPresenterAndViewState() {
        ActivityMvpViewStateDelegateImpl<MvpView, MvpPresenter<MvpView>, ViewState<MvpView>> delegate = new ActivityMvpViewStateDelegateImpl(activity, callback, false);
        startActivity(delegate, null, 1, 1, 1, 1, 1, 0, null, 0, 1, 0);
        Bundle bundle = BundleMocker.create();
        finishActivity(delegate, bundle, 1, 1, true, false);
        startActivity(delegate, bundle, 2, 2, 2, 2, 2, 0, null, 0, 2, 0);
        finishActivity(delegate, bundle, 2, 2, false, true);
    }

    @Test
    public void appStartAfterProcessDeathAndViewStateRecreationFromBundle() {
        ActivityMvpViewStateDelegateImpl<MvpView, MvpPresenter<MvpView>, ViewState<MvpView>> delegate = new ActivityMvpViewStateDelegateImpl(activity, callback, true);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                viewState = Mockito.spy(new SimpleRestorableViewState());
                return viewState;
            }
        }).when(callback).createViewState();
        Bundle bundle = BundleMocker.create();
        bundle.putString(KEY_MOSBY_VIEW_ID, "123456789");
        startActivity(delegate, bundle, 1, 1, 1, 1, 1, 1, false, 1, 0, 1);
    }

    @Test
    public void appStartWithViewStateFromMemoryAndBundleButPreferViewStateFromMemory() {
        ActivityMvpViewStateDelegateImpl<MvpView, MvpPresenter<MvpView>, ViewState<MvpView>> delegate = new ActivityMvpViewStateDelegateImpl(activity, callback, true);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                viewState = Mockito.spy(new SimpleRestorableViewState());
                return viewState;
            }
        }).when(callback).createViewState();
        startActivity(delegate, null, 1, 1, 1, 1, 1, 0, null, 0, 1, 0);
        Bundle bundle = BundleMocker.create();
        finishActivity(delegate, bundle, 1, 0, true, false);
        startActivity(delegate, bundle, 1, 2, 2, 1, 2, 1, true, 1, 1, 1);
        finishActivity(delegate, bundle, 2, 1, false, true);
    }
}

