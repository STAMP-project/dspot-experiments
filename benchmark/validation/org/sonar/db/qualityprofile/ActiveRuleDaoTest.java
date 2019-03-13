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
package org.sonar.db.qualityprofile;


import ActiveRuleCountQuery.Builder;
import RuleType.BUG;
import RuleType.CODE_SMELL;
import RuleType.SECURITY_HOTSPOT;
import RuleType.VULNERABILITY;
import Severity.CRITICAL;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.db.rule.RuleParamDto;


public class ActiveRuleDaoTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final long NOW = 10000000L;

    private OrganizationDto organization;

    private QProfileDto profile1;

    private QProfileDto profile2;

    private RuleDefinitionDto rule1;

    private RuleDefinitionDto rule2;

    private RuleDefinitionDto rule3;

    private RuleDefinitionDto removedRule;

    private RuleParamDto rule1Param1;

    private RuleParamDto rule1Param2;

    private RuleParamDto rule2Param1;

    private System2 system = new TestSystem2().setNow(ActiveRuleDaoTest.NOW);

    @Rule
    public DbTester db = DbTester.create(system);

    private DbSession dbSession = db.getSession();

    private ActiveRuleDao underTest = db.getDbClient().activeRuleDao();

    @Test
    public void selectByKey() {
        ActiveRuleDto activeRule = ActiveRuleDto.createFor(profile1, rule1).setSeverity(BLOCKER);
        underTest.insert(dbSession, activeRule);
        assertThat(underTest.selectByKey(dbSession, activeRule.getKey())).isPresent();
        assertThat(underTest.selectByKey(dbSession, ActiveRuleKey.of(profile2, rule2.getKey()))).isEmpty();
    }

    @Test
    public void selectByRuleId() {
        ActiveRuleDto activeRule1 = ActiveRuleDto.createFor(profile1, rule1).setSeverity(BLOCKER);
        ActiveRuleDto activeRule2 = ActiveRuleDto.createFor(profile2, rule1).setSeverity(BLOCKER);
        underTest.insert(dbSession, activeRule1);
        underTest.insert(dbSession, activeRule2);
        dbSession.commit();
        assertThat(underTest.selectByRuleId(dbSession, organization, rule1.getId())).extracting("key").containsOnly(activeRule1.getKey(), activeRule2.getKey());
        assertThat(underTest.selectByRuleId(dbSession, organization, rule3.getId())).isEmpty();
    }

    @Test
    public void selectByRuleIds() {
        ActiveRuleDto activeRule1 = ActiveRuleDto.createFor(profile1, rule1).setSeverity(BLOCKER);
        ActiveRuleDto activeRule2 = ActiveRuleDto.createFor(profile1, rule2).setSeverity(BLOCKER);
        ActiveRuleDto activeRule3 = ActiveRuleDto.createFor(profile2, rule1).setSeverity(BLOCKER);
        underTest.insert(dbSession, activeRule1);
        underTest.insert(dbSession, activeRule2);
        underTest.insert(dbSession, activeRule3);
        dbSession.commit();
        assertThat(underTest.selectByRuleIds(dbSession, organization, Arrays.asList(rule1.getId()))).extracting("key").containsOnly(activeRule1.getKey(), activeRule3.getKey());
        assertThat(underTest.selectByRuleIds(dbSession, organization, Lists.newArrayList(rule1.getId(), rule2.getId()))).extracting("key").containsOnly(activeRule1.getKey(), activeRule2.getKey(), activeRule3.getKey());
    }

    @Test
    public void selectByProfile() {
        ActiveRuleDto activeRule1 = ActiveRuleDto.createFor(profile1, rule1).setSeverity(BLOCKER);
        ActiveRuleDto activeRule2 = ActiveRuleDto.createFor(profile1, rule2).setSeverity(BLOCKER);
        underTest.insert(dbSession, activeRule1);
        underTest.insert(dbSession, activeRule2);
        List<OrgActiveRuleDto> result = underTest.selectByProfile(dbSession, profile1);
        assertThat(result).hasSize(2).extracting(OrgActiveRuleDto::getOrganizationUuid, OrgActiveRuleDto::getProfileUuid, OrgActiveRuleDto::getProfileId).containsOnly(tuple(organization.getUuid(), profile1.getKee(), profile1.getId()));
        assertThat(underTest.selectByProfile(dbSession, profile2)).isEmpty();
    }

    @Test
    public void selectByProfileUuid_ignores_removed_rules() {
        ActiveRuleDto activeRule = ActiveRuleDto.createFor(profile1, removedRule).setSeverity(BLOCKER);
        underTest.insert(dbSession, activeRule);
        assertThat(underTest.selectByProfile(dbSession, profile1)).isEmpty();
    }

    @Test
    public void selectByTypeAndProfileUuids() {
        RuleDefinitionDto rule1 = db.rules().insert(( r) -> r.setType(VULNERABILITY.getDbConstant()));
        ActiveRuleDto activeRule1 = ActiveRuleDto.createFor(profile1, rule1).setSeverity(BLOCKER);
        underTest.insert(dbSession, activeRule1);
        assertThat(underTest.selectByTypeAndProfileUuids(dbSession, Collections.singletonList(VULNERABILITY.getDbConstant()), Collections.singletonList(profile1.getKee()))).extracting(OrgActiveRuleDto::getProfileUuid, OrgActiveRuleDto::getOrganizationUuid, OrgActiveRuleDto::getRuleId).contains(tuple(profile1.getKee(), profile1.getOrganizationUuid(), rule1.getId()));
    }

    @Test
    public void selectByTypeAndProfileUuids_ignores_rules_in_other_profiles() {
        RuleDefinitionDto rule1 = db.rules().insert(( r) -> r.setType(VULNERABILITY.getDbConstant()));
        ActiveRuleDto activeRule1 = ActiveRuleDto.createFor(profile2, rule1).setSeverity(BLOCKER);
        underTest.insert(dbSession, activeRule1);
        assertThat(underTest.selectByTypeAndProfileUuids(dbSession, Collections.singletonList(VULNERABILITY.getDbConstant()), Collections.singletonList(profile1.getKee()))).isEmpty();
    }

    @Test
    public void selectByTypeAndProfileUuids_ignores_rules_with_another_rule_type() {
        RuleDefinitionDto rule1 = db.rules().insert(( r) -> r.setType(VULNERABILITY.getDbConstant()));
        ActiveRuleDto activeRule1 = ActiveRuleDto.createFor(profile1, rule1).setSeverity(BLOCKER);
        underTest.insert(dbSession, activeRule1);
        assertThat(underTest.selectByTypeAndProfileUuids(dbSession, Collections.singletonList(VULNERABILITY.getDbConstant()), Collections.singletonList(profile1.getKee()))).extracting(OrgActiveRuleDto::getProfileUuid, OrgActiveRuleDto::getOrganizationUuid, OrgActiveRuleDto::getRuleId).contains(tuple(profile1.getKee(), profile1.getOrganizationUuid(), rule1.getId()));
        assertThat(underTest.selectByTypeAndProfileUuids(dbSession, Arrays.asList(CODE_SMELL.getDbConstant(), SECURITY_HOTSPOT.getDbConstant(), BUG.getDbConstant()), Collections.singletonList(profile1.getKee()))).isEmpty();
    }

    @Test
    public void selectByRuleProfile() {
        ActiveRuleDto activeRule1 = ActiveRuleDto.createFor(profile1, rule1).setSeverity(BLOCKER);
        ActiveRuleDto activeRule2 = ActiveRuleDto.createFor(profile1, rule2).setSeverity(MAJOR);
        underTest.insert(dbSession, activeRule1);
        underTest.insert(dbSession, activeRule2);
        List<ActiveRuleDto> result = underTest.selectByRuleProfile(dbSession, RulesProfileDto.from(profile1));
        assertThat(result).hasSize(2).extracting(ActiveRuleDto::getProfileId, ActiveRuleDto::getRuleKey, ActiveRuleDto::getSeverityString).containsOnly(tuple(profile1.getId(), rule1.getKey(), BLOCKER), tuple(profile1.getId(), rule2.getKey(), MAJOR));
        assertThat(underTest.selectByProfile(dbSession, profile2)).isEmpty();
    }

    @Test
    public void selectByRulesAndRuleProfileUuids() {
        ActiveRuleDto rule1P1 = ActiveRuleDto.createFor(profile1, rule1).setSeverity(MAJOR);
        ActiveRuleDto rule2P1 = ActiveRuleDto.createFor(profile1, rule2).setSeverity(MAJOR);
        ActiveRuleDto rule1P2 = ActiveRuleDto.createFor(profile2, rule1).setSeverity(MAJOR);
        underTest.insert(dbSession, rule1P1);
        underTest.insert(dbSession, rule2P1);
        underTest.insert(dbSession, rule1P2);
        // empty rules
        Collection<ActiveRuleDto> result = underTest.selectByRulesAndRuleProfileUuids(dbSession, Collections.emptyList(), Arrays.asList(profile1.getRulesProfileUuid()));
        assertThat(result).isEmpty();
        // empty profiles
        result = underTest.selectByRulesAndRuleProfileUuids(dbSession, Arrays.asList(rule1), Collections.emptyList());
        assertThat(result).isEmpty();
        // match
        result = underTest.selectByRulesAndRuleProfileUuids(dbSession, Arrays.asList(rule1), Arrays.asList(profile1.getRulesProfileUuid(), profile2.getRulesProfileUuid()));
        assertThat(result).extracting(ActiveRuleDto::getId).containsExactlyInAnyOrder(rule1P1.getId(), rule1P2.getId());
        result = underTest.selectByRulesAndRuleProfileUuids(dbSession, Arrays.asList(rule1, rule2), Arrays.asList(profile1.getRulesProfileUuid(), profile2.getRulesProfileUuid()));
        assertThat(result).extracting(ActiveRuleDto::getId).containsExactlyInAnyOrder(rule1P1.getId(), rule1P2.getId(), rule2P1.getId());
        // do not match
        result = underTest.selectByRulesAndRuleProfileUuids(dbSession, Arrays.asList(rule3), Arrays.asList(profile1.getRulesProfileUuid(), profile2.getRulesProfileUuid()));
        assertThat(result).isEmpty();
        result = underTest.selectByRulesAndRuleProfileUuids(dbSession, Arrays.asList(rule1), Arrays.asList("unknown"));
        assertThat(result).isEmpty();
    }

    @Test
    public void insert() {
        ActiveRuleDto activeRule = ActiveRuleDto.createFor(profile1, rule1).setSeverity(BLOCKER).setInheritance(ActiveRuleDto.INHERITED).setCreatedAt(1000L).setUpdatedAt(2000L);
        underTest.insert(dbSession, activeRule);
        dbSession.commit();
        ActiveRuleDto result = underTest.selectByKey(dbSession, activeRule.getKey()).get();
        assertThat(result.getId()).isEqualTo(activeRule.getId());
        assertThat(result.getKey()).isEqualTo(ActiveRuleKey.of(profile1, rule1.getKey()));
        assertThat(result.getRuleId()).isEqualTo(rule1.getId());
        assertThat(result.getProfileId()).isEqualTo(profile1.getId());
        assertThat(result.getSeverityString()).isEqualTo(BLOCKER);
        assertThat(result.getInheritance()).isEqualTo(ActiveRuleDto.INHERITED);
        assertThat(result.getCreatedAt()).isEqualTo(1000L);
        assertThat(result.getUpdatedAt()).isEqualTo(2000L);
    }

    @Test
    public void fail_to_insert_when_profile_id_is_null() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Quality profile is not persisted (missing id)");
        underTest.insert(dbSession, ActiveRuleDto.createFor(profile1, rule1).setProfileId(null));
    }

    @Test
    public void fail_to_insert_when_rule_id_is_null() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Rule is not persisted");
        underTest.insert(dbSession, ActiveRuleDto.createFor(profile1, rule1).setRuleId(null));
    }

    @Test
    public void fail_to_insert_when_id_is_not_null() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("ActiveRule is already persisted");
        underTest.insert(dbSession, ActiveRuleDto.createFor(profile1, rule1).setId(100));
    }

    @Test
    public void update() {
        ActiveRuleDto activeRule = ActiveRuleDto.createFor(profile1, rule1).setSeverity(BLOCKER).setInheritance(ActiveRuleDto.INHERITED).setCreatedAt(1000L).setUpdatedAt(2000L);
        underTest.insert(dbSession, activeRule);
        dbSession.commit();
        ActiveRuleDto activeRuleUpdated = // created at should not be updated
        activeRule.setSeverity(MAJOR).setInheritance(ActiveRuleDto.OVERRIDES).setCreatedAt(3000L).setUpdatedAt(4000L);
        underTest.update(dbSession, activeRuleUpdated);
        dbSession.commit();
        ActiveRuleDto result = underTest.selectByKey(dbSession, ActiveRuleKey.of(profile1, rule1.getKey())).get();
        assertThat(result.getId()).isEqualTo(activeRule.getId());
        assertThat(result.getKey()).isEqualTo(ActiveRuleKey.of(profile1, rule1.getKey()));
        assertThat(result.getRuleId()).isEqualTo(rule1.getId());
        assertThat(result.getProfileId()).isEqualTo(profile1.getId());
        assertThat(result.getSeverityString()).isEqualTo(MAJOR);
        assertThat(result.getInheritance()).isEqualTo(ActiveRuleDto.OVERRIDES);
        assertThat(result.getCreatedAt()).isEqualTo(1000L);
        assertThat(result.getUpdatedAt()).isEqualTo(4000L);
    }

    @Test
    public void fail_to_update_when_profile_id_is_null() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Quality profile is not persisted (missing id)");
        underTest.update(dbSession, ActiveRuleDto.createFor(profile1, rule1).setId(100).setProfileId(null));
    }

    @Test
    public void fail_to_update_when_rule_id_is_null() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Rule is not persisted");
        underTest.update(dbSession, ActiveRuleDto.createFor(profile1, rule1).setId(100).setRuleId(null));
    }

    @Test
    public void fail_to_update_when_id_is_null() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("ActiveRule is not persisted");
        underTest.update(dbSession, ActiveRuleDto.createFor(profile1, rule1).setId(null));
    }

    @Test
    public void delete() {
        ActiveRuleDto activeRule = ActiveRuleDto.createFor(profile1, rule1).setSeverity(BLOCKER).setInheritance(ActiveRuleDto.INHERITED).setCreatedAt(1000L).setUpdatedAt(2000L);
        underTest.insert(dbSession, activeRule);
        underTest.delete(dbSession, activeRule.getKey());
        assertThat(underTest.selectByKey(dbSession, ActiveRuleKey.of(profile1, rule1.getKey()))).isEmpty();
    }

    @Test
    public void delete_does_not_fail_when_active_rule_does_not_exist() {
        underTest.delete(dbSession, ActiveRuleKey.of(profile1, rule1.getKey()));
    }

    @Test
    public void deleteByRuleProfileUuids_deletes_rows_from_table() {
        underTest.insert(dbSession, ActiveRuleDaoTest.newRow(profile1, rule1));
        underTest.insert(dbSession, ActiveRuleDaoTest.newRow(profile1, rule2));
        underTest.insert(dbSession, ActiveRuleDaoTest.newRow(profile2, rule1));
        underTest.deleteByRuleProfileUuids(dbSession, Arrays.asList(profile1.getRulesProfileUuid()));
        assertThat(db.countRowsOfTable(dbSession, "active_rules")).isEqualTo(1);
        assertThat(underTest.selectByKey(dbSession, ActiveRuleKey.of(profile2, rule1.getKey()))).isPresent();
    }

    @Test
    public void deleteByRuleProfileUuids_does_not_fail_when_rules_profile_with_specified_key_does_not_exist() {
        underTest.insert(dbSession, ActiveRuleDaoTest.newRow(profile1, rule1));
        underTest.deleteByRuleProfileUuids(dbSession, Arrays.asList("does_not_exist"));
        assertThat(db.countRowsOfTable(dbSession, "active_rules")).isEqualTo(1);
    }

    @Test
    public void deleteByIds() {
        ActiveRuleDto ar1 = underTest.insert(dbSession, ActiveRuleDaoTest.newRow(profile1, rule1));
        ActiveRuleDto ar2 = underTest.insert(dbSession, ActiveRuleDaoTest.newRow(profile1, rule2));
        ActiveRuleDto ar3 = underTest.insert(dbSession, ActiveRuleDaoTest.newRow(profile2, rule1));
        underTest.deleteByIds(dbSession, Arrays.asList(ar1.getId(), ar3.getId()));
        assertThat(db.countRowsOfTable(dbSession, "active_rules")).isEqualTo(1);
        assertThat(underTest.selectByProfile(dbSession, profile1)).extracting(ActiveRuleDto::getId).containsExactly(ar2.getId());
    }

    @Test
    public void deleteByIds_does_nothing_if_empty_list_of_ids() {
        underTest.insert(dbSession, ActiveRuleDaoTest.newRow(profile1, rule1));
        underTest.deleteByIds(dbSession, Collections.emptyList());
        assertThat(db.countRowsOfTable(dbSession, "active_rules")).isEqualTo(1);
    }

    @Test
    public void select_params_by_active_rule_id() {
        ActiveRuleDto activeRule = ActiveRuleDto.createFor(profile1, rule1).setSeverity(BLOCKER);
        underTest.insert(dbSession, activeRule);
        ActiveRuleParamDto activeRuleParam1 = ActiveRuleParamDto.createFor(rule1Param1);
        underTest.insertParam(dbSession, activeRule, activeRuleParam1);
        ActiveRuleParamDto activeRuleParam2 = ActiveRuleParamDto.createFor(rule1Param2);
        underTest.insertParam(dbSession, activeRule, activeRuleParam2);
        dbSession.commit();
        assertThat(underTest.selectParamsByActiveRuleId(dbSession, activeRule.getId())).hasSize(2);
    }

    @Test
    public void select_params_by_active_rule_ids() {
        ActiveRuleDto activeRule1 = ActiveRuleDto.createFor(profile1, rule1).setSeverity(BLOCKER);
        underTest.insert(dbSession, activeRule1);
        underTest.insertParam(dbSession, activeRule1, ActiveRuleParamDto.createFor(rule1Param1));
        underTest.insertParam(dbSession, activeRule1, ActiveRuleParamDto.createFor(rule1Param2));
        ActiveRuleDto activeRule2 = ActiveRuleDto.createFor(profile1, rule2).setSeverity(BLOCKER);
        underTest.insert(dbSession, activeRule2);
        underTest.insertParam(dbSession, activeRule2, ActiveRuleParamDto.createFor(rule2Param1));
        dbSession.commit();
        assertThat(underTest.selectParamsByActiveRuleIds(dbSession, Arrays.asList(activeRule1.getId(), activeRule2.getId()))).hasSize(3);
    }

    @Test
    public void insertParam() {
        ActiveRuleDto activeRule = ActiveRuleDto.createFor(profile1, rule1).setSeverity(CRITICAL);
        underTest.insert(dbSession, activeRule);
        ActiveRuleParamDto activeRuleParam = ActiveRuleParamDto.createFor(rule1Param1).setValue("foo");
        underTest.insertParam(dbSession, activeRule, activeRuleParam);
        List<ActiveRuleParamDto> reloaded = underTest.selectParamsByActiveRuleId(dbSession, activeRule.getId());
        assertThat(reloaded).hasSize(1);
        assertThat(reloaded.get(0)).matches(( p) -> Objects.equals(p.getId(), activeRuleParam.getId())).matches(( p) -> p.getKey().equals(activeRuleParam.getKey())).matches(( p) -> p.getActiveRuleId().equals(activeRule.getId())).matches(( p) -> p.getRulesParameterId().equals(rule1Param1.getId())).matches(( p) -> p.getValue().equals("foo"));
    }

    @Test
    public void insertParam_fails_when_active_rule_id_is_null() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("ActiveRule is not persisted");
        underTest.insertParam(dbSession, ActiveRuleDto.createFor(profile1, rule1).setId(null), ActiveRuleParamDto.createFor(rule1Param1).setValue("activeValue1"));
    }

    @Test
    public void insertParam_fails_when_active_rule_param_id_is_null() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("ActiveRuleParam is already persisted");
        underTest.insertParam(dbSession, ActiveRuleDto.createFor(profile1, rule1).setId(100), ActiveRuleParamDto.createFor(rule1Param1).setValue("activeValue1").setId(100));
    }

    @Test
    public void insertParam_fails_when_id_is_not_null() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Rule param is not persisted");
        underTest.insertParam(dbSession, ActiveRuleDto.createFor(profile1, rule1).setId(100), ActiveRuleParamDto.createFor(rule1Param1).setValue("activeValue1").setRulesParameterId(null));
    }

    @Test
    public void updateParam() {
        ActiveRuleDto activeRule = ActiveRuleDto.createFor(profile1, rule1).setSeverity(CRITICAL);
        underTest.insert(dbSession, activeRule);
        ActiveRuleParamDto activeRuleParam = ActiveRuleParamDto.createFor(rule1Param1).setValue("foo");
        underTest.insertParam(dbSession, activeRule, activeRuleParam);
        underTest.updateParam(dbSession, activeRuleParam.setValue("bar"));
        List<ActiveRuleParamDto> reloaded = underTest.selectParamsByActiveRuleId(dbSession, activeRule.getId());
        assertThat(reloaded).hasSize(1);
        assertThat(reloaded.get(0)).matches(( p) -> Objects.equals(p.getId(), activeRuleParam.getId())).matches(( p) -> p.getKey().equals(activeRuleParam.getKey())).matches(( p) -> p.getActiveRuleId().equals(activeRule.getId())).matches(( p) -> p.getRulesParameterId().equals(rule1Param1.getId())).matches(( p) -> p.getValue().equals("bar"));
    }

    @Test
    public void deleteParam_deletes_rows_by_id() {
        ActiveRuleDto activeRule = ActiveRuleDaoTest.newRow(profile1, rule1);
        underTest.insert(dbSession, activeRule);
        ActiveRuleParamDto param = ActiveRuleParamDto.createFor(rule1Param1).setValue("foo");
        underTest.insertParam(dbSession, activeRule, param);
        underTest.deleteParam(dbSession, param);
        assertThat(underTest.selectParamsByActiveRuleId(dbSession, activeRule.getId())).hasSize(0);
    }

    @Test
    public void deleteParametersByRuleProfileUuids_deletes_rows_by_rule_profile_uuids() {
        ActiveRuleDto activeRuleInProfile1 = ActiveRuleDaoTest.newRow(profile1, rule1);
        underTest.insert(dbSession, activeRuleInProfile1);
        ActiveRuleParamDto param1 = ActiveRuleParamDto.createFor(rule1Param1).setValue("foo");
        underTest.insertParam(dbSession, activeRuleInProfile1, param1);
        ActiveRuleDto activeRuleInProfile2 = ActiveRuleDaoTest.newRow(profile2, rule1);
        underTest.insert(dbSession, activeRuleInProfile2);
        ActiveRuleParamDto param2 = ActiveRuleParamDto.createFor(rule1Param1).setValue("bar");
        underTest.insertParam(dbSession, activeRuleInProfile2, param2);
        underTest.deleteParametersByRuleProfileUuids(dbSession, Arrays.asList(profile1.getRulesProfileUuid(), "does_not_exist"));
        assertThat(underTest.selectParamsByActiveRuleId(dbSession, activeRuleInProfile1.getId())).isEmpty();
        assertThat(underTest.selectParamsByActiveRuleId(dbSession, activeRuleInProfile2.getId())).extracting(ActiveRuleParamDto::getKey, ActiveRuleParamDto::getValue).containsExactly(tuple(rule1Param1.getName(), "bar"));
    }

    @Test
    public void deleteParametersByRuleProfileUuids_does_nothing_if_keys_are_empty() {
        ActiveRuleDto activeRuleInProfile1 = ActiveRuleDaoTest.newRow(profile1, rule1);
        underTest.insert(dbSession, activeRuleInProfile1);
        ActiveRuleParamDto param1 = ActiveRuleParamDto.createFor(rule1Param1).setValue("foo");
        underTest.insertParam(dbSession, activeRuleInProfile1, param1);
        underTest.deleteParametersByRuleProfileUuids(dbSession, Collections.emptyList());
        assertThat(underTest.selectParamsByActiveRuleId(dbSession, activeRuleInProfile1.getId())).hasSize(1);
    }

    @Test
    public void deleteParamsByRuleParamOfAllOrganizations() {
        ActiveRuleDto activeRule1 = ActiveRuleDto.createFor(profile1, rule1).setSeverity(BLOCKER);
        underTest.insert(dbSession, activeRule1);
        ActiveRuleParamDto activeRuleParam1 = ActiveRuleParamDto.createFor(rule1Param1).setValue("foo");
        underTest.insertParam(dbSession, activeRule1, activeRuleParam1);
        ActiveRuleDto activeRule2 = ActiveRuleDto.createFor(profile2, rule1).setSeverity(BLOCKER);
        underTest.insert(dbSession, activeRule2);
        ActiveRuleParamDto activeRuleParam2 = ActiveRuleParamDto.createFor(rule1Param1).setValue("bar");
        underTest.insertParam(dbSession, activeRule2, activeRuleParam2);
        List<Integer> activeRuleIds = Arrays.asList(activeRule1.getId(), activeRule2.getId());
        assertThat(underTest.selectParamsByActiveRuleIds(dbSession, activeRuleIds)).hasSize(2);
        underTest.deleteParamsByRuleParamOfAllOrganizations(dbSession, rule1Param1);
        assertThat(underTest.selectParamsByActiveRuleIds(dbSession, activeRuleIds)).isEmpty();
    }

    @Test
    public void deleteParamsByActiveRuleIds() {
        ActiveRuleDto ar1 = underTest.insert(dbSession, ActiveRuleDaoTest.newRow(profile1, rule1));
        ActiveRuleParamDto param = ActiveRuleParamDto.createFor(rule1Param1).setValue("foo");
        underTest.insertParam(dbSession, ar1, param);
        ActiveRuleDto ar2 = underTest.insert(dbSession, ActiveRuleDaoTest.newRow(profile1, rule2));
        ActiveRuleParamDto param2 = ActiveRuleParamDto.createFor(rule2Param1).setValue("bar");
        underTest.insertParam(dbSession, ar2, param2);
        underTest.deleteParamsByActiveRuleIds(dbSession, Arrays.asList(ar1.getId()));
        assertThat(underTest.selectParamsByActiveRuleId(dbSession, ar1.getId())).hasSize(0);
        assertThat(underTest.selectParamsByActiveRuleId(dbSession, ar2.getId())).hasSize(1);
    }

    @Test
    public void countActiveRulesByQuery_filter_by_profiles() {
        db.qualityProfiles().activateRule(profile1, rule1);
        db.qualityProfiles().activateRule(profile1, rule2);
        db.qualityProfiles().activateRule(profile1, removedRule);
        db.qualityProfiles().activateRule(profile2, rule1);
        QProfileDto profileWithoutActiveRule = db.qualityProfiles().insert(organization);
        ActiveRuleCountQuery.Builder builder = ActiveRuleCountQuery.builder().setOrganization(organization);
        assertThat(underTest.countActiveRulesByQuery(dbSession, builder.setProfiles(Arrays.asList(profile1, profile2)).build())).containsOnly(entry(profile1.getKee(), 2L), entry(profile2.getKee(), 1L));
        assertThat(underTest.countActiveRulesByQuery(dbSession, builder.setProfiles(Arrays.asList(profileWithoutActiveRule)).build())).isEmpty();
        assertThat(underTest.countActiveRulesByQuery(dbSession, builder.setProfiles(Arrays.asList(profile1, profile2, profileWithoutActiveRule)).build())).containsOnly(entry(profile1.getKee(), 2L), entry(profile2.getKee(), 1L));
        assertThat(underTest.countActiveRulesByQuery(dbSession, builder.setProfiles(Collections.emptyList()).build())).isEmpty();
    }

    @Test
    public void countActiveRulesByQuery_filter_by_rule_status() {
        RuleDefinitionDto betaRule = db.rules().insert(( r) -> r.setStatus(BETA));
        db.qualityProfiles().activateRule(profile1, rule1);
        db.qualityProfiles().activateRule(profile1, rule2);
        db.qualityProfiles().activateRule(profile1, betaRule);
        db.qualityProfiles().activateRule(profile1, removedRule);
        db.qualityProfiles().activateRule(profile2, rule1);
        db.qualityProfiles().activateRule(profile2, betaRule);
        ActiveRuleCountQuery.Builder builder = ActiveRuleCountQuery.builder().setOrganization(organization);
        assertThat(underTest.countActiveRulesByQuery(dbSession, builder.setProfiles(Arrays.asList(profile1, profile2)).setRuleStatus(BETA).build())).containsOnly(entry(profile1.getKee(), 1L), entry(profile2.getKee(), 1L));
        assertThat(underTest.countActiveRulesByQuery(dbSession, builder.setProfiles(Arrays.asList(profile1)).setRuleStatus(READY).build())).containsOnly(entry(profile1.getKee(), 2L));
        assertThat(underTest.countActiveRulesByQuery(dbSession, builder.setProfiles(Arrays.asList(profile1)).setRuleStatus(REMOVED).build())).containsOnly(entry(profile1.getKee(), 1L));
    }

    @Test
    public void countActiveRulesByQuery_filter_by_inheritance() {
        db.qualityProfiles().activateRule(profile1, rule1);
        db.qualityProfiles().activateRule(profile1, rule2, ( ar) -> ar.setInheritance(OVERRIDES));
        db.qualityProfiles().activateRule(profile1, removedRule, ( ar) -> ar.setInheritance(OVERRIDES));
        db.qualityProfiles().activateRule(profile2, rule1, ( ar) -> ar.setInheritance(OVERRIDES));
        db.qualityProfiles().activateRule(profile2, rule2, ( ar) -> ar.setInheritance(INHERITED));
        ActiveRuleCountQuery.Builder builder = ActiveRuleCountQuery.builder().setOrganization(organization);
        assertThat(underTest.countActiveRulesByQuery(dbSession, builder.setProfiles(Arrays.asList(profile1, profile2)).setInheritance(ActiveRuleDto.OVERRIDES).build())).containsOnly(entry(profile1.getKee(), 1L), entry(profile2.getKee(), 1L));
        assertThat(underTest.countActiveRulesByQuery(dbSession, builder.setProfiles(Arrays.asList(profile1, profile2)).setInheritance(ActiveRuleDto.INHERITED).build())).containsOnly(entry(profile2.getKee(), 1L));
    }

    @Test
    public void countActiveRulesByQuery_filter_by_organization() {
        db.qualityProfiles().activateRule(profile1, rule1);
        OrganizationDto anotherOrganization = db.organizations().insert();
        QProfileDto profileOnAnotherOrganization = db.qualityProfiles().insert(anotherOrganization);
        db.qualityProfiles().activateRule(profileOnAnotherOrganization, rule1);
        assertThat(underTest.countActiveRulesByQuery(dbSession, ActiveRuleCountQuery.builder().setOrganization(organization).setProfiles(Arrays.asList(profile1, profileOnAnotherOrganization)).build())).containsOnly(entry(profile1.getKee(), 1L));
    }

    @Test
    public void scrollAllForIndexing_empty_table() {
        ActiveRuleDaoTest.Accumulator accumulator = new ActiveRuleDaoTest.Accumulator();
        underTest.scrollAllForIndexing(dbSession, accumulator);
        assertThat(accumulator.list).isEmpty();
    }

    @Test
    public void scrollAllForIndexing() {
        ActiveRuleDto ar1 = db.qualityProfiles().activateRule(profile1, rule1);
        ActiveRuleDto ar2 = db.qualityProfiles().activateRule(profile2, rule1);
        ActiveRuleDto ar3 = db.qualityProfiles().activateRule(profile2, rule2);
        ActiveRuleDaoTest.Accumulator accumulator = new ActiveRuleDaoTest.Accumulator();
        underTest.scrollAllForIndexing(dbSession, accumulator);
        assertThat(accumulator.list).extracting(IndexedActiveRuleDto::getId, IndexedActiveRuleDto::getRuleId, IndexedActiveRuleDto::getRepository, IndexedActiveRuleDto::getKey, IndexedActiveRuleDto::getRuleProfileUuid, IndexedActiveRuleDto::getSeverity, IndexedActiveRuleDto::getInheritance).containsExactlyInAnyOrder(tuple(((long) (ar1.getId())), rule1.getId(), ar1.getRuleKey().repository(), ar1.getRuleKey().rule(), profile1.getRulesProfileUuid(), ar1.getSeverity(), ar1.getInheritance()), tuple(((long) (ar2.getId())), rule1.getId(), ar2.getRuleKey().repository(), ar2.getRuleKey().rule(), profile2.getRulesProfileUuid(), ar2.getSeverity(), ar2.getInheritance()), tuple(((long) (ar3.getId())), rule2.getId(), ar3.getRuleKey().repository(), ar3.getRuleKey().rule(), profile2.getRulesProfileUuid(), ar3.getSeverity(), ar3.getInheritance()));
    }

    @Test
    public void scrollByIdsForIndexing() {
        ActiveRuleDto ar1 = db.qualityProfiles().activateRule(profile1, rule1);
        ActiveRuleDto ar2 = db.qualityProfiles().activateRule(profile2, rule1);
        ActiveRuleDto ar3 = db.qualityProfiles().activateRule(profile2, rule2);
        ActiveRuleDaoTest.Accumulator accumulator = new ActiveRuleDaoTest.Accumulator();
        underTest.scrollByIdsForIndexing(dbSession, Arrays.asList(((long) (ar1.getId())), ((long) (ar2.getId()))), accumulator);
        assertThat(accumulator.list).extracting(IndexedActiveRuleDto::getId, IndexedActiveRuleDto::getRuleId, IndexedActiveRuleDto::getRepository, IndexedActiveRuleDto::getKey, IndexedActiveRuleDto::getRuleProfileUuid, IndexedActiveRuleDto::getSeverity).containsExactlyInAnyOrder(tuple(((long) (ar1.getId())), rule1.getId(), ar1.getRuleKey().repository(), ar1.getRuleKey().rule(), profile1.getRulesProfileUuid(), ar1.getSeverity()), tuple(((long) (ar2.getId())), rule1.getId(), ar2.getRuleKey().repository(), ar2.getRuleKey().rule(), profile2.getRulesProfileUuid(), ar2.getSeverity()));
    }

    @Test
    public void scrollByRuleProfileForIndexing() {
        ActiveRuleDto ar1 = db.qualityProfiles().activateRule(profile1, rule1);
        ActiveRuleDto ar2 = db.qualityProfiles().activateRule(profile2, rule1);
        ActiveRuleDto ar3 = db.qualityProfiles().activateRule(profile2, rule2);
        ActiveRuleDaoTest.Accumulator accumulator = new ActiveRuleDaoTest.Accumulator();
        underTest.scrollByRuleProfileForIndexing(dbSession, profile2.getRulesProfileUuid(), accumulator);
        assertThat(accumulator.list).extracting(IndexedActiveRuleDto::getId, IndexedActiveRuleDto::getRepository, IndexedActiveRuleDto::getKey, IndexedActiveRuleDto::getRuleProfileUuid, IndexedActiveRuleDto::getSeverity).containsExactlyInAnyOrder(tuple(((long) (ar2.getId())), ar2.getRuleKey().repository(), ar2.getRuleKey().rule(), profile2.getRulesProfileUuid(), ar2.getSeverity()), tuple(((long) (ar3.getId())), ar3.getRuleKey().repository(), ar3.getRuleKey().rule(), profile2.getRulesProfileUuid(), ar3.getSeverity()));
    }

    private static class Accumulator implements Consumer<IndexedActiveRuleDto> {
        private final List<IndexedActiveRuleDto> list = new ArrayList<>();

        @Override
        public void accept(IndexedActiveRuleDto dto) {
            list.add(dto);
        }
    }
}

