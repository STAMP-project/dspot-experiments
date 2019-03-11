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
package org.isoron.uhabits.core.ui.screens.habits.show;


import ShowHabitMenuBehavior.Screen;
import ShowHabitMenuBehavior.System;
import java.io.File;
import java.nio.file.Files;
import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.isoron.uhabits.core.BaseUnitTest;
import org.isoron.uhabits.core.models.Habit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ShowHabitMenuBehaviorTest extends BaseUnitTest {
    private System system;

    private Screen screen;

    private Habit habit;

    private ShowHabitMenuBehavior menu;

    @Test
    public void testOnEditHabit() {
        menu.onEditHabit();
        Mockito.verify(screen).showEditHabitScreen(habit);
    }

    @Test
    public void testOnExport() throws Exception {
        File outputDir = Files.createTempDirectory("CSV").toFile();
        Mockito.when(system.getCSVOutputDir()).thenReturn(outputDir);
        menu.onExportCSV();
        Assert.assertThat(FileUtils.listFiles(outputDir, null, false).size(), CoreMatchers.equalTo(1));
        FileUtils.deleteDirectory(outputDir);
    }
}

