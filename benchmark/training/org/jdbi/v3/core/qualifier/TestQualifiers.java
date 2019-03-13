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


import java.lang.reflect.Parameter;
import org.junit.Test;


public class TestQualifiers {
    @Test
    public void getQualifiers() throws Exception {
        assertThat(SampleQualifiers.foo(1)).isEqualTo(SampleQualifiers.foo(1)).isNotEqualTo(SampleQualifiers.foo(2));
        assertThat(Qualifiers.getQualifiers(getClass().getDeclaredField("qualifiedField"))).containsExactly(SampleQualifiers.foo(1));
        assertThat(Qualifiers.getQualifiers(getClass().getDeclaredMethod("qualifiedMethod"))).containsExactly(SampleQualifiers.foo(2));
        assertThat(Qualifiers.getQualifiers(getClass().getDeclaredMethod("qualifiedParameter", String.class).getParameters()[0])).containsExactly(SampleQualifiers.foo(3));
        assertThat(Qualifiers.getQualifiers(TestQualifiers.QualifiedClass.class)).containsExactly(SampleQualifiers.foo(4));
        assertThat(Qualifiers.getQualifiers(TestQualifiers.QualifiedClass.class.getDeclaredConstructor(String.class).getParameters()[0])).containsExactly(SampleQualifiers.foo(5));
        assertThat(Qualifiers.getQualifiers(getClass().getDeclaredField("twoQualifiers"))).containsExactlyInAnyOrder(SampleQualifiers.foo(6), SampleQualifiers.bar("six"));
    }

    @SampleQualifiers.Foo(1)
    private String qualifiedField;

    @SampleQualifiers.Foo(4)
    private static class QualifiedClass {
        QualifiedClass(@SampleQualifiers.Foo(5)
        String param) {
        }
    }

    @SampleQualifiers.Foo(6)
    @SampleQualifiers.Bar("six")
    private String twoQualifiers;
}

