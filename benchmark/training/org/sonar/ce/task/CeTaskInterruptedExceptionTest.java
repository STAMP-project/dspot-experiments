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
package org.sonar.ce.task;


import CeActivityDto.Status;
import org.junit.Test;
import org.sonar.db.ce.CeActivityDto;


public class CeTaskInterruptedExceptionTest {
    @Test
    public void isCauseInterruptedException_returns_CeTaskInterruptedException_or_subclass() {
        String message = randomAlphabetic(50);
        CeActivityDto.Status status = CeTaskInterruptedExceptionTest.randomStatus();
        CeTaskInterruptedException e1 = new CeTaskInterruptedException(message, status) {};
        CeTaskInterruptedException e2 = new CeTaskInterruptedExceptionTest.CeTaskInterruptedExceptionSubclass(message, status);
        assertThat(CeTaskInterruptedException.isTaskInterruptedException(e1)).contains(e1);
        assertThat(CeTaskInterruptedException.isTaskInterruptedException(e2)).contains(e2);
        assertThat(CeTaskInterruptedException.isTaskInterruptedException(new RuntimeException())).isEmpty();
        assertThat(CeTaskInterruptedException.isTaskInterruptedException(new Exception())).isEmpty();
    }

    @Test
    public void isCauseInterruptedException_returns_CeTaskInterruptedException_or_subclass_in_cause_chain() {
        String message = randomAlphabetic(50);
        CeActivityDto.Status status = CeTaskInterruptedExceptionTest.randomStatus();
        CeTaskInterruptedException e1 = new CeTaskInterruptedException(message, status) {};
        CeTaskInterruptedException e2 = new CeTaskInterruptedExceptionTest.CeTaskInterruptedExceptionSubclass(message, status);
        assertThat(CeTaskInterruptedException.isTaskInterruptedException(new RuntimeException(e1))).contains(e1);
        assertThat(CeTaskInterruptedException.isTaskInterruptedException(new Exception(new RuntimeException(e2)))).contains(e2);
    }

    private static class CeTaskInterruptedExceptionSubclass extends CeTaskInterruptedException {
        public CeTaskInterruptedExceptionSubclass(String message, CeActivityDto.Status status) {
            super(message, status);
        }
    }
}

