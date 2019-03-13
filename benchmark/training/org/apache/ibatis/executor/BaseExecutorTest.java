/**
 * Copyright 2009-2012 the original author or authors.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javassist.util.proxy.Proxy;
import javax.sql.DataSource;
import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.domain.blog.Author;
import org.apache.ibatis.domain.blog.Blog;
import org.apache.ibatis.domain.blog.Post;
import org.apache.ibatis.domain.blog.Section;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import org.junit.Assert;
import org.junit.Test;

import static BatchExecutor.BATCH_UPDATE_RETURN_VALUE;


public class BaseExecutorTest extends BaseDataTest {
    protected final Configuration config;

    protected static DataSource ds;

    public BaseExecutorTest() {
        config = new Configuration();
        config.setLazyLoadingEnabled(true);
        config.setUseGeneratedKeys(false);
        config.setMultipleResultSetsEnabled(true);
        config.setUseColumnLabel(true);
        config.setDefaultStatementTimeout(5000);
    }

    @Test
    public void shouldInsertNewAuthorWithBeforeAutoKey() throws Exception {
        Executor executor = createExecutor(new JdbcTransaction(BaseExecutorTest.ds, null, false));
        try {
            Author author = new Author((-1), "someone", "******", "someone@apache.org", null, Section.NEWS);
            MappedStatement insertStatement = ExecutorTestHelper.prepareInsertAuthorMappedStatementWithBeforeAutoKey(config);
            MappedStatement selectStatement = ExecutorTestHelper.prepareSelectOneAuthorMappedStatement(config);
            int rows = executor.update(insertStatement, author);
            Assert.assertTrue(((rows > 0) || (rows == (BATCH_UPDATE_RETURN_VALUE))));
            if (rows == (BATCH_UPDATE_RETURN_VALUE)) {
                executor.flushStatements();
            }
            Assert.assertEquals(123456, author.getId());
            if ((author.getId()) != (BATCH_UPDATE_RETURN_VALUE)) {
                List<Author> authors = executor.query(selectStatement, author.getId(), DEFAULT, NO_RESULT_HANDLER);
                executor.rollback(true);
                Assert.assertEquals(1, authors.size());
                Assert.assertEquals(author.toString(), authors.get(0).toString());
                Assert.assertTrue(((author.getId()) >= 10000));
            }
        } finally {
            executor.rollback(true);
            executor.close(false);
        }
    }

    @Test
    public void shouldInsertNewAuthor() throws Exception {
        Executor executor = createExecutor(new JdbcTransaction(BaseExecutorTest.ds, null, false));
        try {
            Author author = new Author(99, "someone", "******", "someone@apache.org", null, Section.NEWS);
            MappedStatement insertStatement = ExecutorTestHelper.prepareInsertAuthorMappedStatement(config);
            MappedStatement selectStatement = ExecutorTestHelper.prepareSelectOneAuthorMappedStatement(config);
            int rows = executor.update(insertStatement, author);
            List<Author> authors = executor.query(selectStatement, 99, DEFAULT, NO_RESULT_HANDLER);
            executor.flushStatements();
            executor.rollback(true);
            Assert.assertEquals(1, authors.size());
            Assert.assertEquals(author.toString(), authors.get(0).toString());
            Assert.assertTrue(((1 == rows) || ((BATCH_UPDATE_RETURN_VALUE) == rows)));
        } finally {
            executor.rollback(true);
            executor.close(false);
        }
    }

    @Test
    public void shouldSelectAllAuthorsAutoMapped() throws Exception {
        Executor executor = createExecutor(new JdbcTransaction(BaseExecutorTest.ds, null, false));
        try {
            MappedStatement selectStatement = ExecutorTestHelper.prepareSelectAllAuthorsAutoMappedStatement(config);
            List<Author> authors = executor.query(selectStatement, null, DEFAULT, NO_RESULT_HANDLER);
            Assert.assertEquals(2, authors.size());
            Author author = authors.get(0);
            // id,username, password, email, bio, favourite_section
            // (101,'jim','********','jim@ibatis.apache.org','','NEWS');
            Assert.assertEquals(101, author.getId());
            Assert.assertEquals("jim", author.getUsername());
            Assert.assertEquals("jim@ibatis.apache.org", author.getEmail());
            Assert.assertEquals("", author.getBio());
            Assert.assertEquals(Section.NEWS, author.getFavouriteSection());
        } finally {
            executor.rollback(true);
            executor.close(false);
        }
    }

    @Test
    public void shouldInsertNewAuthorWithAutoKey() throws Exception {
        Executor executor = createExecutor(new JdbcTransaction(BaseExecutorTest.ds, null, false));
        try {
            Author author = new Author((-1), "someone", "******", "someone@apache.org", null, Section.NEWS);
            MappedStatement insertStatement = ExecutorTestHelper.prepareInsertAuthorMappedStatementWithAutoKey(config);
            MappedStatement selectStatement = ExecutorTestHelper.prepareSelectOneAuthorMappedStatement(config);
            int rows = executor.update(insertStatement, author);
            Assert.assertTrue(((rows > 0) || (rows == (BATCH_UPDATE_RETURN_VALUE))));
            if (rows == (BATCH_UPDATE_RETURN_VALUE)) {
                executor.flushStatements();
            }
            Assert.assertTrue(((-1) != (author.getId())));
            if ((author.getId()) != (BATCH_UPDATE_RETURN_VALUE)) {
                List<Author> authors = executor.query(selectStatement, author.getId(), DEFAULT, NO_RESULT_HANDLER);
                executor.rollback(true);
                Assert.assertEquals(1, authors.size());
                Assert.assertEquals(author.toString(), authors.get(0).toString());
                Assert.assertTrue(((author.getId()) >= 10000));
            }
        } finally {
            executor.rollback(true);
            executor.close(false);
        }
    }

    @Test
    public void shouldInsertNewAuthorByProc() throws Exception {
        Executor executor = createExecutor(new JdbcTransaction(BaseExecutorTest.ds, null, false));
        try {
            Author author = new Author(97, "someone", "******", "someone@apache.org", null, null);
            MappedStatement insertStatement = ExecutorTestHelper.prepareInsertAuthorProc(config);
            MappedStatement selectStatement = ExecutorTestHelper.prepareSelectOneAuthorMappedStatement(config);
            int rows = executor.update(insertStatement, author);
            List<Author> authors = executor.query(selectStatement, 97, DEFAULT, NO_RESULT_HANDLER);
            executor.flushStatements();
            executor.rollback(true);
            Assert.assertEquals(1, authors.size());
            Assert.assertEquals(author.toString(), authors.get(0).toString());
        } finally {
            executor.rollback(true);
            executor.close(false);
        }
    }

    @Test
    public void shouldInsertNewAuthorUsingSimpleNonPreparedStatements() throws Exception {
        Executor executor = createExecutor(new JdbcTransaction(BaseExecutorTest.ds, null, false));
        try {
            Author author = new Author(99, "someone", "******", "someone@apache.org", null, null);
            MappedStatement insertStatement = ExecutorTestHelper.createInsertAuthorWithIDof99MappedStatement(config);
            MappedStatement selectStatement = ExecutorTestHelper.createSelectAuthorWithIDof99MappedStatement(config);
            int rows = executor.update(insertStatement, null);
            List<Author> authors = executor.query(selectStatement, 99, DEFAULT, NO_RESULT_HANDLER);
            executor.flushStatements();
            executor.rollback(true);
            Assert.assertEquals(1, authors.size());
            Assert.assertEquals(author.toString(), authors.get(0).toString());
            Assert.assertTrue(((1 == rows) || ((BATCH_UPDATE_RETURN_VALUE) == rows)));
        } finally {
            executor.rollback(true);
            executor.close(false);
        }
    }

    @Test
    public void shouldUpdateAuthor() throws Exception {
        Executor executor = createExecutor(new JdbcTransaction(BaseExecutorTest.ds, null, false));
        try {
            Author author = new Author(101, "someone", "******", "someone@apache.org", null, Section.NEWS);
            MappedStatement updateStatement = ExecutorTestHelper.prepareUpdateAuthorMappedStatement(config);
            MappedStatement selectStatement = ExecutorTestHelper.prepareSelectOneAuthorMappedStatement(config);
            int rows = executor.update(updateStatement, author);
            List<Author> authors = executor.query(selectStatement, 101, DEFAULT, NO_RESULT_HANDLER);
            executor.flushStatements();
            executor.rollback(true);
            Assert.assertEquals(1, authors.size());
            Assert.assertEquals(author.toString(), authors.get(0).toString());
            Assert.assertTrue(((1 == rows) || ((BATCH_UPDATE_RETURN_VALUE) == rows)));
        } finally {
            executor.rollback(true);
            executor.close(false);
        }
    }

    @Test
    public void shouldDeleteAuthor() throws Exception {
        Executor executor = createExecutor(new JdbcTransaction(BaseExecutorTest.ds, null, false));
        try {
            Author author = new Author(101, null, null, null, null, null);
            MappedStatement deleteStatement = ExecutorTestHelper.prepareDeleteAuthorMappedStatement(config);
            MappedStatement selectStatement = ExecutorTestHelper.prepareSelectOneAuthorMappedStatement(config);
            int rows = executor.update(deleteStatement, author);
            List<Author> authors = executor.query(selectStatement, 101, DEFAULT, NO_RESULT_HANDLER);
            executor.flushStatements();
            executor.rollback(true);
            Assert.assertEquals(0, authors.size());
            Assert.assertTrue(((1 == rows) || ((BATCH_UPDATE_RETURN_VALUE) == rows)));
        } finally {
            executor.rollback(true);
            executor.close(false);
        }
    }

    @Test
    public void shouldSelectDiscriminatedPost() throws Exception {
        Executor executor = createExecutor(new JdbcTransaction(BaseExecutorTest.ds, null, false));
        try {
            MappedStatement selectStatement = ExecutorTestHelper.prepareSelectDiscriminatedPost(config);
            List<Map<String, String>> products = executor.query(selectStatement, null, DEFAULT, NO_RESULT_HANDLER);
            Assert.assertEquals(5, products.size());
            for (Map<String, String> m : products) {
                if ("IMAGES".equals(m.get("SECTION"))) {
                    Assert.assertNull(m.get("subject"));
                } else {
                    Assert.assertNotNull(m.get("subject"));
                }
            }
        } finally {
            executor.close(false);
        }
    }

    @Test
    public void shouldSelect2DiscriminatedPosts() throws Exception {
        Executor executor = createExecutor(new JdbcTransaction(BaseExecutorTest.ds, null, false));
        try {
            MappedStatement selectStatement = ExecutorTestHelper.prepareSelectDiscriminatedPost(config);
            List<Map<String, String>> products = executor.query(selectStatement, null, new RowBounds(2, 2), NO_RESULT_HANDLER);
            Assert.assertEquals(2, products.size());
            for (Map<String, String> m : products) {
                if ("IMAGES".equals(m.get("SECTION"))) {
                    Assert.assertNull(m.get("subject"));
                } else {
                    Assert.assertNotNull(m.get("subject"));
                }
            }
        } finally {
            executor.rollback(true);
            executor.close(false);
        }
    }

    @Test
    public void shouldSelectTwoSetsOfAuthorsViaProc() throws Exception {
        Executor executor = createExecutor(new JdbcTransaction(BaseExecutorTest.ds, null, false));
        try {
            MappedStatement selectStatement = ExecutorTestHelper.prepareSelectTwoSetsOfAuthorsProc(config);
            List<List<Author>> authorSets = executor.query(selectStatement, new HashMap<String, Object>() {
                {
                    put("id1", 101);
                    put("id2", 102);
                }
            }, DEFAULT, NO_RESULT_HANDLER);
            Assert.assertEquals(2, authorSets.size());
            for (List<Author> authors : authorSets) {
                Assert.assertEquals(2, authors.size());
                for (Object author : authors) {
                    Assert.assertTrue((author instanceof Author));
                }
            }
        } finally {
            executor.rollback(true);
            executor.close(false);
        }
    }

    @Test
    public void shouldSelectAuthorViaOutParams() throws Exception {
        Executor executor = createExecutor(new JdbcTransaction(BaseExecutorTest.ds, null, false));
        try {
            MappedStatement selectStatement = ExecutorTestHelper.prepareSelectAuthorViaOutParams(config);
            Author author = new Author(102, null, null, null, null, null);
            executor.query(selectStatement, author, DEFAULT, NO_RESULT_HANDLER);
            Assert.assertEquals("sally", author.getUsername());
            Assert.assertEquals("********", author.getPassword());
            Assert.assertEquals("sally@ibatis.apache.org", author.getEmail());
            Assert.assertEquals(null, author.getBio());
        } catch (ExecutorException e) {
            if (executor instanceof CachingExecutor) {
                // TODO see issue #464. Fail is OK.
                Assert.assertTrue(e.getMessage().contains("OUT params is not supported"));
            } else {
                throw e;
            }
        } finally {
            executor.rollback(true);
            executor.close(false);
        }
    }

    @Test
    public void shouldFetchPostsForBlog() throws Exception {
        Executor executor = createExecutor(new JdbcTransaction(BaseExecutorTest.ds, null, false));
        try {
            MappedStatement selectBlog = ExecutorTestHelper.prepareComplexSelectBlogMappedStatement(config);
            MappedStatement selectPosts = ExecutorTestHelper.prepareSelectPostsForBlogMappedStatement(config);
            config.addMappedStatement(selectBlog);
            config.addMappedStatement(selectPosts);
            List<Post> posts = executor.query(selectPosts, 1, DEFAULT, NO_RESULT_HANDLER);
            executor.flushStatements();
            Assert.assertEquals(2, posts.size());
            Assert.assertTrue(((posts.get(1)) instanceof Proxy));
            Assert.assertNotNull(posts.get(1).getBlog());
            Assert.assertEquals(1, posts.get(1).getBlog().getId());
            executor.rollback(true);
        } finally {
            executor.rollback(true);
            executor.close(false);
        }
    }

    @Test
    public void shouldFetchOneOrphanedPostWithNoBlog() throws Exception {
        Executor executor = createExecutor(new JdbcTransaction(BaseExecutorTest.ds, null, false));
        try {
            MappedStatement selectBlog = ExecutorTestHelper.prepareComplexSelectBlogMappedStatement(config);
            MappedStatement selectPost = ExecutorTestHelper.prepareSelectPostMappedStatement(config);
            config.addMappedStatement(selectBlog);
            config.addMappedStatement(selectPost);
            List<Post> posts = executor.query(selectPost, 5, DEFAULT, NO_RESULT_HANDLER);
            executor.flushStatements();
            executor.rollback(true);
            Assert.assertEquals(1, posts.size());
            Post post = posts.get(0);
            Assert.assertNull(post.getBlog());
        } finally {
            executor.rollback(true);
            executor.close(false);
        }
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
            Assert.assertEquals(1, posts.size());
            Post post = posts.get(0);
            Assert.assertNotNull(post.getBlog());
            Assert.assertEquals(101, post.getBlog().getAuthor().getId());
            executor.rollback(true);
        } finally {
            executor.rollback(true);
            executor.close(false);
        }
    }

    @Test
    public void shouldFetchComplexBlogs() throws Exception {
        Executor executor = createExecutor(new JdbcTransaction(BaseExecutorTest.ds, null, false));
        try {
            MappedStatement selectBlog = ExecutorTestHelper.prepareComplexSelectBlogMappedStatement(config);
            MappedStatement selectPosts = ExecutorTestHelper.prepareSelectPostsForBlogMappedStatement(config);
            config.addMappedStatement(selectBlog);
            config.addMappedStatement(selectPosts);
            List<Blog> blogs = executor.query(selectBlog, 1, DEFAULT, NO_RESULT_HANDLER);
            executor.flushStatements();
            Assert.assertEquals(1, blogs.size());
            Assert.assertNotNull(blogs.get(0).getPosts());
            Assert.assertEquals(2, blogs.get(0).getPosts().size());
            Assert.assertEquals(1, blogs.get(0).getPosts().get(1).getBlog().getPosts().get(1).getBlog().getId());
            executor.rollback(true);
        } finally {
            executor.rollback(true);
            executor.close(false);
        }
    }

    @Test
    public void shouldMapConstructorResults() throws Exception {
        Executor executor = createExecutor(new JdbcTransaction(BaseExecutorTest.ds, null, false));
        try {
            MappedStatement selectStatement = ExecutorTestHelper.prepareSelectOneAuthorMappedStatementWithConstructorResults(config);
            List<Author> authors = executor.query(selectStatement, 102, DEFAULT, NO_RESULT_HANDLER);
            executor.flushStatements();
            executor.rollback(true);
            Assert.assertEquals(1, authors.size());
            Author author = authors.get(0);
            Assert.assertEquals(102, author.getId());
        } finally {
            executor.rollback(true);
            executor.close(false);
        }
    }

    @Test
    public void shouldClearDeferredLoads() throws Exception {
        Executor executor = createExecutor(new JdbcTransaction(BaseExecutorTest.ds, null, false));
        try {
            MappedStatement selectBlog = ExecutorTestHelper.prepareComplexSelectBlogMappedStatement(config);
            MappedStatement selectPosts = ExecutorTestHelper.prepareSelectPostsForBlogMappedStatement(config);
            config.addMappedStatement(selectBlog);
            config.addMappedStatement(selectPosts);
            MappedStatement selectAuthor = ExecutorTestHelper.prepareSelectOneAuthorMappedStatement(config);
            MappedStatement insertAuthor = ExecutorTestHelper.prepareInsertAuthorMappedStatement(config);
            // generate DeferredLoads
            executor.query(selectPosts, 1, DEFAULT, NO_RESULT_HANDLER);
            Author author = new Author((-1), "someone", "******", "someone@apache.org", null, Section.NEWS);
            executor.update(insertAuthor, author);
            executor.query(selectAuthor, (-1), DEFAULT, NO_RESULT_HANDLER);
            executor.flushStatements();
            executor.rollback(true);
        } finally {
            executor.rollback(true);
            executor.close(false);
        }
    }
}

