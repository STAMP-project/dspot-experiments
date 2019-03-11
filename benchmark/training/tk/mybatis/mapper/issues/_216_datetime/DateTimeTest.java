package tk.mybatis.mapper.issues._216_datetime;


import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.base.BaseTest;


public class DateTimeTest extends BaseTest {
    @Test
    public void testSelect() {
        SqlSession sqlSession = getSqlSession();
        try {
            TimeModelMapper mapper = sqlSession.getMapper(TimeModelMapper.class);
            List<TimeModel> list = selectAll();
            Assert.assertEquals(2, list.size());
            Assert.assertEquals("2018-01-01", toDate(list.get(0).getTestDate()));
            Assert.assertEquals("12:11:00", toTime(list.get(0).getTestTime()));
            Assert.assertEquals("2018-01-01 12:00:00", toDatetime(list.get(0).getTestDatetime()));
            Assert.assertEquals("2018-11-11", toDate(list.get(1).getTestDate()));
            Assert.assertEquals("01:59:11", toTime(list.get(1).getTestTime()));
            Assert.assertEquals("2018-02-12 17:58:12", toDatetime(list.get(1).getTestDatetime()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testInsert() {
        SqlSession sqlSession = getSqlSession();
        try {
            TimeModelMapper mapper = sqlSession.getMapper(TimeModelMapper.class);
            TimeModel timeModel = new TimeModel();
            timeModel.setId(3);
            Date now = new Date();
            timeModel.setTestDate(now);
            timeModel.setTestTime(now);
            timeModel.setTestDatetime(now);
            Assert.assertEquals(1, mapper.insert(timeModel));
            timeModel = selectByPrimaryKey(3);
            // ??????????????
            Assert.assertEquals(toDate(now), toDate(timeModel.getTestDate()));
            Assert.assertEquals(((toDate(now)) + " 00:00:00"), toDatetime(timeModel.getTestDate()));
            // ???????
            Assert.assertEquals(toTime(now), toTime(timeModel.getTestTime()));
            Assert.assertEquals(toDatetime(now), toDatetime(timeModel.getTestTime()));
            Assert.assertEquals(toDatetime(now), toDatetime(timeModel.getTestDatetime()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSelect2() {
        SqlSession sqlSession = getSqlSession();
        try {
            TimeModel2Mapper mapper = sqlSession.getMapper(TimeModel2Mapper.class);
            List<TimeModel2> list = selectAll();
            Assert.assertEquals(2, list.size());
            Assert.assertEquals("2018-01-01", toDate(list.get(0).getTestDate()));
            Assert.assertEquals("12:11:00", toTime(list.get(0).getTestTime()));
            Assert.assertEquals("2018-01-01 12:00:00", toDatetime(list.get(0).getTestDatetime()));
            Assert.assertEquals("2018-11-11", toDate(list.get(1).getTestDate()));
            Assert.assertEquals("01:59:11", toTime(list.get(1).getTestTime()));
            Assert.assertEquals("2018-02-12 17:58:12", toDatetime(list.get(1).getTestDatetime()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testInsert2() {
        SqlSession sqlSession = getSqlSession();
        try {
            TimeModel2Mapper mapper = sqlSession.getMapper(TimeModel2Mapper.class);
            TimeModel2 timeModel = new TimeModel2();
            timeModel.setId(3);
            Date now = new Date();
            Timestamp now2 = new Timestamp(now.getTime());
            timeModel.setTestDate(now);
            timeModel.setTestTime(now);
            timeModel.setTestDatetime(now2);
            Assert.assertEquals(1, mapper.insert(timeModel));
            timeModel = mapper.selectByPrimaryKey(3);
            // ??????????????
            Assert.assertEquals(toDate(now), toDate(timeModel.getTestDate()));
            Assert.assertEquals(((toDate(now)) + " 00:00:00"), toDatetime(timeModel.getTestDate()));
            // ???????
            Assert.assertEquals(toTime(now), toTime(timeModel.getTestTime()));
            Assert.assertEquals(toDatetime(now), toDatetime(timeModel.getTestTime()));
            Assert.assertEquals(toDatetime(now), toDatetime(timeModel.getTestDatetime()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSelect3() {
        SqlSession sqlSession = getSqlSession();
        try {
            TimeModel3Mapper mapper = sqlSession.getMapper(TimeModel3Mapper.class);
            List<TimeModel3> list = selectAll();
            Assert.assertEquals(2, list.size());
            Assert.assertEquals("2018-01-01", toDate(list.get(0).getTestDate()));
            Assert.assertEquals("12:11:00", toTime(list.get(0).getTestTime()));
            Assert.assertEquals("2018-01-01 12:00:00", toDatetime(list.get(0).getTestDatetime()));
            Assert.assertEquals("2018-11-11", toDate(list.get(1).getTestDate()));
            Assert.assertEquals("01:59:11", toTime(list.get(1).getTestTime()));
            Assert.assertEquals("2018-02-12 17:58:12", toDatetime(list.get(1).getTestDatetime()));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testInsert3() {
        SqlSession sqlSession = getSqlSession();
        try {
            TimeModel3Mapper mapper = sqlSession.getMapper(TimeModel3Mapper.class);
            TimeModel3 timeModel = new TimeModel3();
            timeModel.setId(3);
            Date now = new Date();
            timeModel.setTestDate(now);
            timeModel.setTestTime(now);
            timeModel.setTestDatetime(now);
            /* insert ????????? jdbcType ????

            DEBUG [main] - ==>  Preparing: INSERT INTO test_timestamp ( id,test_date,test_time,test_datetime ) VALUES( ?,?,?,? )
            DEBUG [main] - ==> Parameters: 3(Integer), 2018-02-25(Date), 11:50:18(Time), 2018-02-25 11:50:18.263(Timestamp)
             */
            Assert.assertEquals(1, insert(timeModel));
            timeModel = mapper.selectByPrimaryKey(3);
            // ??????????????
            Assert.assertEquals(toDate(now), toDate(timeModel.getTestDate()));
            Assert.assertEquals(((toDate(now)) + " 00:00:00"), toDatetime(timeModel.getTestDate()));
            // ??
            Assert.assertEquals(toTime(now), toTime(timeModel.getTestTime()));
            // ??????????? jdbcType=TIME?????????????
            Assert.assertEquals(("1970-01-01 " + (toTime(now))), toDatetime(timeModel.getTestTime()));
            Assert.assertEquals(toDatetime(now), toDatetime(timeModel.getTestDatetime()));
        } finally {
            sqlSession.close();
        }
    }
}

