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
package org.sonar.ce.task.projectanalysis.purge;


import CoreProperties.PROFILING_LOG_PROPERTY;
import PurgeConstants.DAYS_BEFORE_DELETING_CLOSED_ISSUES;
import java.util.Collections;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.core.config.PurgeProperties;
import org.sonar.db.DbSession;
import org.sonar.db.purge.PurgeDao;
import org.sonar.db.purge.PurgeListener;
import org.sonar.db.purge.PurgeProfiler;
import org.sonar.db.purge.period.DefaultPeriodCleaner;


public class ProjectCleanerTest {
    private ProjectCleaner underTest;

    private PurgeDao dao = Mockito.mock(PurgeDao.class);

    private PurgeProfiler profiler = Mockito.mock(PurgeProfiler.class);

    private DefaultPeriodCleaner periodCleaner = Mockito.mock(DefaultPeriodCleaner.class);

    private PurgeListener purgeListener = Mockito.mock(PurgeListener.class);

    private MapSettings settings = new MapSettings(new org.sonar.api.config.PropertyDefinitions(PurgeProperties.all()));

    @Test
    public void no_profiling_when_property_is_false() {
        settings.setProperty(PROFILING_LOG_PROPERTY, false);
        underTest.purge(Mockito.mock(DbSession.class), "root", "project", settings.asConfig(), Collections.emptyList());
        Mockito.verify(profiler, Mockito.never()).dump(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
    }

    @Test
    public void profiling_when_property_is_true() {
        settings.setProperty(PROFILING_LOG_PROPERTY, true);
        underTest.purge(Mockito.mock(DbSession.class), "root", "project", settings.asConfig(), Collections.emptyList());
        Mockito.verify(profiler).dump(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
    }

    @Test
    public void call_period_cleaner_index_client_and_purge_dao() {
        settings.setProperty(DAYS_BEFORE_DELETING_CLOSED_ISSUES, 5);
        underTest.purge(Mockito.mock(DbSession.class), "root", "project", settings.asConfig(), Collections.emptyList());
        Mockito.verify(periodCleaner).clean(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(dao).purge(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }
}

