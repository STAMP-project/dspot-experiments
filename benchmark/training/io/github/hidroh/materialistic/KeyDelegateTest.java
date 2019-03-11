/**
 * Copyright (c) 2016 Ha Duy Trung
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
package io.github.hidroh.materialistic;


import KeyEvent.FLAG_CANCELED_LONG_PRESS;
import KeyEvent.KEYCODE_BACK;
import KeyEvent.KEYCODE_VOLUME_DOWN;
import KeyEvent.KEYCODE_VOLUME_UP;
import R.string.pref_volume;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.view.KeyEvent;
import io.github.hidroh.materialistic.test.TestRunner;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.android.controller.ActivityController;


@RunWith(TestRunner.class)
public class KeyDelegateTest {
    private KeyDelegate delegate;

    private ActivityController<Activity> controller;

    private Activity activity;

    private Scrollable scrollable = Mockito.mock(Scrollable.class);

    private AppBarLayout appBar = Mockito.mock(AppBarLayout.class);

    @Test
    public void testInterceptBack() {
        Assert.assertFalse(delegate.onKeyDown(KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK)));
        delegate.setBackInterceptor(() -> true);
        Assert.assertTrue(delegate.onKeyDown(KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK)));
        delegate.setBackInterceptor(() -> false);
        Assert.assertFalse(delegate.onKeyDown(KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK)));
    }

    @Test
    public void testOnKeyDown() {
        Assert.assertTrue(delegate.onKeyDown(KEYCODE_VOLUME_UP, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_VOLUME_UP)));
        Assert.assertTrue(delegate.onKeyDown(KEYCODE_VOLUME_DOWN, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_VOLUME_DOWN)));
        Assert.assertFalse(delegate.onKeyDown(KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK)));
    }

    @Test
    public void testOnKeyUp() {
        KeyEvent keyEvent = Mockito.mock(KeyEvent.class);
        Mockito.when(keyEvent.getFlags()).thenReturn(FLAG_CANCELED_LONG_PRESS);
        Assert.assertFalse(delegate.onKeyUp(KEYCODE_BACK, keyEvent));
        Assert.assertFalse(delegate.onKeyUp(KEYCODE_VOLUME_UP, keyEvent));
        Assert.assertFalse(delegate.onKeyUp(KEYCODE_VOLUME_DOWN, keyEvent));
        KeyEvent keyEventVolUp = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_VOLUME_UP);
        KeyEvent keyEventVolDown = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_VOLUME_DOWN);
        Assert.assertTrue(delegate.onKeyUp(KEYCODE_VOLUME_UP, keyEventVolUp));
        Mockito.verify(scrollable).scrollToPrevious();
        Mockito.verify(appBar).setExpanded(ArgumentMatchers.eq(true), ArgumentMatchers.anyBoolean());
        Mockito.reset(scrollable, appBar);
        Mockito.when(scrollable.scrollToPrevious()).thenReturn(true);
        Assert.assertTrue(delegate.onKeyUp(KEYCODE_VOLUME_UP, keyEventVolUp));
        Mockito.verify(scrollable).scrollToPrevious();
        Mockito.verify(appBar, Mockito.never()).setExpanded(ArgumentMatchers.eq(true), ArgumentMatchers.anyBoolean());
        Mockito.reset(scrollable, appBar);
        Mockito.when(appBar.getHeight()).thenReturn(10);
        Mockito.when(appBar.getBottom()).thenReturn(10);
        Assert.assertTrue(delegate.onKeyUp(KEYCODE_VOLUME_DOWN, keyEventVolDown));
        Mockito.verify(scrollable, Mockito.never()).scrollToNext();
        Mockito.verify(appBar).setExpanded(ArgumentMatchers.eq(false), ArgumentMatchers.anyBoolean());
        Mockito.reset(scrollable, appBar);
        Mockito.when(appBar.getHeight()).thenReturn(10);
        Mockito.when(appBar.getBottom()).thenReturn(0);
        Assert.assertTrue(delegate.onKeyUp(KEYCODE_VOLUME_DOWN, keyEventVolDown));
        Mockito.verify(scrollable).scrollToNext();
        Mockito.verify(appBar, Mockito.never()).setExpanded(ArgumentMatchers.eq(false), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testOnKeyLongPress() {
        Assert.assertFalse(delegate.onKeyLongPress(KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK)));
        Assert.assertTrue(delegate.onKeyLongPress(KEYCODE_VOLUME_UP, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_VOLUME_UP)));
        Mockito.verify(appBar).setExpanded(ArgumentMatchers.eq(true), ArgumentMatchers.anyBoolean());
        Mockito.verify(scrollable).scrollToTop();
        Assert.assertTrue(delegate.onKeyLongPress(KEYCODE_VOLUME_DOWN, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_VOLUME_DOWN)));
    }

    @Test
    public void testNoBinding() {
        delegate.setScrollable(null, null);
        Assert.assertTrue(delegate.onKeyUp(KEYCODE_VOLUME_UP, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_VOLUME_UP)));
        Assert.assertTrue(delegate.onKeyUp(KEYCODE_VOLUME_DOWN, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_VOLUME_DOWN)));
        Assert.assertTrue(delegate.onKeyLongPress(KEYCODE_VOLUME_UP, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_VOLUME_UP)));
        Assert.assertTrue(delegate.onKeyLongPress(KEYCODE_VOLUME_DOWN, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_VOLUME_DOWN)));
    }

    @Test
    public void testOnKeyDownDisabled() {
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean(activity.getString(pref_volume), false).apply();
        Assert.assertFalse(delegate.onKeyDown(KEYCODE_VOLUME_UP, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_VOLUME_UP)));
    }

    @Test
    public void testOnKeyUpDisabled() {
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean(activity.getString(pref_volume), false).apply();
        Assert.assertFalse(delegate.onKeyUp(KEYCODE_VOLUME_UP, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_VOLUME_UP)));
    }

    @Test
    public void testOnKeyLongPressDisabled() {
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean(activity.getString(pref_volume), false).apply();
        Assert.assertFalse(delegate.onKeyLongPress(KEYCODE_VOLUME_UP, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_VOLUME_UP)));
    }
}

