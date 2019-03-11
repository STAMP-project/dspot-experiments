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


import KsqlConfig.KSQL_SERVICE_ID_CONFIG;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.testing.EqualsTester;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.KsqlException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@SuppressWarnings("SameParameterValue")
public class KsqlRequestTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String A_JSON_REQUEST = ((("{" + (("\"ksql\":\"sql\"," + "\"streamsProperties\":{") + "\"")) + (KsqlConfig.KSQL_SERVICE_ID_CONFIG)) + "\":\"some-service-id\"") + "}}";

    private static final String A_JSON_REQUEST_WITH_COMMAND_NUMBER = (((("{" + (("\"ksql\":\"sql\"," + "\"streamsProperties\":{") + "\"")) + (KsqlConfig.KSQL_SERVICE_ID_CONFIG)) + "\":\"some-service-id\"") + "},") + "\"commandSequenceNumber\":2}";

    private static final String A_JSON_REQUEST_WITH_NULL_COMMAND_NUMBER = (((("{" + (("\"ksql\":\"sql\"," + "\"streamsProperties\":{") + "\"")) + (KsqlConfig.KSQL_SERVICE_ID_CONFIG)) + "\":\"some-service-id\"") + "},") + "\"commandSequenceNumber\":null}";

    private static final ImmutableMap<String, Object> SOME_PROPS = ImmutableMap.of(KSQL_SERVICE_ID_CONFIG, "some-service-id");

    private static final long SOME_COMMAND_NUMBER = 2L;

    private static final KsqlRequest A_REQUEST = new KsqlRequest("sql", KsqlRequestTest.SOME_PROPS, null);

    private static final KsqlRequest A_REQUEST_WITH_COMMAND_NUMBER = new KsqlRequest("sql", KsqlRequestTest.SOME_PROPS, KsqlRequestTest.SOME_COMMAND_NUMBER);

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldHandleNullStatement() {
        MatcherAssert.assertThat(new KsqlRequest(null, KsqlRequestTest.SOME_PROPS, KsqlRequestTest.SOME_COMMAND_NUMBER).getKsql(), Matchers.is(""));
    }

    @Test
    public void shouldHandleNullProps() {
        MatcherAssert.assertThat(new KsqlRequest("sql", null, KsqlRequestTest.SOME_COMMAND_NUMBER).getStreamsProperties(), Matchers.is(Collections.emptyMap()));
    }

    @Test
    public void shouldHandleNullCommandNumber() {
        MatcherAssert.assertThat(new KsqlRequest("sql", KsqlRequestTest.SOME_PROPS, null).getCommandSequenceNumber(), Matchers.is(Optional.empty()));
    }

    @Test
    public void shouldDeserializeFromJson() {
        // When:
        final KsqlRequest request = KsqlRequestTest.deserialize(KsqlRequestTest.A_JSON_REQUEST);
        // Then:
        MatcherAssert.assertThat(request, Matchers.is(KsqlRequestTest.A_REQUEST));
    }

    @Test
    public void shouldDeserializeFromJsonWithCommandNumber() {
        // When:
        final KsqlRequest request = KsqlRequestTest.deserialize(KsqlRequestTest.A_JSON_REQUEST_WITH_COMMAND_NUMBER);
        // Then:
        MatcherAssert.assertThat(request, Matchers.is(KsqlRequestTest.A_REQUEST_WITH_COMMAND_NUMBER));
    }

    @Test
    public void shouldDeserializeFromJsonWithNullCommandNumber() {
        // When:
        final KsqlRequest request = KsqlRequestTest.deserialize(KsqlRequestTest.A_JSON_REQUEST_WITH_NULL_COMMAND_NUMBER);
        // Then:
        MatcherAssert.assertThat(request, Matchers.is(KsqlRequestTest.A_REQUEST));
    }

    @Test
    public void shouldSerializeToJson() {
        // When:
        final String jsonRequest = KsqlRequestTest.serialize(KsqlRequestTest.A_REQUEST);
        // Then:
        MatcherAssert.assertThat(jsonRequest, Matchers.is(KsqlRequestTest.A_JSON_REQUEST_WITH_NULL_COMMAND_NUMBER));
    }

    @Test
    public void shouldSerializeToJsonWithCommandNumber() {
        // When:
        final String jsonRequest = KsqlRequestTest.serialize(KsqlRequestTest.A_REQUEST_WITH_COMMAND_NUMBER);
        // Then:
        MatcherAssert.assertThat(jsonRequest, Matchers.is(KsqlRequestTest.A_JSON_REQUEST_WITH_COMMAND_NUMBER));
    }

    @Test
    public void shouldImplementHashCodeAndEqualsCorrectly() {
        new EqualsTester().addEqualityGroup(new KsqlRequest("sql", KsqlRequestTest.SOME_PROPS, KsqlRequestTest.SOME_COMMAND_NUMBER), new KsqlRequest("sql", KsqlRequestTest.SOME_PROPS, KsqlRequestTest.SOME_COMMAND_NUMBER)).addEqualityGroup(new KsqlRequest("different-sql", KsqlRequestTest.SOME_PROPS, KsqlRequestTest.SOME_COMMAND_NUMBER)).addEqualityGroup(new KsqlRequest("sql", ImmutableMap.of(), KsqlRequestTest.SOME_COMMAND_NUMBER)).addEqualityGroup(new KsqlRequest("sql", KsqlRequestTest.SOME_PROPS, null)).testEquals();
    }

    @Test
    public void shouldHandleShortProperties() {
        // Given:
        final String jsonRequest = ((("{" + (("\"ksql\":\"sql\"," + "\"streamsProperties\":{") + "\"")) + (KsqlConfig.SINK_NUMBER_OF_REPLICAS_PROPERTY)) + "\":2") + "}}";
        // When:
        final KsqlRequest request = KsqlRequestTest.deserialize(jsonRequest);
        // Then:
        MatcherAssert.assertThat(request.getStreamsProperties().get(SINK_NUMBER_OF_REPLICAS_PROPERTY), Matchers.is(((short) (2))));
    }

    @Test
    public void shouldThrowOnInvalidPropertyValue() {
        // Given:
        final KsqlRequest request = new KsqlRequest("sql", ImmutableMap.of(SINK_NUMBER_OF_REPLICAS_PROPERTY, "not-parsable"), null);
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage(Matchers.containsString(SINK_NUMBER_OF_REPLICAS_PROPERTY));
        expectedException.expectMessage(Matchers.containsString("not-parsable"));
        // When:
        request.getStreamsProperties();
    }

    @Test
    public void shouldHandleNullPropertyValue() {
        // Given:
        final KsqlRequest request = new KsqlRequest("sql", Collections.singletonMap(KSQL_TRANSIENT_QUERY_NAME_PREFIX_CONFIG, null), null);
        // When:
        final Map<String, Object> props = request.getStreamsProperties();
        // Then:
        MatcherAssert.assertThat(props.keySet(), Matchers.hasItem(KSQL_TRANSIENT_QUERY_NAME_PREFIX_CONFIG));
        MatcherAssert.assertThat(props.get(KSQL_TRANSIENT_QUERY_NAME_PREFIX_CONFIG), Matchers.is(Matchers.nullValue()));
    }
}

