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


import android.app.Application;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.hannesdorfmann.mosby3.mvp.viewstate.ViewState;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Hannes Dorfmann
 */
public class ViewGroupMvpViewStateDelegateImplTest {
    private MvpView view;

    private ViewState<MvpView> viewState;

    private MvpPresenter<MvpView> presenter;

    private PartialViewGroupMvpViewStateDelegateCallbackImpl callback;

    private ViewGroupMvpViewStateDelegateImpl<MvpView, MvpPresenter<MvpView>, ViewState<MvpView>> delegate;

    private FragmentActivity activity;

    private Application application;

    private View androidView;

    private Parcelable savedState;

    @Test
    public void appStartWithScreenOrientationChangeAndFinallyFinishing() {
        startViewGroup(1, 1, 1, 1, 1, 0, null, 0, 1, 0);
        finishViewGroup(1, 0, true, false);
        delegate = new ViewGroupMvpViewStateDelegateImpl(androidView, callback, true);
        startViewGroup(1, 2, 2, 1, 2, 1, true, 1, 1, 1);
        finishViewGroup(2, 1, false, true);
    }

    @Test
    public void appStartFinishing() {
        startViewGroup(1, 1, 1, 1, 1, 0, null, 0, 1, 0);
        finishViewGroup(1, 1, false, true);
    }

    @Test
    public void dontKeepPresenter() {
        delegate = new ViewGroupMvpViewStateDelegateImpl(androidView, callback, false);
        startViewGroup(1, 1, 1, 1, 1, 0, null, 0, 1, 0);
        finishViewGroup(1, 1, true, false);
        delegate = new ViewGroupMvpViewStateDelegateImpl(androidView, callback, false);
        startViewGroup(2, 2, 2, 2, 2, 0, null, 0, 2, 0);
        finishViewGroup(2, 2, false, true);
    }

    /**
     * Checks if two Views one that keeps presenter, the other who doesn't keep presenter during
     * screen orientation changes work properly
     *
     * https://github.com/sockeqwe/mosby/issues/231
     */
    @Test
    public void dontKeepPresenterIfSecondPresenterIsInPresenterManager() {
        MvpView view1 = new MvpView() {};
        View androidView1 = Mockito.mock(View.class);
        ViewState<MvpView> viewState1 = Mockito.mock(ViewState.class);
        MvpPresenter<MvpView> presenter1 = Mockito.mock(MvpPresenter.class);
        PartialViewGroupMvpViewStateDelegateCallbackImpl callback1 = Mockito.mock(PartialViewGroupMvpViewStateDelegateCallbackImpl.class);
        Mockito.doCallRealMethod().when(callback1).setPresenter(presenter1);
        Mockito.doCallRealMethod().when(callback1).getPresenter();
        Mockito.doCallRealMethod().when(callback1).setViewState(viewState1);
        Mockito.doCallRealMethod().when(callback1).getViewState();
        Mockito.when(getMvpView()).thenReturn(view1);
        Mockito.when(getContext()).thenReturn(activity);
        Mockito.when(createPresenter()).thenReturn(presenter1);
        Mockito.when(createViewState()).thenReturn(viewState1);
        Mockito.when(androidView1.isInEditMode()).thenReturn(false);
        ViewGroupMvpViewStateDelegateImpl keepDelegate = new ViewGroupMvpViewStateDelegateImpl(androidView1, callback1, true);
        keepDelegate.onAttachedToWindow();
        delegate = new ViewGroupMvpViewStateDelegateImpl(androidView, callback, false);
        startViewGroup(1, 1, 1, 1, 1, 0, null, 0, 1, 0);
        finishViewGroup(1, 1, true, false);
        delegate = new ViewGroupMvpViewStateDelegateImpl(androidView, callback, false);
        startViewGroup(2, 2, 2, 2, 2, 0, null, 0, 2, 0);
        finishViewGroup(2, 2, false, true);
        keepDelegate.onDetachedFromWindow();
    }

    @Test
    public void appStartWithProcessDeathAndViewStateRecreationFromBundle() {
        // Assert.fail("Not implemented");
        // TODO implement
    }

    @Test
    public void appStartWithViewStateFromMemoryAndBundleButPreferViewStateFromMemory() {
        // Assert.fail("Not implemented");
        // TODO implement
    }
}

