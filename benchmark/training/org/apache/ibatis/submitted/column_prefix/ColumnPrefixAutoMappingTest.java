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
package org.apache.ibatis.submitted.column_prefix;


import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;


public class ColumnPrefixAutoMappingTest extends ColumnPrefixTest {
    @Test
    public void testCaseInsensitivity() throws Exception {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            Brand brand = sqlSession.selectOne("org.apache.ibatis.submitted.column_prefix.MapperAutoMapping.selectBrandWithProducts", 1);
            Assert.assertEquals(Integer.valueOf(1), brand.getId());
            Assert.assertEquals(2, brand.getProducts().size());
            Assert.assertEquals(Integer.valueOf(10), brand.getProducts().get(0).getId());
            Assert.assertEquals("alpha", brand.getProducts().get(0).getName());
        } finally {
            sqlSession.close();
        }
    }
}

