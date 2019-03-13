/**
 * Copyright 2006-2018 the original author or authors.
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
package mbg.test.mb3.annotated.miscellaneous;


import java.util.List;
import mbg.test.common.util.TestUtilities;
import mbg.test.mb3.AbstractTest;
import mbg.test.mb3.generated.annotated.miscellaneous.mapper.GeneratedalwaystestMapper;
import mbg.test.mb3.generated.annotated.miscellaneous.model.Generatedalwaystest;
import mbg.test.mb3.generated.annotated.miscellaneous.model.GeneratedalwaystestCriteria;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class GeneratedAlwaysTest extends AbstractAnnotatedMiscellaneousTest {
    @Test
    public void testInsert() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            GeneratedalwaystestMapper mapper = sqlSession.getMapper(GeneratedalwaystestMapper.class);
            Generatedalwaystest gaTest = new Generatedalwaystest();
            gaTest.setId(1);
            gaTest.setName("fred");
            gaTest.setIdPlus1(55);
            gaTest.setIdPlus2(66);
            gaTest.setBlob1(TestUtilities.generateRandomBlob());
            int rows = mapper.insert(gaTest);
            Assertions.assertEquals(1, rows);
            List<Generatedalwaystest> returnedRecords = mapper.selectByExampleWithBLOBs(null);
            Assertions.assertEquals(1, returnedRecords.size());
            Generatedalwaystest returnedRecord = returnedRecords.get(0);
            Assertions.assertEquals(1, returnedRecord.getId().intValue());
            Assertions.assertEquals(2, returnedRecord.getIdPlus1().intValue());
            Assertions.assertEquals(3, returnedRecord.getIdPlus2().intValue());
            Assertions.assertEquals("fred", returnedRecord.getName());
            Assertions.assertTrue(TestUtilities.blobsAreEqual(gaTest.getBlob1(), returnedRecord.getBlob1()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testInsertSelective() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            GeneratedalwaystestMapper mapper = sqlSession.getMapper(GeneratedalwaystestMapper.class);
            Generatedalwaystest gaTest = new Generatedalwaystest();
            gaTest.setId(1);
            gaTest.setName("fred");
            gaTest.setIdPlus1(55);
            gaTest.setIdPlus2(66);
            int rows = mapper.insert(gaTest);
            Assertions.assertEquals(1, rows);
            List<Generatedalwaystest> returnedRecords = mapper.selectByExample(null);
            Assertions.assertEquals(1, returnedRecords.size());
            Generatedalwaystest returnedRecord = returnedRecords.get(0);
            Assertions.assertEquals(1, returnedRecord.getId().intValue());
            Assertions.assertEquals(2, returnedRecord.getIdPlus1().intValue());
            Assertions.assertEquals(3, returnedRecord.getIdPlus2().intValue());
            Assertions.assertEquals("fred", returnedRecord.getName());
            Assertions.assertNull(returnedRecord.getBlob1());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testUpdateByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            GeneratedalwaystestMapper mapper = sqlSession.getMapper(GeneratedalwaystestMapper.class);
            Generatedalwaystest gaTest = new Generatedalwaystest();
            gaTest.setId(1);
            gaTest.setName("fred");
            gaTest.setIdPlus1(55);// should be ignored

            gaTest.setIdPlus2(66);// should be ignored

            gaTest.setBlob1(TestUtilities.generateRandomBlob());
            int rows = mapper.insert(gaTest);
            Assertions.assertEquals(1, rows);
            byte[] originalBlob = gaTest.getBlob1();
            gaTest.setName("barney");
            gaTest.setIdPlus1(77);// should be ignored

            gaTest.setIdPlus2(88);// should be ignored

            gaTest.setBlob1(TestUtilities.generateRandomBlob());
            GeneratedalwaystestCriteria gaCriteria = new GeneratedalwaystestCriteria();
            gaCriteria.or().andIdPlus1EqualTo(2).andIdPlus2EqualTo(3);
            rows = mapper.updateByExample(gaTest, gaCriteria);
            Assertions.assertEquals(1, rows);
            List<Generatedalwaystest> returnedRecords = mapper.selectByExampleWithBLOBs(gaCriteria);
            Assertions.assertEquals(1, returnedRecords.size());
            Generatedalwaystest returnedRecord = returnedRecords.get(0);
            Assertions.assertEquals(1, returnedRecord.getId().intValue());
            Assertions.assertEquals(2, returnedRecord.getIdPlus1().intValue());
            Assertions.assertEquals(3, returnedRecord.getIdPlus2().intValue());
            Assertions.assertEquals("barney", returnedRecord.getName());
            // should not have update the BLOB in regular update by primary key
            Assertions.assertTrue(TestUtilities.blobsAreEqual(originalBlob, returnedRecord.getBlob1()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testUpdateByExampleSelective() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            GeneratedalwaystestMapper mapper = sqlSession.getMapper(GeneratedalwaystestMapper.class);
            Generatedalwaystest gaTest = new Generatedalwaystest();
            gaTest.setId(1);
            gaTest.setName("fred");
            gaTest.setIdPlus1(55);// should be ignored

            gaTest.setIdPlus2(66);// should be ignored

            gaTest.setBlob1(TestUtilities.generateRandomBlob());
            int rows = mapper.insert(gaTest);
            Assertions.assertEquals(1, rows);
            gaTest.setName(null);
            gaTest.setIdPlus1(77);// should be ignored

            gaTest.setIdPlus2(88);// should be ignored

            gaTest.setBlob1(TestUtilities.generateRandomBlob());
            GeneratedalwaystestCriteria gaCriteria = new GeneratedalwaystestCriteria();
            gaCriteria.or().andIdPlus1EqualTo(2).andIdPlus2EqualTo(3);
            rows = mapper.updateByExampleSelective(gaTest, gaCriteria);
            Assertions.assertEquals(1, rows);
            List<Generatedalwaystest> returnedRecords = mapper.selectByExampleWithBLOBs(gaCriteria);
            Assertions.assertEquals(1, returnedRecords.size());
            Generatedalwaystest returnedRecord = returnedRecords.get(0);
            Assertions.assertEquals(1, returnedRecord.getId().intValue());
            Assertions.assertEquals(2, returnedRecord.getIdPlus1().intValue());
            Assertions.assertEquals(3, returnedRecord.getIdPlus2().intValue());
            Assertions.assertEquals("fred", returnedRecord.getName());
            Assertions.assertTrue(TestUtilities.blobsAreEqual(gaTest.getBlob1(), returnedRecord.getBlob1()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testUpdateByExampleWithBlobs() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            GeneratedalwaystestMapper mapper = sqlSession.getMapper(GeneratedalwaystestMapper.class);
            Generatedalwaystest gaTest = new Generatedalwaystest();
            gaTest.setId(1);
            gaTest.setName("fred");
            gaTest.setIdPlus1(55);// should be ignored

            gaTest.setIdPlus2(66);// should be ignored

            gaTest.setBlob1(TestUtilities.generateRandomBlob());
            int rows = mapper.insert(gaTest);
            Assertions.assertEquals(1, rows);
            gaTest.setName("barney");
            gaTest.setIdPlus1(77);// should be ignored

            gaTest.setIdPlus2(88);// should be ignored

            gaTest.setBlob1(TestUtilities.generateRandomBlob());
            GeneratedalwaystestCriteria gaCriteria = new GeneratedalwaystestCriteria();
            gaCriteria.or().andIdPlus1EqualTo(2).andIdPlus2EqualTo(3);
            rows = mapper.updateByExampleWithBLOBs(gaTest, gaCriteria);
            Assertions.assertEquals(1, rows);
            List<Generatedalwaystest> returnedRecords = mapper.selectByExampleWithBLOBs(gaCriteria);
            Assertions.assertEquals(1, returnedRecords.size());
            Generatedalwaystest returnedRecord = returnedRecords.get(0);
            Assertions.assertEquals(1, returnedRecord.getId().intValue());
            Assertions.assertEquals(2, returnedRecord.getIdPlus1().intValue());
            Assertions.assertEquals(3, returnedRecord.getIdPlus2().intValue());
            Assertions.assertEquals("barney", returnedRecord.getName());
            Assertions.assertTrue(TestUtilities.blobsAreEqual(gaTest.getBlob1(), returnedRecord.getBlob1()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testUpdateByPrimaryKey() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            GeneratedalwaystestMapper mapper = sqlSession.getMapper(GeneratedalwaystestMapper.class);
            Generatedalwaystest gaTest = new Generatedalwaystest();
            gaTest.setId(1);
            gaTest.setName("fred");
            gaTest.setIdPlus1(55);// should be ignored

            gaTest.setIdPlus2(66);// should be ignored

            gaTest.setBlob1(TestUtilities.generateRandomBlob());
            int rows = mapper.insert(gaTest);
            Assertions.assertEquals(1, rows);
            byte[] originalBlob = gaTest.getBlob1();
            gaTest.setName("barney");
            gaTest.setIdPlus1(77);// should be ignored

            gaTest.setIdPlus2(88);// should be ignored

            gaTest.setBlob1(TestUtilities.generateRandomBlob());
            rows = mapper.updateByPrimaryKey(gaTest);
            Assertions.assertEquals(1, rows);
            List<Generatedalwaystest> returnedRecords = mapper.selectByExampleWithBLOBs(null);
            Assertions.assertEquals(1, returnedRecords.size());
            Generatedalwaystest returnedRecord = returnedRecords.get(0);
            Assertions.assertEquals(1, returnedRecord.getId().intValue());
            Assertions.assertEquals(2, returnedRecord.getIdPlus1().intValue());
            Assertions.assertEquals(3, returnedRecord.getIdPlus2().intValue());
            Assertions.assertEquals("barney", returnedRecord.getName());
            // should not have update the BLOB in regular update by primary key
            Assertions.assertTrue(TestUtilities.blobsAreEqual(originalBlob, returnedRecord.getBlob1()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testUpdateByPrimaryKeySelective() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            GeneratedalwaystestMapper mapper = sqlSession.getMapper(GeneratedalwaystestMapper.class);
            Generatedalwaystest gaTest = new Generatedalwaystest();
            gaTest.setId(1);
            gaTest.setName("fred");
            gaTest.setIdPlus1(55);// should be ignored

            gaTest.setIdPlus2(66);// should be ignored

            gaTest.setBlob1(TestUtilities.generateRandomBlob());
            int rows = mapper.insert(gaTest);
            Assertions.assertEquals(1, rows);
            gaTest.setName(null);
            gaTest.setIdPlus1(77);// should be ignored

            gaTest.setIdPlus2(88);// should be ignored

            gaTest.setBlob1(TestUtilities.generateRandomBlob());
            rows = mapper.updateByPrimaryKeySelective(gaTest);
            Assertions.assertEquals(1, rows);
            List<Generatedalwaystest> returnedRecords = mapper.selectByExampleWithBLOBs(null);
            Assertions.assertEquals(1, returnedRecords.size());
            Generatedalwaystest returnedRecord = returnedRecords.get(0);
            Assertions.assertEquals(1, returnedRecord.getId().intValue());
            Assertions.assertEquals(2, returnedRecord.getIdPlus1().intValue());
            Assertions.assertEquals(3, returnedRecord.getIdPlus2().intValue());
            Assertions.assertEquals("fred", returnedRecord.getName());
            Assertions.assertTrue(TestUtilities.blobsAreEqual(gaTest.getBlob1(), returnedRecord.getBlob1()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testUpdateByPrimaryKeyWithBlobs() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            GeneratedalwaystestMapper mapper = sqlSession.getMapper(GeneratedalwaystestMapper.class);
            Generatedalwaystest gaTest = new Generatedalwaystest();
            gaTest.setId(1);
            gaTest.setName("fred");
            gaTest.setIdPlus1(55);// should be ignored

            gaTest.setIdPlus2(66);// should be ignored

            gaTest.setBlob1(TestUtilities.generateRandomBlob());
            int rows = mapper.insert(gaTest);
            Assertions.assertEquals(1, rows);
            gaTest.setName("barney");
            gaTest.setIdPlus1(77);// should be ignored

            gaTest.setIdPlus2(88);// should be ignored

            gaTest.setBlob1(TestUtilities.generateRandomBlob());
            rows = mapper.updateByPrimaryKeyWithBLOBs(gaTest);
            Assertions.assertEquals(1, rows);
            List<Generatedalwaystest> returnedRecords = mapper.selectByExampleWithBLOBs(null);
            Assertions.assertEquals(1, returnedRecords.size());
            Generatedalwaystest returnedRecord = returnedRecords.get(0);
            Assertions.assertEquals(1, returnedRecord.getId().intValue());
            Assertions.assertEquals(2, returnedRecord.getIdPlus1().intValue());
            Assertions.assertEquals(3, returnedRecord.getIdPlus2().intValue());
            Assertions.assertEquals("barney", returnedRecord.getName());
            Assertions.assertTrue(TestUtilities.blobsAreEqual(gaTest.getBlob1(), returnedRecord.getBlob1()));
        } finally {
            sqlSession.close();
        }
    }
}

