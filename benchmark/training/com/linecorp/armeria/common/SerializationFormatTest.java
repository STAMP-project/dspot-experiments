/**
 * Copyright 2015 LINE Corporation
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
package com.linecorp.armeria.common;


import org.junit.Test;

import static SerializationFormat.UNKNOWN;


public class SerializationFormatTest {
    @Test
    public void findByMediaType_exactMatch() {
        for (SerializationFormat format : SerializationFormat.values()) {
            if (format == (UNKNOWN)) {
                continue;
            }
            assertThat(SerializationFormat.find(format.mediaType()).get()).isSameAs(format);
        }
    }

    @Test
    public void findByMediaType_notRecognized() {
        assertThat(SerializationFormat.find(MediaType.parse("foo/bar"))).isEmpty();
    }

    @Test
    @SuppressWarnings("deprecation")
    public void nullThriftSerializationFormats() {
        SerializationFormatTest.assumeNoThriftInClasspath();
        assertThat(SerializationFormat.THRIFT_BINARY).isNull();
        assertThat(SerializationFormat.THRIFT_COMPACT).isNull();
        assertThat(SerializationFormat.THRIFT_JSON).isNull();
        assertThat(SerializationFormat.THRIFT_TEXT).isNull();
    }

    @Test
    @SuppressWarnings("deprecation")
    public void failingOfThrift() {
        SerializationFormatTest.assumeNoThriftInClasspath();
        assertThatThrownBy(SerializationFormat::ofThrift).isInstanceOf(IllegalStateException.class);
    }
}

