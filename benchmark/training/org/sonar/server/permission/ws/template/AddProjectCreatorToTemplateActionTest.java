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
package org.sonar.server.permission.ws.template;


import Qualifiers.PROJECT;
import System2.INSTANCE;
import UserRole.ADMIN;
import UserRole.USER;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.resources.ResourceTypes;
import org.sonar.api.utils.System2;
import org.sonar.db.component.ResourceTypesRule;
import org.sonar.db.permission.template.PermissionTemplateCharacteristicDto;
import org.sonar.db.permission.template.PermissionTemplateDto;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.permission.PermissionService;
import org.sonar.server.permission.ws.BasePermissionWsTest;
import org.sonar.server.permission.ws.RequestValidator;
import org.sonar.server.permission.ws.WsParameters;


public class AddProjectCreatorToTemplateActionTest extends BasePermissionWsTest<AddProjectCreatorToTemplateAction> {
    private System2 system = Mockito.spy(INSTANCE);

    private PermissionTemplateDto template;

    private ResourceTypes resourceTypes = new ResourceTypesRule().setRootQualifiers(PROJECT);

    private PermissionService permissionService = new org.sonar.server.permission.PermissionServiceImpl(resourceTypes);

    private WsParameters wsParameters = new WsParameters(permissionService);

    private RequestValidator requestValidator = new RequestValidator(permissionService);

    @Test
    public void insert_row_when_no_template_permission() {
        loginAsAdmin(db.getDefaultOrganization());
        newRequest().setParam(PARAM_PERMISSION, ADMIN).setParam(PARAM_TEMPLATE_ID, template.getUuid()).execute();
        assertThatProjectCreatorIsPresentFor(ADMIN, template.getId());
    }

    @Test
    public void update_row_when_existing_template_permission() {
        loginAsAdmin(db.getDefaultOrganization());
        PermissionTemplateCharacteristicDto characteristic = db.getDbClient().permissionTemplateCharacteristicDao().insert(db.getSession(), new PermissionTemplateCharacteristicDto().setTemplateId(template.getId()).setPermission(USER).setWithProjectCreator(false).setCreatedAt(1000000000L).setUpdatedAt(1000000000L));
        db.commit();
        Mockito.when(system.now()).thenReturn(3000000000L);
        newRequest().setParam(PARAM_PERMISSION, USER).setParam(PARAM_TEMPLATE_NAME, template.getName()).execute();
        assertThatProjectCreatorIsPresentFor(USER, template.getId());
        PermissionTemplateCharacteristicDto reloaded = reload(characteristic);
        assertThat(reloaded.getCreatedAt()).isEqualTo(1000000000L);
        assertThat(reloaded.getUpdatedAt()).isEqualTo(3000000000L);
    }

    @Test
    public void fail_when_template_does_not_exist() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(NotFoundException.class);
        newRequest().setParam(PARAM_PERMISSION, ADMIN).setParam(PARAM_TEMPLATE_ID, "42").execute();
    }

    @Test
    public void fail_if_permission_is_not_a_project_permission() {
        loginAsAdmin(db.getDefaultOrganization());
        expectedException.expect(IllegalArgumentException.class);
        newRequest().setParam(PARAM_PERMISSION, QUALITY_GATE_ADMIN).setParam(PARAM_TEMPLATE_ID, template.getUuid()).execute();
    }

    @Test
    public void fail_if_not_admin_of_default_organization() {
        userSession.logIn().addPermission(ADMINISTER_QUALITY_GATES, db.getDefaultOrganization());
        expectedException.expect(ForbiddenException.class);
        newRequest().setParam(PARAM_PERMISSION, ADMIN).setParam(PARAM_TEMPLATE_ID, template.getUuid()).execute();
    }
}

