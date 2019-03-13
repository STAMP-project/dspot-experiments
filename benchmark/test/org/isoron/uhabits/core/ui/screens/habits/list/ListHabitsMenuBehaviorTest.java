/**
 * Copyright (C) 2017 ?linson Santos Xavier <isoron@gmail.com>
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
package org.isoron.uhabits.core.ui.screens.habits.list;


import ListHabitsMenuBehavior.Adapter;
import ListHabitsMenuBehavior.Screen;
import junit.framework.TestCase;
import org.hamcrest.MatcherAssert;
import org.isoron.uhabits.core.BaseUnitTest;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ListHabitsMenuBehaviorTest extends BaseUnitTest {
    private ListHabitsMenuBehavior behavior;

    @Mock
    private Screen screen;

    @Mock
    private Adapter adapter;

    @Mock
    private Preferences prefs;

    @Mock
    private ThemeSwitcher themeSwitcher;

    @Captor
    private ArgumentCaptor<HabitMatcher> matcherCaptor;

    @Captor
    private ArgumentCaptor<HabitList.Order> orderCaptor;

    @Test
    public void testInitialFilter() {
        Mockito.when(prefs.getShowArchived()).thenReturn(true);
        Mockito.when(prefs.getShowCompleted()).thenReturn(true);
        behavior = new ListHabitsMenuBehavior(screen, adapter, prefs, themeSwitcher);
        Mockito.verify(adapter).setFilter(matcherCaptor.capture());
        Mockito.verify(adapter).refresh();
        Mockito.verifyNoMoreInteractions(adapter);
        Mockito.clearInvocations(adapter);
        TestCase.assertTrue(matcherCaptor.getValue().isArchivedAllowed());
        TestCase.assertTrue(matcherCaptor.getValue().isCompletedAllowed());
        Mockito.when(prefs.getShowArchived()).thenReturn(false);
        Mockito.when(prefs.getShowCompleted()).thenReturn(false);
        behavior = new ListHabitsMenuBehavior(screen, adapter, prefs, themeSwitcher);
        Mockito.verify(adapter).setFilter(matcherCaptor.capture());
        Mockito.verify(adapter).refresh();
        Mockito.verifyNoMoreInteractions(adapter);
        TestCase.assertFalse(matcherCaptor.getValue().isArchivedAllowed());
        TestCase.assertFalse(matcherCaptor.getValue().isCompletedAllowed());
    }

    @Test
    public void testOnCreateHabit() {
        behavior.onCreateHabit();
        Mockito.verify(screen).showCreateHabitScreen();
    }

    @Test
    public void testOnSortByColor() {
        behavior.onSortByColor();
        Mockito.verify(adapter).setOrder(orderCaptor.capture());
        MatcherAssert.assertThat(orderCaptor.getValue(), equalTo(BY_COLOR));
    }

    @Test
    public void testOnSortManually() {
        behavior.onSortByManually();
        Mockito.verify(adapter).setOrder(orderCaptor.capture());
        MatcherAssert.assertThat(orderCaptor.getValue(), equalTo(BY_POSITION));
    }

    @Test
    public void testOnSortScore() {
        behavior.onSortByScore();
        Mockito.verify(adapter).setOrder(orderCaptor.capture());
        MatcherAssert.assertThat(orderCaptor.getValue(), equalTo(BY_SCORE));
    }

    @Test
    public void testOnSortName() {
        behavior.onSortByName();
        Mockito.verify(adapter).setOrder(orderCaptor.capture());
        MatcherAssert.assertThat(orderCaptor.getValue(), equalTo(BY_NAME));
    }

    @Test
    public void testOnToggleShowArchived() {
        behavior.onToggleShowArchived();
        Mockito.verify(adapter).setFilter(matcherCaptor.capture());
        TestCase.assertTrue(matcherCaptor.getValue().isArchivedAllowed());
        Mockito.clearInvocations(adapter);
        behavior.onToggleShowArchived();
        Mockito.verify(adapter).setFilter(matcherCaptor.capture());
        TestCase.assertFalse(matcherCaptor.getValue().isArchivedAllowed());
    }

    @Test
    public void testOnToggleShowCompleted() {
        behavior.onToggleShowCompleted();
        Mockito.verify(adapter).setFilter(matcherCaptor.capture());
        TestCase.assertTrue(matcherCaptor.getValue().isCompletedAllowed());
        Mockito.clearInvocations(adapter);
        behavior.onToggleShowCompleted();
        Mockito.verify(adapter).setFilter(matcherCaptor.capture());
        TestCase.assertFalse(matcherCaptor.getValue().isCompletedAllowed());
    }

    @Test
    public void testOnViewAbout() {
        behavior.onViewAbout();
        Mockito.verify(screen).showAboutScreen();
    }

    @Test
    public void testOnViewFAQ() {
        behavior.onViewFAQ();
        Mockito.verify(screen).showFAQScreen();
    }

    @Test
    public void testOnViewSettings() {
        behavior.onViewSettings();
        Mockito.verify(screen).showSettingsScreen();
    }

    @Test
    public void testOnToggleNightMode() {
        behavior.onToggleNightMode();
        Mockito.verify(themeSwitcher).toggleNightMode();
        Mockito.verify(screen).applyTheme();
    }
}

