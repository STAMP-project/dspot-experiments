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
package org.sonar.server.component.ws;


import MediaTypes.JSON;
import Qualifiers.UNIT_TEST_FILE;
import SuggestionCategory.APP;
import SuggestionCategory.SUBVIEW;
import SuggestionCategory.VIEW;
import System2.INSTANCE;
import WebService.Action;
import WebService.Param;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.assertj.core.groups.Tuple;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.utils.System2;
import org.sonar.api.web.UserRole;
import org.sonar.core.util.stream.MoreCollectors;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.component.ResourceTypesRule;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.server.component.index.ComponentIndex;
import org.sonar.server.component.index.ComponentIndexer;
import org.sonar.server.es.EsTester;
import org.sonar.server.favorite.FavoriteFinder;
import org.sonar.server.permission.index.PermissionIndexerTester;
import org.sonar.server.permission.index.WebAuthorizationTypeSupport;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.TestResponse;
import org.sonar.server.ws.WsActionTester;
import org.sonarqube.ws.Components.SuggestionsWsResponse;
import org.sonarqube.ws.Components.SuggestionsWsResponse.Suggestion;


public class SuggestionsActionTest {
    private static final String[] SUGGESTION_QUALIFIERS = Stream.of(SuggestionCategory.values()).map(SuggestionCategory::getQualifier).collect(MoreCollectors.toList()).toArray(new String[0]);

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public UserSessionRule userSessionRule = UserSessionRule.standalone();

    public ResourceTypesRule resourceTypes = new ResourceTypesRule();

    private ComponentIndexer componentIndexer = new ComponentIndexer(db.getDbClient(), es.client());

    private FavoriteFinder favoriteFinder = Mockito.mock(FavoriteFinder.class);

    private ComponentIndex index = new ComponentIndex(es.client(), new WebAuthorizationTypeSupport(userSessionRule), System2.INSTANCE);

    private SuggestionsAction underTest = new SuggestionsAction(db.getDbClient(), index, favoriteFinder, userSessionRule, resourceTypes);

    private OrganizationDto organization;

    private PermissionIndexerTester authorizationIndexerTester = new PermissionIndexerTester(es, componentIndexer);

    private WsActionTester ws = new WsActionTester(underTest);

    @Test
    public void define_suggestions_action() {
        WebService.Action action = ws.getDef();
        assertThat(action).isNotNull();
        assertThat(action.isInternal()).isTrue();
        assertThat(action.isPost()).isFalse();
        assertThat(action.handler()).isNotNull();
        assertThat(action.responseExampleAsString()).isNotEmpty();
        assertThat(action.params()).extracting(WebService.Param::key).containsExactlyInAnyOrder(SuggestionsAction.PARAM_MORE, SuggestionsAction.PARAM_QUERY, SuggestionsAction.PARAM_RECENTLY_BROWSED);
        assertThat(action.changelog()).extracting(Change::getVersion, Change::getDescription).containsExactlyInAnyOrder(tuple("7.6", "The use of 'BRC' as value for parameter 'more' is deprecated"), tuple("6.4", "Parameter 's' is optional"));
        WebService.Param recentlyBrowsed = action.param(SuggestionsAction.PARAM_RECENTLY_BROWSED);
        assertThat(recentlyBrowsed.since()).isEqualTo("6.4");
        assertThat(recentlyBrowsed.exampleValue()).isNotEmpty();
        assertThat(recentlyBrowsed.description()).isNotEmpty();
        assertThat(recentlyBrowsed.isRequired()).isFalse();
        WebService.Param query = action.param(SuggestionsAction.PARAM_QUERY);
        assertThat(query.exampleValue()).isNotEmpty();
        assertThat(query.description()).isNotEmpty();
        assertThat(query.isRequired()).isFalse();
    }

    @Test
    public void test_example_json_response() {
        OrganizationDto organization = db.organizations().insert(( o) -> o.setKey("default-organization").setName("Default Organization"));
        ComponentDto project1 = db.components().insertPublicProject(organization, ( p) -> p.setDbKey("org.sonarsource:sonarqube").setName("SonarSource :: SonarQube"));
        ComponentDto project2 = db.components().insertPublicProject(organization, ( p) -> p.setDbKey("org.sonarsource:sonarlint").setName("SonarSource :: SonarLint"));
        componentIndexer.indexOnStartup(null);
        authorizationIndexerTester.allowOnlyAnyone(project1);
        authorizationIndexerTester.allowOnlyAnyone(project2);
        TestResponse wsResponse = ws.newRequest().setParam(SuggestionsAction.PARAM_QUERY, "Sonar").setParam(SuggestionsAction.PARAM_RECENTLY_BROWSED, project1.getDbKey()).setMethod("POST").setMediaType(JSON).execute();
        assertJson(ws.getDef().responseExampleAsString()).isSimilarTo(wsResponse.getInput());
    }

    @Test
    public void suggestions_without_query_should_contain_recently_browsed() {
        ComponentDto project = db.components().insertComponent(newPrivateProjectDto(organization));
        componentIndexer.indexOnStartup(null);
        userSessionRule.addProjectPermission(UserRole.USER, project);
        SuggestionsWsResponse response = ws.newRequest().setMethod("POST").setParam(SuggestionsAction.PARAM_RECENTLY_BROWSED, project.getDbKey()).executeProtobuf(SuggestionsWsResponse.class);
        // assert match in qualifier "TRK"
        assertThat(response.getResultsList()).filteredOn(( q) -> (q.getItemsCount()) > 0).extracting(Category::getQ).containsExactly(PROJECT);
        // assert correct id to be found
        assertThat(response.getResultsList()).flatExtracting(Category::getItemsList).extracting(Suggestion::getKey, Suggestion::getIsRecentlyBrowsed).containsExactly(tuple(project.getDbKey(), true));
    }

    @Test
    public void suggestions_without_query_should_contain_recently_browsed_public_project() {
        ComponentDto project = db.components().insertComponent(newPublicProjectDto(organization));
        componentIndexer.indexOnStartup(null);
        SuggestionsWsResponse response = ws.newRequest().setMethod("POST").setParam(SuggestionsAction.PARAM_RECENTLY_BROWSED, project.getDbKey()).executeProtobuf(SuggestionsWsResponse.class);
        // assert match in qualifier "TRK"
        assertThat(response.getResultsList()).filteredOn(( q) -> (q.getItemsCount()) > 0).extracting(Category::getQ).containsExactly(PROJECT);
        // assert correct id to be found
        assertThat(response.getResultsList()).flatExtracting(Category::getItemsList).extracting(Suggestion::getKey, Suggestion::getIsRecentlyBrowsed).containsExactly(tuple(project.getDbKey(), true));
    }

    @Test
    public void suggestions_without_query_should_not_contain_recently_browsed_without_permission() {
        ComponentDto project = db.components().insertComponent(newPrivateProjectDto(organization));
        componentIndexer.indexOnStartup(null);
        SuggestionsWsResponse response = ws.newRequest().setMethod("POST").setParam(SuggestionsAction.PARAM_RECENTLY_BROWSED, project.getDbKey()).executeProtobuf(SuggestionsWsResponse.class);
        assertThat(response.getResultsList()).flatExtracting(Category::getItemsList).isEmpty();
    }

    @Test
    public void suggestions_without_query_should_contain_favorites() {
        ComponentDto project = db.components().insertComponent(newPrivateProjectDto(organization));
        Mockito.doReturn(Collections.singletonList(project)).when(favoriteFinder).list();
        componentIndexer.indexOnStartup(null);
        userSessionRule.addProjectPermission(UserRole.USER, project);
        SuggestionsWsResponse response = ws.newRequest().setMethod("POST").executeProtobuf(SuggestionsWsResponse.class);
        // assert match in qualifier "TRK"
        assertThat(response.getResultsList()).filteredOn(( q) -> (q.getItemsCount()) > 0).extracting(Category::getQ).containsExactly(PROJECT);
        // assert correct id to be found
        assertThat(response.getResultsList()).flatExtracting(Category::getItemsList).extracting(Suggestion::getKey, Suggestion::getIsFavorite).containsExactly(tuple(project.getDbKey(), true));
    }

    @Test
    public void suggestions_without_query_should_not_contain_favorites_without_permission() {
        ComponentDto project = db.components().insertComponent(newPrivateProjectDto(organization));
        Mockito.doReturn(Collections.singletonList(project)).when(favoriteFinder).list();
        componentIndexer.indexOnStartup(null);
        SuggestionsWsResponse response = ws.newRequest().setMethod("POST").executeProtobuf(SuggestionsWsResponse.class);
        assertThat(response.getResultsList()).flatExtracting(Category::getItemsList).isEmpty();
    }

    @Test
    public void suggestions_without_query_should_contain_recently_browsed_favorites() {
        ComponentDto project = db.components().insertComponent(newPrivateProjectDto(organization));
        Mockito.doReturn(Collections.singletonList(project)).when(favoriteFinder).list();
        componentIndexer.indexOnStartup(null);
        userSessionRule.addProjectPermission(UserRole.USER, project);
        SuggestionsWsResponse response = ws.newRequest().setMethod("POST").setParam(SuggestionsAction.PARAM_RECENTLY_BROWSED, project.getDbKey()).executeProtobuf(SuggestionsWsResponse.class);
        // assert match in qualifier "TRK"
        assertThat(response.getResultsList()).filteredOn(( q) -> (q.getItemsCount()) > 0).extracting(Category::getQ).containsExactly(Qualifiers.PROJECT);
        // assert correct id to be found
        assertThat(response.getResultsList()).flatExtracting(Category::getItemsList).extracting(Suggestion::getKey, Suggestion::getIsFavorite, Suggestion::getIsRecentlyBrowsed).containsExactly(tuple(project.getDbKey(), true, true));
    }

    @Test
    public void suggestions_without_query_should_not_contain_matches_that_are_neither_favorites_nor_recently_browsed() {
        ComponentDto project = db.components().insertComponent(newPrivateProjectDto(organization));
        componentIndexer.indexOnStartup(null);
        userSessionRule.addProjectPermission(UserRole.USER, project);
        SuggestionsWsResponse response = ws.newRequest().setMethod("POST").executeProtobuf(SuggestionsWsResponse.class);
        // assert match in qualifier "TRK"
        assertThat(response.getResultsList()).filteredOn(( q) -> (q.getItemsCount()) > 0).extracting(Category::getQ).isEmpty();
    }

    @Test
    public void suggestions_without_query_should_order_results() {
        ComponentDto project1 = db.components().insertComponent(newPrivateProjectDto(organization).setName("Alpha"));
        ComponentDto project2 = db.components().insertComponent(newPrivateProjectDto(organization).setName("Bravo"));
        ComponentDto project3 = db.components().insertComponent(newPrivateProjectDto(organization).setName("Charlie"));
        ComponentDto project4 = db.components().insertComponent(newPrivateProjectDto(organization).setName("Delta"));
        Mockito.doReturn(Arrays.asList(project4, project2)).when(favoriteFinder).list();
        componentIndexer.indexOnStartup(null);
        userSessionRule.addProjectPermission(UserRole.USER, project1);
        userSessionRule.addProjectPermission(UserRole.USER, project2);
        userSessionRule.addProjectPermission(UserRole.USER, project3);
        userSessionRule.addProjectPermission(UserRole.USER, project4);
        SuggestionsWsResponse response = ws.newRequest().setMethod("POST").setParam(SuggestionsAction.PARAM_RECENTLY_BROWSED, Stream.of(project3, project1).map(ComponentDto::getDbKey).collect(Collectors.joining(","))).executeProtobuf(SuggestionsWsResponse.class);
        // assert order of keys
        assertThat(response.getResultsList()).flatExtracting(Category::getItemsList).extracting(Suggestion::getName, Suggestion::getIsFavorite, Suggestion::getIsRecentlyBrowsed).containsExactly(tuple("Bravo", true, false), tuple("Delta", true, false), tuple("Alpha", false, true), tuple("Charlie", false, true));
    }

    @Test
    public void suggestions_without_query_should_return_empty_qualifiers() {
        ComponentDto project = db.components().insertComponent(newPrivateProjectDto(organization));
        componentIndexer.indexOnAnalysis(project.projectUuid());
        userSessionRule.addProjectPermission(UserRole.USER, project);
        SuggestionsWsResponse response = ws.newRequest().setMethod("POST").setParam(SuggestionsAction.PARAM_RECENTLY_BROWSED, project.getDbKey()).executeProtobuf(SuggestionsWsResponse.class);
        assertThat(response.getResultsList()).extracting(Category::getQ, Category::getItemsCount).containsExactlyInAnyOrder(tuple("VW", 0), tuple("APP", 0), tuple("SVW", 0), tuple("TRK", 1), tuple("FIL", 0), tuple("UTS", 0)).doesNotContain(tuple("BRC", 0));
    }

    @Test
    public void suggestions_should_filter_allowed_qualifiers() {
        resourceTypes.setAllQualifiers(PROJECT, MODULE, FILE);
        ComponentDto project = db.components().insertComponent(newPrivateProjectDto(organization));
        componentIndexer.indexOnAnalysis(project.projectUuid());
        userSessionRule.addProjectPermission(UserRole.USER, project);
        SuggestionsWsResponse response = ws.newRequest().setMethod("POST").setParam(SuggestionsAction.PARAM_RECENTLY_BROWSED, project.getDbKey()).executeProtobuf(SuggestionsWsResponse.class);
        assertThat(response.getResultsList()).extracting(Category::getQ).containsExactlyInAnyOrder(PROJECT, FILE).doesNotContain(MODULE);
    }

    @Test
    public void exact_match_in_one_qualifier() {
        ComponentDto project = db.components().insertComponent(newPrivateProjectDto(organization));
        componentIndexer.indexOnStartup(null);
        authorizationIndexerTester.allowOnlyAnyone(project);
        SuggestionsWsResponse response = ws.newRequest().setMethod("POST").setParam(SuggestionsAction.PARAM_QUERY, project.getDbKey()).executeProtobuf(SuggestionsWsResponse.class);
        // assert match in qualifier "TRK"
        assertThat(response.getResultsList()).filteredOn(( q) -> (q.getItemsCount()) > 0).extracting(Category::getQ).containsExactly(PROJECT);
        // assert correct id to be found
        assertThat(response.getResultsList()).flatExtracting(Category::getItemsList).extracting(Suggestion::getKey, Suggestion::getOrganization).containsExactly(tuple(project.getDbKey(), organization.getKey()));
    }

    @Test
    public void should_not_return_suggestion_on_non_existing_project() {
        ComponentDto project = db.components().insertComponent(newPrivateProjectDto(organization));
        componentIndexer.indexOnStartup(null);
        authorizationIndexerTester.allowOnlyAnyone(project);
        db.getDbClient().componentDao().delete(db.getSession(), project.getId());
        db.commit();
        SuggestionsWsResponse response = ws.newRequest().setMethod("POST").setParam(SuggestionsAction.PARAM_QUERY, project.getDbKey()).executeProtobuf(SuggestionsWsResponse.class);
        // assert match in qualifier "TRK"
        assertThat(response.getResultsList()).filteredOn(( q) -> (q.getItemsCount()) > 0).isEmpty();
    }

    @Test
    public void must_not_search_if_no_valid_tokens_are_provided() {
        ComponentDto project = db.components().insertComponent(newPrivateProjectDto(organization).setName("SonarQube"));
        componentIndexer.indexOnStartup(null);
        authorizationIndexerTester.allowOnlyAnyone(project);
        SuggestionsWsResponse response = ws.newRequest().setMethod("POST").setParam(SuggestionsAction.PARAM_QUERY, "S o").executeProtobuf(SuggestionsWsResponse.class);
        assertThat(response.getResultsList()).filteredOn(( q) -> (q.getItemsCount()) > 0).isEmpty();
        assertThat(response.getWarning()).contains(SuggestionsAction.SHORT_INPUT_WARNING);
    }

    @Test
    public void should_warn_about_short_inputs() {
        SuggestionsWsResponse response = ws.newRequest().setMethod("POST").setParam(SuggestionsAction.PARAM_QUERY, "validLongToken x").executeProtobuf(SuggestionsWsResponse.class);
        assertThat(response.getWarning()).contains(SuggestionsAction.SHORT_INPUT_WARNING);
    }

    @Test
    public void should_warn_about_short_inputs_but_return_results_based_on_other_terms() {
        ComponentDto project = db.components().insertComponent(newPrivateProjectDto(organization).setName("SonarQube"));
        componentIndexer.indexOnStartup(null);
        authorizationIndexerTester.allowOnlyAnyone(project);
        SuggestionsWsResponse response = ws.newRequest().setMethod("POST").setParam(SuggestionsAction.PARAM_QUERY, "Sonar Q").executeProtobuf(SuggestionsWsResponse.class);
        assertThat(response.getResultsList()).flatExtracting(Category::getItemsList).extracting(Suggestion::getKey).contains(project.getDbKey());
        assertThat(response.getWarning()).contains(SuggestionsAction.SHORT_INPUT_WARNING);
    }

    @Test
    public void should_contain_component_names() {
        OrganizationDto organization1 = db.organizations().insert(( o) -> o.setKey("org-1").setName("Organization One"));
        ComponentDto project1 = db.components().insertComponent(newPrivateProjectDto(organization1).setName("Project1"));
        componentIndexer.indexOnAnalysis(project1.projectUuid());
        authorizationIndexerTester.allowOnlyAnyone(project1);
        SuggestionsWsResponse response = ws.newRequest().setMethod("POST").setParam(SuggestionsAction.PARAM_QUERY, "Project").executeProtobuf(SuggestionsWsResponse.class);
        assertThat(response.getResultsList()).flatExtracting(Category::getItemsList).extracting(Suggestion::getKey, Suggestion::getName).containsExactlyInAnyOrder(tuple(project1.getDbKey(), project1.name()));
    }

    @Test
    public void should_contain_organization_names() {
        OrganizationDto organization1 = db.organizations().insert(( o) -> o.setKey("org-1").setName("Organization One"));
        OrganizationDto organization2 = db.organizations().insert(( o) -> o.setKey("org-2").setName("Organization Two"));
        ComponentDto project1 = db.components().insertComponent(newPrivateProjectDto(organization1).setName("Project1"));
        componentIndexer.indexOnAnalysis(project1.projectUuid());
        authorizationIndexerTester.allowOnlyAnyone(project1);
        ComponentDto project2 = db.components().insertComponent(newPrivateProjectDto(organization2).setName("Project2"));
        componentIndexer.indexOnAnalysis(project2.projectUuid());
        authorizationIndexerTester.allowOnlyAnyone(project2);
        SuggestionsWsResponse response = ws.newRequest().setMethod("POST").setParam(SuggestionsAction.PARAM_QUERY, "Project").executeProtobuf(SuggestionsWsResponse.class);
        assertThat(response.getOrganizationsList()).extracting(Organization::getKey, Organization::getName).containsExactlyInAnyOrder(Stream.of(organization1, organization2).map(( o) -> tuple(o.getKey(), o.getName())).toArray(Tuple[]::new));
    }

    @Test
    public void should_not_return_modules() {
        ComponentDto project = db.components().insertComponent(newPrivateProjectDto(organization).setName("ProjectWithModules"));
        db.components().insertComponent(newModuleDto(project).setName("Module1"));
        db.components().insertComponent(newModuleDto(project).setName("Module2"));
        componentIndexer.indexOnAnalysis(project.projectUuid());
        authorizationIndexerTester.allowOnlyAnyone(project);
        SuggestionsWsResponse response = ws.newRequest().setMethod("POST").setParam(SuggestionsAction.PARAM_QUERY, "Module").executeProtobuf(SuggestionsWsResponse.class);
        assertThat(response.getResultsList()).flatExtracting(Category::getItemsList).extracting(Suggestion::getKey).containsOnly(project.getDbKey());
    }

    @Test
    public void should_mark_recently_browsed_items() {
        ComponentDto project = db.components().insertComponent(newPrivateProjectDto(organization).setName("ProjectModule"));
        ComponentDto module1 = newModuleDto(project).setName("Module1");
        db.components().insertComponent(module1);
        ComponentDto module2 = newModuleDto(project).setName("Module2");
        db.components().insertComponent(module2);
        componentIndexer.indexOnAnalysis(project.projectUuid());
        authorizationIndexerTester.allowOnlyAnyone(project);
        SuggestionsWsResponse response = ws.newRequest().setMethod("POST").setParam(SuggestionsAction.PARAM_QUERY, "Module").setParam(SuggestionsAction.PARAM_RECENTLY_BROWSED, Stream.of(module1.getDbKey(), project.getDbKey()).collect(Collectors.joining(","))).executeProtobuf(SuggestionsWsResponse.class);
        assertThat(response.getResultsList()).flatExtracting(Category::getItemsList).extracting(Suggestion::getIsRecentlyBrowsed).containsExactly(true);
    }

    @Test
    public void should_mark_favorite_items() {
        ComponentDto favouriteProject = db.components().insertComponent(newPrivateProjectDto(organization).setName("Project1"));
        ComponentDto nonFavouriteProject = db.components().insertComponent(newPublicProjectDto(organization).setName("Project2"));
        Mockito.doReturn(Collections.singletonList(favouriteProject)).when(favoriteFinder).list();
        componentIndexer.indexOnAnalysis(favouriteProject.projectUuid());
        componentIndexer.indexOnAnalysis(nonFavouriteProject.projectUuid());
        authorizationIndexerTester.allowOnlyAnyone(favouriteProject, nonFavouriteProject);
        SuggestionsWsResponse response = ws.newRequest().setMethod("POST").setParam(SuggestionsAction.PARAM_QUERY, "Project").executeProtobuf(SuggestionsWsResponse.class);
        assertThat(response.getResultsList()).flatExtracting(Category::getItemsList).extracting(Suggestion::getKey, Suggestion::getIsFavorite).containsExactly(tuple(favouriteProject.getDbKey(), true), tuple(nonFavouriteProject.getDbKey(), false));
    }

    @Test
    public void should_return_empty_qualifiers() {
        ComponentDto project = db.components().insertComponent(newPrivateProjectDto(organization));
        componentIndexer.indexOnAnalysis(project.projectUuid());
        authorizationIndexerTester.allowOnlyAnyone(project);
        SuggestionsWsResponse response = ws.newRequest().setMethod("POST").setParam(SuggestionsAction.PARAM_QUERY, project.name()).executeProtobuf(SuggestionsWsResponse.class);
        assertThat(response.getResultsList()).extracting(Category::getQ, Category::getItemsCount).containsExactlyInAnyOrder(tuple("VW", 0), tuple("SVW", 0), tuple("APP", 0), tuple("TRK", 1), tuple("FIL", 0), tuple("UTS", 0));
    }

    @Test
    public void should_only_provide_project_for_certain_qualifiers() {
        String query = randomAlphabetic(10);
        ComponentDto app = db.components().insertApplication(organization, ( v) -> v.setName(query));
        ComponentDto view = db.components().insertView(organization, ( v) -> v.setName(query));
        ComponentDto subView = db.components().insertComponent(ComponentTesting.newSubView(view).setName(query));
        ComponentDto project = db.components().insertPrivateProject(organization, ( p) -> p.setName(query));
        ComponentDto module = db.components().insertComponent(ComponentTesting.newModuleDto(project).setName(query));
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(module).setName(query));
        ComponentDto test = db.components().insertComponent(ComponentTesting.newFileDto(module).setName(query).setQualifier(UNIT_TEST_FILE));
        componentIndexer.indexOnStartup(null);
        authorizationIndexerTester.allowOnlyAnyone(project);
        authorizationIndexerTester.allowOnlyAnyone(view);
        authorizationIndexerTester.allowOnlyAnyone(app);
        SuggestionsWsResponse response = ws.newRequest().setMethod("POST").setParam(SuggestionsAction.PARAM_QUERY, project.name()).executeProtobuf(SuggestionsWsResponse.class);
        assertThat(response.getResultsList()).extracting(Category::getQ, ( c) -> c.getItemsList().stream().map(Suggestion::hasProject).findFirst().orElse(null)).containsExactlyInAnyOrder(tuple(APP.getName(), false), tuple(VIEW.getName(), false), tuple(SUBVIEW.getName(), false), tuple(SuggestionCategory.PROJECT.getName(), false), tuple(SuggestionCategory.FILE.getName(), true), tuple(SuggestionCategory.UNIT_TEST_FILE.getName(), true));
    }

    @Test
    public void does_not_return_branches() {
        ComponentDto project = db.components().insertMainBranch();
        authorizationIndexerTester.allowOnlyAnyone(project);
        ComponentDto branch = db.components().insertProjectBranch(project);
        componentIndexer.indexOnStartup(null);
        authorizationIndexerTester.allowOnlyAnyone(project);
        SuggestionsWsResponse response = ws.newRequest().setMethod("POST").setParam(SuggestionsAction.PARAM_QUERY, project.name()).executeProtobuf(SuggestionsWsResponse.class);
        assertThat(response.getResultsList()).filteredOn(( c) -> "TRK".equals(c.getQ())).extracting(Category::getItemsList).hasSize(1);
    }

    @Test
    public void should_not_propose_to_show_more_results_if_0_projects_are_found() {
        check_proposal_to_show_more_results(0, 0, 0L, null, true);
    }

    @Test
    public void should_not_propose_to_show_more_results_if_0_projects_are_found_and_no_search_query_is_provided() {
        check_proposal_to_show_more_results(0, 0, 0L, null, false);
    }

    @Test
    public void should_not_propose_to_show_more_results_if_5_projects_are_found() {
        check_proposal_to_show_more_results(5, 5, 0L, null, true);
    }

    @Test
    public void should_not_propose_to_show_more_results_if_5_projects_are_found_and_no_search_query_is_provided() {
        check_proposal_to_show_more_results(5, 5, 0L, null, false);
    }

    @Test
    public void should_not_propose_to_show_more_results_if_6_projects_are_found() {
        check_proposal_to_show_more_results(6, 6, 0L, null, true);
    }

    @Test
    public void should_not_propose_to_show_more_results_if_6_projects_are_found_and_no_search_query_is_provided() {
        check_proposal_to_show_more_results(6, 6, 0L, null, false);
    }

    @Test
    public void should_propose_to_show_more_results_if_7_projects_are_found() {
        check_proposal_to_show_more_results(7, 6, 1L, null, true);
    }

    @Test
    public void should_propose_to_show_more_results_if_7_projects_are_found_and_no_search_query_is_provided() {
        check_proposal_to_show_more_results(7, 6, 1L, null, false);
    }

    @Test
    public void show_more_results_if_requested_and_5_projects_are_found() {
        check_proposal_to_show_more_results(5, 0, 0L, SuggestionCategory.PROJECT, true);
    }

    @Test
    public void show_more_results_if_requested_and_5_projects_are_found_and_no_search_query_is_provided() {
        check_proposal_to_show_more_results(5, 0, 0L, SuggestionCategory.PROJECT, false);
    }

    @Test
    public void show_more_results_if_requested_and_6_projects_are_found() {
        check_proposal_to_show_more_results(6, 0, 0L, SuggestionCategory.PROJECT, true);
    }

    @Test
    public void show_more_results_if_requested_and_6_projects_are_found_and_no_search_query_is_provided() {
        check_proposal_to_show_more_results(6, 0, 0L, SuggestionCategory.PROJECT, false);
    }

    @Test
    public void show_more_results_if_requested_and_7_projects_are_found() {
        check_proposal_to_show_more_results(7, 1, 0L, SuggestionCategory.PROJECT, true);
    }

    @Test
    public void show_more_results_if_requested_and_7_projects_are_found_and_no_search_query_is_provided() {
        check_proposal_to_show_more_results(7, 1, 0L, SuggestionCategory.PROJECT, false);
    }

    @Test
    public void show_more_results_if_requested_and_26_projects_are_found() {
        check_proposal_to_show_more_results(26, 20, 0L, SuggestionCategory.PROJECT, true);
    }

    @Test
    public void show_more_results_if_requested_and_26_projects_are_found_and_no_search_query_is_provided() {
        check_proposal_to_show_more_results(26, 20, 0L, SuggestionCategory.PROJECT, false);
    }

    @Test
    public void show_more_results_if_requested_and_27_projects_are_found() {
        check_proposal_to_show_more_results(27, 20, 1L, SuggestionCategory.PROJECT, true);
    }

    @Test
    public void show_more_results_if_requested_and_27_projects_are_found_and_no_search_query_is_provided() {
        check_proposal_to_show_more_results(27, 20, 1L, SuggestionCategory.PROJECT, false);
    }

    @Test
    public void show_more_results_filter_out_if_non_allowed_qualifiers() {
        resourceTypes.setAllQualifiers(APP, VIEW, SUBVIEW);
        check_proposal_to_show_more_results(10, 0, 0L, SuggestionCategory.PROJECT, true);
    }
}

