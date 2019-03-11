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
package org.sonar.db.mapping;


import java.util.Map;
import java.util.Objects;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.assertj.core.api.AbstractAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.core.util.SequenceUuidFactory;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;


public class ProjectMappingsDaoTest {
    private static final String EMPTY_STRING = "";

    private static final String A_KEY_TYPE = "a_key_type";

    private static final String A_KEY = "a_key";

    private static final String ANOTHER_KEY = "another_key";

    private static final long DATE = 1600000000000L;

    private static final String PROJECT_UUID = "123456789";

    private static final String OTHER_PROJECT_UUID = "987654321";

    private System2 system2 = Mockito.mock(System2.class);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester dbTester = DbTester.create(system2);

    private DbSession dbSession = dbTester.getSession();

    private ProjectMappingsDao underTest = new ProjectMappingsDao(system2, new SequenceUuidFactory());

    @Test
    public void put_throws_IAE_if_key_type_is_null() {
        expectKeyTypeNullOrEmptyIAE();
        underTest.put(dbSession, null, ProjectMappingsDaoTest.A_KEY, ProjectMappingsDaoTest.PROJECT_UUID);
    }

    @Test
    public void put_throws_IAE_if_key_is_null() {
        expectKeyNullOrEmptyIAE();
        underTest.put(dbSession, ProjectMappingsDaoTest.A_KEY_TYPE, null, ProjectMappingsDaoTest.PROJECT_UUID);
    }

    @Test
    public void put_throws_IAE_if_key_is_empty() {
        expectKeyNullOrEmptyIAE();
        underTest.put(dbSession, ProjectMappingsDaoTest.A_KEY_TYPE, ProjectMappingsDaoTest.EMPTY_STRING, ProjectMappingsDaoTest.PROJECT_UUID);
    }

    @Test
    public void save_throws_IAE_if_project_uuid_is_null() {
        expectValueNullOrEmptyIAE();
        underTest.put(dbSession, ProjectMappingsDaoTest.A_KEY_TYPE, ProjectMappingsDaoTest.A_KEY, null);
    }

    @Test
    public void put_throws_IAE_if_project_uuid_is_empty() {
        expectValueNullOrEmptyIAE();
        underTest.put(dbSession, ProjectMappingsDaoTest.A_KEY_TYPE, ProjectMappingsDaoTest.A_KEY, ProjectMappingsDaoTest.EMPTY_STRING);
    }

    @Test
    public void put() {
        Mockito.when(system2.now()).thenReturn(ProjectMappingsDaoTest.DATE);
        underTest.put(dbSession, ProjectMappingsDaoTest.A_KEY_TYPE, ProjectMappingsDaoTest.A_KEY, ProjectMappingsDaoTest.PROJECT_UUID);
        assertThatProjectMapping(ProjectMappingsDaoTest.A_KEY_TYPE, ProjectMappingsDaoTest.A_KEY).hasProjectUuid(ProjectMappingsDaoTest.PROJECT_UUID).hasCreatedAt(ProjectMappingsDaoTest.DATE);
    }

    @Test
    public void clear() {
        Mockito.when(system2.now()).thenReturn(ProjectMappingsDaoTest.DATE);
        underTest.put(dbSession, ProjectMappingsDaoTest.A_KEY_TYPE, ProjectMappingsDaoTest.A_KEY, ProjectMappingsDaoTest.PROJECT_UUID);
        assertThatProjectMapping(ProjectMappingsDaoTest.A_KEY_TYPE, ProjectMappingsDaoTest.A_KEY).hasProjectUuid(ProjectMappingsDaoTest.PROJECT_UUID).hasCreatedAt(ProjectMappingsDaoTest.DATE);
        underTest.clear(dbSession, ProjectMappingsDaoTest.A_KEY_TYPE, ProjectMappingsDaoTest.A_KEY);
        assertThat(underTest.get(dbSession, ProjectMappingsDaoTest.A_KEY_TYPE, ProjectMappingsDaoTest.A_KEY)).isEmpty();
    }

    @Test
    public void putMultiple() {
        Mockito.when(system2.now()).thenReturn(ProjectMappingsDaoTest.DATE);
        underTest.put(dbSession, ProjectMappingsDaoTest.A_KEY_TYPE, ProjectMappingsDaoTest.A_KEY, ProjectMappingsDaoTest.PROJECT_UUID);
        underTest.put(dbSession, ProjectMappingsDaoTest.A_KEY_TYPE, ProjectMappingsDaoTest.ANOTHER_KEY, ProjectMappingsDaoTest.OTHER_PROJECT_UUID);
        assertThatProjectMapping(ProjectMappingsDaoTest.A_KEY_TYPE, ProjectMappingsDaoTest.A_KEY).hasProjectUuid(ProjectMappingsDaoTest.PROJECT_UUID).hasCreatedAt(ProjectMappingsDaoTest.DATE);
        assertThatProjectMapping(ProjectMappingsDaoTest.A_KEY_TYPE, ProjectMappingsDaoTest.ANOTHER_KEY).hasProjectUuid(ProjectMappingsDaoTest.OTHER_PROJECT_UUID).hasCreatedAt(ProjectMappingsDaoTest.DATE);
    }

    @Test
    public void get_throws_IAE_when_key_type_is_null() {
        expectKeyTypeNullOrEmptyIAE();
        underTest.get(dbSession, null, ProjectMappingsDaoTest.A_KEY);
    }

    @Test
    public void get_throws_IAE_when_key_is_null() {
        expectKeyNullOrEmptyIAE();
        underTest.get(dbSession, ProjectMappingsDaoTest.A_KEY_TYPE, null);
    }

    @Test
    public void get_throws_IAE_when_key_is_empty() {
        expectKeyNullOrEmptyIAE();
        underTest.get(dbSession, ProjectMappingsDaoTest.A_KEY_TYPE, ProjectMappingsDaoTest.EMPTY_STRING);
    }

    @Test
    public void get_returns_empty_optional_when_mapping_does_not_exist_in_DB() {
        assertThat(underTest.get(dbSession, ProjectMappingsDaoTest.A_KEY_TYPE, ProjectMappingsDaoTest.A_KEY)).isEmpty();
    }

    @Test
    public void get_returns_project_uuid_when_mapping_has_project_uuid_stored() {
        underTest.put(dbSession, ProjectMappingsDaoTest.A_KEY_TYPE, ProjectMappingsDaoTest.A_KEY, ProjectMappingsDaoTest.PROJECT_UUID);
        assertThat(underTest.get(dbSession, ProjectMappingsDaoTest.A_KEY_TYPE, ProjectMappingsDaoTest.A_KEY).get().getProjectUuid()).isEqualTo(ProjectMappingsDaoTest.PROJECT_UUID);
    }

    private static class ProjectMappingAssert extends AbstractAssert<ProjectMappingsDaoTest.ProjectMappingAssert, ProjectMappingsDaoTest.ProjectMapping> {
        private ProjectMappingAssert(DbTester dbTester, DbSession dbSession, String internalMappingKeyType, String internalMappingKey) {
            super(ProjectMappingsDaoTest.ProjectMappingAssert.asProjectMapping(dbTester, dbSession, internalMappingKeyType, internalMappingKey), ProjectMappingsDaoTest.ProjectMappingAssert.class);
        }

        private static ProjectMappingsDaoTest.ProjectMapping asProjectMapping(DbTester dbTester, DbSession dbSession, String projectMappingKeyType, String projectMappingKey) {
            Map<String, Object> row = dbTester.selectFirst(dbSession, ((((("select" + ((" project_uuid as \"projectUuid\", created_at as \"createdAt\"" + " from project_mappings") + " where key_type='")) + projectMappingKeyType) + "' and kee='") + projectMappingKey) + "'"));
            return new ProjectMappingsDaoTest.ProjectMapping(((String) (row.get("projectUuid"))), ((Long) (row.get("createdAt"))));
        }

        public void doesNotExist() {
            isNull();
        }

        public ProjectMappingsDaoTest.ProjectMappingAssert hasProjectUuid(String expected) {
            isNotNull();
            if (!(Objects.equals(actual.getProjectUuid(), expected))) {
                failWithMessage("Expected Internal mapping to have column VALUE to be <%s> but was <%s>", true, actual.getProjectUuid());
            }
            return this;
        }

        public ProjectMappingsDaoTest.ProjectMappingAssert hasCreatedAt(long expected) {
            isNotNull();
            if (!(Objects.equals(actual.getCreatedAt(), expected))) {
                failWithMessage("Expected Internal mapping to have column CREATED_AT to be <%s> but was <%s>", expected, actual.getCreatedAt());
            }
            return this;
        }
    }

    private static final class ProjectMapping {
        private final String projectUuid;

        private final Long createdAt;

        public ProjectMapping(@Nullable
        String projectUuid, @Nullable
        Long createdAt) {
            this.projectUuid = projectUuid;
            this.createdAt = createdAt;
        }

        @CheckForNull
        public String getProjectUuid() {
            return projectUuid;
        }

        @CheckForNull
        public Long getCreatedAt() {
            return createdAt;
        }
    }
}

