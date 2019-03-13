package dao.test;


import com.ssm.maven.core.dao.UserDao;
import com.ssm.maven.core.entity.User;
import com.ssm.maven.core.util.MD5Util;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;


/**
 * Created by 13 on 2017/3/30.
 */
// ?????????? ??????Junit4
// ????,?????????????,???????????,????????.
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-context.xml")
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class UserDaoTest {
    @Autowired
    private UserDao userDao;

    @Test
    public void loginTest() {
        User user = new User();
        user.setUserName("admin");
        user.setPassword("123456");
        // ?????????????
        Assert.assertEquals(userDao.login(user), null);
        User user2 = new User();
        user2.setUserName("admin");
        user2.setPassword(MD5Util.MD5Encode("123456", "UTF-8"));
        // ?????????????????,???id?2
        Assert.assertTrue(((userDao.login(user2).getId()) == 2));
        // Assert.assertTrue(userDao.login(user2).getId() == 3);
    }

    @Test
    public void findUsersTest() {
        // ??????????????0
        Assert.assertTrue(((userDao.findUsers(null).size()) > 0));
        // ??????????????3,????????,???????????????
        Assert.assertTrue(((userDao.findUsers(null).size()) == 3));
    }

    @Test
    public void getTotalUserTest() {
        Assert.assertTrue(((userDao.getTotalUser(null)) > 0));
        Assert.assertTrue(((userDao.getTotalUser(null)) == 3));
    }

    @Test
    public void updateUserTest() {
        User user = new User();
        user.setId(51);
        user.setPassword("1221");
        // ??0?????????????,?????,??updateUser()???????0,?????
        Assert.assertTrue(((userDao.updateUser(user)) > 0));
        User user2 = new User();
        user2.setId(1000);
        user2.setPassword("234y9823y89hhao");
        Assert.assertTrue(((userDao.updateUser(user2)) > 0));
    }

    @Test
    public void addUserTest() {
        User user = new User();
        user.setUserName("????");
        user.setPassword(MD5Util.MD5Encode("testuser", "UTF-8"));
        // ??0?????????????,?????
        Assert.assertTrue(((userDao.addUser(user)) > 0));
    }

    @Test
    public void deleteUserTest() {
        Assert.assertTrue(((userDao.deleteUser(51)) > 0));
    }
}

