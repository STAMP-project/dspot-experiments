/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.core.qualifier;


import org.junit.Test;


public class TestQualifiedType {
    @Test
    public void testQualifiedType() {
        assertThat(QualifiedType.of(String.class).with(NVarchar.class)).isEqualTo(QualifiedType.of(String.class).with(NVarchar.class)).hasSameHashCodeAs(QualifiedType.of(String.class).with(NVarchar.class)).hasToString("@org.jdbi.v3.core.qualifier.NVarchar() java.lang.String");
        assertThat(QualifiedType.of(int.class)).isEqualTo(QualifiedType.of(int.class)).hasSameHashCodeAs(QualifiedType.of(int.class)).hasToString("int");
        assertThat(QualifiedType.of(new org.jdbi.v3.core.generic.GenericType<java.util.List<String>>() {})).isEqualTo(QualifiedType.of(new org.jdbi.v3.core.generic.GenericType<java.util.List<String>>() {})).hasSameHashCodeAs(QualifiedType.of(new org.jdbi.v3.core.generic.GenericType<java.util.List<String>>() {})).hasToString("java.util.List<java.lang.String>");
        assertThat(QualifiedType.of(String.class).with(SampleQualifiers.foo(1), SampleQualifiers.bar("1"))).isEqualTo(QualifiedType.of(String.class).with(SampleQualifiers.foo(1), SampleQualifiers.bar("1"))).isEqualTo(QualifiedType.of(String.class).with(SampleQualifiers.bar("1"), SampleQualifiers.foo(1))).hasSameHashCodeAs(QualifiedType.of(String.class).with(SampleQualifiers.foo(1), SampleQualifiers.bar("1"))).hasSameHashCodeAs(QualifiedType.of(String.class).with(SampleQualifiers.bar("1"), SampleQualifiers.foo(1))).isNotEqualTo(QualifiedType.of(int.class).with(SampleQualifiers.bar("1"), SampleQualifiers.foo(1))).isNotEqualTo(QualifiedType.of(String.class).with(SampleQualifiers.bar("2"), SampleQualifiers.foo(1))).isNotEqualTo(QualifiedType.of(String.class).with(SampleQualifiers.bar("1"), SampleQualifiers.foo(2))).isNotEqualTo(QualifiedType.of(String.class).with(SampleQualifiers.foo(1))).isNotEqualTo(QualifiedType.of(String.class).with(SampleQualifiers.bar("1")));
    }
}

