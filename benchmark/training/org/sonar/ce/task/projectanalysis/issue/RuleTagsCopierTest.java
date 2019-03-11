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


import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.core.issue.DefaultIssue;


public class RuleTagsCopierTest {
    DumbRule rule = new DumbRule(XOO_X1);

    @Rule
    public RuleRepositoryRule ruleRepository = new RuleRepositoryRule().add(rule);

    DefaultIssue issue = new DefaultIssue().setRuleKey(rule.getKey());

    RuleTagsCopier underTest = new RuleTagsCopier(ruleRepository);

    @Test
    public void copy_tags_if_new_issue() {
        rule.setTags(Sets.newHashSet("bug", "performance"));
        issue.setNew(true);
        underTest.onIssue(Mockito.mock(Component.class), issue);
        assertThat(issue.tags()).containsExactly("bug", "performance");
    }

    @Test
    public void do_not_copy_tags_if_existing_issue() {
        rule.setTags(Sets.newHashSet("bug", "performance"));
        issue.setNew(false).setTags(Arrays.asList("misra"));
        underTest.onIssue(Mockito.mock(Component.class), issue);
        assertThat(issue.tags()).containsExactly("misra");
    }

    @Test
    public void do_not_copy_tags_if_existing_issue_without_tags() {
        rule.setTags(Sets.newHashSet("bug", "performance"));
        issue.setNew(false).setTags(Collections.emptyList());
        underTest.onIssue(Mockito.mock(Component.class), issue);
        assertThat(issue.tags()).isEmpty();
    }
}

