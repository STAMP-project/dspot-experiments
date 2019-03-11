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
package org.sonar.ce.task.projectanalysis.issue;


import DbIssues.Locations;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sonar.api.rule.RuleKey;
import org.sonar.ce.task.projectanalysis.analysis.Analysis;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolderRule;
import org.sonar.ce.task.projectanalysis.analysis.ScannerPlugin;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.filemove.AddedFileRepository;
import org.sonar.ce.task.projectanalysis.qualityprofile.ActiveRule;
import org.sonar.ce.task.projectanalysis.qualityprofile.ActiveRulesHolder;
import org.sonar.ce.task.projectanalysis.qualityprofile.QProfileStatusRepository;
import org.sonar.ce.task.projectanalysis.scm.ScmInfo;
import org.sonar.ce.task.projectanalysis.scm.ScmInfoRepository;
import org.sonar.core.issue.DefaultIssue;
import org.sonar.db.protobuf.DbIssues.Locations.Builder;
import org.sonar.server.issue.IssueFieldsSetter;


@RunWith(DataProviderRunner.class)
public class IssueCreationDateCalculatorTest {
    private static final String COMPONENT_UUID = "ab12";

    @Rule
    public AnalysisMetadataHolderRule analysisMetadataHolder = new AnalysisMetadataHolderRule();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private ScmInfoRepository scmInfoRepository = Mockito.mock(ScmInfoRepository.class);

    private IssueFieldsSetter issueUpdater = Mockito.mock(IssueFieldsSetter.class);

    private ActiveRulesHolder activeRulesHolder = Mockito.mock(ActiveRulesHolder.class);

    private Component component = Mockito.mock(Component.class);

    private RuleKey ruleKey = RuleKey.of("reop", "rule");

    private DefaultIssue issue = Mockito.mock(DefaultIssue.class);

    private ActiveRule activeRule = Mockito.mock(ActiveRule.class);

    private IssueCreationDateCalculator underTest;

    private Analysis baseAnalysis = Mockito.mock(Analysis.class);

    private Map<String, ScannerPlugin> scannerPlugins = new HashMap<>();

    private RuleRepository ruleRepository = Mockito.mock(RuleRepository.class);

    private AddedFileRepository addedFileRepository = Mockito.mock(AddedFileRepository.class);

    private QProfileStatusRepository qProfileStatusRepository = Mockito.mock(QProfileStatusRepository.class);

    private ScmInfo scmInfo;

    private Rule rule = Mockito.mock(Rule.class);

    @Test
    public void should_not_backdate_if_no_scm_available() {
        previousAnalysisWas(2000L);
        currentAnalysisIs(3000L);
        makeIssueNew();
        noScm();
        setRuleUpdatedAt(2800L);
        run();
        assertNoChangeOfCreationDate();
    }

    @Test
    public void should_not_fail_for_issue_about_to_be_closed() {
        previousAnalysisWas(2000L);
        currentAnalysisIs(3000L);
        makeIssueNotNew();
        setIssueBelongToNonExistingRule();
        run();
        assertNoChangeOfCreationDate();
    }

    @Test
    public void should_fail_if_rule_is_not_found() {
        previousAnalysisWas(2000L);
        currentAnalysisIs(3000L);
        Mockito.when(ruleRepository.findByKey(ruleKey)).thenReturn(Optional.empty());
        makeIssueNew();
        exception.expect(IllegalStateException.class);
        exception.expectMessage("The rule with key 'reop:rule' raised an issue, but no rule with that key was found");
        run();
    }

    private static class NoIssueLocation implements BiConsumer<DefaultIssue, ScmInfo> {
        @Override
        public void accept(DefaultIssue issue, ScmInfo scmInfo) {
            IssueCreationDateCalculatorTest.setDateOfLatestScmChangeset(scmInfo, 1200L);
        }
    }

    private static class OnlyPrimaryLocation implements BiConsumer<DefaultIssue, ScmInfo> {
        @Override
        public void accept(DefaultIssue issue, ScmInfo scmInfo) {
            Mockito.when(issue.getLocations()).thenReturn(Locations.newBuilder().setTextRange(IssueCreationDateCalculatorTest.range(2, 3)).build());
            IssueCreationDateCalculatorTest.setDateOfChangetsetAtLine(scmInfo, 2, 1200L);
            IssueCreationDateCalculatorTest.setDateOfChangetsetAtLine(scmInfo, 3, 1300L);
        }
    }

    private static class FlowOnCurrentFileOnly implements BiConsumer<DefaultIssue, ScmInfo> {
        @Override
        public void accept(DefaultIssue issue, ScmInfo scmInfo) {
            Builder locations = Locations.newBuilder().setTextRange(IssueCreationDateCalculatorTest.range(2, 3)).addFlow(IssueCreationDateCalculatorTest.newFlow(IssueCreationDateCalculatorTest.newLocation(4, 5))).addFlow(IssueCreationDateCalculatorTest.newFlow(IssueCreationDateCalculatorTest.newLocation(6, 7, IssueCreationDateCalculatorTest.COMPONENT_UUID), IssueCreationDateCalculatorTest.newLocation(8, 9, IssueCreationDateCalculatorTest.COMPONENT_UUID)));
            Mockito.when(issue.getLocations()).thenReturn(locations.build());
            IssueCreationDateCalculatorTest.setDateOfChangetsetAtLine(scmInfo, 2, 1200L);
            IssueCreationDateCalculatorTest.setDateOfChangetsetAtLine(scmInfo, 3, 1300L);
            IssueCreationDateCalculatorTest.setDateOfChangetsetAtLine(scmInfo, 4, 1400L);
            IssueCreationDateCalculatorTest.setDateOfChangetsetAtLine(scmInfo, 5, 1500L);
            // some lines missing should be ok
            IssueCreationDateCalculatorTest.setDateOfChangetsetAtLine(scmInfo, 9, 1900L);
        }
    }

    private static class FlowOnMultipleFiles implements BiConsumer<DefaultIssue, ScmInfo> {
        @Override
        public void accept(DefaultIssue issue, ScmInfo scmInfo) {
            Builder locations = Locations.newBuilder().setTextRange(IssueCreationDateCalculatorTest.range(2, 3)).addFlow(IssueCreationDateCalculatorTest.newFlow(IssueCreationDateCalculatorTest.newLocation(4, 5))).addFlow(IssueCreationDateCalculatorTest.newFlow(IssueCreationDateCalculatorTest.newLocation(6, 7, IssueCreationDateCalculatorTest.COMPONENT_UUID), IssueCreationDateCalculatorTest.newLocation(8, 9, "another")));
            Mockito.when(issue.getLocations()).thenReturn(locations.build());
            IssueCreationDateCalculatorTest.setDateOfChangetsetAtLine(scmInfo, 2, 1200L);
            IssueCreationDateCalculatorTest.setDateOfChangetsetAtLine(scmInfo, 3, 1300L);
            IssueCreationDateCalculatorTest.setDateOfChangetsetAtLine(scmInfo, 4, 1400L);
            IssueCreationDateCalculatorTest.setDateOfChangetsetAtLine(scmInfo, 5, 1500L);
            IssueCreationDateCalculatorTest.setDateOfChangetsetAtLine(scmInfo, 6, 1600L);
            IssueCreationDateCalculatorTest.setDateOfChangetsetAtLine(scmInfo, 7, 1700L);
            IssueCreationDateCalculatorTest.setDateOfChangetsetAtLine(scmInfo, 8, 1800L);
            IssueCreationDateCalculatorTest.setDateOfChangetsetAtLine(scmInfo, 9, 1900L);
        }
    }
}

