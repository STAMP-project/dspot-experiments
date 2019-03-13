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
package org.apache.ibatis.submitted.blobtest;


import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class BlobTest {
    private static SqlSessionFactory sqlSessionFactory;

    /* This test demonstrates the use of type aliases for primitive types
    in constructor based result maps
     */
    @Test
    public void testInsertBlobThenSelectAll() {
        SqlSession sqlSession = BlobTest.sqlSessionFactory.openSession();
        try {
            BlobMapper blobMapper = sqlSession.getMapper(BlobMapper.class);
            byte[] myblob = new byte[]{ 1, 2, 3, 4, 5 };
            BlobRecord blobRecord = new BlobRecord(1, myblob);
            int rows = blobMapper.insert(blobRecord);
            Assert.assertEquals(1, rows);
            // NPE here due to unresolved type handler
            List<BlobRecord> results = blobMapper.selectAll();
            Assert.assertEquals(1, results.size());
            BlobRecord result = results.get(0);
            Assert.assertEquals(blobRecord.getId(), result.getId());
            Assert.assertTrue(BlobTest.blobsAreEqual(blobRecord.getBlob(), result.getBlob()));
        } finally {
            sqlSession.close();
        }
    }

    /* This test demonstrates the use of type aliases for primitive types
    in constructor based result maps
     */
    @Test
    public void testInsertBlobObjectsThenSelectAll() {
        SqlSession sqlSession = BlobTest.sqlSessionFactory.openSession();
        try {
            BlobMapper blobMapper = sqlSession.getMapper(BlobMapper.class);
            Byte[] myblob = new Byte[]{ 1, 2, 3, 4, 5 };
            BlobRecord blobRecord = new BlobRecord(1, myblob);
            int rows = blobMapper.insert(blobRecord);
            Assert.assertEquals(1, rows);
            // NPE here due to unresolved type handler
            List<BlobRecord> results = blobMapper.selectAllWithBlobObjects();
            Assert.assertEquals(1, results.size());
            BlobRecord result = results.get(0);
            Assert.assertEquals(blobRecord.getId(), result.getId());
            Assert.assertTrue(BlobTest.blobsAreEqual(blobRecord.getBlob(), result.getBlob()));
        } finally {
            sqlSession.close();
        }
    }
}

