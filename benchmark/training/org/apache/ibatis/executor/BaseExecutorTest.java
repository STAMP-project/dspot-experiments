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
package org.apache.ibatis.executor;


import Executor.NO_RESULT_HANDLER;
import RowBounds.DEFAULT;
import java.util.List;
import javax.sql.DataSource;
import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.domain.blog.Post;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class BaseExecutorTest extends BaseDataTest {
    protected final Configuration config;

    private static DataSource ds;

    BaseExecutorTest() {
        config = new Configuration();
        config.setLazyLoadingEnabled(true);
        config.setUseGeneratedKeys(false);
        config.setMultipleResultSetsEnabled(true);
        config.setUseColumnLabel(true);
        config.setDefaultStatementTimeout(5000);
        config.setDefaultFetchSize(100);
    }

    @Test
    public void shouldFetchPostWithBlogWithCompositeKey() throws Exception {
        Executor executor = createExecutor(new JdbcTransaction(BaseExecutorTest.ds, null, false));
        try {
            MappedStatement selectBlog = ExecutorTestHelper.prepareSelectBlogByIdAndAuthor(config);
            MappedStatement selectPost = ExecutorTestHelper.prepareSelectPostWithBlogByAuthorMappedStatement(config);
            config.addMappedStatement(selectBlog);
            config.addMappedStatement(selectPost);
            List<Post> posts = executor.query(selectPost, 2, DEFAULT, NO_RESULT_HANDLER);
            executor.flushStatements();
            Assertions.assertEquals(1, posts.size());
            Post post = posts.get(0);
            Assertions.assertNotNull(post.getBlog());
            Assertions.assertEquals(101, post.getBlog().getAuthor().getId());
            executor.rollback(true);
        } finally {
            executor.rollback(true);
            executor.close(false);
        }
    }
}

