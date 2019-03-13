/**
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.spanner.spi.v1;


import Metadata.ASCII_STRING_MARSHALLER;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class SpannerMetadataProviderTest {
    @Test
    public void testGetHeadersAsMetadata() {
        Map<String, String> headers = ImmutableMap.of("header1", "value1", "header2", "value2");
        SpannerMetadataProvider metadataProvider = SpannerMetadataProvider.create(headers, "header3");
        Metadata metadata = metadataProvider.newMetadata(null, "stuff");
        Assert.assertEquals(((headers.size()) + 1), metadata.keys().size());
        Assert.assertEquals(headers.get("header1"), metadata.get(Key.of("header1", ASCII_STRING_MARSHALLER)));
        Assert.assertEquals(headers.get("header2"), metadata.get(Key.of("header2", ASCII_STRING_MARSHALLER)));
        Assert.assertEquals("stuff", metadata.get(Key.of("header3", ASCII_STRING_MARSHALLER)));
    }

    @Test
    public void testGetResourceHeaderValue() {
        SpannerMetadataProvider metadataProvider = SpannerMetadataProvider.create(ImmutableMap.<String, String>of(), "header3");
        Assert.assertEquals("projects/p", getResourceHeaderValue(metadataProvider, "garbage"));
        Assert.assertEquals("projects/p", getResourceHeaderValue(metadataProvider, "projects/p"));
        Assert.assertEquals("projects/p/instances/i", getResourceHeaderValue(metadataProvider, "projects/p/instances/i"));
        Assert.assertEquals("projects/p/instances/i/databases/d", getResourceHeaderValue(metadataProvider, "projects/p/instances/i/databases/d"));
        Assert.assertEquals("projects/p/instances/i/databases/d", getResourceHeaderValue(metadataProvider, "projects/p/instances/i/databases/d/sessions/s"));
        Assert.assertEquals("projects/p/instances/i", getResourceHeaderValue(metadataProvider, "projects/p/instances/i/operations/op"));
        Assert.assertEquals("projects/p/instances/i/databases/d", getResourceHeaderValue(metadataProvider, "projects/p/instances/i/databases/d/operations/op"));
        Assert.assertEquals("projects/p/instances/i", getResourceHeaderValue(metadataProvider, "projects/p/instances/i/operations"));
        Assert.assertEquals("projects/p/instances/i/databases/d", getResourceHeaderValue(metadataProvider, "projects/p/instances/i/databases/d/operations"));
    }

    @Test
    public void testNewExtraHeaders() {
        SpannerMetadataProvider metadataProvider = SpannerMetadataProvider.create(ImmutableMap.<String, String>of(), "header1");
        Map<String, List<String>> extraHeaders = metadataProvider.newExtraHeaders(null, "value1");
        assertThat(extraHeaders).containsExactlyEntriesIn(ImmutableMap.<String, List<String>>of("header1", ImmutableList.<String>of("value1")));
    }
}

