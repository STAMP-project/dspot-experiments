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
package mbg.test.mb3.miscellaneous;


import TestEnum.FRED;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import mbg.test.common.FirstName;
import mbg.test.common.MyTime;
import mbg.test.mb3.AbstractTest;
import mbg.test.mb3.generated.miscellaneous.mapper.EnumtestMapper;
import mbg.test.mb3.generated.miscellaneous.mapper.MyMapper;
import mbg.test.mb3.generated.miscellaneous.mapper.RegexrenameMapper;
import mbg.test.mb3.generated.miscellaneous.model.Enumtest;
import mbg.test.mb3.generated.miscellaneous.model.MyObject;
import mbg.test.mb3.generated.miscellaneous.model.MyObjectCriteria;
import mbg.test.mb3.generated.miscellaneous.model.MyObjectKey;
import mbg.test.mb3.generated.miscellaneous.model.Regexrename;
import mbg.test.mb3.generated.miscellaneous.model.example.mbgtest.AnotherawfultableExample;
import mbg.test.mb3.generated.miscellaneous.model.mbgtest.Anotherawfultable;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 *
 *
 * @author Jeff Butler
 */
public class MiscellaneousTest extends AbstractMiscellaneousTest {
    @Test
    public void testMyObjectinsertMyObject() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            MyMapper mapper = sqlSession.getMapper(MyMapper.class);
            MyObject record = new MyObject();
            record.setStartDate(new Date());
            record.setDecimal100field(10L);
            record.setDecimal155field(15.12345);
            record.setDecimal60field(6);
            FirstName fn = new FirstName();
            fn.setValue("Jeff");
            record.setFirstname(fn);
            record.setId1(1);
            record.setId2(2);
            record.setLastname("Butler");
            MyTime myTime = new MyTime();
            myTime.setHours(12);
            myTime.setMinutes(34);
            myTime.setSeconds(5);
            record.setTimefield(myTime);
            record.setTimestampfield(new Date());
            mapper.insert(record);
            MyObjectKey key = new MyObjectKey();
            key.setId1(1);
            key.setId2(2);
            MyObject returnedRecord = mapper.selectByPrimaryKey(key);
            Assertions.assertNotNull(returnedRecord);
            Assertions.assertTrue(datesAreEqual(record.getStartDate(), returnedRecord.getStartDate()));
            Assertions.assertEquals(record.getDecimal100field(), returnedRecord.getDecimal100field());
            Assertions.assertEquals(record.getDecimal155field(), returnedRecord.getDecimal155field());
            Assertions.assertEquals(record.getDecimal60field(), returnedRecord.getDecimal60field());
            Assertions.assertEquals(record.getFirstname(), returnedRecord.getFirstname());
            Assertions.assertEquals(record.getId1(), returnedRecord.getId1());
            Assertions.assertEquals(record.getId2(), returnedRecord.getId2());
            Assertions.assertEquals(record.getLastname(), returnedRecord.getLastname());
            Assertions.assertEquals(record.getTimefield(), returnedRecord.getTimefield());
            Assertions.assertEquals(record.getTimestampfield(), returnedRecord.getTimestampfield());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testMyObjectUpdateByPrimaryKey() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            MyMapper mapper = sqlSession.getMapper(MyMapper.class);
            MyObject record = new MyObject();
            FirstName fn = new FirstName();
            fn.setValue("Jeff");
            record.setFirstname(fn);
            record.setLastname("Smith");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            fn = new FirstName();
            fn.setValue("Scott");
            record.setFirstname(fn);
            record.setLastname("Jones");
            int rows = mapper.updateByPrimaryKey(record);
            Assertions.assertEquals(1, rows);
            MyObjectKey key = new MyObjectKey();
            key.setId1(1);
            key.setId2(2);
            MyObject record2 = mapper.selectByPrimaryKey(key);
            Assertions.assertEquals(record.getFirstname(), record2.getFirstname());
            Assertions.assertEquals(record.getLastname(), record2.getLastname());
            Assertions.assertEquals(record.getId1(), record2.getId1());
            Assertions.assertEquals(record.getId2(), record2.getId2());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testMyObjectUpdateByPrimaryKeySelective() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            MyMapper mapper = sqlSession.getMapper(MyMapper.class);
            MyObject record = new MyObject();
            FirstName fn = new FirstName();
            fn.setValue("Jeff");
            record.setFirstname(fn);
            record.setLastname("Smith");
            record.setDecimal60field(5);
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            MyObject newRecord = new MyObject();
            newRecord.setId1(1);
            newRecord.setId2(2);
            fn = new FirstName();
            fn.setValue("Scott");
            newRecord.setFirstname(fn);
            record.setStartDate(new Date());
            int rows = mapper.updateByPrimaryKeySelective(newRecord);
            Assertions.assertEquals(1, rows);
            MyObjectKey key = new MyObjectKey();
            key.setId1(1);
            key.setId2(2);
            MyObject returnedRecord = mapper.selectByPrimaryKey(key);
            Assertions.assertTrue(datesAreEqual(newRecord.getStartDate(), returnedRecord.getStartDate()));
            Assertions.assertEquals(record.getDecimal100field(), returnedRecord.getDecimal100field());
            Assertions.assertEquals(record.getDecimal155field(), returnedRecord.getDecimal155field());
            // with columns mapped to primitive types, the column is always
            // updated
            Assertions.assertEquals(newRecord.getDecimal60field(), returnedRecord.getDecimal60field());
            Assertions.assertEquals(newRecord.getFirstname(), returnedRecord.getFirstname());
            Assertions.assertEquals(record.getId1(), returnedRecord.getId1());
            Assertions.assertEquals(record.getId2(), returnedRecord.getId2());
            Assertions.assertEquals(record.getLastname(), returnedRecord.getLastname());
            Assertions.assertEquals(record.getTimefield(), returnedRecord.getTimefield());
            Assertions.assertEquals(record.getTimestampfield(), returnedRecord.getTimestampfield());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testMyObjectDeleteByPrimaryKey() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            MyMapper mapper = sqlSession.getMapper(MyMapper.class);
            MyObject record = new MyObject();
            FirstName fn = new FirstName();
            fn.setValue("Jeff");
            record.setFirstname(fn);
            record.setLastname("Smith");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            MyObjectKey key = new MyObjectKey();
            key.setId1(1);
            key.setId2(2);
            int rows = mapper.deleteByPrimaryKey(key);
            Assertions.assertEquals(1, rows);
            MyObjectCriteria example = new MyObjectCriteria();
            List<MyObject> answer = mapper.selectByExample(example);
            Assertions.assertEquals(0, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testMyObjectDeleteByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            MyMapper mapper = sqlSession.getMapper(MyMapper.class);
            MyObject record = new MyObject();
            FirstName fn = new FirstName();
            fn.setValue("Jeff");
            record.setFirstname(fn);
            record.setLastname("Smith");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Bob");
            record.setFirstname(fn);
            record.setLastname("Jones");
            record.setId1(3);
            record.setId2(4);
            mapper.insert(record);
            MyObjectCriteria example = new MyObjectCriteria();
            List<MyObject> answer = mapper.selectByExample(example);
            Assertions.assertEquals(2, answer.size());
            example = new MyObjectCriteria();
            example.createCriteria().andLastnameLike("J%");
            int rows = mapper.deleteByExample(example);
            Assertions.assertEquals(1, rows);
            example = new MyObjectCriteria();
            answer = mapper.selectByExample(example);
            Assertions.assertEquals(1, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testMyObjectSelectByPrimaryKey() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            MyMapper mapper = sqlSession.getMapper(MyMapper.class);
            MyObject record = new MyObject();
            FirstName fn = new FirstName();
            fn.setValue("Jeff");
            record.setFirstname(fn);
            record.setLastname("Smith");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Bob");
            record.setFirstname(fn);
            record.setLastname("Jones");
            record.setId1(3);
            record.setId2(4);
            mapper.insert(record);
            MyObjectKey key = new MyObjectKey();
            key.setId1(3);
            key.setId2(4);
            MyObject newRecord = mapper.selectByPrimaryKey(key);
            Assertions.assertNotNull(newRecord);
            Assertions.assertEquals(record.getFirstname(), newRecord.getFirstname());
            Assertions.assertEquals(record.getLastname(), newRecord.getLastname());
            Assertions.assertEquals(record.getId1(), newRecord.getId1());
            Assertions.assertEquals(record.getId2(), newRecord.getId2());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testMyObjectSelectByExampleLike() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            MyMapper mapper = sqlSession.getMapper(MyMapper.class);
            MyObject record = new MyObject();
            FirstName fn = new FirstName();
            fn.setValue("Fred");
            record.setFirstname(fn);
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(1);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Wilma");
            record.setFirstname(fn);
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Pebbles");
            record.setFirstname(fn);
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(3);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Barney");
            record.setFirstname(fn);
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(1);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Betty");
            record.setFirstname(fn);
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(2);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Bamm Bamm");
            record.setFirstname(fn);
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(3);
            mapper.insert(record);
            MyObjectCriteria example = new MyObjectCriteria();
            fn = new FirstName();
            fn.setValue("B%");
            example.createCriteria().andFirstnameLike(fn);
            example.setOrderByClause("ID1, ID2");
            List<MyObject> answer = mapper.selectByExample(example);
            Assertions.assertEquals(3, answer.size());
            MyObject returnedRecord = answer.get(0);
            Assertions.assertEquals(2, returnedRecord.getId1().intValue());
            Assertions.assertEquals(1, returnedRecord.getId2().intValue());
            returnedRecord = answer.get(1);
            Assertions.assertEquals(2, returnedRecord.getId1().intValue());
            Assertions.assertEquals(2, returnedRecord.getId2().intValue());
            returnedRecord = answer.get(2);
            Assertions.assertEquals(2, returnedRecord.getId1().intValue());
            Assertions.assertEquals(3, returnedRecord.getId2().intValue());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testMyObjectSelectByExampleNotLike() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            MyMapper mapper = sqlSession.getMapper(MyMapper.class);
            MyObject record = new MyObject();
            FirstName fn = new FirstName();
            fn.setValue("Fred");
            record.setFirstname(fn);
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(1);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Wilma");
            record.setFirstname(fn);
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Pebbles");
            record.setFirstname(fn);
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(3);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Barney");
            record.setFirstname(fn);
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(1);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Betty");
            record.setFirstname(fn);
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(2);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Bamm Bamm");
            record.setFirstname(fn);
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(3);
            mapper.insert(record);
            MyObjectCriteria example = new MyObjectCriteria();
            fn = new FirstName();
            fn.setValue("B%");
            example.createCriteria().andFirstnameNotLike(fn);
            example.setOrderByClause("ID1, ID2");
            List<MyObject> answer = mapper.selectByExample(example);
            Assertions.assertEquals(3, answer.size());
            MyObject returnedRecord = answer.get(0);
            Assertions.assertEquals(1, returnedRecord.getId1().intValue());
            Assertions.assertEquals(1, returnedRecord.getId2().intValue());
            returnedRecord = answer.get(1);
            Assertions.assertEquals(1, returnedRecord.getId1().intValue());
            Assertions.assertEquals(2, returnedRecord.getId2().intValue());
            returnedRecord = answer.get(2);
            Assertions.assertEquals(1, returnedRecord.getId1().intValue());
            Assertions.assertEquals(3, returnedRecord.getId2().intValue());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testMyObjectSelectByExampleComplexLike() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            MyMapper mapper = sqlSession.getMapper(MyMapper.class);
            MyObject record = new MyObject();
            FirstName fn = new FirstName();
            fn.setValue("Fred");
            record.setFirstname(fn);
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(1);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Wilma");
            record.setFirstname(fn);
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Pebbles");
            record.setFirstname(fn);
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(3);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Barney");
            record.setFirstname(fn);
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(1);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Betty");
            record.setFirstname(fn);
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(2);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Bamm Bamm");
            record.setFirstname(fn);
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(3);
            mapper.insert(record);
            MyObjectCriteria example = new MyObjectCriteria();
            fn = new FirstName();
            fn.setValue("B%");
            example.createCriteria().andFirstnameLike(fn).andId2EqualTo(3);
            fn = new FirstName();
            fn.setValue("W%");
            example.or(example.createCriteria().andFirstnameLike(fn));
            example.setOrderByClause("ID1, ID2");
            List<MyObject> answer = mapper.selectByExample(example);
            Assertions.assertEquals(2, answer.size());
            MyObject returnedRecord = answer.get(0);
            Assertions.assertEquals(1, returnedRecord.getId1().intValue());
            Assertions.assertEquals(2, returnedRecord.getId2().intValue());
            returnedRecord = answer.get(1);
            Assertions.assertEquals(2, returnedRecord.getId1().intValue());
            Assertions.assertEquals(3, returnedRecord.getId2().intValue());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testMyObjectSelectByExampleIn() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            MyMapper mapper = sqlSession.getMapper(MyMapper.class);
            MyObject record = new MyObject();
            FirstName fn = new FirstName();
            fn.setValue("Fred");
            record.setFirstname(fn);
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(1);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Wilma");
            record.setFirstname(fn);
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Pebbles");
            record.setFirstname(fn);
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(3);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Barney");
            record.setFirstname(fn);
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(1);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Betty");
            record.setFirstname(fn);
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(2);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Bamm Bamm");
            record.setFirstname(fn);
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(3);
            mapper.insert(record);
            List<Integer> ids = new ArrayList<>();
            ids.add(1);
            ids.add(3);
            MyObjectCriteria example = new MyObjectCriteria();
            example.createCriteria().andId2In(ids);
            example.setOrderByClause("ID1, ID2");
            List<MyObject> answer = mapper.selectByExample(example);
            Assertions.assertEquals(4, answer.size());
            MyObject returnedRecord = answer.get(0);
            Assertions.assertEquals(1, returnedRecord.getId1().intValue());
            Assertions.assertEquals(1, returnedRecord.getId2().intValue());
            Assertions.assertEquals("Flintstone", returnedRecord.getLastname());
            returnedRecord = answer.get(1);
            Assertions.assertEquals(1, returnedRecord.getId1().intValue());
            Assertions.assertEquals(3, returnedRecord.getId2().intValue());
            Assertions.assertEquals("Flintstone", returnedRecord.getLastname());
            returnedRecord = answer.get(2);
            Assertions.assertEquals(2, returnedRecord.getId1().intValue());
            Assertions.assertEquals(1, returnedRecord.getId2().intValue());
            Assertions.assertEquals("Rubble", returnedRecord.getLastname());
            returnedRecord = answer.get(3);
            Assertions.assertEquals(2, returnedRecord.getId1().intValue());
            Assertions.assertEquals(3, returnedRecord.getId2().intValue());
            Assertions.assertEquals("Rubble", returnedRecord.getLastname());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testMyObjectSelectByExampleBetween() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            MyMapper mapper = sqlSession.getMapper(MyMapper.class);
            MyObject record = new MyObject();
            FirstName fn = new FirstName();
            fn.setValue("Fred");
            record.setFirstname(fn);
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(1);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Wilma");
            record.setFirstname(fn);
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Pebbles");
            record.setFirstname(fn);
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(3);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Barney");
            record.setFirstname(fn);
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(1);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Betty");
            record.setFirstname(fn);
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(2);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Bamm Bamm");
            record.setFirstname(fn);
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(3);
            mapper.insert(record);
            MyObjectCriteria example = new MyObjectCriteria();
            example.createCriteria().andId2Between(1, 3);
            example.setOrderByClause("ID1, ID2");
            List<MyObject> answer = mapper.selectByExample(example);
            Assertions.assertEquals(6, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testMyObjectSelectByExampleTimeEquals() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            MyMapper mapper = sqlSession.getMapper(MyMapper.class);
            MyObject record = new MyObject();
            record.setStartDate(new Date());
            record.setDecimal100field(10L);
            record.setDecimal155field(15.12345);
            record.setDecimal60field(6);
            FirstName fn = new FirstName();
            fn.setValue("Jeff");
            record.setFirstname(fn);
            record.setId1(1);
            record.setId2(2);
            record.setLastname("Butler");
            MyTime myTime = new MyTime();
            myTime.setHours(12);
            myTime.setMinutes(34);
            myTime.setSeconds(5);
            record.setTimefield(myTime);
            record.setTimestampfield(new Date());
            mapper.insert(record);
            MyObjectCriteria example = new MyObjectCriteria();
            example.createCriteria().andTimefieldEqualTo(myTime);
            List<MyObject> results = mapper.selectByExample(example);
            Assertions.assertEquals(1, results.size());
            MyObject returnedRecord = results.get(0);
            Assertions.assertTrue(datesAreEqual(record.getStartDate(), returnedRecord.getStartDate()));
            Assertions.assertEquals(record.getDecimal100field(), returnedRecord.getDecimal100field());
            Assertions.assertEquals(record.getDecimal155field(), returnedRecord.getDecimal155field());
            Assertions.assertEquals(record.getDecimal60field(), returnedRecord.getDecimal60field());
            Assertions.assertEquals(record.getFirstname(), returnedRecord.getFirstname());
            Assertions.assertEquals(record.getId1(), returnedRecord.getId1());
            Assertions.assertEquals(record.getId2(), returnedRecord.getId2());
            Assertions.assertEquals(record.getLastname(), returnedRecord.getLastname());
            Assertions.assertEquals(record.getTimefield(), returnedRecord.getTimefield());
            Assertions.assertEquals(record.getTimestampfield(), returnedRecord.getTimestampfield());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testFieldIgnored() {
        Assertions.assertThrows(NoSuchFieldException.class, () -> {
            MyObject.class.getDeclaredField("decimal30field");
        });
    }

    @Test
    public void testFluentBuilderMethodGenerated() {
        MyObject myObject = new MyObject();
        FirstName firstname = new FirstName();
        firstname.setValue("Bob");
        Integer wierdField = 4711;
        myObject.withWierdField(wierdField).withFirstname(firstname);
        Assertions.assertEquals("Bob", myObject.getFirstname().getValue());
        Assertions.assertEquals(wierdField, myObject.getWierdField());
    }

    @Test
    public void testMyObjectUpdateByExampleSelective() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            MyMapper mapper = sqlSession.getMapper(MyMapper.class);
            MyObject record = new MyObject();
            FirstName fn = new FirstName();
            fn.setValue("Jeff");
            record.setFirstname(fn);
            record.setLastname("Smith");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Bob");
            record.setFirstname(fn);
            record.setLastname("Jones");
            record.setId1(3);
            record.setId2(4);
            mapper.insert(record);
            MyObject newRecord = new MyObject();
            newRecord.setLastname("Barker");
            MyObjectCriteria example = new MyObjectCriteria();
            fn = new FirstName();
            fn.setValue("B%");
            example.createCriteria().andFirstnameLike(fn);
            int rows = mapper.updateByExampleSelective(newRecord, example);
            Assertions.assertEquals(1, rows);
            List<MyObject> answer = mapper.selectByExample(example);
            Assertions.assertEquals(1, answer.size());
            MyObject returnedRecord = answer.get(0);
            Assertions.assertEquals(newRecord.getLastname(), returnedRecord.getLastname());
            Assertions.assertEquals(record.getFirstname(), returnedRecord.getFirstname());
            Assertions.assertEquals(record.getId1(), returnedRecord.getId1());
            Assertions.assertEquals(record.getId2(), returnedRecord.getId2());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testMyObjectUpdateByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            MyMapper mapper = sqlSession.getMapper(MyMapper.class);
            MyObject record = new MyObject();
            FirstName fn = new FirstName();
            fn.setValue("Jeff");
            record.setFirstname(fn);
            record.setLastname("Smith");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Bob");
            record.setFirstname(fn);
            record.setLastname("Jones");
            record.setId1(3);
            record.setId2(4);
            mapper.insert(record);
            MyObject newRecord = new MyObject();
            newRecord.setLastname("Barker");
            newRecord.setId1(3);
            newRecord.setId2(4);
            MyObjectCriteria example = new MyObjectCriteria();
            example.createCriteria().andId1EqualTo(3).andId2EqualTo(4);
            int rows = mapper.updateByExample(newRecord, example);
            Assertions.assertEquals(1, rows);
            List<MyObject> answer = mapper.selectByExample(example);
            Assertions.assertEquals(1, answer.size());
            MyObject returnedRecord = answer.get(0);
            Assertions.assertEquals(newRecord.getLastname(), returnedRecord.getLastname());
            Assertions.assertNull(returnedRecord.getFirstname());
            Assertions.assertEquals(newRecord.getId1(), returnedRecord.getId1());
            Assertions.assertEquals(newRecord.getId2(), returnedRecord.getId2());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testRegexRenameInsert() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            RegexrenameMapper mapper = sqlSession.getMapper(RegexrenameMapper.class);
            Regexrename record = new Regexrename();
            record.setAddress("123 Main Street");
            record.setName("Fred");
            record.setZipCode("99999");
            mapper.insert(record);
            Regexrename returnedRecord = mapper.selectByPrimaryKey(1);
            Assertions.assertEquals(record.getAddress(), returnedRecord.getAddress());
            Assertions.assertEquals(1, returnedRecord.getId().intValue());
            Assertions.assertEquals(record.getName(), returnedRecord.getName());
            Assertions.assertEquals(record.getZipCode(), returnedRecord.getZipCode());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testRegexRenameInsertSelective() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            RegexrenameMapper mapper = sqlSession.getMapper(RegexrenameMapper.class);
            Regexrename record = new Regexrename();
            record.setZipCode("99999");
            mapper.insertSelective(record);
            Integer key = 1;
            Assertions.assertEquals(key, record.getId());
            Regexrename returnedRecord = mapper.selectByPrimaryKey(key);
            Assertions.assertNull(returnedRecord.getAddress());
            Assertions.assertEquals(record.getId(), returnedRecord.getId());
            Assertions.assertNull(record.getName(), returnedRecord.getName());
            Assertions.assertEquals(record.getZipCode(), returnedRecord.getZipCode());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testAnotherAwfulTableInsert() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            Anotherawfultable record = new Anotherawfultable();
            record.setId(5);
            record.setSelect("select");
            record.setInsert("insert");
            sqlSession.insert("mbg.test.mb3.generated.miscellaneous.xml.AnotherawfultableMapper.insert", record);
            Anotherawfultable key = new Anotherawfultable();
            key.setId(5);
            Anotherawfultable returnedRecord = ((Anotherawfultable) (sqlSession.selectOne("mbg.test.mb3.generated.miscellaneous.xml.AnotherawfultableMapper.selectByPrimaryKey", key)));
            Assertions.assertEquals(record.getId(), returnedRecord.getId());
            Assertions.assertEquals(record.getSelect(), returnedRecord.getSelect());
            Assertions.assertEquals(record.getInsert(), returnedRecord.getInsert());
            Assertions.assertEquals(record.getUpdate(), returnedRecord.getUpdate());
            Assertions.assertEquals(record.getDelete(), returnedRecord.getDelete());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testAnotherAwfulTableSelectByExample() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            Anotherawfultable record = new Anotherawfultable();
            record.setId(5);
            record.setSelect("select");
            record.setInsert("insert");
            sqlSession.insert("mbg.test.mb3.generated.miscellaneous.xml.AnotherawfultableMapper.insert", record);
            AnotherawfultableExample example = new AnotherawfultableExample();
            example.or().andIdEqualTo(5);
            List<?> returnedRecords = sqlSession.selectList("mbg.test.mb3.generated.miscellaneous.xml.AnotherawfultableMapper.selectByExample", example);
            Assertions.assertEquals(returnedRecords.size(), 1);
            Anotherawfultable returnedRecord = ((Anotherawfultable) (returnedRecords.get(0)));
            Assertions.assertEquals(record.getId(), returnedRecord.getId());
            Assertions.assertEquals(record.getSelect(), returnedRecord.getSelect());
            Assertions.assertEquals(record.getInsert(), returnedRecord.getInsert());
            Assertions.assertEquals(record.getUpdate(), returnedRecord.getUpdate());
            Assertions.assertEquals(record.getDelete(), returnedRecord.getDelete());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testMyObjectSelectByExampleLikeInsensitive() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            MyMapper mapper = sqlSession.getMapper(MyMapper.class);
            MyObject record = new MyObject();
            FirstName fn = new FirstName();
            fn.setValue("Fred");
            record.setFirstname(fn);
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(1);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Wilma");
            record.setFirstname(fn);
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(2);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Pebbles");
            record.setFirstname(fn);
            record.setLastname("Flintstone");
            record.setId1(1);
            record.setId2(3);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Barney");
            record.setFirstname(fn);
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(1);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Betty");
            record.setFirstname(fn);
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(2);
            mapper.insert(record);
            record = new MyObject();
            fn = new FirstName();
            fn.setValue("Bamm Bamm");
            record.setFirstname(fn);
            record.setLastname("Rubble");
            record.setId1(2);
            record.setId2(3);
            mapper.insert(record);
            MyObjectCriteria example = new MyObjectCriteria();
            example.createCriteria().andLastnameLike("RU%");
            example.setOrderByClause("ID1, ID2");
            List<MyObject> answer = mapper.selectByExample(example);
            Assertions.assertEquals(0, answer.size());
            example.clear();
            example.createCriteria().andLastnameLikeInsensitive("RU%");
            answer = mapper.selectByExample(example);
            Assertions.assertEquals(3, answer.size());
            MyObject returnedRecord = answer.get(0);
            Assertions.assertEquals(2, returnedRecord.getId1().intValue());
            Assertions.assertEquals(1, returnedRecord.getId2().intValue());
            returnedRecord = answer.get(1);
            Assertions.assertEquals(2, returnedRecord.getId1().intValue());
            Assertions.assertEquals(2, returnedRecord.getId2().intValue());
            returnedRecord = answer.get(2);
            Assertions.assertEquals(2, returnedRecord.getId1().intValue());
            Assertions.assertEquals(3, returnedRecord.getId2().intValue());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testEnum() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            EnumtestMapper mapper = sqlSession.getMapper(EnumtestMapper.class);
            Enumtest enumTest = new Enumtest();
            enumTest.setId(1);
            enumTest.setName(FRED);
            int rows = mapper.insert(enumTest);
            Assertions.assertEquals(1, rows);
            List<Enumtest> returnedRecords = mapper.selectByExample(null);
            Assertions.assertEquals(1, returnedRecords.size());
            Enumtest returnedRecord = returnedRecords.get(0);
            Assertions.assertEquals(1, returnedRecord.getId().intValue());
            Assertions.assertEquals(FRED, returnedRecord.getName());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testModelOnly1Nameview() {
        if (classExists("mbg.test.mb3.generated.miscellaneous.modelonly1.model.NameviewExample")) {
            Assertions.fail("NameviewExample class should not be generated in model only configuration");
        }
        if (!(classExists("mbg.test.mb3.generated.miscellaneous.modelonly1.model.Nameview"))) {
            Assertions.fail("Nameview class should be generated in model only configuration");
        }
    }

    @Test
    public void testModelOnly1Anotherawfultable() {
        if (classExists("mbg.test.mb3.generated.miscellaneous.modelonly1.model.AnotherawfultableExample")) {
            Assertions.fail("NameviewExample class should not be generated in model only configuration");
        }
        if (!(classExists("mbg.test.mb3.generated.miscellaneous.modelonly1.model.Anotherawfultable"))) {
            Assertions.fail("Nameview class should be generated in model only configuration");
        }
    }

    @Test
    public void testModelOnly2Pkblobs() {
        if (classExists("mbg.test.mb3.generated.miscellaneous.modelonly2.mapper.PkblobsMapper")) {
            Assertions.fail("PkblobsMapper class should not be generated in model only configuration");
        }
        if (classExists("mbg.test.mb3.generated.miscellaneous.modelonly2.model.PkblobsExample")) {
            Assertions.fail("PkblobsExample class should not be generated in model only configuration");
        }
        if (!(classExists("mbg.test.mb3.generated.miscellaneous.modelonly2.model.Pkblobs"))) {
            Assertions.fail("Pkblobs class should be generated in model only configuration");
        }
        if (!(resourceExists("mbg/test/mb3/generated/miscellaneous/modelonly2/xml/PkblobsMapper.xml"))) {
            Assertions.fail("PkblobsMapper.xml file should be generated in model only configuration");
        }
    }

    @Test
    public void testModelOnly2Pkfields() {
        if (classExists("mbg.test.mb3.generated.miscellaneous.modelonly2.mapper.PkfieldsMapper")) {
            Assertions.fail("PkfieldsMapper class should not be generated in model only configuration");
        }
        if (classExists("mbg.test.mb3.generated.miscellaneous.modelonly2.model.PkfieldsExample")) {
            Assertions.fail("PkfieldsExample class should not be generated in model only configuration");
        }
        if (!(classExists("mbg.test.mb3.generated.miscellaneous.modelonly2.model.Pkfields"))) {
            Assertions.fail("Pkfields class should be generated in model only configuration");
        }
        if (!(classExists("mbg.test.mb3.generated.miscellaneous.modelonly2.model.PkfieldsKey"))) {
            Assertions.fail("PkfieldsKey class should be generated in model only configuration");
        }
        if (!(resourceExists("mbg/test/mb3/generated/miscellaneous/modelonly2/xml/PkfieldsMapper.xml"))) {
            Assertions.fail("PkfieldsMapper.xml file should be generated in model only configuration");
        }
    }

    @Test
    public void testModelOnly2Fieldsonly() {
        if (!(classExists("mbg.test.mb3.generated.miscellaneous.modelonly2.mapper.FieldsonlyMapper"))) {
            Assertions.fail("FieldsonlyMapper class should be generated in model only configuration");
        }
        if (!(classExists("mbg.test.mb3.generated.miscellaneous.modelonly2.model.FieldsonlyExample"))) {
            Assertions.fail("FieldsonlyExample class should be generated in model only configuration");
        }
        if (!(classExists("mbg.test.mb3.generated.miscellaneous.modelonly2.model.Fieldsonly"))) {
            Assertions.fail("Fieldsonly class should be generated in model only configuration");
        }
        if (!(resourceExists("mbg/test/mb3/generated/miscellaneous/modelonly2/xml/FieldsonlyMapper.xml"))) {
            Assertions.fail("FieldsonlyMapper.xml file should be generated in model only configuration");
        }
    }

    @Test
    public void testDomainObjcetRename() {
        if (!(classExists("mbg.test.mb3.generated.miscellaneous.model.Rename"))) {
            Assertions.fail("Rename class should be generated (renamed from suffix_rename)");
        }
        if (!(classExists("mbg.test.mb3.generated.miscellaneous.model.RenameCriteria"))) {
            Assertions.fail("RenameCriteria class should be generated (renamed from suffix_rename)");
        }
        if (!(classExists("mbg.test.mb3.generated.miscellaneous.mapper.RenameMapper"))) {
            Assertions.fail("RenameMapper class should be generated (renamed from suffix_rename)");
        }
        if (!(resourceExists("mbg/test/mb3/generated/miscellaneous/xml/RenameMapper.xml"))) {
            Assertions.fail("RenameMapper.xml file should be generated (renamed from suffix_rename)");
        }
    }
}

