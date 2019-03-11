/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.config;


import Authorization.NAME;
import Authorization.PRIVILEGES;
import Authorization.TYPE;
import BasicPipelineConfigs.AUTHORIZATION;
import BasicPipelineConfigs.DEFAULT_GROUP;
import BasicPipelineConfigs.GROUP;
import BasicPipelineConfigs.NO_REMOTE_AUTHORIZATION;
import com.thoughtworks.go.config.remote.FileConfigOrigin;
import com.thoughtworks.go.config.remote.RepoConfigOrigin;
import com.thoughtworks.go.domain.config.Admin;
import com.thoughtworks.go.helper.PipelineConfigMother;
import com.thoughtworks.go.util.DataStructureUtils;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public abstract class PipelineConfigsTestBase {
    @Test
    public void shouldReturnTrueIfPipelineExist() {
        PipelineConfig pipelineConfig = PipelineConfigMother.pipelineConfig("pipeline1");
        PipelineConfigs configs = createWithPipeline(pipelineConfig);
        Assert.assertThat("shouldReturnTrueIfPipelineExist", configs.hasPipeline(new CaseInsensitiveString("pipeline1")), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseIfPipelineNotExist() {
        PipelineConfigs configs = createWithPipeline(PipelineConfigMother.pipelineConfig("pipeline1"));
        Assert.assertThat("shouldReturnFalseIfPipelineNotExist", configs.hasPipeline(new CaseInsensitiveString("not-exist")), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueIfAuthorizationIsNotDefined() {
        Assert.assertThat(createEmpty().hasViewPermission(new CaseInsensitiveString("anyone"), null), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseIfViewPermissionIsNotDefined() {
        PipelineConfigs group = createWithPipeline(PipelineConfigMother.pipelineConfig("pipeline1"));
        group.getAuthorization().getOperationConfig().add(new AdminUser(new CaseInsensitiveString("jez")));
        Assert.assertThat(group.hasViewPermission(new CaseInsensitiveString("jez"), null), Matchers.is(false));
    }

    @Test
    public void shouldReturnFalseIfUserDoesNotHaveViewPermission() {
        PipelineConfigs group = createWithPipeline(PipelineConfigMother.pipelineConfig("pipeline1"));
        group.getAuthorization().getViewConfig().add(new AdminUser(new CaseInsensitiveString("jez")));
        Assert.assertThat(group.hasViewPermission(new CaseInsensitiveString("anyone"), null), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueIfUserHasViewPermission() {
        PipelineConfigs group = createWithPipeline(PipelineConfigMother.pipelineConfig("pipeline1"));
        group.getAuthorization().getViewConfig().add(new AdminUser(new CaseInsensitiveString("jez")));
        Assert.assertThat(group.hasViewPermission(new CaseInsensitiveString("jez"), null), Matchers.is(true));
    }

    @Test
    public void shouldReturnTrueForOperatePermissionIfAuthorizationIsNotDefined() {
        Assert.assertThat(createEmpty().hasOperatePermission(new CaseInsensitiveString("anyone"), null), Matchers.is(true));
    }

    @Test
    public void validate_shouldMakeSureTheNameIsAppropriate() {
        PipelineConfigs group = createEmpty();
        group.validate(null);
        Assert.assertThat(group.errors().on(GROUP), Matchers.is("Invalid group name 'null'. This must be alphanumeric and can contain underscores and periods (however, it cannot start with a period). The maximum allowed length is 255 characters."));
    }

    @Test
    public void shouldErrorWhenAuthorizationIsDefinedInConfigRepo() {
        BasicPipelineConfigs group = new BasicPipelineConfigs(new RepoConfigOrigin());
        group.setGroup("gr");
        group.setConfigAttributes(DataStructureUtils.m(AUTHORIZATION, DataStructureUtils.a(DataStructureUtils.m(NAME, "loser", TYPE, UserType.USER.toString(), PRIVILEGES, privileges(PrivilegeState.ON, PrivilegeState.DISABLED, PrivilegeState.DISABLED)), DataStructureUtils.m(NAME, "boozer", TYPE, UserType.USER.toString(), PRIVILEGES, privileges(PrivilegeState.OFF, PrivilegeState.ON, PrivilegeState.ON)), DataStructureUtils.m(NAME, "geezer", TYPE, UserType.USER.toString(), PRIVILEGES, privileges(PrivilegeState.DISABLED, PrivilegeState.OFF, PrivilegeState.ON)), DataStructureUtils.m(NAME, "gang_of_losers", TYPE, UserType.ROLE.toString(), PRIVILEGES, privileges(PrivilegeState.DISABLED, PrivilegeState.OFF, PrivilegeState.ON)), DataStructureUtils.m(NAME, "blinds", TYPE, UserType.ROLE.toString(), PRIVILEGES, privileges(PrivilegeState.ON, PrivilegeState.ON, PrivilegeState.OFF)))));
        group.validate(null);
        Assert.assertThat(group.errors().on(NO_REMOTE_AUTHORIZATION), Matchers.is("Authorization can be defined only in configuration file"));
    }

    @Test
    public void shouldNotErrorWhenNoAuthorizationIsDefined_AndInConfigRepo() {
        BasicPipelineConfigs group = new BasicPipelineConfigs(new RepoConfigOrigin());
        group.setGroup("gr");
        group.validate(null);
        Assert.assertThat(group.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldNotErrorWhenAuthorizationIsDefinedLocally() {
        BasicPipelineConfigs group = new BasicPipelineConfigs(new FileConfigOrigin());
        group.setGroup("gr");
        group.setConfigAttributes(DataStructureUtils.m(AUTHORIZATION, DataStructureUtils.a(DataStructureUtils.m(NAME, "loser", TYPE, UserType.USER.toString(), PRIVILEGES, privileges(PrivilegeState.ON, PrivilegeState.DISABLED, PrivilegeState.DISABLED)), DataStructureUtils.m(NAME, "boozer", TYPE, UserType.USER.toString(), PRIVILEGES, privileges(PrivilegeState.OFF, PrivilegeState.ON, PrivilegeState.ON)), DataStructureUtils.m(NAME, "geezer", TYPE, UserType.USER.toString(), PRIVILEGES, privileges(PrivilegeState.DISABLED, PrivilegeState.OFF, PrivilegeState.ON)), DataStructureUtils.m(NAME, "gang_of_losers", TYPE, UserType.ROLE.toString(), PRIVILEGES, privileges(PrivilegeState.DISABLED, PrivilegeState.OFF, PrivilegeState.ON)), DataStructureUtils.m(NAME, "blinds", TYPE, UserType.ROLE.toString(), PRIVILEGES, privileges(PrivilegeState.ON, PrivilegeState.ON, PrivilegeState.OFF)))));
        group.validate(null);
        Assert.assertThat(group.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldValidateThatPipelineNameIsUnique() {
        PipelineConfig first = PipelineConfigMother.pipelineConfig("first");
        PipelineConfig second = PipelineConfigMother.pipelineConfig("second");
        PipelineConfigs group = createWithPipelines(first, second);
        PipelineConfig duplicate = PipelineConfigMother.pipelineConfig("first");
        group.addWithoutValidation(duplicate);
        group.validate(null);
        Assert.assertThat(duplicate.errors().on(PipelineConfig.NAME), Matchers.is("You have defined multiple pipelines called 'first'. Pipeline names are case-insensitive and must be unique."));
        Assert.assertThat(first.errors().on(PipelineConfig.NAME), Matchers.is("You have defined multiple pipelines called 'first'. Pipeline names are case-insensitive and must be unique."));
    }

    @Test
    public void shouldReturnFalseIfOperatePermissionIsNotDefined() {
        PipelineConfigs group = createWithPipeline(PipelineConfigMother.pipelineConfig("pipeline1"));
        group.getAuthorization().getViewConfig().add(new AdminUser(new CaseInsensitiveString("jez")));
        Assert.assertThat(group.hasOperatePermission(new CaseInsensitiveString("jez"), null), Matchers.is(false));
    }

    @Test
    public void shouldReturnFalseIfUserDoesNotHaveOperatePermission() {
        PipelineConfigs group = createWithPipeline(PipelineConfigMother.pipelineConfig("pipeline1"));
        group.getAuthorization().getOperationConfig().add(new AdminUser(new CaseInsensitiveString("jez")));
        Assert.assertThat(group.hasOperatePermission(new CaseInsensitiveString("anyone"), null), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueIfUserHasOperatePermission() {
        PipelineConfigs group = createWithPipeline(PipelineConfigMother.pipelineConfig("pipeline1"));
        group.getAuthorization().getOperationConfig().add(new AdminUser(new CaseInsensitiveString("jez")));
        Assert.assertThat(group.hasOperatePermission(new CaseInsensitiveString("jez"), null), Matchers.is(true));
    }

    @Test
    public void hasViewPermissionDefinedShouldReturnTrueIfAuthorizationIsDefined() {
        PipelineConfigs group = createWithPipeline(PipelineConfigMother.pipelineConfig("pipeline1"));
        group.getAuthorization().getViewConfig().add(new AdminUser(new CaseInsensitiveString("jez")));
        Assert.assertThat("hasViewPermissionDefinedShouldReturnTrueIfAuthorizationIsDefined", group.hasViewPermissionDefined(), Matchers.is(true));
    }

    @Test
    public void hasViewPermissionDefinedShouldReturnFalseIfAuthorizationIsNotDefined() {
        PipelineConfigs group = createWithPipeline(PipelineConfigMother.pipelineConfig("pipeline1"));
        Assert.assertThat("hasViewPermissionDefinedShouldReturnFalseIfAuthorizationIsNotDefined", group.hasViewPermissionDefined(), Matchers.is(false));
    }

    @Test
    public void shouldReturnIndexOfPipeline() {
        PipelineConfigs group = createWithPipeline(PipelineConfigMother.pipelineConfig("pipeline1"));
        PipelineConfig pipelineConfig = ((PipelineConfig) (group.get(0).clone()));
        pipelineConfig.setLabelTemplate("blah");
        group.update(group.getGroup(), pipelineConfig, "pipeline1");
        Assert.assertThat(group.get(0).getLabelTemplate(), Matchers.is("blah"));
    }

    @Test
    public void shouldAddPipelineAtIndex() {
        PipelineConfigs group = createWithPipeline(PipelineConfigMother.pipelineConfig("pipeline0"));
        PipelineConfig p1 = PipelineConfigMother.pipelineConfig("pipeline1");
        group.add(1, p1);
        Assert.assertThat(group.get(1), Matchers.is(p1));
    }

    @Test
    public void shouldRemovePipelineAtIndex() {
        PipelineConfigs group = createWithPipeline(PipelineConfigMother.pipelineConfig("pipeline0"));
        PipelineConfig p1 = PipelineConfigMother.pipelineConfig("pipeline1");
        group.add(1, p1);
        group.remove(0);
        Assert.assertThat(group.get(0), Matchers.is(p1));
        Assert.assertThat(group.size(), Matchers.is(1));
    }

    @Test
    public void shouldRemovePipeline() {
        PipelineConfig p0 = PipelineConfigMother.pipelineConfig("pipeline0");
        PipelineConfigs group = createWithPipeline(p0);
        PipelineConfig p1 = PipelineConfigMother.pipelineConfig("pipeline1");
        group.add(1, p1);
        group.remove(p0);
        Assert.assertThat(group.get(0), Matchers.is(p1));
        Assert.assertThat(group.size(), Matchers.is(1));
    }

    @Test
    public void shouldReturnIndexOfPipeline_When2Pipelines() {
        PipelineConfigs group = createWithPipelines(PipelineConfigMother.pipelineConfig("pipeline1"), PipelineConfigMother.pipelineConfig("pipeline2"));
        PipelineConfig pipelineConfig = group.findBy(new CaseInsensitiveString("pipeline2"));
        Assert.assertThat(group.indexOf(pipelineConfig), Matchers.is(1));
    }

    @Test
    public void shouldUpdateAuthorization() {
        PipelineConfigs group = createWithPipeline(PipelineConfigMother.pipelineConfig("pipeline1"));
        group.setConfigAttributes(DataStructureUtils.m(AUTHORIZATION, DataStructureUtils.a(DataStructureUtils.m(NAME, "loser", TYPE, UserType.USER.toString(), PRIVILEGES, privileges(PrivilegeState.ON, PrivilegeState.DISABLED, PrivilegeState.DISABLED)), DataStructureUtils.m(NAME, "boozer", TYPE, UserType.USER.toString(), PRIVILEGES, privileges(PrivilegeState.OFF, PrivilegeState.ON, PrivilegeState.ON)), DataStructureUtils.m(NAME, "geezer", TYPE, UserType.USER.toString(), PRIVILEGES, privileges(PrivilegeState.DISABLED, PrivilegeState.OFF, PrivilegeState.ON)), DataStructureUtils.m(NAME, "gang_of_losers", TYPE, UserType.ROLE.toString(), PRIVILEGES, privileges(PrivilegeState.DISABLED, PrivilegeState.OFF, PrivilegeState.ON)), DataStructureUtils.m(NAME, "blinds", TYPE, UserType.ROLE.toString(), PRIVILEGES, privileges(PrivilegeState.ON, PrivilegeState.ON, PrivilegeState.OFF)))));
        Authorization authorization = group.getAuthorization();
        Assert.assertThat(authorization.getAdminsConfig().size(), Matchers.is(2));
        Assert.assertThat(authorization.getAdminsConfig(), Matchers.hasItems(new AdminUser(new CaseInsensitiveString("loser")), new AdminRole(new CaseInsensitiveString("blinds"))));
        Assert.assertThat(authorization.getOperationConfig().size(), Matchers.is(2));
        Assert.assertThat(authorization.getOperationConfig(), Matchers.hasItems(new AdminUser(new CaseInsensitiveString("boozer")), new AdminRole(new CaseInsensitiveString("blinds"))));
        Assert.assertThat(authorization.getViewConfig().size(), Matchers.is(3));
        Assert.assertThat(authorization.getViewConfig(), Matchers.hasItems(new AdminUser(new CaseInsensitiveString("boozer")), new AdminUser(new CaseInsensitiveString("geezer")), new AdminRole(new CaseInsensitiveString("gang_of_losers"))));
    }

    @Test
    public void shouldReInitializeAuthorizationIfWeClearAllPermissions() {
        PipelineConfigs group = createWithPipeline(PipelineConfigMother.pipelineConfig("pipeline1"));
        group.setConfigAttributes(DataStructureUtils.m(AUTHORIZATION, DataStructureUtils.a(DataStructureUtils.m(NAME, "loser", TYPE, UserType.USER.toString(), PRIVILEGES, privileges(PrivilegeState.ON, PrivilegeState.DISABLED, PrivilegeState.DISABLED)), DataStructureUtils.m(NAME, "boozer", TYPE, UserType.USER.toString(), PRIVILEGES, privileges(PrivilegeState.OFF, PrivilegeState.ON, PrivilegeState.ON)), DataStructureUtils.m(NAME, "geezer", TYPE, UserType.USER.toString(), PRIVILEGES, privileges(PrivilegeState.DISABLED, PrivilegeState.OFF, PrivilegeState.ON)), DataStructureUtils.m(NAME, "gang_of_losers", TYPE, UserType.ROLE.toString(), PRIVILEGES, privileges(PrivilegeState.DISABLED, PrivilegeState.OFF, PrivilegeState.ON)), DataStructureUtils.m(NAME, "blinds", TYPE, UserType.ROLE.toString(), PRIVILEGES, privileges(PrivilegeState.ON, PrivilegeState.ON, PrivilegeState.OFF)))));
        Authorization authorization = group.getAuthorization();
        Assert.assertThat(authorization.getAdminsConfig().size(), Matchers.is(2));
        Assert.assertThat(authorization.getOperationConfig().size(), Matchers.is(2));
        Assert.assertThat(authorization.getViewConfig().size(), Matchers.is(3));
        group.setConfigAttributes(DataStructureUtils.m());
        authorization = group.getAuthorization();
        Assert.assertThat(authorization.getAdminsConfig().size(), Matchers.is(0));
        Assert.assertThat(authorization.getOperationConfig().size(), Matchers.is(0));
        Assert.assertThat(authorization.getViewConfig().size(), Matchers.is(0));
    }

    @Test
    public void shouldIgnoreBlankUserOrRoleNames_whileSettingAttributes() {
        PipelineConfigs group = createWithPipeline(PipelineConfigMother.pipelineConfig("pipeline1"));
        group.setConfigAttributes(DataStructureUtils.m(AUTHORIZATION, DataStructureUtils.a(DataStructureUtils.m(NAME, "", TYPE, UserType.USER.toString(), PRIVILEGES, privileges(PrivilegeState.ON, PrivilegeState.DISABLED, PrivilegeState.DISABLED)), DataStructureUtils.m(NAME, null, TYPE, UserType.USER.toString(), PRIVILEGES, privileges(PrivilegeState.OFF, PrivilegeState.ON, PrivilegeState.ON)), DataStructureUtils.m(NAME, "geezer", TYPE, UserType.USER.toString(), PRIVILEGES, privileges(PrivilegeState.DISABLED, PrivilegeState.OFF, PrivilegeState.ON)), DataStructureUtils.m(NAME, "", TYPE, UserType.ROLE.toString(), PRIVILEGES, privileges(PrivilegeState.DISABLED, PrivilegeState.ON, PrivilegeState.ON)), DataStructureUtils.m(NAME, null, TYPE, UserType.ROLE.toString(), PRIVILEGES, privileges(PrivilegeState.ON, PrivilegeState.OFF, PrivilegeState.ON)), DataStructureUtils.m(NAME, "blinds", TYPE, UserType.ROLE.toString(), PRIVILEGES, privileges(PrivilegeState.ON, PrivilegeState.ON, PrivilegeState.OFF)))));
        Authorization authorization = group.getAuthorization();
        Assert.assertThat(authorization.getAdminsConfig().size(), Matchers.is(1));
        Assert.assertThat(authorization.getAdminsConfig(), Matchers.hasItem(((Admin) (new AdminRole(new CaseInsensitiveString("blinds"))))));
        Assert.assertThat(authorization.getOperationConfig().size(), Matchers.is(1));
        Assert.assertThat(authorization.getOperationConfig(), Matchers.hasItem(((Admin) (new AdminRole(new CaseInsensitiveString("blinds"))))));
        Assert.assertThat(authorization.getViewConfig().size(), Matchers.is(1));
        Assert.assertThat(authorization.getViewConfig(), Matchers.hasItem(((Admin) (new AdminUser(new CaseInsensitiveString("geezer"))))));
    }

    @Test
    public void shouldSetViewPermissionByDefaultIfNameIsPresentAndPermissionsAreOff_whileSettingAttributes() {
        PipelineConfigs group = createWithPipeline(PipelineConfigMother.pipelineConfig("pipeline1"));
        group.setConfigAttributes(DataStructureUtils.m(AUTHORIZATION, DataStructureUtils.a(DataStructureUtils.m(NAME, "user1", TYPE, UserType.USER.toString(), PRIVILEGES, privileges(PrivilegeState.OFF, PrivilegeState.OFF, PrivilegeState.OFF)), DataStructureUtils.m(NAME, "role1", TYPE, UserType.ROLE.toString(), PRIVILEGES, privileges(PrivilegeState.OFF, PrivilegeState.OFF, PrivilegeState.OFF)))));
        Authorization authorization = group.getAuthorization();
        Assert.assertThat(authorization.getViewConfig().size(), Matchers.is(2));
        Assert.assertThat(authorization.getViewConfig(), Matchers.hasItems(new AdminRole(new CaseInsensitiveString("role1")), new AdminUser(new CaseInsensitiveString("user1"))));
        Assert.assertThat(authorization.getOperationConfig().size(), Matchers.is(0));
        Assert.assertThat(authorization.getAdminsConfig().size(), Matchers.is(0));
    }

    @Test
    public void shouldSetToDefaultGroupWithGroupNameIsEmptyString() {
        PipelineConfigs pipelineConfigs = createEmpty();
        pipelineConfigs.setGroup("");
        Assert.assertThat(pipelineConfigs.getGroup(), Matchers.is(DEFAULT_GROUP));
    }

    @Test
    public void shouldValidateGroupNameUniqueness() {
        Map<String, PipelineConfigs> nameToConfig = new HashMap<>();
        PipelineConfigs group1 = createEmpty();
        group1.setGroup("joe");
        PipelineConfigs group2 = createEmpty();
        group2.setGroup("joe");
        group1.validateNameUniqueness(nameToConfig);
        group2.validateNameUniqueness(nameToConfig);
        Assert.assertThat(group1.errors().on(PipelineConfigs.GROUP), Matchers.is("Group with name 'joe' already exists"));
    }

    @Test
    public void shouldGetAllPipelinesList() {
        PipelineConfig pipeline1 = PipelineConfigMother.pipelineConfig("pipeline1");
        PipelineConfig pipeline2 = PipelineConfigMother.pipelineConfig("pipeline2");
        PipelineConfigs group = createWithPipelines(pipeline1, pipeline2);
        Assert.assertThat(group.getPipelines(), Matchers.hasItem(pipeline1));
        Assert.assertThat(group.getPipelines(), Matchers.hasItem(pipeline2));
    }
}

