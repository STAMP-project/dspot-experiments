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


import io.vavr.Tuple;
import io.vavr.Tuple0;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.Tuple4;
import io.vavr.Tuple5;
import io.vavr.Tuple6;
import io.vavr.Tuple7;
import io.vavr.Tuple8;
import java.sql.SQLException;
import org.junit.Test;


public class TestVavrTupleRowMapperFactory {
    private VavrTupleRowMapperFactory unit;

    @Test
    public void testBuildRowMapperForUntypedTupleShouldFail() {
        assertThat(unit.build(Tuple.class, null)).isEmpty();
    }

    @Test
    public void testBuildRowMapperForTuple0ShouldFail() {
        assertThat(unit.build(Tuple0.class, null)).isEmpty();
    }

    @Test
    public void testBuildRowMapperForTuple1ShouldSucceed() throws SQLException {
        testProjectionMapper(new org.jdbi.v3.core.generic.GenericType<io.vavr.Tuple1<Integer>>() {}, Tuple.of(1));
    }

    @Test
    public void testBuildRowMapperForTuple2ShouldSucceed() throws SQLException {
        testProjectionMapper(new org.jdbi.v3.core.generic.GenericType<Tuple2<Integer, Integer>>() {}, Tuple.of(1, 2));
    }

    @Test
    public void testBuildRowMapperForTuple3ShouldSucceed() throws SQLException {
        testProjectionMapper(new org.jdbi.v3.core.generic.GenericType<Tuple3<Integer, Integer, Integer>>() {}, Tuple.of(1, 2, 3));
    }

    @Test
    public void testBuildRowMapperForTuple4ShouldSucceed() throws SQLException {
        testProjectionMapper(new org.jdbi.v3.core.generic.GenericType<Tuple4<?, ?, ?, ?>>() {}, Tuple.of(1, 2, 3, 4));
    }

    @Test
    public void testBuildRowMapperForTuple5ShouldSucceed() throws SQLException {
        testProjectionMapper(new org.jdbi.v3.core.generic.GenericType<Tuple5<?, ?, ?, ?, ?>>() {}, Tuple.of(1, 2, 3, 4, 5));
    }

    @Test
    public void testBuildRowMapperForTuple6ShouldSucceed() throws SQLException {
        testProjectionMapper(new org.jdbi.v3.core.generic.GenericType<Tuple6<?, ?, ?, ?, ?, ?>>() {}, Tuple.of(1, 2, 3, 4, 5, 6));
    }

    @Test
    public void testBuildRowMapperForTuple7ShouldSucceed() throws SQLException {
        testProjectionMapper(new org.jdbi.v3.core.generic.GenericType<Tuple7<?, ?, ?, ?, ?, ?, ?>>() {}, Tuple.of(1, 2, 3, 4, 5, 6, 7));
    }

    @Test
    public void testBuildRowMapperForTuple8ShouldSucceed() throws SQLException {
        testProjectionMapper(new org.jdbi.v3.core.generic.GenericType<Tuple8<?, ?, ?, ?, ?, ?, ?, ?>>() {}, Tuple.of(1, 2, 3, 4, 5, 6, 7, 8));
    }
}

