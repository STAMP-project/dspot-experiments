/**
 * Copyright 2019 Confluent Inc.
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
package io.confluent.ksql;


import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.Ksql.CliBuilder;
import io.confluent.ksql.Ksql.KsqlClientBuilder;
import io.confluent.ksql.cli.Cli;
import io.confluent.ksql.cli.Options;
import io.confluent.ksql.rest.client.KsqlRestClient;
import java.util.Properties;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class KsqlTest {
    @ClassRule
    public static final TemporaryFolder TMP = new TemporaryFolder();

    @Mock
    private Options options;

    @Mock
    private KsqlClientBuilder clientBuilder;

    @Mock
    private KsqlRestClient client;

    @Mock
    private CliBuilder cliBuilder;

    @Mock
    private Cli cli;

    private Properties systemProps;

    private Ksql ksql;

    @Test
    public void shouldBuildClientWithCorrectServerAddress() {
        // Given:
        Mockito.when(options.getServer()).thenReturn("in a galaxy far far away");
        // When:
        ksql.run();
        // Then:
        Mockito.verify(clientBuilder).build(ArgumentMatchers.eq("in a galaxy far far away"), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void shouldSupportSslConfigInConfigFile() throws Exception {
        // Given:
        givenConfigFile((("ssl.truststore.location=some/path" + (System.lineSeparator())) + "ssl.truststore.password=letmein"));
        // When:
        ksql.run();
        // Then:
        Mockito.verify(clientBuilder).build(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq(ImmutableMap.of("ssl.truststore.location", "some/path", "ssl.truststore.password", "letmein")));
    }

    @Test
    public void shouldUseSslConfigInSystemConfigInPreferenceToAnyInConfigFile() throws Exception {
        // Given:
        givenConfigFile((("ssl.truststore.location=should not use" + (System.lineSeparator())) + "ssl.truststore.password=should not use"));
        givenSystemProperties("ssl.truststore.location", "some/path", "ssl.truststore.password", "letmein");
        // When:
        ksql.run();
        // Then:
        Mockito.verify(clientBuilder).build(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq(ImmutableMap.of("ssl.truststore.location", "some/path", "ssl.truststore.password", "letmein")));
    }

    @Test
    public void shouldStripSslConfigFromConfigFileWhenMakingLocalProperties() throws Exception {
        // Given:
        givenConfigFile((((("ssl.truststore.location=some/path" + (System.lineSeparator())) + "ssl.truststore.password=letmein") + (System.lineSeparator())) + "some.other.setting=value"));
        // When:
        ksql.run();
        // Then:
        Mockito.verify(clientBuilder).build(ArgumentMatchers.any(), ArgumentMatchers.eq(ImmutableMap.of("some.other.setting", "value")), ArgumentMatchers.any());
    }
}

