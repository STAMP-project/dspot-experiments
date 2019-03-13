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
package org.sonar.ce.task.projectanalysis.issue;


import System2.INSTANCE;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.System2;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolderRule;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.rule.RuleDao;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.db.rule.RuleDto;
import org.sonar.server.rule.index.RuleIndexer;


public class RuleRepositoryImplTest {
    private static final RuleDto AB_RULE = RuleRepositoryImplTest.createABRuleDto().setId(9688);

    private static final RuleKey AB_RULE_DEPRECATED_KEY_1 = RuleKey.of("old_a", "old_b");

    private static final RuleKey AB_RULE_DEPRECATED_KEY_2 = RuleKey.of(RuleRepositoryImplTest.AB_RULE.getRepositoryKey(), "old_b");

    private static final RuleKey DEPRECATED_KEY_OF_NON_EXITING_RULE = RuleKey.of("some_rep", "some_key");

    private static final RuleKey AC_RULE_KEY = RuleKey.of("a", "c");

    private static final int AC_RULE_ID = 684;

    private static final String ORGANIZATION_UUID = "org-1";

    private static final String QUALITY_GATE_UUID = "QUALITY_GATE_UUID";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public AnalysisMetadataHolderRule analysisMetadataHolder = new AnalysisMetadataHolderRule().setOrganizationUuid(RuleRepositoryImplTest.ORGANIZATION_UUID, RuleRepositoryImplTest.QUALITY_GATE_UUID);

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private DbClient dbClient = Mockito.mock(DbClient.class);

    private DbSession dbSession = Mockito.mock(DbSession.class);

    private RuleDao ruleDao = Mockito.mock(RuleDao.class);

    private RuleIndexer ruleIndexer = Mockito.mock(RuleIndexer.class);

    private AdHocRuleCreator adHocRuleCreator = new AdHocRuleCreator(db.getDbClient(), System2.INSTANCE, ruleIndexer);

    private RuleRepositoryImpl underTest = new RuleRepositoryImpl(adHocRuleCreator, dbClient, analysisMetadataHolder);

    @Test
    public void constructor_does_not_query_DB_to_retrieve_rules() {
        Mockito.verifyNoMoreInteractions(dbClient);
    }

    @Test
    public void first_call_to_getByKey_triggers_call_to_db_and_any_subsequent_get_or_find_call_does_not() {
        underTest.getByKey(RuleRepositoryImplTest.AB_RULE.getKey());
        Mockito.verify(ruleDao, VerificationModeFactory.times(1)).selectAll(ArgumentMatchers.any(DbSession.class), ArgumentMatchers.eq(RuleRepositoryImplTest.ORGANIZATION_UUID));
        verifyNoMethodCallTriggersCallToDB();
    }

    @Test
    public void first_call_to_findByKey_triggers_call_to_db_and_any_subsequent_get_or_find_call_does_not() {
        underTest.findByKey(RuleRepositoryImplTest.AB_RULE.getKey());
        Mockito.verify(ruleDao, VerificationModeFactory.times(1)).selectAll(ArgumentMatchers.any(DbSession.class), ArgumentMatchers.eq(RuleRepositoryImplTest.ORGANIZATION_UUID));
        verifyNoMethodCallTriggersCallToDB();
    }

    @Test
    public void first_call_to_getById_triggers_call_to_db_and_any_subsequent_get_or_find_call_does_not() {
        underTest.getById(RuleRepositoryImplTest.AB_RULE.getId());
        Mockito.verify(ruleDao, VerificationModeFactory.times(1)).selectAll(ArgumentMatchers.any(DbSession.class), ArgumentMatchers.eq(RuleRepositoryImplTest.ORGANIZATION_UUID));
        verifyNoMethodCallTriggersCallToDB();
    }

    @Test
    public void first_call_to_findById_triggers_call_to_db_and_any_subsequent_get_or_find_call_does_not() {
        underTest.findById(RuleRepositoryImplTest.AB_RULE.getId());
        Mockito.verify(ruleDao, VerificationModeFactory.times(1)).selectAll(ArgumentMatchers.any(DbSession.class), ArgumentMatchers.eq(RuleRepositoryImplTest.ORGANIZATION_UUID));
        verifyNoMethodCallTriggersCallToDB();
    }

    @Test
    public void getByKey_throws_NPE_if_key_argument_is_null() {
        expectNullRuleKeyNPE();
        underTest.getByKey(null);
    }

    @Test
    public void getByKey_does_not_call_DB_if_key_argument_is_null() {
        try {
            underTest.getByKey(null);
        } catch (NullPointerException e) {
            assertNoCallToDb();
        }
    }

    @Test
    public void getByKey_returns_Rule_if_it_exists_in_DB() {
        Rule rule = underTest.getByKey(RuleRepositoryImplTest.AB_RULE.getKey());
        assertIsABRule(rule);
    }

    @Test
    public void getByKey_returns_Rule_if_argument_is_deprecated_key_in_DB_of_rule_in_DB() {
        Rule rule = underTest.getByKey(RuleRepositoryImplTest.AB_RULE_DEPRECATED_KEY_1);
        assertIsABRule(rule);
    }

    @Test
    public void getByKey_throws_IAE_if_rules_does_not_exist_in_DB() {
        expectIAERuleNotFound(RuleRepositoryImplTest.AC_RULE_KEY);
        underTest.getByKey(RuleRepositoryImplTest.AC_RULE_KEY);
    }

    @Test
    public void getByKey_throws_IAE_if_argument_is_deprecated_key_in_DB_of_non_existing_rule() {
        expectIAERuleNotFound(RuleRepositoryImplTest.DEPRECATED_KEY_OF_NON_EXITING_RULE);
        underTest.getByKey(RuleRepositoryImplTest.DEPRECATED_KEY_OF_NON_EXITING_RULE);
    }

    @Test
    public void findByKey_throws_NPE_if_key_argument_is_null() {
        expectNullRuleKeyNPE();
        underTest.findByKey(null);
    }

    @Test
    public void findByKey_does_not_call_DB_if_key_argument_is_null() {
        try {
            underTest.findByKey(null);
        } catch (NullPointerException e) {
            assertNoCallToDb();
        }
    }

    @Test
    public void findByKey_returns_absent_if_rule_does_not_exist_in_DB() {
        Optional<Rule> rule = underTest.findByKey(RuleRepositoryImplTest.AC_RULE_KEY);
        assertThat(rule).isEmpty();
    }

    @Test
    public void findByKey_returns_Rule_if_it_exists_in_DB() {
        Optional<Rule> rule = underTest.findByKey(RuleRepositoryImplTest.AB_RULE.getKey());
        assertIsABRule(rule.get());
    }

    @Test
    public void findByKey_returns_Rule_if_argument_is_deprecated_key_in_DB_of_rule_in_DB() {
        Optional<Rule> rule = underTest.findByKey(RuleRepositoryImplTest.AB_RULE_DEPRECATED_KEY_1);
        assertIsABRule(rule.get());
    }

    @Test
    public void findByKey_returns_empty_if_argument_is_deprecated_key_in_DB_of_rule_in_DB() {
        Optional<Rule> rule = underTest.findByKey(RuleRepositoryImplTest.DEPRECATED_KEY_OF_NON_EXITING_RULE);
        assertThat(rule).isEmpty();
    }

    @Test
    public void getById_returns_Rule_if_it_exists_in_DB() {
        Rule rule = underTest.getById(RuleRepositoryImplTest.AB_RULE.getId());
        assertIsABRule(rule);
    }

    @Test
    public void getById_throws_IAE_if_rules_does_not_exist_in_DB() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage((("Can not find rule for id " + (RuleRepositoryImplTest.AC_RULE_ID)) + ". This rule does not exist in DB"));
        underTest.getById(RuleRepositoryImplTest.AC_RULE_ID);
    }

    @Test
    public void findById_returns_absent_if_rule_does_not_exist_in_DB() {
        Optional<Rule> rule = underTest.findById(RuleRepositoryImplTest.AC_RULE_ID);
        assertThat(rule).isEmpty();
    }

    @Test
    public void findById_returns_Rule_if_it_exists_in_DB() {
        Optional<Rule> rule = underTest.findById(RuleRepositoryImplTest.AB_RULE.getId());
        assertIsABRule(rule.get());
    }

    @Test
    public void accept_new_externally_defined_Rules() {
        RuleKey ruleKey = RuleKey.of("external_eslint", "no-cond-assign");
        underTest.addOrUpdateAddHocRuleIfNeeded(ruleKey, () -> new NewAdHocRule(ScannerReport.ExternalIssue.newBuilder().setEngineId("eslint").setRuleId("no-cond-assign").build()));
        assertThat(underTest.getByKey(ruleKey)).isNotNull();
        assertThat(underTest.getByKey(ruleKey).getType()).isNull();
        RuleDao ruleDao = dbClient.ruleDao();
        Optional<RuleDefinitionDto> ruleDefinitionDto = ruleDao.selectDefinitionByKey(dbClient.openSession(false), ruleKey);
        assertThat(ruleDefinitionDto).isNotPresent();
    }

    @Test
    public void persist_new_externally_defined_Rules() {
        underTest = new RuleRepositoryImpl(adHocRuleCreator, db.getDbClient(), analysisMetadataHolder);
        RuleKey ruleKey = RuleKey.of("external_eslint", "no-cond-assign");
        underTest.addOrUpdateAddHocRuleIfNeeded(ruleKey, () -> new NewAdHocRule(ScannerReport.ExternalIssue.newBuilder().setEngineId("eslint").setRuleId("no-cond-assign").build()));
        underTest.saveOrUpdateAddHocRules(db.getSession());
        db.commit();
        Optional<RuleDefinitionDto> ruleDefinitionDto = db.getDbClient().ruleDao().selectDefinitionByKey(db.getSession(), ruleKey);
        assertThat(ruleDefinitionDto).isPresent();
        Rule rule = underTest.getByKey(ruleKey);
        assertThat(rule).isNotNull();
        assertThat(underTest.getById(ruleDefinitionDto.get().getId())).isNotNull();
        Mockito.verify(ruleIndexer).commitAndIndex(db.getSession(), ruleDefinitionDto.get().getId());
    }
}

