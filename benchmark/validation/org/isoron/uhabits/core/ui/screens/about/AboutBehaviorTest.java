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
package org.isoron.uhabits.core.ui.screens.about;


import AboutBehavior.Screen;
import org.isoron.uhabits.core.BaseUnitTest;
import org.isoron.uhabits.core.preferences.Preferences;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class AboutBehaviorTest extends BaseUnitTest {
    private AboutBehavior behavior;

    @Mock
    private Preferences prefs;

    @Mock
    private Screen screen;

    @Test
    public void onPressDeveloperCountdown() throws Exception {
        behavior.onPressDeveloperCountdown();
        behavior.onPressDeveloperCountdown();
        behavior.onPressDeveloperCountdown();
        behavior.onPressDeveloperCountdown();
        Mockito.verifyZeroInteractions(screen);
        Mockito.verifyZeroInteractions(prefs);
        behavior.onPressDeveloperCountdown();
        Mockito.verify(screen).showMessage(Message.YOU_ARE_NOW_A_DEVELOPER);
        Mockito.verify(prefs).setDeveloper(true);
        behavior.onPressDeveloperCountdown();
        Mockito.verifyZeroInteractions(screen);
        Mockito.verifyZeroInteractions(prefs);
    }

    @Test
    public void onRateApp() throws Exception {
        behavior.onRateApp();
        Mockito.verify(screen).showRateAppWebsite();
    }

    @Test
    public void onSendFeedback() throws Exception {
        behavior.onSendFeedback();
        Mockito.verify(screen).showSendFeedbackScreen();
    }

    @Test
    public void onTranslateApp() throws Exception {
        behavior.onTranslateApp();
        Mockito.verify(screen).showTranslationWebsite();
    }

    @Test
    public void onViewSourceCode() throws Exception {
        behavior.onViewSourceCode();
        Mockito.verify(screen).showSourceCodeWebsite();
    }
}

