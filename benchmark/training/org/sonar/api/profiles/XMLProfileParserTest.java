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
package org.sonar.api.profiles;


import RulePriority.CRITICAL;
import org.junit.Test;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.utils.ValidationMessages;


public class XMLProfileParserTest {
    @Test
    public void importProfile() {
        ValidationMessages validation = ValidationMessages.create();
        RulesProfile profile = parse("importProfile.xml", validation);
        assertThat(profile.getLanguage()).isEqualTo("java");
        assertThat(profile.getName()).isEqualTo("sonar way");
        assertThat(validation.hasErrors()).isFalse();
        assertThat(profile).isNotNull();
        assertThat(profile.getActiveRule("checkstyle", "IllegalRegexp").getSeverity()).isEqualTo(CRITICAL);
    }

    @Test
    public void nameAndLanguageShouldBeMandatory() {
        ValidationMessages validation = ValidationMessages.create();
        parse("nameAndLanguageShouldBeMandatory.xml", validation);
        assertThat(validation.getErrors()).hasSize(2);
        assertThat(validation.getErrors().get(0)).contains("");
    }

    @Test
    public void importProfileWithRuleParameters() {
        ValidationMessages validation = ValidationMessages.create();
        RulesProfile profile = parse("importProfileWithRuleParameters.xml", validation);
        assertThat(validation.hasErrors()).isFalse();
        assertThat(validation.hasWarnings()).isFalse();
        ActiveRule rule = profile.getActiveRule("checkstyle", "IllegalRegexp");
        assertThat(rule.getParameter("format")).isEqualTo("foo");
        assertThat(rule.getParameter("message")).isEqualTo("with special characters < > &");
    }

    @Test
    public void importProfileWithUnknownRuleParameter() {
        ValidationMessages validation = ValidationMessages.create();
        RulesProfile profile = parse("importProfileWithUnknownRuleParameter.xml", validation);
        assertThat(validation.getWarnings()).hasSize(1);
        ActiveRule rule = profile.getActiveRule("checkstyle", "IllegalRegexp");
        assertThat(rule.getParameter("unknown")).isNull();
    }
}

