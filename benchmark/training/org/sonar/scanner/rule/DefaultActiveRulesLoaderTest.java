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


import java.io.IOException;
import java.util.Collection;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rule.Severity;
import org.sonar.scanner.WsTestUtil;
import org.sonar.scanner.bootstrap.ScannerWsClient;
import org.sonar.scanner.scan.branch.BranchConfiguration;


public class DefaultActiveRulesLoaderTest {
    private static final int PAGE_SIZE_1 = 150;

    private static final int PAGE_SIZE_2 = 76;

    private static final RuleKey EXAMPLE_KEY = RuleKey.of("squid", "S108");

    private static final String FORMAT_KEY = "format";

    private static final String FORMAT_VALUE = "^[a-z][a-zA-Z0-9]*$";

    private static final String SEVERITY_VALUE = Severity.MINOR;

    private DefaultActiveRulesLoader loader;

    private ScannerWsClient wsClient;

    private BranchConfiguration branchConfig;

    @Test
    public void feed_real_response_encode_qp() throws IOException {
        int total = (DefaultActiveRulesLoaderTest.PAGE_SIZE_1) + (DefaultActiveRulesLoaderTest.PAGE_SIZE_2);
        WsTestUtil.mockStream(wsClient, urlOfPage(1, false), responseOfSize(DefaultActiveRulesLoaderTest.PAGE_SIZE_1, total));
        WsTestUtil.mockStream(wsClient, urlOfPage(2, false), responseOfSize(DefaultActiveRulesLoaderTest.PAGE_SIZE_2, total));
        Collection<LoadedActiveRule> activeRules = loader.load("c+-test_c+-values-17445");
        assertThat(activeRules).hasSize(total);
        assertThat(activeRules).filteredOn(( r) -> r.getRuleKey().equals(EXAMPLE_KEY)).extracting(LoadedActiveRule::getParams).extracting(( p) -> p.get(FORMAT_KEY)).containsExactly(DefaultActiveRulesLoaderTest.FORMAT_VALUE);
        assertThat(activeRules).filteredOn(( r) -> r.getRuleKey().equals(EXAMPLE_KEY)).extracting(LoadedActiveRule::getSeverity).containsExactly(DefaultActiveRulesLoaderTest.SEVERITY_VALUE);
        WsTestUtil.verifyCall(wsClient, urlOfPage(1, false));
        WsTestUtil.verifyCall(wsClient, urlOfPage(2, false));
        Mockito.verifyNoMoreInteractions(wsClient);
    }

    @Test
    public void no_hotspots_on_pr_or_short_branches() throws IOException {
        Mockito.when(branchConfig.isShortOrPullRequest()).thenReturn(true);
        int total = (DefaultActiveRulesLoaderTest.PAGE_SIZE_1) + (DefaultActiveRulesLoaderTest.PAGE_SIZE_2);
        WsTestUtil.mockStream(wsClient, urlOfPage(1, true), responseOfSize(DefaultActiveRulesLoaderTest.PAGE_SIZE_1, total));
        WsTestUtil.mockStream(wsClient, urlOfPage(2, true), responseOfSize(DefaultActiveRulesLoaderTest.PAGE_SIZE_2, total));
        Collection<LoadedActiveRule> activeRules = loader.load("c+-test_c+-values-17445");
        assertThat(activeRules).hasSize(total);
        assertThat(activeRules).filteredOn(( r) -> r.getRuleKey().equals(EXAMPLE_KEY)).extracting(LoadedActiveRule::getParams).extracting(( p) -> p.get(FORMAT_KEY)).containsExactly(DefaultActiveRulesLoaderTest.FORMAT_VALUE);
        assertThat(activeRules).filteredOn(( r) -> r.getRuleKey().equals(EXAMPLE_KEY)).extracting(LoadedActiveRule::getSeverity).containsExactly(DefaultActiveRulesLoaderTest.SEVERITY_VALUE);
        WsTestUtil.verifyCall(wsClient, urlOfPage(1, true));
        WsTestUtil.verifyCall(wsClient, urlOfPage(2, true));
        Mockito.verifyNoMoreInteractions(wsClient);
    }
}

