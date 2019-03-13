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


import ClassName.EMPTY;
import org.apache.geode.management.internal.cli.domain.ClassName;
import org.junit.Test;


public class ClassNameConverterTest {
    private ClassNameConverter converter;

    @Test
    public void convertClassOnly() {
        ClassName declarable = converter.convertFromText("abc", ClassName.class, "");
        assertThat(declarable.getClassName()).isEqualTo("abc");
        assertThat(declarable.getInitProperties()).isEmpty();
    }

    @Test
    public void convertClassAndEmptyProp() {
        ClassName declarable = converter.convertFromText("abc{}", ClassName.class, "");
        assertThat(declarable.getClassName()).isEqualTo("abc");
        assertThat(declarable.getInitProperties()).isEmpty();
    }

    @Test
    public void convertWithOnlyDelimiter() {
        assertThat(converter.convertFromText("{", ClassName.class, "")).isEqualTo(EMPTY);
    }

    @Test
    public void convertWithInvalidClassName() {
        assertThatThrownBy(() -> converter.convertFromText("abc?{}", .class, "")).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Invalid className");
    }

    @Test
    public void convertWithEmptyString() {
        ClassName className = converter.convertFromText("", ClassName.class, "");
        assertThat(className).isEqualTo(EMPTY);
    }

    @Test
    public void convertClassAndProperties() {
        String json = "{'k1':'v1','k2':'v2'}";
        ClassName declarable = converter.convertFromText(("abc" + json), ClassName.class, "");
        assertThat(declarable.getClassName()).isEqualTo("abc");
        assertThat(declarable.getInitProperties()).containsOnlyKeys("k1", "k2").containsEntry("k1", "v1").containsEntry("k2", "v2");
    }

    @Test
    public void convertClassAndPropertiesWithDoubleQuotes() {
        String json = "{\"k1\":\"v1\",\"k2\":\"v2\"}";
        ClassName declarable = converter.convertFromText(("abc" + json), ClassName.class, "");
        assertThat(declarable.getClassName()).isEqualTo("abc");
        assertThat(declarable.getInitProperties()).containsOnlyKeys("k1", "k2").containsEntry("k1", "v1").containsEntry("k2", "v2");
    }
}

