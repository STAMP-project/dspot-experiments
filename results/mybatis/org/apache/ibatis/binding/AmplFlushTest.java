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


package org.apache.ibatis.binding;


public class AmplFlushTest {
    private static org.apache.ibatis.session.SqlSessionFactory sqlSessionFactory;

    @org.junit.BeforeClass
    public static void setup() throws java.lang.Exception {
        javax.sql.DataSource dataSource = org.apache.ibatis.BaseDataTest.createBlogDataSource();
        org.apache.ibatis.transaction.TransactionFactory transactionFactory = new org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory();
        org.apache.ibatis.mapping.Environment environment = new org.apache.ibatis.mapping.Environment("Production", transactionFactory, dataSource);
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration(environment);
        configuration.setDefaultExecutorType(org.apache.ibatis.session.ExecutorType.BATCH);
        configuration.getTypeAliasRegistry().registerAlias(org.apache.ibatis.domain.blog.Post.class);
        configuration.getTypeAliasRegistry().registerAlias(org.apache.ibatis.domain.blog.Author.class);
        configuration.addMapper(org.apache.ibatis.binding.BoundAuthorMapper.class);
        org.apache.ibatis.binding.AmplFlushTest.sqlSessionFactory = new org.apache.ibatis.session.SqlSessionFactoryBuilder().build(configuration);
    }

    @org.junit.Test
    public void invokeFlushStatementsViaMapper() {
        org.apache.ibatis.session.SqlSession session = org.apache.ibatis.binding.AmplFlushTest.sqlSessionFactory.openSession();
        try {
            org.apache.ibatis.binding.BoundAuthorMapper mapper = session.getMapper(org.apache.ibatis.binding.BoundAuthorMapper.class);
            org.apache.ibatis.domain.blog.Author author = new org.apache.ibatis.domain.blog.Author((-1), "cbegin", "******", "cbegin@nowhere.com", "N/A", org.apache.ibatis.domain.blog.Section.NEWS);
            java.util.List<java.lang.Integer> ids = new java.util.ArrayList<java.lang.Integer>();
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
            java.util.List<org.apache.ibatis.executor.BatchResult> results = mapper.flush();
            org.junit.Assert.assertThat(results.size(), org.hamcrest.core.Is.is(1));
            org.junit.Assert.assertThat(results.get(0).getUpdateCounts().length, org.hamcrest.core.Is.is(ids.size()));
            for (int id : ids) {
                org.apache.ibatis.domain.blog.Author selectedAuthor = mapper.selectAuthor(id);
                org.junit.Assert.assertNotNull((id + " is not found."), selectedAuthor);
            }
            session.rollback();
        } finally {
            session.close();
        }
    }
}

