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
package org.jdbi.v3.sqlobject;


import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Something;
import org.jdbi.v3.core.mapper.SomethingMapper;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;
import org.junit.Rule;
import org.junit.Test;


public class TestReturningQueryResults {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withSomething().withPlugin(new SqlObjectPlugin());

    private Handle handle;

    @Test
    public void testSingleValue() {
        handle.execute("insert into something (id, name) values (7, 'Tim')");
        dbRule.getJdbi().useExtension(TestReturningQueryResults.Spiffy.class, ( spiffy) -> {
            Something s = spiffy.findById(7);
            assertThat(s.getName()).isEqualTo("Tim");
        });
    }

    @Test
    public void testIterator() {
        handle.execute("insert into something (id, name) values (7, 'Tim')");
        handle.execute("insert into something (id, name) values (3, 'Diego')");
        dbRule.getJdbi().useExtension(TestReturningQueryResults.Spiffy.class, ( spiffy) -> {
            Iterator<Something> itty = spiffy.findByIdRange(2, 10);
            assertThat(itty).toIterable().containsOnlyOnce(new Something(7, "Tim"), new Something(3, "Diego"));
        });
    }

    @Test
    public void testList() {
        handle.execute("insert into something (id, name) values (7, 'Tim')");
        handle.execute("insert into something (id, name) values (3, 'Diego')");
        dbRule.getJdbi().useExtension(TestReturningQueryResults.Spiffy.class, ( spiffy) -> {
            List<Something> all = spiffy.findTwoByIds(3, 7);
            assertThat(all).containsOnlyOnce(new Something(7, "Tim"), new Something(3, "Diego"));
        });
    }

    public interface Spiffy {
        @SqlQuery("select id, name from something where id = :id")
        @UseRowMapper(SomethingMapper.class)
        Something findById(@Bind("id")
        int id);

        @SqlQuery("select id, name from something where id >= :from and id <= :to")
        @UseRowMapper(SomethingMapper.class)
        java.util.Iterator<Something> findByIdRange(@Bind("from")
        int from, @Bind("to")
        int to);

        @SqlQuery("select id, name from something where id = :first or id = :second")
        @UseRowMapper(SomethingMapper.class)
        java.util.List<Something> findTwoByIds(@Bind("first")
        int from, @Bind("second")
        int to);
    }
}

