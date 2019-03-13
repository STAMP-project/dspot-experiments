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
package org.sonar.db.component;


import BranchType.SHORT;
import ComponentQuery.Builder;
import Qualifiers.MODULE;
import Qualifiers.VIEW;
import Scopes.FILE;
import System2.INSTANCE;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.RowNotFoundException;
import org.sonar.db.metric.MetricDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.source.FileSourceDto;


@RunWith(DataProviderRunner.class)
public class ComponentDaoTest {
    private static final String PROJECT_UUID = "project-uuid";

    private static final String MODULE_UUID = "module-uuid";

    private static final String FILE_1_UUID = "file-1-uuid";

    private static final String FILE_2_UUID = "file-2-uuid";

    private static final String FILE_3_UUID = "file-3-uuid";

    private static final String A_VIEW_UUID = "view-uuid";

    private static final ComponentQuery ALL_PROJECTS_COMPONENT_QUERY = ComponentQuery.builder().setQualifiers("TRK").build();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private Random random = new Random();

    private DbSession dbSession = db.getSession();

    private ComponentDao underTest = new ComponentDao();

    @Test
    public void get_by_uuid() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPrivateProject(organization, ( p) -> p.setDbKey("org.struts:struts").setName("Struts").setLongName("Apache Struts"));
        ComponentDto anotherProject = db.components().insertPrivateProject(organization);
        ComponentDto result = underTest.selectByUuid(dbSession, project.uuid()).get();
        assertThat(result).isNotNull();
        assertThat(result.getOrganizationUuid()).isEqualTo(organization.getUuid());
        assertThat(result.uuid()).isEqualTo(project.uuid());
        assertThat(result.getUuidPath()).isEqualTo(".");
        assertThat(result.moduleUuid()).isNull();
        assertThat(result.moduleUuidPath()).isEqualTo((("." + (project.uuid())) + "."));
        assertThat(result.projectUuid()).isEqualTo(project.uuid());
        assertThat(result.getDbKey()).isEqualTo("org.struts:struts");
        assertThat(result.path()).isNull();
        assertThat(result.name()).isEqualTo("Struts");
        assertThat(result.longName()).isEqualTo("Apache Struts");
        assertThat(result.qualifier()).isEqualTo("TRK");
        assertThat(result.scope()).isEqualTo("PRJ");
        assertThat(result.language()).isNull();
        assertThat(result.getCopyResourceUuid()).isNull();
        assertThat(result.isPrivate()).isTrue();
        assertThat(underTest.selectByUuid(dbSession, "UNKNOWN")).isEmpty();
    }

    @Test
    public void get_by_uuid_on_technical_project_copy() {
        ComponentDto view = db.components().insertView();
        ComponentDto project = db.components().insertPublicProject(( p) -> p.setDbKey("org.struts:struts").setName("Struts").setLongName("Apache Struts"));
        ComponentDto projectCopy = db.components().insertComponent(ComponentTesting.newProjectCopy(project, view));
        ComponentDto anotherProject = db.components().insertPrivateProject();
        ComponentDto anotherProjectCopy = db.components().insertComponent(ComponentTesting.newProjectCopy(anotherProject, view));
        ComponentDto result = underTest.selectByUuid(dbSession, projectCopy.uuid()).get();
        assertThat(result.uuid()).isEqualTo(projectCopy.uuid());
        assertThat(result.moduleUuid()).isEqualTo(view.uuid());
        assertThat(result.moduleUuidPath()).isEqualTo((("." + (view.uuid())) + "."));
        assertThat(result.projectUuid()).isEqualTo(view.uuid());
        assertThat(result.getDbKey()).isEqualTo(((view.getDbKey()) + (project.getDbKey())));
        assertThat(result.path()).isNull();
        assertThat(result.name()).isEqualTo("Struts");
        assertThat(result.longName()).isEqualTo("Apache Struts");
        assertThat(result.qualifier()).isEqualTo("TRK");
        assertThat(result.scope()).isEqualTo("FIL");
        assertThat(result.language()).isNull();
        assertThat(result.getCopyResourceUuid()).isEqualTo(project.uuid());
        assertThat(result.isPrivate()).isFalse();
    }

    @Test
    public void selectByUuid_on_disabled_component() {
        ComponentDto enabledProject = db.components().insertPublicProject(( p) -> p.setEnabled(true));
        ComponentDto disabledProject = db.components().insertPublicProject(( p) -> p.setEnabled(false));
        ComponentDto result = underTest.selectByUuid(dbSession, disabledProject.uuid()).get();
        assertThat(result).isNotNull();
        assertThat(result.isEnabled()).isFalse();
    }

    @Test
    public void selectOrFailByUuid_fails_when_component_not_found() {
        db.components().insertPublicProject();
        expectedException.expect(RowNotFoundException.class);
        underTest.selectOrFailByUuid(dbSession, "unknown");
    }

    @Test
    public void selectByKey() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPrivateProject(organization);
        ComponentDto directory = db.components().insertComponent(ComponentTesting.newDirectory(project, "src"));
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(project, directory).setDbKey("org.struts:struts-core:src/org/struts/RequestContext.java").setName("RequestContext.java").setLongName("org.struts.RequestContext").setLanguage("java").setPath("src/RequestContext.java"));
        Optional<ComponentDto> optional = underTest.selectByKey(dbSession, file.getDbKey());
        ComponentDto result = optional.get();
        assertThat(result.getOrganizationUuid()).isEqualTo(organization.getUuid());
        assertThat(result.uuid()).isEqualTo(file.uuid());
        assertThat(result.getDbKey()).isEqualTo("org.struts:struts-core:src/org/struts/RequestContext.java");
        assertThat(result.path()).isEqualTo("src/RequestContext.java");
        assertThat(result.name()).isEqualTo("RequestContext.java");
        assertThat(result.longName()).isEqualTo("org.struts.RequestContext");
        assertThat(result.qualifier()).isEqualTo("FIL");
        assertThat(result.scope()).isEqualTo("FIL");
        assertThat(result.language()).isEqualTo("java");
        assertThat(result.projectUuid()).isEqualTo(project.uuid());
        assertThat(underTest.selectByKey(dbSession, "unknown")).isEmpty();
    }

    @Test
    public void selectByKeyAndPullRequest() {
        ComponentDto project = db.components().insertMainBranch();
        ComponentDto branch = db.components().insertProjectBranch(project, ( b) -> b.setKey("my_branch"));
        ComponentDto pullRequest = db.components().insertProjectBranch(project, ( b) -> b.setKey("my_PR").setBranchType(PULL_REQUEST));
        ComponentDto pullRequestNamedAsMainBranch = db.components().insertProjectBranch(project, ( b) -> b.setKey("master").setBranchType(PULL_REQUEST));
        ComponentDto pullRequestNamedAsBranch = db.components().insertProjectBranch(project, ( b) -> b.setKey("my_branch").setBranchType(PULL_REQUEST));
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(pullRequest));
        assertThat(underTest.selectByKeyAndPullRequest(dbSession, project.getKey(), "my_PR").get().uuid()).isEqualTo(pullRequest.uuid());
        assertThat(underTest.selectByKeyAndBranch(dbSession, project.getKey(), "master").get().uuid()).isEqualTo(project.uuid());
        assertThat(underTest.selectByKeyAndPullRequest(dbSession, project.getKey(), "master").get().uuid()).isEqualTo(pullRequestNamedAsMainBranch.uuid());
        assertThat(underTest.selectByKeyAndBranch(dbSession, branch.getKey(), "my_branch").get().uuid()).isEqualTo(branch.uuid());
        assertThat(underTest.selectByKeyAndPullRequest(dbSession, branch.getKey(), "my_branch").get().uuid()).isEqualTo(pullRequestNamedAsBranch.uuid());
        assertThat(underTest.selectByKeyAndPullRequest(dbSession, file.getKey(), "my_PR").get().uuid()).isEqualTo(file.uuid());
        assertThat(underTest.selectByKeyAndPullRequest(dbSession, "unknown", "my_branch")).isNotPresent();
        assertThat(underTest.selectByKeyAndPullRequest(dbSession, file.getKey(), "unknown")).isNotPresent();
    }

    @Test
    public void selectOrFailByKey_fails_when_component_not_found() {
        db.components().insertPrivateProject();
        expectedException.expect(RowNotFoundException.class);
        underTest.selectOrFailByKey(dbSession, "unknown");
    }

    @Test
    public void get_by_key_on_disabled_component() {
        ComponentDto project = db.components().insertPrivateProject(( p) -> p.setEnabled(false));
        ComponentDto result = underTest.selectOrFailByKey(dbSession, project.getDbKey());
        assertThat(result.isEnabled()).isFalse();
    }

    @Test
    public void get_by_key_on_a_root_project() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto result = underTest.selectOrFailByKey(dbSession, project.getDbKey());
        assertThat(result.getDbKey()).isEqualTo(project.getDbKey());
        assertThat(result.uuid()).isEqualTo(project.uuid());
        assertThat(result.getUuidPath()).isEqualTo(project.getUuidPath());
        assertThat(result.getRootUuid()).isEqualTo(project.uuid());
        assertThat(result.projectUuid()).isEqualTo(project.uuid());
    }

    @Test
    public void get_by_keys() {
        ComponentDto project1 = db.components().insertPrivateProject();
        ComponentDto project2 = db.components().insertPrivateProject();
        List<ComponentDto> results = underTest.selectByKeys(dbSession, Arrays.asList(project1.getDbKey(), project2.getDbKey()));
        assertThat(results).extracting(ComponentDto::uuid, ComponentDto::getDbKey).containsExactlyInAnyOrder(tuple(project1.uuid(), project1.getDbKey()), tuple(project2.uuid(), project2.getDbKey()));
        assertThat(underTest.selectByKeys(dbSession, Collections.singletonList("unknown"))).isEmpty();
    }

    @Test
    public void selectByKeysAndBranch() {
        ComponentDto project = db.components().insertMainBranch();
        ComponentDto branch = db.components().insertProjectBranch(project, ( b) -> b.setKey("my_branch"));
        ComponentDto file1 = db.components().insertComponent(ComponentTesting.newFileDto(branch));
        ComponentDto file2 = db.components().insertComponent(ComponentTesting.newFileDto(branch));
        ComponentDto anotherBranch = db.components().insertProjectBranch(project, ( b) -> b.setKey("another_branch"));
        ComponentDto fileOnAnotherBranch = db.components().insertComponent(ComponentTesting.newFileDto(anotherBranch));
        assertThat(underTest.selectByKeysAndBranch(dbSession, Arrays.asList(branch.getKey(), file1.getKey(), file2.getKey()), "my_branch")).extracting(ComponentDto::uuid).containsExactlyInAnyOrder(branch.uuid(), file1.uuid(), file2.uuid());
        assertThat(underTest.selectByKeysAndBranch(dbSession, Arrays.asList(file1.getKey(), file2.getKey(), fileOnAnotherBranch.getKey()), "my_branch")).extracting(ComponentDto::uuid).containsExactlyInAnyOrder(file1.uuid(), file2.uuid());
        assertThat(underTest.selectByKeysAndBranch(dbSession, Collections.singletonList(fileOnAnotherBranch.getKey()), "my_branch")).isEmpty();
        assertThat(underTest.selectByKeysAndBranch(dbSession, Collections.singletonList(file1.getKey()), "unknown")).isEmpty();
        assertThat(underTest.selectByKeysAndBranch(dbSession, Collections.singletonList("unknown"), "my_branch")).isEmpty();
        assertThat(underTest.selectByKeysAndBranch(dbSession, Collections.singletonList(branch.getKey()), "master")).extracting(ComponentDto::uuid).containsExactlyInAnyOrder(project.uuid());
    }

    @Test
    public void select_by_keys_and_branches() {
        ComponentDto project = db.components().insertMainBranch();
        ComponentDto projectBranch = db.components().insertProjectBranch(project, ( b) -> b.setKey("my_branch"));
        ComponentDto application = db.components().insertMainBranch(( a) -> a.setQualifier(APP));
        ComponentDto applicationBranch = db.components().insertProjectBranch(application, ( b) -> b.setKey("my_branch"));
        assertThat(underTest.selectByKeysAndBranches(db.getSession(), ImmutableMap.of(projectBranch.getKey(), projectBranch.getBranch(), applicationBranch.getKey(), applicationBranch.getBranch()))).extracting(ComponentDto::getKey, ComponentDto::getBranch).containsExactlyInAnyOrder(tuple(projectBranch.getKey(), "my_branch"), tuple(applicationBranch.getKey(), "my_branch"));
        assertThat(underTest.selectByKeysAndBranches(db.getSession(), ImmutableMap.of(projectBranch.getKey(), "unknown", "unknown", projectBranch.getBranch()))).extracting(ComponentDto::getDbKey).isEmpty();
        assertThat(underTest.selectByKeysAndBranches(db.getSession(), Collections.emptyMap())).isEmpty();
    }

    @Test
    public void get_by_ids() {
        ComponentDto project1 = db.components().insertPrivateProject();
        ComponentDto project2 = db.components().insertPrivateProject();
        List<ComponentDto> results = underTest.selectByIds(dbSession, Arrays.asList(project1.getId(), project2.getId()));
        assertThat(results).extracting(ComponentDto::uuid, ComponentDto::getDbKey).containsExactlyInAnyOrder(tuple(project1.uuid(), project1.getDbKey()), tuple(project2.uuid(), project2.getDbKey()));
        assertThat(underTest.selectByIds(dbSession, Collections.singletonList(0L))).isEmpty();
    }

    @Test
    public void get_by_uuids() {
        ComponentDto project1 = db.components().insertPrivateProject();
        ComponentDto project2 = db.components().insertPrivateProject();
        List<ComponentDto> results = underTest.selectByUuids(dbSession, Arrays.asList(project1.uuid(), project2.uuid()));
        assertThat(results).extracting(ComponentDto::uuid, ComponentDto::getDbKey).containsExactlyInAnyOrder(tuple(project1.uuid(), project1.getDbKey()), tuple(project2.uuid(), project2.getDbKey()));
        assertThat(underTest.selectByUuids(dbSession, Collections.singletonList("unknown"))).isEmpty();
    }

    @Test
    public void get_by_uuids_on_removed_components() {
        ComponentDto project1 = db.components().insertPrivateProject();
        ComponentDto project2 = db.components().insertPrivateProject(( p) -> p.setEnabled(false));
        List<ComponentDto> results = underTest.selectByUuids(dbSession, Arrays.asList(project1.uuid(), project2.uuid()));
        assertThat(results).extracting(ComponentDto::getDbKey, ComponentDto::isEnabled).containsExactlyInAnyOrder(tuple(project1.getDbKey(), true), tuple(project2.getDbKey(), false));
    }

    @Test
    public void select_existing_uuids() {
        ComponentDto project1 = db.components().insertPrivateProject();
        ComponentDto project2 = db.components().insertPrivateProject(( p) -> p.setEnabled(false));
        assertThat(underTest.selectExistingUuids(dbSession, Arrays.asList(project1.uuid(), project2.uuid()))).containsExactlyInAnyOrder(project1.uuid(), project2.uuid());
        assertThat(underTest.selectExistingUuids(dbSession, Arrays.asList(project1.uuid(), "unknown"))).containsExactlyInAnyOrder(project1.uuid());
        assertThat(underTest.selectExistingUuids(dbSession, Collections.singletonList("unknown"))).isEmpty();
    }

    @Test
    public void get_by_id() {
        ComponentDto project = db.components().insertPrivateProject();
        assertThat(underTest.selectById(dbSession, project.getId())).isNotNull();
    }

    @Test
    public void get_by_id_on_disabled_component() {
        ComponentDto enabledProject = db.components().insertPrivateProject();
        ComponentDto disabledProject = db.components().insertPrivateProject(( p) -> p.setEnabled(false));
        Optional<ComponentDto> result = underTest.selectById(dbSession, disabledProject.getId());
        assertThat(result).isPresent();
        assertThat(result.get().isEnabled()).isFalse();
    }

    @Test
    public void get_nullable_by_id() {
        ComponentDto project = db.components().insertPrivateProject();
        assertThat(underTest.selectById(dbSession, project.getId())).isPresent();
        assertThat(underTest.selectById(dbSession, 0L)).isEmpty();
    }

    @Test
    public void select_component_keys_by_qualifiers() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto module = db.components().insertComponent(ComponentTesting.newModuleDto(project));
        ComponentDto directory = db.components().insertComponent(ComponentTesting.newDirectory(module, "src"));
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(directory));
        assertThat(underTest.selectComponentsByQualifiers(dbSession, Sets.newHashSet("TRK"))).extracting(ComponentDto::getDbKey).containsExactlyInAnyOrder(project.getDbKey());
        assertThat(underTest.selectComponentsByQualifiers(dbSession, Sets.newHashSet("BRC"))).extracting(ComponentDto::getDbKey).containsExactlyInAnyOrder(module.getDbKey());
        assertThat(underTest.selectComponentsByQualifiers(dbSession, Sets.newHashSet("DIR"))).extracting(ComponentDto::getDbKey).containsExactlyInAnyOrder(directory.getDbKey());
        assertThat(underTest.selectComponentsByQualifiers(dbSession, Sets.newHashSet("FIL"))).extracting(ComponentDto::getDbKey).containsExactlyInAnyOrder(file.getDbKey());
        assertThat(underTest.selectComponentsByQualifiers(dbSession, Sets.newHashSet("unknown"))).isEmpty();
    }

    @Test
    public void fail_with_IAE_select_component_keys_by_qualifiers_on_empty_qualifier() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Qualifiers cannot be empty");
        underTest.selectComponentsByQualifiers(dbSession, Collections.emptySet());
    }

    @Test
    public void count_enabled_modules_by_project_uuid() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto module = db.components().insertComponent(ComponentTesting.newModuleDto(project));
        db.components().insertComponent(ComponentTesting.newModuleDto(module));
        ComponentDto subModule2 = ComponentTesting.newModuleDto(module);
        subModule2.setEnabled(false);
        db.components().insertComponent(subModule2);
        int result = underTest.countEnabledModulesByProjectUuid(dbSession, project.uuid());
        assertThat(result).isEqualTo(2);
    }

    @Test
    public void find_sub_projects_by_component_keys() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto removedProject = db.components().insertPrivateProject(( p) -> p.setEnabled(false));
        ComponentDto module = db.components().insertComponent(ComponentTesting.newModuleDto(project));
        ComponentDto removedModule = db.components().insertComponent(ComponentTesting.newModuleDto(project).setEnabled(false));
        ComponentDto subModule = db.components().insertComponent(ComponentTesting.newModuleDto(module));
        ComponentDto removedSubModule = db.components().insertComponent(ComponentTesting.newModuleDto(module).setEnabled(false));
        ComponentDto directory = db.components().insertComponent(ComponentTesting.newDirectory(subModule, "src"));
        ComponentDto removedDirectory = db.components().insertComponent(ComponentTesting.newDirectory(subModule, "src2").setEnabled(false));
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(subModule, directory));
        ComponentDto removedFile = db.components().insertComponent(ComponentTesting.newFileDto(subModule, directory).setEnabled(false));
        // Sub project of a file
        assertThat(underTest.selectSubProjectsByComponentUuids(dbSession, Collections.singletonList(file.uuid()))).extracting(ComponentDto::getDbKey).containsExactlyInAnyOrder(subModule.getDbKey());
        // Sub project of a directory
        assertThat(underTest.selectSubProjectsByComponentUuids(dbSession, Collections.singletonList(directory.uuid()))).extracting(ComponentDto::uuid).containsExactlyInAnyOrder(subModule.uuid());
        // Sub project of a sub module
        assertThat(underTest.selectSubProjectsByComponentUuids(dbSession, Collections.singletonList(subModule.uuid()))).extracting(ComponentDto::uuid).containsExactlyInAnyOrder(module.uuid());
        // Sub project of a module
        assertThat(underTest.selectSubProjectsByComponentUuids(dbSession, Collections.singletonList(module.uuid()))).extracting(ComponentDto::uuid).containsExactlyInAnyOrder(project.uuid());
        // Sub project of a project
        assertThat(underTest.selectSubProjectsByComponentUuids(dbSession, Collections.singletonList(project.uuid()))).extracting(ComponentDto::uuid).containsExactlyInAnyOrder(project.uuid());
        // SUb projects of a component and a sub module
        assertThat(underTest.selectSubProjectsByComponentUuids(dbSession, Arrays.asList(file.uuid(), subModule.uuid()))).extracting(ComponentDto::uuid).containsExactlyInAnyOrder(subModule.uuid(), module.uuid());
        assertThat(underTest.selectSubProjectsByComponentUuids(dbSession, Collections.singletonList("unknown"))).isEmpty();
        assertThat(underTest.selectSubProjectsByComponentUuids(dbSession, Collections.emptyList())).isEmpty();
    }

    @Test
    public void select_enabled_modules_tree() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto removedProject = db.components().insertPrivateProject(( p) -> p.setEnabled(false));
        ComponentDto module = db.components().insertComponent(ComponentTesting.newModuleDto(project));
        ComponentDto removedModule = db.components().insertComponent(ComponentTesting.newModuleDto(project).setEnabled(false));
        ComponentDto subModule = db.components().insertComponent(ComponentTesting.newModuleDto(module));
        ComponentDto removedSubModule = db.components().insertComponent(ComponentTesting.newModuleDto(module).setEnabled(false));
        ComponentDto directory = db.components().insertComponent(ComponentTesting.newDirectory(subModule, "src"));
        ComponentDto removedDirectory = db.components().insertComponent(ComponentTesting.newDirectory(subModule, "src2").setEnabled(false));
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(subModule, directory));
        ComponentDto removedFile = db.components().insertComponent(ComponentTesting.newFileDto(subModule, directory).setEnabled(false));
        // From root project
        assertThat(underTest.selectEnabledDescendantModules(dbSession, project.uuid())).extracting(ComponentDto::uuid).containsExactlyInAnyOrder(project.uuid(), module.uuid(), subModule.uuid()).doesNotContain(removedModule.uuid(), removedSubModule.uuid());
        // From module
        assertThat(underTest.selectEnabledDescendantModules(dbSession, module.uuid())).extracting(ComponentDto::uuid).containsExactlyInAnyOrder(module.uuid(), subModule.uuid()).doesNotContain(removedModule.uuid(), removedModule.uuid());
        // From sub module
        assertThat(underTest.selectEnabledDescendantModules(dbSession, subModule.uuid())).extracting(ComponentDto::uuid).containsExactlyInAnyOrder(subModule.uuid());
        // Folder
        assertThat(underTest.selectEnabledDescendantModules(dbSession, directory.uuid())).isEmpty();
        assertThat(underTest.selectEnabledDescendantModules(dbSession, "unknown")).isEmpty();
    }

    @Test
    public void select_enabled_components_with_module_dto() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto module = db.components().insertComponent(ComponentTesting.newModuleDto(project));
        ComponentDto removedModule = db.components().insertComponent(ComponentTesting.newModuleDto(project).setEnabled(false));
        ComponentDto subModule = db.components().insertComponent(ComponentTesting.newModuleDto(module));
        ComponentDto removedSubModule = db.components().insertComponent(ComponentTesting.newModuleDto(module).setEnabled(false));
        ComponentDto directory = db.components().insertComponent(ComponentTesting.newDirectory(subModule, "src"));
        ComponentDto removedDirectory = db.components().insertComponent(ComponentTesting.newDirectory(subModule, "src2").setEnabled(false));
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(subModule, directory));
        ComponentDto removedFile = db.components().insertComponent(ComponentTesting.newFileDto(subModule, directory).setEnabled(false));
        // From root project
        assertThat(underTest.selectEnabledComponentsWithModuleUuidFromProjectKey(dbSession, project.getDbKey())).extracting(ComponentWithModuleUuidDto::uuid).containsExactlyInAnyOrder(project.uuid(), module.uuid(), subModule.uuid(), directory.uuid(), file.uuid());
    }

    @Test
    public void select_all_modules_tree() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto removedProject = db.components().insertPrivateProject(( p) -> p.setEnabled(false));
        ComponentDto module = db.components().insertComponent(ComponentTesting.newModuleDto(project));
        ComponentDto removedModule = db.components().insertComponent(ComponentTesting.newModuleDto(project).setEnabled(false));
        ComponentDto subModule = db.components().insertComponent(ComponentTesting.newModuleDto(module));
        ComponentDto removedSubModule = db.components().insertComponent(ComponentTesting.newModuleDto(module).setEnabled(false));
        ComponentDto directory = db.components().insertComponent(ComponentTesting.newDirectory(subModule, "src"));
        ComponentDto removedDirectory = db.components().insertComponent(ComponentTesting.newDirectory(subModule, "src2").setEnabled(false));
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(subModule, directory));
        ComponentDto removedFile = db.components().insertComponent(ComponentTesting.newFileDto(subModule, directory).setEnabled(false));
        // From root project, disabled sub module is returned
        assertThat(underTest.selectDescendantModules(dbSession, project.uuid())).extracting(ComponentDto::uuid).containsExactlyInAnyOrder(project.uuid(), module.uuid(), removedModule.uuid(), subModule.uuid(), removedSubModule.uuid());
        // From module, disabled sub module is returned
        assertThat(underTest.selectDescendantModules(dbSession, module.uuid())).extracting(ComponentDto::uuid).containsExactlyInAnyOrder(module.uuid(), subModule.uuid(), removedSubModule.uuid());
        // From removed sub module -> should not be returned
        assertThat(underTest.selectDescendantModules(dbSession, removedSubModule.uuid())).isEmpty();
    }

    @Test
    public void select_enabled_module_files_tree_from_module() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto module = db.components().insertComponent(ComponentTesting.newModuleDto(project));
        ComponentDto fileDirectlyOnModule = db.components().insertComponent(ComponentTesting.newFileDto(module));
        FileSourceDto fileSourceDirectlyOnModule = db.fileSources().insertFileSource(fileDirectlyOnModule);
        ComponentDto subModule = db.components().insertComponent(ComponentTesting.newModuleDto(module));
        ComponentDto directory = db.components().insertComponent(ComponentTesting.newDirectory(subModule, "src"));
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(subModule, directory));
        FileSourceDto fileSource = db.fileSources().insertFileSource(file);
        // From root project
        assertThat(underTest.selectEnabledDescendantFiles(dbSession, project.uuid())).extracting(FilePathWithHashDto::getUuid, FilePathWithHashDto::getModuleUuid, FilePathWithHashDto::getSrcHash, FilePathWithHashDto::getPath, FilePathWithHashDto::getRevision).containsExactlyInAnyOrder(tuple(fileDirectlyOnModule.uuid(), module.uuid(), fileSourceDirectlyOnModule.getSrcHash(), fileDirectlyOnModule.path(), fileSourceDirectlyOnModule.getRevision()), tuple(file.uuid(), subModule.uuid(), fileSource.getSrcHash(), file.path(), fileSource.getRevision()));
        // From module
        assertThat(underTest.selectEnabledDescendantFiles(dbSession, module.uuid())).extracting(FilePathWithHashDto::getUuid, FilePathWithHashDto::getModuleUuid, FilePathWithHashDto::getSrcHash, FilePathWithHashDto::getPath, FilePathWithHashDto::getRevision).containsExactlyInAnyOrder(tuple(fileDirectlyOnModule.uuid(), module.uuid(), fileSourceDirectlyOnModule.getSrcHash(), fileDirectlyOnModule.path(), fileSourceDirectlyOnModule.getRevision()), tuple(file.uuid(), subModule.uuid(), fileSource.getSrcHash(), file.path(), fileSource.getRevision()));
        // From sub module
        assertThat(underTest.selectEnabledDescendantFiles(dbSession, subModule.uuid())).extracting(FilePathWithHashDto::getUuid, FilePathWithHashDto::getModuleUuid, FilePathWithHashDto::getSrcHash, FilePathWithHashDto::getPath, FilePathWithHashDto::getRevision).containsExactlyInAnyOrder(tuple(file.uuid(), subModule.uuid(), fileSource.getSrcHash(), file.path(), fileSource.getRevision()));
        // From directory
        assertThat(underTest.selectEnabledDescendantFiles(dbSession, directory.uuid())).isEmpty();
        assertThat(underTest.selectEnabledDescendantFiles(dbSession, "unknown")).isEmpty();
    }

    @Test
    public void select_enabled_module_files_tree_from_project() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto module = db.components().insertComponent(ComponentTesting.newModuleDto(project));
        ComponentDto fileDirectlyOnModule = db.components().insertComponent(ComponentTesting.newFileDto(module));
        FileSourceDto fileSourceDirectlyOnModule = db.fileSources().insertFileSource(fileDirectlyOnModule);
        ComponentDto subModule = db.components().insertComponent(ComponentTesting.newModuleDto(module));
        ComponentDto directory = db.components().insertComponent(ComponentTesting.newDirectory(subModule, "src"));
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(subModule, directory));
        FileSourceDto fileSource = db.fileSources().insertFileSource(file);
        // From root project
        assertThat(underTest.selectEnabledFilesFromProject(dbSession, project.uuid())).extracting(FilePathWithHashDto::getUuid, FilePathWithHashDto::getModuleUuid, FilePathWithHashDto::getSrcHash, FilePathWithHashDto::getPath, FilePathWithHashDto::getRevision).containsExactlyInAnyOrder(tuple(fileDirectlyOnModule.uuid(), module.uuid(), fileSourceDirectlyOnModule.getSrcHash(), fileDirectlyOnModule.path(), fileSourceDirectlyOnModule.getRevision()), tuple(file.uuid(), subModule.uuid(), fileSource.getSrcHash(), file.path(), fileSource.getRevision()));
        // From module
        assertThat(underTest.selectEnabledFilesFromProject(dbSession, module.uuid())).isEmpty();
        // From sub module
        assertThat(underTest.selectEnabledFilesFromProject(dbSession, subModule.uuid())).isEmpty();
        // From directory
        assertThat(underTest.selectEnabledFilesFromProject(dbSession, directory.uuid())).isEmpty();
        assertThat(underTest.selectEnabledFilesFromProject(dbSession, "unknown")).isEmpty();
    }

    @Test
    public void select_all_components_from_project() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto removedProject = db.components().insertPrivateProject(( p) -> p.setEnabled(false));
        ComponentDto module = db.components().insertComponent(ComponentTesting.newModuleDto(project));
        ComponentDto removedModule = db.components().insertComponent(ComponentTesting.newModuleDto(project).setEnabled(false));
        ComponentDto subModule = db.components().insertComponent(ComponentTesting.newModuleDto(module));
        ComponentDto removedSubModule = db.components().insertComponent(ComponentTesting.newModuleDto(module).setEnabled(false));
        ComponentDto directory = db.components().insertComponent(ComponentTesting.newDirectory(subModule, "src"));
        ComponentDto removedDirectory = db.components().insertComponent(ComponentTesting.newDirectory(subModule, "src2").setEnabled(false));
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(subModule, directory));
        ComponentDto removedFile = db.components().insertComponent(ComponentTesting.newFileDto(subModule, directory).setEnabled(false));
        // Removed components are included
        assertThat(underTest.selectAllComponentsFromProjectKey(dbSession, project.getDbKey())).extracting(ComponentDto::getDbKey).containsExactlyInAnyOrder(project.getDbKey(), module.getDbKey(), removedModule.getDbKey(), subModule.getDbKey(), removedSubModule.getDbKey(), directory.getDbKey(), removedDirectory.getDbKey(), file.getDbKey(), removedFile.getDbKey());
        assertThat(underTest.selectAllComponentsFromProjectKey(dbSession, "UNKNOWN")).isEmpty();
    }

    @Test
    public void select_uuids_by_key_from_project() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto removedProject = db.components().insertPrivateProject(( p) -> p.setEnabled(false));
        ComponentDto module = db.components().insertComponent(ComponentTesting.newModuleDto(project));
        ComponentDto removedModule = db.components().insertComponent(ComponentTesting.newModuleDto(project).setEnabled(false));
        ComponentDto subModule = db.components().insertComponent(ComponentTesting.newModuleDto(module));
        ComponentDto removedSubModule = db.components().insertComponent(ComponentTesting.newModuleDto(module).setEnabled(false));
        ComponentDto directory = db.components().insertComponent(ComponentTesting.newDirectory(subModule, "src"));
        ComponentDto removedDirectory = db.components().insertComponent(ComponentTesting.newDirectory(subModule, "src2").setEnabled(false));
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(subModule, directory));
        ComponentDto removedFile = db.components().insertComponent(ComponentTesting.newFileDto(subModule, directory).setEnabled(false));
        Map<String, String> uuidsByKey = underTest.selectUuidsByKeyFromProjectKey(dbSession, project.getDbKey()).stream().collect(Collectors.toMap(KeyWithUuidDto::key, KeyWithUuidDto::uuid));
        assertThat(uuidsByKey).containsOnly(entry(project.getDbKey(), project.uuid()), entry(module.getDbKey(), module.uuid()), entry(removedModule.getDbKey(), removedModule.uuid()), entry(subModule.getDbKey(), subModule.uuid()), entry(removedSubModule.getDbKey(), removedSubModule.uuid()), entry(directory.getDbKey(), directory.uuid()), entry(removedDirectory.getDbKey(), removedDirectory.uuid()), entry(file.getDbKey(), file.uuid()), entry(removedFile.getDbKey(), removedFile.uuid()));
    }

    @Test
    public void select_enabled_modules_from_project() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto removedProject = db.components().insertPrivateProject(( p) -> p.setEnabled(false));
        ComponentDto module = db.components().insertComponent(ComponentTesting.newModuleDto(project));
        ComponentDto removedModule = db.components().insertComponent(ComponentTesting.newModuleDto(project).setEnabled(false));
        ComponentDto subModule = db.components().insertComponent(ComponentTesting.newModuleDto(module));
        ComponentDto removedSubModule = db.components().insertComponent(ComponentTesting.newModuleDto(module).setEnabled(false));
        ComponentDto directory = db.components().insertComponent(ComponentTesting.newDirectory(subModule, "src"));
        ComponentDto removedDirectory = db.components().insertComponent(ComponentTesting.newDirectory(subModule, "src2").setEnabled(false));
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(subModule, directory));
        ComponentDto removedFile = db.components().insertComponent(ComponentTesting.newFileDto(subModule, directory).setEnabled(false));
        // Removed modules are not included
        assertThat(underTest.selectEnabledModulesFromProjectKey(dbSession, project.getDbKey())).extracting(ComponentDto::getDbKey).containsExactlyInAnyOrder(project.getDbKey(), module.getDbKey(), subModule.getDbKey());
        assertThat(underTest.selectEnabledModulesFromProjectKey(dbSession, "UNKNOWN")).isEmpty();
    }

    @Test
    public void select_views_and_sub_views_and_applications() {
        OrganizationDto organization = db.organizations().insert();
        db.components().insertView(organization, "ABCD");
        db.components().insertView(organization, "IJKL");
        ComponentDto view = db.components().insertView(organization, "EFGH");
        db.components().insertSubView(view, ( dto) -> dto.setUuid("FGHI"));
        ComponentDto application = db.components().insertApplication(organization);
        assertThat(underTest.selectAllViewsAndSubViews(dbSession)).extracting(UuidWithProjectUuidDto::getUuid).containsExactlyInAnyOrder("ABCD", "EFGH", "FGHI", "IJKL", application.uuid());
        assertThat(underTest.selectAllViewsAndSubViews(dbSession)).extracting(UuidWithProjectUuidDto::getProjectUuid).containsExactlyInAnyOrder("ABCD", "EFGH", "EFGH", "IJKL", application.projectUuid());
    }

    @Test
    public void selectViewKeysWithEnabledCopyOfProject_returns_empty_when_set_is_empty() {
        assertThat(underTest.selectViewKeysWithEnabledCopyOfProject(dbSession, Collections.emptySet())).isEmpty();
    }

    @Test
    public void select_projects_from_view() {
        ComponentDto project1 = db.components().insertPrivateProject();
        ComponentDto project2 = db.components().insertPrivateProject();
        ComponentDto view = db.components().insertView();
        db.components().insertComponent(ComponentTesting.newProjectCopy(project1, view));
        ComponentDto viewWithSubView = db.components().insertView();
        db.components().insertComponent(ComponentTesting.newProjectCopy(project2, viewWithSubView));
        ComponentDto subView = db.components().insertSubView(viewWithSubView);
        db.components().insertComponent(ComponentTesting.newProjectCopy(project1, subView));
        ComponentDto viewWithoutProject = db.components().insertView();
        assertThat(underTest.selectProjectsFromView(dbSession, view.uuid(), view.uuid())).containsExactlyInAnyOrder(project1.uuid());
        assertThat(underTest.selectProjectsFromView(dbSession, viewWithSubView.uuid(), viewWithSubView.uuid())).containsExactlyInAnyOrder(project1.uuid(), project2.uuid());
        assertThat(underTest.selectProjectsFromView(dbSession, subView.uuid(), viewWithSubView.uuid())).containsExactlyInAnyOrder(project1.uuid());
        assertThat(underTest.selectProjectsFromView(dbSession, viewWithoutProject.uuid(), viewWithoutProject.uuid())).isEmpty();
        assertThat(underTest.selectProjectsFromView(dbSession, "Unknown", "Unknown")).isEmpty();
    }

    @Test
    public void select_projects() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto provisionedProject = db.components().insertPrivateProject();
        ComponentDto provisionedView = db.components().insertView(organization);
        String projectUuid = db.components().insertProjectAndSnapshot(ComponentTesting.newPrivateProjectDto(organization)).getComponentUuid();
        String disabledProjectUuid = db.components().insertProjectAndSnapshot(ComponentTesting.newPrivateProjectDto(organization).setEnabled(false)).getComponentUuid();
        String viewUuid = db.components().insertProjectAndSnapshot(ComponentTesting.newView(organization)).getComponentUuid();
        assertThat(underTest.selectProjects(dbSession)).extracting(ComponentDto::uuid).containsOnly(provisionedProject.uuid(), projectUuid);
    }

    @Test
    public void select_projects_does_not_return_branches() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertMainBranch();
        ComponentDto branch = db.components().insertProjectBranch(project);
        assertThat(underTest.selectProjects(dbSession)).extracting(ComponentDto::uuid).containsOnly(project.uuid());
    }

    @Test
    public void select_projects_by_organization() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project1 = db.components().insertPrivateProject(organization);
        ComponentDto module = db.components().insertComponent(ComponentTesting.newModuleDto(project1));
        ComponentDto directory = db.components().insertComponent(ComponentTesting.newDirectory(module, "dir"));
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(module, directory));
        ComponentDto project2 = db.components().insertPrivateProject(organization);
        ComponentDto view = db.components().insertView(organization);
        ComponentDto application = db.components().insertApplication(organization);
        OrganizationDto otherOrganization = db.organizations().insert();
        ComponentDto projectOnOtherOrganization = db.components().insertPrivateProject(otherOrganization);
        assertThat(underTest.selectProjectsByOrganization(dbSession, organization.getUuid())).extracting(ComponentDto::uuid).containsExactlyInAnyOrder(project1.uuid(), project2.uuid()).doesNotContain(view.uuid(), application.uuid());
    }

    @Test
    public void select_projects_by_organization_does_not_return_branches() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertMainBranch(organization);
        ComponentDto branch = db.components().insertProjectBranch(project);
        assertThat(underTest.selectProjectsByOrganization(dbSession, organization.getUuid())).extracting(ComponentDto::uuid).containsExactlyInAnyOrder(project.uuid()).doesNotContain(branch.uuid());
    }

    @Test
    public void selectByQuery_provisioned() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto provisionedProject = db.components().insertPrivateProject(organization, ( p) -> p.setDbKey("provisioned.project").setName("Provisioned Project"));
        ComponentDto provisionedPortfolio = db.components().insertPrivatePortfolio(organization);
        SnapshotDto analyzedProject = db.components().insertProjectAndSnapshot(ComponentTesting.newPrivateProjectDto(organization));
        SnapshotDto analyzedDisabledProject = db.components().insertProjectAndSnapshot(ComponentTesting.newPrivateProjectDto(organization).setEnabled(false));
        SnapshotDto analyzedPortfolio = db.components().insertProjectAndSnapshot(ComponentTesting.newView(organization));
        Supplier<ComponentQuery.Builder> query = () -> ComponentQuery.builder().setQualifiers(PROJECT).setOnProvisionedOnly(true);
        assertThat(underTest.selectByQuery(dbSession, organization.getUuid(), query.get().build(), 0, 10)).extracting(ComponentDto::uuid).containsOnly(provisionedProject.uuid());
        // pagination
        assertThat(underTest.selectByQuery(dbSession, organization.getUuid(), query.get().build(), 2, 10)).isEmpty();
        // filter on qualifiers
        assertThat(underTest.selectByQuery(dbSession, organization.getUuid(), query.get().setQualifiers("XXX").build(), 0, 10)).isEmpty();
        assertThat(underTest.selectByQuery(dbSession, organization.getUuid(), query.get().setQualifiers(PROJECT, "XXX").build(), 0, 10)).extracting(ComponentDto::uuid).containsOnly(provisionedProject.uuid());
        assertThat(underTest.selectByQuery(dbSession, organization.getUuid(), query.get().setQualifiers(PROJECT, VIEW).build(), 0, 10)).extracting(ComponentDto::uuid).containsOnly(provisionedProject.uuid(), provisionedPortfolio.uuid());
        // match key
        assertThat(underTest.selectByQuery(dbSession, organization.getUuid(), query.get().setNameOrKeyQuery(provisionedProject.getDbKey()).build(), 0, 10)).extracting(ComponentDto::uuid).containsExactly(provisionedProject.uuid());
        assertThat(underTest.selectByQuery(dbSession, organization.getUuid(), query.get().setNameOrKeyQuery("pROvisiONed.proJEcT").setPartialMatchOnKey(true).build(), 0, 10)).extracting(ComponentDto::uuid).containsExactly(provisionedProject.uuid());
        assertThat(underTest.selectByQuery(dbSession, organization.getUuid(), query.get().setNameOrKeyQuery("missing").setPartialMatchOnKey(true).build(), 0, 10)).isEmpty();
        assertThat(underTest.selectByQuery(dbSession, organization.getUuid(), query.get().setNameOrKeyQuery("to be escaped \'\"\\%").setPartialMatchOnKey(true).build(), 0, 10)).isEmpty();
        // match name
        assertThat(underTest.selectByQuery(dbSession, organization.getUuid(), query.get().setNameOrKeyQuery("ned proj").setPartialMatchOnKey(true).build(), 0, 10)).extracting(ComponentDto::uuid).containsExactly(provisionedProject.uuid());
    }

    @Test
    public void selectByQuery_onProvisionedOnly_filters_projects_with_analysis_on_branch() {
        Supplier<ComponentQuery.Builder> query = () -> ComponentQuery.builder().setQualifiers(PROJECT).setOnProvisionedOnly(true);
        // the project does not have any analysis
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertMainBranch(organization);
        assertThat(underTest.selectByQuery(dbSession, organization.getUuid(), query.get().build(), 0, 10)).extracting(ComponentDto::uuid).containsOnly(project.uuid());
        // the project does not have analysis of main branch but only
        // analysis of non-main branches
        ComponentDto branchWithoutAnalysis = db.components().insertProjectBranch(project);
        ComponentDto branchWithAnalysis = db.components().insertProjectBranch(project);
        db.components().insertSnapshot(branchWithAnalysis);
        assertThat(underTest.selectByQuery(dbSession, organization.getUuid(), query.get().build(), 0, 10)).isEmpty();
    }

    @Test
    public void count_provisioned() {
        OrganizationDto organization = db.organizations().insert();
        db.components().insertPrivateProject(organization);
        db.components().insertProjectAndSnapshot(ComponentTesting.newPrivateProjectDto(organization));
        db.components().insertProjectAndSnapshot(ComponentTesting.newView(organization));
        Supplier<ComponentQuery.Builder> query = () -> ComponentQuery.builder().setOnProvisionedOnly(true);
        assertThat(underTest.countByQuery(dbSession, organization.getUuid(), query.get().setQualifiers(PROJECT).build())).isEqualTo(1);
        assertThat(underTest.countByQuery(dbSession, organization.getUuid(), query.get().setQualifiers(VIEW).build())).isEqualTo(0);
        assertThat(underTest.countByQuery(dbSession, organization.getUuid(), query.get().setQualifiers(PROJECT, VIEW).build())).isEqualTo(1);
    }

    @Test
    public void countByQuery_with_organization_throws_NPE_of_organizationUuid_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("organizationUuid can't be null");
        underTest.countByQuery(dbSession, null, ComponentDaoTest.ALL_PROJECTS_COMPONENT_QUERY);
    }

    @Test
    public void countByQuery_throws_IAE_if_too_many_component_ids() {
        Set<Long> ids = LongStream.range(0L, 1010L).boxed().collect(Collectors.toSet());
        ComponentQuery.Builder query = ComponentQuery.builder().setQualifiers(PROJECT).setComponentIds(ids);
        assertThatCountByQueryThrowsIAE(query, "Too many component ids in query");
    }

    @Test
    public void countByQuery_throws_IAE_if_too_many_component_keys() {
        Set<String> keys = IntStream.range(0, 1010).mapToObj(String::valueOf).collect(Collectors.toSet());
        ComponentQuery.Builder query = ComponentQuery.builder().setQualifiers(PROJECT).setComponentKeys(keys);
        assertThatCountByQueryThrowsIAE(query, "Too many component keys in query");
    }

    @Test
    public void countByQuery_throws_IAE_if_too_many_component_uuids() {
        Set<String> uuids = IntStream.range(0, 1010).mapToObj(String::valueOf).collect(Collectors.toSet());
        ComponentQuery.Builder query = ComponentQuery.builder().setQualifiers(PROJECT).setComponentUuids(uuids);
        assertThatCountByQueryThrowsIAE(query, "Too many component UUIDs in query");
    }

    @Test
    public void select_ghost_projects() {
        OrganizationDto organization = db.organizations().insert();
        // ghosts because has at least one snapshot with status U but none with status P
        ComponentDto ghostProject = db.components().insertPrivateProject(organization);
        db.components().insertSnapshot(ghostProject, ( dto) -> dto.setStatus("U"));
        db.components().insertSnapshot(ghostProject, ( dto) -> dto.setStatus("U"));
        ComponentDto ghostProject2 = db.components().insertPrivateProject(organization);
        db.components().insertSnapshot(ghostProject2, ( dto) -> dto.setStatus("U"));
        ComponentDto disabledGhostProject = db.components().insertPrivateProject(( dto) -> dto.setEnabled(false));
        db.components().insertSnapshot(disabledGhostProject, ( dto) -> dto.setStatus("U"));
        ComponentDto project1 = db.components().insertPrivateProject(organization);
        db.components().insertSnapshot(project1, ( dto) -> dto.setStatus("P"));
        db.components().insertSnapshot(project1, ( dto) -> dto.setStatus("U"));
        ComponentDto module = db.components().insertComponent(ComponentTesting.newModuleDto(project1));
        ComponentDto dir = db.components().insertComponent(ComponentTesting.newDirectory(module, "foo"));
        db.components().insertComponent(ComponentTesting.newFileDto(module, dir, "bar"));
        ComponentDto provisionedProject = db.components().insertPrivateProject(organization);
        // not a ghost because has at least one snapshot with status P
        ComponentDto project2 = db.components().insertPrivateProject(organization);
        db.components().insertSnapshot(project2, ( dto) -> dto.setStatus("P"));
        // not a ghost because it's not a project
        ComponentDto view = db.components().insertView(organization);
        db.components().insertSnapshot(view, ( dto) -> dto.setStatus("U"));
        db.components().insertComponent(ComponentTesting.newProjectCopy("do", project1, view));
        assertThat(underTest.selectGhostProjects(dbSession, organization.getUuid(), null, 0, 10)).extracting(ComponentDto::uuid).containsOnly(ghostProject.uuid(), ghostProject2.uuid(), disabledGhostProject.uuid());
        assertThat(underTest.countGhostProjects(dbSession, organization.getUuid(), null)).isEqualTo(3);
    }

    @Test
    public void dont_select_branch_ghost_projects() {
        OrganizationDto organization = db.organizations().insert();
        // ghosts because has at least one snapshot with status U but none with status P
        ComponentDto ghostProject = db.components().insertPrivateProject(organization);
        db.components().insertSnapshot(ghostProject, ( dto) -> dto.setStatus("U"));
        db.components().insertSnapshot(ghostProject, ( dto) -> dto.setStatus("U"));
        ComponentDto ghostBranchProject = db.components().insertProjectBranch(ghostProject);
        db.components().insertSnapshot(ghostBranchProject, ( dto) -> dto.setStatus("U"));
        assertThat(underTest.selectGhostProjects(dbSession, organization.getUuid(), null, 0, 10)).extracting(ComponentDto::uuid).containsOnly(ghostProject.uuid());
        assertThat(underTest.countGhostProjects(dbSession, organization.getUuid(), null)).isEqualTo(1);
    }

    @Test
    public void selectByProjectUuid() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto removedProject = db.components().insertPrivateProject(( p) -> p.setEnabled(false));
        ComponentDto module = db.components().insertComponent(ComponentTesting.newModuleDto(project));
        ComponentDto removedModule = db.components().insertComponent(ComponentTesting.newModuleDto(project).setEnabled(false));
        ComponentDto subModule = db.components().insertComponent(ComponentTesting.newModuleDto(module));
        ComponentDto removedSubModule = db.components().insertComponent(ComponentTesting.newModuleDto(module).setEnabled(false));
        ComponentDto directory = db.components().insertComponent(ComponentTesting.newDirectory(subModule, "src"));
        ComponentDto removedDirectory = db.components().insertComponent(ComponentTesting.newDirectory(subModule, "src2").setEnabled(false));
        ComponentDto file = db.components().insertComponent(ComponentTesting.newFileDto(subModule, directory));
        ComponentDto removedFile = db.components().insertComponent(ComponentTesting.newFileDto(subModule, directory).setEnabled(false));
        assertThat(underTest.selectByProjectUuid(project.uuid(), dbSession)).extracting(ComponentDto::uuid).containsExactlyInAnyOrder(project.uuid(), module.uuid(), removedModule.uuid(), subModule.uuid(), removedSubModule.uuid(), directory.uuid(), removedDirectory.uuid(), file.uuid(), removedFile.uuid());
    }

    @Test
    public void selectForIndexing_all() {
        assertSelectForIndexing(null).doesNotContain("DIS7").doesNotContain("COPY8").containsExactlyInAnyOrder("U1", "U2", "U3", "U4", "U5", "U6", "VW1");
    }

    @Test
    public void selectForIndexing_project() {
        assertSelectForIndexing("U1").doesNotContain("DIS7").doesNotContain("COPY8").containsExactlyInAnyOrder("U1", "U2", "U3", "U4");
    }

    @Test
    public void update() {
        db.components().insertPrivateProject(db.getDefaultOrganization(), "U1");
        underTest.update(dbSession, new ComponentUpdateDto().setUuid("U1").setBKey("key").setBCopyComponentUuid("copy").setBChanged(true).setBDescription("desc").setBEnabled(true).setBUuidPath("uuid_path").setBLanguage("lang").setBLongName("longName").setBModuleUuid("moduleUuid").setBModuleUuidPath("moduleUuidPath").setBName("name").setBPath("path").setBQualifier("qualifier"));
        dbSession.commit();
        Map<String, Object> row = selectBColumnsForUuid("U1");
        /* for Oracle */
        assertThat(row.get("bChanged")).isIn(true, 1L, 1);
        assertThat(row.get("bKey")).isEqualTo("key");
        assertThat(row.get("bCopyComponentUuid")).isEqualTo("copy");
        assertThat(row.get("bDescription")).isEqualTo("desc");
        /* for Oracle */
        assertThat(row.get("bEnabled")).isIn(true, 1L, 1);
        assertThat(row.get("bUuidPath")).isEqualTo("uuid_path");
        assertThat(row.get("bLanguage")).isEqualTo("lang");
        assertThat(row.get("bLongName")).isEqualTo("longName");
        assertThat(row.get("bModuleUuid")).isEqualTo("moduleUuid");
        assertThat(row.get("bModuleUuidPath")).isEqualTo("moduleUuidPath");
        assertThat(row.get("bName")).isEqualTo("name");
        assertThat(row.get("bPath")).isEqualTo("path");
        assertThat(row.get("bQualifier")).isEqualTo("qualifier");
    }

    @Test
    public void updateBEnabledToFalse() {
        ComponentDto dto1 = ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization(), "U1");
        ComponentDto dto2 = ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization(), "U2");
        ComponentDto dto3 = ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization(), "U3");
        underTest.insert(dbSession, dto1, dto2, dto3);
        underTest.updateBEnabledToFalse(dbSession, Arrays.asList("U1", "U2"));
        dbSession.commit();
        Map<String, Object> row1 = selectBColumnsForUuid("U1");
        /* for Oracle */
        assertThat(row1.get("bChanged")).isIn(true, 1L, 1);
        assertThat(row1.get("bKey")).isEqualTo(dto1.getDbKey());
        assertThat(row1.get("bCopyComponentUuid")).isEqualTo(dto1.getCopyResourceUuid());
        assertThat(row1.get("bDescription")).isEqualTo(dto1.description());
        /* for Oracle */
        assertThat(row1.get("bEnabled")).isIn(false, 0L, 0);
        assertThat(row1.get("bUuidPath")).isEqualTo(dto1.getUuidPath());
        assertThat(row1.get("bLanguage")).isEqualTo(dto1.language());
        assertThat(row1.get("bLongName")).isEqualTo(dto1.longName());
        assertThat(row1.get("bModuleUuid")).isEqualTo(dto1.moduleUuid());
        assertThat(row1.get("bModuleUuidPath")).isEqualTo(dto1.moduleUuidPath());
        assertThat(row1.get("bName")).isEqualTo(dto1.name());
        assertThat(row1.get("bPath")).isEqualTo(dto1.path());
        assertThat(row1.get("bQualifier")).isEqualTo(dto1.qualifier());
        Map<String, Object> row2 = selectBColumnsForUuid("U2");
        /* for Oracle */
        assertThat(row2.get("bChanged")).isIn(true, 1L, 1);
        assertThat(row2.get("bKey")).isEqualTo(dto2.getDbKey());
        assertThat(row2.get("bCopyComponentUuid")).isEqualTo(dto2.getCopyResourceUuid());
        assertThat(row2.get("bDescription")).isEqualTo(dto2.description());
        /* for Oracle */
        assertThat(row2.get("bEnabled")).isIn(false, 0L, 0);
        assertThat(row2.get("bUuidPath")).isEqualTo(dto2.getUuidPath());
        assertThat(row2.get("bLanguage")).isEqualTo(dto2.language());
        assertThat(row2.get("bLongName")).isEqualTo(dto2.longName());
        assertThat(row2.get("bModuleUuid")).isEqualTo(dto2.moduleUuid());
        assertThat(row2.get("bModuleUuidPath")).isEqualTo(dto2.moduleUuidPath());
        assertThat(row2.get("bName")).isEqualTo(dto2.name());
        assertThat(row2.get("bPath")).isEqualTo(dto2.path());
        assertThat(row2.get("bQualifier")).isEqualTo(dto2.qualifier());
        Map<String, Object> row3 = selectBColumnsForUuid("U3");
        /* for Oracle */
        assertThat(row3.get("bChanged")).isIn(false, 0L, 0);
    }

    @Test
    public void update_tags() {
        ComponentDto project = db.components().insertPrivateProject(( p) -> p.setTags(Collections.emptyList()));
        underTest.updateTags(dbSession, project.setTags(Lists.newArrayList("finance", "toto", "tutu")));
        dbSession.commit();
        assertThat(underTest.selectOrFailByKey(dbSession, project.getDbKey()).getTags()).containsOnly("finance", "toto", "tutu");
    }

    @Test
    public void delete() {
        ComponentDto project1 = db.components().insertPrivateProject(db.getDefaultOrganization(), ( t) -> t.setDbKey("PROJECT_1"));
        db.components().insertPrivateProject(db.getDefaultOrganization(), ( t) -> t.setDbKey("PROJECT_2"));
        underTest.delete(dbSession, project1.getId());
        dbSession.commit();
        assertThat(underTest.selectByKey(dbSession, "PROJECT_1")).isEmpty();
        assertThat(underTest.selectByKey(dbSession, "PROJECT_2")).isPresent();
    }

    @Test
    public void selectByQuery_with_organization_throws_NPE_of_organizationUuid_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("organizationUuid can't be null");
        underTest.selectByQuery(dbSession, null, ComponentDaoTest.ALL_PROJECTS_COMPONENT_QUERY, 1, 1);
    }

    @Test
    public void selectByQuery_throws_IAE_if_too_many_component_ids() {
        Set<Long> ids = LongStream.range(0L, 1010L).boxed().collect(Collectors.toSet());
        ComponentQuery.Builder query = ComponentQuery.builder().setQualifiers(PROJECT).setComponentIds(ids);
        assertThatSelectByQueryThrowsIAE(query, "Too many component ids in query");
    }

    @Test
    public void selectByQuery_throws_IAE_if_too_many_component_keys() {
        Set<String> keys = IntStream.range(0, 1010).mapToObj(String::valueOf).collect(Collectors.toSet());
        ComponentQuery.Builder query = ComponentQuery.builder().setQualifiers(PROJECT).setComponentKeys(keys);
        assertThatSelectByQueryThrowsIAE(query, "Too many component keys in query");
    }

    @Test
    public void selectByQuery_throws_IAE_if_too_many_component_uuids() {
        Set<String> uuids = IntStream.range(0, 1010).mapToObj(String::valueOf).collect(Collectors.toSet());
        ComponentQuery.Builder query = ComponentQuery.builder().setQualifiers(PROJECT).setComponentUuids(uuids);
        assertThatSelectByQueryThrowsIAE(query, "Too many component UUIDs in query");
    }

    @Test
    public void selectByQuery_with_paging_query_and_qualifiers() {
        OrganizationDto organizationDto = db.organizations().insert();
        db.components().insertProjectAndSnapshot(ComponentTesting.newPrivateProjectDto(organizationDto).setName("aaaa-name"));
        db.components().insertProjectAndSnapshot(ComponentTesting.newView(organizationDto));
        for (int i = 9; i >= 1; i--) {
            db.components().insertProjectAndSnapshot(ComponentTesting.newPrivateProjectDto(organizationDto).setName(("project-" + i)));
        }
        ComponentQuery query = ComponentQuery.builder().setNameOrKeyQuery("oJect").setQualifiers(PROJECT).build();
        List<ComponentDto> result = underTest.selectByQuery(dbSession, query, 1, 3);
        int count = underTest.countByQuery(dbSession, query);
        assertThat(result).hasSize(3);
        assertThat(count).isEqualTo(9);
        assertThat(result).extracting(ComponentDto::name).containsExactly("project-2", "project-3", "project-4");
        assertThat(result).extracting(ComponentDto::getOrganizationUuid).containsOnly(organizationDto.getUuid());
    }

    @Test
    public void selectByQuery_with_organization_filters_on_specified_organization() {
        OrganizationDto organization1 = db.organizations().insert();
        OrganizationDto organization2 = db.organizations().insert();
        ComponentDto project1 = db.components().insertPrivateProject(organization1);
        ComponentDto project2 = db.components().insertPrivateProject(organization2);
        assertThat(underTest.selectByQuery(dbSession, ComponentDaoTest.ALL_PROJECTS_COMPONENT_QUERY, 0, 2)).extracting(ComponentDto::uuid).containsOnly(project1.uuid(), project2.uuid());
        assertThat(underTest.selectByQuery(dbSession, organization1.getUuid(), ComponentDaoTest.ALL_PROJECTS_COMPONENT_QUERY, 0, 2)).extracting(ComponentDto::uuid).containsOnly(project1.uuid());
        assertThat(underTest.selectByQuery(dbSession, organization2.getUuid(), ComponentDaoTest.ALL_PROJECTS_COMPONENT_QUERY, 0, 2)).extracting(ComponentDto::uuid).containsOnly(project2.uuid());
        assertThat(underTest.selectByQuery(dbSession, "non existent organization uuid", ComponentDaoTest.ALL_PROJECTS_COMPONENT_QUERY, 0, 2)).isEmpty();
    }

    @Test
    public void selectByQuery_should_not_return_branches() {
        ComponentDto main = db.components().insertMainBranch();
        ComponentDto branch = db.components().insertProjectBranch(main);
        assertThat(underTest.selectByQuery(dbSession, ComponentDaoTest.ALL_PROJECTS_COMPONENT_QUERY, 0, 2)).hasSize(1);
        assertThat(underTest.selectByQuery(dbSession, ComponentDaoTest.ALL_PROJECTS_COMPONENT_QUERY, 0, 2).get(0).uuid()).isEqualTo(main.uuid());
    }

    @Test
    public void countByQuery_should_not_include_branches() {
        ComponentDto main = db.components().insertMainBranch();
        ComponentDto branch = db.components().insertProjectBranch(main);
        assertThat(underTest.countByQuery(dbSession, ComponentDaoTest.ALL_PROJECTS_COMPONENT_QUERY)).isEqualTo(1);
    }

    @Test
    public void countByQuery_with_organization_filters_on_specified_organization() {
        OrganizationDto organization1 = db.organizations().insert();
        OrganizationDto organization2 = db.organizations().insert();
        ComponentDto project1 = db.components().insertPrivateProject(organization1);
        ComponentDto project2 = db.components().insertPrivateProject(organization2);
        assertThat(underTest.countByQuery(dbSession, ComponentDaoTest.ALL_PROJECTS_COMPONENT_QUERY)).isEqualTo(2);
        assertThat(underTest.countByQuery(dbSession, organization1.getUuid(), ComponentDaoTest.ALL_PROJECTS_COMPONENT_QUERY)).isEqualTo(1);
        assertThat(underTest.countByQuery(dbSession, organization2.getUuid(), ComponentDaoTest.ALL_PROJECTS_COMPONENT_QUERY)).isEqualTo(1);
        assertThat(underTest.countByQuery(dbSession, "non existent organization uuid", ComponentDaoTest.ALL_PROJECTS_COMPONENT_QUERY)).isEqualTo(0);
    }

    @Test
    public void selectByQuery_name_with_special_characters() {
        db.components().insertProjectAndSnapshot(ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setName("project-\\_%/-name"));
        ComponentQuery query = ComponentQuery.builder().setNameOrKeyQuery("-\\_%/-").setQualifiers(PROJECT).build();
        List<ComponentDto> result = underTest.selectByQuery(dbSession, query, 0, 10);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("project-\\_%/-name");
    }

    @Test
    public void selectByQuery_key_with_special_characters() {
        db.components().insertProjectAndSnapshot(ComponentTesting.newPrivateProjectDto(db.organizations().insert()).setDbKey("project-_%-key"));
        db.components().insertProjectAndSnapshot(ComponentTesting.newPrivateProjectDto(db.organizations().insert()).setDbKey("project-key-that-does-not-match"));
        ComponentQuery query = ComponentQuery.builder().setNameOrKeyQuery("project-_%-key").setQualifiers(PROJECT).build();
        List<ComponentDto> result = underTest.selectByQuery(dbSession, query, 0, 10);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDbKey()).isEqualTo("project-_%-key");
    }

    @Test
    public void selectByQuery_on_key_partial_match_case_insensitive() {
        db.components().insertProjectAndSnapshot(ComponentTesting.newPrivateProjectDto(db.organizations().insert()).setDbKey("project-key"));
        ComponentQuery query = ComponentQuery.builder().setNameOrKeyQuery("JECT-K").setPartialMatchOnKey(true).setQualifiers(PROJECT).build();
        List<ComponentDto> result = underTest.selectByQuery(dbSession, query, 0, 10);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDbKey()).isEqualTo("project-key");
    }

    @Test
    public void selectByQuery_filter_last_analysis_date() {
        long aLongTimeAgo = 1000000000L;
        long recentTime = 3000000000L;
        ComponentDto oldProject = db.components().insertPrivateProject();
        db.components().insertSnapshot(oldProject, ( s) -> s.setCreatedAt(aLongTimeAgo));
        ComponentDto recentProject = db.components().insertPrivateProject();
        db.components().insertSnapshot(recentProject, ( s) -> s.setCreatedAt(recentTime).setLast(true));
        db.components().insertSnapshot(recentProject, ( s) -> s.setCreatedAt(aLongTimeAgo).setLast(false));
        // before date on main branch
        assertThat(selectProjectUuidsByQuery(( q) -> q.setAnalyzedBefore(recentTime))).containsExactlyInAnyOrder(oldProject.uuid());
        assertThat(selectProjectUuidsByQuery(( q) -> q.setAnalyzedBefore(aLongTimeAgo))).isEmpty();
        assertThat(selectProjectUuidsByQuery(( q) -> q.setAnalyzedBefore((recentTime + 1000L)))).containsExactlyInAnyOrder(oldProject.uuid(), recentProject.uuid());
        // before date on any branch
        assertThat(selectProjectUuidsByQuery(( q) -> q.setAnyBranchAnalyzedBefore(recentTime))).containsExactlyInAnyOrder(oldProject.uuid());
        assertThat(selectProjectUuidsByQuery(( q) -> q.setAnyBranchAnalyzedBefore(aLongTimeAgo))).isEmpty();
        assertThat(selectProjectUuidsByQuery(( q) -> q.setAnyBranchAnalyzedBefore((recentTime + 1000L)))).containsExactlyInAnyOrder(oldProject.uuid(), recentProject.uuid());
        // after date
        assertThat(selectProjectUuidsByQuery(( q) -> q.setAnyBranchAnalyzedAfter((recentTime - 1000L)))).containsExactlyInAnyOrder(recentProject.uuid());
        assertThat(selectProjectUuidsByQuery(( q) -> q.setAnyBranchAnalyzedAfter((recentTime + 1000L)))).isEmpty();
        assertThat(selectProjectUuidsByQuery(( q) -> q.setAnyBranchAnalyzedAfter(aLongTimeAgo))).containsExactlyInAnyOrder(oldProject.uuid(), recentProject.uuid());
    }

    @Test
    public void selectByQuery_filter_last_analysis_date_on_non_main_branches() {
        long aLongTimeAgo = 1000000000L;
        long recentTime = 3000000000L;
        // project with only a non-main and old analyzed branch
        ComponentDto oldProject = db.components().insertMainBranch();
        ComponentDto oldProjectBranch = db.components().insertProjectBranch(oldProject, ComponentTesting.newBranchDto(oldProject).setBranchType(SHORT));
        db.components().insertSnapshot(oldProjectBranch, ( s) -> s.setLast(true).setCreatedAt(aLongTimeAgo));
        // project with only a old main branch and a recent non-main branch
        ComponentDto recentProject = db.components().insertMainBranch();
        ComponentDto recentProjectBranch = db.components().insertProjectBranch(recentProject, ComponentTesting.newBranchDto(recentProject).setBranchType(SHORT));
        db.components().insertSnapshot(recentProjectBranch, ( s) -> s.setCreatedAt(recentTime).setLast(true));
        db.components().insertSnapshot(recentProjectBranch, ( s) -> s.setCreatedAt(aLongTimeAgo).setLast(false));
        // before date on main branch only
        assertThat(selectProjectUuidsByQuery(( q) -> q.setAnalyzedBefore(recentTime))).isEmpty();
        assertThat(selectProjectUuidsByQuery(( q) -> q.setAnalyzedBefore(aLongTimeAgo))).isEmpty();
        assertThat(selectProjectUuidsByQuery(( q) -> q.setAnalyzedBefore((recentTime + 1000L)))).isEmpty();
        // before date on any branch
        assertThat(selectProjectUuidsByQuery(( q) -> q.setAnyBranchAnalyzedBefore(recentTime))).containsExactlyInAnyOrder(oldProject.uuid());
        assertThat(selectProjectUuidsByQuery(( q) -> q.setAnyBranchAnalyzedBefore(aLongTimeAgo))).isEmpty();
        assertThat(selectProjectUuidsByQuery(( q) -> q.setAnyBranchAnalyzedBefore((recentTime + 1000L)))).containsExactlyInAnyOrder(oldProject.uuid(), recentProject.uuid());
        // after date
        assertThat(selectProjectUuidsByQuery(( q) -> q.setAnyBranchAnalyzedAfter((recentTime - 1000L)))).containsExactlyInAnyOrder(recentProject.uuid());
        assertThat(selectProjectUuidsByQuery(( q) -> q.setAnyBranchAnalyzedAfter((recentTime + 1000L)))).isEmpty();
        assertThat(selectProjectUuidsByQuery(( q) -> q.setAnyBranchAnalyzedAfter(aLongTimeAgo))).containsExactlyInAnyOrder(oldProject.uuid(), recentProject.uuid());
    }

    @Test
    public void selectByQuery_filter_created_at() {
        ComponentDto project1 = db.components().insertPrivateProject(( p) -> p.setCreatedAt(parseDate("2018-02-01")));
        ComponentDto project2 = db.components().insertPrivateProject(( p) -> p.setCreatedAt(parseDate("2018-06-01")));
        assertThat(selectProjectUuidsByQuery(( q) -> q.setCreatedAfter(parseDate("2017-12-01")))).containsExactlyInAnyOrder(project1.uuid(), project2.uuid());
        assertThat(selectProjectUuidsByQuery(( q) -> q.setCreatedAfter(parseDate("2018-02-20")))).containsExactlyInAnyOrder(project2.uuid());
        assertThat(selectProjectUuidsByQuery(( q) -> q.setCreatedAfter(parseDate("2019-01-01")))).isEmpty();
    }

    @Test
    public void selectByQuery_filter_on_visibility() {
        db.components().insertComponent(ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization()).setDbKey("private-key"));
        db.components().insertComponent(ComponentTesting.newPublicProjectDto(db.getDefaultOrganization()).setDbKey("public-key"));
        ComponentQuery privateProjectsQuery = ComponentQuery.builder().setPrivate(true).setQualifiers(PROJECT).build();
        ComponentQuery publicProjectsQuery = ComponentQuery.builder().setPrivate(false).setQualifiers(PROJECT).build();
        ComponentQuery allProjectsQuery = ComponentQuery.builder().setPrivate(null).setQualifiers(PROJECT).build();
        assertThat(underTest.selectByQuery(dbSession, privateProjectsQuery, 0, 10)).extracting(ComponentDto::getDbKey).containsExactly("private-key");
        assertThat(underTest.selectByQuery(dbSession, publicProjectsQuery, 0, 10)).extracting(ComponentDto::getDbKey).containsExactly("public-key");
        assertThat(underTest.selectByQuery(dbSession, allProjectsQuery, 0, 10)).extracting(ComponentDto::getDbKey).containsOnly("public-key", "private-key");
    }

    @Test
    public void selectByQuery_on_empty_list_of_component_id() {
        db.components().insertPrivateProject();
        ComponentQuery dbQuery = ComponentQuery.builder().setQualifiers(PROJECT).setComponentIds(Collections.emptySet()).build();
        List<ComponentDto> result = underTest.selectByQuery(dbSession, dbQuery, 0, 10);
        int count = underTest.countByQuery(dbSession, dbQuery);
        assertThat(result).isEmpty();
        assertThat(count).isEqualTo(0);
    }

    @Test
    public void selectByQuery_on_component_ids() {
        OrganizationDto organizationDto = db.organizations().insert();
        ComponentDto sonarqube = db.components().insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto));
        ComponentDto jdk8 = db.components().insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto));
        ComponentDto cLang = db.components().insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto));
        ComponentQuery query = ComponentQuery.builder().setQualifiers(PROJECT).setComponentIds(Sets.newHashSet(sonarqube.getId(), jdk8.getId())).build();
        List<ComponentDto> result = underTest.selectByQuery(dbSession, query, 0, 10);
        assertThat(result).hasSize(2).extracting(ComponentDto::getId).containsOnlyOnce(sonarqube.getId(), jdk8.getId()).doesNotContain(cLang.getId());
    }

    @Test
    public void selectByQuery_on_empty_list_of_component_key() {
        db.components().insertPrivateProject();
        ComponentQuery dbQuery = ComponentQuery.builder().setQualifiers(PROJECT).setComponentKeys(Collections.emptySet()).build();
        List<ComponentDto> result = underTest.selectByQuery(dbSession, dbQuery, 0, 10);
        int count = underTest.countByQuery(dbSession, dbQuery);
        assertThat(result).isEmpty();
        assertThat(count).isEqualTo(0);
    }

    @Test
    public void selectByQuery_on_component_keys() {
        OrganizationDto organizationDto = db.organizations().insert();
        ComponentDto sonarqube = db.components().insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto));
        ComponentDto jdk8 = db.components().insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto));
        ComponentDto cLang = db.components().insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto));
        ComponentQuery query = ComponentQuery.builder().setQualifiers(PROJECT).setComponentKeys(Sets.newHashSet(sonarqube.getDbKey(), jdk8.getDbKey())).build();
        List<ComponentDto> result = underTest.selectByQuery(dbSession, query, 0, 10);
        assertThat(result).hasSize(2).extracting(ComponentDto::getDbKey).containsExactlyInAnyOrder(sonarqube.getDbKey(), jdk8.getDbKey()).doesNotContain(cLang.getDbKey());
    }

    @Test
    public void selectByQuery_on_empty_list_of_component_uuids() {
        db.components().insertPrivateProject();
        ComponentQuery dbQuery = ComponentQuery.builder().setQualifiers(PROJECT).setComponentUuids(Collections.emptySet()).build();
        List<ComponentDto> result = underTest.selectByQuery(dbSession, dbQuery, 0, 10);
        int count = underTest.countByQuery(dbSession, dbQuery);
        assertThat(result).isEmpty();
        assertThat(count).isEqualTo(0);
    }

    @Test
    public void selectByQuery_on_component_uuids() {
        OrganizationDto organizationDto = db.organizations().insert();
        ComponentDto sonarqube = db.components().insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto));
        ComponentDto jdk8 = db.components().insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto));
        ComponentDto cLang = db.components().insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto));
        ComponentQuery query = ComponentQuery.builder().setQualifiers(PROJECT).setComponentUuids(Sets.newHashSet(sonarqube.uuid(), jdk8.uuid())).build();
        List<ComponentDto> result = underTest.selectByQuery(dbSession, query, 0, 10);
        assertThat(result).hasSize(2).extracting(ComponentDto::uuid).containsOnlyOnce(sonarqube.uuid(), jdk8.uuid()).doesNotContain(cLang.uuid());
    }

    @Test
    public void selectAncestors() {
        // organization
        OrganizationDto organization = db.organizations().insert();
        // project -> module -> file
        ComponentDto project = ComponentTesting.newPrivateProjectDto(organization, ComponentDaoTest.PROJECT_UUID);
        db.components().insertProjectAndSnapshot(project);
        ComponentDto module = ComponentTesting.newModuleDto(ComponentDaoTest.MODULE_UUID, project);
        db.components().insertComponent(module);
        ComponentDto file = ComponentTesting.newFileDto(module, null, ComponentDaoTest.FILE_1_UUID);
        db.components().insertComponent(file);
        db.commit();
        // ancestors of root
        List<ComponentDto> ancestors = underTest.selectAncestors(dbSession, project);
        assertThat(ancestors).isEmpty();
        // ancestors of module
        ancestors = underTest.selectAncestors(dbSession, module);
        assertThat(ancestors).extracting("uuid").containsExactly(ComponentDaoTest.PROJECT_UUID);
        // ancestors of file
        ancestors = underTest.selectAncestors(dbSession, file);
        assertThat(ancestors).extracting("uuid").containsExactly(ComponentDaoTest.PROJECT_UUID, ComponentDaoTest.MODULE_UUID);
    }

    @Test
    public void select_descendants_with_children_strategy() {
        // project has 2 children: module and file 1. Other files are part of module.
        ComponentDto project = ComponentTesting.newPrivateProjectDto(db.organizations().insert(), ComponentDaoTest.PROJECT_UUID);
        db.components().insertProjectAndSnapshot(project);
        ComponentDto module = ComponentTesting.newModuleDto(ComponentDaoTest.MODULE_UUID, project);
        db.components().insertComponent(module);
        ComponentDto fileInProject = ComponentTesting.newFileDto(project, null, ComponentDaoTest.FILE_1_UUID).setDbKey("file-key-1").setName("File One");
        db.components().insertComponent(fileInProject);
        ComponentDto file1InModule = ComponentTesting.newFileDto(module, null, ComponentDaoTest.FILE_2_UUID).setDbKey("file-key-2").setName("File Two");
        db.components().insertComponent(file1InModule);
        ComponentDto file2InModule = ComponentTesting.newFileDto(module, null, ComponentDaoTest.FILE_3_UUID).setDbKey("file-key-3").setName("File Three");
        db.components().insertComponent(file2InModule);
        db.commit();
        // test children of root
        ComponentTreeQuery query = ComponentDaoTest.newTreeQuery(ComponentDaoTest.PROJECT_UUID).build();
        List<ComponentDto> children = underTest.selectDescendants(dbSession, query);
        assertThat(children).extracting("uuid").containsOnly(ComponentDaoTest.FILE_1_UUID, ComponentDaoTest.MODULE_UUID);
        // test children of root, filtered by qualifier
        query = ComponentDaoTest.newTreeQuery(ComponentDaoTest.PROJECT_UUID).setQualifiers(Arrays.asList(MODULE)).build();
        children = underTest.selectDescendants(dbSession, query);
        assertThat(children).extracting("uuid").containsOnly(ComponentDaoTest.MODULE_UUID);
        // test children of intermediate component (module here), default ordering by
        query = ComponentDaoTest.newTreeQuery(ComponentDaoTest.MODULE_UUID).build();
        assertThat(underTest.selectDescendants(dbSession, query)).extracting("uuid").containsOnly(ComponentDaoTest.FILE_2_UUID, ComponentDaoTest.FILE_3_UUID);
        // test children of leaf component (file here)
        query = ComponentDaoTest.newTreeQuery(ComponentDaoTest.FILE_1_UUID).build();
        assertThat(underTest.selectDescendants(dbSession, query)).isEmpty();
        // test children of root, matching name
        query = ComponentDaoTest.newTreeQuery(ComponentDaoTest.PROJECT_UUID).setNameOrKeyQuery("One").build();
        assertThat(underTest.selectDescendants(dbSession, query)).extracting("uuid").containsOnly(ComponentDaoTest.FILE_1_UUID);
        // test children of root, matching case-insensitive name
        query = ComponentDaoTest.newTreeQuery(ComponentDaoTest.PROJECT_UUID).setNameOrKeyQuery("OnE").build();
        assertThat(underTest.selectDescendants(dbSession, query)).extracting("uuid").containsOnly(ComponentDaoTest.FILE_1_UUID);
        // test children of root, matching key
        query = ComponentDaoTest.newTreeQuery(ComponentDaoTest.PROJECT_UUID).setNameOrKeyQuery("file-key-1").build();
        assertThat(underTest.selectDescendants(dbSession, query)).extracting("uuid").containsOnly(ComponentDaoTest.FILE_1_UUID);
        // test children of root, without matching name nor key
        query = ComponentDaoTest.newTreeQuery(ComponentDaoTest.PROJECT_UUID).setNameOrKeyQuery("does-not-exist").build();
        assertThat(underTest.selectDescendants(dbSession, query)).isEmpty();
        // test children of intermediate component (module here), matching name
        query = ComponentDaoTest.newTreeQuery(ComponentDaoTest.MODULE_UUID).setNameOrKeyQuery("Two").build();
        assertThat(underTest.selectDescendants(dbSession, query)).extracting("uuid").containsOnly(ComponentDaoTest.FILE_2_UUID);
        // test children of intermediate component (module here), without matching name
        query = ComponentDaoTest.newTreeQuery(ComponentDaoTest.MODULE_UUID).setNameOrKeyQuery("does-not-exist").build();
        assertThat(underTest.selectDescendants(dbSession, query)).isEmpty();
        // test children of leaf component (file here)
        query = ComponentDaoTest.newTreeQuery(ComponentDaoTest.FILE_1_UUID).build();
        assertThat(underTest.selectDescendants(dbSession, query)).isEmpty();
        // test children of leaf component (file here), matching name
        query = ComponentDaoTest.newTreeQuery(ComponentDaoTest.FILE_1_UUID).setNameOrKeyQuery("Foo").build();
        assertThat(underTest.selectDescendants(dbSession, query)).isEmpty();
        // test filtering by scope
        query = ComponentDaoTest.newTreeQuery(project.uuid()).setScopes(Arrays.asList(FILE)).build();
        assertThat(underTest.selectDescendants(dbSession, query)).extracting(ComponentDto::uuid).containsExactlyInAnyOrder(fileInProject.uuid());
        query = ComponentDaoTest.newTreeQuery(project.uuid()).setScopes(Arrays.asList(Scopes.PROJECT)).build();
        assertThat(underTest.selectDescendants(dbSession, query)).extracting(ComponentDto::uuid).containsExactlyInAnyOrder(module.uuid());
    }

    @Test
    public void select_descendants_with_leaves_strategy() {
        ComponentDto project = ComponentTesting.newPrivateProjectDto(db.getDefaultOrganization(), ComponentDaoTest.PROJECT_UUID);
        db.components().insertProjectAndSnapshot(project);
        db.components().insertComponent(ComponentTesting.newModuleDto("module-1-uuid", project));
        db.components().insertComponent(ComponentTesting.newFileDto(project, null, "file-1-uuid"));
        db.components().insertComponent(ComponentTesting.newFileDto(project, null, "file-2-uuid"));
        db.commit();
        ComponentTreeQuery query = ComponentDaoTest.newTreeQuery(ComponentDaoTest.PROJECT_UUID).setStrategy(Strategy.LEAVES).build();
        List<ComponentDto> result = underTest.selectDescendants(dbSession, query);
        assertThat(result).extracting("uuid").containsOnly("file-1-uuid", "file-2-uuid", "module-1-uuid");
    }

    @Test
    public void select_descendants_returns_empty_list_if_base_component_does_not_exist() {
        ComponentTreeQuery query = ComponentDaoTest.newTreeQuery(ComponentDaoTest.PROJECT_UUID).setStrategy(Strategy.CHILDREN).build();
        List<ComponentDto> result = underTest.selectDescendants(dbSession, query);
        assertThat(result).isEmpty();
    }

    @Test
    public void select_descendants_of_a_view_and_filter_by_name() {
        OrganizationDto organizationDto = db.organizations().insert();
        ComponentDto view = ComponentTesting.newView(organizationDto, ComponentDaoTest.A_VIEW_UUID);
        db.components().insertViewAndSnapshot(view);
        // one subview
        ComponentDto subView = ComponentTesting.newSubView(view, "subview-uuid", "subview-key").setName("subview name");
        db.components().insertComponent(subView);
        // one project and its copy linked to the view
        ComponentDto project = ComponentTesting.newPrivateProjectDto(organizationDto, ComponentDaoTest.PROJECT_UUID).setName("project name");
        db.components().insertProjectAndSnapshot(project);
        db.components().insertComponent(ComponentTesting.newProjectCopy("project-copy-uuid", project, view));
        ComponentTreeQuery dbQuery = ComponentDaoTest.newTreeQuery(ComponentDaoTest.A_VIEW_UUID).setNameOrKeyQuery("name").setStrategy(Strategy.CHILDREN).build();
        List<ComponentDto> components = underTest.selectDescendants(dbSession, dbQuery);
        assertThat(components).extracting("uuid").containsOnly("project-copy-uuid", "subview-uuid");
        assertThat(components).extracting("organizationUuid").containsOnly(organizationDto.getUuid());
    }

    @Test
    public void select_projects_by_name_ignore_branches() {
        OrganizationDto organizationDto = db.organizations().insert();
        ComponentDto project1 = db.components().insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto).setName("project1"));
        ComponentDto module1 = db.components().insertComponent(ComponentTesting.newModuleDto(project1).setName("project1"));
        ComponentDto subModule1 = db.components().insertComponent(ComponentTesting.newModuleDto(module1).setName("project1"));
        db.components().insertComponent(ComponentTesting.newFileDto(subModule1).setName("project1"));
        db.components().insertProjectBranch(project1, ( b) -> b.setKey("branch1"));
        // check that branch is present with same name as main branch
        assertThat(underTest.selectByKeyAndBranch(dbSession, project1.getKey(), "branch1").get().name()).isEqualTo("project1");
        // branch is not returned
        assertThat(underTest.selectProjectsByNameQuery(dbSession, null, false)).extracting(ComponentDto::uuid).containsOnly(project1.uuid());
        assertThat(underTest.selectProjectsByNameQuery(dbSession, "project", false)).extracting(ComponentDto::uuid).containsOnly(project1.uuid());
    }

    @Test
    public void select_projects_by_name_query() {
        OrganizationDto organizationDto = db.organizations().insert();
        ComponentDto project1 = db.components().insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto).setName("project1"));
        ComponentDto module1 = db.components().insertComponent(ComponentTesting.newModuleDto(project1).setName("module1"));
        ComponentDto subModule1 = db.components().insertComponent(ComponentTesting.newModuleDto(module1).setName("subModule1"));
        db.components().insertComponent(ComponentTesting.newFileDto(subModule1).setName("file"));
        ComponentDto project2 = db.components().insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto).setName("project2"));
        ComponentDto project3 = db.components().insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto).setName("project3"));
        assertThat(underTest.selectProjectsByNameQuery(dbSession, null, false)).extracting(ComponentDto::uuid).containsOnly(project1.uuid(), project2.uuid(), project3.uuid());
        assertThat(underTest.selectProjectsByNameQuery(dbSession, null, true)).extracting(ComponentDto::uuid).containsOnly(project1.uuid(), project2.uuid(), project3.uuid(), module1.uuid(), subModule1.uuid());
        assertThat(underTest.selectProjectsByNameQuery(dbSession, "project1", false)).extracting(ComponentDto::uuid).containsOnly(project1.uuid());
        assertThat(underTest.selectProjectsByNameQuery(dbSession, "ct1", false)).extracting(ComponentDto::uuid).containsOnly(project1.uuid());
        assertThat(underTest.selectProjectsByNameQuery(dbSession, "pro", false)).extracting(ComponentDto::uuid).containsOnly(project1.uuid(), project2.uuid(), project3.uuid());
        assertThat(underTest.selectProjectsByNameQuery(dbSession, "jec", false)).extracting(ComponentDto::uuid).containsOnly(project1.uuid(), project2.uuid(), project3.uuid());
        assertThat(underTest.selectProjectsByNameQuery(dbSession, "1", true)).extracting(ComponentDto::uuid).containsOnly(project1.uuid(), module1.uuid(), subModule1.uuid());
        assertThat(underTest.selectProjectsByNameQuery(dbSession, "unknown", true)).extracting(ComponentDto::uuid).isEmpty();
    }

    @Test
    public void setPrivateForRootComponentUuid_updates_private_column_to_specified_value_for_all_rows_with_specified_projectUuid() {
        String uuid1 = "uuid1";
        String uuid2 = "uuid2";
        OrganizationDto organizationDto = db.organizations().insert();
        String[] uuids = new String[]{ db.components().insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto).setProjectUuid(uuid1).setPrivate(true)).uuid(), db.components().insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto).setProjectUuid(uuid1).setPrivate(false)).uuid(), db.components().insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto).setProjectUuid(uuid2).setPrivate(true)).uuid(), db.components().insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto).setProjectUuid(uuid2).setPrivate(false)).uuid(), db.components().insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto).setRootUuid(uuid1).setProjectUuid("foo").setPrivate(false)).uuid() };
        underTest.setPrivateForRootComponentUuid(db.getSession(), uuid1, true);
        assertThat(privateFlagOfUuid(uuids[0])).isTrue();
        assertThat(privateFlagOfUuid(uuids[1])).isTrue();
        assertThat(privateFlagOfUuid(uuids[2])).isTrue();
        assertThat(privateFlagOfUuid(uuids[3])).isFalse();
        assertThat(privateFlagOfUuid(uuids[4])).isFalse();
        underTest.setPrivateForRootComponentUuid(db.getSession(), uuid1, false);
        assertThat(privateFlagOfUuid(uuids[0])).isFalse();
        assertThat(privateFlagOfUuid(uuids[1])).isFalse();
        assertThat(privateFlagOfUuid(uuids[2])).isTrue();
        assertThat(privateFlagOfUuid(uuids[3])).isFalse();
        assertThat(privateFlagOfUuid(uuids[4])).isFalse();
        underTest.setPrivateForRootComponentUuid(db.getSession(), uuid2, false);
        assertThat(privateFlagOfUuid(uuids[0])).isFalse();
        assertThat(privateFlagOfUuid(uuids[1])).isFalse();
        assertThat(privateFlagOfUuid(uuids[2])).isFalse();
        assertThat(privateFlagOfUuid(uuids[3])).isFalse();
        assertThat(privateFlagOfUuid(uuids[4])).isFalse();
        underTest.setPrivateForRootComponentUuid(db.getSession(), uuid2, true);
        assertThat(privateFlagOfUuid(uuids[0])).isFalse();
        assertThat(privateFlagOfUuid(uuids[1])).isFalse();
        assertThat(privateFlagOfUuid(uuids[2])).isTrue();
        assertThat(privateFlagOfUuid(uuids[3])).isTrue();
        assertThat(privateFlagOfUuid(uuids[4])).isFalse();
    }

    @Test
    public void selectPrivateProjectsWithNcloc() {
        MetricDto metric = db.measures().insertMetric(( m) -> m.setKey("ncloc"));
        OrganizationDto organizationDto = db.organizations().insert();
        // project1, not the biggest branch - not returned
        final ComponentDto project1 = db.components().insertMainBranch(organizationDto, ( b) -> b.setName("foo"));
        insertMeasure(20.0, project1, metric);
        // long branch of project1 - returned
        insertMeasure(30.0, db.components().insertProjectBranch(project1, ( b) -> b.setBranchType(BranchType.LONG)), metric);
        // project2 - returned
        insertMeasure(10.0, db.components().insertMainBranch(organizationDto, ( b) -> b.setName("bar")), metric);
        // public project - not returned
        insertMeasure(11.0, db.components().insertMainBranch(organizationDto, ( b) -> b.setPrivate(false)), metric);
        // different org - not returned
        insertMeasure(12.0, db.components().insertMainBranch(db.organizations().insert()), metric);
        List<ProjectNclocDistributionDto> result = underTest.selectPrivateProjectsWithNcloc(db.getSession(), organizationDto.getUuid());
        assertThat(result).extracting(ProjectNclocDistributionDto::getName).containsExactly("foo", "bar");
        assertThat(result).extracting(ProjectNclocDistributionDto::getNcloc).containsExactly(30L, 10L);
    }
}

