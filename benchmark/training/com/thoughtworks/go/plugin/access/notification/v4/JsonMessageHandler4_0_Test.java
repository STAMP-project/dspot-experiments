/**
 * Copyright 2018 ThoughtWorks, Inc.
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
package com.thoughtworks.go.plugin.access.notification.v4;


import com.thoughtworks.go.domain.notificationdata.AgentNotificationData;
import com.thoughtworks.go.plugin.api.response.Result;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class JsonMessageHandler4_0_Test {
    private JsonMessageHandler4_0 messageHandler;

    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    public static final String DATE_PATTERN_FOR_V3 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    @Test
    public void shouldBuildNotificationsInterestedInFromResponseBody() throws Exception {
        String responseBody = "{notifications=[\"pipeline-status\",\"stage-status\"]}";
        List<String> notificationsInterestedIn = messageHandler.responseMessageForNotificationsInterestedIn(responseBody);
        Assert.assertThat(notificationsInterestedIn, Matchers.is(Arrays.asList("pipeline-status", "stage-status")));
    }

    @Test
    public void shouldValidateIncorrectJsonResponseForNotificationsInterestedIn() {
        Assert.assertThat(errorMessageForNotificationsInterestedIn("{\"notifications\":{}}"), Matchers.is("Unable to de-serialize json response. 'notifications' should be of type list of string"));
        Assert.assertThat(errorMessageForNotificationsInterestedIn("{\"notifications\":[{},{}]}"), Matchers.is("Unable to de-serialize json response. Notification 'name' should be of type string"));
    }

    @Test
    public void shouldBuildSuccessResultFromNotify() throws Exception {
        String responseBody = "{\"status\":\"success\",messages=[\"message-one\",\"message-two\"]}";
        Result result = messageHandler.responseMessageForNotify(responseBody);
        assertSuccessResult(result, Arrays.asList("message-one", "message-two"));
    }

    @Test
    public void shouldBuildFailureResultFromNotify() throws Exception {
        String responseBody = "{\"status\":\"failure\",messages=[\"message-one\",\"message-two\"]}";
        Result result = messageHandler.responseMessageForNotify(responseBody);
        assertFailureResult(result, Arrays.asList("message-one", "message-two"));
    }

    @Test
    public void shouldHandleNullMessagesForNotify() throws Exception {
        assertSuccessResult(messageHandler.responseMessageForNotify("{\"status\":\"success\"}"), new ArrayList());
        assertFailureResult(messageHandler.responseMessageForNotify("{\"status\":\"failure\"}"), new ArrayList());
    }

    @Test
    public void shouldValidateIncorrectJsonForResult() {
        Assert.assertThat(errorMessageForNotifyResult(""), Matchers.is("Unable to de-serialize json response. Empty response body"));
        Assert.assertThat(errorMessageForNotifyResult("[{\"result\":\"success\"}]"), Matchers.is("Unable to de-serialize json response. Notify result should be returned as map, with key represented as string and messages represented as list"));
        Assert.assertThat(errorMessageForNotifyResult("{\"status\":true}"), Matchers.is("Unable to de-serialize json response. Notify result 'status' should be of type string"));
        Assert.assertThat(errorMessageForNotifyResult("{\"result\":true}"), Matchers.is("Unable to de-serialize json response. Notify result 'status' is a required field"));
        Assert.assertThat(errorMessageForNotifyResult("{\"status\":\"success\",\"messages\":{}}"), Matchers.is("Unable to de-serialize json response. Notify result 'messages' should be of type list of string"));
        Assert.assertThat(errorMessageForNotifyResult("{\"status\":\"success\",\"messages\":[{},{}]}"), Matchers.is("Unable to de-serialize json response. Notify result 'message' should be of type string"));
    }

    @Test
    public void shouldConstructTheStageNotificationRequest() throws Exception {
        Pipeline pipeline = createPipeline();
        String gitModifiedTime = JsonMessageHandler4_0_Test.dateToString(pipeline.getBuildCause().getMaterialRevisions().getMaterialRevision(0).getLatestModification().getModifiedTime());
        String gitFingerprint = pipeline.getBuildCause().getMaterialRevisions().getMaterialRevision(0).getMaterial().getFingerprint();
        String hgModifiedTime = JsonMessageHandler4_0_Test.dateToString(pipeline.getBuildCause().getMaterialRevisions().getMaterialRevision(1).getLatestModification().getModifiedTime());
        String hgFingerprint = pipeline.getBuildCause().getMaterialRevisions().getMaterialRevision(1).getMaterial().getFingerprint();
        String svnModifiedTime = JsonMessageHandler4_0_Test.dateToString(pipeline.getBuildCause().getMaterialRevisions().getMaterialRevision(2).getLatestModification().getModifiedTime());
        String svnFingerprint = pipeline.getBuildCause().getMaterialRevisions().getMaterialRevision(2).getMaterial().getFingerprint();
        String tfsModifiedTime = JsonMessageHandler4_0_Test.dateToString(pipeline.getBuildCause().getMaterialRevisions().getMaterialRevision(3).getLatestModification().getModifiedTime());
        String tfsFingerprint = pipeline.getBuildCause().getMaterialRevisions().getMaterialRevision(3).getMaterial().getFingerprint();
        String p4ModifiedTime = JsonMessageHandler4_0_Test.dateToString(pipeline.getBuildCause().getMaterialRevisions().getMaterialRevision(4).getLatestModification().getModifiedTime());
        String p4Fingerprint = pipeline.getBuildCause().getMaterialRevisions().getMaterialRevision(4).getMaterial().getFingerprint();
        String dependencyModifiedTime = JsonMessageHandler4_0_Test.dateToString(pipeline.getBuildCause().getMaterialRevisions().getMaterialRevision(5).getLatestModification().getModifiedTime());
        String dependencyMaterialFingerprint = pipeline.getBuildCause().getMaterialRevisions().getMaterialRevision(5).getMaterial().getFingerprint();
        String packageMaterialModifiedTime = JsonMessageHandler4_0_Test.dateToString(pipeline.getBuildCause().getMaterialRevisions().getMaterialRevision(6).getLatestModification().getModifiedTime());
        String packageMaterialFingerprint = pipeline.getBuildCause().getMaterialRevisions().getMaterialRevision(6).getMaterial().getFingerprint();
        String pluggableScmModifiedTime = JsonMessageHandler4_0_Test.dateToString(pipeline.getBuildCause().getMaterialRevisions().getMaterialRevision(7).getLatestModification().getModifiedTime());
        String pluggableScmMaterialFingerprint = pipeline.getBuildCause().getMaterialRevisions().getMaterialRevision(7).getMaterial().getFingerprint();
        String expected = (((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("{\n" + ((((((((((((" \"pipeline\": {\n" + "   \"name\": \"pipeline-name\",\n") + "   \"label\": \"LABEL-1\",\n") + "   \"counter\": \"1\",\n") + "   \"group\": \"pipeline-group\",\n") + "   \"build-cause\": [{\n") + "     \"material\": {\n") + "       \"git-configuration\": {\n") + "         \"shallow-clone\": false,\n") + "         \"branch\": \"branch\",\n") + "         \"url\": \"http://user:******@gitrepo.com\"\n") + "       },\n") + "       \"fingerprint\": \"")) + gitFingerprint) + "\",\n") + "       \"type\": \"git\"\n") + "     },\n") + "     \"changed\": true,\n") + "     \"modifications\": [{\n") + "       \"revision\": \"1\",\n") + "       \"modified-time\": \"") + gitModifiedTime) + "\",\n") + "       \"data\": {}\n") + "     }]\n") + "   }, {\n") + "     \"material\": {\n") + "       \"type\": \"mercurial\",\n") + "       \"fingerprint\": \"") + hgFingerprint) + "\",\n") + "       \"mercurial-configuration\": {\n") + "         \"url\": \"http://user:******@hgrepo.com\"\n") + "       }\n") + "     },\n") + "     \"changed\": true,\n") + "     \"modifications\": [{\n") + "       \"revision\": \"1\",\n") + "       \"modified-time\": \"") + hgModifiedTime) + "\",\n") + "       \"data\": {}\n") + "     }]\n") + "   }, {\n") + "     \"material\": {\n") + "       \"svn-configuration\": {\n") + "         \"check-externals\": false,\n") + "         \"url\": \"http://user:******@svnrepo.com\",\n") + "         \"username\": \"username\"\n") + "       },\n") + "       \"fingerprint\": \"") + svnFingerprint) + "\",\n") + "       \"type\": \"svn\"\n") + "     },\n") + "     \"changed\": true,\n") + "     \"modifications\": [{\n") + "       \"revision\": \"1\",\n") + "       \"modified-time\": \"") + svnModifiedTime) + "\",\n") + "       \"data\": {}\n") + "     }]\n") + "   }, {\n") + "     \"material\": {\n") + "       \"type\": \"tfs\",\n") + "       \"fingerprint\": \"") + tfsFingerprint) + "\",\n") + "       \"tfs-configuration\": {\n") + "         \"domain\": \"domain\",\n") + "         \"project-path\": \"project-path\",\n") + "         \"url\": \"http://user:******@tfsrepo.com\",\n") + "         \"username\": \"username\"\n") + "       }\n") + "     },\n") + "     \"changed\": true,\n") + "     \"modifications\": [{\n") + "       \"revision\": \"1\",\n") + "       \"modified-time\": \"") + tfsModifiedTime) + "\",\n") + "       \"data\": {}\n") + "     }]\n") + "   }, {\n") + "     \"material\": {\n") + "       \"perforce-configuration\": {\n") + "         \"view\": \"view\",\n") + "         \"use-tickets\": false,\n") + "         \"url\": \"127.0.0.1:1666\",\n") + "         \"username\": \"username\"\n") + "       },\n") + "       \"fingerprint\": \"") + p4Fingerprint) + "\",\n") + "       \"type\": \"perforce\"\n") + "     },\n") + "     \"changed\": true,\n") + "     \"modifications\": [{\n") + "       \"revision\": \"1\",\n") + "       \"modified-time\": \"") + p4ModifiedTime) + "\",\n") + "       \"data\": {}\n") + "     }]\n") + "   }, {\n") + "     \"material\": {\n") + "       \"pipeline-configuration\": {\n") + "         \"pipeline-name\": \"pipeline-name\",\n") + "         \"stage-name\": \"stage-name\"\n") + "       },\n") + "       \"fingerprint\": \"") + dependencyMaterialFingerprint) + "\",\n") + "       \"type\": \"pipeline\"\n") + "     },\n") + "     \"changed\": true,\n") + "     \"modifications\": [{\n") + "       \"revision\": \"pipeline-name/1/stage-name/1\",\n") + "       \"modified-time\": \"") + dependencyModifiedTime) + "\",\n") + "       \"data\": {}\n") + "     }]\n") + "   }, {\n") + "     \"material\": {\n") + "       \"plugin-id\": \"pluginid\",\n") + "       \"package-configuration\": {\n") + "         \"k3\": \"package-v1\"\n") + "       },\n") + "       \"repository-configuration\": {\n") + "         \"k1\": \"repo-v1\"\n") + "       },\n") + "       \"fingerprint\": \"") + packageMaterialFingerprint) + "\",\n") + "       \"type\": \"package\"\n") + "     },\n") + "     \"changed\": true,\n") + "     \"modifications\": [{\n") + "       \"revision\": \"1\",\n") + "       \"modified-time\": \"") + packageMaterialModifiedTime) + "\",\n") + "       \"data\": {}\n") + "     }]\n") + "   }, {\n") + "     \"material\": {\n") + "       \"plugin-id\": \"pluginid\",\n") + "       \"scm-configuration\": {\n") + "         \"k1\": \"v1\"\n") + "       },\n") + "       \"fingerprint\": \"") + pluggableScmMaterialFingerprint) + "\",\n") + "       \"type\": \"scm\"\n") + "     },\n") + "     \"changed\": true,\n") + "     \"modifications\": [{\n") + "       \"revision\": \"1\",\n") + "       \"modified-time\": \"") + pluggableScmModifiedTime) + "\",\n") + "       \"data\": {}\n") + "     }]\n") + "   }],\n") + "   \"stage\": {\n") + "     \"name\": \"stage-name\",\n") + "     \"counter\": \"1\",\n") + "     \"state\": \"Passed\",\n") + "     \"approval-type\": \"success\",\n") + "     \"approved-by\": \"changes\",\n") + "     \"previous-stage-name\": \"previous-stage\",\n") + "     \"previous-stage-counter\": 1,\n") + "     \"result\": \"Passed\",\n") + "     \"create-time\": \"2011-07-13T14:13:37.100+0000\",\n") + "     \"last-transition-time\": \"2011-07-13T14:13:37.100+0000\",\n") + "     \"jobs\": [{\n") + "       \"name\": \"job-name\",\n") + "       \"schedule-time\": \"2011-07-13T14:13:37.100+0000\",\n") + "       \"assign-time\": \"2011-07-13T14:13:37.100+0000\",\n") + "       \"complete-time\": \"2011-07-13T14:13:37.100+0000\",\n") + "       \"state\": \"Completed\",\n") + "       \"result\": \"Passed\",\n") + "       \"agent-uuid\": \"uuid\"\n") + "     }]\n") + "   }\n") + " }\n") + "}";
        String request = messageHandler.requestMessageForNotify(new com.thoughtworks.go.domain.notificationdata.StageNotificationData(pipeline.getFirstStage(), pipeline.getBuildCause(), "pipeline-group"));
        JsonFluentAssert.assertThatJson(expected).isEqualTo(request);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldThrowExceptionIfAnUnhandledObjectIsPassed() {
        thrown.expectMessage(String.format("Converter for %s not supported", Pipeline.class.getCanonicalName()));
        messageHandler.requestMessageForNotify(new Pipeline());
    }

    @Test
    public void shouldConstructAgentNotificationRequestMessage() throws Exception {
        Date transition_time = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(JsonMessageHandler4_0_Test.DATE_PATTERN_FOR_V3);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String time = simpleDateFormat.format(transition_time);
        AgentNotificationData agentNotificationData = new AgentNotificationData("agent_uuid", "agent_hostname", true, "127.0.0.1", "rh", "100", "enabled", "building", "building", transition_time);
        String expected = ((("{\n" + ((((((((("    \"agent_config_state\": \"enabled\",\n" + "    \"agent_state\": \"building\",\n") + "    \"build_state\": \"building\",\n") + "    \"is_elastic\": true,\n") + "    \"free_space\": \"100\",\n") + "    \"host_name\": \"agent_hostname\",\n") + "    \"ip_address\": \"127.0.0.1\",\n") + "    \"operating_system\": \"rh\",\n") + "    \"uuid\": \"agent_uuid\",\n") + "    \"transition_time\": \"")) + time) + "\"\n") + "}\n";
        String message = messageHandler.requestMessageForNotify(agentNotificationData);
        JsonFluentAssert.assertThatJson(expected).isEqualTo(message);
    }
}

