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
package org.sonar.scanner.report;


import Constants.Severity.BLOCKER;
import ScannerReport.ActiveRule;
import java.io.File;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.rule.internal.NewActiveRule;
import org.sonar.api.rule.RuleKey;
import org.sonar.core.util.CloseableIterator;
import org.sonar.scanner.protocol.output.ScannerReport;
import org.sonar.scanner.protocol.output.ScannerReportReader;
import org.sonar.scanner.protocol.output.ScannerReportWriter;


public class ActiveRulesPublisherTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void write() throws Exception {
        File outputDir = temp.newFolder();
        ScannerReportWriter writer = new ScannerReportWriter(outputDir);
        NewActiveRule ar = new NewActiveRule.Builder().setRuleKey(RuleKey.of("java", "S001")).setSeverity("BLOCKER").setParam("p1", "v1").setCreatedAt(1000L).setUpdatedAt(2000L).setQProfileKey("qp1").build();
        ActiveRules activeRules = new org.sonar.api.batch.rule.internal.DefaultActiveRules(Collections.singletonList(ar));
        ActiveRulesPublisher underTest = new ActiveRulesPublisher(activeRules);
        underTest.publish(writer);
        ScannerReportReader reader = new ScannerReportReader(outputDir);
        try (CloseableIterator<ScannerReport.ActiveRule> readIt = reader.readActiveRules()) {
            ScannerReport.ActiveRule reportAr = readIt.next();
            assertThat(reportAr.getRuleRepository()).isEqualTo("java");
            assertThat(reportAr.getRuleKey()).isEqualTo("S001");
            assertThat(reportAr.getSeverity()).isEqualTo(BLOCKER);
            assertThat(reportAr.getCreatedAt()).isEqualTo(1000L);
            assertThat(reportAr.getUpdatedAt()).isEqualTo(2000L);
            assertThat(reportAr.getQProfileKey()).isEqualTo("qp1");
            assertThat(reportAr.getParamsByKeyMap()).hasSize(1);
            assertThat(reportAr.getParamsByKeyMap().entrySet().iterator().next().getKey()).isEqualTo("p1");
            assertThat(reportAr.getParamsByKeyMap().entrySet().iterator().next().getValue()).isEqualTo("v1");
            assertThat(readIt.hasNext()).isFalse();
        }
    }
}

