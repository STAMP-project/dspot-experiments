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
package org.sonar.server.rule.ws;


import DebtRemediationFunction.Type.CONSTANT_ISSUE;
import DebtRemediationFunction.Type.LINEAR;
import DebtRemediationFunction.Type.LINEAR_OFFSET;
import Rules.Active;
import WebService.Action;
import WebService.Param;
import WebService.Param.FIELDS;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.resources.Languages;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.AlwaysIncreasingSystem2;
import org.sonar.db.DbTester;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.qualityprofile.ActiveRuleParamDto;
import org.sonar.db.qualityprofile.QProfileDto;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.db.rule.RuleMetadataDto;
import org.sonar.db.rule.RuleParamDto;
import org.sonar.db.user.UserDto;
import org.sonar.server.es.EsTester;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.language.LanguageTesting;
import org.sonar.server.organization.DefaultOrganizationProvider;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.qualityprofile.ActiveRuleChange;
import org.sonar.server.qualityprofile.QProfileRules;
import org.sonar.server.qualityprofile.RuleActivation;
import org.sonar.server.qualityprofile.RuleActivator;
import org.sonar.server.qualityprofile.index.ActiveRuleIndexer;
import org.sonar.server.rule.index.RuleIndex;
import org.sonar.server.rule.index.RuleIndexer;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.text.MacroInterpreter;
import org.sonar.server.util.IntegerTypeValidation;
import org.sonar.server.util.StringTypeValidation;
import org.sonar.server.util.TypeValidations;
import org.sonar.server.ws.TestRequest;
import org.sonar.server.ws.WsActionTester;
import org.sonarqube.ws.Common;
import org.sonarqube.ws.Rules;
import org.sonarqube.ws.Rules.SearchResponse;


public class SearchActionTest {
    private static final String JAVA = "java";

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private System2 system2 = new AlwaysIncreasingSystem2();

    @Rule
    public DbTester db = DbTester.create(system2);

    @Rule
    public EsTester es = EsTester.create();

    private DefaultOrganizationProvider defaultOrganizationProvider = TestDefaultOrganizationProvider.from(db);

    private RuleIndex ruleIndex = new RuleIndex(es.client(), system2);

    private RuleIndexer ruleIndexer = new RuleIndexer(es.client(), db.getDbClient());

    private ActiveRuleIndexer activeRuleIndexer = new ActiveRuleIndexer(db.getDbClient(), es.client());

    private Languages languages = LanguageTesting.newLanguages(SearchActionTest.JAVA, "js");

    private ActiveRuleCompleter activeRuleCompleter = new ActiveRuleCompleter(db.getDbClient(), languages);

    private RuleWsSupport wsSupport = new RuleWsSupport(db.getDbClient(), userSession, defaultOrganizationProvider);

    private RuleQueryFactory ruleQueryFactory = new RuleQueryFactory(db.getDbClient(), wsSupport);

    private MacroInterpreter macroInterpreter = Mockito.mock(MacroInterpreter.class);

    private RuleMapper ruleMapper = new RuleMapper(languages, macroInterpreter);

    private SearchAction underTest = new SearchAction(ruleIndex, activeRuleCompleter, ruleQueryFactory, db.getDbClient(), ruleMapper, new RuleWsSupport(db.getDbClient(), userSession, defaultOrganizationProvider));

    private TypeValidations typeValidations = new TypeValidations(Arrays.asList(new StringTypeValidation(), new IntegerTypeValidation()));

    private RuleActivator ruleActivator = new RuleActivator(System2.INSTANCE, db.getDbClient(), typeValidations, userSession);

    private QProfileRules qProfileRules = new org.sonar.server.qualityprofile.QProfileRulesImpl(db.getDbClient(), ruleActivator, ruleIndex, activeRuleIndexer);

    private WsActionTester ws = new WsActionTester(underTest);

    @Test
    public void test_definition() {
        WebService.Action def = ws.getDef();
        assertThat(def.isPost()).isFalse();
        assertThat(def.since()).isEqualTo("4.4");
        assertThat(def.isInternal()).isFalse();
        assertThat(def.responseExampleAsString()).isNotEmpty();
        assertThat(def.params()).hasSize(24);
        WebService.Param compareToProfile = def.param("compareToProfile");
        assertThat(compareToProfile.since()).isEqualTo("6.5");
        assertThat(compareToProfile.isRequired()).isFalse();
        assertThat(compareToProfile.isInternal()).isTrue();
        assertThat(compareToProfile.description()).isEqualTo("Quality profile key to filter rules that are activated. Meant to compare easily to profile set in 'qprofile'");
    }

    @Test
    public void return_empty_result() {
        Rules.SearchResponse response = ws.newRequest().setParam(FIELDS, "actives").executeProtobuf(SearchResponse.class);
        assertThat(response.getTotal()).isEqualTo(0L);
        assertThat(response.getP()).isEqualTo(1);
        assertThat(response.getRulesCount()).isEqualTo(0);
    }

    @Test
    public void return_all_rules() {
        RuleDefinitionDto rule1 = db.rules().insert(( r1) -> r1.setLanguage("java"));
        RuleDefinitionDto rule2 = db.rules().insert(( r1) -> r1.setLanguage("java"));
        indexRules();
        verify(( r) -> {
        }, rule1, rule2);
    }

    @Test
    public void return_note_login() {
        OrganizationDto organization = db.organizations().insert();
        UserDto user1 = db.users().insertUser();
        RuleDefinitionDto rule1 = db.rules().insert();
        db.rules().insertOrUpdateMetadata(rule1, user1, organization);
        UserDto disableUser = db.users().insertDisabledUser();
        RuleDefinitionDto rule2 = db.rules().insert();
        db.rules().insertOrUpdateMetadata(rule2, disableUser, organization);
        indexRules();
        SearchResponse result = ws.newRequest().setParam("f", "noteLogin").setParam("organization", organization.getKey()).executeProtobuf(SearchResponse.class);
        assertThat(result.getRulesList()).extracting(Rule::getKey, Rule::getNoteLogin).containsExactlyInAnyOrder(tuple(rule1.getKey().toString(), user1.getLogin()), tuple(rule2.getKey().toString(), disableUser.getLogin()));
    }

    @Test
    public void filter_by_rule_key() {
        RuleDefinitionDto rule1 = db.rules().insert(( r1) -> r1.setLanguage("java"));
        RuleDefinitionDto rule2 = db.rules().insert(( r1) -> r1.setLanguage("java"));
        indexRules();
        verify(( r) -> r.setParam(PARAM_RULE_KEY, rule1.getKey().toString()), rule1);
        verifyNoResults(( r) -> r.setParam(RulesWsParameters.PARAM_RULE_KEY, "missing"));
    }

    @Test
    public void filter_by_rule_name() {
        RuleDefinitionDto rule1 = db.rules().insert(( r1) -> r1.setName("Best rule ever"));
        RuleDefinitionDto rule2 = db.rules().insert(( r1) -> r1.setName("Some other stuff"));
        indexRules();
        verify(( r) -> r.setParam("q", "Be"), rule1);
        verify(( r) -> r.setParam("q", "Bes"), rule1);
        verify(( r) -> r.setParam("q", "Best"), rule1);
        verify(( r) -> r.setParam("q", "Best "), rule1);
        verify(( r) -> r.setParam("q", "Best rule"), rule1);
        verify(( r) -> r.setParam("q", "Best rule eve"), rule1);
        verify(( r) -> r.setParam("q", "Best rule ever"), rule1);
        verify(( r) -> r.setParam("q", "ru ev"), rule1);
        verify(( r) -> r.setParam("q", "ru ever"), rule1);
        verify(( r) -> r.setParam("q", "ev ve ver ru le"), rule1);
        verify(( r) -> r.setParam("q", "other"), rule2);
    }

    @Test
    public void filter_by_rule_name_requires_all_words_to_match() {
        RuleDefinitionDto rule1 = db.rules().insert(( r1) -> r1.setName("Best rule ever"));
        RuleDefinitionDto rule2 = db.rules().insert(( r1) -> r1.setName("Some other stuff"));
        indexRules();
        verify(( r) -> r.setParam("q", "Best other"));
        verify(( r) -> r.setParam("q", "Best rule"), rule1);
        verify(( r) -> r.setParam("q", "rule ever"), rule1);
    }

    @Test
    public void filter_by_rule_name_does_not_interpret_query() {
        RuleDefinitionDto rule1 = db.rules().insert(( r1) -> r1.setName("Best rule for-ever"));
        RuleDefinitionDto rule2 = db.rules().insert(( r1) -> r1.setName("Some other stuff"));
        indexRules();
        // do not interpret "-" as a "not"
        verify(( r) -> r.setParam("q", "-ever"), rule1);
    }

    @Test
    public void filter_by_rule_description() {
        RuleDefinitionDto rule1 = db.rules().insert(( r1) -> r1.setDescription("This is the <bold>best</bold> rule now&amp;for<b>ever</b>"));
        RuleDefinitionDto rule2 = db.rules().insert(( r1) -> r1.setName("Some other stuff"));
        indexRules();
        verify(( r) -> r.setParam("q", "Best "), rule1);
        verify(( r) -> r.setParam("q", "bold"));
        verify(( r) -> r.setParam("q", "now&forever"), rule1);
    }

    @Test
    public void filter_by_rule_name_or_descriptions_requires_all_words_to_match_anywhere() {
        RuleDefinitionDto rule1 = db.rules().insert(( r1) -> r1.setName("Best rule ever").setDescription("This is a good rule"));
        RuleDefinitionDto rule2 = db.rules().insert(( r1) -> r1.setName("Some other stuff").setDescription("Another thing"));
        indexRules();
        verify(( r) -> r.setParam("q", "Best good"), rule1);
        verify(( r) -> r.setParam("q", "Best Another"));
    }

    @Test
    public void return_all_rule_fields_by_default() {
        RuleDefinitionDto rule = db.rules().insert(( r) -> r.setLanguage("java"));
        indexRules();
        Rules.SearchResponse response = ws.newRequest().executeProtobuf(SearchResponse.class);
        Rules.Rule result = response.getRules(0);
        assertThat(result.getCreatedAt()).isNotEmpty();
        assertThat(result.getEffortToFixDescription()).isNotEmpty();
        assertThat(result.getHtmlDesc()).isNotEmpty();
        assertThat(result.hasIsTemplate()).isTrue();
        assertThat(result.getLang()).isEqualTo(rule.getLanguage());
        assertThat(result.getLangName()).isEqualTo(languages.get(rule.getLanguage()).getName());
        assertThat(result.getName()).isNotEmpty();
        assertThat(result.getRepo()).isNotEmpty();
        assertThat(result.getSeverity()).isNotEmpty();
        assertThat(result.getType().name()).isEqualTo(org.sonar.api.rules.RuleType.valueOf(rule.getType()).name());
    }

    @Test
    public void return_subset_of_fields() {
        RuleDefinitionDto rule = db.rules().insert(( r) -> r.setLanguage("java"));
        indexRules();
        Rules.SearchResponse response = ws.newRequest().setParam(FIELDS, "createdAt,langName").executeProtobuf(SearchResponse.class);
        Rules.Rule result = response.getRules(0);
        // mandatory fields
        assertThat(result.getKey()).isEqualTo(rule.getKey().toString());
        assertThat(result.getType().getNumber()).isEqualTo(rule.getType());
        // selected fields
        assertThat(result.getCreatedAt()).isNotEmpty();
        assertThat(result.getLangName()).isNotEmpty();
        // not returned fields
        assertThat(result.hasEffortToFixDescription()).isFalse();
        assertThat(result.hasHtmlDesc()).isFalse();
        assertThat(result.hasIsTemplate()).isFalse();
        assertThat(result.hasLang()).isFalse();
        assertThat(result.hasName()).isFalse();
        assertThat(result.hasSeverity()).isFalse();
        assertThat(result.hasRepo()).isFalse();
    }

    @Test
    public void should_filter_on_organization_specific_tags() {
        OrganizationDto organization = db.organizations().insert();
        RuleDefinitionDto rule1 = db.rules().insert(( r) -> r.setLanguage("java"));
        RuleMetadataDto metadata1 = insertMetadata(organization, rule1, setTags("tag1", "tag2"));
        RuleDefinitionDto rule2 = db.rules().insert(( r) -> r.setLanguage("java"));
        RuleMetadataDto metadata2 = insertMetadata(organization, rule2);
        indexRules();
        Consumer<TestRequest> request = ( r) -> r.setParam("f", "repo,name").setParam("tags", metadata1.getTags().stream().collect(Collectors.joining(","))).setParam("organization", organization.getKey());
        verify(request, rule1);
    }

    @Test
    public void when_searching_for_several_tags_combine_them_with_OR() {
        OrganizationDto organization = db.organizations().insert();
        RuleDefinitionDto bothTagsRule = db.rules().insert(( r) -> r.setLanguage("java"));
        insertMetadata(organization, bothTagsRule, setTags("tag1", "tag2"));
        RuleDefinitionDto oneTagRule = db.rules().insert(( r) -> r.setLanguage("java"));
        insertMetadata(organization, oneTagRule, setTags("tag1"));
        RuleDefinitionDto otherTagRule = db.rules().insert(( r) -> r.setLanguage("java"));
        insertMetadata(organization, otherTagRule, setTags("tag2"));
        RuleDefinitionDto noTagRule = db.rules().insert(( r) -> r.setLanguage("java"));
        insertMetadata(organization, noTagRule, setTags());
        indexRules();
        Consumer<TestRequest> request = ( r) -> r.setParam("f", "repo,name").setParam("tags", "tag1,tag2").setParam("organization", organization.getKey());
        verify(request, bothTagsRule, oneTagRule, otherTagRule);
    }

    @Test
    public void should_list_tags_in_tags_facet() {
        OrganizationDto organization = db.organizations().insert();
        String[] tags = get101Tags();
        RuleDefinitionDto rule = db.rules().insert(setSystemTags("x"));
        insertMetadata(organization, rule, setTags(tags));
        indexRules();
        SearchResponse result = ws.newRequest().setParam("f", "repo,name").setParam("facets", "tags").setParam("organization", organization.getKey()).executeProtobuf(SearchResponse.class);
        assertThat(result.getFacets().getFacets(0).getValuesList()).extracting(( v) -> v.getVal(), ( v) -> v.getCount()).contains(tuple("tag0", 1L), tuple("tag25", 1L), tuple("tag99", 1L)).doesNotContain(tuple("x", 1L));
    }

    @Test
    public void should_list_tags_ordered_by_count_then_by_name_in_tags_facet() {
        OrganizationDto organization = db.organizations().insert();
        RuleDefinitionDto rule = db.rules().insert(setSystemTags("tag7", "tag5", "tag3", "tag1", "tag9"));
        insertMetadata(organization, rule, setTags("tag2", "tag4", "tag6", "tag8", "tagA"));
        db.rules().insert(setSystemTags("tag2"));
        indexRules();
        SearchResponse result = ws.newRequest().setParam("f", "repo,name").setParam("facets", "tags").setParam("organization", organization.getKey()).executeProtobuf(SearchResponse.class);
        assertThat(result.getFacets().getFacets(0).getValuesList()).extracting(( v) -> v.getVal(), ( v) -> v.getCount()).containsExactly(tuple("tag2", 2L), tuple("tag1", 1L), tuple("tag3", 1L), tuple("tag4", 1L), tuple("tag5", 1L), tuple("tag6", 1L), tuple("tag7", 1L), tuple("tag8", 1L), tuple("tag9", 1L), tuple("tagA", 1L));
    }

    @Test
    public void should_include_selected_matching_tag_in_facet() {
        RuleDefinitionDto rule = db.rules().insert(setSystemTags("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tagA", "x"));
        indexRules();
        SearchResponse result = ws.newRequest().setParam("facets", "tags").setParam("tags", "x").executeProtobuf(SearchResponse.class);
        assertThat(result.getFacets().getFacets(0).getValuesList()).extracting(( v) -> entry(v.getVal(), v.getCount())).contains(entry("x", 1L));
    }

    @Test
    public void should_included_selected_non_matching_tag_in_facet() {
        RuleDefinitionDto rule = db.rules().insert(setSystemTags("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8", "tag9", "tagA"));
        indexRules();
        SearchResponse result = ws.newRequest().setParam("facets", "tags").setParam("tags", "x").executeProtobuf(SearchResponse.class);
        assertThat(result.getFacets().getFacets(0).getValuesList()).extracting(( v) -> entry(v.getVal(), v.getCount())).contains(entry("x", 0L));
    }

    @Test
    public void should_return_organization_specific_tags() {
        OrganizationDto organization = db.organizations().insert();
        RuleDefinitionDto rule = db.rules().insert(( r) -> r.setLanguage("java"));
        RuleMetadataDto metadata = insertMetadata(organization, rule, setTags("tag1", "tag2"));
        indexRules();
        SearchResponse result = ws.newRequest().setParam("f", "tags").setParam("organization", organization.getKey()).executeProtobuf(SearchResponse.class);
        assertThat(result.getRulesList()).extracting(Rule::getKey).containsExactly(rule.getKey().toString());
        assertThat(result.getRulesList()).extracting(Rule::getTags).flatExtracting(Rules.Tags::getTagsList).containsExactly(metadata.getTags().toArray(new String[0]));
    }

    @Test
    public void should_not_return_tags_of_foreign_organization() {
        OrganizationDto organizationWithSpecificTags = db.organizations().insert();
        OrganizationDto myOrganization = db.organizations().insert();
        RuleDefinitionDto rule = db.rules().insert(setSystemTags("system1", "system2"));
        insertMetadata(organizationWithSpecificTags, rule, setTags("tag1", "tag2"));
        indexRules();
        SearchResponse result = ws.newRequest().setParam("facets", "tags").setParam("f", "tags").setParam("organization", myOrganization.getKey()).executeProtobuf(SearchResponse.class);
        assertThat(result.getRulesList()).extracting(Rule::getKey).containsExactly(rule.getKey().toString());
        assertThat(result.getFacets().getFacets(0).getValuesList()).extracting(( v) -> tuple(v.getVal(), v.getCount())).containsExactly(tuple("system1", 1L), tuple("system2", 1L));
    }

    @Test
    public void should_return_specified_fields() {
        RuleDefinitionDto rule = db.rules().insert(( r1) -> r1.setLanguage("java"));
        indexRules();
        checkField(rule, "repo", Rule::getRepo, rule.getRepositoryKey());
        checkField(rule, "name", Rule::getName, rule.getName());
        checkField(rule, "severity", Rule::getSeverity, rule.getSeverityString());
        checkField(rule, "status", ( r) -> r.getStatus().toString(), rule.getStatus().toString());
        checkField(rule, "internalKey", Rule::getInternalKey, rule.getConfigKey());
        checkField(rule, "isTemplate", Rule::getIsTemplate, rule.isTemplate());
        checkField(rule, "sysTags", ( r) -> r.getSysTags().getSysTagsList().stream().collect(Collectors.joining(",")), rule.getSystemTags().stream().collect(Collectors.joining(",")));
        checkField(rule, "lang", Rule::getLang, rule.getLanguage());
        checkField(rule, "langName", Rule::getLangName, languages.get(rule.getLanguage()).getName());
        checkField(rule, "gapDescription", Rule::getGapDescription, rule.getGapDescription());
        // to be continued...
    }

    @Test
    public void return_lang_key_field_when_language_name_is_not_available() {
        OrganizationDto organization = db.organizations().insert();
        String unknownLanguage = "unknown_" + (randomAlphanumeric(5));
        RuleDefinitionDto rule = db.rules().insert(( r) -> r.setLanguage(unknownLanguage));
        indexRules();
        SearchResponse result = ws.newRequest().setParam("f", "langName").setParam("organization", organization.getKey()).executeProtobuf(SearchResponse.class);
        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getRulesCount()).isEqualTo(1);
        org.sonarqube.ws.Rules.Rule searchedRule = result.getRules(0);
        assertThat(searchedRule).isNotNull();
        assertThat(searchedRule.getLangName()).isEqualTo(unknownLanguage);
    }

    @Test
    public void search_debt_rules_with_default_and_overridden_debt_values() {
        RuleDefinitionDto rule = db.rules().insert(( r) -> r.setLanguage("java").setDefRemediationFunction(DebtRemediationFunction.Type.LINEAR_OFFSET.name()).setDefRemediationGapMultiplier("1h").setDefRemediationBaseEffort("15min"));
        RuleMetadataDto metadata = insertMetadata(db.getDefaultOrganization(), rule, ( r) -> r.setRemediationFunction(DebtRemediationFunction.Type.LINEAR_OFFSET.name()).setRemediationGapMultiplier("2h").setRemediationBaseEffort("25min"));
        indexRules();
        SearchResponse result = ws.newRequest().setParam("f", "debtRemFn,debtOverloaded,defaultDebtRemFn").executeProtobuf(SearchResponse.class);
        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getRulesCount()).isEqualTo(1);
        org.sonarqube.ws.Rules.Rule searchedRule = result.getRules(0);
        assertThat(searchedRule).isNotNull();
        assertThat(searchedRule.getDefaultDebtRemFnCoeff()).isEqualTo("1h");
        assertThat(searchedRule.getDefaultDebtRemFnOffset()).isEqualTo("15min");
        assertThat(searchedRule.getDefaultDebtRemFnType()).isEqualTo(LINEAR_OFFSET.name());
        assertThat(searchedRule.getDefaultRemFnBaseEffort()).isEqualTo("15min");
        assertThat(searchedRule.getDefaultRemFnGapMultiplier()).isEqualTo("1h");
        assertThat(searchedRule.getDefaultRemFnType()).isEqualTo(LINEAR_OFFSET.name());
        assertThat(searchedRule.getDebtOverloaded()).isTrue();
        assertThat(searchedRule.getDebtRemFnCoeff()).isEqualTo("2h");
        assertThat(searchedRule.getDebtRemFnOffset()).isEqualTo("25min");
        assertThat(searchedRule.getDebtRemFnType()).isEqualTo(LINEAR_OFFSET.name());
    }

    @Test
    public void search_debt_rules_with_default_linear_offset_and_overridden_constant_debt() {
        RuleDefinitionDto rule = db.rules().insert(( r) -> r.setLanguage("java").setDefRemediationFunction(DebtRemediationFunction.Type.LINEAR_OFFSET.name()).setDefRemediationGapMultiplier("1h").setDefRemediationBaseEffort("15min"));
        RuleMetadataDto metadata = insertMetadata(db.getDefaultOrganization(), rule, ( r) -> r.setRemediationFunction(DebtRemediationFunction.Type.CONSTANT_ISSUE.name()).setRemediationGapMultiplier(null).setRemediationBaseEffort("5min"));
        indexRules();
        SearchResponse result = ws.newRequest().setParam("f", "debtRemFn,debtOverloaded,defaultDebtRemFn").executeProtobuf(SearchResponse.class);
        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getRulesCount()).isEqualTo(1);
        org.sonarqube.ws.Rules.Rule searchedRule = result.getRules(0);
        assertThat(searchedRule).isNotNull();
        assertThat(searchedRule.getDefaultDebtRemFnCoeff()).isEqualTo("1h");
        assertThat(searchedRule.getDefaultDebtRemFnOffset()).isEqualTo("15min");
        assertThat(searchedRule.getDefaultDebtRemFnType()).isEqualTo(LINEAR_OFFSET.name());
        assertThat(searchedRule.getDefaultRemFnBaseEffort()).isEqualTo("15min");
        assertThat(searchedRule.getDefaultRemFnGapMultiplier()).isEqualTo("1h");
        assertThat(searchedRule.getDefaultRemFnType()).isEqualTo(LINEAR_OFFSET.name());
        assertThat(searchedRule.getDebtOverloaded()).isTrue();
        assertThat(searchedRule.getDebtRemFnCoeff()).isEmpty();
        assertThat(searchedRule.getDebtRemFnOffset()).isEqualTo("5min");
        assertThat(searchedRule.getDebtRemFnType()).isEqualTo(CONSTANT_ISSUE.name());
    }

    @Test
    public void search_debt_rules_with_default_linear_offset_and_overridden_linear_debt() {
        RuleDefinitionDto rule = db.rules().insert(( r) -> r.setLanguage("java").setDefRemediationFunction(DebtRemediationFunction.Type.LINEAR_OFFSET.name()).setDefRemediationGapMultiplier("1h").setDefRemediationBaseEffort("15min"));
        RuleMetadataDto metadata = insertMetadata(db.getDefaultOrganization(), rule, ( r) -> r.setRemediationFunction(DebtRemediationFunction.Type.LINEAR.name()).setRemediationGapMultiplier("1h").setRemediationBaseEffort(null));
        indexRules();
        SearchResponse result = ws.newRequest().setParam("f", "debtRemFn,debtOverloaded,defaultDebtRemFn").executeProtobuf(SearchResponse.class);
        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getRulesCount()).isEqualTo(1);
        org.sonarqube.ws.Rules.Rule searchedRule = result.getRules(0);
        assertThat(searchedRule).isNotNull();
        assertThat(searchedRule.getDefaultDebtRemFnCoeff()).isEqualTo("1h");
        assertThat(searchedRule.getDefaultDebtRemFnOffset()).isEqualTo("15min");
        assertThat(searchedRule.getDefaultDebtRemFnType()).isEqualTo(LINEAR_OFFSET.name());
        assertThat(searchedRule.getDefaultRemFnBaseEffort()).isEqualTo("15min");
        assertThat(searchedRule.getDefaultRemFnGapMultiplier()).isEqualTo("1h");
        assertThat(searchedRule.getDefaultRemFnType()).isEqualTo(LINEAR_OFFSET.name());
        assertThat(searchedRule.getDebtOverloaded()).isTrue();
        assertThat(searchedRule.getDebtRemFnCoeff()).isEqualTo("1h");
        assertThat(searchedRule.getDebtRemFnOffset()).isEmpty();
        assertThat(searchedRule.getDebtRemFnType()).isEqualTo(LINEAR.name());
    }

    @Test
    public void search_template_rules() {
        RuleDefinitionDto templateRule = db.rules().insert(( r) -> r.setLanguage("java").setIsTemplate(true));
        RuleDefinitionDto rule = db.rules().insert(( r) -> r.setLanguage("java").setTemplateId(templateRule.getId()));
        indexRules();
        SearchResponse result = ws.newRequest().setParam("f", "isTemplate").setParam("is_template", "true").executeProtobuf(SearchResponse.class);
        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getRulesCount()).isEqualTo(1);
        org.sonarqube.ws.Rules.Rule searchedRule = result.getRules(0);
        assertThat(searchedRule).isNotNull();
        assertThat(searchedRule.getIsTemplate()).isTrue();
        assertThat(searchedRule.getKey()).isEqualTo((((templateRule.getRepositoryKey()) + ":") + (templateRule.getRuleKey())));
    }

    @Test
    public void search_custom_rules_from_template_key() {
        RuleDefinitionDto templateRule = db.rules().insert(( r) -> r.setLanguage("java").setIsTemplate(true));
        RuleDefinitionDto rule = db.rules().insert(( r) -> r.setLanguage("java").setTemplateId(templateRule.getId()));
        indexRules();
        SearchResponse result = ws.newRequest().setParam("f", "templateKey").setParam("template_key", (((templateRule.getRepositoryKey()) + ":") + (templateRule.getRuleKey()))).executeProtobuf(SearchResponse.class);
        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getRulesCount()).isEqualTo(1);
        org.sonarqube.ws.Rules.Rule searchedRule = result.getRules(0);
        assertThat(searchedRule).isNotNull();
        assertThat(searchedRule.getKey()).isEqualTo((((rule.getRepositoryKey()) + ":") + (rule.getRuleKey())));
        assertThat(searchedRule.getTemplateKey()).isEqualTo((((templateRule.getRepositoryKey()) + ":") + (templateRule.getRuleKey())));
    }

    @Test
    public void do_not_return_external_rule() {
        db.rules().insert(( r) -> r.setIsExternal(true));
        indexRules();
        SearchResponse result = ws.newRequest().executeProtobuf(SearchResponse.class);
        assertThat(result.getTotal()).isZero();
        assertThat(result.getRulesCount()).isZero();
    }

    @Test
    public void search_all_active_rules() {
        OrganizationDto organization = db.organizations().insert();
        QProfileDto profile = db.qualityProfiles().insert(organization, ( p) -> p.setLanguage("java"));
        RuleDefinitionDto rule = db.rules().insert(( r) -> r.setLanguage("java"));
        RuleActivation activation = RuleActivation.create(rule.getId(), BLOCKER, null);
        qProfileRules.activateAndCommit(db.getSession(), profile, Collections.singleton(activation));
        indexRules();
        SearchResponse result = ws.newRequest().setParam("q", rule.getName()).setParam("activation", "true").setParam("organization", organization.getKey()).executeProtobuf(SearchResponse.class);
        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getRulesCount()).isEqualTo(1);
        org.sonarqube.ws.Rules.Rule searchedRule = result.getRules(0);
        assertThat(searchedRule).isNotNull();
        assertThat(searchedRule.getKey()).isEqualTo((((rule.getRepositoryKey()) + ":") + (rule.getRuleKey())));
        assertThat(searchedRule.getName()).isEqualTo(rule.getName());
    }

    @Test
    public void search_profile_active_rules() {
        OrganizationDto organization = db.organizations().insert();
        QProfileDto profile = db.qualityProfiles().insert(organization, ( p) -> p.setLanguage("java"));
        QProfileDto waterproofProfile = db.qualityProfiles().insert(organization, ( p) -> p.setLanguage("java"));
        RuleDefinitionDto rule = db.rules().insert(( r) -> r.setLanguage("java"));
        RuleParamDto ruleParam1 = db.rules().insertRuleParam(rule, ( p) -> p.setDefaultValue("some value").setType("STRING").setDescription("My small description").setName("my_var"));
        RuleParamDto ruleParam2 = db.rules().insertRuleParam(rule, ( p) -> p.setDefaultValue("1").setType("INTEGER").setDescription("My small description").setName("the_var"));
        // SONAR-7083
        RuleParamDto ruleParam3 = db.rules().insertRuleParam(rule, ( p) -> p.setDefaultValue(null).setType("STRING").setDescription("Empty Param").setName("empty_var"));
        RuleActivation activation = RuleActivation.create(rule.getId());
        List<ActiveRuleChange> activeRuleChanges1 = qProfileRules.activateAndCommit(db.getSession(), profile, Collections.singleton(activation));
        qProfileRules.activateAndCommit(db.getSession(), waterproofProfile, Collections.singleton(activation));
        assertThat(activeRuleChanges1).hasSize(1);
        indexRules();
        indexActiveRules();
        SearchResponse result = ws.newRequest().setParam("f", "actives").setParam("q", rule.getName()).setParam("organization", organization.getKey()).setParam("activation", "true").setParam("qprofile", profile.getKee()).executeProtobuf(SearchResponse.class);
        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getRulesCount()).isEqualTo(1);
        assertThat(result.getActives()).isNotNull();
        assertThat(result.getActives().getActives().get(rule.getKey().toString())).isNotNull();
        assertThat(result.getActives().getActives().get(rule.getKey().toString()).getActiveListList()).hasSize(1);
        // The rule without value is not inserted
        Rules.Active activeList = result.getActives().getActives().get(rule.getKey().toString()).getActiveList(0);
        assertThat(activeList.getParamsCount()).isEqualTo(2);
        assertThat(activeList.getParamsList()).extracting("key", "value").containsExactlyInAnyOrder(tuple(ruleParam1.getName(), ruleParam1.getDefaultValue()), tuple(ruleParam2.getName(), ruleParam2.getDefaultValue()));
        String unknownProfile = "unknown_profile" + (randomAlphanumeric(5));
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage((("The specified qualityProfile '" + unknownProfile) + "' does not exist"));
        ws.newRequest().setParam("activation", "true").setParam("qprofile", unknownProfile).executeProtobuf(SearchResponse.class);
    }

    @Test
    public void search_for_active_rules_when_parameter_value_is_null() {
        OrganizationDto organization = db.organizations().insert();
        QProfileDto profile = db.qualityProfiles().insert(organization, ( p) -> p.setLanguage("java"));
        RuleDefinitionDto rule = db.rules().insert(( r) -> r.setLanguage("java"));
        RuleParamDto ruleParam = db.rules().insertRuleParam(rule, ( p) -> p.setDefaultValue("some value").setType("STRING").setDescription("My small description").setName("my_var"));
        RuleActivation activation = RuleActivation.create(rule.getId());
        List<ActiveRuleChange> activeRuleChanges = qProfileRules.activateAndCommit(db.getSession(), profile, Collections.singleton(activation));
        // Insert directly in database a rule parameter with a null value
        ActiveRuleParamDto activeRuleParam = ActiveRuleParamDto.createFor(ruleParam).setValue(null);
        db.getDbClient().activeRuleDao().insertParam(db.getSession(), activeRuleChanges.get(0).getActiveRule(), activeRuleParam);
        db.commit();
        indexRules();
        indexActiveRules();
        SearchResponse result = ws.newRequest().setParam("f", "actives").setParam("q", rule.getName()).setParam("organization", organization.getKey()).setParam("activation", "true").setParam("qprofile", profile.getKee()).executeProtobuf(SearchResponse.class);
        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getRulesCount()).isEqualTo(1);
        assertThat(result.getActives()).isNotNull();
        assertThat(result.getActives().getActives().get(rule.getKey().toString())).isNotNull();
        assertThat(result.getActives().getActives().get(rule.getKey().toString()).getActiveListList()).hasSize(1);
        Rules.Active activeList = result.getActives().getActives().get(rule.getKey().toString()).getActiveList(0);
        assertThat(activeList.getParamsCount()).isEqualTo(2);
        assertThat(activeList.getParamsList()).extracting("key", "value").containsExactlyInAnyOrder(tuple(ruleParam.getName(), ruleParam.getDefaultValue()), tuple(activeRuleParam.getKey(), ""));
    }

    /**
     * When the user searches for inactive rules (for example for to "activate more"), then
     * only rules of the quality profiles' language are relevant
     */
    @Test
    public void facet_filtering_when_searching_for_inactive_rules() {
        OrganizationDto organization = db.organizations().insert();
        QProfileDto profile = db.qualityProfiles().insert(organization, ( q) -> q.setLanguage("language1"));
        // on same language, not activated => match
        RuleDefinitionDto rule1 = db.rules().insert(( r) -> r.setLanguage(profile.getLanguage()).setRepositoryKey("repositoryKey1").setSystemTags(new HashSet<>(singletonList("tag1"))).setSeverity("CRITICAL").setStatus(RuleStatus.BETA).setType(RuleType.CODE_SMELL));
        // on same language, activated => no match
        RuleDefinitionDto rule2 = db.rules().insert(( r) -> r.setLanguage(profile.getLanguage()).setRepositoryKey("repositoryKey2").setSystemTags(new HashSet<>(singletonList("tag2"))).setSeverity("MAJOR").setStatus(RuleStatus.DEPRECATED).setType(RuleType.VULNERABILITY));
        RuleActivation activation = RuleActivation.create(rule2.getId(), null, null);
        qProfileRules.activateAndCommit(db.getSession(), profile, Collections.singleton(activation));
        // on other language, not activated => no match
        RuleDefinitionDto rule3 = db.rules().insert(( r) -> r.setLanguage("language3").setRepositoryKey("repositoryKey3").setSystemTags(new HashSet<>(singletonList("tag3"))).setSeverity("BLOCKER").setStatus(RuleStatus.READY).setType(RuleType.BUG));
        indexRules();
        indexActiveRules();
        SearchResponse result = ws.newRequest().setParam("facets", "languages,repositories,tags,severities,statuses,types").setParam("organization", organization.getKey()).setParam("activation", "false").setParam("qprofile", profile.getKee()).executeProtobuf(SearchResponse.class);
        assertThat(result.getRulesList()).extracting(Rule::getKey).containsExactlyInAnyOrder(rule1.getKey().toString());
        // known limitation: irrelevant languages are shown in this case (SONAR-9683)
        assertThat(result.getFacets().getFacetsList().stream().filter(( f) -> "languages".equals(f.getProperty())).findAny().get().getValuesList()).extracting(Common.FacetValue::getVal, Common.FacetValue::getCount).as("Facet languages").containsExactlyInAnyOrder(tuple(rule1.getLanguage(), 1L), tuple(rule3.getLanguage(), 1L));
        assertThat(result.getFacets().getFacetsList().stream().filter(( f) -> "tags".equals(f.getProperty())).findAny().get().getValuesList()).extracting(Common.FacetValue::getVal, Common.FacetValue::getCount).as("Facet tags").containsExactlyInAnyOrder(tuple(rule1.getSystemTags().iterator().next(), 1L));
        assertThat(result.getFacets().getFacetsList().stream().filter(( f) -> "repositories".equals(f.getProperty())).findAny().get().getValuesList()).extracting(Common.FacetValue::getVal, Common.FacetValue::getCount).as("Facet repositories").containsExactlyInAnyOrder(tuple(rule1.getRepositoryKey(), 1L));
        assertThat(result.getFacets().getFacetsList().stream().filter(( f) -> "severities".equals(f.getProperty())).findAny().get().getValuesList()).extracting(Common.FacetValue::getVal, Common.FacetValue::getCount).as("Facet severities").containsExactlyInAnyOrder(/* rule2 */
        tuple("BLOCKER", 0L), /* rule1 */
        tuple("CRITICAL", 1L), tuple("MAJOR", 0L), tuple("MINOR", 0L), tuple("INFO", 0L));
        assertThat(result.getFacets().getFacetsList().stream().filter(( f) -> "statuses".equals(f.getProperty())).findAny().get().getValuesList()).extracting(Common.FacetValue::getVal, Common.FacetValue::getCount).as("Facet statuses").containsExactlyInAnyOrder(/* rule2 */
        tuple("READY", 0L), /* rule1 */
        tuple("BETA", 1L), tuple("DEPRECATED", 0L));
        assertThat(result.getFacets().getFacetsList().stream().filter(( f) -> "types".equals(f.getProperty())).findAny().get().getValuesList()).extracting(Common.FacetValue::getVal, Common.FacetValue::getCount).as("Facet types").containsExactlyInAnyOrder(/* rule2 */
        tuple("BUG", 0L), /* rule1 */
        tuple("CODE_SMELL", 1L), tuple("VULNERABILITY", 0L), tuple("SECURITY_HOTSPOT", 0L));
    }

    @Test
    public void statuses_facet_should_be_sticky() {
        RuleDefinitionDto rule1 = db.rules().insert(( r) -> r.setLanguage("java"));
        RuleDefinitionDto rule2 = db.rules().insert(( r) -> r.setLanguage("java").setStatus(RuleStatus.BETA));
        RuleDefinitionDto rule3 = db.rules().insert(( r) -> r.setLanguage("java").setStatus(RuleStatus.DEPRECATED));
        indexRules();
        SearchResponse result = ws.newRequest().setParam("f", "status").setParam("status", "DEPRECATED").executeProtobuf(SearchResponse.class);
        assertThat(result.getRulesCount()).isEqualTo(3);
        assertThat(result.getRulesList()).extracting("key", "status.name").containsExactlyInAnyOrder(tuple(rule1.getKey().toString(), rule1.getStatus().name()), tuple(rule2.getKey().toString(), rule2.getStatus().name()), tuple(rule3.getKey().toString(), rule3.getStatus().name()));
    }

    @Test
    public void compare_to_another_profile() {
        OrganizationDto organization = db.organizations().insert();
        QProfileDto profile = db.qualityProfiles().insert(organization, ( p) -> p.setLanguage(JAVA));
        QProfileDto anotherProfile = db.qualityProfiles().insert(organization, ( p) -> p.setLanguage(JAVA));
        RuleDefinitionDto commonRule = db.rules().insertRule(( r) -> r.setLanguage(JAVA)).getDefinition();
        RuleDefinitionDto profileRule1 = db.rules().insertRule(( r) -> r.setLanguage(JAVA)).getDefinition();
        RuleDefinitionDto profileRule2 = db.rules().insertRule(( r) -> r.setLanguage(JAVA)).getDefinition();
        RuleDefinitionDto profileRule3 = db.rules().insertRule(( r) -> r.setLanguage(JAVA)).getDefinition();
        RuleDefinitionDto anotherProfileRule1 = db.rules().insertRule(( r) -> r.setLanguage(JAVA)).getDefinition();
        RuleDefinitionDto anotherProfileRule2 = db.rules().insertRule(( r) -> r.setLanguage(JAVA)).getDefinition();
        db.qualityProfiles().activateRule(profile, commonRule);
        db.qualityProfiles().activateRule(profile, profileRule1);
        db.qualityProfiles().activateRule(profile, profileRule2);
        db.qualityProfiles().activateRule(profile, profileRule3);
        db.qualityProfiles().activateRule(anotherProfile, commonRule);
        db.qualityProfiles().activateRule(anotherProfile, anotherProfileRule1);
        db.qualityProfiles().activateRule(anotherProfile, anotherProfileRule2);
        indexRules();
        indexActiveRules();
        SearchResponse result = ws.newRequest().setParam(RulesWsParameters.PARAM_ORGANIZATION, organization.getKey()).setParam(RulesWsParameters.PARAM_QPROFILE, profile.getKee()).setParam(RulesWsParameters.PARAM_ACTIVATION, "false").setParam(RulesWsParameters.PARAM_COMPARE_TO_PROFILE, anotherProfile.getKee()).executeProtobuf(SearchResponse.class);
        assertThat(result.getRulesList()).extracting(Rule::getKey).containsExactlyInAnyOrder(anotherProfileRule1.getKey().toString(), anotherProfileRule2.getKey().toString());
    }

    @Test
    public void active_rules_are_returned_when_member_of_paid_organization() {
        OrganizationDto organization = db.organizations().insert(( o) -> o.setSubscription(PAID));
        QProfileDto profile = db.qualityProfiles().insert(organization, ( p) -> p.setLanguage("java"));
        RuleDefinitionDto rule = db.rules().insert(( r) -> r.setLanguage("java"));
        RuleActivation activation = RuleActivation.create(rule.getId(), BLOCKER, null);
        qProfileRules.activateAndCommit(db.getSession(), profile, Collections.singleton(activation));
        userSession.logIn(db.users().insertUser()).addMembership(organization);
        indexRules();
        SearchResponse result = ws.newRequest().setParam(FIELDS, "actives").setParam(RulesWsParameters.PARAM_ORGANIZATION, organization.getKey()).executeProtobuf(SearchResponse.class);
        assertThat(result.getRulesCount()).isEqualTo(1);
        assertThat(result.getActives().getActivesMap()).isNotEmpty();
    }

    @Test
    public void active_rules_are_not_returned_when_not_member_of_paid_organization() {
        OrganizationDto organization = db.organizations().insert(( o) -> o.setSubscription(PAID));
        QProfileDto profile = db.qualityProfiles().insert(organization, ( p) -> p.setLanguage("java"));
        RuleDefinitionDto rule = db.rules().insert(( r) -> r.setLanguage("java"));
        RuleActivation activation = RuleActivation.create(rule.getId(), BLOCKER, null);
        qProfileRules.activateAndCommit(db.getSession(), profile, Collections.singleton(activation));
        indexRules();
        SearchResponse result = ws.newRequest().setParam(FIELDS, "actives").setParam(RulesWsParameters.PARAM_ORGANIZATION, organization.getKey()).executeProtobuf(SearchResponse.class);
        assertThat(result.getRulesCount()).isEqualTo(1);
        assertThat(result.getActives().getActivesMap()).isEmpty();
    }

    @Test
    public void search_for_active_rules_when_member_of_paid_organization() {
        OrganizationDto organization = db.organizations().insert(( o) -> o.setSubscription(PAID));
        QProfileDto profile = db.qualityProfiles().insert(organization, ( p) -> p.setLanguage("java"));
        // Rule1 is activated on profile
        RuleDefinitionDto rule1 = db.rules().insert(( r) -> r.setLanguage("java"));
        RuleActivation activation = RuleActivation.create(rule1.getId(), BLOCKER, null);
        qProfileRules.activateAndCommit(db.getSession(), profile, Collections.singleton(activation));
        // Rule2 is not activated
        RuleDefinitionDto rule2 = db.rules().insert(( r) -> r.setLanguage("java"));
        userSession.logIn(db.users().insertUser()).addMembership(organization);
        indexRules();
        SearchResponse result = ws.newRequest().setParam(FIELDS, "actives").setParam(RulesWsParameters.PARAM_QPROFILE, profile.getKee()).setParam(RulesWsParameters.PARAM_ACTIVATION, "true").setParam(RulesWsParameters.PARAM_ORGANIZATION, organization.getKey()).executeProtobuf(SearchResponse.class);
        assertThat(result.getRulesList()).extracting(Rule::getKey).containsExactlyInAnyOrder(rule1.getKey().toString());
    }

    @Test
    public void search_for_active_rules_is_ignored_when_not_member_of_paid_organization() {
        OrganizationDto organization = db.organizations().insert(( o) -> o.setSubscription(PAID));
        QProfileDto profile = db.qualityProfiles().insert(organization, ( p) -> p.setLanguage("java"));
        // Rule1 is activated on profile
        RuleDefinitionDto rule1 = db.rules().insert(( r) -> r.setLanguage("java"));
        RuleActivation activation = RuleActivation.create(rule1.getId(), BLOCKER, null);
        qProfileRules.activateAndCommit(db.getSession(), profile, Collections.singleton(activation));
        // Rule2 is not activated
        RuleDefinitionDto rule2 = db.rules().insert(( r) -> r.setLanguage("java"));
        indexRules();
        SearchResponse result = ws.newRequest().setParam(FIELDS, "actives").setParam(RulesWsParameters.PARAM_QPROFILE, profile.getKee()).setParam(RulesWsParameters.PARAM_ACTIVATION, "true").setParam(RulesWsParameters.PARAM_ORGANIZATION, organization.getKey()).executeProtobuf(SearchResponse.class);
        // the 2 rules are returned as filter on profile is ignored
        assertThat(result.getRulesList()).extracting(Rule::getKey).containsExactlyInAnyOrder(rule1.getKey().toString(), rule2.getKey().toString());
    }
}

