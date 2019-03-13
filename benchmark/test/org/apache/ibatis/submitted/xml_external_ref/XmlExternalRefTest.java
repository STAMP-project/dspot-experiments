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
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.Test;


public class XmlExternalRefTest {
    @Test
    public void testCrossReferenceXmlConfig() throws Exception {
        testCrossReference(getSqlSessionFactoryXmlConfig());
    }

    @Test
    public void testCrossReferenceJavaConfig() throws Exception {
        testCrossReference(getSqlSessionFactoryJavaConfig());
    }

    @Test(expected = BuilderException.class)
    public void testFailFastOnBuildAll() throws Exception {
        Configuration configuration = new Configuration();
        try {
            configuration.addMapper(InvalidMapper.class);
        } catch (Exception e) {
            Assert.fail("No exception should be thrown before parsing statement nodes.");
        }
        configuration.getMappedStatementNames();
    }

    @Test(expected = BuilderException.class)
    public void testFailFastOnBuildAllWithInsert() throws Exception {
        Configuration configuration = new Configuration();
        try {
            configuration.addMapper(InvalidWithInsertMapper.class);
            configuration.addMapper(InvalidMapper.class);
        } catch (Exception e) {
            Assert.fail("No exception should be thrown before parsing statement nodes.");
        }
        configuration.getMappedStatementNames();
    }

    @Test
    public void testMappedStatementCache() throws Exception {
        Reader configReader = Resources.getResourceAsReader("org/apache/ibatis/submitted/xml_external_ref/MapperConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configReader);
        configReader.close();
        Configuration configuration = sqlSessionFactory.getConfiguration();
        configuration.getMappedStatementNames();
        MappedStatement selectPetStatement = configuration.getMappedStatement("org.apache.ibatis.submitted.xml_external_ref.PetMapper.select");
        MappedStatement selectPersonStatement = configuration.getMappedStatement("org.apache.ibatis.submitted.xml_external_ref.PersonMapper.select");
        Cache cache = selectPetStatement.getCache();
        Assert.assertEquals("org.apache.ibatis.submitted.xml_external_ref.PetMapper", cache.getId());
        Assert.assertSame(cache, selectPersonStatement.getCache());
    }
}

