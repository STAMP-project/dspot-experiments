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
package org.sonar.scanner.rule;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.rule.RuleKey;


public class ActiveRulesProviderTest {
    private ActiveRulesProvider provider;

    @Mock
    private DefaultActiveRulesLoader loader;

    @Test
    public void testCombinationOfRules() {
        LoadedActiveRule r1 = ActiveRulesProviderTest.mockRule("rule1");
        LoadedActiveRule r2 = ActiveRulesProviderTest.mockRule("rule2");
        LoadedActiveRule r3 = ActiveRulesProviderTest.mockRule("rule3");
        List<LoadedActiveRule> qp1Rules = ImmutableList.of(r1, r2);
        List<LoadedActiveRule> qp2Rules = ImmutableList.of(r2, r3);
        List<LoadedActiveRule> qp3Rules = ImmutableList.of(r1, r3);
        Mockito.when(loader.load(ArgumentMatchers.eq("qp1"))).thenReturn(qp1Rules);
        Mockito.when(loader.load(ArgumentMatchers.eq("qp2"))).thenReturn(qp2Rules);
        Mockito.when(loader.load(ArgumentMatchers.eq("qp3"))).thenReturn(qp3Rules);
        QualityProfiles profiles = ActiveRulesProviderTest.mockProfiles("qp1", "qp2", "qp3");
        ActiveRules activeRules = provider.provide(loader, profiles);
        assertThat(activeRules.findAll()).hasSize(3);
        assertThat(activeRules.findAll()).extracting("ruleKey").containsOnly(RuleKey.of("rule1", "rule1"), RuleKey.of("rule2", "rule2"), RuleKey.of("rule3", "rule3"));
        Mockito.verify(loader).load(ArgumentMatchers.eq("qp1"));
        Mockito.verify(loader).load(ArgumentMatchers.eq("qp2"));
        Mockito.verify(loader).load(ArgumentMatchers.eq("qp3"));
        Mockito.verifyNoMoreInteractions(loader);
    }

    @Test
    public void testParamsAreTransformed() {
        LoadedActiveRule r1 = ActiveRulesProviderTest.mockRule("rule1");
        LoadedActiveRule r2 = ActiveRulesProviderTest.mockRule("rule2");
        r2.setParams(ImmutableMap.of("foo1", "bar1", "foo2", "bar2"));
        List<LoadedActiveRule> qpRules = ImmutableList.of(r1, r2);
        Mockito.when(loader.load(ArgumentMatchers.eq("qp"))).thenReturn(qpRules);
        QualityProfiles profiles = ActiveRulesProviderTest.mockProfiles("qp");
        ActiveRules activeRules = provider.provide(loader, profiles);
        assertThat(activeRules.findAll()).hasSize(2);
        assertThat(activeRules.findAll()).extracting("ruleKey", "params").containsOnly(Tuple.tuple(RuleKey.of("rule1", "rule1"), ImmutableMap.of()), Tuple.tuple(RuleKey.of("rule2", "rule2"), ImmutableMap.of("foo1", "bar1", "foo2", "bar2")));
        Mockito.verify(loader).load(ArgumentMatchers.eq("qp"));
        Mockito.verifyNoMoreInteractions(loader);
    }
}

