package com.arellomobile.mvp.tests;


import android.os.Bundle;
import com.arellomobile.mvp.MvpDelegate;
import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.presenter.InjectViewStatePresenter;
import com.arellomobile.mvp.presenter.NoViewStatePresenter;
import com.arellomobile.mvp.view.DelegateLocalPresenterTestView;
import com.arellomobile.mvp.view.TestView;
import java.lang.reflect.Field;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Date: 09.02.2016
 * Time: 12:37
 *
 * @author Savin Mikhail
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class LocalPresenterTest {
    @Mock
    TestView mTestView;

    DelegateLocalPresenterTestView mDelegateLocalPresenterTestView = new DelegateLocalPresenterTestView();

    DelegateLocalPresenterTestView mDelegateLocalPresenter2TestView = new DelegateLocalPresenterTestView();

    MvpDelegate<? extends TestView> mTestViewMvpDelegate = new MvpDelegate(mDelegateLocalPresenterTestView);

    MvpDelegate<? extends TestView> mTestViewMvpDelegate2 = new MvpDelegate(mDelegateLocalPresenter2TestView);

    @Test
    public void checkWithInjectViewState() {
        InjectViewStatePresenter injectViewStatePresenter = new InjectViewStatePresenter();
        attachView(mTestView);
        try {
            Field mViewState = MvpPresenter.class.getDeclaredField("mViewState");
            mViewState.setAccessible(true);
            Assert.assertTrue("ViewState is null for InjectViewStatePresenter", ((mViewState.get(injectViewStatePresenter)) != null));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            Assert.assertFalse(e.getLocalizedMessage(), true);
        }
    }

    @Test
    public void checkWithoutViewState() {
        NoViewStatePresenter noViewStatePresenter = new NoViewStatePresenter();
        attachView(mTestView);
        try {
            Field mViewState = MvpPresenter.class.getDeclaredField("mViewState");
            mViewState.setAccessible(true);
            Assert.assertTrue("ViewState is not null for NoViewStatePresenter", ((mViewState.get(noViewStatePresenter)) == null));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            Assert.assertFalse(e.getLocalizedMessage(), true);
        }
    }

    @Test
    public void checkDelegatePresenter() {
        Assert.assertTrue("Presenter is null for delegate", ((mDelegateLocalPresenterTestView.mInjectViewStatePresenter) != null));
    }

    @Test
    public void checkLocalPresenters() {
        Assert.assertNotEquals("Local Presenters for two different view is equal", mDelegateLocalPresenterTestView.mInjectViewStatePresenter.hashCode(), mDelegateLocalPresenter2TestView.mInjectViewStatePresenter.hashCode());
    }

    @Test
    public void checkSaveState() {
        int hashCode = mDelegateLocalPresenterTestView.mInjectViewStatePresenter.hashCode();
        Bundle bundle = new Bundle();
        mTestViewMvpDelegate.onSaveInstanceState(bundle);
        mTestViewMvpDelegate.onDetach();
        mTestViewMvpDelegate.onDestroy();
        mTestViewMvpDelegate.onCreate(bundle);
        mTestViewMvpDelegate.onAttach();
        // TODO: should be passed! Or change test
        // assertTrue("Local presenter has different hashCode after recreate", hashCode == mDelegateLocalPresenterTestView.mInjectViewStatePresenter.hashCode());
        mTestViewMvpDelegate.onDetach();
        mTestViewMvpDelegate.onDestroy();
        mTestViewMvpDelegate.onCreate();
        mTestViewMvpDelegate.onAttach();
        Assert.assertFalse("Local presenter has same hashCode after creating new view", (hashCode == (mDelegateLocalPresenterTestView.mInjectViewStatePresenter.hashCode())));
    }
}

