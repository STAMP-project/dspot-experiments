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
package io.confluent.ksql.cli;


import Code.FORBIDDEN;
import Code.OK;
import Code.UNAUTHORIZED;
import JaasUtils.JAVA_LOGIN_CONFIG_PARAM;
import RestConfig.AUTHENTICATION_METHOD_CONFIG;
import RestConfig.AUTHENTICATION_REALM_CONFIG;
import RestConfig.AUTHENTICATION_ROLES_CONFIG;
import io.confluent.common.utils.IntegrationTest;
import io.confluent.ksql.test.util.EmbeddedSingleNodeKafkaCluster;
import io.confluent.ksql.test.util.TestKsqlRestApp;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.RuleChain;
import org.junit.rules.TemporaryFolder;


@Category({ IntegrationTest.class })
public class BasicAuthFunctionalTest {
    private static final TemporaryFolder TMP_FOLDER = new TemporaryFolder();

    static {
        BasicAuthFunctionalTest.createTmpFolder();
    }

    private static final String PROPS_JAAS_REALM = "KsqlServer-Props";

    private static final String KSQL_CLUSTER_ID = "ksql-11";

    private static final String USER_WITH_ACCESS = "harry";

    private static final String USER_WITH_ACCESS_PWD = "changeme";

    private static final String USER_NO_ACCESS = "tom";

    private static final String USER_NO_ACCESS_PWD = "changeme";

    private static final String BASIC_PASSWORDS_FILE_CONTENT = ((((((((("# Each line generated using org.eclipse.jetty.util.security.Password\n" + (BasicAuthFunctionalTest.USER_WITH_ACCESS)) + ": ") + (BasicAuthFunctionalTest.USER_WITH_ACCESS_PWD)) + ",") + (BasicAuthFunctionalTest.KSQL_CLUSTER_ID)) + "\n") + (BasicAuthFunctionalTest.USER_NO_ACCESS)) + ": ") + (BasicAuthFunctionalTest.USER_NO_ACCESS_PWD)) + ",ksql-other\n";

    private static final EmbeddedSingleNodeKafkaCluster CLUSTER = EmbeddedSingleNodeKafkaCluster.newBuilder().withAdditionalJaasConfig(BasicAuthFunctionalTest.createJaasConfigContent()).build();

    private static final TestKsqlRestApp REST_APP = TestKsqlRestApp.builder(BasicAuthFunctionalTest.CLUSTER::bootstrapServers).withProperty(AUTHENTICATION_METHOD_CONFIG, "BASIC").withProperty(AUTHENTICATION_REALM_CONFIG, BasicAuthFunctionalTest.PROPS_JAAS_REALM).withProperty(AUTHENTICATION_ROLES_CONFIG, BasicAuthFunctionalTest.KSQL_CLUSTER_ID).withProperty(JAVA_LOGIN_CONFIG_PARAM, BasicAuthFunctionalTest.CLUSTER.getJaasConfigPath()).build();

    @ClassRule
    public static final RuleChain CHAIN = RuleChain.outerRule(BasicAuthFunctionalTest.CLUSTER).around(BasicAuthFunctionalTest.REST_APP);

    @Test
    public void shouldNotBeAbleToUseWsWithNoCreds() throws Exception {
        MatcherAssert.assertThat(makeWsRequest("", ""), Matchers.is(UNAUTHORIZED));
    }

    @Test
    public void shouldNotBeAbleToUseCliWithInvalidPassword() {
        MatcherAssert.assertThat(canMakeCliRequest(BasicAuthFunctionalTest.USER_WITH_ACCESS, "wrong pwd"), Matchers.is(ERROR_CODE_UNAUTHORIZED));
    }

    @Test
    public void shouldNotBeAbleToUseWsWithInvalidPassword() throws Exception {
        MatcherAssert.assertThat(makeWsRequest(BasicAuthFunctionalTest.USER_WITH_ACCESS, "wrong pwd"), Matchers.is(UNAUTHORIZED));
    }

    @Test
    public void shouldNotBeAbleToUseCliWithUnknownUser() {
        MatcherAssert.assertThat(canMakeCliRequest("Unknown-user", "some password"), Matchers.is(ERROR_CODE_UNAUTHORIZED));
    }

    @Test
    public void shouldNotBeAbleToUseWsWithUnknownUser() throws Exception {
        MatcherAssert.assertThat(makeWsRequest("Unknown-user", "some password"), Matchers.is(UNAUTHORIZED));
    }

    @Test
    public void shouldNotBeAbleToUseCliWithValidCredsIfUserHasNoAccessToThisCluster() {
        MatcherAssert.assertThat(canMakeCliRequest(BasicAuthFunctionalTest.USER_NO_ACCESS, BasicAuthFunctionalTest.USER_NO_ACCESS_PWD), Matchers.is(ERROR_CODE_FORBIDDEN));
    }

    @Test
    public void shouldNotBeAbleToUseWsWithValidCredsIfUserHasNoAccessToThisCluster() throws Exception {
        MatcherAssert.assertThat(makeWsRequest(BasicAuthFunctionalTest.USER_NO_ACCESS, BasicAuthFunctionalTest.USER_NO_ACCESS_PWD), Matchers.is(FORBIDDEN));
    }

    @Test
    public void shouldBeAbleToUseCliWithValidCreds() {
        MatcherAssert.assertThat(canMakeCliRequest(BasicAuthFunctionalTest.USER_WITH_ACCESS, BasicAuthFunctionalTest.USER_WITH_ACCESS_PWD), Matchers.is(OK.getCode()));
    }

    @Test
    public void shouldBeAbleToUseWsWithValidCreds() throws Exception {
        MatcherAssert.assertThat(makeWsRequest(BasicAuthFunctionalTest.USER_WITH_ACCESS, BasicAuthFunctionalTest.USER_WITH_ACCESS_PWD), Matchers.is(OK));
    }

    @SuppressWarnings("unused")
    @WebSocket
    public static class WebSocketListener {
        final CountDownLatch latch = new CountDownLatch(1);

        final AtomicReference<Throwable> error = new AtomicReference<>();

        @OnWebSocketConnect
        public void onConnect(final Session session) {
            latch.countDown();
        }

        @OnWebSocketError
        public void onError(final Throwable t) {
            error.set(t);
            latch.countDown();
        }
    }
}

