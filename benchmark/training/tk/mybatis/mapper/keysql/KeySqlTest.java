package tk.mybatis.mapper.keysql;


import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import tk.mybatis.mapper.base.BaseTest;


/**
 *
 *
 * @author liuzh
 */
@Ignore("???????? MySql ???")
public class KeySqlTest extends BaseTest {
    @Test
    public void testUserAutoIncrement() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserAutoIncrementMapper mapper = sqlSession.getMapper(UserAutoIncrementMapper.class);
            UserAutoIncrement user = new UserAutoIncrement();
            user.setName("liuzh");
            Assert.assertEquals(1, mapper.insert(user));
            Assert.assertNotNull(user.getId());
            user = mapper.selectByPrimaryKey(user.getId());
            Assert.assertEquals("liuzh", user.getName());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testUserAutoIncrementIdentity() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserAutoIncrementIdentityMapper mapper = sqlSession.getMapper(UserAutoIncrementIdentityMapper.class);
            UserAutoIncrementIdentity user = new UserAutoIncrementIdentity();
            user.setName("liuzh");
            Assert.assertEquals(1, mapper.insert(user));
            Assert.assertNotNull(user.getId());
            user = mapper.selectByPrimaryKey(user.getId());
            Assert.assertEquals("liuzh", user.getName());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testUserSqlAfter() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserSqlAfterMapper mapper = sqlSession.getMapper(UserSqlAfterMapper.class);
            UserSqlAfter user = new UserSqlAfter();
            user.setName("liuzh");
            Assert.assertEquals(1, mapper.insert(user));
            Assert.assertNotNull(user.getId());
            user = mapper.selectByPrimaryKey(user.getId());
            Assert.assertEquals("liuzh", user.getName());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testUserSqlBefore() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserSqlBeforeMapper mapper = sqlSession.getMapper(UserSqlBeforeMapper.class);
            UserSqlBefore user = new UserSqlBefore();
            user.setName("liuzh");
            Assert.assertEquals(1, insert(user));
            Assert.assertEquals(new Integer(12345), user.getId());
            user = selectByPrimaryKey(12345);
            Assert.assertEquals("liuzh", user.getName());
        } finally {
            sqlSession.close();
        }
    }
}

