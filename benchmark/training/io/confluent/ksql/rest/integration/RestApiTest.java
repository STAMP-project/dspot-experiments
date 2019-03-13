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
package io.confluent.ksql.rest.integration;


import MediaType.APPLICATION_JSON_TYPE;
import Response.Status.OK;
import Versions.KSQL_V1_JSON;
import io.confluent.common.utils.IntegrationTest;
import io.confluent.ksql.integration.IntegrationTestHarness;
import io.confluent.ksql.test.util.TestKsqlRestApp;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.RuleChain;


@SuppressWarnings("unchecked")
@Category({ IntegrationTest.class })
public class RestApiTest {
    private static final String PAGE_VIEW_TOPIC = "pageviews";

    private static final String PAGE_VIEW_STREAM = "pageviews_original";

    private static final IntegrationTestHarness TEST_HARNESS = IntegrationTestHarness.build();

    private static final TestKsqlRestApp REST_APP = TestKsqlRestApp.builder(RestApiTest.TEST_HARNESS::kafkaBootstrapServers).build();

    @ClassRule
    public static final RuleChain CHAIN = RuleChain.outerRule(RestApiTest.TEST_HARNESS).around(RestApiTest.REST_APP);

    private Client restClient;

    @Test
    public void shouldExecuteStreamingQueryWithV1ContentType() {
        try (final Response response = makeStreamingRequest((("SELECT * from " + (RestApiTest.PAGE_VIEW_STREAM)) + ";"), KSQL_V1_JSON, KSQL_V1_JSON)) {
            Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        }
    }

    @Test
    public void shouldExecuteStreamingQueryWithJsonContentType() {
        try (final Response response = makeStreamingRequest((("SELECT * from " + (RestApiTest.PAGE_VIEW_STREAM)) + "; "), APPLICATION_JSON_TYPE.toString(), APPLICATION_JSON_TYPE.toString())) {
            Assert.assertEquals(OK.getStatusCode(), response.getStatus());
        }
    }
}

