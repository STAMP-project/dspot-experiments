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
package org.apache.ibatis.submitted.multiple_resultsets;


import java.io.IOException;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/* This class contains tests for multiple results.  
It is based on Jeff's ref cursor tests.

The tests require a
local install of PostgreSQL and cannot be run as a part of the normal
MyBatis build unless PostreSQL is setup on the build machine as 
described in setupdb.txt

If PostgreSQL is setup as described in setupdb.txt, then remove
the @Ignore annotation to enable the tests.
 */
@Ignore("See setupdb.txt for instructions on how to run the tests in this class")
public class MultipleResultTest {
    private static SqlSessionFactory sqlSessionFactory;

    @Test
    public void shouldGetMultipleResultSetsWithOneStatement() throws IOException {
        SqlSession sqlSession = MultipleResultTest.sqlSessionFactory.openSession();
        try {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            List<?> usersAndGroups = mapper.getUsersAndGroups();
            Assert.assertEquals(2, usersAndGroups.size());
        } finally {
            sqlSession.close();
        }
    }
}

