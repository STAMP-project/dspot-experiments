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


import PipelineTemplateConfig.NAME;
import com.thoughtworks.go.helper.PipelineTemplateConfigMother;
import com.thoughtworks.go.helper.StageConfigMother;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TemplatesConfigTest {
    @Test
    public void shouldRemoveATemplateByName() {
        PipelineTemplateConfig template2 = template("template2");
        TemplatesConfig templates = new TemplatesConfig(template("template1"), template2);
        templates.removeTemplateNamed(new CaseInsensitiveString("template1"));
        Assert.assertThat(templates.size(), Matchers.is(1));
        Assert.assertThat(templates.get(0), Matchers.is(template2));
    }

    @Test
    public void shouldIgnoreTryingToRemoveNonExistentTemplate() {
        TemplatesConfig templates = new TemplatesConfig(template("template1"), template("template2"));
        templates.removeTemplateNamed(new CaseInsensitiveString("sachin"));
        Assert.assertThat(templates.size(), Matchers.is(2));
    }

    @Test
    public void shouldReturnTemplateByName() {
        PipelineTemplateConfig template1 = template("template1");
        TemplatesConfig templates = new TemplatesConfig(template1, template("template2"));
        Assert.assertThat(templates.templateByName(new CaseInsensitiveString("template1")), Matchers.is(template1));
    }

    @Test
    public void shouldReturnNullIfTemplateIsNotFound() {
        PipelineTemplateConfig template1 = template("template1");
        TemplatesConfig templates = new TemplatesConfig(template1);
        Assert.assertThat(templates.templateByName(new CaseInsensitiveString("some_invalid_template")), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldErrorOutIfTemplateNameIsAlreadyPresent() {
        PipelineTemplateConfig template = template("template1");
        TemplatesConfig templates = new TemplatesConfig(template);
        PipelineTemplateConfig duplicateTemplate = template("template1");
        templates.add(duplicateTemplate);
        templates.validate(null);
        Assert.assertThat(template.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(duplicateTemplate.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(template.errors().on(NAME), Matchers.is(String.format("Template name '%s' is not unique", template.name())));
        Assert.assertThat(duplicateTemplate.errors().on(NAME), Matchers.is(String.format("Template name '%s' is not unique", template.name())));
    }

    @Test
    public void shouldErrorOutIfTemplateNameIsAlreadyPresent_CaseInsensitiveMap() {
        PipelineTemplateConfig template = template("TEmplatE1");
        TemplatesConfig templates = new TemplatesConfig(template);
        PipelineTemplateConfig duplicateTemplate = template("template1");
        templates.add(duplicateTemplate);
        templates.validate(null);
        Assert.assertThat(template.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(duplicateTemplate.errors().isEmpty(), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueIfUserCanViewAndEditAtLeastOneTemplate() throws Exception {
        CaseInsensitiveString templateAdmin = new CaseInsensitiveString("template-admin");
        TemplatesConfig templates = configForUserWhoCanViewATemplate();
        templates.add(PipelineTemplateConfigMother.createTemplate("template200", new Authorization(new AdminsConfig(new AdminUser(templateAdmin))), StageConfigMother.manualStage("stage-name")));
        Assert.assertThat(templates.canViewAndEditTemplate(templateAdmin, null), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseIfUserCannotViewAndEditAtLeastOneTemplate() throws Exception {
        CaseInsensitiveString templateAdmin = new CaseInsensitiveString("template-admin");
        CaseInsensitiveString nonTemplateAdmin = new CaseInsensitiveString("some-random-user");
        TemplatesConfig templates = configForUserWhoCanViewATemplate();
        templates.add(PipelineTemplateConfigMother.createTemplate("template200", new Authorization(new AdminsConfig(new AdminUser(templateAdmin))), StageConfigMother.manualStage("stage-name")));
        Assert.assertThat(templates.canViewAndEditTemplate(nonTemplateAdmin, null), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueIfUserCanViewAtLeastOneTemplate() {
        CaseInsensitiveString templateViewUser = new CaseInsensitiveString("template-view");
        TemplatesConfig templates = configForUserWhoCanViewATemplate();
        templates.add(PipelineTemplateConfigMother.createTemplate("template200", new Authorization(new ViewConfig(new AdminUser(templateViewUser))), StageConfigMother.manualStage("stage-name")));
        Assert.assertThat(templates.canUserViewTemplates(templateViewUser, null, false), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseIfUserCannotViewAtLeastOneTemplate() {
        CaseInsensitiveString templateViewUser = new CaseInsensitiveString("template-view");
        TemplatesConfig templates = configForUserWhoCanViewATemplate();
        Assert.assertThat(templates.canUserViewTemplates(templateViewUser, null, false), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueIfUserWithinARoleCanViewAndEditTemplates() {
        CaseInsensitiveString templateAdmin = new CaseInsensitiveString("template-admin");
        Role securityConfigRole = getSecurityConfigRole(templateAdmin);
        List<Role> roles = setupRoles(securityConfigRole);
        ArrayList<PipelineTemplateConfig> templateList = new ArrayList<>();
        templateList.add(PipelineTemplateConfigMother.createTemplate("templateName", new Authorization(new AdminsConfig(new AdminRole(securityConfigRole))), StageConfigMother.manualStage("some-random-stage")));
        TemplatesConfig templates = new TemplatesConfig(templateList.toArray(new PipelineTemplateConfig[0]));
        Assert.assertThat(templates.canViewAndEditTemplate(templateAdmin, roles), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseIfUserWithinARoleCannotViewAndEditTemplates() {
        CaseInsensitiveString templateAdmin = new CaseInsensitiveString("template-admin");
        Role securityConfigRole = getSecurityConfigRole(templateAdmin);
        List<Role> roles = setupRoles(securityConfigRole);
        ArrayList<PipelineTemplateConfig> templateList = new ArrayList<>();
        templateList.add(PipelineTemplateConfigMother.createTemplate("templateName", new Authorization(new AdminsConfig(new AdminUser(new CaseInsensitiveString("random-user")))), StageConfigMother.manualStage("stage-name")));
        TemplatesConfig templates = new TemplatesConfig(templateList.toArray(new PipelineTemplateConfig[0]));
        Assert.assertThat(templates.canViewAndEditTemplate(templateAdmin, roles), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueIfUserCanEditTemplate() {
        CaseInsensitiveString templateAdmin = new CaseInsensitiveString("template-admin");
        String templateName = "template1";
        PipelineTemplateConfig template = PipelineTemplateConfigMother.createTemplate(templateName, new Authorization(new AdminsConfig(new AdminUser(templateAdmin))), StageConfigMother.manualStage("stage-name"));
        TemplatesConfig templates = new TemplatesConfig(template);
        Assert.assertThat(templates.canUserEditTemplate(template, templateAdmin, null), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseIfUserCannotEditTemplate() {
        CaseInsensitiveString templateAdmin = new CaseInsensitiveString("template-admin");
        CaseInsensitiveString templateAdminWhoDoesNotHavePermissionToThisTemplate = new CaseInsensitiveString("user");
        String templateName = "template1";
        PipelineTemplateConfig template = PipelineTemplateConfigMother.createTemplate(templateName, new Authorization(new AdminsConfig(new AdminUser(templateAdmin))), StageConfigMother.manualStage("stage-name"));
        TemplatesConfig templates = new TemplatesConfig(template);
        Assert.assertThat(templates.canUserEditTemplate(template, templateAdminWhoDoesNotHavePermissionToThisTemplate, null), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueIfUserWithinARoleCanEditTemplate() {
        CaseInsensitiveString templateAdmin = new CaseInsensitiveString("template-admin");
        Role securityConfigRole = getSecurityConfigRole(templateAdmin);
        List<Role> roles = setupRoles(securityConfigRole);
        String templateName = "template1";
        PipelineTemplateConfig template = PipelineTemplateConfigMother.createTemplate(templateName, new Authorization(new AdminsConfig(new AdminRole(securityConfigRole))), StageConfigMother.manualStage("random-stage-name"));
        TemplatesConfig templates = new TemplatesConfig(template);
        Assert.assertThat(templates.canUserEditTemplate(template, templateAdmin, roles), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseIfUserWithinARoleCannotEditTemplate() {
        CaseInsensitiveString templateAdmin = new CaseInsensitiveString("template-admin");
        Role securityConfigRole = getSecurityConfigRole(templateAdmin);
        List<Role> roles = setupRoles(securityConfigRole);
        String templateName = "template1";
        PipelineTemplateConfig template = PipelineTemplateConfigMother.createTemplate(templateName, new Authorization(new AdminsConfig(new AdminRole(new CaseInsensitiveString("another-role")))), StageConfigMother.manualStage("random-stage"));
        TemplatesConfig templates = new TemplatesConfig(template);
        Assert.assertThat(templates.canUserEditTemplate(template, templateAdmin, roles), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueIfUserCanViewTemplate() {
        CaseInsensitiveString templateViewUser = new CaseInsensitiveString("view");
        String templateName = "template";
        PipelineTemplateConfig template = PipelineTemplateConfigMother.createTemplate(templateName, StageConfigMother.manualStage("stage"));
        template.setAuthorization(new Authorization(new ViewConfig(new AdminUser(templateViewUser))));
        TemplatesConfig templates = new TemplatesConfig(template);
        Assert.assertThat(templates.hasViewAccessToTemplate(template, templateViewUser, null, false), Matchers.is(true));
    }

    @Test
    public void shouldReturnTrueIfGroupAdminCanViewTemplate() {
        CaseInsensitiveString templateViewUser = new CaseInsensitiveString("view");
        String templateName = "template";
        PipelineTemplateConfig template = PipelineTemplateConfigMother.createTemplate(templateName, StageConfigMother.manualStage("stage"));
        TemplatesConfig templates = new TemplatesConfig(template);
        Assert.assertThat(templates.hasViewAccessToTemplate(template, templateViewUser, null, true), Matchers.is(true));
    }

    @Test
    public void shouldReturnTrueIfUserWithinARoleCanViewTemplate() {
        CaseInsensitiveString templateViewUser = new CaseInsensitiveString("template-admin");
        Role securityConfigRole = getSecurityConfigRole(templateViewUser);
        List<Role> roles = setupRoles(securityConfigRole);
        String templateName = "template1";
        PipelineTemplateConfig template = PipelineTemplateConfigMother.createTemplate(templateName, StageConfigMother.manualStage("stage"));
        template.setAuthorization(new Authorization(new ViewConfig(new AdminRole(securityConfigRole))));
        TemplatesConfig templates = new TemplatesConfig(template);
        Assert.assertThat(templates.hasViewAccessToTemplate(template, templateViewUser, roles, false), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseIfUserCannotViewTemplate() {
        CaseInsensitiveString templateViewUser = new CaseInsensitiveString("view");
        String templateName = "template";
        PipelineTemplateConfig template = PipelineTemplateConfigMother.createTemplate(templateName, StageConfigMother.manualStage("stage"));
        TemplatesConfig templates = new TemplatesConfig(template);
        Assert.assertThat(templates.hasViewAccessToTemplate(template, templateViewUser, null, false), Matchers.is(false));
    }

    @Test
    public void shouldReturnFalseIfGroupAdminCanViewTemplate() {
        CaseInsensitiveString templateViewUser = new CaseInsensitiveString("view");
        String templateName = "template";
        PipelineTemplateConfig template = PipelineTemplateConfigMother.createTemplate(templateName, StageConfigMother.manualStage("stage"));
        template.getAuthorization().setAllowGroupAdmins(false);
        TemplatesConfig templates = new TemplatesConfig(template);
        Assert.assertThat(templates.hasViewAccessToTemplate(template, templateViewUser, null, true), Matchers.is(false));
    }

    @Test
    public void shouldReturnFalseIfUserWithinARoleCannotViewTemplate() {
        CaseInsensitiveString templateViewUser = new CaseInsensitiveString("template-admin");
        Role securityConfigRole = getSecurityConfigRole(templateViewUser);
        List<Role> roles = setupRoles(securityConfigRole);
        String templateName = "template1";
        PipelineTemplateConfig template = PipelineTemplateConfigMother.createTemplate(templateName, StageConfigMother.manualStage("stage"));
        template.setAuthorization(new Authorization(new ViewConfig(new AdminRole(new CaseInsensitiveString("another-role")))));
        TemplatesConfig templates = new TemplatesConfig(template);
        Assert.assertThat(templates.hasViewAccessToTemplate(template, templateViewUser, roles, false), Matchers.is(false));
    }
}

