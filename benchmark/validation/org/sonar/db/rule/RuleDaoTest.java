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
package org.sonar.db.rule;


import DebtRemediationFunction.Type.LINEAR;
import DebtRemediationFunction.Type.LINEAR_OFFSET;
import RuleDto.Format.MARKDOWN;
import RuleStatus.DEPRECATED;
import RuleType.BUG;
import RuleType.CODE_SMELL;
import RuleType.SECURITY_HOTSPOT;
import RuleType.VULNERABILITY;
import Scope.ALL;
import Severity.BLOCKER;
import Severity.INFO;
import System2.INSTANCE;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.ResultHandler;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.RuleQuery;
import org.sonar.api.utils.DateUtils;
import org.sonar.db.DbTester;
import org.sonar.db.RowNotFoundException;
import org.sonar.db.es.RuleExtensionId;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.organization.OrganizationTesting;


public class RuleDaoTest {
    private static final String ORGANIZATION_UUID = "org-1";

    private static final int UNKNOWN_RULE_ID = 1234567890;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private RuleDao underTest = db.getDbClient().ruleDao();

    private OrganizationDto organization;

    @Test
    public void selectByKey() {
        RuleDefinitionDto ruleDefinition = db.rules().insert();
        OrganizationDto organization = db.organizations().insert();
        RuleMetadataDto metadata = RuleTesting.newRuleMetadata(ruleDefinition, organization);
        db.rules().insertRule(ruleDefinition, metadata);
        assertThat(underTest.selectByKey(db.getSession(), organization, RuleKey.of("foo", "bar"))).isEmpty();
        RuleDto rule = underTest.selectByKey(db.getSession(), organization, ruleDefinition.getKey()).get();
        assertEquals(rule.getDefinition(), ruleDefinition);
        RuleDaoTest.verifyMetadata(rule.getMetadata(), ruleDefinition, metadata);
    }

    @Test
    public void selectByKey_return_rule_even_if_organization_does_not_exist() {
        RuleDefinitionDto ruleDefinition = db.rules().insert();
        assertThat(underTest.selectByKey(db.getSession(), OrganizationTesting.newOrganizationDto(), ruleDefinition.getKey())).isNotEmpty();
    }

    @Test
    public void selectByKey_populates_organizationUuid_even_when_organization_has_no_metadata() {
        OrganizationDto organization = db.organizations().insert();
        RuleDefinitionDto ruleDefinition = db.rules().insert();
        RuleDto rule = underTest.selectByKey(db.getSession(), organization, ruleDefinition.getKey()).get();
        RuleDaoTest.verifyNoMetadata(rule.getMetadata(), ruleDefinition, organization);
    }

    @Test
    public void selectByKey_returns_metadata_of_specified_organization() {
        RuleDefinitionDto ruleDefinition = db.rules().insert();
        OrganizationDto organization1 = db.organizations().insert();
        RuleMetadataDto expectedOrg1 = RuleTesting.newRuleMetadata(ruleDefinition, organization1);
        db.rules().insertRule(ruleDefinition, expectedOrg1);
        OrganizationDto organization2 = db.organizations().insert();
        RuleMetadataDto expectedOrg2 = RuleTesting.newRuleMetadata(ruleDefinition, organization2);
        db.rules().insertRule(ruleDefinition, expectedOrg2);
        RuleDto rule = underTest.selectByKey(db.getSession(), organization1, ruleDefinition.getKey()).get();
        RuleDaoTest.verifyMetadata(rule.getMetadata(), ruleDefinition, expectedOrg1);
        rule = underTest.selectByKey(db.getSession(), organization2, ruleDefinition.getKey()).get();
        RuleDaoTest.verifyMetadata(rule.getMetadata(), ruleDefinition, expectedOrg2);
    }

    @Test
    public void selectDefinitionByKey() {
        RuleDefinitionDto rule = db.rules().insert();
        assertThat(underTest.selectDefinitionByKey(db.getSession(), RuleKey.of("NOT", "FOUND")).isPresent()).isFalse();
        Optional<RuleDefinitionDto> reloaded = underTest.selectDefinitionByKey(db.getSession(), rule.getKey());
        assertThat(reloaded.isPresent()).isTrue();
    }

    @Test
    public void selectById() {
        RuleDefinitionDto ruleDefinition = db.rules().insert();
        OrganizationDto organization = db.organizations().insert();
        RuleMetadataDto metadata = RuleTesting.newRuleMetadata(ruleDefinition, organization);
        RuleDto expected = db.rules().insertRule(ruleDefinition, metadata);
        assertThat(underTest.selectById(((expected.getId()) + 500), organization.getUuid(), db.getSession())).isEmpty();
        RuleDto rule = underTest.selectById(expected.getId(), organization.getUuid(), db.getSession()).get();
        assertEquals(rule.getDefinition(), ruleDefinition);
        RuleDaoTest.verifyMetadata(rule.getMetadata(), ruleDefinition, metadata);
    }

    @Test
    public void selectById_return_rule_even_if_organization_does_not_exist() {
        RuleDefinitionDto ruleDefinition = db.rules().insert();
        assertThat(underTest.selectById(ruleDefinition.getId(), "dfdfdf", db.getSession())).isNotEmpty();
    }

    @Test
    public void selectById_populates_organizationUuid_even_when_organization_has_no_metadata() {
        OrganizationDto organization = db.organizations().insert();
        RuleDefinitionDto ruleDefinition = db.rules().insert();
        RuleDto rule = underTest.selectById(ruleDefinition.getId(), organization.getUuid(), db.getSession()).get();
        RuleDaoTest.verifyNoMetadata(rule.getMetadata(), ruleDefinition, organization);
    }

    @Test
    public void selectById_returns_metadata_of_specified_organization() {
        RuleDefinitionDto ruleDefinition = db.rules().insert();
        OrganizationDto organization1 = db.organizations().insert();
        RuleMetadataDto expectedOrg1 = RuleTesting.newRuleMetadata(ruleDefinition, organization1);
        db.rules().insertRule(ruleDefinition, expectedOrg1);
        OrganizationDto organization2 = db.organizations().insert();
        RuleMetadataDto expectedOrg2 = RuleTesting.newRuleMetadata(ruleDefinition, organization2);
        db.rules().insertRule(ruleDefinition, expectedOrg2);
        RuleDto rule = underTest.selectById(ruleDefinition.getId(), organization1.getUuid(), db.getSession()).get();
        RuleDaoTest.verifyMetadata(rule.getMetadata(), ruleDefinition, expectedOrg1);
        rule = underTest.selectById(ruleDefinition.getId(), organization2.getUuid(), db.getSession()).get();
        RuleDaoTest.verifyMetadata(rule.getMetadata(), ruleDefinition, expectedOrg2);
    }

    @Test
    public void selectDefinitionById() {
        RuleDefinitionDto rule = db.rules().insert();
        assertThat(underTest.selectDefinitionById(1234567L, db.getSession())).isEmpty();
        Optional<RuleDefinitionDto> ruleDtoOptional = underTest.selectDefinitionById(rule.getId(), db.getSession());
        assertThat(ruleDtoOptional).isPresent();
    }

    @Test
    public void selectByIds() {
        OrganizationDto organization = db.organizations().insert();
        RuleDefinitionDto rule1 = db.rules().insert();
        db.rules().insertOrUpdateMetadata(rule1, organization);
        RuleDefinitionDto rule2 = db.rules().insert();
        db.rules().insertOrUpdateMetadata(rule2, organization);
        RuleDefinitionDto removedRule = db.rules().insert(( r) -> r.setStatus(REMOVED));
        db.rules().insertOrUpdateMetadata(removedRule, organization);
        assertThat(underTest.selectByIds(db.getSession(), organization.getUuid(), Collections.singletonList(rule1.getId()))).hasSize(1);
        assertThat(underTest.selectByIds(db.getSession(), organization.getUuid(), Arrays.asList(rule1.getId(), rule2.getId()))).hasSize(2);
        assertThat(underTest.selectByIds(db.getSession(), organization.getUuid(), Arrays.asList(rule1.getId(), rule2.getId(), RuleDaoTest.UNKNOWN_RULE_ID))).hasSize(2);
        assertThat(underTest.selectByIds(db.getSession(), organization.getUuid(), Arrays.asList(rule1.getId(), rule2.getId(), removedRule.getId()))).hasSize(3);
        assertThat(underTest.selectByIds(db.getSession(), organization.getUuid(), Collections.singletonList(RuleDaoTest.UNKNOWN_RULE_ID))).isEmpty();
    }

    @Test
    public void selectByIds_populates_organizationUuid_even_when_organization_has_no_metadata() {
        OrganizationDto organization = db.organizations().insert();
        RuleDefinitionDto rule1 = db.rules().insert();
        RuleDefinitionDto rule2 = db.rules().insert();
        assertThat(underTest.selectByIds(db.getSession(), organization.getUuid(), Arrays.asList(rule1.getId(), rule2.getId()))).extracting(RuleDto::getOrganizationUuid).containsExactly(organization.getUuid(), organization.getUuid());
    }

    @Test
    public void selectDefinitionByIds() {
        RuleDefinitionDto rule1 = db.rules().insert();
        RuleDefinitionDto rule2 = db.rules().insert();
        assertThat(underTest.selectDefinitionByIds(db.getSession(), Collections.singletonList(rule1.getId()))).hasSize(1);
        assertThat(underTest.selectDefinitionByIds(db.getSession(), Arrays.asList(rule1.getId(), rule2.getId()))).hasSize(2);
        assertThat(underTest.selectDefinitionByIds(db.getSession(), Arrays.asList(rule1.getId(), rule2.getId(), RuleDaoTest.UNKNOWN_RULE_ID))).hasSize(2);
        assertThat(underTest.selectDefinitionByIds(db.getSession(), Collections.singletonList(RuleDaoTest.UNKNOWN_RULE_ID))).isEmpty();
    }

    @Test
    public void selectOrFailByKey() {
        OrganizationDto organization = db.organizations().insert();
        RuleDefinitionDto rule1 = db.rules().insert();
        RuleDefinitionDto rule2 = db.rules().insert();
        RuleDto rule = underTest.selectOrFailByKey(db.getSession(), organization, rule1.getKey());
        assertThat(rule.getId()).isEqualTo(rule1.getId());
    }

    @Test
    public void selectOrFailByKey_fails_if_rule_not_found() {
        OrganizationDto organization = db.organizations().insert();
        thrown.expect(RowNotFoundException.class);
        thrown.expectMessage("Rule with key 'NOT:FOUND' does not exist");
        underTest.selectOrFailByKey(db.getSession(), organization, RuleKey.of("NOT", "FOUND"));
    }

    @Test
    public void selectOrFailByKey_populates_organizationUuid_even_when_organization_has_no_metadata() {
        OrganizationDto organization = db.organizations().insert();
        RuleDefinitionDto rule = db.rules().insert();
        assertThat(underTest.selectOrFailByKey(db.getSession(), organization, rule.getKey()).getOrganizationUuid()).isEqualTo(organization.getUuid());
    }

    @Test
    public void selectOrFailDefinitionByKey_fails_if_rule_not_found() {
        thrown.expect(RowNotFoundException.class);
        thrown.expectMessage("Rule with key 'NOT:FOUND' does not exist");
        underTest.selectOrFailDefinitionByKey(db.getSession(), RuleKey.of("NOT", "FOUND"));
    }

    @Test
    public void selectByKeys() {
        OrganizationDto organization = db.organizations().insert();
        RuleDefinitionDto rule1 = db.rules().insert();
        db.rules().insertOrUpdateMetadata(rule1, organization);
        RuleDefinitionDto rule2 = db.rules().insert();
        db.rules().insertOrUpdateMetadata(rule2, organization);
        assertThat(underTest.selectByKeys(db.getSession(), organization.getUuid(), Collections.emptyList())).isEmpty();
        assertThat(underTest.selectByKeys(db.getSession(), organization.getUuid(), Arrays.asList(RuleKey.of("NOT", "FOUND")))).isEmpty();
        List<RuleDto> rules = underTest.selectByKeys(db.getSession(), organization.getUuid(), Arrays.asList(rule1.getKey(), RuleKey.of("java", "OTHER")));
        assertThat(rules).hasSize(1);
        assertThat(rules.get(0).getId()).isEqualTo(rule1.getId());
    }

    @Test
    public void selectByKeys_populates_organizationUuid_even_when_organization_has_no_metadata() {
        OrganizationDto organization = db.organizations().insert();
        RuleDefinitionDto rule = db.rules().insert();
        assertThat(underTest.selectByKeys(db.getSession(), organization.getUuid(), Collections.singletonList(rule.getKey()))).extracting(RuleDto::getOrganizationUuid).containsExactly(organization.getUuid());
    }

    @Test
    public void selectDefinitionByKeys() {
        RuleDefinitionDto rule = db.rules().insert();
        assertThat(underTest.selectDefinitionByKeys(db.getSession(), Collections.emptyList())).isEmpty();
        assertThat(underTest.selectDefinitionByKeys(db.getSession(), Arrays.asList(RuleKey.of("NOT", "FOUND")))).isEmpty();
        List<RuleDefinitionDto> rules = underTest.selectDefinitionByKeys(db.getSession(), Arrays.asList(rule.getKey(), RuleKey.of("java", "OTHER")));
        assertThat(rules).hasSize(1);
        assertThat(rules.get(0).getId()).isEqualTo(rule.getId());
    }

    @Test
    public void selectAll() {
        OrganizationDto organization = db.organizations().insert();
        RuleDto rule1 = db.rules().insertRule(organization);
        RuleDto rule2 = db.rules().insertRule(organization);
        RuleDto rule3 = db.rules().insertRule(organization);
        assertThat(underTest.selectAll(db.getSession(), organization.getUuid())).extracting(RuleDto::getId).containsOnly(rule1.getId(), rule2.getId(), rule3.getId());
    }

    @Test
    public void selectAll_returns_all_rules_even_if_organization_does_not_exist() {
        RuleDefinitionDto rule1 = db.rules().insert();
        RuleDefinitionDto rule2 = db.rules().insert();
        RuleDefinitionDto rule3 = db.rules().insert();
        assertThat(underTest.selectAll(db.getSession(), "dfdfdf")).extracting(RuleDto::getId).containsOnly(rule1.getId(), rule2.getId(), rule3.getId());
    }

    @Test
    public void selectAll_populates_organizationUuid_even_when_organization_has_no_metadata() {
        OrganizationDto organization = db.organizations().insert();
        RuleDefinitionDto ruleDefinition1 = db.rules().insert();
        RuleDefinitionDto ruleDefinition2 = db.rules().insert();
        List<RuleDto> rules = underTest.selectAll(db.getSession(), organization.getUuid());
        assertThat(rules).extracting(RuleDto::getId).containsOnly(ruleDefinition1.getId(), ruleDefinition2.getId());
        assertThat(rules).extracting(RuleDto::getOrganizationUuid).containsExactly(organization.getUuid(), organization.getUuid());
    }

    @Test
    public void selectAll_returns_metadata_of_specified_organization() {
        RuleDefinitionDto ruleDefinition = db.rules().insert();
        OrganizationDto organization = db.organizations().insert();
        RuleMetadataDto expected = RuleTesting.newRuleMetadata(ruleDefinition, organization);
        db.rules().insertRule(ruleDefinition, expected);
        List<RuleDto> rules = underTest.selectAll(db.getSession(), organization.getUuid());
        assertThat(rules).hasSize(1);
        RuleDaoTest.verifyMetadata(rules.iterator().next().getMetadata(), ruleDefinition, expected);
    }

    @Test
    public void selectAllDefinitions() {
        RuleDefinitionDto rule1 = db.rules().insert();
        RuleDefinitionDto rule2 = db.rules().insert();
        RuleDefinitionDto removedRule = db.rules().insert(( r) -> r.setStatus(REMOVED));
        List<RuleDefinitionDto> ruleDtos = underTest.selectAllDefinitions(db.getSession());
        assertThat(ruleDtos).extracting(RuleDefinitionDto::getId).containsOnly(rule1.getId(), rule2.getId(), removedRule.getId());
    }

    @Test
    public void selectEnabled_with_ResultHandler() {
        RuleDefinitionDto rule = db.rules().insert();
        RuleDefinitionDto removedRule = db.rules().insert(( r) -> r.setStatus(REMOVED));
        final List<RuleDefinitionDto> rules = new ArrayList<>();
        ResultHandler<RuleDefinitionDto> resultHandler = ( resultContext) -> rules.add(resultContext.getResultObject());
        underTest.selectEnabled(db.getSession(), resultHandler);
        assertThat(rules.size()).isEqualTo(1);
        RuleDefinitionDto ruleDto = rules.get(0);
        assertThat(ruleDto.getId()).isEqualTo(rule.getId());
    }

    @Test
    public void selectByTypeAndLanguages() {
        OrganizationDto organization = db.organizations().insert();
        OrganizationDto organization2 = db.organizations().insert();
        RuleDefinitionDto rule1 = db.rules().insert(( r) -> r.setKey(RuleKey.of("java", "S001")).setConfigKey("S1").setType(VULNERABILITY).setLanguage("java"));
        db.rules().insertOrUpdateMetadata(rule1, organization);
        RuleDefinitionDto rule2 = db.rules().insert(( r) -> r.setKey(RuleKey.of("js", "S002")).setType(SECURITY_HOTSPOT).setLanguage("js"));
        db.rules().insertOrUpdateMetadata(rule2, organization);
        assertThat(underTest.selectByTypeAndLanguages(db.getSession(), organization.getUuid(), Collections.singletonList(VULNERABILITY.getDbConstant()), Collections.singletonList("java"))).extracting(RuleDto::getOrganizationUuid, RuleDto::getId, RuleDto::getLanguage, RuleDto::getType).containsExactly(tuple(organization.getUuid(), rule1.getId(), "java", VULNERABILITY.getDbConstant()));
        // Rule available also on organization2
        assertThat(underTest.selectByTypeAndLanguages(db.getSession(), organization2.getUuid(), Collections.singletonList(VULNERABILITY.getDbConstant()), Collections.singletonList("java"))).extracting(RuleDto::getOrganizationUuid, RuleDto::getId, RuleDto::getLanguage, RuleDto::getType).containsExactly(tuple(organization2.getUuid(), rule1.getId(), "java", VULNERABILITY.getDbConstant()));
        assertThat(underTest.selectByTypeAndLanguages(db.getSession(), organization.getUuid(), Collections.singletonList(SECURITY_HOTSPOT.getDbConstant()), Collections.singletonList("js"))).extracting(RuleDto::getOrganizationUuid, RuleDto::getId, RuleDto::getLanguage, RuleDto::getType).containsExactly(tuple(organization.getUuid(), rule2.getId(), "js", SECURITY_HOTSPOT.getDbConstant()));
        // Rule available also on organization2
        assertThat(underTest.selectByTypeAndLanguages(db.getSession(), organization2.getUuid(), Collections.singletonList(SECURITY_HOTSPOT.getDbConstant()), Collections.singletonList("js"))).extracting(RuleDto::getOrganizationUuid, RuleDto::getId, RuleDto::getLanguage, RuleDto::getType).containsExactly(tuple(organization2.getUuid(), rule2.getId(), "js", SECURITY_HOTSPOT.getDbConstant()));
        assertThat(underTest.selectByTypeAndLanguages(db.getSession(), organization.getUuid(), Collections.singletonList(SECURITY_HOTSPOT.getDbConstant()), Collections.singletonList("java"))).isEmpty();
        assertThat(underTest.selectByTypeAndLanguages(db.getSession(), organization.getUuid(), Collections.singletonList(VULNERABILITY.getDbConstant()), Collections.singletonList("js"))).isEmpty();
    }

    @Test
    public void selectByTypeAndLanguages_return_nothing_when_no_rule_on_languages() {
        OrganizationDto organization = db.organizations().insert();
        RuleDefinitionDto rule1 = db.rules().insert(( r) -> r.setKey(RuleKey.of("java", "S001")).setConfigKey("S1").setType(VULNERABILITY).setLanguage("java"));
        db.rules().insertOrUpdateMetadata(rule1, organization);
        RuleDefinitionDto rule2 = db.rules().insert(( r) -> r.setKey(RuleKey.of("js", "S002")).setType(VULNERABILITY).setLanguage("js"));
        db.rules().insertOrUpdateMetadata(rule2, organization);
        assertThat(underTest.selectByTypeAndLanguages(db.getSession(), organization.getUuid(), Collections.singletonList(VULNERABILITY.getDbConstant()), Collections.singletonList("cpp"))).isEmpty();
    }

    @Test
    public void selectByTypeAndLanguages_return_nothing_when_no_rule_with_type() {
        OrganizationDto organization = db.organizations().insert();
        RuleDefinitionDto rule1 = db.rules().insert(( r) -> r.setKey(RuleKey.of("java", "S001")).setConfigKey("S1").setType(VULNERABILITY).setLanguage("java"));
        db.rules().insertOrUpdateMetadata(rule1, organization);
        RuleDefinitionDto rule2 = db.rules().insert(( r) -> r.setKey(RuleKey.of("java", "S002")).setType(SECURITY_HOTSPOT).setLanguage("java"));
        db.rules().insertOrUpdateMetadata(rule2, organization);
        RuleDefinitionDto rule3 = db.rules().insert(( r) -> r.setKey(RuleKey.of("java", "S003")).setType(CODE_SMELL).setLanguage("java"));
        db.rules().insertOrUpdateMetadata(rule3, organization);
        assertThat(underTest.selectByTypeAndLanguages(db.getSession(), organization.getUuid(), Collections.singletonList(BUG.getDbConstant()), Collections.singletonList("java"))).isEmpty();
    }

    @Test
    public void selectByTypeAndLanguages_ignores_external_rules() {
        OrganizationDto organization = db.organizations().insert();
        RuleDefinitionDto rule1 = db.rules().insert(( r) -> r.setKey(RuleKey.of("java", "S001")).setConfigKey("S1").setType(VULNERABILITY).setIsExternal(true).setLanguage("java"));
        db.rules().insertOrUpdateMetadata(rule1, organization);
        assertThat(underTest.selectByTypeAndLanguages(db.getSession(), organization.getUuid(), Collections.singletonList(VULNERABILITY.getDbConstant()), Collections.singletonList("java"))).extracting(RuleDto::getOrganizationUuid, RuleDto::getId, RuleDto::getLanguage, RuleDto::getType).isEmpty();
    }

    @Test
    public void selectByTypeAndLanguages_ignores_template_rules() {
        OrganizationDto organization = db.organizations().insert();
        RuleDefinitionDto rule1 = db.rules().insert(( r) -> r.setKey(RuleKey.of("java", "S001")).setConfigKey("S1").setType(VULNERABILITY).setIsTemplate(true).setLanguage("java"));
        db.rules().insertOrUpdateMetadata(rule1, organization);
        assertThat(underTest.selectByTypeAndLanguages(db.getSession(), organization.getUuid(), Collections.singletonList(VULNERABILITY.getDbConstant()), Collections.singletonList("java"))).extracting(RuleDto::getOrganizationUuid, RuleDto::getId, RuleDto::getLanguage, RuleDto::getType).isEmpty();
    }

    @Test
    public void select_by_query() {
        OrganizationDto organization = db.organizations().insert();
        RuleDefinitionDto rule1 = db.rules().insert(( r) -> r.setKey(RuleKey.of("java", "S001")).setConfigKey("S1"));
        db.rules().insertOrUpdateMetadata(rule1, organization);
        RuleDefinitionDto rule2 = db.rules().insert(( r) -> r.setKey(RuleKey.of("java", "S002")));
        db.rules().insertOrUpdateMetadata(rule2, organization);
        RuleDefinitionDto removedRule = db.rules().insert(( r) -> r.setStatus(REMOVED));
        assertThat(underTest.selectByQuery(db.getSession(), organization.getUuid(), RuleQuery.create())).hasSize(2);
        assertThat(underTest.selectByQuery(db.getSession(), organization.getUuid(), RuleQuery.create().withKey("S001"))).hasSize(1);
        assertThat(underTest.selectByQuery(db.getSession(), organization.getUuid(), RuleQuery.create().withConfigKey("S1"))).hasSize(1);
        assertThat(underTest.selectByQuery(db.getSession(), organization.getUuid(), RuleQuery.create().withRepositoryKey("java"))).hasSize(2);
        assertThat(underTest.selectByQuery(db.getSession(), organization.getUuid(), RuleQuery.create().withKey("S001").withConfigKey("S1").withRepositoryKey("java"))).hasSize(1);
    }

    @Test
    public void select_by_query_populates_organizationUuid_even_when_organization_has_no_metadata() {
        OrganizationDto organization = db.organizations().insert();
        RuleDefinitionDto rule1 = db.rules().insert();
        RuleDefinitionDto rule2 = db.rules().insert();
        assertThat(underTest.selectByQuery(db.getSession(), organization.getUuid(), RuleQuery.create())).extracting(RuleDto::getOrganizationUuid).containsExactly(organization.getUuid(), organization.getUuid());
    }

    @Test
    public void insert() {
        RuleDefinitionDto newRule = new RuleDefinitionDto().setRuleKey("NewRuleKey").setRepositoryKey("plugin").setName("new name").setDescription("new description").setDescriptionFormat(MARKDOWN).setStatus(DEPRECATED).setConfigKey("NewConfigKey").setSeverity(INFO).setIsTemplate(true).setIsExternal(true).setIsAdHoc(true).setLanguage("dart").setTemplateId(3).setDefRemediationFunction(LINEAR_OFFSET.toString()).setDefRemediationGapMultiplier("5d").setDefRemediationBaseEffort("10h").setGapDescription("squid.S115.effortToFix").setSystemTags(Sets.newHashSet("systag1", "systag2")).setSecurityStandards(Sets.newHashSet("owaspTop10:a1", "cwe:123")).setType(BUG).setScope(ALL).setCreatedAt(1500000000000L).setUpdatedAt(2000000000000L);
        underTest.insert(db.getSession(), newRule);
        db.getSession().commit();
        RuleDefinitionDto ruleDto = underTest.selectOrFailDefinitionByKey(db.getSession(), RuleKey.of("plugin", "NewRuleKey"));
        assertThat(ruleDto.getId()).isNotNull();
        assertThat(ruleDto.getName()).isEqualTo("new name");
        assertThat(ruleDto.getDescription()).isEqualTo("new description");
        assertThat(ruleDto.getDescriptionFormat()).isEqualTo(MARKDOWN);
        assertThat(ruleDto.getStatus()).isEqualTo(DEPRECATED);
        assertThat(ruleDto.getRuleKey()).isEqualTo("NewRuleKey");
        assertThat(ruleDto.getRepositoryKey()).isEqualTo("plugin");
        assertThat(ruleDto.getConfigKey()).isEqualTo("NewConfigKey");
        assertThat(ruleDto.getSeverity()).isEqualTo(0);
        assertThat(ruleDto.getLanguage()).isEqualTo("dart");
        assertThat(ruleDto.isTemplate()).isTrue();
        assertThat(ruleDto.isExternal()).isTrue();
        assertThat(ruleDto.isAdHoc()).isTrue();
        assertThat(ruleDto.getTemplateId()).isEqualTo(3);
        assertThat(ruleDto.getDefRemediationFunction()).isEqualTo("LINEAR_OFFSET");
        assertThat(ruleDto.getDefRemediationGapMultiplier()).isEqualTo("5d");
        assertThat(ruleDto.getDefRemediationBaseEffort()).isEqualTo("10h");
        assertThat(ruleDto.getGapDescription()).isEqualTo("squid.S115.effortToFix");
        assertThat(ruleDto.getSystemTags()).containsOnly("systag1", "systag2");
        assertThat(ruleDto.getSecurityStandards()).containsOnly("owaspTop10:a1", "cwe:123");
        assertThat(ruleDto.getScope()).isEqualTo(ALL);
        assertThat(ruleDto.getType()).isEqualTo(BUG.getDbConstant());
        assertThat(ruleDto.getCreatedAt()).isEqualTo(1500000000000L);
        assertThat(ruleDto.getUpdatedAt()).isEqualTo(2000000000000L);
    }

    @Test
    public void update_RuleDefinitionDto() {
        RuleDefinitionDto rule = db.rules().insert();
        RuleDefinitionDto ruleToUpdate = new RuleDefinitionDto().setId(rule.getId()).setRuleKey("NewRuleKey").setRepositoryKey("plugin").setName("new name").setDescription("new description").setDescriptionFormat(MARKDOWN).setStatus(DEPRECATED).setConfigKey("NewConfigKey").setSeverity(INFO).setIsTemplate(true).setIsExternal(true).setIsAdHoc(true).setLanguage("dart").setTemplateId(3).setDefRemediationFunction(LINEAR_OFFSET.toString()).setDefRemediationGapMultiplier("5d").setDefRemediationBaseEffort("10h").setGapDescription("squid.S115.effortToFix").setSystemTags(Sets.newHashSet("systag1", "systag2")).setSecurityStandards(Sets.newHashSet("owaspTop10:a1", "cwe:123")).setScope(ALL).setType(BUG).setUpdatedAt(2000000000000L);
        underTest.update(db.getSession(), ruleToUpdate);
        db.getSession().commit();
        RuleDefinitionDto ruleDto = underTest.selectOrFailDefinitionByKey(db.getSession(), RuleKey.of("plugin", "NewRuleKey"));
        assertThat(ruleDto.getName()).isEqualTo("new name");
        assertThat(ruleDto.getDescription()).isEqualTo("new description");
        assertThat(ruleDto.getDescriptionFormat()).isEqualTo(MARKDOWN);
        assertThat(ruleDto.getStatus()).isEqualTo(DEPRECATED);
        assertThat(ruleDto.getRuleKey()).isEqualTo("NewRuleKey");
        assertThat(ruleDto.getRepositoryKey()).isEqualTo("plugin");
        assertThat(ruleDto.getConfigKey()).isEqualTo("NewConfigKey");
        assertThat(ruleDto.getSeverity()).isEqualTo(0);
        assertThat(ruleDto.getLanguage()).isEqualTo("dart");
        assertThat(ruleDto.isTemplate()).isTrue();
        assertThat(ruleDto.isExternal()).isTrue();
        assertThat(ruleDto.isAdHoc()).isTrue();
        assertThat(ruleDto.getTemplateId()).isEqualTo(3);
        assertThat(ruleDto.getDefRemediationFunction()).isEqualTo("LINEAR_OFFSET");
        assertThat(ruleDto.getDefRemediationGapMultiplier()).isEqualTo("5d");
        assertThat(ruleDto.getDefRemediationBaseEffort()).isEqualTo("10h");
        assertThat(ruleDto.getGapDescription()).isEqualTo("squid.S115.effortToFix");
        assertThat(ruleDto.getSystemTags()).containsOnly("systag1", "systag2");
        assertThat(ruleDto.getSecurityStandards()).containsOnly("owaspTop10:a1", "cwe:123");
        assertThat(ruleDto.getScope()).isEqualTo(ALL);
        assertThat(ruleDto.getType()).isEqualTo(BUG.getDbConstant());
        assertThat(ruleDto.getCreatedAt()).isEqualTo(rule.getCreatedAt());
        assertThat(ruleDto.getUpdatedAt()).isEqualTo(2000000000000L);
    }

    @Test
    public void update_RuleMetadataDto_inserts_row_in_RULE_METADATA_if_not_exists_yet() {
        RuleDefinitionDto rule = db.rules().insert();
        String organizationUuid = "org-1";
        RuleMetadataDto metadataToUpdate = new RuleMetadataDto().setRuleId(rule.getId()).setOrganizationUuid(organizationUuid).setNoteData("My note").setNoteUserUuid("admin").setNoteCreatedAt(DateUtils.parseDate("2013-12-19").getTime()).setNoteUpdatedAt(DateUtils.parseDate("2013-12-20").getTime()).setRemediationFunction(LINEAR.toString()).setRemediationGapMultiplier("1h").setRemediationBaseEffort("5min").setTags(Sets.newHashSet("tag1", "tag2")).setAdHocName("ad hoc name").setAdHocDescription("ad hoc desc").setAdHocSeverity(BLOCKER).setAdHocType(CODE_SMELL).setCreatedAt(3500000000000L).setUpdatedAt(4000000000000L);
        underTest.insertOrUpdate(db.getSession(), metadataToUpdate);
        db.getSession().commit();
        OrganizationDto organization = OrganizationTesting.newOrganizationDto().setUuid(organizationUuid);
        RuleDto ruleDto = underTest.selectOrFailByKey(db.getSession(), organization, rule.getKey());
        assertThat(ruleDto.getNoteData()).isEqualTo("My note");
        assertThat(ruleDto.getNoteUserUuid()).isEqualTo("admin");
        assertThat(ruleDto.getNoteCreatedAt()).isNotNull();
        assertThat(ruleDto.getNoteUpdatedAt()).isNotNull();
        assertThat(ruleDto.getRemediationFunction()).isEqualTo("LINEAR");
        assertThat(ruleDto.getRemediationGapMultiplier()).isEqualTo("1h");
        assertThat(ruleDto.getRemediationBaseEffort()).isEqualTo("5min");
        assertThat(ruleDto.getTags()).containsOnly("tag1", "tag2");
        assertThat(ruleDto.getAdHocName()).isEqualTo("ad hoc name");
        assertThat(ruleDto.getAdHocDescription()).isEqualTo("ad hoc desc");
        assertThat(ruleDto.getAdHocSeverity()).isEqualTo(BLOCKER);
        assertThat(ruleDto.getAdHocType()).isEqualTo(CODE_SMELL.getDbConstant());
        assertThat(ruleDto.getSecurityStandards()).isEmpty();
        assertThat(ruleDto.getCreatedAt()).isEqualTo(3500000000000L);
        assertThat(ruleDto.getUpdatedAt()).isEqualTo(4000000000000L);
        // Info from rule definition
        assertThat(ruleDto.getDefRemediationFunction()).isEqualTo(rule.getDefRemediationFunction());
        assertThat(ruleDto.getDefRemediationGapMultiplier()).isEqualTo(rule.getDefRemediationGapMultiplier());
        assertThat(ruleDto.getDefRemediationBaseEffort()).isEqualTo(rule.getDefRemediationBaseEffort());
        assertThat(ruleDto.getGapDescription()).isEqualTo(rule.getGapDescription());
        assertThat(ruleDto.getSystemTags()).containsAll(rule.getSystemTags());
        assertThat(ruleDto.getType()).isEqualTo(rule.getType());
    }

    @Test
    public void update_RuleMetadataDto_updates_row_in_RULE_METADATA_if_already_exists() {
        RuleDefinitionDto rule = db.rules().insert();
        String organizationUuid = "org-1";
        OrganizationDto organization = OrganizationTesting.newOrganizationDto().setUuid(organizationUuid);
        RuleMetadataDto metadataV1 = new RuleMetadataDto().setRuleId(rule.getId()).setOrganizationUuid(organizationUuid).setCreatedAt(3500000000000L).setUpdatedAt(4000000000000L);
        RuleMetadataDto metadataV2 = new RuleMetadataDto().setRuleId(rule.getId()).setOrganizationUuid(organizationUuid).setNoteData("My note").setNoteUserUuid("admin").setNoteCreatedAt(DateUtils.parseDate("2013-12-19").getTime()).setNoteUpdatedAt(DateUtils.parseDate("2013-12-20").getTime()).setRemediationFunction(LINEAR.toString()).setRemediationGapMultiplier("1h").setRemediationBaseEffort("5min").setTags(Sets.newHashSet("tag1", "tag2")).setAdHocName("ad hoc name").setAdHocDescription("ad hoc desc").setAdHocSeverity(BLOCKER).setAdHocType(CODE_SMELL).setCreatedAt(6500000000000L).setUpdatedAt(7000000000000L);
        underTest.insertOrUpdate(db.getSession(), metadataV1);
        db.commit();
        assertThat(db.countRowsOfTable("RULES_METADATA")).isEqualTo(1);
        RuleDto ruleDto = underTest.selectOrFailByKey(db.getSession(), organization, rule.getKey());
        assertThat(ruleDto.getNoteData()).isNull();
        assertThat(ruleDto.getNoteUserUuid()).isNull();
        assertThat(ruleDto.getNoteCreatedAt()).isNull();
        assertThat(ruleDto.getNoteUpdatedAt()).isNull();
        assertThat(ruleDto.getRemediationFunction()).isNull();
        assertThat(ruleDto.getRemediationGapMultiplier()).isNull();
        assertThat(ruleDto.getRemediationBaseEffort()).isNull();
        assertThat(ruleDto.getTags()).isEmpty();
        assertThat(ruleDto.getAdHocName()).isNull();
        assertThat(ruleDto.getAdHocDescription()).isNull();
        assertThat(ruleDto.getAdHocSeverity()).isNull();
        assertThat(ruleDto.getAdHocType()).isNull();
        assertThat(ruleDto.getSecurityStandards()).isEmpty();
        assertThat(ruleDto.getCreatedAt()).isEqualTo(3500000000000L);
        assertThat(ruleDto.getUpdatedAt()).isEqualTo(4000000000000L);
        underTest.insertOrUpdate(db.getSession(), metadataV2);
        db.commit();
        ruleDto = underTest.selectOrFailByKey(db.getSession(), organization, rule.getKey());
        assertThat(ruleDto.getNoteData()).isEqualTo("My note");
        assertThat(ruleDto.getNoteUserUuid()).isEqualTo("admin");
        assertThat(ruleDto.getNoteCreatedAt()).isNotNull();
        assertThat(ruleDto.getNoteUpdatedAt()).isNotNull();
        assertThat(ruleDto.getRemediationFunction()).isEqualTo("LINEAR");
        assertThat(ruleDto.getRemediationGapMultiplier()).isEqualTo("1h");
        assertThat(ruleDto.getRemediationBaseEffort()).isEqualTo("5min");
        assertThat(ruleDto.getTags()).containsOnly("tag1", "tag2");
        assertThat(ruleDto.getAdHocName()).isEqualTo("ad hoc name");
        assertThat(ruleDto.getAdHocDescription()).isEqualTo("ad hoc desc");
        assertThat(ruleDto.getAdHocSeverity()).isEqualTo(BLOCKER);
        assertThat(ruleDto.getAdHocType()).isEqualTo(CODE_SMELL.getDbConstant());
        assertThat(ruleDto.getSecurityStandards()).isEmpty();
        assertThat(ruleDto.getCreatedAt()).isEqualTo(3500000000000L);
        assertThat(ruleDto.getUpdatedAt()).isEqualTo(7000000000000L);
    }

    @Test
    public void select_parameters_by_rule_key() {
        RuleDefinitionDto rule = db.rules().insert();
        RuleParamDto ruleParam = db.rules().insertRuleParam(rule);
        List<RuleParamDto> ruleDtos = underTest.selectRuleParamsByRuleKey(db.getSession(), rule.getKey());
        assertThat(ruleDtos.size()).isEqualTo(1);
        RuleParamDto ruleDto = ruleDtos.get(0);
        assertThat(ruleDto.getId()).isEqualTo(ruleParam.getId());
        assertThat(ruleDto.getName()).isEqualTo(ruleParam.getName());
        assertThat(ruleDto.getDescription()).isEqualTo(ruleParam.getDescription());
        assertThat(ruleDto.getType()).isEqualTo(ruleParam.getType());
        assertThat(ruleDto.getRuleId()).isEqualTo(rule.getId());
    }

    @Test
    public void select_parameters_by_rule_keys() {
        RuleDefinitionDto rule1 = db.rules().insert();
        db.rules().insertRuleParam(rule1);
        RuleDefinitionDto rule2 = db.rules().insert();
        db.rules().insertRuleParam(rule2);
        assertThat(underTest.selectRuleParamsByRuleKeys(db.getSession(), Arrays.asList(rule1.getKey(), rule2.getKey()))).hasSize(2);
        assertThat(underTest.selectRuleParamsByRuleKeys(db.getSession(), Collections.singletonList(RuleKey.of("unknown", "Unknown")))).isEmpty();
    }

    @Test
    public void insert_parameter() {
        RuleDefinitionDto ruleDefinitionDto = db.rules().insert();
        RuleParamDto orig = RuleParamDto.createFor(ruleDefinitionDto).setName("max").setType("INTEGER").setDefaultValue("30").setDescription("My Parameter");
        underTest.insertRuleParam(db.getSession(), ruleDefinitionDto, orig);
        List<RuleParamDto> ruleParamDtos = underTest.selectRuleParamsByRuleKey(db.getSession(), ruleDefinitionDto.getKey());
        assertThat(ruleParamDtos).hasSize(1);
        RuleParamDto loaded = ruleParamDtos.get(0);
        assertThat(loaded.getRuleId()).isEqualTo(orig.getRuleId());
        assertThat(loaded.getName()).isEqualTo(orig.getName());
        assertThat(loaded.getType()).isEqualTo(orig.getType());
        assertThat(loaded.getDefaultValue()).isEqualTo(orig.getDefaultValue());
        assertThat(loaded.getDescription()).isEqualTo(orig.getDescription());
    }

    @Test
    public void should_fail_to_insert_duplicate_parameter() {
        RuleDefinitionDto ruleDefinitionDto = db.rules().insert();
        RuleParamDto param = RuleParamDto.createFor(ruleDefinitionDto).setName("max").setType("INTEGER").setDefaultValue("30").setDescription("My Parameter");
        underTest.insertRuleParam(db.getSession(), ruleDefinitionDto, param);
        thrown.expect(PersistenceException.class);
        underTest.insertRuleParam(db.getSession(), ruleDefinitionDto, param);
    }

    @Test
    public void update_parameter() {
        RuleDefinitionDto rule = db.rules().insert();
        RuleParamDto ruleParam = db.rules().insertRuleParam(rule);
        List<RuleParamDto> params = underTest.selectRuleParamsByRuleKey(db.getSession(), rule.getKey());
        assertThat(params).hasSize(1);
        RuleParamDto param = // Name will not be updated
        new RuleParamDto().setId(ruleParam.getId()).setRuleId(rule.getId()).setName("format").setType("STRING").setDefaultValue("^[a-z]+(\\.[a-z][a-z0-9]*)*$").setDescription("Regular expression used to check the package names against.");
        underTest.updateRuleParam(db.getSession(), rule, param);
        assertThat(underTest.selectRuleParamsByRuleKey(db.getSession(), rule.getKey())).extracting(RuleParamDto::getName, RuleParamDto::getType, RuleParamDto::getDefaultValue, RuleParamDto::getDescription).containsExactlyInAnyOrder(tuple(ruleParam.getName(), "STRING", "^[a-z]+(\\.[a-z][a-z0-9]*)*$", "Regular expression used to check the package names against."));
    }

    @Test
    public void delete_parameter() {
        RuleDefinitionDto rule = db.rules().insert();
        RuleParamDto ruleParam = db.rules().insertRuleParam(rule);
        assertThat(underTest.selectRuleParamsByRuleKey(db.getSession(), rule.getKey())).hasSize(1);
        underTest.deleteRuleParam(db.getSession(), ruleParam.getId());
        assertThat(underTest.selectRuleParamsByRuleKey(db.getSession(), rule.getKey())).isEmpty();
    }

    @Test
    public void scrollIndexingRules_on_empty_table() {
        RuleDaoTest.Accumulator<RuleForIndexingDto> accumulator = new RuleDaoTest.Accumulator<>();
        underTest.scrollIndexingRules(db.getSession(), accumulator);
        assertThat(accumulator.list).isEmpty();
    }

    @Test
    public void scrollIndexingRules() {
        RuleDaoTest.Accumulator<RuleForIndexingDto> accumulator = new RuleDaoTest.Accumulator<>();
        RuleDefinitionDto r1 = db.rules().insert();
        RuleDefinitionDto r2 = db.rules().insert(( r) -> r.setIsExternal(true));
        underTest.scrollIndexingRules(db.getSession(), accumulator);
        assertThat(accumulator.list).extracting(RuleForIndexingDto::getId, RuleForIndexingDto::getRuleKey).containsExactlyInAnyOrder(tuple(r1.getId(), r1.getKey()), tuple(r2.getId(), r2.getKey()));
        Iterator<RuleForIndexingDto> it = accumulator.list.iterator();
        RuleForIndexingDto firstRule = it.next();
        assertThat(firstRule.getRepository()).isEqualTo(r1.getRepositoryKey());
        assertThat(firstRule.getPluginRuleKey()).isEqualTo(r1.getRuleKey());
        assertThat(firstRule.getName()).isEqualTo(r1.getName());
        assertThat(firstRule.getDescription()).isEqualTo(r1.getDescription());
        assertThat(firstRule.getDescriptionFormat()).isEqualTo(r1.getDescriptionFormat());
        assertThat(firstRule.getSeverity()).isEqualTo(r1.getSeverity());
        assertThat(firstRule.getStatus()).isEqualTo(r1.getStatus());
        assertThat(firstRule.isExternal()).isFalse();
        assertThat(firstRule.isTemplate()).isEqualTo(r1.isTemplate());
        assertThat(firstRule.getSystemTagsAsSet()).isEqualTo(r1.getSystemTags());
        assertThat(firstRule.getSecurityStandardsAsSet()).isEqualTo(r1.getSecurityStandards());
        assertThat(firstRule.getTemplateRuleKey()).isNull();
        assertThat(firstRule.getTemplateRepository()).isNull();
        assertThat(firstRule.getInternalKey()).isEqualTo(r1.getConfigKey());
        assertThat(firstRule.getLanguage()).isEqualTo(r1.getLanguage());
        assertThat(firstRule.getType()).isEqualTo(r1.getType());
        assertThat(firstRule.getCreatedAt()).isEqualTo(r1.getCreatedAt());
        assertThat(firstRule.getUpdatedAt()).isEqualTo(r1.getUpdatedAt());
        RuleForIndexingDto secondRule = it.next();
        assertThat(secondRule.isExternal()).isTrue();
    }

    @Test
    public void scrollIndexingRules_maps_rule_definition_fields_for_regular_rule_and_template_rule() {
        RuleDaoTest.Accumulator<RuleForIndexingDto> accumulator = new RuleDaoTest.Accumulator<>();
        RuleDefinitionDto r1 = db.rules().insert();
        RuleDefinitionDto r2 = db.rules().insert(( rule) -> rule.setTemplateId(r1.getId()));
        underTest.scrollIndexingRules(db.getSession(), accumulator);
        assertThat(accumulator.list).hasSize(2);
        RuleForIndexingDto firstRule = accumulator.list.get(0);
        RuleForIndexingDto secondRule = accumulator.list.get(1);
        assertRuleDefinitionFieldsAreEquals(r1, firstRule);
        assertThat(firstRule.getTemplateRuleKey()).isNull();
        assertThat(firstRule.getTemplateRepository()).isNull();
        assertRuleDefinitionFieldsAreEquals(r2, secondRule);
        assertThat(secondRule.getTemplateRuleKey()).isEqualTo(r1.getRuleKey());
        assertThat(secondRule.getTemplateRepository()).isEqualTo(r1.getRepositoryKey());
    }

    @Test
    public void scrollIndexingRulesByKeys() {
        RuleDaoTest.Accumulator<RuleForIndexingDto> accumulator = new RuleDaoTest.Accumulator<>();
        RuleDefinitionDto r1 = db.rules().insert();
        db.rules().insert();
        underTest.scrollIndexingRulesByKeys(db.getSession(), Collections.singletonList(r1.getId()), accumulator);
        assertThat(accumulator.list).extracting(RuleForIndexingDto::getId, RuleForIndexingDto::getRuleKey).containsExactlyInAnyOrder(tuple(r1.getId(), r1.getKey()));
    }

    @Test
    public void scrollIndexingRulesByKeys_maps_rule_definition_fields_for_regular_rule_and_template_rule() {
        RuleDaoTest.Accumulator<RuleForIndexingDto> accumulator = new RuleDaoTest.Accumulator<>();
        RuleDefinitionDto r1 = db.rules().insert();
        RuleDefinitionDto r2 = db.rules().insert(( rule) -> rule.setTemplateId(r1.getId()));
        underTest.scrollIndexingRulesByKeys(db.getSession(), Arrays.asList(r1.getId(), r2.getId()), accumulator);
        assertThat(accumulator.list).hasSize(2);
        RuleForIndexingDto firstRule = accumulator.list.stream().filter(( t) -> t.getId().equals(r1.getId())).findFirst().get();
        RuleForIndexingDto secondRule = accumulator.list.stream().filter(( t) -> t.getId().equals(r2.getId())).findFirst().get();
        assertRuleDefinitionFieldsAreEquals(r1, firstRule);
        assertThat(firstRule.getTemplateRuleKey()).isNull();
        assertThat(firstRule.getTemplateRepository()).isNull();
        assertRuleDefinitionFieldsAreEquals(r2, secondRule);
        assertThat(secondRule.getTemplateRuleKey()).isEqualTo(r1.getRuleKey());
        assertThat(secondRule.getTemplateRepository()).isEqualTo(r1.getRepositoryKey());
    }

    @Test
    public void scrollIndexingRulesByKeys_scrolls_nothing_if_key_does_not_exist() {
        RuleDaoTest.Accumulator<RuleForIndexingDto> accumulator = new RuleDaoTest.Accumulator<>();
        db.rules().insert();
        int nonExistingRuleId = 42;
        underTest.scrollIndexingRulesByKeys(db.getSession(), Collections.singletonList(nonExistingRuleId), accumulator);
        assertThat(accumulator.list).isEmpty();
    }

    @Test
    public void scrollIndexingRuleExtensions() {
        RuleDaoTest.Accumulator<RuleExtensionForIndexingDto> accumulator = new RuleDaoTest.Accumulator<>();
        RuleDefinitionDto r1 = db.rules().insert();
        RuleMetadataDto r1Extension = db.rules().insertOrUpdateMetadata(r1, organization, ( r) -> r.setTagsField("t1,t2"));
        RuleDefinitionDto r2 = db.rules().insert();
        RuleMetadataDto r2Extension = db.rules().insertOrUpdateMetadata(r2, organization, ( r) -> r.setTagsField("t1,t3"));
        underTest.scrollIndexingRuleExtensions(db.getSession(), accumulator);
        assertThat(accumulator.list).extracting(RuleExtensionForIndexingDto::getRuleId, RuleExtensionForIndexingDto::getRuleKey, RuleExtensionForIndexingDto::getOrganizationUuid, RuleExtensionForIndexingDto::getTags).containsExactlyInAnyOrder(tuple(r1.getId(), r1.getKey(), organization.getUuid(), r1Extension.getTagsAsString()), tuple(r2.getId(), r2.getKey(), organization.getUuid(), r2Extension.getTagsAsString()));
    }

    @Test
    public void scrollIndexingRuleExtensionsByIds() {
        RuleDaoTest.Accumulator<RuleExtensionForIndexingDto> accumulator = new RuleDaoTest.Accumulator<>();
        RuleDefinitionDto r1 = db.rules().insert();
        RuleMetadataDto r1Extension = db.rules().insertOrUpdateMetadata(r1, organization, ( r) -> r.setTagsField("t1,t2"));
        RuleExtensionId r1ExtensionId = new RuleExtensionId(organization.getUuid(), r1.getId());
        RuleDefinitionDto r2 = db.rules().insert();
        db.rules().insertOrUpdateMetadata(r2, organization, ( r) -> r.setTagsField("t1,t3"));
        underTest.scrollIndexingRuleExtensionsByIds(db.getSession(), Collections.singletonList(r1ExtensionId), accumulator);
        assertThat(accumulator.list).extracting(RuleExtensionForIndexingDto::getRuleId, RuleExtensionForIndexingDto::getRuleKey, RuleExtensionForIndexingDto::getOrganizationUuid, RuleExtensionForIndexingDto::getTags).containsExactlyInAnyOrder(tuple(r1.getId(), r1.getKey(), organization.getUuid(), r1Extension.getTagsAsString()));
    }

    @Test
    public void selectAllDeprecatedRuleKeys() {
        RuleDefinitionDto r1 = db.rules().insert();
        RuleDefinitionDto r2 = db.rules().insert();
        db.rules().insertDeprecatedKey(( r) -> r.setRuleId(r1.getId()));
        db.rules().insertDeprecatedKey(( r) -> r.setRuleId(r2.getId()));
        db.getSession().commit();
        Set<DeprecatedRuleKeyDto> deprecatedRuleKeyDtos = underTest.selectAllDeprecatedRuleKeys(db.getSession());
        assertThat(deprecatedRuleKeyDtos).hasSize(2);
    }

    @Test
    public void selectAllDeprecatedRuleKeys_return_values_even_if_there_is_no_rule() {
        db.rules().insertDeprecatedKey();
        db.rules().insertDeprecatedKey();
        Set<DeprecatedRuleKeyDto> deprecatedRuleKeyDtos = underTest.selectAllDeprecatedRuleKeys(db.getSession());
        assertThat(deprecatedRuleKeyDtos).hasSize(2);
        assertThat(deprecatedRuleKeyDtos).extracting(DeprecatedRuleKeyDto::getNewRepositoryKey, DeprecatedRuleKeyDto::getNewRuleKey).containsExactly(tuple(null, null), tuple(null, null));
    }

    @Test
    public void deleteDeprecatedRuleKeys_with_empty_list_has_no_effect() {
        db.rules().insertDeprecatedKey();
        db.rules().insertDeprecatedKey();
        assertThat(underTest.selectAllDeprecatedRuleKeys(db.getSession())).hasSize(2);
        underTest.deleteDeprecatedRuleKeys(db.getSession(), Collections.emptyList());
        assertThat(underTest.selectAllDeprecatedRuleKeys(db.getSession())).hasSize(2);
    }

    @Test
    public void deleteDeprecatedRuleKeys_with_non_existing_uuid_has_no_effect() {
        db.rules().insertDeprecatedKey(( d) -> d.setUuid("A1"));
        db.rules().insertDeprecatedKey(( d) -> d.setUuid("A2"));
        assertThat(underTest.selectAllDeprecatedRuleKeys(db.getSession())).hasSize(2);
        underTest.deleteDeprecatedRuleKeys(db.getSession(), Arrays.asList("B1", "B2"));
        assertThat(underTest.selectAllDeprecatedRuleKeys(db.getSession())).hasSize(2);
    }

    @Test
    public void deleteDeprecatedRuleKeys() {
        DeprecatedRuleKeyDto deprecatedRuleKeyDto1 = db.rules().insertDeprecatedKey();
        db.rules().insertDeprecatedKey();
        assertThat(underTest.selectAllDeprecatedRuleKeys(db.getSession())).hasSize(2);
        underTest.deleteDeprecatedRuleKeys(db.getSession(), Collections.singletonList(deprecatedRuleKeyDto1.getUuid()));
        assertThat(underTest.selectAllDeprecatedRuleKeys(db.getSession())).hasSize(1);
    }

    @Test
    public void insertDeprecatedRuleKey() {
        RuleDefinitionDto r1 = db.rules().insert();
        DeprecatedRuleKeyDto deprecatedRuleKeyDto = db.rules().insertDeprecatedKey(( d) -> d.setRuleId(r1.getId()));
        db.getSession().commit();
        Set<DeprecatedRuleKeyDto> deprecatedRuleKeyDtos = underTest.selectAllDeprecatedRuleKeys(db.getSession());
        assertThat(deprecatedRuleKeyDtos).hasSize(1);
        DeprecatedRuleKeyDto deprecatedRuleKeyDto1 = deprecatedRuleKeyDtos.iterator().next();
        assertThat(deprecatedRuleKeyDto1.getOldRepositoryKey()).isEqualTo(deprecatedRuleKeyDto.getOldRepositoryKey());
        assertThat(deprecatedRuleKeyDto1.getOldRuleKey()).isEqualTo(deprecatedRuleKeyDto.getOldRuleKey());
        assertThat(deprecatedRuleKeyDto1.getNewRepositoryKey()).isEqualTo(r1.getRepositoryKey());
        assertThat(deprecatedRuleKeyDto1.getNewRuleKey()).isEqualTo(r1.getRuleKey());
        assertThat(deprecatedRuleKeyDto1.getUuid()).isEqualTo(deprecatedRuleKeyDto.getUuid());
        assertThat(deprecatedRuleKeyDto1.getCreatedAt()).isEqualTo(deprecatedRuleKeyDto.getCreatedAt());
        assertThat(deprecatedRuleKeyDto1.getRuleId()).isEqualTo(r1.getId());
    }

    @Test
    public void insertDeprecatedRuleKey_with_same_RuleKey_should_fail() {
        String repositoryKey = randomAlphanumeric(50);
        String ruleKey = randomAlphanumeric(50);
        db.rules().insertDeprecatedKey(( d) -> d.setOldRepositoryKey(repositoryKey).setOldRuleKey(ruleKey));
        thrown.expect(PersistenceException.class);
        db.rules().insertDeprecatedKey(( d) -> d.setOldRepositoryKey(repositoryKey).setOldRuleKey(ruleKey));
    }

    private static class Accumulator<T> implements Consumer<T> {
        private final List<T> list = new ArrayList<>();

        @Override
        public void accept(T dto) {
            list.add(dto);
        }
    }
}

