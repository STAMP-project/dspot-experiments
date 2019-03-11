/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.webserver;


import io.helidon.common.http.Parameters;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.jupiter.api.Test;


/**
 * The UriComponentTest.
 */
public class UriComponentTest {
    @Test
    public void noDecode() throws Exception {
        Parameters parameters = UriComponent.decodeQuery(("a=" + (URLEncoder.encode("1&b=2", StandardCharsets.US_ASCII.name()))), false);
        MatcherAssert.assertThat(parameters.first("a").get(), Is.is(URLEncoder.encode("1&b=2", StandardCharsets.US_ASCII.name())));
    }

    @Test
    public void yesDecode() throws Exception {
        Parameters parameters = UriComponent.decodeQuery(("a=" + (URLEncoder.encode("1&b=2", StandardCharsets.US_ASCII.name()))), true);
        MatcherAssert.assertThat(parameters.first("a").get(), Is.is("1&b=2"));
    }

    @Test
    public void testNonExistingParam() throws Exception {
        Parameters parameters = UriComponent.decodeQuery("a=b", true);
        MatcherAssert.assertThat(parameters.first("c"), Is.is(Optional.empty()));
    }

    @Test
    public void sanityParse() throws Exception {
        Parameters parameters = UriComponent.decodeQuery(URI.create("http://foo/bar?a=b&c=d&a=e").getRawQuery(), true);
        MatcherAssert.assertThat(parameters.all("a"), IsCollectionContaining.hasItems(Is.is("b"), Is.is("e")));
        MatcherAssert.assertThat(parameters.all("c"), IsCollectionContaining.hasItems(Is.is("d")));
        MatcherAssert.assertThat(parameters.all("z"), hasSize(0));
    }
}

