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
package org.apache.ibatis.submitted.multipleresultsetswithassociation;


import java.io.IOException;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


/* This class contains tests for multiple result sets with an association mapping.
This test is based on the org.apache.ibatis.submitted.multiple_resultsets test.
 */
public class MultipleResultSetTest {
    private static SqlSessionFactory sqlSessionFactory;

    @Test
    public void shouldGetOrderDetailsEachHavingAnOrderHeader() throws IOException {
        SqlSession sqlSession = MultipleResultSetTest.sqlSessionFactory.openSession();
        try {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            List<OrderDetail> orderDetails = mapper.getOrderDetailsWithHeaders();
            // There are six order detail records in the database
            // As long as the data does not change this should be successful
            Assert.assertEquals(6, orderDetails.size());
            // Each order detail should have a corresponding OrderHeader
            // Only 2 of 6 orderDetails have orderHeaders
            for (OrderDetail orderDetail : orderDetails) {
                Assert.assertNotNull(orderDetail.getOrderHeader());
            }
        } finally {
            sqlSession.close();
        }
    }
}

