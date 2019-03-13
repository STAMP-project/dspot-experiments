/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.ce.taskprocessor;


import CeActivityDto.Status;
import java.util.Random;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.ce.task.CeTask;
import org.sonar.ce.task.CeTaskInterrupter;
import org.sonar.db.ce.CeActivityDto;


public class CeTaskInterrupterWorkerExecutionListenerTest {
    private CeTaskInterrupter ceTaskInterrupter = Mockito.mock(CeTaskInterrupter.class);

    private CeTaskInterrupterWorkerExecutionListener underTest = new CeTaskInterrupterWorkerExecutionListener(ceTaskInterrupter);

    @Test
    public void onStart_delegates_to_ceTaskInterrupter_onStart() {
        CeTask ceTask = Mockito.mock(CeTask.class);
        underTest.onStart(ceTask);
        Mockito.verify(ceTaskInterrupter).onStart(ArgumentMatchers.same(ceTask));
    }

    @Test
    public void onEnd_delegates_to_ceTaskInterrupter_onEnd() {
        CeTask ceTask = Mockito.mock(CeTask.class);
        CeActivityDto.Status randomStatus = Status.values()[new Random().nextInt(Status.values().length)];
        underTest.onEnd(ceTask, randomStatus, null, null);
        Mockito.verify(ceTaskInterrupter).onEnd(ArgumentMatchers.same(ceTask));
    }
}

