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
package org.sonar.ce.queue;


import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.db.DbTester;


public class PurgeCeActivitiesTest {
    private System2 system2 = Mockito.mock(System2.class);

    @Rule
    public DbTester dbTester = DbTester.create(system2);

    private PurgeCeActivities underTest = new PurgeCeActivities(dbTester.getDbClient(), system2);

    @Test
    public void delete_activity_older_than_180_days_and_their_scanner_context() {
        LocalDateTime now = LocalDateTime.now();
        insertWithDate("VERY_OLD", now.minusDays(180).minusMonths(10));
        insertWithDate("JUST_OLD_ENOUGH", now.minusDays(180).minusDays(1));
        insertWithDate("NOT_OLD_ENOUGH", now.minusDays(180));
        insertWithDate("RECENT", now.minusDays(1));
        Mockito.when(system2.now()).thenReturn(now.toInstant(ZoneOffset.UTC).toEpochMilli());
        underTest.start();
        assertThat(selectActivity("VERY_OLD").isPresent()).isFalse();
        assertThat(selectTaskInput("VERY_OLD").isPresent()).isFalse();
        assertThat(selectTaskCharecteristic("VERY_OLD")).hasSize(0);
        assertThat(scannerContextExists("VERY_OLD")).isFalse();
        assertThat(selectActivity("JUST_OLD_ENOUGH").isPresent()).isFalse();
        assertThat(selectTaskInput("JUST_OLD_ENOUGH").isPresent()).isFalse();
        assertThat(selectTaskCharecteristic("JUST_OLD_ENOUGH")).hasSize(0);
        assertThat(scannerContextExists("JUST_OLD_ENOUGH")).isFalse();
        assertThat(selectActivity("NOT_OLD_ENOUGH").isPresent()).isTrue();
        assertThat(selectTaskInput("NOT_OLD_ENOUGH").isPresent()).isTrue();
        assertThat(selectTaskCharecteristic("NOT_OLD_ENOUGH")).hasSize(1);
        assertThat(scannerContextExists("NOT_OLD_ENOUGH")).isFalse();// because more than 4 weeks old

        assertThat(selectActivity("RECENT").isPresent()).isTrue();
        assertThat(selectTaskInput("RECENT").isPresent()).isTrue();
        assertThat(selectTaskCharecteristic("RECENT")).hasSize(1);
        assertThat(scannerContextExists("RECENT")).isTrue();
    }

    @Test
    public void delete_ce_scanner_context_older_than_28_days() {
        LocalDateTime now = LocalDateTime.now();
        insertWithDate("VERY_OLD", now.minusDays(28).minusMonths(12));
        insertWithDate("JUST_OLD_ENOUGH", now.minusDays(28).minusDays(1));
        insertWithDate("NOT_OLD_ENOUGH", now.minusDays(28));
        insertWithDate("RECENT", now.minusDays(1));
        Mockito.when(system2.now()).thenReturn(now.toInstant(ZoneOffset.UTC).toEpochMilli());
        underTest.start();
        assertThat(scannerContextExists("VERY_OLD")).isFalse();
        assertThat(scannerContextExists("JUST_OLD_ENOUGH")).isFalse();
        assertThat(scannerContextExists("NOT_OLD_ENOUGH")).isTrue();
        assertThat(scannerContextExists("RECENT")).isTrue();
    }
}

