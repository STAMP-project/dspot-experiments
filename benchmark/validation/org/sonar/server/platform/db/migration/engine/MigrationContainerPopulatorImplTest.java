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
package org.sonar.server.platform.db.migration.engine;


import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.server.platform.db.migration.history.MigrationHistory;
import org.sonar.server.platform.db.migration.step.MigrationStep;
import org.sonar.server.platform.db.migration.step.MigrationStepRegistry;
import org.sonar.server.platform.db.migration.step.MigrationSteps;
import org.sonar.server.platform.db.migration.step.MigrationStepsExecutorImpl;
import org.sonar.server.platform.db.migration.step.RegisteredMigrationStep;
import org.sonar.server.platform.db.migration.version.DbVersion;


public class MigrationContainerPopulatorImplTest {
    private MigrationContainer migrationContainer = new SimpleMigrationContainer();

    private MigrationSteps migrationSteps = Mockito.mock(MigrationSteps.class);

    private MigrationContainerPopulatorImpl underTest = new MigrationContainerPopulatorImpl();

    @Test
    public void populateContainer_adds_components_of_DbVersion_getSupportComponents() {
        MigrationContainerPopulatorImpl underTest = new MigrationContainerPopulatorImpl(new MigrationContainerPopulatorImplTest.NoRegistryDbVersion() {
            @Override
            public Stream<Object> getSupportComponents() {
                return Stream.of(MigrationContainerPopulatorImplTest.Clazz2.class);
            }
        }, new MigrationContainerPopulatorImplTest.NoRegistryDbVersion(), new MigrationContainerPopulatorImplTest.NoRegistryDbVersion() {
            @Override
            public Stream<Object> getSupportComponents() {
                return Stream.of(MigrationContainerPopulatorImplTest.Clazz1.class, MigrationContainerPopulatorImplTest.Clazz3.class);
            }
        });
        Mockito.when(migrationSteps.readAll()).thenReturn(Collections.emptyList());
        underTest.populateContainer(migrationContainer);
        assertThat(migrationContainer.getComponentsByType(MigrationContainerPopulatorImplTest.Clazz1.class)).isNotNull();
        assertThat(migrationContainer.getComponentsByType(MigrationContainerPopulatorImplTest.Clazz2.class)).isNotNull();
        assertThat(migrationContainer.getComponentsByType(MigrationContainerPopulatorImplTest.Clazz3.class)).isNotNull();
    }

    @Test
    public void populateContainer_adds_MigrationStepsExecutorImpl() {
        Mockito.when(migrationSteps.readAll()).thenReturn(Collections.emptyList());
        // add MigrationStepsExecutorImpl's dependencies
        migrationContainer.add(Mockito.mock(MigrationHistory.class));
        underTest.populateContainer(migrationContainer);
        assertThat(migrationContainer.getComponentByType(MigrationStepsExecutorImpl.class)).isNotNull();
    }

    @Test
    public void populateContainer_adds_classes_of_all_steps_defined_in_MigrationSteps() {
        Mockito.when(migrationSteps.readAll()).thenReturn(Arrays.asList(new RegisteredMigrationStep(1, "foo", MigrationContainerPopulatorImplTest.MigrationStep1.class), new RegisteredMigrationStep(2, "bar", MigrationContainerPopulatorImplTest.MigrationStep2.class), new RegisteredMigrationStep(3, "dor", MigrationContainerPopulatorImplTest.MigrationStep3.class)));
        underTest.populateContainer(migrationContainer);
        assertThat(migrationContainer.getComponentsByType(MigrationContainerPopulatorImplTest.MigrationStep1.class)).isNotNull();
        assertThat(migrationContainer.getComponentsByType(MigrationContainerPopulatorImplTest.MigrationStep2.class)).isNotNull();
        assertThat(migrationContainer.getComponentsByType(MigrationContainerPopulatorImplTest.MigrationStep3.class)).isNotNull();
    }

    @Test
    public void populateCotnainer_does_not_fail_if_same_class_is_used_for_more_than_one_migration() {
        Mockito.when(migrationSteps.readAll()).thenReturn(Arrays.asList(new RegisteredMigrationStep(1, "foo", MigrationContainerPopulatorImplTest.MigrationStep1.class), new RegisteredMigrationStep(2, "bar", MigrationContainerPopulatorImplTest.MigrationStep2.class), new RegisteredMigrationStep(3, "bar2", MigrationContainerPopulatorImplTest.MigrationStep2.class), new RegisteredMigrationStep(4, "foo2", MigrationContainerPopulatorImplTest.MigrationStep1.class), new RegisteredMigrationStep(5, "dor", MigrationContainerPopulatorImplTest.MigrationStep3.class)));
        underTest.populateContainer(migrationContainer);
        assertThat(migrationContainer.getComponentsByType(MigrationContainerPopulatorImplTest.MigrationStep1.class)).isNotNull();
        assertThat(migrationContainer.getComponentsByType(MigrationContainerPopulatorImplTest.MigrationStep2.class)).isNotNull();
        assertThat(migrationContainer.getComponentsByType(MigrationContainerPopulatorImplTest.MigrationStep3.class)).isNotNull();
    }

    private abstract static class NoopMigrationStep implements MigrationStep {
        @Override
        public void execute() throws SQLException {
            throw new UnsupportedOperationException("execute not implemented");
        }
    }

    public static final class MigrationStep1 extends MigrationContainerPopulatorImplTest.NoopMigrationStep {}

    public static final class MigrationStep2 extends MigrationContainerPopulatorImplTest.NoopMigrationStep {}

    public static final class MigrationStep3 extends MigrationContainerPopulatorImplTest.NoopMigrationStep {}

    public static final class Clazz1 {}

    public static final class Clazz2 {}

    public static final class Clazz3 {}

    /**
     * An implementation of DbVersion to be passed to {@link MigrationContainerPopulatorImpl}'s constructor which is
     * not supposed to call the {@link DbVersion#addSteps(MigrationStepRegistry)} method and therefor has an
     * implementation that throws a {@link UnsupportedOperationException} when called.
     */
    private static class NoRegistryDbVersion implements DbVersion {
        @Override
        public void addSteps(MigrationStepRegistry registry) {
            throw new UnsupportedOperationException("addSteps is not supposed to be called");
        }
    }
}

