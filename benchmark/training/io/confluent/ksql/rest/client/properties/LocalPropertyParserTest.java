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
package io.confluent.ksql.rest.client.properties;


import KsqlConfig.KSQL_SERVICE_ID_CONFIG;
import KsqlConstants.LEGACY_RUN_SCRIPT_STATEMENTS_CONTENT;
import ProducerConfig.LINGER_MS_CONFIG;
import io.confluent.ksql.config.ConfigItem;
import io.confluent.ksql.config.ConfigResolver;
import io.confluent.ksql.config.PropertyValidator;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class LocalPropertyParserTest {
    private static final Object PARSED_VALUE = new Object();

    private static final String PARSED_PROP_NAME = "PARSED";

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private PropertyValidator validator;

    @Mock
    private ConfigResolver resolver;

    @Mock
    private ConfigItem configItem;

    private LocalPropertyParser parser;

    @Test
    public void shouldNotCallResolverForRunScriptConstant() {
        // When:
        parser.parse(LEGACY_RUN_SCRIPT_STATEMENTS_CONTENT, "100");
        // Then:
        Mockito.verify(resolver, Mockito.never()).resolve(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void shouldCallValidatorForRunScriptConstant() {
        // When:
        parser.parse(LEGACY_RUN_SCRIPT_STATEMENTS_CONTENT, "something2");
        // Then:
        Mockito.verify(validator).validate(LEGACY_RUN_SCRIPT_STATEMENTS_CONTENT, "something2");
    }

    @Test
    public void shouldCallResolverForOtherProperties() {
        // When:
        parser.parse(KSQL_SERVICE_ID_CONFIG, "100");
        // Then:
        Mockito.verify(resolver).resolve(KSQL_SERVICE_ID_CONFIG, true);
    }

    @Test
    public void shouldThrowIfResolverFailsToResolve() {
        // Given:
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Not recognizable as ksql, streams, consumer, or producer property: 'Unknown'");
        Mockito.when(resolver.resolve(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(Optional.empty());
        // When:
        parser.parse("Unknown", "100");
    }

    @Test
    public void shouldCallValidatorWithParsedValue() {
        // When:
        parser.parse(LINGER_MS_CONFIG, "100");
        // Then:
        Mockito.verify(validator).validate(LocalPropertyParserTest.PARSED_PROP_NAME, LocalPropertyParserTest.PARSED_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfValidatorThrows() {
        // Given:
        Mockito.doThrow(new IllegalArgumentException("Boom")).when(validator).validate(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object.class));
        // When:
        parser.parse(LINGER_MS_CONFIG, "100");
    }
}

