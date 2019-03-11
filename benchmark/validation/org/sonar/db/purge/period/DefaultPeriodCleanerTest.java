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
package org.sonar.db.purge.period;


import System2.INSTANCE;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.sonar.db.DbSession;
import org.sonar.db.purge.IdUuidPair;
import org.sonar.db.purge.PurgeDao;
import org.sonar.db.purge.PurgeProfiler;
import org.sonar.db.purge.PurgeableAnalysisDto;


public class DefaultPeriodCleanerTest {
    @Test
    public void doClean() {
        PurgeDao dao = Mockito.mock(PurgeDao.class);
        DbSession session = Mockito.mock(DbSession.class);
        Mockito.when(dao.selectPurgeableAnalyses("uuid_123", session)).thenReturn(Arrays.asList(new PurgeableAnalysisDto().setAnalysisId(999).setAnalysisUuid("u999").setDate(INSTANCE.now()), new PurgeableAnalysisDto().setAnalysisId(456).setAnalysisUuid("u456").setDate(INSTANCE.now())));
        Filter filter1 = newFirstSnapshotInListFilter();
        Filter filter2 = newFirstSnapshotInListFilter();
        PurgeProfiler profiler = new PurgeProfiler();
        DefaultPeriodCleaner cleaner = new DefaultPeriodCleaner(dao, profiler);
        cleaner.doClean("uuid_123", Arrays.asList(filter1, filter2), session);
        InOrder inOrder = Mockito.inOrder(dao, filter1, filter2);
        inOrder.verify(filter1).log();
        inOrder.verify(dao, Mockito.times(1)).deleteAnalyses(ArgumentMatchers.eq(session), ArgumentMatchers.eq(profiler), ArgumentMatchers.eq(ImmutableList.of(new IdUuidPair(999, "u999"))));
        inOrder.verify(filter2).log();
        inOrder.verify(dao, Mockito.times(1)).deleteAnalyses(ArgumentMatchers.eq(session), ArgumentMatchers.eq(profiler), ArgumentMatchers.eq(ImmutableList.of(new IdUuidPair(456, "u456"))));
        inOrder.verifyNoMoreInteractions();
    }
}

