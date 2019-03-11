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


import ListHabitsBehavior.BugReporter;
import ListHabitsBehavior.DirFinder;
import ListHabitsBehavior.Screen;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.isoron.uhabits.core.BaseUnitTest;
import org.isoron.uhabits.core.models.Habit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ListHabitsBehaviorTest extends BaseUnitTest {
    @Mock
    private DirFinder dirFinder;

    @Mock
    private Preferences prefs;

    private ListHabitsBehavior behavior;

    @Mock
    private Screen screen;

    private Habit habit1;

    private Habit habit2;

    @Captor
    ArgumentCaptor<ListHabitsBehavior.NumberPickerCallback> picker;

    @Mock
    private BugReporter bugReporter;

    @Test
    public void testOnEdit() {
        behavior.onEdit(habit2, DateUtils.getToday());
        Mockito.verify(screen).showNumberPicker(ArgumentMatchers.eq(0.1), ArgumentMatchers.eq("miles"), picker.capture());
        picker.getValue().onNumberPicked(100);
        MatcherAssert.assertThat(habit2.getCheckmarks().getTodayValue(), CoreMatchers.equalTo(100000));
    }

    @Test
    public void testOnExportCSV() throws Exception {
        File outputDir = Files.createTempDirectory("CSV").toFile();
        Mockito.when(dirFinder.getCSVOutputDir()).thenReturn(outputDir);
        behavior.onExportCSV();
        Mockito.verify(screen).showSendFileScreen(ArgumentMatchers.any());
        MatcherAssert.assertThat(FileUtils.listFiles(outputDir, null, false).size(), CoreMatchers.equalTo(1));
        FileUtils.deleteDirectory(outputDir);
    }

    @Test
    public void testOnExportCSV_fail() throws Exception {
        File outputDir = Files.createTempDirectory("CSV").toFile();
        outputDir.setWritable(false);
        Mockito.when(dirFinder.getCSVOutputDir()).thenReturn(outputDir);
        behavior.onExportCSV();
        Mockito.verify(screen).showMessage(Message.COULD_NOT_EXPORT);
        Assert.assertTrue(outputDir.delete());
    }

    @Test
    public void testOnHabitClick() {
        behavior.onClickHabit(habit1);
        Mockito.verify(screen).showHabitScreen(habit1);
    }

    @Test
    public void testOnHabitReorder() {
        Habit from = habit1;
        Habit to = habit2;
        behavior.onReorderHabit(from, to);
        Mockito.verify(habitList).reorder(from, to);
    }

    @Test
    public void testOnRepairDB() {
        behavior.onRepairDB();
        Mockito.verify(habitList).repair();
        Mockito.verify(screen).showMessage(Message.DATABASE_REPAIRED);
    }

    @Test
    public void testOnSendBugReport() throws IOException {
        Mockito.when(bugReporter.getBugReport()).thenReturn("hello");
        behavior.onSendBugReport();
        Mockito.verify(bugReporter).dumpBugReportToFile();
        Mockito.verify(screen).showSendBugReportToDeveloperScreen("hello");
        Mockito.when(bugReporter.getBugReport()).thenThrow(new IOException());
        behavior.onSendBugReport();
        Mockito.verify(screen).showMessage(Message.COULD_NOT_GENERATE_BUG_REPORT);
    }

    @Test
    public void testOnStartup_firstLaunch() {
        Timestamp today = DateUtils.getToday();
        Mockito.when(prefs.isFirstRun()).thenReturn(true);
        behavior.onStartup();
        Mockito.verify(prefs).setFirstRun(false);
        Mockito.verify(prefs).updateLastHint((-1), today);
        Mockito.verify(screen).showIntroScreen();
    }

    @Test
    public void testOnStartup_notFirstLaunch() {
        Mockito.when(prefs.isFirstRun()).thenReturn(false);
        behavior.onStartup();
        Mockito.verify(prefs).incrementLaunchCount();
    }

    @Test
    public void testOnToggle() {
        Assert.assertTrue(habit1.isCompletedToday());
        behavior.onToggle(habit1, DateUtils.getToday());
        Assert.assertFalse(habit1.isCompletedToday());
    }
}

