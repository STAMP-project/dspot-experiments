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
package org.sonar.server.rule;


import System2.INSTANCE;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.RuleQuery;
import org.sonar.api.utils.internal.AlwaysIncreasingSystem2;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.rule.RuleDao;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.db.rule.RuleParamDto;


public class CachingRuleFinderTest {
    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    private DbClient dbClient = dbTester.getDbClient();

    private AlwaysIncreasingSystem2 system2 = new AlwaysIncreasingSystem2();

    private RuleDefinitionDto[] ruleDefinitions;

    private RuleParamDto[] ruleParams;

    private CachingRuleFinder underTest;

    @Test
    public void constructor_reads_rules_from_DB() {
        DbClient dbClient = Mockito.mock(DbClient.class);
        DbSession dbSession = Mockito.mock(DbSession.class);
        RuleDao ruleDao = Mockito.mock(RuleDao.class);
        Mockito.when(dbClient.openSession(ArgumentMatchers.anyBoolean())).thenReturn(dbSession);
        Mockito.when(dbClient.ruleDao()).thenReturn(ruleDao);
        new CachingRuleFinder(dbClient);
        Mockito.verify(dbClient).openSession(ArgumentMatchers.anyBoolean());
        Mockito.verify(ruleDao).selectAllDefinitions(dbSession);
        Mockito.verifyNoMoreInteractions(ruleDao);
    }

    @Test
    public void constructor_reads_parameters_from_DB() {
        DbClient dbClient = Mockito.mock(DbClient.class);
        DbSession dbSession = Mockito.mock(DbSession.class);
        RuleDao ruleDao = Mockito.mock(RuleDao.class);
        Mockito.when(dbClient.openSession(ArgumentMatchers.anyBoolean())).thenReturn(dbSession);
        Mockito.when(dbClient.ruleDao()).thenReturn(ruleDao);
        List<RuleKey> ruleKeys = Arrays.asList(RuleKey.of("A", "B"), RuleKey.of("C", "D"), RuleKey.of("E", "F"));
        Mockito.when(ruleDao.selectAllDefinitions(dbSession)).thenReturn(ruleKeys.stream().map(RuleTesting::newRule).collect(Collectors.toList()));
        new CachingRuleFinder(dbClient);
        Mockito.verify(ruleDao).selectRuleParamsByRuleKeys(dbSession, ImmutableSet.copyOf(ruleKeys));
    }

    @Test
    public void findById_returns_all_loaded_rules_by_id() {
        for (int i = 0; i < (ruleDefinitions.length); i++) {
            RuleDefinitionDto ruleDefinition = ruleDefinitions[i];
            RuleParamDto ruleParam = ruleParams[i];
            org.sonar.api.rules.Rule rule = underTest.findById(ruleDefinition.getId());
            verifyRule(rule, ruleDefinition, ruleParam);
        }
    }

    @Test
    public void findById_returns_null_for_non_existing_id() {
        assertThat(underTest.findById(new Random().nextInt())).isNull();
    }

    @Test
    public void findByKey_returns_all_loaded_rules_by_id() {
        for (int i = 0; i < (ruleDefinitions.length); i++) {
            RuleDefinitionDto ruleDefinition = ruleDefinitions[i];
            RuleParamDto ruleParam = ruleParams[i];
            org.sonar.api.rules.Rule rule = underTest.findByKey(ruleDefinition.getKey());
            verifyRule(rule, ruleDefinition, ruleParam);
            assertThat(underTest.findByKey(ruleDefinition.getRepositoryKey(), ruleDefinition.getRuleKey())).isSameAs(rule);
        }
    }

    @Test
    public void findByKey_returns_null_when_RuleKey_is_null() {
        assertThat(underTest.findByKey(null)).isNull();
    }

    @Test
    public void findByKey_returns_null_when_repository_key_is_null() {
        assertThat(underTest.findByKey(null, randomAlphabetic(2))).isNull();
    }

    @Test
    public void findByKey_returns_null_when_key_is_null() {
        assertThat(underTest.findByKey(randomAlphabetic(2), null)).isNull();
    }

    @Test
    public void findByKey_returns_null_when_both_repository_key_and_key_are_null() {
        assertThat(underTest.findByKey(null, null)).isNull();
    }

    @Test
    public void find_returns_null_when_RuleQuery_is_empty() {
        assertThat(underTest.find(null)).isNull();
    }

    @Test
    public void find_returns_most_recent_rule_when_RuleQuery_has_no_non_null_field() {
        org.sonar.api.rules.Rule rule = underTest.find(RuleQuery.create());
        assertThat(CachingRuleFinderTest.toRuleKey(rule)).isEqualTo(ruleDefinitions[5].getKey());
    }

    @Test
    public void find_searches_by_exact_match_of_repository_key_and_returns_most_recent_rule() {
        String repoKey = "ABCD";
        RuleDefinitionDto[] sameRepoKey = new RuleDefinitionDto[]{ dbTester.rules().insert(( rule) -> rule.setRepositoryKey(repoKey).setUpdatedAt(system2.now())), dbTester.rules().insert(( rule) -> rule.setRepositoryKey(repoKey).setUpdatedAt(system2.now())) };
        RuleDefinitionDto otherRule = dbTester.rules().insert(( rule) -> rule.setUpdatedAt(system2.now()));
        CachingRuleFinder underTest = new CachingRuleFinder(dbClient);
        assertThat(CachingRuleFinderTest.toRuleKey(underTest.find(RuleQuery.create().withRepositoryKey(repoKey)))).isEqualTo(sameRepoKey[1].getKey());
        assertThat(CachingRuleFinderTest.toRuleKey(underTest.find(RuleQuery.create().withRepositoryKey(otherRule.getRepositoryKey())))).isEqualTo(otherRule.getKey());
        assertThat(underTest.find(RuleQuery.create().withRepositoryKey(repoKey.toLowerCase()))).isNull();
        assertThat(underTest.find(RuleQuery.create().withRepositoryKey(randomAlphabetic(3)))).isNull();
    }

    @Test
    public void find_searches_by_exact_match_of_ruleKey_and_returns_most_recent_rule() {
        String ruleKey = "ABCD";
        RuleDefinitionDto[] sameRuleKey = new RuleDefinitionDto[]{ dbTester.rules().insert(( rule) -> rule.setRuleKey(ruleKey).setUpdatedAt(system2.now())), dbTester.rules().insert(( rule) -> rule.setRuleKey(ruleKey).setUpdatedAt(system2.now())) };
        RuleDefinitionDto otherRule = dbTester.rules().insert(( rule) -> rule.setUpdatedAt(system2.now()));
        CachingRuleFinder underTest = new CachingRuleFinder(dbClient);
        assertThat(CachingRuleFinderTest.toRuleKey(underTest.find(RuleQuery.create().withKey(ruleKey)))).isEqualTo(sameRuleKey[1].getKey());
        assertThat(CachingRuleFinderTest.toRuleKey(underTest.find(RuleQuery.create().withKey(otherRule.getRuleKey())))).isEqualTo(otherRule.getKey());
        assertThat(underTest.find(RuleQuery.create().withKey(ruleKey.toLowerCase()))).isNull();
        assertThat(underTest.find(RuleQuery.create().withKey(randomAlphabetic(3)))).isNull();
    }

    @Test
    public void find_searches_by_exact_match_of_configKey_and_returns_most_recent_rule() {
        String configKey = "ABCD";
        RuleDefinitionDto[] sameConfigKey = new RuleDefinitionDto[]{ dbTester.rules().insert(( rule) -> rule.setConfigKey(configKey).setUpdatedAt(system2.now())), dbTester.rules().insert(( rule) -> rule.setConfigKey(configKey).setUpdatedAt(system2.now())) };
        RuleDefinitionDto otherRule = dbTester.rules().insert(( rule) -> rule.setUpdatedAt(system2.now()));
        CachingRuleFinder underTest = new CachingRuleFinder(dbClient);
        assertThat(CachingRuleFinderTest.toRuleKey(underTest.find(RuleQuery.create().withConfigKey(configKey)))).isEqualTo(sameConfigKey[1].getKey());
        assertThat(CachingRuleFinderTest.toRuleKey(underTest.find(RuleQuery.create().withConfigKey(otherRule.getConfigKey())))).isEqualTo(otherRule.getKey());
        assertThat(underTest.find(RuleQuery.create().withConfigKey(configKey.toLowerCase()))).isNull();
        assertThat(underTest.find(RuleQuery.create().withConfigKey(randomAlphabetic(3)))).isNull();
    }

    @Test
    public void find_searches_by_exact_match_and_match_on_all_criterias_and_returns_most_recent_match() {
        String repoKey = "ABCD";
        String ruleKey = "EFGH";
        String configKey = "IJKL";
        RuleDefinitionDto[] rules = new RuleDefinitionDto[]{ dbTester.rules().insert(( rule) -> rule.setRepositoryKey(repoKey).setRuleKey(ruleKey).setConfigKey(configKey).setUpdatedAt(system2.now())), dbTester.rules().insert(( rule) -> rule.setRuleKey(ruleKey).setConfigKey(configKey).setUpdatedAt(system2.now())), dbTester.rules().insert(( rule) -> rule.setRepositoryKey(repoKey).setConfigKey(configKey).setUpdatedAt(system2.now())), dbTester.rules().insert(( rule) -> rule.setUpdatedAt(system2.now())) };
        RuleQuery allQuery = RuleQuery.create().withRepositoryKey(repoKey).withKey(ruleKey).withConfigKey(configKey);
        RuleQuery ruleAndConfigKeyQuery = RuleQuery.create().withKey(ruleKey).withConfigKey(configKey);
        RuleQuery repoAndConfigKeyQuery = RuleQuery.create().withRepositoryKey(repoKey).withConfigKey(configKey);
        RuleQuery repoAndKeyQuery = RuleQuery.create().withRepositoryKey(repoKey).withKey(ruleKey);
        RuleQuery configKeyQuery = RuleQuery.create().withConfigKey(configKey);
        RuleQuery ruleKeyQuery = RuleQuery.create().withKey(ruleKey);
        RuleQuery repoKeyQuery = RuleQuery.create().withRepositoryKey(repoKey);
        CachingRuleFinder underTest = new CachingRuleFinder(dbClient);
        assertThat(CachingRuleFinderTest.toRuleKey(underTest.find(allQuery))).isEqualTo(rules[0].getKey());
        assertThat(CachingRuleFinderTest.toRuleKey(underTest.find(ruleAndConfigKeyQuery))).isEqualTo(rules[1].getKey());
        assertThat(CachingRuleFinderTest.toRuleKey(underTest.find(repoAndConfigKeyQuery))).isEqualTo(rules[2].getKey());
        assertThat(CachingRuleFinderTest.toRuleKey(underTest.find(repoAndKeyQuery))).isEqualTo(rules[0].getKey());
        assertThat(CachingRuleFinderTest.toRuleKey(underTest.find(repoKeyQuery))).isEqualTo(rules[2].getKey());
        assertThat(CachingRuleFinderTest.toRuleKey(underTest.find(ruleKeyQuery))).isEqualTo(rules[1].getKey());
        assertThat(CachingRuleFinderTest.toRuleKey(underTest.find(configKeyQuery))).isEqualTo(rules[2].getKey());
    }

    @Test
    public void findAll_returns_empty_when_RuleQuery_is_empty() {
        assertThat(underTest.findAll(null)).isEmpty();
    }

    @Test
    public void findAll_returns_all_rules_when_RuleQuery_has_no_non_null_field() {
        assertThat(underTest.findAll(RuleQuery.create())).extracting(CachingRuleFinderTest::toRuleKey).containsOnly(Arrays.stream(ruleDefinitions).map(RuleDefinitionDto::getKey).toArray(RuleKey[]::new));
    }

    @Test
    public void findAll_returns_all_rules_with_exact_same_repository_key_and_order_them_most_recent_first() {
        String repoKey = "ABCD";
        RuleDefinitionDto[] sameRepoKey = new RuleDefinitionDto[]{ dbTester.rules().insert(( rule) -> rule.setRepositoryKey(repoKey).setUpdatedAt(system2.now())), dbTester.rules().insert(( rule) -> rule.setRepositoryKey(repoKey).setUpdatedAt(system2.now())) };
        RuleDefinitionDto otherRule = dbTester.rules().insert(( rule) -> rule.setUpdatedAt(system2.now()));
        CachingRuleFinder underTest = new CachingRuleFinder(dbClient);
        assertThat(underTest.findAll(RuleQuery.create().withRepositoryKey(repoKey))).extracting(CachingRuleFinderTest::toRuleKey).containsExactly(sameRepoKey[1].getKey(), sameRepoKey[0].getKey());
        assertThat(underTest.findAll(RuleQuery.create().withRepositoryKey(otherRule.getRepositoryKey()))).extracting(CachingRuleFinderTest::toRuleKey).containsExactly(otherRule.getKey());
        assertThat(underTest.findAll(RuleQuery.create().withRepositoryKey(repoKey.toLowerCase()))).isEmpty();
        assertThat(underTest.findAll(RuleQuery.create().withRepositoryKey(randomAlphabetic(3)))).isEmpty();
    }

    @Test
    public void findAll_returns_all_rules_with_exact_same_rulekey_and_order_them_most_recent_first() {
        String ruleKey = "ABCD";
        RuleDefinitionDto[] sameRuleKey = new RuleDefinitionDto[]{ dbTester.rules().insert(( rule) -> rule.setRuleKey(ruleKey).setUpdatedAt(system2.now())), dbTester.rules().insert(( rule) -> rule.setRuleKey(ruleKey).setUpdatedAt(system2.now())) };
        RuleDefinitionDto otherRule = dbTester.rules().insert(( rule) -> rule.setUpdatedAt(system2.now()));
        CachingRuleFinder underTest = new CachingRuleFinder(dbClient);
        assertThat(underTest.findAll(RuleQuery.create().withKey(ruleKey))).extracting(CachingRuleFinderTest::toRuleKey).containsExactly(sameRuleKey[1].getKey(), sameRuleKey[0].getKey());
        assertThat(underTest.findAll(RuleQuery.create().withKey(otherRule.getRuleKey()))).extracting(CachingRuleFinderTest::toRuleKey).containsExactly(otherRule.getKey());
        assertThat(underTest.findAll(RuleQuery.create().withKey(ruleKey.toLowerCase()))).isEmpty();
        assertThat(underTest.findAll(RuleQuery.create().withKey(randomAlphabetic(3)))).isEmpty();
    }

    @Test
    public void findAll_returns_all_rules_with_exact_same_configkey_and_order_them_most_recent_first() {
        String configKey = "ABCD";
        RuleDefinitionDto[] sameConfigKey = new RuleDefinitionDto[]{ dbTester.rules().insert(( rule) -> rule.setConfigKey(configKey).setUpdatedAt(system2.now())), dbTester.rules().insert(( rule) -> rule.setConfigKey(configKey).setUpdatedAt(system2.now())) };
        RuleDefinitionDto otherRule = dbTester.rules().insert(( rule) -> rule.setUpdatedAt(system2.now()));
        CachingRuleFinder underTest = new CachingRuleFinder(dbClient);
        assertThat(underTest.findAll(RuleQuery.create().withConfigKey(configKey))).extracting(CachingRuleFinderTest::toRuleKey).containsExactly(sameConfigKey[1].getKey(), sameConfigKey[0].getKey());
        assertThat(underTest.findAll(RuleQuery.create().withConfigKey(otherRule.getConfigKey()))).extracting(CachingRuleFinderTest::toRuleKey).containsExactly(otherRule.getKey());
        assertThat(underTest.findAll(RuleQuery.create().withConfigKey(configKey.toLowerCase()))).isEmpty();
        assertThat(underTest.findAll(RuleQuery.create().withConfigKey(randomAlphabetic(3)))).isEmpty();
    }

    @Test
    public void findAll_returns_all_rules_which_match_exactly_all_criteria_and_order_then_by_most_recent_first() {
        String repoKey = "ABCD";
        String ruleKey = "EFGH";
        String configKey = "IJKL";
        RuleDefinitionDto[] rules = new RuleDefinitionDto[]{ dbTester.rules().insert(( rule) -> rule.setRepositoryKey(repoKey).setRuleKey(ruleKey).setConfigKey(configKey).setUpdatedAt(system2.now())), dbTester.rules().insert(( rule) -> rule.setRuleKey(ruleKey).setConfigKey(configKey).setUpdatedAt(system2.now())), dbTester.rules().insert(( rule) -> rule.setRepositoryKey(repoKey).setConfigKey(configKey).setUpdatedAt(system2.now())), dbTester.rules().insert(( rule) -> rule.setUpdatedAt(system2.now())) };
        RuleQuery allQuery = RuleQuery.create().withRepositoryKey(repoKey).withKey(ruleKey).withConfigKey(configKey);
        RuleQuery ruleAndConfigKeyQuery = RuleQuery.create().withKey(ruleKey).withConfigKey(configKey);
        RuleQuery repoAndConfigKeyQuery = RuleQuery.create().withRepositoryKey(repoKey).withConfigKey(configKey);
        RuleQuery repoAndKeyQuery = RuleQuery.create().withRepositoryKey(repoKey).withKey(ruleKey);
        RuleQuery configKeyQuery = RuleQuery.create().withConfigKey(configKey);
        RuleQuery ruleKeyQuery = RuleQuery.create().withKey(ruleKey);
        RuleQuery repoKeyQuery = RuleQuery.create().withRepositoryKey(repoKey);
        CachingRuleFinder underTest = new CachingRuleFinder(dbClient);
        assertThat(underTest.findAll(allQuery)).extracting(CachingRuleFinderTest::toRuleKey).containsExactly(rules[0].getKey());
        assertThat(underTest.findAll(ruleAndConfigKeyQuery)).extracting(CachingRuleFinderTest::toRuleKey).containsExactly(rules[1].getKey(), rules[0].getKey());
        assertThat(underTest.findAll(repoAndConfigKeyQuery)).extracting(CachingRuleFinderTest::toRuleKey).containsExactly(rules[2].getKey(), rules[0].getKey());
        assertThat(underTest.findAll(repoAndKeyQuery)).extracting(CachingRuleFinderTest::toRuleKey).containsExactly(rules[0].getKey());
        assertThat(underTest.findAll(repoKeyQuery)).extracting(CachingRuleFinderTest::toRuleKey).containsExactly(rules[2].getKey(), rules[0].getKey());
        assertThat(underTest.findAll(ruleKeyQuery)).extracting(CachingRuleFinderTest::toRuleKey).containsExactly(rules[1].getKey(), rules[0].getKey());
        assertThat(underTest.findAll(configKeyQuery)).extracting(CachingRuleFinderTest::toRuleKey).containsExactly(rules[2].getKey(), rules[1].getKey(), rules[0].getKey());
    }
}

