/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.internal.urls;


import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.Uris;
import org.junit.jupiter.api.Test;


public class Uris_getParameters_Test {
    @Test
    public void should_return_empty_for_empty_query() {
        Assertions.assertThat(Uris.getParameters("")).isEmpty();
    }

    @Test
    public void should_return_empty_for_null_query() {
        Assertions.assertThat(Uris.getParameters(null)).isEmpty();
    }

    @Test
    public void should_accept_parameter_with_no_value() {
        Map<String, List<String>> parameters = Uris.getParameters("foo");
        Assertions.assertThat(parameters).containsKey("foo");
        Assertions.assertThat(parameters.get("foo")).hasSize(1).containsNull();
    }

    @Test
    public void should_accept_parameter_with_value() {
        Map<String, List<String>> parameters = Uris.getParameters("foo=bar");
        Assertions.assertThat(parameters).containsKey("foo");
        Assertions.assertThat(parameters.get("foo")).containsExactly("bar");
    }

    @Test
    public void should_decode_name() {
        Assertions.assertThat(Uris.getParameters("foo%3Dbar=baz")).containsKey("foo=bar");
    }

    @Test
    public void should_decode_value() {
        Assertions.assertThat(Uris.getParameters("foo=bar%3Dbaz").get("foo")).contains("bar=baz");
    }

    @Test
    public void should_accept_duplicate_names() {
        Map<String, List<String>> parameters = Uris.getParameters("foo&foo=bar");
        Assertions.assertThat(parameters).containsKey("foo");
        Assertions.assertThat(parameters.get("foo")).containsOnly(null, "bar");
    }

    @Test
    public void should_accept_duplicate_values() {
        Map<String, List<String>> parameters = Uris.getParameters("foo=bar&foo=bar");
        Assertions.assertThat(parameters).containsKey("foo");
        Assertions.assertThat(parameters.get("foo")).containsExactly("bar", "bar");
    }
}

