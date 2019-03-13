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
package org.sonar.server.issue.index;


import LoggerLevel.ERROR;
import ProjectIndexer.Cause.PROJECT_CREATION;
import ProjectIndexer.Cause.PROJECT_DELETION;
import ProjectIndexer.Cause.PROJECT_KEY_UPDATE;
import ProjectIndexer.Cause.PROJECT_TAGS_UPDATE;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.function.Predicate;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.utils.log.LogTester;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.es.EsQueueDto;
import org.sonar.db.issue.IssueDto;
import org.sonar.db.issue.IssueTesting;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.server.es.EsTester;
import org.sonar.server.es.IndexingResult;
import org.sonar.server.permission.index.AuthorizationScope;
import org.sonar.server.permission.index.IndexPermissions;


public class IssueIndexerTest {
    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public DbTester db = DbTester.create();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public LogTester logTester = new LogTester();

    private OrganizationDto organization;

    private IssueIndexer underTest = new IssueIndexer(es.client(), db.getDbClient(), new IssueIteratorFactory(db.getDbClient()));

    @Test
    public void test_getIndexTypes() {
        assertThat(underTest.getIndexTypes()).containsExactly(IssueIndexDefinition.INDEX_TYPE_ISSUE);
    }

    @Test
    public void test_getAuthorizationScope() {
        AuthorizationScope scope = underTest.getAuthorizationScope();
        assertThat(scope.getIndexType().getIndex()).isEqualTo(IssueIndexDefinition.INDEX_TYPE_ISSUE.getIndex());
        assertThat(scope.getIndexType().getType()).isEqualTo(TYPE_AUTHORIZATION);
        Predicate<IndexPermissions> projectPredicate = scope.getProjectPredicate();
        IndexPermissions project = new IndexPermissions("P1", Qualifiers.PROJECT);
        IndexPermissions file = new IndexPermissions("F1", Qualifiers.FILE);
        assertThat(projectPredicate.test(project)).isTrue();
        assertThat(projectPredicate.test(file)).isFalse();
    }

    @Test
    public void indexOnStartup_scrolls_db_and_adds_all_issues_to_index() {
        IssueDto issue1 = db.issues().insertIssue(organization);
        IssueDto issue2 = db.issues().insertIssue(organization);
        underTest.indexOnStartup(Collections.emptySet());
        assertThatIndexHasOnly(issue1, issue2);
    }

    @Test
    public void verify_indexed_fields() {
        RuleDefinitionDto rule = db.rules().insert();
        ComponentDto project = db.components().insertPrivateProject(organization);
        ComponentDto dir = db.components().insertComponent(ComponentTesting.newDirectory(project, "src/main/java/foo"));
        ComponentDto file = db.components().insertComponent(newFileDto(project, dir, "F1"));
        IssueDto issue = db.issues().insertIssue(IssueTesting.newIssue(rule, project, file));
        underTest.indexOnStartup(Collections.emptySet());
        IssueDoc doc = es.getDocuments(IssueIndexDefinition.INDEX_TYPE_ISSUE, IssueDoc.class).get(0);
        assertThat(doc.getId()).isEqualTo(issue.getKey());
        assertThat(doc.organizationUuid()).isEqualTo(organization.getUuid());
        assertThat(doc.assigneeUuid()).isEqualTo(issue.getAssigneeUuid());
        assertThat(doc.authorLogin()).isEqualTo(issue.getAuthorLogin());
        assertThat(doc.componentUuid()).isEqualTo(file.uuid());
        assertThat(doc.projectUuid()).isEqualTo(project.uuid());
        assertThat(doc.branchUuid()).isEqualTo(project.uuid());
        assertThat(doc.isMainBranch()).isTrue();
        assertThat(doc.closeDate()).isEqualTo(issue.getIssueCloseDate());
        assertThat(doc.creationDate()).isEqualToIgnoringMillis(issue.getIssueCreationDate());
        assertThat(doc.directoryPath()).isEqualTo(dir.path());
        assertThat(doc.filePath()).isEqualTo(file.path());
        assertThat(doc.getParent()).isEqualTo(project.uuid());
        assertThat(doc.getRouting()).isEqualTo(project.uuid());
        assertThat(doc.language()).isEqualTo(issue.getLanguage());
        assertThat(doc.line()).isEqualTo(issue.getLine());
        // functional date
        assertThat(doc.updateDate()).isEqualToIgnoringMillis(new Date(issue.getIssueUpdateTime()));
        assertThat(doc.getCwe()).containsExactlyInAnyOrder(SecurityStandardHelper.UNKNOWN_STANDARD);
        assertThat(doc.getOwaspTop10()).containsExactlyInAnyOrder(SecurityStandardHelper.UNKNOWN_STANDARD);
        assertThat(doc.getSansTop25()).isEmpty();
    }

    @Test
    public void verify_security_standards_indexation() {
        RuleDefinitionDto rule = db.rules().insert(( r) -> r.setSecurityStandards(new HashSet<>(Arrays.asList("cwe:123", "owaspTop10:a3", "cwe:863"))));
        ComponentDto project = db.components().insertPrivateProject(organization);
        ComponentDto dir = db.components().insertComponent(ComponentTesting.newDirectory(project, "src/main/java/foo"));
        ComponentDto file = db.components().insertComponent(newFileDto(project, dir, "F1"));
        IssueDto issue = db.issues().insertIssue(IssueTesting.newIssue(rule, project, file));
        underTest.indexOnStartup(Collections.emptySet());
        IssueDoc doc = es.getDocuments(IssueIndexDefinition.INDEX_TYPE_ISSUE, IssueDoc.class).get(0);
        assertThat(doc.getCwe()).containsExactlyInAnyOrder("123", "863");
        assertThat(doc.getOwaspTop10()).containsExactlyInAnyOrder("a3");
        assertThat(doc.getSansTop25()).containsExactlyInAnyOrder(SecurityStandardHelper.SANS_TOP_25_POROUS_DEFENSES);
    }

    @Test
    public void indexOnStartup_does_not_fail_on_errors_and_does_enable_recovery_mode() {
        es.lockWrites(IssueIndexDefinition.INDEX_TYPE_ISSUE);
        db.issues().insertIssue(organization);
        try {
            // FIXME : test also message
            expectedException.expect(IllegalStateException.class);
            underTest.indexOnStartup(Collections.emptySet());
        } finally {
            assertThatIndexHasSize(0);
            assertThatEsQueueTableHasSize(0);
            es.unlockWrites(IssueIndexDefinition.INDEX_TYPE_ISSUE);
        }
    }

    @Test
    public void indexOnAnalysis_indexes_the_issues_of_project() {
        RuleDefinitionDto rule = db.rules().insert();
        ComponentDto project = db.components().insertPrivateProject(organization);
        ComponentDto file = db.components().insertComponent(newFileDto(project));
        IssueDto issue = db.issues().insertIssue(IssueTesting.newIssue(rule, project, file));
        ComponentDto otherProject = db.components().insertPrivateProject(organization);
        ComponentDto fileOnOtherProject = db.components().insertComponent(newFileDto(otherProject));
        underTest.indexOnAnalysis(project.uuid());
        assertThatIndexHasOnly(issue);
    }

    @Test
    public void indexOnAnalysis_does_not_delete_orphan_docs() {
        RuleDefinitionDto rule = db.rules().insert();
        ComponentDto project = db.components().insertPrivateProject(organization);
        ComponentDto file = db.components().insertComponent(newFileDto(project));
        IssueDto issue = db.issues().insertIssue(IssueTesting.newIssue(rule, project, file));
        // orphan in the project
        addIssueToIndex(project.uuid(), "orphan");
        underTest.indexOnAnalysis(project.uuid());
        assertThat(es.getDocuments(IssueIndexDefinition.INDEX_TYPE_ISSUE)).extracting(SearchHit::getId).containsExactlyInAnyOrder(issue.getKey(), "orphan");
    }

    /**
     * Indexing recovery is handled by Compute Engine, without using
     * the table es_queue
     */
    @Test
    public void indexOnAnalysis_does_not_fail_on_errors_and_does_not_enable_recovery_mode() {
        es.lockWrites(IssueIndexDefinition.INDEX_TYPE_ISSUE);
        IssueDto issue = db.issues().insertIssue(organization);
        try {
            // FIXME : test also message
            expectedException.expect(IllegalStateException.class);
            underTest.indexOnAnalysis(issue.getProjectUuid());
        } finally {
            assertThatIndexHasSize(0);
            assertThatEsQueueTableHasSize(0);
            es.unlockWrites(IssueIndexDefinition.INDEX_TYPE_ISSUE);
        }
    }

    @Test
    public void index_is_not_updated_when_creating_project() {
        // it's impossible to already have an issue on a project
        // that is being created, but it's just to verify that
        // indexing is disabled
        IssueDto issue = db.issues().insertIssue(organization);
        IndexingResult result = indexProject(issue.getProjectUuid(), PROJECT_CREATION);
        assertThat(result.getTotal()).isEqualTo(0L);
        assertThatIndexHasSize(0);
    }

    @Test
    public void index_is_not_updated_when_updating_project_key() {
        // issue is inserted to verify that indexing of project is not triggered
        IssueDto issue = db.issues().insertIssue(organization);
        IndexingResult result = indexProject(issue.getProjectUuid(), PROJECT_KEY_UPDATE);
        assertThat(result.getTotal()).isEqualTo(0L);
        assertThatIndexHasSize(0);
    }

    @Test
    public void index_is_not_updated_when_updating_tags() {
        // issue is inserted to verify that indexing of project is not triggered
        IssueDto issue = db.issues().insertIssue(organization);
        IndexingResult result = indexProject(issue.getProjectUuid(), PROJECT_TAGS_UPDATE);
        assertThat(result.getTotal()).isEqualTo(0L);
        assertThatIndexHasSize(0);
    }

    @Test
    public void index_is_updated_when_deleting_project() {
        addIssueToIndex("P1", "I1");
        assertThatIndexHasSize(1);
        IndexingResult result = indexProject("P1", PROJECT_DELETION);
        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getSuccess()).isEqualTo(1L);
        assertThatIndexHasSize(0);
    }

    @Test
    public void errors_during_project_deletion_are_recovered() {
        addIssueToIndex("P1", "I1");
        assertThatIndexHasSize(1);
        es.lockWrites(IssueIndexDefinition.INDEX_TYPE_ISSUE);
        IndexingResult result = indexProject("P1", PROJECT_DELETION);
        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getFailures()).isEqualTo(1L);
        // index is still read-only, fail to recover
        result = recover();
        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getFailures()).isEqualTo(1L);
        assertThatIndexHasSize(1);
        es.unlockWrites(IssueIndexDefinition.INDEX_TYPE_ISSUE);
        result = recover();
        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getFailures()).isEqualTo(0L);
        assertThatIndexHasSize(0);
    }

    @Test
    public void commitAndIndexIssues_commits_db_transaction_and_adds_issues_to_index() {
        RuleDefinitionDto rule = db.rules().insert();
        ComponentDto project = db.components().insertPrivateProject(organization);
        ComponentDto file = db.components().insertComponent(newFileDto(project));
        // insert issues in db without committing
        IssueDto issue1 = IssueTesting.newIssue(rule, project, file);
        IssueDto issue2 = IssueTesting.newIssue(rule, project, file);
        db.getDbClient().issueDao().insert(db.getSession(), issue1, issue2);
        underTest.commitAndIndexIssues(db.getSession(), Arrays.asList(issue1, issue2));
        // issues are persisted and indexed
        assertThatIndexHasOnly(issue1, issue2);
        assertThatDbHasOnly(issue1, issue2);
        assertThatEsQueueTableHasSize(0);
    }

    @Test
    public void commitAndIndexIssues_removes_issue_from_index_if_it_does_not_exist_in_db() {
        IssueDto issue1 = new IssueDto().setKee("I1").setProjectUuid("P1");
        addIssueToIndex(issue1.getProjectUuid(), issue1.getKey());
        IssueDto issue2 = db.issues().insertIssue(organization);
        underTest.commitAndIndexIssues(db.getSession(), Arrays.asList(issue1, issue2));
        // issue1 is removed from index, issue2 is persisted and indexed
        assertThatIndexHasOnly(issue2);
        assertThatDbHasOnly(issue2);
        assertThatEsQueueTableHasSize(0);
    }

    @Test
    public void indexing_errors_during_commitAndIndexIssues_are_recovered() {
        RuleDefinitionDto rule = db.rules().insert();
        ComponentDto project = db.components().insertPrivateProject(organization);
        ComponentDto file = db.components().insertComponent(newFileDto(project));
        // insert issues in db without committing
        IssueDto issue1 = IssueTesting.newIssue(rule, project, file);
        IssueDto issue2 = IssueTesting.newIssue(rule, project, file);
        db.getDbClient().issueDao().insert(db.getSession(), issue1, issue2);
        // index is read-only
        es.lockWrites(IssueIndexDefinition.INDEX_TYPE_ISSUE);
        underTest.commitAndIndexIssues(db.getSession(), Arrays.asList(issue1, issue2));
        // issues are persisted but not indexed
        assertThatIndexHasSize(0);
        assertThatDbHasOnly(issue1, issue2);
        assertThatEsQueueTableHasSize(2);
        // re-enable write on index
        es.unlockWrites(IssueIndexDefinition.INDEX_TYPE_ISSUE);
        // emulate the recovery daemon
        IndexingResult result = recover();
        assertThatEsQueueTableHasSize(0);
        assertThatIndexHasOnly(issue1, issue2);
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getTotal()).isEqualTo(2L);
    }

    @Test
    public void recovery_does_not_fail_if_unsupported_docIdType() {
        EsQueueDto item = EsQueueDto.create(IssueIndexDefinition.INDEX_TYPE_ISSUE.format(), "I1", "unknown", "P1");
        db.getDbClient().esQueueDao().insert(db.getSession(), item);
        db.commit();
        recover();
        assertThat(logTester.logs(ERROR)).filteredOn(( l) -> l.contains("Unsupported es_queue.doc_id_type for issues. Manual fix is required: ")).hasSize(1);
        assertThatEsQueueTableHasSize(1);
    }

    @Test
    public void indexing_recovers_multiple_errors_on_the_same_issue() {
        es.lockWrites(IssueIndexDefinition.INDEX_TYPE_ISSUE);
        IssueDto issue = db.issues().insertIssue(organization);
        // three changes on the same issue
        underTest.commitAndIndexIssues(db.getSession(), Arrays.asList(issue));
        underTest.commitAndIndexIssues(db.getSession(), Arrays.asList(issue));
        underTest.commitAndIndexIssues(db.getSession(), Arrays.asList(issue));
        assertThatIndexHasSize(0);
        // three attempts of indexing are stored in es_queue recovery table
        assertThatEsQueueTableHasSize(3);
        es.unlockWrites(IssueIndexDefinition.INDEX_TYPE_ISSUE);
        recover();
        assertThatIndexHasOnly(issue);
        assertThatEsQueueTableHasSize(0);
    }

    @Test
    public void indexing_recovers_multiple_errors_on_the_same_project() {
        RuleDefinitionDto rule = db.rules().insert();
        ComponentDto project = db.components().insertPrivateProject(organization);
        ComponentDto file = db.components().insertComponent(newFileDto(project));
        IssueDto issue1 = db.issues().insertIssue(IssueTesting.newIssue(rule, project, file));
        IssueDto issue2 = db.issues().insertIssue(IssueTesting.newIssue(rule, project, file));
        es.lockWrites(IssueIndexDefinition.INDEX_TYPE_ISSUE);
        IndexingResult result = indexProject(project.uuid(), PROJECT_DELETION);
        assertThat(result.getTotal()).isEqualTo(2L);
        assertThat(result.getFailures()).isEqualTo(2L);
        // index is still read-only, fail to recover
        result = recover();
        assertThat(result.getTotal()).isEqualTo(2L);
        assertThat(result.getFailures()).isEqualTo(2L);
        assertThatIndexHasSize(0);
        es.unlockWrites(IssueIndexDefinition.INDEX_TYPE_ISSUE);
        result = recover();
        assertThat(result.getTotal()).isEqualTo(2L);
        assertThat(result.getFailures()).isEqualTo(0L);
        assertThatIndexHasSize(2);
        assertThatEsQueueTableHasSize(0);
    }

    @Test
    public void deleteByKeys_deletes_docs_by_keys() {
        addIssueToIndex("P1", "Issue1");
        addIssueToIndex("P1", "Issue2");
        addIssueToIndex("P1", "Issue3");
        addIssueToIndex("P2", "Issue4");
        assertThatIndexHasOnly("Issue1", "Issue2", "Issue3", "Issue4");
        underTest.deleteByKeys("P1", Arrays.asList("Issue1", "Issue2"));
        assertThatIndexHasOnly("Issue3", "Issue4");
    }

    @Test
    public void deleteByKeys_does_not_recover_from_errors() {
        addIssueToIndex("P1", "Issue1");
        es.lockWrites(IssueIndexDefinition.INDEX_TYPE_ISSUE);
        try {
            // FIXME : test also message
            expectedException.expect(IllegalStateException.class);
            underTest.deleteByKeys("P1", Arrays.asList("Issue1"));
        } finally {
            assertThatIndexHasOnly("Issue1");
            assertThatEsQueueTableHasSize(0);
            es.unlockWrites(IssueIndexDefinition.INDEX_TYPE_ISSUE);
        }
    }

    @Test
    public void nothing_to_do_when_delete_issues_on_empty_list() {
        addIssueToIndex("P1", "Issue1");
        addIssueToIndex("P1", "Issue2");
        addIssueToIndex("P1", "Issue3");
        underTest.deleteByKeys("P1", Collections.emptyList());
        assertThatIndexHasOnly("Issue1", "Issue2", "Issue3");
    }

    /**
     * This is a technical constraint, to ensure, that the indexers can be called in any order, during startup.
     */
    @Test
    public void parent_child_relationship_does_not_require_ordering_of_index_requests() {
        IssueDoc issueDoc = new IssueDoc();
        issueDoc.setKey("key");
        issueDoc.setProjectUuid("parent-does-not-exist");
        new IssueIndexer(es.client(), db.getDbClient(), new IssueIteratorFactory(db.getDbClient())).index(Arrays.asList(issueDoc).iterator());
        assertThat(es.countDocuments(IssueIndexDefinition.INDEX_TYPE_ISSUE)).isEqualTo(1L);
    }

    @Test
    public void index_issue_in_non_main_branch() {
        RuleDefinitionDto rule = db.rules().insert();
        ComponentDto project = db.components().insertPrivateProject(organization);
        ComponentDto branch = db.components().insertProjectBranch(project, ( b) -> b.setKey("feature/foo"));
        ComponentDto dir = db.components().insertComponent(ComponentTesting.newDirectory(branch, "src/main/java/foo"));
        ComponentDto file = db.components().insertComponent(newFileDto(branch, dir, "F1"));
        IssueDto issue = db.issues().insertIssue(IssueTesting.newIssue(rule, branch, file));
        underTest.indexOnStartup(Collections.emptySet());
        IssueDoc doc = es.getDocuments(IssueIndexDefinition.INDEX_TYPE_ISSUE, IssueDoc.class).get(0);
        assertThat(doc.getId()).isEqualTo(issue.getKey());
        assertThat(doc.organizationUuid()).isEqualTo(organization.getUuid());
        assertThat(doc.componentUuid()).isEqualTo(file.uuid());
        assertThat(doc.projectUuid()).isEqualTo(branch.getMainBranchProjectUuid());
        assertThat(doc.branchUuid()).isEqualTo(branch.uuid());
        assertThat(doc.isMainBranch()).isFalse();
    }
}

