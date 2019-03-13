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
package org.sonar.ce.task.projectanalysis.webhook;


import QualityGate.EvaluationStatus.OK;
import QualityGate.Operator.LESS_THAN;
import QualityGate.Status;
import java.util.Random;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.ce.posttask.PostProjectAnalysisTaskTester;
import org.sonar.api.ce.posttask.QualityGate;
import org.sonar.api.config.Configuration;
import org.sonar.ce.task.projectanalysis.component.ConfigurationRepository;
import org.sonar.server.qualitygate.Condition;
import org.sonar.server.webhook.WebHooks;
import org.sonar.server.webhook.WebhookPayload;
import org.sonar.server.webhook.WebhookPayloadFactory;


public class WebhookPostTaskTest {
    private final Random random = new Random();

    private final Configuration configuration = Mockito.mock(Configuration.class);

    private final WebhookPayload webhookPayload = Mockito.mock(WebhookPayload.class);

    private final WebhookPayloadFactory payloadFactory = Mockito.mock(WebhookPayloadFactory.class);

    private final WebHooks webHooks = Mockito.mock(WebHooks.class);

    private final ConfigurationRepository configurationRepository = Mockito.mock(ConfigurationRepository.class);

    private WebhookPostTask underTest = new WebhookPostTask(payloadFactory, webHooks);

    @Test
    public void call_webhooks_when_no_analysis_not_qualitygate() {
        callWebHooks(null, null);
    }

    @Test
    public void call_webhooks_with_analysis_and_qualitygate() {
        QualityGate.Condition condition = PostProjectAnalysisTaskTester.newConditionBuilder().setMetricKey(randomAlphanumeric(96)).setOperator(LESS_THAN).setErrorThreshold(randomAlphanumeric(22)).setOnLeakPeriod(random.nextBoolean()).build(OK, randomAlphanumeric(33));
        QualityGate qualityGate = PostProjectAnalysisTaskTester.newQualityGateBuilder().setId(randomAlphanumeric(23)).setName(randomAlphanumeric(66)).setStatus(Status.values()[random.nextInt(Status.values().length)]).add(condition).build();
        callWebHooks(randomAlphanumeric(40), qualityGate);
    }
}

