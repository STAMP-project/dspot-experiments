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
package org.apache.ibatis.submitted.extends_with_constructor;


import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.ibatis.submitted.extends_with_constructor.StudentConstructor.Constructor.ID;
import static org.apache.ibatis.submitted.extends_with_constructor.StudentConstructor.Constructor.ID_NAME;


/* Test for NPE when using extends.

@author poitrac
 */
public class NpeExtendsTest {
    @Test
    public void testNoConstructorConfiguration() {
        Configuration configuration = new Configuration();
        configuration.addMapper(StudentMapper.class);
        configuration.addMapper(TeacherMapper.class);
        configuration.getMappedStatementNames();
    }

    @Test
    public void testWithConstructorConfiguration() {
        Configuration configuration = new Configuration();
        configuration.addMapper(StudentConstructorMapper.class);
        configuration.addMapper(TeacherMapper.class);
        configuration.getMappedStatementNames();
    }

    @Test
    public void testSelectWithTeacher() {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactoryWithConstructor();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            StudentConstructorMapper studentConstructorMapper = sqlSession.getMapper(StudentConstructorMapper.class);
            StudentConstructor testStudent = studentConstructorMapper.selectWithTeacherById(1);
            Assert.assertEquals(1, testStudent.getConstructors().size());
            Assert.assertTrue(testStudent.getConstructors().contains(ID_NAME));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSelectNoName() {
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactoryWithConstructor();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            StudentConstructorMapper studentConstructorMapper = sqlSession.getMapper(StudentConstructorMapper.class);
            StudentConstructor testStudent = studentConstructorMapper.selectNoNameById(1);
            Assert.assertEquals(1, testStudent.getConstructors().size());
            Assert.assertTrue(testStudent.getConstructors().contains(ID));
            Assert.assertNull(testStudent.getName());
        } finally {
            sqlSession.close();
        }
    }
}

