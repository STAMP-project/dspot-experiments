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
package org.isoron.uhabits.core.io;


import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.isoron.uhabits.core.BaseUnitTest;
import org.junit.Test;


public class HabitsCSVExporterTest extends BaseUnitTest {
    private File baseDir;

    @Test
    public void testExportCSV() throws IOException {
        List<Habit> selected = new LinkedList<>();
        for (Habit h : habitList)
            selected.add(h);

        HabitsCSVExporter exporter = new HabitsCSVExporter(habitList, selected, baseDir);
        String filename = exporter.writeArchive();
        assertAbsolutePathExists(filename);
        File archive = new File(filename);
        unzip(archive);
        assertPathExists("Habits.csv");
        assertPathExists("001 Wake up early");
        assertPathExists("001 Wake up early/Checkmarks.csv");
        assertPathExists("001 Wake up early/Scores.csv");
        assertPathExists("002 Meditate/Checkmarks.csv");
        assertPathExists("002 Meditate/Scores.csv");
        assertPathExists("Checkmarks.csv");
        assertPathExists("Scores.csv");
    }
}

