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
package tk.mybatis.mapper.test.country2;


import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.mapper.Country2Mapper;
import tk.mybatis.mapper.mapper.MybatisHelper;
import tk.mybatis.mapper.model.Country2;


/**
 * ??????????? - Country2 ????ID
 *
 * @author liuzh
 */
public class TestInsert {
    /**
     * ?????,id???null,???
     */
    @Test
    public void testDynamicInsertAll() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            Country2Mapper mapper = sqlSession.getMapper(Country2Mapper.class);
            Country2 country2 = new Country2();
            country2.setCountrycode("CN");
            country2.setId(100);
            Assert.assertEquals(1, insert(country2));
            country2 = select(country2).get(0);
            Assert.assertNotNull(country2);
            Assert.assertEquals(1, mapper.deleteByPrimaryKey(country2.getId()));
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ????null
     */
    @Test
    public void testDynamicInsertAllByNull() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            Country2Mapper mapper = sqlSession.getMapper(Country2Mapper.class);
            mapper.insert(null);
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ??????
     */
    @Test
    public void testDynamicInsert() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            Country2Mapper mapper = sqlSession.getMapper(Country2Mapper.class);
            Country2 country = new Country2();
            country.setId(10086);
            country.setCountrycode("CN");
            country.setCountryname("??");
            Assert.assertEquals(1, insert(country));
            // ??CN??2?
            country = new Country2();
            country.setCountrycode("CN");
            List<Country2> list = mapper.select(country);
            Assert.assertEquals(1, list.size());
            // ???????,???????????
            Assert.assertEquals(1, deleteByPrimaryKey(10086));
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ??code?null???,???????HH
     */
    @Test
    public void testDynamicInsertNull() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            Country2Mapper mapper = sqlSession.getMapper(Country2Mapper.class);
            Country2 country = new Country2();
            country.setId(10086);
            country.setCountryname("??");
            Assert.assertEquals(1, insert(country));
            // ??CN??2?
            country = new Country2();
            country.setId(10086);
            List<Country2> list = mapper.select(country);
            Assert.assertEquals(1, list.size());
            Assert.assertNull(list.get(0).getCountrycode());
            // ???????,???????????
            Assert.assertEquals(1, deleteByPrimaryKey(10086));
        } finally {
            sqlSession.close();
        }
    }
}

