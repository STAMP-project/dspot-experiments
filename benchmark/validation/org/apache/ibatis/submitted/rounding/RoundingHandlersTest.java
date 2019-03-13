package org.apache.ibatis.submitted.rounding;


import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class RoundingHandlersTest {
    private static SqlSessionFactory sqlSessionFactory;

    @Test
    public void shouldGetAUser() {
        SqlSession session = RoundingHandlersTest.sqlSessionFactory.openSession();
        try {
            Mapper mapper = session.getMapper(Mapper.class);
            User user = mapper.getUser(1);
            Assert.assertEquals("User1", user.getName());
            Assert.assertEquals(RoundingMode.UP, user.getRoundingMode());
            user = mapper.getUser2(1);
            Assert.assertEquals("User1", user.getName());
            Assert.assertEquals(RoundingMode.UP, user.getRoundingMode());
        } finally {
            session.close();
        }
    }

    @Test
    public void shouldInsertUser2() {
        SqlSession session = RoundingHandlersTest.sqlSessionFactory.openSession();
        try {
            Mapper mapper = session.getMapper(Mapper.class);
            User user = new User();
            user.setId(2);
            user.setName("User2");
            user.setFunkyNumber(BigDecimal.ZERO);
            user.setRoundingMode(RoundingMode.UNNECESSARY);
            mapper.insert(user);
            mapper.insert2(user);
        } finally {
            session.close();
        }
    }
}

