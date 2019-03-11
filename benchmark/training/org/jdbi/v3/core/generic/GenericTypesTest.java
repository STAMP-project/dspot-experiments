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
package org.jdbi.v3.core.generic;


import java.lang.reflect.Type;
import java.util.Optional;
import org.junit.Test;


public class GenericTypesTest {
    @Test
    public void getErasedTypeOfRaw() throws NoSuchMethodException {
        assertThat(GenericTypes.getErasedType(GenericTypesTest.methodReturnType(GenericTypesTest.Foo.class, "raw"))).isEqualTo(GenericTypesTest.Foo.class);
    }

    @Test
    public void findGenericParameterOfRaw() throws NoSuchMethodException {
        assertThat(GenericTypes.findGenericParameter(GenericTypesTest.methodReturnType(GenericTypesTest.Foo.class, "raw"), GenericTypesTest.Foo.class)).isEqualTo(Optional.empty());
    }

    @Test
    public void getErasedTypeOfGeneric() throws NoSuchMethodException {
        assertThat(GenericTypes.getErasedType(GenericTypesTest.methodReturnType(GenericTypesTest.Foo.class, "generic"))).isEqualTo(GenericTypesTest.Foo.class);
    }

    @Test
    public void findGenericParameterOfGeneric() throws NoSuchMethodException {
        assertThat(GenericTypes.findGenericParameter(GenericTypesTest.methodReturnType(GenericTypesTest.Foo.class, "generic"), GenericTypesTest.Foo.class)).contains(String.class);
    }

    @Test
    public void getErasedTypeOfNestedGeneric() throws NoSuchMethodException {
        assertThat(GenericTypes.getErasedType(GenericTypesTest.methodReturnType(GenericTypesTest.Foo.class, "nestedGeneric"))).isEqualTo(GenericTypesTest.Foo.class);
    }

    @Test
    public void findGenericParameterOfNestedGeneric() throws NoSuchMethodException {
        assertThat(GenericTypes.findGenericParameter(GenericTypesTest.methodReturnType(GenericTypesTest.Foo.class, "nestedGeneric"), GenericTypesTest.Foo.class)).contains(GenericTypesTest.methodReturnType(GenericTypesTest.Foo.class, "generic"));
    }

    @Test
    public void findGenericParameterOfSuperClass() throws NoSuchMethodException {
        assertThat(GenericTypes.findGenericParameter(GenericTypesTest.methodReturnType(GenericTypesTest.Bar.class, "subTypeGeneric"), GenericTypesTest.Foo.class)).isEqualTo(Optional.of(Integer.class));
    }

    @Test
    public void findGenericParameterOfAncestorClass() throws NoSuchMethodException {
        assertThat(GenericTypes.findGenericParameter(GenericTypesTest.methodReturnType(GenericTypesTest.Baz.class, "descendentTypeGeneric"), GenericTypesTest.Foo.class)).contains(String.class);
    }

    @Test
    public void findMultipleGenericParameters() throws NoSuchMethodException {
        assertThat(GenericTypes.findGenericParameter(GenericTypesTest.methodReturnType(GenericTypesTest.Xyz.class, "sample"), GenericTypesTest.Xyz.class, 0)).contains(String.class);
        assertThat(GenericTypes.findGenericParameter(GenericTypesTest.methodReturnType(GenericTypesTest.Xyz.class, "sample"), GenericTypesTest.Xyz.class, 1)).contains(Integer.class);
        assertThat(GenericTypes.findGenericParameter(GenericTypesTest.methodReturnType(GenericTypesTest.Xyz.class, "sample"), GenericTypesTest.Xyz.class, 2)).contains(Void.class);
    }

    @Test
    public void resolveType() throws NoSuchMethodException {
        abstract class A<T> {
            abstract T a();
        }
        abstract class B extends A<String> {}
        assertThat(GenericTypes.resolveType(A.class.getDeclaredMethod("a").getGenericReturnType(), B.class)).isEqualTo(String.class);
    }

    @Test
    public void resolveTypeUnrelatedContext() throws NoSuchMethodException {
        abstract class A1<T> {
            abstract T a();
        }
        abstract class A2<T> {
            abstract T a();
        }
        abstract class B extends A2<String> {}
        Type t = A1.class.getDeclaredMethod("a").getGenericReturnType();
        assertThat(GenericTypes.resolveType(t, B.class)).isEqualTo(t);
    }

    private static class Foo<T> {
        private static GenericTypesTest.Foo raw() {
            return null;
        }

        private static GenericTypesTest.Foo<String> generic() {
            return null;
        }

        private static GenericTypesTest.Foo<GenericTypesTest.Foo<String>> nestedGeneric() {
            return null;
        }
    }

    private static class Bar<T> extends GenericTypesTest.Foo<T> {
        private static GenericTypesTest.Bar<Integer> subTypeGeneric() {
            return null;
        }
    }

    private static class Baz<T> extends GenericTypesTest.Bar<T> {
        private static GenericTypesTest.Baz<String> descendentTypeGeneric() {
            return null;
        }
    }

    private static class Xyz<X, Y, Z> {
        private static GenericTypesTest.Xyz<String, Integer, Void> sample() {
            return null;
        }
    }
}

