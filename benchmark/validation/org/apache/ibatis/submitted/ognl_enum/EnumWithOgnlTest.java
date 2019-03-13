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
package org.apache.ibatis.submitted.ognl_enum;


import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.ibatis.submitted.ognl_enum.Person.Type.DIRECTOR;


public class EnumWithOgnlTest {
    private static SqlSessionFactory sqlSessionFactory;

    @Test
    public void testEnumWithOgnl() {
        SqlSession sqlSession = EnumWithOgnlTest.sqlSessionFactory.openSession();
        PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
        List<Person> persons = personMapper.selectAllByType(null);
        Assert.assertEquals("Persons must contain 3 persons", 3, persons.size());
        sqlSession.close();
    }

    @Test
    public void testEnumWithOgnlDirector() {
        SqlSession sqlSession = EnumWithOgnlTest.sqlSessionFactory.openSession();
        PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
        List<Person> persons = personMapper.selectAllByType(DIRECTOR);
        Assert.assertEquals("Persons must contain 1 persons", 1, persons.size());
        sqlSession.close();
    }

    @Test
    public void testEnumWithOgnlDirectorNameAttribute() {
        SqlSession sqlSession = EnumWithOgnlTest.sqlSessionFactory.openSession();
        PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
        List<Person> persons = personMapper.selectAllByTypeNameAttribute(DIRECTOR);
        Assert.assertEquals("Persons must contain 1 persons", 1, persons.size());
        sqlSession.close();
    }

    @Test
    public void testEnumWithOgnlDirectorWithInterface() {
        SqlSession sqlSession = EnumWithOgnlTest.sqlSessionFactory.openSession();
        PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
        List<Person> persons = personMapper.selectAllByTypeWithInterface(new PersonMapper.PersonType() {
            public Person.Type getType() {
                return DIRECTOR;
            }
        });
        Assert.assertEquals("Persons must contain 1 persons", 1, persons.size());
        sqlSession.close();
    }

    @Test
    public void testEnumWithOgnlDirectorNameAttributeWithInterface() {
        SqlSession sqlSession = EnumWithOgnlTest.sqlSessionFactory.openSession();
        PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
        List<Person> persons = personMapper.selectAllByTypeNameAttributeWithInterface(new PersonMapper.PersonType() {
            public Person.Type getType() {
                return DIRECTOR;
            }
        });
        Assert.assertEquals("Persons must contain 1 persons", 1, persons.size());
        sqlSession.close();
    }
}

