/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.cli.converters;


import ConverterHint.DISABLE_ENUM_CONVERTER;
import ConverterHint.INDEX_TYPE;
import IndexType.FUNCTIONAL;
import IndexType.HASH;
import IndexType.PRIMARY_KEY;
import org.apache.geode.cache.query.IndexType;
import org.junit.Test;


public class IndexTypeConverterTest {
    IndexTypeConverter typeConverter;

    EnumConverter enumConverter;

    @Test
    public void supports() throws Exception {
        assertThat(typeConverter.supports(IndexType.class, INDEX_TYPE)).isTrue();
        assertThat(typeConverter.supports(Enum.class, INDEX_TYPE)).isFalse();
        assertThat(typeConverter.supports(IndexType.class, "")).isFalse();
        assertThat(enumConverter.supports(IndexType.class, "")).isTrue();
        assertThat(enumConverter.supports(Enum.class, "")).isTrue();
        assertThat(enumConverter.supports(IndexType.class, INDEX_TYPE)).isFalse();
        assertThat(enumConverter.supports(Enum.class, DISABLE_ENUM_CONVERTER)).isFalse();
    }

    @Test
    public void convert() throws Exception {
        assertThat(typeConverter.convertFromText("hash", IndexType.class, "")).isEqualTo(HASH);
        assertThat(typeConverter.convertFromText("range", IndexType.class, "")).isEqualTo(FUNCTIONAL);
        assertThat(typeConverter.convertFromText("key", IndexType.class, "")).isEqualTo(PRIMARY_KEY);
        assertThatThrownBy(() -> typeConverter.convertFromText("invalid", .class, "")).isInstanceOf(IllegalArgumentException.class);
    }
}

