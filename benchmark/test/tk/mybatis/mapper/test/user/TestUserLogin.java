/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package tk.mybatis.mapper.test.user;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.mapper.MybatisHelper;
import tk.mybatis.mapper.mapper.UserLoginMapper;
import tk.mybatis.mapper.model.UserLogin;


/**
 * ??????
 *
 * @author liuzh
 */
public class TestUserLogin {
    /**
     * ??
     */
    @Test
    public void testInsert() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            UserLoginMapper mapper = sqlSession.getMapper(UserLoginMapper.class);
            UserLogin userLogin = new UserLogin();
            userLogin.setUsername("abel533");
            userLogin.setLogindate(new Date());
            userLogin.setLoginip("192.168.123.1");
            Assert.assertEquals(1, insert(userLogin));
            Assert.assertNotNull(userLogin.getLogid());
            Assert.assertTrue(((userLogin.getLogid()) > 10));
            // ????????????
            Assert.assertEquals(1, mapper.deleteByPrimaryKey(userLogin));
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ??????
     */
    @Test
    public void testDelete() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            UserLoginMapper mapper = sqlSession.getMapper(UserLoginMapper.class);
            // ????
            Assert.assertEquals(10, selectCount(new UserLogin()));
            // ??????
            Map<String, Object> key = new HashMap<String, Object>();
            key.put("logid", 1);
            key.put("username", "test1");
            UserLogin userLogin = selectByPrimaryKey(key);
            // ??????
            Assert.assertEquals(1, deleteByPrimaryKey(key));
            // ????
            Assert.assertEquals(9, selectCount(new UserLogin()));
            // ??
            Assert.assertEquals(1, insert(userLogin));
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ??
     */
    @Test
    public void testSelect() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            UserLoginMapper mapper = sqlSession.getMapper(UserLoginMapper.class);
            UserLogin userLogin = new UserLogin();
            userLogin.setUsername("test1");
            List<UserLogin> userLogins = select(userLogin);
            Assert.assertEquals(5, userLogins.size());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ???????
     */
    @Test
    public void testUpdateByPrimaryKey() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            UserLoginMapper mapper = sqlSession.getMapper(UserLoginMapper.class);
            Map<String, Object> key = new HashMap<String, Object>();
            key.put("logid", 2);
            key.put("username", "test1");
            UserLogin userLogin = selectByPrimaryKey(key);
            Assert.assertNotNull(userLogin);
            userLogin.setLoginip("1.1.1.1");
            userLogin.setLogindate(null);
            // ????username
            Assert.assertEquals(1, updateByPrimaryKey(userLogin));
            userLogin = mapper.selectByPrimaryKey(userLogin);
            Assert.assertNull(userLogin.getLogindate());
            Assert.assertEquals("1.1.1.1", userLogin.getLoginip());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ???????null
     */
    @Test
    public void testUpdateByPrimaryKeySelective() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            UserLoginMapper mapper = sqlSession.getMapper(UserLoginMapper.class);
            Map<String, Object> key = new HashMap<String, Object>();
            key.put("logid", 1);
            key.put("username", "test1");
            UserLogin userLogin = selectByPrimaryKey(key);
            Assert.assertNotNull(userLogin);
            userLogin.setLogindate(null);
            userLogin.setLoginip("1.1.1.1");
            // ????username
            Assert.assertEquals(1, updateByPrimaryKeySelective(userLogin));
            userLogin = selectByPrimaryKey(key);
            Assert.assertNotNull(userLogin.getLogindate());
            Assert.assertEquals("1.1.1.1", userLogin.getLoginip());
        } finally {
            sqlSession.close();
        }
    }
}

