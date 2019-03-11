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
package org.sonar.server.qualityprofile;


import BuiltInQualityProfilesDefinition.Context;
import Severity.CRITICAL;
import Severity.MAJOR;
import Severity.MINOR;
import java.util.Arrays;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition.NewBuiltInQualityProfile;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.db.DbTester;
import org.sonar.db.qualityprofile.ActiveRuleDto;
import org.sonar.db.qualityprofile.RulesProfileDto;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.db.rule.RuleParamDto;
import org.sonar.server.qualityprofile.index.ActiveRuleIndexer;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.util.IntegerTypeValidation;
import org.sonar.server.util.StringTypeValidation;
import org.sonar.server.util.TypeValidations;


public class BuiltInQProfileUpdateImplTest {
    private static final long NOW = 1000;

    private static final long PAST = (BuiltInQProfileUpdateImplTest.NOW) - 100;

    @Rule
    public BuiltInQProfileRepositoryRule builtInProfileRepository = new BuiltInQProfileRepositoryRule();

    @Rule
    public DbTester db = DbTester.create().setDisableDefaultOrganization(true);

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    private System2 system2 = new TestSystem2().setNow(BuiltInQProfileUpdateImplTest.NOW);

    private ActiveRuleIndexer activeRuleIndexer = Mockito.mock(ActiveRuleIndexer.class);

    private TypeValidations typeValidations = new TypeValidations(Arrays.asList(new StringTypeValidation(), new IntegerTypeValidation()));

    private RuleActivator ruleActivator = new RuleActivator(system2, db.getDbClient(), typeValidations, userSession);

    private BuiltInQProfileUpdateImpl underTest = new BuiltInQProfileUpdateImpl(db.getDbClient(), ruleActivator, activeRuleIndexer);

    private RulesProfileDto persistedProfile;

    @Test
    public void activate_new_rules() {
        RuleDefinitionDto rule1 = db.rules().insert(( r) -> r.setLanguage("xoo"));
        RuleDefinitionDto rule2 = db.rules().insert(( r) -> r.setLanguage("xoo"));
        BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
        NewBuiltInQualityProfile newQp = context.createBuiltInQualityProfile("Sonar way", "xoo");
        newQp.activateRule(rule1.getRepositoryKey(), rule1.getRuleKey()).overrideSeverity(CRITICAL);
        newQp.activateRule(rule2.getRepositoryKey(), rule2.getRuleKey()).overrideSeverity(MAJOR);
        newQp.done();
        BuiltInQProfile builtIn = builtInProfileRepository.create(context.profile("xoo", "Sonar way"), rule1, rule2);
        underTest.update(db.getSession(), builtIn, persistedProfile);
        List<ActiveRuleDto> activeRules = db.getDbClient().activeRuleDao().selectByRuleProfile(db.getSession(), persistedProfile);
        assertThat(activeRules).hasSize(2);
        BuiltInQProfileUpdateImplTest.assertThatRuleIsNewlyActivated(activeRules, rule1, RulePriority.CRITICAL);
        BuiltInQProfileUpdateImplTest.assertThatRuleIsNewlyActivated(activeRules, rule2, RulePriority.MAJOR);
        assertThatProfileIsMarkedAsUpdated(persistedProfile);
    }

    @Test
    public void already_activated_rule_is_updated_in_case_of_differences() {
        RuleDefinitionDto rule = db.rules().insert(( r) -> r.setLanguage("xoo"));
        BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
        NewBuiltInQualityProfile newQp = context.createBuiltInQualityProfile("Sonar way", "xoo");
        newQp.activateRule(rule.getRepositoryKey(), rule.getRuleKey()).overrideSeverity(CRITICAL);
        newQp.done();
        BuiltInQProfile builtIn = builtInProfileRepository.create(context.profile("xoo", "Sonar way"), rule);
        activateRuleInDb(persistedProfile, rule, RulePriority.BLOCKER);
        underTest.update(db.getSession(), builtIn, persistedProfile);
        List<ActiveRuleDto> activeRules = db.getDbClient().activeRuleDao().selectByRuleProfile(db.getSession(), persistedProfile);
        assertThat(activeRules).hasSize(1);
        BuiltInQProfileUpdateImplTest.assertThatRuleIsUpdated(activeRules, rule, RulePriority.CRITICAL);
        assertThatProfileIsMarkedAsUpdated(persistedProfile);
    }

    @Test
    public void already_activated_rule_is_not_touched_if_no_differences() {
        RuleDefinitionDto rule = db.rules().insert(( r) -> r.setLanguage("xoo"));
        BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
        NewBuiltInQualityProfile newQp = context.createBuiltInQualityProfile("Sonar way", "xoo");
        newQp.activateRule(rule.getRepositoryKey(), rule.getRuleKey()).overrideSeverity(CRITICAL);
        newQp.done();
        BuiltInQProfile builtIn = builtInProfileRepository.create(context.profile("xoo", "Sonar way"), rule);
        activateRuleInDb(persistedProfile, rule, RulePriority.CRITICAL);
        underTest.update(db.getSession(), builtIn, persistedProfile);
        List<ActiveRuleDto> activeRules = db.getDbClient().activeRuleDao().selectByRuleProfile(db.getSession(), persistedProfile);
        assertThat(activeRules).hasSize(1);
        BuiltInQProfileUpdateImplTest.assertThatRuleIsUntouched(activeRules, rule, RulePriority.CRITICAL);
        assertThatProfileIsNotMarkedAsUpdated(persistedProfile);
    }

    @Test
    public void deactivate_rule_that_is_not_in_built_in_definition_anymore() {
        RuleDefinitionDto rule1 = db.rules().insert(( r) -> r.setLanguage("xoo"));
        RuleDefinitionDto rule2 = db.rules().insert(( r) -> r.setLanguage("xoo"));
        BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
        NewBuiltInQualityProfile newQp = context.createBuiltInQualityProfile("Sonar way", "xoo");
        newQp.activateRule(rule2.getRepositoryKey(), rule2.getRuleKey()).overrideSeverity(MAJOR);
        newQp.done();
        BuiltInQProfile builtIn = builtInProfileRepository.create(context.profile("xoo", "Sonar way"), rule1, rule2);
        // built-in definition contains only rule2
        // so rule1 must be deactivated
        activateRuleInDb(persistedProfile, rule1, RulePriority.CRITICAL);
        underTest.update(db.getSession(), builtIn, persistedProfile);
        List<ActiveRuleDto> activeRules = db.getDbClient().activeRuleDao().selectByRuleProfile(db.getSession(), persistedProfile);
        assertThat(activeRules).hasSize(1);
        BuiltInQProfileUpdateImplTest.assertThatRuleIsDeactivated(activeRules, rule1);
        assertThatProfileIsMarkedAsUpdated(persistedProfile);
    }

    @Test
    public void activate_deactivate_and_update_three_rules_at_the_same_time() {
        RuleDefinitionDto rule1 = db.rules().insert(( r) -> r.setLanguage("xoo"));
        RuleDefinitionDto rule2 = db.rules().insert(( r) -> r.setLanguage("xoo"));
        RuleDefinitionDto rule3 = db.rules().insert(( r) -> r.setLanguage("xoo"));
        BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
        NewBuiltInQualityProfile newQp = context.createBuiltInQualityProfile("Sonar way", "xoo");
        newQp.activateRule(rule1.getRepositoryKey(), rule1.getRuleKey()).overrideSeverity(CRITICAL);
        newQp.activateRule(rule2.getRepositoryKey(), rule2.getRuleKey()).overrideSeverity(MAJOR);
        newQp.done();
        BuiltInQProfile builtIn = builtInProfileRepository.create(context.profile("xoo", "Sonar way"), rule1, rule2);
        // rule1 must be updated (blocker to critical)
        // rule2 must be activated
        // rule3 must be deactivated
        activateRuleInDb(persistedProfile, rule1, RulePriority.BLOCKER);
        activateRuleInDb(persistedProfile, rule3, RulePriority.BLOCKER);
        underTest.update(db.getSession(), builtIn, persistedProfile);
        List<ActiveRuleDto> activeRules = db.getDbClient().activeRuleDao().selectByRuleProfile(db.getSession(), persistedProfile);
        assertThat(activeRules).hasSize(2);
        BuiltInQProfileUpdateImplTest.assertThatRuleIsUpdated(activeRules, rule1, RulePriority.CRITICAL);
        BuiltInQProfileUpdateImplTest.assertThatRuleIsNewlyActivated(activeRules, rule2, RulePriority.MAJOR);
        BuiltInQProfileUpdateImplTest.assertThatRuleIsDeactivated(activeRules, rule3);
        assertThatProfileIsMarkedAsUpdated(persistedProfile);
    }

    // SONAR-10473
    @Test
    public void activate_rule_on_built_in_profile_resets_severity_to_default_if_not_overridden() {
        RuleDefinitionDto rule = db.rules().insert(( r) -> r.setSeverity(Severity.MAJOR).setLanguage("xoo"));
        BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
        NewBuiltInQualityProfile newQp = context.createBuiltInQualityProfile("Sonar way", "xoo");
        newQp.activateRule(rule.getRepositoryKey(), rule.getRuleKey());
        newQp.done();
        BuiltInQProfile builtIn = builtInProfileRepository.create(context.profile("xoo", "Sonar way"), rule);
        underTest.update(db.getSession(), builtIn, persistedProfile);
        List<ActiveRuleDto> activeRules = db.getDbClient().activeRuleDao().selectByRuleProfile(db.getSession(), persistedProfile);
        BuiltInQProfileUpdateImplTest.assertThatRuleIsNewlyActivated(activeRules, rule, RulePriority.MAJOR);
        // emulate an upgrade of analyzer that changes the default severity of the rule
        rule.setSeverity(MINOR);
        db.rules().update(rule);
        underTest.update(db.getSession(), builtIn, persistedProfile);
        activeRules = db.getDbClient().activeRuleDao().selectByRuleProfile(db.getSession(), persistedProfile);
        BuiltInQProfileUpdateImplTest.assertThatRuleIsNewlyActivated(activeRules, rule, RulePriority.MINOR);
    }

    @Test
    public void activate_rule_on_built_in_profile_resets_params_to_default_if_not_overridden() {
        RuleDefinitionDto rule = db.rules().insert(( r) -> r.setLanguage("xoo"));
        RuleParamDto ruleParam = db.rules().insertRuleParam(rule, ( p) -> p.setName("min").setDefaultValue("10"));
        BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
        NewBuiltInQualityProfile newQp = context.createBuiltInQualityProfile("Sonar way", "xoo");
        newQp.activateRule(rule.getRepositoryKey(), rule.getRuleKey());
        newQp.done();
        BuiltInQProfile builtIn = builtInProfileRepository.create(context.profile("xoo", "Sonar way"), rule);
        underTest.update(db.getSession(), builtIn, persistedProfile);
        List<ActiveRuleDto> activeRules = db.getDbClient().activeRuleDao().selectByRuleProfile(db.getSession(), persistedProfile);
        assertThat(activeRules).hasSize(1);
        BuiltInQProfileUpdateImplTest.assertThatRuleHasParams(db, activeRules.get(0), tuple("min", "10"));
        // emulate an upgrade of analyzer that changes the default value of parameter min
        ruleParam.setDefaultValue("20");
        db.getDbClient().ruleDao().updateRuleParam(db.getSession(), rule, ruleParam);
        underTest.update(db.getSession(), builtIn, persistedProfile);
        activeRules = db.getDbClient().activeRuleDao().selectByRuleProfile(db.getSession(), persistedProfile);
        assertThat(activeRules).hasSize(1);
        BuiltInQProfileUpdateImplTest.assertThatRuleHasParams(db, activeRules.get(0), tuple("min", "20"));
    }
}

