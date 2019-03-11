package tk.mybatis.mapper.typehandler;


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
public class TypeHandlerTest extends BaseTest {
    @Test
    public void testSelect() {
        SqlSession sqlSession = getSqlSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            List<User> users = selectAll();
            Assert.assertNotNull(users);
            Assert.assertEquals(2, users.size());
            Assert.assertEquals("abel533", users.get(0).getName());
            Assert.assertEquals("Hebei", users.get(0).getAddress().getProvince());
            Assert.assertEquals("Shijiazhuang", users.get(0).getAddress().getCity());
            Assert.assertEquals(StateEnum.enabled, users.get(0).getState());
            Assert.assertEquals("isea533", users.get(1).getName());
            Assert.assertEquals("Hebei/Handan", users.get(1).getAddress().toString());
            Assert.assertEquals(StateEnum.disabled, users.get(1).getState());
            User user = selectByPrimaryKey(1);
            Assert.assertEquals("abel533", user.getName());
            Assert.assertEquals("Hebei", user.getAddress().getProvince());
            Assert.assertEquals("Shijiazhuang", user.getAddress().getCity());
            Assert.assertEquals(StateEnum.enabled, user.getState());
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
            Address address = new Address();
            address.setProvince("Hebei");
            address.setCity("Qinhuangdao");
            user.setAddress(address);
            user.setState(StateEnum.enabled);
            Assert.assertEquals(1, insert(user));
            user = selectByPrimaryKey(3);
            Assert.assertEquals("liuzh", user.getName());
            Assert.assertEquals("Hebei", user.getAddress().getProvince());
            Assert.assertEquals("Qinhuangdao", user.getAddress().getCity());
            Assert.assertEquals(StateEnum.enabled, user.getState());
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
            Assert.assertEquals("Hebei", user.getAddress().getProvince());
            Assert.assertEquals("Shijiazhuang", user.getAddress().getCity());
            Assert.assertEquals(StateEnum.enabled, user.getState());
            user.setState(StateEnum.disabled);
            user.getAddress().setCity("Handan");
            Assert.assertEquals(1, updateByPrimaryKey(user));
            user = selectByPrimaryKey(1);
            Assert.assertEquals("abel533", user.getName());
            Assert.assertEquals("Hebei", user.getAddress().getProvince());
            Assert.assertEquals("Handan", user.getAddress().getCity());
            Assert.assertEquals(StateEnum.disabled, user.getState());
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
            Address address = new Address();
            address.setProvince("Hebei");
            address.setCity("Handan");
            user.setAddress(address);
            user.setState(StateEnum.enabled);
            Assert.assertEquals(0, delete(user));
            user.setState(StateEnum.disabled);
            Assert.assertEquals(1, delete(user));
        } finally {
            sqlSession.close();
        }
    }
}

