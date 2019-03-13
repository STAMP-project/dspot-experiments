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
package mbg.test.mb3.dsql;


import awfulTable.customerId;
import awfulTable.eMail;
import fieldsonly.integerfield;
import java.util.List;
import mbg.test.mb3.generated.dsql.mapper.AwfulTableMapper;
import mbg.test.mb3.generated.dsql.mapper.FieldsblobsMapper;
import mbg.test.mb3.generated.dsql.mapper.FieldsonlyMapper;
import mbg.test.mb3.generated.dsql.mapper.PkblobsMapper;
import mbg.test.mb3.generated.dsql.mapper.PkfieldsMapper;
import mbg.test.mb3.generated.dsql.mapper.PkfieldsblobsMapper;
import mbg.test.mb3.generated.dsql.mapper.PkonlyMapper;
import mbg.test.mb3.generated.dsql.model.AwfulTable;
import mbg.test.mb3.generated.dsql.model.Fieldsblobs;
import mbg.test.mb3.generated.dsql.model.Fieldsonly;
import mbg.test.mb3.generated.dsql.model.Pkblobs;
import mbg.test.mb3.generated.dsql.model.Pkfields;
import mbg.test.mb3.generated.dsql.model.Pkfieldsblobs;
import mbg.test.mb3.generated.dsql.model.Pkonly;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pkfields.firstname;
import pkfields.id1;
import pkfields.id2;
import pkfields.lastname;
import pkonly.id;
import pkonly.seqNum;


/**
 *
 *
 * @author Jeff Butler
 */
public class UpdateByExampleTest extends AbstractTest {
    @Test
    public void testFieldsOnlyUpdateByExampleSelective() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            FieldsonlyMapper mapper = sqlSession.getMapper(FieldsonlyMapper.class);
            Fieldsonly record = new Fieldsonly();
            record.setDoublefield(11.22);
            record.setFloatfield(33.44);
            record.setIntegerfield(5);
            mapper.insert(record);
            record = new Fieldsonly();
            record.setDoublefield(44.55);
            record.setFloatfield(66.77);
            record.setIntegerfield(8);
            mapper.insert(record);
            record = new Fieldsonly();
            record.setDoublefield(88.99);
            record.setFloatfield(100.111);
            record.setIntegerfield(9);
            mapper.insert(record);
            record = new Fieldsonly();
            record.setDoublefield(99.0);
            int rows = mapper.updateByExampleSelective(record).where(integerfield, isGreaterThan(5)).build().execute();
            Assertions.assertEquals(2, rows);
            List<Fieldsonly> answer = mapper.selectByExample().where(integerfield, isEqualTo(5)).build().execute();
            Assertions.assertEquals(1, answer.size());
            record = answer.get(0);
            Assertions.assertEquals(record.getDoublefield(), 11.22, 0.001);
            Assertions.assertEquals(record.getFloatfield(), 33.44, 0.001);
            Assertions.assertEquals(record.getIntegerfield().intValue(), 5);
            answer = mapper.selectByExample().where(integerfield, isEqualTo(8)).build().execute();
            Assertions.assertEquals(1, answer.size());
            record = answer.get(0);
            Assertions.assertEquals(record.getDoublefield(), 99.0, 0.001);
            Assertions.assertEquals(record.getFloatfield(), 66.77, 0.001);
            Assertions.assertEquals(record.getIntegerfield().intValue(), 8);
            answer = mapper.selectByExample().where(integerfield, isEqualTo(9)).build().execute();
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
            Fieldsonly record = new Fieldsonly();
            record.setDoublefield(11.22);
            record.setFloatfield(33.44);
            record.setIntegerfield(5);
            mapper.insert(record);
            record = new Fieldsonly();
            record.setDoublefield(44.55);
            record.setFloatfield(66.77);
            record.setIntegerfield(8);
            mapper.insert(record);
            record = new Fieldsonly();
            record.setDoublefield(88.99);
            record.setFloatfield(100.111);
            record.setIntegerfield(9);
            mapper.insert(record);
            record = new Fieldsonly();
            record.setIntegerfield(22);
            int rows = mapper.updateByExample(record).where(integerfield, isEqualTo(5)).build().execute();
            Assertions.assertEquals(1, rows);
            List<Fieldsonly> answer = mapper.selectByExample().where(integerfield, isEqualTo(22)).build().execute();
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
            Pkonly key = new Pkonly(1, 3);
            mapper.insert(key);
            key = new Pkonly(5, 6);
            mapper.insert(key);
            key = new Pkonly(7, 8);
            mapper.insert(key);
            key = new Pkonly(null, 3);
            int rows = mapper.updateByExampleSelective(key).where(id, isGreaterThan(4)).build().execute();
            Assertions.assertEquals(2, rows);
            long returnedRows = mapper.countByExample().where(id, isEqualTo(5)).and(seqNum, isEqualTo(3)).build().execute();
            Assertions.assertEquals(1, returnedRows);
            returnedRows = mapper.countByExample().where(id, isEqualTo(7)).and(seqNum, isEqualTo(3)).build().execute();
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
            Pkonly key = new Pkonly(1, 3);
            mapper.insert(key);
            key = new Pkonly(5, 6);
            mapper.insert(key);
            key = new Pkonly(7, 8);
            mapper.insert(key);
            key = new Pkonly(22, 3);
            int rows = mapper.updateByExample(key).where(id, isEqualTo(7)).build().execute();
            Assertions.assertEquals(1, rows);
            long returnedRows = mapper.countByExample().where(id, isEqualTo(22)).and(seqNum, isEqualTo(3)).build().execute();
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
            int rows = mapper.updateByExampleSelective(record).where(lastname, isLike("J%")).build().execute();
            Assertions.assertEquals(1, rows);
            long returnedRows = mapper.countByExample().where(firstname, isEqualTo("Fred")).and(lastname, isEqualTo("Jones")).and(id1, isEqualTo(3)).and(id2, isEqualTo(4)).build().execute();
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
            int rows = mapper.updateByExample(record).where(id1, isEqualTo(3)).and(id2, isEqualTo(4)).build().execute();
            Assertions.assertEquals(1, rows);
            long returnedRows = mapper.countByExample().where(firstname, isEqualTo("Fred")).and(id1, isEqualTo(3)).and(id2, isEqualTo(4)).build().execute();
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
            Pkblobs record = new Pkblobs();
            record.setId(3);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            record = new Pkblobs();
            record.setId(6);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            Pkblobs newRecord = new Pkblobs();
            newRecord.setBlob1(generateRandomBlob());
            int rows = mapper.updateByExampleSelective(newRecord).where(pkblobs.id, isGreaterThan(4)).build().execute();
            Assertions.assertEquals(1, rows);
            List<Pkblobs> answer = mapper.selectByExample().where(pkblobs.id, isGreaterThan(4)).build().execute();
            Assertions.assertEquals(1, answer.size());
            Pkblobs returnedRecord = answer.get(0);
            Assertions.assertEquals(6, returnedRecord.getId().intValue());
            Assertions.assertTrue(blobsAreEqual(newRecord.getBlob1(), returnedRecord.getBlob1()));
            Assertions.assertTrue(blobsAreEqual(record.getBlob2(), returnedRecord.getBlob2()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKBlobsUpdateByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkblobsMapper mapper = sqlSession.getMapper(PkblobsMapper.class);
            Pkblobs record = new Pkblobs();
            record.setId(3);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            record = new Pkblobs();
            record.setId(6);
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            Pkblobs newRecord = new Pkblobs();
            newRecord.setId(8);
            int rows = mapper.updateByExample(newRecord).where(pkblobs.id, isGreaterThan(4)).build().execute();
            Assertions.assertEquals(1, rows);
            List<Pkblobs> answer = mapper.selectByExample().where(pkblobs.id, isGreaterThan(4)).build().execute();
            Assertions.assertEquals(1, answer.size());
            Pkblobs returnedRecord = answer.get(0);
            Assertions.assertEquals(8, returnedRecord.getId().intValue());
            Assertions.assertNull(returnedRecord.getBlob1());
            Assertions.assertNull(returnedRecord.getBlob2());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testPKFieldsBlobsUpdateByExampleSelective() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs();
            record.setId1(3);
            record.setId2(4);
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);
            record = new Pkfieldsblobs();
            record.setId1(5);
            record.setId2(6);
            record.setFirstname("Scott");
            record.setLastname("Jones");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);
            Pkfieldsblobs newRecord = new Pkfieldsblobs();
            newRecord.setFirstname("Fred");
            int rows = mapper.updateByExampleSelective(newRecord).where(pkfieldsblobs.id1, isNotEqualTo(3)).build().execute();
            Assertions.assertEquals(1, rows);
            List<Pkfieldsblobs> answer = mapper.selectByExample().where(pkfieldsblobs.id1, isNotEqualTo(3)).build().execute();
            Assertions.assertEquals(1, answer.size());
            Pkfieldsblobs returnedRecord = answer.get(0);
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
    public void testPKFieldsBlobsUpdateByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PkfieldsblobsMapper mapper = sqlSession.getMapper(PkfieldsblobsMapper.class);
            Pkfieldsblobs record = new Pkfieldsblobs();
            record.setId1(3);
            record.setId2(4);
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);
            record = new Pkfieldsblobs();
            record.setId1(5);
            record.setId2(6);
            record.setFirstname("Scott");
            record.setLastname("Jones");
            record.setBlob1(generateRandomBlob());
            mapper.insert(record);
            Pkfieldsblobs newRecord = new Pkfieldsblobs();
            newRecord.setId1(3);
            newRecord.setId2(8);
            newRecord.setFirstname("Fred");
            int rows = mapper.updateByExample(newRecord).where(pkfieldsblobs.id1, isEqualTo(3)).build().execute();
            Assertions.assertEquals(1, rows);
            List<Pkfieldsblobs> answer = mapper.selectByExample().where(pkfieldsblobs.id1, isEqualTo(3)).build().execute();
            Assertions.assertEquals(1, answer.size());
            Pkfieldsblobs returnedRecord = answer.get(0);
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
            Fieldsblobs record = new Fieldsblobs();
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            record = new Fieldsblobs();
            record.setFirstname("Scott");
            record.setLastname("Jones");
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            Fieldsblobs newRecord = new Fieldsblobs();
            newRecord.setLastname("Doe");
            int rows = mapper.updateByExampleSelective(newRecord).where(fieldsblobs.firstname, isLike("S%")).build().execute();
            Assertions.assertEquals(1, rows);
            List<Fieldsblobs> answer = mapper.selectByExample().where(fieldsblobs.firstname, isLike("S%")).build().execute();
            Assertions.assertEquals(1, answer.size());
            Fieldsblobs returnedRecord = answer.get(0);
            Assertions.assertEquals(record.getFirstname(), returnedRecord.getFirstname());
            Assertions.assertEquals(newRecord.getLastname(), returnedRecord.getLastname());
            Assertions.assertTrue(blobsAreEqual(record.getBlob1(), returnedRecord.getBlob1()));
            Assertions.assertTrue(blobsAreEqual(record.getBlob2(), returnedRecord.getBlob2()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testFieldsBlobsUpdateByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            FieldsblobsMapper mapper = sqlSession.getMapper(FieldsblobsMapper.class);
            Fieldsblobs record = new Fieldsblobs();
            record.setFirstname("Jeff");
            record.setLastname("Smith");
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            record = new Fieldsblobs();
            record.setFirstname("Scott");
            record.setLastname("Jones");
            record.setBlob1(generateRandomBlob());
            record.setBlob2(generateRandomBlob());
            mapper.insert(record);
            Fieldsblobs newRecord = new Fieldsblobs();
            newRecord.setFirstname("Scott");
            newRecord.setLastname("Doe");
            int rows = mapper.updateByExample(newRecord).where(fieldsblobs.firstname, isLike("S%")).build().execute();
            Assertions.assertEquals(1, rows);
            List<Fieldsblobs> answer = mapper.selectByExample().where(fieldsblobs.firstname, isLike("S%")).build().execute();
            Assertions.assertEquals(1, answer.size());
            Fieldsblobs returnedRecord = answer.get(0);
            Assertions.assertEquals(newRecord.getFirstname(), returnedRecord.getFirstname());
            Assertions.assertEquals(newRecord.getLastname(), returnedRecord.getLastname());
            Assertions.assertNull(returnedRecord.getBlob1());
            Assertions.assertNull(returnedRecord.getBlob2());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testAwfulTableUpdateByExampleSelective() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            AwfulTableMapper mapper = sqlSession.getMapper(AwfulTableMapper.class);
            AwfulTable record = new AwfulTable();
            record.seteMail("fred@fred.com");
            record.setEmailaddress("alsofred@fred.com");
            record.setFirstFirstName("fred1");
            record.setFrom("from field");
            record.setId1(1);
            record.setId2(2);
            record.setId5(5);
            record.setId6(6);
            record.setId7(7);
            record.setSecondFirstName("fred2");
            record.setThirdFirstName("fred3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("fred2@fred.com");
            record.setEmailaddress("alsofred2@fred.com");
            record.setFirstFirstName("fred11");
            record.setFrom("from from field");
            record.setId1(11);
            record.setId2(22);
            record.setId5(55);
            record.setId6(66);
            record.setId7(77);
            record.setSecondFirstName("fred22");
            record.setThirdFirstName("fred33");
            mapper.insert(record);
            AwfulTable newRecord = new AwfulTable();
            newRecord.setFirstFirstName("Alonzo");
            int rows = mapper.updateByExampleSelective(newRecord).where(eMail, isLike("fred2@%")).build().execute();
            Assertions.assertEquals(1, rows);
            List<AwfulTable> answer = mapper.selectByExample().where(eMail, isLike("fred2@%")).build().execute();
            Assertions.assertEquals(1, answer.size());
            AwfulTable returnedRecord = answer.get(0);
            Assertions.assertEquals(record.getCustomerId(), returnedRecord.getCustomerId());
            Assertions.assertEquals(record.geteMail(), returnedRecord.geteMail());
            Assertions.assertEquals(record.getEmailaddress(), returnedRecord.getEmailaddress());
            Assertions.assertEquals(newRecord.getFirstFirstName(), returnedRecord.getFirstFirstName());
            Assertions.assertEquals(record.getFrom(), returnedRecord.getFrom());
            Assertions.assertEquals(record.getId1(), returnedRecord.getId1());
            Assertions.assertEquals(record.getId2(), returnedRecord.getId2());
            Assertions.assertEquals(record.getId5(), returnedRecord.getId5());
            Assertions.assertEquals(record.getId6(), returnedRecord.getId6());
            Assertions.assertEquals(record.getId7(), returnedRecord.getId7());
            Assertions.assertEquals(record.getSecondFirstName(), returnedRecord.getSecondFirstName());
            Assertions.assertEquals(record.getThirdFirstName(), returnedRecord.getThirdFirstName());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testAwfulTableUpdateByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            AwfulTableMapper mapper = sqlSession.getMapper(AwfulTableMapper.class);
            AwfulTable record = new AwfulTable();
            record.seteMail("fred@fred.com");
            record.setEmailaddress("alsofred@fred.com");
            record.setFirstFirstName("fred1");
            record.setFrom("from field");
            record.setId1(1);
            record.setId2(2);
            record.setId5(5);
            record.setId6(6);
            record.setId7(7);
            record.setSecondFirstName("fred2");
            record.setThirdFirstName("fred3");
            mapper.insert(record);
            record = new AwfulTable();
            record.seteMail("fred2@fred.com");
            record.setEmailaddress("alsofred2@fred.com");
            record.setFirstFirstName("fred11");
            record.setFrom("from from field");
            record.setId1(11);
            record.setId2(22);
            record.setId5(55);
            record.setId6(66);
            record.setId7(77);
            record.setSecondFirstName("fred22");
            record.setThirdFirstName("fred33");
            mapper.insert(record);
            AwfulTable newRecord = new AwfulTable();
            newRecord.setFirstFirstName("Alonzo");
            newRecord.setCustomerId(58);
            newRecord.setId1(111);
            newRecord.setId2(222);
            newRecord.setId5(555);
            newRecord.setId6(666);
            newRecord.setId7(777);
            int rows = mapper.updateByExample(newRecord).where(eMail, isLike("fred2@%")).build().execute();
            Assertions.assertEquals(1, rows);
            List<AwfulTable> answer = mapper.selectByExample().where(customerId, isEqualTo(58)).build().execute();
            Assertions.assertEquals(1, answer.size());
            AwfulTable returnedRecord = answer.get(0);
            Assertions.assertEquals(newRecord.getCustomerId(), returnedRecord.getCustomerId());
            Assertions.assertNull(returnedRecord.geteMail());
            Assertions.assertNull(returnedRecord.getEmailaddress());
            Assertions.assertEquals(newRecord.getFirstFirstName(), returnedRecord.getFirstFirstName());
            Assertions.assertNull(returnedRecord.getFrom());
            Assertions.assertEquals(newRecord.getId1(), returnedRecord.getId1());
            Assertions.assertEquals(newRecord.getId2(), returnedRecord.getId2());
            Assertions.assertEquals(newRecord.getId5(), returnedRecord.getId5());
            Assertions.assertEquals(newRecord.getId6(), returnedRecord.getId6());
            Assertions.assertEquals(newRecord.getId7(), returnedRecord.getId7());
            Assertions.assertNull(returnedRecord.getSecondFirstName());
            Assertions.assertNull(returnedRecord.getThirdFirstName());
        } finally {
            sqlSession.close();
        }
    }
}

