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


import java.util.List;
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
public class TestDelete {
    /**
     * ??????
     */
    @Test
    public void testDynamicDelete() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            UserLoginMapper mapper = sqlSession.getMapper(UserLoginMapper.class);
            // ????
            Assert.assertEquals(10, selectCount(new UserLogin()));
            UserLogin userLogin = new UserLogin();
            userLogin.setUsername("test1");
            List<UserLogin> userLoginList = select(userLogin);
            // ????
            Assert.assertEquals(userLoginList.size(), delete(userLogin));
            // ????
            for (int i = 0; i < (userLoginList.size()); i++) {
                Assert.assertEquals(1, insert(userLoginList.get(i)));
                Assert.assertEquals((i + 1), ((int) (userLoginList.get(i).getLogid())));
            }
            // ????
            Assert.assertEquals(10, selectCount(new UserLogin()));
        } finally {
            sqlSession.close();
        }
    }
}

