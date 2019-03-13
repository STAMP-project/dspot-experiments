/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.rest.server.resources;


import CommandId.Action;
import CommandId.Action.CREATE;
import CommandId.Type;
import CommandId.Type.STREAM;
import CommandStatus.Status;
import Errors.ERROR_CODE_NOT_FOUND;
import Response.Status.NOT_FOUND;
import io.confluent.ksql.rest.entity.CommandStatus;
import io.confluent.ksql.rest.entity.CommandStatuses;
import io.confluent.ksql.rest.entity.KsqlErrorMessage;
import io.confluent.ksql.rest.server.computation.CommandId;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class StatusResourceTest {
    private static final Map<CommandId, CommandStatus> mockCommandStatuses;

    static {
        mockCommandStatuses = new HashMap();
        StatusResourceTest.mockCommandStatuses.put(new CommandId(Type.TOPIC, "test_topic", Action.CREATE), new CommandStatus(Status.SUCCESS, "Topic created successfully"));
        StatusResourceTest.mockCommandStatuses.put(new CommandId(Type.STREAM, "test_stream", Action.CREATE), new CommandStatus(Status.ERROR, "Hi Ewen!"));
        StatusResourceTest.mockCommandStatuses.put(new CommandId(Type.TERMINATE, "5", Action.CREATE), new CommandStatus(Status.QUEUED, "Command written to command topic"));
    }

    @Test
    public void testGetAllStatuses() {
        final StatusResource testResource = getTestStatusResource();
        final Object statusesEntity = testResource.getAllStatuses().getEntity();
        Assert.assertThat(statusesEntity, CoreMatchers.instanceOf(CommandStatuses.class));
        final CommandStatuses testCommandStatuses = ((CommandStatuses) (statusesEntity));
        final Map<CommandId, CommandStatus.Status> expectedCommandStatuses = CommandStatuses.fromFullStatuses(StatusResourceTest.mockCommandStatuses);
        Assert.assertEquals(expectedCommandStatuses, testCommandStatuses);
    }

    @Test
    public void testGetStatus() throws Exception {
        final StatusResource testResource = getTestStatusResource();
        for (final Map.Entry<CommandId, CommandStatus> commandEntry : StatusResourceTest.mockCommandStatuses.entrySet()) {
            final CommandId commandId = commandEntry.getKey();
            final CommandStatus expectedCommandStatus = commandEntry.getValue();
            final Object statusEntity = testResource.getStatus(commandId.getType().name(), commandId.getEntity(), commandId.getAction().name()).getEntity();
            Assert.assertThat(statusEntity, CoreMatchers.instanceOf(CommandStatus.class));
            final CommandStatus testCommandStatus = ((CommandStatus) (statusEntity));
            Assert.assertEquals(expectedCommandStatus, testCommandStatus);
        }
    }

    @Test
    public void testGetStatusNotFound() throws Exception {
        final StatusResource testResource = getTestStatusResource();
        final Response response = testResource.getStatus(STREAM.name(), "foo", CREATE.name());
        Assert.assertThat(response.getStatus(), CoreMatchers.equalTo(NOT_FOUND.getStatusCode()));
        Assert.assertThat(response.getEntity(), CoreMatchers.instanceOf(KsqlErrorMessage.class));
        final KsqlErrorMessage errorMessage = ((KsqlErrorMessage) (response.getEntity()));
        Assert.assertThat(errorMessage.getErrorCode(), CoreMatchers.equalTo(ERROR_CODE_NOT_FOUND));
        Assert.assertThat(errorMessage.getMessage(), CoreMatchers.equalTo("Command not found"));
    }
}

