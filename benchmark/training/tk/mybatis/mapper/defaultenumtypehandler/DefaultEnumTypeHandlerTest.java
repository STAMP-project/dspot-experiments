package tk.mybatis.mapper.defaultenumtypehandler;


import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.base.BaseTest;


/**
 *
 *
 * @author liuzh
 */
public class DefaultEnumTypeHandlerTest extends BaseTest {
    @Test
    public void testSelect() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            List<User> users = selectAll();
            Assert.assertNotNull(users);
            Assert.assertEquals(2, users.size());
            Assert.assertEquals("abel533", users.get(0).getName());
            Assert.assertEquals(LockDictEnum.unlocked, users.get(0).getLock());
            Assert.assertEquals(StateDictEnum.enabled, users.get(0).getState());
            Assert.assertEquals("isea533", users.get(1).getName());
            Assert.assertEquals(LockDictEnum.locked, users.get(1).getLock());
            Assert.assertEquals(StateDictEnum.disabled, users.get(1).getState());
            User user = selectByPrimaryKey(1);
            Assert.assertEquals("abel533", user.getName());
            Assert.assertEquals(LockDictEnum.unlocked, users.get(0).getLock());
            Assert.assertEquals(StateDictEnum.enabled, user.getState());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testInsert() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = new User();
            user.setId(3);
            user.setName("liuzh");
            user.setLock(LockDictEnum.unlocked);
            user.setState(StateDictEnum.enabled);
            Assert.assertEquals(1, insert(user));
            user = selectByPrimaryKey(3);
            Assert.assertEquals("liuzh", user.getName());
            Assert.assertEquals(LockDictEnum.unlocked, user.getLock());
            Assert.assertEquals(StateDictEnum.enabled, user.getState());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testUpdate() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = selectByPrimaryKey(1);
            Assert.assertEquals("abel533", user.getName());
            Assert.assertEquals(LockDictEnum.unlocked, user.getLock());
            Assert.assertEquals(StateDictEnum.enabled, user.getState());
            user.setLock(LockDictEnum.locked);
            user.setState(StateDictEnum.disabled);
            Assert.assertEquals(1, updateByPrimaryKey(user));
            user = selectByPrimaryKey(1);
            Assert.assertEquals("abel533", user.getName());
            Assert.assertEquals(LockDictEnum.locked, user.getLock());
            Assert.assertEquals(StateDictEnum.disabled, user.getState());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testDelete() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            Assert.assertEquals(1, deleteByPrimaryKey(1));
            User user = new User();
            user.setState(StateDictEnum.enabled);
            Assert.assertEquals(0, delete(user));
            user = new User();
            user.setLock(LockDictEnum.unlocked);
            Assert.assertEquals(0, delete(user));
            user = new User();
            user.setLock(LockDictEnum.locked);
            user.setState(StateDictEnum.disabled);
            Assert.assertEquals(1, delete(user));
        } finally {
            sqlSession.close();
        }
    }
}

