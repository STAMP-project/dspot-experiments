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
package io.confluent.ksql.cli.console;


import OutputFormat.JSON;
import OutputFormat.TABULAR;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class OutputFormatTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldReturnAllValues() {
        MatcherAssert.assertThat(OutputFormat.VALID_FORMATS, Matchers.is("'JSON', 'TABULAR'"));
    }

    @Test
    public void shouldResolve() {
        MatcherAssert.assertThat(OutputFormat.valueOf("JSON"), Matchers.is(JSON));
        MatcherAssert.assertThat(OutputFormat.valueOf("TABULAR"), Matchers.is(TABULAR));
    }

    @Test
    public void shouldThrowOnUnknownFormat() {
        // Expect:
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage((("Unknown Output format: This-is-unknown. " + "Valid values are: ") + (OutputFormat.VALID_FORMATS)));
        // When:
        OutputFormat.get("This-is-unknown");
    }
}

