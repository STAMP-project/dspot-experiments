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
package org.sonar.server.debt;


import DebtRemediationFunction.Type.LINEAR;
import DebtRemediationFunction.Type.LINEAR_OFFSET;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.SystemUtils;
import org.junit.Test;
import org.sonar.api.rule.RuleKey;


public class DebtModelXMLExporterTest {
    DebtModelXMLExporter underTest = new DebtModelXMLExporter();

    @Test
    public void export_empty() {
        assertThat(underTest.export(Collections.emptyList())).isEqualTo(("<sqale/>" + (SystemUtils.LINE_SEPARATOR)));
    }

    @Test
    public void export_xml() throws Exception {
        List<DebtModelXMLExporter.RuleDebt> rules = Lists.newArrayList(new DebtModelXMLExporter.RuleDebt().setRuleKey(RuleKey.of("checkstyle", "Regexp")).setFunction(LINEAR_OFFSET.name()).setCoefficient("3d").setOffset("15min"));
        assertThat(underTest.export(rules)).isXmlEqualTo(DebtModelXMLExporterTest.getFileContent("export_xml.xml"));
    }

    @Test
    public void pretty_print_exported_xml() {
        List<DebtModelXMLExporter.RuleDebt> rules = Lists.newArrayList(new DebtModelXMLExporter.RuleDebt().setRuleKey(RuleKey.of("checkstyle", "Regexp")).setFunction(LINEAR.name()).setCoefficient("3d"));
        assertThat(underTest.export(rules)).isEqualTo(((((((((((((((((((((((((((((("<sqale>" + (SystemUtils.LINE_SEPARATOR)) + "  <chc>") + (SystemUtils.LINE_SEPARATOR)) + "    <rule-repo>checkstyle</rule-repo>") + (SystemUtils.LINE_SEPARATOR)) + "    <rule-key>Regexp</rule-key>") + (SystemUtils.LINE_SEPARATOR)) + "    <prop>") + (SystemUtils.LINE_SEPARATOR)) + "      <key>remediationFunction</key>") + (SystemUtils.LINE_SEPARATOR)) + "      <txt>LINEAR</txt>") + (SystemUtils.LINE_SEPARATOR)) + "    </prop>") + (SystemUtils.LINE_SEPARATOR)) + "    <prop>") + (SystemUtils.LINE_SEPARATOR)) + "      <key>remediationFactor</key>") + (SystemUtils.LINE_SEPARATOR)) + "      <val>3</val>") + (SystemUtils.LINE_SEPARATOR)) + "      <txt>d</txt>") + (SystemUtils.LINE_SEPARATOR)) + "    </prop>") + (SystemUtils.LINE_SEPARATOR)) + "  </chc>") + (SystemUtils.LINE_SEPARATOR)) + "</sqale>") + (SystemUtils.LINE_SEPARATOR)));
    }
}

