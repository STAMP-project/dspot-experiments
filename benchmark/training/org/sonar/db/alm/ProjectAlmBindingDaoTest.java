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
package org.sonar.db.alm;


import actual.createdAt;
import actual.githubSlug;
import actual.projectUuid;
import actual.updatedAt;
import actual.url;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import org.assertj.core.api.AbstractAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.core.util.UuidFactory;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;


public class ProjectAlmBindingDaoTest {
    private static final String A_UUID = "abcde1234";

    private static final String ANOTHER_UUID = "xyz789";

    private static final String EMPTY_STRING = "";

    private static final String A_REPO = "my_repo";

    private static final String ANOTHER_REPO = "another_repo";

    private static final String A_GITHUB_SLUG = null;

    private static final String ANOTHER_GITHUB_SLUG = "example/foo";

    private static final String A_URL = "foo url";

    private static final String ANOTHER_URL = "bar url";

    private static final long DATE = 1600000000000L;

    private static final long DATE_LATER = 1700000000000L;

    private System2 system2 = Mockito.mock(System2.class);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester dbTester = DbTester.create(system2);

    private DbClient dbClient = dbTester.getDbClient();

    private DbSession dbSession = dbTester.getSession();

    private UuidFactory uuidFactory = Mockito.mock(UuidFactory.class);

    private ProjectAlmBindingDao underTest = new ProjectAlmBindingDao(system2, uuidFactory);

    @Test
    public void insert_throws_NPE_if_alm_is_null() {
        expectAlmNPE();
        underTest.insertOrUpdate(dbSession, null, ProjectAlmBindingDaoTest.A_REPO, ProjectAlmBindingDaoTest.A_UUID, ProjectAlmBindingDaoTest.A_GITHUB_SLUG, ProjectAlmBindingDaoTest.A_URL);
    }

    @Test
    public void insert_throws_IAE_if_repo_id_is_null() {
        expectRepoIdNullOrEmptyIAE();
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, null, ProjectAlmBindingDaoTest.A_UUID, ProjectAlmBindingDaoTest.A_GITHUB_SLUG, ProjectAlmBindingDaoTest.A_URL);
    }

    @Test
    public void insert_throws_IAE_if_repo_id_is_empty() {
        expectRepoIdNullOrEmptyIAE();
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, ProjectAlmBindingDaoTest.EMPTY_STRING, ProjectAlmBindingDaoTest.A_UUID, ProjectAlmBindingDaoTest.A_GITHUB_SLUG, ProjectAlmBindingDaoTest.A_URL);
    }

    @Test
    public void insert_throws_IAE_if_project_uuid_is_null() {
        expectProjectUuidNullOrEmptyIAE();
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, ProjectAlmBindingDaoTest.A_REPO, null, ProjectAlmBindingDaoTest.A_GITHUB_SLUG, ProjectAlmBindingDaoTest.A_URL);
    }

    @Test
    public void insert_throws_IAE_if_project_uuid_is_empty() {
        expectProjectUuidNullOrEmptyIAE();
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, ProjectAlmBindingDaoTest.A_REPO, ProjectAlmBindingDaoTest.EMPTY_STRING, ProjectAlmBindingDaoTest.A_GITHUB_SLUG, ProjectAlmBindingDaoTest.A_URL);
    }

    @Test
    public void insert_throws_IAE_if_url_is_null() {
        expectUrlNullOrEmptyIAE();
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, ProjectAlmBindingDaoTest.A_REPO, ProjectAlmBindingDaoTest.A_UUID, ProjectAlmBindingDaoTest.A_GITHUB_SLUG, null);
    }

    @Test
    public void insert_throws_IAE_if_url_is_empty() {
        expectUrlNullOrEmptyIAE();
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, ProjectAlmBindingDaoTest.A_REPO, ProjectAlmBindingDaoTest.A_UUID, ProjectAlmBindingDaoTest.A_GITHUB_SLUG, ProjectAlmBindingDaoTest.EMPTY_STRING);
    }

    @Test
    public void insert() {
        Mockito.when(system2.now()).thenReturn(ProjectAlmBindingDaoTest.DATE);
        Mockito.when(uuidFactory.create()).thenReturn("uuid1");
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, ProjectAlmBindingDaoTest.A_REPO, ProjectAlmBindingDaoTest.A_UUID, ProjectAlmBindingDaoTest.A_GITHUB_SLUG, ProjectAlmBindingDaoTest.A_URL);
        assertThatProjectAlmBinding(ALM.GITHUB, ProjectAlmBindingDaoTest.A_REPO).hasProjectUuid(ProjectAlmBindingDaoTest.A_UUID).hasGithubSlug(ProjectAlmBindingDaoTest.A_GITHUB_SLUG).hasUrl(ProjectAlmBindingDaoTest.A_URL).hasCreatedAt(ProjectAlmBindingDaoTest.DATE).hasUpdatedAt(ProjectAlmBindingDaoTest.DATE);
    }

    @Test
    public void update() {
        Mockito.when(system2.now()).thenReturn(ProjectAlmBindingDaoTest.DATE);
        Mockito.when(uuidFactory.create()).thenReturn("uuid1");
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, ProjectAlmBindingDaoTest.A_REPO, ProjectAlmBindingDaoTest.A_UUID, ProjectAlmBindingDaoTest.A_GITHUB_SLUG, ProjectAlmBindingDaoTest.A_URL);
        Mockito.when(system2.now()).thenReturn(ProjectAlmBindingDaoTest.DATE_LATER);
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, ProjectAlmBindingDaoTest.A_REPO, ProjectAlmBindingDaoTest.ANOTHER_UUID, ProjectAlmBindingDaoTest.ANOTHER_GITHUB_SLUG, ProjectAlmBindingDaoTest.ANOTHER_URL);
        assertThatProjectAlmBinding(ALM.GITHUB, ProjectAlmBindingDaoTest.A_REPO).hasProjectUuid(ProjectAlmBindingDaoTest.ANOTHER_UUID).hasGithubSlug(ProjectAlmBindingDaoTest.ANOTHER_GITHUB_SLUG).hasUrl(ProjectAlmBindingDaoTest.ANOTHER_URL).hasCreatedAt(ProjectAlmBindingDaoTest.DATE).hasUpdatedAt(ProjectAlmBindingDaoTest.DATE_LATER);
    }

    @Test
    public void insert_multiple() {
        Mockito.when(system2.now()).thenReturn(ProjectAlmBindingDaoTest.DATE);
        Mockito.when(uuidFactory.create()).thenReturn("uuid1").thenReturn("uuid2");
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, ProjectAlmBindingDaoTest.A_REPO, ProjectAlmBindingDaoTest.A_UUID, ProjectAlmBindingDaoTest.A_GITHUB_SLUG, ProjectAlmBindingDaoTest.A_URL);
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, ProjectAlmBindingDaoTest.ANOTHER_REPO, ProjectAlmBindingDaoTest.ANOTHER_UUID, ProjectAlmBindingDaoTest.ANOTHER_GITHUB_SLUG, ProjectAlmBindingDaoTest.ANOTHER_URL);
        assertThatProjectAlmBinding(ALM.GITHUB, ProjectAlmBindingDaoTest.A_REPO).hasProjectUuid(ProjectAlmBindingDaoTest.A_UUID).hasGithubSlug(ProjectAlmBindingDaoTest.A_GITHUB_SLUG).hasUrl(ProjectAlmBindingDaoTest.A_URL).hasCreatedAt(ProjectAlmBindingDaoTest.DATE).hasUpdatedAt(ProjectAlmBindingDaoTest.DATE);
        assertThatProjectAlmBinding(ALM.GITHUB, ProjectAlmBindingDaoTest.ANOTHER_REPO).hasProjectUuid(ProjectAlmBindingDaoTest.ANOTHER_UUID).hasGithubSlug(ProjectAlmBindingDaoTest.ANOTHER_GITHUB_SLUG).hasUrl(ProjectAlmBindingDaoTest.ANOTHER_URL).hasCreatedAt(ProjectAlmBindingDaoTest.DATE).hasUpdatedAt(ProjectAlmBindingDaoTest.DATE);
    }

    @Test
    public void select_by_repo_id() {
        Mockito.when(system2.now()).thenReturn(ProjectAlmBindingDaoTest.DATE);
        Mockito.when(uuidFactory.create()).thenReturn("uuid1").thenReturn("uuid2").thenReturn("uuid3");
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, ProjectAlmBindingDaoTest.A_REPO, ProjectAlmBindingDaoTest.A_UUID, ProjectAlmBindingDaoTest.A_GITHUB_SLUG, ProjectAlmBindingDaoTest.A_URL);
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, ProjectAlmBindingDaoTest.ANOTHER_REPO, ProjectAlmBindingDaoTest.ANOTHER_UUID, null, ProjectAlmBindingDaoTest.ANOTHER_URL);
        underTest.insertOrUpdate(dbSession, ALM.BITBUCKETCLOUD, ProjectAlmBindingDaoTest.ANOTHER_REPO, "foo", null, "http://foo");
        assertThat(underTest.selectByRepoId(dbSession, ALM.GITHUB, "foo")).isNotPresent();
        Optional<ProjectAlmBindingDto> dto = underTest.selectByRepoId(dbSession, ALM.GITHUB, ProjectAlmBindingDaoTest.A_REPO);
        assertThat(dto).isPresent();
        assertThat(dto.get().getUuid()).isEqualTo("uuid1");
        assertThat(dto.get().getAlm()).isEqualTo(ALM.GITHUB);
        assertThat(dto.get().getRepoId()).isEqualTo(ProjectAlmBindingDaoTest.A_REPO);
        assertThat(dto.get().getProjectUuid()).isEqualTo(ProjectAlmBindingDaoTest.A_UUID);
        assertThat(dto.get().getUrl()).isEqualTo(ProjectAlmBindingDaoTest.A_URL);
        assertThat(dto.get().getGithubSlug()).isEqualTo(ProjectAlmBindingDaoTest.A_GITHUB_SLUG);
    }

    @Test
    public void select_by_project_uuid() {
        Mockito.when(system2.now()).thenReturn(ProjectAlmBindingDaoTest.DATE);
        Mockito.when(uuidFactory.create()).thenReturn("uuid1").thenReturn("uuid2").thenReturn("uuid3");
        underTest.insertOrUpdate(dbSession, ALM.BITBUCKETCLOUD, ProjectAlmBindingDaoTest.A_REPO, ProjectAlmBindingDaoTest.A_UUID, ProjectAlmBindingDaoTest.A_GITHUB_SLUG, ProjectAlmBindingDaoTest.A_URL);
        underTest.insertOrUpdate(dbSession, ALM.BITBUCKETCLOUD, ProjectAlmBindingDaoTest.ANOTHER_REPO, ProjectAlmBindingDaoTest.ANOTHER_UUID, null, ProjectAlmBindingDaoTest.ANOTHER_URL);
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, ProjectAlmBindingDaoTest.ANOTHER_REPO, "foo", null, "http://foo");
        assertThat(underTest.selectByProjectUuid(dbSession, "missing")).isNotPresent();
        Optional<ProjectAlmBindingDto> dto = underTest.selectByProjectUuid(dbSession, ProjectAlmBindingDaoTest.A_UUID);
        assertThat(dto).isPresent();
        assertThat(dto.get().getUuid()).isEqualTo("uuid1");
        assertThat(dto.get().getAlm()).isEqualTo(ALM.BITBUCKETCLOUD);
        assertThat(dto.get().getRepoId()).isEqualTo(ProjectAlmBindingDaoTest.A_REPO);
        assertThat(dto.get().getProjectUuid()).isEqualTo(ProjectAlmBindingDaoTest.A_UUID);
        assertThat(dto.get().getUrl()).isEqualTo(ProjectAlmBindingDaoTest.A_URL);
        assertThat(dto.get().getGithubSlug()).isEqualTo(ProjectAlmBindingDaoTest.A_GITHUB_SLUG);
    }

    @Test
    public void select_by_repo_ids() {
        Mockito.when(system2.now()).thenReturn(ProjectAlmBindingDaoTest.DATE);
        Mockito.when(uuidFactory.create()).thenReturn("uuid1").thenReturn("uuid2").thenReturn("uuid3");
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, ProjectAlmBindingDaoTest.A_REPO, ProjectAlmBindingDaoTest.A_UUID, ProjectAlmBindingDaoTest.A_GITHUB_SLUG, ProjectAlmBindingDaoTest.A_URL);
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, ProjectAlmBindingDaoTest.ANOTHER_REPO, ProjectAlmBindingDaoTest.ANOTHER_UUID, null, ProjectAlmBindingDaoTest.ANOTHER_URL);
        underTest.insertOrUpdate(dbSession, ALM.BITBUCKETCLOUD, ProjectAlmBindingDaoTest.ANOTHER_REPO, "foo", null, "http://foo");
        assertThat(underTest.selectByRepoIds(dbSession, ALM.GITHUB, Arrays.asList(ProjectAlmBindingDaoTest.A_REPO, ProjectAlmBindingDaoTest.ANOTHER_REPO, "foo"))).extracting(ProjectAlmBindingDto::getUuid, ( t) -> t.getAlm(), ProjectAlmBindingDto::getRepoId, ProjectAlmBindingDto::getProjectUuid, ProjectAlmBindingDto::getUrl, ProjectAlmBindingDto::getGithubSlug).containsExactlyInAnyOrder(tuple("uuid1", ALM.GITHUB, ProjectAlmBindingDaoTest.A_REPO, ProjectAlmBindingDaoTest.A_UUID, ProjectAlmBindingDaoTest.A_URL, ProjectAlmBindingDaoTest.A_GITHUB_SLUG), tuple("uuid2", ALM.GITHUB, ProjectAlmBindingDaoTest.ANOTHER_REPO, ProjectAlmBindingDaoTest.ANOTHER_UUID, ProjectAlmBindingDaoTest.ANOTHER_URL, null));
    }

    @Test
    public void findProjectKey_throws_NPE_when_alm_is_null() {
        expectAlmNPE();
        underTest.findProjectKey(dbSession, null, ProjectAlmBindingDaoTest.A_REPO);
    }

    @Test
    public void findProjectKey_throws_IAE_when_repo_id_is_null() {
        expectRepoIdNullOrEmptyIAE();
        underTest.findProjectKey(dbSession, ALM.GITHUB, null);
    }

    @Test
    public void findProjectKey_throws_IAE_when_repo_id_is_empty() {
        expectRepoIdNullOrEmptyIAE();
        underTest.findProjectKey(dbSession, ALM.GITHUB, ProjectAlmBindingDaoTest.EMPTY_STRING);
    }

    @Test
    public void findProjectKey_returns_empty_when_entry_does_not_exist_in_DB() {
        assertThat(underTest.findProjectKey(dbSession, ALM.GITHUB, ProjectAlmBindingDaoTest.A_REPO)).isEmpty();
    }

    @Test
    public void findProjectKey_returns_projectKey_when_entry_exists() {
        String projectKey = randomAlphabetic(10);
        ComponentDto project = createProject(projectKey);
        Mockito.when(uuidFactory.create()).thenReturn("uuid1");
        underTest.insertOrUpdate(dbSession, ALM.GITHUB, ProjectAlmBindingDaoTest.A_REPO, project.projectUuid(), ProjectAlmBindingDaoTest.A_GITHUB_SLUG, ProjectAlmBindingDaoTest.A_URL);
        assertThat(underTest.findProjectKey(dbSession, ALM.GITHUB, ProjectAlmBindingDaoTest.A_REPO)).contains(projectKey);
    }

    private static class ProjectAlmBindingAssert extends AbstractAssert<ProjectAlmBindingDaoTest.ProjectAlmBindingAssert, ProjectAlmBindingDaoTest.ProjectAlmBinding> {
        private ProjectAlmBindingAssert(DbTester dbTester, DbSession dbSession, ALM alm, String repoId) {
            super(ProjectAlmBindingDaoTest.ProjectAlmBindingAssert.asProjectAlmBinding(dbTester, dbSession, alm, repoId), ProjectAlmBindingDaoTest.ProjectAlmBindingAssert.class);
        }

        private static ProjectAlmBindingDaoTest.ProjectAlmBinding asProjectAlmBinding(DbTester dbTester, DbSession dbSession, ALM alm, String repoId) {
            List<Map<String, Object>> rows = dbTester.select(dbSession, ((((("select" + (((" project_uuid as \"projectUuid\", github_slug as \"githubSlug\", url as \"url\", " + " created_at as \"createdAt\", updated_at as \"updatedAt\"") + " from project_alm_bindings") + " where alm_id='")) + (alm.getId())) + "' and repo_id='") + repoId) + "'"));
            if (rows.isEmpty()) {
                return null;
            }
            if ((rows.size()) > 1) {
                throw new IllegalStateException("Unique index violation");
            }
            return new ProjectAlmBindingDaoTest.ProjectAlmBinding(((String) (rows.get(0).get("projectUuid"))), ((String) (rows.get(0).get("githubSlug"))), ((String) (rows.get(0).get("url"))), ((Long) (rows.get(0).get("createdAt"))), ((Long) (rows.get(0).get("updatedAt"))));
        }

        public void doesNotExist() {
            isNull();
        }

        ProjectAlmBindingDaoTest.ProjectAlmBindingAssert hasProjectUuid(String expected) {
            isNotNull();
            if (!(Objects.equals(projectUuid, expected))) {
                failWithMessage("Expected Project ALM Binding to have column PROJECT_UUID to be <%s> but was <%s>", expected, projectUuid);
            }
            return this;
        }

        ProjectAlmBindingDaoTest.ProjectAlmBindingAssert hasGithubSlug(String expected) {
            isNotNull();
            if (!(Objects.equals(githubSlug, expected))) {
                failWithMessage("Expected Project ALM Binding to have column GITHUB_SLUG to be <%s> but was <%s>", expected, githubSlug);
            }
            return this;
        }

        ProjectAlmBindingDaoTest.ProjectAlmBindingAssert hasUrl(String expected) {
            isNotNull();
            if (!(Objects.equals(url, expected))) {
                failWithMessage("Expected Project ALM Binding to have column URL to be <%s> but was <%s>", expected, url);
            }
            return this;
        }

        ProjectAlmBindingDaoTest.ProjectAlmBindingAssert hasCreatedAt(long expected) {
            isNotNull();
            if (!(Objects.equals(createdAt, expected))) {
                failWithMessage("Expected Project ALM Binding to have column CREATED_AT to be <%s> but was <%s>", expected, createdAt);
            }
            return this;
        }

        ProjectAlmBindingDaoTest.ProjectAlmBindingAssert hasUpdatedAt(long expected) {
            isNotNull();
            if (!(Objects.equals(updatedAt, expected))) {
                failWithMessage("Expected Project ALM Binding to have column UPDATED_AT to be <%s> but was <%s>", expected, updatedAt);
            }
            return this;
        }
    }

    private static final class ProjectAlmBinding {
        private final String projectUuid;

        private final String githubSlug;

        private final String url;

        private final Long createdAt;

        private final Long updatedAt;

        ProjectAlmBinding(@Nullable
        String projectUuid, @Nullable
        String githubSlug, @Nullable
        String url, @Nullable
        Long createdAt, @Nullable
        Long updatedAt) {
            this.projectUuid = projectUuid;
            this.githubSlug = githubSlug;
            this.url = url;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
    }
}

