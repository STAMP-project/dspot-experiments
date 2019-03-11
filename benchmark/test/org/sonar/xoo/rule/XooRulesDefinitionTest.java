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
package org.sonar.xoo.rule;


import DebtRemediationFunction.Type.LINEAR;
import OneIssuePerLineSensor.RULE_KEY;
import RulesDefinition.Context;
import RulesDefinition.Repository;
import RulesDefinition.Rule;
import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition;


public class XooRulesDefinitionTest {
    Context context;

    @Test
    public void define_xoo_rules() {
        RulesDefinition.Repository repo = context.repository("xoo");
        assertThat(repo).isNotNull();
        assertThat(repo.name()).isEqualTo("Xoo");
        assertThat(repo.language()).isEqualTo("xoo");
        assertThat(repo.rules()).hasSize(19);
        RulesDefinition.Rule rule = repo.rule(RULE_KEY);
        assertThat(rule.name()).isNotEmpty();
        assertThat(rule.debtRemediationFunction().type()).isEqualTo(LINEAR);
        assertThat(rule.debtRemediationFunction().gapMultiplier()).isEqualTo("1min");
        assertThat(rule.debtRemediationFunction().baseEffort()).isNull();
        assertThat(rule.gapDescription()).isNotEmpty();
    }

    @Test
    public void define_xoo_hotspot_rule() {
        RulesDefinition.Repository repo = context.repository("xoo");
        assertThat(repo).isNotNull();
        assertThat(repo.name()).isEqualTo("Xoo");
        assertThat(repo.language()).isEqualTo("xoo");
        assertThat(repo.rules()).hasSize(19);
        RulesDefinition.Rule rule = repo.rule(HotspotSensor.RULE_KEY);
        assertThat(rule.name()).isNotEmpty();
        assertThat(rule.securityStandards()).isNotEmpty().containsExactlyInAnyOrder("cwe:1", "cwe:123", "cwe:863", "owaspTop10:a1", "owaspTop10:a3");
    }

    @Test
    public void define_xooExternal_rules() {
        RulesDefinition.Repository repo = context.repository("external_XooEngine");
        assertThat(repo).isNotNull();
        assertThat(repo.name()).isEqualTo("XooEngine");
        assertThat(repo.language()).isEqualTo("xoo");
        assertThat(repo.rules()).hasSize(1);
    }

    @Test
    public void define_xoo2_rules() {
        RulesDefinition.Repository repo = context.repository("xoo2");
        assertThat(repo).isNotNull();
        assertThat(repo.name()).isEqualTo("Xoo2");
        assertThat(repo.language()).isEqualTo("xoo2");
        assertThat(repo.rules()).hasSize(2);
    }
}

