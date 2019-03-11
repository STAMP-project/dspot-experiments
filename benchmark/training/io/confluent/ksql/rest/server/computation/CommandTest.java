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
package io.confluent.ksql.rest.server.computation;


import JsonMapper.INSTANCE;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class CommandTest {
    @Test
    public void shouldDeserializeCorrectly() throws IOException {
        final String commandStr = "{" + ((("\"statement\": \"test statement;\", " + "\"streamsProperties\": {\"foo\": \"bar\"}, ") + "\"originalProperties\": {\"biz\": \"baz\"} ") + "}");
        final ObjectMapper mapper = INSTANCE.mapper;
        final Command command = mapper.readValue(commandStr, Command.class);
        Assert.assertThat(command.getStatement(), CoreMatchers.equalTo("test statement;"));
        final Map<String, Object> expecteOverwriteProperties = Collections.singletonMap("foo", "bar");
        Assert.assertThat(command.getOverwriteProperties(), CoreMatchers.equalTo(expecteOverwriteProperties));
        final Map<String, Object> expectedOriginalProperties = Collections.singletonMap("biz", "baz");
        Assert.assertThat(command.getOriginalProperties(), CoreMatchers.equalTo(expectedOriginalProperties));
        Assert.assertThat(command.isPreVersion5(), CoreMatchers.is(false));
    }

    @Test
    public void shouldDeserializeWithoutKsqlConfigCorrectly() throws IOException {
        final String commandStr = "{" + (("\"statement\": \"test statement;\", " + "\"streamsProperties\": {\"foo\": \"bar\"}") + "}");
        final ObjectMapper mapper = INSTANCE.mapper;
        final Command command = mapper.readValue(commandStr, Command.class);
        Assert.assertThat(command.getStatement(), CoreMatchers.equalTo("test statement;"));
        final Map<String, Object> expecteOverwriteProperties = Collections.singletonMap("foo", "bar");
        Assert.assertThat(command.getOverwriteProperties(), CoreMatchers.equalTo(expecteOverwriteProperties));
        Assert.assertThat(command.getOriginalProperties(), CoreMatchers.equalTo(Collections.emptyMap()));
        Assert.assertThat(command.isPreVersion5(), CoreMatchers.equalTo(true));
    }

    @Test
    public void shouldSerializeDeserializeCorrectly() throws IOException {
        final Command command = new Command("test statement;", Collections.singletonMap("foo", "bar"), Collections.singletonMap("biz", "baz"));
        final ObjectMapper mapper = INSTANCE.mapper;
        final String serialized = mapper.writeValueAsString(command);
        grep(serialized, ".*\"streamsProperties\" *: *\\{ *\"foo\" *: *\"bar\" *\\}.*");
        grep(serialized, ".*\"statement\" *: *\"test statement;\".*");
        grep(serialized, ".*\"originalProperties\" *: *\\{ *\"biz\" *: *\"baz\" *\\}.*");
        final Command deserialized = mapper.readValue(serialized, Command.class);
        Assert.assertThat(deserialized, CoreMatchers.equalTo(command));
    }
}

