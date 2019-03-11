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
package org.sonar.server.rule.index;


import AnalyzeResponse.AnalyzeToken;
import IndexDefinition.IndexDefinitionContext;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.server.es.DefaultIndexSettingsElement;
import org.sonar.server.es.EsTester;
import org.sonar.server.es.IndexDefinition;
import org.sonar.server.es.NewIndex;


public class RuleIndexDefinitionTest {
    private MapSettings settings = new MapSettings();

    private RuleIndexDefinition underTest = new RuleIndexDefinition(settings.asConfig());

    @Rule
    public EsTester tester = EsTester.create();

    @Test
    public void test_definition_of_index() {
        IndexDefinition.IndexDefinitionContext context = new IndexDefinition.IndexDefinitionContext();
        underTest.define(context);
        assertThat(context.getIndices()).hasSize(1);
        NewIndex ruleIndex = context.getIndices().get("rules");
        assertThat(ruleIndex).isNotNull();
        assertThat(ruleIndex.getTypes().keySet()).containsOnly("activeRule", "ruleExtension", "rule");
        // no cluster by default
        assertThat(ruleIndex.getSettings().get("index.number_of_shards")).isEqualTo("2");
        assertThat(ruleIndex.getSettings().get("index.number_of_replicas")).isEqualTo("0");
    }

    @Test
    public void enable_replica_if_clustering_is_enabled() {
        settings.setProperty(CLUSTER_ENABLED.getKey(), true);
        IndexDefinition.IndexDefinitionContext context = new IndexDefinition.IndexDefinitionContext();
        underTest.define(context);
        NewIndex ruleIndex = context.getIndices().get("rules");
        assertThat(ruleIndex.getSettings().get("index.number_of_replicas")).isEqualTo("1");
    }

    @Test
    public void support_long_html_description() {
        String longText = StringUtils.repeat("The quick brown fox jumps over the lazy dog ", 700);
        List<AnalyzeResponse.AnalyzeToken> tokens = analyzeIndexedTokens(longText);
        assertThat(tokens).extracting(AnalyzeResponse.AnalyzeToken::getTerm).containsOnly("quick", "brown", "fox", "jump", "over", "lazi", "dog");
        // the following method fails if PUT fails
        tester.putDocuments(RuleIndexDefinition.INDEX_TYPE_RULE, new RuleDoc(ImmutableMap.of(RuleIndexDefinition.FIELD_RULE_ID, "123", RuleIndexDefinition.FIELD_RULE_HTML_DESCRIPTION, longText, RuleIndexDefinition.FIELD_RULE_REPOSITORY, "squid", RuleIndexDefinition.FIELD_RULE_KEY, "squid:S001")));
        assertThat(tester.countDocuments(RuleIndexDefinition.INDEX_TYPE_RULE)).isEqualTo(1);
        assertThat(tester.client().prepareSearch(RuleIndexDefinition.INDEX_TYPE_RULE.getIndex()).setQuery(matchQuery(DefaultIndexSettingsElement.ENGLISH_HTML_ANALYZER.subField(RuleIndexDefinition.FIELD_RULE_HTML_DESCRIPTION), "brown fox jumps lazy")).get().getHits().getTotalHits()).isEqualTo(1);
    }

    @Test
    public void remove_html_characters_of_html_description() {
        String text = "<p>html <i>line</i></p>";
        List<AnalyzeResponse.AnalyzeToken> tokens = analyzeIndexedTokens(text);
        assertThat(tokens).extracting("term").containsOnly("html", "line");
    }

    @Test
    public void sanitize_html_description_as_it_is_english() {
        String text = "this is a small list of words";
        // "this", "is", "a" and "of" are not indexed.
        // Plural "words" is converted to singular "word"
        List<AnalyzeResponse.AnalyzeToken> tokens = analyzeIndexedTokens(text);
        assertThat(tokens).extracting("term").containsOnly("small", "list", "word");
    }
}

