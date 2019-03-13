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
package org.sonar.server.platform.db.migration.step;


import java.util.Random;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.sonar.server.platform.db.migration.version.DbVersion;


public class MigrationStepsProviderTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private InternalMigrationStepRegistry internalMigrationStepRegistry = Mockito.mock(InternalMigrationStepRegistry.class);

    private MigrationStepsProvider underTest = new MigrationStepsProvider();

    @Test
    public void provide_throws_ISE_with_registry_build_throws_ISE_because_it_is_empty() {
        IllegalStateException expected = new IllegalStateException("faking ISE because registry is empty");
        Mockito.when(internalMigrationStepRegistry.build()).thenThrow(expected);
        expectedException.expect(expected.getClass());
        expectedException.expectMessage(expected.getMessage());
        underTest.provide(internalMigrationStepRegistry);
    }

    @Test
    public void provide_calls_DbVersion_addStep_in_order_and_only_once() {
        DbVersion dbVersion1 = MigrationStepsProviderTest.newMockFailingOnSecondBuildCall();
        DbVersion dbVersion2 = MigrationStepsProviderTest.newMockFailingOnSecondBuildCall();
        DbVersion dbVersion3 = MigrationStepsProviderTest.newMockFailingOnSecondBuildCall();
        InOrder inOrder = Mockito.inOrder(dbVersion1, dbVersion2, dbVersion3);
        MigrationSteps expected = Mockito.mock(MigrationSteps.class);
        Mockito.when(internalMigrationStepRegistry.build()).thenReturn(expected);
        assertThat(underTest.provide(internalMigrationStepRegistry, dbVersion1, dbVersion2, dbVersion3)).isSameAs(expected);
        inOrder.verify(dbVersion1).addSteps(internalMigrationStepRegistry);
        inOrder.verify(dbVersion2).addSteps(internalMigrationStepRegistry);
        inOrder.verify(dbVersion3).addSteps(internalMigrationStepRegistry);
        inOrder.verifyNoMoreInteractions();
        // calling a second time with another argument, it's just ignored
        DbVersion dbVersion4 = MigrationStepsProviderTest.newMockFailingOnSecondBuildCall();
        assertThat(underTest.provide(internalMigrationStepRegistry, dbVersion4)).isSameAs(expected);
        Mockito.verifyZeroInteractions(dbVersion4);
    }

    @Test
    public void provide_always_returns_the_same_MigrationSteps_instance_and_calls_registry_build_only_once() {
        MigrationSteps migrationSteps = Mockito.mock(MigrationSteps.class);
        Mockito.when(internalMigrationStepRegistry.build()).thenReturn(migrationSteps).thenThrow(new RuntimeException("method build should not be called twice"));
        for (int i = 0; i < ((Math.abs(new Random().nextInt(50))) + 1); i++) {
            assertThat(underTest.provide(internalMigrationStepRegistry)).isSameAs(migrationSteps);
        }
    }
}

