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
package tk.mybatis.mapper.test.country;


import java.util.HashMap;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.mapper.CountryMapper;
import tk.mybatis.mapper.mapper.MybatisHelper;
import tk.mybatis.mapper.model.Country;


/**
 * ??????
 *
 * @author liuzh
 */
public class TestDeleteByPrimaryKey {
    /**
     * ??????
     */
    @Test
    public void testDynamicDelete() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            // ????
            Assert.assertEquals(183, selectCount(new Country()));
            // ??100
            Country country = selectByPrimaryKey(100);
            // ??????
            Assert.assertEquals(1, deleteByPrimaryKey(100));
            // ????
            Assert.assertEquals(182, selectCount(new Country()));
            // ??
            Assert.assertEquals(1, insert(country));
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ????????
     */
    @Test
    public void testDynamicDeleteZero() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            // ??????
            Assert.assertEquals(0, mapper.deleteByPrimaryKey(null));
            Assert.assertEquals(0, deleteByPrimaryKey((-100)));
            Assert.assertEquals(0, deleteByPrimaryKey(0));
            Assert.assertEquals(0, deleteByPrimaryKey(1000));
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ????????
     */
    @Test
    public void testDynamicDeleteEntity() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Country country = new Country();
            country.setId(100);
            Assert.assertEquals(1, mapper.deleteByPrimaryKey(country));
        } finally {
            sqlSession.close();
        }
    }

    /**
     * Map????
     */
    @Test
    public void testDynamicDeleteMap() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Map map = new HashMap();
            map.put("id", 100);
            Assert.assertEquals(1, mapper.deleteByPrimaryKey(map));
            map = new HashMap();
            map.put("countryname", "China");
            Assert.assertEquals(0, mapper.deleteByPrimaryKey(map));
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ???????
     */
    @Test(expected = Exception.class)
    public void testDynamicDeleteNotFoundKeyProperties() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            // ??????
            Assert.assertEquals(0, mapper.deleteByPrimaryKey(new TestDeleteByPrimaryKey.Key()));
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ??????
     */
    @Test
    public void testDynamicDeleteException() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            // ??????
            Assert.assertEquals(1, deleteByPrimaryKey(100));
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    class Key {}
}

