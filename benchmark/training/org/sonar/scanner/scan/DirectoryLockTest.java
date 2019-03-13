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
package org.sonar.scanner.scan;


import java.nio.file.Paths;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


public class DirectoryLockTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private DirectoryLock lock;

    @Test
    public void tryLock() {
        assertThat(temp.getRoot().list()).isEmpty();
        lock.tryLock();
        assertThat(temp.getRoot().toPath().resolve(".sonar_lock")).exists();
        lock.unlock();
    }

    @Test
    public void unlockWithoutLock() {
        lock.unlock();
    }

    @Test
    public void errorTryLock() {
        lock = new DirectoryLock(Paths.get("non", "existing", "path"));
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Failed to create lock");
        lock.tryLock();
    }
}

