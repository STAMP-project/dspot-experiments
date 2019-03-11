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


import FragmentMvpViewStateDelegateImpl.KEY_MOSBY_VIEW_ID;
import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.hannesdorfmann.mosby3.mvp.viewstate.ViewState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Hannes Dorfmann
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Fragment.class })
public class FragmentMvpViewStateDelegateImplTest {
    // TODO write test for retaining fragment
    private MvpView view;

    private MvpPresenter<MvpView> presenter;

    private MvpViewStateDelegateCallback<MvpView, MvpPresenter<MvpView>, ViewState<MvpView>> callback;

    private FragmentMvpViewStateDelegateImpl<MvpView, MvpPresenter<MvpView>, ViewState<MvpView>> delegate;

    private Fragment fragment;

    private FragmentActivity activity;

    private Application application;

    private ViewState<MvpView> viewState;

    @Test
    public void appStartWithScreenOrientationChangeAndFinallyFinishing() {
        startFragment(null, 1, 1, 1, 1, 1, 0, null, 0, 1, 0);
        Bundle bundle = BundleMocker.create();
        finishFragment(bundle, 1, 0, true, false);
        startFragment(bundle, 1, 2, 2, 1, 2, 1, true, 1, 1, 1);
        finishFragment(bundle, 2, 1, false, true);
    }

    @Test
    public void appStartFinishing() {
        startFragment(null, 1, 1, 1, 1, 1, 0, null, 0, 1, 0);
        Bundle bundle = BundleMocker.create();
        finishFragment(bundle, 1, 1, false, true);
        Mockito.verifyNoMoreInteractions(viewState);
    }

    @Test
    public void dontKeepPresenter() {
        delegate = new FragmentMvpViewStateDelegateImpl(fragment, callback, false, false);
        startFragment(null, 1, 1, 1, 1, 1, 0, null, 0, 1, 0);
        Bundle bundle = BundleMocker.create();
        finishFragment(bundle, 1, 1, true, false);
        startFragment(null, 2, 2, 2, 2, 2, 0, null, 0, 2, 0);
        finishFragment(bundle, 2, 2, false, true);
    }

    /**
     * Checks if two Fragments one that keeps presenter, the other who doesn't keep presenter during
     * screen orientation changes work properly
     *
     * https://github.com/sockeqwe/mosby/issues/231
     */
    @Test
    public void dontKeepPresenterWithSecondFragmentInPresenterManager() {
        MvpView view1 = new MvpView() {};
        ViewState<MvpView> viewState1 = Mockito.mock(ViewState.class);
        MvpPresenter<MvpView> presenter1 = Mockito.mock(MvpPresenter.class);
        PartialMvpViewStateDelegateCallbackImpl callback1 = Mockito.spy(PartialMvpViewStateDelegateCallbackImpl.class);
        Fragment fragment1 = PowerMockito.mock(Fragment.class);
        Mockito.doCallRealMethod().when(callback1).setPresenter(presenter1);
        Mockito.doCallRealMethod().when(callback1).getPresenter();
        Mockito.doCallRealMethod().when(callback1).setViewState(viewState1);
        Mockito.doCallRealMethod().when(callback1).getViewState();
        Mockito.when(callback1.getMvpView()).thenReturn(view1);
        Mockito.when(fragment1.getActivity()).thenReturn(activity);
        Mockito.when(callback1.createPresenter()).thenReturn(presenter1);
        Mockito.when(callback1.createViewState()).thenReturn(viewState1);
        FragmentMvpViewStateDelegateImpl<MvpView, MvpPresenter<MvpView>, ViewState<MvpView>> keepDelegate = new FragmentMvpViewStateDelegateImpl(fragment1, callback1, true, false);
        startFragment(keepDelegate, null);
        delegate = new FragmentMvpViewStateDelegateImpl(fragment, callback, false, false);
        startFragment(null, 1, 1, 1, 1, 1, 0, null, 0, 1, 0);
        Bundle bundle = BundleMocker.create();
        finishFragment(bundle, 1, 1, true, false);
        startFragment(null, 2, 2, 2, 2, 2, 0, null, 0, 2, 0);
        finishFragment(bundle, 2, 2, false, true);
        finishFragment(keepDelegate, BundleMocker.create());
    }

    @Test
    public void appStartAfterProcessDeathAndViewStateRecreationFromBundle() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                viewState = Mockito.spy(new SimpleRestorableViewState());
                return viewState;
            }
        }).when(callback).createViewState();
        Bundle bundle = BundleMocker.create();
        bundle.putString(KEY_MOSBY_VIEW_ID, "123456789");
        startFragment(bundle, 1, 1, 1, 1, 1, 1, false, 1, 0, 1);
    }

    @Test
    public void appStartWithViewStateFromMemoryAndBundleShouldPreferViewStateFromMemory() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                viewState = Mockito.spy(new SimpleRestorableViewState());
                return viewState;
            }
        }).when(callback).createViewState();
        startFragment(null, 1, 1, 1, 1, 1, 0, null, 0, 1, 0);
        Bundle bundle = BundleMocker.create();
        finishFragment(bundle, 1, 0, true, false);
        startFragment(bundle, 1, 2, 2, 1, 2, 1, true, 1, 1, 1);
        finishFragment(bundle, 2, 1, false, true);
    }
}

