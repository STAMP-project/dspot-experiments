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
package org.sonar.scanner.issue.ignore;


import com.google.common.collect.ImmutableList;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.notifications.AnalysisWarnings;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.scan.issue.filter.IssueFilterChain;
import org.sonar.scanner.issue.DefaultFilterableIssue;
import org.sonar.scanner.issue.ignore.pattern.IssueInclusionPatternInitializer;
import org.sonar.scanner.issue.ignore.pattern.IssuePattern;


public class EnforceIssuesFilterTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private IssueInclusionPatternInitializer exclusionPatternInitializer;

    private EnforceIssuesFilter ignoreFilter;

    private DefaultFilterableIssue issue;

    private IssueFilterChain chain;

    @Test
    public void shouldPassToChainIfNoConfiguredPatterns() {
        ignoreFilter = new EnforceIssuesFilter(exclusionPatternInitializer, Mockito.mock(AnalysisWarnings.class));
        assertThat(ignoreFilter.accept(issue, chain)).isTrue();
        Mockito.verify(chain).accept(issue);
    }

    @Test
    public void shouldPassToChainIfRuleDoesNotMatch() {
        String rule = "rule";
        RuleKey ruleKey = Mockito.mock(RuleKey.class);
        Mockito.when(ruleKey.toString()).thenReturn(rule);
        Mockito.when(issue.ruleKey()).thenReturn(ruleKey);
        IssuePattern matching = Mockito.mock(IssuePattern.class);
        Mockito.when(matching.matchRule(ruleKey)).thenReturn(false);
        Mockito.when(exclusionPatternInitializer.getMulticriteriaPatterns()).thenReturn(ImmutableList.of(matching));
        ignoreFilter = new EnforceIssuesFilter(exclusionPatternInitializer, Mockito.mock(AnalysisWarnings.class));
        assertThat(ignoreFilter.accept(issue, chain)).isTrue();
        Mockito.verify(chain).accept(issue);
    }

    @Test
    public void shouldAcceptIssueIfFullyMatched() {
        String rule = "rule";
        String path = "org/sonar/api/Issue.java";
        RuleKey ruleKey = Mockito.mock(RuleKey.class);
        Mockito.when(ruleKey.toString()).thenReturn(rule);
        Mockito.when(issue.ruleKey()).thenReturn(ruleKey);
        IssuePattern matching = Mockito.mock(IssuePattern.class);
        Mockito.when(matching.matchRule(ruleKey)).thenReturn(true);
        Mockito.when(matching.matchFile(path)).thenReturn(true);
        Mockito.when(exclusionPatternInitializer.getMulticriteriaPatterns()).thenReturn(ImmutableList.of(matching));
        Mockito.when(issue.getComponent()).thenReturn(createComponentWithPath(path));
        ignoreFilter = new EnforceIssuesFilter(exclusionPatternInitializer, Mockito.mock(AnalysisWarnings.class));
        assertThat(ignoreFilter.accept(issue, chain)).isTrue();
        Mockito.verifyZeroInteractions(chain);
    }

    @Test
    public void shouldRefuseIssueIfRuleMatchesButNotPath() {
        String rule = "rule";
        String path = "org/sonar/api/Issue.java";
        String componentKey = "org.sonar.api.Issue";
        RuleKey ruleKey = Mockito.mock(RuleKey.class);
        Mockito.when(ruleKey.toString()).thenReturn(rule);
        Mockito.when(issue.ruleKey()).thenReturn(ruleKey);
        Mockito.when(issue.componentKey()).thenReturn(componentKey);
        IssuePattern matching = Mockito.mock(IssuePattern.class);
        Mockito.when(matching.matchRule(ruleKey)).thenReturn(true);
        Mockito.when(matching.matchFile(path)).thenReturn(false);
        Mockito.when(exclusionPatternInitializer.getMulticriteriaPatterns()).thenReturn(ImmutableList.of(matching));
        Mockito.when(issue.getComponent()).thenReturn(createComponentWithPath(path));
        ignoreFilter = new EnforceIssuesFilter(exclusionPatternInitializer, Mockito.mock(AnalysisWarnings.class));
        assertThat(ignoreFilter.accept(issue, chain)).isFalse();
        Mockito.verifyZeroInteractions(chain);
    }

    @Test
    public void shouldRefuseIssueIfRuleMatchesAndNotFile() throws IOException {
        String rule = "rule";
        String path = "org/sonar/api/Issue.java";
        String componentKey = "org.sonar.api.Issue";
        RuleKey ruleKey = Mockito.mock(RuleKey.class);
        Mockito.when(ruleKey.toString()).thenReturn(rule);
        Mockito.when(issue.ruleKey()).thenReturn(ruleKey);
        IssuePattern matching = Mockito.mock(IssuePattern.class);
        Mockito.when(matching.matchRule(ruleKey)).thenReturn(true);
        Mockito.when(matching.matchFile(path)).thenReturn(true);
        Mockito.when(exclusionPatternInitializer.getMulticriteriaPatterns()).thenReturn(ImmutableList.of(matching));
        Mockito.when(issue.getComponent()).thenReturn(TestInputFileBuilder.newDefaultInputProject("foo", tempFolder.newFolder()));
        ignoreFilter = new EnforceIssuesFilter(exclusionPatternInitializer, Mockito.mock(AnalysisWarnings.class));
        assertThat(ignoreFilter.accept(issue, chain)).isFalse();
        Mockito.verifyZeroInteractions(chain);
    }
}

