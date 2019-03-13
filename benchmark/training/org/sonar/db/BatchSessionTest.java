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
package org.sonar.db;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class BatchSessionTest {
    @Test
    public void shouldCommitWhenReachingBatchSize() {
        DbSession mybatisSession = Mockito.mock(DbSession.class);
        BatchSession session = new BatchSession(mybatisSession, 10);
        for (int i = 0; i < 9; i++) {
            session.insert(("id" + i));
            Mockito.verify(mybatisSession).insert(("id" + i));
            Mockito.verify(mybatisSession, Mockito.never()).commit();
            Mockito.verify(mybatisSession, Mockito.never()).commit(ArgumentMatchers.anyBoolean());
        }
        session.insert("id9");
        Mockito.verify(mybatisSession).commit();
        session.close();
    }

    @Test
    public void shouldCommitWhenReachingBatchSizeWithoutCommits() {
        DbSession mybatisSession = Mockito.mock(DbSession.class);
        BatchSession session = new BatchSession(mybatisSession, 10);
        for (int i = 0; i < 9; i++) {
            session.delete("delete something");
            Mockito.verify(mybatisSession, Mockito.never()).commit();
            Mockito.verify(mybatisSession, Mockito.never()).commit(ArgumentMatchers.anyBoolean());
        }
        session.delete("delete something");
        Mockito.verify(mybatisSession).commit();
        session.close();
    }

    @Test
    public void shouldResetCounterAfterCommit() {
        DbSession mybatisSession = Mockito.mock(DbSession.class);
        BatchSession session = new BatchSession(mybatisSession, 10);
        for (int i = 0; i < 35; i++) {
            session.insert(("id" + i));
        }
        Mockito.verify(mybatisSession, Mockito.times(3)).commit();
        session.close();
    }
}

