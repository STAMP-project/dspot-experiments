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


import Branch.Type;
import CeTask.Status;
import Condition.Operator;
import EvaluatedCondition.EvaluationStatus.NO_VALUE;
import Metric.Level.ERROR;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Map;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.platform.Server;
import org.sonar.api.utils.System2;
import org.sonar.server.qualitygate.Condition;
import org.sonar.server.qualitygate.EvaluatedQualityGate;
import org.sonar.server.qualitygate.QualityGate;


public class WebhookPayloadFactoryImplTest {
    private static final String PROJECT_KEY = "P1";

    private Server server = Mockito.mock(Server.class);

    private System2 system2 = Mockito.mock(System2.class);

    private WebhookPayloadFactory underTest = new WebhookPayloadFactoryImpl(server, system2);

    @Test
    public void create_payload_for_successful_analysis() {
        CeTask task = new CeTask("#1", Status.SUCCESS);
        Condition condition = new Condition("coverage", Operator.GREATER_THAN, "70.0");
        EvaluatedQualityGate gate = EvaluatedQualityGate.newBuilder().setQualityGate(new QualityGate("G1", "Gate One", Collections.singleton(condition))).setStatus(ERROR).addCondition(condition, EvaluatedCondition.EvaluationStatus.ERROR, "74.0").build();
        ProjectAnalysis analysis = WebhookPayloadFactoryImplTest.newAnalysis(task, gate, null, 1500000000000L, Collections.emptyMap());
        WebhookPayload payload = underTest.create(analysis);
        assertThat(payload.getProjectKey()).isEqualTo(WebhookPayloadFactoryImplTest.PROJECT_KEY);
        assertJson(payload.getJson()).isSimilarTo(("{" + ((((((((((((((((((((((((("  \"serverUrl\": \"http://foo\"," + "  \"taskId\": \"#1\",") + "  \"status\": \"SUCCESS\",") + "  \"analysedAt\": \"2017-07-14T04:40:00+0200\",") + "  \"changedAt\": \"2017-07-14T04:40:00+0200\",") + "  \"project\": {") + "    \"key\": \"P1\",") + "    \"name\": \"Project One\",") + "    \"url\": \"http://foo/dashboard?id=P1\"") + "  },") + "  \"qualityGate\": {") + "    \"name\": \"Gate One\",") + "    \"status\": \"ERROR\",") + "    \"conditions\": [") + "      {") + "        \"metric\": \"coverage\",") + "        \"operator\": \"GREATER_THAN\",") + "        \"value\": \"74.0\",") + "        \"status\": \"ERROR\",") + "        \"errorThreshold\": \"70.0\"") + "      }") + "    ]") + "  },") + "  \"properties\": {") + "  }") + "}")));
    }

    @Test
    public void create_payload_with_gate_conditions_without_value() {
        CeTask task = new CeTask("#1", Status.SUCCESS);
        Condition condition = new Condition("coverage", Operator.GREATER_THAN, "70.0");
        EvaluatedQualityGate gate = EvaluatedQualityGate.newBuilder().setQualityGate(new QualityGate("G1", "Gate One", Collections.singleton(condition))).setStatus(ERROR).addCondition(condition, NO_VALUE, null).build();
        ProjectAnalysis analysis = WebhookPayloadFactoryImplTest.newAnalysis(task, gate, null, 1500000000000L, Collections.emptyMap());
        WebhookPayload payload = underTest.create(analysis);
        assertThat(payload.getProjectKey()).isEqualTo(WebhookPayloadFactoryImplTest.PROJECT_KEY);
        assertJson(payload.getJson()).isSimilarTo(("{" + (((((((((((((((((((((("  \"serverUrl\": \"http://foo\"," + "  \"taskId\": \"#1\",") + "  \"status\": \"SUCCESS\",") + "  \"analysedAt\": \"2017-07-14T04:40:00+0200\",") + "  \"changedAt\": \"2017-07-14T04:40:00+0200\",") + "  \"project\": {") + "    \"key\": \"P1\",") + "    \"name\": \"Project One\",") + "    \"url\": \"http://foo/dashboard?id=P1\"") + "  },") + "  \"qualityGate\": {") + "    \"name\": \"Gate One\",") + "    \"status\": \"ERROR\",") + "    \"conditions\": [") + "      {") + "        \"metric\": \"coverage\",") + "        \"operator\": \"GREATER_THAN\",") + "        \"status\": \"NO_VALUE\",") + "        \"errorThreshold\": \"70.0\"") + "      }") + "    ]") + "  }") + "}")));
    }

    @Test
    public void create_payload_with_analysis_properties() {
        CeTask task = new CeTask("#1", Status.SUCCESS);
        EvaluatedQualityGate gate = EvaluatedQualityGate.newBuilder().setQualityGate(new QualityGate("G1", "Gate One", Collections.emptySet())).setStatus(ERROR).build();
        Map<String, String> scannerProperties = ImmutableMap.of("sonar.analysis.revision", "ab45d24", "sonar.analysis.buildNumber", "B123", "not.prefixed.with.sonar.analysis", "should be ignored", "ignored", "should be ignored too");
        ProjectAnalysis analysis = WebhookPayloadFactoryImplTest.newAnalysis(task, gate, null, 1500000000000L, scannerProperties);
        WebhookPayload payload = underTest.create(analysis);
        assertJson(payload.getJson()).isSimilarTo(("{" + (((((((((((((((((((("  \"serverUrl\": \"http://foo\"," + "  \"taskId\": \"#1\",") + "  \"status\": \"SUCCESS\",") + "  \"analysedAt\": \"2017-07-14T04:40:00+0200\",") + "  \"changedAt\": \"2017-07-14T04:40:00+0200\",") + "  \"project\": {") + "    \"key\": \"P1\",") + "    \"name\": \"Project One\",") + "    \"url\": \"http://foo/dashboard?id=P1\"") + "  },") + "  \"qualityGate\": {") + "    \"name\": \"Gate One\",") + "    \"status\": \"ERROR\",") + "    \"conditions\": [") + "    ]") + "  },") + "  \"properties\": {") + "    \"sonar.analysis.revision\": \"ab45d24\",") + "    \"sonar.analysis.buildNumber\": \"B123\"") + "  }") + "}")));
        assertThat(payload.getJson()).doesNotContain("not.prefixed.with.sonar.analysis").doesNotContain("ignored");
    }

    @Test
    public void create_payload_for_failed_analysis() {
        CeTask ceTask = new CeTask("#1", Status.FAILED);
        ProjectAnalysis analysis = WebhookPayloadFactoryImplTest.newAnalysis(ceTask, null, null, 1500000000000L, Collections.emptyMap());
        WebhookPayload payload = underTest.create(analysis);
        assertThat(payload.getProjectKey()).isEqualTo(WebhookPayloadFactoryImplTest.PROJECT_KEY);
        assertJson(payload.getJson()).isSimilarTo(("{" + ((((((((((("  \"serverUrl\": \"http://foo\"," + "  \"taskId\": \"#1\",") + "  \"status\": \"FAILED\",") + "  \"changedAt\": \"2017-07-14T04:40:00+0200\",") + "  \"project\": {") + "    \"key\": \"P1\",") + "    \"name\": \"Project One\",") + "    \"url\": \"http://foo/dashboard?id=P1\"") + "  },") + "  \"properties\": {") + "  }") + "}")));
    }

    @Test
    public void create_payload_for_no_analysis_date() {
        CeTask ceTask = new CeTask("#1", Status.FAILED);
        ProjectAnalysis analysis = WebhookPayloadFactoryImplTest.newAnalysis(ceTask, null, null, null, Collections.emptyMap());
        WebhookPayload payload = underTest.create(analysis);
        assertThat(payload.getProjectKey()).isEqualTo(WebhookPayloadFactoryImplTest.PROJECT_KEY);
        assertJson(payload.getJson()).isSimilarTo(("{" + (((((((((("  \"serverUrl\": \"http://foo\"," + "  \"taskId\": \"#1\",") + "  \"status\": \"FAILED\",") + "  \"changedAt\": \"1970-01-01T01:25:00+0100\",") + "  \"project\": {") + "    \"key\": \"P1\",") + "    \"name\": \"Project One\"") + "  },") + "  \"properties\": {") + "  }") + "}")));
    }

    @Test
    public void create_payload_on_short_branch() {
        CeTask task = new CeTask("#1", Status.SUCCESS);
        ProjectAnalysis analysis = WebhookPayloadFactoryImplTest.newAnalysis(task, null, new Branch(false, "feature/foo", Type.SHORT), 1500000000000L, Collections.emptyMap());
        WebhookPayload payload = underTest.create(analysis);
        assertJson(payload.getJson()).isSimilarTo(("{" + (((((("\"branch\": {" + "  \"name\": \"feature/foo\",") + "  \"type\": \"SHORT\",") + "  \"isMain\": false,") + "  \"url\": \"http://foo/project/issues?branch=feature%2Ffoo&id=P1&resolved=false\"") + "}") + "}")));
    }

    @Test
    public void create_payload_on_pull_request() {
        CeTask task = new CeTask("#1", Status.SUCCESS);
        ProjectAnalysis analysis = WebhookPayloadFactoryImplTest.newAnalysis(task, null, new Branch(false, "pr/foo", Type.PULL_REQUEST), 1500000000000L, Collections.emptyMap());
        WebhookPayload payload = underTest.create(analysis);
        assertJson(payload.getJson()).isSimilarTo(("{" + (((((("\"branch\": {" + "  \"name\": \"pr/foo\",") + "  \"type\": \"PULL_REQUEST\",") + "  \"isMain\": false,") + "  \"url\": \"http://foo/project/issues?pullRequest=pr%2Ffoo&id=P1&resolved=false\"") + "}") + "}")));
    }

    @Test
    public void create_without_ce_task() {
        ProjectAnalysis analysis = WebhookPayloadFactoryImplTest.newAnalysis(null, null, null, null, Collections.emptyMap());
        WebhookPayload payload = underTest.create(analysis);
        String json = payload.getJson();
        assertThat(json).doesNotContain("taskId");
        assertJson(json).isSimilarTo(("{" + ((((((((("  \"serverUrl\": \"http://foo\"," + "  \"status\": \"SUCCESS\",") + "  \"changedAt\": \"1970-01-01T01:25:00+0100\",") + "  \"project\": {") + "    \"key\": \"P1\",") + "    \"name\": \"Project One\"") + "  },") + "  \"properties\": {") + "  }") + "}")));
    }

    @Test
    public void create_payload_on_long_branch() {
        CeTask task = new CeTask("#1", Status.SUCCESS);
        ProjectAnalysis analysis = WebhookPayloadFactoryImplTest.newAnalysis(task, null, new Branch(false, "feature/foo", Type.LONG), 1500000000000L, Collections.emptyMap());
        WebhookPayload payload = underTest.create(analysis);
        assertJson(payload.getJson()).isSimilarTo(("{" + (((((("\"branch\": {" + "  \"name\": \"feature/foo\"") + "  \"type\": \"LONG\"") + "  \"isMain\": false,") + "  \"url\": \"http://foo/dashboard?branch=feature%2Ffoo&id=P1\"") + "}") + "}")));
    }

    @Test
    public void create_payload_on_main_branch_without_name() {
        CeTask task = new CeTask("#1", Status.SUCCESS);
        ProjectAnalysis analysis = WebhookPayloadFactoryImplTest.newAnalysis(task, null, new Branch(true, null, Type.LONG), 1500000000000L, Collections.emptyMap());
        WebhookPayload payload = underTest.create(analysis);
        assertJson(payload.getJson()).isSimilarTo(("{" + ((((("\"branch\": {" + "  \"type\": \"LONG\"") + "  \"isMain\": true,") + "  \"url\": \"http://foo/dashboard?id=P1\"") + "}") + "}")));
    }
}

