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
package org.sonar.server.qualityprofile.index;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.utils.System2;
import org.sonar.db.DbTester;
import org.sonar.db.es.EsQueueDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.qualityprofile.ActiveRuleDto;
import org.sonar.db.qualityprofile.QProfileDto;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.server.es.EsTester;
import org.sonar.server.rule.index.RuleIndexDefinition;


public class ActiveRuleIndexerTest {
    private System2 system2 = System2.INSTANCE;

    @Rule
    public DbTester db = DbTester.create(system2);

    @Rule
    public EsTester es = EsTester.create();

    private ActiveRuleIndexer underTest = new ActiveRuleIndexer(db.getDbClient(), es.client());

    private RuleDefinitionDto rule1;

    private RuleDefinitionDto rule2;

    private OrganizationDto org;

    private QProfileDto profile1;

    private QProfileDto profile2;

    @Test
    public void getIndexTypes() {
        assertThat(underTest.getIndexTypes()).containsExactly(RuleIndexDefinition.INDEX_TYPE_ACTIVE_RULE);
    }

    @Test
    public void indexOnStartup_does_nothing_if_no_data() {
        underTest.indexOnStartup(Collections.emptySet());
        assertThat(es.countDocuments(RuleIndexDefinition.INDEX_TYPE_ACTIVE_RULE)).isZero();
    }

    @Test
    public void indexOnStartup_indexes_all_data() {
        ActiveRuleDto activeRule = db.qualityProfiles().activateRule(profile1, rule1);
        underTest.indexOnStartup(Collections.emptySet());
        List<ActiveRuleDoc> docs = es.getDocuments(RuleIndexDefinition.INDEX_TYPE_ACTIVE_RULE, ActiveRuleDoc.class);
        assertThat(docs).hasSize(1);
        verify(docs.get(0), profile1, activeRule);
        assertThatEsQueueTableIsEmpty();
    }

    @Test
    public void test_commitAndIndex() {
        ActiveRuleDto ar1 = db.qualityProfiles().activateRule(profile1, rule1);
        ActiveRuleDto ar2 = db.qualityProfiles().activateRule(profile2, rule1);
        ActiveRuleDto ar3 = db.qualityProfiles().activateRule(profile2, rule2);
        commitAndIndex(rule1, ar1, ar2);
        verifyOnlyIndexed(ar1, ar2);
        assertThatEsQueueTableIsEmpty();
    }

    @Test
    public void commitAndIndex_empty_list() {
        ActiveRuleDto ar = db.qualityProfiles().activateRule(profile1, rule1);
        underTest.commitAndIndex(db.getSession(), Collections.emptyList());
        assertThat(es.countDocuments(RuleIndexDefinition.INDEX_TYPE_ACTIVE_RULE)).isEqualTo(0);
        assertThatEsQueueTableIsEmpty();
    }

    @Test
    public void commitAndIndex_keeps_elements_to_recover_in_ES_QUEUE_on_errors() {
        ActiveRuleDto ar = db.qualityProfiles().activateRule(profile1, rule1);
        es.lockWrites(RuleIndexDefinition.INDEX_TYPE_ACTIVE_RULE);
        commitAndIndex(rule1, ar);
        EsQueueDto expectedItem = EsQueueDto.create(RuleIndexDefinition.INDEX_TYPE_ACTIVE_RULE.format(), ("" + (ar.getId())), "activeRuleId", String.valueOf(ar.getRuleId()));
        assertThatEsQueueContainsExactly(expectedItem);
        es.unlockWrites(RuleIndexDefinition.INDEX_TYPE_ACTIVE_RULE);
    }

    @Test
    public void commitAndIndex_deletes_the_documents_that_dont_exist_in_database() {
        ActiveRuleDto ar = db.qualityProfiles().activateRule(profile1, rule1);
        indexAll();
        assertThat(es.countDocuments(RuleIndexDefinition.INDEX_TYPE_ACTIVE_RULE)).isEqualTo(1);
        db.getDbClient().activeRuleDao().delete(db.getSession(), ar.getKey());
        commitAndIndex(rule1, ar);
        assertThat(es.countDocuments(RuleIndexDefinition.INDEX_TYPE_ACTIVE_RULE)).isEqualTo(0);
        assertThatEsQueueTableIsEmpty();
    }

    @Test
    public void index_fails_and_deletes_doc_if_docIdType_is_unsupported() {
        EsQueueDto item = EsQueueDto.create(RuleIndexDefinition.INDEX_TYPE_ACTIVE_RULE.format(), "the_id", "unsupported", "the_routing");
        db.getDbClient().esQueueDao().insert(db.getSession(), item);
        underTest.index(db.getSession(), Arrays.asList(item));
        assertThatEsQueueTableIsEmpty();
        assertThat(es.countDocuments(RuleIndexDefinition.INDEX_TYPE_ACTIVE_RULE)).isEqualTo(0);
    }

    @Test
    public void commitDeletionOfProfiles() {
        ActiveRuleDto ar1 = db.qualityProfiles().activateRule(profile1, rule1);
        ActiveRuleDto ar2 = db.qualityProfiles().activateRule(profile2, rule1);
        ActiveRuleDto ar3 = db.qualityProfiles().activateRule(profile2, rule2);
        indexAll();
        db.getDbClient().qualityProfileDao().deleteRulesProfilesByUuids(db.getSession(), Collections.singletonList(profile2.getRulesProfileUuid()));
        underTest.commitDeletionOfProfiles(db.getSession(), Collections.singletonList(profile2));
        verifyOnlyIndexed(ar1);
    }

    @Test
    public void commitDeletionOfProfiles_does_nothing_if_profiles_are_not_indexed() {
        db.qualityProfiles().activateRule(profile1, rule1);
        indexAll();
        assertThat(es.countDocuments(RuleIndexDefinition.INDEX_TYPE_ACTIVE_RULE)).isEqualTo(1);
        underTest.commitDeletionOfProfiles(db.getSession(), Collections.singletonList(profile2));
        assertThat(es.countDocuments(RuleIndexDefinition.INDEX_TYPE_ACTIVE_RULE)).isEqualTo(1);
    }
}

