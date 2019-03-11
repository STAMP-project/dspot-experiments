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
package org.jdbi.v3.sqlobject.config;


import java.util.List;
import java.util.Optional;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.sqlobject.SqlObject;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.junit.Rule;
import org.junit.Test;


public class TestRegisterConstructorMapper {
    @Rule
    public H2DatabaseRule rule = new H2DatabaseRule().withPlugin(new SqlObjectPlugin());

    Handle handle;

    @Test
    public void registerConstructorMappers() {
        TestRegisterConstructorMapper.BlogDao dao = handle.attach(TestRegisterConstructorMapper.BlogDao.class);
        assertThat(dao.listArticles()).containsExactly(Article.newArticle(1, "title 1", "content 1"), Article.newArticle(2, "title 2", "content 2"));
        assertThat(dao.getArticleWithComments(0)).isEmpty();
        assertThat(dao.getArticleWithComments(1)).contains(Article.newArticle(1, "title 1", "content 1", Comment.newComment(10, "comment 10"), Comment.newComment(11, "comment 11")));
        assertThat(dao.getArticleWithComments(2)).contains(Article.newArticle(2, "title 2", "content 2", Comment.newComment(20, "comment 20")));
    }

    public interface BlogDao extends SqlObject {
        @SqlQuery("select * from articles order by id")
        @RegisterConstructorMapper(Article.class)
        List<Article> listArticles();

        @RegisterConstructorMapper(value = Article.class, prefix = "a")
        @RegisterConstructorMapper(value = Comment.class, prefix = "c")
        default Optional<Article> getArticleWithComments(long id) {
            return getHandle().select(("select " + (((((((((" a.id a_id, " + " a.title a_title, ") + " a.content a_content, ") + " c.id c_id, ") + " c.content c_content ") + "from articles a ") + "left join comments c ") + " on a.id = c.article_id ") + "where a.id = ? ") + "order by c.id")), id).reduceRows(Optional.empty(), ( acc, rv) -> {
                Article a = acc.orElseGet(() -> rv.getRow(.class));
                if ((rv.getColumn("c_id", .class)) != null) {
                    a.getComments().add(rv.getRow(.class));
                }
                return Optional.of(a);
            });
        }
    }
}

