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
package org.isoron.uhabits.core.tasks;


import org.isoron.uhabits.core.BaseUnitTest;
import org.isoron.uhabits.core.SingleThreadTaskRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class SingleThreadTaskRunnerTest extends BaseUnitTest {
    private SingleThreadTaskRunner runner;

    private Task task;

    @Test
    public void test() {
        runner.execute(task);
        InOrder inOrder = Mockito.inOrder(task);
        inOrder.verify(task).onAttached(runner);
        inOrder.verify(task).onPreExecute();
        inOrder.verify(task).doInBackground();
        inOrder.verify(task).onPostExecute();
    }
}

