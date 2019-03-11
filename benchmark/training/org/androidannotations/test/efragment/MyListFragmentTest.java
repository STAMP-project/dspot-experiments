/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2019 the AndroidAnnotations project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.test.efragment;


import R.id.conventionButton;
import android.R.id.list;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ListView;
import org.androidannotations.api.UiThreadExecutor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLooper;


@RunWith(RobolectricTestRunner.class)
public class MyListFragmentTest {
    private static final int TESTED_CLICKED_INDEX = 4;

    MyListFragment_ myListFragment;

    FragmentManager fragmentManager;

    @Test
    public void isItemClickAvailableFromListFragment() {
        startFragment(myListFragment);
        ListView listView = ((ListView) (myListFragment.internalFindViewById(list)));
        long itemId = listView.getAdapter().getItemId(MyListFragmentTest.TESTED_CLICKED_INDEX);
        View view = listView.getChildAt(MyListFragmentTest.TESTED_CLICKED_INDEX);
        assertThat(myListFragment.listItemClicked).isFalse();
        listView.performItemClick(view, MyListFragmentTest.TESTED_CLICKED_INDEX, itemId);
        assertThat(myListFragment.listItemClicked).isTrue();
    }

    @Test
    public void notIgnoredMethodIsCalled() {
        startFragment(myListFragment);
        Assert.assertFalse(myListFragment.didExecute);
        myListFragment.notIgnored();
        Assert.assertTrue(myListFragment.didExecute);
    }

    @Test
    public void uithreadMethodIsCalled() {
        startFragment(myListFragment);
        Assert.assertFalse(myListFragment.didExecute);
        myListFragment.uiThread();
        Assert.assertTrue(myListFragment.didExecute);
    }

    @Test
    public void uithreadMethodIsCanceled() {
        startFragment(myListFragment);
        ShadowLooper.pauseMainLooper();
        myListFragment.uiThreadWithId();
        UiThreadExecutor.cancelAll("id");
        ShadowLooper.unPauseMainLooper();
        Assert.assertFalse(myListFragment.uiThreadWithIdDidExecute);
    }

    @Test
    public void backgroundMethodIsCalled() {
        startFragment(myListFragment);
        Assert.assertFalse(myListFragment.didExecute);
        runBackgroundsOnSameThread();
        myListFragment.backgroundThread();
        Assert.assertTrue(myListFragment.didExecute);
    }

    @Test
    public void ignoredWhenDetachedWorksForUithreadMethod() {
        startFragment(myListFragment);
        popBackStack();
        Assert.assertFalse(myListFragment.didExecute);
        myListFragment.uiThreadIgnored();
        Assert.assertFalse(myListFragment.didExecute);
    }

    @Test
    public void ignoredWhenDetachedWorksForBackgroundMethod() {
        startFragment(myListFragment);
        popBackStack();
        Assert.assertFalse(myListFragment.didExecute);
        runBackgroundsOnSameThread();
        myListFragment.backgroundThreadIgnored();
        Assert.assertFalse(myListFragment.didExecute);
    }

    @Test
    public void ignoredWhenDetachedWorksForIgnoredMethod() {
        startFragment(myListFragment);
        popBackStack();
        Assert.assertFalse(myListFragment.didExecute);
        myListFragment.ignored();
        Assert.assertFalse(myListFragment.didExecute);
    }

    @Test
    public void ignoredBeforeOnCreateView() {
        Assert.assertFalse(myListFragment.didExecute);
        myListFragment.ignoreWhenViewDestroyed();
        Assert.assertFalse(myListFragment.didExecute);
    }

    @Test
    public void notIgnoredAfterOnCreateView() {
        startFragment(myListFragment);
        Assert.assertFalse(myListFragment.didExecute);
        myListFragment.onCreateView(null, null, null);
        Assert.assertFalse(myListFragment.didExecute);
        myListFragment.ignoreWhenViewDestroyed();
        Assert.assertTrue(myListFragment.didExecute);
    }

    @Test
    public void ignoredWhenViewDestroyedForIgnoredMethod() {
        startFragment(myListFragment);
        Assert.assertFalse(myListFragment.didExecute);
        myListFragment.onDestroyView();
        myListFragment.ignoreWhenViewDestroyed();
        Assert.assertFalse(myListFragment.didExecute);
    }

    @Test
    public void notIgnoredAfterFragmentRecreate() {
        startFragment(myListFragment);
        Assert.assertFalse(myListFragment.didExecute);
        myListFragment.onDestroyView();
        myListFragment.onCreateView(null, null, null);
        myListFragment.ignoreWhenViewDestroyed();
        Assert.assertTrue(myListFragment.didExecute);
    }

    @Test
    public void notIgnoredBeforeDetached() {
        startFragment(myListFragment);
        Assert.assertFalse(myListFragment.didExecute);
        myListFragment.ignored();
        Assert.assertTrue(myListFragment.didExecute);
    }

    @Test
    public void notIgnoredBeforeViewDestroyed() {
        startFragment(myListFragment);
        Assert.assertFalse(myListFragment.didExecute);
        myListFragment.ignoreWhenViewDestroyed();
        Assert.assertTrue(myListFragment.didExecute);
    }

    @Test
    public void layoutNotInjectedWithoutForce() {
        startFragment(myListFragment);
        View buttonInInjectedLayout = myListFragment.getView().findViewById(conventionButton);
        assertThat(buttonInInjectedLayout).isNull();
    }
}

