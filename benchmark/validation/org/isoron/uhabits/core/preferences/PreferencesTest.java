/**
 * Copyright (C) 2015-2017 ?linson Santos Xavier <isoron@gmail.com>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isoron.uhabits.core.preferences;


import HabitList.Order.BY_POSITION;
import HabitList.Order.BY_SCORE;
import Preferences.DEFAULT_SYNC_SERVER;
import Preferences.Listener;
import ThemeSwitcher.THEME_DARK;
import ThemeSwitcher.THEME_LIGHT;
import Timestamp.ZERO;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.isoron.uhabits.core.BaseUnitTest;
import org.isoron.uhabits.core.NonNull;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class PreferencesTest extends BaseUnitTest {
    @NonNull
    private Preferences prefs;

    @Mock
    private Listener listener;

    private PropertiesStorage storage;

    @Test
    public void testClear() throws Exception {
        prefs.setDefaultHabitColor(99);
        prefs.clear();
        MatcherAssert.assertThat(prefs.getDefaultHabitColor(0), IsEqual.equalTo(0));
    }

    @Test
    public void testHabitColor() throws Exception {
        MatcherAssert.assertThat(prefs.getDefaultHabitColor(999), IsEqual.equalTo(999));
        prefs.setDefaultHabitColor(10);
        MatcherAssert.assertThat(prefs.getDefaultHabitColor(999), IsEqual.equalTo(10));
    }

    @Test
    public void testDefaultOrder() throws Exception {
        MatcherAssert.assertThat(prefs.getDefaultOrder(), IsEqual.equalTo(BY_POSITION));
        prefs.setDefaultOrder(BY_SCORE);
        MatcherAssert.assertThat(prefs.getDefaultOrder(), IsEqual.equalTo(BY_SCORE));
        storage.putString("pref_default_order", "BOGUS");
        MatcherAssert.assertThat(prefs.getDefaultOrder(), IsEqual.equalTo(BY_POSITION));
        MatcherAssert.assertThat(storage.getString("pref_default_order", ""), IsEqual.equalTo("BY_POSITION"));
    }

    @Test
    public void testDefaultSpinnerPosition() throws Exception {
        MatcherAssert.assertThat(prefs.getDefaultScoreSpinnerPosition(), IsEqual.equalTo(1));
        prefs.setDefaultScoreSpinnerPosition(4);
        MatcherAssert.assertThat(prefs.getDefaultScoreSpinnerPosition(), IsEqual.equalTo(4));
        storage.putInt("pref_score_view_interval", 9000);
        MatcherAssert.assertThat(prefs.getDefaultScoreSpinnerPosition(), IsEqual.equalTo(1));
        MatcherAssert.assertThat(storage.getInt("pref_score_view_interval", 0), IsEqual.equalTo(1));
    }

    @Test
    public void testLastHint() throws Exception {
        MatcherAssert.assertThat(prefs.getLastHintNumber(), IsEqual.equalTo((-1)));
        Assert.assertNull(prefs.getLastHintTimestamp());
        prefs.updateLastHint(34, ZERO.plus(100));
        MatcherAssert.assertThat(prefs.getLastHintNumber(), IsEqual.equalTo(34));
        MatcherAssert.assertThat(prefs.getLastHintTimestamp(), IsEqual.equalTo(ZERO.plus(100)));
    }

    @Test
    public void testSync() throws Exception {
        MatcherAssert.assertThat(prefs.getLastSync(), IsEqual.equalTo(0L));
        prefs.setLastSync(100);
        MatcherAssert.assertThat(prefs.getLastSync(), IsEqual.equalTo(100L));
        MatcherAssert.assertThat(prefs.getSyncAddress(), IsEqual.equalTo(DEFAULT_SYNC_SERVER));
        prefs.setSyncAddress("example");
        MatcherAssert.assertThat(prefs.getSyncAddress(), IsEqual.equalTo("example"));
        Mockito.verify(listener).onSyncFeatureChanged();
        Mockito.reset(listener);
        MatcherAssert.assertThat(prefs.getSyncKey(), IsEqual.equalTo(""));
        prefs.setSyncKey("123");
        MatcherAssert.assertThat(prefs.getSyncKey(), IsEqual.equalTo("123"));
        Mockito.verify(listener).onSyncFeatureChanged();
        Mockito.reset(listener);
        Assert.assertFalse(prefs.isSyncEnabled());
        prefs.setSyncEnabled(true);
        Assert.assertTrue(prefs.isSyncEnabled());
        Mockito.verify(listener).onSyncFeatureChanged();
        Mockito.reset(listener);
        String id = prefs.getSyncClientId();
        Assert.assertFalse(id.isEmpty());
        MatcherAssert.assertThat(prefs.getSyncClientId(), IsEqual.equalTo(id));
    }

    @Test
    public void testTheme() throws Exception {
        MatcherAssert.assertThat(prefs.getTheme(), IsEqual.equalTo(THEME_LIGHT));
        prefs.setTheme(THEME_DARK);
        MatcherAssert.assertThat(prefs.getTheme(), IsEqual.equalTo(THEME_DARK));
        Assert.assertFalse(prefs.isPureBlackEnabled());
        prefs.setPureBlackEnabled(true);
        Assert.assertTrue(prefs.isPureBlackEnabled());
    }

    @Test
    public void testNotifications() throws Exception {
        Assert.assertFalse(prefs.shouldMakeNotificationsSticky());
        prefs.setNotificationsSticky(true);
        Assert.assertTrue(prefs.shouldMakeNotificationsSticky());
        Assert.assertFalse(prefs.shouldMakeNotificationsLed());
        prefs.setNotificationsLed(true);
        Assert.assertTrue(prefs.shouldMakeNotificationsLed());
        MatcherAssert.assertThat(prefs.getSnoozeInterval(), IsEqual.equalTo(15L));
        prefs.setSnoozeInterval(30);
        MatcherAssert.assertThat(prefs.getSnoozeInterval(), IsEqual.equalTo(30L));
    }

    @Test
    public void testAppVersionAndLaunch() throws Exception {
        MatcherAssert.assertThat(prefs.getLastAppVersion(), IsEqual.equalTo(0));
        prefs.setLastAppVersion(23);
        MatcherAssert.assertThat(prefs.getLastAppVersion(), IsEqual.equalTo(23));
        Assert.assertTrue(prefs.isFirstRun());
        prefs.setFirstRun(false);
        Assert.assertFalse(prefs.isFirstRun());
        MatcherAssert.assertThat(prefs.getLaunchCount(), IsEqual.equalTo(0));
        prefs.incrementLaunchCount();
        MatcherAssert.assertThat(prefs.getLaunchCount(), IsEqual.equalTo(1));
    }

    @Test
    public void testCheckmarks() throws Exception {
        Assert.assertFalse(prefs.isCheckmarkSequenceReversed());
        prefs.setCheckmarkSequenceReversed(true);
        Assert.assertTrue(prefs.isCheckmarkSequenceReversed());
        Assert.assertFalse(prefs.isShortToggleEnabled());
        prefs.setShortToggleEnabled(true);
        Assert.assertTrue(prefs.isShortToggleEnabled());
    }

    @Test
    public void testNumericalHabits() throws Exception {
        Assert.assertFalse(prefs.isNumericalHabitsFeatureEnabled());
        prefs.setNumericalHabitsFeatureEnabled(true);
        Assert.assertTrue(prefs.isNumericalHabitsFeatureEnabled());
    }

    @Test
    public void testDeveloper() throws Exception {
        Assert.assertFalse(prefs.isDeveloper());
        prefs.setDeveloper(true);
        Assert.assertTrue(prefs.isDeveloper());
    }

    @Test
    public void testFiltering() throws Exception {
        Assert.assertFalse(prefs.getShowArchived());
        Assert.assertTrue(prefs.getShowCompleted());
        prefs.setShowArchived(true);
        prefs.setShowCompleted(false);
        Assert.assertTrue(prefs.getShowArchived());
        Assert.assertFalse(prefs.getShowCompleted());
    }
}

