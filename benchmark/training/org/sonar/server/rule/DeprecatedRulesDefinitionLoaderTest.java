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


import DebtModelXMLExporter.RuleDebt;
import DebtRemediationFunction.Type.LINEAR_OFFSET;
import Rule.STATUS_BETA;
import RulePriority.BLOCKER;
import RuleStatus.BETA;
import RulesDefinition.Context;
import RulesDefinition.Param;
import RulesDefinition.Repository;
import com.google.common.collect.Lists;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleRepository;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.core.i18n.RuleI18nManager;
import org.sonar.server.debt.DebtModelPluginRepository;
import org.sonar.server.debt.DebtModelXMLExporter;
import org.sonar.server.debt.DebtRulesXMLImporter;
import org.sonar.server.plugins.ServerPluginRepository;


@RunWith(MockitoJUnitRunner.class)
public class DeprecatedRulesDefinitionLoaderTest {
    @Mock
    RuleI18nManager i18n;

    @Mock
    DebtModelPluginRepository debtModelRepository;

    @Mock
    DebtRulesXMLImporter importer;

    @Mock
    ServerPluginRepository pluginRepository;

    static class CheckstyleRules extends RuleRepository {
        public CheckstyleRules() {
            super("checkstyle", "java");
            setName("Checkstyle");
        }

        @Override
        public List<Rule> createRules() {
            Rule rule = Rule.create("checkstyle", "ConstantName", "Constant Name");
            rule.setDescription("Checks that constant names conform to the specified format");
            rule.setConfigKey("Checker/TreeWalker/ConstantName");
            rule.setSeverity(BLOCKER);
            rule.setStatus(STATUS_BETA);
            rule.setTags(new String[]{ "style", "clumsy" });
            rule.createParameter("format").setDescription("Regular expression").setDefaultValue("A-Z").setType("REGULAR_EXPRESSION");
            return Arrays.asList(rule);
        }
    }

    static class UseBundles extends RuleRepository {
        public UseBundles() {
            super("checkstyle", "java");
            setName("Checkstyle");
        }

        @Override
        public List<Rule> createRules() {
            Rule rule = Rule.create("checkstyle", "ConstantName");
            rule.createParameter("format");
            return Arrays.asList(rule);
        }
    }

    @Test
    public void wrap_deprecated_rule_repositories() {
        RulesDefinition.Context context = new RulesDefinition.Context();
        DeprecatedRulesDefinitionLoaderTest.CheckstyleRules checkstyleRules = new DeprecatedRulesDefinitionLoaderTest.CheckstyleRules();
        Mockito.when(pluginRepository.getPluginKey(checkstyleRules)).thenReturn("unittest");
        new DeprecatedRulesDefinitionLoader(i18n, debtModelRepository, importer, pluginRepository, new RuleRepository[]{ checkstyleRules }).complete(context);
        assertThat(context.repositories()).hasSize(1);
        RulesDefinition.Repository checkstyle = context.repository("checkstyle");
        assertThat(checkstyle).isNotNull();
        assertThat(checkstyle.key()).isEqualTo("checkstyle");
        assertThat(checkstyle.name()).isEqualTo("Checkstyle");
        assertThat(checkstyle.language()).isEqualTo("java");
        assertThat(checkstyle.rules()).hasSize(1);
        RulesDefinition.Rule rule = checkstyle.rule("ConstantName");
        assertThat(rule).isNotNull();
        assertThat(rule.key()).isEqualTo("ConstantName");
        assertThat(rule.pluginKey()).isEqualTo("unittest");
        assertThat(rule.name()).isEqualTo("Constant Name");
        assertThat(rule.htmlDescription()).isEqualTo("Checks that constant names conform to the specified format");
        assertThat(rule.severity()).isEqualTo(Severity.BLOCKER);
        assertThat(rule.internalKey()).isEqualTo("Checker/TreeWalker/ConstantName");
        assertThat(rule.status()).isEqualTo(BETA);
        assertThat(rule.tags()).containsOnly("style", "clumsy");
        assertThat(rule.params()).hasSize(1);
        RulesDefinition.Param param = rule.param("format");
        assertThat(param).isNotNull();
        assertThat(param.key()).isEqualTo("format");
        assertThat(param.name()).isEqualTo("format");
        assertThat(param.description()).isEqualTo("Regular expression");
        assertThat(param.defaultValue()).isEqualTo("A-Z");
    }

    @Test
    public void emulate_the_day_deprecated_api_can_be_dropped() {
        RulesDefinition.Context context = new RulesDefinition.Context();
        // no more RuleRepository !
        new DeprecatedRulesDefinitionLoader(i18n, debtModelRepository, importer, pluginRepository);
        assertThat(context.repositories()).isEmpty();
    }

    @Test
    public void use_l10n_bundles() {
        RulesDefinition.Context context = new RulesDefinition.Context();
        Mockito.when(i18n.getName("checkstyle", "ConstantName")).thenReturn("Constant Name");
        Mockito.when(i18n.getDescription("checkstyle", "ConstantName")).thenReturn("Checks that constant names conform to the specified format");
        Mockito.when(i18n.getParamDescription("checkstyle", "ConstantName", "format")).thenReturn("Regular expression");
        new DeprecatedRulesDefinitionLoader(i18n, debtModelRepository, importer, pluginRepository, new RuleRepository[]{ new DeprecatedRulesDefinitionLoaderTest.UseBundles() }).complete(context);
        RulesDefinition.Repository checkstyle = context.repository("checkstyle");
        RulesDefinition.Rule rule = checkstyle.rule("ConstantName");
        assertThat(rule.key()).isEqualTo("ConstantName");
        assertThat(rule.name()).isEqualTo("Constant Name");
        assertThat(rule.htmlDescription()).isEqualTo("Checks that constant names conform to the specified format");
        RulesDefinition.Param param = rule.param("format");
        assertThat(param.key()).isEqualTo("format");
        assertThat(param.name()).isEqualTo("format");
        assertThat(param.description()).isEqualTo("Regular expression");
    }

    @Test
    public void define_rule_debt() {
        RulesDefinition.Context context = new RulesDefinition.Context();
        List<DebtModelXMLExporter.RuleDebt> ruleDebts = Lists.newArrayList(new DebtModelXMLExporter.RuleDebt().setRuleKey(RuleKey.of("checkstyle", "ConstantName")).setFunction(LINEAR_OFFSET.name()).setCoefficient("1d").setOffset("10min"));
        Reader javaModelReader = Mockito.mock(Reader.class);
        Mockito.when(debtModelRepository.createReaderForXMLFile("java")).thenReturn(javaModelReader);
        Mockito.when(debtModelRepository.getContributingPluginList()).thenReturn(Lists.newArrayList("java"));
        Mockito.when(importer.importXML(ArgumentMatchers.eq(javaModelReader), ArgumentMatchers.any(ValidationMessages.class))).thenReturn(ruleDebts);
        new DeprecatedRulesDefinitionLoader(i18n, debtModelRepository, importer, pluginRepository, new RuleRepository[]{ new DeprecatedRulesDefinitionLoaderTest.CheckstyleRules() }).complete(context);
        assertThat(context.repositories()).hasSize(1);
        RulesDefinition.Repository checkstyle = context.repository("checkstyle");
        assertThat(checkstyle.rules()).hasSize(1);
        RulesDefinition.Rule rule = checkstyle.rule("ConstantName");
        assertThat(rule).isNotNull();
        assertThat(rule.key()).isEqualTo("ConstantName");
        assertThat(rule.debtRemediationFunction().type()).isEqualTo(LINEAR_OFFSET);
        assertThat(rule.debtRemediationFunction().gapMultiplier()).isEqualTo("1d");
        assertThat(rule.debtRemediationFunction().baseEffort()).isEqualTo("10min");
    }

    @Test
    public void fail_on_invalid_rule_debt() {
        RulesDefinition.Context context = new RulesDefinition.Context();
        List<DebtModelXMLExporter.RuleDebt> ruleDebts = Lists.newArrayList(new DebtModelXMLExporter.RuleDebt().setRuleKey(RuleKey.of("checkstyle", "ConstantName")).setFunction(LINEAR_OFFSET.name()).setCoefficient("1d"));
        Reader javaModelReader = Mockito.mock(Reader.class);
        Mockito.when(debtModelRepository.createReaderForXMLFile("java")).thenReturn(javaModelReader);
        Mockito.when(debtModelRepository.getContributingPluginList()).thenReturn(Lists.newArrayList("java"));
        Mockito.when(importer.importXML(ArgumentMatchers.eq(javaModelReader), ArgumentMatchers.any(ValidationMessages.class))).thenReturn(ruleDebts);
        try {
            new DeprecatedRulesDefinitionLoader(i18n, debtModelRepository, importer, pluginRepository, new RuleRepository[]{ new DeprecatedRulesDefinitionLoaderTest.CheckstyleRules() }).complete(context);
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(IllegalArgumentException.class);
        }
        assertThat(context.repositories()).isEmpty();
    }
}

