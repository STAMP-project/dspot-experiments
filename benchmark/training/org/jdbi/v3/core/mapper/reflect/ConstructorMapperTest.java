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
package org.jdbi.v3.core.mapper.reflect;


import java.beans.ConstructorProperties;
import javax.annotation.Nullable;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.mapper.Nested;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.junit.Rule;
import org.junit.Test;


public class ConstructorMapperTest {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule();

    @Test
    public void testSimple() {
        ConstructorMapperTest.ConstructorBean bean = selectOne("SELECT s, i FROM bean", ConstructorMapperTest.ConstructorBean.class);
        assertThat(bean.s).isEqualTo("3");
        assertThat(bean.i).isEqualTo(2);
    }

    @Test
    public void testReversed() {
        ConstructorMapperTest.ConstructorBean bean = selectOne("SELECT i, s FROM bean", ConstructorMapperTest.ConstructorBean.class);
        assertThat(bean.s).isEqualTo("3");
        assertThat(bean.i).isEqualTo(2);
    }

    @Test
    public void testExtra() {
        ConstructorMapperTest.ConstructorBean bean = selectOne("SELECT 1 as ignored, i, s FROM bean", ConstructorMapperTest.ConstructorBean.class);
        assertThat(bean.s).isEqualTo("3");
        assertThat(bean.i).isEqualTo(2);
    }

    static class ConstructorBean {
        private final String s;

        private final int i;

        ConstructorBean(int some, String other, long constructor) {
            throw new UnsupportedOperationException("You don't belong here!");
        }

        @JdbiConstructor
        ConstructorBean(String s, int i) {
            this.s = s;
            this.i = i;
        }
    }

    @Test
    public void testDuplicate() {
        assertThatThrownBy(() -> selectOne("SELECT i, s, s FROM bean", .class)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testMismatch() {
        assertThatThrownBy(() -> selectOne("SELECT i, '7' FROM bean", .class)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testNullableParameterPresent() {
        ConstructorMapperTest.NullableParameterBean bean = selectOne("select s, i from bean", ConstructorMapperTest.NullableParameterBean.class);
        assertThat(bean.s).isEqualTo("3");
        assertThat(bean.i).isEqualTo(2);
    }

    @Test
    public void testNullableParameterAbsent() {
        ConstructorMapperTest.NullableParameterBean bean = selectOne("select i from bean", ConstructorMapperTest.NullableParameterBean.class);
        assertThat(bean.s).isNull();
        assertThat(bean.i).isEqualTo(2);
    }

    @Test
    public void testNonNullableAbsent() {
        assertThatThrownBy(() -> selectOne("select s from bean", .class)).isInstanceOf(IllegalArgumentException.class);
    }

    static class NullableParameterBean {
        private final String s;

        private final int i;

        NullableParameterBean(@Nullable
        String s, int i) {
            this.s = s;
            this.i = i;
        }
    }

    @Test
    public void testName() {
        ConstructorMapperTest.NamedParameterBean nb = selectOne("SELECT 3 AS xyz", ConstructorMapperTest.NamedParameterBean.class);
        assertThat(nb.i).isEqualTo(3);
    }

    static class NamedParameterBean {
        final int i;

        NamedParameterBean(@ColumnName("xyz")
        int i) {
            this.i = i;
        }
    }

    @Test
    public void testConstructorProperties() {
        final ConstructorMapperTest.ConstructorPropertiesBean cpi = dbRule.getSharedHandle().createQuery("SELECT * FROM bean").mapTo(ConstructorMapperTest.ConstructorPropertiesBean.class).findOnly();
        assertThat(cpi.s).isEqualTo("3");
        assertThat(cpi.i).isEqualTo(2);
    }

    static class ConstructorPropertiesBean {
        final String s;

        final int i;

        ConstructorPropertiesBean() {
            this.s = null;
            this.i = 0;
        }

        @ConstructorProperties({ "s", "i" })
        ConstructorPropertiesBean(String x, int y) {
            this.s = x;
            this.i = y;
        }
    }

    @Test
    public void nestedParameters() {
        assertThat(dbRule.getSharedHandle().registerRowMapper(ConstructorMapper.factory(ConstructorMapperTest.NestedBean.class)).select("select s, i from bean").mapTo(ConstructorMapperTest.NestedBean.class).findOnly()).extracting("nested.s", "nested.i").containsExactly("3", 2);
    }

    @Test
    public void nestedParametersStrict() {
        Handle handle = dbRule.getSharedHandle();
        handle.getConfig(ReflectionMappers.class).setStrictMatching(true);
        handle.registerRowMapper(ConstructorMapper.factory(ConstructorMapperTest.NestedBean.class));
        assertThat(dbRule.getSharedHandle().registerRowMapper(ConstructorMapper.factory(ConstructorMapperTest.NestedBean.class)).select("select s, i from bean").mapTo(ConstructorMapperTest.NestedBean.class).findOnly()).extracting("nested.s", "nested.i").containsExactly("3", 2);
        assertThatThrownBy(() -> handle.createQuery("select s, i, 1 as other from bean").mapTo(.class).findOnly()).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("could not match parameters for columns: [other]");
    }

    static class NestedBean {
        final ConstructorMapperTest.ConstructorBean nested;

        NestedBean(@Nested
        ConstructorMapperTest.ConstructorBean nested) {
            this.nested = nested;
        }
    }

    @Test
    public void nullableNestedParameterPresent() {
        ConstructorMapperTest.NullableNestedBean bean = selectOne("select 'a' a, s, i from bean", ConstructorMapperTest.NullableNestedBean.class);
        assertThat(bean.a).isEqualTo("a");
        assertThat(bean.nested).isNotNull();
        assertThat(bean.nested.s).isEqualTo("3");
        assertThat(bean.nested.i).isEqualTo(2);
    }

    @Test
    public void nullableNestedNullableParameterAbsent() {
        ConstructorMapperTest.NullableNestedBean bean = selectOne("select 'a' a, i from bean", ConstructorMapperTest.NullableNestedBean.class);
        assertThat(bean.a).isEqualTo("a");
        assertThat(bean.nested).isNotNull();
        assertThat(bean.nested.s).isNull();
        assertThat(bean.nested.i).isEqualTo(2);
    }

    @Test
    public void allColumnsOfNullableNestedObjectAbsent() {
        ConstructorMapperTest.NullableNestedBean bean = selectOne("select 'a' a from bean", ConstructorMapperTest.NullableNestedBean.class);
        assertThat(bean.a).isEqualTo("a");
        assertThat(bean.nested).isNull();
    }

    @Test
    public void nonNullableColumnOfNestedObjectAbsent() {
        assertThatThrownBy(() -> selectOne("select 'a' a, s from bean", .class)).isInstanceOf(IllegalArgumentException.class);
    }

    static class NullableNestedBean {
        private final String a;

        private final ConstructorMapperTest.NullableParameterBean nested;

        NullableNestedBean(String a, @Nullable
        @Nested
        ConstructorMapperTest.NullableParameterBean nested) {
            this.a = a;
            this.nested = nested;
        }
    }

    @Test
    public void nestedPrefixParameters() {
        ConstructorMapperTest.NestedPrefixBean result = dbRule.getSharedHandle().registerRowMapper(ConstructorMapper.factory(ConstructorMapperTest.NestedPrefixBean.class)).select("select i nested_i, s nested_s from bean").mapTo(ConstructorMapperTest.NestedPrefixBean.class).findOnly();
        assertThat(result.nested.s).isEqualTo("3");
        assertThat(result.nested.i).isEqualTo(2);
    }

    @Test
    public void nestedPrefixParametersStrict() {
        Handle handle = dbRule.getSharedHandle();
        handle.getConfig(ReflectionMappers.class).setStrictMatching(true);
        handle.registerRowMapper(ConstructorMapper.factory(ConstructorMapperTest.NestedPrefixBean.class));
        assertThat(handle.createQuery("select i nested_i, s nested_s from bean").mapTo(ConstructorMapperTest.NestedPrefixBean.class).findOnly()).extracting("nested.s", "nested.i").containsExactly("3", 2);
        assertThatThrownBy(() -> handle.createQuery("select i nested_i, s nested_s, 1 as other from bean").mapTo(.class).findOnly()).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("could not match parameters for columns: [other]");
        assertThatThrownBy(() -> handle.createQuery("select i nested_i, s nested_s, 1 as nested_other from bean").mapTo(.class).findOnly()).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("could not match parameters for columns: [nested_other]");
    }

    static class NestedPrefixBean {
        final ConstructorMapperTest.ConstructorBean nested;

        NestedPrefixBean(@Nested("nested")
        ConstructorMapperTest.ConstructorBean nested) {
            this.nested = nested;
        }
    }

    @Test
    public void testFactoryMethod() {
        ConstructorMapperTest.StaticFactoryMethodBean bean = selectOne("SELECT s, i FROM bean", ConstructorMapperTest.StaticFactoryMethodBean.class);
        assertThat(bean.s).isEqualTo("3");
        assertThat(bean.i).isEqualTo(2);
    }

    @Test
    public void testFactoryMethodReversed() {
        ConstructorMapperTest.StaticFactoryMethodBean bean = selectOne("SELECT i, s FROM bean", ConstructorMapperTest.StaticFactoryMethodBean.class);
        assertThat(bean.s).isEqualTo("3");
        assertThat(bean.i).isEqualTo(2);
    }

    static class StaticFactoryMethodBean {
        private final String s;

        private final int i;

        StaticFactoryMethodBean(String s, int i, int pass) {
            assertThat(pass).isEqualTo(42);// Make sure this constructor is not used directly

            this.s = s;
            this.i = i;
        }

        @JdbiConstructor
        static ConstructorMapperTest.StaticFactoryMethodBean create(String s, int i) {
            return new ConstructorMapperTest.StaticFactoryMethodBean(s, i, 42);
        }
    }

    @Test
    public void testMultipleFactoryMethods() {
        assertThatThrownBy(() -> ConstructorMapper.factory(.class)).isInstanceOf(IllegalArgumentException.class).hasMessageMatching("class .* may have at most one constructor or static factory method annotated @JdbiConstructor");
    }

    static class MultipleStaticFactoryMethodsBean {
        @JdbiConstructor
        static ConstructorMapperTest.MultipleStaticFactoryMethodsBean one(String s) {
            return new ConstructorMapperTest.MultipleStaticFactoryMethodsBean();
        }

        @JdbiConstructor
        static ConstructorMapperTest.MultipleStaticFactoryMethodsBean two(String s) {
            return new ConstructorMapperTest.MultipleStaticFactoryMethodsBean();
        }
    }
}

