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
package io.confluent.ksql.rest.entity;


import CommandStatus.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.confluent.ksql.rest.server.computation.CommandId;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
public class CommandStatusEntityTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String JSON_ENTITY = "{" + (((((((("\"@type\":\"currentStatus\"," + "\"statementText\":\"sql\",") + "\"commandId\":\"topic/1/create\",") + "\"commandStatus\":{") + "\"status\":\"SUCCESS\",") + "\"message\":\"some success message\"") + "},") + "\"commandSequenceNumber\":2") + "}");

    private static final String OLD_JSON_ENTITY = "{" + (((((("\"@type\":\"currentStatus\"," + "\"statementText\":\"sql\",") + "\"commandId\":\"topic/1/create\",") + "\"commandStatus\":{") + "\"status\":\"SUCCESS\",") + "\"message\":\"some success message\"") + "}}");

    private static final String STATEMENT_TEXT = "sql";

    private static final CommandId COMMAND_ID = CommandId.fromString("topic/1/create");

    private static final CommandStatus COMMAND_STATUS = new CommandStatus(Status.SUCCESS, "some success message");

    private static final long COMMAND_SEQUENCE_NUMBER = 2L;

    private static final CommandStatusEntity ENTITY = new CommandStatusEntity(CommandStatusEntityTest.STATEMENT_TEXT, CommandStatusEntityTest.COMMAND_ID, CommandStatusEntityTest.COMMAND_STATUS, CommandStatusEntityTest.COMMAND_SEQUENCE_NUMBER);

    private static final CommandStatusEntity ENTITY_WITHOUT_SEQUENCE_NUMBER = new CommandStatusEntity(CommandStatusEntityTest.STATEMENT_TEXT, CommandStatusEntityTest.COMMAND_ID, CommandStatusEntityTest.COMMAND_STATUS, null);

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldSerializeToJson() throws Exception {
        // When:
        final String json = CommandStatusEntityTest.OBJECT_MAPPER.writeValueAsString(CommandStatusEntityTest.ENTITY);
        // Then:
        MatcherAssert.assertThat(json, CoreMatchers.is(CommandStatusEntityTest.JSON_ENTITY));
    }

    @Test
    public void shouldDeserializeFromJson() throws Exception {
        // When:
        final CommandStatusEntity entity = CommandStatusEntityTest.OBJECT_MAPPER.readValue(CommandStatusEntityTest.JSON_ENTITY, CommandStatusEntity.class);
        // Then:
        MatcherAssert.assertThat(entity, CoreMatchers.is(CommandStatusEntityTest.ENTITY));
    }

    @Test
    public void shouldBeAbleToDeserializeOlderServerMessage() throws Exception {
        // When:
        final CommandStatusEntity entity = CommandStatusEntityTest.OBJECT_MAPPER.readValue(CommandStatusEntityTest.OLD_JSON_ENTITY, CommandStatusEntity.class);
        // Then:
        MatcherAssert.assertThat(entity, CoreMatchers.is(CommandStatusEntityTest.ENTITY_WITHOUT_SEQUENCE_NUMBER));
    }

    @Test
    public void shouldHandleNullSequenceNumber() {
        // When:
        final CommandStatusEntity entity = new CommandStatusEntity(CommandStatusEntityTest.STATEMENT_TEXT, CommandStatusEntityTest.COMMAND_ID, CommandStatusEntityTest.COMMAND_STATUS, null);
        // Then:
        MatcherAssert.assertThat(entity.getCommandSequenceNumber(), CoreMatchers.is((-1L)));
    }

    @Test
    public void shouldThrowOnNullCommandId() {
        // Given:
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("commandId");
        // When:
        new CommandStatusEntity(CommandStatusEntityTest.STATEMENT_TEXT, null, CommandStatusEntityTest.COMMAND_STATUS, CommandStatusEntityTest.COMMAND_SEQUENCE_NUMBER);
    }

    @Test
    public void shouldThrowOnNullCommandStatus() {
        // Given:
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("commandStatus");
        // When:
        new CommandStatusEntity(CommandStatusEntityTest.STATEMENT_TEXT, CommandStatusEntityTest.COMMAND_ID, null, CommandStatusEntityTest.COMMAND_SEQUENCE_NUMBER);
    }
}

