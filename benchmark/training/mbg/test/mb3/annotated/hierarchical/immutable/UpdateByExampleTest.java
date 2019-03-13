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
package mbg.test.mb3.annotated.hierarchical.immutable;


import java.util.List;
import mbg.test.mb3.AbstractTest;
import mbg.test.mb3.generated.annotated.hierarchical.Immutable.Mapper.FieldsblobsMapper;
import mbg.test.mb3.generated.annotated.hierarchical.Immutable.Mapper.FieldsonlyMapper;
import mbg.test.mb3.generated.annotated.hierarchical.Immutable.Mapper.PkblobsMapper;
import mbg.test.mb3.generated.annotated.hierarchical.Immutable.Mapper.PkfieldsMapper;
import mbg.test.mb3.generated.annotated.hierarchical.Immutable.Mapper.PkfieldsblobsMapper;
import mbg.test.mb3.generated.annotated.hierarchical.Immutable.Mapper.PkonlyMapper;
import mbg.test.mb3.generated.annotated.hierarchical.Immutable.Model.Fieldsblobs;
import mbg.test.mb3.generated.annotated.hierarchical.Immutable.Model.FieldsblobsExample;
import mbg.test.mb3.generated.annotated.hierarchical.Immutable.Model.FieldsblobsWithBLOBs;
import mbg.test.mb3.generated.annotated.hierarchical.Immutable.Model.Fieldsonly;
import mbg.test.mb3.generated.annotated.hierarchical.Immutable.Model.FieldsonlyExample;
import mbg.test.mb3.generated.annotated.hierarchical.Immutable.Model.PkblobsExample;
import mbg.test.mb3.generated.annotated.hierarchical.Immutable.Model.PkblobsKey;
import mbg.test.mb3.generated.annotated.hierarchical.Immutable.Model.PkblobsWithBLOBs;
import mbg.test.mb3.generated.annotated.hierarchical.Immutable.Model.Pkfields;
import mbg.test.mb3.generated.annotated.hierarchical.Immutable.Model.PkfieldsExample;
import mbg.test.mb3.generated.annotated.hierarchical.Immutable.Model.Pkfieldsblobs;
import mbg.test.mb3.generated.annotated.hierarchical.Immutable.Model.PkfieldsblobsExample;
import mbg.test.mb3.generated.annotated.hierarchical.Immutable.Model.PkfieldsblobsWithBLOBs;
import mbg.test.mb3.generated.annotated.hierarchical.Immutable.Model.PkonlyExample;
import mbg.test.mb3.generated.annotated.hierarchical.Immutable.Model.PkonlyKey;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Jeff Butler
 */
public class UpdateByExampleTest extends AbstractAnnotatedHierarchicalImmutableTest {
    @Test
    public void testFieldsOnlyUpdateByExampleSelective() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            FieldsonlyMapper mapper = sqlSession.getMapper(FieldsonlyMapper.class);
            Fieldsonly record = new Fieldsonly(5, 11.22, 33.44);
            mapper.insert(record);
            record = new Fieldsonly(8, 44.55, 66.77);
            mapper.insert(record);
            record = new Fieldsonly(9, 88.99, 100.111);
            mapper.insert(record);
            record = new Fieldsonly(null, 99.0, null);
            FieldsonlyExample example = new FieldsonlyExample();
            example.createCriteria().andIntegerfieldGreaterThan(5);
            int rows = mapper.updateByExampleSelective(record, example);
            Assertions.assertEquals(2, rows);
            example.clear();
            example.createCriteria().andIntegerfieldEqualTo(5);
            List<Fieldsonly> answer = mapper.selectByExample(example);
            Assertions.assertEquals(1, answer.size());
            record = answer.get(0);
            Assertions.assertEquals(record.getDoublefield(), 11.22, 0.001);
            Assertions.assertEquals(record.getFloatfield(), 33.44, 0.001);
            Assertions.assertEquals(record.getIntegerfield().intValue(), 5);
            example.clear();
            example.createCriteria().andIntegerfieldEqualTo(8);
            answer = mapper.selectByExample(example);
            Assertions.assertEquals(1, answer.size());
            record = answer.get(0);
            Assertions.assertEquals(record.getDoublefield(), 99.0, 0.001);
            Assertions.assertEquals(record.getFloatfield(), 66.77, 0.001);
            Assertions.assertEquals(record.getIntegerfield().intValue(), 8);
            example.clear();
            example.createCriteria().andIntegerfieldEqualTo(9);
            answer = mapper.selectByExample(example);
            Assertions.assertEquals(1, answer.size());
            record = answer.get(0);
            Assertions.assertEquals(record.getDoublefield(), 99.0, 0.001);
            Assertions.assertEquals(record.getFloatfield(), 100.111, 0.001);
            Assertions.assertEquals(record.getIntegerfield().intValue(), 9);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testFieldsOnlyUpdateByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            FieldsonlyMapper mapper = sqlSession.getMapper(FieldsonlyMapper.class);
            Fieldsonly record = new Fieldsonly(5, 11.22, 33.44);
            mapper.insert(record);
            record = new Fieldsonly(8, 44.55, 66.77);
            mapper.insert(record);
            record = new Fieldsonly(9, 88.99, 100.111);
            mapper.insert(record);
            record = new Fieldsonly(22, null, null);
            FieldsonlyExample example = new FieldsonlyExample();
            example.createCriteria().andIntegerfieldEqualTo(5);
            int rows = mapper.updateByExample(record, example);
            Assertions.assertEquals(1, rows);
            example.clear();
            example.createCriteria().andIntegerfieldEqualTo(22);
            List<Fieldsonly> answer = mapper.selectByExample(example);
            Assertions.assertEquals(1, answer.size());
            record = answer.get(0);
            Assertions.assertNull(record.getDoublefield());
            Assertions.assertNull(record.getFloatfield());
            Assertions.assertEquals(record.getIntegerfield().intValue(), 22);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKOnlyUpdateByExampleSelective() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkonlyMapper mapper = sqlSession.getMapper(PkonlyMapper.class);
            PkonlyKey key = new PkonlyKey(1, 3);
            mapper.insert(key);
            key = new PkonlyKey(5, 6);
            mapper.insert(key);
            key = new PkonlyKey(7, 8);
            mapper.insert(key);
            PkonlyExample example = new PkonlyExample();
            example.createCriteria().andIdGreaterThan(4);
            key = new PkonlyKey(null, 3);
            int rows = mapper.updateByExampleSelective(key, example);
            Assertions.assertEquals(2, rows);
            example.clear();
            example.createCriteria().andIdEqualTo(5).andSeqNumEqualTo(3);
            long returnedRows = mapper.countByExample(example);
            Assertions.assertEquals(1, returnedRows);
            example.clear();
            example.createCriteria().andIdEqualTo(7).andSeqNumEqualTo(3);
            returnedRows = mapper.countByExample(example);
            Assertions.assertEquals(1, returnedRows);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKOnlyUpdateByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkonlyMapper mapper = sqlSession.getMapper(PkonlyMapper.class);
            PkonlyKey key = new PkonlyKey(1, 3);
            mapper.insert(key);
            key = new PkonlyKey(5, 6);
            mapper.insert(key);
            key = new PkonlyKey(7, 8);
            mapper.insert(key);
            PkonlyExample example = new PkonlyExample();
            example.createCriteria().andIdEqualTo(7);
            key = new PkonlyKey(22, 3);
            int rows = mapper.updateByExample(key, example);
            Assertions.assertEquals(1, rows);
            example.clear();
            example.createCriteria().andIdEqualTo(22).andSeqNumEqualTo(3);
            long returnedRows = mapper.countByExample(example);
            Assertions.assertEquals(1, returnedRows);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsUpdateByExampleSelective() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = new Pkfields();
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Bob");
            record.setLastname("Jones");
            record.setId1(3);
            record.setId2(4);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Fred");
            PkfieldsExample example = new PkfieldsExample();
            example.createCriteria().andLastnameLike("J%");
            int rows = mapper.updateByExampleSelective(record, example);
            Assertions.assertEquals(1, rows);
            example.clear();
            example.createCriteria().andFirstnameEqualTo("Fred").andLastnameEqualTo("Jones").andId1EqualTo(3).andId2EqualTo(4);
            long returnedRows = mapper.countByExample(example);
            Assertions.assertEquals(1, returnedRows);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsUpdateByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsMapper mapper = sqlSession.getMapper(PkfieldsMapper.class);
            Pkfields record = new Pkfields();
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Bob");
            record.setLastname("Jones");
            record.setId1(3);
            record.setId2(4);
            mapper.insert(record);
            record = new Pkfields();
            record.setFirstname("Fred");
            record.setId1(3);
            record.setId2(4);
            PkfieldsExample example = new PkfieldsExample();
            example.createCriteria().andId1EqualTo(3).andId2EqualTo(4);
            int rows = mapper.updateByExample(record, example);
            Assertions.assertEquals(1, rows);
            example.clear();
            example.createCriteria().andFirstnameEqualTo("Fred").andLastnameIsNull().andId1EqualTo(3).andId2EqualTo(4);
            long returnedRows = mapper.countByExample(example);
            Assertions.assertEquals(1, returnedRows);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKBlobsUpdateByExampleSelective() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            PkblobsWithBLOBs record = new PkblobsWithBLOBs(3, generateRandomBlob(), generateRandomBlob(), "Long String 1");
            mapper.insert(record);
            record = new PkblobsWithBLOBs(6, generateRandomBlob(), generateRandomBlob(), "Long String 2");
            mapper.insert(record);
            PkblobsWithBLOBs newRecord = new PkblobsWithBLOBs(null, generateRandomBlob(), null, null);
            PkblobsExample example = new PkblobsExample();
            example.createCriteria().andIdGreaterThan(4);
            int rows = mapper.updateByExampleSelective(newRecord, example);
            Assertions.assertEquals(1, rows);
            List<PkblobsWithBLOBs> answer = mapper.selectByExampleWithBLOBs(example);
            Assertions.assertEquals(1, answer.size());
            PkblobsWithBLOBs returnedRecord = answer.get(0);
            Assertions.assertEquals(6, returnedRecord.getId().intValue());
            Assertions.assertTrue(blobsAreEqual(newRecord.getBlob1(), returnedRecord.getBlob1()));
            Assertions.assertTrue(blobsAreEqual(record.getBlob2(), returnedRecord.getBlob2()));
            Assertions.assertEquals(record.getCharacterlob(), returnedRecord.getCharacterlob());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKBlobsUpdateByExampleWithoutBLOBs() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            PkblobsWithBLOBs record = new PkblobsWithBLOBs(3, generateRandomBlob(), generateRandomBlob(), "Long String 1");
            mapper.insert(record);
            record = new PkblobsWithBLOBs(6, generateRandomBlob(), generateRandomBlob(), "Long String 2");
            mapper.insert(record);
            PkblobsKey newRecord = new PkblobsKey(8);
            PkblobsExample example = new PkblobsExample();
            example.createCriteria().andIdGreaterThan(4);
            int rows = mapper.updateByExample(newRecord, example);
            Assertions.assertEquals(1, rows);
            List<PkblobsWithBLOBs> answer = mapper.selectByExampleWithBLOBs(example);
            Assertions.assertEquals(1, answer.size());
            PkblobsWithBLOBs returnedRecord = answer.get(0);
            Assertions.assertEquals(8, returnedRecord.getId().intValue());
            Assertions.assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));
            Assertions.assertTrue(blobsAreEqual(record.getBlob2(), returnedRecord.getBlob2()));
            Assertions.assertEquals(record.getCharacterlob(), returnedRecord.getCharacterlob());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKBlobsUpdateByExampleWithBLOBs() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            PkblobsWithBLOBs record = new PkblobsWithBLOBs(3, generateRandomBlob(), generateRandomBlob(), "Long String 1");
            mapper.insert(record);
            record = new PkblobsWithBLOBs(6, generateRandomBlob(), generateRandomBlob(), "Long String 2");
            mapper.insert(record);
            PkblobsWithBLOBs newRecord = new PkblobsWithBLOBs(8, null, null, null);
            PkblobsExample example = new PkblobsExample();
            example.createCriteria().andIdGreaterThan(4);
            int rows = mapper.updateByExampleWithBLOBs(newRecord, example);
            Assertions.assertEquals(1, rows);
            List<PkblobsWithBLOBs> answer = mapper.selectByExampleWithBLOBs(example);
            Assertions.assertEquals(1, answer.size());
            PkblobsWithBLOBs returnedRecord = answer.get(0);
            Assertions.assertEquals(8, returnedRecord.getId().intValue());
            Assertions.assertNull(returnedRecord.getBlob1());
            Assertions.assertNull(returnedRecord.getBlob2());
            Assertions.assertNull(returnedRecord.getCharacterlob());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsBlobsUpdateByExampleSelective() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            PkfieldsblobsWithBLOBs record = new PkfieldsblobsWithBLOBs(3, 4, "Jeff", "Smith", generateRandomBlob());
            mapper.insert(record);
            record = new PkfieldsblobsWithBLOBs(5, 6, "Scott", "Jones", generateRandomBlob());
            mapper.insert(record);
            PkfieldsblobsWithBLOBs newRecord = new PkfieldsblobsWithBLOBs(null, null, "Fred", null, null);
            PkfieldsblobsExample example = new PkfieldsblobsExample();
            example.createCriteria().andId1NotEqualTo(3);
            int rows = mapper.updateByExampleSelective(newRecord, example);
            Assertions.assertEquals(1, rows);
            List<PkfieldsblobsWithBLOBs> answer = mapper.selectByExampleWithBLOBs(example);
            Assertions.assertEquals(1, answer.size());
            PkfieldsblobsWithBLOBs returnedRecord = answer.get(0);
            Assertions.assertEquals(record.getId1(), returnedRecord.getId1());
            Assertions.assertEquals(record.getId2(), returnedRecord.getId2());
            Assertions.assertEquals(newRecord.getFirstname(), returnedRecord.getFirstname());
            Assertions.assertEquals(record.getLastname(), returnedRecord.getLastname());
            Assertions.assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsBlobsUpdateByExampleWithoutBLOBs() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            PkfieldsblobsWithBLOBs record = new PkfieldsblobsWithBLOBs(3, 4, "Jeff", "Smith", generateRandomBlob());
            mapper.insert(record);
            record = new PkfieldsblobsWithBLOBs(5, 6, "Scott", "Jones", generateRandomBlob());
            mapper.insert(record);
            Pkfieldsblobs newRecord = new Pkfieldsblobs(5, 8, "Fred", null);
            PkfieldsblobsExample example = new PkfieldsblobsExample();
            example.createCriteria().andId1EqualTo(5);
            int rows = mapper.updateByExample(newRecord, example);
            Assertions.assertEquals(1, rows);
            List<PkfieldsblobsWithBLOBs> answer = mapper.selectByExampleWithBLOBs(example);
            Assertions.assertEquals(1, answer.size());
            PkfieldsblobsWithBLOBs returnedRecord = answer.get(0);
            Assertions.assertEquals(newRecord.getId1(), returnedRecord.getId1());
            Assertions.assertEquals(newRecord.getId2(), returnedRecord.getId2());
            Assertions.assertEquals(newRecord.getFirstname(), returnedRecord.getFirstname());
            Assertions.assertNull(returnedRecord.getLastname());
            Assertions.assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsBlobsUpdateByExampleWithBLOBs() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            PkfieldsblobsWithBLOBs record = new PkfieldsblobsWithBLOBs(3, 4, "Jeff", "Smith", generateRandomBlob());
            mapper.insert(record);
            record = new PkfieldsblobsWithBLOBs(5, 6, "Scott", "Jones", generateRandomBlob());
            mapper.insert(record);
            PkfieldsblobsWithBLOBs newRecord = new PkfieldsblobsWithBLOBs(3, 8, "Fred", null, null);
            PkfieldsblobsExample example = new PkfieldsblobsExample();
            example.createCriteria().andId1EqualTo(3);
            int rows = mapper.updateByExampleWithBLOBs(newRecord, example);
            Assertions.assertEquals(1, rows);
            List<PkfieldsblobsWithBLOBs> answer = mapper.selectByExampleWithBLOBs(example);
            Assertions.assertEquals(1, answer.size());
            PkfieldsblobsWithBLOBs returnedRecord = answer.get(0);
            Assertions.assertEquals(newRecord.getId1(), returnedRecord.getId1());
            Assertions.assertEquals(newRecord.getId2(), returnedRecord.getId2());
            Assertions.assertEquals(newRecord.getFirstname(), returnedRecord.getFirstname());
            Assertions.assertNull(returnedRecord.getLastname());
            Assertions.assertNull(returnedRecord.getBlob1());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testFieldsBlobsUpdateByExampleSelective() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            FieldsblobsMapper mapper = sqlSession.getMapper(FieldsblobsMapper.class);
            FieldsblobsWithBLOBs record = new FieldsblobsWithBLOBs("Jeff", "Smith", generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);
            record = new FieldsblobsWithBLOBs("Scott", "Jones", generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);
            FieldsblobsWithBLOBs newRecord = new FieldsblobsWithBLOBs(null, "Doe", null, null, null);
            FieldsblobsExample example = new FieldsblobsExample();
            example.createCriteria().andFirstnameLike("S%");
            int rows = mapper.updateByExampleSelective(newRecord, example);
            Assertions.assertEquals(1, rows);
            List<FieldsblobsWithBLOBs> answer = mapper.selectByExampleWithBLOBs(example);
            Assertions.assertEquals(1, answer.size());
            FieldsblobsWithBLOBs returnedRecord = answer.get(0);
            Assertions.assertEquals(record.getFirstname(), returnedRecord.getFirstname());
            Assertions.assertEquals(newRecord.getLastname(), returnedRecord.getLastname());
            Assertions.assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));
            Assertions.assertTrue(blobsAreEqual(record.getBlob2(), returnedRecord.getBlob2()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testFieldsBlobsUpdateByExampleWithoutBLOBs() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            FieldsblobsMapper mapper = sqlSession.getMapper(FieldsblobsMapper.class);
            FieldsblobsWithBLOBs record = new FieldsblobsWithBLOBs("Jeff", "Smith", generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);
            record = new FieldsblobsWithBLOBs("Scott", "Jones", generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);
            Fieldsblobs newRecord = new Fieldsblobs("Scott", "Doe");
            FieldsblobsExample example = new FieldsblobsExample();
            example.createCriteria().andFirstnameLike("S%");
            int rows = mapper.updateByExample(newRecord, example);
            Assertions.assertEquals(1, rows);
            List<FieldsblobsWithBLOBs> answer = mapper.selectByExampleWithBLOBs(example);
            Assertions.assertEquals(1, answer.size());
            FieldsblobsWithBLOBs returnedRecord = answer.get(0);
            Assertions.assertEquals(newRecord.getFirstname(), returnedRecord.getFirstname());
            Assertions.assertEquals(newRecord.getLastname(), returnedRecord.getLastname());
            Assertions.assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));
            Assertions.assertTrue(blobsAreEqual(record.getBlob2(), returnedRecord.getBlob2()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testFieldsBlobsUpdateByExampleWithBLOBs() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            FieldsblobsMapper mapper = sqlSession.getMapper(FieldsblobsMapper.class);
            FieldsblobsWithBLOBs record = new FieldsblobsWithBLOBs("Jeff", "Smith", generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);
            record = new FieldsblobsWithBLOBs("Scott", "Jones", generateRandomBlob(), generateRandomBlob(), null);
            mapper.insert(record);
            FieldsblobsWithBLOBs newRecord = new FieldsblobsWithBLOBs("Scott", "Doe", null, null, null);
            FieldsblobsExample example = new FieldsblobsExample();
            example.createCriteria().andFirstnameLike("S%");
            int rows = mapper.updateByExampleWithBLOBs(newRecord, example);
            Assertions.assertEquals(1, rows);
            List<FieldsblobsWithBLOBs> answer = mapper.selectByExampleWithBLOBs(example);
            Assertions.assertEquals(1, answer.size());
            FieldsblobsWithBLOBs returnedRecord = answer.get(0);
            Assertions.assertEquals(newRecord.getFirstname(), returnedRecord.getFirstname());
            Assertions.assertEquals(newRecord.getLastname(), returnedRecord.getLastname());
            Assertions.assertNull(returnedRecord.getBlob1());
            Assertions.assertNull(returnedRecord.getBlob2());
        } finally {
            sqlSession.close();
        }
    }
}

