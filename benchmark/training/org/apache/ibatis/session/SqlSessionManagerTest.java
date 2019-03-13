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
package org.apache.ibatis.session;


import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.domain.blog.Author;
import org.apache.ibatis.domain.blog.mappers.AuthorMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.junit.Assert;
import org.junit.Test;


public class SqlSessionManagerTest extends BaseDataTest {
    private static SqlSessionManager manager;

    @Test
    public void shouldThrowExceptionIfMappedStatementDoesNotExistAndSqlSessionIsOpen() throws Exception {
        try {
            SqlSessionManagerTest.manager.startManagedSession();
            SqlSessionManagerTest.manager.selectList("ThisStatementDoesNotExist");
            Assert.fail("Expected exception to be thrown due to statement that does not exist.");
        } catch (PersistenceException e) {
            Assert.assertTrue(e.getMessage().contains("does not contain value for ThisStatementDoesNotExist"));
        } finally {
            SqlSessionManagerTest.manager.close();
        }
    }

    @Test
    public void shouldCommitInsertedAuthor() throws Exception {
        try {
            SqlSessionManagerTest.manager.startManagedSession();
            AuthorMapper mapper = SqlSessionManagerTest.manager.getMapper(AuthorMapper.class);
            Author expected = new Author(500, "cbegin", "******", "cbegin@somewhere.com", "Something...", null);
            mapper.insertAuthor(expected);
            SqlSessionManagerTest.manager.commit();
            Author actual = mapper.selectAuthor(500);
            Assert.assertNotNull(actual);
        } finally {
            SqlSessionManagerTest.manager.close();
        }
    }

    @Test
    public void shouldRollbackInsertedAuthor() throws Exception {
        try {
            SqlSessionManagerTest.manager.startManagedSession();
            AuthorMapper mapper = SqlSessionManagerTest.manager.getMapper(AuthorMapper.class);
            Author expected = new Author(501, "lmeadors", "******", "lmeadors@somewhere.com", "Something...", null);
            mapper.insertAuthor(expected);
            SqlSessionManagerTest.manager.rollback();
            Author actual = mapper.selectAuthor(501);
            Assert.assertNull(actual);
        } finally {
            SqlSessionManagerTest.manager.close();
        }
    }

    @Test
    public void shouldImplicitlyRollbackInsertedAuthor() throws Exception {
        SqlSessionManagerTest.manager.startManagedSession();
        AuthorMapper mapper = SqlSessionManagerTest.manager.getMapper(AuthorMapper.class);
        Author expected = new Author(502, "emacarron", "******", "emacarron@somewhere.com", "Something...", null);
        mapper.insertAuthor(expected);
        SqlSessionManagerTest.manager.close();
        Author actual = mapper.selectAuthor(502);
        Assert.assertNull(actual);
    }
}

