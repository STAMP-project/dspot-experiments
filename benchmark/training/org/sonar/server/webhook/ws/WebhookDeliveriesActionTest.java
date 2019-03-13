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
package org.sonar.server.webhook.ws;


import System2.INSTANCE;
import UserRole.ADMIN;
import UserRole.USER;
import Webhooks.DeliveriesWsResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.server.ws.WebService;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.webhook.WebhookDeliveryDbTester;
import org.sonar.db.webhook.WebhookDeliveryDto;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;
import org.sonarqube.ws.Webhooks;


public class WebhookDeliveriesActionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private DbClient dbClient = db.getDbClient();

    private WebhookDeliveryDbTester webhookDeliveryDbTester = db.webhookDelivery();

    private WsActionTester ws;

    private ComponentDto project;

    @Test
    public void test_definition() {
        assertThat(ws.getDef().params()).extracting(WebService.Param::key).containsExactlyInAnyOrder("componentKey", "ceTaskId", "webhook", "p", "ps");
        assertThat(ws.getDef().isPost()).isFalse();
        assertThat(ws.getDef().isInternal()).isFalse();
        assertThat(ws.getDef().responseExampleAsString()).isNotEmpty();
    }

    @Test
    public void throw_UnauthorizedException_if_anonymous() {
        expectedException.expect(UnauthorizedException.class);
        ws.newRequest().execute();
    }

    @Test
    public void search_by_component_and_return_no_records() {
        userSession.logIn().addProjectPermission(ADMIN, project);
        Webhooks.DeliveriesWsResponse response = ws.newRequest().setParam("componentKey", project.getDbKey()).executeProtobuf(DeliveriesWsResponse.class);
        assertThat(response.getDeliveriesCount()).isEqualTo(0);
    }

    @Test
    public void search_by_task_and_return_no_records() {
        userSession.logIn().addProjectPermission(ADMIN, project);
        Webhooks.DeliveriesWsResponse response = ws.newRequest().setParam("ceTaskId", "t1").executeProtobuf(DeliveriesWsResponse.class);
        assertThat(response.getDeliveriesCount()).isEqualTo(0);
    }

    @Test
    public void search_by_webhook_and_return_no_records() {
        userSession.logIn().addProjectPermission(ADMIN, project);
        Webhooks.DeliveriesWsResponse response = ws.newRequest().setParam("webhook", "t1").executeProtobuf(DeliveriesWsResponse.class);
        assertThat(response.getDeliveriesCount()).isEqualTo(0);
    }

    @Test
    public void search_by_component_and_return_records_of_example() {
        WebhookDeliveryDto dto = newDto().setUuid("d1").setComponentUuid(project.uuid()).setCeTaskUuid("task-1").setName("Jenkins").setUrl("http://jenkins").setCreatedAt(1500000000000L).setSuccess(true).setDurationMs(10).setHttpStatus(200);
        dbClient.webhookDeliveryDao().insert(db.getSession(), dto);
        db.commit();
        userSession.logIn().addProjectPermission(ADMIN, project);
        String json = ws.newRequest().setParam("componentKey", project.getDbKey()).execute().getInput();
        assertJson(json).isSimilarTo(ws.getDef().responseExampleAsString());
    }

    @Test
    public void search_by_task_and_return_records() {
        WebhookDeliveryDto dto1 = newDto().setComponentUuid(project.uuid()).setCeTaskUuid("t1");
        WebhookDeliveryDto dto2 = newDto().setComponentUuid(project.uuid()).setCeTaskUuid("t1");
        WebhookDeliveryDto dto3 = newDto().setComponentUuid(project.uuid()).setCeTaskUuid("t2");
        dbClient.webhookDeliveryDao().insert(db.getSession(), dto1);
        dbClient.webhookDeliveryDao().insert(db.getSession(), dto2);
        dbClient.webhookDeliveryDao().insert(db.getSession(), dto3);
        db.commit();
        userSession.logIn().addProjectPermission(ADMIN, project);
        Webhooks.DeliveriesWsResponse response = ws.newRequest().setParam("ceTaskId", "t1").executeProtobuf(DeliveriesWsResponse.class);
        assertThat(response.getDeliveriesCount()).isEqualTo(2);
        assertThat(response.getDeliveriesList()).extracting(Webhooks.Delivery::getId).containsOnly(dto1.getUuid(), dto2.getUuid());
    }

    @Test
    public void search_by_webhook_and_return_records() {
        WebhookDeliveryDto dto1 = newDto().setComponentUuid(project.uuid()).setCeTaskUuid("t1").setWebhookUuid("wh-1-uuid");
        WebhookDeliveryDto dto2 = newDto().setComponentUuid(project.uuid()).setCeTaskUuid("t1").setWebhookUuid("wh-1-uuid");
        WebhookDeliveryDto dto3 = newDto().setComponentUuid(project.uuid()).setCeTaskUuid("t2").setWebhookUuid("wh-2-uuid");
        dbClient.webhookDeliveryDao().insert(db.getSession(), dto1);
        dbClient.webhookDeliveryDao().insert(db.getSession(), dto2);
        dbClient.webhookDeliveryDao().insert(db.getSession(), dto3);
        db.commit();
        userSession.logIn().addProjectPermission(ADMIN, project);
        Webhooks.DeliveriesWsResponse response = ws.newRequest().setParam("webhook", "wh-1-uuid").executeProtobuf(DeliveriesWsResponse.class);
        assertThat(response.getDeliveriesCount()).isEqualTo(2);
        assertThat(response.getDeliveriesList()).extracting(Webhooks.Delivery::getId).containsOnly(dto1.getUuid(), dto2.getUuid());
    }

    @Test
    public void validate_default_pagination() {
        for (int i = 0; i < 15; i++) {
            webhookDeliveryDbTester.insert(newDto().setComponentUuid(project.uuid()).setCeTaskUuid("t1").setWebhookUuid("wh-1-uuid"));
        }
        userSession.logIn().addProjectPermission(ADMIN, project);
        Webhooks.DeliveriesWsResponse response = ws.newRequest().setParam("webhook", "wh-1-uuid").executeProtobuf(DeliveriesWsResponse.class);
        assertThat(response.getDeliveriesCount()).isEqualTo(10);
    }

    @Test
    public void validate_pagination_first_page() {
        for (int i = 0; i < 12; i++) {
            webhookDeliveryDbTester.insert(newDto().setComponentUuid(project.uuid()).setCeTaskUuid("t1").setWebhookUuid("wh-1-uuid"));
        }
        userSession.logIn().addProjectPermission(ADMIN, project);
        Webhooks.DeliveriesWsResponse response = ws.newRequest().setParam("webhook", "wh-1-uuid").setParam("p", "1").setParam("ps", "10").executeProtobuf(DeliveriesWsResponse.class);
        assertThat(response.getDeliveriesCount()).isEqualTo(10);
        assertThat(response.getPaging().getTotal()).isEqualTo(12);
        assertThat(response.getPaging().getPageIndex()).isEqualTo(1);
    }

    @Test
    public void validate_pagination_last_page() {
        for (int i = 0; i < 12; i++) {
            webhookDeliveryDbTester.insert(newDto().setComponentUuid(project.uuid()).setCeTaskUuid("t1").setWebhookUuid("wh-1-uuid"));
        }
        userSession.logIn().addProjectPermission(ADMIN, project);
        Webhooks.DeliveriesWsResponse response = ws.newRequest().setParam("webhook", "wh-1-uuid").setParam("p", "2").setParam("ps", "10").executeProtobuf(DeliveriesWsResponse.class);
        assertThat(response.getDeliveriesCount()).isEqualTo(2);
        assertThat(response.getPaging().getTotal()).isEqualTo(12);
        assertThat(response.getPaging().getPageIndex()).isEqualTo(2);
    }

    @Test
    public void search_by_component_and_throw_ForbiddenException_if_not_admin_of_project() {
        WebhookDeliveryDto dto = newDto().setComponentUuid(project.uuid());
        dbClient.webhookDeliveryDao().insert(db.getSession(), dto);
        db.commit();
        userSession.logIn().addProjectPermission(USER, project);
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        ws.newRequest().setParam("componentKey", project.getDbKey()).execute();
    }

    @Test
    public void search_by_task_and_throw_ForbiddenException_if_not_admin_of_project() {
        WebhookDeliveryDto dto = newDto().setComponentUuid(project.uuid());
        dbClient.webhookDeliveryDao().insert(db.getSession(), dto);
        db.commit();
        userSession.logIn().addProjectPermission(USER, project);
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        ws.newRequest().setParam("ceTaskId", dto.getCeTaskUuid()).execute();
    }

    @Test
    public void throw_IAE_if_both_component_and_task_parameters_are_set() {
        userSession.logIn();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Either 'ceTaskId' or 'componentKey' or 'webhook' must be provided");
        ws.newRequest().setParam("componentKey", project.getDbKey()).setParam("ceTaskId", "t1").execute();
    }

    @Test
    public void throw_IAE_if_both_component_and_webhook_are_set() {
        userSession.logIn();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Either 'ceTaskId' or 'componentKey' or 'webhook' must be provided");
        ws.newRequest().setParam("componentKey", project.getDbKey()).setParam("webhook", "wh-uuid").execute();
    }
}

