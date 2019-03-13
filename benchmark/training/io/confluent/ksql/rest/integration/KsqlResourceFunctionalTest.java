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


import io.confluent.common.utils.IntegrationTest;
import io.confluent.ksql.integration.IntegrationTestHarness;
import io.confluent.ksql.rest.client.KsqlRestClient;
import io.confluent.ksql.rest.entity.CommandStatusEntity;
import io.confluent.ksql.rest.entity.KsqlEntity;
import io.confluent.ksql.rest.entity.SourceDescriptionEntity;
import io.confluent.ksql.test.util.TestKsqlRestApp;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.RuleChain;


@SuppressWarnings("unchecked")
@Category({ IntegrationTest.class })
public class KsqlResourceFunctionalTest {
    private static final String PAGE_VIEW_TOPIC = "pageviews";

    private static final String PAGE_VIEW_STREAM = "pageviews_original";

    private static final AtomicInteger NEXT_QUERY_ID = new AtomicInteger(0);

    private static final IntegrationTestHarness TEST_HARNESS = IntegrationTestHarness.build();

    private static final TestKsqlRestApp REST_APP = TestKsqlRestApp.builder(KsqlResourceFunctionalTest.TEST_HARNESS::kafkaBootstrapServers).build();

    @ClassRule
    public static final RuleChain CHAIN = RuleChain.outerRule(KsqlResourceFunctionalTest.TEST_HARNESS).around(KsqlResourceFunctionalTest.REST_APP);

    private KsqlRestClient restClient;

    private String source;

    @Test
    public void shouldDistributeMultipleInterDependantDmlStatements() {
        // When:
        final List<KsqlEntity> results = makeKsqlRequest(((("CREATE STREAM S AS SELECT * FROM " + (source)) + ";") + "CREATE STREAM S2 AS SELECT * FROM S;"));
        // Then:
        Assert.assertThat(results, Matchers.contains(CoreMatchers.instanceOf(CommandStatusEntity.class), CoreMatchers.instanceOf(CommandStatusEntity.class)));
        KsqlResourceFunctionalTest.assertSuccessful(results);
        Assert.assertThat(KsqlResourceFunctionalTest.REST_APP.getPersistentQueries(), Matchers.hasItems(Matchers.startsWith("CSAS_S_"), Matchers.startsWith("CSAS_S2_")));
    }

    @Test
    public void shouldHandleInterDependantExecutableAndNonExecutableStatements() {
        // When:
        final List<KsqlEntity> results = makeKsqlRequest(((("CREATE STREAM S AS SELECT * FROM " + (source)) + ";") + "DESCRIBE S;"));
        // Then:
        Assert.assertThat(results, Matchers.contains(CoreMatchers.instanceOf(CommandStatusEntity.class), CoreMatchers.instanceOf(SourceDescriptionEntity.class)));
    }

    @Test
    public void shouldHandleInterDependantCsasTerminateAndDrop() {
        // When:
        final List<KsqlEntity> results = makeKsqlRequest((((((("CREATE STREAM SS AS SELECT * FROM " + (source)) + ";") + "TERMINATE CSAS_SS_") + (KsqlResourceFunctionalTest.NEXT_QUERY_ID.get())) + ";") + "DROP STREAM SS;"));
        // Then:
        Assert.assertThat(results, Matchers.contains(CoreMatchers.instanceOf(CommandStatusEntity.class), CoreMatchers.instanceOf(CommandStatusEntity.class), CoreMatchers.instanceOf(CommandStatusEntity.class)));
        KsqlResourceFunctionalTest.assertSuccessful(results);
    }
}

