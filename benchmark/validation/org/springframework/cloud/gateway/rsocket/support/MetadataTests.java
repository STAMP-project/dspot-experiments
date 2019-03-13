/**
 * Copyright 2018-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.gateway.rsocket.support;


import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


public class MetadataTests {
    @Test
    public void encodeAndDecodeJustName() {
        ByteBuf byteBuf = Metadata.from("test").encode();
        assertMetadata(byteBuf, "test");
    }

    @Test
    public void encodeAndDecodeWorks() {
        ByteBuf byteBuf = Metadata.from("test1").with("key1111", "val111111").with("key22", "val222").encode();
        Metadata metadata = assertMetadata(byteBuf, "test1");
        Map<String, String> properties = metadata.getProperties();
        assertThat(properties).hasSize(2).containsOnlyKeys("key1111", "key22").containsValues("val111111", "val222");
    }

    @Test
    public void nullMetadataDoesNotMatch() {
        assertThat(Metadata.matches(null, new HashMap())).isFalse();
        assertThat(Metadata.matches(new HashMap(), null)).isFalse();
    }

    @Test
    public void metadataSubsetMatches() {
        assertThat(Metadata.matches(metadata(2), metadata(3))).isTrue();
    }

    @Test
    public void metadataEqualSetMatches() {
        assertThat(Metadata.matches(metadata(3), metadata(3))).isTrue();
    }

    @Test
    public void metadataSuperSetDoesNotMatch() {
        assertThat(Metadata.matches(metadata(3), metadata(2))).isFalse();
    }
}

