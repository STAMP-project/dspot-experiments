/**
 * Copyright 2009-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.binding;


import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.domain.blog.Author;
import org.apache.ibatis.domain.blog.Section;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FlushTest {
    private static SqlSessionFactory sqlSessionFactory;

    @Test
    public void invokeFlushStatementsViaMapper() {
        try (SqlSession session = FlushTest.sqlSessionFactory.openSession()) {
            BoundAuthorMapper mapper = session.getMapper(BoundAuthorMapper.class);
            Author author = new Author((-1), "cbegin", "******", "cbegin@nowhere.com", "N/A", Section.NEWS);
            List<Integer> ids = new ArrayList<>();
            mapper.insertAuthor(author);
            ids.add(author.getId());
            mapper.insertAuthor(author);
            ids.add(author.getId());
            mapper.insertAuthor(author);
            ids.add(author.getId());
            mapper.insertAuthor(author);
            ids.add(author.getId());
            mapper.insertAuthor(author);
            ids.add(author.getId());
            // test
            List<BatchResult> results = mapper.flush();
            assertThat(results.size()).isEqualTo(1);
            assertThat(results.get(0).getUpdateCounts().length).isEqualTo(ids.size());
            for (int id : ids) {
                Author selectedAuthor = mapper.selectAuthor(id);
                Assertions.assertNotNull(selectedAuthor, (id + " is not found."));
            }
            session.rollback();
        }
    }
}

