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
public class TestSelectByPrimaryKey {
    /**
     * ??PK????
     */
    @Test
    public void testDynamicSelectByPrimaryKey2() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Country country = mapper.selectByPrimaryKey(35);
            Assert.assertNotNull(country);
            Assert.assertEquals(true, ((country.getId()) == 35));
            Assert.assertEquals("China", country.getCountryname());
            Assert.assertEquals("CN", country.getCountrycode());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ????????????
     */
    @Test
    public void testDynamicSelectByPrimaryKey() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Country country = new Country();
            country.setId(35);
            country = mapper.selectByPrimaryKey(country);
            Assert.assertNotNull(country);
            Assert.assertEquals(true, ((country.getId()) == 35));
            Assert.assertEquals("China", country.getCountryname());
            Assert.assertEquals("CN", country.getCountrycode());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ????????
     */
    @Test
    public void testDynamicSelectByPrimaryKeyZero() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Assert.assertNull(mapper.selectByPrimaryKey(new Country()));
            Assert.assertNull(mapper.selectByPrimaryKey(new HashMap<String, Object>()));
            Assert.assertNull(selectByPrimaryKey((-10)));
            Assert.assertNull(selectByPrimaryKey(0));
            Assert.assertNull(selectByPrimaryKey(1000));
            Assert.assertNull(mapper.selectByPrimaryKey(null));
        } finally {
            sqlSession.close();
        }
    }

    /**
     * Map????
     */
    @Test
    public void testSelectByPrimaryKeyMap() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Map map = new HashMap();
            map.put("id", 35);
            Country country = mapper.selectByPrimaryKey(map);
            Assert.assertNotNull(country);
            Assert.assertEquals(true, ((country.getId()) == 35));
            Assert.assertEquals("China", country.getCountryname());
            Assert.assertEquals("CN", country.getCountrycode());
            map = new HashMap();
            map.put("countryname", "China");
            Assert.assertNull(mapper.selectByPrimaryKey(map));
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
            mapper.selectByPrimaryKey(new TestSelectByPrimaryKey.Key());
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
            selectByPrimaryKey(100);
        } finally {
            sqlSession.close();
        }
    }

    class Key {}
}

