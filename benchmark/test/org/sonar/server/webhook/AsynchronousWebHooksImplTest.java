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
package org.sonar.server.webhook;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.organization.OrganizationDbTester;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.webhook.WebhookDbTester;
import org.sonar.server.async.AsyncExecution;
import org.sonar.server.organization.DefaultOrganizationProvider;
import org.sonar.server.organization.TestDefaultOrganizationProvider;


public class AsynchronousWebHooksImplTest {
    private System2 system2 = Mockito.mock(System2.class);

    @Rule
    public DbTester db = create(system2);

    private WebhookDbTester webhookDbTester = db.webhooks();

    private ComponentDbTester componentDbTester = db.components();

    private OrganizationDbTester organizationDbTester = db.organizations();

    private DefaultOrganizationProvider defaultOrganizationProvider = TestDefaultOrganizationProvider.from(db);

    private static final long NOW = 1500000000000L;

    private final TestWebhookCaller caller = new TestWebhookCaller();

    private final WebhookDeliveryStorage deliveryStorage = Mockito.mock(WebhookDeliveryStorage.class);

    private final WebhookPayload mock = Mockito.mock(WebhookPayload.class);

    private final AsynchronousWebHooksImplTest.RecordingAsyncExecution asyncExecution = new AsynchronousWebHooksImplTest.RecordingAsyncExecution();

    private final WebHooksImpl underTest = new WebHooksImpl(caller, deliveryStorage, asyncExecution, db.getDbClient());

    @Test
    public void send_global_webhooks() {
        OrganizationDto organizationDto = db.getDefaultOrganization();
        ComponentDto project = componentDbTester.insertPrivateProject(( componentDto) -> componentDto.setOrganizationUuid(organizationDto.getUuid()));
        webhookDbTester.insert(newWebhook(organizationDto).setName("First").setUrl("http://url1"));
        webhookDbTester.insert(newWebhook(organizationDto).setName("Second").setUrl("http://url2"));
        caller.enqueueSuccess(AsynchronousWebHooksImplTest.NOW, 200, 1234);
        caller.enqueueFailure(AsynchronousWebHooksImplTest.NOW, new IOException("Fail to connect"));
        underTest.sendProjectAnalysisUpdate(new WebHooks.Analysis(project.uuid(), "1", "#1"), () -> mock);
        assertThat(caller.countSent()).isZero();
        Mockito.verifyZeroInteractions(deliveryStorage);
        asyncExecution.executeRecorded();
        assertThat(caller.countSent()).isEqualTo(2);
        Mockito.verify(deliveryStorage, Mockito.times(2)).persist(ArgumentMatchers.any(WebhookDelivery.class));
        Mockito.verify(deliveryStorage).purge(project.uuid());
    }

    private static class RecordingAsyncExecution implements AsyncExecution {
        private final List<Runnable> runnableList = new ArrayList<>();

        @Override
        public void addToQueue(Runnable r) {
            runnableList.add(Objects.requireNonNull(r));
        }

        public void executeRecorded() {
            ArrayList<Runnable> runnables = new ArrayList<>(runnableList);
            runnableList.clear();
            runnables.forEach(Runnable::run);
        }
    }
}

