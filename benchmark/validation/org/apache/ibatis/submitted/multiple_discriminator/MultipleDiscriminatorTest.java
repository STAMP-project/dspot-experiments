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
package org.apache.ibatis.submitted.multiple_discriminator;


import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class MultipleDiscriminatorTest {
    private static SqlSessionFactory sqlSessionFactory;

    @Test
    public void testMultipleDiscriminator() {
        SqlSession sqlSession = MultipleDiscriminatorTest.sqlSessionFactory.openSession();
        PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
        Person person = personMapper.get(1L);
        Assert.assertNotNull("Person must not be null", person);
        Assert.assertEquals("Person must be a director", Director.class, person.getClass());
        sqlSession.close();
    }

    @Test
    public void testMultipleDiscriminator2() {
        SqlSession sqlSession = MultipleDiscriminatorTest.sqlSessionFactory.openSession();
        PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
        Person person = personMapper.get2(1L);
        Assert.assertNotNull("Person must not be null", person);
        Assert.assertEquals("Person must be a director", Director.class, person.getClass());
        sqlSession.close();
    }

    @Test(timeout = 20000)
    public void testMultipleDiscriminatorLoop() {
        SqlSession sqlSession = MultipleDiscriminatorTest.sqlSessionFactory.openSession();
        PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
        personMapper.getLoop();
        sqlSession.close();
    }
}

