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
package io.confluent.ksql.cli.console.table.builder;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.cli.console.Console;
import io.confluent.ksql.cli.console.table.Table;
import io.confluent.ksql.rest.entity.PropertiesList;
import io.confluent.ksql.util.KsqlConfig;
import java.util.Collections;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class PropertiesListTableBuilderTest {
    @Mock
    private Console console;

    @Captor
    private ArgumentCaptor<List<List<String>>> rowsCaptor;

    private PropertiesListTableBuilder builder;

    private static final String SOME_KEY = (KsqlConfig.KSQL_STREAMS_PREFIX) + (ConsumerConfig.AUTO_OFFSET_RESET_CONFIG);

    @Test
    public void shouldHandleClientOverwrittenProperties() {
        // Given:
        final PropertiesList propList = new PropertiesList("list properties;", ImmutableMap.of(PropertiesListTableBuilderTest.SOME_KEY, "earliest"), ImmutableList.of(PropertiesListTableBuilderTest.SOME_KEY), Collections.emptyList());
        // When:
        final Table table = builder.buildTable(propList);
        // Then:
        MatcherAssert.assertThat(getRows(table), Matchers.contains(PropertiesListTableBuilderTest.row(PropertiesListTableBuilderTest.SOME_KEY, "SESSION", "earliest")));
    }

    @Test
    public void shouldHandleServerOverwrittenProperties() {
        // Given:
        final PropertiesList propList = new PropertiesList("list properties;", ImmutableMap.of(PropertiesListTableBuilderTest.SOME_KEY, "earliest"), Collections.emptyList(), Collections.emptyList());
        // When:
        final Table table = builder.buildTable(propList);
        // Then:
        MatcherAssert.assertThat(getRows(table), Matchers.contains(PropertiesListTableBuilderTest.row(PropertiesListTableBuilderTest.SOME_KEY, "SERVER", "earliest")));
    }

    @Test
    public void shouldHandleDefaultProperties() {
        // Given:
        final PropertiesList propList = new PropertiesList("list properties;", ImmutableMap.of(PropertiesListTableBuilderTest.SOME_KEY, "earliest"), Collections.emptyList(), ImmutableList.of(PropertiesListTableBuilderTest.SOME_KEY));
        // When:
        final Table table = builder.buildTable(propList);
        // Then:
        MatcherAssert.assertThat(getRows(table), Matchers.contains(PropertiesListTableBuilderTest.row(PropertiesListTableBuilderTest.SOME_KEY, "", "earliest")));
    }

    @Test
    public void shouldHandlePropertiesWithNullValue() {
        // Given:
        final PropertiesList propList = new PropertiesList("list properties;", Collections.singletonMap(PropertiesListTableBuilderTest.SOME_KEY, null), Collections.emptyList(), ImmutableList.of(PropertiesListTableBuilderTest.SOME_KEY));
        // When:
        final Table table = builder.buildTable(propList);
        // Then:
        MatcherAssert.assertThat(getRows(table), Matchers.contains(PropertiesListTableBuilderTest.row(PropertiesListTableBuilderTest.SOME_KEY, "", "NULL")));
    }
}

