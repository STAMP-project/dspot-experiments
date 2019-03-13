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
package org.jdbi.v3.vavr;


import io.vavr.Tuple1;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.Tuple4;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Something;
import org.jdbi.v3.core.mapper.NoSuchMapperException;
import org.jdbi.v3.core.mapper.SomethingMapper;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.junit.Rule;
import org.junit.Test;


public class TestVavrTupleRowMapperFactoryWithDB {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withSomething().withPlugins();

    @Test
    public void testMapTuple1WithNameOnlyUsingColumnMapperShouldSucceed() {
        Tuple1<String> result = dbRule.getSharedHandle().createQuery("select name from something where id = 1").mapTo(new org.jdbi.v3.core.generic.GenericType<Tuple1<String>>() {}).findOnly();
        assertThat(result._1).isEqualToIgnoringCase("eric");
    }

    @Test
    public void testMapTuple1UsingRegisteredRowMapperShouldSucceed() {
        Handle handle = dbRule.getSharedHandle();
        handle.registerRowMapper(new SomethingMapper());
        Tuple1<Something> result = handle.createQuery("select id, name from something where id = 1").mapTo(new org.jdbi.v3.core.generic.GenericType<Tuple1<Something>>() {}).findOnly();
        assertThat(result._1).isEqualTo(new Something(1, "eric"));
    }

    @Test
    public void testMapTuple2UsingRegisteredRowMappersShouldSucceed() {
        Handle handle = dbRule.getSharedHandle();
        handle.registerRowMapper(new SomethingMapper());
        handle.registerRowMapper(TestVavrTupleRowMapperFactoryWithDB.SomethingValues.class, ( rs, ctx) -> new org.jdbi.v3.vavr.SomethingValues(rs.getInt("integerValue"), rs.getInt("intValue")));
        Tuple2<Something, TestVavrTupleRowMapperFactoryWithDB.SomethingValues> result = handle.createQuery("select * from something where id = 2").mapTo(new org.jdbi.v3.core.generic.GenericType<Tuple2<Something, TestVavrTupleRowMapperFactoryWithDB.SomethingValues>>() {}).findOnly();
        assertThat(result._1).isEqualTo(new Something(2, "brian"));
        assertThat(result._2).isEqualTo(new TestVavrTupleRowMapperFactoryWithDB.SomethingValues(101, 102));
    }

    @Test
    public void testMapTuple2HavingOnlyOneRowMapperShouldFail() {
        final Handle handle = dbRule.getSharedHandle();
        handle.registerRowMapper(new SomethingMapper());
        assertThatThrownBy(() -> handle.createQuery("select * from something where id = 1").mapTo(new GenericType<Tuple2<Something, org.jdbi.v3.vavr.SomethingValues>>() {}).findOnly()).isInstanceOf(NoSuchMapperException.class).hasMessageContaining("SomethingValues");
    }

    @Test
    public void testMapTuple3WithExtraSpecifiedColumnShouldSucceed() {
        Handle handle = dbRule.getSharedHandle();
        handle.registerRowMapper(new SomethingMapper());
        handle.configure(TupleMappers.class, ( c) -> c.setColumn(2, "integerValue").setColumn(3, "intValue"));
        Tuple3<Something, Integer, Integer> result = handle.createQuery("select * from something where id = 1").mapTo(new org.jdbi.v3.core.generic.GenericType<Tuple3<Something, Integer, Integer>>() {}).findOnly();
        assertThat(result._1).isEqualTo(new Something(1, "eric"));
        assertThat(result._2).isEqualTo(99);
        assertThat(result._3).isEqualTo(100);
    }

    @Test
    public void testMapTuple3WithAllSpecifiedColumnsShouldRespectConfiguration() {
        Handle handle = dbRule.getSharedHandle();
        handle.configure(TupleMappers.class, ( c) -> c.setColumn(1, "integerValue").setColumn(2, "intValue").setColumn(3, "id"));
        Tuple3<Integer, Integer, Integer> result = handle.createQuery("select * from something where id = 1").mapTo(new org.jdbi.v3.core.generic.GenericType<Tuple3<Integer, Integer, Integer>>() {}).findOnly();
        assertThat(result._1).isEqualTo(99);
        assertThat(result._2).isEqualTo(100);
        assertThat(result._3).isEqualTo(1);
    }

    @Test
    public void testMapTuple3WithoutSpecifiedColumnShouldFail() {
        Handle handle = dbRule.getSharedHandle();
        handle.registerRowMapper(new SomethingMapper());
        assertThatThrownBy(() -> handle.createQuery("select * from something where id = 1").mapTo(new GenericType<Tuple3<Integer, Something, Integer>>() {}).findOnly()).isInstanceOf(NoSuchMapperException.class).hasMessageContaining("TupleMappers config class");
    }

    @Test
    public void testMapTuple3WithOnlyOneSpecifiedColumnShouldFail() {
        Handle handle = dbRule.getSharedHandle();
        handle.registerRowMapper(new SomethingMapper());
        handle.configure(TupleMappers.class, ( c) -> c.setColumn(1, "integerValue"));
        assertThatThrownBy(() -> handle.createQuery("select * from something where id = 1").mapTo(new GenericType<Tuple3<Integer, Something, Integer>>() {}).findOnly()).isInstanceOf(NoSuchMapperException.class).isInstanceOf(NoSuchMapperException.class).hasMessageContaining("TupleMappers config class");
    }

    @Test
    public void testMapTuple2SelectedColumnsShouldSucceed() {
        Tuple2<Integer, String> result = dbRule.getSharedHandle().createQuery("select intValue, name from something where id = 2").mapTo(new org.jdbi.v3.core.generic.GenericType<Tuple2<Integer, String>>() {}).findOnly();
        assertThat(result._1).isEqualTo(102);
        assertThat(result._2).isEqualTo("brian");
    }

    @Test
    public void testMapTuple4AllColumnsShouldSucceed() {
        Tuple4<Integer, String, Integer, Integer> result = dbRule.getSharedHandle().createQuery("select * from something where id = 2").mapTo(new org.jdbi.v3.core.generic.GenericType<Tuple4<Integer, String, Integer, Integer>>() {}).findOnly();
        assertThat(result._1).isEqualTo(2);
        assertThat(result._2).isEqualTo("brian");
        assertThat(result._3).isEqualTo(101);
        assertThat(result._4).isEqualTo(102);
    }

    private static class SomethingValues {
        private int integerValue;

        private int intValue;

        SomethingValues(int integerValue, int intValue) {
            this.integerValue = integerValue;
            this.intValue = intValue;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            TestVavrTupleRowMapperFactoryWithDB.SomethingValues that = ((TestVavrTupleRowMapperFactoryWithDB.SomethingValues) (o));
            if ((integerValue) != (that.integerValue)) {
                return false;
            }
            return (intValue) == (that.intValue);
        }

        @Override
        public int hashCode() {
            int result = integerValue;
            result = (31 * result) + (intValue);
            return result;
        }
    }
}

