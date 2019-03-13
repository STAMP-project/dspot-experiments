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
package org.apache.ibatis.submitted.xml_external_ref;


import java.io.Reader;
import java.sql.Connection;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.Test;


public class NonFullyQualifiedNamespaceTest {
    @Test
    public void testCrossReferenceXmlConfig() throws Exception {
        Reader configReader = Resources.getResourceAsReader("org/apache/ibatis/submitted/xml_external_ref/NonFullyQualifiedNamespaceConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configReader);
        configReader.close();
        Configuration configuration = sqlSessionFactory.getConfiguration();
        MappedStatement selectPerson = configuration.getMappedStatement("person namespace.select");
        Assert.assertEquals("org/apache/ibatis/submitted/xml_external_ref/NonFullyQualifiedNamespacePersonMapper.xml", selectPerson.getResource());
        Connection conn = configuration.getEnvironment().getDataSource().getConnection();
        NonFullyQualifiedNamespaceTest.initDb(conn);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            Person person = ((Person) (sqlSession.selectOne("person namespace.select", 1)));
            Assert.assertEquals(((Integer) (1)), person.getId());
            Assert.assertEquals(2, person.getPets().size());
            Assert.assertEquals(((Integer) (2)), person.getPets().get(1).getId());
            Pet pet = ((Pet) (sqlSession.selectOne("person namespace.selectPet", 1)));
            Assert.assertEquals(Integer.valueOf(1), pet.getId());
            Pet pet2 = ((Pet) (sqlSession.selectOne("pet namespace.select", 3)));
            Assert.assertEquals(((Integer) (3)), pet2.getId());
            Assert.assertEquals(((Integer) (2)), pet2.getOwner().getId());
        } finally {
            sqlSession.close();
        }
    }
}

