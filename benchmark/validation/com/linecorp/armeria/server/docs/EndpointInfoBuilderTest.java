/**
 * Copyright 2018 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.server.docs;


import MediaType.JSON_PATCH;
import MediaType.JSON_UTF_8;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;


public class EndpointInfoBuilderTest {
    @Test
    public void testBuild() {
        final EndpointInfoBuilder endpointInfoBuilder = new EndpointInfoBuilder("*", "/foo");
        final EndpointInfo endpointInfo = endpointInfoBuilder.availableMimeTypes(JSON_UTF_8).build();
        assertThat(endpointInfo).isEqualTo(new EndpointInfo("*", "/foo", "", "", null, ImmutableSet.of(JSON_UTF_8)));
    }

    @Test
    public void shouldHaveAtLeastOneMimeType() {
        final EndpointInfoBuilder endpointInfoBuilder = new EndpointInfoBuilder("*", "/foo");
        assertThatThrownBy(endpointInfoBuilder::build).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    public void cannotSetBothPrefixAndFragment() {
        final EndpointInfoBuilder endpointInfoBuilder = new EndpointInfoBuilder("*", "/foo");
        endpointInfoBuilder.regexPathPrefix("/prefix/");
        assertThatThrownBy(() -> endpointInfoBuilder.fragment("/fragment")).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    public void defaultTypeIsAddedToAvailableTypes() {
        EndpointInfoBuilder endpointInfoBuilder = new EndpointInfoBuilder("*", "/foo");
        // Add the defaultMiMeType first.
        endpointInfoBuilder.defaultMimeType(JSON_UTF_8);
        endpointInfoBuilder.availableMimeTypes(JSON_PATCH);
        EndpointInfo endpointInfo = endpointInfoBuilder.build();
        assertThat(endpointInfo.availableMimeTypes()).containsExactlyInAnyOrder(JSON_UTF_8, JSON_PATCH);
        endpointInfoBuilder = new EndpointInfoBuilder("*", "/foo");
        // Add the availableMimeTypes first.
        endpointInfoBuilder.availableMimeTypes(JSON_PATCH);
        endpointInfoBuilder.defaultMimeType(JSON_UTF_8);
        endpointInfo = endpointInfoBuilder.build();
        assertThat(endpointInfo.availableMimeTypes()).containsExactlyInAnyOrder(JSON_UTF_8, JSON_PATCH);
    }
}

