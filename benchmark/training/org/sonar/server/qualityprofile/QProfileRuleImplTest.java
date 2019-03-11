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


import ActiveRuleChange.Type.DEACTIVATED;
import ActiveRuleInheritance.OVERRIDES;
import RuleStatus.REMOVED;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.AlwaysIncreasingSystem2;
import org.sonar.db.DbTester;
import org.sonar.db.qualityprofile.QProfileDto;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.db.rule.RuleDto;
import org.sonar.db.rule.RuleParamDto;
import org.sonar.server.es.EsTester;
import org.sonar.server.es.SearchOptions;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.qualityprofile.index.ActiveRuleIndexer;
import org.sonar.server.rule.index.RuleIndex;
import org.sonar.server.rule.index.RuleIndexer;
import org.sonar.server.rule.index.RuleQuery;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.util.IntegerTypeValidation;
import org.sonar.server.util.StringTypeValidation;
import org.sonar.server.util.TypeValidations;


public class QProfileRuleImplTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private System2 system2 = new AlwaysIncreasingSystem2();

    @Rule
    public DbTester db = DbTester.create(system2);

    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    private RuleIndex ruleIndex = new RuleIndex(es.client(), system2);

    private ActiveRuleIndexer activeRuleIndexer = new ActiveRuleIndexer(db.getDbClient(), es.client());

    private RuleIndexer ruleIndexer = new RuleIndexer(es.client(), db.getDbClient());

    private TypeValidations typeValidations = new TypeValidations(Arrays.asList(new StringTypeValidation(), new IntegerTypeValidation()));

    private RuleActivator ruleActivator = new RuleActivator(system2, db.getDbClient(), typeValidations, userSession);

    private QProfileRules underTest = new QProfileRulesImpl(db.getDbClient(), ruleActivator, ruleIndex, activeRuleIndexer);

    @Test
    public void system_activates_rule_without_parameters() {
        RuleDefinitionDto rule = createRule();
        QProfileDto profile = createProfile(rule);
        RuleActivation activation = RuleActivation.create(rule.getId(), BLOCKER, null);
        List<ActiveRuleChange> changes = activate(profile, activation);
        assertThatRuleIsActivated(profile, rule, changes, BLOCKER, null, Collections.emptyMap());
        assertThatProfileIsUpdatedBySystem(profile);
    }

    @Test
    public void user_activates_rule_without_parameters() {
        userSession.logIn();
        RuleDefinitionDto rule = createRule();
        QProfileDto profile = createProfile(rule);
        RuleActivation activation = RuleActivation.create(rule.getId(), BLOCKER, null);
        List<ActiveRuleChange> changes = activate(profile, activation);
        assertThatRuleIsActivated(profile, rule, changes, BLOCKER, null, Collections.emptyMap());
        assertThatProfileIsUpdatedByUser(profile);
    }

    @Test
    public void activate_rule_with_default_severity_and_parameters() {
        RuleDefinitionDto rule = createRule();
        RuleParamDto ruleParam = db.rules().insertRuleParam(rule, ( p) -> p.setName("min").setDefaultValue("10"));
        QProfileDto profile = createProfile(rule);
        RuleActivation activation = RuleActivation.create(rule.getId());
        List<ActiveRuleChange> changes = activate(profile, activation);
        assertThatRuleIsActivated(profile, rule, changes, rule.getSeverityString(), null, ImmutableMap.of("min", "10"));
        assertThatProfileIsUpdatedBySystem(profile);
    }

    @Test
    public void activate_rule_with_parameters() {
        RuleDefinitionDto rule = createRule();
        RuleParamDto ruleParam = db.rules().insertRuleParam(rule, ( p) -> p.setName("min").setDefaultValue("10"));
        QProfileDto profile = createProfile(rule);
        RuleActivation activation = RuleActivation.create(rule.getId(), null, ImmutableMap.of(ruleParam.getName(), "15"));
        List<ActiveRuleChange> changes = activate(profile, activation);
        assertThatRuleIsActivated(profile, rule, changes, rule.getSeverityString(), null, ImmutableMap.of("min", "15"));
        assertThatProfileIsUpdatedBySystem(profile);
    }

    @Test
    public void activate_rule_with_default_severity() {
        RuleDefinitionDto rule = createRule();
        QProfileDto profile = createProfile(rule);
        RuleActivation activation = RuleActivation.create(rule.getId());
        List<ActiveRuleChange> changes = activate(profile, activation);
        assertThatRuleIsActivated(profile, rule, changes, rule.getSeverityString(), null, Collections.emptyMap());
        assertThatProfileIsUpdatedBySystem(profile);
    }

    /**
     * SONAR-5841
     */
    @Test
    public void activate_rule_with_empty_parameter_having_no_default_value() {
        RuleDefinitionDto rule = createRule();
        RuleParamDto ruleParam = db.rules().insertRuleParam(rule, ( p) -> p.setName("min").setDefaultValue("10"));
        QProfileDto profile = createProfile(rule);
        RuleActivation activation = RuleActivation.create(rule.getId(), null, ImmutableMap.of("min", ""));
        List<ActiveRuleChange> changes = activate(profile, activation);
        assertThatRuleIsActivated(profile, rule, changes, rule.getSeverityString(), null, ImmutableMap.of("min", "10"));
        assertThatProfileIsUpdatedBySystem(profile);
    }

    /**
     * //   * SONAR-5840
     * //
     */
    @Test
    public void activate_rule_with_negative_integer_value_on_parameter_having_no_default_value() {
        RuleDefinitionDto rule = createRule();
        RuleParamDto paramWithoutDefault = db.rules().insertRuleParam(rule, ( p) -> p.setName("min").setDefaultValue(null));
        RuleParamDto paramWithDefault = db.rules().insertRuleParam(rule, ( p) -> p.setName("max").setDefaultValue("10"));
        QProfileDto profile = createProfile(rule);
        RuleActivation activation = RuleActivation.create(rule.getId(), null, ImmutableMap.of(paramWithoutDefault.getName(), "-10"));
        List<ActiveRuleChange> changes = activate(profile, activation);
        assertThatRuleIsActivated(profile, rule, changes, rule.getSeverityString(), null, ImmutableMap.of(paramWithoutDefault.getName(), "-10", paramWithDefault.getName(), paramWithDefault.getDefaultValue()));
        assertThatProfileIsUpdatedBySystem(profile);
    }

    @Test
    public void activation_ignores_unsupported_parameters() {
        RuleDefinitionDto rule = createRule();
        RuleParamDto param = db.rules().insertRuleParam(rule, ( p) -> p.setName("max").setDefaultValue("10"));
        QProfileDto profile = createProfile(rule);
        RuleActivation activation = RuleActivation.create(rule.getId(), null, ImmutableMap.of("xxx", "yyy"));
        List<ActiveRuleChange> changes = activate(profile, activation);
        assertThatRuleIsActivated(profile, rule, changes, rule.getSeverityString(), null, ImmutableMap.of(param.getName(), param.getDefaultValue()));
        assertThatProfileIsUpdatedBySystem(profile);
    }

    @Test
    public void update_an_already_activated_rule() {
        RuleDefinitionDto rule = createRule();
        RuleParamDto param = db.rules().insertRuleParam(rule, ( p) -> p.setName("max").setDefaultValue("10"));
        QProfileDto profile = createProfile(rule);
        // initial activation
        RuleActivation activation = RuleActivation.create(rule.getId(), MAJOR, null);
        activate(profile, activation);
        // update
        RuleActivation updateActivation = RuleActivation.create(rule.getId(), CRITICAL, ImmutableMap.of(param.getName(), "20"));
        List<ActiveRuleChange> changes = activate(profile, updateActivation);
        assertThatRuleIsUpdated(profile, rule, CRITICAL, null, ImmutableMap.of(param.getName(), "20"));
        assertThatProfileIsUpdatedBySystem(profile);
    }

    @Test
    public void update_activation_with_parameter_without_default_value() {
        RuleDefinitionDto rule = createRule();
        RuleParamDto paramWithoutDefault = db.rules().insertRuleParam(rule, ( p) -> p.setName("min").setDefaultValue(null));
        RuleParamDto paramWithDefault = db.rules().insertRuleParam(rule, ( p) -> p.setName("max").setDefaultValue("10"));
        QProfileDto profile = createProfile(rule);
        // initial activation -> param "max" has a default value
        RuleActivation activation = RuleActivation.create(rule.getId());
        activate(profile, activation);
        // update param "min", which has no default value
        RuleActivation updateActivation = RuleActivation.create(rule.getId(), MAJOR, ImmutableMap.of(paramWithoutDefault.getName(), "3"));
        List<ActiveRuleChange> changes = activate(profile, updateActivation);
        assertThatRuleIsUpdated(profile, rule, MAJOR, null, ImmutableMap.of(paramWithDefault.getName(), "10", paramWithoutDefault.getName(), "3"));
        assertThatProfileIsUpdatedBySystem(profile);
    }

    @Test
    public void reset_parameter_to_default_value() {
        RuleDefinitionDto rule = createRule();
        RuleParamDto paramWithDefault = db.rules().insertRuleParam(rule, ( p) -> p.setName("max").setDefaultValue("10"));
        QProfileDto profile = createProfile(rule);
        // initial activation -> param "max" has a default value
        RuleActivation activation = RuleActivation.create(rule.getId(), null, ImmutableMap.of(paramWithDefault.getName(), "20"));
        activate(profile, activation);
        // reset to default_value
        RuleActivation updateActivation = RuleActivation.create(rule.getId(), null, ImmutableMap.of(paramWithDefault.getName(), ""));
        List<ActiveRuleChange> changes = activate(profile, updateActivation);
        assertThatRuleIsUpdated(profile, rule, rule.getSeverityString(), null, ImmutableMap.of(paramWithDefault.getName(), "10"));
        assertThat(changes).hasSize(1);
    }

    @Test
    public void update_activation_removes_parameter_without_default_value() {
        RuleDefinitionDto rule = createRule();
        RuleParamDto paramWithoutDefault = db.rules().insertRuleParam(rule, ( p) -> p.setName("min").setDefaultValue(null));
        RuleParamDto paramWithDefault = db.rules().insertRuleParam(rule, ( p) -> p.setName("max").setDefaultValue("10"));
        QProfileDto profile = createProfile(rule);
        // initial activation -> param "max" has a default value
        RuleActivation activation = RuleActivation.create(rule.getId(), null, ImmutableMap.of(paramWithoutDefault.getName(), "20"));
        activate(profile, activation);
        // remove parameter
        RuleActivation updateActivation = RuleActivation.create(rule.getId(), null, ImmutableMap.of(paramWithoutDefault.getName(), ""));
        List<ActiveRuleChange> changes = activate(profile, updateActivation);
        assertThatRuleIsUpdated(profile, rule, rule.getSeverityString(), null, ImmutableMap.of(paramWithDefault.getName(), paramWithDefault.getDefaultValue()));
        assertThat(changes).hasSize(1);
    }

    @Test
    public void update_activation_with_new_parameter() {
        RuleDefinitionDto rule = createRule();
        RuleParamDto param = db.rules().insertRuleParam(rule, ( p) -> p.setName("max").setDefaultValue("10"));
        QProfileDto profile = createProfile(rule);
        // initial activation -> param "max" has a default value
        RuleActivation activation = RuleActivation.create(rule.getId());
        List<ActiveRuleChange> changes = activate(profile, activation);
        db.getDbClient().activeRuleDao().deleteParametersByRuleProfileUuids(db.getSession(), Arrays.asList(profile.getRulesProfileUuid()));
        assertThatRuleIsActivated(profile, rule, changes, rule.getSeverityString(), null, Collections.emptyMap());
        // contrary to activerule, the param is supposed to be inserted but not updated
        RuleActivation updateActivation = RuleActivation.create(rule.getId(), null, ImmutableMap.of(param.getName(), ""));
        changes = activate(profile, updateActivation);
        assertThatRuleIsUpdated(profile, rule, rule.getSeverityString(), null, ImmutableMap.of(param.getName(), param.getDefaultValue()));
        assertThat(changes).hasSize(1);
    }

    @Test
    public void ignore_activation_without_changes() {
        RuleDefinitionDto rule = createRule();
        QProfileDto profile = createProfile(rule);
        // initial activation
        RuleActivation activation = RuleActivation.create(rule.getId());
        activate(profile, activation);
        // update with exactly the same severity and params
        activation = RuleActivation.create(rule.getId());
        List<ActiveRuleChange> changes = activate(profile, activation);
        assertThat(changes).isEmpty();
    }

    @Test
    public void do_not_change_severity_and_params_if_unset_and_already_activated() {
        RuleDefinitionDto rule = createRule();
        RuleParamDto param = db.rules().insertRuleParam(rule, ( p) -> p.setName("max").setDefaultValue("10"));
        QProfileDto profile = createProfile(rule);
        // initial activation -> param "max" has a default value
        RuleActivation activation = RuleActivation.create(rule.getId(), BLOCKER, ImmutableMap.of(param.getName(), "20"));
        activate(profile, activation);
        // update without any severity or params => keep
        RuleActivation update = RuleActivation.create(rule.getId());
        List<ActiveRuleChange> changes = activate(profile, update);
        assertThat(changes).isEmpty();
    }

    @Test
    public void fail_to_activate_rule_if_profile_is_on_different_languages() {
        RuleDefinitionDto rule = createJavaRule();
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setLanguage("js"));
        RuleActivation activation = RuleActivation.create(rule.getId());
        expectFailure(((("java rule " + (rule.getKey())) + " cannot be activated on js profile ") + (profile.getKee())), () -> activate(profile, activation));
    }

    @Test
    public void fail_to_activate_rule_if_rule_has_REMOVED_status() {
        RuleDefinitionDto rule = db.rules().insert(( r) -> r.setStatus(RuleStatus.REMOVED));
        QProfileDto profile = createProfile(rule);
        RuleActivation activation = RuleActivation.create(rule.getId());
        expectFailure(("Rule was removed: " + (rule.getKey())), () -> activate(profile, activation));
    }

    @Test
    public void fail_to_activate_if_template() {
        RuleDefinitionDto rule = db.rules().insert(( r) -> r.setIsTemplate(true));
        QProfileDto profile = createProfile(rule);
        RuleActivation activation = RuleActivation.create(rule.getId());
        expectFailure(("Rule template can't be activated on a Quality profile: " + (rule.getKey())), () -> activate(profile, activation));
    }

    @Test
    public void fail_to_activate_if_invalid_parameter() {
        RuleDefinitionDto rule = createRule();
        RuleParamDto param = db.rules().insertRuleParam(rule, ( p) -> p.setName("max").setDefaultValue("10").setType(PropertyType.INTEGER.name()));
        QProfileDto profile = createProfile(rule);
        RuleActivation activation = RuleActivation.create(rule.getId(), null, ImmutableMap.of(param.getName(), "foo"));
        expectFailure("Value 'foo' must be an integer.", () -> activate(profile, activation));
    }

    @Test
    public void ignore_parameters_when_activating_custom_rule() {
        RuleDefinitionDto templateRule = db.rules().insert(( r) -> r.setIsTemplate(true));
        RuleParamDto templateParam = db.rules().insertRuleParam(templateRule, ( p) -> p.setName("format"));
        RuleDefinitionDto customRule = db.rules().insert(newCustomRule(templateRule));
        RuleParamDto customParam = db.rules().insertRuleParam(customRule, ( p) -> p.setName("format").setDefaultValue("txt"));
        QProfileDto profile = createProfile(customRule);
        // initial activation
        RuleActivation activation = RuleActivation.create(customRule.getId(), MAJOR, Collections.emptyMap());
        activate(profile, activation);
        assertThatRuleIsActivated(profile, customRule, null, MAJOR, null, ImmutableMap.of("format", "txt"));
        // update -> parameter is not changed
        RuleActivation updateActivation = RuleActivation.create(customRule.getId(), BLOCKER, ImmutableMap.of("format", "xml"));
        activate(profile, updateActivation);
        assertThatRuleIsActivated(profile, customRule, null, BLOCKER, null, ImmutableMap.of("format", "txt"));
    }

    @Test
    public void user_deactivates_a_rule() {
        userSession.logIn();
        RuleDefinitionDto rule = createRule();
        QProfileDto profile = createProfile(rule);
        RuleActivation activation = RuleActivation.create(rule.getId());
        activate(profile, activation);
        List<ActiveRuleChange> changes = deactivate(profile, rule);
        verifyNoActiveRules();
        assertThatProfileIsUpdatedByUser(profile);
        assertThat(changes).hasSize(1);
        assertThat(changes.get(0).getType()).isEqualTo(DEACTIVATED);
    }

    @Test
    public void system_deactivates_a_rule() {
        RuleDefinitionDto rule = createRule();
        QProfileDto profile = createProfile(rule);
        RuleActivation activation = RuleActivation.create(rule.getId());
        activate(profile, activation);
        List<ActiveRuleChange> changes = deactivate(profile, rule);
        verifyNoActiveRules();
        assertThatProfileIsUpdatedBySystem(profile);
        assertThatChangeIsDeactivation(changes, rule);
    }

    @Test
    public void ignore_deactivation_if_rule_is_not_activated() {
        RuleDefinitionDto rule = createRule();
        QProfileDto profile = createProfile(rule);
        List<ActiveRuleChange> changes = deactivate(profile, rule);
        verifyNoActiveRules();
        assertThat(changes).hasSize(0);
    }

    @Test
    public void deactivate_rule_that_has_REMOVED_status() {
        RuleDefinitionDto rule = createRule();
        QProfileDto profile = createProfile(rule);
        RuleActivation activation = RuleActivation.create(rule.getId());
        activate(profile, activation);
        rule.setStatus(REMOVED);
        db.getDbClient().ruleDao().update(db.getSession(), rule);
        List<ActiveRuleChange> changes = deactivate(profile, rule);
        verifyNoActiveRules();
        assertThatChangeIsDeactivation(changes, rule);
    }

    @Test
    public void activation_on_child_profile_is_propagated_to_descendants() {
        RuleDefinitionDto rule = createRule();
        QProfileDto parentProfile = createProfile(rule);
        QProfileDto childProfile = createChildProfile(parentProfile);
        QProfileDto grandChildProfile = createChildProfile(childProfile);
        List<ActiveRuleChange> changes = activate(childProfile, RuleActivation.create(rule.getId()));
        assertThatProfileHasNoActiveRules(parentProfile);
        assertThatRuleIsActivated(childProfile, rule, changes, rule.getSeverityString(), null, Collections.emptyMap());
        assertThatRuleIsActivated(grandChildProfile, rule, changes, rule.getSeverityString(), ActiveRuleInheritance.INHERITED, Collections.emptyMap());
    }

    @Test
    public void update_on_child_profile_is_propagated_to_descendants() {
        RuleDefinitionDto rule = createRule();
        RuleParamDto param = db.rules().insertRuleParam(rule);
        QProfileDto parentProfile = createProfile(rule);
        QProfileDto childProfile = createChildProfile(parentProfile);
        QProfileDto grandChildProfile = createChildProfile(childProfile);
        RuleActivation initialActivation = RuleActivation.create(rule.getId(), MAJOR, ImmutableMap.of(param.getName(), "foo"));
        activate(childProfile, initialActivation);
        RuleActivation updateActivation = RuleActivation.create(rule.getId(), CRITICAL, ImmutableMap.of(param.getName(), "bar"));
        List<ActiveRuleChange> changes = activate(childProfile, updateActivation);
        assertThatProfileHasNoActiveRules(parentProfile);
        assertThatRuleIsUpdated(childProfile, rule, CRITICAL, null, ImmutableMap.of(param.getName(), "bar"));
        assertThatRuleIsUpdated(grandChildProfile, rule, CRITICAL, ActiveRuleInheritance.INHERITED, ImmutableMap.of(param.getName(), "bar"));
        assertThat(changes).hasSize(2);
    }

    @Test
    public void override_activation_of_inherited_profile() {
        RuleDefinitionDto rule = createRule();
        RuleParamDto param = db.rules().insertRuleParam(rule);
        QProfileDto parentProfile = createProfile(rule);
        QProfileDto childProfile = createChildProfile(parentProfile);
        QProfileDto grandChildProfile = createChildProfile(childProfile);
        RuleActivation initialActivation = RuleActivation.create(rule.getId(), MAJOR, ImmutableMap.of(param.getName(), "foo"));
        activate(childProfile, initialActivation);
        RuleActivation overrideActivation = RuleActivation.create(rule.getId(), CRITICAL, ImmutableMap.of(param.getName(), "bar"));
        List<ActiveRuleChange> changes = activate(grandChildProfile, overrideActivation);
        assertThatProfileHasNoActiveRules(parentProfile);
        assertThatRuleIsUpdated(childProfile, rule, MAJOR, null, ImmutableMap.of(param.getName(), "foo"));
        assertThatRuleIsUpdated(grandChildProfile, rule, CRITICAL, OVERRIDES, ImmutableMap.of(param.getName(), "bar"));
        assertThat(changes).hasSize(1);
    }

    @Test
    public void updated_activation_on_parent_is_not_propagated_to_overridden_profiles() {
        RuleDefinitionDto rule = createRule();
        RuleParamDto param = db.rules().insertRuleParam(rule);
        QProfileDto parentProfile = createProfile(rule);
        QProfileDto childProfile = createChildProfile(parentProfile);
        QProfileDto grandChildProfile = createChildProfile(childProfile);
        RuleActivation initialActivation = RuleActivation.create(rule.getId(), MAJOR, ImmutableMap.of(param.getName(), "foo"));
        activate(childProfile, initialActivation);
        RuleActivation overrideActivation = RuleActivation.create(rule.getId(), CRITICAL, ImmutableMap.of(param.getName(), "bar"));
        activate(grandChildProfile, overrideActivation);
        // update child --> do not touch grandChild
        RuleActivation updateActivation = RuleActivation.create(rule.getId(), BLOCKER, ImmutableMap.of(param.getName(), "baz"));
        List<ActiveRuleChange> changes = activate(childProfile, updateActivation);
        assertThatProfileHasNoActiveRules(parentProfile);
        assertThatRuleIsUpdated(childProfile, rule, BLOCKER, null, ImmutableMap.of(param.getName(), "baz"));
        assertThatRuleIsUpdated(grandChildProfile, rule, CRITICAL, OVERRIDES, ImmutableMap.of(param.getName(), "bar"));
        assertThat(changes).hasSize(1);
    }

    @Test
    public void reset_on_parent_is_not_propagated_to_overridden_profiles() {
        RuleDefinitionDto rule = createRule();
        RuleParamDto param = db.rules().insertRuleParam(rule);
        QProfileDto parentProfile = createProfile(rule);
        QProfileDto childProfile = createChildProfile(parentProfile);
        QProfileDto grandChildProfile = createChildProfile(childProfile);
        RuleActivation initialActivation = RuleActivation.create(rule.getId(), MAJOR, ImmutableMap.of(param.getName(), "foo"));
        activate(parentProfile, initialActivation);
        RuleActivation overrideActivation = RuleActivation.create(rule.getId(), CRITICAL, ImmutableMap.of(param.getName(), "bar"));
        activate(grandChildProfile, overrideActivation);
        // reset parent --> touch child but not grandChild
        RuleActivation updateActivation = RuleActivation.createReset(rule.getId());
        List<ActiveRuleChange> changes = activate(parentProfile, updateActivation);
        assertThatRuleIsUpdated(parentProfile, rule, rule.getSeverityString(), null, ImmutableMap.of(param.getName(), param.getDefaultValue()));
        assertThatRuleIsUpdated(childProfile, rule, rule.getSeverityString(), ActiveRuleInheritance.INHERITED, ImmutableMap.of(param.getName(), param.getDefaultValue()));
        assertThatRuleIsUpdated(grandChildProfile, rule, CRITICAL, OVERRIDES, ImmutableMap.of(param.getName(), "bar"));
        assertThat(changes).hasSize(2);
    }

    @Test
    public void active_on_parent_a_rule_already_activated_on_child() {
        RuleDefinitionDto rule = createRule();
        RuleParamDto param = db.rules().insertRuleParam(rule);
        QProfileDto parentProfile = createProfile(rule);
        QProfileDto childProfile = createChildProfile(parentProfile);
        RuleActivation childActivation = RuleActivation.create(rule.getId(), MAJOR, ImmutableMap.of(param.getName(), "foo"));
        activate(childProfile, childActivation);
        RuleActivation parentActivation = RuleActivation.create(rule.getId(), CRITICAL, ImmutableMap.of(param.getName(), "bar"));
        List<ActiveRuleChange> changes = activate(parentProfile, parentActivation);
        assertThatRuleIsUpdated(parentProfile, rule, CRITICAL, null, ImmutableMap.of(param.getName(), "bar"));
        assertThatRuleIsUpdated(childProfile, rule, MAJOR, OVERRIDES, ImmutableMap.of(param.getName(), "foo"));
        assertThat(changes).hasSize(2);
    }

    @Test
    public void do_not_mark_as_overridden_if_same_values_than_parent() {
        RuleDefinitionDto rule = createRule();
        RuleParamDto param = db.rules().insertRuleParam(rule);
        QProfileDto parentProfile = createProfile(rule);
        QProfileDto childProfile = createChildProfile(parentProfile);
        RuleActivation parentActivation = RuleActivation.create(rule.getId(), MAJOR, ImmutableMap.of(param.getName(), "foo"));
        activate(parentProfile, parentActivation);
        RuleActivation overrideActivation = RuleActivation.create(rule.getId(), MAJOR, ImmutableMap.of(param.getName(), "foo"));
        List<ActiveRuleChange> changes = activate(childProfile, overrideActivation);
        assertThatRuleIsUpdated(childProfile, rule, MAJOR, ActiveRuleInheritance.INHERITED, ImmutableMap.of(param.getName(), "foo"));
        assertThat(changes).hasSize(0);
    }

    @Test
    public void propagate_deactivation_on_children() {
        RuleDefinitionDto rule = createRule();
        QProfileDto parentProfile = createProfile(rule);
        QProfileDto childProfile = createChildProfile(parentProfile);
        RuleActivation activation = RuleActivation.create(rule.getId());
        List<ActiveRuleChange> changes = activate(parentProfile, activation);
        assertThatRuleIsActivated(parentProfile, rule, changes, rule.getSeverityString(), null, Collections.emptyMap());
        assertThatRuleIsActivated(childProfile, rule, changes, rule.getSeverityString(), ActiveRuleInheritance.INHERITED, Collections.emptyMap());
        changes = deactivate(parentProfile, rule);
        assertThatProfileHasNoActiveRules(parentProfile);
        assertThatProfileHasNoActiveRules(childProfile);
        assertThat(changes).hasSize(2);
    }

    @Test
    public void propagate_deactivation_on_children_even_when_overridden() {
        RuleDefinitionDto rule = createRule();
        QProfileDto parentProfile = createProfile(rule);
        QProfileDto childProfile = createChildProfile(parentProfile);
        RuleActivation activation = RuleActivation.create(rule.getId());
        List<ActiveRuleChange> changes = activate(parentProfile, activation);
        assertThatRuleIsActivated(parentProfile, rule, changes, rule.getSeverityString(), null, Collections.emptyMap());
        assertThatRuleIsActivated(childProfile, rule, changes, rule.getSeverityString(), ActiveRuleInheritance.INHERITED, Collections.emptyMap());
        activation = RuleActivation.create(rule.getId(), CRITICAL, null);
        activate(childProfile, activation);
        changes = deactivate(parentProfile, rule);
        assertThatProfileHasNoActiveRules(parentProfile);
        assertThatProfileHasNoActiveRules(childProfile);
        assertThat(changes).hasSize(2);
    }

    @Test
    public void cannot_deactivate_rule_inherited() {
        RuleDefinitionDto rule = createRule();
        QProfileDto parentProfile = createProfile(rule);
        QProfileDto childProfile = createChildProfile(parentProfile);
        RuleActivation activation = RuleActivation.create(rule.getId());
        List<ActiveRuleChange> changes = activate(parentProfile, activation);
        assertThatRuleIsActivated(parentProfile, rule, changes, rule.getSeverityString(), null, Collections.emptyMap());
        assertThatRuleIsActivated(childProfile, rule, changes, rule.getSeverityString(), ActiveRuleInheritance.INHERITED, Collections.emptyMap());
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Cannot deactivate inherited rule");
        deactivate(childProfile, rule);
    }

    @Test
    public void reset_child_profile_do_not_change_parent() {
        RuleDefinitionDto rule = createRule();
        QProfileDto parentProfile = createProfile(rule);
        QProfileDto childProfile = createChildProfile(parentProfile);
        RuleActivation activation = RuleActivation.create(rule.getId(), CRITICAL, null);
        List<ActiveRuleChange> changes = activate(parentProfile, activation);
        assertThatRuleIsActivated(parentProfile, rule, changes, CRITICAL, null, Collections.emptyMap());
        assertThatRuleIsActivated(childProfile, rule, changes, CRITICAL, ActiveRuleInheritance.INHERITED, Collections.emptyMap());
        assertThat(changes).hasSize(2);
        RuleActivation childActivation = RuleActivation.create(rule.getId(), BLOCKER, null);
        changes = activate(childProfile, childActivation);
        assertThatRuleIsUpdated(childProfile, rule, BLOCKER, OVERRIDES, Collections.emptyMap());
        assertThat(changes).hasSize(1);
        RuleActivation resetActivation = RuleActivation.createReset(rule.getId());
        changes = activate(childProfile, resetActivation);
        assertThatRuleIsUpdated(childProfile, rule, CRITICAL, ActiveRuleInheritance.INHERITED, Collections.emptyMap());
        assertThatRuleIsUpdated(parentProfile, rule, CRITICAL, null, Collections.emptyMap());
        assertThat(changes).hasSize(1);
    }

    @Test
    public void reset_parent_is_not_propagated_when_child_overrides() {
        RuleDefinitionDto rule = createRule();
        QProfileDto baseProfile = createProfile(rule);
        QProfileDto childProfile = createChildProfile(baseProfile);
        QProfileDto grandChildProfile = createChildProfile(childProfile);
        RuleActivation activation = RuleActivation.create(rule.getId(), CRITICAL, null);
        List<ActiveRuleChange> changes = activate(baseProfile, activation);
        assertThatRuleIsActivated(baseProfile, rule, changes, CRITICAL, null, Collections.emptyMap());
        assertThatRuleIsActivated(childProfile, rule, changes, CRITICAL, ActiveRuleInheritance.INHERITED, Collections.emptyMap());
        assertThatRuleIsActivated(grandChildProfile, rule, changes, CRITICAL, ActiveRuleInheritance.INHERITED, Collections.emptyMap());
        assertThat(changes).hasSize(3);
        RuleActivation childActivation = RuleActivation.create(rule.getId(), BLOCKER, null);
        changes = activate(childProfile, childActivation);
        assertThatRuleIsUpdated(childProfile, rule, BLOCKER, OVERRIDES, Collections.emptyMap());
        assertThatRuleIsUpdated(grandChildProfile, rule, BLOCKER, ActiveRuleInheritance.INHERITED, Collections.emptyMap());
        assertThat(changes).hasSize(2);
        // Reset on parent do not change child nor grandchild
        RuleActivation resetActivation = RuleActivation.createReset(rule.getId());
        changes = activate(baseProfile, resetActivation);
        assertThatRuleIsUpdated(baseProfile, rule, rule.getSeverityString(), null, Collections.emptyMap());
        assertThatRuleIsUpdated(childProfile, rule, BLOCKER, OVERRIDES, Collections.emptyMap());
        assertThatRuleIsUpdated(grandChildProfile, rule, BLOCKER, ActiveRuleInheritance.INHERITED, Collections.emptyMap());
        assertThat(changes).hasSize(1);
        // Reset on child change grandchild
        resetActivation = RuleActivation.createReset(rule.getId());
        changes = activate(childProfile, resetActivation);
        assertThatRuleIsUpdated(baseProfile, rule, rule.getSeverityString(), null, Collections.emptyMap());
        assertThatRuleIsUpdated(childProfile, rule, rule.getSeverityString(), ActiveRuleInheritance.INHERITED, Collections.emptyMap());
        assertThatRuleIsUpdated(grandChildProfile, rule, rule.getSeverityString(), ActiveRuleInheritance.INHERITED, Collections.emptyMap());
        assertThat(changes).hasSize(2);
    }

    @Test
    public void ignore_reset_if_not_activated() {
        RuleDefinitionDto rule = createRule();
        QProfileDto parentProfile = createProfile(rule);
        createChildProfile(parentProfile);
        RuleActivation resetActivation = RuleActivation.createReset(rule.getId());
        List<ActiveRuleChange> changes = activate(parentProfile, resetActivation);
        verifyNoActiveRules();
        assertThat(changes).hasSize(0);
    }

    @Test
    public void bulk_activation() {
        int bulkSize = ((SearchOptions.MAX_LIMIT) + 10) + (new Random().nextInt(100));
        String language = randomAlphanumeric(10);
        String repositoryKey = randomAlphanumeric(10);
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setLanguage(language));
        List<RuleDto> rules = new ArrayList<>();
        IntStream.rangeClosed(1, bulkSize).forEach(( i) -> rules.add(db.rules().insertRule(( r) -> r.setLanguage(language).setRepositoryKey(repositoryKey))));
        verifyNoActiveRules();
        ruleIndexer.indexOnStartup(ruleIndexer.getIndexTypes());
        RuleQuery ruleQuery = new RuleQuery().setRepositories(Collections.singletonList(repositoryKey));
        BulkChangeResult bulkChangeResult = underTest.bulkActivateAndCommit(db.getSession(), profile, ruleQuery, MINOR);
        assertThat(bulkChangeResult.countFailed()).isEqualTo(0);
        assertThat(bulkChangeResult.countSucceeded()).isEqualTo(bulkSize);
        assertThat(bulkChangeResult.getChanges()).hasSize(bulkSize);
        assertThat(db.getDbClient().activeRuleDao().selectByProfile(db.getSession(), profile)).hasSize(bulkSize);
        rules.stream().forEach(( r) -> assertThatRuleIsActivated(profile, r.getDefinition(), null, MINOR, null, emptyMap()));
    }

    @Test
    public void bulk_deactivation() {
        int bulkSize = ((SearchOptions.MAX_LIMIT) + 10) + (new Random().nextInt(100));
        String language = randomAlphanumeric(10);
        String repositoryKey = randomAlphanumeric(10);
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setLanguage(language));
        List<RuleDto> rules = new ArrayList<>();
        IntStream.rangeClosed(1, bulkSize).forEach(( i) -> rules.add(db.rules().insertRule(( r) -> r.setLanguage(language).setRepositoryKey(repositoryKey))));
        verifyNoActiveRules();
        ruleIndexer.indexOnStartup(ruleIndexer.getIndexTypes());
        RuleQuery ruleQuery = new RuleQuery().setRepositories(Collections.singletonList(repositoryKey));
        BulkChangeResult bulkChangeResult = underTest.bulkActivateAndCommit(db.getSession(), profile, ruleQuery, MINOR);
        assertThat(bulkChangeResult.countFailed()).isEqualTo(0);
        assertThat(bulkChangeResult.countSucceeded()).isEqualTo(bulkSize);
        assertThat(bulkChangeResult.getChanges()).hasSize(bulkSize);
        assertThat(db.getDbClient().activeRuleDao().selectByProfile(db.getSession(), profile)).hasSize(bulkSize);
        // Now deactivate all rules
        bulkChangeResult = underTest.bulkDeactivateAndCommit(db.getSession(), profile, ruleQuery);
        assertThat(bulkChangeResult.countFailed()).isEqualTo(0);
        assertThat(bulkChangeResult.countSucceeded()).isEqualTo(bulkSize);
        assertThat(bulkChangeResult.getChanges()).hasSize(bulkSize);
        assertThat(db.getDbClient().activeRuleDao().selectByProfile(db.getSession(), profile)).hasSize(0);
        rules.stream().forEach(( r) -> assertThatRuleIsNotPresent(profile, r.getDefinition()));
    }

    @Test
    public void bulk_deactivation_ignores_errors() {
        RuleDefinitionDto rule = createRule();
        QProfileDto parentProfile = createProfile(rule);
        QProfileDto childProfile = createChildProfile(parentProfile);
        List<ActiveRuleChange> changes = activate(parentProfile, RuleActivation.create(rule.getId()));
        assertThatRuleIsActivated(parentProfile, rule, null, rule.getSeverityString(), null, Collections.emptyMap());
        assertThatRuleIsActivated(childProfile, rule, null, rule.getSeverityString(), ActiveRuleInheritance.INHERITED, Collections.emptyMap());
        ruleIndexer.indexOnStartup(ruleIndexer.getIndexTypes());
        RuleQuery ruleQuery = new RuleQuery().setQProfile(childProfile);
        BulkChangeResult bulkChangeResult = underTest.bulkDeactivateAndCommit(db.getSession(), childProfile, ruleQuery);
        assertThat(bulkChangeResult.countFailed()).isEqualTo(1);
        assertThat(bulkChangeResult.countSucceeded()).isEqualTo(0);
        assertThat(bulkChangeResult.getChanges()).hasSize(0);
        assertThatRuleIsActivated(parentProfile, rule, null, rule.getSeverityString(), null, Collections.emptyMap());
        assertThatRuleIsActivated(childProfile, rule, null, rule.getSeverityString(), ActiveRuleInheritance.INHERITED, Collections.emptyMap());
    }

    @Test
    public void bulk_change_severity() {
        RuleDefinitionDto rule1 = createJavaRule();
        RuleDefinitionDto rule2 = createJavaRule();
        QProfileDto parentProfile = createProfile(rule1);
        QProfileDto childProfile = createChildProfile(parentProfile);
        QProfileDto grandchildProfile = createChildProfile(childProfile);
        activate(parentProfile, RuleActivation.create(rule1.getId()));
        activate(parentProfile, RuleActivation.create(rule2.getId()));
        ruleIndexer.indexOnStartup(ruleIndexer.getIndexTypes());
        RuleQuery query = new RuleQuery().setRuleKey(rule1.getRuleKey()).setQProfile(parentProfile);
        BulkChangeResult result = underTest.bulkActivateAndCommit(db.getSession(), parentProfile, query, "BLOCKER");
        assertThat(result.getChanges()).hasSize(3);
        assertThat(result.countSucceeded()).isEqualTo(1);
        assertThat(result.countFailed()).isEqualTo(0);
        // Rule1 must be activated with BLOCKER on all profiles
        assertThatRuleIsActivated(parentProfile, rule1, null, BLOCKER, null, Collections.emptyMap());
        assertThatRuleIsActivated(childProfile, rule1, null, BLOCKER, ActiveRuleInheritance.INHERITED, Collections.emptyMap());
        assertThatRuleIsActivated(grandchildProfile, rule1, null, BLOCKER, ActiveRuleInheritance.INHERITED, Collections.emptyMap());
        // Rule2 did not changed
        assertThatRuleIsActivated(parentProfile, rule2, null, rule2.getSeverityString(), null, Collections.emptyMap());
        assertThatRuleIsActivated(childProfile, rule2, null, rule2.getSeverityString(), ActiveRuleInheritance.INHERITED, Collections.emptyMap());
        assertThatRuleIsActivated(grandchildProfile, rule2, null, rule2.getSeverityString(), ActiveRuleInheritance.INHERITED, Collections.emptyMap());
    }

    @Test
    public void delete_rule_from_all_profiles() {
        RuleDefinitionDto rule = createRule();
        QProfileDto parentProfile = createProfile(rule);
        QProfileDto childProfile = createChildProfile(parentProfile);
        QProfileDto grandChildProfile = createChildProfile(childProfile);
        RuleActivation activation = RuleActivation.create(rule.getId(), CRITICAL, null);
        activate(parentProfile, activation);
        RuleActivation overrideActivation = RuleActivation.create(rule.getId(), BLOCKER, null);
        activate(grandChildProfile, overrideActivation);
        // Reset on parent do not change child nor grandchild
        List<ActiveRuleChange> changes = underTest.deleteRule(db.getSession(), rule);
        assertThatRuleIsNotPresent(parentProfile, rule);
        assertThatRuleIsNotPresent(childProfile, rule);
        assertThatRuleIsNotPresent(grandChildProfile, rule);
        assertThat(changes).extracting(ActiveRuleChange::getType).containsOnly(DEACTIVATED).hasSize(3);
    }

    @Test
    public void activation_fails_when_profile_is_built_in() {
        RuleDefinitionDto rule = createRule();
        QProfileDto builtInProfile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setLanguage(rule.getLanguage()).setIsBuiltIn(true));
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage((("The built-in profile " + (builtInProfile.getName())) + " is read-only and can't be updated"));
        underTest.activateAndCommit(db.getSession(), builtInProfile, Collections.singleton(RuleActivation.create(rule.getId())));
    }
}

