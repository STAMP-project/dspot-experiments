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


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.scan.issue.filter.IssueFilterChain;
import org.sonar.api.utils.WildcardPattern;
import org.sonar.scanner.issue.DefaultFilterableIssue;


public class IgnoreIssuesFilterTest {
    private DefaultFilterableIssue issue = Mockito.mock(DefaultFilterableIssue.class);

    private IssueFilterChain chain = Mockito.mock(IssueFilterChain.class);

    private IgnoreIssuesFilter underTest = new IgnoreIssuesFilter();

    private DefaultInputFile component;

    private RuleKey ruleKey = RuleKey.of("foo", "bar");

    @Test
    public void shouldPassToChainIfMatcherHasNoPatternForIssue() {
        Mockito.when(chain.accept(issue)).thenReturn(true);
        assertThat(underTest.accept(issue, chain)).isTrue();
        Mockito.verify(chain).accept(ArgumentMatchers.any());
    }

    @Test
    public void shouldRejectIfRulePatternMatches() {
        WildcardPattern pattern = Mockito.mock(WildcardPattern.class);
        Mockito.when(pattern.match(ruleKey.toString())).thenReturn(true);
        underTest.addRuleExclusionPatternForComponent(component, pattern);
        assertThat(underTest.accept(issue, chain)).isFalse();
    }

    @Test
    public void shouldAcceptIfRulePatternDoesNotMatch() {
        WildcardPattern pattern = Mockito.mock(WildcardPattern.class);
        Mockito.when(pattern.match(ruleKey.toString())).thenReturn(false);
        underTest.addRuleExclusionPatternForComponent(component, pattern);
        assertThat(underTest.accept(issue, chain)).isFalse();
    }
}

