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


import Format.MARKDOWN;
import RuleStatus.READY;
import RuleStatus.REMOVED;
import Severity.INFO;
import Severity.MAJOR;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import org.assertj.core.api.Fail;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.System2;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.db.rule.RuleDto;
import org.sonar.db.rule.RuleParamDto;
import org.sonar.db.rule.RuleTesting;
import org.sonar.server.es.EsTester;
import org.sonar.server.es.SearchOptions;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.rule.index.RuleIndex;
import org.sonar.server.rule.index.RuleIndexer;
import org.sonar.server.rule.index.RuleQuery;
import org.sonar.server.util.TypeValidationsTesting;


public class RuleCreatorTest {
    private System2 system2 = Mockito.mock(System2.class);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester dbTester = DbTester.create(system2);

    @Rule
    public EsTester es = EsTester.create();

    private RuleIndex ruleIndex = new RuleIndex(es.client(), system2);

    private RuleIndexer ruleIndexer = new RuleIndexer(es.client(), dbTester.getDbClient());

    private DbSession dbSession = dbTester.getSession();

    private RuleCreator underTest = new RuleCreator(system2, new RuleIndexer(es.client(), dbTester.getDbClient()), dbTester.getDbClient(), TypeValidationsTesting.newFullTypeValidations(), TestDefaultOrganizationProvider.from(dbTester));

    @Test
    public void create_custom_rule() {
        // insert template rule
        RuleDto templateRule = createTemplateRule();
        // Create custom rule
        NewCustomRule newRule = NewCustomRule.createForCustomRule("CUSTOM_RULE", templateRule.getKey()).setName("My custom").setMarkdownDescription("Some description").setSeverity(MAJOR).setStatus(READY).setParameters(ImmutableMap.of("regex", "a.*"));
        RuleKey customRuleKey = underTest.create(dbSession, newRule);
        RuleDto rule = dbTester.getDbClient().ruleDao().selectOrFailByKey(dbSession, dbTester.getDefaultOrganization(), customRuleKey);
        assertThat(rule).isNotNull();
        assertThat(rule.getKey()).isEqualTo(RuleKey.of("java", "CUSTOM_RULE"));
        assertThat(rule.getPluginKey()).isEqualTo("sonarjava");
        assertThat(rule.getTemplateId()).isEqualTo(templateRule.getId());
        assertThat(rule.getName()).isEqualTo("My custom");
        assertThat(rule.getDescription()).isEqualTo("Some description");
        assertThat(rule.getSeverityString()).isEqualTo("MAJOR");
        assertThat(rule.getStatus()).isEqualTo(READY);
        assertThat(rule.getLanguage()).isEqualTo("java");
        assertThat(rule.getConfigKey()).isEqualTo("S001");
        assertThat(rule.getDefRemediationFunction()).isEqualTo("LINEAR_OFFSET");
        assertThat(rule.getDefRemediationGapMultiplier()).isEqualTo("1h");
        assertThat(rule.getDefRemediationBaseEffort()).isEqualTo("5min");
        assertThat(rule.getGapDescription()).isEqualTo("desc");
        assertThat(rule.getTags()).containsOnly("usertag1", "usertag2");
        assertThat(rule.getSystemTags()).containsOnly("tag1", "tag4");
        assertThat(rule.getSecurityStandards()).containsOnly("owaspTop10:a1", "cwe:123");
        assertThat(rule.isExternal()).isFalse();
        assertThat(rule.isAdHoc()).isFalse();
        List<RuleParamDto> params = dbTester.getDbClient().ruleDao().selectRuleParamsByRuleKey(dbSession, customRuleKey);
        assertThat(params).hasSize(1);
        RuleParamDto param = params.get(0);
        // From template rule
        assertThat(param.getName()).isEqualTo("regex");
        assertThat(param.getDescription()).isEqualTo("Reg ex");
        assertThat(param.getType()).isEqualTo("STRING");
        // From user
        assertThat(param.getDefaultValue()).isEqualTo("a.*");
        assertThat(ruleIndex.search(new RuleQuery(), new SearchOptions()).getIds()).containsOnly(rule.getId(), templateRule.getId());
    }

    @Test
    public void create_custom_rule_with_empty_parameter_value() {
        // insert template rule
        RuleDto templateRule = createTemplateRule();
        NewCustomRule newRule = NewCustomRule.createForCustomRule("CUSTOM_RULE", templateRule.getKey()).setName("My custom").setHtmlDescription("Some description").setSeverity(MAJOR).setStatus(READY).setParameters(ImmutableMap.of("regex", ""));
        RuleKey customRuleKey = underTest.create(dbSession, newRule);
        List<RuleParamDto> params = dbTester.getDbClient().ruleDao().selectRuleParamsByRuleKey(dbSession, customRuleKey);
        assertThat(params).hasSize(1);
        RuleParamDto param = params.get(0);
        assertThat(param.getName()).isEqualTo("regex");
        assertThat(param.getDescription()).isEqualTo("Reg ex");
        assertThat(param.getType()).isEqualTo("STRING");
        assertThat(param.getDefaultValue()).isNull();
    }

    @Test
    public void create_custom_rule_with_no_parameter_value() {
        // insert template rule
        RuleDefinitionDto templateRule = createTemplateRuleWithIntArrayParam();
        NewCustomRule newRule = NewCustomRule.createForCustomRule("CUSTOM_RULE", templateRule.getKey()).setName("My custom").setHtmlDescription("Some description").setSeverity(MAJOR).setStatus(READY);
        RuleKey customRuleKey = underTest.create(dbSession, newRule);
        List<RuleParamDto> params = dbTester.getDbClient().ruleDao().selectRuleParamsByRuleKey(dbSession, customRuleKey);
        assertThat(params).hasSize(1);
        RuleParamDto param = params.get(0);
        assertThat(param.getName()).isEqualTo("myIntegers");
        assertThat(param.getDescription()).isEqualTo("My Integers");
        assertThat(param.getType()).isEqualTo("INTEGER,multiple=true,values=1;2;3");
        assertThat(param.getDefaultValue()).isNull();
    }

    @Test
    public void create_custom_rule_with_multiple_parameter_values() {
        // insert template rule
        RuleDefinitionDto templateRule = createTemplateRuleWithIntArrayParam();
        NewCustomRule newRule = NewCustomRule.createForCustomRule("CUSTOM_RULE", templateRule.getKey()).setName("My custom").setHtmlDescription("Some description").setSeverity(MAJOR).setStatus(READY).setParameters(ImmutableMap.of("myIntegers", "1,3"));
        RuleKey customRuleKey = underTest.create(dbSession, newRule);
        List<RuleParamDto> params = dbTester.getDbClient().ruleDao().selectRuleParamsByRuleKey(dbSession, customRuleKey);
        assertThat(params).hasSize(1);
        RuleParamDto param = params.get(0);
        assertThat(param.getName()).isEqualTo("myIntegers");
        assertThat(param.getDescription()).isEqualTo("My Integers");
        assertThat(param.getType()).isEqualTo("INTEGER,multiple=true,values=1;2;3");
        assertThat(param.getDefaultValue()).isEqualTo("1,3");
    }

    @Test
    public void fail_to_create_custom_rule_with_invalid_parameter() {
        // insert template rule
        RuleDefinitionDto templateRule = createTemplateRuleWithIntArrayParam();
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Value 'polop' must be an integer.");
        // Create custom rule
        NewCustomRule newRule = NewCustomRule.createForCustomRule("CUSTOM_RULE", templateRule.getKey()).setName("My custom").setMarkdownDescription("Some description").setSeverity(MAJOR).setStatus(READY).setParameters(ImmutableMap.of("myIntegers", "1,polop,2"));
        underTest.create(dbSession, newRule);
    }

    @Test
    public void fail_to_create_custom_rule_with_invalid_parameters() {
        // insert template rule
        RuleDefinitionDto templateRule = createTemplateRuleWithTwoIntParams();
        // Create custom rule
        NewCustomRule newRule = NewCustomRule.createForCustomRule("CUSTOM_RULE", templateRule.getKey()).setName("My custom").setMarkdownDescription("Some description").setSeverity(MAJOR).setStatus(READY).setParameters(ImmutableMap.of("first", "polop", "second", "palap"));
        try {
            underTest.create(dbSession, newRule);
            Fail.failBecauseExceptionWasNotThrown(BadRequestException.class);
        } catch (BadRequestException badRequest) {
            assertThat(badRequest.errors().toString()).contains("palap").contains("polop");
        }
    }

    @Test
    public void reactivate_custom_rule_if_already_exists_in_removed_status() {
        String key = "CUSTOM_RULE";
        // insert template rule
        RuleDto templateRule = createTemplateRule();
        // insert a removed rule
        RuleDto rule = RuleTesting.newCustomRule(templateRule).setRuleKey(key).setStatus(REMOVED).setName("Old name").setDescription("Old description").setDescriptionFormat(MARKDOWN).setSeverity(INFO);
        dbTester.rules().insert(rule.getDefinition());
        dbTester.rules().insertRuleParam(rule.getDefinition(), ( param) -> param.setDefaultValue("a.*"));
        dbSession.commit();
        // Create custom rule with same key, but with different values
        NewCustomRule newRule = NewCustomRule.createForCustomRule(key, templateRule.getKey()).setName("New name").setMarkdownDescription("New description").setSeverity(MAJOR).setStatus(READY).setParameters(ImmutableMap.of("regex", "c.*"));
        RuleKey customRuleKey = underTest.create(dbSession, newRule);
        RuleDefinitionDto result = dbTester.getDbClient().ruleDao().selectOrFailDefinitionByKey(dbSession, customRuleKey);
        assertThat(result.getKey()).isEqualTo(RuleKey.of("java", key));
        assertThat(result.getStatus()).isEqualTo(READY);
        // These values should be the same than before
        assertThat(result.getName()).isEqualTo("Old name");
        assertThat(result.getDescription()).isEqualTo("Old description");
        assertThat(result.getSeverityString()).isEqualTo(INFO);
        List<RuleParamDto> params = dbTester.getDbClient().ruleDao().selectRuleParamsByRuleKey(dbSession, customRuleKey);
        assertThat(params).hasSize(1);
        assertThat(params.get(0).getDefaultValue()).isEqualTo("a.*");
    }

    @Test
    public void generate_reactivation_exception_when_rule_exists_in_removed_status_and_prevent_reactivation_parameter_is_true() {
        String key = "CUSTOM_RULE";
        // insert template rule
        RuleDto templateRule = createTemplateRule();
        // insert a removed rule
        RuleDto rule = RuleTesting.newCustomRule(templateRule).setRuleKey(key).setStatus(REMOVED).setName("Old name").setDescription("Old description").setSeverity(INFO);
        dbTester.rules().insert(rule.getDefinition());
        dbTester.rules().insertRuleParam(rule.getDefinition(), ( param) -> param.setDefaultValue("a.*"));
        dbSession.commit();
        // Create custom rule with same key, but with different values
        NewCustomRule newRule = NewCustomRule.createForCustomRule(key, templateRule.getKey()).setName("New name").setHtmlDescription("New description").setSeverity(MAJOR).setStatus(READY).setParameters(ImmutableMap.of("regex", "c.*")).setPreventReactivation(true);
        try {
            underTest.create(dbSession, newRule);
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(ReactivationException.class);
            ReactivationException reactivationException = ((ReactivationException) (e));
            assertThat(reactivationException.ruleKey()).isEqualTo(rule.getKey());
        }
    }

    @Test
    public void fail_to_create_custom_rule_when_invalid_key() {
        // insert template rule
        RuleDto templateRule = createTemplateRule();
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("The rule key \"*INVALID*\" is invalid, it should only contain: a-z, 0-9, \"_\"");
        NewCustomRule newRule = NewCustomRule.createForCustomRule("*INVALID*", templateRule.getKey()).setName("My custom").setHtmlDescription("Some description").setSeverity(MAJOR).setStatus(READY).setParameters(ImmutableMap.of("regex", "a.*"));
        underTest.create(dbSession, newRule);
    }

    @Test
    public void fail_to_create_custom_rule_when_rule_key_already_exists() {
        // insert template rule
        RuleDto templateRule = createTemplateRule();
        // Create a custom rule
        NewCustomRule newRule = NewCustomRule.createForCustomRule("CUSTOM_RULE", templateRule.getKey()).setName("My custom").setHtmlDescription("Some description").setSeverity(MAJOR).setStatus(READY).setParameters(ImmutableMap.of("regex", "a.*"));
        underTest.create(dbSession, newRule);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("A rule with the key 'CUSTOM_RULE' already exists");
        // Create another custom rule having same key
        newRule = NewCustomRule.createForCustomRule("CUSTOM_RULE", templateRule.getKey()).setName("My another custom").setHtmlDescription("Some description").setSeverity(MAJOR).setStatus(READY).setParameters(ImmutableMap.of("regex", "a.*"));
        underTest.create(dbSession, newRule);
    }

    @Test
    public void fail_to_create_custom_rule_when_missing_name() {
        // insert template rule
        RuleDto templateRule = createTemplateRule();
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("The name is missing");
        NewCustomRule newRule = NewCustomRule.createForCustomRule("CUSTOM_RULE", templateRule.getKey()).setHtmlDescription("Some description").setSeverity(MAJOR).setStatus(READY).setParameters(ImmutableMap.of("regex", "a.*"));
        underTest.create(dbSession, newRule);
    }

    @Test
    public void fail_to_create_custom_rule_when_missing_description() {
        // insert template rule
        RuleDto templateRule = createTemplateRule();
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("The description is missing");
        NewCustomRule newRule = NewCustomRule.createForCustomRule("CUSTOM_RULE", templateRule.getKey()).setName("My custom").setSeverity(MAJOR).setStatus(READY).setParameters(ImmutableMap.of("regex", "a.*"));
        underTest.create(dbSession, newRule);
    }

    @Test
    public void fail_to_create_custom_rule_when_missing_severity() {
        // insert template rule
        RuleDto templateRule = createTemplateRule();
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("The severity is missing");
        NewCustomRule newRule = NewCustomRule.createForCustomRule("CUSTOM_RULE", templateRule.getKey()).setName("My custom").setHtmlDescription("Some description").setStatus(READY).setParameters(ImmutableMap.of("regex", "a.*"));
        underTest.create(dbSession, newRule);
    }

    @Test
    public void fail_to_create_custom_rule_when_invalid_severity() {
        // insert template rule
        RuleDto templateRule = createTemplateRule();
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Severity \"INVALID\" is invalid");
        NewCustomRule newRule = NewCustomRule.createForCustomRule("CUSTOM_RULE", templateRule.getKey()).setName("My custom").setHtmlDescription("Some description").setSeverity("INVALID").setStatus(READY).setParameters(ImmutableMap.of("regex", "a.*"));
        underTest.create(dbSession, newRule);
    }

    @Test
    public void fail_to_create_custom_rule_when_missing_status() {
        // insert template rule
        RuleDto templateRule = createTemplateRule();
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("The status is missing");
        NewCustomRule newRule = NewCustomRule.createForCustomRule("CUSTOM_RULE", templateRule.getKey()).setName("My custom").setHtmlDescription("Some description").setSeverity(MAJOR).setParameters(ImmutableMap.of("regex", "a.*"));
        underTest.create(dbSession, newRule);
    }

    @Test
    public void fail_to_create_custom_rule_when_wrong_rule_template() {
        // insert rule
        RuleDefinitionDto rule = newRule(RuleKey.of("java", "S001")).setIsTemplate(false);
        dbTester.rules().insert(rule);
        dbSession.commit();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("This rule is not a template rule: java:S001");
        // Create custom rule with unknown template rule
        NewCustomRule newRule = NewCustomRule.createForCustomRule("CUSTOM_RULE", rule.getKey()).setName("My custom").setHtmlDescription("Some description").setSeverity(MAJOR).setStatus(READY).setParameters(ImmutableMap.of("regex", "a.*"));
        underTest.create(dbSession, newRule);
    }

    @Test
    public void fail_to_create_custom_rule_when_unknown_template() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The template key doesn't exist: java:S001");
        // Create custom rule
        NewCustomRule newRule = NewCustomRule.createForCustomRule("CUSTOM_RULE", RuleKey.of("java", "S001")).setName("My custom").setMarkdownDescription("Some description").setSeverity(MAJOR).setStatus(READY);
        underTest.create(dbSession, newRule);
    }
}

