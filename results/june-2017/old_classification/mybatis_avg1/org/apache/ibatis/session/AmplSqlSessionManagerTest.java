/**
 * Copyright 2009-2015 the original author or authors.
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


public class AmplSqlSessionManagerTest extends org.apache.ibatis.BaseDataTest {
    private static org.apache.ibatis.session.SqlSessionManager manager;

    @org.junit.BeforeClass
    public static void setup() throws java.lang.Exception {
        org.apache.ibatis.BaseDataTest.createBlogDataSource();
        final java.lang.String resource = "org/apache/ibatis/builder/MapperConfig.xml";
        final java.io.Reader reader = org.apache.ibatis.io.Resources.getResourceAsReader(resource);
        org.apache.ibatis.session.AmplSqlSessionManagerTest.manager = org.apache.ibatis.session.SqlSessionManager.newInstance(reader);
    }

    @org.junit.Test
    public void shouldThrowExceptionIfMappedStatementDoesNotExistAndSqlSessionIsOpen() throws java.lang.Exception {
        try {
            org.apache.ibatis.session.AmplSqlSessionManagerTest.manager.startManagedSession();
            org.apache.ibatis.session.AmplSqlSessionManagerTest.manager.selectList("ThisStatementDoesNotExist");
            org.junit.Assert.fail("Expected exception to be thrown due to statement that does not exist.");
        } catch (org.apache.ibatis.exceptions.PersistenceException e) {
            org.junit.Assert.assertTrue(e.getMessage().contains("does not contain value for ThisStatementDoesNotExist"));
        } finally {
            org.apache.ibatis.session.AmplSqlSessionManagerTest.manager.close();
        }
    }

    @org.junit.Test
    public void shouldCommitInsertedAuthor() throws java.lang.Exception {
        try {
            org.apache.ibatis.session.AmplSqlSessionManagerTest.manager.startManagedSession();
            org.apache.ibatis.domain.blog.mappers.AuthorMapper mapper = org.apache.ibatis.session.AmplSqlSessionManagerTest.manager.getMapper(org.apache.ibatis.domain.blog.mappers.AuthorMapper.class);
            org.apache.ibatis.domain.blog.Author expected = new org.apache.ibatis.domain.blog.Author(500, "cbegin", "******", "cbegin@somewhere.com", "Something...", null);
            mapper.insertAuthor(expected);
            org.apache.ibatis.session.AmplSqlSessionManagerTest.manager.commit();
            org.apache.ibatis.domain.blog.Author actual = mapper.selectAuthor(500);
            org.junit.Assert.assertNotNull(actual);
        } finally {
            org.apache.ibatis.session.AmplSqlSessionManagerTest.manager.close();
        }
    }

    @org.junit.Test
    public void shouldRollbackInsertedAuthor() throws java.lang.Exception {
        try {
            org.apache.ibatis.session.AmplSqlSessionManagerTest.manager.startManagedSession();
            org.apache.ibatis.domain.blog.mappers.AuthorMapper mapper = org.apache.ibatis.session.AmplSqlSessionManagerTest.manager.getMapper(org.apache.ibatis.domain.blog.mappers.AuthorMapper.class);
            org.apache.ibatis.domain.blog.Author expected = new org.apache.ibatis.domain.blog.Author(501, "lmeadors", "******", "lmeadors@somewhere.com", "Something...", null);
            mapper.insertAuthor(expected);
            org.apache.ibatis.session.AmplSqlSessionManagerTest.manager.rollback();
            org.apache.ibatis.domain.blog.Author actual = mapper.selectAuthor(501);
            org.junit.Assert.assertNull(actual);
        } finally {
            org.apache.ibatis.session.AmplSqlSessionManagerTest.manager.close();
        }
    }

    @org.junit.Test
    public void shouldImplicitlyRollbackInsertedAuthor() throws java.lang.Exception {
        org.apache.ibatis.session.AmplSqlSessionManagerTest.manager.startManagedSession();
        org.apache.ibatis.domain.blog.mappers.AuthorMapper mapper = org.apache.ibatis.session.AmplSqlSessionManagerTest.manager.getMapper(org.apache.ibatis.domain.blog.mappers.AuthorMapper.class);
        org.apache.ibatis.domain.blog.Author expected = new org.apache.ibatis.domain.blog.Author(502, "emacarron", "******", "emacarron@somewhere.com", "Something...", null);
        mapper.insertAuthor(expected);
        org.apache.ibatis.session.AmplSqlSessionManagerTest.manager.close();
        org.apache.ibatis.domain.blog.Author actual = mapper.selectAuthor(502);
        org.junit.Assert.assertNull(actual);
    }
}

