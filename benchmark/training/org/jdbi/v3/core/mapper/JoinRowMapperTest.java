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
package org.jdbi.v3.core.mapper;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Objects;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.junit.Rule;
import org.junit.Test;


public class JoinRowMapperTest {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule();

    private Handle h;

    @Test
    public void testCartesianProduct() {
        Multimap<JoinRowMapperTest.User, JoinRowMapperTest.Article> product = HashMultimap.create();
        h.createQuery("SELECT * FROM user, article").map(JoinRowMapper.forTypes(JoinRowMapperTest.User.class, JoinRowMapperTest.Article.class)).forEach(( jr) -> product.put(jr.get(.class), jr.get(.class)));
        Multimap<JoinRowMapperTest.User, JoinRowMapperTest.Article> expected = HashMultimap.create();
        IntStream.rangeClosed(1, 3).forEach(( u) -> IntStream.rangeClosed(1, 3).forEach(( a) -> expected.put(JoinRowMapperTest.u(u), JoinRowMapperTest.a(a))));
        assertThat(product).isEqualTo(expected);
    }

    @Test
    public void testJoin() {
        // tag::multimap[]
        Multimap<JoinRowMapperTest.User, JoinRowMapperTest.Article> joined = HashMultimap.create();
        h.createQuery("SELECT * FROM user NATURAL JOIN author NATURAL JOIN article").map(JoinRowMapper.forTypes(JoinRowMapperTest.User.class, JoinRowMapperTest.Article.class)).forEach(( jr) -> joined.put(jr.get(.class), jr.get(.class)));
        // end::multimap[]
        assertThat(joined).isEqualTo(JoinRowMapperTest.getExpected());
    }

    public static class User {
        private final int uid;

        private final String name;

        public User(int uid, String name) {
            this.uid = uid;
            this.name = name;
        }

        @Override
        public int hashCode() {
            return Objects.hash(uid, name);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof JoinRowMapperTest.User) {
                JoinRowMapperTest.User that = ((JoinRowMapperTest.User) (obj));
                return (Objects.equals(uid, that.uid)) && (Objects.equals(name, that.name));
            }
            return false;
        }
    }

    public static class Article {
        private final int aid;

        private final String title;

        public Article(int aid, String title) {
            this.aid = aid;
            this.title = title;
        }

        @Override
        public int hashCode() {
            return Objects.hash(aid, title);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof JoinRowMapperTest.Article) {
                JoinRowMapperTest.Article that = ((JoinRowMapperTest.Article) (obj));
                return (Objects.equals(aid, that.aid)) && (Objects.equals(title, that.title));
            }
            return false;
        }
    }
}

