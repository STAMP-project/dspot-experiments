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
package org.apache.ibatis.submitted.missing_id_property;


import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class MissingIdPropertyTest {
    private static SqlSessionFactory sqlSessionFactory;

    @Test
    public void shouldMapResultsWithoutActuallyWritingIdProperties() throws Exception {
        SqlSession sqlSession = MissingIdPropertyTest.sqlSessionFactory.openSession();
        try {
            CarMapper carMapper = sqlSession.getMapper(CarMapper.class);
            Car car = carMapper.getCarsInfo(1L);
            Assert.assertNotNull(car.getName());
            Assert.assertNotNull(car.getCarParts());
            Assert.assertEquals(3, car.getCarParts().size());
        } finally {
            sqlSession.close();
        }
    }
}

