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
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.junit.Rule;
import org.junit.Test;


public class TestBindAutomaticNames {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withSomething().withPlugin(new SqlObjectPlugin());

    private Handle handle;

    @Test
    public void testAnnotationNoValue() {
        TestBindAutomaticNames.Spiffy spiffy = handle.attach(TestBindAutomaticNames.Spiffy.class);
        Something s = spiffy.findById(7);
        assertThat(s.getName()).isEqualTo("Tim");
    }

    @Test
    public void testNoAnnotation() {
        TestBindAutomaticNames.Spiffy spiffy = dbRule.getSharedHandle().attach(TestBindAutomaticNames.Spiffy.class);
        Something s = spiffy.findByIdNoAnnotation(7);
        assertThat(s.getName()).isEqualTo("Tim");
    }

    @RegisterRowMapper(SomethingMapper.class)
    public interface Spiffy {
        @SqlQuery("select id, name from something where id = :id")
        Something findById(@Bind
        int id);

        @SqlQuery("select id, name from something where id = :id")
        Something findByIdNoAnnotation(int id);
    }
}

