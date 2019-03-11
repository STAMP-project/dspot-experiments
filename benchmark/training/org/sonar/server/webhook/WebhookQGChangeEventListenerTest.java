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


import BranchType.LONG;
import Metric.Level;
import System2.INSTANCE;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Collections;
import java.util.Set;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sonar.api.config.Configuration;
import org.sonar.api.measures.Metric;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.component.BranchDto;
import org.sonar.db.component.ComponentDto;
import org.sonar.server.qualitygate.EvaluatedQualityGate;
import org.sonar.server.qualitygate.changeevent.QGChangeEvent;
import org.sonar.server.qualitygate.changeevent.QGChangeEventListener;


@RunWith(DataProviderRunner.class)
public class WebhookQGChangeEventListenerTest {
    private static final Set<QGChangeEventListener.ChangedIssue> CHANGED_ISSUES_ARE_IGNORED = Collections.emptySet();

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    private DbClient dbClient = dbTester.getDbClient();

    private EvaluatedQualityGate newQualityGate = Mockito.mock(EvaluatedQualityGate.class);

    private WebHooks webHooks = Mockito.mock(WebHooks.class);

    private WebhookPayloadFactory webhookPayloadFactory = Mockito.mock(WebhookPayloadFactory.class);

    private DbClient spiedOnDbClient = Mockito.spy(dbClient);

    private WebhookQGChangeEventListener underTest = new WebhookQGChangeEventListener(webHooks, webhookPayloadFactory, spiedOnDbClient);

    private DbClient mockedDbClient = Mockito.mock(DbClient.class);

    private WebhookQGChangeEventListener mockedUnderTest = new WebhookQGChangeEventListener(webHooks, webhookPayloadFactory, mockedDbClient);

    @Test
    public void onIssueChanges_has_no_effect_if_event_has_neither_previousQGStatus_nor_qualityGate() {
        Configuration configuration = Mockito.mock(Configuration.class);
        QGChangeEvent qualityGateEvent = WebhookQGChangeEventListenerTest.newQGChangeEvent(configuration, null, null);
        mockWebhookEnabled(qualityGateEvent.getProject());
        underTest.onIssueChanges(qualityGateEvent, WebhookQGChangeEventListenerTest.CHANGED_ISSUES_ARE_IGNORED);
        Mockito.verifyZeroInteractions(webhookPayloadFactory, mockedDbClient);
    }

    @Test
    public void onIssueChanges_has_no_effect_if_event_has_same_status_in_previous_and_new_QG() {
        Configuration configuration = Mockito.mock(Configuration.class);
        Metric.Level previousStatus = WebhookQGChangeEventListenerTest.randomLevel();
        Mockito.when(newQualityGate.getStatus()).thenReturn(previousStatus);
        QGChangeEvent qualityGateEvent = WebhookQGChangeEventListenerTest.newQGChangeEvent(configuration, previousStatus, newQualityGate);
        mockWebhookEnabled(qualityGateEvent.getProject());
        underTest.onIssueChanges(qualityGateEvent, WebhookQGChangeEventListenerTest.CHANGED_ISSUES_ARE_IGNORED);
        Mockito.verifyZeroInteractions(webhookPayloadFactory, mockedDbClient);
    }

    @Test
    public void onIssueChanges_calls_webhook_on_long_branch() {
        onIssueChangesCallsWebhookOnBranch(LONG);
    }

    @Test
    public void onIssueChanges_calls_webhook_on_short_branch() {
        onIssueChangesCallsWebhookOnBranch(SHORT);
    }

    private static class ComponentAndBranch {
        private final ComponentDto component;

        private final BranchDto branch;

        private ComponentAndBranch(ComponentDto component, BranchDto branch) {
            this.component = component;
            this.branch = branch;
        }

        public ComponentDto getComponent() {
            return component;
        }

        public BranchDto getBranch() {
            return branch;
        }

        public String uuid() {
            return component.uuid();
        }
    }
}

