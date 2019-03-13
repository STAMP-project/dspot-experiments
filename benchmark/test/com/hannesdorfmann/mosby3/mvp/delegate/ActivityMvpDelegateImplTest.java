/**
 * Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hannesdorfmann.mosby3.mvp.delegate;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


/**
 *
 *
 * @author Hannes Dorfmann
 */
public class ActivityMvpDelegateImplTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private MvpView view;

    private MvpPresenter<MvpView> presenter;

    private PartialMvpDelegateCallbackImpl callback;

    private Activity activity;

    private Application application;

    @Test
    public void appStartWithScreenOrientationChangeAndFinallyFinishing() {
        ActivityMvpDelegateImpl<MvpView, MvpPresenter<MvpView>> delegate = new ActivityMvpDelegateImpl(activity, callback, true);
        startActivity(delegate, null, 1, 1, 1);
        Bundle bundle = BundleMocker.create();
        finishActivity(delegate, bundle, true, 1, 0, true, false);
        startActivity(delegate, bundle, 1, 2, 2);
        finishActivity(delegate, bundle, false, 2, 1, false, true);
    }

    @Test
    public void appStartFinishing() {
        ActivityMvpDelegateImpl<MvpView, MvpPresenter<MvpView>> delegate = new ActivityMvpDelegateImpl(activity, callback, true);
        startActivity(delegate, null, 1, 1, 1);
        Bundle bundle = BundleMocker.create();
        finishActivity(delegate, bundle, false, 1, 1, false, true);
    }

    @Test
    public void dontKeepPresenter() {
        ActivityMvpDelegateImpl<MvpView, MvpPresenter<MvpView>> delegate = new ActivityMvpDelegateImpl(activity, callback, false);
        startActivity(delegate, null, 1, 1, 1);
        Bundle bundle = BundleMocker.create();
        finishActivity(delegate, bundle, false, 1, 1, true, false);
        startActivity(delegate, bundle, 2, 2, 2);
        finishActivity(delegate, bundle, false, 2, 2, false, true);
    }
}

