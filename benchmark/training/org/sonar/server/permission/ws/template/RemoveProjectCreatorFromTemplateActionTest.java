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


import GlobalPermissions.QUALITY_GATE_ADMIN;
import Qualifiers.PROJECT;
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
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.permission.PermissionService;
import org.sonar.server.permission.ws.BasePermissionWsTest;
import org.sonar.server.permission.ws.RequestValidator;
import org.sonar.server.permission.ws.WsParameters;


public class RemoveProjectCreatorFromTemplateActionTest extends BasePermissionWsTest<RemoveProjectCreatorFromTemplateAction> {
    private System2 system = Mockito.mock(System2.class);

    private PermissionTemplateDto template;

    private ResourceTypes resourceTypes = new ResourceTypesRule().setRootQualifiers(PROJECT);

    private PermissionService permissionService = new org.sonar.server.permission.PermissionServiceImpl(resourceTypes);

    private WsParameters wsParameters = new WsParameters(permissionService);

    private RequestValidator requestValidator = new RequestValidator(permissionService);

    @Test
    public void update_template_permission() {
        PermissionTemplateCharacteristicDto characteristic = db.getDbClient().permissionTemplateCharacteristicDao().insert(db.getSession(), new PermissionTemplateCharacteristicDto().setTemplateId(template.getId()).setPermission(USER).setWithProjectCreator(false).setCreatedAt(1000000000L).setUpdatedAt(1000000000L));
        db.commit();
        Mockito.when(system.now()).thenReturn(3000000000L);
        newRequest().setParam(PARAM_PERMISSION, USER).setParam(PARAM_TEMPLATE_NAME, template.getName()).execute();
        assertWithoutProjectCreatorFor(USER);
        PermissionTemplateCharacteristicDto reloaded = reload(characteristic);
        assertThat(reloaded.getCreatedAt()).isEqualTo(1000000000L);
        assertThat(reloaded.getUpdatedAt()).isEqualTo(3000000000L);
    }

    @Test
    public void do_not_fail_when_no_template_permission() {
        newRequest().setParam(PARAM_PERMISSION, ADMIN).setParam(PARAM_TEMPLATE_ID, template.getUuid()).execute();
        assertNoTemplatePermissionFor(ADMIN);
    }

    @Test
    public void fail_when_template_does_not_exist() {
        expectedException.expect(NotFoundException.class);
        newRequest().setParam(PARAM_PERMISSION, ADMIN).setParam(PARAM_TEMPLATE_ID, "42").execute();
    }

    @Test
    public void fail_if_permission_is_not_a_project_permission() {
        expectedException.expect(IllegalArgumentException.class);
        newRequest().setParam(PARAM_PERMISSION, QUALITY_GATE_ADMIN).setParam(PARAM_TEMPLATE_ID, template.getUuid()).execute();
    }

    @Test
    public void fail_if_not_authenticated() {
        userSession.anonymous();
        expectedException.expect(UnauthorizedException.class);
        newRequest().setParam(PARAM_PERMISSION, ADMIN).setParam(PARAM_TEMPLATE_ID, template.getUuid()).execute();
    }

    @Test
    public void fail_if_insufficient_privileges() {
        userSession.logIn();
        expectedException.expect(ForbiddenException.class);
        newRequest().setParam(PARAM_PERMISSION, ADMIN).setParam(PARAM_TEMPLATE_ID, template.getUuid()).execute();
    }
}

